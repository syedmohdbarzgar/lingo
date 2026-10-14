package org.token.english

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.domain.model.LearningLevel
import org.token.english.feature.onboarding.OnboardingViewModel

/**
 * OnboardingViewModel tests (checklist v9 B-6 — it had none). The only decision
 * the screen delegates is "skip the test": it must pin the level to A1 and mark
 * onboarding done, so the placement flow is never shown again.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `skipping without the test starts A1 and finishes onboarding`() {
        // A learner who had drifted to another level still gets A1 on skip, and the
        // first-launch flag must flip or onboarding would reappear.
        val settings = FakeSettingsRepository(
            initial = testSettings().copy(isFirstLaunch = true, level = LearningLevel.B2),
        )
        val vm = OnboardingViewModel(settings)

        var leftOnboarding = false
        vm.skipWithA1 { leftOnboarding = true }

        assertTrue("the caller is told to leave onboarding", leftOnboarding)
        assertEquals("skip pins the level to A1", LearningLevel.A1, settings.settingsFlow.value.level)
        assertFalse("onboarding is not shown again", settings.settingsFlow.value.isFirstLaunch)
    }

    @Test
    fun `skipping on a fresh install reports done once and leaves A1 in place`() {
        // The common path: a brand-new install that skips straight away. The callback
        // fires exactly once (a second call would push the caller off onboarding
        // twice) and A1 must not be disturbed by the pin.
        val settings = FakeSettingsRepository(
            initial = testSettings().copy(isFirstLaunch = true, level = LearningLevel.A1),
        )
        val vm = OnboardingViewModel(settings)

        var completions = 0
        vm.skipWithA1 { completions++ }

        assertEquals("the caller is notified exactly once", 1, completions)
        assertEquals(LearningLevel.A1, settings.settingsFlow.value.level)
        assertFalse(settings.settingsFlow.value.isFirstLaunch)
    }
}
