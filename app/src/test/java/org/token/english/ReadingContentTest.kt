package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guards the A-5 reading content and the A-6 placement reading items.
 *
 * A-5 asks for real reading comprehension, not one-line questions: a short
 * passage (3–5 sentences) that several questions reuse, plus English→Persian
 * "choose the meaning" items. The `passage` field is what the UI renders above
 * the question, so an item without one is unanswerable when it is re-queued
 * alone — exactly what these checks prevent.
 */
class ReadingContentTest {

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun exercises(): List<JSONObject> {
        val array = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    private val persian = Regex("[\\u0600-\\u06FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]")

    private fun sentences(text: String): Int =
        text.split(Regex("[.!?]+")).count { it.trim().isNotEmpty() }

    @Test
    fun `every authored passage is English and non-blank`() {
        val offenders = exercises()
            .filter { it.has("passage") }
            .mapNotNull { e ->
                val passage = e.optString("passage").trim()
                when {
                    passage.isEmpty() -> "${e.getString("id")}: blank passage"
                    persian.containsMatchIn(passage) -> "${e.getString("id")}: passage is not English"
                    else -> null
                }
            }
        assertTrue("passage problem(s): ${offenders.joinToString()}", offenders.isEmpty())
    }

    @Test
    fun `each level has a comprehension passage that several questions reuse`() {
        val comprehension = exercises().filter { e ->
            e.optString("type") == "multiple_choice" &&
                e.optString("skill") == "READING" &&
                sentences(e.optString("passage")) in 3..5
        }

        val lessons = JSONObject(contentFile("lessons.json").readText()).getJSONArray("lessons")
        val levelOfLesson = (0 until lessons.length()).associate { i ->
            val l = lessons.getJSONObject(i)
            l.getString("id") to l.getString("level")
        }

        val levelsWithPassages = comprehension.mapNotNull { levelOfLesson[it.getString("lessonId")] }.toSet()
        assertEquals(
            "every CEFR level needs a reading passage",
            setOf("A1", "A2", "B1", "B2", "C1", "C2"),
            levelsWithPassages,
        )

        // A passage shared by a single question is a quiz item, not reading practice.
        val reused = comprehension.groupBy { it.getString("passage") }.values
        val lonely = reused.filter { it.size < 2 }.map { it.first().getString("id") }
        assertTrue("comprehension passage(s) used by fewer than two questions: $lonely", lonely.isEmpty())
    }

    @Test
    fun `meaning items offer Persian options with exactly one correct answer`() {
        val meaning = exercises().filter { it.optString("question") == "What does this sentence mean?" }
        assertTrue("no English-to-Persian meaning items authored", meaning.isNotEmpty())

        val problems = meaning.flatMap { e ->
            val id = e.getString("id")
            val options = e.getJSONArray("options")
            val list = (0 until options.length()).map { options.getString(it) }
            val correct = list.getOrNull(e.getInt("correctIndex"))
            buildList {
                if (list.size != 4) add("$id: expected 4 options, found ${list.size}")
                if (list.any { !persian.containsMatchIn(it) }) add("$id: a non-Persian option")
                if (correct == null || !persian.containsMatchIn(correct)) add("$id: correct option is not Persian")
                list.filter { persian.containsMatchIn(it) }.let { persianOptions ->
                    if (persianOptions.distinct().size != persianOptions.size) add("$id: duplicate Persian options")
                }
            }
        }
        assertTrue("meaning item problem(s): ${problems.joinToString()}", problems.isEmpty())
    }

    @Test
    fun `placement measures reading alongside grammar and vocabulary`() {
        val questions = JSONObject(contentFile("placement.json").readText()).getJSONArray("questions")
        val bySkill = (0 until questions.length())
            .map { questions.getJSONObject(it).optString("skill") }
            .groupingBy { it }
            .eachCount()
        assertEquals("each band carries one reading item", 6, bySkill["READING"])
        assertTrue("grammar is still measured", (bySkill["GRAMMAR"] ?: 0) > 0)
        assertTrue("vocabulary is still measured", (bySkill["VOCABULARY"] ?: 0) > 0)
        // Reading items must carry their passage, or they cannot be answered.
        val readingWithoutPassage = (0 until questions.length())
            .map { questions.getJSONObject(it) }
            .filter { it.optString("skill") == "READING" && it.optString("passage").isBlank() }
            .map { it.getString("id") }
        assertTrue("reading placement item(s) without a passage: $readingWithoutPassage", readingWithoutPassage.isEmpty())
    }
}
