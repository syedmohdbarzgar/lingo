package org.token.english.feature.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.audio.Speaker
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.VocabularyRepository

data class VocabularyUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val all: List<VocabularyItem> = emptyList(),
    val filtered: List<VocabularyItem> = emptyList(),
)

/** Vocabulary browser state (checklist B-6): just the repository it observes. */
class VocabularyViewModel(
    private val vocabularyRepository: VocabularyRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VocabularyUiState())
    val state: StateFlow<VocabularyUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            vocabularyRepository.observeAll().collect { items ->
                _state.update { current ->
                    val filtered = filter(items, current.query)
                    current.copy(isLoading = false, all = items, filtered = filtered)
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, filtered = filter(it.all, query)) }
    }

    private fun filter(items: List<VocabularyItem>, query: String): List<VocabularyItem> {
        val q = query.trim()
        if (q.isEmpty()) return items
        return items.filter {
            it.word.contains(q, ignoreCase = true) || it.translation.contains(q, ignoreCase = true)
        }
    }
}

data class VocabularyDetailUiState(
    val isLoading: Boolean = true,
    val item: VocabularyItem? = null,
    val reviewItem: ReviewItem? = null,
    val isPlaying: Boolean = false,
    val message: String? = null,
)

/** Vocabulary detail state (checklist B-6): the two repositories and the audio seam. */
class VocabularyDetailViewModel(
    private val vocabId: String,
    private val vocabularyRepository: VocabularyRepository,
    private val reviewRepository: ReviewRepository,
    private val speaker: Speaker,
) : ViewModel() {

    private val _state = MutableStateFlow(VocabularyDetailUiState())
    val state: StateFlow<VocabularyDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val item = vocabularyRepository.get(vocabId)
            val review = reviewRepository.getItem(vocabId)
            _state.update {
                it.copy(isLoading = false, item = item, reviewItem = review)
            }
        }
    }

    fun playWord() {
        val word = _state.value.item ?: return
        _state.update { it.copy(isPlaying = true) }
        speaker.speak(text = word.word, onDone = { _state.update { it.copy(isPlaying = false) } })
    }

    fun addToReview() {
        val current = _state.value
        if (current.item == null || current.reviewItem != null) return
        viewModelScope.launch {
            reviewRepository.schedule(
                ReviewItem(
                    contentId = current.item.id,
                    contentType = ReviewContentType.VOCABULARY,
                    state = ReviewState.NEW,
                    dueAt = System.currentTimeMillis(),
                    intervalDays = 0,
                    easeFactor = Sm2ReviewScheduler.DEFAULT_EASE,
                    repetitions = 0,
                    lapses = 0,
                    lastReviewedAt = null,
                ),
            )
            _state.update {
                it.copy(
                    reviewItem = reviewRepository.getItem(vocabId),
                    message = "به صف مرور اضافه شد.",
                )
            }
        }
    }
}
