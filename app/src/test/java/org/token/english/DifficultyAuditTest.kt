package org.token.english

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.common.TimeUtil
import org.token.english.domain.engine.DifficultyAudit
import org.token.english.domain.engine.DifficultyVerdict
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.usecase.GetDifficultyAuditUseCase
import java.util.Calendar
import java.util.TimeZone

/**
 * The roadmap's other half of difficulty (P1): the authored number is a hand
 * judgement, and this is the read that checks it against what learners actually did —
 * without ever correcting it from a handful of answers.
 *
 * The sample is *independent* answers: one per learner-local calendar day, because a
 * retry made seconds after the feedback is not a second observation of the item. The
 * device calendar is the day boundary, not UTC — which is why these tests set the
 * default time zone and take answers from both sides of a local midnight.
 */
class DifficultyAuditTest {

    private val day = 86_400_000L
    private val start = 1_760_000_000_000L
    private lateinit var previousZone: TimeZone

    /** Tehran: +03:30 all year — the app's primary audience, and no DST since 2022. */
    @Before
    fun useLearnerZone() {
        previousZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tehran"))
    }

    @After
    fun restoreZone() {
        TimeZone.setDefault(previousZone)
    }

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

    /** Local noon of the [index]th day after [start]'s day, in the default zone. */
    private fun noonOfDay(index: Int, zone: TimeZone = TimeZone.getDefault()): Long =
        Calendar.getInstance(zone).apply {
            timeInMillis = start
            add(Calendar.DAY_OF_YEAR, index)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /**
     * [count] independent answers — one per day, at local noon — of which the first
     * [correct] were right. Days are separate on purpose: the audit's bar is that many
     * *days*, so a helper that packed them into one sitting would test nothing.
     */
    private fun answers(
        exerciseId: String,
        count: Int,
        correct: Int,
        lessonId: String = "a1.l",
        fromDay: Int = 0,
    ): List<LessonAttempt> = (0 until count).map { index ->
        attempt(
            exerciseId = exerciseId,
            correct = index < correct,
            at = noonOfDay(fromDay + index),
            lessonId = lessonId,
        )
    }

    /** The audit with the app's own day boundary, which is what the use case passes. */
    private fun audit(
        attempts: List<LessonAttempt>,
        exercises: Map<String, Exercise>,
        levels: Map<String, LearningLevel>,
    ) = DifficultyAudit.report(attempts, exercises, levels, dayKey = TimeUtil::dateKey)

    private fun levels(level: LearningLevel = LearningLevel.A1) = mapOf("a1.l" to level)

    @Test
    fun `one answer proves nothing, however wrong it was`() {
        val report = audit(
            listOf(attempt("ex1", correct = false, at = start)),
            mapOf("ex1" to mc("ex1", difficulty = 1)),
            levels(),
        )

        assertEquals(DifficultyVerdict.INSUFFICIENT, report.items.single().verdict)
        assertTrue("no suggestion may be drawn from one answer", report.suggestions.isEmpty())
    }

    @Test
    fun `a single sitting is one state, not eight answers`() {
        // Eight misses inside twenty minutes: the log's volume is there, the evidence is
        // not — a learner grinding one item on one day says nothing about its difficulty.
        val sameDay = (0 until DifficultyAudit.MIN_ATTEMPTS).map { index ->
            attempt("ex1", correct = false, at = start + index * 60_000L)
        }

        val report = audit(sameDay, mapOf("ex1" to mc("ex1", difficulty = 1)), levels())

        val item = report.items.single()
        assertEquals("the log keeps every answer", 8, item.attempts)
        assertEquals("but only one of them is evidence", 1, item.independent)
        assertEquals(1, item.days)
        assertEquals(DifficultyVerdict.INSUFFICIENT, item.verdict)
    }

    @Test
    fun `a retry on the same day is not a second observation`() {
        // Four answers a day over two days: eight in the log, two independent. The learner
        // has seen the correction three times by the fourth tap, so only the first of each
        // day can say how hard the item was.
        val log = (0 until 2).flatMap { dayIndex ->
            (0 until 4).map { within ->
                attempt("ex1", correct = false, at = noonOfDay(dayIndex) + within * 45_000L)
            }
        }

        val item = audit(log, mapOf("ex1" to mc("ex1", difficulty = 1)), levels()).items.single()

        assertEquals(8, item.attempts)
        assertEquals(8, item.misses)
        assertEquals(2, item.independent)
        assertEquals(2, item.days)
        assertEquals(DifficultyVerdict.INSUFFICIENT, item.verdict)
    }

    @Test
    fun `an answer just after local midnight starts the learner's new day, not UTC's`() {
        // 22:00 and 01:00 Tehran are 18:30 and 21:30 UTC on the *same* date, so an
        // epoch/24h day key would call this one day and swallow the second observation.
        val zone = TimeZone.getTimeZone("Asia/Tehran")
        fun late(hour: Int, year: Int, month: Int, dayOfMonth: Int): Long =
            Calendar.getInstance(zone).apply {
                clear()
                set(year, month, dayOfMonth, hour, 0)
            }.timeInMillis

        val evening = late(22, 2026, Calendar.JANUARY, 1)
        val afterMidnight = late(1, 2026, Calendar.JANUARY, 2)
        assertEquals(
            "the two answers share a UTC day — this is the case the old key got wrong",
            evening / day,
            afterMidnight / day,
        )

        val log = listOf(
            attempt("ex1", correct = false, at = evening),
            attempt("ex1", correct = true, at = afterMidnight),
        )
        val item = audit(log, mapOf("ex1" to mc("ex1", difficulty = 1)), levels()).items.single()

        assertEquals("two local days, so two observations", 2, item.days)
        assertEquals(2, item.independent)
    }

    @Test
    fun `a day that daylight saving shortens is still one day`() {
        // 28 March 23:30 → 29 March 00:30 in Berlin: one hour apart, two local days.
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))

        val item = audit(
            listOf(
                attempt("ex1", correct = true, at = berlin(2026, Calendar.MARCH, 28, 23, 30)),
                attempt("ex1", correct = false, at = berlin(2026, Calendar.MARCH, 29, 0, 30)),
            ),
            mapOf("ex1" to mc("ex1", difficulty = 1)),
            levels(),
        ).items.single()

        assertEquals("the clock moved, the calendar did not", 2, item.days)
    }

    @Test
    fun `a day that daylight saving lengthens is still one day`() {
        // 25 October 2026 is 25 hours long in Berlin: 00:10 and 23:50 are one local day
        // although more than 24 hours apart, which a fixed 24-hour division would split.
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))
        val early = berlin(2026, Calendar.OCTOBER, 25, 0, 10)
        val late = berlin(2026, Calendar.OCTOBER, 25, 23, 50)
        assertTrue("more than a day apart in millis", late - early > day)

        val item = audit(
            listOf(attempt("ex1", correct = true, at = early), attempt("ex1", correct = true, at = late)),
            mapOf("ex1" to mc("ex1", difficulty = 1)),
            levels(),
        ).items.single()

        assertEquals(1, item.days)
        assertEquals(1, item.independent)
    }

    private fun berlin(year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int): Long =
        Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin")).apply {
            clear()
            set(year, month, dayOfMonth, hour, minute)
        }.timeInMillis

    @Test
    fun `an easy-authored item missed half the time asks for a harder number`() {
        val report = audit(
            answers("easy", count = 10, correct = 3),
            mapOf("easy" to mc("easy", difficulty = 1)),
            levels(),
        )

        val item = report.items.single()
        assertEquals(10, item.attempts)
        assertEquals(7, item.misses)
        assertEquals(10, item.independent)
        assertEquals(DifficultyVerdict.SUGGEST_HARDER, item.verdict)
        assertEquals(listOf("easy"), report.suggestions.map { it.exerciseId })
    }

    @Test
    fun `a hard-authored item answered almost always right asks for an easier number`() {
        // B1 uses the full scale, so 3 is its hard end and 2 exists below it.
        val report = audit(
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
        val report = audit(
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
        val report = audit(
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

        val item = audit(log, mapOf("ex1" to mc("ex1", difficulty = 2)), levels()).items.single()

        assertEquals(20_000L, item.medianGapMs)
    }

    @Test
    fun `an item the bundle no longer ships is skipped, not invented`() {
        val log = answers("gone", count = 10, correct = 0) +
            answers("kept", count = 10, correct = 10, fromDay = 12)

        val report = audit(log, mapOf("kept" to mc("kept", difficulty = 3)), levels())

        assertEquals(listOf("kept"), report.items.map { it.exerciseId })
    }

    @Test
    fun `the level fold adds the items of one level up`() {
        val lessonB1 = "b1.l"
        val log = answers("a1ex", count = 10, correct = 5) +
            answers("b1ex", count = 10, correct = 9, lessonId = lessonB1)

        val report = audit(
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
        val report = audit(emptyList(), emptyMap(), emptyMap())

        assertTrue(report.items.isEmpty())
        assertTrue(report.byLevel.isEmpty())
        assertTrue(report.suggestions.isEmpty())
    }

    @Test
    fun `the use case joins the log to the authored difficulty and the lesson level`() {
        // The recording half already exists (v9): `lesson_attempt` has the exercise, the
        // verdict and the time of every answer. This pins the read that turns it into a
        // per-item judgement, joined on the authored exercise and its lesson's level —
        // and the day it counts by is the device's, not UTC's.
        val lessons = FakeLessonRepository(
            lessons = listOf(Lesson("a1.l", LearningLevel.A1, "t", "ت", "topic", 5, 1)),
        )
        lessons.exercises["a1.l"] = listOf(mc("ex1", difficulty = 1))
        lessons.attemptsLog += (0 until 10).flatMap { dayIndex ->
            listOf(
                attempt("ex1", correct = false, at = noonOfDay(dayIndex)),
                attempt("ex1", correct = false, at = noonOfDay(dayIndex) + 60_000L),
            )
        }

        val report = runBlocking { GetDifficultyAuditUseCase(lessons)() }

        val item = report.items.single()
        assertEquals("ex1", item.exerciseId)
        assertEquals(LearningLevel.A1, item.level)
        assertEquals(1, item.authoredDifficulty)
        assertEquals(20, item.attempts)
        assertEquals("ten days, one observation each", 10, item.independent)
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
