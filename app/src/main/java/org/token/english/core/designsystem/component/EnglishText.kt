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
 * The paragraph direction follows the content (first strong letter): pure English
 * stays LTR, while a stray Persian/Arabic first letter would flip the paragraph to
 * RTL inside this forced-LTR subtree. We therefore force the paragraph back to LTR
 * whenever the first letter found is not an ASCII Latin letter — a safety net for
 * the forced system-level RTL root layout.
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
            style = style.copy(
                fontFamily = FontFamily.Default,
                textDirection = textDirectionFor(text),
            ),
            color = color,
            textAlign = textAlign,
        )
    }
}

/**
 * Returns the paragraph text direction to use for [text].
 * Mirrors [TextDirection.Content] but forces LTR whenever the first strong
 * letter is not a plain English letter (A–Z / a–z).
 */
private fun textDirectionFor(text: String): TextDirection {
    return if (firstStrongIsLtr(text)) TextDirection.Content else TextDirection.Ltr
}

/**
 * First-strong-character test (BiDi rule P2, simplified): skips every non-letter
 * (whitespace, digits, punctuation, format chars such as ZWNJ, marks, emoji) and
 * reports whether the first letter found is an ASCII Latin letter — the only
 * letters EnglishText is meant to lay out LTR-first.
 */
private fun firstStrongIsLtr(text: String): Boolean {
    var i = 0
    while (i < text.length) {
        val cp = text.codePointAt(i)
        if (!Character.isLetter(cp)) {
            i += Character.charCount(cp)
            continue
        }
        // First letter reached: ASCII Latin → Content keeps it LTR; any other
        // script (Persian/Arabic slip-in) → force an explicit LTR paragraph.
        return cp in 'A'.code..'Z'.code || cp in 'a'.code..'z'.code
    }
    // No letter at all (digits/punct only) — safe default is LTR.
    return false
}
