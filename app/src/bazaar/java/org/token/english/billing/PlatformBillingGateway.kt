package org.token.english.billing

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.entity.PurchaseInfo
import ir.cafebazaar.poolakey.entity.PurchaseState
import ir.cafebazaar.poolakey.request.PurchaseRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import org.token.english.BuildConfig
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
 * Cafe Bazaar billing via the official Poolakey SDK (bazaar flavor only).
 *
 * - Real subscription products via Payment.subscribeProduct (panel: sub_monthly / sub_yearly).
 * - Local RSA verification key comes from keystore.properties `bazaarRsaKey`
 *   (from developers.cafebazaar.ir). Release builds FAIL while it is unset
 *   (build.gradle.kts guard); only debug builds fall back to SecurityCheck.Disable.
 * - The local mirror tracks the store's real renewal boundary (purchase time
 *   rolled forward in calendar months) and is only rewritten when Bazaar
 *   actually answers — an unreachable store never zeroes an offline learner (P3).
 */
class PlatformBillingGateway(
    context: Context,
    private val settings: SettingsRepository,
) : BillingGateway {

    override val storefront = Storefront.BAZAAR

    private val appContext = context.applicationContext
    private var payment: Payment? = null
    private var connected = false
    private var priceById: Map<String, String> = emptyMap()
    private var purchaseContinuation: Continuation<PurchaseOutcome>? = null

    /** True from checkout launch until its continuation resolves (double-tap guard). */
    private var purchaseInFlight: Boolean = false

    private fun ensurePayment(): Payment = payment ?: run {
        val security = if (BuildConfig.BAZAAR_RSA_KEY.isEmpty()) {
            SecurityCheck.Disable
        } else {
            SecurityCheck.Enable(rsaPublicKey = BuildConfig.BAZAAR_RSA_KEY)
        }
        Payment(
            context = appContext,
            config = PaymentConfiguration(localSecurityCheck = security, shouldSupportSubscription = true),
        ).also { payment = it }
    }

    override suspend fun initialize(): Boolean {
        if (connected) return true
        val p = ensurePayment()
        return suspendCancellableCoroutine { cont ->
            p.connect {
                connectionSucceed {
                    connected = true
                    if (cont.isActive) cont.resume(true)
                }
                connectionFailed {
                    if (cont.isActive) cont.resume(false)
                }
                disconnected {
                    connected = false
                }
            }
        }
    }

    override suspend fun fetchPlans(): List<SubscriptionPlan> {
        if (!connected) initialize()
        val p = payment ?: return emptyList()
        val prices = suspendCancellableCoroutine<Map<String, String>> { cont ->
            p.getSubscriptionSkuDetails(listOf(ProductIds.MONTHLY, ProductIds.YEARLY)) {
                getSkuDetailsSucceed { list ->
                    if (cont.isActive) cont.resume(list.associate { it.sku to it.price })
                }
                getSkuDetailsFailed {
                    if (cont.isActive) cont.resume(emptyMap())
                }
            }
        }
        priceById = prices
        return listOf(ProductIds.MONTHLY, ProductIds.YEARLY).mapNotNull { id ->
            ProductIds.planFor(id, prices[id].orEmpty())
        }
    }

    override suspend fun purchase(activity: Activity, plan: SubscriptionPlan): PurchaseOutcome {
        // Never run two store checkouts at once — a second subscribeProduct would
        // steal the first continuation and leak the pending coroutine (checklist P2).
        if (purchaseInFlight) {
            return PurchaseOutcome.Failure("a purchase is already in progress")
        }
        val registry: ActivityResultRegistry = (activity as? ComponentActivity)?.activityResultRegistry
            ?: return PurchaseOutcome.Failure("activity cannot host the billing flow")
        if (!connected) initialize()
        val p = payment ?: return PurchaseOutcome.Failure("billing unavailable")
        purchaseInFlight = true
        return try {
            suspendCancellableCoroutine { cont ->
                purchaseContinuation = cont
                p.subscribeProduct(
                    registry = registry,
                    request = PurchaseRequest(productId = plan.productId, payload = "", dynamicPriceToken = null),
                ) {
                    purchaseFlowBegan { /* checkout opened */ }
                    failedToBeginFlow { throwable ->
                        finishPurchase(PurchaseOutcome.Failure(throwable.message))
                    }
                    purchaseSucceed {
                        finishPurchase(PurchaseOutcome.Success)
                    }
                    purchaseCanceled {
                        finishPurchase(PurchaseOutcome.Cancelled)
                    }
                    purchaseFailed { throwable ->
                        finishPurchase(PurchaseOutcome.Failure(throwable.message))
                    }
                }
            }
        } finally {
            purchaseInFlight = false
        }
    }

    private fun finishPurchase(outcome: PurchaseOutcome) {
        val cont = purchaseContinuation
        purchaseContinuation = null
        cont?.resume(outcome)
    }

    override suspend fun checkSubscription(): Boolean {
        if (!connected && !initialize()) {
            // Bazaar unreachable (offline / not installed) — keep local mirror.
            return subscriptionUntil() > System.currentTimeMillis()
        }
        val p = payment ?: return subscriptionUntil() > System.currentTimeMillis()
        val result = suspendCancellableCoroutine<Result<List<PurchaseInfo>>> { cont ->
            p.getSubscribedProducts {
                querySucceed { products -> if (cont.isActive) cont.resume(Result.success(products)) }
                queryFailed { throwable -> if (cont.isActive) cont.resume(Result.failure(throwable)) }
            }
        }
        val products = result.getOrNull()
        if (products == null) {
            // Query failed — a network/store problem is not "no subscription".
            return subscriptionUntil() > System.currentTimeMillis()
        }
        val activePurchases = products.filter {
            it.purchaseState == PurchaseState.PURCHASED &&
                (it.productId == ProductIds.MONTHLY || it.productId == ProductIds.YEARLY)
        }
        mirrorStoreTruth(activePurchases)
        return activePurchases.isNotEmpty()
    }

    override suspend fun subscriptionUntil(): Long =
        settings.observeTrialAndSubscription().first().subscriptionUntil

    /** Ties the local mirror to Bazaar's real renewal boundary (see class docs). */
    private suspend fun mirrorStoreTruth(purchases: List<PurchaseInfo>) {
        val now = System.currentTimeMillis()
        if (purchases.isEmpty()) {
            settings.setSubscriptionUntil(0L)
            return
        }
        val expiry = purchases.maxOf { purchase ->
            SubscriptionMath.nextRenewalBoundary(
                purchaseTimeMs = purchase.purchaseTime,
                monthly = SubscriptionMath.isMonthly(purchase.productId),
                nowMs = now,
            )
        }
        val current = settings.observeTrialAndSubscription().first().subscriptionUntil
        settings.setSubscriptionUntil(maxOf(current, expiry))
    }
}
