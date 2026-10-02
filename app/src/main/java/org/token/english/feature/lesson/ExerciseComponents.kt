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
import org.token.english.core.designsystem.component.EnglishText

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

/** Listening exercise: on-device TTS playback + optional transcript (design.md §21). */
@Composable
fun ListeningCard(
    audioText: String,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTranscript by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
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
        TextButton(onClick = { showTranscript = !showTranscript }) {
            Text(if (showTranscript) "پنهان کردن متن" else "نمایش متن")
        }
        if (showTranscript) {
            EnglishText(
                text = audioText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
