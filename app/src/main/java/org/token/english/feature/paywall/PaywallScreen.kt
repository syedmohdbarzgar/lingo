package org.token.english.feature.paywall

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.billing.SubscriptionPlan
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SecondaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.appViewModelFactory

/**
 * Subscription paywall: 24h trial → monthly/yearly plans, sold through the
 * hosting marketplace's own billing (bazaar / myket / googlePlay flavor).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onClose: () -> Unit,
    onLockedExit: () -> Unit,
    onPurchased: () -> Unit,
) {
    val vm: PaywallViewModel = viewModel(factory = appViewModelFactory { PaywallViewModel(it) })
    val state by vm.state.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val locked = state.access == org.token.english.core.billing.AccessLevel.LOCKED

    // Purchase/restore succeeded → hand back to the app.
    LaunchedEffect(state.purchased) {
        if (state.purchased) {
            vm.consumePurchased()
            onPurchased()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اشتراک") },
                navigationIcon = {
                    IconButton(onClick = { if (locked) onLockedExit() else onClose() }) {
                        Icon(Icons.Outlined.Close, contentDescription = "بستن")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Hero(state)

            when {
                state.isLoading -> LoadingBlock()

                state.plans.isNotEmpty() -> {
                    state.plans.forEach { plan ->
                        PlanCard(
                            plan = plan,
                            selected = plan.productId == state.selectedPlanId,
                            onClick = { vm.selectPlan(plan.productId) },
                        )
                    }

                    state.errorMessage?.let { ErrorText(it) }

                    PrimaryButton(
                        text = "خرید اشتراک",
                        onClick = { activity?.let { vm.purchase(it) } },
                        loading = state.isPurchasing,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    SecondaryButton(
                        text = "بازیابی خرید قبلی",
                        onClick = vm::restore,
                        enabled = !state.isPurchasing,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                locked -> {
                    // No plans yet and trial is over: explain + offer retry and a
                    // way out (settings) instead of a dead end.
                    state.errorMessage?.let { ErrorText(it) }
                    PrimaryButton(
                        text = "تلاش مجدد",
                        onClick = vm::retry,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SecondaryButton(
                        text = "تنظیمات",
                        onClick = onLockedExit,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                else -> {
                    state.errorMessage?.let { ErrorText(it) }
                    PrimaryButton(
                        text = "تلاش مجدد",
                        onClick = vm::retry,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Benefits()

            Text(
                text = "پرداخت از طریق ${state.storeName} انجام می‌شود. اشتراک تمدید خودکار دارد و هر زمان قابل لغو است.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(AppSpacing.sm))
        }
    }
}

@Composable
private fun Hero(state: PaywallUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Outlined.WorkspacePremium,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "دسترسی کامل به همه درس‌ها",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = when (state.access) {
                org.token.english.core.billing.AccessLevel.TRIAL -> {
                    val hours = state.trialRemainingMillis / (60 * 60 * 1000)
                    val days = state.trialRemainingMillis / (24 * 60 * 60 * 1000)
                    when {
                        days >= 1 -> "دوره رایگان شما $days روز و ${hours % 24} ساعت دیگر تمام می‌شود."
                        hours >= 1 -> "دوره رایگان شما $hours ساعت دیگر تمام می‌شود."
                        else -> "دوره رایگان شما رو به پایان است."
                    }
                }

                org.token.english.core.billing.AccessLevel.PREMIUM -> "اشتراک شما فعال است. ممنونیم!"
                org.token.english.core.billing.AccessLevel.LOCKED ->
                    "برای ادامه یادگیری، اشتراک فعال کنید. برای اعضا: خرید یا بازیابی اشتراک از همین صفحه انجام می‌شود."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoadingBlock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppSpacing.lg),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = if (selected) 2.dp else 1.dp, color = borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(AppSpacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(
                    text = plan.titleFa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                )
                Text(
                    text = plan.periodFa,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                if (plan.priceText.isNotEmpty()) {
                    EnglishText(
                        text = plan.priceText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        }
    }
}

@Composable
private fun Benefits() {
    AppCard(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            SectionHeader("با اشتراک چه می‌گیرید")
            listOf(
                "همه درس‌های A1 تا C2 بدون محدودیت",
                "مرور هوشمند واژگان با فاصله‌گذاری منظم",
                "آمار پیشرفت و مسیر یادگیری شخصی‌سازی‌شده",
                "به‌روزرسانی محتوای جدید، بدون پرداخت اضافه",
            ).forEach { benefit ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(AppSpacing.compact),
        )
    }
}
