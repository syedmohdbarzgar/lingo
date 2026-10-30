package org.token.english.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.MicNone
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppChip
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CefrBadge
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.LevelHeroCard
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.ProgressRing
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.StaggeredReveal
import org.token.english.core.designsystem.component.SkillTrackProgress
import org.token.english.di.homeViewModelFactory
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.engine.SkillProfile
import org.token.english.domain.model.Skill
import org.token.english.domain.usecase.FocusPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    /**
     * Screen-level seam (checklist v15 B-6): the screen smoke tests inject a
     * ViewModel built from fakes; production omits this and gets the
     * container-wired one, exactly as before.
     */
    vm: HomeViewModel = viewModel(factory = homeViewModelFactory()),
    onOpenLesson: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenPaywall: () -> Unit,
    /** Speaking practice: say the lesson's target sentences back, graded on-device. */
    onOpenConversation: () -> Unit,
    /** Focused practice on the engine's weak spot: (lessonId, curriculum node id). */
    onOpenFocus: (String, String) -> Unit,
) {
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
            // The page assembles itself in reading order instead of appearing all at
            // once ("every card at the same time" was the complaint): each slot is
            // 55ms after the one above it. See StaggeredReveal.
            var slot = 0

            StaggeredReveal(slot++) { LevelHeroCard(state.level) }

            StaggeredReveal(slot++) { DailyGoalCard(state) }

            val nextLevel = state.nextLevel
            if (state.levelComplete && nextLevel != null) {
                StaggeredReveal(slot++) {
                    LevelUpCard(
                        currentLevel = state.level,
                        nextLevel = nextLevel,
                        onAdvance = vm::advanceLevel,
                    )
                }
            }

            CompanionOfferBanner(state, onOpenPaywall = onOpenPaywall)

            CompanionFreeBanner(state)

            state.plan?.let { plan ->
                StaggeredReveal(slot++) {
                    TodayPlanCard(
                        plan = plan,
                        onOpenReview = onOpenReview,
                        onOpenLesson = onOpenLesson,
                    )
                }
                StaggeredReveal(slot++) {
                    TodayLessonCard(
                        plan = plan,
                        onStart = { plan.nextLesson?.let { onOpenLesson(it.id) } },
                        onOpenReview = onOpenReview,
                    )
                }
            }

            state.focus?.let { focus ->
                StaggeredReveal(slot++) {
                    FocusSpotCard(
                        focus = focus,
                        onStart = { onOpenFocus(focus.lessonId, focus.itemId) },
                    )
                }
            }

            StaggeredReveal(slot++) { ConversationCard(onStart = onOpenConversation) }

            StaggeredReveal(slot++) { SkillsCard(profiles = state.profiles) }

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
) {        AppCard {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                SectionHeader("سطح بعدی")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    CefrBadge(currentLevel)
                    Text(
                        text = "این سطح را کامل کردید.",
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                ) {
                    CefrBadge(nextLevel)
                    Text(
                        text = "برای ادامه مسیر، به این سطح بروید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            PrimaryButton(
                text = "ادامه در سطح ${nextLevel.name}",
                onClick = onAdvance,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * Free-access companion (org.token.zaribar): while it is installed the
 * subscription costs nothing. Say so plainly and stop there — no store link,
 * because the companion is by definition already on the device and asking the
 * learner to download it again reads as a mistake. The Bazaar link belongs to
 * the not-installed state (`CompanionApp.openDownloadPage` stays the entry point
 * for it) — the free access simply ends if the companion is removed.
 */
@Composable
private fun CompanionFreeBanner(state: HomeUiState) {
    if (state.accessReason != org.token.english.core.billing.AccessReason.COMPANION_APP) return

    // The one card on Home that is the brand itself: full-strength brand fill with
    // white ink, so "your access is free" reads as a status, not as another task.
    val extended = LocalAppExtendedColors.current
    AppCard(
        containerColor = extended.brandStrong,
        contentColor = extended.onBrandStrong,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Text(
                text = "اشتراک رایگان فعال است",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "برنامهٔ «زریبار» روی این دستگاه نصب است، بنابراین دسترسی شما به همهٔ درس‌ها رایگان است.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "تا زمانی که «زریبار» نصب بماند، اشتراک رایگان ادامه دارد.",
                style = MaterialTheme.typography.bodySmall,
                color = extended.onBrandStrong.copy(alpha = 0.85f),
            )
        }
    }
}

/**
 * The free route, on Home (checklist N-2): with no trial there is exactly one
 * zero-cost way in — the companion app — so that is the only "free" Home talks
 * about. It renders only while the offer can be taken ([CompanionOffer.isOffered]):
 * a paying learner is never upsold a free alternative, and a device that already
 * holds the grant gets no download prompt back.
 */
@Composable
private fun CompanionOfferBanner(state: HomeUiState, onOpenPaywall: () -> Unit) {
    val offered = org.token.english.core.billing.CompanionOffer.isOffered(
        companionStatus = state.companionStatus,
        access = state.access,
        reason = state.accessReason,
        storeLinkAllowed = org.token.english.BuildConfig.EXTERNAL_COMPANION_OFFER,
    )
    if (!offered) return
    // Installed but untrusted (N-3): the build on the device is not one we can
    // grant for — say "update", never "install it again".
    val untrusted = state.companionStatus == org.token.english.core.billing.CompanionStatus.UNTRUSTED

    AppCard(onClick = onOpenPaywall) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(
                    text = if (untrusted) "نسخهٔ «زریبار» تأییدنشده است" else "راه رایگان: «زریبار»",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = if (untrusted) {
                        "«زریبار» روی این دستگاه هست اما نسخه‌اش تأیید نمی‌شود؛ آن را از کافه " +
                            "بازار به‌روزرسانی کنید تا همهٔ درس‌ها رایگان شود."
                    } else {
                        "با نصب «زریبار» از کافه بازار، همهٔ درس‌ها روی این دستگاه رایگان می‌شود."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            PrimaryButton(text = "اشتراک", onClick = onOpenPaywall)
        }
    }
}

/**
 * Conversation practice, introduced instead of merely named (v28).
 *
 * It used to be a bare «تمرین مکالمه» button at the bottom of Home — a label that
 * told the learner nothing about what would happen inside, so the feature only
 * existed for people who already knew it. The card says who speaks first, that
 * the answer is spoken aloud, that the feedback is immediate, and that none of it
 * touches the network; the button is the last thing in the card, not the card.
 */
@Composable
private fun ConversationCard(onStart: () -> Unit) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.compact),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.MicNone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Text(
                    text = "تمرین مکالمه",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "پارتنر می‌پرسد، تو بلند جواب می‌دهی.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text = "برای هر درس یک گفتگو ساخته شده؛ سؤال را می‌شنوی، با میکروفون پاسخ می‌دهی و " +
                "همان لحظه می‌فهمی جمله‌ات درست بود یا نه — بعد گفتگو ادامه پیدا می‌کند.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = AppSpacing.sm),
        )

        Row(
            modifier = Modifier.padding(top = AppSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            AppChip(label = "بدون اینترنت")
            AppChip(label = "بازخورد فوری")
            AppChip(label = "گفتگوی دوبخشی")
        }

        PrimaryButton(
            text = "شروع گفتگو",
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.sm),
        )
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
 * The remediation engine's weak spot (checklist B-1): one specific node, why it is
 * worth drilling now, and what it blocks. The card carries the engine's own words;
 * the screen only renders them (technical spec §19).
 */
@Composable
private fun FocusSpotCard(focus: FocusPlan, onStart: () -> Unit) {
    // Default surface, like every other card on the page. The tinted families this
    // card used to wear (amber / violet / green) turned the home screen into three
    // competing colours for the same three actions; the sections are already told
    // apart by their title, so the colour was decoration, not information.
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("نقطهٔ ضعف")
            Text(
                text = focus.titleFa,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = focus.reasonFa,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = buildString {
                    append("تسلط فعلی ${(focus.mastery * 100).toInt()}٪")
                    if (focus.blockedCount > 0) {
                        append(" — ${focus.blockedCount} مفهوم بعدی به آن گره خورده است")
                    }
                },
                style = MaterialTheme.typography.labelMedium,
            )
            PrimaryButton(
                text = "تمرین هدفمند",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
            )
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
                        )
                    }
                    PrimaryButton(
                        text = "شروع مرور",
                        onClick = onOpenReview,
                    )
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
                    )
                    EnglishText(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = lesson.titleFa,
                        style = MaterialTheme.typography.bodyMedium,
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
                )
            }
        }
    }
}

@Composable
private fun SkillsCard(profiles: List<SkillProfile>) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.compact)) {
            SectionHeader("مهارت‌ها")
            // The same three tracks Progress shows (P2). This card used to list
            // Vocabulary / Grammar / Listening, which was neither the profile's axes
            // nor the same set the Progress screen presented.
            val hasData = profiles.any { it.evidence > 0 }
            if (!hasData) {
                Text(
                    text = "با اولین تمرین، سطح مهارت‌هایت اینجا نمایش داده می‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                profiles.forEach { profile ->
                    SkillTrackProgress(profile = profile, compact = true)
                }
            }
        }
    }
}
