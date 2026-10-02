package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.EntitlementPolicy

class EntitlementPolicyTest {

    private val now = 1_759_300_000_000L
    private val day = 24L * 60 * 60 * 1000

    @Test
    fun `subscription wins over everything while active`() {
        val level = EntitlementPolicy.level(
            now = now,
            trialRemainingMs = 0L, // trial long used up
            subscriptionUntil = now + 60_000,
        )
        assertEquals(AccessLevel.PREMIUM, level)
    }

    @Test
    fun `expired subscription falls back to trial while trial lasts`() {
        val level = EntitlementPolicy.level(
            now = now,
            trialRemainingMs = 3 * day,
            subscriptionUntil = now - day,
        )
        assertEquals(AccessLevel.TRIAL, level)
    }

    @Test
    fun `active subscription outranks a running trial`() {
        val level = EntitlementPolicy.level(
            now = now,
            trialRemainingMs = day,
            subscriptionUntil = now + day,
        )
        assertEquals(AccessLevel.PREMIUM, level)
    }

    @Test
    fun `trial over and no subscription means locked`() {
        val level = EntitlementPolicy.level(
            now = now,
            trialRemainingMs = 0L,
            subscriptionUntil = 0L,
        )
        assertEquals(AccessLevel.LOCKED, level)
    }

    @Test
    fun `trial never started and no subscription means locked`() {
        val level = EntitlementPolicy.level(
            now = now,
            trialRemainingMs = 0L,
            subscriptionUntil = 0L,
        )
        assertEquals(AccessLevel.LOCKED, level)
    }

    @Test
    fun `subscription that ended exactly now is no longer premium`() {
        val level = EntitlementPolicy.level(
            now = now,
            trialRemainingMs = 0L,
            subscriptionUntil = now,
        )
        assertEquals(AccessLevel.LOCKED, level)
    }
}
