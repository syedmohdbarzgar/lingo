package org.token.english.core.billing

import android.app.Activity
import kotlinx.coroutines.flow.Flow

/** The marketplace this binary ships to (one per product flavor). */
enum class Storefront { BAZAAR, MYKET, GOOGLE_PLAY }

data class SubscriptionPlan(
    val productId: String,
    val titleFa: String,
    val periodFa: String,
    /** Price as formatted by the store; empty while loading / if unknown. */
    val priceText: String,
    val monthly: Boolean,
)

sealed interface PurchaseOutcome {
    data object Success : PurchaseOutcome
    data object Cancelled : PurchaseOutcome
    data class Failure(val message: String?) : PurchaseOutcome
}

/**
 * Store-specific in-app billing, implemented once per product flavor
 * (src/bazaar, src/myket, src/googlePlay). The app only ever sees this interface —
 * swapping or adding a marketplace never touches UI or domain code.
 *
 * Product ids (must exist in every store panel): sub_monthly, sub_yearly.
 */
interface BillingGateway {
    val storefront: Storefront

    /** Connects to the store's billing service. Safe to call more than once. */
    suspend fun initialize(): Boolean

    /** Store-formatted prices for the two subscription plans. */
    suspend fun fetchPlans(): List<SubscriptionPlan>

    /** Launches the store checkout; suspends until the user finishes/cancels. */
    suspend fun purchase(activity: Activity, plan: SubscriptionPlan): PurchaseOutcome

    /**
     * Store-side truth about the subscription.
     * Myket has no subscription products, so its gateway derives this from the
     * locally persisted expiry of the last purchase (see AGENTS.md).
     */
    suspend fun checkSubscription(): Boolean

    /** Epoch millis until which the subscription is valid, 0 = none. */
    suspend fun subscriptionUntil(): Long
}

/** Store product ids — must be registered in every marketplace panel. */
object ProductIds {
    const val MONTHLY = "sub_monthly"
    const val YEARLY = "sub_yearly"

    fun planFor(productId: String, priceText: String): SubscriptionPlan? = when (productId) {
        MONTHLY -> SubscriptionPlan(MONTHLY, "اشتراک ماهانه", "یک ماه", priceText, monthly = true)
        YEARLY -> SubscriptionPlan(YEARLY, "اشتراک سالانه", "یک سال", priceText, monthly = false)
        else -> null
    }
}

/** What the learner may access right now (trial → subscription). */
enum class AccessLevel { TRIAL, PREMIUM, LOCKED }

/**
 * Pure entitlement rules — unit tested, no Android types.
 * Trial *duration* accounting lives in [TrialClock]; this object only decides
 * access from the two numbers it is given.
 */
object EntitlementPolicy {
    fun level(now: Long, trialRemainingMs: Long, subscriptionUntil: Long): AccessLevel = when {
        subscriptionUntil > now -> AccessLevel.PREMIUM
        trialRemainingMs > 0L -> AccessLevel.TRIAL
        else -> AccessLevel.LOCKED
    }
}

/** Reads/writes the persisted subscription state (DataStore-backed). */
interface SubscriptionStore {
    fun observeTrialAndSubscription(): Flow<TrialAndSubscription>

    /**
     * Starts the trial on first run (writes the initial checkpoint) or — when it
     * already runs — advances the persisted checkpoint to (now, elapsedRealtime).
     */
    suspend fun ensureTrialStarted(now: Long, elapsedRealtime: Long)

    /** Persists an advanced trial checkpoint (periodic tick, see AppContainer). */
    suspend fun saveTrialCheckpoint(consumedMs: Long, lastWallMs: Long, lastElapsedMs: Long)

    suspend fun setSubscriptionUntil(epochMillis: Long)
}

data class TrialAndSubscription(
    val trialStartedAt: Long,
    val subscriptionUntil: Long,
    val trialConsumedMs: Long,
    val trialLastWallMs: Long,
    val trialLastElapsedMs: Long,
) {
    fun trialClockState(): TrialClockState =
        TrialClockState(
            startedAt = trialStartedAt,
            consumedMs = trialConsumedMs,
            lastWallMs = trialLastWallMs,
            lastElapsedMs = trialLastElapsedMs,
        )
}
