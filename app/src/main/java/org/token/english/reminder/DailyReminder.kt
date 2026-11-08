package org.token.english.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.token.english.EnglishApp
import org.token.english.R
import org.token.english.core.common.persianMeasure
import org.token.english.domain.model.AppSettings
import java.util.Calendar

/**
 * Daily review reminder (checklist P9) — on-device, no services, no network:
 * an inexact daily AlarmManager reminder at the learner's chosen hour shows a
 * local notification. Scheduling is persisted through the settings record and
 * re-applied after a reboot (alarms do not survive one). POST_NOTIFICATIONS is
 * requested in the UI when the learner switches the toggle on (API 33+).
 *
 * The hour is a setting rather than a constant since B-4: 19:00 is only the
 * shipped default ([AppSettings.DEFAULT_REMINDER_HOUR]), and a learner whose day
 * ends at 23:00 was otherwise told to revise at the wrong time — or turned the
 * reminder off, which is the outcome a reminder must not produce.
 */
/**
 * What the reminder says, given what is actually waiting for the learner.
 *
 * The notification used to announce «مرور امروز آماده است» unconditionally — a
 * claim nothing had checked, on the one screen the app can show without the
 * learner asking for it. [copyFor] is the whole rule, pure and testable: a due
 * count above zero says so and says how many, zero says there is nothing to
 * review (and suggests, rather than promises, a lesson), and an unreadable count
 * says neither — a failure must not be papered over with a reassuring sentence.
 */
data class ReminderCopy(val title: String, val body: String)

object DailyReminder {
    const val CHANNEL_ID = "daily_reminder"

    /** Kept for callers that want the shipped default without the model. */
    const val REMINDER_HOUR = AppSettings.DEFAULT_REMINDER_HOUR
    private const val REQUEST_CODE = 1001

    /** Schedules the daily alarm at [hour] (0–23 local, clamped). */
    fun schedule(context: Context, hour: Int = REMINDER_HOUR) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextReminderMillis(hour),
            AlarmManager.INTERVAL_DAY,
            pendingIntent(context),
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pendingIntent(context))
    }

    /**
     * The notification's words for [dueCount] due cards; `null` means the count
     * could not be read. Persian, local-only, and never a promise the data does not
     * back up.
     */
    fun copyFor(dueCount: Int?): ReminderCopy = when {
        dueCount == null -> ReminderCopy(
            title = "یادآوری مطالعهٔ امروز",
            body = "چند دقیقه‌ای برای انگلیسی وقت بگذار — همه‌چیز روی همین گوشی است.",
        )
        dueCount > 0 -> ReminderCopy(
            title = "مرور امروز آماده است",
            body = "${persianMeasure(dueCount, "کارت")} برای مرور آماده است؛ چند دقیقه‌ای " +
                "سرشان بنشین تا یادگیری‌ات تازه بماند.",
        )
        // Nothing due: say that, and suggest the next door instead of promising one.
        else -> ReminderCopy(
            title = "امروز مروری نداری",
            body = "کارت‌هایت سر وقت خودشان می‌رسند؛ اگر دوست داشتی، یک درس تازه یا یک " +
                "گفتگو را تمرین کن.",
        )
    }

    /** Next [hour]:00 local time (tomorrow if today's has passed). */
    fun nextReminderMillis(hour: Int = REMINDER_HOUR): Long = Calendar.getInstance().apply {
        // Clamped, not trusted: an out-of-range hour would be rolled into another
        // day by Calendar and the rebooting alarm would fire at a time nobody chose.
        set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
    }.timeInMillis

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

class DailyReminderReceiver : BroadcastReceiver() {

    /**
     * Never `runBlocking` here (checklist B-4): the broadcast runs on the main
     * thread and blocking it to read DataStore can trip an ANR. `goAsync()` hands
     * the receiver a limited window instead, and `finish()` releases it in every
     * path (including failure) so the system never keeps the process alive.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                    // Alarms die with the reboot — restore it at the learner's own
                    // hour, not the default one, if they enabled it.
                    reminderHour(appContext)?.let { DailyReminder.schedule(appContext, it) }
                } else {
                    // What is due decides what the notification may claim.
                    showNotification(appContext, copy = DailyReminder.copyFor(dueCount(appContext)))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * Due cards right now, or null when the count cannot be read. A notification
     * that guesses is worse than one that stays general.
     */
    private suspend fun dueCount(context: Context): Int? = try {
        val app = context.applicationContext as? EnglishApp ?: return null
        app.container.reviewRepository.countDue(System.currentTimeMillis())
    } catch (t: Throwable) {
        null
    }

    /**
     * Settings read behind the boot handler: the hour to schedule at, or null when
     * the reminder is off. A failure means "do not schedule" — the learner who
     * turned it on is not punished for a transient read error.
     */
    private suspend fun reminderHour(context: Context): Int? = try {
        val app = context.applicationContext as? EnglishApp ?: return null
        val settings = app.container.settingsRepository.settings.first()
        if (settings.dailyReminderEnabled) settings.dailyReminderHour else null
    } catch (t: Throwable) {
        null
    }

    private fun showNotification(context: Context, copy: ReminderCopy) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    DailyReminder.CHANNEL_ID,
                    "یادآوری روزانه",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ),
            )
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, org.token.english.MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, DailyReminder.CHANNEL_ID)
            // Monochrome status-bar icon (B-4) — the launcher icon renders as a blob.
            .setSmallIcon(R.drawable.ic_stat_reminder)
            .setContentTitle(copy.title)
            .setContentText(copy.body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        NotificationManagerCompat.from(context).notify(DAILY_REMINDER_NOTIFICATION_ID, notification)
    }

    private companion object {
        const val DAILY_REMINDER_NOTIFICATION_ID = 42
    }
}
