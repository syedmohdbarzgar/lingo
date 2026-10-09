package org.token.english

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.token.english.di.AppContainer
import org.token.english.reminder.DailyReminder

class EnglishApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val container: AppContainer by lazy { AppContainer(this, appScope) }

    /** Started-activity count — study time only accrues while > 0 (checklist P7). */
    private var startedActivities = 0
    val isAppInForeground: Boolean get() = startedActivities > 0

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                startedActivities++
            }

            override fun onActivityStopped(activity: Activity) {
                startedActivities = (startedActivities - 1).coerceAtLeast(0)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityResumed(activity: Activity) {
                // The free-access companion (org.token.zaribar) can be installed or
                // removed between sessions; the grant must follow the install, not
                // wait for the next cold start.
                appScope.launch { container.refreshCompanionInstalled() }
            }

            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })

        // Offline content bundle → Room, in the background. Screens that need
        // content wait on container.contentSeeded instead of racing the seeder.
        appScope.launch {
            try {
                container.contentSeeder.ensureSeeded()
            } catch (t: Throwable) {
                Log.e("EnglishApp", "content seeding failed", t)
            } finally {
                container.markContentSeeded()
            }
        }
        // Keep the hot sound flag in sync for the (non-suspend) audio helper,
        // and (un)schedule the daily reminder from its persisted flag — one
        // place for both normal starts and post-reboot re-application.
        appScope.launch {
            container.settingsRepository.settings.collect {
                container.soundEnabled = it.soundEnabled
                if (it.dailyReminderEnabled) {
                    DailyReminder.schedule(this@EnglishApp)
                } else {
                    DailyReminder.cancel(this@EnglishApp)
                }
            }
        }
        // Trial + store subscription check (payment is the only online surface),
        // then keep the trial checkpoint advancing with real elapsed time.
        appScope.launch {
            container.refreshCompanionInstalled()
            container.refreshEntitlements()
            while (true) {
                delay(TRIAL_CHECKPOINT_MS)
                container.persistTrialProgress()
            }
        }
    }

    private companion object {
        const val TRIAL_CHECKPOINT_MS = 60_000L
    }
}
