package org.token.english.domain.model

/**
 * One step as the lesson actually ships it: the exercises it runs (empty for the
 * teaching steps), and whether the lesson has the material for it at all.
 *
 * A stage that is available but has no exercises is a teaching step the learner
 * leaves with Continue; a stage with exercises completes when every one of them
 * has been answered.
 */
data class LessonStageUnit(
    val stage: LearningStage,
    val exercises: List<Exercise> = emptyList(),
) {
    /** Teaching steps have no exercises; their completion is the learner's Continue. */
    val isTeaching: Boolean get() = exercises.isEmpty()
}
