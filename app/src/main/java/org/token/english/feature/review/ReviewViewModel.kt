package org.token.english.feature.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.audio.AudioPlayer
import org.token.english.core.audio.Speaker
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.engine.StudyTimePolicy
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.NodeExerciseCard
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.VocabularyRepository
import org.token.english.domain.usecase.DueKnowledgeReview
import org.token.english.domain.usecase.ExerciseOutcome
import org.token.english.domain.usecase.GetDueKnowledgeReviewsUseCase
import org.token.english.domain.usecase.GetDueNodeCardsUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase

enum class ReviewPhase { SUMMARY, SESSION, DONE }

data class ReviewUiState(
    val isLoading: Boolean = true,
    val phase: ReviewPhase = ReviewPhase.SUMMARY,
    /** Word cards, in due order — the queue `review_item` owns. */
    val queue: List<ReviewItem> = emptyList(),
    val vocabulary: Map<String, VocabularyItem> = emptyMap(),
    /** Position inside [queue]. Equals `queue.size` once the word cards are done. */
    val index: Int = 0,
    /**
     * The graph's own questions (graph-card pass): one exercise per due curriculum
     * node that can be asked with a tap or a keyboard. Asked **after** the word cards,
     * so a session keeps one shape and the cards the learner came for come first.
     */
    val nodeCards: List<NodeExerciseCard> = emptyList(),
    /** Position inside [nodeCards]. */
    val nodeIndex: Int = 0,
    /** The option the learner tapped on a node card (null until tapped). */
    val nodeSelectedOption: Int? = null,
    /** The node card's verdict, once submitted — null means not answered yet. */
    val nodeOutcome: ExerciseOutcome? = null,
    val revealed: Boolean = false,
    val gradedCount: Int = 0,
    val againCount: Int = 0,
    val isPlaying: Boolean = false,
    /** A listening node card's audio is playing (listening-card pass). */
    val isPlayingNodeAudio: Boolean = false,
    /**
     * Whether this device can speak English — `null` while the engine connects, `false`
     * when there is no voice. A listening card then shows its transcript instead of a
     * play button that cannot speak, exactly as the lesson's step does.
     */
    val nodeAudioAvailable: Boolean? = null,
    /** Learner's typed answer in production mode (empty until typed). */
    val typedAnswer: String = "",
    /** Result of a checked production answer: null = not a production card / not checked yet. */
    val answerCorrect: Boolean? = null,
    /** One edit away from the word (A-2): graded wrong, but shown as «تقریباً درست». */
    val answerAlmost: Boolean = false,
    /** The due-queue read threw — shown as an error state instead of a spinner. */
    val loadFailed: Boolean = false,
    /**
     * Curriculum nodes whose **own** review is due, with the drill that serves each one
     * (review→graph pass). Empty is normal: a fresh install, or a graph still inside its
     * intervals. A failed read of this *half* never blanks the cards — see [refresh].
     */
    val dueReviews: List<DueKnowledgeReview> = emptyList(),
) {
    val currentItem: ReviewItem? get() = queue.getOrNull(index)
    val currentWord: VocabularyItem? get() = currentItem?.let { vocabulary[it.contentId] }
    val currentNodeCard: NodeExerciseCard?
        get() = if (index >= queue.size) nodeCards.getOrNull(nodeIndex) else null
    val total: Int get() = queue.size + nodeCards.size

    /** How many questions are behind the learner — both halves count. */
    val answered: Int get() = index.coerceAtMost(queue.size) + nodeIndex

    /**
     * Active production: odd positions ask the learner to WRITE the English
     * word from its Persian meaning (typed, checked) instead of only recalling
     * the meaning — typing/listening/writing generation for the SRS (checklist P5).
     */
    val produceMode: Boolean get() = index % 2 == 1

    /**
     * Whether the session's current question is the node card's — the word cards are
     * always asked first, so a card surface and the words never interleave.
     */
    val askingNode: Boolean get() = currentNodeCard != null

    /** The node card's question already has an answer to submit? */
    val canSubmitNode: Boolean
        get() {
            val card = currentNodeCard ?: return false
            return when (val exercise = card.exercise) {
                is Exercise.MultipleChoice -> nodeSelectedOption != null
                else -> typedAnswer.isNotBlank()
            }
        }
}

sealed interface ReviewEvent {
    data object Start : ReviewEvent
    data object Reveal : ReviewEvent
    data class Grade(val result: ReviewResult) : ReviewEvent
    data object PlayWord : ReviewEvent
    data object Reset : ReviewEvent
    data class AnswerChanged(val value: String) : ReviewEvent
    data object CheckAnswer : ReviewEvent
    /** Play a listening node card's audio — the card's question is the sound. */
    data object PlayNodeAudio : ReviewEvent
    /** A tap on one of a node card's options. */
    data class SelectNodeOption(val index: Int) : ReviewEvent
    /** Submit the node card's answer — it is graded, not self-assessed. */
    data object SubmitNode : ReviewEvent
    /** Leave an answered node card and take the next question. */
    data object NextNode : ReviewEvent
    /** From the finished-session screen back to the summary, where due nodes wait. */
    data object ShowDue : ReviewEvent
}

/**
 * Review session: due queue → reveal → grade (SRS) → next, then the graph's own
 * questions for the nodes whose schedule is due.
 * Time is epoch millis; response time feeds the attempt log.
 */
/**
 * Review session state (checklist B-6): the review/vocabulary/progress
 * repositories, the SRS use case and the audio seam — not the whole container.
 */
class ReviewViewModel(
    private val reviewRepository: ReviewRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val progressRepository: ProgressRepository,
    private val submitReview: SubmitReviewUseCase,
    private val getDueKnowledgeReviews: GetDueKnowledgeReviewsUseCase,
    /** The graph half's cards (graph-card pass): a due node's own exercise. */
    private val getDueNodeCards: GetDueNodeCardsUseCase,
    /**
     * One answer's evidence — the same use case the lesson answers through, so a
     * node card's format bit and mastery are written by one rule, not a second copy.
     */
    private val submitExercise: SubmitExerciseUseCase,
    /** The lesson's TTS seam: word audio on the summary, and a listening card's line. */
    private val audioPlayer: AudioPlayer,
    private val speaker: Speaker,
    private val isAppInForeground: () -> Boolean,
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    private var itemStartedAtMs: Long = System.currentTimeMillis()

    /** Blocks a second grade() while the first SRS write is still in flight. */
    private var grading: Boolean = false

    /** contentId → times already re-shown after AGAIN in this session. */
    private val requeueCount = mutableMapOf<String, Int>()

    /** Last time the learner acted here — study time stops crediting when this goes stale (P7). */
    private var lastInteractionAtMs: Long = System.currentTimeMillis()

    /** True once the first read has landed — a resume before that would double-read. */
    private var hasLoaded: Boolean = false

    init {
        refresh()
        // The engine may still be connecting when the first listening card appears, so the
        // availability is mirrored here rather than read once: a card must never offer a
        // play button on a device that has no English voice.
        viewModelScope.launch {
            audioPlayer.isEnglishAvailable.collect { available ->
                _state.update { it.copy(nodeAudioAvailable = available) }
            }
        }
    }

    /**
     * The screen came back to the foreground (returning from a focused drill, or from
     * another tab). Both queues are local reads that only ever ran in `init`, so a
     * session finished elsewhere — the drill just completed, a card graded — used to
     * leave the screen showing work that was already done.
     *
     * Deliberately narrow: only the summary re-reads, never a session in progress, and
     * never with a spinner — the current content stays until the fresh read lands.
     */
    fun onScreenResumed() {
        if (!hasLoaded) return
        val current = _state.value
        if (current.phase != ReviewPhase.SUMMARY || current.isLoading || current.loadFailed) return
        load(resumeOnly = true)
    }

    fun refresh() {
        load(resumeOnly = false)
    }

    private fun load(resumeOnly: Boolean) {
        lastInteractionAtMs = System.currentTimeMillis()
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            // Guarded: the queue and its words come from Room, and a throw used to
            // leave the screen on its spinner forever.
            val (due, vocab) = runCatchingCancellable {
                val queue = reviewRepository.getDue(now, limit = 100)
                val words = queue.mapNotNull { item ->
                    vocabularyRepository.get(item.contentId)?.let { item.contentId to it }
                }.toMap()
                queue to words
            }.getOrElse { error ->
                android.util.Log.w("ReviewViewModel", "review queue read failed", error)
                _state.update { it.copy(isLoading = false, loadFailed = true) }
                return@launch
            }
            // The graph half is read separately and its failure is *contained*: a broken
            // node read must not turn a working card queue into an error screen, so it
            // degrades to "nothing due in the graph" and only logs.
            val dueNodes = runCatchingCancellable { getDueKnowledgeReviews(now) }.getOrElse { error ->
                android.util.Log.w("ReviewViewModel", "due node read failed", error)
                emptyList()
            }
            // The same containment for the graph's cards: a node whose exercise cannot be
            // read is one question fewer, never an error screen over the word cards.
            val nodeCards = runCatchingCancellable { getDueNodeCards(now) }.getOrElse { error ->
                android.util.Log.w("ReviewViewModel", "due node card read failed", error)
                emptyList()
            }
            hasLoaded = true
            _state.update { current ->
                // A resume read that finishes after the learner already started a session
                // must not yank them back to card 0; an explicit refresh always applies.
                if (resumeOnly && current.phase != ReviewPhase.SUMMARY) return@update current
                current.copy(
                    isLoading = false,
                    loadFailed = false,
                    queue = due,
                    vocabulary = vocab,
                    dueReviews = dueNodes,
                    nodeCards = nodeCards,
                    index = 0,
                    nodeIndex = 0,
                    nodeSelectedOption = null,
                    nodeOutcome = null,
                    revealed = false,
                    typedAnswer = "",
                    answerCorrect = null,
                    answerAlmost = false,
                    isPlayingNodeAudio = false,
                    phase = if (due.isEmpty() && nodeCards.isEmpty()) {
                        ReviewPhase.SUMMARY
                    } else {
                        current.phase.takeIf { p -> p != ReviewPhase.DONE } ?: ReviewPhase.SUMMARY
                    },
                )
            }
            itemStartedAtMs = now
        }
    }

    /** The error state's action: read the due queue again (it is local data). */
    fun retry() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        refresh()
    }

    /**
     * One study-time sample from the screen's lifecycle ticker (P7): a tick is
     * credited only while the app is in the foreground AND the learner has acted
     * within [StudyTimePolicy.IDLE_CAP_MILLIS]. Merely sitting on an open screen
     * stops filling the daily goal; the screen drives the ticking, so a tab on
     * the back stack ticks at all only while it is the resumed screen.
     */
    fun onStudyTick(nowMs: Long = System.currentTimeMillis()) {
        if (!isAppInForeground()) return
        if (!StudyTimePolicy.credits(lastInteractionAtMs, nowMs)) return
        viewModelScope.launch {
            progressRepository.addStudySeconds(StudyTimePolicy.TICK_SECONDS)
        }
    }

    fun onEvent(event: ReviewEvent) {
        lastInteractionAtMs = System.currentTimeMillis()
        when (event) {
            ReviewEvent.Start -> _state.update {
                it.copy(
                    phase = ReviewPhase.SESSION,
                    index = 0,
                    nodeIndex = 0,
                    nodeSelectedOption = null,
                    nodeOutcome = null,
                    revealed = false,
                    gradedCount = 0,
                    againCount = 0,
                    typedAnswer = "",
                    answerCorrect = null,
                    answerAlmost = false,
                )
            }

            ReviewEvent.Reveal -> _state.update { if (it.revealed) it else it.copy(revealed = true) }

            is ReviewEvent.Grade -> grade(event.result)

            ReviewEvent.PlayWord -> {
                val word = _state.value.currentWord ?: return
                speaker.speak(text = word.word, onDone = { _state.update { it.copy(isPlaying = false) } })
            }

            ReviewEvent.PlayNodeAudio -> playNodeAudio()

            ReviewEvent.Reset -> refresh()

            ReviewEvent.ShowDue -> _state.update { it.copy(phase = ReviewPhase.SUMMARY) }

            is ReviewEvent.AnswerChanged -> _state.update {
                when {
                    it.askingNode && it.nodeOutcome != null -> it
                    it.askingNode -> it.copy(typedAnswer = event.value)
                    it.revealed -> it
                    else -> it.copy(typedAnswer = event.value)
                }
            }

            is ReviewEvent.SelectNodeOption -> _state.update {
                if (it.nodeOutcome != null) it else it.copy(nodeSelectedOption = event.index)
            }

            ReviewEvent.SubmitNode -> submitNodeCard()

            ReviewEvent.NextNode -> nextNodeCard()

            ReviewEvent.CheckAnswer -> checkAnswer()
        }
    }

    /** Checks the typed production answer, then reveals the card for grading. */
    private fun checkAnswer() {
        val s = _state.value
        val word = s.currentWord ?: return
        if (s.revealed || s.typedAnswer.isBlank()) return
        // Same grading rules as lessons (A-2): digits ≡ words, one-edit near-miss
        // graded wrong but reported as «تقریباً درست» — never auto-accepted.
        val verdict = AnswerChecker.grade(listOf(word.word), s.typedAnswer)
        _state.update {
            it.copy(
                revealed = true,
                answerCorrect = verdict == AnswerChecker.Verdict.CORRECT,
                answerAlmost = verdict == AnswerChecker.Verdict.ALMOST,
            )
        }
    }

    /**
     * Grades a node card's answer and writes its evidence.
     *
     * The verdict is formed first and shown at once, because it is pure and the write
     * can fail: an accurate grade with no evidence is honest, a tap that does nothing
     * is not (the rule the lesson's answer path already follows). The write is the
     * lesson's own ([SubmitExerciseUseCase]), so the node's format mask, the node
     * itself and the skill meter move exactly as they would for the same answer inside
     * the lesson — a card is recall and may never set a format bit, an exercise is
     * transfer and does.
     */
    private fun submitNodeCard() {
        val s = _state.value
        val card = s.currentNodeCard ?: return
        if (s.nodeOutcome != null || grading || !s.canSubmitNode) return
        val exercise = card.exercise
        val answer = when (exercise) {
            is Exercise.MultipleChoice -> exercise.options.getOrNull(s.nodeSelectedOption ?: -1).orEmpty()
            else -> s.typedAnswer
        }
        if (answer.isBlank()) return
        val outcome = submitExercise.grade(exercise, answer)
        val now = System.currentTimeMillis()
        grading = true
        _state.update { it.copy(nodeOutcome = outcome) }
        viewModelScope.launch {
            try {
                runCatchingCancellable {
                    submitExercise(exercise, outcome, now, source = "review-node-card")
                }.onFailure { error ->
                    // The verdict stands; only its evidence is missing, and the next
                    // attempt with the same item can still claim the day.
                    android.util.Log.w("ReviewViewModel", "node card write failed", error)
                }
                itemStartedAtMs = System.currentTimeMillis()
            } finally {
                grading = false
            }
        }
    }

    /**
     * Plays a listening card's audio through the player the lesson's own step uses.
     *
     * The audio **is** the question ([Exercise.Listening]), so a card that offered no way
     * to hear the line would be a dead end. A second tap while it is speaking is ignored
     * (it would restart the line mid-sentence), and nothing plays when the device has no
     * English voice — the card shows its transcript instead.
     */
    private fun playNodeAudio() {
        val s = _state.value
        val listening = s.currentNodeCard?.exercise as? Exercise.Listening ?: return
        if (s.isPlayingNodeAudio || s.nodeAudioAvailable == false) return
        _state.update { it.copy(isPlayingNodeAudio = true) }
        val done = { _state.update { it.copy(isPlayingNodeAudio = false) } }
        audioPlayer.speak(text = listening.audioText, onDone = done, onError = done)
    }

    /** Leaves an answered node card; the last one ends the session. */
    private fun nextNodeCard() {
        val s = _state.value
        if (s.nodeOutcome == null) return
        val next = s.nodeIndex + 1
        // The next card must not inherit this one's voice: a listening line still playing
        // while the learner reads the next question is noise, not review.
        if (s.isPlayingNodeAudio) audioPlayer.stop()
        _state.update {
            it.copy(
                nodeIndex = next,
                nodeSelectedOption = null,
                nodeOutcome = null,
                typedAnswer = "",
                isPlayingNodeAudio = false,
                gradedCount = it.gradedCount + 1,
                phase = if (next >= it.nodeCards.size) ReviewPhase.DONE else ReviewPhase.SESSION,
            )
        }
        itemStartedAtMs = System.currentTimeMillis()
    }

    private fun grade(result: ReviewResult) {
        val s = _state.value
        if (grading) return
        val item = s.currentItem ?: return
        val revealed = s.revealed
        if (!revealed) return
        val now = System.currentTimeMillis()
        grading = true
        viewModelScope.launch {
            try {
                submitReview(
                    item = item,
                    result = result,
                    now = now,
                    responseTimeMs = now - itemStartedAtMs,
                    // The card's own word carries the lesson that taught it, which is how
                    // the grade reaches a curriculum node — a card from a lesson the
                    // bundle no longer has simply has none, and the graph stays as it was.
                    lessonId = s.currentWord?.lessonId,
                )
                // Relearning: an AGAIN card comes back later in THIS session with
                // its fresh schedule already stored — capped so the queue ends.
                var queue = s.queue
                if (result == ReviewResult.AGAIN && (requeueCount[item.contentId] ?: 0) < MAX_IN_SESSION_REQUEUES) {
                    requeueCount[item.contentId] = (requeueCount[item.contentId] ?: 0) + 1
                    reviewRepository.getItem(item.contentId)?.let { fresh -> queue = queue + fresh }
                }
                val next = s.index + 1
                _state.update {
                    it.copy(
                        queue = queue,
                        index = next,
                        revealed = false,
                        typedAnswer = "",
                        answerCorrect = null,
                        answerAlmost = false,
                        gradedCount = it.gradedCount + 1,
                        againCount = if (result == ReviewResult.AGAIN) it.againCount + 1 else it.againCount,
                        // The word cards are followed by the graph's own questions, so
                        // finishing the queue is the session's end only when no node
                        // card is waiting (graph-card pass).
                        phase = if (next >= queue.size && it.nodeCards.isEmpty()) {
                            ReviewPhase.DONE
                        } else {
                            ReviewPhase.SESSION
                        },
                    )
                }
                itemStartedAtMs = System.currentTimeMillis()
            } finally {
                grading = false
            }
        }
    }

    override fun onCleared() {
        audioPlayer.stop()
    }

    private companion object {
        /** Max times one card is re-shown per session after AGAIN. */
        const val MAX_IN_SESSION_REQUEUES = 2
    }
}
