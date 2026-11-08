package org.token.english

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.token.english.core.common.ContentStatus
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.LearningStage
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.feature.lesson.LessonEvent
import org.token.english.feature.lesson.LessonViewModel

/**
 * The guided lesson as a learner walks it (checklist Phase 2/4): one step at a
 * time, Continue that validates, Previous that keeps the work, a skip that is
 * not mastery, a resume that lands on the same step, and a finish that only
 * happens at the summary.
 *
 * `LearningStagePlanTest` covers the rules themselves; this drives the session
 * that obeys them.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GuidedLessonTest {

    private val lessonId = "a2.guided.lesson-01"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun lesson(grammar: String? = "## قاعده\nمتن توضیح") = Lesson(
        id = lessonId,
        level = LearningLevel.A2,
        title = "Guided",
        titleFa = "راهنماشده",
        topic = "Testing",
        estimatedMinutes = 6,
        order = 1,
        grammarTipFa = grammar,
    )

    private fun word() = VocabularyItem(
        id = "$lessonId.w1",
        word = "ticket",
        translation = "بلیت",
        definition = "a pass",
        pronunciation = "/ˈtɪkɪt/",
        level = LearningLevel.A2,
        partOfSpeech = "noun",
        examples = emptyList(),
        collocations = emptyList(),
        lessonId = lessonId,
    )

    private fun choice() = Exercise.MultipleChoice(
        id = "$lessonId.ex.01",
        lessonId = lessonId,
        question = "Choose",
        questionFa = "انتخاب کن",
        options = listOf("right", "wrong"),
        correctIndex = 0,
        skill = Skill.VOCABULARY,
        explanation = "because",
    )

    private fun listening() = Exercise.Listening(
        id = "$lessonId.ex.02",
        lessonId = lessonId,
        audioText = "Where is the station?",
        accepted = listOf("Where is the station?"),
        skill = Skill.LISTENING,
    )

    private fun production() = Exercise.SentenceProduction(
        id = "$lessonId.ex.03",
        lessonId = lessonId,
        prompt = "بگو بلیت می‌خواهم",
        mustUse = listOf("want", "ticket"),
        minWords = 3,
        accepted = listOf("I want a ticket"),
        skill = Skill.WRITING,
    )

    private inner class Harness(
        val lessonRepo: FakeLessonRepository = FakeLessonRepository(lessons = listOf(lesson()))
            .apply {
                vocabulary[lessonId] = listOf(word())
                exercises[lessonId] = listOf(choice(), listening(), production())
            },
        val progress: FakeProgressRepository = FakeProgressRepository(),
    ) {
        val knowledge = FakeKnowledgeRepository()

        fun build(): LessonViewModel = LessonViewModel(
            lessonId = lessonId,
            contentStatus = MutableStateFlow(ContentStatus.READY),
            seedContent = { true },
            lessonRepository = lessonRepo,
            progressRepository = progress,
            submitExercise = SubmitExerciseUseCase(progress, knowledge),
            completeLesson = CompleteLessonUseCase(lessonRepo, progress),
            audioPlayer = FakeAudioPlayer(),
            voiceNote = FakeVoiceNote(),
            speaker = RecordingSpeaker(),
            isAppInForeground = { true },
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessonRepo),
            profileEngine = DefaultMasteryProfileEngine(),
            selector = AdaptiveExerciseSelector(),
            focusItemId = null,
        )

        /** Continue through every teaching step up to and including [stage]. */
        fun walkTo(vm: LessonViewModel, stage: LearningStage) {
            while (vm.state.value.activeStage != stage) {
                vm.onEvent(LessonEvent.Next())
            }
        }

        /** Answers the current multiple-choice item correctly. */
        fun answerCorrectly(vm: LessonViewModel) {
            val exercise = vm.state.value.currentExercise as Exercise.MultipleChoice
            val correct = exercise.options.indexOf(exercise.options[exercise.correctIndex])
            vm.onEvent(LessonEvent.SelectOption(correct))
            vm.onEvent(LessonEvent.Submit)
        }
    }

    @Test
    fun `a lesson opens on its context step and shows no exercise yet`() {
        val vm = Harness().build()
        val state = vm.state.value

        assertFalse("the content is loaded", state.isLoading)
        assertEquals(LearningStage.CONTEXT, state.activeStage)
        assertTrue("nothing to grade on the first step", state.isTeachingStage)
        assertNull("no exercise is on screen", state.currentExercise)
        assertEquals("step one", 1, state.stageNumber)
        assertEquals("the lesson's own steps", 7, state.stageTotal)
        assertEquals("opening a step is not progress", 0, state.lessonPercent)
    }

    @Test
    fun `continue walks the teaching steps and records each one`() {
        val h = Harness()
        val vm = h.build()

        vm.onEvent(LessonEvent.Next())
        assertEquals(LearningStage.GRAMMAR, vm.state.value.activeStage)
        vm.onEvent(LessonEvent.Next())
        assertEquals(LearningStage.VOCABULARY, vm.state.value.activeStage)
        vm.onEvent(LessonEvent.Next())
        assertEquals(LearningStage.PRACTICE, vm.state.value.activeStage)

        assertEquals(
            setOf(LearningStage.CONTEXT.id, LearningStage.GRAMMAR.id, LearningStage.VOCABULARY.id),
            h.lessonRepo.savedCompleted[lessonId],
        )
        assertEquals("the saved step is where the learner stands", LearningStage.PRACTICE.id, h.lessonRepo.savedStages[lessonId])
        assertTrue("three of the required steps are done", vm.state.value.lessonPercent > 0)
    }

    @Test
    fun `a continue queued against a step already left is ignored`() {
        val vm = Harness().build()

        // Two taps of the same rendered button: the first moves on, the second still
        // names the step that button belonged to, so it must not complete the next one.
        vm.onEvent(LessonEvent.Next(LearningStage.CONTEXT))
        assertEquals(LearningStage.GRAMMAR, vm.state.value.activeStage)
        vm.onEvent(LessonEvent.Next(LearningStage.CONTEXT))
        assertEquals(
            "the stale tap never skips the card it landed on",
            LearningStage.GRAMMAR,
            vm.state.value.activeStage,
        )
    }

    @Test
    fun `practice does not advance until its exercise is answered`() {
        val h = Harness()
        val vm = h.build()
        h.walkTo(vm, LearningStage.PRACTICE)

        vm.onEvent(LessonEvent.Next())
        assertEquals("an unanswered step holds", LearningStage.PRACTICE, vm.state.value.activeStage)

        h.answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())
        assertEquals(LearningStage.LISTENING, vm.state.value.activeStage)
        assertTrue(
            "practice is done once its items are answered",
            h.lessonRepo.savedCompleted[lessonId].orEmpty().contains(LearningStage.PRACTICE.id),
        )
    }

    @Test
    fun `previous reopens a completed step without erasing progress`() {
        val h = Harness()
        val vm = h.build()
        h.walkTo(vm, LearningStage.PRACTICE)
        h.answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())
        assertEquals(LearningStage.LISTENING, vm.state.value.activeStage)
        val percentBefore = vm.state.value.lessonPercent
        val doneBefore = h.lessonRepo.savedCompleted[lessonId]

        vm.onEvent(LessonEvent.Previous)
        assertEquals(LearningStage.PRACTICE, vm.state.value.activeStage)
        assertEquals("going back changes no percentage", percentBefore, vm.state.value.lessonPercent)
        assertEquals("nothing was un-completed", doneBefore, h.lessonRepo.savedCompleted[lessonId])
        assertTrue("the step can be revisited", vm.state.value.stageState!!.isCompleted(LearningStage.PRACTICE))
    }

    @Test
    fun `an optional step can be skipped and is never counted as mastery`() {
        val h = Harness()
        val vm = h.build()
        h.walkTo(vm, LearningStage.PRACTICE)
        h.answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())
        assertEquals(LearningStage.LISTENING, vm.state.value.activeStage)

        vm.onEvent(LessonEvent.SkipStage)

        assertEquals(LearningStage.WRITING, vm.state.value.activeStage)
        assertTrue("the skip is stored", h.lessonRepo.savedSkipped[lessonId].orEmpty().contains(LearningStage.LISTENING.id))
        assertFalse(
            "a skip is not a completion",
            h.lessonRepo.savedCompleted[lessonId].orEmpty().contains(LearningStage.LISTENING.id),
        )
        assertTrue(vm.state.value.stageState!!.isSkipped(LearningStage.LISTENING))
    }

    @Test
    fun `a required step offers no skip`() {
        val h = Harness()
        val vm = h.build()

        vm.onEvent(LessonEvent.SkipStage)

        assertEquals("the context step cannot be skipped", LearningStage.CONTEXT, vm.state.value.activeStage)
        assertTrue(h.lessonRepo.savedSkipped[lessonId].orEmpty().isEmpty())
    }

    @Test
    fun `a restart resumes on the saved step with its bookkeeping`() {
        val h = Harness()
        val first = h.build()
        h.walkTo(first, LearningStage.PRACTICE)
        h.answerCorrectly(first)
        first.onEvent(LessonEvent.Next())
        first.onEvent(LessonEvent.SkipStage)
        assertEquals(LearningStage.WRITING, first.state.value.activeStage)
        val percentBefore = first.state.value.lessonPercent
        // Close the first session like a process death would.
        first.viewModelScope.coroutineContext.cancelChildren()

        val resumed = h.build()
        val state = resumed.state.value

        assertEquals("the learner lands on the same step", LearningStage.WRITING, state.activeStage)
        assertTrue("completed steps are restored", state.stageState!!.isCompleted(LearningStage.PRACTICE))
        assertTrue("the skip is restored as a skip", state.stageState!!.isSkipped(LearningStage.LISTENING))
        assertEquals("no progress was lost", percentBefore, state.lessonPercent)
    }

    @Test
    fun `the review step is the summary and completes the lesson`() {
        val h = Harness(
            lessonRepo = FakeLessonRepository(lessons = listOf(lesson(grammar = null))).apply {
                exercises[lessonId] = listOf(choice())
            },
        )
        val vm = h.build()

        vm.onEvent(LessonEvent.Next()) // context
        assertEquals(LearningStage.PRACTICE, vm.state.value.activeStage)
        h.answerCorrectly(vm)
        vm.onEvent(LessonEvent.Next())

        assertEquals(LearningStage.REVIEW, vm.state.value.activeStage)
        assertEquals("the whole bar is filled at the summary", 100, vm.state.value.lessonPercent)
        assertTrue("the lesson is completed", vm.state.value.completed)
        assertTrue("completion is persisted", h.lessonRepo.completed.contains(lessonId))
    }
}
