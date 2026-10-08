package org.token.english.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.EmojiEvents
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
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.data.content.ContentParser
import org.token.english.di.AppContainer
import org.token.english.di.appViewModelFactory
import org.token.english.domain.engine.PlacementAnswer
import org.token.english.domain.engine.PlacementAssessment
import org.token.english.domain.engine.PlacementSkillReport
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Skill
import org.token.english.domain.model.shuffledForDisplay
import org.token.english.domain.usecase.ScorePlacementUseCase
import org.token.english.feature.lesson.ExerciseOption
import org.token.english.feature.lesson.OptionVisual

data class PlacementUiState(
    val isLoading: Boolean = true,
    val questions: List<Exercise.MultipleChoice> = emptyList(),
    val index: Int = 0,
    val selected: Int? = null,
    /**
     * Per-question answers in order, each tagged with its CEFR band and skill
     * (P1-1). Order feeds the band scorer; the skill tag feeds the profile.
     */
    val answers: List<PlacementAnswer> = emptyList(),
    val finished: Boolean = false,
    /** Overall CEFR estimate — kept in sync with the skill assessment's level. */
    val resultLevel: LearningLevel = LearningLevel.A1,
    /** Per-skill read of the same answers (null until the test finishes). */
    val assessment: PlacementAssessment? = null,
) {
    val correctCount: Int get() = answers.count { it.correct }
    val total: Int get() = questions.size
    val current: Exercise.MultipleChoice? get() = questions.getOrNull(index)
    /** Back-compat convenience: the boolean results in order (scorer input). */
    val results: List<Boolean> get() = answers.map { it.correct }
}

sealed interface PlacementEvent {
    data class Select(val optionIndex: Int) : PlacementEvent
    data object Next : PlacementEvent
}

class PlacementViewModel(
    private val container: AppContainer,
) : ViewModel() {

    private val _state = MutableStateFlow(PlacementUiState())
    val state: StateFlow<PlacementUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Hold the spinner until seeding finished — on a fresh install the
            // placement questions do not exist in Room for a moment.
            container.contentSeeded.first { it }
            val questions = container.lessonRepository
                .getExercises(ContentParser.PLACEMENT_LESSON_ID)
                .filterIsInstance<Exercise.MultipleChoice>()
                // Display-time shuffle: order derives from the question id, so the
                // authored answer position can never be guessed (P1).
                .map { it.shuffledForDisplay() }
            _state.update { it.copy(isLoading = false, questions = questions) }
        }
    }

    fun onEvent(event: PlacementEvent) {
        when (event) {
            is PlacementEvent.Select -> _state.update {
                if (it.finished) it else it.copy(selected = event.optionIndex)
            }

            PlacementEvent.Next -> next()
        }
    }

    private fun next() {
        val s = _state.value
        val question = s.current ?: return
        val selectedIndex = s.selected ?: return
        val wasCorrect = selectedIndex == question.correctIndex
        val answer = PlacementAnswer(
            level = levelOfQuestion(s.questions, s.index),
            skill = AnswerChecker.skillOf(question),
            correct = wasCorrect,
        )
        val answers = s.answers + answer
        val nextIndex = s.index + 1

        // Adaptive order: stop as soon as the current band can no longer reach its
        // pass threshold (or the questions run out). The scorer stops walking at
        // the first failed band, so a partial final band never inflates the level.
        if (nextIndex >= s.questions.size || bandIsUnwinnable(answers.map { it.correct })) {
            _state.update { it.copy(index = nextIndex, selected = null, answers = answers) }
            finishAssessment(answers)
        } else {
            _state.update {
                it.copy(
                    index = nextIndex,
                    selected = null,
                    answers = answers,
                )
            }
        }
    }

    /**
     * Scores the finished test *and* calibrates mastery (P1-1) — the answer set is
     * replayed through the real progress pipeline, so the profile is not blank
     * after placement. Work happens off the click; the result screen appears once
     * the assessment is ready.
     */
    private fun finishAssessment(answers: List<PlacementAnswer>) {
        viewModelScope.launch {
            val assessment = container.assessPlacement(answers)
            _state.update {
                it.copy(
                    finished = true,
                    resultLevel = assessment.level,
                    assessment = assessment,
                )
            }
        }
    }

    /**
     * The CEFR band of a question. placement.json is authored as six bands of
     * [ScorePlacementUseCase.DEFAULT_BAND_SIZE] in A1 → C2 order — the same order
     * the scorer already relies on, so the band is derivable from the index and
     * needs no schema change (P1-1).
     */
    private fun levelOfQuestion(questions: List<Exercise.MultipleChoice>, index: Int): LearningLevel {
        val band = index / ScorePlacementUseCase.DEFAULT_BAND_SIZE
        return LearningLevel.entries.getOrElse(band) { LearningLevel.C2 }
    }

    /** True when even a perfect finish on the current band cannot reach the pass threshold. */
    private fun bandIsUnwinnable(results: List<Boolean>): Boolean {
        if (results.isEmpty()) return false
        val bandSize = ScorePlacementUseCase.DEFAULT_BAND_SIZE
        val remainder = results.size % bandSize
        val answeredInBand = if (remainder == 0) bandSize else remainder
        val bandStart = results.size - answeredInBand
        val correctInBand = results.subList(bandStart, results.size).count { it }
        val maxPossible = correctInBand + (bandSize - answeredInBand)
        val passThreshold = (bandSize * 2 + 2) / 3 // ceil(2/3 × bandSize)
        return maxPossible < passThreshold
    }

    fun finish(onDone: () -> Unit) {
        val level = _state.value.resultLevel
        viewModelScope.launch {
            container.settingsRepository.setLevel(level)
            container.settingsRepository.completeFirstLaunch()
            onDone()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementScreen(
    onFinished: () -> Unit,
    onBack: () -> Unit,
) {
    val vm: PlacementViewModel = viewModel(factory = appViewModelFactory { PlacementViewModel(it) })
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تعیین سطح", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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

                state.questions.isEmpty() -> AppEmptyState(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "آزمون در دسترس نیست",
                    subtitle = "محتوای آفلاین هنوز آماده نشده است. دوباره تلاش کن.",
                    actionText = "بازگشت",
                    onAction = onBack,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.finished -> ResultContent(
                    level = state.resultLevel,
                    assessment = state.assessment,
                    onFinish = { vm.finish(onFinished) },
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> QuestionContent(state = state, onEvent = vm::onEvent)
            }
        }
    }
}

@Composable
private fun QuestionContent(state: PlacementUiState, onEvent: (PlacementEvent) -> Unit) {
    val question = state.current ?: return
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        AppLinearProgress(
            progress = if (state.total == 0) 0f else state.index.toFloat() / state.total,
            contentDescription = "پیشرفت آزمون",
        )
        Text(
            text = "سؤال ${state.index + 1} از ${state.total}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            EnglishText(text = question.question, style = MaterialTheme.typography.titleMedium)
            question.questionFa?.let {
                Text(
                    text = it,
                    // May open with English («Not uncommon» …) — pin the
                    // paragraph to RTL so first-strong can't reverse the Persian.
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            question.options.forEachIndexed { index, option ->
                ExerciseOption(
                    text = option,
                    visual = if (index == state.selected) OptionVisual.SELECTED else OptionVisual.DEFAULT,
                    onClick = { onEvent(PlacementEvent.Select(index)) },
                )
            }
        }

        PrimaryButton(
            text = if (state.index + 1 >= state.total) "پایان آزمون" else "سؤال بعدی",
            onClick = { onEvent(PlacementEvent.Next) },
            enabled = state.selected != null,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = "نتیجه در پایان آزمون نمایش داده می‌شود.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ResultContent(
    level: LearningLevel,
    assessment: PlacementAssessment?,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Icon(
            imageVector = Icons.Outlined.EmojiEvents,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        SectionHeader("نتیجه آزمون")
        Text(
            text = "سطح تخمینی شما: ${level.name}",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "درس‌های این سطح برایت آماده می‌شود. با پیشرفت، سطح خودت هم به‌روز می‌شود.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        // P1-1: the same answers read per skill, so the learner sees *what* to
        // work on, not only *where* to start. Masks stay honest: only the skills
        // this test actually measured are listed.
        assessment?.let { result ->
            result.bySkill.takeIf { it.isNotEmpty() }?.let { reports ->
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                SectionHeader("مهارت‌های سنجیده‌شده")
                // A-6: honest scope. The test is recognition-based and measures
                // exactly these skills — do not let the report read as a full
                // profile of reading, writing or speaking too.
                Text(
                    text = "این آزمون گرامر و واژگان را می‌سنجد؛ مهارت‌های دیگر با تمرین‌های درس‌ها شکل می‌گیرند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                reports.forEach { report ->
                    SkillReportRow(report)
                }
                val focus = result.focusSkills
                if (focus.isNotEmpty()) {
                    Text(
                        text = "پیشنهاد: تمرکز اول روی ${focus.joinToString(" و ") { skillLabelFa(it) }} — برنامهٔ روزانه از همین‌جا شروع می‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            }
        }

        PrimaryButton(
            text = "شروع یادگیری",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SkillReportRow(report: PlacementSkillReport) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = skillLabelFa(report.skill),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "${report.correct} از ${report.asked} درست",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AppLinearProgress(
            progress = report.accuracy,
            contentDescription = "درصد پاسخ درست ${skillLabelFa(report.skill)}",
        )
    }
}

/** Persian names for the skills the placement test can measure. */
private fun skillLabelFa(skill: Skill): String = when (skill) {
    Skill.VOCABULARY -> "واژگان"
    Skill.GRAMMAR -> "گرامر"
    Skill.READING -> "درک مطلب"
    Skill.WRITING -> "نوشتن"
    Skill.LISTENING -> "شنیدن"
    Skill.SPEAKING -> "گفتار"
}
