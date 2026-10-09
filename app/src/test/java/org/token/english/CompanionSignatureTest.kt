package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.CompanionSignature

/**
 * The companion grant is only as trustworthy as its certificate check (N-1): a
 * repackaged APK that declares `org.token.zaribar` must not unlock the paid tier.
 * The Android lookup cannot run on the JVM, so the decision it feeds — digest
 * normalization and matching — is pinned here.
 */
class CompanionSignatureTest {

    private val releaseKey =
        "3A1F" + "9C7B".repeat(7) + "D2E4" // 64 hex chars, shape of a real digest

    @Test
    fun `normalize accepts keytool formatting`() {
        assertEquals(releaseKey, CompanionSignature.normalize(releaseKey.lowercase()))
        assertEquals("AABBCC", CompanionSignature.normalize("aa:bb:cc"))
        assertEquals("AABBCC", CompanionSignature.normalize("AA BB CC"))
    }

    @Test
    fun `a matching certificate is trusted`() {
        assertTrue(
            CompanionSignature.matches(
                expected = setOf(releaseKey),
                actual = listOf(releaseKey.lowercase()),
            ),
        )
    }

    @Test
    fun `a repackaged apk with a foreign key is rejected`() {
        assertFalse(
            CompanionSignature.matches(
                expected = setOf(releaseKey),
                actual = listOf("00".repeat(32)),
            ),
        )
    }

    @Test
    fun `an install with no readable certificate is rejected once a digest is configured`() {
        assertFalse(CompanionSignature.matches(setOf(releaseKey), emptyList()))
    }

    @Test
    fun `any of the configured digests may match - key rotation stays working`() {
        val rotated = "11".repeat(32)
        assertTrue(
            CompanionSignature.matches(
                expected = setOf(releaseKey, rotated),
                actual = listOf(rotated),
            ),
        )
    }

    @Test
    fun `an unconfigured digest set does not fail closed - it is a documented debt`() {
        assertTrue(CompanionSignature.matches(emptySet(), listOf("00".repeat(32))))
        assertTrue(CompanionSignature.matches(setOf("", "  "), listOf("00".repeat(32))))
        assertFalse(CompanionSignature.isConfigured(emptySet()))
        assertFalse(CompanionSignature.isConfigured(setOf(" ")))
        assertTrue(CompanionSignature.isConfigured(setOf(releaseKey)))
    }
}
