package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.StudyTimePolicy

/**
 * StudyTimePolicy (checklist P7): study time is interaction-based with an idle
 * cap — the fix for the daily goal filling up by itself while the learner only
 * had the app open.
 */
class StudyTimePolicyTest {

    private val now = 1_000_000_000L

    @Test
    fun `a tick right after an interaction credits`() {
        assertTrue(StudyTimePolicy.credits(lastInteractionAtMs = now, nowMs = now + 1_000))
    }

    @Test
    fun `a tick at the idle cap still credits`() {
        assertTrue(
            StudyTimePolicy.credits(
                lastInteractionAtMs = now - StudyTimePolicy.IDLE_CAP_MILLIS,
                nowMs = now,
            ),
        )
    }

    @Test
    fun `a tick past the idle cap credits nothing`() {
        assertFalse(
            StudyTimePolicy.credits(
                lastInteractionAtMs = now - StudyTimePolicy.IDLE_CAP_MILLIS - 1,
                nowMs = now,
            ),
        )
    }

    @Test
    fun `a backwards clock credits nothing`() {
        assertFalse(StudyTimePolicy.credits(lastInteractionAtMs = now, nowMs = now - 1))
    }

    @Test
    fun `one tick credits exactly the tick length in seconds`() {
        assertEquals(30L, StudyTimePolicy.TICK_SECONDS)
        assertEquals(30_000L, StudyTimePolicy.TICK_MILLIS)
    }
}
