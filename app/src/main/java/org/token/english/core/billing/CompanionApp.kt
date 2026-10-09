package org.token.english.core.billing

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

/**
 * Free-access companion app (product decision, Oct 2026).
 *
 * While the Zaribar app (`org.token.zaribar`) is installed on the device, this
 * app's subscription is **free**: [EntitlementPolicy] grants PREMIUM without a
 * purchase. The grant follows the install — uninstalling the companion ends the
 * free access again — so it is re-checked on every foreground, never cached.
 *
 * Everything here is local and offline: one PackageManager lookup plus a store
 * deep link. No server, no account, no network.
 */
object CompanionApp {

    /** Package id of the companion app that grants free access while installed. */
    const val PACKAGE = "org.token.zaribar"

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

/** Whether the free-access companion app is installed right now. */
fun interface CompanionDetector {
    fun isInstalled(): Boolean
}

/**
 * Real detector. Requires `org.token.zaribar` in the manifest's `<queries>` block —
 * without it package visibility on API 30+ hides the companion and the free
 * subscription would never activate.
 */
class PackageManagerCompanionDetector(private val context: Context) : CompanionDetector {

    override fun isInstalled(): Boolean = try {
        val pm = context.packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(CompanionApp.PACKAGE, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(CompanionApp.PACKAGE, 0)
        }
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
