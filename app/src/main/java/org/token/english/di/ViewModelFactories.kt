package org.token.english.di

import androidx.lifecycle.ViewModelProvider
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
        progressRepository = c.progressRepository,
        reviewRepository = c.reviewRepository,
        studyStats = c.studyStats,
        planner = c.learningPlanner,
        getTodayPlan = c.getTodayPlan,
        getFocusPlan = c.getFocusPlan,
        companionInstalled = c.companionInstalled,
        refreshCompanion = { c.refreshCompanionInstalled() },
    )
}

fun lessonViewModelFactory(
    lessonId: String,
    focusItemId: String? = null,
): ViewModelProvider.Factory = appViewModelFactory { c ->
    LessonViewModel(
        lessonId = lessonId,
        contentSeeded = c.contentSeeded,
        lessonRepository = c.lessonRepository,
        progressRepository = c.progressRepository,
        submitExercise = c.submitExercise,
        completeLesson = c.completeLesson,
        audioPlayer = c.audioPlayer,
        speaker = c,
        isAppInForeground = { c.isAppInForeground },
        getFocusPlan = c.getFocusPlan,
        focusItemId = focusItemId,
    )
}

fun reviewViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    ReviewViewModel(
        reviewRepository = c.reviewRepository,
        vocabularyRepository = c.vocabularyRepository,
        progressRepository = c.progressRepository,
        submitReview = c.submitReview,
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
        companionInstalled = c.companionInstalled,
        refreshCompanion = { c.refreshCompanionInstalled() },
    )
}

fun paywallViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    PaywallViewModel(
        billing = c.billing,
        settingsRepository = c.settingsRepository,
        companionInstalled = c.companionInstalled,
        refreshCompanion = { c.refreshCompanionInstalled() },
        refreshEntitlements = { c.refreshEntitlements() },
    )
}

fun placementViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    PlacementViewModel(
        contentSeeded = c.contentSeeded,
        lessonRepository = c.lessonRepository,
        assessPlacement = c.assessPlacement,
        settingsRepository = c.settingsRepository,
    )
}

fun onboardingViewModelFactory(): ViewModelProvider.Factory = appViewModelFactory { c ->
    OnboardingViewModel(settingsRepository = c.settingsRepository)
}
