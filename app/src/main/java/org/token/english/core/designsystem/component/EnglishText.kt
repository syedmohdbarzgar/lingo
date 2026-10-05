package org.token.english.core.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Renders English learning content in LTR regardless of the surrounding RTL layout,
 * using the system (Roboto) family instead of Vazirmatn (design.md §8/§51).
 *
 * The paragraph direction itself follows the content (first strong character):
 * pure English still lays out LTR, while mixed or Persian strings that slip in
 * keep a correct RTL sentence instead of being mirrored by the forced layout.
 */
@Composable
fun EnglishText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(fontFamily = FontFamily.Default, textDirection = TextDirection.Content),
            color = color,
            textAlign = textAlign,
        )
    }
}
