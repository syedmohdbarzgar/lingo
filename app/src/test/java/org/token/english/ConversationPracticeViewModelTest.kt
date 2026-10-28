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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.feature.conversation.ConversationEvent
import org.token.english.feature.conversation.ConversationPracticeViewModel
import org.token.english.feature.conversation.SpeechError
import org.token.english.feature.conversation.SpeechState

/**
 * ConversationPracticeViewModel tests (checklist B-6): the screen's own logic with
 * no microphone, no Vosk and no Android — the recogniser is replaced by a
 * [SpeechState] flow the test drives by hand, which is exactly the seam the
 * service publishes through in production.
 *
 * What is pinned here: which exercises become practice targets, when a take is
 * graded exactly once, when a retry clears the previous verdict instead of
 * showing a stale one, and that leaving the feature stops the microphone.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ConversationPracticeViewModelTest {

    private val speech = MutableStateFlow(SpeechState())
    private val speaker = RecordingSpeaker()
    private var starts = 0
    private var stops = 0

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun lesson(id: String, order: Int) = Lesson(
        id = id,
        level = LearningLevel.A1,
        title = id,
        titleFa = id,
        topic = "directions",
        estimatedMinutes = 5,
        order = order,
    )

    private fun speaking(lessonId: String, id: String, prompt: String, target: String) =
        Exercise.Speaking(
            id = id,
            lessonId = lessonId,
            prompt = prompt,
            referenceAnswers = listOf(target),
        )

    private fun viewModel(lessonRepository: FakeLessonRepository): ConversationPracticeViewModel =
        ConversationPracticeViewModel(
            lessonRepository = lessonRepository,
            speaker = speaker,
            contentSeeded = MutableStateFlow(true),
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

    // ---- targets --------------------------------------------------------------

    @Test
    fun `only speaking items become targets, in lesson order`() {
        val lessons = FakeLessonRepository(
            lessons = listOf(lesson("a1.second", order = 2), lesson("a1.first", order = 1)),
        )
        lessons.exercises["a1.first"] = listOf(
            speaking("a1.first", "s1", "از بانک بپرس کجاست", "The bank is on the left"),
            Exercise.Translation(
                id = "m1",
                lessonId = "a1.first",
                prompt = "…",
                accepted = listOf("a"),
            ),
        )
        lessons.exercises["a1.second"] = listOf(
            speaking("a1.second", "s2", "بگو مستقیم برو", "Go straight and turn left"),
        )

        val vm = viewModel(lessons)

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertEquals(listOf("s1", "s2"), state.items.map { it.id })
        assertEquals("1 / 2", state.progressLabel)
    }

    @Test
    fun `a bundle with no speaking items is an empty state, not an error`() {
        val vm = viewModel(FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1))))

        assertTrue(vm.state.value.isEmpty)
        assertEquals(SpeechError.NONE, vm.state.value.error)
    }

    // ---- taking a turn --------------------------------------------------------

    @Test
    fun `targets wait for the content seeder instead of reading an empty table`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight"),
        )
        val seeded = MutableStateFlow(false)
        val vm = ConversationPracticeViewModel(
            lessonRepository = lessons,
            speaker = speaker,
            contentSeeded = seeded,
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

        // Still loading: an empty table at this instant must not become the
        // permanent "no targets" state.
        assertTrue(vm.state.value.isLoading)

        seeded.value = true

        assertFalse(vm.state.value.isLoading)
        assertEquals(listOf("s1"), vm.state.value.items.map { it.id })
    }

    @Test
    fun `starting a take starts the recogniser`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight and turn left"),
        )
        val vm = viewModel(lessons)

        vm.onEvent(ConversationEvent.StartTake)

        assertEquals(1, starts)
    }

    @Test
    fun `starting a take never stops the recogniser first`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight"),
        )
        val vm = viewModel(lessons)
        stops = 0

        vm.onEvent(ConversationEvent.StartTake)

        // A stop racing the start tears the foreground service down before it can
        // call startForeground() — process crash, seen on a real device (Android 12).
        assertEquals(0, stops)
        assertEquals(1, starts)
    }

    @Test
    fun `a partial is shown while speaking and graded once the take is final`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight and turn left"),
        )
        val vm = viewModel(lessons)

        speech.value = SpeechState(isListening = true, partial = "go straight and")
        assertEquals("go straight and", vm.state.value.partial)
        assertNull(vm.state.value.verdict) // nothing to grade yet

        speech.value = SpeechState(isListening = false, finalText = "go straight and turn left")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
        assertEquals("go straight and turn left", vm.state.value.finalText)
    }

    @Test
    fun `the same take is not graded twice`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight and turn left"),
        )
        val vm = viewModel(lessons)

        speech.value = SpeechState(finalText = "go straight and turn left")
        // A re-emission of the SAME second of audio must not overwrite the verdict
        // the learner is already reading (e.g. a stale partial arriving late).
        speech.value = SpeechState(finalText = "go straight and turn left")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)

        // A new empty take reopens grading, so the same sentence can be said again.
        speech.value = SpeechState()
        assertNull(vm.state.value.verdict)
        speech.value = SpeechState(finalText = "go straight and turn left")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
    }

    @Test
    fun `retry clears the previous verdict so no stale feedback is left`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight and turn left"),
        )
        val vm = viewModel(lessons)

        speech.value = SpeechState(finalText = "i would like a coffee")
        assertEquals(SpeakingVerdict.RETRY, vm.state.value.verdict)

        vm.onEvent(ConversationEvent.Retry)

        assertNull(vm.state.value.verdict)
        assertEquals("", vm.state.value.finalText)
        assertEquals("", vm.state.value.partial)
    }

    @Test
    fun `next moves to the following target and stops at the end`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "اول", "Go straight"),
            speaking("a1.one", "s2", "دوم", "Turn left"),
        )
        val vm = viewModel(lessons)

        vm.onEvent(ConversationEvent.Next)
        assertEquals("s2", vm.state.value.current?.id)

        vm.onEvent(ConversationEvent.Next)
        assertTrue(vm.state.value.isFinished)
    }

    @Test
    fun `the model sentence is spoken when asked`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight and turn left"),
        )
        val vm = viewModel(lessons)

        vm.onEvent(ConversationEvent.SpeakModel)

        assertEquals(listOf("Go straight and turn left"), speaker.spoken)
    }

    @Test
    fun `leaving the feature stops the microphone`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson("a1.one", order = 1)))
        lessons.exercises["a1.one"] = listOf(
            speaking("a1.one", "s1", "بگو", "Go straight"),
        )
        clearCallsOf(viewModel(lessons))

        assertEquals(1, stops)
    }

    /** `onCleared` is protected; a subclass is how a test reaches it. */
    private fun clearCallsOf(vm: ConversationPracticeViewModel) {
        val method = androidx.lifecycle.ViewModel::class.java
            .getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(vm)
    }
}
