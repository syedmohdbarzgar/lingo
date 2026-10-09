package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.CompanionApp

/**
 * The free-access companion grant hangs on a single package id — the same string
 * the manifest's `<queries>` block declares and Cafe Bazaar publishes. A typo
 * would silently disable the free subscription with no visible failure, so the
 * contract is pinned here.
 */
class CompanionAppTest {

    @Test
    fun `companion package id is the zaribar app`() {
        assertEquals("org.token.zaribar", CompanionApp.PACKAGE)
    }

    @Test
    fun `bazaar links point at the companion product page`() {
        assertEquals("bazaar://details?id=org.token.zaribar", CompanionApp.bazaarDeepLink())
        assertEquals("https://cafebazaar.ir/app/org.token.zaribar", CompanionApp.bazaarWebUrl())
    }

    @Test
    fun `links follow a custom package id`() {
        assertTrue(
            CompanionApp.bazaarDeepLink("com.example.other").endsWith("?id=com.example.other"),
        )
        assertTrue(
            CompanionApp.bazaarWebUrl("com.example.other").endsWith("/app/com.example.other"),
        )
    }
}
