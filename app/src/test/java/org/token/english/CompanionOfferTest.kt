package org.token.english

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.CompanionOffer

/**
 * Checklist N-2: a learner who does not have the companion has to be told how the
 * subscription becomes free, and a learner who already holds the grant must not be
 * shown a download button again (the v12 bug, where the rule hung off
 * `AccessReason` instead of the install). One case per state, so the decision that
 * three screens now share cannot drift.
 */
class CompanionOfferTest {

    @Test
    fun `a learner without the companion is told the free route`() {
        assertTrue(
            CompanionOffer.isOffered(
                companionInstalled = false,
                access = AccessLevel.TRIAL,
                reason = AccessReason.TRIAL,
            ),
        )
        assertTrue(
            "a locked learner needs the free route most",
            CompanionOffer.isOffered(false, AccessLevel.LOCKED, AccessReason.NONE),
        )
    }

    @Test
    fun `a device that already holds the grant is never offered a download`() {
        assertFalse(
            CompanionOffer.isOffered(true, AccessLevel.PREMIUM, AccessReason.COMPANION_APP),
        )
        // Stale-state guard: the install flag alone decides, never the side signals.
        assertFalse(CompanionOffer.isOffered(true, AccessLevel.TRIAL, AccessReason.TRIAL))
    }

    @Test
    fun `a paying subscriber is not upsold a free alternative`() {
        assertFalse(
            CompanionOffer.isOffered(false, AccessLevel.PREMIUM, AccessReason.SUBSCRIPTION),
        )
    }

    @Test
    fun `the companion reason without the install never advertises the offer`() {
        assertFalse(
            CompanionOffer.isOffered(false, AccessLevel.TRIAL, AccessReason.COMPANION_APP),
        )
    }

    @Test
    fun `omitting the reason keeps the same decision`() {
        assertTrue(CompanionOffer.isOffered(companionInstalled = false, access = AccessLevel.LOCKED))
        assertFalse(CompanionOffer.isOffered(companionInstalled = true, access = AccessLevel.LOCKED))
    }
}
