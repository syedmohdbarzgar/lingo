package org.token.english

import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.token.english.content.ContentValidator
import java.io.File

/**
 * The content gate as a unit test (AGENTS.md §5): the SAME `ContentValidator`
 * that `./gradlew validateContent` executes before every build runs here on the
 * host JVM, so the suite and the build cannot disagree about what "valid content"
 * means — one implementation, two consumers.
 *
 * The mutation fixtures matter as much as the green run: a validator that never
 * fails would pass the bundle by accident, so each fixture breaks exactly one
 * rule and asserts the gate names it.
 */
class ContentValidationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun contentDir(): File {
        val candidates = listOf(
            File("src/main/assets/content"),
            File("app/src/main/assets/content"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content directory not found")
    }

    // --- the real bundle -------------------------------------------------

    @Test
    fun `the bundled content passes the gate`() {
        val report = ContentValidator.validate(contentDir())
        assertTrue(report.errors.joinToString("\n"), report.errors.isEmpty())
        assertTrue("summary should count the bundle", report.summary.contains("Content validation passed"))
    }

    // --- mutation fixtures: the gate must actually fail ------------------

    /** A minimal but fully valid bundle: one lesson with one word, one exercise, one node. */
    private fun writeValidBundle(): File {
        val dir = tmp.newFolder("content")
        dir.resolve("lessons.json").writeText(
            """
            {"contentVersion": 1, "lessons": [
              {"id": "l1", "level": "A1", "title": "T", "titleFa": "ت", "topic": "greetings",
               "order": 1, "estimatedMinutes": 5}
            ]}
            """.trimIndent(),
        )
        dir.resolve("vocabulary.json").writeText(
            """
            {"contentVersion": 1, "vocabulary": [
              {"id": "l1.word.hello", "lessonId": "l1", "word": "hello", "translation": "سلام",
               "level": "A1",
               "examples": [
                 {"en": "Hello there.", "fa": "سلام."},
                 {"en": "She said hello.", "fa": "او سلام گفت."}
               ]}
            ]}
            """.trimIndent(),
        )
        dir.resolve("exercises.json").writeText(
            """
            {"contentVersion": 1, "exercises": [
              {"id": "l1.ex.01", "lessonId": "l1", "type": "multiple_choice", "order": 1,
               "question": "Which one?", "options": ["a", "b", "c", "d"], "correctIndex": 0,
               "explanationFa": "توضیح"},
              {"id": "l1.dlg.01", "lessonId": "l1", "type": "speaking", "order": 2,
               "sceneFa": "کلاس", "partnerFa": "معلم", "prompt": "جواب بده",
               "question": "How are you?", "questionFa": "حالت چطور است؟",
               "accepted": ["I'm fine, thank you."], "skill": "SPEAKING", "explanationFa": "توضیح"},
              {"id": "l1.dlg.02", "lessonId": "l1", "type": "speaking", "order": 3,
               "sceneFa": "کلاس", "partnerFa": "معلم", "prompt": "جواب بده",
               "question": "Where are you from?", "questionFa": "اهل کجایی؟",
               "accepted": ["I'm from Tehran."], "skill": "SPEAKING", "explanationFa": "توضیح"},
              {"id": "l1.dlg.03", "lessonId": "l1", "type": "speaking", "order": 4,
               "sceneFa": "کلاس", "partnerFa": "معلم", "prompt": "جواب بده",
               "question": "Do you study here?", "questionFa": "اینجا درس می‌خوانی؟",
               "accepted": ["Yes, I do."], "skill": "SPEAKING", "explanationFa": "توضیح"},
              {"id": "l1.dlg.04", "lessonId": "l1", "type": "speaking", "order": 5,
               "sceneFa": "کلاس", "partnerFa": "معلم", "prompt": "جواب بده",
               "question": "See you tomorrow?", "questionFa": "فردا می‌بینمت؟",
               "accepted": ["See you tomorrow."], "skill": "SPEAKING", "explanationFa": "توضیح"}
            ]}
            """.trimIndent(),
        )
        dir.resolve("placement.json").writeText(
            """
            {"contentVersion": 1, "questions": [
              {"id": "placement.q01", "type": "multiple_choice", "level": "A1",
               "question": "Pick one?", "options": ["a", "b", "c", "d"], "correctIndex": 1,
               "skill": "GRAMMAR"}
            ]}
            """.trimIndent(),
        )
        dir.resolve("knowledge.json").writeText(
            """
            {"contentVersion": 1, "knowledge": [
              {"id": "k1", "type": "GRAMMAR", "level": "A1", "title": "T", "titleFa": "ت",
               "prerequisites": [], "lessons": ["l1"], "skills": ["GRAMMAR"]}
            ]}
            """.trimIndent(),
        )
        return dir
    }

    private fun assertFails(dir: File, vararg expected: String) {
        val report = ContentValidator.validate(dir)
        assertTrue("expected the gate to fail, got: ${report.errors}", report.errors.isNotEmpty())
        val all = report.errors.joinToString("\n")
        expected.forEach { needle ->
            assertTrue("expected error mentioning <$needle>, got:\n$all", all.contains(needle))
        }
    }

    @Test
    fun `a valid fixture passes`() {
        val report = ContentValidator.validate(writeValidBundle())
        assertTrue(report.errors.joinToString("\n"), report.errors.isEmpty())
    }

    @Test
    fun `a dangling lessonId fails`() {
        val dir = writeValidBundle()
        val file = dir.resolve("exercises.json")
        file.writeText(file.readText().replace("\"l1\"", "\"l404\""))
        assertFails(dir, "lessonId \"l404\" is not a lesson")
    }

    @Test
    fun `an out-of-range correctIndex fails`() {
        val dir = writeValidBundle()
        val file = dir.resolve("exercises.json")
        file.writeText(file.readText().replace("\"correctIndex\": 0", "\"correctIndex\": 7"))
        assertFails(dir, "correctIndex 7 outside 0..3")
    }

    @Test
    fun `a duplicate id fails`() {
        val dir = writeValidBundle()
        // Two vocabulary rows sharing one primary key: Room would silently keep one.
        dir.resolve("vocabulary.json").writeText(
            """
            {"contentVersion": 1, "vocabulary": [
              {"id": "l1.word.hello", "lessonId": "l1", "word": "hello", "translation": "سلام",
               "level": "A1",
               "examples": [
                 {"en": "Hello there.", "fa": "سلام."},
                 {"en": "She said hello.", "fa": "او سلام گفت."}
               ]},
              {"id": "l1.word.hello", "lessonId": "l1", "word": "hallo", "translation": "سلام",
               "level": "A1",
               "examples": [
                 {"en": "Hallo there.", "fa": "سلام."},
                 {"en": "She said hallo.", "fa": "او سلام گفت."}
               ]}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "duplicate id \"l1.word.hello\"")
    }

    @Test
    fun `disagreeing bundle versions fail`() {
        val dir = writeValidBundle()
        val file = dir.resolve("knowledge.json")
        file.writeText(file.readText().replace("\"contentVersion\": 1", "\"contentVersion\": 2"))
        assertFails(dir, "contentVersion", "files disagree")
    }

    @Test
    fun `a prerequisite cycle fails`() {
        val dir = writeValidBundle()
        // Two nodes that require each other cannot be ordered by KnowledgeGraph.
        dir.resolve("knowledge.json").writeText(
            """
            {"contentVersion": 1, "knowledge": [
              {"id": "k1", "type": "GRAMMAR", "level": "A1", "title": "T", "titleFa": "ت",
               "prerequisites": ["k2"], "lessons": ["l1"], "skills": ["GRAMMAR"]},
              {"id": "k2", "type": "GRAMMAR", "level": "A1", "title": "T", "titleFa": "ت",
               "prerequisites": ["k1"], "lessons": ["l1"], "skills": ["GRAMMAR"]}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "participates in a prerequisite cycle")
    }

    @Test
    fun `a fill_blank without a skill tag fails`() {
        val dir = writeValidBundle()
        dir.resolve("exercises.json").writeText(
            """
            {"contentVersion": 1, "exercises": [
              {"id": "l1.ex.01", "lessonId": "l1", "type": "fill_blank", "order": 1,
               "sentence": "I ___ a student.", "accepted": ["am"], "explanationFa": "توضیح"}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "fill_blank must declare skill")
    }

    @Test
    fun `a typed placement item is held to the lesson exercise rules`() {
        val dir = writeValidBundle()
        dir.resolve("placement.json").writeText(
            """
            {"contentVersion": 1, "questions": [
              {"id": "placement.t01", "type": "fill_blank", "level": "A1",
               "sentence": "I ___ here.", "accepted": [], "skill": "GRAMMAR"}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "no accepted answers")
    }

    @Test
    fun `an audio question cannot enter placement`() {
        val dir = writeValidBundle()
        dir.resolve("placement.json").writeText(
            """
            {"contentVersion": 1, "questions": [
              {"id": "placement.q01", "type": "listening", "level": "A1",
               "question": "Listen", "text": "hello", "accepted": ["hello"],
               "options": ["a", "b", "c", "d"], "correctIndex": 0, "skill": "LISTENING"}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "not answerable here")
    }

    @Test
    fun `a lesson with no content fails coverage`() {
        val dir = writeValidBundle()
        dir.resolve("lessons.json").writeText(
            """
            {"contentVersion": 1, "lessons": [
              {"id": "l1", "level": "A1", "title": "T", "titleFa": "ت", "topic": "greetings",
               "order": 1, "estimatedMinutes": 5},
              {"id": "l2", "level": "A1", "title": "T2", "titleFa": "ت۲", "topic": "numbers",
               "order": 2, "estimatedMinutes": 5}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "lesson has no exercises", "lesson has no vocabulary", "no knowledge item teaches this lesson")
    }

    // --- Phase 3: the semantic rules -------------------------------------

    /**
     * Swaps the fixture's one choice item for [json] (one or more items), keeping the
     * four dialogue turns the coverage rule requires. Injecting into the written bundle
     * keeps each fixture to the rule it breaks.
     */
    private fun replaceFirstExercise(dir: File, json: String) {
        val file = dir.resolve("exercises.json")
        val original = file.readText()
        val start = original.indexOf("{\"id\": \"l1.ex.01\"")
        val end = original.indexOf("}, ", start).let {
            if (it >= 0) it + 1 else original.indexOf("},\n", start) + 1
        }
        require(start >= 0 && end > start) { "fixture exercise not found" }
        file.writeText(original.substring(0, start) + json + original.substring(end))
    }

    @Test
    fun `an accepted model under the item's own word floor fails`() {
        val dir = writeValidBundle()
        replaceFirstExercise(
            dir,
            """{"id": "l1.ex.01", "lessonId": "l1", "type": "sentence_production", "order": 6, "prompt": "این جمله را بنویس.", "mustUse": ["evidence"], "minWords": 6, "accepted": ["the evidence holds"], "skill": "WRITING", "explanationFa": "توضیح"}""",
        )
        assertFails(dir, "under minWords 6")
    }

    @Test
    fun `a listening item whose accepted answers ignore the spoken text fails`() {
        val dir = writeValidBundle()
        replaceFirstExercise(
            dir,
            """{"id": "l1.ex.01", "lessonId": "l1", "type": "listening", "order": 6, "text": "Close the door.", "accepted": ["shut the door"], "skill": "LISTENING", "explanationFa": "توضیح"}""",
        )
        assertFails(dir, "no accepted answer matches the spoken text")
    }

    @Test
    fun `a hyphen and a number are the same answer`() {
        val dir = writeValidBundle()
        // "twenty-six" vs "twenty six" must not be a finding: the rule compares the
        // sentence, not its punctuation.
        replaceFirstExercise(
            dir,
            """{"id": "l1.ex.01", "lessonId": "l1", "type": "listening", "order": 6, "text": "The alphabet has twenty-six letters.", "accepted": ["the alphabet has twenty six letters"], "skill": "LISTENING", "explanationFa": "توضیح"}""",
        )
        val report = ContentValidator.validate(dir)
        assertTrue(
            "a hyphenated word and its spaced form are the same answer: ${report.errors}",
            report.errors.none { it.contains("no accepted answer matches") },
        )
    }

    @Test
    fun `a question that already contains its own answer fails`() {
        val dir = writeValidBundle()
        replaceFirstExercise(
            dir,
            """{"id": "l1.ex.01", "lessonId": "l1", "type": "multiple_choice", "order": 6, "question": "Which word is right?", "options": ["right", "left", "up", "down"], "correctIndex": 0, "skill": "VOCABULARY", "explanationFa": "توضیح"}""",
        )
        assertFails(dir, "question already contains the correct option")
    }

    @Test
    fun `the same recall question twice in the bundle fails`() {
        val dir = writeValidBundle()
        replaceFirstExercise(
            dir,
            """{"id": "l1.ex.01", "lessonId": "l1", "type": "word_recall", "order": 6, "prompt": "سرفه", "accepted": ["cough"], "skill": "VOCABULARY", "explanationFa": "توضیح"},""" +
                """{"id": "l1.ex.02", "lessonId": "l1", "type": "word_recall", "order": 7, "prompt": "سرفه", "accepted": ["cough"], "skill": "VOCABULARY", "explanationFa": "توضیح"}""",
        )
        assertFails(dir, "the same recall question is asked twice")
    }

    @Test
    fun `one entry cannot ship the same example twice`() {
        val dir = writeValidBundle()
        dir.resolve("vocabulary.json").writeText(
            """
            {"contentVersion": 1, "vocabulary": [
              {"id": "l1.word.hello", "lessonId": "l1", "word": "hello", "translation": "سلام",
               "level": "A1",
               "examples": [
                 {"en": "Hello there.", "fa": "سلام."},
                 {"en": "Hello there.", "fa": "سلام."}
               ]}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "repeats an earlier example of the same word")
    }

    @Test
    fun `the same word cannot show the same example at two levels`() {
        val dir = writeValidBundle()
        // Two lessons, two entries for "menu", one sentence: the A2 entry exists to show
        // a harder context, so reusing the A1 sentence leaves that slot empty.
        dir.resolve("vocabulary.json").writeText(
            """
            {"contentVersion": 1, "vocabulary": [
              {"id": "l1.word.menu", "lessonId": "l1", "word": "menu", "translation": "منو",
               "level": "A1",
               "examples": [
                 {"en": "Can I see the menu, please?", "fa": "می‌شود منو را ببینم؟"},
                 {"en": "The menu is on the table.", "fa": "منو روی میز است."}
               ]},
              {"id": "l1.word.dish", "lessonId": "l1", "word": "dish", "translation": "غذا",
               "level": "A1",
               "examples": [
                 {"en": "This dish is hot.", "fa": "این غذا تند است."},
                 {"en": "The dish costs ten dollars.", "fa": "این غذا ده دالر است."}
               ]},
              {"id": "l1.word.menu2", "lessonId": "l1", "word": "menu", "translation": "منو",
               "level": "A2",
               "examples": [
                 {"en": "Can I see the menu, please?", "fa": "می‌شود منو را ببینم؟"},
                 {"en": "The menu lists the drinks.", "fa": "منو نوشیدنی‌ها را دارد."}
               ]}
            ]}
            """.trimIndent(),
        )
        assertFails(dir, "repeats the one in l1.word.menu for the same word")
    }

    @Test
    fun `one item shipped in two lessons fails`() {
        val dir = writeValidBundle()
        replaceFirstExercise(
            dir,
            """{"id": "l1.ex.01", "lessonId": "l1", "type": "multiple_choice", "order": 6, "question": "Where is the cat?", "options": ["on the mat", "in the box", "under the bed", "by the door"], "correctIndex": 0, "skill": "VOCABULARY", "explanationFa": "توضیح"},""" +
                """{"id": "l1.ex.02", "lessonId": "l1", "type": "multiple_choice", "order": 7, "question": "Where is the cat?", "options": ["on the mat", "in the box", "under the bed", "by the door"], "correctIndex": 0, "skill": "VOCABULARY", "explanationFa": "توضیح"}""",
        )
        assertFails(dir, "the same item is shipped 2 times")
    }
}
