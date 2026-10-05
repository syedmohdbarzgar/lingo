package org.token.english.feature.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CorrectBanner
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.IncorrectBanner
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.appViewModelFactory
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onExit: () -> Unit,
) {
    val vm: LessonViewModel = viewModel(factory = appViewModelFactory { LessonViewModel(it, lessonId) })
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.lesson?.title ?: "درس",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        state.lesson?.let {
                            Text(
                                text = it.titleFa,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.error != null -> AppEmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "مشکلی پیش آمد",
                    subtitle = state.error.orEmpty(),
                    actionText = "بازگشت",
                    onAction = onExit,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.completed -> LessonSummary(
                    correct = state.correctCount,
                    total = state.askedCount,
                    weakAnswers = state.weakAnswers,
                    onDone = onExit,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.stage == LessonStage.INTRO -> IntroContent(state = state, onEvent = vm::onEvent)

                else -> ExerciseContent(state = state, onEvent = vm::onEvent)
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    state: LessonUiState,
    onEvent: (LessonEvent) -> Unit,
) {
    val exercise = state.currentExercise ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppLinearProgress(
            progress = state.progress,
            contentDescription = "پیشرفت درس",
        )
        Text(
            // The total grows when a missed exercise is re-queued in this session.
            text = "تمرین ${state.askedCount + 1} از ${state.visibleTotal}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AppCard {
            when (exercise) {
                is Exercise.MultipleChoice -> {
                    EnglishText(
                        text = exercise.question,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    exercise.questionFa?.let {
                        Text(
                            text = it,
                            // Authored Persian may open with English («Not
                            // uncommon» …): pin RTL so first-strong can't flip it.
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        exercise.options.forEachIndexed { index, option ->
                            val visual = when {
                                state.outcome != null && index == exercise.correctIndex -> OptionVisual.CORRECT
                                state.outcome != null && index == state.selectedOption -> OptionVisual.INCORRECT
                                index == state.selectedOption -> OptionVisual.SELECTED
                                else -> OptionVisual.DEFAULT
                            }
                            ExerciseOption(
                                text = option,
                                visual = visual,
                                enabled = state.outcome == null,
                                onClick = { onEvent(LessonEvent.SelectOption(index)) },
                            )
                        }
                    }
                }

                is Exercise.FillBlank -> {
                    SectionHeader("جمله را کامل کنید")
                    EnglishText(
                        text = exercise.sentence,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    AnswerField(
                        value = state.textAnswer,
                        onValueChange = { onEvent(LessonEvent.TextAnswerChanged(it)) },
                        enabled = state.outcome == null,
                    )
                }

                is Exercise.Translation -> {
                    SectionHeader("ترجمه کنید")
                    Text(
                        text = exercise.prompt,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (exercise.bank.isNotEmpty()) {
                        // Lighter A1 production: build the sentence from word chips.
                        WordBankAnswer(
                            answer = state.textAnswer,
                            bank = exercise.bank,
                            onPickWord = { word ->
                                onEvent(
                                    LessonEvent.TextAnswerChanged(
                                        (state.textAnswer + " " + word).trim().replace(Regex("\\s+"), " "),
                                    ),
                                )
                            },
                            onRemoveWord = { word ->
                                val words = state.textAnswer
                                    .split(Regex("\\s+")).filter { it.isNotEmpty() }.toMutableList()
                                val index = words.indexOfFirst { it == word }
                                if (index >= 0) {
                                    words.removeAt(index)
                                    onEvent(LessonEvent.TextAnswerChanged(words.joinToString(" ")))
                                }
                            },
                            onClear = { onEvent(LessonEvent.TextAnswerChanged("")) },
                            enabled = state.outcome == null,
                        )
                    } else {
                        AnswerField(
                            value = state.textAnswer,
                            onValueChange = { onEvent(LessonEvent.TextAnswerChanged(it)) },
                            enabled = state.outcome == null,
                            label = "پاسخ انگلیسی",
                        )
                    }
                }

                is Exercise.Listening -> {
                    SectionHeader("گوش کن و بنویس")
                    ListeningCard(
                        audioText = exercise.audioText,
                        isPlaying = state.isPlaying,
                        onPlay = { onEvent(LessonEvent.ReplayAudio) },
                    )
                    AnswerField(
                        value = state.textAnswer,
                        onValueChange = { onEvent(LessonEvent.TextAnswerChanged(it)) },
                        enabled = state.outcome == null,
                    )
                }

                is Exercise.Speaking -> {
                    // Speech evaluation is not implemented yet — AGENTS.md
                    SectionHeader("تمرین مکالمه")
                    Text(
                        text = "این تمرین به ارزیابی گفتار نیاز دارد و هنوز فعال نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (state.outcome != null) {
                val outcome = state.outcome
                if (outcome != null) {
                    if (outcome.correct) {
                        CorrectBanner(Modifier.fillMaxWidth())
                    } else {
                        IncorrectBanner(
                            modifier = Modifier.fillMaxWidth(),
                            message = "پاسخ درست: ${outcome.correctAnswer}",
                        )
                        // A short explanation after a miss: the authored one if the
                        // content has it, otherwise the lesson's grammar tip (P4).
                        val explanation = exercise.explanation
                            ?: state.lesson?.grammarTipFa?.takeIf {
                                AnswerChecker.skillOf(exercise) == Skill.GRAMMAR
                            }
                        explanation?.let { tip ->
                            Text(
                                text = "نکته: $tip",
                                // Long mixed tips wrap across lines — keep the
                                // paragraph pinned to RTL for stable bidi wrapping.
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

        if (state.outcome == null) {
            val canSubmit = when (exercise) {
                is Exercise.MultipleChoice -> state.selectedOption != null
                is Exercise.Speaking -> true
                else -> state.textAnswer.isNotBlank()
            }
            PrimaryButton(
                text = "بررسی پاسخ",
                onClick = { onEvent(LessonEvent.Submit) },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            PrimaryButton(
                text = if (state.remainingCount == 0 && !state.repeatsCurrent) "پایان درس" else "ادامه",
                onClick = { onEvent(LessonEvent.Next) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LessonSummary(
    correct: Int,
    total: Int,
    weakAnswers: List<String>,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Text(
            text = "درس تمام شد!",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "$correct از $total پاسخ درست",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        // Missed items are marked separately instead of gating completion (P4).
        if (weakAnswers.isNotEmpty()) {
            AppCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    SectionHeader("مواردی که نیاز به تکرار دارند")
                    weakAnswers.forEach { answer ->
                        EnglishText(
                            text = answer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Text(
                text = "این موارد را مرور کن — دوباره به سراغشان می‌آییم.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = "واژه‌های این درس به صف مرور اضافه شدند. مرور فاصله‌دار باعث می‌شود یادگیری ماندگار شود.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        PrimaryButton(
            text = "بازگشت به خانه",
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Intro stage: vocabulary with examples, the grammar tip, then start (P4). */
@Composable
private fun IntroContent(state: LessonUiState, onEvent: (LessonEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        SectionHeader("معرفی درس")
        state.lesson?.let { lesson ->
            Text(text = lesson.titleFa, style = MaterialTheme.typography.headlineSmall)
            EnglishText(
                text = lesson.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            lesson.grammarTipFa?.let { tip ->
                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        SectionHeader("نکتهٔ گرامری")
                        Text(text = tip, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (state.vocabulary.isNotEmpty()) {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    SectionHeader("واژه‌های درس")
                    state.vocabulary.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                            ) {
                                EnglishText(text = item.word, style = MaterialTheme.typography.titleMedium)
                                item.pronunciation?.let {
                                    EnglishText(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Text(
                                    text = item.translation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                item.examples.firstOrNull()?.let { example ->
                                    EnglishText(
                                        text = example,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            androidx.compose.material3.IconButton(
                                onClick = { onEvent(LessonEvent.SpeakText(item.word)) },
                            ) {
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = "پخش تلفظ",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        PrimaryButton(
            text = "شروع تمرین‌ها",
            onClick = { onEvent(LessonEvent.StartExercises) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * Word-bank answer for lighter A1 production (checklist P4): tap bank words to
 * build the sentence, tap a placed word to take it back. All content is English,
 * so the whole control re-asserts LTR inside the forced-RTL root.
 */
@Composable
fun WordBankAnswer(
    answer: String,
    bank: List<String>,
    onPickWord: (String) -> Unit,
    onRemoveWord: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val answerWords = remember(answer) { answer.split(Regex("\\s+")).filter { it.isNotEmpty() } }
    val availableWords = remember(bank, answerWords) {
        val used = answerWords.toMutableList()
        bank.filter { word ->
            val index = used.indexOf(word)
            if (index >= 0) {
                used.removeAt(index)
                false
            } else {
                true
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        MaterialTheme.shapes.medium,
                    )
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                    .padding(AppSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                if (answerWords.isEmpty()) {
                    Text(
                        text = "برای ساخت جمله روی کلمات بزن…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    answerWords.chunked(4).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                            row.forEach { word ->
                                WordChip(text = word, onClick = { if (enabled) onRemoveWord(word) })
                            }
                        }
                    }
                }

                Text(
                    text = "کلمات:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                availableWords.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                        row.forEach { word ->
                            WordChip(text = word, onClick = { if (enabled) onPickWord(word) })
                        }
                    }
                }
            }
        }

        TextButton(
            onClick = onClear,
            enabled = enabled && answer.isNotEmpty(),
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("پاک کردن")
        }
    }
}

@Composable
private fun WordChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        EnglishText(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}
