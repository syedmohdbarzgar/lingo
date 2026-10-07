package org.token.english.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppChip
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CefrBadge
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.ProgressRing
import org.token.english.core.designsystem.component.SecondaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.SkillProgressBar
import org.token.english.core.designsystem.labelFa
import org.token.english.di.appViewModelFactory
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenLesson: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenVocabulary: () -> Unit,
    onOpenPaywall: () -> Unit,
) {
    val vm: HomeViewModel = viewModel(factory = appViewModelFactory { HomeViewModel(it) })
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.greeting,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "آماده یادگیری امروز؟",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "تنظیمات")
                    }
                },
            )
        },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            DailyGoalCard(state)

            val nextLevel = state.nextLevel
            if (state.levelComplete && nextLevel != null) {
                LevelUpCard(
                    currentLevel = state.level,
                    nextLevel = nextLevel,
                    onAdvance = vm::advanceLevel,
                )
            }

            TrialBanner(state, onOpenPaywall = onOpenPaywall)

            state.plan?.let { plan ->
                TodayPlanCard(
                    plan = plan,
                    onOpenReview = onOpenReview,
                    onOpenLesson = onOpenLesson,
                )
                TodayLessonCard(
                    plan = plan,
                    onStart = { plan.nextLesson?.let { onOpenLesson(it.id) } },
                    onOpenReview = onOpenReview,
                )
            }

            SkillsCard(mastery = state.mastery)

            state.stats?.let { stats ->
                if (stats.streakDays > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    ) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "${stats.streakDays} روز پیاپی یادگیری",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            SecondaryButton(
                text = "مرور واژه‌ها",
                onClick = onOpenVocabulary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Shown once the learner has finished every lesson at their current level, so the
 * path upward is explicit instead of implied by the next-lesson card.
 */
@Composable
private fun LevelUpCard(
    currentLevel: LearningLevel,
    nextLevel: LearningLevel,
    onAdvance: () -> Unit,
) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("سطح بعدی")
            Text(
                text = "سطح ${currentLevel.name} را کامل کردید.",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "برای ادامه مسیر، به سطح ${nextLevel.name} بروید.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PrimaryButton(
                text = "ادامه در سطح ${nextLevel.name}",
                onClick = onAdvance,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TrialBanner(state: HomeUiState, onOpenPaywall: () -> Unit) {
    // Premium subscribers see nothing; trial learners always see how much free
    // time is left — the subscription requirement has to be visible from day one,
    // not only in the last two days of the trial.
    if (state.access != org.token.english.core.billing.AccessLevel.TRIAL) return
    val hours = state.trialRemainingMillis / (60 * 60 * 1000)
    val days = state.trialRemainingMillis / (24 * 60 * 60 * 1000)

    AppCard(onClick = onOpenPaywall) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(
                    text = when {
                        days >= 1 -> "دوره رایگان شما $days روز و ${hours % 24} ساعت دیگر ادامه دارد"
                        hours >= 1 -> "دوره رایگان شما $hours ساعت دیگر ادامه دارد"
                        else -> "دوره رایگان رو به پایان است"
                    },
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "با اشتراک، دسترسی به همه درس‌ها قطع نمی‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            PrimaryButton(text = "اشتراک", onClick = onOpenPaywall)
        }
    }
}

@Composable
private fun DailyGoalCard(state: HomeUiState) {
    val plan = state.plan
    val goal = plan?.targetMinutes ?: 15
    val doneSeconds = plan?.todayStudySeconds ?: 0L
    val doneMinutes = doneSeconds / 60
    val progress = if (goal <= 0) 0f else doneSeconds.toFloat() / (goal * 60)

    AppCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            ProgressRing(
                progress = progress,
                modifier = Modifier.size(84.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$doneMinutes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "/ $goal دقیقه",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(
                    text = "هدف امروز",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = if (doneSeconds >= goal * 60) {
                        "هدف امروز انجام شد. آفرین!"
                    } else {
                        "${(doneSeconds / 60)} دقیقه از $goal دقیقه"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppLinearProgress(progress = progress, contentDescription = "پیشرفت هدف روزانه")
            }
        }
    }
}

/**
 * "Today's plan" (checklist B-1): the adaptive planner's ordered actions, each
 * with the reason the engine chose it. The screen renders the decision — it never
 * decides the order or the justification (technical spec §19).
 */
@Composable
private fun TodayPlanCard(
    plan: org.token.english.domain.model.TodayPlan,
    onOpenReview: () -> Unit,
    onOpenLesson: (String) -> Unit,
) {
    if (plan.actions.isEmpty()) return
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("برنامهٔ امروز")
            plan.actions.forEach { action ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = action.titleFa,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = action.reasonFa,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    AppChip(label = when (action.type) {
                        org.token.english.domain.model.LearningActionType.REVIEW -> "مرور"
                        org.token.english.domain.model.LearningActionType.REMEDIATE -> "پیش‌نیاز"
                        org.token.english.domain.model.LearningActionType.LEARN -> "درس جدید"
                        org.token.english.domain.model.LearningActionType.PRACTISE -> "تمرین"
                    })
                }
            }
            val lead = plan.actions.first()
            if (lead.type == org.token.english.domain.model.LearningActionType.REVIEW) {
                PrimaryButton(
                    text = "شروع مرور ${plan.dueReviewCount} کارت",
                    onClick = onOpenReview,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                plan.nextLesson?.let { lesson ->
                    PrimaryButton(
                        text = "شروع درس: ${lesson.titleFa}",
                        onClick = { onOpenLesson(lesson.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayLessonCard(
    plan: org.token.english.domain.model.TodayPlan,
    onStart: () -> Unit,
    onOpenReview: () -> Unit,
) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("مسیر امروز")

            // The engine-driven card above already offers the review CTA; only
            // fall back to this row when the plan has no actions yet (graph not
            // seeded, or a focus-less day).
            if (plan.dueReviewCount > 0 && plan.actions.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "مرور امروز",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = "${plan.dueReviewCount} مورد آماده مرور است",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    PrimaryButton(text = "شروع مرور", onClick = onOpenReview)
                }
            }

            val lesson = plan.nextLesson
            if (lesson != null) {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    Text(
                        text = "درس بعدی",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    EnglishText(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = lesson.titleFa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CefrBadge(lesson.level)
                        AppChip(label = "${lesson.estimatedMinutes} دقیقه")
                        AppChip(label = lesson.topic)
                    }
                    PrimaryButton(
                        text = "شروع درس",
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Text(
                    text = "همه درس‌های این سطح را تمام کردید. برای ادامه مسیر، در مرور واژه‌ها تمرین کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SkillsCard(mastery: Map<Skill, Float>) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.compact)) {
            SectionHeader("مهارت‌ها")
            val tracked = listOf(Skill.VOCABULARY, Skill.GRAMMAR, Skill.LISTENING)
            val hasData = tracked.any { (mastery[it] ?: 0f) > 0f }
            if (!hasData) {
                Text(
                    text = "با اولین تمرین، سطح مهارت‌هایت اینجا نمایش داده می‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                tracked.forEach { skill ->
                    SkillProgressBar(
                        label = skill.labelFa(),
                        progress = mastery[skill] ?: 0f,
                    )
                }
            }
        }
    }
}
