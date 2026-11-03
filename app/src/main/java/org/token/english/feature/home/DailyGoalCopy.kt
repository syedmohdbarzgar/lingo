package org.token.english.feature.home

import org.token.english.core.common.persianMeasure

/**
 * The daily-goal card's two phrases, built as **one** bidi-safe string each.
 *
 * The card used to interpolate the numbers straight into the copy — `"/ $goal دقیقه"`
 * in the ring and `"$done دقیقه از $goal دقیقه"` under it. Both left the digits as a
 * bare directional run with a plain space before «دقیقه»: on a device the renderer
 * resolved them against the surrounding Persian and, when the narrow ring forced a
 * line break, the number and its unit landed on different lines — «از ۱۵ دقیقه» read
 * with the number in the wrong place. The numbers now go through [persianMeasure]
 * (an LTR isolate plus a non-breaking space), so the phrase keeps its authored order
 * and never splits.
 *
 * Pure Kotlin, no Compose: `DailyGoalCopyTest` pins these strings.
 */

/** Small label inside the ring, under the minutes-learned number. */
internal fun dailyGoalRingLabel(goalMinutes: Int): String =
    "از ${persianMeasure(goalMinutes, "دقیقه")}"

/** The line under the ring: minutes learned out of the daily target. */
internal fun dailyGoalProgressLabel(doneMinutes: Int, goalMinutes: Int): String =
    "${persianMeasure(doneMinutes, "دقیقه")} از ${persianMeasure(goalMinutes, "دقیقه")}"
