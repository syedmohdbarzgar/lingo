package org.token.english.feature.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.BillingGateway
import org.token.english.core.billing.EntitlementPolicy
import org.token.english.core.billing.PurchaseOutcome
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.core.common.runCatchingCancellable
import org.token.english.di.AppContainer

data class PaywallUiState(
    val isLoading: Boolean = true,
    val plans: List<SubscriptionPlan> = emptyList(),
    val selectedPlanId: String? = null,
    val isPurchasing: Boolean = false,
    val errorMessage: String? = null,
    /** True right after a successful purchase — used to close the sheet. */
    val purchased: Boolean = false,
    val access: AccessLevel = AccessLevel.TRIAL,
    val trialRemainingMillis: Long = 0L,
    val storeName: String = "",
)

class PaywallViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val billing: BillingGateway get() = container.billing

    private val _state = MutableStateFlow(PaywallUiState())
    val state: StateFlow<PaywallUiState> = _state.asStateFlow()

    private var trialAndSubscription: org.token.english.core.billing.TrialAndSubscription? = null

    init {
        _state.update { it.copy(storeName = STORE_NAME) }
        viewModelScope.launch {
            container.settingsRepository.observeTrialAndSubscription().collect { ts ->
                trialAndSubscription = ts
                recomputeAccess()
            }
        }
        viewModelScope.launch {
            // The countdown decays with wall time even when nothing is written
            // to settings — tick while the paywall is open.
            while (true) {
                kotlinx.coroutines.delay(60_000L)
                if (trialAndSubscription != null) recomputeAccess()
            }
        }
        loadPlans()
    }

    private fun recomputeAccess() {
        val ts = trialAndSubscription ?: return
        val now = System.currentTimeMillis()
        val elapsed = android.os.SystemClock.elapsedRealtime()
        val remaining = org.token.english.core.billing.TrialClock.remainingMs(ts.trialClockState(), now, elapsed)
        _state.update {
            it.copy(
                access = EntitlementPolicy.level(now, remaining, ts.subscriptionUntil),
                trialRemainingMillis = remaining,
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
                    container.refreshEntitlements()
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
                    container.refreshEntitlements()
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
