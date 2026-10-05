package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DefaultKnowledgeEngine
import org.token.english.domain.model.KnowledgeState

/**
 * Pins the whole SRS lifecycle for one knowledge node — New → Correct → Correct →
 * Correct → Wrong → Recovery — recording the exact mastery, ease, interval and
 * next-review at each step (audit §3, "تست سناریو").
 *
 * This is a regression fence: if a future change to the scheduler or the mastery
 * model moves any of these numbers, the test says so instead of leaving the shift
 * to be discovered by a learner.
 */
class ReviewLifecycleTest {

    private val engine = DefaultKnowledgeEngine()
    private val now = 1_700_000_000_000L
    private val tenMinutes = 10 * 60 * 1000L

    private fun step(state: KnowledgeState?, correct: Boolean) =
        engine.applyAttempt("node", state, correct, now)

    @Test
    fun `new node with three correct answers then a lapse then recovery`() {
        // New + first correct: scheduled a day out, ease untouched, mastery full.
        val first = step(null, correct = true)
        assertEquals(1f, first.mastery, 0.0001f)
        assertEquals(1, first.intervalDays)
        assertEquals(2.5f, first.easeFactor, 0.0001f)
        assertEquals(1, first.repetitions)
        assertEquals(1, first.consecutiveCorrect)
        assertTrue(first.nextReviewAt > now)

        // Second correct: interval stretches, mastery already saturated.
        val second = step(first, correct = true)
        assertEquals(1f, second.mastery, 0.0001f)
        assertEquals(3, second.intervalDays)
        assertEquals(2.5f, second.easeFactor, 0.0001f)
        assertEquals(2, second.repetitions)

        // Third correct: interval keeps growing.
        val third = step(second, correct = true)
        assertEquals(1f, third.mastery, 0.0001f)
        assertEquals(8, third.intervalDays)
        assertEquals(3, third.repetitions)

        // A miss: mastery drops a fifth, ease loses 0.2, card returns within the session.
        val lapse = step(third, correct = false)
        assertEquals(0.8f, lapse.mastery, 0.0001f)
        assertEquals(0, lapse.intervalDays)
        assertEquals(2.3f, lapse.easeFactor, 0.0001f)
        assertEquals(now + tenMinutes, lapse.nextReviewAt)
        assertEquals(1, lapse.lapses)
        assertEquals(0, lapse.consecutiveCorrect)

        // Recovery: mastery climbs gradually back, interval restarts at one day.
        val recovery = step(lapse, correct = true)
        assertEquals(0.84f, recovery.mastery, 0.0001f)
        assertEquals(1, recovery.intervalDays)
        assertEquals(2.3f, recovery.easeFactor, 0.0001f)
        assertTrue(recovery.nextReviewAt > now)
    }

    @Test
    fun `mastery rises gradually after a first miss rather than jumping to full`() {
        var state = step(null, correct = false)
        assertEquals(0f, state.mastery, 0.0001f)
        state = step(state, correct = true)
        assertEquals(0.2f, state.mastery, 0.0001f)
        state = step(state, correct = true)
        assertEquals(0.36f, state.mastery, 0.0001f)
        assertTrue("still not fully known after two recoveries", state.mastery < 0.5f)
    }
}
