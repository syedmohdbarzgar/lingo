package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import java.io.File

/**
 * The `writing` type is reachable from every level, and every shipped text is one
 * its own item accepts.
 *
 * The type shipped in v79 with six items — one per level — in the six lessons whose
 * topic was obviously a piece of writing, and nothing stopped a later pass from
 * dropping one: a level with no text simply has a writing step that only ever asks
 * for a sentence, which is the format the type was added to get past. The first case
 * makes that structural. The second is the same fairness rule the validator applies
 * at seed time, restated here against the parsed domain objects, because the
 * exemplar is the model a learner compares their own text with after a miss.
 */
class WritingContentTest {

    private fun contentFile(name: String): File {
        // Content tests read assets through File, so they run with --rerun.
        val fromApp = File("src/main/assets/content/$name")
        return if (fromApp.exists()) fromApp else File("app/src/main/assets/content/$name")
    }

    private fun writingItems(): List<Exercise.Writing> {
        // Parsed one by one: `parseExercises` filters to one lesson, and this reads
        // the whole bundle.
        val array = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        return (0 until array.length()).mapNotNull { i ->
            val o = array.getJSONObject(i)
            ContentParser.parseExercise(o, o.getString("lessonId")) as? Exercise.Writing
        }
    }

    @Test
    fun `every level ships at least one written text`() {
        val lessons = JSONObject(contentFile("lessons.json").readText()).getJSONArray("lessons")
        val levelOf = (0 until lessons.length()).associate { i ->
            val o = lessons.getJSONObject(i)
            o.getString("id") to o.getString("level")
        }

        val perLevel = writingItems().groupingBy { levelOf[it.lessonId] ?: "?" }.eachCount()

        for (level in listOf("A1", "A2", "B1", "B2", "C1", "C2")) {
            assertTrue(
                "$level ships no writing item — its writing step can only ever ask for a " +
                    "sentence, which is the format the type exists to get past (found: $perLevel)",
                (perLevel[level] ?: 0) >= 1,
            )
        }
    }

    @Test
    fun `no lesson asks for two texts`() {
        // One text per lesson: two prompts on the same topic in one step is the same
        // writing task twice, and the step's own order has no way to tell them apart.
        val duplicated = writingItems().groupingBy { it.lessonId }.eachCount().filter { it.value > 1 }

        assertTrue("lessons with more than one writing item: $duplicated", duplicated.isEmpty())
    }

    @Test
    fun `every shipped text is one its own item accepts`() {
        val offenders = writingItems().filter { item ->
            item.accepted.isEmpty() || item.accepted.none { AnswerChecker.isCorrect(item, it) }
        }

        assertTrue(
            "these writing items show a model their own grader rejects: " +
                offenders.joinToString(", ") { it.id },
            offenders.isEmpty(),
        )
    }

    @Test
    fun `every shipped item asks for at least two sentences of real text`() {
        // The floors the validator enforces, restated on the parsed items: a text with
        // a one-sentence floor or a two-word floor is a sentence with a longer name.
        val offenders = writingItems().filter { it.minSentences < 2 || it.minWords < 8 }

        assertTrue(
            "writing items below the text floor: " +
                offenders.joinToString(", ") { "${it.id} (${it.minWords} words, ${it.minSentences} sentences)" },
            offenders.isEmpty(),
        )
    }

    @Test
    fun `every level's items ask for different structures`() {
        // The point of a second item per level is a second ask, not a second subject:
        // two texts of one level must not want the same anchors.
        val byLevel = writingItems().groupingBy { it.lessonId.substringBefore('.') }.eachCount()
        assertTrue("the test needs at least two items somewhere: $byLevel", byLevel.values.any { it >= 2 })

        val levels = writingItems()
            .groupBy { it.lessonId.substringBefore('.') }
            .filterValues { it.size > 1 }
        val repeats = levels.flatMap { (level, items) ->
            items.groupBy { it.mustUse.sorted() }.filterValues { it.size > 1 }.map { level }
        }
        assertTrue("levels whose texts share one anchor set: $repeats", repeats.isEmpty())
        assertEquals(
            "a level's items live in different lessons",
            levels.values.flatten().map { it.lessonId }.distinct().size,
            levels.values.sumOf { it.size },
        )
    }
}
