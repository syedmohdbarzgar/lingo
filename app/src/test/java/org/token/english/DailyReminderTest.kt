package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.common.persianMeasure
import org.token.english.reminder.DailyReminder
import java.util.Calendar

/**
 * The daily reminder's clock (checklist B-4).
 *
 * The hour stopped being a constant when the setting landed, and the failure this
 * pins is the one that would be invisible in review: `Calendar` happily accepts
 * hour 25 by rolling it into the next day, so an un-clamped hour would schedule a
 * reminder nobody chose — and the reboot path re-applies whatever was stored.
 */
class DailyReminderTest {

    private fun instantOf(millis: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = millis }

    @Test
    fun `the alarm lands on the chosen hour, at the top of it`() {
        val now = System.currentTimeMillis()

        val atEight = DailyReminder.nextReminderMillis(8)

        val cal = instantOf(atEight)
        assertEquals("the chosen hour, not the shipped one", 8, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))
        assertTrue("a reminder in the past would never fire", atEight > now)
        assertTrue("but never more than a day away", atEight <= now + DAY_MILLIS + MINUTE_MILLIS)
    }

    @Test
    fun `the shipped default is 19 and needs no argument`() {
        assertEquals(19, DailyReminder.REMINDER_HOUR)

        val cal = instantOf(DailyReminder.nextReminderMillis())
        assertEquals(19, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `an out-of-range hour is clamped instead of rolling into another day`() {
        // 25 would silently become 01:00 tomorrow under Calendar's own arithmetic.
        val late = instantOf(DailyReminder.nextReminderMillis(25))
        assertEquals(23, late.get(Calendar.HOUR_OF_DAY))

        val early = instantOf(DailyReminder.nextReminderMillis(-3))
        assertEquals(0, early.get(Calendar.HOUR_OF_DAY))
        assertTrue(
            "midnight is computed forward to the next day, never returned in the past",
            early.timeInMillis > System.currentTimeMillis(),
        )
    }

    // ---- what the notification may claim -------------------------------------

    @Test
    fun `due cards say how many are waiting`() {
        val copy = DailyReminder.copyFor(dueCount = 12)

        assertEquals("مرور امروز آماده است", copy.title)
        // Through the app's number rule, not a bare "12": the root layout is forced
        // RTL and only an isolated run keeps the count beside its unit (`BidiText`).
        assertTrue(
            "the body carries the real count: ${copy.body}",
            copy.body.contains(persianMeasure(12, "کارت")),
        )
    }

    @Test
    fun `nothing due says so instead of pretending`() {
        // The notification is the one screen the app shows unasked, and it used to
        // announce ready review on an evening with zero cards due.
        val copy = DailyReminder.copyFor(dueCount = 0)

        assertEquals("امروز مروری نداری", copy.title)
        assertFalse("must not claim review is ready", copy.body.contains("آماده است"))
    }

    @Test
    fun `an unreadable count claims nothing at all`() {
        // A failed read is not zero due cards: saying either would be inventing data.
        val copy = DailyReminder.copyFor(dueCount = null)

        assertEquals("یادآوری مطالعهٔ امروز", copy.title)
        assertFalse(copy.body.contains("آماده است"))
        assertFalse("and does not accuse the learner of having nothing to do", copy.body.contains("نداری"))
    }

    private companion object {
        const val DAY_MILLIS = 24L * 60 * 60 * 1000
        const val MINUTE_MILLIS = 60L * 1000
    }
}
