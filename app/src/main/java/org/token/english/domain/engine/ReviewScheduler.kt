package org.token.english.domain.engine

import org.token.english.core.common.TimeUtil
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewSchedule
import org.token.english.domain.model.ReviewState
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Spaced-repetition scheduler. The UI never knows how intervals are computed
 * (technical spec §19) — this implementation is an SM-2-style scheduler that can be
 * swapped for a stability/difficulty model later without touching any screen.
 *
 * Time is epoch millis (Long) instead of java.time.Instant because minSdk 24
 * lacks java.time without core-library desugaring; multi-day gaps are anchored
 * to calendar-day boundaries ([TimeUtil.startOfDayPlusDays]) rather than raw
 * 24-hour multiples.
 */
interface ReviewScheduler {
    fun schedule(item: ReviewItem, result: ReviewResult, now: Long): ReviewSchedule

    /** Ease adjustments after a grade — used by the repository when persisting. */
    fun nextEase(current: Float, result: ReviewResult): Float
}

class Sm2ReviewScheduler : ReviewScheduler {

    override fun schedule(item: ReviewItem, result: ReviewResult, now: Long): ReviewSchedule {
        val ease = item.easeFactor.coerceIn(MIN_EASE, MAX_EASE)
        return when (result) {
            ReviewResult.AGAIN -> ReviewSchedule(
                nextReviewAt = now + TEN_MINUTES_MS,
                intervalDays = 0,
                state = when (item.state) {
                    ReviewState.NEW, ReviewState.LEARNING -> ReviewState.LEARNING
                    else -> ReviewState.RELEARNING
                },
            ).also { /* lapses recorded by caller */ }

            ReviewResult.HARD -> {
                when {
                    // New/learning cards get their own short step: Hard means
                    // "repeat soon today", Good means "graduate to tomorrow" —
                    // the two are no longer identical on a fresh card (P5).
                    item.state == ReviewState.NEW || item.state == ReviewState.LEARNING -> ReviewSchedule(
                        nextReviewAt = now + TEN_MINUTES_MS,
                        intervalDays = 0,
                        state = ReviewState.LEARNING,
                    )
                    else -> {
                        val interval = max(1, (item.intervalDays * 1.2f).roundToInt())
                        ReviewSchedule(
                            nextReviewAt = TimeUtil.startOfDayPlusDays(now, interval),
                            intervalDays = interval,
                            state = if (interval >= MASTERED_INTERVAL_DAYS) ReviewState.MASTERED else ReviewState.REVIEW,
                        )
                    }
                }
            }

            ReviewResult.GOOD -> {
                val interval = when {
                    item.state == ReviewState.NEW || item.state == ReviewState.LEARNING -> 1
                    item.repetitions == 0 -> 1
                    else -> max(1, (item.intervalDays * ease).roundToInt())
                }
                ReviewSchedule(
                    nextReviewAt = TimeUtil.startOfDayPlusDays(now, interval),
                    intervalDays = interval,
                    state = if (interval >= MASTERED_INTERVAL_DAYS) ReviewState.MASTERED else ReviewState.REVIEW,
                )
            }

            ReviewResult.EASY -> {
                val base = when {
                    item.state == ReviewState.NEW || item.state == ReviewState.LEARNING -> 4
                    else -> max(4, (item.intervalDays * ease * 1.3f).roundToInt())
                }
                ReviewSchedule(
                    nextReviewAt = TimeUtil.startOfDayPlusDays(now, base),
                    intervalDays = base,
                    state = if (base >= MASTERED_INTERVAL_DAYS) ReviewState.MASTERED else ReviewState.REVIEW,
                )
            }
        }
    }

    /** Ease adjustments after a grade — used by the repository when persisting. */
    override fun nextEase(current: Float, result: ReviewResult): Float = when (result) {
        ReviewResult.AGAIN -> (current - 0.2f).coerceAtLeast(MIN_EASE)
        ReviewResult.HARD -> (current - 0.15f).coerceAtLeast(MIN_EASE)
        ReviewResult.GOOD -> current
        ReviewResult.EASY -> (current + 0.15f).coerceAtMost(MAX_EASE)
    }

    companion object {
        const val MIN_EASE = 1.3f
        const val MAX_EASE = 3.0f
        const val DEFAULT_EASE = 2.5f
        const val MASTERED_INTERVAL_DAYS = 30
        private const val TEN_MINUTES_MS = 10 * 60 * 1000L
    }
}
