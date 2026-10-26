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

    /** Mirrors ContentParser.supportedTypes. */
    private val EXERCISE_TYPES = setOf(
        "multiple_choice", "fill_blank", "word_recall", "translation",
        "sentence_production", "listening", "speaking",
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
                }
                "listening" -> {
                    if (text(o, "text").isEmpty()) report(id, "blank text")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                }
                // Speaking practice is self-assessed: the first accepted answer is
                // the model the learner hears and repeats, so it must exist and be
                // English (a Persian model cannot be spoken back through TTS).
                "speaking" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    val models = array(o, "accepted").map { it.toString().trim() }.filter { it.isNotEmpty() }
                    if (models.isEmpty()) report(id, "speaking needs at least one accepted model sentence")
                    if (models.any { it.any { ch -> ch in '\u0600'..'\u06FF' } }) {
                        report(id, "speaking model must be English (it is spoken by TTS)")
                    }
                    if (text(o, "skill").isEmpty()) report(id, "speaking must declare skill")
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
        vocabulary.forEachIndexed { index, word ->
            val id = text(word, "id").ifEmpty { "vocabulary[$index]" }
            if (text(word, "word").isEmpty()) report(id, "blank word")
            if (text(word, "translation").isEmpty()) report(id, "blank translation")
            if (text(word, "level") !in LEVELS) report(id, "invalid level \"${text(word, "level")}\"")
            val examples = array(word, "examples")
            if (examples.size < 2) report(id, "needs at least 2 examples (has ${examples.size})")
            examples.forEachIndexed { ei, raw ->
                val example = raw as? JSONObject
                if (example == null) {
                    report(id, "example[$ei] must be an object {en, fa} — plain strings are the legacy format")
                    return@forEachIndexed
                }
                if (text(example, "en").isEmpty()) report(id, "example[$ei] has no English sentence")
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
            checkExercise(id, type, exercise)
        }
        ordersPerLesson.forEach { (lessonId, orders) ->
            orders.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
                .forEach { report(lessonId, "exercise order $it is used twice") }
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
        lessonIds.sorted().forEach { lessonId ->
            if (lessonId !in exerciseLessonIds) report(lessonId, "lesson has no exercises")
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
