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
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.TouchTargetMin

/** Primary action — design.md §15. Full-width on phones, 48dp high, 12dp radius (shape token). */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    contentDescription: String? = null,
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
        shape = MaterialTheme.shapes.medium,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(AppSpacing.xs),
                strokeWidth = 2.dp,
                color = Color.White,
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
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
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
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
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
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
