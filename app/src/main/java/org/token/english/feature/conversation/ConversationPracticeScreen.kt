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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CorrectBanner
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.IncorrectBanner
import org.token.english.core.designsystem.component.InfoBanner
import org.token.english.core.designsystem.component.OutlinedActionButton
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.ProgressRing
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.StaggeredReveal
import org.token.english.di.conversationPracticeViewModelFactory
import org.token.english.domain.engine.SpeakingVerdict

/**
 * Conversation practice: an actual two-party exchange, not a repetition drill.
 *
 * The device (the partner) asks its question aloud; the learner answers into the
 * microphone; the reply is graded and the partner moves on. The transcript above
 * the live turn is what makes it read as a conversation rather than a form.
 *
 * Layout rules kept from the rest of the app: every English line goes through
 * [EnglishText] (forced LTR inside the RTL root), and every verdict is icon
 * **and** text — never colour alone (design.md §27).
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
    val scroll = rememberScrollState()

    // The mic prompt lives on this screen: nothing else here touches the mic, so
    // there is no global prompt and a refusal can be explained in place.
    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionRefused = !granted
        if (granted) vm.onEvent(ConversationEvent.StartTake)
    }

    // A conversation scrolls: the newest turn must be the one in view, or the
    // learner answers a question they can no longer see.
    LaunchedEffect(state.index, state.verdict, state.unheard, state.isListening) {
        scroll.animateScrollTo(scroll.maxValue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تمرین مکالمه") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            // Auto-mirrored: inside the forced RTL layout the plain
                            // icon points left, i.e. forward in reading order. This
                            // one mirrors to point right, the way back.
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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
                .verticalScroll(scroll)
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
                    title = "هنوز گفتگویی برای تمرین نداریم",
                    subtitle = "این تمرین از گفتگوهای درس‌ها ساخته می‌شود؛ وقتی درس‌های گفتاری اضافه شوند، اینجا پر می‌شود.",
                )

                state.isFinished -> SessionSummary(
                    state = state,
                    onRestart = { vm.onEvent(ConversationEvent.Restart) },
                    onBack = onBack,
                )

                else -> {
                    val turn = state.current
                    if (turn != null) {
                        TurnHeader(
                            turn = turn,
                            progressLabel = state.progressLabel,
                            progress = state.progress,
                        )

                        if (state.history.isNotEmpty()) {
                            TranscriptCard(history = state.history)
                        }

                        // A new exchange fades in instead of replacing the previous
                        // one in the same frame: the turn changing is the one moment
                        // this screen moves, and it should be readable as a change.
                        key(state.index) {
                            StaggeredReveal(index = 0) {
                                PartnerTurn(
                                    turn = turn,
                                    replayEnabled = !state.isListening && !state.isPartnerSpeaking,
                                    onReplay = { vm.onEvent(ConversationEvent.SpeakPartner) },
                                )
                            }
                        }

                        if (state.hasTake || state.isListening) {
                            LearnerTake(state = state)
                        }

                        Outcome(state = state, turn = turn, permissionRefused = permissionRefused)

                        AnswerControls(
                            state = state,
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
}

/**
 * Where the learner is in the conversation: the lesson it belongs to, the
 * exchange number **as a bar** (not only as "۳ / ۱۲" — the number alone says how
 * much is left only after the learner does the arithmetic), and the count.
 */
@Composable
private fun TurnHeader(turn: DialogueTurn, progressLabel: String, progress: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = turn.lessonTitleFa,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = progressLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AppLinearProgress(progress = progress, contentDescription = "پیشرفت گفتگو")
    }
}

/** What has already been said — read top to bottom, it is the conversation. */
@Composable
private fun TranscriptCard(history: List<AnsweredTurn>) {
    AppCard(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        SectionHeader("گفتگو تا اینجا")
        history.forEachIndexed { i, answered ->
            if (i > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = AppSpacing.xs),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                answered.turn.partnerLine?.let { line ->
                    Row(verticalAlignment = Alignment.Top) {
                        VerdictMark(verdict = answered.verdict)
                        EnglishText(
                            text = line,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = AppSpacing.sm),
                        )
                    }
                }
                EnglishText(
                    text = answered.said.ifBlank { "—" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = AppSpacing.lg),
                )
            }
        }
    }
}

/** The partner's current turn: English line, its Persian gloss, and a replay. */
@Composable
private fun PartnerTurn(
    turn: DialogueTurn,
    replayEnabled: Boolean,
    onReplay: () -> Unit,
) {
    // Neutral surface, not a tint: the learner's own take below is the accent
    // card, and two peach cards stacked read as one block — you could not tell
    // who said what. The partner is the page, the learner is the highlight.
    AppCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (turn.partnerLine != null) "پارتنر می‌پرسد" else "این جمله را بگو",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            IconButton(onClick = onReplay, enabled = replayEnabled) {
                Icon(
                    imageVector = Icons.Outlined.VolumeUp,
                    contentDescription = "شنیدن دوباره",
                )
            }
        }
        EnglishText(
            text = turn.partnerLine ?: turn.answer,
            style = MaterialTheme.typography.titleLarge,
        )
        turn.partnerLineFa?.let { gloss ->
            Text(
                text = gloss,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = AppSpacing.xs),
            )
        }
        Text(
            text = turn.taskFa,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = AppSpacing.sm),
        )
    }
}

/**
 * The learner's own words: live while speaking, and the whole take afterwards.
 *
 * Read-only on purpose. The transcript is evidence of what the recogniser heard,
 * not an input field — editing it here only scored a text the learner typed, and
 * the two extra buttons competed with the one action that matters (answer again).
 */
@Composable
private fun LearnerTake(state: ConversationUiState) {
    AppCard(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.MicNone, contentDescription = null, Modifier.size(20.dp))
            Text(
                text = if (state.isListening) "شما (در حال صحبت…)" else "شما گفتید",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = AppSpacing.xs),
            )
        }

        // The whole take, not the last fragment: a pause mid-sentence continues
        // the same transcript instead of restarting it.
        EnglishText(
            text = state.liveTranscript.ifBlank { "…" },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = AppSpacing.xs),
        )

        if (state.isListening) {
            Text(
                text = "می‌توانی وسط جمله مکث کنی؛ تا اینجا حفظ می‌شود.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = AppSpacing.xs),
            )
        }
    }
}

/** The graded outcome: a banner, then the model answer and the lesson's note. */
@Composable
private fun Outcome(
    state: ConversationUiState,
    turn: DialogueTurn,
    permissionRefused: Boolean,
) {
    val verdict = state.verdict

    when {
        state.error != SpeechError.NONE -> ErrorCard(state.error)

        // No words at all: the app heard nothing. Never dressed up as a wrong answer.
        state.unheard -> InfoBanner(
            message = "چیزی نشنیدم. کمی نزدیک‌تر به میکروفون و کمی بلندتر بگو — این تلاش حساب نمی‌شود.",
        )

        verdict == SpeakingVerdict.CORRECT -> CorrectBanner(
            message = "درست جواب دادی! گفتگو ادامه دارد.",
        )

        verdict == SpeakingVerdict.NEAR -> InfoBanner(
            message = "نزدیک بود! بیشترِ کلمه‌های کلیدی را گفتی.",
        )

        verdict == SpeakingVerdict.RETRY -> IncorrectBanner(
            message = "این جواب با جملهٔ هدف فرق داشت.",
        )
    }

    if (permissionRefused && state.error == SpeechError.NONE) {
        IncorrectBanner(
            message = "برای پاسخ دادن به میکروفون نیاز داریم. دسترسی را از تنظیمات برنامه بدهید.",
        )
    }

    // Non-perfect takes show both sides plus the lesson's own note: the learner
    // needs the gap and the next thing to change, not just a verdict.
    if (verdict != null && verdict != SpeakingVerdict.CORRECT) {
        AppCard(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
            Text(
                text = "جواب درست این نوبت:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            EnglishText(
                text = turn.answer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = AppSpacing.xs),
            )
            turn.tipFa?.let { tip ->
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = AppSpacing.sm),
                )
            }
        }
    }
}

@Composable
private fun AnswerControls(
    state: ConversationUiState,
    permissionRefused: Boolean,
    onStart: () -> Unit,
    onEvent: (ConversationEvent) -> Unit,
) {
    val verdict = state.verdict

    when {
        // The device is still speaking. Keeping the button disabled (rather than
        // hiding it) tells the learner *why* nothing happens; the VM refuses a
        // take in this state either way, so a tap can never record the partner.
        state.isPartnerSpeaking -> {
            PrimaryButton(
                text = "پاسخ دادن",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
            )
            Text(
                text = "صبر کن تا سؤال کامل پخش شود، بعد جواب بده.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        state.isListening -> OutlinedActionButton(
            text = "پایان پاسخ",
            onClick = { onEvent(ConversationEvent.StopTake) },
            modifier = Modifier.fillMaxWidth(),
        )

        state.isPreparing -> OutlinedActionButton(
            text = "در حال آماده‌سازی…",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
        )

        verdict == SpeakingVerdict.CORRECT -> PrimaryButton(
            text = "ادامهٔ گفتگو",
            onClick = { onEvent(ConversationEvent.Next) },
            modifier = Modifier.fillMaxWidth(),
        )

        // "Near" is the target said with a slip: moving on is the default, and a
        // retry stays one tap away. Retry used to be the primary action here, which
        // is what sent learners back to repeat a sentence they had mostly said.
        verdict == SpeakingVerdict.NEAR -> {
            PrimaryButton(
                text = "ادامهٔ گفتگو",
                onClick = { onEvent(ConversationEvent.Next) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedActionButton(
                text = "تلاش دوباره",
                onClick = { onEvent(ConversationEvent.Retry) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // A miss (or a take the app never heard): retry is the point, but never a
        // dead end — a learner stuck on one long sentence still has to move on.
        verdict != null || state.unheard -> {
            PrimaryButton(
                text = "تلاش دوباره",
                onClick = { onEvent(ConversationEvent.Retry) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (verdict != null) {
                OutlinedActionButton(
                    text = "ادامهٔ گفتگو",
                    onClick = { onEvent(ConversationEvent.Next) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        else -> {
            PrimaryButton(
                text = "پاسخ دادن",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = if (permissionRefused) {
                    "دسترسی میکروفون داده نشده است."
                } else {
                    "میکروفون را روشن کن و انگلیسی جواب بده."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Icon + text per verdict — never colour alone. */
@Composable
private fun VerdictMark(verdict: SpeakingVerdict) {
    val extended = LocalAppExtendedColors.current
    val (icon, tint) = when (verdict) {
        SpeakingVerdict.CORRECT -> Icons.Outlined.CheckCircle to extended.success
        SpeakingVerdict.NEAR -> Icons.Outlined.Info to extended.info
        SpeakingVerdict.RETRY -> Icons.Outlined.Refresh to MaterialTheme.colorScheme.error
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = when (verdict) {
                SpeakingVerdict.CORRECT -> "درست"
                SpeakingVerdict.NEAR -> "نزدیک"
                SpeakingVerdict.RETRY -> "دوباره"
            },
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            modifier = Modifier.padding(start = AppSpacing.xs),
        )
    }
}

@Composable
private fun ErrorCard(error: SpeechError) {
    val (title, subtitle) = when (error) {
        SpeechError.NO_MICROPHONE -> "میکروفون در دسترس نیست" to
            "این دستگاه میکروفون ندارد یا میکروفون را برنامهٔ دیگری گرفته است. این تمرین بدون میکروفون ممکن نیست."
        SpeechError.PERMISSION_DENIED -> "دسترسی به میکروفون داده نشد" to
            "برای پاسخ دادن به میکروفون نیاز داریم. از تنظیمات برنامه، مجوز میکروفون را فعال کنید."

        SpeechError.MODEL_MISSING -> "مدل گفتار پیدا نشد" to
            "مدل آفلاین تشخیص گفتار در برنامه نیست. نصب را با `scripts/fetch_vosk_model.sh` آماده و دوباره بسازید."

        SpeechError.ASSET_COPY_FAILED -> "آماده‌سازی مدل ناموفق بود" to
            "فضای کافی برای کپی مدل وجود ندارد یا کپی ناتمام مانده است. فضای دستگاه را آزاد کنید و دوباره تلاش کنید."

        SpeechError.INTERRUPTED -> "ضبط قطع شد" to
            "ضبط صدا وسط کار از کار افتاد. این تلاش حساب نشد؛ دوباره شروع کنید."

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

/**
 * The end of a pass: what the learner actually achieved, not just "finished".
 *
 * A conversation that ends on a blank "همهٔ گفتگوها را جواب دادی" throws away the
 * only feedback the session produced. The summary keeps the score (a ring, so the
 * share is read before the numbers), the breakdown (correct / near / retry) and
 * the whole transcript, then offers both ways out: run the same conversation
 * again, or go home.
 */
@Composable
private fun SessionSummary(
    state: ConversationUiState,
    onRestart: () -> Unit,
    onBack: () -> Unit,
) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            ProgressRing(
                progress = state.accuracyPercent / 100f,
                modifier = Modifier.size(88.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.accuracyPercent}٪",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "درست",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Text(
                    text = "گفتگو تمام شد",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${state.answeredCount} نوبت جواب دادی: ${state.correctCount} درست، " +
                        "${state.nearCount} نزدیک و ${state.retryCount} نیازمند تکرار.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (state.history.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = AppSpacing.compact),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                state.history.forEach { answered ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        VerdictMark(verdict = answered.verdict)
                        Column(Modifier.weight(1f)) {
                            EnglishText(
                                text = answered.turn.partnerLine ?: answered.turn.answer,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            EnglishText(
                                text = answered.said.ifBlank { "—" },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        PrimaryButton(
            text = "گفتگو از اول",
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.md),
        )
        OutlinedActionButton(
            text = "بازگشت به خانه",
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.sm),
        )
    }
}
