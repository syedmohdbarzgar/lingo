package org.token.english.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppTextButton
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.di.onboardingViewModelFactory
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.repository.SettingsRepository

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Outlined.MenuBook,
        title = "انگلیسی یاد بگیر،\nقدم به قدم.",
        subtitle = "مسیر یادگیری بر اساس سطح CEFR، با درس‌های کوتاه و روزمره.",
    ),
    OnboardingPage(
        icon = Icons.Outlined.Grade,
        title = "هر روز تمرین کن.",
        subtitle = "مرور فاصله‌دار واژه‌ها باعث می‌شود آنچه یاد گرفته‌ای فراموش نشود.",
    ),
    OnboardingPage(
        icon = Icons.Outlined.TrendingUp,
        title = "مهارت واقعی بساز.",
        subtitle = "شنیدن، خواندن و تولید زبان در موقعیت‌های واقعی، نه فقط حفظ کردن.",
    ),
    OnboardingPage(
        icon = Icons.Outlined.WorkspacePremium,
        title = "۷ روز رایگان، بعد اشتراک.",
        subtitle = "کل محتوا ۷ روز به‌صورت رایگان در دسترس است. بعد از آن، برای ادامه یادگیری " +
            "اشتراک ماهانه یا سالانه از فروشگاه فعال می‌شود و هر زمان می‌توانی لغو کنی.",
    ),
)

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    fun skipWithA1(onDone: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.setLevel(LearningLevel.A1)
            settingsRepository.completeFirstLaunch()
            onDone()
        }
    }
}

@Composable
fun OnboardingScreen(
    onPlacement: () -> Unit,
    onSkip: () -> Unit,
) {
    val vm: OnboardingViewModel = viewModel(factory = onboardingViewModelFactory())
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            // The app shell no longer folds system insets into its content, and
            // onboarding has no TopAppBar of its own — so it pads them itself.
            .safeDrawingPadding()
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
            ) {
                Box(
                    Modifier
                        .padding(top = AppSpacing.major)
                        .size(96.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.extraLarge,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    Modifier
                        .padding(AppSpacing.xs)
                        .size(if (index == pagerState.currentPage) 10.dp else 8.dp)
                        .background(
                            if (index == pagerState.currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            },
                            MaterialTheme.shapes.extraLarge,
                        ),
                )
            }
        }

        if (pagerState.currentPage == pages.lastIndex) {
            PrimaryButton(
                text = "تعیین سطح",
                onClick = onPlacement,
                modifier = Modifier.fillMaxWidth(),
            )
            AppTextButton(
                text = "شروع بدون آزمون (سطح A1)",
                onClick = { vm.skipWithA1(onSkip) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            PrimaryButton(
                text = "ادامه",
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
