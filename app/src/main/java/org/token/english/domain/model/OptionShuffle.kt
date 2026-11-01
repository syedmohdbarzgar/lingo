package org.token.english.domain.model

import kotlin.random.Random

/**
 * Deterministic per-question option shuffle.
 *
 * Authored content must never make the correct answer guessable from its
 * position ("the first option is always right"), so the order shown to the
 * learner is re-derived from the exercise id: the same question always shows
 * the same order — stable within a session and across app launches — while the
 * correct option is relocated by an index-independent permutation.
 *
 * The correct index is remapped, so graders and UI keep working unchanged.
 *
 * [variant] is how a repeat of the same question gets a *different* order: the
 * lesson re-queues a missed exercise once, and showing it in the same layout the
 * learner just got wrong lets them remember the position instead of the answer.
 * Zero is the first appearance (stable across launches); a repeat passes its own
 * variant, so the same repeat always looks the same too.
 */
fun Exercise.MultipleChoice.shuffledForDisplay(variant: Int = 0): Exercise.MultipleChoice {
    if (options.size < 2) return this
    val correct = correctIndex.coerceIn(0, options.lastIndex)
    val order = options.indices.shuffled(Random(id.hashCode() + VARIANT_SEED_STEP * variant))
    return copy(
        options = order.map { options[it] },
        correctIndex = order.indexOf(correct),
    )
}

/** Keeps a variant well clear of another exercise's own id hash. */
private const val VARIANT_SEED_STEP = 7_919
