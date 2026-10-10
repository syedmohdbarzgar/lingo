package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.usecase.SubmitReviewUseCase
import org.token.english.feature.review.ReviewEvent
import org.token.english.feature.review.ReviewPhase
import org.token.english.feature.review.ReviewViewModel

/**
 * ReviewViewModel tests (checklist B-6): the SRS write on a grade and the
 * in-session re-queue of an AGAIN card.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    private val contentId = "a1.test.word.hello"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val card = ReviewItem(
        contentId = contentId,
        contentType = ReviewContentType.VOCABULARY,
        state = ReviewState.NEW,
        dueAt = 0L,
        intervalDays = 0,
        easeFactor = Sm2ReviewScheduler.DEFAULT_EASE,
        repetitions = 0,
        lapses = 0,
        lastReviewedAt = null,
    )

    private val word = VocabularyItem(
        id = contentId,
        word = "hello",
        translation = "سلام",
        definition = null,
        pronunciation = null,
        level = LearningLevel.A1,
        partOfSpeech = "interjection",
        examples = emptyList(),
        collocations = emptyList(),
        lessonId = "a1.test.lesson-01",
    )

    private class Harness {
        val reviews = FakeReviewRepository()
        val vocabulary = FakeVocabularyRepository()
        val progress = FakeProgressRepository()
        val audio = FakeAudioPlayer()
        val speaker = RecordingSpeaker()

        fun build(): ReviewViewModel = ReviewViewModel(
            reviewRepository = reviews,
            vocabularyRepository = vocabulary,
            progressRepository = progress,
            submitReview = SubmitReviewUseCase(reviews, Sm2ReviewScheduler(), progress),
            audioPlayer = audio,
            speaker = speaker,
            isAppInForeground = { true },
            studyTickMillis = 0,
        )
    }

    private fun Harness.withDueCard(): Harness {
        reviews.dueFlow.value = listOf(card)
        vocabulary.allFlow.value = listOf(word)
        return this
    }

    @Test
    fun `grading a card writes the schedule and finishes the session`() {
        val h = Harness().withDueCard()
        val vm = h.build()
        assertEquals(1, vm.state.value.queue.size)

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.GOOD))

        assertEquals(ReviewPhase.DONE, vm.state.value.phase)
        assertEquals(1, h.reviews.attempts.size)
        assertEquals(ReviewResult.GOOD, h.reviews.attempts.first().result)
        assertTrue("the next schedule is persisted", h.reviews.scheduled.isNotEmpty())
        assertTrue("a GOOD grade moves the vocabulary meter", h.progress.attempts.contains(Skill.VOCABULARY to true))
    }

    @Test
    fun `an AGAIN card comes back later in the same session`() {
        val h = Harness().withDueCard()
        val vm = h.build()

        vm.onEvent(ReviewEvent.Start)
        vm.onEvent(ReviewEvent.Reveal)
        vm.onEvent(ReviewEvent.Grade(ReviewResult.AGAIN))

        assertEquals("the card is re-queued", 2, vm.state.value.queue.size)
        assertEquals(1, vm.state.value.againCount)
        assertEquals(ReviewPhase.SESSION, vm.state.value.phase)
        assertTrue("an AGAIN grade does not credit mastery", h.progress.attempts.contains(Skill.VOCABULARY to false))
    }
}
