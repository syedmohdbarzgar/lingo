package org.token.english.feature.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.CompanionStatus
import org.token.english.core.billing.BillingGateway
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.PurchaseOutcome
import org.token.english.core.billing.Storefront
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.core.common.runCatchingCancellable
import org.token.english.domain.repository.SettingsRepository

data class PaywallUiState(
    val isLoading: Boolean = true,
    val plans: List<SubscriptionPlan> = emptyList(),
    val selectedPlanId: String? = null,
    val isPurchasing: Boolean = false,
    val errorMessage: String? = null,
    /** True right after a successful purchase — used to close the sheet. */
    val purchased: Boolean = false,
    val access: AccessLevel = AccessLevel.LOCKED,
    val storeName: String = "",
    /**
     * Which marketplace this build ships to. The small print must match its own
     * rules — Myket sells fixed-length plans as consumables, not subscriptions —
     * so the screen branches on this instead of assuming auto-renewal (§4a).
     */
    val storefront: Storefront = Storefront.BAZAAR,
    /** Why access is held right now — a free companion grant is not a purchase. */
    val accessReason: AccessReason = AccessReason.NONE,
    /** What the companion lookup found — only TRUSTED earns the free grant (N-3). */
    val companionStatus: CompanionStatus = CompanionStatus.ABSENT,
)

/**
 * Paywall state (checklist B-6): the billing gateway, settings, the companion
 * signals and an entitlement-refresh action — not the whole container.
 */
class PaywallViewModel(
    private val billing: BillingGateway,
    private val settingsRepository: SettingsRepository,
    private val companionStatus: StateFlow<CompanionStatus>,
    private val refreshCompanion: suspend () -> Boolean,
    private val refreshEntitlements: suspend () -> Unit,
    /** Entitlement re-check tick (a subscription can lapse mid-session); tests pass 0. */
    private val accessTickMillis: Long = 60_000L,
) : ViewModel() {

    private val _state = MutableStateFlow(PaywallUiState())
    val state: StateFlow<PaywallUiState> = _state.asStateFlow()

    private var trialAndSubscription: TrialAndSubscription? = null
    private var companion: CompanionStatus = CompanionStatus.ABSENT

    init {
        _state.update { it.copy(storeName = STORE_NAME, storefront = billing.storefront) }
        viewModelScope.launch {
            // Re-check the companion install when the paywall opens: a learner may
            // have installed zaribar since launch, which makes the app free.
            refreshCompanion()
            companionStatus.collect { status ->
                companion = status
                recomputeAccess()
            }
        }
        viewModelScope.launch {
            settingsRepository.observeTrialAndSubscription().collect { ts ->
                trialAndSubscription = ts
                recomputeAccess()
            }
        }
        if (accessTickMillis > 0) {
            viewModelScope.launch {
                // A subscription can lapse while the paywall is open — keep the
                // status line honest without waiting for a settings write.
                while (true) {
                    kotlinx.coroutines.delay(accessTickMillis)
                    if (trialAndSubscription != null) recomputeAccess()
                }
            }
        }
        loadPlans()
    }

    private fun recomputeAccess() {
        val ts = trialAndSubscription ?: return
        val entitlement = EntitlementPolicy.entitlement(
            now = System.currentTimeMillis(),
            subscriptionUntil = ts.subscriptionUntil,
            companionAppInstalled = companion == CompanionStatus.TRUSTED,
        )
        _state.update {
            it.copy(
                access = entitlement.level,
                accessReason = entitlement.reason,
                companionStatus = companion,
            )
        }
    }

    private fun loadPlans() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            var storeReachable = false
            val result = runCatchingCancellable {
                storeReachable = billing.initialize()
                if (storeReachable) billing.fetchPlans() else emptyList()
            }
            val plans = result.getOrElse { emptyList() }
                // Plans arrive with an empty price when the store couldn't answer
                // (product missing / panel not ready / old store app). Showing an
                // unpriced card is a dead end — drop them so the precise message
                // below explains what actually happened instead.
                .filter { it.priceText.isNotBlank() }
            _state.update {
                it.copy(
                    isLoading = false,
                    plans = plans,
                    // Preselect the yearly plan (best value) when prices loaded.
                    selectedPlanId = it.selectedPlanId
                        ?: plans.lastOrNull { p -> !p.monthly }?.productId
                        ?: plans.firstOrNull()?.productId,
                )
            }
            if (plans.isEmpty()) {
                // Precise failure per cause (checklist P3) — never blame the
                // network when the store simply has no products yet.
                val message = result.exceptionOrNull()
                    ?.let { e -> "خطا در برقراری ارتباط با فروشگاه: ${e.message ?: "نامشخص"}." }
                    ?: if (!storeReachable) {
                        "ارتباط با فروشگاه برقرار نشد. اتصال اینترنت و نصب بودن برنامهٔ فروشگاه را بررسی کنید."
                    } else {
                        "قیمتی از فروشگاه دریافت نشد. فروشگاه را به‌روزرسانی کنید یا بعداً دوباره تلاش کنید."
                    }
                _state.update { it.copy(errorMessage = message) }
            }
        }
    }

    fun selectPlan(planId: String) {
        _state.update { it.copy(selectedPlanId = planId) }
    }

    fun retry() = loadPlans()

    fun purchase(activity: Activity) {
        val plan = _state.value.plans.firstOrNull { it.productId == _state.value.selectedPlanId }
            ?: return
        if (_state.value.isPurchasing) return
        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true, errorMessage = null) }
            when (val outcome = runCatchingCancellable { billing.purchase(activity, plan) }
                .getOrElse { PurchaseOutcome.Failure(it.message) }
            ) {
                PurchaseOutcome.Success -> {
                    // Sync store truth into local entitlement (also updates the gating flow).
                    runCatchingCancellable { billing.checkSubscription() }
                    refreshEntitlements()
                    _state.update { it.copy(isPurchasing = false, purchased = true) }
                }

                PurchaseOutcome.Cancelled ->
                    _state.update { it.copy(isPurchasing = false) }

                is PurchaseOutcome.Failure ->
                    _state.update {
                        it.copy(
                            isPurchasing = false,
                            errorMessage = outcome.message
                                ?.takeIf { m -> m.isNotBlank() }
                                ?.let { m -> "خرید انجام نشد: $m" }
                                ?: "خرید انجام نشد. دوباره تلاش کنید.",
                        )
                    }
            }
        }
    }

    /** Re-asks the store — for users who already subscribed (or restored a purchase). */
    fun restore() {
        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true, errorMessage = null) }
            val result = runCatchingCancellable {
                billing.initialize()
                billing.checkSubscription()
            }
            _state.update { it.copy(isPurchasing = false) }
            val active = result.getOrDefault(false)
            when {
                active -> {
                    refreshEntitlements()
                    _state.update { it.copy(purchased = true) }
                }

                result.isFailure -> _state.update {
                    it.copy(
                        errorMessage =
                            "بررسی اشتراک ممکن نشد: ${result.exceptionOrNull()?.message ?: "خطای ناشناخته"}. " +
                                "اتصال اینترنت را بررسی کنید.",
                    )
                }

                else -> _state.update { it.copy(errorMessage = "اشتراک فعالی برای این حساب پیدا نشد.") }
            }
        }
    }

    fun consumePurchased() {
        _state.update { it.copy(purchased = false) }
    }

    private companion object {
        val STORE_NAME: String
            get() = org.token.english.BuildConfig.STORE_NAME
    }
}
