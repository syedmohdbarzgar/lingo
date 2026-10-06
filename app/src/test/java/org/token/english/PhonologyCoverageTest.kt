package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A-7: phonology / pronunciation coverage of the A1 curriculum.
 *
 * The content is plain JSON, so the Kotlin compiler cannot notice a lesson that
 * lost its pronunciation teaching. These tests pin the three surfaces the
 * feature actually ships:
 *
 *  1. a `## تلفظ` section in the lesson's teaching page (the A-8 parser renders
 *     it as its own CUSTOM card — vowel length, word stress, word sounds);
 *  2. at least one PHONOLOGY knowledge item teaching the lesson, so the graph
 *     has a node for pronunciation and the Progress screen can show it;
 *  3. a guided listening drill (a listening exercise with an `explanationFa`
 *     naming what to listen for) — listening practice is what moves a
 *     phonology node, because `KnowledgeEvidence` credits LISTENING-tagged
 *     nodes on every listening attempt.
 *
 * Plus one graph safety rule: PHONOLOGY nodes are leaves here, so a lesson can
 * never be locked behind pronunciation mastery that only listening builds.
 */
class PhonologyCoverageTest {

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

    private fun a1Lessons(): List<JSONObject> =
        rows("lessons.json", "lessons").filter { it.optString("level") == "A1" }

    private fun knowledgeItems(): List<JSONObject> = rows("knowledge.json", "knowledge")

    private fun phonologyItems(): List<JSONObject> =
        knowledgeItems().filter { it.optString("type").equals("PHONOLOGY", ignoreCase = true) }

    private fun strings(o: JSONObject, key: String): List<String> {
        val array = o.optJSONArray(key) ?: return emptyList()
        return (0 until array.length()).map { array.getString(it) }
    }

    @Test
    fun `every A1 lesson carries a pronunciation section in its teaching page`() {
        val missing = a1Lessons()
            .filter { lesson ->
                lesson.optString("grammarTipFa").lineSequence().none { it.startsWith("## تلفظ") }
            }
            .map { it.getString("id") }
        assertTrue(
            "A1 lesson(s) without a '## تلفظ' section — vowel length and word " +
                "sounds must be taught there too: $missing",
            missing.isEmpty(),
        )
    }

    @Test
    fun `every A1 lesson is taught by a phonology knowledge item`() {
        val covered = phonologyItems().flatMap { strings(it, "lessons") }.toSet()
        val missing = a1Lessons().map { it.getString("id") }.filterNot { it in covered }
        assertTrue(
            "A1 lesson(s) with no PHONOLOGY knowledge item — pronunciation would " +
                "have no node in the graph: $missing",
            missing.isEmpty(),
        )
    }

    @Test
    fun `every A1 lesson has a guided listening drill`() {
        val drills = rows("exercises.json", "exercises")
            .filter {
                it.optString("type") == "listening" && it.optString("explanationFa").isNotBlank()
            }
            .groupBy { it.getString("lessonId") }
        val missing = a1Lessons()
            .map { it.getString("id") }
            .filter { (drills[it]?.size ?: 0) == 0 }
        assertTrue(
            "A1 lesson(s) without a listening drill that explains what to listen " +
                "for: $missing",
            missing.isEmpty(),
        )
    }

    @Test
    fun `phonology nodes listen for listening attempts and never block a lesson`() {
        val items = phonologyItems()
        assertTrue("no PHONOLOGY knowledge items authored", items.isNotEmpty())

        val prerequisitesOf = knowledgeItems().flatMap { strings(it, "prerequisites") }.toSet()
        items.forEach { item ->
            val id = item.getString("id")
            assertTrue(
                "$id must list the LISTENING skill — listening is the only evidence " +
                    "axis it can collect today (SPEAKING is suspended)",
                strings(item, "skills").any { it.equals("LISTENING", ignoreCase = true) },
            )
            assertTrue(
                "$id is a prerequisite of another item — pronunciation mastery only " +
                    "grows from listening, so gating content on it would strand learners",
                id !in prerequisitesOf,
            )
        }
    }
}
