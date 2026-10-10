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
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.model.ThemeMode
import org.token.english.feature.settings.SettingsViewModel

/**
 * SettingsViewModel tests (checklist v8 item 3): the learner-facing switches
 * (reminder, theme, goal, sound, level), the destructive reset, and the access
 * status the screen reports — all on in-memory fakes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Harness(companion: Boolean = false) {
        val settings = FakeSettingsRepository()
        val progress = FakeProgressRepository()
        val knowledge = FakeKnowledgeRepository()
        val companionFlow = MutableStateFlow(companion)

        fun build(): SettingsViewModel = SettingsViewModel(
            settingsRepository = settings,
            progressRepository = progress,
            knowledgeRepository = knowledge,
            companionInstalled = companionFlow,
            refreshCompanion = { companionFlow.value },
            accessTickMillis = 0,
        )
    }

    @Test
    fun `the reminder toggle is persisted and reaches the screen`() {
        val h = Harness()
        val vm = h.build()
        assertFalse("off until the learner turns it on", vm.state.value.settings!!.dailyReminderEnabled)

        vm.setDailyReminderEnabled(true)

        assertTrue("persisted", h.settings.settingsFlow.value.dailyReminderEnabled)
        assertTrue("and rendered", vm.state.value.settings!!.dailyReminderEnabled)
    }

    @Test
    fun `theme, goal, sound and level each reach the repository`() {
        val h = Harness()
        val vm = h.build()

        vm.setThemeMode(ThemeMode.DARK)
        vm.setDailyGoal(minutes = 25)
        vm.setSoundEnabled(false)
        vm.setLevel(LearningLevel.B1)

        val stored = h.settings.settingsFlow.value
        assertEquals(ThemeMode.DARK, stored.themeMode)
        assertEquals(25, stored.dailyGoalMinutes)
        assertFalse(stored.soundEnabled)
        assertEquals(LearningLevel.B1, stored.level)
        assertEquals("the screen follows the same value", ThemeMode.DARK, vm.state.value.settings!!.themeMode)
    }

    @Test
    fun `resetting progress clears mastery, knowledge state and reports back`() {
        val h = Harness()
        h.progress.masteryFlow.value = mapOf(Skill.GRAMMAR to 0.7f)
        h.knowledge.statesFlow.value = listOf(
            KnowledgeState(
                itemId = "grammar.test", mastery = 0.7f, exposureCount = 4,
                consecutiveCorrect = 2, consecutiveIncorrect = 0, intervalDays = 3,
                easeFactor = 2.5f, repetitions = 2, lapses = 0, lastReviewedAt = 1L, nextReviewAt = 2L,
            ),
        )
        val vm = h.build()
        var done = false

        vm.resetProgress { done = true }

        assertTrue("the learner is told when it is over", done)
        assertTrue("skill mastery is gone", h.progress.masteryFlow.value.isEmpty())
        assertTrue("the learner model goes with it", h.knowledge.statesFlow.value.isEmpty())
    }

    @Test
    fun `the companion install is reported as a free grant, not a purchase`() {
        val h = Harness(companion = true)
        val vm = h.build()

        assertEquals(AccessLevel.PREMIUM, vm.state.value.access)
        assertEquals(AccessReason.COMPANION_APP, vm.state.value.accessReason)
        assertTrue(vm.state.value.companionInstalled)
    }

    @Test
    fun `a settings change does not wipe the access status`() {
        // The settings collector must update the settings fields only: a regression
        // here would make the subscription card flip back to "trial" on any toggle.
        val h = Harness(companion = true)
        val vm = h.build()
        assertEquals(AccessLevel.PREMIUM, vm.state.value.access)

        vm.setSoundEnabled(false)
        vm.setDailyGoal(minutes = 30)

        assertEquals("access survives a settings write", AccessLevel.PREMIUM, vm.state.value.access)
        assertEquals(AccessReason.COMPANION_APP, vm.state.value.accessReason)
        assertTrue(vm.state.value.companionInstalled)
    }
}
