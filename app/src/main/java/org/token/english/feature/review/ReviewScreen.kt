package org.token.english.feature.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import org.token.english.core.common.bidiNumber
import org.token.english.core.common.persianMeasure
import org.token.english.core.common.persianStepLabel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LearningTargetStyle
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CorrectBanner
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.IncorrectBanner
import org.token.english.core.designsystem.component.InfoBanner
import org.token.english.core.designsystem.component.isolateEmbeddedLtr
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SecondaryButton
import org.token.english.core.designsystem.component.AnswerField
import org.token.english.core.designsystem.component.ExerciseOption
import org.token.english.core.designsystem.component.ExercisePrompt
import org.token.english.core.designsystem.component.OptionVisual
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.stepChange
import org.token.english.di.reviewViewModelFactory
import org.token.english.domain.engine.StudyTimePolicy
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.NodeExerciseCard
import org.token.english.domain.model.ReviewResult
import org.token.english.domain.model.ReviewState
import org.token.english.domain.model.VocabularyItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onBrowseVocabulary: () -> Unit,
    onGoHome: () -> Unit,
    /**
     * Opens the focused drill for one due curriculum node (review→graph pass). The row
     * the learner taps and the session it opens are the same decision: the row came
     * from that use case's own plan, lesson id included.
     */
    onOpenFocus: (lessonId: String, itemId: String) -> Unit = { _, _ -> },
    /**
     * Screen-level seam (checklist v15 B-6): the smoke tests inject a ViewModel
     * built from fakes; production omits this and gets the container-wired one.
     */
    vm: ReviewViewModel = viewModel(factory = reviewViewModelFactory()),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    // Study time is sampled only while THIS screen is resumed (P7): a review
    // tab left on the back stack never credits a second, and ticking stops the
    // moment the app goes to the background.
    val lifecycleOwner = LocalLifecycleOwner.current
    var isResumed by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    DisposableEffect(lifecycleOwner) {
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                isResumed = source.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            isResumed = false
        }
    }
    LaunchedEffect(isResumed) {
        // Both queues are local reads; returning from the focused drill (or another
        // tab) is exactly when they may have gone stale. The ViewModel ignores the
        // first resume and any resume mid-session.
        if (isResumed) vm.onScreenResumed()
        while (isResumed) {
            delay(StudyTimePolicy.TICK_MILLIS)
            vm.onStudyTick()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("مرور", style = MaterialTheme.typography.titleMedium) }) },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                // The queue is local (Room), so the copy never blames the network.
                state.loadFailed -> AppEmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "مرور خوانده نشد",
                    subtitle = "کارت‌های مرور روی همین گوشی ذخیره شده‌اند، اما خواندنشان این بار " +
                        "کامل نشد. این خطا به اینترنت مربوط نیست؛ یک بار دیگر تلاش کن.",
                    actionText = "تلاش دوباره",
                    onAction = vm::retry,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.phase == ReviewPhase.SUMMARY && state.queue.isEmpty() &&
                    state.nodeCards.isEmpty() && state.dueReviews.isEmpty() -> AppEmptyState(
                    icon = Icons.Default.CheckCircle,
                    title = "امروز مروری در کار نیست",
                    subtitle = "همه موارد سررسید را مرور کردی. ادامه یادگیری را از خانه شروع کن.",
                    actionText = "یادگیری را ادامه بده",
                    onAction = onGoHome,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.phase == ReviewPhase.SUMMARY -> SummaryContent(state, vm, onOpenFocus)

                state.phase == ReviewPhase.SESSION -> SessionContent(state, vm)

                state.phase == ReviewPhase.DONE -> DoneContent(state, vm, onGoHome, onBrowseVocabulary, Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun SummaryContent(
    state: ReviewUiState,
    vm: ReviewViewModel,
    onOpenFocus: (lessonId: String, itemId: String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        // The card queue can be empty while the graph still owes reviews, so the two
        // halves are separate cards rather than one number that could read "0" while
        // there is work on this very screen.
        if (state.total > 0) {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("مرور امروز")
                    Text(
                        text = persianMeasure(state.total, "مورد"),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        // Two kinds of question, one session: the words the queue owes
                        // and the due nodes' own exercises (graph-card pass).
                        text = if (state.nodeCards.isEmpty()) {
                            "واژگان: ${bidiNumber(state.queue.size)}"
                        } else {
                            "واژگان: ${bidiNumber(state.queue.size)} · " +
                                "تمرین گرههای سررسید: ${bidiNumber(state.nodeCards.size)}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    InfoBanner(
                        message = "مرور فاصله‌دار بر اساس عملکرد تو زمان هر مورد را تعیین می‌کند. پاسخ صادقانه بهترین نتیجه را می‌دهد.",
                    )
                    PrimaryButton(
                        text = "شروع مرور",
                        onClick = { vm.onEvent(ReviewEvent.Start) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // The graph half (review→graph pass): a node whose own schedule is due is
        // practised through its own exercises, not through a word card.
        if (state.dueReviews.isNotEmpty()) {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                    SectionHeader("مرور هدفمند")
                    Text(
                        text = "این گره‌های برنامهٔ درسی وقت مرورشان رسیده است. هر بخش فقط تمرین‌های خودش را می‌پرسد.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    state.dueReviews.forEach { review ->
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                            Text(
                                text = review.titleFa,
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = review.reasonFa,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            SecondaryButton(
                                text = "مرور این بخش",
                                onClick = { onOpenFocus(review.lessonId, review.itemId) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionContent(state: ReviewUiState, vm: ReviewViewModel) {
    val word = state.currentWord
    val nodeCard = state.currentNodeCard
    val extended = LocalAppExtendedColors.current
    // A new card arrives at the top of the screen, not at the offset the previous one
    // was scrolled to (see the reveal below).
    val scroll = remember(state.index, state.nodeIndex) { ScrollState(0) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppLinearProgress(
            progress = if (state.total == 0) 0f else state.answered.toFloat() / state.total,
            contentDescription = "پیشرفت مرور",
        )
        Text(
            text = persianStepLabel("مورد", state.answered + 1, state.total),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // A due node's own exercise (graph-card pass) is asked after the word cards,
        // and the lesson's own cue and controls draw it — the same question the lesson
        // asks, so it cannot read differently in the two places it appears.
        if (nodeCard != null) {
            NodeCardBody(nodeCard, state, vm)
        } else {
            WordCardBody(word, state, vm)
        }

        if (state.revealed && nodeCard == null) {
            SectionHeader("چقدر بلد بودی؟")
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                GradeButton(
                    text = "دوباره",
                    container = MaterialTheme.colorScheme.errorContainer,
                    content = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onEvent(ReviewEvent.Grade(ReviewResult.AGAIN)) },
                )
                GradeButton(
                    text = "سخت",
                    container = extended.warningContainer,
                    content = extended.onWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onEvent(ReviewEvent.Grade(ReviewResult.HARD)) },
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                GradeButton(
                    text = "خوب",
                    container = MaterialTheme.colorScheme.primary,
                    content = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onEvent(ReviewEvent.Grade(ReviewResult.GOOD)) },
                )
                GradeButton(
                    text = "آسان",
                    container = extended.successContainer,
                    content = extended.onSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = { vm.onEvent(ReviewEvent.Grade(ReviewResult.EASY)) },
                )
            }
        }

        // A node card is graded, not self-assessed: one verdict, then the next
        // question. No four-button SRS row — that belongs to a recall card.
        if (nodeCard != null) {
            if (state.nodeOutcome == null) {
                PrimaryButton(
                    text = "بررسی پاسخ",
                    onClick = { vm.onEvent(ReviewEvent.SubmitNode) },
                    enabled = state.canSubmitNode,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                PrimaryButton(
                    text = if (state.nodeIndex + 1 >= state.nodeCards.size) "پایان مرور" else "ادامه",
                    onClick = { vm.onEvent(ReviewEvent.NextNode) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        val itemState = state.currentItem?.state
        if (itemState != null && itemState != ReviewState.NEW) {
            Text(
                text = "وضعیت فعلی: ${itemState.name} — فاصله بعدی توسط برنامه مرور تعیین می‌شود.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


/**
 * One word card: reveal, then grade. Deliberately separate from the node card — a
 * word card asks about one word, a node card asks one of the lesson's exercises, and
 * the two share nothing but the session they arrive in.
 */
@Composable
private fun WordCardBody(
    word: VocabularyItem?,
    state: ReviewUiState,
    vm: ReviewViewModel,
) {
    AppCard(modifier = Modifier.stepChange(state.index)) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            if (word == null) {
                Text(
                    text = "این مورد یافت نشد.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                val producing = state.produceMode && !state.revealed
                if (producing) {
                    // Active production: the Persian meaning is the prompt and
                    // the English word (and its audio) stays hidden.
                    Text(
                        text = word.translation,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    EnglishText(
                        text = word.word,
                        style = LearningTargetStyle,
                        textAlign = TextAlign.Center,
                    )
                    word.pronunciation?.let {
                        EnglishText(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = { vm.onEvent(ReviewEvent.PlayWord) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "پخش تلفظ",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                if (producing) {
                    AnswerField(
                        value = state.typedAnswer,
                        onValueChange = { vm.onEvent(ReviewEvent.AnswerChanged(it)) },
                        label = "معنی انگلیسی را بنویس",
                    )
                    PrimaryButton(
                        text = "بررسی پاسخ",
                        onClick = { vm.onEvent(ReviewEvent.CheckAnswer) },
                        enabled = state.typedAnswer.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TextButton(
                        onClick = { vm.onEvent(ReviewEvent.Reveal) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("نمایش پاسخ")
                    }
                } else if (!state.revealed) {
                    PrimaryButton(
                        text = "نمایش معنی",
                        onClick = { vm.onEvent(ReviewEvent.Reveal) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(
                        text = word.translation,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                    // A-10: the example is bilingual, so a missed card teaches
                    // meaning as well as form.
                    word.examples.forEach { example ->
                        EnglishText(
                            text = example.en,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        if (example.fa.isNotBlank()) {
                            Text(
                                text = isolateEmbeddedLtr(example.fa),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    // Authored Persian usage note (A-1): the same "why" the
                    // lesson screen shows after a miss.
                    word.explanationFa?.let { note ->
                        Text(
                            text = isolateEmbeddedLtr(note),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (word.collocations.isNotEmpty()) {
                        Text(
                            text = word.collocations.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // Production feedback: never color-only — banner + text (design.md §50).
                state.answerCorrect?.let { correct ->
                    if (correct) {
                        CorrectBanner(Modifier.fillMaxWidth())
                    } else {
                        IncorrectBanner(
                            modifier = Modifier.fillMaxWidth(),
                            // A-2 parity with lessons: one edit away gets an
                            // actionable spelling hint, still graded wrong.
                            message = if (state.answerAlmost) {
                                "تقریباً درست — املای کلمه را بررسی کن. پاسخ درست: ${word.word}"
                            } else {
                                "پاسخ درست: ${word.word}"
                            },
                        )
                        // Explain the miss (A-1), as the lesson screen does.
                        word.explanationFa?.let { note ->
                            Text(
                                text = isolateEmbeddedLtr(note),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * One due curriculum node's own exercise, asked inside the review session.
 *
 * It is deliberately **not** a word card with an extra field: the question is the
 * lesson's, the controls are the shared ones, and grading goes through
 * `SubmitExerciseUseCase` — the same write a lesson answer makes, so the node's format
 * mask and its mastery move together. The node's name is on the card because the
 * learner is reviewing a *thing they studied*, not a random question.
 */
@Composable
private fun NodeCardBody(
    card: NodeExerciseCard,
    state: ReviewUiState,
    vm: ReviewViewModel,
) {
    val exercise = card.exercise
    val outcome = state.nodeOutcome
    AppCard(modifier = Modifier.stepChange(state.nodeIndex)) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            Text(
                text = "مرور هدفمند · ${card.titleFa}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            ExercisePrompt(exercise)
            when (exercise) {
                is Exercise.MultipleChoice -> {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        exercise.options.forEachIndexed { index, option ->
                            val visual = when {
                                outcome != null && index == exercise.correctIndex -> OptionVisual.CORRECT
                                outcome != null && index == state.nodeSelectedOption -> OptionVisual.INCORRECT
                                index == state.nodeSelectedOption -> OptionVisual.SELECTED
                                else -> OptionVisual.DEFAULT
                            }
                            ExerciseOption(
                                text = option,
                                visual = visual,
                                enabled = outcome == null,
                                onClick = { vm.onEvent(ReviewEvent.SelectNodeOption(index)) },
                            )
                        }
                    }
                }

                is Exercise.Writing -> AnswerField(
                    value = state.typedAnswer,
                    onValueChange = { vm.onEvent(ReviewEvent.AnswerChanged(it)) },
                    enabled = outcome == null,
                    label = "متن انگلیسی",
                    singleLine = false,
                    minLines = 4,
                )

                is Exercise.WordRecall -> AnswerField(
                    value = state.typedAnswer,
                    onValueChange = { vm.onEvent(ReviewEvent.AnswerChanged(it)) },
                    enabled = outcome == null,
                    label = "واژهٔ انگلیسی",
                )

                else -> AnswerField(
                    value = state.typedAnswer,
                    onValueChange = { vm.onEvent(ReviewEvent.AnswerChanged(it)) },
                    enabled = outcome == null,
                    label = "پاسخ انگلیسی",
                )
            }

            if (outcome != null) {
                if (outcome.correct) {
                    CorrectBanner(Modifier.fillMaxWidth())
                    // A produced sentence or a written text has many valid wordings, so
                    // the authored exemplar is shown on a pass as well.
                    if (exercise is Exercise.SentenceProduction || exercise is Exercise.Writing) {
                        EnglishText(
                            text = outcome.correctAnswer,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    IncorrectBanner(
                        modifier = Modifier.fillMaxWidth(),
                        message = if (outcome.almostCorrect) {
                            "تقریباً درست — املای کلمه را بررسی کن. پاسخ درست: ${outcome.correctAnswer}"
                        } else {
                            "پاسخ درست: ${outcome.correctAnswer}"
                        },
                    )
                    exercise.explanation?.let { note ->
                        Text(
                            text = "نکته: $note",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GradeButton(
    text: String,
    container: Color,
    content: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun DoneContent(
    state: ReviewUiState,
    vm: ReviewViewModel,
    onGoHome: () -> Unit,
    onBrowseVocabulary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Text("مرور تمام شد!", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "${persianMeasure(state.gradedCount, "مورد")} مرور شد",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        if (state.againCount > 0) {
            Text(
                text = "${persianMeasure(state.againCount, "مورد")} برای تکرار زودتر برنامه‌ریزی شد.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        // The card session can end while the graph still owes reviews. Without this
        // action that work was reachable only by re-opening the screen — the summary
        // that lists it was two phases back.
        if (state.dueReviews.isNotEmpty()) {
            SecondaryButton(
                text = "مرور هدفمند: ${persianMeasure(state.dueReviews.size, "بخش")}",
                onClick = { vm.onEvent(ReviewEvent.ShowDue) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        PrimaryButton(text = "بازگشت به خانه", onClick = onGoHome, modifier = Modifier.fillMaxWidth())
        SecondaryButton(text = "مرور واژه‌ها", onClick = onBrowseVocabulary, modifier = Modifier.fillMaxWidth())
    }
}
