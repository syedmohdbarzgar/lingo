package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.Exercise
import java.io.File

/**
 * Conversation practice is only a conversation if the bundle contains both sides
 * of an exchange. These are the authoring rules the screen relies on, checked
 * against the shipped bundle (the same way `ExplanationCoverageTest` holds the
 * explanation backlog):
 *
 *  - a `speaking` item still parses into the domain model;
 *  - a turn that asks a question must also carry its Persian gloss, or the learner
 *    is spoken to in a language they may not understand yet;
 *  - the question is English (it is spoken by TTS) and is not the answer (that
 *    would be an echo, and the scorer would pass the partner's line back);
 *  - the dialogue spread stays CEFR-wide, so practice is not all A2 directions.
 */
class DialogueContentTest {

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun speakingItems(): List<Exercise.Speaking> {
        val root = JSONObject(contentFile("exercises.json").readText())
        val exercises = root.getJSONArray("exercises")
        return (0 until exercises.length()).mapNotNull { i ->
            val o = exercises.getJSONObject(i)
            if (o.optString("type") != "speaking") return@mapNotNull null
            ContentParser.parseExercise(o, o.optString("lessonId")) as? Exercise.Speaking
        }
    }

    /** lessonId → CEFR level, from the lesson the turn belongs to. */
    private fun levelOf(): Map<String, String> {
        val root = JSONObject(contentFile("lessons.json").readText())
        val lessons = root.getJSONArray("lessons")
        return (0 until lessons.length()).associate { i ->
            val o = lessons.getJSONObject(i)
            o.getString("id") to o.getString("level")
        }
    }

    private fun hasPersian(text: String) = text.any { it in '\u0600'..'\u06FF' }

    @Test
    fun `every speaking item parses and keeps its graded answer`() {
        val items = speakingItems()
        assertTrue("the bundle has no speaking items at all", items.isNotEmpty())
        items.forEach { item ->
            val answer = item.referenceAnswers.firstOrNull().orEmpty()
            assertTrue("${item.id}: blank first answer", answer.isNotBlank())
            assertTrue("${item.id}: the answer must be English", !hasPersian(answer))
        }
    }

    @Test
    fun `a dialogue question is English and comes with its Persian gloss`() {
        val withQuestion = speakingItems().filter { it.question != null }
        assertTrue("no dialogue turns authored at all", withQuestion.isNotEmpty())

        withQuestion.forEach { item ->
            val question = item.question.orEmpty()
            assertTrue("${item.id}: question must be English", !hasPersian(question))
            assertNotNull("${item.id}: question without a Persian gloss", item.questionFa)
            assertTrue(
                "${item.id}: questionFa must be Persian so the learner knows what was asked",
                hasPersian(item.questionFa.orEmpty()),
            )
            assertTrue(
                "${item.id}: the question must not be the answer itself",
                !question.trim().equals(item.referenceAnswers.first().trim(), ignoreCase = true),
            )
        }
    }

    @Test
    fun `the dialogue spread reaches every practised level`() {
        val levels = levelOf()
        val byLevel = speakingItems()
            .filter { it.question != null }
            .groupBy { levels[it.lessonId] }
            .mapValues { it.value.size }

        // A1 uses single-clause replies, B2 a hypothesis: practice that lived on
        // one level would leave the other levels with nothing to say.
        listOf("A1", "A2", "B1", "B2").forEach { level ->
            assertTrue(
                "no dialogue turn at $level — the spread is $byLevel",
                (byLevel[level] ?: 0) > 0,
            )
        }
    }
}
