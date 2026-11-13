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
 * for a sentence, which is the format the type was added to get past. v80 gave each
 * level a second text, and a level back at one is the same gap half-closed — so the
 * bar is **two per level**, in different lessons, asking for different structures.
 * A count alone is not enough either: two texts of one level that want the same
 * anchors ask the learner to write the same paragraph twice. The first case makes
 * that structural. The second is the same fairness rule the validator applies at seed
 * time, restated here against the parsed domain objects, because the exemplar is the
 * model a learner compares their own text with after a miss.
 */
class WritingContentTest {

    private val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    /** Level of every lesson, read from the bundle rather than inferred from the id. */
    private fun levelByLesson(): Map<String, String> {
        val lessons = JSONObject(contentFile("lessons.json").readText()).getJSONArray("lessons")
        return (0 until lessons.length()).associate { i ->
            val o = lessons.getJSONObject(i)
            o.getString("id") to o.getString("level")
        }
    }

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
    fun `every level ships two written texts, not one`() {
        // Two, because one is the state v79 shipped and it is one edit away from none:
        // a level whose only text is dropped or re-levelled silently turns its writing
        // step back into a sentence prompt. The second is also what makes "a different
        // structure" checkable within a level at all.
        val levelOf = levelByLesson()
        val perLevel = writingItems().groupingBy { levelOf[it.lessonId] ?: "?" }.eachCount()

        for (level in levels) {
            assertTrue(
                "$level ships ${perLevel[level] ?: 0} writing item(s) — every level needs two, " +
                    "each asking for a different structure (found: $perLevel)",
                (perLevel[level] ?: 0) >= 2,
            )
        }
        assertTrue(
            "a writing item outside the six levels: ${perLevel.filterKeys { it !in levels }}",
            perLevel.keys.all { it in levels },
        )
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
    fun `each level's texts ask for different structures, in different lessons`() {
        // The point of a second item per level is a second ask, not a second subject:
        // two texts of one level must not want the same anchors, and one lesson may not
        // carry both (one step cannot tell two prompts apart).
        val levelOf = levelByLesson()
        val byLevel = writingItems().groupBy { levelOf[it.lessonId] ?: "?" }

        val repeats = byLevel.flatMap { (level, items) ->
            items.groupBy { item -> item.mustUse.map { it.trim().lowercase() }.sorted() }
                .filterValues { it.size > 1 }
                .map { (anchors, _) -> "$level: $anchors" }
        }
        assertTrue("levels whose texts share one anchor set: $repeats", repeats.isEmpty())

        val collisions = byLevel.filterValues { items ->
            items.map { it.lessonId }.distinct().size != items.size
        }
        assertTrue("a lesson asks for two texts: ${collisions.keys}", collisions.isEmpty())
    }
}
