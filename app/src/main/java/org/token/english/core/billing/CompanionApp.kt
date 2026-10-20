package org.token.english.core.billing

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * Free-access companion app (product decision, Oct 2026).
 *
 * While the Zaribar app (`org.token.zaribar`) is installed on the device, this
 * app's subscription is **free**: [EntitlementPolicy] grants PREMIUM without a
 * purchase. The grant follows the install — uninstalling the companion ends the
 * free access again — so it is re-checked on every foreground, never cached.
 *
 * Trust (checklist N-1): the package id alone is spoofable — any APK declaring
 * `org.token.zaribar` would unlock the paid tier — so the install is only trusted
 * when its signing certificate matches [EXPECTED_SIGNING_SHA256]. That set now
 * holds the Zaribar certificate installed on the device, so the grant is verified
 * rather than assumed. Were it emptied again the check could not run: debug builds
 * would accept the package id alone and say so in the log, while
 * `app/build.gradle.kts` reads this constant and fails the assemble/bundle task of
 * every release variant — the same fail-closed stance as `bazaarRsaKey`.
 *
 * Everything here is local and offline: a PackageManager lookup plus a store deep
 * link. No server, no account, no network.
 */
object CompanionApp {

    /** Package id of the companion app that grants free access while installed. */
    const val PACKAGE = "org.token.zaribar"

    /**
     * SHA-256 digests of the companion's **release signing certificate(s)**, hex
     * without separators, case-insensitive (a `keytool`-style `AA:BB:…` paste also
     * works — [CompanionSignature] normalizes it). Fill from the Zaribar release
     * key: `keytool -printcert -jarfile zaribar.apk`.
     *
     * Multiple entries are allowed so a key rotation does not break the grant.
     * An empty set means "not configured": debug builds then trust the package id
     * alone and log a warning, while release builds refuse to assemble (see the
     * object KDoc). The value below came from the Zaribar 3.4.10 build installed on
     * the device (certificate `CN=wolfandmoon`) and was **verified against the APK
     * Cafe Bazaar publishes** (versionCode 16, pulled via the store's download-info
     * API in Oct 2026 — same V2 digest, so store installs pass; N-1). Still re-read
     * it from the store build before each release (`docs/release.md`): a different
     * key there fails closed for store installs instead of opening the bypass.
     */
    val EXPECTED_SIGNING_SHA256: Set<String> = setOf(
        // `apksigner verify --print-certs`: "V2 Signer: certificate SHA-256 digest".
        "cf5865350615d32e4f984f5b2e71a286426c35ef8e33a9a971b380b70b64fd6b",
    )

    /**
     * Bazaar deep link that opens the companion's product page in the Bazaar app.
     * Built as a plain string so the constant is unit-testable without Android.
     */
    fun bazaarDeepLink(packageId: String = PACKAGE): String = "bazaar://details?id=$packageId"

    /** Cafe Bazaar web page — the fallback when the Bazaar app is not installed. */
    fun bazaarWebUrl(packageId: String = PACKAGE): String = "https://cafebazaar.ir/app/$packageId"

    /**
     * Opens the companion's Cafe Bazaar page. Tries the Bazaar deep link first and
     * falls back to the web page when no app handles `bazaar://` (a device without
     * the store installed). Never throws — a failed launch is not user-visible
     * beyond nothing happening.
     *
     * Shown in every flavor on purpose: the companion is published on Cafe Bazaar
     * only, so even a Google Play install has no other way to get it.
     */
    fun openDownloadPage(context: Context) {
        val deepLink = Intent(Intent.ACTION_VIEW, Uri.parse(bazaarDeepLink()))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (runCatching { context.startActivity(deepLink) }.isSuccess) return
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(bazaarWebUrl()))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}

/**
 * Certificate comparison for the companion grant — pure, no Android types, unit
 * tested (`CompanionSignatureTest`). The Android lookup itself cannot run on the
 * JVM, so the decision it feeds is kept here.
 */
object CompanionSignature {

    /** Canonical form: uppercase hex with separators removed (`AA:BB` ≡ `aabb`). */
    fun normalize(digest: String): String = digest.filter { it.isLetterOrDigit() }.uppercase()

    /** True once at least one usable digest is configured. */
    fun isConfigured(expected: Set<String>): Boolean = expected.any { normalize(it).isNotEmpty() }

    /**
     * True when one of the installed certificate [actual] digests is expected.
     *
     * An unconfigured [expected] set passes: verification is not set up yet, and the
     * caller reports that (it is a known debt, not a silent bypass). A configured set
     * that matches nothing fails closed — a repackaged APK gets no free access.
     */
    fun matches(expected: Set<String>, actual: Collection<String>): Boolean {
        val wanted = expected.map(::normalize).filter { it.isNotEmpty() }.toSet()
        if (wanted.isEmpty()) return true
        return actual.any { normalize(it) in wanted }
    }

    /**
     * RHS of the `EXPECTED_SIGNING_SHA256` declaration as written in [source], or
     * null when the declaration is absent. Mirrors the shape
     * `app/build.gradle.kts` reads to fail a release while the digest is empty, kept
     * here so a unit test can prove the gate's view and the runtime constant agree
     * (checklist N-1) instead of trusting two regexes to stay in step.
     */
    fun declaredValue(source: String): String? =
        Regex("""val EXPECTED_SIGNING_SHA256[^\n]*?=\s*([^\n]*)""")
            .find(source)
            ?.groupValues?.get(1)?.trim()

    /**
     * True when a declared RHS is a real value the release gate accepts. `emptySet()`,
     * blank and null are all "not configured" — the exact stance in the Gradle gate, so
     * a valid digest cannot silently start failing the release build.
     */
    fun declarationIsConfigured(declared: String?): Boolean =
        !declared.isNullOrEmpty() && !declared.startsWith("emptySet")
}

/** Whether the free-access companion is installed, and whether we trust it. */
interface CompanionDetector {

    /** True when the companion package is present, whoever signed it (N-3). */
    fun isInstalled(): Boolean

    /** True when it is installed **and** its signature is trusted (N-1). */
    fun isTrusted(): Boolean
}

/**
 * Real detector: one PackageManager lookup for the companion's signing
 * certificate, compared against [CompanionApp.EXPECTED_SIGNING_SHA256].
 *
 * Requires `org.token.zaribar` in the manifest's `<queries>` block — without it
 * package visibility on API 30+ hides the companion and the free subscription
 * would never activate.
 */
class PackageManagerCompanionDetector(
    private val context: Context,
    private val expectedDigests: Set<String> = CompanionApp.EXPECTED_SIGNING_SHA256,
) : CompanionDetector {

    /** The unconfigured-digest warning is worth saying once, not on every resume. */
    @Volatile
    private var warnedUnconfigured = false

    /** Presence alone (N-3): a Zaribar under a foreign key still counts as installed. */
    override fun isInstalled(): Boolean = try {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(CompanionApp.PACKAGE, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    override fun isTrusted(): Boolean {
        val digests = try {
            signingDigests(context.packageManager)
        } catch (_: PackageManager.NameNotFoundException) {
            return false
        }
        val trusted = CompanionSignature.matches(expectedDigests, digests)
        when {
            !trusted -> Log.w(
                TAG,
                "companion ${CompanionApp.PACKAGE} is signed by an unexpected key — free access denied",
            )

            !CompanionSignature.isConfigured(expectedDigests) && !warnedUnconfigured -> {
                warnedUnconfigured = true
                Log.w(
                    TAG,
                    "companion signing digest is not configured — free access trusts the " +
                        "package id alone; set CompanionApp.EXPECTED_SIGNING_SHA256",
                )
            }
        }
        return trusted
    }

    /** Certificate digests of the installed companion, or empty when unavailable. */
    private fun signingDigests(pm: PackageManager): List<String> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> digestsOf(
            pm.getPackageInfo(
                CompanionApp.PACKAGE,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong()),
            ),
        )

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
            @Suppress("DEPRECATION")
            digestsOf(pm.getPackageInfo(CompanionApp.PACKAGE, PackageManager.GET_SIGNING_CERTIFICATES))
        }

        else -> {
            @Suppress("DEPRECATION")
            val info = pm.getPackageInfo(CompanionApp.PACKAGE, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION")
            val signatures = info.signatures.orEmpty().toList()
            signatures.map { sha256(it.toByteArray()) }
        }
    }

    /**
     * Current signers when the APK is multi-signed, otherwise the moment-in-time
     * history — the latter is what keeps a rotated-but-still-valid key working.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun digestsOf(info: PackageInfo): List<String> {
        val signing = info.signingInfo ?: return emptyList()
        val signers =
            if (signing.hasMultipleSigners()) signing.apkContentsSigners
            else signing.signingCertificateHistory
        return signers.orEmpty().map { sha256(it.toByteArray()) }
    }

    private fun sha256(bytes: ByteArray): String =
        java.security.MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02X".format(it) }

    private companion object {
        const val TAG = "CompanionApp"
    }
}
