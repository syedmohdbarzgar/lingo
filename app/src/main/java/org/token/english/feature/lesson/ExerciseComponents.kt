package org.token.english.feature.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LearningTargetStyle
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SecondaryButton

enum class OptionVisual { DEFAULT, SELECTED, CORRECT, INCORRECT }

/**
 * Multiple-choice option card — min 56dp, 1.5dp state border, icon + color so the
 * result never depends on color alone (Lingua design system §2, design.md §50).
 */
@Composable
fun ExerciseOption(
    text: String,
    visual: OptionVisual,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val scheme = MaterialTheme.colorScheme
    val (borderColor, backgroundColor, contentColor) = when (visual) {
        OptionVisual.DEFAULT -> Triple(scheme.outlineVariant, scheme.surface, scheme.onSurface)
        OptionVisual.SELECTED -> Triple(scheme.primary, scheme.primaryContainer, scheme.onPrimaryContainer)
        OptionVisual.CORRECT -> {
            val ext = org.token.english.core.designsystem.LocalAppExtendedColors.current
            Triple(scheme.primary, ext.successContainer, ext.onSuccess)
        }
        OptionVisual.INCORRECT -> Triple(
            scheme.error,
            scheme.errorContainer,
            scheme.onErrorContainer,
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .border(BorderStroke(if (visual == OptionVisual.DEFAULT) 1.dp else 1.5.dp, borderColor), MaterialTheme.shapes.medium)
            .background(backgroundColor, MaterialTheme.shapes.medium)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Box(Modifier.weight(1f)) {
            EnglishText(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
            )
        }
        when (visual) {
            OptionVisual.CORRECT -> Icon(
                Icons.Default.CheckCircle,
                contentDescription = "درست",
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            OptionVisual.INCORRECT -> Icon(
                Icons.Default.Cancel,
                contentDescription = "نادرست",
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            else -> Unit
        }
    }
}

/** LTR text answer field for fill-blank / translation / listening answers. */
@Composable
fun AnswerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "پاسخ شما",
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            label = { Text(label) },
            textStyle = LearningTargetStyle.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
            ),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

/**
 * Speaking practice (the `speaking` item): hear the model from the on-device TTS,
 * record your own take, hear it back and compare. Nothing is graded and nothing
 * is uploaded — there is no recogniser offline, and the take lives in the app
 * cache (AGENTS §7: no network, no cloud speech).
 *
 * Every action is a labelled button, so the states carry themselves without
 * leaning on colour alone (design.md §50).
 */
@Composable
fun SpeakingCard(
    modelSentence: String,
    promptFa: String,
    isPlayingModel: Boolean,
    isRecording: Boolean,
    hasRecording: Boolean,
    isPlayingRecording: Boolean,
    recordingFailed: Boolean,
    onPlayModel: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onStopPlayback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        if (promptFa.isNotBlank()) {
            Text(
                text = promptFa,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        // The model sentence is English: EnglishText re-asserts LTR inside the
        // forced-RTL root and pins the paragraph direction.
        AppCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                EnglishText(
                    text = modelSentence,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                TextButton(onClick = onPlayModel, enabled = !isPlayingModel) {
                    Text(if (isPlayingModel) "در حال پخش…" else "پخش نمونه")
                }
            }
        }

        PrimaryButton(
            text = if (isRecording) "پایان ضبط" else "ضبط صدای من",
            onClick = if (isRecording) onStopRecording else onStartRecording,
            modifier = Modifier.fillMaxWidth(),
        )

        SecondaryButton(
            text = when {
                isPlayingRecording -> "توقف پخش"
                else -> "پخش صدای من"
            },
            onClick = if (isPlayingRecording) onStopPlayback else onPlayRecording,
            enabled = hasRecording,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = when {
                isRecording -> "در حال ضبط — جمله را واضح بگو و بعد ضبط را ببند."
                hasRecording -> "صدای خودت را با نمونه مقایسه کن و بعد ادامه بده."
                else -> "اول نمونه را گوش کن، بعد جمله را خودت بگو و ضبط کن."
            },
            style = MaterialTheme.typography.bodySmall.copy(
                textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        if (recordingFailed) {
            Text(
                text = "ضبط صدا ممکن نشد — اجازه‌ی میکروفون را بررسی کن و دوباره تلاش کن.",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

/**
 * Listening exercise: on-device TTS playback + optional transcript (design.md §21).
 *
 * States (checklist B-3):
 *  - normal / playing — tap the speaker to (re)play.
 *  - [unavailable] — no English voice on this device: no fake play affordance;
 *    the transcript becomes the exercise (read + type) with an install-voice action.
 *  - [playbackFailed] — an attempt produced no sound: text hint + retry by tap.
 */
@Composable
fun ListeningCard(
    audioText: String,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
    unavailable: Boolean = false,
    playbackFailed: Boolean = false,
    onInstallVoiceData: (() -> Unit)? = null,
) {
    var showTranscript by remember { mutableStateOf(false) }
    // Without audio the transcript IS the exercise — always visible, no toggle:
    // a hidden text with no sound would make the task impossible.
    val transcriptVisible = showTranscript || unavailable
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        if (unavailable) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    Text(
                        text = "صدای انگلیسی در دسترس نیست",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "این تمرین را با خواندن متن و تایپ آن ادامه بده",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            onInstallVoiceData?.let { install ->
                TextButton(onClick = install) {
                    Text("نصب داده‌های صدا")
                }
            }
        } else {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "پخش صدا",
                modifier = Modifier
                    .size(64.dp)
                    .clickable(enabled = !isPlaying, onClick = onPlay),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = if (isPlaying) "در حال پخش…" else "برای شنیدن ضربه بزنید",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // Failure is never color-only: a sentence that says what to do (design.md §50).
            if (playbackFailed) {
                Text(
                    text = "پخش صدا انجام نشد — دوباره ضربه بزنید",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            TextButton(onClick = { showTranscript = !showTranscript }) {
                Text(if (showTranscript) "پنهان کردن متن" else "نمایش متن")
            }
        }
        if (transcriptVisible) {
            EnglishText(
                text = audioText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
