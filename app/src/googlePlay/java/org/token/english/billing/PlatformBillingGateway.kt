package org.token.english.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import org.token.english.core.billing.BillingGateway
import org.token.english.core.billing.ProductIds
import org.token.english.core.billing.PurchaseOutcome
import org.token.english.core.billing.Storefront
import org.token.english.core.billing.SubscriptionMath
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.domain.repository.SettingsRepository
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * Google Play Billing 9.x implementation (googlePlay flavor only).
 * Store is the source of truth; the local mirror is tied to the store's real
 * renewal boundary (purchase time rolled forward in calendar months, see
 * [SubscriptionMath]) and is only rewritten when the store actually answers —
 * an unreachable store never zeroes an offline learner's entitlement (P3).
 */
class PlatformBillingGateway(
    context: Context,
    private val settings: SettingsRepository,
) : BillingGateway {

    override val storefront = Storefront.GOOGLE_PLAY

    private val appContext = context.applicationContext
    private var client: BillingClient? = null
    private var detailsById: Map<String, ProductDetails> = emptyMap()
    private var purchaseContinuation: Continuation<PurchaseOutcome>? = null

    /** True from checkout launch until its continuation resolves (double-tap guard). */
    private var purchaseInFlight: Boolean = false

    private val purchaseListener = PurchasesUpdatedListener { result, purchases ->
        val cont = purchaseContinuation
        purchaseContinuation = null
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                val purchase = purchases?.firstOrNull()
                if (purchase != null) {
                    acknowledge(purchase)
                    cont?.resume(PurchaseOutcome.Success)
                } else {
                    cont?.resume(PurchaseOutcome.Failure(result.debugMessage))
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> cont?.resume(PurchaseOutcome.Cancelled)
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> cont?.resume(PurchaseOutcome.Success)
            else -> cont?.resume(PurchaseOutcome.Failure(result.debugMessage))
        }
    }

    override suspend fun initialize(): Boolean {
        client?.takeIf { it.isReady }?.let { return true }
        val created = client ?: BillingClient.newBuilder(appContext)
            .setListener(purchaseListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
            .also { client = it }
        return suspendCancellableCoroutine { cont ->
            created.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (cont.isActive) cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
                }

                override fun onBillingServiceDisconnected() {
                    if (cont.isActive) cont.resume(false)
                }
            })
        }
    }

    override suspend fun fetchPlans(): List<SubscriptionPlan> {
        if (client == null) initialize()
        val c = client ?: return emptyList()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    ProductIds.MONTHLY,
                    ProductIds.YEARLY,
                ).map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                },
            )
            .build()
        val list = suspendCancellableCoroutine<List<ProductDetails>> { cont ->
            c.queryProductDetailsAsync(params) { _, result ->
                if (cont.isActive) cont.resume(result.productDetailsList)
            }
        }
        detailsById = list.associateBy { it.productId }
        return detailsById.mapNotNull { (id, details) -> planOf(id, details) }
    }

    private fun planOf(id: String, details: ProductDetails): SubscriptionPlan? {
        val phase = details.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.firstOrNull()
        val price = phase?.formattedPrice.orEmpty()
        val base = ProductIds.planFor(id, price) ?: return null
        return base.copy(
            periodFa = when (phase?.billingPeriod) {
                "P1M" -> "یک ماه"
                "P1Y" -> "یک سال"
                else -> base.periodFa
            },
        )
    }

    override suspend fun purchase(activity: Activity, plan: SubscriptionPlan): PurchaseOutcome {
        // Never run two store checkouts at once — a second launch would steal the
        // first continuation and leak the pending coroutine (checklist P2).
        if (purchaseInFlight) {
            return PurchaseOutcome.Failure("a purchase is already in progress")
        }
        if (client == null) initialize()
        val c = client ?: return PurchaseOutcome.Failure("billing unavailable")
        val details = detailsById[plan.productId] ?: fetchPlans().let { detailsById[plan.productId] }
            ?: return PurchaseOutcome.Failure("plan not found")
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return PurchaseOutcome.Failure("no offer")
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build(),
                ),
            )
            .build()
        purchaseInFlight = true
        return try {
            suspendCancellableCoroutine { cont ->
                purchaseContinuation = cont
                val launched = c.launchBillingFlow(activity, params)
                if (launched.responseCode != BillingClient.BillingResponseCode.OK) {
                    purchaseContinuation = null
                    if (cont.isActive) cont.resume(PurchaseOutcome.Failure(launched.debugMessage))
                }
            }
        } finally {
            purchaseInFlight = false
        }
    }

    override suspend fun checkSubscription(): Boolean {
        if (client == null && !initialize()) {
            // Store unreachable — keep the local mirror as-is (offline grace).
            return subscriptionUntil() > System.currentTimeMillis()
        }
        val c = client ?: return subscriptionUntil() > System.currentTimeMillis()
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val (responseCode, purchases) =
            suspendCancellableCoroutine<Pair<Int, List<Purchase>>> { cont ->
                c.queryPurchasesAsync(params) { result, list ->
                    if (cont.isActive) cont.resume(result.responseCode to list)
                }
            }
        if (responseCode != BillingClient.BillingResponseCode.OK) {
            // Network/service problem is not "no subscription": local mirror stays.
            return subscriptionUntil() > System.currentTimeMillis()
        }
        val activePurchases = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        activePurchases.forEach(::acknowledge)
        mirrorStoreTruth(activePurchases)
        return activePurchases.isNotEmpty()
    }

    override suspend fun subscriptionUntil(): Long =
        settings.observeTrialAndSubscription().first().subscriptionUntil

    /**
     * Ties the local mirror to the store truth: real renewal boundary while the
     * store reports a purchase, hard 0 when the reachable store reports none.
     */
    private suspend fun mirrorStoreTruth(purchases: List<Purchase>) {
        val now = System.currentTimeMillis()
        if (purchases.isEmpty()) {
            settings.setSubscriptionUntil(0L)
            return
        }
        val expiry = purchases.maxOf { purchase ->
            SubscriptionMath.nextRenewalBoundary(
                purchaseTimeMs = purchase.purchaseTime,
                monthly = SubscriptionMath.isMonthly(purchase.products.firstOrNull().orEmpty()),
                nowMs = now,
            )
        }
        val current = settings.observeTrialAndSubscription().first().subscriptionUntil
        settings.setSubscriptionUntil(maxOf(current, expiry))
    }

    private fun acknowledge(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        val c = client ?: return
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        c.acknowledgePurchase(params) { /* best effort; store retries for 3 days */ }
    }
}
