package org.token.english.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing

/**
 * Empty state = context + explanation + next action (design.md §37).
 * Never scolds the user; always offers the next step.
 *
 * [secondaryActionText] is the quieter way out of a *failure* state — the retry is
 * the first thing to try, and reaching the publisher is the second, so the two must
 * not compete for the same emphasis (checklist P1-7: a critical error needs a
 * support path, not only a button that re-runs the same failing read).
 */
@Composable
fun AppEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionText != null && onAction != null) {
            SecondaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.padding(top = AppSpacing.sm),
            )
        }
        if (secondaryActionText != null && onSecondaryAction != null) {
            // Text-only, and never smaller than a 48dp touch target.
            TextButton(
                onClick = onSecondaryAction,
                modifier = Modifier.heightIn(min = 48.dp),
            ) { Text(secondaryActionText) }
        }
    }
}
