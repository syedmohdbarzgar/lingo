package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.core.billing.CompanionApp
import org.token.english.core.billing.CompanionSignature

/**
 * The free-access companion grant hangs on a package id — the same string the
 * manifest's `<queries>` block declares and Cafe Bazaar publishes — plus the
 * signing digest that decides whether the install can be trusted (N-1). Both
 * fail silently when they are wrong, so the contract is pinned here.
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
    fun `every configured signing digest is a well-formed sha-256 hex string`() {
        // A truncated or colon-typo'd paste would silently stop matching the real
        // companion, so the constant is validated even while it is still empty.
        val malformed = CompanionApp.EXPECTED_SIGNING_SHA256.filterNot { digest ->
            CompanionSignature.normalize(digest).let { it.length == 64 && it.all { c -> c in "0123456789ABCDEF" } }
        }
        assertTrue("malformed signing digest(s): $malformed", malformed.isEmpty())
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
