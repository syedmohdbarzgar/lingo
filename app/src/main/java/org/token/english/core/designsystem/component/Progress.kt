package org.token.english.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import org.token.english.core.designsystem.AppSpacing

/** Plain clickable without ripple — used by chips/inline targets. */
internal fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = null,
    onClick = onClick,
)

/**
 * Linear progress bar — 8dp track, rounded, brand fill (design.md / Lingua §5).
 * [progress] in 0f..1f. Always carries a content description for TalkBack.
 */
@Composable
fun AppLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val clamped = progress.coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { clamped },
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        gapSize = 0.dp,
    )
}

/**
 * Circular progress ring for the daily goal (design.md §24).
 * Center content (e.g. "12m / 15m") is provided by the caller.
 */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeWidth = strokeWidth,
            gapSize = 0.dp,
        )
        content()
    }
}

/** Labeled skill mastery bar (design.md §23) — value in 0..1. */
@Composable
fun SkillProgressBar(
    label: String,
    progress: Float,
    modifier: Modifier = Modifier,
    valueText: String? = null,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = valueText ?: "${(progress.coerceIn(0f, 1f) * 100).toInt()}٪",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        AppLinearProgress(progress = progress)
    }
}

/** Compact labeled section heading used across screens. */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

/** Vertically stacked content placeholder helper. */
@Composable
fun CenteredColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}
