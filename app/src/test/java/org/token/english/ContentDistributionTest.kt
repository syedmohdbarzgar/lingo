package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.Exercise
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
        "b1.plans.ex.03" to "unique second-conditional modal",
        "b1.experiences.ex.03" to "unique past-perfect auxiliary",
        "b1.experiences.ex.04" to "past-continuous target — 'up to' dodges the -ing structure",
        "b2.media.ex.03" to "unique future-perfect auxiliary",
        "b2.interview.ex.03" to "look forward to + gerund target — no synonym fits the frame",
        "c1.persuasion.ex.03" to "deduction modal with evidence clause — weaker modals change the meaning",
        "c1.culture.ex.03" to "wish + past target — no alternative exists",
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
}
