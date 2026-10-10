package org.token.english.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.core.billing.TrialClock
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.SettingsRepository

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings? = null,
    /** Current entitlement so the subscription card can state it plainly. */
    val access: AccessLevel = AccessLevel.TRIAL,
    val trialRemainingMillis: Long = 0L,
    val subscriptionUntil: Long = 0L,
    /** Why access is held — a free companion grant is not a purchase. */
    val accessReason: AccessReason = AccessReason.NONE,
    /** True while the free-access companion app (org.token.zaribar) is installed. */
    val companionInstalled: Boolean = false,
)

/** Settings screen state (checklist B-6): narrow, testable dependencies. */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val knowledgeRepository: KnowledgeRepository,
    private val companionInstalled: StateFlow<Boolean>,
    private val refreshCompanion: suspend () -> Boolean,
    /** Wall-clock tick for the trial countdown; tests pass 0 to keep it still. */
    private val accessTickMillis: Long = 60_000L,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private var trialAndSubscription: TrialAndSubscription? = null
    private var companionIsInstalled: Boolean = false

    init {
        viewModelScope.launch {
            // Free companion grant: re-read the install state, then follow changes.
            refreshCompanion()
            companionInstalled.collect { installed ->
                companionIsInstalled = installed
                recomputeAccess()
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect {
                // Keep the entitlement fields — this collector must not wipe them.
                _state.update { s -> s.copy(isLoading = false, settings = it) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeTrialAndSubscription().collect { ts ->
                trialAndSubscription = ts
                recomputeAccess()
            }
        }
        if (accessTickMillis > 0) {
            viewModelScope.launch {
                // Trial remaining decays with wall time while the flow stays silent —
                // tick so the status line never shows a stale countdown.
                while (true) {
                    delay(accessTickMillis)
                    if (trialAndSubscription != null) recomputeAccess()
                }
            }
        }
    }

    private fun recomputeAccess() {
        val ts = trialAndSubscription ?: return
        val now = System.currentTimeMillis()
        val remaining = TrialClock.remainingMs(
            ts.trialClockState(),
            now,
            android.os.SystemClock.elapsedRealtime(),
        )
        val entitlement = EntitlementPolicy.entitlement(
            now = now,
            trialRemainingMs = remaining,
            subscriptionUntil = ts.subscriptionUntil,
            companionAppInstalled = companionIsInstalled,
        )
        _state.update {
            it.copy(
                access = entitlement.level,
                accessReason = entitlement.reason,
                companionInstalled = companionIsInstalled,
                trialRemainingMillis = remaining,
                subscriptionUntil = ts.subscriptionUntil,
            )
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setDailyGoal(minutes: Int) {
        viewModelScope.launch { settingsRepository.setDailyGoalMinutes(minutes) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEnabled(enabled) }
    }

    /**
     * Persists the reminder flag; scheduling/canceling the alarm follows the
     * flag in EnglishApp's settings collector (single place for both, incl. boot).
     */
    fun setDailyReminderEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDailyReminderEnabled(enabled) }
    }

    fun setLevel(level: LearningLevel) {
        viewModelScope.launch { settingsRepository.setLevel(level) }
    }

    fun resetProgress(onDone: () -> Unit) {
        viewModelScope.launch {
            progressRepository.reset()
            // Knowledge state is learner data and must go with the rest of it.
            knowledgeRepository.reset()
            onDone()
        }
    }
}
