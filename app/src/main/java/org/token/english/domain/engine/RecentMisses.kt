package org.token.english.domain.engine

import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LessonAttempt

/**
 * One question the learner got wrong recently, as the app can honestly describe it
 * (recent-misses pass).
 *
 * Progress already says *how much* is known and *what kind of knowing* the misses are
 * about ([MissProfile]). Neither names a question, so «weak in grammar» could not be
 * acted on other than by guessing which lesson. This is the row behind that sentence: the
 * lesson, the category, the authored answer, and how often it went wrong.
 */
data class RecentMiss(
    val exerciseId: String,
    val lessonId: String,
    /** The lesson's authored Persian title — what the learner studied. */
    val lessonTitleFa: String,
    /** Which kind of knowing failed; the taxonomy's own category, never a second rule. */
    val category: MissCategory,
    /**
     * The authored answer. Always the English side, so a row can render it LTR through
     * `EnglishText` — and it is what the app actually knows: what was right, not why the
     * learner's answer was wrong, which it cannot observe.
     */
    val answer: String,
    /** How many times this question was answered wrong inside the window. */
    val misses: Int,
    /** When it was last got wrong — the row's place in the list. */
    val lastMissAt: Long,
)

/**
 * Picks the questions behind the miss mix out of the answer log.
 *
 * Four rules, each a decision:
 *
 * - **Only what the bundle can still name.** An attempt whose exercise the content no
 *   longer ships is dropped rather than shown: both the question and the right answer
 *   would be missing, so the row would be an id the learner cannot recognise. The same
 *   goes for a lesson that no longer exists.
 * - **One row per question, newest first.** A question missed three times is one row
 *   with a count, not three rows that push the other questions out of a short list — the
 *   list is "what to fix", and the same question three times fixes nothing.
 * - **A short list.** [DEFAULT_LIMIT] rows, because this sits under the mix on Progress:
 *   a ledger of every miss belongs in an authoring tool, not on a screen a learner opens
 *   between lessons.
 * - **The category comes from the taxonomy.** `MissTaxonomy.categoryOf(skillOf(...))` is
 *   the one mapping the planner, the profile and this list share; a second copy is how
 *   the same gap gets two names.
 *
 * The read itself is the taxonomy's window ([MissTaxonomy.WINDOW]) rather than the
 * archive: "recent" is the point, and the log's index is `at` only through the same
 * bounded scan the profile already pays for.
 */
object RecentMisses {

    /** Rows the screen shows: enough to see a pattern, few enough to act on. */
    const val DEFAULT_LIMIT = 6

    /**
     * @param exercises every exercise the window's lessons ship, keyed by id.
     * @param titles the Persian title of each lesson that holds a miss; a lesson missing
     *   from it no longer exists, so its rows go with it.
     */
    fun of(
        attempts: List<LessonAttempt>,
        exercises: Map<String, Exercise>,
        titles: Map<String, String>,
        limit: Int = DEFAULT_LIMIT,
    ): List<RecentMiss> {
        if (limit <= 0) return emptyList()
        val byExercise = mutableMapOf<String, MutableList<LessonAttempt>>()
        for (attempt in attempts) {
            if (attempt.correct) continue
            // A question the bundle no longer ships cannot be shown; see the rule above.
            if (attempt.exerciseId !in exercises) continue
            byExercise.getOrPut(attempt.exerciseId) { mutableListOf() } += attempt
        }
        return byExercise
            .mapNotNull { (exerciseId, rows) ->
                val exercise = exercises.getValue(exerciseId)
                val newest = rows.maxBy { it.at }
                val title = titles[newest.lessonId] ?: return@mapNotNull null
                val answer = AnswerChecker.correctAnswerText(exercise)
                if (answer.isBlank()) return@mapNotNull null
                RecentMiss(
                    exerciseId = exerciseId,
                    lessonId = newest.lessonId,
                    lessonTitleFa = title,
                    category = MissTaxonomy.categoryOf(AnswerChecker.skillOf(exercise)),
                    answer = answer,
                    misses = rows.size,
                    lastMissAt = newest.at,
                )
            }
            .sortedByDescending { it.lastMissAt }
            .take(limit)
    }
}
