package org.token.english.domain.engine

import org.token.english.domain.model.Exercise
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.MasteryProfile

/**
 * One graded attempt, tagged with the kind of knowing it exercised
 * (audit §2: a single mastery number hides recognition from production).
 */
data class DimensionAttempt(
    val dimension: MasteryDimension,
    val correct: Boolean,
)

/**
 * The dimension an exercise actually demands (audit §2/§6). This is the only
 * place the exercise formats are tied to the mastery model, so adding a format
 * means touching one exhaustive `when` — the compiler flags it, nothing else
 * silently mis-classifies.
 *
 * `RETENTION` is deliberately not produced here: retention is a property of the
 * schedule (how well knowledge survives a delay), not of any one answer, so it
 * stays with the SRS interval rather than an exercise type.
 */
object ExerciseDimension {

    fun dimensionOf(exercise: Exercise): MasteryDimension = when (exercise) {
        is Exercise.MultipleChoice -> MasteryDimension.RECOGNITION
        is Exercise.FillBlank -> MasteryDimension.RECALL
        is Exercise.Translation -> MasteryDimension.APPLICATION
        is Exercise.Listening -> MasteryDimension.COMPREHENSION
        is Exercise.Speaking -> MasteryDimension.PRODUCTION
    }
}

/**
 * Builds a per-dimension [MasteryProfile] from graded evidence (audit §2).
 *
 * Each dimension is moved by the same exponentially-weighted [MasteryEngine] the
 * knowledge and skill models use, so "mastery" keeps one definition everywhere
 * and there is no second, drifting learning rule to keep in sync.
 *
 * Seeding mirrors [DefaultKnowledgeEngine]: the first answer on a dimension sets
 * it to 1f (correct) or 0f (wrong) so an untouched dimension is never mistaken
 * for a half-learned one; later answers move it smoothly.
 *
 * Pure and deterministic — a fold over evidence, no clock, no I/O.
 */
class DefaultMasteryProfileEngine(
    private val masteryEngine: MasteryEngine = DefaultMasteryEngine(),
) {

    fun apply(profile: MasteryProfile, dimension: MasteryDimension, correct: Boolean): MasteryProfile {
        val current = profile.byDimension[dimension]
        val next = when (current) {
            null -> if (correct) 1f else 0f
            else -> masteryEngine.update(current, correct)
        }
        return profile.copy(byDimension = profile.byDimension + (dimension to next))
    }

    /** Folds a run of graded attempts into a profile, oldest first. */
    fun profileOf(attempts: List<DimensionAttempt>): MasteryProfile =
        attempts.fold(MasteryProfile()) { profile, attempt ->
            apply(profile, attempt.dimension, attempt.correct)
        }
}
