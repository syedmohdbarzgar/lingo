package org.token.english.feature.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.common.bidiNumber
import org.token.english.core.common.persianCountPair
import org.token.english.core.common.persianMeasure
import org.token.english.core.common.persianPercent
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.LevelHeroCard
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.SkillProgressBar
import org.token.english.core.designsystem.component.SkillTrackProgress
import org.token.english.di.progressViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen() {
    val vm: ProgressViewModel = viewModel(factory = progressViewModelFactory())
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("پیشرفت", style = MaterialTheme.typography.titleMedium) }) },
    ) { padding ->
        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        // Local data, local failure: say what happened and offer the retry — a bare
        // spinner or a blank page would read as "the app is broken" or "no signal".
        if (state.loadFailed) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                AppEmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "پیشرفت خوانده نشد",
                    subtitle = "آمار و مسیرها روی همین گوشی ذخیره شده‌اند، اما خواندنشان این بار " +
                        "کامل نشد. این خطا به اینترنت مربوط نیست؛ یک بار دیگر تلاش کن.",
                    actionText = "تلاش دوباره",
                    onAction = vm::retry,
                )
            }
            return@Scaffold
        }

        val stats = state.stats
        val hasAnyData = stats != null && (stats.lessonsCompleted > 0 || stats.reviewsToday > 0 || state.mastery.values.any { it > 0f })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            // Same "your level" card as Home: the band's portrait, badge, name and
            // goal. Progress used to show a bare "current level" row, which named
            // the level without ever saying what it means.
            LevelHeroCard(
                level = state.level,
                extra = "${persianMeasure(stats?.lessonsCompleted ?: 0, "درس")} از " +
                    "${bidiNumber(state.totalLessons)} تمام شده",
            )

            if (!hasAnyData) {
                AppEmptyState(
                    icon = Icons.Outlined.Insights,
                    title = "هنوز داده‌ای ثبت نشده",
                    subtitle = "پس از اولین درس یا مرور، آمار یادگیری‌ات اینجا نمایش داده می‌شود.",
                )
            } else {
                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                        SectionHeader("خلاصه")
                        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                            StatBox(
                                label = "درس تمام‌شده",
                                value = persianCountPair(stats?.lessonsCompleted ?: 0, state.totalLessons),
                                modifier = Modifier.weight(1f),
                            )
                            StatBox(
                                label = "واژه در مرور",
                                value = "${stats?.wordsLearned ?: 0}",
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                            StatBox(
                                label = "دقت مرور",
                                value = if ((stats?.reviewsToday ?: 0) > 0) {
                                    persianPercent(((stats?.reviewAccuracy ?: 0f) * 100).toInt())
                                } else {
                                    "—"
                                },
                                modifier = Modifier.weight(1f),
                            )
                            StatBox(
                                label = "روز پیاپی",
                                value = "${stats?.streakDays ?: 0}",
                                modifier = Modifier.weight(1f),
                            )
                        }
                        // Finished steps and known language are two claims. The second
                        // one is earned by the lesson's own nodes, so it can lag — and
                        // saying so is more useful than letting «تمام‌شده» imply "learned".
                        if ((stats?.lessonsCompleted ?: 0) > 0) {
                            Text(
                                text = "مسلط: " +
                                    persianCountPair(state.masteredLessons, stats?.lessonsCompleted ?: 0) +
                                    " از درس‌های تمام‌شده — درسی که هیچ نودش به تمرین امروز نیاز ندارد.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (state.profiles.isNotEmpty()) {
                    AppCard {
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                            SectionHeader("سه محور یادگیری")
                            // Three independent tracks — grammar, vocabulary, reading —
                            // each with the evidence behind its number (P2). Not one
                            // blended figure, and not six bars where a suspended skill
                            // (speaking) could never move off "شروع نشده".
                            state.profiles.forEach { profile ->
                                SkillTrackProgress(profile = profile)
                            }
                            Text(
                                text = "هر محور فقط با کار خودش رشد می‌کند؛ عددی که روی «تعداد پاسخ» بنا شده باشد قابل اعتمادتر است.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (state.knowledgeTotal > 0) {
                    AppCard {
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.compact)) {
                            SectionHeader("گراف دانش")
                            SkillProgressBar(
                                label = "گره‌های تمرین‌شده",
                                progress = state.knowledgePractised.toFloat() / state.knowledgeTotal,
                                valueText = persianCountPair(state.knowledgePractised, state.knowledgeTotal),
                            )
                            state.weakestKnowledge?.let { weakest ->
                                Text(
                                    text = "ضعیف‌ترین گره فعلی: $weakest",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
