package org.token.english

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Checklist v15 N-4: the free-route offer opens Cafe Bazaar from the `bazaar` and
 * `myket` builds, and is gated **out** of `googlePlay` (executed v65 — Play's
 * external-content-links program is US-only and needs enrollment, so a link to
 * another store is not something to leave to a policy read). This test is what
 * makes both halves provable:
 *
 *  - every flavor declares the flag, so gating is one value, never a code change;
 *  - `googlePlay` declares it `false` and the other two `true`, so neither a
 *    forgotten revert nor a careless copy-paste can re-open the link in Play;
 *  - every surface that shows the offer or opens the store reads the flag, so a
 *    gated build cannot still link to Bazaar through a call site that ignores it;
 *  - `docs/release.md` names the flag, so the rule survives doc rewrites and
 *    release-checklist edits.
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

    /**
     * The `create("googlePlay") { … }` block, cut at the next flavor or at the
     * signing-config section — the build script declares the flavors first, so one
     * of those two markers always closes it.
     */
    private fun flavorBlock(name: String): String {
        val text = buildScript().readText()
        val start = text.indexOf("create(\"$name\")")
        assertTrue("flavor '$name' must exist in the build script", start >= 0)
        val candidates = listOf("create(\"", "signingConfigs", "buildTypes")
            .map { text.indexOf(it, start + 1) }
            .filter { it > start }
        val end = candidates.minOrNull() ?: text.length
        return text.substring(start, end)
    }

    @Test
    fun `the Play build gates the Bazaar offer out and the other stores keep it`() {
        assertTrue(
            "the googlePlay flavor must declare EXTERNAL_COMPANION_OFFER false — a Play build " +
                "that shows the offer links to another store (N-4, executed v65)",
            flavorBlock("googlePlay").contains("\"EXTERNAL_COMPANION_OFFER\", \"false\""),
        )
        for (store in listOf("bazaar", "myket")) {
            assertTrue(
                "the $store flavor must keep EXTERNAL_COMPANION_OFFER true — the free route is " +
                    "that flavor's offer, and Bazaar's policy allows it",
                flavorBlock(store).contains("\"EXTERNAL_COMPANION_OFFER\", \"true\""),
            )
        }
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
        // If the doc stops naming the flag, the rule that Play never links out is
        // only in a build script — and the next reader of the ritual cannot tell
        // why the value differs per flavor.
        val doc = listOf(File("../docs/release.md"), File("docs/release.md"))
            .firstOrNull(File::exists)
            ?: error("docs/release.md not found — run tests from the repo root or app/")
        assertTrue(
            "docs/release.md must name EXTERNAL_COMPANION_OFFER as the switch to flip",
            doc.readText().contains("EXTERNAL_COMPANION_OFFER"),
        )
    }
}
