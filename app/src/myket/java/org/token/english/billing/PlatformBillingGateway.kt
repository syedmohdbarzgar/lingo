package org.token.english.billing

import android.app.Activity
import android.content.Context
import ir.myket.billingclient.IabHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import org.token.english.BuildConfig
import org.token.english.core.billing.BillingGateway
import org.token.english.core.billing.ProductIds
import org.token.english.core.billing.PurchaseOutcome
import org.token.english.core.billing.Storefront
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.domain.repository.SettingsRepository
import kotlin.coroutines.resume

/**
 * Myket billing (myket flavor only).
 *
 * Myket officially does NOT support subscription products
 * (myket.ir knowledge base: «مایکت از اشتراک پشتیبانی نمی‌کند»), so the two plans are
 * sold as CONSUMABLE products in the Myket panel:
 *   sub_monthly → 30 days of access, sub_yearly → 365 days.
 *
 * Reinstall/restore (checklist P3): a purchase is deliberately NOT consumed the
 * moment it is bought. While it is owned, [checkSubscription] re-derives expiry
 * from `purchaseTime + period`, which restores entitlement after a reinstall
 * (local DataStore is lost there). Expired purchases are consumed so the plan
 * can be bought again, and [purchase] frees a lingering purchase before opening
 * checkout (the local expiry is already persisted, so consuming never loses
 * paid time). Known limit: expiry itself is local (AGENTS.md) — support can
 * re-prove a purchase through the Myket panel.
 */
class PlatformBillingGateway(
    context: Context,
    private val settings: SettingsRepository,
) : BillingGateway {

    override val storefront = Storefront.MYKET

    private val appContext = context.applicationContext
    private var helper: IabHelper? = null
    private var setupDone = false
    private var priceById: Map<String, String> = emptyMap()

    private fun ensureHelper(): IabHelper = helper ?: IabHelper(appContext, BuildConfig.MYKET_PUBLIC_KEY).also {
        helper = it
    }

    override suspend fun initialize(): Boolean {
        if (setupDone) return true
        val h = ensureHelper()
        return suspendCancellableCoroutine { cont ->
            h.startSetup { result ->
                setupDone = result.isSuccess
                if (cont.isActive) cont.resume(result.isSuccess)
            }
        }
    }

    override suspend fun fetchPlans(): List<SubscriptionPlan> {
        if (!setupDone) initialize()
        val h = helper ?: return defaultPlans(emptyMap())
        val prices = suspendCancellableCoroutine<Map<String, String>> { cont ->
            h.queryInventoryAsync(true, listOf(ProductIds.MONTHLY, ProductIds.YEARLY)) { _, inventory ->
                val result = buildMap {
                    listOf(ProductIds.MONTHLY, ProductIds.YEARLY).forEach { id ->
                        inventory?.getSkuDetails(id)?.let { put(id, it.price) }
                    }
                }
                if (cont.isActive) cont.resume(result)
            }
        }
        priceById = prices
        return defaultPlans(prices)
    }

    private fun defaultPlans(prices: Map<String, String>): List<SubscriptionPlan> =
        listOf(ProductIds.MONTHLY, ProductIds.YEARLY).mapNotNull { id ->
            ProductIds.planFor(id, prices[id].orEmpty())
        }

    override suspend fun purchase(activity: Activity, plan: SubscriptionPlan): PurchaseOutcome {
        if (!setupDone) initialize()
        val h = helper ?: return PurchaseOutcome.Failure("billing unavailable")
        // Free any lingering purchase first — local expiry is already persisted,
        // so consuming here never discards paid time and lets renewals re-buy.
        consumeOwnedPurchases(h)
        val outcome = suspendCancellableCoroutine<PurchaseOutcome> { cont ->
            h.launchPurchaseFlow(activity, plan.productId, { result, purchase ->
                when {
                    result.isFailure || purchase == null -> {
                        if (result.response == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                            cont.resume(PurchaseOutcome.Cancelled)
                        } else {
                            cont.resume(PurchaseOutcome.Failure(result.message))
                        }
                    }

                    else -> cont.resume(PurchaseOutcome.Success)
                }
            }, "")
        }
        if (outcome == PurchaseOutcome.Success) {
            extendExpiry(plan)
            // Deliberately NOT consumed here: the owned purchase is the only
            // reinstall-proof receipt (see class docs). It is consumed once it
            // expires or when a new purchase starts.
        }
        return outcome
    }

    /** Consumes every owned plan purchase, one at a time (best effort). */
    private suspend fun consumeOwnedPurchases(h: IabHelper) {
        val owned = suspendCancellableCoroutine<List<ir.myket.billingclient.util.Purchase>> { cont ->
            h.queryInventoryAsync(false, null) { _, inventory ->
                if (cont.isActive) {
                    cont.resume(
                        listOf(ProductIds.MONTHLY, ProductIds.YEARLY).mapNotNull { id ->
                            inventory?.getPurchase(id)
                        },
                    )
                }
            }
        }
        owned.forEach { purchase ->
            suspendCancellableCoroutine<Unit> { cont ->
                h.consumeAsync(purchase) { _, _ -> if (cont.isActive) cont.resume(Unit) }
            }
        }
    }

    private suspend fun extendExpiry(plan: SubscriptionPlan) {
        val now = System.currentTimeMillis()
        val current = settings.observeTrialAndSubscription().first().subscriptionUntil
        val base = maxOf(current, now)
        val period = if (plan.monthly) MONTH_MILLIS else YEAR_MILLIS
        settings.setSubscriptionUntil(base + period)
    }

    override suspend fun checkSubscription(): Boolean = syncEntitlement() > System.currentTimeMillis()

    /**
     * Reconciles the local expiry with the store's owned purchases:
     * restores lost state after a reinstall (purchaseTime + period) and consumes
     * purchases whose period already ended. A unreachable store keeps the local
     * value untouched.
     */
    private suspend fun syncEntitlement(): Long {
        val local = subscriptionUntil()
        if (!setupDone && !initialize()) return local
        val h = helper ?: return local
        val now = System.currentTimeMillis()
        val owned = kotlinx.coroutines.suspendCancellableCoroutine<List<ir.myket.billingclient.util.Purchase>> { cont ->
            h.queryInventoryAsync(false, null) { _, inventory ->
                if (cont.isActive) {
                    cont.resume(
                        listOf(ProductIds.MONTHLY, ProductIds.YEARLY).mapNotNull { id ->
                            inventory?.getPurchase(id)
                        },
                    )
                }
            }
        }
        var best = local
        owned.forEach { purchase ->
            val period = if (purchase.sku == ProductIds.YEARLY) YEAR_MILLIS else MONTH_MILLIS
            val restored = purchase.purchaseTime + period
            if (restored > best) best = restored
            if (restored <= now) {
                // Period over: free the slot so the plan can be bought again.
                kotlinx.coroutines.suspendCancellableCoroutine<Unit> { cont ->
                    h.consumeAsync(purchase) { _, _ -> if (cont.isActive) cont.resume(Unit) }
                }
            }
        }
        if (best != local) settings.setSubscriptionUntil(best)
        return best
    }

    override suspend fun subscriptionUntil(): Long =
        settings.observeTrialAndSubscription().first().subscriptionUntil

    private companion object {
        const val MONTH_MILLIS = 30L * 24 * 60 * 60 * 1000
        const val YEAR_MILLIS = 365L * 24 * 60 * 60 * 1000
    }
}
