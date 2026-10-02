package org.token.english.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing

/**
 * Standard container card — flat, 16dp radius, hairline border instead of heavy shadow
 * (design.md §13/§16).
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { Column(Modifier.padding(AppSpacing.md), content = content) }
    } else {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) { Column(Modifier.padding(AppSpacing.md), content = content) }
    }
}
