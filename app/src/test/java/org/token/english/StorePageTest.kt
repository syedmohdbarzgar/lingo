package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import org.token.english.core.billing.StorePage

/**
 * The publisher-contact link (Settings → «درباره و قوانین» → «ارتباط با ناشر») must
 * open the app's page in the store **this build ships to**, and each of the three
 * stores documents a different way in. That makes the values a per-flavor fact
 * rather than a shared guess, and it is exactly the kind of fact that rots quietly:
 *
 *  - a flavor that forgets `STORE_DEEP_LINK`/`STORE_WEB_BASE` compiles and ships a
 *    contact button that opens nothing;
 *  - a shared `market://details?id=` shortcut compiles too, and is *not* the form any
 *    of these three stores documents for an app page (a device with several stores
 *    installed would then route the tap somewhere unexpected instead of its own store).
 *
 * So the flavors' declarations and the wiring are asserted here, next to
 * [PlayStoreLinkGateTest] which guards the same build script for the Companion offer.
 */
class StorePageTest {

    /** Same lookup as [PlayStoreLinkGateTest]: tests may run from the repo root or app/. */
    private fun sourceFile(path: String): File =
        listOf(File(path), File("app/$path")).firstOrNull(File::exists)
            ?: error("$path not found — run tests from the repo root or app/")

    private fun buildScript(): File =
        listOf(File("build.gradle.kts"), File("app/build.gradle.kts"))
            .firstOrNull { it.exists() && it.readText().contains("productFlavors") }
            ?: error("app/build.gradle.kts not found — run tests from the repo root or app/")

    /**
     * A `buildConfigField` value exactly as the script declares it — the Kotlin string
     * literal `"\"<value>\""`. Matching the declaration form rather than the bare text
     * is what keeps these checks honest: a comment may mention a URL (and the comments
     * about *why* `market://` is not used do mention it) without that counting as a
     * declared value.
     */
    private fun declaredValue(value: String): String = "\"\\\"" + value + "\\\"\""

    // ---- the pure URL builders -------------------------------------------------

    @Test
    fun `deep link puts the package id after the flavor's prefix`() {
        assertEquals("bazaar://details?id=org.token.english", StorePage.deepLink("org.token.english", "bazaar://details?id="))
        assertEquals("myket://details?id=org.token.english", StorePage.deepLink("org.token.english", "myket://details?id="))
    }

    @Test
    fun `web fallback is the flavor's store page for this package`() {
        assertEquals("https://cafebazaar.ir/app/org.token.english", StorePage.webUrl("org.token.english", "https://cafebazaar.ir/app/"))
        assertEquals("https://myket.ir/app/org.token.english", StorePage.webUrl("org.token.english", "https://myket.ir/app/"))
        assertEquals(
            "https://play.google.com/store/apps/details?id=org.token.english",
            StorePage.webUrl("org.token.english", "https://play.google.com/store/apps/details?id="),
        )
    }

    @Test
    fun `the app's real package id reaches both links`() {
        // BuildConfig.APPLICATION_ID is the id in the manifest — the link must point at
        // this app, not at a placeholder left behind by a copy-paste.
        assertEquals(BuildConfig.APPLICATION_ID, "org.token.english")
        assertTrue(StorePage.deepLink().endsWith(BuildConfig.APPLICATION_ID))
        assertTrue(StorePage.webUrl().endsWith(BuildConfig.APPLICATION_ID))
    }

    // ---- the per-flavor declarations -------------------------------------------

    @Test
    fun `every flavor declares its store page fields`() {
        val text = buildScript().readText()
        for (field in listOf("STORE_DEEP_LINK", "STORE_WEB_BASE", "STORE_PACKAGE")) {
            val declared = Regex("buildConfigField\\(\\s*\"String\",\\s*\"" + field + "\"")
                .findAll(text)
                .count()
            assertEquals(
                "each of the three flavors must declare $field — otherwise that build's " +
                    "«ارتباط با ناشر» button opens nothing",
                3,
                declared,
            )
        }
    }

    @Test
    fun `each flavor uses the scheme its own store documents`() {
        val text = buildScript().readText()
        // Cafe Bazaar's official intent guide, Myket's knowledge base, and Google's
        // "Linking to Google Play" guide — the three sources behind these values.
        assertTrue(
            "bazaar's documented deep link missing",
            text.contains(declaredValue("bazaar://details?id=")),
        )
        assertTrue(
            "the market package the Bazaar intent guide pins to is missing",
            text.contains(declaredValue("com.farsitel.bazaar")),
        )
        assertTrue(
            "myket's documented deep link missing",
            text.contains(declaredValue("myket://details?id=")),
        )
        assertTrue("the public Myket page is missing", text.contains(declaredValue("https://myket.ir/app/")))
        assertTrue(
            "Play must lead with the documented HTTPS listing URL",
            text.contains(declaredValue("https://play.google.com/store/apps/details?id=")),
        )
        assertTrue(
            "Google's documented setPackage target is missing",
            text.contains(declaredValue("com.android.vending")),
        )
        assertTrue(
            "the guessed market:// form must not come back as a declared value — it is " +
                "not the form any of these stores documents for an app page",
            !text.contains(declaredValue("market://details?id=")),
        )
    }

    // ---- the wiring ------------------------------------------------------------

    @Test
    fun `the contact page opens the store page`() {
        val screen = sourceFile("src/main/java/org/token/english/feature/settings/SettingsScreen.kt").readText()
        assertTrue(
            "«ارتباط با ناشر» must route through StorePage so the flavor's own store page opens",
            screen.contains("StorePage.open("),
        )
    }
}
