package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.common.TimeUtil

class TimeUtilTest {

    @Test
    fun `dateKey walks one day back across year boundary`() {
        // Deterministic assertions (dateKey itself is timezone-dependent by design)
        assertEquals(20251231, TimeUtil.previousDateKey(20260101))
        assertEquals(20260301, TimeUtil.previousDateKey(20260302))
        assertTrue(TimeUtil.dateKey(1_759_300_000_000L) in 20250101..20261231)
    }

    @Test
    fun `streak counts consecutive days including today`() {
        val today = TimeUtil.dateKey(1_759_300_000_000L)
        val sessions = setOf(today, TimeUtil.previousDateKey(today))
        val streak = TimeUtil.streakDays(sessions, 1_759_300_000_000L)
        assertEquals(2, streak)
    }

    @Test
    fun `streak is zero when today and yesterday are missing`() {
        val today = TimeUtil.dateKey(1_759_300_000_000L)
        val stale = setOf(20260101, 20260102)
        assertEquals(0, TimeUtil.streakDays(stale, 1_759_300_000_000L))
        assertTrue(TimeUtil.streakDays(emptySet(), 1_759_300_000_000L) == 0)
    }

    @Test
    fun `streak breaks on a gap`() {
        val today = TimeUtil.dateKey(1_759_300_000_000L)
        val twoDaysAgo = TimeUtil.previousDateKey(TimeUtil.previousDateKey(today))
        val sessions = setOf(today, twoDaysAgo) // yesterday missing
        assertEquals(1, TimeUtil.streakDays(sessions, 1_759_300_000_000L))
    }
}
