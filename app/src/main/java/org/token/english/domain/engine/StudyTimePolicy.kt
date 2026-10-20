package org.token.english.domain.engine

/**
 * When study time may be credited (checklist P7 rework).
 *
 * Study time is **interaction-based with a cap**: a tick is credited only while
 * the learner has acted on the session within [IDLE_CAP_MILLIS]. Merely keeping
 * the app open — or leaving a lesson/review tab on the back stack — must never
 * fill today's goal on its own. The screens drive the ticking (only the resumed
 * lesson/review screen ticks at all); this policy decides whether a tick that
 * fired may actually be written.
 */
object StudyTimePolicy {

    /** Sampling period of the screen-scoped ticker; one tick credits [TICK_SECONDS]. */
    const val TICK_MILLIS = 30_000L

    /** Seconds credited per tick — exactly the tick length, no rounding. */
    const val TICK_SECONDS: Long = TICK_MILLIS / 1_000

    /**
     * How long after the learner's last interaction the session still counts as
     * active. Generous enough that reading a card or a grammar tip slowly is
     * never punished, short enough that an abandoned screen stops filling the
     * 15-minute goal after minutes instead of filling it all by itself.
     */
    const val IDLE_CAP_MILLIS = 4 * 60_000L

    /**
     * True when a tick fired at [nowMs] may be credited: the learner interacted
     * within the idle cap, and the clock has not gone backwards.
     */
    fun credits(lastInteractionAtMs: Long, nowMs: Long): Boolean {
        val sinceLast = nowMs - lastInteractionAtMs
        return sinceLast in 0..IDLE_CAP_MILLIS
    }
}
