package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.common.bidiNumber
import org.token.english.core.designsystem.component.firstStrongIsLtr
import org.token.english.core.common.persianMeasure
import org.token.english.feature.home.dailyGoalProgressLabel
import org.token.english.feature.home.dailyGoalRingLabel

/**
 * The daily-goal card's numbers, pinned to the form that survives a device run.
 *
 * The ring used to read `"/ 15 دقیقه"`: a leading neutral plus a plain space before
 * «دقیقه». In the forced-RTL layout that is a bare directional run the renderer is
 * free to resolve against the Persian around it, and inside an 84dp ring the plain
 * space is a legal line break — the device showed the number and its unit on
 * different lines, so «از ۱۵ دقیقه» read wrong way round. These tests hold the fix:
 * the digits are isolated (`LRI … PDI`) and the space before the unit is
 * non-breaking, so the phrase keeps its authored order and never splits.
 */
class DailyGoalCopyTest {

    private val lri = "\u2066"
    private val pdi = "\u2069"
    private val nbsp = "\u00A0"

    /** «۱۵ دقیقه» as the card must build it: isolated number, glued unit. */
    private fun measure(value: Int) = "$lri$value$pdi${nbsp}دقیقه"

    @Test
    fun `the ring label reads as the goal, not a slash`() {
        val label = dailyGoalRingLabel(15)

        assertEquals("از ${measure(15)}", label)
        // The slash was the old copy; it is a neutral that the RTL paragraph may
        // place on either side of the number.
        assertFalse("the ring label still uses a slash", label.contains("/"))
    }

    @Test
    fun `the number keeps its place in the phrase`() {
        val label = dailyGoalRingLabel(15)

        assertTrue("the number must come after «از»", label.indexOf("15") > label.indexOf("از"))
        assertTrue("the unit must follow the number", label.indexOf("دقیقه") > label.indexOf("15"))
    }

    @Test
    fun `the number and its unit are one unbreakable run`() {
        for (label in listOf(dailyGoalRingLabel(15), dailyGoalProgressLabel(7, 15))) {
            // A plain space is where the device broke the line and left «دقیقه» alone.
            assertFalse("a plain space separates the number from its unit: $label", label.contains("15 "))
            assertFalse("a plain space separates the number from its unit: $label", label.contains("7 "))
            assertTrue("the unit is not glued to the number: $label", label.contains("${nbsp}دقیقه"))
        }
    }

    @Test
    fun `the progress line keeps both measures in order`() {
        val label = dailyGoalProgressLabel(7, 15)

        assertEquals("${measure(7)} از ${measure(15)}", label)
        assertTrue("done must be named first", label.indexOf("7") < label.indexOf("از"))
        assertTrue("the goal must be named last", label.indexOf("15") > label.indexOf("از"))
    }

    @Test
    fun `the phrase stays a persian paragraph`() {
        // Isolation must not flip the paragraph: the card is Persian copy around a
        // number, so the first strong letter is still Persian and the text is RTL.
        assertFalse(firstStrongIsLtr(dailyGoalRingLabel(15)))
        assertFalse(firstStrongIsLtr(dailyGoalProgressLabel(7, 15)))
    }

    @Test
    fun `a number is isolated exactly once`() {
        assertEquals("$lri" + "0" + pdi, bidiNumber(0))
        assertEquals("$lri" + "15" + pdi, bidiNumber(15))
        assertEquals("${measure(1)} و ${measure(2)}", "${persianMeasure(1, "دقیقه")} و ${persianMeasure(2, "دقیقه")}")
    }
}
