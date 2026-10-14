package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.CompanionApp
import org.token.english.core.billing.CompanionSignature
import java.io.File

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

    // --- N-1: the release gate's view of the constant -------------------------

    private fun companionSource(): String {
        val candidates = listOf(
            File("src/main/java/org/token/english/core/billing/CompanionApp.kt"),
            File("app/src/main/java/org/token/english/core/billing/CompanionApp.kt"),
        )
        return candidates.first { it.exists() }.readText()
    }

    @Test
    fun `a valid digest passes the release gate while an empty set fails it`() {
        // The false-negative this guards against: a real digest the gate quietly
        // rejects (checklist N-1). Only `emptySet` / blank / absent count as missing.
        assertTrue(CompanionSignature.declarationIsConfigured("""setOf("AA:BB:CC")"""))
        assertTrue(CompanionSignature.declarationIsConfigured("""setOf(\n        "AA:BB",\n    )"""))
        assertFalse(CompanionSignature.declarationIsConfigured("emptySet()"))
        assertFalse(CompanionSignature.declarationIsConfigured(""))
        assertFalse(CompanionSignature.declarationIsConfigured(null))
    }

    @Test
    fun `the gate reads the same truth as the runtime constant`() {
        // The build script parses the declaration with its own regex; if the two ever
        // disagree (a stray `=` on the KDoc line, a renamed constant) the release gate
        // would be checking something other than what ships. Asserted against the real
        // source, so the invariant is enforced on every build.
        val declared = CompanionSignature.declaredValue(companionSource())
        assertNotNull("the gate must be able to find the declaration at all", declared)
        assertEquals(
            "the build.gradle gate and CompanionApp.EXPECTED_SIGNING_SHA256 disagree",
            CompanionSignature.isConfigured(CompanionApp.EXPECTED_SIGNING_SHA256),
            CompanionSignature.declarationIsConfigured(declared),
        )
    }
}
