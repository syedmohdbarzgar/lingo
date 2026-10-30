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
import org.token.english.domain.model.Skill
import org.token.english.feature.conversation.ConversationEvent
import org.token.english.feature.conversation.ConversationPracticeViewModel
import org.token.english.feature.conversation.SpeechBus
import org.token.english.feature.conversation.SpeechError
import org.token.english.feature.conversation.SpeechState

/**
 * ConversationPracticeViewModel tests (checklist B-6): the screen's own logic with
 * no microphone, no Vosk and no Android — the recogniser is replaced by a
 * [SpeechState] flow the test drives by hand, which is exactly the seam the
 * service publishes through in production.
 *
 * What is pinned here: which exercises become dialogue turns, that the partner's
 * question is spoken and shown with its Persian gloss, that a take paused
 * mid-sentence is graded as a whole, that a take the app never heard is not
 * scored as a wrong answer, and that leaving the feature stops the microphone.
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
        // The recogniser's allowed phrases live on the process-wide bus; a leak
        // between tests would make the grammar assertions pass for the wrong turn.
        SpeechBus.reset()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun lesson(id: String, order: Int, titleFa: String = "درس $id") = Lesson(
        id = id,
        level = LearningLevel.A1,
        title = id,
        titleFa = titleFa,
        topic = "greetings",
        estimatedMinutes = 5,
        order = order,
    )

    /** A dialogue turn: the lesson authored a question for the partner to ask. */
    private fun dialogue(
        lessonId: String,
        id: String,
        question: String,
        answer: String,
        questionFa: String = "پرسش پارتنر",
        tip: String? = null,
        moreAnswers: List<String> = emptyList(),
    ) = Exercise.Speaking(
        id = id,
        lessonId = lessonId,
        prompt = "پاسخ بده",
        referenceAnswers = listOf(answer) + moreAnswers,
        question = question,
        questionFa = questionFa,
        skill = Skill.SPEAKING,
        explanation = tip,
    )

    /** The legacy shape: no question, the learner repeats the model. */
    private fun echo(lessonId: String, id: String, answer: String) = Exercise.Speaking(
        id = id,
        lessonId = lessonId,
        prompt = "این جمله را بگو",
        referenceAnswers = listOf(answer),
        skill = Skill.SPEAKING,
    )

    private fun repository(vararg lessons: Pair<Lesson, List<Exercise>>) =
        FakeLessonRepository(lessons = lessons.map { it.first }).apply {
            lessons.forEach { (lesson, exercises) -> this.exercises[lesson.id] = exercises }
        }

    private fun viewModel(lessonRepository: FakeLessonRepository): ConversationPracticeViewModel =
        ConversationPracticeViewModel(
            lessonRepository = lessonRepository,
            speaker = speaker,
            contentSeeded = MutableStateFlow(true),
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

    // ---- turns ----------------------------------------------------------------

    @Test
    fun `only speaking items become turns, in lesson order`() {
        val lessons = repository(
            lesson("a1.second", order = 2, titleFa = "درس دوم") to
                listOf(dialogue("a1.second", "s2", "How are you?", "I'm fine, thank you.")),
            lesson("a1.first", order = 1, titleFa = "درس اول") to listOf(
                dialogue("a1.first", "s1", "Where is the bank?", "The bank is on the left."),
                Exercise.Translation(
                    id = "m1",
                    lessonId = "a1.first",
                    prompt = "…",
                    accepted = listOf("a"),
                ),
            ),
        )

        val vm = viewModel(lessons)

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertEquals(listOf("s1", "s2"), state.turns.map { it.exerciseId })
        assertEquals(listOf("درس اول", "درس دوم"), state.turns.map { it.lessonTitleFa })
        assertEquals("1 / 2", state.progressLabel)
    }

    @Test
    fun `an authored question becomes the partner's line and a repeat item has none`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "What are you doing this weekend?", "I'm going to visit my family.", questionFa = "آخر هفته چه کار می‌کنی؟", tip = "با going to حرف بزن."),
                    echo("a1.one", "s2", "Nice to meet you too."),
                ),
            ),
        )

        val dialogueTurn = vm.state.value.turns[0]
        assertEquals("What are you doing this weekend?", dialogueTurn.partnerLine)
        assertEquals("آخر هفته چه کار می‌کنی؟", dialogueTurn.partnerLineFa)
        assertEquals("I'm going to visit my family.", dialogueTurn.answer)
        assertEquals("با going to حرف بزن.", dialogueTurn.tipFa)

        // A repeat item still works: nothing is asked, the model is what to say.
        assertNull(vm.state.value.turns[1].partnerLine)
        assertNull(vm.state.value.turns[1].partnerLineFa)
    }

    @Test
    fun `a bundle with no speaking items is an empty state, not an error`() {
        val vm = viewModel(repository(lesson("a1.one", order = 1) to emptyList()))

        assertTrue(vm.state.value.isEmpty)
        assertEquals(SpeechError.NONE, vm.state.value.error)
    }

    @Test
    fun `turns wait for the content seeder instead of reading an empty table`() {
        val repository = repository(
            lesson("a1.one", order = 1) to
                listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
        )
        val seeded = MutableStateFlow(false)
        val vm = ConversationPracticeViewModel(
            lessonRepository = repository,
            speaker = speaker,
            contentSeeded = seeded,
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

        // Still loading: an empty table at this instant must not become the
        // permanent "no turns" state.
        assertTrue(vm.state.value.isLoading)

        seeded.value = true

        assertFalse(vm.state.value.isLoading)
        assertEquals(listOf("s1"), vm.state.value.turns.map { it.exerciseId })
    }

    @Test
    fun `the partner opens the conversation by speaking its question`() {
        viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        assertEquals(listOf("How are you?"), speaker.spoken)
    }

    @Test
    fun `the partner can ask again when the learner reopens the turn`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        speaker.spoken.clear()

        vm.onEvent(ConversationEvent.SpeakPartner)

        assertEquals(listOf("How are you?"), speaker.spoken)
    }

    @Test
    fun `a repeat item speaks the model instead of a question`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(echo("a1.one", "s1", "Nice to meet you too.")),
            ),
        )
        speaker.spoken.clear()

        vm.onEvent(ConversationEvent.SpeakPartner)

        assertEquals(listOf("Nice to meet you too."), speaker.spoken)
    }

    // ---- taking a turn --------------------------------------------------------

    @Test
    fun `starting a response starts the recogniser and never stops it first`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        stops = 0

        vm.onEvent(ConversationEvent.StartTake)

        // A stop racing the start tears the foreground service down before it can
        // call startForeground() — process crash, seen on a real device (Android 12).
        assertEquals(0, stops)
        assertEquals(1, starts)
    }

    @Test
    fun `a partial is shown live and never graded while the learner speaks`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        speech.value = SpeechState(isListening = true, partial = "i'm fine")
        assertEquals("i'm fine", vm.state.value.liveTranscript)
        assertNull(vm.state.value.verdict) // nothing to grade yet
    }

    @Test
    fun `a sentence paused mid-way is graded as the whole sentence`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue(
                        "a1.one",
                        "s1",
                        "What are you doing this weekend?",
                        "I'm going to visit my family.",
                    ),
                ),
            ),
        )

        // Vosk finalises an utterance at every pause. The service accumulates the
        // segments, so the screen sees the take growing instead of restarting —
        // this is the state sequence that used to wipe the first half.
        speech.value = SpeechState(isListening = true, finalText = "i'm going")
        speech.value = SpeechState(isListening = true, finalText = "i'm going to visit")
        speech.value = SpeechState(isListening = true, finalText = "i'm going to visit my family")
        assertNull(vm.state.value.verdict)

        speech.value = SpeechState(isListening = false, finalText = "i'm going to visit my family")

        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
        assertEquals("i'm going to visit my family", vm.state.value.finalText)
    }

    @Test
    fun `the same take is not graded twice`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(isListening = false, finalText = "i'm fine thank you")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)

        // A late re-emission of the same finished take must not grade again.
        speech.value = SpeechState(isListening = false, finalText = "i would like a coffee")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
    }

    @Test
    fun `a take with no recognised words is unheard, never a wrong answer`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(isListening = false, finalText = "")

        assertTrue(vm.state.value.unheard)
        assertNull(vm.state.value.verdict)
    }

    @Test
    fun `a take broken by the recogniser is not graded`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(
            isListening = false,
            finalText = "i'm fine thank you",
            error = SpeechError.INTERRUPTED,
        )

        assertNull(vm.state.value.verdict)
        assertFalse(vm.state.value.unheard)
        assertEquals(SpeechError.INTERRUPTED, vm.state.value.error)
    }

    @Test
    fun `retry clears the previous verdict and restarts the microphone`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(isListening = false, finalText = "i would like a coffee")
        assertEquals(SpeakingVerdict.RETRY, vm.state.value.verdict)
        starts = 0

        vm.onEvent(ConversationEvent.Retry)

        assertNull(vm.state.value.verdict)
        assertEquals("", vm.state.value.finalText)
        assertEquals("", vm.state.value.partial)
        assertEquals(1, starts)
    }

    @Test
    fun `next records the exchange, advances, and speaks the following question`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                    dialogue("a1.one", "s2", "Do you speak English?", "Yes, a little."),
                ),
            ),
        )
        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(isListening = false, finalText = "i'm fine thank you")
        speaker.spoken.clear()

        vm.onEvent(ConversationEvent.Next)

        val state = vm.state.value
        assertEquals(1, state.index)
        assertEquals("s2", state.current?.exerciseId)
        assertEquals(1, state.history.size)
        assertEquals(SpeakingVerdict.CORRECT, state.history.single().verdict)
        assertEquals("i'm fine thank you", state.history.single().said)
        assertEquals(listOf("Do you speak English?"), speaker.spoken)
    }

    @Test
    fun `a near answer can still move on, so one hard sentence is never a dead end`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How do you get to the station?", "Go straight and turn left at the traffic light."),
                    dialogue("a1.one", "s2", "Do you speak English?", "Yes, a little."),
                ),
            ),
        )
        speech.value = SpeechState(isListening = true)
        // 5 of the 6 content words: unmistakably the same sentence, not the whole one.
        speech.value = SpeechState(isListening = false, finalText = "go straight and turn left at the traffic")
        assertEquals(SpeakingVerdict.NEAR, vm.state.value.verdict)

        vm.onEvent(ConversationEvent.Next)

        assertEquals(1, vm.state.value.index)
        assertEquals(SpeakingVerdict.NEAR, vm.state.value.history.single().verdict)
    }

    @Test
    fun `next at the last turn finishes the conversation`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        vm.onEvent(ConversationEvent.Next)

        assertTrue(vm.state.value.isFinished)
    }

    // ---- the partner's voice -------------------------------------------------

    @Test
    fun `retry starts a new take without stopping the recogniser first`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        speech.value = SpeechState(isListening = true, finalText = "hello")
        speech.value = SpeechState(isListening = false, finalText = "hello")
        stops = 0
        starts = 0

        vm.onEvent(ConversationEvent.Retry)

        // The stop/start race is what tore the service down and left the next take
        // unable to stop: a retry is a new take, never a stop first.
        assertEquals(0, stops)
        assertEquals(1, starts)
    }

    @Test
    fun `the mic stays closed until the partner has finished speaking`() {
        val slowSpeaker = RecordingSpeaker(finishImmediately = false)
        val vm = ConversationPracticeViewModel(
            lessonRepository = repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
            speaker = slowSpeaker,
            contentSeeded = MutableStateFlow(true),
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )
        starts = 0

        // The opening question is still playing: answering now would record the
        // device's own voice and grade the question as the answer.
        assertTrue(vm.state.value.isPartnerSpeaking)
        vm.onEvent(ConversationEvent.StartTake)
        assertEquals(0, starts)

        slowSpeaker.finishSpeaking()

        assertFalse(vm.state.value.isPartnerSpeaking)
        vm.onEvent(ConversationEvent.StartTake)
        assertEquals(1, starts)
    }

    @Test
    fun `leaving the screen silences the partner`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        clearCallsOf(vm)

        assertEquals(1, speaker.stops)
    }

    // ---- the summary ---------------------------------------------------------

    @Test
    fun `the summary counts answered exchanges, not skipped ones`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                    dialogue("a1.one", "s2", "Where are you from?", "I'm from Iran."),
                ),
            ),
        )

        speech.value = SpeechState(isListening = true, finalText = "i'm fine thank you")
        speech.value = SpeechState(isListening = false, finalText = "i'm fine thank you")
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
        vm.onEvent(ConversationEvent.Next)

        // The second exchange is skipped without speaking: a turn the learner moved
        // past is not a miss, so it must not drag the accuracy down.
        vm.onEvent(ConversationEvent.Next)

        val state = vm.state.value
        assertTrue(state.isFinished)
        assertEquals(1, state.answeredCount)
        assertEquals(1, state.correctCount)
        assertEquals(0, state.retryCount)
        assertEquals(100, state.accuracyPercent)
    }

    @Test
    fun `restart rewinds to the first exchange and clears the score`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                    dialogue("a1.one", "s2", "Where are you from?", "I'm from Iran."),
                ),
            ),
        )
        vm.onEvent(ConversationEvent.Next)
        vm.onEvent(ConversationEvent.Next)
        assertTrue(vm.state.value.isFinished)
        speaker.spoken.clear()

        vm.onEvent(ConversationEvent.Restart)

        val state = vm.state.value
        assertEquals(0, state.index)
        assertTrue(state.history.isEmpty())
        assertFalse(state.isFinished)
        assertEquals(0, state.answeredCount)
        // The conversation opens again from the top, spoken.
        assertEquals(listOf("How are you?"), speaker.spoken)
    }

    @Test
    fun `leaving the feature stops the microphone`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        stops = 0

        clearCallsOf(vm)

        assertEquals(1, stops)
    }

    // ---- grading against everything the turn accepts --------------------------

    @Test
    fun `an authored alternative reply is graded correct`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue(
                        "a1.one",
                        "s1",
                        "How are you?",
                        "I'm fine, thank you.",
                        moreAnswers = listOf("Fine thanks."),
                    ),
                ),
            ),
        )

        speech.value = SpeechState(isListening = true, finalText = "fine thanks")
        speech.value = SpeechState(isListening = false, finalText = "fine thanks")

        // Grading only the first accepted answer made this NEAR: "you" is missing
        // from a reply the bundle itself lists.
        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
    }

    @Test
    fun `the recogniser is constrained to the turn's own sentences`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        // Nothing is recorded until the learner asks to answer.
        assertNull(SpeechBus.grammar)

        vm.onEvent(ConversationEvent.StartTake)

        val grammar = SpeechBus.grammar.orEmpty()
        assertTrue(
            "recogniser grammar does not list the accepted answer: $grammar",
            grammar.contains("\"i am fine thank you\""),
        )
        assertTrue("unknown token missing: $grammar", grammar.contains("\"[unk]\""))
    }

    @Test
    fun `an echo of the partner's line is not accepted as the answer`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "Nice to meet you.", "Nice to meet you too."),
                ),
            ),
        )

        // The recogniser picked the device up through the speaker; before the echo
        // rule this take was "near" — almost an answer — which it never was.
        speech.value = SpeechState(isListening = true, finalText = "nice to meet you")
        speech.value = SpeechState(isListening = false, finalText = "nice to meet you")

        assertEquals(SpeakingVerdict.RETRY, vm.state.value.verdict)
    }

    /** `onCleared` is protected; a subclass is how a test reaches it. */
    private fun clearCallsOf(vm: ConversationPracticeViewModel) {
        val method = androidx.lifecycle.ViewModel::class.java
            .getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(vm)
    }
}
