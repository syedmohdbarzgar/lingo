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
import java.util.Calendar

/**
 * Daily review reminder (checklist P9) — on-device, no services, no network:
 * an inexact daily AlarmManager reminder at 19:00 shows a local notification.
 * Scheduling is persisted through the settings flag and re-applied after a
 * reboot (alarms do not survive one). POST_NOTIFICATIONS is requested in the
 * UI when the learner switches the toggle on (API 33+).
 */
object DailyReminder {
    const val CHANNEL_ID = "daily_reminder"
    const val REMINDER_HOUR = 19
    private const val REQUEST_CODE = 1001

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextReminderMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent(context),
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pendingIntent(context))
    }

    /** Next 19:00 local time (tomorrow if today's has passed). */
    fun nextReminderMillis(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
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
                    // Alarms die with the reboot — restore only if the learner enabled it.
                    if (reminderEnabled(appContext)) DailyReminder.schedule(appContext)
                } else {
                    showNotification(appContext)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    /** Settings read behind the boot handler; a failure just means "do not schedule". */
    private suspend fun reminderEnabled(context: Context): Boolean = try {
        val app = context.applicationContext as? EnglishApp ?: return false
        app.container.settingsRepository.settings.first().dailyReminderEnabled
    } catch (t: Throwable) {
        false
    }

    private fun showNotification(context: Context) {
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
            .setContentTitle("مرور امروز آماده است")
            .setContentText("چند دقیقه‌ای سر واژه‌ها بنشین تا یادگیری‌ات تازه بماند.")
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        NotificationManagerCompat.from(context).notify(DAILY_REMINDER_NOTIFICATION_ID, notification)
    }

    private companion object {
        const val DAILY_REMINDER_NOTIFICATION_ID = 42
    }
}
