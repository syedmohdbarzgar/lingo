package org.token.english.core.common

/**
 * Numbers inside Persian copy, built so a device renderer cannot reorder them or
 * split them from the word they count.
 *
 * The app's root layout is **forced RTL**. A bare digit run in that paragraph is a
 * weak directional run: the BiDi algorithm resolves it against the neighbouring
 * Persian words and their neutral punctuation, and a plain space before «دقیقه»
 * is a legal line break. On device that showed up as the daily-goal ring reading
 * «دقیقه ۱۵» with the number and its unit on different lines, and as counters like
 * «تمرین ۳ از ۱۲» whose digits drifted to the wrong end of the phrase.
 *
 * Two primitives fix every such string:
 *  - **isolation** (`U+2066` LRI … `U+2069` PDI) pins the digits as one LTR run in
 *    the position the sentence put them, and
 *  - a **non-breaking space** (`U+00A0`) glues the number to its unit, so «۱۵ دقیقه»
 *    wraps and renders as one piece.
 *
 * Pure Kotlin, no Compose and no Android: the same rule is used by the UI and by
 * the domain engines that author Persian `reasonFa` text. Pinned by
 * `BidiNumbersTest` and `DailyGoalCopyTest`.
 */

/** U+2066 LEFT-TO-RIGHT ISOLATE. */
private const val LRI = "\u2066"

/** U+2069 POP DIRECTIONAL ISOLATE. */
private const val PDI = "\u2069"

/** U+00A0 NO-BREAK SPACE — the glue between a number and its unit. */
private const val NBSP = "\u00A0"

/** [value] as a self-contained LTR number inside an RTL sentence. */
fun bidiNumber(value: Int): String = "$LRI$value$PDI"

/**
 * A Persian measure — number + unit — that can neither reorder nor break:
 * «۱۵ دقیقه», «۳ درس», «۲ کارت».
 */
fun persianMeasure(value: Int, unitFa: String): String = "${bidiNumber(value)}$NBSP$unitFa"

/** «۴۰٪» — the sign sits against the number, in Persian reading order. */
fun persianPercent(value: Int): String = "${bidiNumber(value)}٪"

/** «۳ از ۱۲» — two counts, done first. */
fun persianCountPair(done: Int, total: Int): String =
    "${bidiNumber(done)} از ${bidiNumber(total)}"

/** «تمرین ۳ از ۱۲» — a labelled position inside a sequence. */
fun persianStepLabel(labelFa: String, index: Int, total: Int): String =
    "$labelFa ${persianCountPair(index, total)}"
