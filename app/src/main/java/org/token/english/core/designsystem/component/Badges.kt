package org.token.english.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.domain.model.LearningLevel

/** The two colors a CEFR level is drawn in — badge fill and its text. */
data class LevelTint(val container: Color, val content: Color)

/**
 * One hue per CEFR level (design.md §30, extended): A1 green → A2 sky →
 * B1 brand blue → B2 violet → C1 amber → C2 rose, so a level reads at a
 * glance everywhere it appears. Theme-aware — dark containers keep the same
 * hue with light text, like the other semantic pairs.
 */
@Composable
fun levelTint(level: LearningLevel): LevelTint {
    val extended = LocalAppExtendedColors.current
    val container: Color
    val content: Color
    when (level) {
        LearningLevel.A1 -> {
            container = extended.successContainer
            content = extended.onSuccess
        }
        LearningLevel.A2 -> {
            container = extended.infoContainer
            content = extended.onInfo
        }
        LearningLevel.B1 -> {
            container = MaterialTheme.colorScheme.primaryContainer
            content = MaterialTheme.colorScheme.onPrimaryContainer
        }
        LearningLevel.B2 -> {
            container = extended.violetContainer
            content = extended.onViolet
        }
        LearningLevel.C1 -> {
            container = extended.warningContainer
            content = extended.onWarning
        }
        LearningLevel.C2 -> {
            container = extended.roseContainer
            content = extended.onRose
        }
    }
    return LevelTint(container, content)
}

/** CEFR badge — pill, one color per level band (design.md §30). */
@Composable
fun CefrBadge(level: LearningLevel, modifier: Modifier = Modifier) {
    val tint = levelTint(level)
    Text(
        text = level.name,
        style = MaterialTheme.typography.labelSmall,
        color = tint.content,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(tint.container)
            .padding(horizontal = AppSpacing.compact, vertical = AppSpacing.xs),
    )
}

/**
 * Generic metadata chip (design.md §31): topics, skills, filters.
 * [containerColor]/[contentColor] tint it (the level picker colors each chip
 * with its own level's hue): selected fills with the tint, unselected keeps the
 * neutral background but colors the label.
 */
@Composable
fun AppChip(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
) {
    val bg = when {
        selected -> containerColor ?: MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }
    val fg = when {
        selected -> contentColor ?: MaterialTheme.colorScheme.onPrimaryContainer
        else -> contentColor ?: MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(bg)
            .then(if (onClick != null) Modifier.clickableNoRipple(onClick) else Modifier)
            .padding(horizontal = AppSpacing.compact, vertical = 6.dp),
    )
}
