package org.token.english.domain.engine

import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryProfile

/**
 * Picks which exercise to show, instead of always walking the authored list
 * (audit §6 — "the system must not always take the next exercise from a fixed
 * list; it chooses by knowledge state → weakness → exercise type").
 *
 * The rule is deliberately narrow and explainable: exercise formats whose mastery
 * dimension is still below [strongThreshold] are front-loaded, weakest dimension
 * first; formats the learner has already consolidated keep their authored order
 * and sink to the back. So a learner with strong recognition but shaky recall
 * stops being served multiple-choice and gets fill-in-the-blank instead.
 *
 * Pure and deterministic — the same inputs always produce the same order, and a
 * learner with no evidence yet sees the lesson exactly as authored (stable sort).
 * Difficulty-aware selection needs an authored difficulty on exercises, which the
 * content does not carry yet — see AGENTS.md §10.
 */
class AdaptiveExerciseSelector(
    /** At or above this, a dimension needs no extra prompting. */
    private val strongThreshold: Float = STRONG_THRESHOLD,
) {

    /**
     * The available exercises, ordered so the learner's weakest dimensions come
     * first. Never drops or duplicates an exercise — this is a permutation.
     */
    fun order(exercises: List<Exercise>, profile: MasteryProfile): List<Exercise> =
        exercises
            .withIndex()
            .sortedWith(
                compareBy(
                    // 0 = still weak (front-load), 1 = consolidated (keep order).
                    { if (profile.masteryOf(ExerciseDimension.dimensionOf(it.value)) < strongThreshold) 0 else 1 },
                    // Weakest first — but only meaningful for the weak group; giving
                    // every consolidated item 0f here lets the index preserve order.
                    {
                        val mastery = profile.masteryOf(ExerciseDimension.dimensionOf(it.value))
                        if (mastery < strongThreshold) mastery else 0f
                    },
                    { it.index },
                ),
            )
            .map { it.value }

    /** The single exercise to show now, or `null` when there is nothing to show. */
    fun next(exercises: List<Exercise>, profile: MasteryProfile): Exercise? =
        order(exercises, profile).firstOrNull()

    companion object {
        const val STRONG_THRESHOLD = 0.85f
    }
}
