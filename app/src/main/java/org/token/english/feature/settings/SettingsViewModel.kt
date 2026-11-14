package org.token.english.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.audio.VoiceNote
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.CompanionStatus
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.core.common.catchLoadFailure
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.SpeechPace
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.repository.ConversationRepository
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.SettingsRepository

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: AppSettings? = null,
    /** Current entitlement so the subscription card can state it plainly. */
    val access: AccessLevel = AccessLevel.LOCKED,
    val subscriptionUntil: Long = 0L,
    /** Why access is held — a free companion grant is not a purchase. */
    val accessReason: AccessReason = AccessReason.NONE,
    /** What the companion lookup found — only TRUSTED earns the free grant (N-3). */
    val companionStatus: CompanionStatus = CompanionStatus.ABSENT,
    /** A read threw — the screen says so instead of showing default switches. */
    val loadFailed: Boolean = false,
)

/** Settings screen state (checklist B-6): narrow, testable dependencies. */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val knowledgeRepository: KnowledgeRepository,
    /**
     * The finished dialogues — learner data outside Room, so only this screen's
     * reset can clear it (see [ConversationRepository.reset]).
     */
    private val conversationRepository: ConversationRepository,
    /**
     * The recorded take, deleted with the rest of the learner's data: the privacy
     * page promises the reset removes it, and a voice recording is the most
     * sensitive thing the app stores.
     */
    private val voiceNote: VoiceNote,
    private val companionStatus: StateFlow<CompanionStatus>,
    private val refreshCompanion: suspend () -> Boolean,
    /** Entitlement re-check tick (a subscription can lapse mid-session); tests pass 0. */
    private val accessTickMillis: Long = 60_000L,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private var trialAndSubscription: TrialAndSubscription? = null
    private var companion: CompanionStatus = CompanionStatus.ABSENT

    /** Declared before `init` — Kotlin initialises properties in source order. */
    private var loadJobs: List<Job> = emptyList()

    init {
        startLoading()
        if (accessTickMillis > 0) {
            viewModelScope.launch {
                // The flow only emits on writes, so an expired subscription would
                // otherwise keep the status line saying "active" until a restart.
                while (true) {
                    delay(accessTickMillis)
                    if (trialAndSubscription != null) recomputeAccess()
                }
            }
        }
    }

    /**
     * Starts the reads; [retry] re-runs this. Guarded, because an unguarded throw
     * left the screen showing default switches (sound on, reminder off) as if they
     * were the stored values — a wrong answer presented as fact.
     */
    private fun startLoading() {
        loadJobs.forEach { it.cancel() }
        loadJobs = listOf(
            viewModelScope.launch {
                // Free companion grant: re-read the install state, then follow changes.
                refreshCompanion()
                companionStatus.catchLoadFailure(::onLoadFailure).collect { status ->
                    companion = status
                    recomputeAccess()
                }
            },
            viewModelScope.launch {
                settingsRepository.settings.catchLoadFailure(::onLoadFailure).collect {
                    // Keep the entitlement fields — this collector must not wipe them.
                    _state.update { s -> s.copy(isLoading = false, loadFailed = false, settings = it) }
                }
            },
            viewModelScope.launch {
                settingsRepository.observeTrialAndSubscription().catchLoadFailure(::onLoadFailure).collect { ts ->
                    trialAndSubscription = ts
                    recomputeAccess()
                }
            },
        )
    }

    private fun onLoadFailure(error: Throwable) {
        android.util.Log.w("SettingsViewModel", "settings read failed", error)
        _state.update { it.copy(isLoading = false, loadFailed = true) }
    }

    /** The error state's action: re-read settings (they live on this device). */
    fun retry() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        startLoading()
    }

    private fun recomputeAccess() {
        val ts = trialAndSubscription ?: return
        val entitlement = EntitlementPolicy.entitlement(
            now = System.currentTimeMillis(),
            subscriptionUntil = ts.subscriptionUntil,
            companionAppInstalled = companion == CompanionStatus.TRUSTED,
        )
        _state.update {
            it.copy(
                access = entitlement.level,
                accessReason = entitlement.reason,
                companionStatus = companion,
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

    /**
     * Persists the speaking pace. The player follows the record: `EnglishApp`'s
     * settings collector applies it, so the next sentence is already at the new
     * speed instead of waiting for a relaunch.
     */
    fun setSpeechPace(pace: SpeechPace) {
        viewModelScope.launch { settingsRepository.setSpeechPace(pace) }
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

    /**
     * Persists the reminder hour (checklist B-4). The alarm follows the record:
     * EnglishApp's collector reschedules on every settings emission, so the same
     * change that moves the stored hour moves the notification.
     */
    fun setDailyReminderHour(hour: Int) {
        viewModelScope.launch { settingsRepository.setDailyReminderHour(hour) }
    }

    fun setLevel(level: LearningLevel) {
        viewModelScope.launch { settingsRepository.setLevel(level) }
    }

    fun resetProgress(onDone: () -> Unit) {
        viewModelScope.launch {
            // Guarded so a failed wipe cannot kill the coroutine silently; a failure
            // simply does not call back as success.
            val reset = runCatchingCancellable {
                progressRepository.reset()
                // Knowledge state is learner data and must go with the rest of it.
                knowledgeRepository.reset()
                // Neither of these lives in Room, so the two calls above left them
                // behind: the finished dialogues stayed marked (the lesson gate kept
                // every lesson they unlocked open after the wipe) and the recorded
                // take stayed in the cache dir.
                conversationRepository.reset()
                voiceNote.discardTake()
            }
            if (reset.isSuccess) onDone()
        }
    }
}
