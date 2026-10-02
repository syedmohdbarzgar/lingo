package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.common.TimeUtil
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.ReviewContentType

class ReviewSchedulerTest {

    private val scheduler = Sm2ReviewScheduler()
    private val now = 1_759_300_000_000L

    private fun newCopy(
        state: ReviewState = ReviewState.NEW,
        intervalDays: Int = 0,
        easeFactor: Float = Sm2ReviewScheduler.DEFAULT_EASE,
        repetitions: Int = 0,
    ) = ReviewItem(
        contentId = "a1.test.word",
        contentType = ReviewContentType.VOCABULARY,
        state = state,
        dueAt = now,
        intervalDays = intervalDays,
        easeFactor = easeFactor,
        repetitions = repetitions,
        lapses = 0,
        lastReviewedAt = null,
    )

    @Test
    fun `good answer on new item schedules the next review after now`() {
        val schedule = scheduler.schedule(newCopy(), ReviewResult.GOOD, now)
        assertTrue("next review must be later than now", schedule.nextReviewAt > now)
        assertEquals(1, schedule.intervalDays)
        assertEquals(ReviewState.REVIEW, schedule.state)
    }

    @Test
    fun `again keeps the item in learning and reschedules within the day`() {
        val schedule = scheduler.schedule(newCopy(), ReviewResult.AGAIN, now)
        assertTrue(schedule.nextReviewAt > now)
        assertTrue(schedule.nextReviewAt <= now + 60 * 60 * 1000)
        assertEquals(0, schedule.intervalDays)
        assertEquals(ReviewState.LEARNING, schedule.state)
    }

    @Test
    fun `again on a mature item moves it to relearning`() {
        val schedule = scheduler.schedule(
            newCopy(state = ReviewState.REVIEW, intervalDays = 7, repetitions = 3),
            ReviewResult.AGAIN,
            now,
        )
        assertEquals(ReviewState.RELEARNING, schedule.state)
    }

    @Test
    fun `good answer grows the interval on a mature item`() {
        val schedule = scheduler.schedule(
            newCopy(state = ReviewState.REVIEW, intervalDays = 7, repetitions = 3),
            ReviewResult.GOOD,
            now,
        )
        assertTrue("interval must grow", schedule.intervalDays >= 7)
        assertTrue(schedule.nextReviewAt > now)
    }

    @Test
    fun `easy answer reaches mastered state on a mature item`() {
        val schedule = scheduler.schedule(
            newCopy(state = ReviewState.REVIEW, intervalDays = 20, repetitions = 5),
            ReviewResult.EASY,
            now,
        )
        assertEquals(ReviewState.MASTERED, schedule.state)
        assertTrue(schedule.intervalDays >= Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS)
    }

    @Test
    fun `hard differs from good on a new card — short learning step vs graduation`() {
        val hard = scheduler.schedule(newCopy(), ReviewResult.HARD, now)
        assertEquals(ReviewState.LEARNING, hard.state)
        assertEquals(0, hard.intervalDays)
        assertTrue("HARD reschedules within the hour", hard.nextReviewAt <= now + 60 * 60 * 1000)

        val good = scheduler.schedule(newCopy(), ReviewResult.GOOD, now)
        assertEquals(ReviewState.REVIEW, good.state)
        assertEquals(1, good.intervalDays)
        assertTrue("HARD must come due before GOOD on a new card", hard.nextReviewAt < good.nextReviewAt)
    }

    @Test
    fun `multi-day gaps land on calendar day boundaries, not now plus n times 24h`() {
        val schedule = scheduler.schedule(
            newCopy(state = ReviewState.REVIEW, intervalDays = 7, repetitions = 3),
            ReviewResult.GOOD,
            now,
        )
        assertEquals(
            TimeUtil.startOfDayPlusDays(now, schedule.intervalDays),
            schedule.nextReviewAt,
        )
        // The anchor is midnight of the target day, whatever hour studying happened.
        assertEquals(0L, schedule.nextReviewAt - TimeUtil.startOfDay(schedule.nextReviewAt))
    }

    @Test
    fun `ease factor is bounded on both ends`() {
        var ease = Sm2ReviewScheduler.DEFAULT_EASE
        repeat(10) { ease = scheduler.nextEase(ease, ReviewResult.AGAIN) }
        assertTrue(ease >= Sm2ReviewScheduler.MIN_EASE)

        repeat(10) { ease = scheduler.nextEase(ease, ReviewResult.EASY) }
        assertTrue(ease <= Sm2ReviewScheduler.MAX_EASE)
    }
}
