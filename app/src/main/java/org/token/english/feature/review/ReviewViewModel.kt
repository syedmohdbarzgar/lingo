package org.token.english.feature.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.di.AppContainer
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.VocabularyItem

enum class ReviewPhase { SUMMARY, SESSION, DONE }

data class ReviewUiState(
    val isLoading: Boolean = true,
    val phase: ReviewPhase = ReviewPhase.SUMMARY,
    val queue: List<ReviewItem> = emptyList(),
    val vocabulary: Map<String, VocabularyItem> = emptyMap(),
    val index: Int = 0,
    val revealed: Boolean = false,
    val gradedCount: Int = 0,
    val againCount: Int = 0,
    val isPlaying: Boolean = false,
    /** Learner's typed answer in production mode (empty until typed). */
    val typedAnswer: String = "",
    /** Result of a checked production answer: null = not a production card / not checked yet. */
    val answerCorrect: Boolean? = null,
) {
    val currentItem: ReviewItem? get() = queue.getOrNull(index)
    val currentWord: VocabularyItem? get() = currentItem?.let { vocabulary[it.contentId] }
    val total: Int get() = queue.size

    /**
     * Active production: odd positions ask the learner to WRITE the English
     * word from its Persian meaning (typed, checked) instead of only recalling
     * the meaning — typing/listening/writing generation for the SRS (checklist P5).
     */
    val produceMode: Boolean get() = index % 2 == 1
}

sealed interface ReviewEvent {
    data object Start : ReviewEvent
    data object Reveal : ReviewEvent
    data class Grade(val result: ReviewResult) : ReviewEvent
    data object PlayWord : ReviewEvent
    data object Reset : ReviewEvent
    data class AnswerChanged(val value: String) : ReviewEvent
    data object CheckAnswer : ReviewEvent
}

/**
 * Review session: due queue → reveal → grade (SRS) → next.
 * Time is epoch millis; response time feeds the attempt log.
 */
class ReviewViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    private var itemStartedAtMs: Long = System.currentTimeMillis()

    /** Blocks a second grade() while the first SRS write is still in flight. */
    private var grading: Boolean = false

    /** contentId → times already re-shown after AGAIN in this session. */
    private val requeueCount = mutableMapOf<String, Int>()

    init {
        refresh()
        viewModelScope.launch {
            // Study time is credited in fixed ticks while the app is visible —
            // background time never counts toward the daily goal (P7).
            while (true) {
                kotlinx.coroutines.delay(STUDY_TICK_MILLIS)
                if (container.isAppInForeground) {
                    container.progressRepository.addStudySeconds(STUDY_TICK_MILLIS / 1000)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val due = container.reviewRepository.getDue(now, limit = 100)
            val vocab = due.mapNotNull { item ->
                container.vocabularyRepository.get(item.contentId)?.let { item.contentId to it }
            }.toMap()
            _state.update {
                it.copy(
                    isLoading = false,
                    queue = due,
                    vocabulary = vocab,
                    index = 0,
                    revealed = false,
                    typedAnswer = "",
                    answerCorrect = null,
                    phase = if (due.isEmpty()) ReviewPhase.SUMMARY else it.phase.takeIf { p -> p != ReviewPhase.DONE }
                        ?: ReviewPhase.SUMMARY,
                )
            }
            itemStartedAtMs = now
        }
    }

    fun onEvent(event: ReviewEvent) {
        when (event) {
            ReviewEvent.Start -> _state.update {
                it.copy(
                    phase = ReviewPhase.SESSION,
                    index = 0,
                    revealed = false,
                    gradedCount = 0,
                    againCount = 0,
                    typedAnswer = "",
                    answerCorrect = null,
                )
            }

            ReviewEvent.Reveal -> _state.update { if (it.revealed) it else it.copy(revealed = true) }

            is ReviewEvent.Grade -> grade(event.result)

            ReviewEvent.PlayWord -> {
                val word = _state.value.currentWord ?: return
                container.speak(word.word) { _state.update { it.copy(isPlaying = false) } }
            }

            ReviewEvent.Reset -> refresh()

            is ReviewEvent.AnswerChanged -> _state.update {
                if (it.revealed) it else it.copy(typedAnswer = event.value)
            }

            ReviewEvent.CheckAnswer -> checkAnswer()
        }
    }

    /** Checks the typed production answer, then reveals the card for grading. */
    private fun checkAnswer() {
        val s = _state.value
        val word = s.currentWord ?: return
        if (s.revealed || s.typedAnswer.isBlank()) return
        _state.update {
            it.copy(
                revealed = true,
                answerCorrect = AnswerChecker.matchesAny(listOf(word.word), s.typedAnswer),
            )
        }
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
                container.submitReview(
                    item = item,
                    result = result,
                    now = now,
                    responseTimeMs = now - itemStartedAtMs,
                )
                // Relearning: an AGAIN card comes back later in THIS session with
                // its fresh schedule already stored — capped so the queue ends.
                var queue = s.queue
                if (result == ReviewResult.AGAIN && (requeueCount[item.contentId] ?: 0) < MAX_IN_SESSION_REQUEUES) {
                    requeueCount[item.contentId] = (requeueCount[item.contentId] ?: 0) + 1
                    container.reviewRepository.getItem(item.contentId)?.let { fresh -> queue = queue + fresh }
                }
                val next = s.index + 1
                _state.update {
                    it.copy(
                        queue = queue,
                        index = next,
                        revealed = false,
                        typedAnswer = "",
                        answerCorrect = null,
                        gradedCount = it.gradedCount + 1,
                        againCount = if (result == ReviewResult.AGAIN) it.againCount + 1 else it.againCount,
                        phase = if (next >= queue.size) ReviewPhase.DONE else ReviewPhase.SESSION,
                    )
                }
                itemStartedAtMs = System.currentTimeMillis()
            } finally {
                grading = false
            }
        }
    }

    override fun onCleared() {
        container.audioPlayer.stop()
    }

    private companion object {
        /** Max times one card is re-shown per session after AGAIN. */
        const val MAX_IN_SESSION_REQUEUES = 2
        const val STUDY_TICK_MILLIS = 30_000L
    }
}
