package org.token.english.feature.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppChip
import org.token.english.core.designsystem.component.OutlinedActionButton
import org.token.english.core.designsystem.component.SecondaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.appViewModelFactory
import org.token.english.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onReTakePlacement: () -> Unit,
) {
    val vm: SettingsViewModel = viewModel(factory = appViewModelFactory { SettingsViewModel(it) })
    val state by vm.state.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // API 33+: notifications need an explicit grant, requested the first time
    // the learner switches the reminder on. Denied → the toggle simply stays off.
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            vm.setDailyReminderEnabled(true)
        } else {
            Toast.makeText(
                context,
                "برای دریافت یادآوری، اجازه اعلان‌ها لازم است.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        val settings = state.settings
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("ظاهر")
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        ThemeMode.entries.forEach { mode ->
                            AppChip(
                                label = when (mode) {
                                    ThemeMode.SYSTEM -> "سیستم"
                                    ThemeMode.LIGHT -> "روشن"
                                    ThemeMode.DARK -> "تاریک"
                                },
                                selected = settings?.themeMode == mode,
                                onClick = { vm.setThemeMode(mode) },
                            )
                        }
                    }
                }
            }

            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("هدف روزانه")
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        listOf(10, 15, 20, 30).forEach { minutes ->
                            AppChip(
                                label = "$minutes دقیقه",
                                selected = settings?.dailyGoalMinutes == minutes,
                                onClick = { vm.setDailyGoal(minutes) },
                            )
                        }
                    }
                }
            }

            AppCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        Text("صدای تلفظ", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "پخش صدا با موتور گفتار دستگاه — بدون اینترنت.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = settings?.soundEnabled ?: true,
                        onCheckedChange = vm::setSoundEnabled,
                    )
                }
            }

            AppCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        Text("یادآوری روزانه مرور", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "هر روز ساعت ۱۹ یک یادآوری محلی نشان می‌دهد — بدون اینترنت و سرویس ابری.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = settings?.dailyReminderEnabled ?: false,
                        onCheckedChange = { enable ->
                            when {
                                !enable -> vm.setDailyReminderEnabled(false)
                                Build.VERSION.SDK_INT >= 33 &&
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    ) != PackageManager.PERMISSION_GRANTED -> {
                                    notificationPermissionLauncher.launch(
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    )
                                }
                                else -> vm.setDailyReminderEnabled(true)
                            }
                        },
                    )
                }
            }

            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("سطح یادگیری")
                    SecondaryButton(
                        text = "تعیین مجدد سطح (آزمون)",
                        onClick = onReTakePlacement,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("داده‌ها")
                    Text(
                        text = "همه داده‌ها فقط روی همین دستگاه ذخیره می‌شوند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedActionButton(
                        text = "پاک‌سازی پیشرفت و شروع دوباره",
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("درباره نسخه آفلاین")
                    Text(
                        text = "این نسخه کاملاً آفلاین است: درس‌ها، واژگان و مرور فاصله‌دار بدون اینترنت کار می‌کنند.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "قابلیت‌های زیر به اتصال یا سرویس ابری نیاز دارند و فعلاً غیرفعال‌اند:",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    listOf(
                        "تمرین مکالمه و ارزیابی تلفظ",
                        "همگام‌سازی و پشتیبان‌گیری ابری",
                        "دریافت بسته‌های محتوای جدید",
                    ).forEach { feature ->
                        Text(
                            text = "• $feature",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("پیشرفت پاک شود؟") },
            text = {
                Text("درس‌ها، واژه‌های در حال مرور و آمار یادگیری از دستگاه حذف می‌شوند. این عمل قابل بازگشت نیست.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        vm.resetProgress {}
                    },
                ) { Text("پاک کن") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("انصراف") }
            },
        )
    }
}
