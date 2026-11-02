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
     * The planner's bar: this node needs no more practice today. See
     * [KnowledgeGates.isSecured] for what "secured" demands — strong mastery earned
     * over a retest, in more than one exercise format, on a streak that has not been
     * broken since.
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

    /** Correct answers in a row that make "this is known" believable. */
    const val MIN_CONSECUTIVE = 3

    /**
     * Distinct exercise formats that must confirm a node before it is *secured*.
     *
     * One format answered well can be pattern-matched — the learner recognises the
     * shape of the question rather than knowing the language — so a second format is
     * the cheapest honest evidence of transfer. It is also what the long-term claim
     * asks for, so the number lives here once.
     */
    const val MIN_FORMATS_TO_SECURE = 2

    /**
     * Gap between the first correct answer and a later one, before a node can count as
     * secured: 20 hours, i.e. at least one *next-day* retest.
     *
     * The mastery average on its own is a same-session number. With EWMA 0.2 from
     * [DefaultKnowledgeEngine.FIRST_HIT_MASTERY] (0.4), seven correct answers in one
     * sitting cross 0.85 without a single delayed recall being tested — enough to move
     * the plan to the next unit the learner has only just met. A span taken from the
     * stored timestamps (not a clock read) keeps the gate pure and testable.
     */
    const val MIN_RETEST_SPAN_MS = 20 * 3_600_000L

    /**
     * Secured: the node needs no more practice in today's plan. All four hold:
     *
     *  - mastery at [STRONG_MASTERY];
     *  - at least [MIN_CONSECUTIVE] correct answers in a row (a miss voids it);
     *  - at least [MIN_FORMATS_TO_SECURE] exercise formats confirmed it;
     *  - a correct answer at least [MIN_RETEST_SPAN_MS] after the first one, so the
     *    node survived a delay instead of only a same-sitting streak.
     *
     * `firstCorrectAt == null` (never answered right) is not secured — a stored number
     * that never came from an answer proves nothing, and legacy rows before Room v7
     * start there. This is the planner's bar, so it must not also demand a month of
     * retention: that is the long-term claim.
     */
    fun isSecured(state: KnowledgeState): Boolean {
        val first = state.firstCorrectAt ?: return false
        val last = state.lastReviewedAt ?: return false
        return state.mastery >= STRONG_MASTERY &&
            state.consecutiveCorrect >= MIN_CONSECUTIVE &&
            state.distinctTypeCount >= MIN_FORMATS_TO_SECURE &&
            last - first >= MIN_RETEST_SPAN_MS
    }

    /**
     * Has the node had time to be retested since it was last seen? The planner keeps a
     * strong-but-unsecured node out of today's plan until this is true, rather than
     * sending the learner back into the same drill they just finished.
     */
    fun hasWaitedForRetest(state: KnowledgeState, now: Long): Boolean {
        val last = state.lastReviewedAt ?: return false
        return now - last >= MIN_RETEST_SPAN_MS
    }

    /**
     * Long-term: [isSecured], plus the retention evidence — [MIN_RECALLS] recalls and
     * an interval past `Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS` (30). The format and
     * streak requirements are already inside [isSecured]; repeating them here would let
     * the two gates disagree about what "confirmed" means.
     */
    fun isLongTermMastered(state: KnowledgeState): Boolean =
        isSecured(state) &&
            state.repetitions >= MIN_RECALLS &&
            state.intervalDays >= Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS
}
