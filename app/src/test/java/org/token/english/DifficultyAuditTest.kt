package org.token.english

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.domain.engine.DifficultyAudit
import org.token.english.domain.engine.DifficultyVerdict
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.usecase.GetDifficultyAuditUseCase

/**
 * The roadmap's other half of difficulty (P1): the authored number is a hand
 * judgement, and this is the read that checks it against what learners actually did —
 * without ever correcting it from a handful of answers.
 */
class DifficultyAuditTest {

    private val day = 86_400_000L
    private val start = 1_760_000_000_000L

    private fun mc(id: String, difficulty: Int, lessonId: String = "a1.l") = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "q",
        questionFa = null,
        options = listOf("a", "b"),
        correctIndex = 0,
        difficulty = difficulty,
    )

    private fun attempt(
        exerciseId: String,
        correct: Boolean,
        at: Long,
        lessonId: String = "a1.l",
        stageId: String = "practice",
    ) = LessonAttempt(
        lessonId = lessonId,
        stageId = stageId,
        exerciseId = exerciseId,
        answer = if (correct) "a" else "b",
        correct = correct,
        at = at,
    )

    /** [count] answers spread over [days] days, so the day rule is satisfied on purpose. */
    private fun answers(
        exerciseId: String,
        count: Int,
        correct: Int,
        days: Int = 2,
        lessonId: String = "a1.l",
        from: Long = start,
    ): List<LessonAttempt> = (0 until count).map { index ->
        attempt(
            exerciseId = exerciseId,
            correct = index < correct,
            at = from + (index % days) * day + index * 30_000L,
            lessonId = lessonId,
        )
    }

    private fun levels(level: LearningLevel = LearningLevel.A1) = mapOf("a1.l" to level)

    @Test
    fun `one answer proves nothing, however wrong it was`() {
        val report = DifficultyAudit.report(
            listOf(attempt("ex1", correct = false, at = start)),
            mapOf("ex1" to mc("ex1", difficulty = 1)),
            levels(),
        )

        assertEquals(DifficultyVerdict.INSUFFICIENT, report.items.single().verdict)
        assertTrue("no suggestion may be drawn from one answer", report.suggestions.isEmpty())
    }

    @Test
    fun `a single sitting is one state, not eight answers`() {
        // Eight misses inside twenty minutes: the sample size is there, the evidence is
        // not — a learner grinding one item on one day says nothing about its difficulty.
        val sameDay = (0 until DifficultyAudit.MIN_ATTEMPTS).map { index ->
            attempt("ex1", correct = false, at = start + index * 60_000L)
        }

        val report = DifficultyAudit.report(sameDay, mapOf("ex1" to mc("ex1", difficulty = 1)), levels())

        assertEquals(1, report.items.single().days)
        assertEquals(DifficultyVerdict.INSUFFICIENT, report.items.single().verdict)
    }

    @Test
    fun `an easy-authored item missed half the time asks for a harder number`() {
        val report = DifficultyAudit.report(
            answers("easy", count = 10, correct = 3),
            mapOf("easy" to mc("easy", difficulty = 1)),
            levels(),
        )

        val item = report.items.single()
        assertEquals(10, item.attempts)
        assertEquals(7, item.misses)
        assertEquals(DifficultyVerdict.SUGGEST_HARDER, item.verdict)
        assertEquals(listOf("easy"), report.suggestions.map { it.exerciseId })
    }

    @Test
    fun `a hard-authored item answered almost always right asks for an easier number`() {
        // B1 uses the full scale, so 3 is its hard end and 2 exists below it.
        val report = DifficultyAudit.report(
            answers("hard", count = 10, correct = 10, lessonId = "b1.l"),
            mapOf("hard" to mc("hard", difficulty = 3, lessonId = "b1.l")),
            mapOf("b1.l" to LearningLevel.B1),
        )

        assertEquals(DifficultyVerdict.SUGGEST_EASIER, report.items.single().verdict)
    }

    @Test
    fun `a middling accuracy moves nothing either way`() {
        // 7 of 10: neither half-right nor almost-always. The audit names no gap because
        // there is no gap to name, and the number stays where the author put it.
        val report = DifficultyAudit.report(
            answers("middle", count = 10, correct = 7, lessonId = "b1.l"),
            mapOf("middle" to mc("middle", difficulty = 2, lessonId = "b1.l")),
            mapOf("b1.l" to LearningLevel.B1),
        )

        assertEquals(DifficultyVerdict.CONSISTENT, report.items.single().verdict)
        assertTrue(report.suggestions.isEmpty())
    }

    @Test
    fun `a suggestion never points below the level's floor`() {
        // C1/C2 start at 2: an item there answered perfectly has no easier value to be
        // given, so the audit stays quiet instead of proposing one the bundle cannot hold.
        val report = DifficultyAudit.report(
            answers("floor", count = 10, correct = 10, lessonId = "c1.l"),
            mapOf("floor" to mc("floor", difficulty = 2, lessonId = "c1.l")),
            mapOf("c1.l" to LearningLevel.C1),
        )

        assertEquals(DifficultyVerdict.CONSISTENT, report.items.single().verdict)
        assertTrue(report.suggestions.isEmpty())
    }

    @Test
    fun `the thinking gap ignores a break and the first answer of a lesson`() {
        // 10 s apart, then a ten-minute break, then 20 s apart: the median is taken over
        // the two real thinking times, never over the break and never over the first
        // answer (which has no predecessor at all).
        val log = listOf(
            attempt("ex1", correct = true, at = start),
            attempt("ex1", correct = true, at = start + 10_000L),
            attempt("ex1", correct = true, at = start + 10_000L + 600_000L),
            attempt("ex1", correct = true, at = start + 630_000L),
        )

        val item = DifficultyAudit.report(log, mapOf("ex1" to mc("ex1", difficulty = 2)), levels())
            .items.single()

        assertEquals(20_000L, item.medianGapMs)
    }

    @Test
    fun `an item the bundle no longer ships is skipped, not invented`() {
        val log = answers("gone", count = 10, correct = 0) + answers("kept", count = 10, correct = 10, from = start + 3 * day)

        val report = DifficultyAudit.report(log, mapOf("kept" to mc("kept", difficulty = 3)), levels())

        assertEquals(listOf("kept"), report.items.map { it.exerciseId })
    }

    @Test
    fun `the level fold adds the items of one level up`() {
        val lessonB1 = "b1.l"
        val log = answers("a1ex", count = 10, correct = 5) +
            answers("b1ex", count = 10, correct = 9, lessonId = lessonB1)

        val report = DifficultyAudit.report(
            log,
            mapOf(
                "a1ex" to mc("a1ex", difficulty = 2),
                "b1ex" to mc("b1ex", difficulty = 3, lessonId = lessonB1),
            ),
            mapOf("a1.l" to LearningLevel.A1, lessonB1 to LearningLevel.B1),
        )

        assertEquals(listOf(LearningLevel.A1, LearningLevel.B1), report.byLevel.map { it.level })
        assertEquals(10, report.byLevel[0].attempts)
        assertEquals(5, report.byLevel[0].misses)
        assertEquals(1, report.byLevel[1].misses)
    }

    @Test
    fun `an empty log reads nothing at all`() {
        val report = DifficultyAudit.report(emptyList(), emptyMap(), emptyMap())

        assertTrue(report.items.isEmpty())
        assertTrue(report.byLevel.isEmpty())
        assertTrue(report.suggestions.isEmpty())
    }

    @Test
    fun `the use case joins the log to the authored difficulty and the lesson level`() {
        // The recording half already exists (v9): `lesson_attempt` has the exercise, the
        // verdict and the time of every answer. This pins the read that turns it into a
        // per-item judgement, joined on the authored exercise and its lesson's level.
        val lessons = FakeLessonRepository(
            lessons = listOf(Lesson("a1.l", LearningLevel.A1, "t", "ت", "topic", 5, 1)),
        )
        lessons.exercises["a1.l"] = listOf(mc("ex1", difficulty = 1))
        lessons.attemptsLog += answers("ex1", count = 10, correct = 2)

        val report = runBlocking { GetDifficultyAuditUseCase(lessons)() }

        val item = report.items.single()
        assertEquals("ex1", item.exerciseId)
        assertEquals(LearningLevel.A1, item.level)
        assertEquals(1, item.authoredDifficulty)
        assertEquals(DifficultyVerdict.SUGGEST_HARDER, item.verdict)
        // Every kept gap in this log is one minute: the day-long break between the two
        // sittings is dropped, not averaged in.
        assertEquals(60_000L, item.medianGapMs)
    }

    @Test
    fun `an install with no answers produces no report rows`() {
        val lessons = FakeLessonRepository(
            lessons = listOf(Lesson("a1.l", LearningLevel.A1, "t", "ت", "topic", 5, 1)),
        )

        val report = runBlocking { GetDifficultyAuditUseCase(lessons)() }

        assertTrue(report.items.isEmpty())
        assertTrue("no level is reported before an item has evidence", report.byLevel.isEmpty())
    }
}
