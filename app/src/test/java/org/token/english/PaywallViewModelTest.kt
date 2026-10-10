package org.token.english

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.token.english.core.billing.AccessLevel
import org.token.english.core.billing.AccessReason
import org.token.english.core.billing.ProductIds
import org.token.english.core.billing.PurchaseOutcome
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.feature.paywall.PaywallViewModel

/**
 * PaywallViewModel tests (checklist v8 item 3). The billing gateway is a double,
 * so the paths that decide whether a learner can pay — store unreachable, products
 * unregistered, purchase cancelled/failed/succeeded, restore — are all reachable
 * without a store account or a device.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PaywallViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Harness(
        companion: Boolean = false,
        val billing: FakeBillingGateway = FakeBillingGateway(),
    ) {
        val companionFlow = MutableStateFlow(companion)
        var entitlementRefreshes = 0

        fun build(): PaywallViewModel = PaywallViewModel(
            billing = billing,
            settingsRepository = FakeSettingsRepository(),
            companionInstalled = companionFlow,
            refreshCompanion = { companionFlow.value },
            refreshEntitlements = { entitlementRefreshes++ },
            accessTickMillis = 0,
        )
    }

    @Test
    fun `plans load and the yearly one is preselected`() {
        val h = Harness()
        val vm = h.build()

        assertFalse(vm.state.value.isLoading)
        assertEquals(listOf(ProductIds.MONTHLY, ProductIds.YEARLY), vm.state.value.plans.map { it.productId })
        assertEquals("best value is the default", ProductIds.YEARLY, vm.state.value.selectedPlanId)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun `a store with no registered products says so instead of blaming the network`() {
        val h = Harness(billing = FakeBillingGateway(plans = emptyList()))
        val vm = h.build()

        assertTrue(vm.state.value.plans.isEmpty())
        assertTrue(
            "the message names the real cause: ${vm.state.value.errorMessage}",
            vm.state.value.errorMessage!!.contains("قیمتی از فروشگاه دریافت نشد"),
        )
    }

    @Test
    fun `an unreachable store reports connectivity, not missing products`() {
        val h = Harness(billing = FakeBillingGateway(storeReachable = false))
        val vm = h.build()

        assertTrue(
            vm.state.value.errorMessage!!.contains("ارتباط با فروشگاه برقرار نشد"),
        )
    }

    @Test
    fun `unpriced plans are dropped rather than shown as a dead end`() {
        val h = Harness(
            billing = FakeBillingGateway(
                plans = listOf(
                    SubscriptionPlan(ProductIds.MONTHLY, "ماهانه", "یک ماه", priceText = "", monthly = true),
                    SubscriptionPlan(ProductIds.YEARLY, "سالانه", "یک سال", "۵۰۰٬۰۰۰ تومان", monthly = false),
                ),
            ),
        )
        val vm = h.build()

        assertEquals("only the priced plan survives", listOf(ProductIds.YEARLY), vm.state.value.plans.map { it.productId })
    }

    @Test
    fun `a failed purchase shows the store's reason and stays unpurchased`() {
        val h = Harness(billing = FakeBillingGateway(purchaseOutcome = PurchaseOutcome.Failure("کارت رد شد")))
        val vm = h.build()
        vm.purchase(activity())

        assertFalse(vm.state.value.isPurchasing)
        assertFalse("a failure is not a purchase", vm.state.value.purchased)
        assertTrue(
            "the store's own reason is surfaced: ${vm.state.value.errorMessage}",
            vm.state.value.errorMessage!!.contains("کارت رد شد"),
        )
        assertEquals(0, h.entitlementRefreshes)
    }

    @Test
    fun `a purchase without a reason still explains itself`() {
        val h = Harness(billing = FakeBillingGateway(purchaseOutcome = PurchaseOutcome.Failure(null)))
        val vm = h.build()
        vm.purchase(activity())

        assertTrue(vm.state.value.errorMessage!!.contains("خرید انجام نشد"))
    }

    @Test
    fun `a cancelled purchase is silent — the learner changed their mind`() {
        val h = Harness(billing = FakeBillingGateway(purchaseOutcome = PurchaseOutcome.Cancelled))
        val vm = h.build()
        vm.purchase(activity())

        assertFalse(vm.state.value.isPurchasing)
        assertFalse(vm.state.value.purchased)
        assertNull("no error for a deliberate cancel", vm.state.value.errorMessage)
    }

    @Test
    fun `a successful purchase syncs store truth and can be consumed`() {
        val h = Harness(billing = FakeBillingGateway(purchaseOutcome = PurchaseOutcome.Success))
        val vm = h.build()

        vm.purchase(activity())

        assertEquals(listOf(ProductIds.YEARLY), h.billing.purchased.map { it.productId })
        assertEquals("the store is re-read after a purchase", 1, h.billing.checkCalls)
        assertEquals("local entitlement is refreshed", 1, h.entitlementRefreshes)
        assertTrue(vm.state.value.purchased)

        vm.consumePurchased()
        assertFalse("the sheet must not re-close on recompose", vm.state.value.purchased)
    }

    @Test
    fun `purchase does nothing until a plan is selected`() {
        val h = Harness(billing = FakeBillingGateway(plans = emptyList()))
        val vm = h.build()

        vm.purchase(activity())

        assertTrue("no plan, no checkout", h.billing.purchased.isEmpty())
        assertFalse(vm.state.value.isPurchasing)
    }

    @Test
    fun `restore reports an active subscription`() {
        val h = Harness(billing = FakeBillingGateway(subscriptionActive = true))
        val vm = h.build()

        vm.restore()

        assertTrue(vm.state.value.purchased)
        assertNull(vm.state.value.errorMessage)
        assertEquals(1, h.entitlementRefreshes)
        assertFalse(vm.state.value.isPurchasing)
    }

    @Test
    fun `restore explains an account with nothing to restore`() {
        val h = Harness(billing = FakeBillingGateway(subscriptionActive = false))
        val vm = h.build()

        vm.restore()

        assertFalse(vm.state.value.purchased)
        assertTrue(
            vm.state.value.errorMessage!!.contains("اشتراک فعالی برای این حساب پیدا نشد"),
        )
    }

    @Test
    fun `restore reports a store failure precisely`() {
        val h = Harness(billing = FakeBillingGateway(throwOnCall = IllegalStateException("timeout")))
        val vm = h.build()

        vm.restore()

        val message = vm.state.value.errorMessage
        assertTrue("the failure is not called 'no subscription': $message", message!!.contains("بررسی اشتراک ممکن نشد"))
        assertTrue("the cause is shown: $message", message.contains("timeout"))
        assertFalse(vm.state.value.purchased)
    }

    @Test
    fun `the companion install is reported as a free grant, not a purchase`() {
        val h = Harness(companion = true)
        val vm = h.build()

        assertEquals(AccessLevel.PREMIUM, vm.state.value.access)
        assertEquals(AccessReason.COMPANION_APP, vm.state.value.accessReason)
        assertTrue(vm.state.value.companionInstalled)
        assertFalse("a free grant is not a purchase", vm.state.value.purchased)
    }

    /** A bare Activity instance: the gateway double never touches it. */
    private fun activity(): Activity = Activity()
}
