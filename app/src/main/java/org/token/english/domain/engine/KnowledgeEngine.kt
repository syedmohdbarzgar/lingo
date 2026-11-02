package org.token.english.domain.engine

import org.token.english.domain.model.ExerciseFormat
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
     *   A first answer is evidence, not proof: a hit seeds [FIRST_HIT_MASTERY] (0.4)
     *   and a miss 0.0. Seeding a first hit at 1.0 made a single tap look like full
     *   knowledge — the planner then stopped offering the node and the meter showed
     *   100% for the very thing the learner had just met.
     */
    /**
     * @param format the exercise format the answer came through. Recorded only on a
     *   correct answer, as evidence of *transfer*: the same format five times still
     *   counts as one confirmation (checklist 2.1). Null when the caller has no
     *   exercise shape to report, e.g. a placement replay.
     */
    fun applyAttempt(
        itemId: String,
        state: KnowledgeState?,
        correct: Boolean,
        now: Long,
        format: ExerciseFormat? = null,
    ): KnowledgeState {
        val result = if (correct) ReviewResult.GOOD else ReviewResult.AGAIN
        val scheduled = scheduler.schedule(state.toReviewItem(itemId, now), result, now)
        val mastery = when (state) {
            null -> if (correct) FIRST_HIT_MASTERY else 0f
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
            // First correct answer wins: the date a node started being known does not
            // move forward every time it is answered again.
            firstCorrectAt = state?.firstCorrectAt ?: if (correct) now else null,
            distinctTypesCorrect =
                if (correct && format != null) {
                    (state?.distinctTypesCorrect ?: 0) or format.bit
                } else {
                    state?.distinctTypesCorrect ?: 0
                },
        )
    }

    /**
     * The planner's bar: this node needs no more practice today. Strong mastery
     * **earned** on a recorded correct answer — the timestamp is the evidence, so a
     * stored number that never came from an answer cannot secure a node.
     *
     * Deliberately *not* the 30-day gate ([isLongTermMastered]): requiring a month of
     * retention before the plan moves on would hold the learner on a node they already
     * know. Both gates are defined once, in [KnowledgeGates], so the planner and the
     * learner-model screen cannot drift apart.
     */
    fun isSecured(state: KnowledgeState): Boolean = KnowledgeGates.isSecured(state)

    /**
     * The 30-day gate from v30, renamed: the node has been recalled
     * [MASTERED_MIN_REPETITIONS] times, its interval clears the scheduler's own
     * threshold, and its mastery is [STRONG_MASTERY]. "Long-term" is the claim about
     * remembered-next-month, not about today's plan.
     */
    fun isLongTermMastered(state: KnowledgeState): Boolean =
        KnowledgeGates.isLongTermMastered(state)

    /** True when the node is due to come back (used by the daily plan, next slice). */
    fun isDue(state: KnowledgeState, now: Long): Boolean = state.nextReviewAt <= now

    companion object {
        /**
         * Mastery after the first correct answer. Enough to show as "started" and to
         * keep the node in the plan; not enough to look learned.
         */
        const val FIRST_HIT_MASTERY = 0.4f

        /** Correct recalls before "mastered" is claimable — one hit is a guess. */
        const val MASTERED_MIN_REPETITIONS = 3
    }

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

/**
 * The two questions the learner model answers about a node, defined **once**
 * (checklist 2.1) so every surface reads the same bar instead of comparing floats
 * of its own:
 *
 * - [isSecured]: does today's plan still need to practise this node?
 * - [isLongTermMastered]: is the node remembered for the long term?
 *
 * Pure functions over stored state — no clock, no repository — so the planner and the
 * screens can ask them without instantiating an engine, and the JVM tests can hold
 * them to one definition.
 */
object KnowledgeGates {

    /** Recalls a node must have to claim long-term mastery — one hit is a guess. */
    const val MIN_RECALLS = DefaultKnowledgeEngine.MASTERED_MIN_REPETITIONS

    /** Distinct exercise formats that must confirm a node before "long-term". */
    const val MIN_CONFIRMING_FORMATS = 2

    /**
     * Secured: mastery at [STRONG_MASTERY], earned on a recorded correct answer.
     *
     * The timestamp matters: a node only counts as secured when the value came from an
     * answer the learner actually gave. This is the planner's bar, so a node it holds
     * back on keeps the focus lesson open ("تمرین بیشتر") — and one it clears lets the
     * plan move on, which is why this gate must not also demand a month of retention.
     */
    fun isSecured(state: KnowledgeState): Boolean =
        state.mastery >= STRONG_MASTERY && state.firstCorrectAt != null

    /**
     * Long-term: [isSecured], plus the retention evidence — [MIN_RECALLS] recalls, an
     * interval past `Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS` (30), and confirmation
     * in at least [MIN_CONFIRMING_FORMATS] exercise formats. A node answered strong in
     * one format has been pattern-matched, not necessarily learned; the mask is what
     * separates the two claims.
     */
    fun isLongTermMastered(state: KnowledgeState): Boolean =
        isSecured(state) &&
            state.repetitions >= MIN_RECALLS &&
            state.intervalDays >= Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS &&
            state.distinctTypeCount >= MIN_CONFIRMING_FORMATS
}
