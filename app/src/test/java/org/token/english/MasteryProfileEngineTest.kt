package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.engine.DimensionAttempt
import org.token.english.domain.engine.ExerciseDimension
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile

/**
 * The mastery model must be more than one number (audit §2): recognition and
 * production are different kinds of knowing and the app has to be able to tell
 * them apart before it can stop over-serving recognition exercises.
 */
class MasteryProfileEngineTest {

    private val engine = DefaultMasteryProfileEngine()

    @Test
    fun `exercise formats map to the kind of knowing they demand`() {
        assertEquals(MasteryDimension.RECOGNITION, ExerciseDimension.dimensionOf(mc("e")))
        assertEquals(MasteryDimension.RECALL, ExerciseDimension.dimensionOf(fill("e")))
        assertEquals(MasteryDimension.APPLICATION, ExerciseDimension.dimensionOf(translation("e")))
        assertEquals(MasteryDimension.COMPREHENSION, ExerciseDimension.dimensionOf(listening("e")))
        assertEquals(MasteryDimension.PRODUCTION, ExerciseDimension.dimensionOf(speaking("e")))
    }

    @Test
    fun `the first answer on a dimension seeds it like the knowledge model`() {
        val right = engine.apply(MasteryProfile(), MasteryDimension.RECOGNITION, correct = true)
        assertEquals(1f, right.masteryOf(MasteryDimension.RECOGNITION), 0f)

        val wrong = engine.apply(MasteryProfile(), MasteryDimension.RECALL, correct = false)
        assertEquals(0f, wrong.masteryOf(MasteryDimension.RECALL), 0f)
    }

    @Test
    fun `later answers move a dimension smoothly, not absolutely`() {
        var profile = engine.apply(MasteryProfile(), MasteryDimension.RECOGNITION, correct = true)
        profile = engine.apply(profile, MasteryDimension.RECOGNITION, correct = false)
        // 1 * (1 - 0.2) + 0 * 0.2 = 0.8
        assertEquals(0.8f, profile.masteryOf(MasteryDimension.RECOGNITION), 0.001f)
    }

    @Test
    fun `dimensions are independent`() {
        val profile = engine.profileOf(
            listOf(
                DimensionAttempt(MasteryDimension.RECOGNITION, correct = true),
                DimensionAttempt(MasteryDimension.RECALL, correct = false),
            ),
        )
        assertEquals(1f, profile.masteryOf(MasteryDimension.RECOGNITION), 0f)
        assertEquals(0f, profile.masteryOf(MasteryDimension.RECALL), 0f)
    }

    @Test
    fun `an untouched dimension is absent, not a practised zero`() {
        val profile = engine.apply(MasteryProfile(), MasteryDimension.RECOGNITION, correct = true)
        assertTrue(profile.isEstablished(MasteryDimension.RECOGNITION))
        assertFalse(profile.isEstablished(MasteryDimension.PRODUCTION))
        // Reads as 0f for ranking, but is not the same as a failed attempt.
        assertEquals(0f, profile.masteryOf(MasteryDimension.PRODUCTION), 0f)
    }

    @Test
    fun `overall is the mean of what has actually been assessed`() {
        val profile = engine.profileOf(
            listOf(
                DimensionAttempt(MasteryDimension.RECOGNITION, correct = true),
                DimensionAttempt(MasteryDimension.RECALL, correct = false),
            ),
        )
        assertEquals(0.5f, profile.overall, 0.001f)
        assertEquals(0f, MasteryProfile().overall, 0f)
    }

    @Test
    fun `the weakest dimension is named deterministically`() {
        val profile = engine.profileOf(
            listOf(
                DimensionAttempt(MasteryDimension.RECOGNITION, correct = true),
                DimensionAttempt(MasteryDimension.RECALL, correct = true),
                DimensionAttempt(MasteryDimension.APPLICATION, correct = false),
            ),
        )
        val candidates = listOf(
            MasteryDimension.RECOGNITION,
            MasteryDimension.RECALL,
            MasteryDimension.APPLICATION,
        )
        assertEquals(MasteryDimension.APPLICATION, profile.weakestOf(candidates))

        // Ties fall back to declaration order, so the answer never wobbles.
        assertEquals(
            MasteryDimension.RECOGNITION,
            MasteryProfile().weakestOf(candidates),
        )
        assertEquals(null, profile.weakestOf(emptyList()))
    }

    // --- exercise fixtures ----------------------------------------------------

    private fun mc(id: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = "l1",
        question = "q",
        questionFa = null,
        options = listOf("a", "b"),
        correctIndex = 0,
    )

    private fun fill(id: String) = Exercise.FillBlank(
        id = id,
        lessonId = "l1",
        sentence = "I ___ here.",
        accepted = listOf("live"),
    )

    private fun translation(id: String) = Exercise.Translation(
        id = id,
        lessonId = "l1",
        prompt = "من اینجا زندگی می‌کنم",
        accepted = listOf("I live here"),
    )

    private fun listening(id: String) = Exercise.Listening(
        id = id,
        lessonId = "l1",
        audioText = "I live here",
        accepted = listOf("I live here"),
    )

    private fun speaking(id: String) = Exercise.Speaking(
        id = id,
        lessonId = "l1",
        prompt = "Introduce yourself",
        referenceAnswers = listOf("My name is Ali"),
    )
}
