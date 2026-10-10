package org.token.english

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.feature.lesson.LessonEvent
import org.token.english.feature.lesson.LessonStage
import org.token.english.feature.lesson.LessonViewModel

/**
 * LessonViewModel tests (checklist B-6): the requeue-once-on-a-miss rule and the
 * TTS-unavailable fallback, both driven through in-memory fakes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LessonViewModelTest {

    private val lessonId = "a1.test.lesson-01"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun choice(id: String = "$lessonId.ex.01", skill: Skill = Skill.VOCABULARY) =
        Exercise.MultipleChoice(
            id = id,
            lessonId = lessonId,
            question = "Choose",
            questionFa = null,
            options = listOf("right", "wrong"),
            correctIndex = 0,
            skill = skill,
            explanation = "because",
        )

    private fun knowledgeState(itemId: String, mastery: Float) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = 3,
        consecutiveCorrect = 0,
        consecutiveIncorrect = 2,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    private fun blank(id: String) = Exercise.FillBlank(
        id = id,
        lessonId = lessonId,
        sentence = "I ___ here.",
        accepted = listOf("am"),
        skill = Skill.GRAMMAR,
    )

    private inner class Harness(available: Boolean? = true) {
        val lessonRepo = FakeLessonRepository(lessons = listOf(Lesson(lessonId, LearningLevel.A1, "t", "ت", "topic", 5, 1)))
        val progress = FakeProgressRepository()
        val audio = FakeAudioPlayer(available)
        val speaker = RecordingSpeaker()
        val contentSeeded = MutableStateFlow(true)
        val knowledge = FakeKnowledgeRepository()

        val submit = SubmitExerciseUseCase(progress, knowledge)
        val complete = CompleteLessonUseCase(lessonRepo, progress)
        val focusPlan = GetFocusPlanUseCase(knowledge, lessonRepo)

        fun build(focusItemId: String? = null): LessonViewModel = LessonViewModel(
            lessonId = lessonId,
            contentSeeded = contentSeeded,
            lessonRepository = lessonRepo,
            progressRepository = progress,
            submitExercise = submit,
            completeLesson = complete,
            audioPlayer = audio,
            speaker = speaker,
            isAppInForeground = { true },
            getFocusPlan = focusPlan,
            focusItemId = focusItemId,
            studyTickMillis = 0,
        )
    }

    private fun Harness.withOneExercise(): Harness {
        lessonRepo.exercises[lessonId] = listOf(choice())
        return this
    }

    private fun Harness.with(vararg exercises: Exercise): Harness {
        lessonRepo.exercises[lessonId] = exercises.toList()
        return this
    }

    /** Answers the current exercise correctly, whatever option the shuffle put first. */
    private fun answerCorrectly(vm: LessonViewModel) {
        when (val exercise = vm.state.value.currentExercise) {
            is Exercise.MultipleChoice -> vm.onEvent(LessonEvent.SelectOption(exercise.correctIndex))
            is Exercise.FillBlank -> vm.onEvent(LessonEvent.TextAnswerChanged(exercise.accepted.first()))
            else -> error("no correct answer for ${exercise?.id}")
        }
        vm.onEvent(LessonEvent.Submit)
    }

    /** Answers the current exercise wrong. */
    private fun answerWrong(vm: LessonViewModel) {
        val exercise = vm.state.value.currentExercise as Exercise.MultipleChoice
        vm.onEvent(LessonEvent.SelectOption((exercise.correctIndex + 1) % exercise.options.size))
        vm.onEvent(LessonEvent.Submit)
    }

    @Test
    fun `a missed exercise is re-asked once and the lesson ends on the second pass`() {
        val h = Harness().withOneExercise()
        val vm = h.build()

        assertEquals(LessonStage.EXERCISES, vm.state.value.stage)

        // Options are shuffled for display, so the wrong index is derived from the
        // exercise the screen is actually showing — never assumed to be 1.
        fun displayed() = vm.state.value.currentExercise as Exercise.MultipleChoice
        fun wrongIndex() = (displayed().correctIndex + 1) % displayed().options.size

        // Wrong answer → graded wrong and marked to repeat.
        vm.onEvent(LessonEvent.SelectOption(wrongIndex()))
        vm.onEvent(LessonEvent.Submit)
        assertEquals(false, vm.state.value.outcome?.correct)
        assertTrue("a miss is flagged for a second pass", vm.state.value.repeatsCurrent)

        // Continue → the SAME exercise comes back, only once.
        vm.onEvent(LessonEvent.Next)
        assertEquals(h.lessonRepo.exercises[lessonId]!!.first().id, vm.state.value.currentExercise?.id)
        assertEquals(0, vm.state.value.remainingCount)
        assertFalse(vm.state.value.repeatsCurrent)

        // Correct answer on the second pass → the session ends and is recorded.
        vm.onEvent(LessonEvent.SelectOption(displayed().correctIndex))
        vm.onEvent(LessonEvent.Submit)
        assertEquals(true, vm.state.value.outcome?.correct)
        vm.onEvent(LessonEvent.Next)
        assertTrue("the lesson is completed", vm.state.value.completed)
        assertTrue("completion is persisted", lessonId in h.lessonRepo.completed)
    }

    @Test
    fun `a device without an English voice reports audio unavailable`() {
        val h = Harness(available = false).withOneExercise()
        val vm = h.build()
        assertTrue("the listening card must fall back to text", vm.state.value.audioUnavailable)
    }

    // --- checklist B-1: the adaptive slice now drives the session ------------

    @Test
    fun `a fresh lesson keeps the authored order`() {
        val h = Harness().with(
            choice("$lessonId.ex.01"),
            choice("$lessonId.ex.02"),
            blank("$lessonId.ex.03"),
        )
        val vm = h.build()
        assertEquals("$lessonId.ex.01", vm.state.value.currentExercise?.id)
        assertEquals(2, vm.state.value.remainingCount)
        assertNull("nothing weak has been observed yet", vm.state.value.weakestDimension)
    }

    @Test
    fun `the session front-loads the kind of knowing that is still untested`() {
        // Recognition first (multiple choice), then more recognition, and a recall
        // item (fill-in-the-blank) authored last.
        val h = Harness().with(
            choice("$lessonId.ex.01"),
            choice("$lessonId.ex.02"),
            choice("$lessonId.ex.03"),
            blank("$lessonId.ex.04"),
        )
        val vm = h.build()
        assertEquals("$lessonId.ex.01", vm.state.value.currentExercise?.id)

        // One correct answer consolidates recognition, so the unassessed recall
        // item leads the rest of the session instead of more multiple choice.
        answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next)
        assertEquals("$lessonId.ex.04", vm.state.value.currentExercise?.id)
    }

    @Test
    fun `a miss comes back later in the session, it does not jump the queue`() {
        val h = Harness().with(choice("$lessonId.ex.01"), choice("$lessonId.ex.02"))
        val vm = h.build()
        answerWrong(vm)
        vm.onEvent(LessonEvent.Next)

        assertEquals("$lessonId.ex.02", vm.state.value.currentExercise?.id)
        assertEquals("the missed item is still queued", 1, vm.state.value.remainingCount)

        answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next)
        assertEquals("the missed item is re-asked once", "$lessonId.ex.01", vm.state.value.currentExercise?.id)
    }

    @Test
    fun `a miss names the kind of knowing that broke`() {
        val h = Harness().withOneExercise()
        val vm = h.build()
        answerWrong(vm)
        assertEquals(MasteryDimension.RECOGNITION, vm.state.value.weakestDimension)
    }

    @Test
    fun `a focused session drills only the exercises that give evidence for the node`() {
        val h = Harness()
        h.knowledge.itemsFlow.value = listOf(
            KnowledgeItem(
                id = "grammar.test",
                type = KnowledgeType.GRAMMAR,
                title = "Test grammar",
                titleFa = "گرامر آزمایشی",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
            KnowledgeItem(
                id = "vocab.test",
                type = KnowledgeType.VOCABULARY,
                title = "Test words",
                titleFa = "واژگان آزمایشی",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.VOCABULARY),
            ),
        )
        // The node has to be a real weakness for the engine to plan a block.
        h.knowledge.statesFlow.value = listOf(knowledgeState("grammar.test", 0.2f))
        h.with(choice("$lessonId.ex.01", Skill.GRAMMAR), choice("$lessonId.ex.02", Skill.VOCABULARY))

        val vm = h.build(focusItemId = "grammar.test")

        assertEquals("only the grammar exercise is evidence for the node", 1, vm.state.value.exercises.size)
        assertEquals("$lessonId.ex.01", vm.state.value.currentExercise?.id)
        assertEquals("گرامر آزمایشی", vm.state.value.focus?.titleFa)
        assertTrue("the engine's reason is shown", vm.state.value.focus!!.reasonFa.isNotEmpty())
    }

    @Test
    fun `a lesson session carries no focus banner`() {
        val h = Harness().withOneExercise()
        val vm = h.build()
        assertNull(vm.state.value.focus)
    }

    // --- checklist B-9: cancellation is never swallowed ----------------------

    @Test
    fun `cancelling the scope stops the session instead of writing state`() {
        val h = Harness()
        h.knowledge.itemsFlow.value = listOf(
            KnowledgeItem(
                id = "grammar.test",
                type = KnowledgeType.GRAMMAR,
                title = "Test grammar",
                titleFa = "گرامر آزمایشی",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
        )
        h.knowledge.statesFlow.value = listOf(knowledgeState("grammar.test", 0.2f))
        h.with(choice("$lessonId.ex.01", Skill.GRAMMAR))
        // The focus lookup parks here, so the session is cancelled mid-use-case.
        val gate = CompletableDeferred<Unit>()
        h.knowledge.allItemsGate = gate

        val vm = h.build(focusItemId = "grammar.test")
        assertEquals("the session is still loading", 0, vm.state.value.exercises.size)

        vm.viewModelScope.cancel()
        gate.complete(Unit)

        // With a plain runCatching the CancellationException is swallowed, the
        // coroutine carries on and loads the lesson anyway — this is the guard.
        assertEquals("nothing may be loaded after cancellation", 0, vm.state.value.exercises.size)
        assertTrue("the screen stays in its loading state", vm.state.value.isLoading)
    }
}
