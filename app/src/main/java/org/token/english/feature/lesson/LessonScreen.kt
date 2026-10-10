package org.token.english.feature.lesson

import android.content.Intent
import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.core.designsystem.labelFa
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CorrectBanner
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.IncorrectBanner
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.di.lessonViewModelFactory
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.GrammarSection
import org.token.english.domain.model.GrammarSectionKind
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.MasteryDimension
import org.token.english.domain.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onExit: () -> Unit,
    /**
     * Focused practice (checklist B-1): the curriculum node the learner came here
     * to fix. The engine — not the screen — decides what that means.
     */
    focusItemId: String? = null,
) {
    val vm: LessonViewModel = viewModel(factory = lessonViewModelFactory(lessonId, focusItemId))
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
                    weakestDimension = state.weakestDimension,
                    onDone = onExit,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.stage == LessonStage.INTRO -> IntroContent(state = state, onEvent = vm::onEvent)

                else -> ExerciseContent(state = state, onEvent = vm::onEvent)
            }
        }
    }
}

/**
 * "Targeted practice" (checklist B-1): the remediation engine's reason for this
 * session, and how far the node is from being consolidated. Purely a render of the
 * decision — the copy comes from the engine, the labels from the design system.
 */
@Composable
private fun FocusCard(focus: FocusBanner) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            SectionHeader("تمرین هدفمند")
            Text(text = focus.titleFa, style = MaterialTheme.typography.titleSmall)
            Text(
                text = focus.reasonFa,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "تسلط فعلی ${focus.masteryPercent}٪ — هدف ${focus.targetPercent}٪",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ExerciseContent(
    state: LessonUiState,
    onEvent: (LessonEvent) -> Unit,
) {
    val exercise = state.currentExercise ?: return
    // B-3: offer the system "install voice data" action only when an activity can
    // handle it (resolveActivity needs the <queries> entry in the manifest on API 30+).
    val context = LocalContext.current
    val canInstallVoiceData = remember(context) {
        Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            .resolveActivity(context.packageManager) != null
    }
    val installVoiceData: (() -> Unit)? = if (canInstallVoiceData) {
        { context.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)) }
    } else {
        null
    }
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

        state.focus?.let { FocusCard(it) }

        AppCard {
            when (exercise) {
                is Exercise.MultipleChoice -> {
                    // A-5: a comprehension item shows its reading text first — the
                    // learner reads, then answers. Plain question items have none.
                    exercise.passage?.let { text ->
                        SectionHeader("متن را بخوانید")
                        EnglishText(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
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
                    SectionHeader(
                        if (state.audioUnavailable) "متن را بخوان و بنویس" else "گوش کن و بنویس",
                    )
                    ListeningCard(
                        audioText = exercise.audioText,
                        isPlaying = state.isPlaying,
                        onPlay = { onEvent(LessonEvent.ReplayAudio) },
                        unavailable = state.audioUnavailable,
                        playbackFailed = state.audioFailed,
                        onInstallVoiceData = installVoiceData,
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
                            message = if (outcome.almostCorrect) {
                                // A-2: one edit away — actionable spelling feedback
                                // instead of a bare miss (still graded wrong).
                                "تقریباً درست — املای کلمه را بررسی کن. پاسخ درست: ${outcome.correctAnswer}"
                            } else {
                                "پاسخ درست: ${outcome.correctAnswer}"
                            },
                        )
                        // A short explanation after a miss: the authored one if the
                        // content has it, otherwise the lesson's grammar tip (P4).
                        val explanation = exercise.explanation
                            // First section only — the multi-section A-8 text must not
                            // be dumped wholesale after a miss (summary = rule section).
                            ?: state.lesson?.grammarSummary?.takeIf {
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
    weakestDimension: MasteryDimension?,
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
        // Naming the kind of knowing that broke (recognition vs recall vs
        // comprehension) is what makes the miss actionable (checklist B-1, audit §2).
        weakestDimension?.let { dimension ->
            AppCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    SectionHeader("نقطهٔ ضعف این جلسه")
                    Text(
                        text = dimension.labelFa(),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = "تمرین‌های بعدی روی این مهارت جلوتر می‌آیند.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
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
            lesson.grammarTipFa?.let { _ ->
                // A-8 stage 1: sectioned teaching cards (rule/table/examples/
                // mistakes); a legacy plain tip renders as its single TIP card.
                GrammarTeachingCards(lesson = lesson)
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
                                // A-10: the example carries its own Persian gloss,
                                // so the intro stage teaches meaning, not just form.
                                item.examples.forEach { example ->
                                    EnglishText(
                                        text = example.en,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    if (example.fa.isNotBlank()) {
                                        Text(
                                            text = example.fa,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
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
 * A-8 stage 1: the lesson's grammar teaching page as one card per section —
 * rule, forms table, bilingual examples, common mistakes of Persian speakers.
 * Sections come from the multi-section format inside `grammarTipFa`; a legacy
 * plain tip parses to a single TIP section and renders like the old card.
 */
@Composable
private fun GrammarTeachingCards(lesson: Lesson) {
    lesson.grammarSections.forEach { section ->
        when (section.kind) {
            GrammarSectionKind.EXAMPLES -> GrammarExamplesCard(section)
            GrammarSectionKind.TABLE -> GrammarTableCard(section)
            GrammarSectionKind.MISTAKES -> GrammarMistakesCard(section)
            else -> AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    SectionHeader(section.title)
                    Text(
                        text = section.lines.joinToString("\n"),
                        // Persian prose with embedded English words: pin RTL so a
                        // leading Latin run can't flip the paragraph (first-strong).
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                        ),
                    )
                }
            }
        }
    }
}

/** Rule / table / example cards are content, not decoration — one coherent block each. */
@Composable
private fun GrammarExamplesCard(section: GrammarSection) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader(section.title)
            section.lines.forEach { line ->
                // Authored line: «English — فارسی». No dash → English only.
                val parts = line.split(" — ", limit = 2)
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    EnglishText(
                        text = parts[0],
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (parts.size == 2) {
                        Text(
                            text = parts[1],
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/** The forms table is English-only — lay it out LTR inside the forced-RTL root. */
@Composable
private fun GrammarTableCard(section: GrammarSection) {
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            SectionHeader(section.title)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            MaterialTheme.shapes.small,
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                        .padding(AppSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    section.lines.forEach { line ->
                        EnglishText(text = line, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/**
 * Common mistakes get the warning palette (icon + text, never color alone —
 * design.md §50) so they read as caution without becoming an error banner.
 */
@Composable
private fun GrammarMistakesCard(section: GrammarSection) {
    val extended = LocalAppExtendedColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(extended.warningContainer, MaterialTheme.shapes.medium)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = extended.onWarning,
            )
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall,
                color = extended.onWarning,
            )
        }
        section.lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                ),
                color = extended.onWarning,
            )
        }
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
