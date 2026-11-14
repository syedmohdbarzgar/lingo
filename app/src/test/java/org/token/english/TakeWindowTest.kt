package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.audio.SpeechBus
import org.token.english.core.audio.TakeWindow

/**
 * What a take may still do after the learner left it — the rule P0 #1 is about.
 *
 * A take ends in two ways and the difference is not cosmetic. «پایان ضبط» is the
 * learner finishing: the transcript must be graded and the WAV handed to the player.
 * Leaving the exercise is abandonment: the same late emission would restore a take on
 * the *next* item's screen, whose playback button would then play a sentence said for
 * another exercise — and its half-written file must not survive at all.
 *
 * The window is pure, so the service's own logic is pinned here rather than read.
 */
class TakeWindowTest {

    private var newest = 0
    private var epoch = 0
    private val window = TakeWindow(newest = { newest }, screenEpoch = { epoch })

    private fun beginTake(): Int {
        newest += 1
        window.begin(newest)
        return newest
    }

    @Test
    fun `a reset while the model is still loading refuses the microphone`() {
        // P0 #1: the window still holds the take as active after a reset — nothing tells
        // it to end a take the service has not reached yet — so "may this take open the
        // mic" has to answer from the epoch as well. Before this rule the take opened
        // AudioRecord and announced «listening» on a screen that had been blanked.
        val take = beginTake()

        epoch += 1 // SpeechBus.reset(), ACTION_STOP not handled yet

        assertFalse("it must not open the recorder after the screen left", window.mayOpenMic(take))
    }

    @Test
    fun `a late failure from an abandoned take reaches nobody`() {
        val take = beginTake()

        epoch += 1 // the learner left while the model was still loading

        assertFalse("its error must not repaint the blanked screen", window.mayPublish(take))
        assertTrue("while a take begun on the live screen may still report", window.mayPublish(beginTake()))
    }

    @Test
    fun `a take the learner ended still reports`() {
        val take = beginTake()

        window.end() // «پایان ضبط»

        assertFalse("the mic is closed", window.mayOpenMic(take))
        assertTrue("but its transcript and WAV are the point of the take", window.mayPublish(take))
    }

    @Test
    fun `a take whose screen was abandoned publishes nothing`() {
        val take = beginTake()

        epoch += 1 // SpeechBus.reset()
        window.end()

        assertFalse("no partial, no path, no error", window.mayPublish(take))
        assertFalse("and not on the next item's screen", window.mayPublish(take))
    }

    @Test
    fun `abandoning while the mic is still open also refuses the microphone`() {
        val take = beginTake()
        assertTrue("the take owns the mic", window.mayOpenMic(take))

        epoch += 1 // the screen left before the stop was handled
        window.end()

        assertFalse("a cancelled take must not open the mic again", window.mayOpenMic(take))
    }

    @Test
    fun `an abandoned take stops reading the microphone`() {
        val take = beginTake()
        assertFalse("a live screen's take is the loop's own", window.isAbandoned(take))

        epoch += 1 // SpeechBus.reset() while the mic is still open

        // The loop condition, not just the publish guard: waiting for the silence fuse
        // would record for up to half a minute after the learner left.
        assertTrue("the capture loop has to leave", window.isAbandoned(take))
        assertFalse("and a new take is not", window.isAbandoned(beginTake()))
    }

    @Test
    fun `a newer take takes the screen from the one still leaving`() {
        val first = beginTake()
        val second = beginTake()

        assertFalse("the old take's last words", window.mayPublish(first))
        assertTrue("the new take's own", window.mayPublish(second))
        assertFalse("and only the new one owns the mic", window.mayOpenMic(first))
    }

    @Test
    fun `a take begun after an abandonment may publish again`() {
        beginTake()
        epoch += 1 // abandoned

        val fresh = beginTake()

        assertTrue("a new screen is not refused for the old one's sake", window.mayPublish(fresh))
        assertTrue(window.mayOpenMic(fresh))
    }

    @Test
    fun `an ending take leaves no active take behind`() {
        beginTake()

        window.end()

        assertFalse(window.hasActiveTake)
        assertFalse("no take can publish with no id", window.mayPublish(0))
    }

    @Test
    fun `reset bumps the epoch the window compares`() {
        val before = SpeechBus.epoch

        SpeechBus.reset()

        assertEquals("the service learns about the reset from this number", before + 1, SpeechBus.epoch)
    }
}
