package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.EntitlementPolicy

/**
 * The gating rule. There is no free trial any more (product decision): a device
 * opens on LOCKED unless it holds a live subscription or the companion grant, and
 * the reason travels with the level so no screen calls a grant a purchase.
 */
class EntitlementPolicyTest {

    private val now = 1_759_300_000_000L
    private val day = 24L * 60 * 60 * 1000

    @Test
    fun `subscription wins over everything while active`() {
        val level = EntitlementPolicy.level(
            now = now,
            subscriptionUntil = now + 60_000,
        )
        assertEquals(AccessLevel.PREMIUM, level)
    }

    @Test
    fun `a fresh install with nothing active is locked`() {
        val entitlement = EntitlementPolicy.entitlement(now = now, subscriptionUntil = 0L)
        assertEquals(AccessLevel.LOCKED, entitlement.level)
        assertEquals(AccessReason.NONE, entitlement.reason)
    }

    @Test
    fun `an expired subscription is locked - there is no trial to fall back to`() {
        val level = EntitlementPolicy.level(
            now = now,
            subscriptionUntil = now - day,
        )
        assertEquals(AccessLevel.LOCKED, level)
    }

    @Test
    fun `subscription that ended exactly now is no longer premium`() {
        val level = EntitlementPolicy.level(
            now = now,
            subscriptionUntil = now,
        )
        assertEquals(AccessLevel.LOCKED, level)
    }

    // ---------------------------------------------- companion app (org.token.zaribar)

    @Test
    fun `installed companion app grants free premium without a purchase`() {
        val entitlement = EntitlementPolicy.entitlement(
            now = now,
            subscriptionUntil = 0L,
            companionAppInstalled = true,
        )
        assertEquals(AccessLevel.PREMIUM, entitlement.level)
        // The free grant must never be reported as a paid subscription.
        assertEquals(AccessReason.COMPANION_APP, entitlement.reason)
    }

    @Test
    fun `removing the companion app takes the free access away again`() {
        val entitlement = EntitlementPolicy.entitlement(
            now = now,
            subscriptionUntil = 0L,
            companionAppInstalled = false,
        )
        assertEquals(AccessLevel.LOCKED, entitlement.level)
        assertEquals(AccessReason.NONE, entitlement.reason)
    }

    @Test
    fun `the companion grant outranks an expired subscription`() {
        val entitlement = EntitlementPolicy.entitlement(
            now = now,
            subscriptionUntil = now - day,
            companionAppInstalled = true,
        )
        assertEquals(AccessLevel.PREMIUM, entitlement.level)
        assertEquals(AccessReason.COMPANION_APP, entitlement.reason)
    }

    @Test
    fun `a paid subscription is still reported as a subscription with the companion installed`() {
        val entitlement = EntitlementPolicy.entitlement(
            now = now,
            subscriptionUntil = now + day,
            companionAppInstalled = true,
        )
        assertEquals(AccessLevel.PREMIUM, entitlement.level)
        assertEquals(AccessReason.SUBSCRIPTION, entitlement.reason)
    }

    @Test
    fun `the level shortcut keeps working and honours the companion flag`() {
        assertEquals(AccessLevel.LOCKED, EntitlementPolicy.level(now, 0L))
        assertEquals(
            AccessLevel.PREMIUM,
            EntitlementPolicy.level(now, 0L, companionAppInstalled = true),
        )
    }
}
