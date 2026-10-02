package org.token.english.core.common

import java.util.Calendar

/**
 * Date helpers. minSdk 24 rules out java.time without desugaring, so the app
 * uses java.util.Calendar for day boundaries and streak math.
 */
object TimeUtil {

    fun startOfDay(now: Long): Long = calendar(now).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /**
     * Midnight of the calendar day [days] days after [now]'s day — the SRS
     * spacing anchor, so reviews come due at day boundaries rather than at
     * `now + n × 24h` (which drifts with the time of day the card was studied).
     */
    fun startOfDayPlusDays(now: Long, days: Int): Long = calendar(now).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, days)
    }.timeInMillis

    /** Compact day id, e.g. 20261001 — primary key for study sessions. */
    fun dateKey(now: Long): Int {
        val c = calendar(now)
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH)
    }

    fun previousDateKey(key: Int): Int {
        val year = key / 10000
        val month = (key / 100) % 100 - 1
        val day = key % 100
        val c = Calendar.getInstance().apply {
            clear()
            set(year, month, day)
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Consecutive-day streak ending today (or yesterday if today has no session yet).
     */
    fun streakDays(sessionKeys: Collection<Int>, now: Long): Int {
        if (sessionKeys.isEmpty()) return 0
        val today = dateKey(now)
        val keys = sessionKeys.toSet()
        var cursor = if (keys.contains(today) || keys.contains(previousDateKey(today))) {
            if (keys.contains(today)) today else previousDateKey(today)
        } else {
            return 0
        }
        var streak = 0
        while (keys.contains(cursor)) {
            streak++
            cursor = previousDateKey(cursor)
        }
        return streak
    }

    private fun calendar(time: Long): Calendar = Calendar.getInstance().apply { timeInMillis = time }
}
