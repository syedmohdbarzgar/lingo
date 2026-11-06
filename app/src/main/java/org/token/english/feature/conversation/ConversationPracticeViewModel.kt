package org.token.english.feature.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.common.persianCountPair
import org.token.english.core.audio.Speaker
import org.token.english.domain.engine.ConversationScorer
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.engine.owedConversation
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.repository.ConversationRepository
import org.token.english.domain.repository.LessonRepository

/**
 * One exchange of the practice conversation: the partner (the device) says
 * [partnerLine], the learner replies with [answer].
 *
 * Both lines come from the lesson's own `speaking` item — the partner's line is
 * authored next to the answer in the bundle, so a conversation always practises
 * the structure its lesson taught (book a table, ask directions, hedge a promise).
 */
data class DialogueTurn(
    val exerciseId: String,
    val lessonId: String,
    /** Persian lesson title: a turn is never a loose sentence, it belongs to a lesson. */
    val lessonTitleFa: String,
    /** The lesson's CEFR level — an A1 turn gets longer to answer (checklist 1.5). */
    val level: LearningLevel,
    /**
     * The partner's English line, spoken by the device. Null on a plain
     * repeat-the-model item, where the thing to say *is* the model.
     */
    val partnerLine: String?,
    /** Persian gloss of [partnerLine], so the learner never has to guess what was asked. */
    val partnerLineFa: String?,
    /** Persian description of what the learner should reply. */
    val taskFa: String,
    /**
     * Every English reply this turn accepts, the model one first. The bundle
     * authors equivalents ("yes please" / "a table for two, please") and the take
     * is graded against all of them — grading only the first failed a learner who
     * said a sentence the content itself lists.
     */
    val answers: List<String>,
    /** Persian note on the structure (the exercise's own explanation), shown after a miss. */
    val tipFa: String?,
) {
    /** The authored reply, shown to the learner as the sentence to say. */
    val answer: String get() = answers.first()
}

/** A finished exchange, kept on screen as the conversation's transcript. */
data class AnsweredTurn(
    val turn: DialogueTurn,
    val said: String,
    val verdict: SpeakingVerdict,
)

data class ConversationUiState(
    val isLoading: Boolean = true,
    val turns: List<DialogueTurn> = emptyList(),
    /** The lesson whose dialogue this is — the whole practice belongs to it. */
    val lessonId: String? = null,
    val lessonTitleFa: String = "",
    /** Where the exchange happens ("در فرودگاه — باجهٔ تحویل بار"). */
    val sceneFa: String? = null,
    /** Who the learner is talking to ("مأمور فرودگاه"). */
    val partnerFa: String? = null,
    val index: Int = 0,
    /** Exchanges already answered, oldest first — the conversation so far. */
    val history: List<AnsweredTurn> = emptyList(),
    val isListening: Boolean = false,
    /** Model being copied/loaded on the first take. */
    val isPreparing: Boolean = false,
    /**
     * The partner's line is being spoken. The mic is unavailable while this is
     * true: a recogniser that hears the device's own voice transcribes the
     * partner's question as the learner's answer.
     */
    val isPartnerSpeaking: Boolean = false,
    /** Live text while the learner speaks. */
    val partial: String = "",
    /**
     * Everything the recogniser has settled in this take, across pauses. The first
     * version replaced this at every utterance boundary, which wiped the sentence
     * whenever the learner breathed — see SpeechRecognitionService.
     */
    val finalText: String = "",
    /** Set once the take is graded; cleared when a new take starts. */
    val verdict: SpeakingVerdict? = null,
    /**
     * The take ended without a single recognised word. A recogniser/volume
     * problem, deliberately not a [SpeakingVerdict]: a learner must never be shown
     * "wrong answer" for a take the app never heard.
     */
    val unheard: Boolean = false,
    val error: SpeechError = SpeechError.NONE,
    /** This turn's answer came from the keyboard, so the transcript is not "what you said". */
    val answeredByTyping: Boolean = false,
) {
    val current: DialogueTurn? get() = turns.getOrNull(index)

    /** No speaking item exists in the bundle — an empty state, not an error. */
    val isEmpty: Boolean get() = !isLoading && turns.isEmpty()

    val isFinished: Boolean get() = !isLoading && turns.isNotEmpty() && index >= turns.size

    val progressLabel: String get() =
        if (turns.isEmpty()) "" else persianCountPair((index + 1).coerceAtMost(turns.size), turns.size)

    /** Fraction of the conversation already answered — drives the header bar. */
    val progress: Float
        get() = if (turns.isEmpty()) 0f else (index.toFloat() / turns.size).coerceIn(0f, 1f)

    /** Exchanges answered so far, whatever the verdict. */
    val answeredCount: Int get() = history.size

    val correctCount: Int get() = history.count { it.verdict == SpeakingVerdict.CORRECT }

    val nearCount: Int get() = history.count { it.verdict == SpeakingVerdict.NEAR }

    val retryCount: Int get() = history.count { it.verdict == SpeakingVerdict.RETRY }

    /**
     * Share of the finished conversation that was answered correctly, 0..100.
     * Only completed exchanges count: a turn the learner moved past without
     * speaking (or a take the app never heard) is not a miss and not a hit.
     */
    val accuracyPercent: Int
        get() = if (answeredCount == 0) 0 else (correctCount * 100) / answeredCount

    /**
     * The learner's words right now: settled words plus the phrase still in flight,
     * so a pause shows the sentence continuing instead of starting over.
     */
    val liveTranscript: String
        get() = listOf(finalText, partial)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    /** A take has produced something worth showing. */
    val hasTake: Boolean get() = finalText.isNotBlank() || (isListening && liveTranscript.isNotBlank())
}

/**
 * Conversation practice as a two-party exchange: the device asks, the learner
 * answers, and the next turn follows.
 *
 * The screen owns the microphone *permission* (a Compose launcher is the only
 * sane place to ask); this class owns everything after that. The recogniser runs
 * in [SpeechRecognitionService], so its state arrives through [SpeechBus] — the
 * ViewModel never touches Vosk, AudioRecord or a Context, and a unit test can
 * drive a whole conversation with a fake flow and a recording [Speaker].
 *
 * Grading is a single gate: the take has to be **over**. Grading on every
 * recogniser update would show "تلاش دوباره" for the first half of a sentence the
 * learner is still saying.
 */
class ConversationPracticeViewModel(
    private val lessonRepository: LessonRepository,
    /**
     * Which lesson's dialogue to run. Null means "the one the learner owes right
     * now": the practice is always a lesson's conversation, never a loose drill
     * drawn from the whole bundle.
     */
    private val lessonId: String? = null,
    /** Records the finished dialogue, which is what opens the next lesson. */
    private val conversationRepository: ConversationRepository,
    private val speaker: Speaker,
    /** Flipped once the JSON bundle has reached Room; turns read after it. */
    private val contentSeeded: StateFlow<Boolean>,
    speechState: StateFlow<SpeechState>,
    private val startRecognition: () -> Unit,
    private val stopRecognition: () -> Unit,
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationUiState())
    val state: StateFlow<ConversationUiState> = _state.asStateFlow()

    /** Last value of the recogniser's listening flag, to detect the end of a take. */
    private var wasListening = false

    /**
     * True while this turn's answer came from the keyboard. The mic is stopped when
     * that happens, but a late emission from the service must neither overwrite the
     * typed transcript nor grade itself over it.
     */
    private var typedThisTurn = false

    /** The finish write is once per session, not once per finished screen. */
    private var markedConversationDone = false

    init {
        loadTurns()
        viewModelScope.launch {
            speechState.collect { onSpeech(it) }
        }
    }

    /**
     * Mirrors the recogniser into the screen state, and grades exactly once per
     * take — when it ends.
     */
    private fun onSpeech(s: SpeechState) {
        // A typed answer owns this turn: the recogniser's bookkeeping is stale from
        // the moment the learner chose the keyboard, so it may not touch the state.
        if (typedThisTurn) {
            wasListening = false
            return
        }

        val takeEnded = wasListening && !s.isListening
        wasListening = s.isListening

        _state.update {
            it.copy(
                isListening = s.isListening,
                isPreparing = s.isPreparing,
                partial = s.partial,
                finalText = s.finalText,
                error = s.error,
            )
        }
        if (takeEnded) finishTake(s)
    }

    /** Grades the take that just ended. Never grades a failed or empty one. */
    private fun finishTake(s: SpeechState) {
        val turn = _state.value.current ?: return
        // A take that broke off (recorder died, permission revoked, model missing)
        // is an app failure: it must not be scored as a language mistake.
        if (s.error != SpeechError.NONE) return

        val said = s.finalText.trim()
        if (said.isEmpty()) {
            _state.update { it.copy(unheard = true, verdict = null) }
            return
        }
        _state.update {
            it.copy(
                verdict = ConversationScorer.assessBest(said, turn.answers, turn.partnerLine),
                unheard = false,
            )
        }
    }

    /**
     * Loads ONE lesson's dialogue — the turn list of the lesson the learner is on,
     * never a pool of turns from every lesson in the bundle. A conversation is the
     * last step of a lesson, so it has that lesson's topic, scene and roles.
     */
    private fun loadTurns() {
        viewModelScope.launch {
            // The seeder runs in the background at app start. Reading the table
            // before it lands returns an empty lesson list, which would show the
            // empty state for a bundle that has turns — and nothing re-emits,
            // because the read below takes a single snapshot.
            contentSeeded.first { it }
            // `first()` takes one emission and closes the flow — a bare `collect`
            // would keep the lesson observation alive for the screen's lifetime.
            val lessons = lessonRepository.observeLessons().first().sortedBy { it.order }
            val completed = lessonRepository.observeLessonStates().first()
                .filter { it.completed }
                .map { it.lessonId }
                .toSet()
            val conversationsDone = conversationRepository.observeCompleted().first()

            // Titled lessons with a dialogue: the ones a conversation can belong to.
            val withDialogue = lessons.filter { lesson ->
                lessonRepository.getExercises(lesson.id).any {
                    it is Exercise.Speaking && it.referenceAnswers.isNotEmpty()
                }
            }
            val dialogueIds = withDialogue.map { it.id }.toSet()
            val target = resolveTarget(
                lessons = withDialogue,
                completedIds = completed,
                dialogueLessonIds = dialogueIds,
                conversationsDone = conversationsDone,
            )

            val speaking = target?.let { lesson ->
                lessonRepository.getExercises(lesson.id)
                    .filterIsInstance<Exercise.Speaking>()
                    .sortedBy { it.id }
            }.orEmpty()

            val turns = target?.let { lesson ->
                speaking.mapNotNull { ex ->
                        val answers = ex.referenceAnswers
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .distinct()
                        if (answers.isEmpty()) return@mapNotNull null
                        DialogueTurn(
                            exerciseId = ex.id,
                            lessonId = lesson.id,
                            lessonTitleFa = lesson.titleFa,
                            level = lesson.level,
                            partnerLine = ex.question?.takeIf { it.isNotBlank() },
                            partnerLineFa = ex.questionFa?.takeIf { it.isNotBlank() },
                            taskFa = ex.prompt,
                            answers = answers,
                            tipFa = ex.explanation?.takeIf { it.isNotBlank() },
                        )
                    }
            }.orEmpty()

            // The scene and the partner's role are authored on the turns; showing
            // them once above the exchange is what makes it a situation rather
            // than a list of prompts.
            val firstSpeaking = speaking.firstOrNull()

            _state.update {
                it.copy(
                    isLoading = false,
                    turns = turns,
                    lessonId = target?.id,
                    lessonTitleFa = target?.titleFa.orEmpty(),
                    sceneFa = firstSpeaking?.sceneFa,
                    partnerFa = firstSpeaking?.partnerFa,
                )
            }
            // The partner opens the conversation: a dialogue that starts silent
            // gives the learner nothing to answer.
            speakPartner()
        }
    }

    /**
     * The lesson whose dialogue to practise: the requested one, else the one the
     * path is currently blocked on, else the first lesson that still has an
     * unfinished dialogue.
     */
    private fun resolveTarget(
        lessons: List<Lesson>,
        completedIds: Set<String>,
        dialogueLessonIds: Set<String>,
        conversationsDone: Set<String>,
    ): Lesson? {
        lessonId?.let { wanted ->
            lessons.firstOrNull { it.id == wanted }?.let { return it }
        }
        owedConversation(lessons, completedIds, dialogueLessonIds, conversationsDone)
            ?.let { return it }
        return lessons.firstOrNull { it.id !in conversationsDone }
    }

    /**
     * Speaks the current partner line (or the model, on a repeat-only turn) and
     * keeps the mic shut until the line has finished.
     *
     * This is the fix for "I have to say it several times": the answer button used
     * to be live while the device was still speaking, so the recogniser picked up
     * the partner's own question through the speaker and graded *that* against the
     * expected reply. A correct answer then came back as a miss, and repeating it
     * only made the two voices overlap again.
     */
    private fun speakPartner() {
        val turn = _state.value.current ?: return
        val line = turn.partnerLine ?: turn.answer
        _state.update { it.copy(isPartnerSpeaking = true) }
        speaker.speak(
            line,
            onDone = { _state.update { it.copy(isPartnerSpeaking = false) } },
        )
    }

    /**
     * Grades an answer the learner **typed** instead of spoke.
     *
     * The keyboard is always there, not a rescue mode: the recogniser needs a
     * downloaded model and a working microphone, and a learner without either was
     * locked out of the lesson's conversation — which is what opens the next
     * lesson. The text goes through the same [ConversationScorer.assessBest] gate as
     * a take, so «درست / نزدیک / تلاش دوباره» means the same thing whichever way the
     * sentence arrived, and [advance] records it exactly like a spoken turn.
     */
    private fun submitTyped(text: String) {
        val turn = _state.value.current ?: return
        val said = text.trim()
        if (said.isEmpty()) return

        // An open take would end later and grade itself over this answer.
        stopRecognition()
        wasListening = false
        typedThisTurn = true

        _state.update {
            it.copy(
                finalText = said,
                partial = "",
                answeredByTyping = true,
                verdict = ConversationScorer.assessBest(said, turn.answers, turn.partnerLine),
                unheard = false,
                error = SpeechError.NONE,
                isListening = false,
                isPreparing = false,
            )
        }
    }

    fun onEvent(event: ConversationEvent) {
        when (event) {
            is ConversationEvent.SubmitTyped -> submitTyped(event.text)
            ConversationEvent.StartTake -> startTake()
            ConversationEvent.StopTake -> stopRecognition()
            // Retry is a NEW take, not a stop followed by a start: the stop/start
            // race tears the foreground service down before startForeground(),
            // which is what left the second take unable to stop.
            ConversationEvent.Retry -> startTake()
            ConversationEvent.Next -> advance()
            ConversationEvent.SpeakPartner -> speakPartner()
            ConversationEvent.Restart -> restart()
        }
    }

    /**
     * Starts the same conversation again from the first exchange, keeping the
     * turns themselves (they come from content and never change mid-session) and
     * clearing the transcript and the score. This is the summary screen's action:
     * finishing is not the end of the practice, it is one pass of it.
     */
    private fun restart() {
        clearTake()
        _state.update { it.copy(index = 0, history = emptyList()) }
        speakPartner()
    }

    private fun startTake() {
        if (_state.value.current == null) return
        // Recording over the partner's own line would capture the device, not the
        // learner — the take would be graded against the question it just heard.
        if (_state.value.isPartnerSpeaking) return
        // Constrain the recogniser to this turn's own sentences before the service
        // opens the microphone; an open vocabulary is what misheard accented
        // English and made a correct answer look like a miss. The same answers also
        // let the service end the take as soon as the sentence is complete.
        _state.value.current?.let {
            SpeechBus.grammar = ConversationScorer.speechGrammar(it.answers)
            SpeechBus.acceptedAnswers = it.answers
            // A1 gets the long pause (checklist 1.5): the learner is assembling the
            // sentence, not finished with it.
            SpeechBus.hesitant = it.level == LearningLevel.A1
        }
        // Deliberately NOT `clearTake()`: that stops the recogniser, and a stop
        // racing the start is what tore the foreground service down before it
        // could call startForeground() (process crash, Android 12). A new take
        // only needs the UI state reset; the previous take is already over.
        resetTake()
        startRecognition()
    }

    /** Closes the current exchange and moves to the next one. */
    private fun advance() {
        val st = _state.value
        val turn = st.current ?: return
        val verdict = st.verdict
        val answered = if (verdict == null) {
            emptyList()
        } else {
            listOf(AnsweredTurn(turn, st.finalText.trim(), verdict))
        }

        clearTake()
        _state.update { it.copy(index = it.index + 1, history = it.history + answered) }
        markFinishedConversation()
        speakPartner()
    }

    /**
     * Closes the lesson's dialogue once every turn has been answered.
     *
     * "Answered" is a graded take, not a skipped one: a turn the learner walked
     * past without speaking is exactly the turn that taught nothing, so the
     * conversation — and with it the next lesson — stays closed until the whole
     * exchange has actually been spoken.
     */
    private fun markFinishedConversation() {
        val st = _state.value
        val id = st.lessonId ?: return
        if (st.turns.isEmpty() || st.history.size < st.turns.size) return
        if (markedConversationDone) return
        markedConversationDone = true
        viewModelScope.launch { conversationRepository.markCompleted(id) }
    }

    /** A take that is not on screen must not stay on screen as if it happened. */
    private fun clearTake() {
        stopRecognition()
        SpeechBus.reset()
        // SpeechBus.reset() emits a blank state; wasListening must follow it so the
        // blank emission is not mistaken for the end of a take.
        wasListening = false
        resetTake()
    }

    private fun resetTake() {
        typedThisTurn = false
        _state.update {
            it.copy(
                partial = "",
                finalText = "",
                answeredByTyping = false,
                verdict = null,
                unheard = false,
                error = SpeechError.NONE,
            )
        }
    }

    override fun onCleared() {
        // Leaving the feature ends the practice: never keep a mic service alive
        // behind a screen the learner already left, and never leave the device
        // talking to an empty room.
        speaker.stop()
        stopRecognition()
        SpeechBus.reset()
        super.onCleared()
    }
}

/** What the screen can ask for. Mirrors the Event convention of the other features. */
sealed interface ConversationEvent {
    data object StartTake : ConversationEvent
    data object StopTake : ConversationEvent
    data object Retry : ConversationEvent

    /** Close this exchange and move to the next one. */
    data object Next : ConversationEvent

    /** Replay the partner's line. */
    data object SpeakPartner : ConversationEvent

    /** Run the conversation again from the first exchange (the summary's action). */
    data object Restart : ConversationEvent

    /** The keyboard path: an answer typed instead of spoken. */
    data class SubmitTyped(val text: String) : ConversationEvent
}
