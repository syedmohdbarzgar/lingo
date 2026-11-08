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
import org.token.english.core.common.ContentStatus
import org.token.english.core.common.persianCountPair
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.ExerciseFormat
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.RecordConversationTurnUseCase
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

    /** The two tracks the screen feeds, so a test can see *which* one a turn moved. */
    private val progress = FakeProgressRepository()
    private val knowledge = FakeKnowledgeRepository()

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

    private fun viewModel(
        lessonRepository: FakeLessonRepository,
        lessonId: String? = null,
        conversations: FakeConversationRepository = FakeConversationRepository(),
    ): ConversationPracticeViewModel =
        ConversationPracticeViewModel(
            lessonRepository = lessonRepository,
            lessonId = lessonId,
            conversationRepository = conversations,
            recordTurn = RecordConversationTurnUseCase(progress, knowledge),
            speaker = speaker,
            contentStatus = MutableStateFlow(ContentStatus.READY),
            seedContent = { true },
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

    // ---- turns ----------------------------------------------------------------

    @Test
    fun `a session runs one lesson's dialogue, never a pool of every lesson`() {
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
        // The current lesson is the first one on the path: the airport lesson must
        // not be answered with the greetings lesson's reply in turn two.
        assertEquals(listOf("s1"), state.turns.map { it.exerciseId })
        assertEquals("a1.first", state.lessonId)
        assertEquals("درس اول", state.lessonTitleFa)
        assertEquals(persianCountPair(1, 1), state.progressLabel)
    }

    @Test
    fun `the requested lesson is the one that runs`() {
        val lessons = repository(
            lesson("a1.first", order = 1, titleFa = "درس اول") to
                listOf(dialogue("a1.first", "s1", "How are you?", "I'm fine, thank you.")),
            lesson("a2.airport", order = 2, titleFa = "در فرودگاه") to listOf(
                dialogue("a2.airport", "s09", "May I see your passport?", "Here you are."),
                dialogue("a2.airport", "s10", "How many bags?", "Two bags, please."),
            ),
        )

        val vm = viewModel(lessons, lessonId = "a2.airport")

        assertEquals("a2.airport", vm.state.value.lessonId)
        assertEquals("در فرودگاه", vm.state.value.lessonTitleFa)
        assertEquals(listOf("s09", "s10"), vm.state.value.turns.map { it.exerciseId })
    }

    @Test
    fun `the scene and the partner's role come from the authored turn`() {
        val lessons = repository(
            lesson("a2.airport", order = 1) to listOf(
                Exercise.Speaking(
                    id = "s1",
                    lessonId = "a2.airport",
                    prompt = "پاسخ بده",
                    referenceAnswers = listOf("Here you are."),
                    question = "May I see your passport?",
                    questionFa = "پاسپورتتان را می‌بینم؟",
                    sceneFa = "فرودگاه، باجهٔ تحویل بار",
                    partnerFa = "مأمور فرودگاه",
                    skill = Skill.SPEAKING,
                ),
            ),
        )

        val state = viewModel(lessons).state.value

        assertEquals("فرودگاه، باجهٔ تحویل بار", state.sceneFa)
        assertEquals("مأمور فرودگاه", state.partnerFa)
    }

    @Test
    fun `finishing every turn records the lesson's conversation`() {
        val lessons = repository(
            lesson("a1.one", order = 1) to listOf(
                dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                dialogue("a1.one", "s2", "Do you speak English?", "Yes, a little."),
            ),
        )
        val conversations = FakeConversationRepository()
        val vm = viewModel(lessons, conversations = conversations)

        // Both turns answered, any verdict counts: the point is that the learner
        // spoke the exchange, not that every sentence was perfect.
        listOf("i'm fine thank you", "yes a little").forEach { said ->
            speech.value = SpeechState(isListening = true)
            speech.value = SpeechState(isListening = false, finalText = said)
            vm.onEvent(ConversationEvent.Next)
        }

        assertTrue(vm.state.value.isFinished)
        assertEquals(setOf("a1.one"), conversations.doneFlow.value)
    }

    @Test
    fun `a skipped turn leaves the conversation unrecorded`() {
        val lessons = repository(
            lesson("a1.one", order = 1) to listOf(
                dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                dialogue("a1.one", "s2", "Do you speak English?", "Yes, a little."),
            ),
        )
        val conversations = FakeConversationRepository()
        val vm = viewModel(lessons, conversations = conversations)

        // Walked past without a take: nothing was said, so nothing was practised.
        vm.onEvent(ConversationEvent.Next)
        vm.onEvent(ConversationEvent.Next)

        assertTrue(vm.state.value.isFinished)
        assertTrue(conversations.doneFlow.value.isEmpty())
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
    fun `turns wait for the bundle write instead of reading an empty table`() {
        val repository = repository(
            lesson("a1.one", order = 1) to
                listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
        )
        val status = MutableStateFlow(ContentStatus.LOADING)
        val vm = ConversationPracticeViewModel(
            lessonRepository = repository,
            conversationRepository = FakeConversationRepository(),
            recordTurn = RecordConversationTurnUseCase(progress, knowledge),
            speaker = speaker,
            contentStatus = status,
            seedContent = { true },
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

        // Still loading: an empty table at this instant must not become the
        // permanent "no turns" state.
        assertTrue(vm.state.value.isLoading)

        status.value = ContentStatus.READY

        assertFalse(vm.state.value.isLoading)
        assertEquals(listOf("s1"), vm.state.value.turns.map { it.exerciseId })
    }

    @Test
    fun `a failed bundle write is a retryable error, not an empty conversation`() {
        // The wait used to be on a boolean: a write that failed left it false
        // forever, and the screen fell through to «هنوز گفتگویی برای تمرین نداریم»
        // — true of the table, wrong about why, and with no action to press.
        val repository = repository(
            lesson("a1.one", order = 1) to
                listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
        )
        val status = MutableStateFlow(ContentStatus.FAILED)
        var seeds = 0
        val vm = ConversationPracticeViewModel(
            lessonRepository = repository,
            conversationRepository = FakeConversationRepository(),
            recordTurn = RecordConversationTurnUseCase(progress, knowledge),
            speaker = speaker,
            contentStatus = status,
            seedContent = {
                seeds++
                status.value = ContentStatus.READY
                true
            },
            speechState = speech,
            startRecognition = { starts++ },
            stopRecognition = { stops++ },
        )

        assertFalse("a failed write ends the wait", vm.state.value.isLoading)
        assertTrue("and the screen can name the write", vm.state.value.contentFailed)
        assertFalse("and it is not the empty state", vm.state.value.isEmpty)

        vm.retry()

        assertEquals("the retry runs the bundle write again", 1, seeds)
        assertFalse(vm.state.value.contentFailed)
        assertEquals("then the dialogue really loads", listOf("s1"), vm.state.value.turns.map { it.exerciseId })
    }

    @Test
    fun `a dialogue read that throws offers a retry instead of a spinner`() {
        val repository = repository(
            lesson("a1.one", order = 1) to
                listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
        )
        repository.readError = IllegalStateException("database cannot be opened")
        val vm = viewModel(repository)

        assertFalse("no spinner nobody can leave", vm.state.value.isLoading)
        assertTrue("the screen can say the read failed", vm.state.value.loadFailed)
        assertFalse("and it is not an empty conversation", vm.state.value.isEmpty)

        repository.readError = null
        vm.retry()

        assertFalse(vm.state.value.loadFailed)
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
            conversationRepository = FakeConversationRepository(),
            recordTurn = RecordConversationTurnUseCase(progress, knowledge),
            speaker = slowSpeaker,
            contentStatus = MutableStateFlow(ContentStatus.READY),
            seedContent = { true },
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
    fun `a take that already is the answer may close before the silence window`() {
        val answer = "Go straight and turn left at the traffic light."
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How do I get to the station?", answer),
                ),
            ),
        )
        // Before the learner answers there is nothing the service could end early on.
        assertFalse(SpeechBus.acceptedAnswer("go straight and turn left at the traffic light"))

        vm.onEvent(ConversationEvent.StartTake)

        // The service asks this every buffer: a complete accepted answer closes the
        // take ~1.2 s after the last word instead of holding the mic for four.
        for (full in listOf(answer, "um go straight and turn left at the traffic light okay")) {
            assertTrue("not a complete answer: $full", SpeechBus.acceptedAnswer(full))
        }
        // Half a sentence, or the partner's question, keeps the full window.
        for (partial in listOf("go straight and turn left", "how do i get to the station")) {
            assertFalse("closed the take early on: $partial", SpeechBus.acceptedAnswer(partial))
        }
    }

    @Test
    fun `leaving the screen forgets the turn's answers`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        vm.onEvent(ConversationEvent.StartTake)
        assertTrue(SpeechBus.acceptedAnswer("i am fine thank you"))

        // Process-wide by design, so a stale list would let the next turn end a take
        // on the previous turn's sentence.
        SpeechBus.reset()
        assertFalse(SpeechBus.acceptedAnswer("i am fine thank you"))
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

    // ---- the keyboard path ----------------------------------------------------

    @Test
    fun `a typed answer is graded by the same gate as a spoken one`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue(
                        "a1.one",
                        "s1",
                        "Where is the bank?",
                        "The bank is on the left next to the pharmacy.",
                    ),
                ),
            ),
        )

        vm.onEvent(ConversationEvent.SubmitTyped("The bank is on the left next to the pharmacy."))

        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
        assertEquals(
            "the typed sentence is the transcript, so Next records what was answered",
            "The bank is on the left next to the pharmacy.",
            vm.state.value.finalText,
        )
        assertTrue("the card must say it was written, not said", vm.state.value.answeredByTyping)
        assertFalse("nothing is being listened to", vm.state.value.isListening)

        // The same three verdicts, whichever way the sentence arrives: one
        // substitution in a long sentence is "near", an unrelated one is "retry".
        vm.onEvent(ConversationEvent.SubmitTyped("The bank is on the right next to the pharmacy."))
        assertEquals(SpeakingVerdict.NEAR, vm.state.value.verdict)
        vm.onEvent(ConversationEvent.SubmitTyped("I would like a coffee, please."))
        assertEquals(SpeakingVerdict.RETRY, vm.state.value.verdict)
    }

    @Test
    fun `a typed answer is not overwritten by a late recogniser emission`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                ),
            ),
        )
        // A take was open when the learner switched to the keyboard.
        speech.value = SpeechState(isListening = true, partial = "i am")
        vm.onEvent(ConversationEvent.SubmitTyped("I'm fine, thank you."))

        // The service finishes its take a moment later with whatever it held.
        speech.value = SpeechState(isListening = false, finalText = "i am")

        assertEquals(
            "a stale take must not grade itself over the typed answer",
            SpeakingVerdict.CORRECT,
            vm.state.value.verdict,
        )
        assertEquals("I'm fine, thank you.", vm.state.value.finalText)
    }

    @Test
    fun `typed answers alone finish and record the conversation`() {
        val lessons = repository(
            lesson("a1.one", order = 1) to listOf(
                dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                dialogue("a1.one", "s2", "Do you speak English?", "Yes, a little."),
            ),
        )
        val conversations = FakeConversationRepository()
        val vm = viewModel(lessons, conversations = conversations)

        // No microphone is involved: this is the learner whose recogniser cannot
        // start at all, and the conversation gate must still open.
        listOf("I'm fine, thank you.", "Yes, a little.").forEach { typed ->
            vm.onEvent(ConversationEvent.SubmitTyped(typed))
            vm.onEvent(ConversationEvent.Next)
        }

        assertTrue(vm.state.value.isFinished)
        assertEquals(setOf("a1.one"), conversations.doneFlow.value)
        assertFalse(
            "the next exchange is a fresh answer, not the previous typed one",
            vm.state.value.answeredByTyping,
        )
    }

    @Test
    fun `a blank typed answer grades nothing`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                ),
            ),
        )

        vm.onEvent(ConversationEvent.SubmitTyped("   "))

        assertNull("an empty field is not an answer", vm.state.value.verdict)
        assertFalse(vm.state.value.answeredByTyping)
        assertEquals("", vm.state.value.finalText)
    }

    @Test
    fun `retrying by voice clears a typed verdict and its transcript`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to listOf(
                    dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you."),
                ),
            ),
        )
        vm.onEvent(ConversationEvent.SubmitTyped("the bank is on the left"))
        assertEquals(SpeakingVerdict.RETRY, vm.state.value.verdict)
        starts = 0

        vm.onEvent(ConversationEvent.Retry)

        assertNull("the typed attempt is over", vm.state.value.verdict)
        assertFalse(vm.state.value.answeredByTyping)
        assertEquals("", vm.state.value.finalText)
        assertEquals("and the microphone is what runs now", 1, starts)
    }

    // ---- evidence: which track a turn feeds -------------------------------------

    @Test
    fun `a spoken correct turn moves speaking, never writing`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )
        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(isListening = false, finalText = "i'm fine thank you")

        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
        assertEquals(
            "the verdict is speaking evidence, and only speaking",
            listOf(Skill.SPEAKING to true),
            progress.attempts,
        )
        assertEquals(
            "attributed to the lesson's phonology node in the speaking shape",
            listOf(Skill.SPEAKING to ExerciseFormat.SPEAKING),
            knowledge.recordedSkills,
        )
    }

    @Test
    fun `a typed correct turn moves writing, never speaking`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        vm.onEvent(ConversationEvent.SubmitTyped("I'm fine, thank you."))

        assertEquals(SpeakingVerdict.CORRECT, vm.state.value.verdict)
        assertEquals(
            "a written sentence is writing evidence — the speaking bar must not move",
            listOf(Skill.WRITING to true),
            progress.attempts,
        )
        assertEquals(
            listOf(Skill.WRITING to ExerciseFormat.SENTENCE_PRODUCTION),
            knowledge.recordedSkills,
        )
    }

    @Test
    fun `a retry is not written as a miss`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        vm.onEvent(ConversationEvent.SubmitTyped("I would like a coffee, please."))

        assertEquals(SpeakingVerdict.RETRY, vm.state.value.verdict)
        assertTrue(
            "the app cannot tell a wrong answer from a bad take, so a retry is not a miss",
            progress.attempts.isEmpty(),
        )
        assertTrue(knowledge.recordedSkills.isEmpty())
    }

    @Test
    fun `a take the recogniser never heard is not written as a miss`() {
        val vm = viewModel(
            repository(
                lesson("a1.one", order = 1) to
                    listOf(dialogue("a1.one", "s1", "How are you?", "I'm fine, thank you.")),
            ),
        )

        speech.value = SpeechState(isListening = true)
        speech.value = SpeechState(isListening = false, finalText = "")

        assertTrue(vm.state.value.unheard)
        assertTrue(
            "a take with no words is not evidence about the learner",
            progress.attempts.isEmpty(),
        )
        assertTrue(knowledge.recordedSkills.isEmpty())
    }

    /** `onCleared` is protected; a subclass is how a test reaches it. */
    private fun clearCallsOf(vm: ConversationPracticeViewModel) {
        val method = androidx.lifecycle.ViewModel::class.java
            .getDeclaredMethod("onCleared")
        method.isAccessible = true
        method.invoke(vm)
    }
}
