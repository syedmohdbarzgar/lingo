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
 *
 * `internal` (not private) so the BiDi rule is unit tested in the JVM — see
 * `BidiTextTest`. Nothing Android-specific is involved: it is a pure string rule.
 */
internal fun textDirectionFor(text: String): TextDirection {
    return if (firstStrongIsLtr(text)) TextDirection.Content else TextDirection.Ltr
}

/**
 * First-strong-character test (BiDi rule P2, simplified): skips every non-letter
 * (whitespace, digits, punctuation, format chars such as ZWNJ, marks, emoji) and
 * reports whether the first letter found is an ASCII Latin letter — the only
 * letters EnglishText is meant to lay out LTR-first.
 *
 * `internal` for the same reason as [textDirectionFor]: it is the BiDi rule the
 * whole RTL root layout depends on, so it is pinned by a test.
 */
internal fun firstStrongIsLtr(text: String): Boolean {
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

/** U+2066 LEFT-TO-RIGHT ISOLATE / U+2069 POP DIRECTIONAL ISOLATE. */
private const val LRI = '\u2066'
private const val PDI = '\u2069'

/**
 * Wraps every embedded Latin run of [text] in directional isolates (LRI … PDI)
 * so mixed Persian/English prose cannot scramble under the BiDi algorithm.
 *
 * A plain RTL paragraph reorders neutral characters (`.`, `—`, `«»`, the Persian
 * comma) against neighbouring Latin runs: a period drifts to the wrong end of a
 * phrase and quoted English flips with its sentence — exactly the garbled
 * «قائده» / «خطاهای رایج» lesson cards the device run reproduced. Inside an
 * isolate each run resolves on its own, so it keeps its authored order and its
 * trailing punctuation while the surrounding Persian still flows right-to-left.
 *
 * Rules: a run starts at an ASCII letter or digit and keeps letters, digits,
 * internal spaces and intra-run punctuation (including `،` inside an English
 * sequence); a candidate with no letter (a bare number) is left plain. A
 * BRACKET PAIR that straddles the run is pulled inside the isolate with it
 * (`(capital)`, `«go straight»`): with the opener outside, the RTL paragraph
 * mirrors only that half and the text reads `(capital))` on screen.
 * Idempotent: text that already carries an isolate is returned unchanged.
 *
 * Pure Kotlin, pinned by `BidiTextTest` — the same convention as
 * [firstStrongIsLtr]. Nothing Android-specific is involved.
 */
internal fun isolateEmbeddedLtr(text: String): String {
    if (text.isEmpty() || text.contains(LRI)) return text
    val out = StringBuilder(text.length + 8)
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        if (!isRunStart(ch)) {
            out.append(ch)
            i++
            continue
        }
        var j = i
        while (j < text.length && isRunChar(text[j])) j++
        // Internal spaces belong to the run; trailing ones stay in the Persian.
        var end = j
        while (end > i && text[end - 1] == ' ') end--
        var run = text.substring(i, end)
        if (run.any { it in 'A'..'Z' || it in 'a'..'z' }) {
            // A bracket pair that straddles the isolate would be split by it:
            // the half left in the RTL text mirrors, the half inside does not,
            // and `(capital)` renders as `(capital))`. Take the opener back out
            // of the output and keep the whole pair inside the isolate.
            val opener = if (out.isEmpty()) null else out[out.length - 1]
            val closer = when (opener) {
                '(' -> ')'
                '[' -> ']'
                '«' -> '»'
                '“' -> '”'
                '"' -> '"'
                else -> null
            }
            var closes = false
            if (closer != null && opener != null) {
                if (run.contains(closer)) {
                    out.setLength(out.length - 1)
                    run = "$opener$run"
                } else if (end < text.length && text[end] == closer) {
                    out.setLength(out.length - 1)
                    run = "$opener$run$closer"
                    closes = true
                }
            }
            out.append(LRI).append(run).append(PDI)
            if (closes) end++
        } else {
            out.append(run)
        }
        i = end
    }
    return out.toString()
}

private fun isRunStart(ch: Char): Boolean =
    ch in 'A'..'Z' || ch in 'a'..'z' || ch in '0'..'9'

private fun isRunChar(ch: Char): Boolean =
    isRunStart(ch) || ch in " .,'-/:?!()&+=…،؛\u201c\u201d\u2018\u2019\""
