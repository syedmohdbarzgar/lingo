package org.token.english.core.billing

import java.util.Calendar

/**
 * Device-clock-independent trial accounting (checklist P3).
 *
 * A wall clock is neither monotonic nor trustworthy: the learner (or a broken
 * NTP update) can jump it forward to expire the trial early, or backward to
 * stretch it. So trial time is *consumed* in checkpoints: between two
 * checkpoints only the wall-clock delta that real time also confirms
 * (`SystemClock.elapsedRealtime`) is counted — forward jumps are clamped to it,
 * backward jumps add nothing.
 *
 * Checkpoints are persisted at app start and once a minute while the process
 * lives (see AppContainer.persistTrialProgress). Pure Kotlin: the caller passes
 * both clocks, so every rule is unit-testable without Android.
 */
data class TrialClockState(
    /** Epoch millis when the trial began; 0 = trial not started. */
    val startedAt: Long,
    /** Real time consumed so far, capped at [TrialClock.TRIAL_MILLIS]. */
    val consumedMs: Long,
    /** Wall clock at the last persisted checkpoint. */
    val lastWallMs: Long,
    /** elapsedRealtime at the last persisted checkpoint. */
    val lastElapsedMs: Long,
)

object TrialClock {
    /**
     * 7-day trial (extended from 24h): SRS only shows its value after a few
     * days of spaced review, so a single day hid the core loop (checklist P9).
     */
    const val TRIAL_MILLIS: Long = 7L * 24 * 60 * 60 * 1000

    fun isStarted(state: TrialClockState): Boolean = state.startedAt > 0L

    fun initial(now: Long, elapsedRealtime: Long): TrialClockState =
        TrialClockState(startedAt = now, consumedMs = 0L, lastWallMs = now, lastElapsedMs = elapsedRealtime)

    /**
     * Moves the checkpoint to (now, elapsedRealtime), adding at most the real
     * time that actually passed between the old checkpoint and now.
     */
    fun advance(state: TrialClockState, now: Long, elapsedRealtime: Long): TrialClockState {
        if (!isStarted(state) || state.lastWallMs <= 0L) return state
        val wallDelta = now - state.lastWallMs
        val elapsedDelta = (elapsedRealtime - state.lastElapsedMs).coerceAtLeast(0L)
        val gained = wallDelta.coerceIn(0L, elapsedDelta)
        val consumed = (state.consumedMs + gained).coerceAtMost(TRIAL_MILLIS)
        return if (consumed == state.consumedMs && now == state.lastWallMs) {
            state
        } else {
            TrialClockState(state.startedAt, consumed, now, elapsedRealtime)
        }
    }

    /** Trial time consumed as of (now, elapsedRealtime). */
    fun consumedMs(state: TrialClockState, now: Long, elapsedRealtime: Long): Long =
        advance(state, now, elapsedRealtime).consumedMs

    /**
     * Remaining trial time as of (now, elapsedRealtime), never negative.
     * An unstarted trial grants nothing — access starts when the checkpoint does.
     */
    fun remainingMs(state: TrialClockState, now: Long, elapsedRealtime: Long): Long {
        if (!isStarted(state)) return 0L
        return (TRIAL_MILLIS - consumedMs(state, now, elapsedRealtime)).coerceAtLeast(0L)
    }
}

/**
 * Client-side expiry math for auto-renewing subscriptions.
 *
 * The app is offline-first and has no server, so the store's "purchase active"
 * answer is combined with the purchase time: while the store keeps reporting
 * the purchase, renewal boundaries are rolled forward from the purchase time
 * using calendar months/years (the way stores bill). This is an approximation —
 * only a server can verify billing cycles — but it ties the local mirror to the
 * purchase date instead of blindly granting `now + grace` (checklist P3).
 */
object SubscriptionMath {

    /** Next renewal boundary after [purchaseTimeMs], strictly after [nowMs]. */
    fun nextRenewalBoundary(purchaseTimeMs: Long, monthly: Boolean, nowMs: Long): Long {
        if (purchaseTimeMs <= 0L) return nowMs
        var boundary = purchaseTimeMs
        if (boundary > nowMs) return boundary
        val cal = Calendar.getInstance().apply { timeInMillis = purchaseTimeMs }
        var guard = 0
        while (boundary <= nowMs && guard < 1200) {
            if (monthly) cal.add(Calendar.MONTH, 1) else cal.add(Calendar.YEAR, 1)
            boundary = cal.timeInMillis
            guard++
        }
        return boundary
    }

    /** Billing period of a store product id; unknown products are monthly. */
    fun isMonthly(productId: String): Boolean = productId != ProductIds.YEARLY
}
