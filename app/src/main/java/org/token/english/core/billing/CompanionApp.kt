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
 * when its signing certificate matches [EXPECTED_SIGNING_SHA256]. That set is
 * **empty until the Zaribar release key digest is pasted in**, and an empty set
 * means the check cannot run; the detector then accepts the package id alone and
 * says so in the log (tracked as debt in AGENTS.md §10).
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
     * Empty = not configured yet (see the object KDoc): the grant then trusts the
     * package id alone and logs a warning.
     */
    val EXPECTED_SIGNING_SHA256: Set<String> = emptySet()

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
}

/** Whether the free-access companion is installed **and** its signature is trusted. */
fun interface CompanionDetector {
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
