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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.feature.home.HomeViewModel

/**
 * First ViewModel test (checklist B-6). Because HomeViewModel now takes exactly
 * the repositories and seams it uses, it can be built here with in-memory fakes —
 * no Android context, no Room, no DataStore.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val lessonId = "a1.test.lesson-01"

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(companion: MutableStateFlow<Boolean>): HomeViewModel {
        val settings = FakeSettingsRepository()
        val lessons = FakeLessonRepository()
        val progress = FakeProgressRepository()
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository()
        val planner = DefaultLearningPlanner()
        return HomeViewModel(
            settingsRepository = settings,
            lessonRepository = lessons,
            progressRepository = progress,
            reviewRepository = reviews,
            studyStats = progress.statsFlow,
            planner = planner,
            getTodayPlan = GetTodayPlanUseCase(lessons, reviews, progress, settings, planner, knowledge),
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessons),
            companionInstalled = companion,
            refreshCompanion = { companion.value },
            accessTickMillis = 0,
            dueTickMillis = 0,
        )
    }

    @Test
    fun `installing the companion grants access and removing it locks the app`() {
        val companion = MutableStateFlow(true)
        val vm = viewModel(companion)

        assertFalse("state finished loading", vm.state.value.isLoading)
        assertEquals(AccessLevel.PREMIUM, vm.state.value.access)
        assertEquals("a free companion grant is not a purchase", AccessReason.COMPANION_APP, vm.state.value.accessReason)
        assertTrue(vm.state.value.companionInstalled)

        // The user uninstalls zaribar: the grant must not outlive the install.
        companion.value = false
        assertEquals(AccessLevel.LOCKED, vm.state.value.access)
        assertEquals(AccessReason.NONE, vm.state.value.accessReason)
        assertFalse(vm.state.value.companionInstalled)
    }

    @Test
    fun `home carries the engine's weak spot when a node is weak and has evidence`() {
        val settings = FakeSettingsRepository()
        val lessons = FakeLessonRepository(
            lessons = listOf(Lesson(lessonId, LearningLevel.A1, "Greetings", "سلام‌ها", "topic", 5, 1)),
        )
        lessons.exercises[lessonId] = listOf(
            Exercise.MultipleChoice(
                id = "$lessonId.ex.01", lessonId = lessonId, question = "q", questionFa = null,
                options = listOf("a", "b"), correctIndex = 0, skill = Skill.GRAMMAR,
            ),
            Exercise.MultipleChoice(
                id = "$lessonId.ex.02", lessonId = lessonId, question = "q", questionFa = null,
                options = listOf("a", "b"), correctIndex = 0, skill = Skill.VOCABULARY,
            ),
        )
        val progress = FakeProgressRepository()
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository(
            items = listOf(
                KnowledgeItem(
                    id = "grammar.test", type = KnowledgeType.GRAMMAR, title = "t", titleFa = "گرامر آزمایشی",
                    level = LearningLevel.A1, prerequisites = emptyList(),
                    lessonIds = listOf(lessonId), skills = listOf(Skill.GRAMMAR),
                ),
                KnowledgeItem(
                    id = "vocab.test", type = KnowledgeType.VOCABULARY, title = "w", titleFa = "واژگان آزمایشی",
                    level = LearningLevel.A1, prerequisites = emptyList(),
                    lessonIds = listOf(lessonId), skills = listOf(Skill.VOCABULARY),
                ),
            ),
        )
        knowledge.statesFlow.value = listOf(
            KnowledgeState(
                itemId = "grammar.test", mastery = 0.2f, exposureCount = 3,
                consecutiveCorrect = 0, consecutiveIncorrect = 2, intervalDays = 0,
                easeFactor = 2.5f, repetitions = 0, lapses = 0, lastReviewedAt = null, nextReviewAt = 0L,
            ),
        )
        val planner = DefaultLearningPlanner()
        val companion = MutableStateFlow(false)

        val vm = HomeViewModel(
            settingsRepository = settings,
            lessonRepository = lessons,
            progressRepository = progress,
            reviewRepository = reviews,
            studyStats = progress.statsFlow,
            planner = planner,
            getTodayPlan = GetTodayPlanUseCase(lessons, reviews, progress, settings, planner, knowledge),
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessons),
            companionInstalled = companion,
            refreshCompanion = { companion.value },
            accessTickMillis = 0,
            dueTickMillis = 0,
        )

        assertFalse("state finished loading", vm.state.value.isLoading)
        val focus = vm.state.value.focus
        assertEquals("the weakest node with evidence", "grammar.test", focus?.itemId)
        assertEquals("گرامر آزمایشی", focus?.titleFa)
        assertEquals("only the exercise that is evidence about the node", setOf("$lessonId.ex.01"), focus?.exerciseIds)
    }
}
