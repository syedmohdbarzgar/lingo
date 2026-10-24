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
               "explanationFa": "توضیح"}
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
}
