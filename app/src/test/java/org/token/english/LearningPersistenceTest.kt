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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.common.ContentStatus
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LearningStage
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonAttempt
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StageResume
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.feature.lesson.LessonEvent
import org.token.english.feature.lesson.LessonViewModel

/**
 * Learning persistence (Phase 2).
 *
 * The session used to remember **how far** it had got — one integer cursor — and
 * nothing else. That is not enough to survive a process death honestly: the cursor
 * cannot say *which* questions were answered, so a reopened step could re-ask one the
 * learner had already done, and a content update that reordered a step silently moved
 * the saved position onto a different question.
 *
 * These tests pin the three facts that fix it: an append-only log of every answer, a
 * cursor derived from that log (the **first unanswered** question, not the last
 * index), and a recorded completion time per step. The pure rule is checked on its
 * own first, then through the ViewModel so the wiring is covered too.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LearningPersistenceTest {

    private val lessonId = "a1.test.persist-01"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ------------------------------------------------------------- pure rule

    @Test
    fun `a step resumes on the first question that has no recorded answer`() {
        val ids = listOf("q1", "q2", "q3")
        assertEquals(
            "an answer on q1 must not make the step skip it again",
            1,
            StageResume.cursorFor(ids, answered = setOf("q1")),
        )
        assertEquals(
            "a gap in the middle is where the learner continues",
            1,
            StageResume.cursorFor(ids, answered = setOf("q1", "q3")),
        )
    }

    @Test
    fun `a fully answered step stays on its last question instead of past the end`() {
        val ids = listOf("q1", "q2")
        assertEquals(
            "the step still needs its «ادامه» tap, so the cursor cannot run off the list",
            1,
            StageResume.cursorFor(ids, answered = setOf("q1", "q2")),
        )
        assertEquals(
            "an empty step has nowhere to resume",
            0,
            StageResume.cursorFor(emptyList(), answered = emptySet()),
        )
    }

    // --------------------------------------------------------------- helpers

    private fun choice(id: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "Choose",
        questionFa = null,
        options = listOf("right", "wrong"),
        correctIndex = 0,
        skill = Skill.VOCABULARY,
        explanation = "because",
    )

    private fun repository(vararg exercises: Exercise): FakeLessonRepository {
        val repo = FakeLessonRepository(
            lessons = listOf(Lesson(lessonId, LearningLevel.A1, "t", "ت", "topic", 5, 1)),
            states = listOf(
                LessonState(
                    lessonId = lessonId,
                    currentIndex = 0,
                    completed = false,
                    completedAt = null,
                    stageId = LearningStage.PRACTICE.id,
                ),
            ),
        )
        repo.exercises[lessonId] = exercises.toList()
        return repo
    }

    private fun answerOn(repo: FakeLessonRepository, exerciseId: String, stageId: String = "practice") {
        repo.attemptsLog += LessonAttempt(
            lessonId = lessonId,
            stageId = stageId,
            exerciseId = exerciseId,
            answer = "right",
            correct = true,
            at = 1_700_000_000_000,
        )
    }

    private fun viewModel(
        repo: FakeLessonRepository,
        progress: FakeProgressRepository = FakeProgressRepository(),
        knowledge: FakeKnowledgeRepository = FakeKnowledgeRepository(),
    ) = LessonViewModel(
        lessonId = lessonId,
        contentStatus = MutableStateFlow(ContentStatus.READY),
        seedContent = { true },
        lessonRepository = repo,
        progressRepository = progress,
        submitExercise = SubmitExerciseUseCase(progress, knowledge),
        completeLesson = CompleteLessonUseCase(repo, progress),
        audioPlayer = FakeAudioPlayer(true),
        speaker = RecordingSpeaker(),
        speechState = FakeSpeech().state,
        startRecognition = {},
        stopRecognition = {},
        isAppInForeground = { true },
        getFocusPlan = GetFocusPlanUseCase(knowledge, repo),
        profileEngine = DefaultMasteryProfileEngine(),
        selector = AdaptiveExerciseSelector(),
    )

    // ---------------------------------------------------------- the reopened step

    @Test
    fun `reopening a half-done step continues on the unanswered question, not the saved cursor`() {
        val repo = repository(choice("q1"), choice("q2"), choice("q3"))
        // Two answers were already given, but the saved cursor still points at the
        // first question (the process died before it was written, or a content update
        // reordered the step). The answers are the durable fact and must win.
        answerOn(repo, "q1")
        answerOn(repo, "q2")

        val vm = viewModel(repo)

        assertEquals("the step reopens on practice", LearningStage.PRACTICE, vm.state.value.activeStage)
        assertEquals("q3 is the first question with no answer", "q3", vm.state.value.currentExercise?.id)
        assertEquals(2, vm.state.value.stageCursor)
    }

    @Test
    fun `a lesson from before the log keeps its saved cursor instead of restarting`() {
        val repo = repository(choice("q1"), choice("q2"), choice("q3"))
        repo.statesFlow.value = listOf(
            LessonState(
                lessonId = lessonId,
                currentIndex = 2,
                completed = false,
                completedAt = null,
                stageId = LearningStage.PRACTICE.id,
            ),
        )

        assertEquals(
            "no recorded answers means the saved position is the only evidence there is",
            2,
            viewModel(repo).state.value.stageCursor,
        )
    }

    @Test
    fun `an answer on another step's question cannot move this step's cursor`() {
        val repo = repository(choice("q1"), choice("q2"))
        // A writing-step answer for a question that is not in practice must be
        // ignored here — the log is per step, not one global position.
        answerOn(repo, "q1", stageId = LearningStage.WRITING.id)

        assertEquals("q1 was never answered *in practice*", 0, viewModel(repo).state.value.stageCursor)
    }

    // ------------------------------------------------------------- the history

    @Test
    fun `every graded answer is appended with the step that asked it`() {
        val repo = repository(choice("q1"), choice("q2"))
        val vm = viewModel(repo)

        // Options are shuffled for display (P1/P4), so the tap index is not the
        // answer — the test picks by what the screen is actually showing.
        val shown = vm.state.value.currentExercise as Exercise.MultipleChoice
        vm.onEvent(LessonEvent.SelectOption(shown.options.indexOf("right")))
        vm.onEvent(LessonEvent.Submit)

        val recorded = repo.attemptsLog.single()
        assertEquals(lessonId, recorded.lessonId)
        assertEquals("the step is part of the record", LearningStage.PRACTICE.id, recorded.stageId)
        assertEquals("q1", recorded.exerciseId)
        assertEquals("what the learner picked is what is kept", "right", recorded.answer)
        assertTrue(recorded.correct)
    }

    @Test
    fun `a miss is recorded as a miss, never as a hit`() {
        val repo = repository(choice("q1"))
        val vm = viewModel(repo)

        val shown = vm.state.value.currentExercise as Exercise.MultipleChoice
        vm.onEvent(LessonEvent.SelectOption(shown.options.indexOf("wrong")))
        vm.onEvent(LessonEvent.Submit)

        val recorded = repo.attemptsLog.single()
        assertFalse("the log must not launder a wrong answer", recorded.correct)
        assertEquals("wrong", recorded.answer)
    }

    // ------------------------------------------------------- the completion stamps

    @Test
    fun `finishing a step records when it was finished, and that survives a restart`() {
        // No saved state: the lesson opens on its first step, the context card.
        val repo = FakeLessonRepository(
            lessons = listOf(Lesson(lessonId, LearningLevel.A1, "t", "ت", "topic", 5, 1)),
        )
        repo.exercises[lessonId] = listOf(choice("q1"))

        val vm = viewModel(repo)
        assertEquals(LearningStage.CONTEXT, vm.state.value.activeStage)
        assertNull(
            "a step that was never finished carries no time",
            repo.savedCompletedAt[lessonId]?.get(LearningStage.CONTEXT.id),
        )

        vm.onEvent(LessonEvent.Next(LearningStage.CONTEXT))

        val stamp = repo.savedCompletedAt[lessonId]?.get(LearningStage.CONTEXT.id)
        assertNotNull("completing the context step stamps it", stamp)

        // A restart reads the stamp back — it is not re-derived from a clock at load.
        val reopened = viewModel(repo)
        val restored = reopened.state.value.stageState
            ?.progress
            ?.firstOrNull { it.stage == LearningStage.CONTEXT }
        assertTrue("the step is still recorded as done", restored?.completed == true)
        assertEquals("and it keeps the time it was actually finished", stamp, restored?.completedAt)
    }

    @Test
    fun `revisiting a finished step does not rewrite when it was finished`() {
        val repo = FakeLessonRepository(
            lessons = listOf(Lesson(lessonId, LearningLevel.A1, "t", "ت", "topic", 5, 1)),
        )
        repo.exercises[lessonId] = listOf(choice("q1"))

        val first = viewModel(repo)
        first.onEvent(LessonEvent.Next(LearningStage.CONTEXT))
        val stamp = repo.savedCompletedAt[lessonId]?.get(LearningStage.CONTEXT.id)

        // Back to the finished step, then forward again: the first completion is the
        // one that happened, so the stamp must not move.
        first.onEvent(LessonEvent.Previous)
        first.onEvent(LessonEvent.Next(LearningStage.CONTEXT))

        assertEquals(stamp, repo.savedCompletedAt[lessonId]?.get(LearningStage.CONTEXT.id))
    }
}
