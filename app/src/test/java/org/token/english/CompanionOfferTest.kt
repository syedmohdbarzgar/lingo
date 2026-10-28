package org.token.english

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.CompanionOffer
import org.token.english.core.billing.CompanionStatus

/**
 * Checklist N-2/N-3: a learner who does not have the companion has to be told how the
 * subscription becomes free, a learner who already holds the grant must not be shown a
 * download button again (the v12 bug, where the rule hung off `AccessReason` instead of
 * the install), and a device holding an **untrusted** build is offered it as an update —
 * never granted, never hidden. One case per state, so the decision that three screens
 * share cannot drift.
 */
class CompanionOfferTest {

    @Test
    fun `a learner without the companion is told the free route`() {
        assertTrue(
            CompanionOffer.isOffered(
                companionStatus = CompanionStatus.ABSENT,
                access = AccessLevel.LOCKED,
                reason = AccessReason.NONE,
            ),
        )
        assertTrue(
            "a locked learner needs the free route most",
            CompanionOffer.isOffered(CompanionStatus.ABSENT, AccessLevel.LOCKED, AccessReason.NONE),
        )
    }

    @Test
    fun `a device that already holds the grant is never offered a download`() {
        assertFalse(
            CompanionOffer.isOffered(CompanionStatus.TRUSTED, AccessLevel.PREMIUM, AccessReason.COMPANION_APP),
        )
        // Stale-state guard: the trusted status alone decides, never the side signals.
        assertFalse(CompanionOffer.isOffered(CompanionStatus.TRUSTED, AccessLevel.LOCKED, AccessReason.NONE))
    }

    @Test
    fun `an untrusted install is offered as an update, never hidden`() {
        // N-3: the device has Zaribar under a key we do not trust — the learner
        // still needs the store page (to update), so the offer stands.
        assertTrue(
            "a locked learner with an untrusted build is told about the route",
            CompanionOffer.isOffered(CompanionStatus.UNTRUSTED, AccessLevel.LOCKED, AccessReason.NONE),
        )
        assertTrue(
            "an untrusted build is offered as an update, never hidden",
            CompanionOffer.isOffered(CompanionStatus.UNTRUSTED, AccessLevel.LOCKED, AccessReason.NONE),
        )
    }

    @Test
    fun `a paying subscriber is not upsold a free alternative`() {
        assertFalse(
            CompanionOffer.isOffered(CompanionStatus.ABSENT, AccessLevel.PREMIUM, AccessReason.SUBSCRIPTION),
        )
    }

    @Test
    fun `the companion reason without a trusted install never advertises the offer`() {
        assertFalse(
            CompanionOffer.isOffered(CompanionStatus.ABSENT, AccessLevel.LOCKED, AccessReason.COMPANION_APP),
        )
    }

    @Test
    fun `omitting the reason keeps the same decision`() {
        assertTrue(CompanionOffer.isOffered(companionStatus = CompanionStatus.ABSENT, access = AccessLevel.LOCKED))
        assertFalse(CompanionOffer.isOffered(companionStatus = CompanionStatus.TRUSTED, access = AccessLevel.LOCKED))
    }

    @Test
    fun `when the store link is gated off by the flavor, no state may offer it`() {
        // N-4: before the first Play release the `googlePlay` flavor flips
        // EXTERNAL_COMPANION_OFFER to false. Every device state then has to come
        // out false here, or the flag would be decorative and the Play build would
        // still route learners to Cafe Bazaar.
        for (status in CompanionStatus.entries) {
            for (access in AccessLevel.entries) {
                for (reason in AccessReason.entries) {
                    assertFalse(
                        "gated-off link leaked for $status/$access/$reason",
                        CompanionOffer.isOffered(
                            companionStatus = status,
                            access = access,
                            reason = reason,
                            storeLinkAllowed = false,
                        ),
                    )
                }
            }
        }
        // …while the very same states keep their behaviour when the link is allowed.
        assertTrue(
            CompanionOffer.isOffered(
                companionStatus = CompanionStatus.ABSENT,
                access = AccessLevel.LOCKED,
                reason = AccessReason.NONE,
                storeLinkAllowed = true,
            ),
        )
    }
}
