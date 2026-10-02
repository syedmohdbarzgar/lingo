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
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.CefrBadge
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.SkillProgressBar
import org.token.english.core.designsystem.labelFa
import org.token.english.di.appViewModelFactory
import org.token.english.domain.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen() {
    val vm: ProgressViewModel = viewModel(factory = appViewModelFactory { ProgressViewModel(it) })
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
            AppCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    Text("سطح فعلی", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    CefrBadge(state.level)
                }
            }

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
                                value = "${stats?.lessonsCompleted ?: 0} از ${state.totalLessons}",
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
                                    "${((stats?.reviewAccuracy ?: 0f) * 100).toInt()}٪"
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
                    }
                }

                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.compact)) {
                        SectionHeader("مهارت‌ها")
                        Skill.entries.forEach { skill ->
                            val mastery = state.mastery[skill] ?: 0f
                            SkillProgressBar(
                                label = skill.labelFa(),
                                progress = mastery,
                                valueText = if (mastery <= 0f) "شروع نشده" else null,
                            )
                        }
                        Text(
                            text = "تسلط با پاسخ‌های تکراری و مرور فاصله‌دار رشد می‌کند، نه فقط با تمام کردن درس.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
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
