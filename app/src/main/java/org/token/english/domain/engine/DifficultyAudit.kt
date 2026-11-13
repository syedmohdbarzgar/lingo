package org.token.english.domain.engine

import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LessonAttempt

/**
 * What the answer log says about one exercise's *real* difficulty.
 *
 * `INSUFFICIENT` is not a verdict about the item — it is the refusal to have one: the
 * roadmap's rule is that a difficulty is never corrected from a handful of answers, or
 * from one learner grinding the same item in a single sitting. Only [DifficultyAudit]
 * decides when the evidence is enough.
 */
enum class DifficultyVerdict {
    /** Not enough answers (or not spread over enough days) to say anything. */
    INSUFFICIENT,

    /** Observed and authored difficulty agree closely enough to leave the number alone. */
    CONSISTENT,

    /** Answered wrong at least half the time although authored easy — raise the number. */
    SUGGEST_HARDER,

    /** Answered almost always right although authored hard — lower the number. */
    SUGGEST_EASIER,
}

/**
 * One exercise's performance over the window the audit read.
 *
 * [medianGapMs] is the median time between this answer and the **previous answer in the
 * same lesson**, capped at [DifficultyAudit.MAX_GAP_MS]: a learner who walked away for
 * ten minutes did not spend ten minutes thinking, so a break is dropped rather than
 * averaged in. It is a diagnostic, never a verdict on its own — a slow correct answer and
 * a slow distracting one look identical here.
 */
data class ItemPerformance(
    val exerciseId: String,
    val lessonId: String,
    val level: LearningLevel?,
    val authoredDifficulty: Int,
    /** Every answer in the window — the diagnostic half, retries included. */
    val attempts: Int,
    val misses: Int,
    /**
     * The answers that count as evidence: the **first** of each local day. A retry ten
     * seconds after the feedback is not a second observation of the item — the learner
     * has just been told the answer — so [independent] keeps the log's volume while
     * [verdict] reads only what the item was met cold with.
     */
    val independent: Int,
    val independentMisses: Int,
    /** Distinct learner-local calendar days the item was answered on. */
    val days: Int,
    val medianGapMs: Long?,
    val verdict: DifficultyVerdict,
) {
    /** 0..1 over the independent sample; 0 when there is none. */
    val accuracy: Float
        get() = if (independent == 0) 0f else (independent - independentMisses) / independent.toFloat()
}

/** The same fold per CEFR level, so a whole level can be read without listing its items. */
data class LevelPerformance(
    val level: LearningLevel,
    val items: Int,
    val attempts: Int,
    val misses: Int,
) {
    val accuracy: Float get() = if (attempts == 0) 0f else (attempts - misses) / attempts.toFloat()
}

/** The audit's read: every exercise with enough evidence, plus the per-level fold. */
data class DifficultyAuditReport(
    val items: List<ItemPerformance>,
    val byLevel: List<LevelPerformance>,
) {
    /** Only the items whose number the evidence argues against. */
    val suggestions: List<ItemPerformance>
        get() = items.filter {
            it.verdict == DifficultyVerdict.SUGGEST_HARDER || it.verdict == DifficultyVerdict.SUGGEST_EASIER
        }
}

/**
 * Checks the authored `difficulty` against what learners actually did (roadmap P1): the
 * hand judgement says what an item *should* cost, this says what it did.
 *
 * Three deliberate limits, each one a rule rather than a tuning knob:
 *
 * - **A minimum sample before any verdict, and one answer per day inside it.**
 *   [MIN_ATTEMPTS] **independent** answers over at least [MIN_DAYS] days — one answer,
 *   or eight taps in one session, is one state of one learner, not a difficulty. An
 *   answer is independent when it is the first of its learner-local calendar day: a
 *   retry is kept in the log ([ItemPerformance.attempts]) for diagnostics, but the
 *   learner has just been shown the answer, so it cannot count as a second observation.
 *   Below the bar every item is [DifficultyVerdict.INSUFFICIENT] and
 *   [DifficultyAuditReport.suggestions] is empty.
 * - **Days are the learner's, not UTC's.** The day of an answer comes from the
 *   [dayKey] seam, which the app fills with `TimeUtil.dateKey` (the device's calendar).
 *   Dividing the epoch by 86 400 000 would put an answer given at 00:30 into the
 *   previous day in Tehran and split a session that happened in one evening in UTC-8 —
 *   and with `MIN_ATTEMPTS` answers now meaning that many *days*, a wrong boundary is
 *   the difference between "no evidence" and a suggestion.
 * - **A suggestion only points where the scale can actually go.** The scale is relative to
 *   the lesson's level (content v66), so A1/A2 run 1..2 and C1/C2 run 2..3 — an item that
 *   is already at its level's floor has no lower number to be given. The engine carries
 *   that band so a suggestion is never one the bundle cannot express, and it never moves an
 *   item off the scale either.
 * - **Nothing is written from it.** The audit is a read; content stays hand-judged, and a
 *   suggestion is a candidate for the next authoring pass (see `docs/debt.md`).
 *
 * Pure and deterministic: attempts in any order, no clock, no I/O.
 */
object DifficultyAudit {

    /** Answers an item needs before its number may be questioned. */
    const val MIN_ATTEMPTS = 8

    /** And they must span at least this many days — one sitting is one state. */
    const val MIN_DAYS = 2

    /** Below this accuracy an easy-authored item is harder than it claims. */
    const val LOW_ACCURACY = 0.5f

    /** At or above this accuracy a hard-authored item is easier than it claims. */
    const val HIGH_ACCURACY = 0.85f

    /** A longer gap is a break, not thinking time, and is dropped from the median. */
    const val MAX_GAP_MS = 120_000L

    /** The difficulty scale's ends (content v66: 1..3, relative to the lesson's level). */
    const val EASY_END = 1
    const val HARD_END = 3

    /**
     * @param attempts the answer log, any order; duplicates are real answers.
     * @param exercisesById the authored exercises — an id the bundle no longer ships is
     *   skipped, because a difficulty judgement about content that is gone is unusable.
     * @param levelByLesson the level each lesson belongs to, for the per-level fold.
     * @param dayKey the learner-local calendar day of an epoch millisecond. Required, not
     *   defaulted: a UTC default here is exactly the bug this parameter exists to close.
     */
    fun report(
        attempts: List<LessonAttempt>,
        exercisesById: Map<String, Exercise>,
        levelByLesson: Map<String, LearningLevel>,
        dayKey: (Long) -> Int,
    ): DifficultyAuditReport {
        if (attempts.isEmpty()) return DifficultyAuditReport(emptyList(), emptyList())

        val gaps: Map<LessonAttempt, Long?> = thinkingGaps(attempts)

        val items = attempts
            .filter { it.exerciseId in exercisesById }
            .groupBy { it.exerciseId }
            .mapNotNull { (exerciseId, answers) ->
                val exercise = exercisesById.getValue(exerciseId)
                val misses = answers.count { !it.correct }
                // One observation per local day: the first answer, before any feedback.
                val firstOfDay = answers
                    .groupBy { dayKey(it.at) }
                    .mapNotNull { (_, ofDay) -> ofDay.minByOrNull { it.at } }
                val independentMisses = firstOfDay.count { !it.correct }
                val days = firstOfDay.size
                val median = answers.mapNotNull { gaps[it] }.sorted().let { sorted ->
                    if (sorted.isEmpty()) null else sorted[sorted.size / 2]
                }
                ItemPerformance(
                    exerciseId = exerciseId,
                    lessonId = exercise.lessonId,
                    level = levelByLesson[exercise.lessonId],
                    authoredDifficulty = exercise.difficulty,
                    attempts = answers.size,
                    misses = misses,
                    independent = days,
                    independentMisses = independentMisses,
                    days = days,
                    medianGapMs = median,
                    verdict = verdict(
                        accuracy = (days - independentMisses) / days.toFloat(),
                        independent = days,
                        days = days,
                        authoredDifficulty = exercise.difficulty,
                        band = bandOf(levelByLesson[exercise.lessonId]),
                    ),
                )
            }
            .sortedBy { it.exerciseId }

        val byLevel = items
            .filter { it.level != null }
            .groupBy { it.level!! }
            .map { (level, ofLevel) ->
                LevelPerformance(
                    level = level,
                    items = ofLevel.size,
                    attempts = ofLevel.sumOf { it.attempts },
                    misses = ofLevel.sumOf { it.misses },
                )
            }
            .sortedBy { it.level.ordinal }

        return DifficultyAuditReport(items = items, byLevel = byLevel)
    }

    /**
     * How long each answer took, measured from the previous answer **in the same lesson**.
     * The first answer of a lesson has no gap (nothing preceded it), and a gap above
     * [MAX_GAP_MS] is dropped: the learner was away, not thinking.
     */
    private fun thinkingGaps(attempts: List<LessonAttempt>): Map<LessonAttempt, Long?> {
        val out = mutableMapOf<LessonAttempt, Long?>()
        attempts.groupBy { it.lessonId }.forEach { (_, ofLesson) ->
            var previous: LessonAttempt? = null
            ofLesson.sortedBy { it.at }.forEach { attempt ->
                val gap = previous?.let { attempt.at - it.at }
                out[attempt] = gap?.takeIf { it in 0..MAX_GAP_MS }
                previous = attempt
            }
        }
        return out
    }

    /**
     * The difficulty band the item's own level can express — the same bounds
     * `scripts/author_difficulty.mjs` seeds with, and `ContentValidator` refuses values
     * outside. A lesson the app cannot place belongs to no band, so the full scale is used
     * and a suggestion may then sit outside the level's own band: better a suggestion the
     * next authoring pass rejects than a silent no-op.
     */
    private fun bandOf(level: LearningLevel?): IntRange = when (level) {
        LearningLevel.A1, LearningLevel.A2 -> EASY_END..2
        LearningLevel.B1, LearningLevel.B2 -> EASY_END..HARD_END
        LearningLevel.C1, LearningLevel.C2 -> 2..HARD_END
        null -> EASY_END..HARD_END
    }

    private fun verdict(
        accuracy: Float,
        independent: Int,
        days: Int,
        authoredDifficulty: Int,
        band: IntRange,
    ): DifficultyVerdict {
        if (independent < MIN_ATTEMPTS || days < MIN_DAYS) return DifficultyVerdict.INSUFFICIENT
        return when {
            // Raised only while there is a harder number to raise it to.
            accuracy < LOW_ACCURACY && authoredDifficulty < band.last -> DifficultyVerdict.SUGGEST_HARDER
            // Lowered only while the level still has an easier value below it.
            accuracy >= HIGH_ACCURACY && authoredDifficulty > band.first -> DifficultyVerdict.SUGGEST_EASIER
            else -> DifficultyVerdict.CONSISTENT
        }
    }
}
