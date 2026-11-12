package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.KnowledgeEvidence
import org.token.english.domain.engine.KnowledgeGates
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile
import org.token.english.domain.model.shuffledForDisplay
import java.io.File

/**
 * Guards the authored content and the display-time shuffle:
 *
 * 1. Correct-answer positions must be balanced across the option slots — an
 *    answer that always sits at one index makes the test guessable (P1).
 *    `scripts/balance_answer_positions.mjs` restores the invariant after edits.
 * 2. The shuffle must be deterministic per question and must never change
 *    which option is correct.
 */
class ContentDistributionTest {

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun multipleChoiceIn(name: String): List<JSONObject> {
        val root = JSONObject(contentFile(name).readText())
        val array = root.optJSONArray("exercises") ?: root.getJSONArray("questions")
        return (0 until array.length()).map { array.getJSONObject(it) }
            .filter { it.optString("type") == "multiple_choice" }
    }

    private fun assertBalanced(file: String, questions: List<JSONObject>) {
        assertTrue("$file: no multiple-choice questions found", questions.isNotEmpty())
        questions.forEach { q ->
            val id = q.getString("id")
            val options = q.getJSONArray("options").length()
            val index = q.getInt("correctIndex")
            assertTrue("$file/$id: correctIndex $index outside options 0..${options - 1}", index in 0 until options)
        }
        val total = questions.size
        val maxOptionCount = questions.maxOf { q -> q.getJSONArray("options").length() }
        val counts = questions.groupingBy { it.getInt("correctIndex") }.eachCount()

        val expectedPositions = (0 until maxOptionCount).toSet()
        assertEquals("$file: some option slots never hold the answer: $counts", expectedPositions, counts.keys)
        counts.forEach { (position, count) ->
            assertTrue(
                "$file: $count/$total answers sit at position $position (clustered): $counts",
                count * 2 <= total, // no slot holds more than half
            )
            assertTrue(
                "$file: position $position holds only $count/$total answers: $counts",
                count * 10 >= total, // every slot holds at least ~10%
            )
        }
    }

    private fun asExercise(q: JSONObject): Exercise.MultipleChoice {
        val lessonId = if (q.getString("id").startsWith("placement.")) {
            ContentParser.PLACEMENT_LESSON_ID
        } else {
            q.getString("lessonId")
        }
        return ContentParser.parseExercise(q, lessonId) as? Exercise.MultipleChoice
            ?: error("could not parse MC question ${q.getString("id")}")
    }

    @Test
    fun `exercise bundle distributes correct answers across option slots`() {
        assertBalanced("exercises.json", multipleChoiceIn("exercises.json"))
    }

    @Test
    fun `placement bundle distributes correct answers across option slots`() {
        assertBalanced("placement.json", multipleChoiceIn("placement.json"))
    }

    @Test
    fun `shuffle is deterministic and never changes which option is correct`() {
        val questions = multipleChoiceIn("exercises.json") + multipleChoiceIn("placement.json")
        questions.forEach { q ->
            val original = asExercise(q)
            val first = original.shuffledForDisplay()
            val second = original.shuffledForDisplay()
            assertEquals("${original.id}: shuffle is not deterministic", first, second)
            assertEquals("${original.id}: option multiset changed", original.options.sorted(), first.options.sorted())
            assertEquals(
                "${original.id}: correct answer changed",
                original.options[original.correctIndex],
                first.options[first.correctIndex],
            )
        }
    }

    @Test
    fun `shuffle actually relocates answers for most questions`() {
        val questions = multipleChoiceIn("exercises.json") + multipleChoiceIn("placement.json")
        val moved = questions.count { q ->
            val original = asExercise(q)
            original.shuffledForDisplay().options != original.options
        }
        assertTrue("only $moved/${questions.size} questions were relocated by the shuffle", moved >= questions.size / 2)
    }

    @Test
    fun `every content file declares the same bundle version`() {
        val versions = listOf("lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json")
            .associateWith { ContentParser.bundleVersion(contentFile(it).readText()) }
        assertEquals("bundle files declare different contentVersion values: $versions", 1, versions.values.toSet().size)
    }

    /**
     * A word is only visible in the graph when its own lesson owns a node of its type:
     * `KnowledgeEvidence.itemsForCard` credits the card's lesson node of the card's own
     * knowledge type and nothing else, so a lesson that ships words without a VOCABULARY
     * node grades its cards while moving no node at all. 35 lessons were in exactly that
     * state until v65 shipped the missing nodes (their grammar nodes list `VOCABULARY` in
     * `skills`, which is what made the broader attribution rules credit the wrong node).
     * A lesson added with words and no node must fail here rather than grade into nothing.
     */
    @Test
    fun `every lesson that ships words is taught by a vocabulary node`() {
        val words = JSONObject(contentFile("vocabulary.json").readText()).getJSONArray("vocabulary")
        val knowledge = JSONObject(contentFile("knowledge.json").readText()).getJSONArray("knowledge")
        val lessonsWithWords = (0 until words.length())
            .map { words.getJSONObject(it).getString("lessonId") }
            .toSet()
        val taught = (0 until knowledge.length())
            .map { knowledge.getJSONObject(it) }
            .filter { it.getString("type") == "VOCABULARY" }
            .flatMap { node ->
                val lessons = node.getJSONArray("lessons")
                (0 until lessons.length()).map { lessons.getString(it) }
            }
            .toSet()
        val orphans = (lessonsWithWords - taught).sorted()
        assertTrue(
            "${orphans.size} lesson(s) ship words but no VOCABULARY node, so their review " +
                "cards move no node: $orphans",
            orphans.isEmpty(),
        )
    }

    /**
     * Dynamically authored check for the other half of the graph's reachability.
     *
     * Securing a node needs **two** exercise formats ([KnowledgeGates.MIN_FORMATS_TO_SECURE]):
     * one shape answered well can be pattern-matched, which is why a second format is the
     * cheapest honest evidence of transfer. A node whose lessons only ever ask it one way can
     * therefore never be secured, however well the learner knows it — it keeps surfacing in the
     * plan and the long-term claim stays out of reach. That was true of seven nodes until v73:
     * five A1 vocabulary nodes whose only credit came from a listening item (the lesson's
     * "What does X mean?" choices were tagged GRAMMAR, though `AnswerChecker.heuristicMc` reads
     * a Persian-option question as vocabulary), and two B2 discourse nodes whose only credit
     * came from a spoken dialogue turn (their translation carried a VOCABULARY override, so the
     * typed sentence never reached the node).
     *
     * Both halves are the production rule — `KnowledgeEvidence.itemsFor` over the parsed
     * exercise and `AnswerChecker.skillOf`/`formatOf` — so this fails on a content edit, not on
     * a second opinion about the attribution.
     */
    @Test
    fun `every knowledge node can be confirmed in two exercise formats`() {
        val nodes = ContentParser.parseKnowledge(contentFile("knowledge.json").readText())
        val nodesByLesson = nodes
            .flatMap { node -> node.lessonIds.map { lessonId -> lessonId to node } }
            .groupBy({ it.first }, { it.second })
        val formats = mutableMapOf<String, MutableSet<ExerciseFormat>>()
        ContentParser.parseRawExercises(contentFile("exercises.json").readText()).forEach { raw ->
            val exercise = ContentParser.exerciseFromPayload(raw.payloadJson, raw.lessonId) ?: return@forEach
            val lessonNodes = nodesByLesson[raw.lessonId].orEmpty()
            KnowledgeEvidence.itemsFor(lessonNodes, AnswerChecker.skillOf(exercise)).forEach { node ->
                formats.getOrPut(node.id) { mutableSetOf() }.add(AnswerChecker.formatOf(exercise))
            }
        }

        val stuck = nodes.mapNotNull { node ->
            val seen = formats[node.id].orEmpty()
            if (seen.size >= KnowledgeGates.MIN_FORMATS_TO_SECURE) {
                null
            } else {
                val ways = seen.joinToString(", ").ifEmpty { "no exercise asks it" }
                "${node.id} [$ways] via ${node.lessonIds.joinToString()}"
            }
        }

        assertTrue(
            "${stuck.size} knowledge node(s) reachable in fewer than " +
                "${KnowledgeGates.MIN_FORMATS_TO_SECURE} exercise formats, so they can never be secured: $stuck",
            stuck.isEmpty(),
        )
    }

    /**
     * The curriculum's own order: level first, then the lesson's `order` inside it — the same
     * path Home walks, so "before" here means what the learner actually meets first.
     */
    private fun lessonPositions(): Map<String, Int> {
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val lessons = JSONObject(contentFile("lessons.json").readText()).getJSONArray("lessons")
        return (0 until lessons.length())
            .map { lessons.getJSONObject(it) }
            .sortedWith(
                compareBy<JSONObject>(
                    { levels.indexOf(it.getString("level")) },
                    { it.getInt("order") },
                ),
            )
            .mapIndexed { index, lesson -> lesson.getString("id") to index }
            .toMap()
    }

    private fun knowledgeNodes(): List<JSONObject> {
        val array = JSONObject(contentFile("knowledge.json").readText()).getJSONArray("knowledge")
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    private fun strings(o: JSONObject, key: String): List<String> {
        val array = o.optJSONArray(key) ?: return emptyList()
        return (0 until array.length()).map { array.getString(it) }
    }

    /**
     * A prerequisite is a promise about order, so it has to be taught at or before the node that
     * needs it — the same lesson is the normal case (C1's idiom node chains onto the phrasal-verb
     * node taught beside it), but a prerequisite the learner reaches *later* is not.
     *
     * `PrerequisiteEngine` turns an unmet prerequisite into a `REMEDIATE` action — that is what it
     * is for — so a prerequisite introduced *later* in the path sends a learner to material a whole
     * level above them: two B2 vocabulary nodes required the two C1 nodes of `c1.phrasal-idioms`, so
     * studying `b2.idioms` or `b2.phrasal-verbs` first demanded a C1 lesson. The graph's own shape
     * (one chain per topic) makes the honest target the node just before it, never the one after.
     */
    @Test
    fun `a prerequisite is introduced before the node that needs it`() {
        val positions = lessonPositions()
        val nodes = knowledgeNodes()
        fun introducedAt(node: JSONObject): Int = strings(node, "lessons")
            .minOfOrNull { positions[it] ?: Int.MAX_VALUE } ?: Int.MAX_VALUE

        val byId = nodes.associateBy { it.getString("id") }
        val offenders = nodes.flatMap { node ->
            val at = introducedAt(node)
            strings(node, "prerequisites").mapNotNull { prerequisiteId ->
                val prerequisite = byId[prerequisiteId] ?: return@mapNotNull null
                val before = introducedAt(prerequisite)
                // The same lesson is fine — C1's idiom node chains onto the phrasal-verb node
                // taught beside it — but a prerequisite the learner meets later is not.
                if (before <= at) {
                    null
                } else {
                    "${node.getString("id")} (${node.getString("level")}, position $at) requires " +
                        "$prerequisiteId (${prerequisite.getString("level")}, introduced at $before)"
                }
            }
        }

        assertTrue(
            "${offenders.size} prerequisite(s) come after the lesson that needs them, so the " +
                "planner would remediate forward: $offenders",
            offenders.isEmpty(),
        )
        // The level half of the same promise, stated where a reader looks for it.
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val levelSkips = nodes.flatMap { node ->
            strings(node, "prerequisites").mapNotNull { prerequisiteId ->
                val prerequisite = byId[prerequisiteId] ?: return@mapNotNull null
                if (levels.indexOf(prerequisite.getString("level")) > levels.indexOf(node.getString("level"))) {
                    "${node.getString("id")} (${node.getString("level")}) requires a " +
                        "${prerequisite.getString("level")} node (${prerequisiteId})"
                } else {
                    null
                }
            }
        }
        assertTrue("prerequisite(s) from a higher level than the node: $levelSkips", levelSkips.isEmpty())
    }

    /**
     * A node's `level` says where it is introduced, which is what the graph, the docs and a
     * placement read all assume. Two grammar nodes claimed B2 while their first lesson was a B1
     * one (`b1.environment`, `b1.eating-habits`), so the graph advertised material a level above
     * where the learner actually meets it.
     */
    @Test
    fun `a node is levelled where its first lesson sits`() {
        val positions = lessonPositions()
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val lessonLevel = JSONObject(contentFile("lessons.json").readText())
            .getJSONArray("lessons")
            .let { arr ->
                (0 until arr.length()).associate {
                    val lesson = arr.getJSONObject(it)
                    lesson.getString("id") to lesson.getString("level")
                }
            }

        val offenders = knowledgeNodes().mapNotNull { node ->
            val first = strings(node, "lessons")
                .minByOrNull { positions[it] ?: Int.MAX_VALUE } ?: return@mapNotNull null
            val declared = node.getString("level")
            val actual = lessonLevel[first] ?: return@mapNotNull null
            if (levels.indexOf(declared) == levels.indexOf(actual)) null
            else "${node.getString("id")} says $declared but is introduced in $first ($actual)"
        }

        assertTrue(
            "${offenders.size} node(s) levelled away from their first lesson: $offenders",
            offenders.isEmpty(),
        )
    }

    /**
     * Reviewed single-answer fill_blank exercises (checklist A-2), each with the
     * pedagogical reason it may accept exactly one answer. This map IS the
     * report: grep it for the current "open blanks" inventory.
     *
     * Everything else with a single accepted answer fails until equivalents are
     * added or a reason is recorded here; entries that gain equivalents must be
     * removed again (second assertion) so the list never rots.
     *
     * Rule of thumb used when reviewing: a lexical swap inside the SAME
     * grammatical structure is an equivalent (drink/have); an answer that uses
     * a different structure and dodges the grammar target is not ("up to"
     * for a past-continuous blank).
     */
    private val reviewedSingleAnswerBlanks = mapOf(
        "a1.greetings.ex.03" to "unique interrogative — nothing but 'how' completes Hello, ___ are you?",
        "a1.shopping.ex.04" to "quantifier frame — How ___ water has no alternative",
        "a2.hobbies.ex.03" to "infinitive marker — wants ___ learn accepts only 'to'",
        "a2.health.ex.03" to "symptom target — migraine is a different condition, not a synonym",
        "a2.city-life.ex.03" to "There + plural agreement target — alternatives change tense/number",
        "b1.work.ex.04" to "past-simple target — 'do' is a different tense the lesson does not teach",
        "b1.news.ex.03" to "past-simple passive target — 'had been' needs context the sentence lacks",
        "b1.news.ex.04" to "be-passive agreement on 'news' — 'gets' is a different passive structure",
        "b1.experiences.ex.03" to "unique past-perfect auxiliary",
        "b1.experiences.ex.04" to "past-continuous target — 'up to' dodges the -ing structure",
        "b2.media.ex.03" to "unique future-perfect auxiliary",
        "b2.interview.ex.03" to "look forward to + gerund target — no synonym fits the frame",
        "c1.persuasion.ex.03" to "deduction modal with evidence clause — weaker modals change the meaning",
        "c1.culture.ex.03" to "wish + past target — no alternative exists",
        "a1.articles.ex.02" to "indefinite article target — 'egg' starts with a vowel sound, so only 'an' completes it",
        "a1.articles.ex.05" to "definite article target — both speakers know which door is meant, no equivalent",
        "a1.pronouns.ex.02" to "object-pronoun target — the following 'our' can only follow 'us'",
        "a1.can.ex.05" to "ability modal with a deadline frame — 'must' would turn ability into obligation",
        "a1.question-words.ex.05" to "interrogative target — only 'where' asks for a place",
        "a1.age-birthday.ex.05" to "passive 'be born' past form — 'was' is the only auxiliary that agrees with 'I'",
        "a1.days-months.ex.03" to "preposition for a day — only 'on' collocates with Friday in this frame",
        "a1.time.ex.03" to "clock-time preposition — only 'at' precedes a time on the clock",
        "a1.colors.ex.03" to "sky colour is fixed by the facts — only 'blue' is true here",
        "c2.literature.ex.04" to "article frame — 'an ___' admits only a vowel-initial noun, and metaphor/foreshadowing need 'a'",
    )

    @Test
    fun `open blanks report - every single-answer fill_blank is reviewed`() {
        val root = JSONObject(contentFile("exercises.json").readText())
        val array = root.getJSONArray("exercises")
        val singles = (0 until array.length())
            .map { array.getJSONObject(it) }
            .filter { it.optString("type") == "fill_blank" && it.getJSONArray("accepted").length() < 2 }
            .map { it.getString("id") }

        val unreviewed = singles.filterNot { it in reviewedSingleAnswerBlanks }
        assertTrue(
            "single-answer fill_blank(s) without a documented reason — add " +
                "equivalents to accepted or record why only one answer is valid: $unreviewed",
            unreviewed.isEmpty(),
        )
        val stale = reviewedSingleAnswerBlanks.keys.filterNot { it in singles }
        assertTrue(
            "reviewed blank(s) now accept multiple answers — drop them from the allowlist: $stale",
            stale.isEmpty(),
        )
    }

    /**
     * The authored difficulty (v66) and its hand-judged levels (v67 B1, v68 B2, v69 C1, v71 C2),
     * measured on the bundle rather than on a fixture.
     *
     * The selector can only change the **level** of a format where a lesson's items of that
     * format disagree about how hard they are. The seed decided by format band, so inside one
     * level every multiple-choice item got the same number and no within-format level axis
     * exists; the hand pass is where the axis becomes real. That is a fact about the content,
     * so this case states it: when a level is judged, these numbers move and this test says so.
     *
     * The judged levels disagree in different places, which is why each is pinned:
     * B1's axis is inside the multiple-choice drills (`b1.work.ex.02` raised to the hard end),
     * B2's is inside the fill-in-the-blank drills — at B2 a blank that tests a structure
     * (passive, perfect, conditional, quantifier agreement) is the hard end and a blank that
     * tests a word is the easy end, where the seed had called every blank easy — and at C1,
     * whose floor is 2, the hard end is the inversion and mixed-conditional structure the level
     * is built on, so `c1.statistics` separates the agreement trap from the plain one. C2 is read
     * the same way as C1 — its hard end is the inversion and mixed conditional it is built on plus
     * the reading questions that ask what a passage implies — but only 9 of its 30 choice lessons
     * have one, because the other 21 carry the level's hard item in a blank or a production,
     * which is what `c2.literature` shows: its one hard choice item is the only one it has.
     */
    @Test
    fun `the hand-judged levels have a within-format difficulty axis, and it reorders`() {
        val levelByLesson = JSONObject(contentFile("lessons.json").readText())
            .getJSONArray("lessons")
            .let { arr ->
                (0 until arr.length()).associate {
                    val lesson = arr.getJSONObject(it)
                    lesson.getString("id") to lesson.getString("level")
                }
            }
        val exercises = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        val spreadByLevel = mutableMapOf<String, Int>()
        val mcByLesson = mutableMapOf<String, MutableSet<Int>>()
        for (i in 0 until exercises.length()) {
            val exercise = exercises.getJSONObject(i)
            if (exercise.optString("type") != "multiple_choice") continue
            mcByLesson.getOrPut(exercise.getString("lessonId")) { mutableSetOf() }
                .add(exercise.getInt("difficulty"))
        }
        mcByLesson.forEach { (lessonId, difficulties) ->
            val level = levelByLesson[lessonId] ?: return@forEach
            if (difficulties.size > 1) spreadByLevel[level] = (spreadByLevel[level] ?: 0) + 1
        }

        assertEquals(
            "B1 was the first level read item by item (v67): 10 of its 30 choice lessons disagree about difficulty",
            10,
            spreadByLevel["B1"] ?: 0,
        )
        assertEquals(
            "B2 was read item by item in v68: 10 of its 32 choice lessons disagree about difficulty",
            10,
            spreadByLevel["B2"] ?: 0,
        )
        // C1 is the level whose axis is complete: v69 judged it and v70 gave the 22 lessons
        // that had no hard choice item one, so every C1 lesson can now raise recognition.
        assertEquals(
            "C1 was read item by item (v69) and completed in v70: all 31 of its choice lessons " +
                "disagree about difficulty",
            31,
            spreadByLevel["C1"] ?: 0,
        )
        assertEquals(
            "C2 was read item by item in v71: 9 of its 30 choice lessons disagree about difficulty",
            9,
            spreadByLevel["C2"] ?: 0,
        )
        // v72 read the last two levels. A1 and A2 run 1..2, so their axis is "first encounter
        // against more", and only the 17 A1 and 11 A2 lessons that own both a first-encounter
        // item and a one-step-harder one can be reordered inside a format.
        assertEquals(
            "A1 was read item by item in v72: 17 of its 30 choice lessons disagree about difficulty",
            17,
            spreadByLevel["A1"] ?: 0,
        )
        assertEquals(
            "A2 was read item by item in v72: 11 of its 30 choice lessons disagree about difficulty",
            11,
            spreadByLevel["A2"] ?: 0,
        )
        // Every level ships judgement now, so none may still read as seeded — a level whose
        // choice items all share one number cannot be reordered inside that format at all.
        assertEquals(
            "all six levels are hand-judged; a level with no within-format spread is seeded again",
            listOf("A1", "A2", "B1", "B2", "C1", "C2"),
            spreadByLevel.filterValues { it > 0 }.keys.sorted(),
        )

        // And the axis is not decoration: on real content the level decides who is served first.
        val workChoices = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { it.optString("type") == "multiple_choice" && it.getString("lessonId") == "b1.work.lesson-01" }
            .mapNotNull { ContentParser.parseExercise(it, "b1.work.lesson-01") as? Exercise.MultipleChoice }
            .sortedBy { it.id }

        val selector = AdaptiveExerciseSelector()
        val consolidated = selector.next(
            workChoices,
            MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.95f)),
        )
        val shaky = selector.next(
            workChoices,
            MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.4f)),
        )

        assertEquals(
            "b1.work.ex.02 (reported speech + past perfect) is the lesson's hardest choice item",
            "b1.work.ex.02",
            consolidated?.id,
        )
        assertEquals("a shaky meter starts at the easy end instead", 1, shaky?.difficulty)
        assertEquals(
            "reordering never drops or duplicates an item",
            workChoices.toSet(),
            selector.order(
                workChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.95f)),
            ).toSet(),
        )

        // B2's axis is the blank, not the multiple-choice drill: the seed gave every
        // fill_blank in the level the same number, and the hand pass split the two that
        // test a structure (passive + since, present perfect passive) from the one that
        // tests a word (make a ______ about the delay).
        val problemBlanks = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { it.optString("type") == "fill_blank" && it.getString("lessonId") == "b2.problems.lesson-01" }
            .mapNotNull { ContentParser.parseExercise(it, "b2.problems.lesson-01") as? Exercise.FillBlank }
            .sortedBy { it.id }
        assertTrue("b2.problems has to ship blanks to steer", problemBlanks.size >= 3)

        assertEquals(
            "a consolidated recall meter meets the structure blank first, not the word blank",
            "b2.problems.ex.10",
            selector.next(
                problemBlanks,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECALL to 0.95f)),
            )?.id,
        )
        assertEquals(
            "a shaky one starts at the word blank instead",
            1,
            selector.next(
                problemBlanks,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECALL to 0.4f)),
            )?.difficulty,
        )

        // C1's floor is 2, so its axis is not easy against hard but typical against the hard
        // end of the level: `Neither of the samples was significant` is the agreement trap the
        // level is about, while `The data show that the effect is small` is the plain plural.
        val statisticBlanks = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { it.optString("type") == "fill_blank" && it.getString("lessonId") == "c1.statistics.lesson-01" }
            .mapNotNull { ContentParser.parseExercise(it, "c1.statistics.lesson-01") as? Exercise.FillBlank }
            .sortedBy { it.id }
        assertEquals(
            "c1.statistics ships the two agreement blanks this axis needs",
            listOf("c1.statistics.ex.04", "c1.statistics.ex.05"),
            statisticBlanks.map { it.id },
        )
        assertEquals(
            "a consolidated recall meter meets the agreement trap",
            "c1.statistics.ex.05",
            selector.next(
                statisticBlanks,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECALL to 0.95f)),
            )?.id,
        )
        assertEquals(
            "a shaky one starts at the plain plural",
            "c1.statistics.ex.04",
            selector.next(
                statisticBlanks,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECALL to 0.4f)),
            )?.id,
        )

        // C2's hard choice item is the one that reads the narrator's tone against the passage's
        // optimism; the other three choices in that lesson only name a device or a word.
        val literaryChoices = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { it.optString("type") == "multiple_choice" && it.getString("lessonId") == "c2.literature.lesson-01" }
            .mapNotNull { ContentParser.parseExercise(it, "c2.literature.lesson-01") as? Exercise.MultipleChoice }
            .sortedBy { it.id }
        assertEquals(
            "a consolidated recognition meter meets the inference item, not a vocabulary match",
            "c2.literature.ex.08",
            selector.next(
                literaryChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.95f)),
            )?.id,
        )
        assertEquals(
            "a shaky one starts at the vocabulary end",
            "c2.literature.ex.01",
            selector.next(
                literaryChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.4f)),
            )?.id,
        )

        // A1's axis is the article: `a university student` (a vowel letter that is not a
        // vowel sound) is a step past a first encounter, while the word-meaning item is not.
        val articleChoices = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { it.optString("type") == "multiple_choice" && it.getString("lessonId") == "a1.articles.lesson-01" }
            .mapNotNull { ContentParser.parseExercise(it, "a1.articles.lesson-01") as? Exercise.MultipleChoice }
            .sortedBy { it.id }
        assertTrue("a1.articles has to ship choices to steer", articleChoices.size >= 3)
        assertEquals(
            "a consolidated recognition meter meets the a/an choice first (a university student)",
            "a1.articles.ex.01",
            selector.next(
                articleChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.95f)),
            )?.id,
        )
        assertEquals(
            "a shaky one starts at the word-meaning item instead",
            "a1.articles.ex.08",
            selector.next(
                articleChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.4f)),
            )?.id,
        )

        // A2's axis is the comparative: the -er form and `more expensive` are the level's
        // structural work, while naming what "crowded" means is a first encounter.
        val cityChoices = (0 until exercises.length())
            .map { exercises.getJSONObject(it) }
            .filter { it.optString("type") == "multiple_choice" && it.getString("lessonId") == "a2.city-life.lesson-01" }
            .mapNotNull { ContentParser.parseExercise(it, "a2.city-life.lesson-01") as? Exercise.MultipleChoice }
            .sortedBy { it.id }
        assertTrue("a2.city-life has to ship choices to steer", cityChoices.size >= 3)
        assertEquals(
            "a consolidated recognition meter meets the comparative first",
            "a2.city-life.ex.01",
            selector.next(
                cityChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.95f)),
            )?.id,
        )
        assertEquals(
            "a shaky one starts at the word-meaning item instead",
            "a2.city-life.ex.02",
            selector.next(
                cityChoices,
                MasteryProfile(byDimension = mapOf(MasteryDimension.RECOGNITION to 0.4f)),
            )?.id,
        )
    }
}
