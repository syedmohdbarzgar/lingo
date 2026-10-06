package org.token.english.core.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * Renders English learning content in LTR regardless of the surrounding RTL layout,
 * using the system (Roboto) family instead of Vazirmatn (design.md §8/§51).
 *
 * The paragraph direction follows the content (first strong character): pure English
 * stays LTR, while a stray Persian/Arabic first character would normally flip the
 * paragraph to RTL and be mirrored. We therefore force the paragraph to LTR when the
 * first strong character is not an English letter or ASCII digit, as a safety net for
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
 * character is not a plain English letter (A–Z / a–z) or ASCII digit.
 */
private fun textDirectionFor(text: String): TextDirection {
    return if (firstStrongIsLtr(text)) TextDirection.Content else TextDirection.Ltr
}

/**
 * First-strong-character test (BiDi rule P2). Skips controls, format chars, marks,
 * surrogates, quotes and neutrals/spaces so the decision matches the Unicode "first
 * strong character" rule.
 */
private fun firstStrongIsLtr(text: String): Boolean {
    var i = 0
    while (i < text.length) {
        val cp = text.codePointAt(i)
        val cat = Character.getType(cp)
        val isNonStrong =
            cat == Character.CONTROL
                || cat == Character.FORMAT
                || cat == Character.PRIVATE_USE
                || cat == Character.SURROGATE
                || cat == Character.NONSPACING_MARK
                || cat == Character.ENCLOSING_MARK
                || cat == Character.INITIAL_QUOTE
                || cat == Character.FINAL_QUOTE
                || cat == Character.OTHER_NEUTRAL
                || cat == Character.PARAGRAPH_SEPARATOR
                || cat == Character.LINE_SEPARATOR
                || cat == Character.SPACE_SEPARATOR
        if (isNonStrong) {
            i += if (Character.isHighSurrogate(text[i])) 2 else 1
            continue
        }
        // Strong character reached — decide based on whether it is an ASCII letter
        // or digit. Any other strong char (Persian/Arabic, other scripts, punctuation)
        // is treated as non-LTR so we fall back to an explicit LTR paragraph.
        val cpLen = if (Character.isHighSurrogate(text[i])) 2 else 1
        val isLtr = cp in 'A'.code..'Z'.code || cp in 'a'.code..'z'.code || cp in '0'.code..'9'.code
        return isLtr
    }
    return false
}
