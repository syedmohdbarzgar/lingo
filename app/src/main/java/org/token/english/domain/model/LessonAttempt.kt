package org.token.english.domain.model

/**
 * One answered lesson question, exactly as the learner answered it (Phase 2).
 *
 * The day's claim ([org.token.english.domain.repository.ProgressRepository.claimAnswer])
 * answers a different question — *has this exercise already moved the model today* — and
 * is overwritten in place. This is the history: which step asked, what the learner typed
 * or picked, whether it was right, and when. It is append-only (a retry is a second row,
 * not a rewrite), which is what makes a resumed step honest: it can point at the first
 * question that has no answer yet instead of trusting a cursor.
 */
data class LessonAttempt(
    val lessonId: String,
    val stageId: String,
    val exerciseId: String,
    val answer: String,
    val correct: Boolean,
    val at: Long,
)

/**
 * Where a step picks up when the lesson is reopened (Phase 2).
 *
 * The saved cursor alone is a weak signal: the learner may have answered the last question
 * of a step and been killed before «ادامه», or a content update may have reordered the
 * step's items since it was saved. The recorded answers are the durable fact, so the step
 * resumes on the **first question of that step with no answer recorded** — never on one
 * already answered, and never past the end.
 *
 * Pure Kotlin, so the rule is testable without Room, a ViewModel or a clock.
 */
object StageResume {
    /**
     * @param exerciseIds the step's items, in show order.
     * @param answered the ids this step already has a recorded answer for.
     * @return the index to open on; the last index when every item was answered
     *   (the step still needs its «ادامه» tap to be completed).
     */
    fun cursorFor(exerciseIds: List<String>, answered: Set<String>): Int {
        if (exerciseIds.isEmpty()) return 0
        val first = exerciseIds.indexOfFirst { it !in answered }
        return if (first >= 0) first else exerciseIds.lastIndex
    }
}
