package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile

/**
 * Audit §6: with recognition strong but recall weak the app must stop serving
 * multiple-choice and ask for recall instead. These tests pin that decision.
 */
class AdaptiveExerciseSelectorTest {

    private val selector = AdaptiveExerciseSelector()

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

    private fun profile(vararg pairs: Pair<MasteryDimension, Float>) =
        MasteryProfile(byDimension = pairs.toMap())

    @Test
    fun `a brand new learner sees the lesson exactly as authored`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        assertEquals(exercises, selector.order(exercises, MasteryProfile()))
    }

    @Test
    fun `strong recognition and weak recall sends recall to the front`() {
        val exercises = listOf(mc("m1"), mc("m2"), fill("f1"))
        val ordered = selector.order(
            exercises,
            profile(MasteryDimension.RECOGNITION to 0.95f, MasteryDimension.RECALL to 0.4f),
        )
        assertEquals(listOf("f1", "m1", "m2"), ordered.map { it.id })
        assertEquals("f1", selector.next(exercises, profile(
            MasteryDimension.RECOGNITION to 0.95f,
            MasteryDimension.RECALL to 0.4f,
        ))?.id)
    }

    @Test
    fun `among weak dimensions the weakest comes first`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        val ordered = selector.order(
            exercises,
            profile(
                MasteryDimension.RECOGNITION to 0.5f,
                MasteryDimension.RECALL to 0.7f,
                MasteryDimension.APPLICATION to 0.2f,
            ),
        )
        assertEquals(listOf("t1", "m1", "f1"), ordered.map { it.id })
    }

    @Test
    fun `a fully consolidated learner keeps the authored order`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        val ordered = selector.order(
            exercises,
            profile(
                MasteryDimension.RECOGNITION to 0.9f,
                MasteryDimension.RECALL to 0.95f,
                MasteryDimension.APPLICATION to 0.88f,
            ),
        )
        assertEquals(exercises, ordered)
    }

    @Test
    fun `consolidated formats sink below weak ones but never disappear`() {
        val exercises = listOf(mc("m1"), translation("t1"), mc("m2"))
        val ordered = selector.order(
            exercises,
            profile(MasteryDimension.RECOGNITION to 0.9f, MasteryDimension.APPLICATION to 0.1f),
        )
        // The weak translation leads; the two strong MCQs keep authored order.
        assertEquals(listOf("t1", "m1", "m2"), ordered.map { it.id })
        // Same members, no duplication or loss — it is only a reordering.
        assertEquals(exercises.toSet(), ordered.toSet())
    }

    @Test
    fun `the order is deterministic for equal input`() {
        val exercises = listOf(mc("m1"), fill("f1"), translation("t1"))
        val p = profile(MasteryDimension.RECALL to 0.3f)
        assertEquals(selector.order(exercises, p), selector.order(exercises, p))
    }

    @Test
    fun `an empty lesson has nothing to pick`() {
        assertEquals(null, selector.next(emptyList(), MasteryProfile()))
        assertTrue(selector.order(emptyList(), MasteryProfile()).isEmpty())
    }
}
