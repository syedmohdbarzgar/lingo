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
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.CompanionOffer
import org.token.english.core.billing.CompanionStatus
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningActionType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewContentType
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.feature.home.HomeViewModel

/**
 * Home screen tests (checklist B-6). Home owns the day: the plan it budgets, the
 * level-progression card, the remediation focus and the companion grant. Because
 * [HomeViewModel] takes exactly the repositories and seams it reads, each of those
 * can be driven here with in-memory fakes — no Android context, no Room, no DataStore.
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

    /**
     * Everything Home is wired to, kept reachable so a test can mutate a fake after
     * construction and observe the ViewModel react.
     */
    private class Harness(
        val settings: FakeSettingsRepository,
        val lessons: FakeLessonRepository,
        val progress: FakeProgressRepository,
        val reviews: FakeReviewRepository,
        val knowledge: FakeKnowledgeRepository,
        val companion: MutableStateFlow<CompanionStatus>,
    ) {
        val planner = DefaultLearningPlanner()
        val vm = HomeViewModel(
            settingsRepository = settings,
            lessonRepository = lessons,
            progressRepository = progress,
            reviewRepository = reviews,
            studyStats = progress.statsFlow,
            planner = planner,
            getTodayPlan = GetTodayPlanUseCase(lessons, reviews, progress, settings, planner, knowledge),
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessons),
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, reviews),
            companionStatus = companion,
            refreshCompanion = { companion.value == CompanionStatus.TRUSTED },
            accessTickMillis = 0,
            dueTickMillis = 0,
        )
    }

    private fun harness(
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        lessons: FakeLessonRepository = FakeLessonRepository(),
        progress: FakeProgressRepository = FakeProgressRepository(),
        reviews: FakeReviewRepository = FakeReviewRepository(),
        knowledge: FakeKnowledgeRepository = FakeKnowledgeRepository(),
        companion: MutableStateFlow<CompanionStatus> = MutableStateFlow(CompanionStatus.ABSENT),
    ): Harness = Harness(settings, lessons, progress, reviews, knowledge, companion)

    private fun lesson(id: String, level: LearningLevel) =
        Lesson(id, level, "Lesson", "درس", "topic", 5, 1)

    private fun dueCard(id: String) = ReviewItem(
        contentId = id,
        contentType = ReviewContentType.VOCABULARY,
        state = ReviewState.REVIEW,
        dueAt = 0L,
        intervalDays = 1,
        easeFactor = 2.5f,
        repetitions = 1,
        lapses = 0,
        lastReviewedAt = null,
    )

    @Test
    fun `installing the companion grants access and removing it locks the app`() {
        val companion = MutableStateFlow(CompanionStatus.TRUSTED)
        val vm = harness(companion = companion).vm

        assertFalse("state finished loading", vm.state.value.isLoading)
        assertEquals(AccessLevel.PREMIUM, vm.state.value.access)
        assertEquals("a free companion grant is not a purchase", AccessReason.COMPANION_APP, vm.state.value.accessReason)
        assertEquals(CompanionStatus.TRUSTED, vm.state.value.companionStatus)

        // The user uninstalls zaribar: the grant must not outlive the install.
        companion.value = CompanionStatus.ABSENT
        assertEquals(AccessLevel.LOCKED, vm.state.value.access)
        assertEquals(AccessReason.NONE, vm.state.value.accessReason)
        assertEquals(CompanionStatus.ABSENT, vm.state.value.companionStatus)
    }

    @Test
    fun `an untrusted zaribar earns no grant and asks to be updated`() {
        val vm = harness(companion = MutableStateFlow(CompanionStatus.UNTRUSTED)).vm

        assertEquals("a foreign key earns nothing", AccessLevel.LOCKED, vm.state.value.access)
        assertEquals(AccessReason.NONE, vm.state.value.accessReason)
        assertEquals(CompanionStatus.UNTRUSTED, vm.state.value.companionStatus)
        assertTrue(
            "the route is still offered — as an update, never hidden",
            CompanionOffer.isOffered(
                vm.state.value.companionStatus,
                vm.state.value.access,
                vm.state.value.accessReason,
            ),
        )
    }

    @Test
    fun `home carries the engine's weak spot when a node is weak and has evidence`() {
        val lessons = FakeLessonRepository(lessons = listOf(lesson(lessonId, LearningLevel.A1)))
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

        val vm = harness(lessons = lessons, knowledge = knowledge).vm

        assertFalse("state finished loading", vm.state.value.isLoading)
        val focus = vm.state.value.focus
        assertEquals("the weakest node with evidence", "grammar.test", focus?.itemId)
        assertEquals("گرامر آزمایشی", focus?.titleFa)
        assertEquals("only the exercise that is evidence about the node", setOf("$lessonId.ex.01"), focus?.exerciseIds)
    }

    @Test
    fun `finishing every lesson at the current level offers the next level and advancing moves there`() {
        val settings = FakeSettingsRepository(initial = testSettings(level = LearningLevel.A1))
        val lessons = FakeLessonRepository(
            lessons = listOf(lesson("a1.l1", LearningLevel.A1), lesson("a2.l1", LearningLevel.A2)),
            states = listOf(LessonState("a1.l1", 0, completed = true, completedAt = 1L)),
        )

        val vm = harness(settings = settings, lessons = lessons).vm

        assertTrue("the only A1 lesson is done", vm.state.value.levelComplete)
        assertEquals("A2 still has lessons to do", LearningLevel.A2, vm.state.value.nextLevel)

        vm.advanceLevel()

        assertEquals("advancing persists the new level", LearningLevel.A2, settings.settingsFlow.value.level)
        assertFalse("A2 is not complete yet", vm.state.value.levelComplete)
        assertNull("nothing above A2 is pending", vm.state.value.nextLevel)
    }

    @Test
    fun `an install with no lessons does not claim the level is complete`() {
        val vm = harness().vm

        assertFalse("state finished loading", vm.state.value.isLoading)
        assertFalse("no lessons is not 'finished'", vm.state.value.levelComplete)
        assertNull("there is nowhere to advance to", vm.state.value.nextLevel)
    }

    @Test
    fun `a due backlog leads the day with review and stays inside the daily goal`() {
        val knowledge = FakeKnowledgeRepository(
            items = listOf(
                KnowledgeItem(
                    id = "vocab.test", type = KnowledgeType.VOCABULARY, title = "w", titleFa = "واژگان آزمایشی",
                    level = LearningLevel.A1, prerequisites = emptyList(),
                    lessonIds = listOf(lessonId), skills = listOf(Skill.VOCABULARY),
                ),
            ),
        )
        val harness = harness(
            settings = FakeSettingsRepository(initial = testSettings(dailyGoalMinutes = 10)),
            lessons = FakeLessonRepository(lessons = listOf(lesson(lessonId, LearningLevel.A1))),
            knowledge = knowledge,
        )
        harness.reviews.dueFlow.value = List(30) { dueCard("card-$it") }

        val plan = harness.vm.state.value.plan ?: error("home should have built a plan")
        assertEquals("30 due cards would estimate 30 minutes on a 10-minute goal", 30, plan.dueReviewCount)
        assertEquals("now", 10, plan.targetMinutes)
        assertEquals("review heads the day, however large it is", LearningActionType.REVIEW, plan.actions.first().type)
        assertTrue(
            "the planned day never exceeds the goal it was budgeted against",
            plan.actions.sumOf { it.estimatedMinutes } <= plan.targetMinutes,
        )
    }
}
