package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.TrialClock
import org.token.english.core.billing.TrialClockState

class TrialClockTest {

    private val t0 = 1_759_300_000_000L // arbitrary wall-clock start
    private val e0 = 1_000_000L // arbitrary elapsedRealtime start
    private val hour = 60L * 60 * 1000
    private val day = 24L * hour

    private fun started(): TrialClockState = TrialClock.initial(now = t0, elapsedRealtime = e0)

    @Test
    fun `trial grants seven days`() {
        assertEquals(7 * day, TrialClock.TRIAL_MILLIS)
        assertEquals(7 * day, TrialClock.remainingMs(started(), t0, e0))
    }

    @Test
    fun `real elapsed time consumes the trial`() {
        val state = TrialClock.advance(started(), t0 + 2 * hour, e0 + 2 * hour)
        assertEquals(2 * hour, state.consumedMs)
        assertEquals(7 * day - 2 * hour, TrialClock.remainingMs(state, t0 + 2 * hour, e0 + 2 * hour))
    }

    @Test
    fun `forward wall-clock jump is clamped to real time`() {
        // The learner sets the clock 10 days ahead after only one real hour.
        val state = TrialClock.advance(started(), t0 + 10 * day, e0 + hour)
        assertEquals(hour, state.consumedMs)
    }

    @Test
    fun `backward wall-clock jump never refunds consumed time`() {
        val s1 = TrialClock.advance(started(), t0 + hour, e0 + hour)
        assertEquals(hour, s1.consumedMs)
        // Clock moved a day into the past; half an hour of real time passes.
        val s2 = TrialClock.advance(s1, t0 + hour - day, e0 + hour + 30 * 60 * 1000)
        assertEquals(hour, s2.consumedMs) // nothing refunded, no extra granted
    }

    @Test
    fun `consumption caps at the trial length`() {
        val state = TrialClock.advance(started(), t0 + 30 * day, e0 + 30 * day)
        assertEquals(TrialClock.TRIAL_MILLIS, state.consumedMs)
        assertEquals(0L, TrialClock.remainingMs(state, t0 + 40 * day, e0 + 40 * day))
    }

    @Test
    fun `reboot resets elapsed but steals at most the last checkpoint window`() {
        val s1 = TrialClock.advance(started(), t0 + hour, e0 + hour)
        // After a reboot elapsedRealtime restarts near zero — wall time alone
        // can't prove anything, so no consumption is added from that window…
        val s2 = TrialClock.advance(s1, t0 + 2 * hour, 5_000L)
        assertEquals(hour, s2.consumedMs)
        // …but consumption resumes normally from the new checkpoint.
        val s3 = TrialClock.advance(s2, t0 + 3 * hour, 5_000L + hour)
        assertEquals(2 * hour, s3.consumedMs)
    }

    @Test
    fun `unstarted trial is inert`() {
        val unstarted = TrialClockState(startedAt = 0L, consumedMs = 0L, lastWallMs = 0L, lastElapsedMs = 0L)
        assertEquals(unstarted, TrialClock.advance(unstarted, t0 + day, e0 + day))
        assertEquals(0L, TrialClock.remainingMs(unstarted, t0 + day, e0 + day))
        assertTrue(!TrialClock.isStarted(unstarted))
    }

    @Test
    fun `unchanged clocks return the identical state with no needless writes`() {
        val state = started()
        assertEquals(state, TrialClock.advance(state, t0, e0))
    }
}
