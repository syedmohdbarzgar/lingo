package org.token.english.core.billing

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.token.english.BuildConfig

/**
 * The app's own page in the marketplace **this build ships to** — the channel behind
 * Settings → «درباره و قوانین» → «ارتباط با ناشر». It lives next to [CompanionApp],
 * which opens a *store page* the same way (deep link, then web fallback): one place
 * per repo for "open a marketplace page".
 *
 * Each flavor carries the store's *documented* way in (`STORE_DEEP_LINK`,
 * `STORE_WEB_BASE`, `STORE_PACKAGE` in `app/build.gradle.kts`) instead of one shared
 * guess, because the three stores do not document the same thing:
 *
 *  - Cafe Bazaar — `bazaar://details?id=…`, pinned to `com.farsitel.bazaar`
 *    (its official intent guide), page at `cafebazaar.ir/app/<package>`.
 *  - Myket — `myket://details?id=…` (its knowledge base); no pinning, because Myket
 *    documents its own scheme only — and that scheme already resolves to Myket alone.
 *  - Google Play — the HTTPS listing URL with `setPackage("com.android.vending")`
 *    (Google's "Linking to Google Play" guide); the `market://details` form is not
 *    what that guide documents for an app page.
 *
 * Whichever value a flavor supplies, this is a plain `ACTION_VIEW`: it opens the store
 * (or the browser), needs no permission, and makes no request itself — the learning
 * core stays offline (docs/connectivity.md).
 */
object StorePage {

    /**
     * Deep link that opens the app's page **inside the store app**. Pure string
     * building, so each flavor's link is unit-testable without Android (the JVM
     * cannot resolve intents).
     */
    fun deepLink(
        packageId: String = BuildConfig.APPLICATION_ID,
        prefix: String = BuildConfig.STORE_DEEP_LINK,
    ): String = prefix + packageId

    /**
     * Public web page — the fallback when the store app is not installed (a device
     * that never had Bazaar/Myket has no handler for the scheme).
     */
    fun webUrl(
        packageId: String = BuildConfig.APPLICATION_ID,
        base: String = BuildConfig.STORE_WEB_BASE,
    ): String = base + packageId

    /**
     * Opens the store page: the flavor's deep link first (pinned to the market
     * package only when that flavor documents one), then the public web page.
     *
     * Never throws — a device with neither the store nor a browser simply does
     * nothing visible, the same stance as [CompanionApp.openDownloadPage].
     */
    fun open(context: Context, packageId: String = BuildConfig.APPLICATION_ID) {
        val marketPackage = BuildConfig.STORE_PACKAGE
        val deepLink = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink(packageId)))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .apply { if (marketPackage.isNotBlank()) setPackage(marketPackage) }
        if (runCatching { context.startActivity(deepLink) }.isSuccess) return // runCatching-ok: not a suspend call (B-9)
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(webUrl(packageId)))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}
