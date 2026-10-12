package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.engine.SkillStatus
import org.token.english.domain.model.AttemptStats
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.feature.progress.ProgressViewModel

/**
 * The Progress screen's three tracks (checklist v8 P2), driven through the real
 * ViewModel on in-memory fakes: no Android, no Room, no device.
 *
 * The screen used to list six `Skill.entries` bars — including `SPEAKING`, which the
 * app cannot train at all, so it could only ever read "شروع نشده". What is asserted
 * here is the replacement: three tracks, each carrying the evidence behind its
 * number, and review practice counted into the track it exercises.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun node(id: String, vararg skills: Skill) = KnowledgeItem(
        id = id,
        type = KnowledgeType.GRAMMAR,
        title = id,
        titleFa = "«$id»",
        level = LearningLevel.A1,
        prerequisites = emptyList(),
        lessonIds = listOf("l.$id"),
        skills = skills.toList(),
    )

    private fun state(itemId: String, mastery: Float, attempts: Int, misses: Int) = KnowledgeState(
        itemId = itemId,
        mastery = mastery,
        exposureCount = attempts,
        consecutiveCorrect = 0,
        consecutiveIncorrect = misses,
        intervalDays = 0,
        easeFactor = 2.5f,
        repetitions = 0,
        lapses = misses,
        lastReviewedAt = null,
        nextReviewAt = 0L,
    )

    private fun viewModel(
        items: List<KnowledgeItem>,
        states: List<KnowledgeState>,
        mastery: Map<Skill, Float> = emptyMap(),
        reviewStats: Map<ReviewContentType, AttemptStats> = emptyMap(),
    ): ProgressViewModel {
        val knowledge = FakeKnowledgeRepository(items).apply { statesFlow.value = states }
        val progress = FakeProgressRepository().apply { masteryFlow.value = mastery }
        val reviews = FakeReviewRepository().apply { attemptStats = reviewStats }
        return ProgressViewModel(
            settingsRepository = FakeSettingsRepository(),
            lessonRepository = FakeLessonRepository(),
            progressRepository = progress,
            knowledgeRepository = knowledge,
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, reviews),
            studyStats = progress.statsFlow,
        )
    }

    @Test
    fun `the profile is exactly the three tracks, never the six-skill list`() = runTest {
        val vm = viewModel(
            items = listOf(node("g1", Skill.GRAMMAR)),
            states = emptyList(),
        )
        assertEquals(
            listOf(Skill.GRAMMAR, Skill.VOCABULARY, Skill.READING),
            vm.state.value.profiles.map { it.skill },
        )
        assertTrue(
            "speaking is not a track the app can train",
            vm.state.value.profiles.none { it.skill == Skill.SPEAKING },
        )
        // Nothing done yet, so every track reports the honest empty state rather than
        // a 0% that would read as "answered everything wrong".
        assertTrue(vm.state.value.profiles.all { it.status == SkillStatus.NOT_STARTED })
        assertTrue(vm.state.value.profiles.all { it.accuracy == null })
    }

    @Test
    fun `each track reaches the state with its own evidence`() = runTest {
        val vm = viewModel(
            items = listOf(node("g1", Skill.GRAMMAR), node("r1", Skill.READING)),
            states = listOf(
                state("g1", 0.9f, attempts = 8, misses = 1),
                state("r1", 0.4f, attempts = 2, misses = 1),
            ),
            mastery = mapOf(Skill.GRAMMAR to 0.9f, Skill.READING to 0.4f),
        )

        val grammar = vm.state.value.profiles.first { it.skill == Skill.GRAMMAR }
        val reading = vm.state.value.profiles.first { it.skill == Skill.READING }
        val vocabulary = vm.state.value.profiles.first { it.skill == Skill.VOCABULARY }

        assertEquals(8, grammar.evidence)
        assertEquals(SkillStatus.CONSOLIDATED, grammar.status)
        assertEquals(2, reading.evidence)
        assertEquals(SkillStatus.BUILDING, reading.status)
        assertEquals("no vocabulary work yet", 0, vocabulary.evidence)
        assertNull(vocabulary.accuracy)
    }

    @Test
    fun `review practice is counted into the vocabulary track`() = runTest {
        // The SRS log is where most vocabulary work happens and it never touches the
        // curriculum graph, so a track that ignored it would report a high number with
        // no evidence behind it.
        val vm = viewModel(
            items = listOf(node("v1", Skill.VOCABULARY)),
            states = emptyList(),
            mastery = mapOf(Skill.VOCABULARY to 0.8f),
            reviewStats = mapOf(ReviewContentType.VOCABULARY to AttemptStats(attempts = 10, misses = 2)),
        )
        val vocabulary = vm.state.value.profiles.first { it.skill == Skill.VOCABULARY }

        assertEquals(10, vocabulary.evidence)
        assertEquals(80, vocabulary.accuracyPercent)
        assertEquals(SkillStatus.CONSOLIDATED, vocabulary.status)
        assertEquals(
            "grammar gets nothing from a vocabulary review",
            0,
            vm.state.value.profiles.first { it.skill == Skill.GRAMMAR }.evidence,
        )
    }
}
