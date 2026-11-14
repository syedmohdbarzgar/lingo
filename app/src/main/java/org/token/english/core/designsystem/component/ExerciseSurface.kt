package org.token.english.core.designsystem.component

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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
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
import org.token.english.domain.model.Exercise

/**
 * The parts of an exercise that more than one screen draws.
 *
 * These lived in `feature/lesson` and were imported from `feature/review` and
 * `feature/onboarding`, which is the import `AGENTS.md` §3 forbids — a shared component
 * was living inside one feature. They are the design system's, so they moved here:
 * an answer field, an option card, the exercise's own cue and the listening surface.
 * Everything that is a *flow* rather than a control (the speaking take) stays in the
 * lesson feature, because no other screen runs it — the listening surface moved here
 * with the review card that asks a listening question (listening-card pass), and it
 * plays through one TTS seam in both places instead of two copies of one button.
 *
 * The review session shows a curriculum node's own exercise (graph-card pass), and it
 * must ask it exactly the way the lesson does — one prompt, one set of controls, so a
 * question cannot read differently in the two places it is asked.
 */

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
    /**
     * A written text needs room to be read back before it is submitted — a
     * one-line field over a two-sentence answer scrolls the start of the learner's
     * own writing out of sight. Everything shorter stays a single line, where Enter
     * is "done" rather than "new line".
     */
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            label = { Text(label) },
            textStyle = LearningTargetStyle.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                imeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
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
 * The cue half of an exercise: what the learner is being asked, and nothing about the
 * answer. The answer controls stay with the screen that owns the flow (an option list,
 * a typed field, a word bank), so one question reads the same in the lesson and in the
 * review session while each keeps its own feedback rules.
 *
 * A `listening` item is its audio and a `speaking` item is the learner's own voice, so
 * both render nothing here — [ListeningCard] draws the first and the lesson draws the
 * second.
 */
@Composable
fun ExercisePrompt(
    exercise: Exercise,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        when (exercise) {
            is Exercise.MultipleChoice -> {
                // A-5: a comprehension item shows its reading text first — the
                // learner reads, then answers. Plain question items have none.
                exercise.passage?.let { text ->
                    SectionHeader("متن را بخوانید")
                    EnglishText(text = text, style = MaterialTheme.typography.bodyLarge)
                }
                EnglishText(text = exercise.question, style = MaterialTheme.typography.titleMedium)
                exercise.questionFa?.let { questionFa ->
                    Text(
                        text = questionFa,
                        // Authored Persian may open with English («Not uncommon» …):
                        // pin RTL so first-strong can't flip it.
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            is Exercise.FillBlank -> {
                SectionHeader("جمله را کامل کنید")
                EnglishText(text = exercise.sentence, style = MaterialTheme.typography.titleLarge)
            }

            is Exercise.WordRecall -> {
                SectionHeader("واژهٔ انگلیسی را بنویسید")
                Text(text = exercise.prompt, style = MaterialTheme.typography.titleMedium)
            }

            is Exercise.Translation -> {
                SectionHeader("ترجمه کنید")
                Text(text = exercise.prompt, style = MaterialTheme.typography.titleMedium)
            }

            is Exercise.SentenceProduction -> {
                SectionHeader("جمله را بسازید")
                Text(text = exercise.prompt, style = MaterialTheme.typography.titleMedium)
                exercise.hintFa?.let { hint ->
                    Text(
                        text = hint,
                        // Authored Persian that may open with an English anchor word:
                        // pin RTL so first-strong cannot flip it.
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            is Exercise.Writing -> {
                SectionHeader("بنویسید")
                Text(
                    text = exercise.prompt,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                    ),
                )
                exercise.hintFa?.let { hint ->
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "حداقل ${exercise.minSentences} جمله",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            is Exercise.Listening, is Exercise.Speaking -> Unit
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
 *
 * It lives in the design system because two screens ask a listening question now: the
 * lesson's `listening` step and the review session's card for a due node whose mask is
 * missing the `listening` format (listening-card pass). One card, so the same question
 * is heard and typed the same way in both places; the caller owns the flow (which TTS
 * seam plays it, whether an install action exists).
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
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
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
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
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
