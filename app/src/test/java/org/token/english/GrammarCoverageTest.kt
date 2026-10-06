package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Skill
import java.io.File

/**
 * A-8 acceptance bar: **every GRAMMAR knowledge item is practised by at least
 * six grammar-targeting exercises.**
 *
 * Counting uses the production rule — `AnswerChecker.skillOf` over the parsed
 * exercise — so the number here is the same number the mastery engine sees: a
 * lesson whose mix is dominated by vocabulary choices no longer counts as
 * "grammar practice", which is exactly how 30 topics ended up at 1–5.
 *
 * Attribution is lesson-level (`KnowledgeEvidence`), so an exercise counts for
 * every GRAMMAR item its lesson teaches, and each item is counted across all
 * of its lessons.
 */
class GrammarCoverageTest {

    private val minimumGrammarExercises = 6

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private fun rows(file: String, key: String): List<JSONObject> {
        val array = JSONObject(contentFile(file).readText()).getJSONArray(key)
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    private fun strings(o: JSONObject, key: String): List<String> {
        val array = o.optJSONArray(key) ?: return emptyList()
        return (0 until array.length()).map { array.getString(it) }
    }

    /** Grammar-skill exercises per lesson, via the same rule the app uses. */
    private fun grammarExercisesByLesson(): Map<String, Int> =
        rows("exercises.json", "exercises")
            .groupBy { it.getString("lessonId") }
            .mapValues { (_, exercises) ->
                exercises.count { row ->
                    val lessonId = row.getString("lessonId")
                    val exercise = ContentParser.parseExercise(row, lessonId) ?: return@count false
                    AnswerChecker.skillOf(exercise) == Skill.GRAMMAR
                }
            }

    @Test
    fun `every grammar topic has at least six grammar-targeting exercises`() {
        val perLesson = grammarExercisesByLesson()
        val grammarItems = rows("knowledge.json", "knowledge")
            .filter { it.optString("type").equals("GRAMMAR", ignoreCase = true) }
        assertTrue("no GRAMMAR knowledge items authored", grammarItems.isNotEmpty())

        val short = grammarItems.mapNotNull { item ->
            val id = item.getString("id")
            val lessons = strings(item, "lessons")
            val count = lessons.sumOf { perLesson[it] ?: 0 }
            if (count < minimumGrammarExercises) {
                "$id has $count (lessons: ${lessons.joinToString()})"
            } else {
                null
            }
        }

        assertTrue(
            "grammar topic(s) below the $minimumGrammarExercises-exercise practice bar — " +
                "add exercises to their lessons: $short",
            short.isEmpty(),
        )
    }
}
