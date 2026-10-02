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
 */
fun Exercise.MultipleChoice.shuffledForDisplay(): Exercise.MultipleChoice {
    if (options.size < 2) return this
    val correct = correctIndex.coerceIn(0, options.lastIndex)
    val order = options.indices.shuffled(Random(id.hashCode()))
    return copy(
        options = order.map { options[it] },
        correctIndex = order.indexOf(correct),
    )
}
