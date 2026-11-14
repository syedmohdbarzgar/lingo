package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.MissCategory
import org.token.english.domain.engine.RecentMisses
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.Skill

/**
 * The list behind the miss mix (recent-misses pass).
 *
 * The engine is pure — the log, the window's exercises and a title lookup in, rows out —
 * so what is pinned here is the four rules that decide *which* questions a learner is
 * shown: only misses, one row per question (with how often), a short list, and nothing the
 * bundle can no longer name. The ViewModel's half (the list reaches Progress, and a failed
 * read never blanks the screen) is in `ProgressViewModelTest`.
 */
class RecentMissesTest {

    private val lessonId = "a2.airport"

    private fun attempt(
        exerciseId: String,
        at: Long,
        correct: Boolean = false,
        lesson: String = lessonId,
    ) = LessonAttempt(
        lessonId = lesson,
        stageId = "practice",
        exerciseId = exerciseId,
        answer = "something",
        correct = correct,
        at = at,
    )

    private fun blank(id: String) = Exercise.FillBlank(
        id = id,
        lessonId = lessonId,
        sentence = "I ______ here.",
        accepted = listOf("live"),
        skill = Skill.GRAMMAR,
    )

    private fun heard(id: String) = Exercise.Listening(
        id = id,
        lessonId = lessonId,
        audioText = "The gate is closed.",
        accepted = listOf("The gate is closed."),
        skill = Skill.LISTENING,
    )

    @Test
    fun `only the questions that went wrong, newest first`() {
        val rows = RecentMisses.of(
            attempts = listOf(
                attempt("ex.01", at = 10L),
                attempt("ex.02", at = 20L),
                // A later correct answer does not erase the miss — the row is "what to fix",
                // and a question answered right after being missed is still worth a look.
                attempt("ex.01", at = 30L, correct = true),
            ),
            exercises = mapOf("ex.01" to blank("ex.01"), "ex.02" to heard("ex.02")),
            titles = mapOf(lessonId to "در فرودگاه"),
        )

        assertEquals(listOf("ex.02", "ex.01"), rows.map { it.exerciseId })
    }

    @Test
    fun `one row per question, carrying how often it was missed`() {
        val rows = RecentMisses.of(
            attempts = listOf(
                attempt("ex.01", at = 10L),
                attempt("ex.01", at = 20L),
                attempt("ex.01", at = 30L),
            ),
            exercises = mapOf("ex.01" to blank("ex.01")),
            titles = mapOf(lessonId to "در فرودگاه"),
        )

        // Three misses of one question are one row: three rows would push the other
        // questions out of a six-row list and fix nothing.
        assertEquals(1, rows.size)
        assertEquals(3, rows.single().misses)
        assertEquals("the row sits where its newest miss was", 30L, rows.single().lastMissAt)
    }

    @Test
    fun `the list is short, and the newest misses are the ones kept`() {
        val exercises = (1..4).associate { "ex.0$it" to blank("ex.0$it") }
        val rows = RecentMisses.of(
            attempts = (1..4).map { attempt("ex.0$it", at = it * 10L) },
            exercises = exercises,
            titles = mapOf(lessonId to "در فرودگاه"),
            limit = 2,
        )

        assertEquals(listOf("ex.04", "ex.03"), rows.map { it.exerciseId })
        assertEquals(RecentMisses.DEFAULT_LIMIT, 6)
    }

    @Test
    fun `a question or a lesson the bundle no longer has is dropped`() {
        val rows = RecentMisses.of(
            attempts = listOf(
                // The exercise is gone: no prompt and no authored answer to show.
                attempt("ex.gone", at = 10L),
                // The lesson is gone: the row would name a lesson the learner cannot open.
                attempt("ex.orphan", at = 20L, lesson = "a2.removed"),
            ),
            exercises = mapOf("ex.orphan" to blank("ex.orphan")),
            titles = mapOf(lessonId to "در فرودگاه"),
        )

        assertTrue("an id nobody can recognise is noise, not a miss", rows.isEmpty())
    }

    @Test
    fun `the row names the taxonomy's category and the authored answer`() {
        val rows = RecentMisses.of(
            attempts = listOf(attempt("ex.01", at = 10L), attempt("ex.02", at = 20L)),
            exercises = mapOf("ex.01" to blank("ex.01"), "ex.02" to heard("ex.02")),
            titles = mapOf(lessonId to "در فرودگاه"),
        )

        assertEquals("در فرودگاه", rows.first().lessonTitleFa)
        assertEquals("a blank that tests a structure is a grammar miss", MissCategory.GRAMMAR, rows.last().category)
        assertEquals("live", rows.last().answer)
        assertEquals(
            "a listening item is comprehension — the skill mapping is the taxonomy's own",
            MissCategory.COMPREHENSION,
            rows.first().category,
        )
        assertEquals("The gate is closed.", rows.first().answer)
    }
}
