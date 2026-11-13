package org.token.english.content

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * The content gate (AGENTS.md §5 / `docs/content-pipeline.md`), the single Kotlin
 * implementation behind BOTH consumers:
 *
 *  - [ValidateContentTask] — `./gradlew validateContent`, wired into `preBuild`,
 *  - `ContentValidationTest` — the same rules on the unit-test classpath,
 *
 * so the check that blocks a build and the check that guards the suite cannot drift
 * apart. The curriculum is plain JSON, so the Kotlin compiler sees none of it:
 * a dangling `lessonId` orphans a lesson silently, an out-of-range `correctIndex`
 * throws mid-lesson, a duplicate id is dropped by Room (the id is the primary key),
 * and a wrong-typed `type` is skipped by ContentParser so the exercise never shows.
 *
 * Every problem is collected — not just the first — and the port from the original
 * Groovy task is 1:1: same rules, same message text, same non-blocking
 * `explanationFa` backlog warning.
 */
data class ContentValidationReport(
    /** Blocking problems, each already formatted as `  - where: what`. */
    val errors: List<String>,
    /** Non-blocking nudges for content authors (the explanationFa backlog). */
    val warnings: List<String>,
    /** One-line "what was checked" message for a passing run. */
    val summary: String,
) {
    val isValid: Boolean get() = errors.isEmpty()
}

object ContentValidator {

    private val LEVELS = setOf("A1", "A2", "B1", "B2", "C1", "C2")

    /**
     * Floor for a lesson's dialogue. A question and its answer is a repetition
     * drill; a conversation has an opening, a middle and a close.
     */
    private const val MIN_DIALOGUE_TURNS = 4

    /** Mirrors ContentParser.supportedTypes. */
    private val EXERCISE_TYPES = setOf(
        "multiple_choice", "fill_blank", "word_recall", "translation",
        "sentence_production", "writing", "listening", "speaking",
    )

    /** Mirrors domain Skill — an authored tag must name a real skill, otherwise ContentParser drops it. */
    private val SKILLS = setOf("VOCABULARY", "GRAMMAR", "LISTENING", "SPEAKING", "READING", "WRITING")

    private val KNOWLEDGE_TYPES = setOf("GRAMMAR", "VOCABULARY", "DISCOURSE", "PHONOLOGY")

    /**
     * What placement accepts besides choice: typed shapes the screen can ask and
     * grade without audio or a recognizer (see PlacementScreen). Mirrors the
     * lessons' formats minus `translation`'s word bank and the audio/speech pair.
     */
    private val TYPED_PLACEMENT_TYPES = setOf(
        "multiple_choice", "fill_blank", "word_recall", "sentence_production",
    )

    private val FILES = listOf(
        "lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json",
    )

    /** Validates the five bundle files in [dir] (`app/src/main/assets/content`). */
    fun validate(dir: File): ContentValidationReport {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        fun report(where: String, what: String) {
            errors += "  - $where: $what"
        }

        fun load(name: String): JSONObject? {
            val file = File(dir, name)
            if (!file.exists()) {
                report(name, "file is missing")
                return null
            }
            return try {
                JSONObject(file.readText())
            } catch (e: Exception) {
                report(name, "not valid JSON (${e.message})")
                null
            }
        }

        fun items(root: JSONObject?, key: String, file: String): List<JSONObject> {
            if (root == null) {
                report(file, "missing top-level \"$key\" array")
                return emptyList()
            }
            val array = root.opt(key) as? JSONArray
            if (array == null) {
                report(file, "\"$key\" is not an array")
                return emptyList()
            }
            val result = mutableListOf<JSONObject>()
            for (index in 0 until array.length()) {
                val entry = array.opt(index)
                if (entry is JSONObject) result += entry
                else report("$file/$key[$index]", "entry is not an object")
            }
            return result
        }

        fun text(o: JSONObject, key: String): String =
            (o.opt(key) as? String)?.trim().orEmpty()

        fun number(o: JSONObject, key: String): Int? = (o.opt(key) as? Number)?.toInt()

        /**
         * Comparison form for the semantic rules below. Case, punctuation, hyphens and
         * apostrophes carry no meaning when the question is "is this the same sentence?",
         * and treating "twenty-six" as different from "twenty six" is how a check
         * invents findings instead of catching them (Phase 3).
         */
        fun normalize(value: String): String = value
            .lowercase()
            .replace(Regex("[’'`]"), "")
            .replace(Regex("[-–—]"), " ")
            .replace(Regex("[.,!?;:\"“”«»()]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        fun wordCount(value: String): Int = normalize(value).split(' ').count { it.isNotEmpty() }

        fun array(o: JSONObject, key: String): List<Any?> {
            val json = o.opt(key) as? JSONArray ?: return emptyList()
            return (0 until json.length()).map { json.get(it) }
        }

        fun collectIds(rows: List<JSONObject>, where: String): Set<String> {
            val ids = mutableListOf<String>()
            rows.forEachIndexed { index, row ->
                val id = text(row, "id")
                if (id.isEmpty()) report("$where[$index]", "missing id") else ids += id
            }
            ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
                .forEach { report(where, "duplicate id \"$it\"") }
            return ids.toSet()
        }

        fun checkChoiceAnswer(id: String, o: JSONObject) {
            val options = array(o, "options").map { it.toString() }
            if (options.size != 4) report(id, "expected 4 options, found ${options.size}")
            options.forEachIndexed { index, option ->
                if (option.isBlank()) report(id, "option $index is blank")
            }
            if (options.map { it.lowercase() }.distinct().size != options.size) {
                report(id, "options contain duplicates")
            }
            val correct = number(o, "correctIndex")
            when {
                correct == null -> report(id, "missing correctIndex")
                correct !in options.indices -> report(id, "correctIndex $correct outside 0..${options.size - 1}")
            }
        }

        fun checkSkill(id: String, o: JSONObject) {
            val skill = text(o, "skill")
            if (skill.isNotEmpty() && skill.uppercase() !in SKILLS) {
                report(id, "invalid skill \"$skill\"")
            }
        }

        fun checkExercise(id: String, type: String, o: JSONObject) {
            checkSkill(id, o)
            when (type) {
                "multiple_choice" -> {
                    if (text(o, "question").isEmpty()) report(id, "blank question")
                    // A-5: an authored reading passage (optional) must not be empty.
                    if (o.has("passage") && text(o, "passage").isEmpty()) report(id, "blank passage")
                    checkChoiceAnswer(id, o)
                    // The answer cannot be part of the stem: a learner who read the
                    // question has already been told which option to tap (Phase 3).
                    val options = array(o, "options").map { it.toString() }
                    val correct = options.getOrNull(number(o, "correctIndex") ?: -1).orEmpty()
                    if (correct.length >= 4 && normalize(text(o, "question")).contains(normalize(correct))) {
                        report(id, "question already contains the correct option — the answer is given away")
                    }
                }
                "fill_blank" -> {
                    val sentence = text(o, "sentence")
                    if (sentence.isEmpty()) report(id, "blank sentence")
                    if (!sentence.contains("___")) report(id, "sentence has no ___ blank marker")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                    // A-2b: an untagged blank silently falls back to GRAMMAR, which decides
                    // whether a near-miss is reported as a spelling slip. That is a content
                    // decision, so the author has to make it explicitly.
                    if (text(o, "skill").isEmpty()) {
                        report(id, "fill_blank must declare skill (VOCABULARY or GRAMMAR)")
                    }
                }
                "translation" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                }
                // P2: recall with no options to recognize. The skill tag decides
                // which mastery meter the recall feeds, so it is not optional.
                "word_recall" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                    if (text(o, "skill").isEmpty()) report(id, "word_recall must declare skill")
                    if (text(o, "prompt").any { it in 'A'..'Z' || it in 'a'..'z' }) {
                        // The cue is the Persian meaning; an English cue turns the
                        // item into a spelling task and leaks the answer.
                        report(id, "prompt must be Persian (it is the meaning to name in English)")
                    }
                }
                // P2: produce a whole sentence. Without anchors and a word floor a
                // single token would satisfy the structural check.
                "sentence_production" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    if (array(o, "accepted").isEmpty()) {
                        report(id, "no accepted answers (the exemplar is shown after a miss)")
                    }
                    if (text(o, "skill").isEmpty()) report(id, "sentence_production must declare skill")
                    val anchors = array(o, "mustUse").map { it.toString().trim() }.filter { it.isNotEmpty() }
                    if (anchors.isEmpty()) {
                        report(id, "sentence_production needs mustUse anchors (the target structure)")
                    }
                    val anchorWords = anchors.sumOf { anchor -> anchor.split(" ").count { it.isNotBlank() } }
                    val minWords = number(o, "minWords") ?: 0
                    when {
                        minWords <= 0 -> report(id, "sentence_production needs a positive minWords floor")
                        minWords < 3 -> report(id, "minWords $minWords is too low to be a sentence (use at least 3)")
                        minWords < anchorWords -> {
                            report(id, "minWords $minWords is below the $anchorWords anchor word(s) — nothing to type")
                        }
                    }
                    // An accepted model the item itself would reject is worse than no
                    // model: after a miss the learner is shown an answer that does not
                    // meet the bar they were held to (Phase 3).
                    if (minWords > 0) {
                        array(o, "accepted").map { it.toString() }.filter { it.isNotBlank() }.forEach { answer ->
                            val count = wordCount(answer)
                            if (count < minWords) {
                                report(id, "accepted answer \"$answer\" has $count words, under minWords $minWords")
                            }
                        }
                    }
                }
                // A short connected text. The floors are the item's own grading rule,
                // so they are content rules too: a text with a one-word floor is a
                // sentence with a longer name, and one sentence cannot be a text.
                "writing" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    if (array(o, "accepted").isEmpty()) {
                        report(id, "no exemplar text (it is shown after a miss)")
                    }
                    if (text(o, "skill").isEmpty()) report(id, "writing must declare skill")
                    val anchors = array(o, "mustUse").map { it.toString().trim() }.filter { it.isNotEmpty() }
                    if (anchors.isEmpty()) {
                        report(id, "writing needs mustUse anchors (the structures the text must use)")
                    }
                    // Speech cheaply: a Persian anchor cannot appear in an English text.
                    if (anchors.any { anchor -> anchor.any { ch -> ch in '\u0600'..'\u06FF' } }) {
                        report(id, "mustUse must be English (the text is written in English)")
                    }
                    val minWords = number(o, "minWords") ?: 0
                    val minSentences = number(o, "minSentences") ?: 0
                    val anchorWords = anchors.sumOf { anchor -> anchor.split(" ").count { it.isNotBlank() } }
                    when {
                        minWords <= 0 -> report(id, "writing needs a positive minWords floor")
                        // Two authored sentences of six words each is the smallest thing
                        // that is still a text rather than a sentence.
                        minWords < 8 -> report(id, "minWords $minWords is too low for a text (use at least 8)")
                        minWords < anchorWords -> {
                            report(id, "minWords $minWords is below the $anchorWords anchor word(s) — nothing to write")
                        }
                    }
                    if (minSentences < 2) {
                        report(id, "writing needs minSentences of at least 2 — one sentence is a production, not a text")
                    }
                    // The exemplar has to pass the bar the learner is held to, or the
                    // reference shown after a miss is a model answer the item rejects.
                    array(o, "accepted").map { it.toString() }.filter { it.isNotBlank() }.forEach { model ->
                        if (minWords > 0 && wordCount(model) < minWords) {
                            report(id, "exemplar has ${wordCount(model)} words, under minWords $minWords")
                        }
                        val modelSentences = model.count { ch -> ch == '.' || ch == '!' || ch == '?' }
                            .let { n -> n + if (model.trimEnd().lastOrNull()?.let { it !in ".!?" } == true) 1 else 0 }
                        if (minSentences > 0 && modelSentences < minSentences) {
                            report(id, "exemplar has $modelSentences sentence(s), under minSentences $minSentences")
                        }
                    }
                }
                "listening" -> {
                    if (text(o, "text").isEmpty()) report(id, "blank text")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                    // What is heard must be gradable as written: if no accepted answer
                    // equals the spoken text, a correct transcription is marked wrong
                    // (Phase 3). Numbers are the usual cause — "26" for "twenty-six".
                    val heard = normalize(text(o, "text"))
                    if (heard.isNotEmpty() && array(o, "accepted").none { normalize(it.toString()) == heard }) {
                        report(id, "no accepted answer matches the spoken text — the learner would type what they did not hear")
                    }
                }
                // Speaking practice is graded on-device against the first accepted
                // answer, so it must exist and be English (a Persian sentence cannot
                // be recognised from English speech).
                "speaking" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    val models = array(o, "accepted").map { it.toString().trim() }.filter { it.isNotEmpty() }
                    if (models.isEmpty()) report(id, "speaking needs at least one accepted model sentence")
                    if (models.any { it.any { ch -> ch in '\u0600'..'\u06FF' } }) {
                        report(id, "speaking model must be English (it is spoken by TTS)")
                    }
                    if (text(o, "skill").isEmpty()) report(id, "speaking must declare skill")

                    // Optional dialogue turn: the apparatus asks `question` and the
                    // learner answers with the model. Authoring rules, in the two
                    // ways this pair can go wrong:
                    //  - a Persian gloss without an English line leaves a turn with
                    //    nothing to say, so the learner only ever hears the answer;
                    //  - a question the learner is also supposed to answer verbatim
                    //    is not a conversation, it is an echo (the scorer would pass
                    //    the partner's own line back as the learner's answer).
                    val question = text(o, "question")
                    val questionFa = text(o, "questionFa")
                    if (question.isEmpty() && questionFa.isNotEmpty()) {
                        report(id, "questionFa without question (nothing to ask)")
                    }
                    if (question.isNotEmpty()) {
                        if (question.any { ch -> ch in '\u0600'..'\u06FF' }) {
                            report(id, "question must be English (the partner speaks it)")
                        }
                        if (questionFa.isEmpty()) {
                            report(id, "question needs questionFa — the learner must know what was asked")
                        }
                        if (models.any { it.equals(question, ignoreCase = true) }) {
                            report(id, "question repeats the accepted answer — write a real reply")
                        }
                        // A turn is part of a situation, not a floating sentence: the
                        // practice screen names the place and the person asking, and
                        // both are authored per lesson, never invented in the UI.
                        if (text(o, "sceneFa").isEmpty()) {
                            report(id, "dialogue turn without sceneFa — where does this exchange happen?")
                        }
                        if (text(o, "partnerFa").isEmpty()) {
                            report(id, "dialogue turn without partnerFa — who is asking?")
                        }
                    }
                }
            }
        }

        val roots = FILES.associateWith { load(it) }

        // --- bundle version -------------------------------------------------
        val versions = FILES.associateWith { file ->
            val root = roots[file]
            val version = root?.let { number(it, "contentVersion") }
            if (version == null) report(file, "missing top-level contentVersion")
            version ?: -1
        }
        if (versions.values.toSet().size > 1) {
            report("contentVersion", "files disagree — ${versions.entries.joinToString { "${it.key}=${it.value}" }}")
        }

        // --- lessons --------------------------------------------------------
        val lessons = items(roots["lessons.json"], "lessons", "lessons.json")
        val lessonIds = collectIds(lessons, "lessons")
        lessons.forEach { lesson ->
            val id = text(lesson, "id").ifEmpty { "<lesson without id>" }
            if (text(lesson, "level") !in LEVELS) report(id, "invalid level \"${text(lesson, "level")}\"")
            listOf("title", "titleFa", "topic").forEach { key ->
                if (text(lesson, key).isEmpty()) report(id, "blank $key")
            }
            if ((number(lesson, "order") ?: 0) <= 0) report(id, "order must be a positive integer")
            if ((number(lesson, "estimatedMinutes") ?: 0) <= 0) report(id, "estimatedMinutes must be positive")
        }
        lessons.mapNotNull { number(it, "order") }.groupingBy { it }.eachCount()
            .filterValues { it > 1 }.keys.sorted()
            .forEach { report("lessons.json", "order $it is used by more than one lesson") }

        // --- vocabulary -----------------------------------------------------
        val vocabulary = items(roots["vocabulary.json"], "vocabulary", "vocabulary.json")
        collectIds(vocabulary, "vocabulary")
        val seenWords = mutableListOf<String>()
        /** `word::sentence` → the entry that already uses it, across lessons. */
        val seenExamplesByWord = mutableMapOf<String, String>()
        vocabulary.forEachIndexed { index, word ->
            val id = text(word, "id").ifEmpty { "vocabulary[$index]" }
            if (text(word, "word").isEmpty()) report(id, "blank word")
            if (text(word, "translation").isEmpty()) report(id, "blank translation")
            if (text(word, "level") !in LEVELS) report(id, "invalid level \"${text(word, "level")}\"")
            val examples = array(word, "examples")
            if (examples.size < 2) report(id, "needs at least 2 examples (has ${examples.size})")
            val seenExamples = mutableSetOf<String>()
            examples.forEachIndexed { ei, raw ->
                val example = raw as? JSONObject
                if (example == null) {
                    report(id, "example[$ei] must be an object {en, fa} — plain strings are the legacy format")
                    return@forEachIndexed
                }
                if (text(example, "en").isEmpty()) report(id, "example[$ei] has no English sentence")
                // Two identical examples are one example: the entry promises two
                // different contexts for the word (Phase 3).
                val sentence = normalize(text(example, "en"))
                if (sentence.isNotEmpty() && !seenExamples.add(sentence)) {
                    report(id, "example[$ei] repeats an earlier example of the same word")
                }
                // The same word can be taught at two levels (70 of them are), and each
                // entry exists because the higher level shows a harder context. Reusing
                // the A1 sentence at A2 means one of those slots is empty (Phase 3).
                val wordKey = "${text(word, "word").lowercase()}::$sentence"
                if (sentence.isNotEmpty()) {
                    val previous = seenExamplesByWord[wordKey]
                    if (previous == null) seenExamplesByWord[wordKey] = id
                    else if (previous != id) report(id, "example[$ei] repeats the one in $previous for the same word")
                }
                val fa = text(example, "fa")
                if (fa.isEmpty()) report(id, "example[$ei] has no Persian translation")
                else if (fa.none { it in '\u0600'..'\u06FF' }) report(id, "example[$ei] translation has no Persian characters")
            }
            val lessonId = text(word, "lessonId")
            if (lessonId.isNotEmpty() && lessonId !in lessonIds) report(id, "lessonId \"$lessonId\" is not a lesson")
            seenWords += "${text(word, "word").lowercase()} @ $lessonId"
        }
        seenWords.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
            .forEach { report("vocabulary.json", "duplicate word in the same lesson: \"$it\"") }

        // --- exercises ------------------------------------------------------
        val exercises = items(roots["exercises.json"], "exercises", "exercises.json")
        val exerciseIds = collectIds(exercises, "exercises")
        val ordersPerLesson = mutableMapOf<String, MutableList<Int>>()
        exercises.forEachIndexed { index, exercise ->
            val id = text(exercise, "id").ifEmpty { "exercises[$index]" }
            val lessonId = text(exercise, "lessonId")
            if (lessonId !in lessonIds) report(id, "lessonId \"$lessonId\" is not a lesson")
            val type = text(exercise, "type")
            if (type !in EXERCISE_TYPES) report(id, "unsupported type \"$type\"")
            val order = number(exercise, "order") ?: 0
            if (order <= 0) report(id, "order must be a positive integer")
            else ordersPerLesson.getOrPut(lessonId) { mutableListOf() } += order
            // Authored difficulty (content v66): the selector raises or lowers the level
            // with it, so a missing value would silently fall back to the middle and the
            // adaptation would be a guess dressed as a decision.
            val difficulty = number(exercise, "difficulty")
            if (difficulty == null) {
                report(id, "difficulty is required (1 = first encounter, 3 = hardest in its lesson)")
            } else if (difficulty !in 1..3) {
                report(id, "difficulty $difficulty is outside 1..3")
            }
            checkExercise(id, type, exercise)
        }
        ordersPerLesson.forEach { (lessonId, orders) ->
            orders.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
                .forEach { report(lessonId, "exercise order $it is used twice") }
        }

        // --- no item is shipped twice (Phase 3) ------------------------------
        // A learner who meets the identical question in two lessons has paid two
        // lesson slots for one item, and one of those lessons is teaching it at the
        // wrong level. Dialogue openers ("How can I help you?") are excluded on
        // purpose: the same service greeting in different scenes is realistic.
        val itemKeys = mutableMapOf<String, MutableList<String>>()
        val recallKeys = mutableMapOf<String, MutableList<String>>()
        exercises.forEach { exercise ->
            val type = text(exercise, "type")
            val id = text(exercise, "id")
            if (type == "speaking") return@forEach
            val stem = text(exercise, "question")
                .ifEmpty { text(exercise, "prompt") }
                .ifEmpty { text(exercise, "sentence") }
                .ifEmpty { text(exercise, "text") }
            val options = array(exercise, "options").joinToString("|") { normalize(it.toString()) }
            itemKeys.getOrPut("$type::${normalize(stem)}::$options") { mutableListOf() } += id
            if (type == "word_recall") {
                val cue = normalize(text(exercise, "prompt")) + "::" +
                    normalize(array(exercise, "accepted").firstOrNull()?.toString().orEmpty())
                recallKeys.getOrPut(cue) { mutableListOf() } += id
            }
        }
        itemKeys.values.filter { it.size > 1 }.forEach { ids ->
            report("exercises", "the same item is shipped ${ids.size} times: ${ids.joinToString(", ")}")
        }
        recallKeys.values.filter { it.size > 1 }.forEach { ids ->
            report("exercises", "the same recall question is asked twice: ${ids.joinToString(", ")}")
        }

        // --- placement ------------------------------------------------------
        val placement = items(roots["placement.json"], "questions", "placement.json")
        collectIds(placement, "placement.questions")
        placement.forEachIndexed { index, question ->
            val id = text(question, "id").ifEmpty { "placement[$index]" }
            if (id in exerciseIds) report(id, "id already used by an exercise")
            if (text(question, "level") !in LEVELS) report(id, "invalid level \"${text(question, "level")}\"")
            // The test is no longer choice-only: one typed item per bundle lets it
            // sample production instead of recognition alone (A-5/A-6 round).
            // Listening/speaking stay out — placement has no audio or recognizer UI.
            val type = text(question, "type").ifEmpty { "multiple_choice" }
            if (type !in TYPED_PLACEMENT_TYPES) {
                report(id, "placement type \"$type\" is not answerable here (choice or typed only)")
            }
            checkSkill(id, question)
            if (type == "multiple_choice") {
                if (text(question, "question").isEmpty()) report(id, "blank question")
                checkChoiceAnswer(id, question)
            } else {
                // Typed placement items are held to the same rules as lesson exercises.
                checkExercise(id, type, question)
            }
        }

        // --- knowledge graph -------------------------------------------------
        val knowledge = items(roots["knowledge.json"], "knowledge", "knowledge.json")
        val knowledgeIds = collectIds(knowledge, "knowledge")
        val prerequisiteOf = mutableMapOf<String, List<String>>()
        knowledge.forEachIndexed { index, item ->
            val id = text(item, "id").ifEmpty { "knowledge[$index]" }
            if (text(item, "type").uppercase() !in KNOWLEDGE_TYPES) {
                report(id, "invalid knowledge type \"${text(item, "type")}\"")
            }
            if (text(item, "level") !in LEVELS) report(id, "invalid level \"${text(item, "level")}\"")
            listOf("title", "titleFa").forEach { key ->
                if (text(item, key).isEmpty()) report(id, "blank $key")
            }
            val prerequisites = array(item, "prerequisites").map { it.toString() }
            prerequisites.filterNot { it in knowledgeIds }.sorted().forEach { missing ->
                report(id, "prerequisite \"$missing\" is not a knowledge item")
            }
            val taught = array(item, "lessons").map { it.toString() }
            if (taught.isEmpty()) report(id, "teaches no lesson")
            if (taught.distinct().size != taught.size) report(id, "duplicate lesson reference")
            taught.filterNot { it in lessonIds }.sorted().forEach { unknown ->
                report(id, "lesson \"$unknown\" is not a lesson")
            }
            if (array(item, "skills").isEmpty()) report(id, "no skills")
            prerequisiteOf[id] = prerequisites
        }
        // A cycle would make the curriculum impossible to order (audit §22).
        knowledgeIds.sorted().forEach { start ->
            val pending = ArrayDeque(prerequisiteOf[start].orEmpty().filter { it in knowledgeIds })
            val visited = mutableSetOf<String>()
            var cyclic = false
            while (pending.isNotEmpty()) {
                val current = pending.removeLast()
                if (current == start) {
                    cyclic = true
                    break
                }
                if (!visited.add(current)) continue
                prerequisiteOf[current]?.filter { it in knowledgeIds }?.forEach { pending += it }
            }
            if (cyclic) report("knowledge", "\"$start\" participates in a prerequisite cycle")
        }

        // --- coverage: a lesson with no content is invisible to the learner ---
        val exerciseLessonIds = exercises.map { text(it, "lessonId") }.toSet()
        val vocabularyLessonIds = vocabulary.map { text(it, "lessonId") }.toSet()
        val taughtLessonIds = knowledge.flatMap { item -> array(item, "lessons").map { it.toString() } }.toSet()
        // Conversation practice is the last step of every lesson, so every lesson
        // needs a real exchange: a two-line pair is a repetition drill, not a
        // dialogue, and the lesson path will not open the next lesson until this
        // one has been spoken.
        val dialogueTurns = exercises
            .filter { text(it, "type") == "speaking" }
            .groupBy { text(it, "lessonId") }
        lessonIds.sorted().forEach { lessonId ->
            if (lessonId !in exerciseLessonIds) report(lessonId, "lesson has no exercises")
            val turns = dialogueTurns[lessonId].orEmpty().size
            if (turns < MIN_DIALOGUE_TURNS) {
                report(lessonId, "lesson has $turns dialogue turns — a conversation needs $MIN_DIALOGUE_TURNS")
            }
            if (lessonId !in vocabularyLessonIds) report(lessonId, "lesson has no vocabulary")
            if (lessonId !in taughtLessonIds) report(lessonId, "no knowledge item teaches this lesson")
        }

        // --- non-blocking nudge (checklist A-1): an exercise without
        // `explanationFa` teaches nothing after a miss. The hard bar lives in
        // ExplanationCoverageTest; here we only surface the backlog so content
        // authors see it while editing.
        val unexplained = exercises.filter { text(it, "explanationFa").isEmpty() }
        if (unexplained.isNotEmpty()) {
            val ids = unexplained.map { text(it, "id") }.sorted()
            warnings += "Content warning: ${ids.size} of ${exercises.size} exercises have no explanationFa " +
                "(shown after a wrong answer). Missing: ${ids.joinToString(", ")}"
        }

        val summary = "Content validation passed: ${lessons.size} lessons, ${vocabulary.size} words, " +
            "${exercises.size} exercises, ${placement.size} placement questions, " +
            "${knowledge.size} knowledge items (bundle v${versions["lessons.json"]})."

        return ContentValidationReport(errors = errors, warnings = warnings, summary = summary)
    }
}
