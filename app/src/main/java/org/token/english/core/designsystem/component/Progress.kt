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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.labelFa
import org.token.english.domain.engine.SkillProfile
import org.token.english.domain.engine.SkillStatus

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

/**
 * One learning track (P2): the mastery bar **plus the evidence behind the number**.
 *
 * A mastery bar alone is not a profile — mastery is seeded by the first observation,
 * so one correct answer already reads as 100%. Showing how many answers the track
 * rests on, and how many of them were right, is what makes the number auditable.
 *
 * Shared by Home and Progress so the same track can never be described two
 * different ways on two screens.
 */
@Composable
fun SkillTrackProgress(
    profile: SkillProfile,
    modifier: Modifier = Modifier,
    /** Home shows the summary line; Progress adds how many nodes were practised. */
    compact: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        SkillProgressBar(
            label = profile.skill.labelFa(),
            progress = profile.mastery,
            // An untouched track has no percentage to report — not "۰٪".
            valueText = if (profile.status == SkillStatus.NOT_STARTED) profile.status.labelFa() else null,
        )
        Text(
            text = profile.evidenceLineFa(compact = compact),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * The evidence sentence under a track's bar: how many answers, how accurate, how
 * many curriculum nodes, and how far along the track is.
 */
private fun SkillProfile.evidenceLineFa(compact: Boolean): String = buildString {
    if (evidence == 0) {
        append("هنوز پاسخی در این محور ثبت نشده")
        return@buildString
    }
    append("$evidence پاسخ")
    accuracyPercent?.let { append(" · دقت $it٪") }
    if (!compact) append(" · $nodesPractised گره")
    append(" · ${status.labelFa()}")
}

/**
 * Compact labeled section heading used across screens.
 *
 * [color] follows the surface it sits on: the themed `onSurface` by default, or the
 * card's own ink when the card is tinted (the coloured Home cards), where the
 * default would be dark-on-tint instead of the card's readable pair.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
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
