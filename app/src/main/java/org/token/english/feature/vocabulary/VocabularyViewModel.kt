package org.token.english.feature.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.audio.Speaker
import org.token.english.core.common.catchLoadFailure
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.engine.VocabularySearch
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.VocabularyRepository

data class VocabularyUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    /** The chosen CEFR level, or `null` for «همه». */
    val level: LearningLevel? = null,
    val all: List<VocabularyItem> = emptyList(),
    val filtered: List<VocabularyItem> = emptyList(),
    /**
     * The levels the bundle actually ships a word in, CEFR order. Read from the words
     * rather than from the enum, so no chip can lead to a guaranteed-empty list.
     */
    val levels: List<LearningLevel> = emptyList(),
    /** A read threw — shown as an error state, so the spinner cannot hang. */
    val loadFailed: Boolean = false,
)

/** Vocabulary browser state (checklist B-6): just the repository it observes. */
class VocabularyViewModel(
    private val vocabularyRepository: VocabularyRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VocabularyUiState())
    val state: StateFlow<VocabularyUiState> = _state.asStateFlow()

    init {
        startLoading()
    }

    /** One collector, guarded: a Room failure becomes state instead of a hung spinner. */
    private fun startLoading() {
        viewModelScope.launch {
            vocabularyRepository.observeAll().catchLoadFailure(::onLoadFailure).collect { items ->
                _state.update { current ->
                    val levels = LearningLevel.entries.filter { level -> items.any { it.level == level } }
                    // A chosen level that left the bundle cannot filter the screen down to
                    // nothing: re-seeding is a content change, not a learner's mistake.
                    val level = current.level?.takeIf { it in levels }
                    current.copy(
                        isLoading = false,
                        loadFailed = false,
                        all = items,
                        levels = levels,
                        level = level,
                        filtered = filter(items, current.query, level),
                    )
                }
            }
        }
    }

    private fun onLoadFailure(error: Throwable) {
        android.util.Log.w("VocabularyViewModel", "vocabulary read failed", error)
        _state.update { it.copy(isLoading = false, loadFailed = true) }
    }

    /** The error state's action: re-subscribe to the same local read. */
    fun retry() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        startLoading()
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, filtered = filter(it.all, query, it.level)) }
    }

    /** «همه» is `null`: a level filter narrows the list, it does not replace the search. */
    fun onLevelChange(level: LearningLevel?) {
        _state.update { it.copy(level = level, filtered = filter(it.all, it.query, level)) }
    }

    /**
     * The browser's search, in the engine: the same letter folded on both sides and
     * the best match first (see [VocabularySearch]). Two `contains` calls on the raw
     * strings read the bundle's spelling as the only spelling — «كتاب» with the Arabic
     * kaf a Persian keyboard produces found nothing.
     */
    private fun filter(
        items: List<VocabularyItem>,
        query: String,
        level: LearningLevel?,
    ): List<VocabularyItem> = VocabularySearch.browse(items, query, level)
}

data class VocabularyDetailUiState(
    val isLoading: Boolean = true,
    val item: VocabularyItem? = null,
    val reviewItem: ReviewItem? = null,
    val isPlaying: Boolean = false,
    val message: String? = null,
    /** A read threw — the screen says so and offers a retry. */
    val loadFailed: Boolean = false,
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
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val loaded = runCatchingCancellable {
                vocabularyRepository.get(vocabId) to reviewRepository.getItem(vocabId)
            }.getOrElse { error ->
                android.util.Log.w("VocabularyDetailViewModel", "vocabulary read failed", error)
                _state.update { it.copy(isLoading = false, loadFailed = true) }
                return@launch
            }
            _state.update {
                it.copy(isLoading = false, loadFailed = false, item = loaded.first, reviewItem = loaded.second)
            }
        }
    }

    /** The error state's action: read the word again (it lives on this device). */
    fun retry() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        load()
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
