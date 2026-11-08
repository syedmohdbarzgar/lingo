package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.common.ContentStatus
import org.token.english.data.content.ContentParser
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.AssessPlacementUseCase
import org.token.english.feature.onboarding.PlacementEvent
import org.token.english.feature.onboarding.PlacementViewModel

/**
 * PlacementViewModel tests (checklist v8 item 3): the adaptive stop (a beginner
 * must not answer all six bands), the band-based level that comes out, the
 * onboarding write, and the mastery calibration (P1-1) — on in-memory fakes.
 *
 * The question set mirrors the real shape the scorer assumes: six CEFR bands of
 * [PlacementViewModel]-visible multiple-choice items, A1 → C2 in order.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlacementViewModelTest {

    private val bandSize = 6
    private val bandCount = LearningLevel.entries.size // A1 … C2

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val skills = listOf(Skill.GRAMMAR, Skill.VOCABULARY, Skill.READING)

    private fun questions(): List<Exercise.MultipleChoice> =
        (0 until bandSize * bandCount).map { index ->
            Exercise.MultipleChoice(
                id = "placement.q${index + 1}",
                lessonId = ContentParser.PLACEMENT_LESSON_ID,
                question = "Question ${index + 1}",
                questionFa = null,
                options = listOf("right", "wrong"),
                correctIndex = 0,
                skill = skills[index % skills.size],
            )
        }

    private class Harness(questions: List<Exercise.MultipleChoice>) {
        val lessonRepo = FakeLessonRepository()
        val progress = FakeProgressRepository()
        // A fresh install: onboarding is still open, so `finish` must close it.
        val settings = FakeSettingsRepository(testSettings().copy(isFirstLaunch = true))
        val contentStatus = MutableStateFlow(ContentStatus.READY)

        /** How many times the retry re-ran the bundle write (the FAILED path's repair). */
        var seeds = 0

        init {
            lessonRepo.exercises[ContentParser.PLACEMENT_LESSON_ID] = questions
        }

        fun build(): PlacementViewModel = PlacementViewModel(
            contentStatus = contentStatus,
            seedContent = {
                seeds++
                contentStatus.value = ContentStatus.READY
                true
            },
            lessonRepository = lessonRepo,
            assessPlacement = AssessPlacementUseCase(progress),
            settingsRepository = settings,
        )
    }

    private fun build(): PlacementViewModel = Harness(questions()).build()

    private fun answerCorrectly(vm: PlacementViewModel) {
        // This harness feeds only choice questions (the typed flow lives in
        // PlacementTypedItemTest) — `current` is any Exercise since the A-5/A-6 round.
        val question = vm.state.value.current as Exercise.MultipleChoice
        vm.onEvent(PlacementEvent.Select(question.correctIndex))
        vm.onEvent(PlacementEvent.Next)
    }

    private fun answerWrong(vm: PlacementViewModel) {
        val question = vm.state.value.current as Exercise.MultipleChoice
        vm.onEvent(PlacementEvent.Select((question.correctIndex + 1) % question.options.size))
        vm.onEvent(PlacementEvent.Next)
    }

    @Test
    fun `the questions load once content is seeded`() {
        val vm = build()

        assertFalse(vm.state.value.isLoading)
        assertEquals(bandSize * bandCount, vm.state.value.questions.size)
        assertEquals(0, vm.state.value.index)
        assertNotNull(vm.state.value.current)
    }

    @Test
    fun `a placement read that throws lands in the empty state, and retry loads it`() {
        // Same shape as the lesson's guard (P1-7): the read used to sit bare in
        // `init`'s launch, so a throw left the spinner up forever — on onboarding,
        // before the learner can reach a single lesson.
        val h = Harness(questions())
        h.lessonRepo.readError = IllegalStateException("database cannot be opened")
        val vm = h.build()

        assertFalse("no spinner nobody can close", vm.state.value.isLoading)
        assertTrue("the screen's own empty state is shown", vm.state.value.questions.isEmpty())

        h.lessonRepo.readError = null
        vm.retry()

        assertFalse(vm.state.value.isLoading)
        assertEquals("the retry reads the questions", bandSize * bandCount, vm.state.value.questions.size)
    }

    @Test
    fun `a failed bundle write ends the wait as a retryable error, and the retry reseeds`() {
        // The wait used to be on "is it seeded yet": a write that failed left it
        // false forever, so onboarding sat on a spinner nobody could leave. The
        // wait is now on the status, which FAILED also ends.
        val h = Harness(questions())
        h.contentStatus.value = ContentStatus.FAILED
        val vm = h.build()

        assertFalse("a failed write ends the wait", vm.state.value.isLoading)
        assertTrue("and the screen can name the write, not a read", vm.state.value.contentFailed)
        assertTrue("no questions were invented", vm.state.value.questions.isEmpty())

        vm.retry()

        assertEquals("the retry runs the bundle write again", 1, h.seeds)
        assertFalse("and the error is behind it", vm.state.value.contentFailed)
        assertEquals(
            "the questions are there once the write lands",
            bandSize * bandCount,
            vm.state.value.questions.size,
        )
    }

    @Test
    fun `a beginner who fails the first band is stopped early`() {
        val vm = build()

        // Three misses in band A1 make it unwinnable: even a perfect finish would be
        // 3/6, below the 2/3 bar of four — so the test stops there.
        repeat(3) { answerWrong(vm) }

        assertTrue("the test ends instead of running 36 questions", vm.state.value.finished)
        assertEquals(3, vm.state.value.answers.size)
        assertEquals(LearningLevel.A1, vm.state.value.resultLevel)
    }

    @Test
    fun `a learner who clears two bands and fails the third starts at the second`() {
        val vm = build()

        repeat(bandSize * 2) { answerCorrectly(vm) }
        repeat(3) { answerWrong(vm) }

        assertTrue(vm.state.value.finished)
        assertEquals("A1 + A2 answered, B1 stopped once it became unwinnable", 15, vm.state.value.answers.size)
        assertEquals(LearningLevel.A2, vm.state.value.resultLevel)
    }

    @Test
    fun `a perfect run is scored all the way to C2`() {
        val vm = build()

        while (!vm.state.value.finished && vm.state.value.current != null) answerCorrectly(vm)

        assertTrue(vm.state.value.finished)
        assertEquals(bandSize * bandCount, vm.state.value.answers.size)
        assertEquals(LearningLevel.C2, vm.state.value.resultLevel)
        val assessment = vm.state.value.assessment!!
        assertEquals("every question is in the report", bandSize * bandCount, assessment.answered)
        assertEquals(LearningLevel.C2, assessment.level)
    }

    @Test
    fun `the same answers calibrate skill mastery, not just the level`() {
        val h = Harness(questions())
        val vm = h.build()

        while (!vm.state.value.finished && vm.state.value.current != null) answerCorrectly(vm)

        assertTrue("placement seeds the profile (P1-1)", h.progress.attempts.isNotEmpty())
        assertTrue(
            "every measured skill is reported",
            vm.state.value.assessment!!.bySkill.map { it.skill }.containsAll(skills),
        )
    }

    @Test
    fun `finishing stores the level and closes onboarding`() {
        val h = Harness(questions())
        val vm = h.build()
        while (!vm.state.value.finished && vm.state.value.current != null) answerCorrectly(vm)
        var done = false

        vm.finish { done = true }

        assertTrue(done)
        assertEquals(LearningLevel.C2, h.settings.settingsFlow.value.level)
        assertFalse("the learner never sees onboarding twice", h.settings.settingsFlow.value.isFirstLaunch)
    }
}
