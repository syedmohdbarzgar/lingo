package org.token.english.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing

/**
 * Standard container card — flat, 16dp radius, hairline border instead of heavy shadow
 * (design.md §13/§16).
 *
 * **Cards always span the available width** — a card sized to its content reads as
 * a chip, and its edges stop lining up with the rest of the column. The width is
 * filled here (not left to each caller) so no new card can forget it; a caller that
 * passes an explicit width still wins, because `fillMaxWidth()` is appended after
 * its modifier.
 *
 * [containerColor] / [contentColor] say what KIND of content the card holds — a
 * teaching rule, a reference table, a set of examples — so a page of cards is not
 * a wall of identical white boxes. Both default to the plain surface, so existing
 * callers are unchanged, and the values come from the theme (never a raw color).
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit,
) {
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
    val fullWidth = modifier.fillMaxWidth()
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = fullWidth,
            shape = MaterialTheme.shapes.large,
            colors = colors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { Column(Modifier.padding(AppSpacing.md), content = content) }
    } else {
        Card(
            modifier = fullWidth,
            shape = MaterialTheme.shapes.large,
            colors = colors,
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { Column(Modifier.padding(AppSpacing.md), content = content) }
    }
}
