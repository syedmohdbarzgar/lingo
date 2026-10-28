package org.token.english

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.token.english.core.billing.CompanionStatus
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase
import org.token.english.feature.home.HomeScreen
import org.token.english.feature.home.HomeViewModel
import org.token.english.feature.lesson.LessonScreen
import org.token.english.feature.lesson.LessonViewModel
import org.token.english.feature.paywall.PaywallScreen
import org.token.english.feature.paywall.PaywallViewModel
import org.token.english.feature.review.ReviewScreen
import org.token.english.feature.review.ReviewViewModel
import org.token.english.core.designsystem.AppTheme

/**
 * Screen-level smoke tests (checklist v15 B-6): one Compose assertion per main
 * screen, on the host JVM via Robolectric — there is no emulator here, and the
 * project's stance is that a dead androidTest suite would be a false signal.
 *
 * Each screen takes its ViewModel through the `vm` seam added for exactly this,
 * built from the same in-memory fakes the ViewModel tests use, so what is proven
 * here is the layer the ViewModel tests cannot reach: the screen renders its
 * state, and the companion offer / "update from Bazaar" copy actually appears on
 * Home, the paywall and (implicitly through the same rule) Settings.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ScreenSmokeTest {

    @get:Rule
    val compose = createComposeRule()

    @Before
    fun setUp() {
        // Same deterministic main as the ViewModel tests: the screens' ViewModels
        // publish through viewModelScope, which must run eagerly here.
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /** A live 7-day trial, so entitlement-dependent cards actually render. */
    private fun liveTrial() = TrialAndSubscription(
        trialStartedAt = System.currentTimeMillis(),
        subscriptionUntil = 0L,
        trialConsumedMs = 0L,
        trialLastWallMs = 0L,
        trialLastElapsedMs = 0L,
    )

    private fun choice(id: String, lessonId: String) = Exercise.MultipleChoice(
        id = id,
        lessonId = lessonId,
        question = "Choose",
        questionFa = null,
        options = listOf("right", "wrong"),
        correctIndex = 0,
        skill = Skill.GRAMMAR,
        explanation = "because",
    )

    @Test
    fun `home renders its plan and the update copy for an untrusted companion`() {
        val settings = FakeSettingsRepository(trial = liveTrial())
        val lessons = FakeLessonRepository()
        val progress = FakeProgressRepository()
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository()
        val companion = MutableStateFlow(CompanionStatus.UNTRUSTED)
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

        compose.setContent {
            AppTheme {
                HomeScreen(
                    vm = vm,
                    onOpenLesson = {},
                    onOpenSettings = {},
                    onOpenReview = {},
                    onOpenVocabulary = {},
                    onOpenPaywall = {},
                    onOpenConversation = {},
                    onOpenFocus = { _, _ -> },
                )
            }
        }

        compose.onNodeWithText("آماده یادگیری امروز؟", substring = true).assertIsDisplayed()
        // N-3 at screen level: a Zaribar under an untrusted key must be told to
        // UPDATE it — the copy the ViewModel tests cannot see.
        compose.onNodeWithText("تأییدنشده", substring = true).assertIsDisplayed()
    }

    @Test
    fun `a lesson renders its first exercise`() {
        val lessonId = "a1.test.lesson-01"
        val lessonRepo = FakeLessonRepository(lessons = listOf(Lesson(lessonId, LearningLevel.A1, "t", "ت", "topic", 5, 1)))
            .apply { exercises[lessonId] = listOf(choice("$lessonId.ex.01", lessonId)) }
        val progress = FakeProgressRepository()
        val knowledge = FakeKnowledgeRepository()
        val vm = LessonViewModel(
            lessonId = lessonId,
            contentSeeded = MutableStateFlow(true),
            lessonRepository = lessonRepo,
            progressRepository = progress,
            submitExercise = SubmitExerciseUseCase(progress, knowledge),
            completeLesson = CompleteLessonUseCase(lessonRepo, progress),
            audioPlayer = FakeAudioPlayer(),
            voiceNote = FakeVoiceNote(),
            speaker = RecordingSpeaker(),
            isAppInForeground = { true },
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessonRepo),
            profileEngine = DefaultMasteryProfileEngine(),
            selector = AdaptiveExerciseSelector(),
            focusItemId = null,
        )

        compose.setContent {
            AppTheme {
                LessonScreen(lessonId = lessonId, onExit = {}, vm = vm)
            }
        }

        compose.onNodeWithText("Choose", substring = true).assertIsDisplayed()
    }

    @Test
    fun `an empty review queue says so instead of showing a blank screen`() {
        val reviews = FakeReviewRepository()
        val vm = ReviewViewModel(
            reviewRepository = reviews,
            vocabularyRepository = FakeVocabularyRepository(),
            progressRepository = FakeProgressRepository(),
            submitReview = SubmitReviewUseCase(reviews, Sm2ReviewScheduler(), FakeProgressRepository()),
            audioPlayer = FakeAudioPlayer(),
            speaker = RecordingSpeaker(),
            isAppInForeground = { true },
        )

        compose.setContent {
            AppTheme {
                ReviewScreen(onBrowseVocabulary = {}, onGoHome = {}, vm = vm)
            }
        }

        compose.onNodeWithText("امروز مروری در کار نیست", substring = true).assertIsDisplayed()
    }

    @Test
    fun `the paywall shows the plans and the free route for a learner without the companion`() {
        val vm = PaywallViewModel(
            billing = FakeBillingGateway(),
            settingsRepository = FakeSettingsRepository(),
            companionStatus = MutableStateFlow(CompanionStatus.ABSENT),
            refreshCompanion = { false },
            refreshEntitlements = {},
            accessTickMillis = 0,
        )

        compose.setContent {
            AppTheme {
                PaywallScreen(
                    onClose = {},
                    onLockedExit = {},
                    onLockedSettings = {},
                    onPurchased = {},
                    vm = vm,
                )
            }
        }

        // The priced plans the store answered with…
        compose.onNodeWithText("اشتراک سالانه", substring = true).assertIsDisplayed()
        // …and the free route (N-2): the offer a locked learner cannot currently
        // take without it, rendered below the price (hence the scroll).
        compose.onNodeWithText("دریافت زریبار از کافه بازار")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
