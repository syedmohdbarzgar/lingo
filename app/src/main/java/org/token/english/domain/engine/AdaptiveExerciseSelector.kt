package org.token.english.domain.engine

import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile

/**
 * Picks which exercise to show, instead of always walking the authored list
 * (audit §6 — "the system must not always take the next exercise from a fixed
 * list; it chooses by knowledge state → weakness → exercise type").
 *
 * **Three states of evidence, not a number (P0 gap between "no evidence" and "zero").**
 * A dimension's mastery reads 0f both when it was never assessed and when it was and the
 * learner got it wrong, so deciding from the number alone made a fresh dimension look like
 * the worst one — and a lesson with one shaky recall item had its never-asked
 * sentence-production pushed to the very front, ahead of the gap that was actually
 * observed. The order below therefore classifies every exercise by
 * [MasteryProfile.isEstablished] first:
 *
 *  - **weak** — assessed and below [strongThreshold]: promoted, weakest first, and served
 *    from its easy end (a proven 0f is weak, and it is weak *with* evidence).
 *  - **unseen** — never assessed: neutral. It neither earns a promotion nor a demotion, and
 *    keeps its authored position; a fresh install therefore sees the lesson exactly as
 *    written.
 *  - **consolidated** — assessed and at or above the bar: demoted, and served from its hard
 *    end instead of re-tapping what is already known.
 *
 * Weak items come before unseen ones because a gap that was observed is the one thing the
 * session knows it must fix; unseen items come before consolidated ones because nothing has
 * shown they are mastered. Nothing is dropped or duplicated — every rule here is a key in
 * one sort, so the result is always a permutation.
 */
class AdaptiveExerciseSelector(
    /** At or above this, a dimension needs no extra prompting. */
    private val strongThreshold: Float = STRONG_THRESHOLD,
) {

    /**
     * What the profile says about one dimension. `UNSEEN` is the whole point: it is
     * not a low score, it is the absence of a score.
     */
    private enum class Evidence { WEAK, UNSEEN, CONSOLIDATED }

    private fun evidenceOf(profile: MasteryProfile, dimension: MasteryDimension): Evidence = when {
        !profile.isEstablished(dimension) -> Evidence.UNSEEN
        profile.masteryOf(dimension) < strongThreshold -> Evidence.WEAK
        else -> Evidence.CONSOLIDATED
    }

    /**
     * The available exercises, ordered so the learner's observed weaknesses come
     * first. Never drops or duplicates an exercise — this is a permutation.
     */
    fun order(exercises: List<Exercise>, profile: MasteryProfile): List<Exercise> =
        exercises
            .withIndex()
            .sortedWith(
                compareBy(
                    // 1. Evidence band: weak → unseen → consolidated.
                    { evidenceOf(profile, ExerciseDimension.dimensionOf(it.value)).ordinal },
                    // 2. Among proven gaps, the deepest one leads. Meaningless for the
                    //    other bands (0f lets the later keys / authored index decide).
                    {
                        val dimension = ExerciseDimension.dimensionOf(it.value)
                        if (evidenceOf(profile, dimension) == Evidence.WEAK) profile.masteryOf(dimension) else 0f
                    },
                    // 3. Level, only where evidence names a direction: weak → easiest
                    //    first, consolidated → hardest first, unseen → 0 (authored order).
                    {
                        when (evidenceOf(profile, ExerciseDimension.dimensionOf(it.value))) {
                            Evidence.WEAK -> it.value.difficulty
                            Evidence.CONSOLIDATED -> -it.value.difficulty
                            Evidence.UNSEEN -> 0
                        }
                    },
                    { it.index },
                ),
            )
            .map { it.value }

    /** The single exercise to show now, or `null` when there is nothing to show. */
    fun next(exercises: List<Exercise>, profile: MasteryProfile): Exercise? =
        order(exercises, profile).firstOrNull()

    companion object {
        /** Same bar as the node gate in [DefaultKnowledgeEngine]: one [STRONG_MASTERY]. */
        const val STRONG_THRESHOLD = STRONG_MASTERY
    }
}
