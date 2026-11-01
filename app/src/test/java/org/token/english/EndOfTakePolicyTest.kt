package org.token.english

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.feature.conversation.EndOfTakePolicy

/**
 * End-of-take timing (checklist 1.4/1.5). These are the numbers a learner feels:
 * too early clips a sentence, too late feels like a hang. Pinned here so a change to
 * the capture loop cannot move them by accident.
 */
class EndOfTakePolicyTest {

    private fun end(
        heard: Boolean = true,
        inFlight: Boolean = false,
        silenceMs: Long,
        complete: Boolean = false,
        hesitant: Boolean = false,
    ) = EndOfTakePolicy.shouldEnd(
        heardSomething = heard,
        inFlight = inFlight,
        silenceMs = silenceMs,
        completeAnswer = complete,
        hesitant = hesitant,
    )

    @Test
    fun `a complete answer closes about a second after the last word`() {
        assertFalse(end(silenceMs = 1_000, complete = true))
        assertTrue(end(silenceMs = 1_300, complete = true))
    }

    @Test
    fun `an incomplete sentence keeps its four second window`() {
        assertFalse("a pause for thought is not the end of the sentence", end(silenceMs = 1_300))
        assertFalse(end(silenceMs = 3_900))
        assertTrue(end(silenceMs = 4_100))
    }

    @Test
    fun `nothing heard yet never ends the take`() {
        // The learner may still be reading the partner's line or drawing breath.
        assertFalse(end(heard = false, silenceMs = 10_000))
    }

    @Test
    fun `a phrase still being recognised is not a pause`() {
        assertFalse(end(inFlight = true, silenceMs = 5_000))
        assertFalse(end(inFlight = true, silenceMs = 5_000, complete = true))
    }

    @Test
    fun `an A1 turn waits longer than four seconds`() {
        // A beginner hunting for the next word was being cut off mid-sentence.
        assertFalse(end(silenceMs = 4_500, hesitant = true))
        assertTrue(end(silenceMs = 6_100, hesitant = true))
        // But a complete answer still gets the short fuse: there is nothing to wait for.
        assertTrue(end(silenceMs = 1_300, complete = true, hesitant = true))
    }
}
