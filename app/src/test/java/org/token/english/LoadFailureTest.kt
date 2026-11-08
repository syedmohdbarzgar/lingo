package org.token.english

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.billing.CompanionStatus
import org.token.english.core.common.catchLoadFailure
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.VocabularyRepository
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase
import org.token.english.feature.progress.ProgressViewModel
import org.token.english.feature.review.ReviewViewModel
import org.token.english.feature.settings.SettingsViewModel
import org.token.english.feature.vocabulary.VocabularyViewModel

/**
 * A failed local read must become state the screen can show — not a spinner nobody
 * can leave (the old behaviour: nothing ever set `isLoading = false`) and not a
 * silent crash of a background collector.
 *
 * These pin the two halves separately: the flow operator that reports instead of
 * swallowing, and one screen each for the two read shapes (a flow and a one-shot
 * query).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoadFailureTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a failing flow is reported and ends instead of crashing the collector`() = runTest {
        val failures = mutableListOf<Throwable>()
        val values = flow<Int> {
            emit(1)
            throw IllegalStateException("database went away")
        }.catchLoadFailure { failures += it }.toList()

        assertEquals("the values before the failure are kept", listOf(1), values)
        assertEquals(1, failures.size)
        assertEquals("database went away", failures.single().message)
    }

    @Test
    fun `cancellation is not a load failure`() = runTest {
        val failures = mutableListOf<Throwable>()
        val thrown = runCatching {
            flow<Int> { throw CancellationException("screen closed") }
                .catchLoadFailure { failures += it }
                .toList()
        }.exceptionOrNull()

        assertTrue("the cancellation still propagates", thrown is CancellationException)
        assertTrue("a closed screen is not an error", failures.isEmpty())
    }

    @Test
    fun `a vocabulary list that fails to load shows an error state and can retry`() = runTest {
        var failing = true
        val repository = object : VocabularyRepository {
            private val words = MutableStateFlow(listOf(word))
            override fun observeAll(): Flow<List<VocabularyItem>> =
                if (failing) flow { throw IllegalStateException("cannot read") } else words
            override suspend fun search(query: String) = words.value
            override suspend fun get(id: String) = words.value.firstOrNull { it.id == id }
            override suspend fun count() = words.value.size
        }

        val vm = VocabularyViewModel(repository)
        assertFalse("the spinner must not outlive the failure", vm.state.value.isLoading)
        assertTrue("the failure is state the screen can render", vm.state.value.loadFailed)

        failing = false
        vm.retry()

        assertFalse(vm.state.value.loadFailed)
        assertEquals(listOf(word), vm.state.value.all)
    }

    @Test
    fun `a review queue that fails to load is reported and its retry reads again`() = runTest {
        val reviews = FakeReviewRepository()
        reviews.dueFlow.value = emptyList()
        reviews.failDueWith = IllegalStateException("cannot read the queue")
        val vm = ReviewViewModel(
            reviewRepository = reviews,
            vocabularyRepository = FakeVocabularyRepository(),
            progressRepository = FakeProgressRepository(),
            submitReview = SubmitReviewUseCase(reviews, Sm2ReviewScheduler(), FakeProgressRepository()),
            audioPlayer = FakeAudioPlayer(),
            speaker = RecordingSpeaker(),
            isAppInForeground = { true },
        )

        assertFalse("no infinite spinner", vm.state.value.isLoading)
        assertTrue(vm.state.value.loadFailed)

        reviews.failDueWith = null
        vm.retry()

        assertFalse(vm.state.value.loadFailed)
        assertTrue("the queue is readable again", vm.state.value.queue.isEmpty())
    }

    @Test
    fun `a progress screen whose read fails shows the error state and its retry reads again`() = runTest {
        val settings = FakeSettingsRepository().apply {
            failSettingsWith = IllegalStateException("cannot read settings")
        }
        val progress = FakeProgressRepository()
        val knowledge = FakeKnowledgeRepository()
        val vm = ProgressViewModel(
            settingsRepository = settings,
            lessonRepository = FakeLessonRepository(),
            progressRepository = progress,
            knowledgeRepository = knowledge,
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, FakeReviewRepository()),
            studyStats = progress.statsFlow,
        )

        assertFalse("the spinner must not outlive the failure", vm.state.value.isLoading)
        assertTrue("the screen can say the read failed", vm.state.value.loadFailed)

        settings.failSettingsWith = null
        vm.retry()

        assertFalse("the retry leaves the error state", vm.state.value.loadFailed)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `a settings screen whose read fails never shows default switches as the stored ones`() = runTest {
        val settings = FakeSettingsRepository().apply {
            failSettingsWith = IllegalStateException("cannot read settings")
        }
        val vm = SettingsViewModel(
            settingsRepository = settings,
            progressRepository = FakeProgressRepository(),
            knowledgeRepository = FakeKnowledgeRepository(),
            // Unused here: a failed read never reaches the reset path.
            conversationRepository = FakeConversationRepository(),
            voiceNote = FakeVoiceNote(),
            companionStatus = MutableStateFlow(CompanionStatus.ABSENT),
            refreshCompanion = { false },
            accessTickMillis = 0,
        )

        assertFalse("no infinite spinner", vm.state.value.isLoading)
        assertTrue(vm.state.value.loadFailed)
        assertNull(
            "nothing is presented as the stored settings while the read failed",
            vm.state.value.settings,
        )

        settings.failSettingsWith = null
        vm.retry()

        assertFalse(vm.state.value.loadFailed)
        assertNotNull("the switches now read the real values", vm.state.value.settings)
    }

    private val word = VocabularyItem(
        id = "a1.test.word.01",
        word = "book",
        translation = "کتاب",
        definition = null,
        pronunciation = null,
        level = LearningLevel.A1,
        partOfSpeech = null,
        examples = emptyList(),
        collocations = emptyList(),
        lessonId = null,
    )
}
