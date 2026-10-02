package org.token.english.domain.engine

import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState

/**
 * Turns one graded answer about a knowledge item into the next learner state
 * (audit §5/§20 — the learner model).
 *
 * Two deliberate reuse decisions, so the app keeps exactly one definition of each
 * idea:
 *
 * - interval, ease and due date come from the existing [ReviewScheduler]. SM-2 is
 *   already implemented and tested there, and the audit's KnowledgeState is the
 *   same shape as the scheduler's input, so nothing is re-implemented.
 * - mastery comes from the shared [MasteryEngine], so per-node mastery and the
 *   per-skill meters on the Progress screen stay on the same scale.
 *
 * Pure and deterministic: no Android, no clock reads — `now` is always passed in.
 */
class DefaultKnowledgeEngine(
    private val masteryEngine: MasteryEngine = DefaultMasteryEngine(),
    private val scheduler: ReviewScheduler = Sm2ReviewScheduler(),
) {

    /**
     * @param state the node's current state, or `null` the first time it is seen.
     *   A first correct answer seeds mastery at 1.0 and a first miss at 0.0, so an
     *   untouched node is never mistaken for a half-learned one.
     */
    fun applyAttempt(itemId: String, state: KnowledgeState?, correct: Boolean, now: Long): KnowledgeState {
        val result = if (correct) ReviewResult.GOOD else ReviewResult.AGAIN
        val scheduled = scheduler.schedule(state.toReviewItem(itemId, now), result, now)
        val mastery = when (state) {
            null -> if (correct) 1f else 0f
            else -> masteryEngine.update(state.mastery, correct)
        }
        return KnowledgeState(
            itemId = itemId,
            mastery = mastery,
            exposureCount = (state?.exposureCount ?: 0) + 1,
            consecutiveCorrect = if (correct) (state?.consecutiveCorrect ?: 0) + 1 else 0,
            consecutiveIncorrect = if (correct) 0 else (state?.consecutiveIncorrect ?: 0) + 1,
            intervalDays = scheduled.intervalDays,
            easeFactor = scheduler.nextEase(
                state?.easeFactor ?: Sm2ReviewScheduler.DEFAULT_EASE,
                result,
            ),
            repetitions = (state?.repetitions ?: 0) + if (correct) 1 else 0,
            lapses = (state?.lapses ?: 0) + if (correct) 0 else 1,
            lastReviewedAt = now,
            nextReviewAt = scheduled.nextReviewAt,
        )
    }

    /**
     * A node counts as mastered once its interval clears the scheduler's
     * threshold — the same definition the review queue uses, so "mastered" cannot
     * mean two different things in two screens.
     */
    fun isMastered(state: KnowledgeState): Boolean =
        state.intervalDays >= Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS

    /** True when the node is due to come back (used by the daily plan, next slice). */
    fun isDue(state: KnowledgeState, now: Long): Boolean = state.nextReviewAt <= now

    private fun KnowledgeState?.toReviewItem(itemId: String, now: Long): ReviewItem = ReviewItem(
        contentId = itemId,
        // The scheduler never reads contentType; the graph node is grammar-shaped.
        contentType = ReviewContentType.GRAMMAR,
        state = reviewStateOf(this),
        dueAt = this?.nextReviewAt ?: now,
        intervalDays = this?.intervalDays ?: 0,
        easeFactor = this?.easeFactor ?: Sm2ReviewScheduler.DEFAULT_EASE,
        repetitions = this?.repetitions ?: 0,
        lapses = this?.lapses ?: 0,
        lastReviewedAt = this?.lastReviewedAt,
    )

    /** Projects stored counters back into the SRS lifecycle the scheduler reasons about. */
    private fun reviewStateOf(state: KnowledgeState?): ReviewState = when {
        state == null -> ReviewState.NEW
        state.intervalDays >= Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS -> ReviewState.MASTERED
        state.repetitions == 0 -> ReviewState.NEW
        state.intervalDays == 0 && state.lapses > 0 -> ReviewState.RELEARNING
        state.intervalDays == 0 -> ReviewState.LEARNING
        else -> ReviewState.REVIEW
    }
}
