package org.token.english.feature.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import org.token.english.BuildConfig
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.TouchTargetMin
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppChip
import org.token.english.core.designsystem.component.OutlinedActionButton
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SecondaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.appViewModelFactory
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onReTakePlacement: () -> Unit,
    onOpenPaywall: () -> Unit,
) {
    val vm: SettingsViewModel = viewModel(factory = appViewModelFactory { SettingsViewModel(it) })
    val state by vm.state.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }
    var infoPage by remember { mutableStateOf<InfoPage?>(null) }
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
            SubscriptionCard(state = state, onOpenPaywall = onOpenPaywall)

            AppCard(Modifier.fillMaxWidth()) {
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

            AppCard(Modifier.fillMaxWidth()) {
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

            AppCard(Modifier.fillMaxWidth()) {
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

            AppCard(Modifier.fillMaxWidth()) {
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

            AppCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("سطح یادگیری")
                    Text(
                        text = "سطح فعلی: ${settings?.level?.name ?: "—"}؛ با تمام‌کردن درس‌های یک سطح، سطح بعدی را انتخاب کنید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        LearningLevel.entries.forEach { lvl ->
                            AppChip(
                                label = lvl.name,
                                selected = settings?.level == lvl,
                                onClick = { vm.setLevel(lvl) },
                            )
                        }
                    }
                    SecondaryButton(
                        text = "تعیین مجدد سطح (آزمون)",
                        onClick = onReTakePlacement,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            AppCard(Modifier.fillMaxWidth()) {
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

            AppCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    SectionHeader("درباره و قوانین")
                    InfoRow("درباره برنامه") { infoPage = InfoPage.ABOUT }
                    InfoRow("حریم خصوصی") { infoPage = InfoPage.PRIVACY }
                    InfoRow("شرایط استفاده") { infoPage = InfoPage.TERMS }
                    InfoRow("ارتباط با ناشر") { infoPage = InfoPage.CONTACT }
                }
            }
        }
    }

    val page = infoPage
    if (page != null) {
        AlertDialog(
            onDismissRequest = { infoPage = null },
            title = { Text(page.title) },
            text = { InfoPageBody(page, onOpenLink = context::openLink) },
            confirmButton = {
                TextButton(onClick = { infoPage = null }) { Text("بستن") }
            },
        )
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

/** The static information pages reachable from settings (offline, no server). */
private enum class InfoPage(val title: String) {
    ABOUT("درباره برنامه"),
    PRIVACY("حریم خصوصی"),
    TERMS("شرایط استفاده"),
    CONTACT("ارتباط با ناشر"),
}

/** The publisher's public support channel (the project issue tracker). */
private const val SUPPORT_URL = "https://github.com/syedmohdbarzgar/lingo/issues"

/**
 * States the learner's access in plain words (trial countdown / active
 * subscription / locked) and is always a doorway to the paywall — a locked
 * learner lands here from the paywall, so it must offer the buy path back.
 */
@Composable
private fun SubscriptionCard(state: SettingsUiState, onOpenPaywall: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("اشتراک")
            val hours = state.trialRemainingMillis / (60 * 60 * 1000)
            val days = state.trialRemainingMillis / (24 * 60 * 60 * 1000)
            val subDays =
                (state.subscriptionUntil - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
            Text(
                text = when (state.access) {
                    org.token.english.core.billing.AccessLevel.TRIAL -> when {
                        days >= 1 -> "دوره آزمایشی رایگان — $days روز و ${hours % 24} ساعت باقی‌مانده."
                        hours >= 1 -> "دوره آزمایشی رایگان — کمتر از $hours ساعت دیگر."
                        else -> "دوره آزمایشی رو به پایان است."
                    }

                    org.token.english.core.billing.AccessLevel.PREMIUM ->
                        if (subDays >= 1) "اشتراک فعال — $subDays روز دیگر تمدید می‌شود."
                        else "اشتراک شما فعال است."

                    org.token.english.core.billing.AccessLevel.LOCKED ->
                        "اشتراکی فعال نیست. برای ادامه یادگیری، اشتراک بخرید یا خرید قبلی را بازیابی کنید."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.access == org.token.english.core.billing.AccessLevel.PREMIUM) {
                SecondaryButton(
                    text = "مدیریت اشتراک",
                    onClick = onOpenPaywall,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                PrimaryButton(
                    text = "خرید اشتراک",
                    onClick = onOpenPaywall,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = TouchTargetMin)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Icon(
            // ArrowForward auto-mirrors in RTL → points left (‹), the Persian
            // "go deeper" direction; the old KeyboardArrowLeft mirrored
            // to › and read as reversed (user report, Oct 2026).
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoPageBody(page: InfoPage, onOpenLink: (String) -> Unit) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        when (page) {
            InfoPage.ABOUT -> {
                Text(
                    text = "درس‌ها، واژگان و مرور فاصله‌دار کامل روی دستگاه ذخیره شده‌اند و بدون اینترنت هم کار می‌کنند.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "نسخه ${BuildConfig.VERSION_NAME} — ${BuildConfig.STORE_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "قابلیت‌های زیر به اتصال یا سرویس ابری نیاز دارند و فعلاً غیرفعال‌اند:",
                    style = MaterialTheme.typography.bodySmall,
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

            InfoPage.PRIVACY -> {
                Text(
                    text = "همه داده‌های یادگیری (پیشرفت، واژگان در حال مرور و آمار) فقط روی همین دستگاه ذخیره می‌شوند.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "برنامه حساب کاربری، تحلیل رفتار و ردیابی ندارد و هیچ اطلاعات یادگیری به سرور فرستاده نمی‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "پرداخت اشتراک از طریق فروشگاه ${BuildConfig.STORE_NAME} انجام می‌شود؛ اطلاعات پرداخت هرگز در اختیار برنامه نیست.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            InfoPage.TERMS -> {
                Text(
                    text = "این برنامه برای تمرین شخصی زبان انگلیسی ارائه شده است.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "دسترسی به محتوا با دوره آزمایشی رایگان یا اشتراک فعال می‌شود. اشتراک به حساب فروشگاه شما گره خورده و مدیریت آن از همان‌جا انجام می‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "محتوای آموزشی بدون تضمین نتیجه ارائه می‌شود و ممکن است در نسخه‌های بعدی بهبود یابد.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            InfoPage.CONTACT -> {
                Text(
                    text = "برای گزارش خطا، پیشنهاد یا درخواست پشتیبانی با ناشر در تماس باشید.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedActionButton(
                    text = "صفحه پشتیبانی",
                    onClick = { onOpenLink(SUPPORT_URL) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** Opens an external link in the user's browser; a no-op when no handler exists. */
private fun Context.openLink(url: String) {
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}
