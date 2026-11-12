package org.token.english.di

import androidx.lifecycle.ViewModelProvider
import org.token.english.domain.engine.AdaptiveExerciseSelector
import org.token.english.domain.engine.DefaultMasteryProfileEngine
import org.token.english.feature.conversation.ConversationPracticeViewModel
import org.token.english.feature.home.HomeViewModel
import org.token.english.feature.lesson.LessonViewModel
import org.token.english.feature.onboarding.OnboardingViewModel
import org.token.english.feature.onboarding.PlacementViewModel
import org.token.english.feature.paywall.PaywallViewModel
import org.token.english.feature.progress.ProgressViewModel
import org.token.english.feature.review.ReviewViewModel
import org.token.english.feature.settings.SettingsViewModel
import org.token.english.feature.vocabulary.VocabularyDetailViewModel
import org.token.english.feature.vocabulary.VocabularyViewModel

// ---------------------------------------------------------------------------
// ViewModel wiring (checklist B-6).
//
// Every ViewModel takes exactly the repositories, use cases and seams it uses —
// never the container. The container is referenced *only here*, in `di/`, so the
// feature layer has no compile-time dependency on it; a ViewModel can be built in
// a unit test out of fakes without an Android context.
// ---------------------------------------------------------------------------

fun homeViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    HomeViewModel(
        settingsRepository = c.settingsRepository,
        lessonRepository = c.lessonRepository,
        conversationRepository = c.conversationRepository,
        progressRepository = c.progressRepository,
        reviewRepository = c.reviewRepository,
        studyStats = c.studyStats,
        planner = c.learningPlanner,
        getTodayPlan = c.getTodayPlan,
        getFocusPlan = c.getFocusPlan,
        getSkillProfiles = c.getSkillProfiles,
        companionStatus = c.companionStatus,
        refreshCompanion = { c.refreshCompanionInstalled() },
        contentStatus = c.contentStatus,
        contentFailure = c.contentFailure,
        seedContent = { c.seedContent() },
        rebuildDatabase = { c.repairUnreadableDatabase() },
        restartApp = { c.restartProcess() },
    )
}

fun lessonViewModelFactory(
    lessonId: String,
    focusItemId: String? = null,
): ViewModelProvider.Factory = appViewModelFactory { c ->
    LessonViewModel(
        lessonId = lessonId,
        contentStatus = c.contentStatus,
        seedContent = { c.seedContent() },
        lessonRepository = c.lessonRepository,
        progressRepository = c.progressRepository,
        submitExercise = c.submitExercise,
        completeLesson = c.completeLesson,
        audioPlayer = c.audioPlayer,
        speaker = c,
        isAppInForeground = { c.isAppInForeground },
        getFocusPlan = c.getFocusPlan,
        // The lesson's `speaking` item now hears the take too (offline recogniser),
        // so it takes the same three seams the dialogue does — not a recorder.
        speechState = c.speechState,
        startRecognition = { c.startRecognition() },
        stopRecognition = { c.stopRecognition() },
        profileEngine = DefaultMasteryProfileEngine(),
        selector = AdaptiveExerciseSelector(),
        focusItemId = focusItemId,
    )
}

fun reviewViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    ReviewViewModel(
        reviewRepository = c.reviewRepository,
        vocabularyRepository = c.vocabularyRepository,
        progressRepository = c.progressRepository,
        submitReview = c.submitReview,
        getDueKnowledgeReviews = c.getDueKnowledgeReviews,
        audioPlayer = c.audioPlayer,
        speaker = c,
        isAppInForeground = { c.isAppInForeground },
    )
}

fun progressViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    ProgressViewModel(
        settingsRepository = c.settingsRepository,
        lessonRepository = c.lessonRepository,
        progressRepository = c.progressRepository,
        knowledgeRepository = c.knowledgeRepository,
        getSkillProfiles = c.getSkillProfiles,
        getMissProfile = c.getMissProfile,
        studyStats = c.studyStats,
    )
}

fun vocabularyViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    VocabularyViewModel(vocabularyRepository = c.vocabularyRepository)
}

fun vocabularyDetailViewModelFactory(vocabId: String): ViewModelProvider.Factory = appViewModelFactory { c ->
    VocabularyDetailViewModel(
        vocabId = vocabId,
        vocabularyRepository = c.vocabularyRepository,
        reviewRepository = c.reviewRepository,
        speaker = c,
    )
}

fun settingsViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    SettingsViewModel(
        settingsRepository = c.settingsRepository,
        progressRepository = c.progressRepository,
        knowledgeRepository = c.knowledgeRepository,
        conversationRepository = c.conversationRepository,
        voiceNote = c.voiceNote,
        companionStatus = c.companionStatus,
        refreshCompanion = { c.refreshCompanionInstalled() },
    )
}

fun paywallViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    PaywallViewModel(
        billing = c.billing,
        settingsRepository = c.settingsRepository,
        companionStatus = c.companionStatus,
        refreshCompanion = { c.refreshCompanionInstalled() },
        refreshEntitlements = { c.refreshEntitlements() },
    )
}

fun placementViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    PlacementViewModel(
        contentStatus = c.contentStatus,
        seedContent = { c.seedContent() },
        lessonRepository = c.lessonRepository,
        assessPlacement = c.assessPlacement,
        settingsRepository = c.settingsRepository,
    )
}

fun onboardingViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    OnboardingViewModel(settingsRepository = c.settingsRepository)
}

fun conversationPracticeViewModelFactory(
    /** The lesson whose dialogue to run; null runs the one the path owes. */
    lessonId: String? = null,
): ViewModelProvider.Factory = appViewModelFactory { c ->
    ConversationPracticeViewModel(
        lessonRepository = c.lessonRepository,
        lessonId = lessonId,
        conversationRepository = c.conversationRepository,
        recordTurn = c.recordConversationTurn,
        speaker = c,
        contentStatus = c.contentStatus,
        seedContent = { c.seedContent() },
        speechState = c.speechState,
        startRecognition = { c.startRecognition() },
        stopRecognition = { c.stopRecognition() },
    )
}
