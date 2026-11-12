package org.token.english

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.token.english.core.billing.CompanionStatus
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.core.common.ContentFailure
import org.token.english.core.common.ContentStatus
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultLearningPlanner
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.domain.engine.Sm2ReviewScheduler
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.CompleteLessonUseCase
import org.token.english.domain.usecase.GetDueKnowledgeReviewsUseCase
import org.token.english.domain.usecase.GetFocusPlanUseCase
import org.token.english.domain.usecase.GetSkillProfilesUseCase
import org.token.english.domain.usecase.GetMissProfileUseCase
import org.token.english.domain.usecase.GetTodayPlanUseCase
import org.token.english.domain.usecase.SubmitExerciseUseCase
import org.token.english.domain.usecase.SubmitReviewUseCase
import org.token.english.feature.home.HomeScreen
import org.token.english.feature.home.HomeViewModel
import org.token.english.feature.lesson.LessonEvent
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
            conversationRepository = FakeConversationRepository(),
            progressRepository = progress,
            reviewRepository = reviews,
            studyStats = progress.statsFlow,
            planner = planner,
            getTodayPlan = GetTodayPlanUseCase(
                lessons, reviews, progress, settings, planner, knowledge,
                GetMissProfileUseCase(lessons, reviews),
            ),
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessons),
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, reviews),
            companionStatus = companion,
            refreshCompanion = { companion.value == CompanionStatus.TRUSTED },
            contentStatus = MutableStateFlow(ContentStatus.READY),
            contentFailure = MutableStateFlow(null),
            seedContent = {},
            rebuildDatabase = { true },
            restartApp = { true },
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
                    onOpenPaywall = {},
                    onOpenConversation = { _ -> },
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
    fun `a home whose bundle never landed says why and offers a retry`() {
        val lessons = FakeLessonRepository()
        val progress = FakeProgressRepository()
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository()
        val companion = MutableStateFlow(CompanionStatus.ABSENT)
        val planner = DefaultLearningPlanner()
        val vm = HomeViewModel(
            settingsRepository = FakeSettingsRepository(trial = liveTrial()),
            lessonRepository = lessons,
            conversationRepository = FakeConversationRepository(),
            progressRepository = progress,
            reviewRepository = reviews,
            studyStats = progress.statsFlow,
            planner = planner,
            getTodayPlan = GetTodayPlanUseCase(
                lessons, reviews, progress, FakeSettingsRepository(), planner, knowledge,
                GetMissProfileUseCase(lessons, reviews),
            ),
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessons),
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, reviews),
            companionStatus = companion,
            refreshCompanion = { false },
            contentStatus = MutableStateFlow(ContentStatus.FAILED),
            contentFailure = MutableStateFlow(null),
            seedContent = {},
            rebuildDatabase = { true },
            restartApp = { true },
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
                    onOpenPaywall = {},
                    onOpenConversation = { _ -> },
                    onOpenFocus = { _, _ -> },
                )
            }
        }

        compose.onNodeWithText("محتوای درس‌ها آماده نشد", substring = true).assertIsDisplayed()
        // The action exists, and the copy says the cause is local — never the network.
        compose.onNodeWithText("تلاش دوباره", substring = true).assertIsDisplayed()
        compose.onNodeWithText("این خطا به اینترنت مربوط نیست", substring = true).assertIsDisplayed()
        // A failed asset is not a broken file: the rebuild must not be offered here,
        // or a learner would wipe readable progress over a bundle that a retry fixes.
        compose.onNodeWithText("ساخت دوبارهٔ پایگاه داده", substring = true).assertDoesNotExist()
    }

    @Test
    fun `a database room cannot open offers the rebuild behind a confirmation`() {
        val lessons = FakeLessonRepository()
        val progress = FakeProgressRepository()
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository()
        val companion = MutableStateFlow(CompanionStatus.ABSENT)
        val planner = DefaultLearningPlanner()
        var restarts = 0
        val vm = HomeViewModel(
            settingsRepository = FakeSettingsRepository(trial = liveTrial()),
            lessonRepository = lessons,
            conversationRepository = FakeConversationRepository(),
            progressRepository = progress,
            reviewRepository = reviews,
            studyStats = progress.statsFlow,
            planner = planner,
            getTodayPlan = GetTodayPlanUseCase(
                lessons, reviews, progress, FakeSettingsRepository(), planner, knowledge,
                GetMissProfileUseCase(lessons, reviews),
            ),
            getFocusPlan = GetFocusPlanUseCase(knowledge, lessons),
            getSkillProfiles = GetSkillProfilesUseCase(knowledge, progress, reviews),
            companionStatus = companion,
            refreshCompanion = { false },
            contentStatus = MutableStateFlow(ContentStatus.FAILED),
            contentFailure = MutableStateFlow(ContentFailure.DATABASE_UNREADABLE),
            seedContent = {},
            rebuildDatabase = { true },
            restartApp = { restarts++; true },
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
                    onOpenPaywall = {},
                    onOpenConversation = { _ -> },
                    onOpenFocus = { _, _ -> },
                )
            }
        }

        // The only failure a retry cannot fix gets the rebuild…
        compose.onNodeWithText("ساخت دوبارهٔ پایگاه داده", substring = true).assertIsDisplayed()

        // …behind a confirmation that names what is lost, and nothing happens until it
        // is accepted (the tap that dismisses must not restart the app).
        compose.onNodeWithText("ساخت دوبارهٔ پایگاه داده", substring = true).performClick()
        compose.onNodeWithText("پایگاه داده از نو ساخته شود؟", substring = true).assertIsDisplayed()
        compose.onNodeWithText("انصراف", substring = true).performClick()
        assertEquals("dismissing the dialog repairs nothing", 0, restarts)
        compose.onNodeWithText("پایگاه داده از نو ساخته شود؟", substring = true).assertDoesNotExist()
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
            contentStatus = MutableStateFlow(ContentStatus.READY),
            seedContent = { true },
            lessonRepository = lessonRepo,
            progressRepository = progress,
            submitExercise = SubmitExerciseUseCase(progress, knowledge),
            completeLesson = CompleteLessonUseCase(lessonRepo, progress),
            audioPlayer = FakeAudioPlayer(),
            speaker = RecordingSpeaker(),
            voiceNote = FakeVoiceNote(),
            speechState = FakeSpeech().state,
            startRecognition = {},
            stopRecognition = {},
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

        // The guided flow opens on the lesson's own first step, not mid-exercise.
        compose.onNodeWithText("زمان تقریبی", substring = true).assertIsDisplayed()

        vm.onEvent(LessonEvent.StartExercises)
        compose.onNodeWithText("Choose", substring = true).assertIsDisplayed()
    }

    @Test
    fun `an empty review queue says so instead of showing a blank screen`() {
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository()
        val vm = ReviewViewModel(
            reviewRepository = reviews,
            vocabularyRepository = FakeVocabularyRepository(),
            progressRepository = FakeProgressRepository(),
            submitReview = SubmitReviewUseCase(
                reviews,
                Sm2ReviewScheduler(),
                FakeProgressRepository(),
                knowledge,
            ),
            getDueKnowledgeReviews = GetDueKnowledgeReviewsUseCase(
                knowledge,
                GetFocusPlanUseCase(knowledge, FakeLessonRepository()),
            ),
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
    fun `a due curriculum node is a row on the review screen and opens its drill`() {
        val reviews = FakeReviewRepository()
        val knowledge = FakeKnowledgeRepository()
        val lessons = FakeLessonRepository()
        val lessonId = "a1.test.lesson-01"
        knowledge.itemsFlow.value = listOf(
            org.token.english.domain.model.KnowledgeItem(
                id = "grammar.test",
                type = org.token.english.domain.model.KnowledgeType.GRAMMAR,
                title = "Verb be",
                titleFa = "فعل be",
                level = LearningLevel.A1,
                prerequisites = emptyList(),
                lessonIds = listOf(lessonId),
                skills = listOf(Skill.GRAMMAR),
            ),
        )
        knowledge.statesFlow.value = listOf(
            org.token.english.domain.model.KnowledgeState(
                itemId = "grammar.test",
                mastery = 0.7f,
                exposureCount = 2,
                consecutiveCorrect = 1,
                consecutiveIncorrect = 0,
                intervalDays = 0,
                easeFactor = 2.5f,
                repetitions = 1,
                lapses = 0,
                lastReviewedAt = 0L,
                nextReviewAt = 0L,
            ),
        )
        lessons.exercises[lessonId] = listOf(
            Exercise.MultipleChoice(
                id = "$lessonId.ex.01",
                lessonId = lessonId,
                question = "Choose",
                questionFa = null,
                options = listOf("is", "are"),
                correctIndex = 0,
                skill = Skill.GRAMMAR,
            ),
        )
        val vm = ReviewViewModel(
            reviewRepository = reviews,
            vocabularyRepository = FakeVocabularyRepository(),
            progressRepository = FakeProgressRepository(),
            submitReview = SubmitReviewUseCase(
                reviews,
                Sm2ReviewScheduler(),
                FakeProgressRepository(),
                knowledge,
            ),
            getDueKnowledgeReviews = GetDueKnowledgeReviewsUseCase(
                knowledge,
                GetFocusPlanUseCase(knowledge, lessons),
            ),
            audioPlayer = FakeAudioPlayer(),
            speaker = RecordingSpeaker(),
            isAppInForeground = { true },
        )
        var opened: Pair<String, String>? = null

        compose.setContent {
            AppTheme {
                ReviewScreen(
                    onBrowseVocabulary = {},
                    onGoHome = {},
                    onOpenFocus = { lesson, item -> opened = lesson to item },
                    vm = vm,
                )
            }
        }

        compose.onNodeWithText("مرور هدفمند", substring = true).assertIsDisplayed()
        compose.onNodeWithText("مرور این بخش", substring = true).performScrollTo().performClick()

        assertEquals(lessonId to "grammar.test", opened)
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
