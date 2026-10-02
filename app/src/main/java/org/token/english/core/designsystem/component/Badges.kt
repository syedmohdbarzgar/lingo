package org.token.english.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.domain.model.LearningLevel

/** CEFR badge — pill, tinted by level band (design.md §30). */
@Composable
fun CefrBadge(level: LearningLevel, modifier: Modifier = Modifier) {
    val extended = LocalAppExtendedColors.current
    val bg = when (level) {
        LearningLevel.A1, LearningLevel.A2 -> extended.infoContainer
        LearningLevel.B1, LearningLevel.B2 -> MaterialTheme.colorScheme.primaryContainer
        LearningLevel.C1, LearningLevel.C2 -> extended.warningContainer
    }
    val fg = when (level) {
        LearningLevel.A1, LearningLevel.A2 -> extended.onInfo
        LearningLevel.B1, LearningLevel.B2 -> MaterialTheme.colorScheme.onPrimaryContainer
        LearningLevel.C1, LearningLevel.C2 -> extended.onWarning
    }
    Text(
        text = level.name,
        style = MaterialTheme.typography.labelSmall,
        color = fg,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(bg)
            .padding(horizontal = AppSpacing.compact, vertical = AppSpacing.xs),
    )
}

/** Generic metadata chip (design.md §31): topics, skills, filters. */
@Composable
fun AppChip(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val bg = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val fg = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
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
