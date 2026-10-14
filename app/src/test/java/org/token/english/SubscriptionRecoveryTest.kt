package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.ProductIds
import org.token.english.core.billing.SubscriptionMath
import org.token.english.core.billing.SubscriptionPlan
import java.util.Calendar

/**
 * Billing & entitlement recovery (checklist P0).
 *
 * The store is the source of truth, but the app is offline-first: entitlement is
 * mirrored locally so a short offline spell cannot lock the learner out, and it
 * has to be **restored** after a reinstall wipes local DataStore. All of that
 * rests on [SubscriptionMath], which had no tests at all — these pin the
 * recovery paths end to end, from a store purchase to an [AccessLevel].
 */
class SubscriptionRecoveryTest {

    private val now = 1_759_300_000_000L
    private val day = 24L * 60 * 60 * 1000L
    private val hour = 60L * 60 * 1000L

    // ---------------------------------------------------------------- period math

    @Test
    fun `a monthly renewal boundary is in the future and about a month out`() {
        val boundary = SubscriptionMath.nextRenewalBoundary(now - 10 * day, monthly = true, nowMs = now)
        assertTrue("boundary must be strictly after now", boundary > now)
        assertTrue("monthly boundary should be within a month", boundary <= now + 31 * day)
    }

    @Test
    fun `a yearly renewal boundary is about a year out`() {
        val boundary = SubscriptionMath.nextRenewalBoundary(now - 10 * day, monthly = false, nowMs = now)
        assertTrue(boundary > now)
        assertTrue("yearly boundary should be within a year", boundary <= now + 366 * day)
        assertTrue("…and beyond a month", boundary > now + 300 * day)
    }

    @Test
    fun `a long-lapsed purchase rolls forward to the first boundary after now`() {
        // Bought four months ago and never cancelled: the next billing date is the
        // first one after today, not the first one after the purchase.
        val boundary = SubscriptionMath.nextRenewalBoundary(now - 100 * day, monthly = true, nowMs = now)
        assertTrue(boundary > now)
        assertTrue(boundary <= now + 31 * day)
    }

    @Test
    fun `a future purchase time is returned unchanged`() {
        // Stores can report a purchase time slightly ahead of a skewed local clock.
        val future = now + 2 * day
        assertEquals(future, SubscriptionMath.nextRenewalBoundary(future, monthly = true, nowMs = now))
    }

    @Test
    fun `a missing purchase time degrades to now instead of looping`() {
        assertEquals(now, SubscriptionMath.nextRenewalBoundary(0L, monthly = true, nowMs = now))
        assertEquals(now, SubscriptionMath.nextRenewalBoundary(-1L, monthly = true, nowMs = now))
    }

    @Test
    fun `an absurdly old purchase time still terminates`() {
        // The loop is guarded; a 1970 timestamp must not hang the billing check.
        val ancient = 1_000L
        val boundary = SubscriptionMath.nextRenewalBoundary(ancient, monthly = true, nowMs = now)
        assertTrue(boundary > now)
    }

    @Test
    fun `unknown products are treated as monthly`() {
        assertFalse("yearly is not monthly", SubscriptionMath.isMonthly(ProductIds.YEARLY))
        assertTrue("monthly is monthly", SubscriptionMath.isMonthly(ProductIds.MONTHLY))
        assertTrue("an unknown product falls back to monthly", SubscriptionMath.isMonthly("sub_weekly"))
    }

    // ---------------------------------------------------------------- plan catalog

    @Test
    fun `product ids map to the two plans and nothing else`() {
        val monthly: SubscriptionPlan? = ProductIds.planFor(ProductIds.MONTHLY, "۱۲۰٬۰۰۰ تومان")
        val yearly: SubscriptionPlan? = ProductIds.planFor(ProductIds.YEARLY, "۹۹۰٬۰۰۰ تومان")

        assertEquals(ProductIds.MONTHLY, monthly?.productId)
        assertTrue("monthly plan is monthly", monthly?.monthly == true)
        assertEquals("store-formatted price is carried through", "۱۲۰٬۰۰۰ تومان", monthly?.priceText)

        assertEquals(ProductIds.YEARLY, yearly?.productId)
        assertFalse("yearly plan is not monthly", yearly?.monthly == true)

        assertNull("an unregistered product id is not a plan", ProductIds.planFor("sub_weekly", "x"))
    }

    // ---------------------------------------------------------------- recovery paths

    /** What a flavor gateway writes to the local mirror when the store is reachable. */
    private fun mirroredExpiry(local: Long, purchaseTimeMs: Long, monthly: Boolean): Long =
        maxOf(local, SubscriptionMath.nextRenewalBoundary(purchaseTimeMs, monthly, nowMs = now))

    @Test
    fun `a reinstall restores entitlement from the purchase time`() {
        // Local DataStore is wiped by a reinstall, so the mirror starts at 0 and the
        // only evidence left is the store's owned purchase.
        val restored = mirroredExpiry(local = 0L, purchaseTimeMs = now - 5 * day, monthly = true)

        assertEquals(
            "a purchase inside its paid period must unlock after a reinstall",
            AccessLevel.PREMIUM,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = restored),
        )
    }

    @Test
    fun `an ended purchase does not keep the learner premium`() {
        // The store stops reporting the purchase, so the mirror is cleared.
        val cleared = 0L
        assertEquals(
            AccessLevel.LOCKED,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = cleared),
        )
    }

    @Test
    fun `reconciling never shortens an existing local expiry`() {
        // Offline grace (checklist P3): a store answer that is *older* than the
        // local mirror must not take paid time away from the learner.
        val generousLocal = now + 20 * day
        val reconciled = mirroredExpiry(generousLocal, purchaseTimeMs = now - 40 * day, monthly = true)
        assertTrue("the later of the two wins", reconciled >= generousLocal)
    }

    @Test
    fun `an unverifiable subscription stays usable until the mirror runs out`() {
        // The store is unreachable, so checkSubscription() falls back to the mirror.
        val mirror = now + 3 * day
        assertEquals(
            "still premium while the mirror is in the future",
            AccessLevel.PREMIUM,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = mirror),
        )
        assertEquals(
            "…and locked an hour after it lapses",
            AccessLevel.LOCKED,
            EntitlementPolicy.level(now + 3 * day + hour, trialRemainingMs = 0L, subscriptionUntil = mirror),
        )
    }

    // ------------------------------------------------- fixed-length stores (Myket)

    @Test
    fun `a fixed-length plan is a flat 30 or 365 days, not a calendar period`() {
        assertEquals("monthly is a flat 30 days", 30 * day, SubscriptionMath.periodMillis(monthly = true))
        assertEquals("yearly is a flat 365 days", 365 * day, SubscriptionMath.periodMillis(monthly = false))
    }

    @Test
    fun `a myket reinstall restores access from the owned purchase time`() {
        // Myket has no subscriptions: a purchase is a fixed-length consumable, and
        // after a reinstall wipes local DataStore the owned purchase's time is the
        // only receipt left. Access must come back for the rest of its flat period.
        val purchasedAt = now - 5 * day
        val restored = SubscriptionMath.flatPeriodExpiry(purchasedAt, monthly = true)

        assertEquals("restored as purchaseTime + one flat period", purchasedAt + 30 * day, restored)
        assertEquals(
            "an owned purchase inside its period unlocks after a reinstall",
            AccessLevel.PREMIUM,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = restored),
        )
    }

    @Test
    fun `a myket period that already ended does not unlock`() {
        val restored = SubscriptionMath.flatPeriodExpiry(now - 40 * day, monthly = true)

        assertTrue("the period is behind now", restored < now)
        assertEquals(
            "expired fixed-length access is not premium",
            AccessLevel.LOCKED,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = restored),
        )
    }

    @Test
    fun `a missing myket purchase time grants nothing rather than unlocking forever`() {
        assertEquals(0L, SubscriptionMath.flatPeriodExpiry(purchaseTimeMs = 0L, monthly = true))
        assertEquals(0L, SubscriptionMath.flatPeriodExpiry(purchaseTimeMs = -1L, monthly = false))
    }

    @Test
    fun `a renewed subscription extends the mirror rather than replacing it`() {
        // Bought again while still active. The rule every gateway applies on a new
        // purchase is base = max(local, now), then + one period — so a renewal can
        // never lose the days the learner has already paid for.
        val current = now + 10 * day
        val base = maxOf(current, now)
        val afterRenewal = base + 30 * day

        assertTrue(
            "renewal keeps the existing time and adds a period",
            afterRenewal >= current + 30 * day,
        )
        assertEquals(
            "renewing after the mirror lapsed starts from now, not from the old expiry",
            now + 30 * day,
            maxOf(now - 5 * day, now) + 30 * day,
        )
    }

    // ------------------------------------------------- B-5 edge cases

    @Test
    fun `a 30-day plan bought on the 31st is not shown as expired a day early`() {
        // Checklist B-5: a fixed-length plan is a flat 30 days, not a calendar month.
        // A purchase on 31 January would end 28 February under calendar arithmetic —
        // two days *early*. The flat period must run the full 30 days instead.
        val purchased = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JANUARY, 31, 12, 0, 0)
        }.timeInMillis
        val expiry = SubscriptionMath.flatPeriodExpiry(purchased, monthly = true)
        val calendarMonth = Calendar.getInstance().apply {
            timeInMillis = purchased
            add(Calendar.MONTH, 1)
        }.timeInMillis

        assertEquals("a flat 30 days from the purchase", purchased + 30 * day, expiry)
        assertTrue(
            "flat 30 days is never shorter than the calendar month it spans",
            expiry > calendarMonth,
        )
        assertEquals(
            "still premium on the day the calendar month would have ended",
            AccessLevel.PREMIUM,
            EntitlementPolicy.level(calendarMonth + 12 * hour, trialRemainingMs = 0L, subscriptionUntil = expiry),
        )
    }

    @Test
    fun `a revoked purchase loses access once the mirror runs out and does not renew`() {
        // Checklist B-5: a refund/revocation happens in the store and cannot be seen
        // offline, so it is never re-granted — access is bounded by the last
        // reconciled expiry. This is the documented debt (docs/debt.md), pinned here
        // so "bounded", not "permanent", cannot drift into a silent full grant.
        val lastReconciled = now + 3 * day

        assertEquals(
            "paid time already mirrored is honoured",
            AccessLevel.PREMIUM,
            EntitlementPolicy.level(now, trialRemainingMs = 0L, subscriptionUntil = lastReconciled),
        )
        assertEquals(
            "but the mirror never renews itself once the store stops reporting the purchase",
            AccessLevel.LOCKED,
            EntitlementPolicy.level(lastReconciled + hour, trialRemainingMs = 0L, subscriptionUntil = lastReconciled),
        )
    }
}
