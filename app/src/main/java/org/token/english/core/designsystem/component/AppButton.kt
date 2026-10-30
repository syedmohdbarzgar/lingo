package org.token.english.core.designsystem.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.PillShape
import org.token.english.core.designsystem.TouchTargetMin

/**
 * Primary action — design.md §15. Full-width on phones, 48dp high, 12dp radius (shape token).
 *
 * [containerColor]/[contentColor] tint it for a card that carries its own hue
 * (Home's plan / path / weak-spot cards): the action then belongs to the card it
 * sits in instead of dropping a brand-blue block onto a coloured surface. Both
 * come from the theme at the call site — never a raw color.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    contentDescription: String? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(TouchTargetMin)
            .widthIn(max = 480.dp)
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
        enabled = enabled && !loading,
        shape = PillShape,
        colors = if (containerColor != null || contentColor != null) {
            ButtonDefaults.buttonColors(
                containerColor = containerColor ?: MaterialTheme.colorScheme.primary,
                contentColor = contentColor ?: MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            ButtonDefaults.buttonColors()
        },
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(AppSpacing.xs),
                strokeWidth = 2.dp,
                color = contentColor ?: MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

/** Secondary action — Primary 100 fill, Primary 700 text (design.md §15). */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(TouchTargetMin),
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
    }
}

/** Outlined/tertiary action (design.md §15). */
@Composable
fun OutlinedActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(TouchTargetMin),
        enabled = enabled,
        shape = PillShape,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
    }
}

/** Low-priority action (design.md §15). */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(TouchTargetMin),
        enabled = enabled,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
