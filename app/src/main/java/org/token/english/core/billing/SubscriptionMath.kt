package org.token.english.core.billing

import java.util.Calendar

/**
 * Client-side expiry math for auto-renewing subscriptions.
 *
 * The app is offline-first and has no server, so the store's "purchase active"
 * answer is combined with the purchase time: while the store keeps reporting
 * the purchase, renewal boundaries are rolled forward from the purchase time
 * using calendar months/years (the way stores bill). This is an approximation —
 * only a server can verify billing cycles — but it ties the local mirror to the
 * purchase date instead of blindly granting `now + grace` (checklist P3).
 */
object SubscriptionMath {

    /** Next renewal boundary after [purchaseTimeMs], strictly after [nowMs]. */
    fun nextRenewalBoundary(purchaseTimeMs: Long, monthly: Boolean, nowMs: Long): Long {
        if (purchaseTimeMs <= 0L) return nowMs
        var boundary = purchaseTimeMs
        if (boundary > nowMs) return boundary
        val cal = Calendar.getInstance().apply { timeInMillis = purchaseTimeMs }
        var guard = 0
        while (boundary <= nowMs && guard < 1200) {
            if (monthly) cal.add(Calendar.MONTH, 1) else cal.add(Calendar.YEAR, 1)
            boundary = cal.timeInMillis
            guard++
        }
        return boundary
    }

    /** Billing period of a store product id; unknown products are monthly. */
    fun isMonthly(productId: String): Boolean = productId != ProductIds.YEARLY

    /** Length of one fixed-length plan in millis: a flat 30 or 365 days. */
    fun periodMillis(monthly: Boolean): Long =
        if (monthly) MONTH_MILLIS else YEAR_MILLIS

    /**
     * Reinstall-proof expiry for a store that sells **fixed-length access** instead
     * of real subscriptions (Myket — AGENTS.md §4a). There is no renewal, so the
     * owned purchase's time is the only receipt left after a reinstall wipes local
     * DataStore: the period is re-derived as `purchaseTime + period`. A missing or
     * nonsensical purchase time grants nothing rather than unlocking forever.
     */
    fun flatPeriodExpiry(purchaseTimeMs: Long, monthly: Boolean): Long =
        if (purchaseTimeMs <= 0L) 0L else purchaseTimeMs + periodMillis(monthly)

    private const val MONTH_MILLIS: Long = 30L * 24 * 60 * 60 * 1000
    private const val YEAR_MILLIS: Long = 365L * 24 * 60 * 60 * 1000
}
