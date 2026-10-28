package org.token.english.feature.conversation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.R
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.CorrectBanner
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.InfoBanner
import org.token.english.core.designsystem.component.IncorrectBanner
import org.token.english.core.designsystem.component.OutlinedActionButton
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SecondaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.conversationPracticeViewModelFactory
import org.token.english.domain.engine.SpeakingVerdict

/**
 * Conversation practice (checklist: on-device recogniser, no server).
 *
 * Layout is one card per take — cue, target, listen, say it, verdict — because a
 * practice loop is a single question at a time; a list would invite skipping.
 * Every English string goes through [EnglishText] (forced LTR inside the RTL
 * root) and every verdict is icon **and** text, never colour alone (design.md §27).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationPracticeScreen(
    vm: ConversationPracticeViewModel = viewModel(factory = conversationPracticeViewModelFactory()),
    onBack: () -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var permissionRefused by remember { mutableStateOf(false) }

    // The mic prompt lives on this screen: nothing else here touches the mic, so
    // there is no global prompt and a refusal can be explained in place.
    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionRefused = !granted
        if (granted) vm.onEvent(ConversationEvent.StartTake)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تمرین مکالمه") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "بازگشت",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            when {
                state.isLoading -> Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.lg),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                state.isEmpty -> AppEmptyState(
                    icon = Icons.Outlined.MicNone,
                    title = "هنوز جملهٔ تمرینی نداریم",
                    subtitle = "این تمرین از جمله‌های درس‌ها می‌آید؛ وقتی درس‌های گفتاری اضافه شد، اینجا پر می‌شود.",
                )

                state.isFinished -> FinishedCard(onBack = onBack)

                else -> state.current?.let { item ->
                    Text(
                        text = "جملهٔ ${state.progressLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TakeCard(
                        state = state,
                        item = item,
                        permissionRefused = permissionRefused,
                        onStart = {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO,
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                permissionRefused = false
                                vm.onEvent(ConversationEvent.StartTake)
                            } else {
                                micPermission.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onEvent = vm::onEvent,
                    )
                }
            }
        }
    }
}

@Composable
private fun TakeCard(
    state: ConversationUiState,
    item: PracticeItem,
    permissionRefused: Boolean,
    onStart: () -> Unit,
    onEvent: (ConversationEvent) -> Unit,
) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("این جمله را بگو")

            Text(
                text = item.promptFa,
                style = MaterialTheme.typography.bodyMedium,
            )

            // English target: LTR inside the forced-RTL root.
            EnglishText(
                text = item.target,
                style = MaterialTheme.typography.titleLarge,
            )

            SecondaryButton(
                text = "شنیدن جمله",
                onClick = { onEvent(ConversationEvent.SpeakModel) },
                modifier = Modifier.fillMaxWidth(),
            )

            when {
                state.isPreparing -> PreparingCard()

                state.error != SpeechError.NONE -> ErrorCard(state.error)

                state.isListening -> ListeningCard(partial = state.partial)

                state.finalText.isNotBlank() && state.verdict != null -> {
                    // Safe: the branch condition guarantees both are non-null.
                    val verdict = state.verdict
                    if (verdict != null) {
                        ResultCard(
                            verdict = verdict,
                            spoken = state.finalText,
                            target = item.target,
                        )
                    }
                }
            }

            if (permissionRefused && state.error == SpeechError.NONE) {
                IncorrectBanner(
                    message = "برای تمرین به میکروفون نیاز داریم. دسترسی را از تنظیمات برنامه بدهید تا بتوانید بگویید.",
                )
            }

            // The mic button is the one action that changes with state; a second
            // button would make the learner choose rather than just speak.
            when {
                state.isListening -> OutlinedActionButton(
                    text = "پایان ضبط",
                    onClick = { onEvent(ConversationEvent.StopTake) },
                    modifier = Modifier.fillMaxWidth(),
                )

                state.isPreparing -> OutlinedActionButton(
                    text = "در حال آماده‌سازی…",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                )

                else -> PrimaryButton(
                    text = if (state.finalText.isBlank()) "شروع ضبط" else "ضبط دوباره",
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Graded and not correct → the retry path is the point of the screen.
            if (state.verdict != null && state.verdict != SpeakingVerdict.CORRECT) {
                OutlinedActionButton(
                    text = "تلاش دوباره",
                    onClick = { onEvent(ConversationEvent.Retry) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (state.verdict == SpeakingVerdict.CORRECT) {
                PrimaryButton(
                    text = "جملهٔ بعدی",
                    onClick = { onEvent(ConversationEvent.Next) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun PreparingCard() {
    Row {
        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        Text(
            text = "در حال آمادهٔ مدل گفتار… بار اول چند ثانیه طول می‌کشد.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = AppSpacing.sm),
        )
    }
}

@Composable
private fun ListeningCard(partial: String) {
    AppCard(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.MicNone, contentDescription = null, Modifier.size(20.dp))
                Text(
                    text = "در حال گوش دادن…",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = AppSpacing.xs),
                )
            }
            // Live partial — English, so LTR.
            EnglishText(
                text = partial.ifBlank { "…" },
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun ResultCard(verdict: SpeakingVerdict, spoken: String, target: String) {
    when (verdict) {
        SpeakingVerdict.CORRECT -> CorrectBanner(
            message = "درست گفتی! جمله را کامل و درست بیان کردی.",
        )

        SpeakingVerdict.NEAR -> InfoBanner(
            message = "نزدیک بود! چیزی که گفتی:",
        )

        SpeakingVerdict.RETRY -> IncorrectBanner(
            message = "تلاش دوباره: جملهٔ شنیده‌شده با جملهٔ هدف یکی نیست.",
        )
    }

    // Show both sides after any non-perfect take: the learner needs to see the
    // gap, not just the verdict. Correct keeps it short — the praise is the point.
    if (verdict != SpeakingVerdict.CORRECT) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Text(
                text = "جملهٔ هدف:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            EnglishText(text = target, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "چیزی که گفتی:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            EnglishText(
                text = spoken.ifBlank { "—" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorCard(error: SpeechError) {
    val (title, subtitle) = when (error) {
        SpeechError.NO_MICROPHONE -> "میکروفون در دسترس نیست" to
            "این دستگاه میکروفون ندارد یا میکروفون را برنامهٔ دیگری گرفته است. این تمرین بدون میکروفون ممکن نیست."
        SpeechError.PERMISSION_DENIED -> "دسترسی به میکروفون داده نشد" to
            "برای گفتن جمله به میکروفون نیاز داریم. از تنظیمات برنامه، مجوز میکروفون را فعال کنید."

        SpeechError.MODEL_MISSING -> "مدل گفتار پیدا نشد" to
            "مدل آفلاین تشخیص گفتار در برنامه نیست. نصب را با `scripts/fetch_vosk_model.sh` آماده و دوباره بسازید."

        SpeechError.ASSET_COPY_FAILED -> "آماده‌سازی مدل ناموفق بود" to
            "فضای کافی برای کپی مدل وجود ندارد یا کپی ناتمام مانده است. فضای دستگاه را آزاد کنید و دوباره تلاش کنید."

        SpeechError.INTERRUPTED -> "ضبط قطع شد" to
            "ضبط صدا وسط کار از کار افتاد. دوباره شروع کنید."

        SpeechError.NONE -> "" to ""
    }
    AppCard(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.MicNone, contentDescription = null, Modifier.size(20.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = AppSpacing.xs),
                )
            }
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun FinishedCard(onBack: () -> Unit) {
    AppEmptyState(
        icon = Icons.Outlined.MicNone,
        title = "تمرین امروز تمام شد",
        subtitle = "همهٔ جمله‌های گفتاری را گفتی. فردا دوباره تمرین کن تا ماندگار شود.",
        actionText = "بازگشت به خانه",
        onAction = onBack,
    )
}
