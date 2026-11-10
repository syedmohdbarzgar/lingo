package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The splash → Home cross-fade changed hands at API 34: `overridePendingTransition`
 * (declared by the finishing [SplashActivity]) is deprecated in favour of
 * `overrideActivityTransition` (declared by the entered [MainActivity]). Declaring
 * both would make two overrides fight over one transition, and declaring neither
 * would drop the tuned fade on a whole platform generation — so the boundary is one
 * pure decision and is pinned here, at 33 and 34, which no screen test can see.
 *
 * It is a boolean rather than an enum because the call sites have to *guard* the new
 * API for lint, and a helper lint cannot see through turns a correct guard into two
 * `NewApi` findings (`@ChecksSdkIntAtLeast` on [SplashHandOff.usesNewApi] fixes that).
 */
class SplashHandOffTest {

    @Test
    fun `below 34 the finishing splash declares the fade`() {
        assertFalse(SplashHandOff.usesNewApi(24))
        assertFalse(SplashHandOff.usesNewApi(30))
        assertFalse(SplashHandOff.usesNewApi(33))
    }

    @Test
    fun `34 and above the incoming activity declares it`() {
        assertTrue(SplashHandOff.usesNewApi(34))
        assertTrue(SplashHandOff.usesNewApi(35))
        assertTrue(SplashHandOff.usesNewApi(40))
    }

    @Test
    fun `the boundary is the platform level the new api landed on`() {
        assertEquals(34, SplashHandOff.NEW_API_LEVEL)
        assertFalse(SplashHandOff.usesNewApi(SplashHandOff.NEW_API_LEVEL - 1))
        assertTrue(SplashHandOff.usesNewApi(SplashHandOff.NEW_API_LEVEL))
    }
}
