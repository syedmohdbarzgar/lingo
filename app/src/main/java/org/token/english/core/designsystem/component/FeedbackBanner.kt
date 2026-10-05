package org.token.english.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors

/**
 * Answer feedback (design.md §27/§28): success/error containers with icon + text,
 * so the state never depends on color alone.
 */
@Composable
fun CorrectBanner(modifier: Modifier = Modifier, message: String = "درست است") {
    val extended = LocalAppExtendedColors.current
    FeedbackSurface(
        modifier = modifier,
        container = extended.successContainer,
        content = extended.onSuccess,
        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp)) },
        text = message,
    )
}

@Composable
fun IncorrectBanner(modifier: Modifier = Modifier, message: String) {
    val extended = LocalAppExtendedColors.current
    FeedbackSurface(
        modifier = modifier,
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
        icon = { Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(20.dp)) },
        text = message,
    )
}

/** Informational hint surface (grammar tips, explanations). */
@Composable
fun InfoBanner(modifier: Modifier = Modifier, message: String) {
    val extended = LocalAppExtendedColors.current
    FeedbackSurface(
        modifier = modifier,
        container = extended.infoContainer,
        content = extended.onInfo,
        icon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp)) },
        text = message,
    )
}

@Composable
private fun FeedbackSurface(
    container: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color,
    icon: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(container)
            .padding(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        icon()
        Text(
            text = text,
            // Persian UI copy that embeds English ("پاسخ درست: passport",
            // "مرور بعدی: Jan 5" …): pin the paragraph to RTL so a leading
            // Latin run can't flip the whole sentence via first-strong.
            style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Rtl),
            color = content,
        )
    }
}
