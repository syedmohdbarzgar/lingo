package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Checklist v15 N-4: the free-route offer opens Cafe Bazaar in every flavor today
 * (decided v14, while `googlePlay` is not published on Play). Before the first
 * Play release the `EXTERNAL_COMPANION_OFFER` flag of that flavor flips to
 * `false` — and this test is what makes the flip forget-proof and provable:
 *
 *  - every flavor declares the flag, so gating is one value, never a code change;
 *  - every surface that shows the offer or opens the store reads the flag, so a
 *    gated build cannot still link to Bazaar through a call site that ignores it;
 *  - `docs/release.md` names the flag, so the re-decision point survives doc
 *    rewrites and release-checklist edits.
 */
class PlayStoreLinkGateTest {

    /**
     * From `app/` the build script is `build.gradle.kts`; from the repo root it is
     * `app/build.gradle.kts` — and the root has one too, so the file must also
     * contain `productFlavors` before it is accepted.
     */
    private fun buildScript(): File =
        listOf(File("build.gradle.kts"), File("app/build.gradle.kts"))
            .firstOrNull { it.exists() && it.readText().contains("productFlavors") }
            ?: error("app/build.gradle.kts not found — run tests from the repo root or app/")

    /** Source file, from either working directory (the repo's test convention). */
    private fun sourceFile(path: String): File =
        listOf(File(path), File("app/$path")).firstOrNull(File::exists)
            ?: error("$path not found — run tests from the repo root or app/")

    @Test
    fun `every flavor declares the companion-offer flag`() {
        val declared = Regex("""buildConfigField\("boolean", "EXTERNAL_COMPANION_OFFER", "(?:true|false)"\)""")
            .findAll(buildScript().readText())
            .count()
        assertEquals(
            "each of the three flavors must declare EXTERNAL_COMPANION_OFFER, so the " +
                "Play gate (N-4) is one value away and never a code change",
            3,
            declared,
        )
    }

    @Test
    fun `every surface that shows the offer or opens the store reads the flag`() {
        // Wiring proof: flipping the flag has to actually reach each call site —
        // otherwise a gated googlePlay build could hide the rule and still link to
        // Bazaar through a screen (or openDownloadPage itself) that ignores it.
        val surfaces = listOf(
            "src/main/java/org/token/english/feature/home/HomeScreen.kt",
            "src/main/java/org/token/english/feature/paywall/PaywallScreen.kt",
            "src/main/java/org/token/english/feature/settings/SettingsScreen.kt",
            "src/main/java/org/token/english/core/billing/CompanionApp.kt",
        )
        for (path in surfaces) {
            assertTrue(
                "$path must read BuildConfig.EXTERNAL_COMPANION_OFFER (N-4 wiring)",
                sourceFile(path).readText().contains("EXTERNAL_COMPANION_OFFER"),
            )
        }
    }

    @Test
    fun `the Play re-decision is named in the release ritual`() {
        // The flag is only safe because release.md re-opens the decision before
        // the first Play release. If the doc stops naming the flag, the tripwire
        // is gone and the flip would be forgotten — exactly the failure the
        // checklist v15 N-4 item exists to prevent.
        val doc = listOf(File("../docs/release.md"), File("docs/release.md"))
            .firstOrNull(File::exists)
            ?: error("docs/release.md not found — run tests from the repo root or app/")
        assertTrue(
            "docs/release.md must name EXTERNAL_COMPANION_OFFER as the switch to flip",
            doc.readText().contains("EXTERNAL_COMPANION_OFFER"),
        )
    }
}
