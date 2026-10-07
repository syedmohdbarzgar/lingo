package org.token.english

import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Skill
import java.io.File

/**
 * A-1 acceptance bar: **grammar and fill-in-the-blank exercises must explain a
 * miss.** A wrong answer is the moment a learner is most receptive, so the two
 * question shapes that test a rule rather than recall have to say *why* the
 * right answer is right.
 *
 * The required set is the production set: every exercise whose
 * `AnswerChecker.skillOf` is GRAMMAR (explicit tag or heuristic) plus every
 * `fill_blank`, since a fill-in-the-blank is exactly where a one-line reason helps.
 *
 * Checklist A-1 set the acceptance bar at 90% and asked for the floor to be
 * raised over time. The backlog is closed and the bundle now sits at 100%, so the
 * floor is 1.0: a new grammar or fill-blank exercise is expected to ship with its
 * explanation. Lower the constant (the bar is deliberately one number here) if a
 * batch needs to land before its explanations do.
 */
class ExplanationCoverageTest {

    private val minimumExplanationRatio = 1.0

    /** Every exercise in the bundle today carries one; keep it that way. */
    private val minimumOverallRatio = 1.0

    private fun contentFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/content/$name"),
            File("app/src/main/assets/content/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: error("content file not found: $name")
    }

    private data class Row(
        val id: String,
        val type: String,
        val skill: Skill?,
        val hasExplanation: Boolean,
        val explanationLength: Int,
    ) {
        /** The two shapes A-1 requires an explanation on. */
        val needsExplanation: Boolean get() = type == "fill_blank" || skill == Skill.GRAMMAR
    }

    private fun exerciseRows(): List<Row> {
        val array = JSONObject(contentFile("exercises.json").readText()).getJSONArray("exercises")
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            val exercise = ContentParser.parseExercise(o, o.optString("lessonId"))
            val explanation = o.optString("explanationFa").trim()
            Row(
                id = o.getString("id"),
                type = o.getString("type"),
                skill = exercise?.let { AnswerChecker.skillOf(it) },
                hasExplanation = explanation.isNotEmpty(),
                explanationLength = explanation.length,
            )
        }
    }

    @Test
    fun `grammar and fill-blank exercises explain the answer`() {
        val required = exerciseRows().filter { it.needsExplanation }
        assertTrue("no grammar / fill-blank exercises authored", required.isNotEmpty())

        val covered = required.count { it.hasExplanation }
        val ratio = covered.toDouble() / required.size
        val missing = required.filterNot { it.hasExplanation }.map { it.id }

        assertTrue(
            "only ${"%.0f".format(ratio * 100)}% of grammar/fill-blank exercises explain the answer " +
                "(bar ${"%.0f".format(minimumExplanationRatio * 100)}%); missing: " +
                missing.joinToString(", "),
            ratio >= minimumExplanationRatio,
        )
    }

    /**
     * The whole bundle, not just the grammar/fill-blank core: a vocabulary meaning
     * question, a translation or a listening item also teaches best when it says
     * why the answer is the answer.
     */
    @Test
    fun `every exercise has an explanation`() {
        val rows = exerciseRows()
        val covered = rows.count { it.hasExplanation }
        val ratio = covered.toDouble() / rows.size
        val missing = rows.filterNot { it.hasExplanation }.map { it.id }
        assertTrue(
            "only ${covered}/${rows.size} exercises explain the answer " +
                "(bar ${(minimumOverallRatio * 100).toInt()}%): ${missing.joinToString(", ")}",
            ratio >= minimumOverallRatio,
        )
    }

    /**
     * The review session shows a vocabulary card's `explanationFa` after a miss
     * (checklist A-1), so every word needs one.
     */
    @Test
    fun `every vocabulary word explains its use`() {
        val array = JSONObject(contentFile("vocabulary.json").readText()).getJSONArray("vocabulary")
        val missing = (0 until array.length()).mapNotNull { i ->
            val o = array.getJSONObject(i)
            if (o.optString("explanationFa").trim().isEmpty()) o.getString("id") else null
        }
        assertTrue(
            "vocabulary words without an explanationFa: ${missing.joinToString(", ")}",
            missing.isEmpty(),
        )
    }

    /** An explanation has to be a sentence, not a placeholder. */
    @Test
    fun `every authored explanation is substantive`() {
        val tooShort = exerciseRows()
            .filter { it.hasExplanation && it.explanationLength < 15 }
            .map { "${it.id} (${it.explanationLength} chars)" }
        assertTrue("explanations too short to teach anything: ${tooShort.joinToString(", ")}", tooShort.isEmpty())
    }
}
