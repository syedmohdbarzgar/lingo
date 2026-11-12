package org.token.english.feature.lesson

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import org.token.english.core.billing.StorePage
import org.token.english.core.common.persianCountPair
import org.token.english.core.common.persianMeasure
import org.token.english.core.common.persianPercent
import org.token.english.core.common.persianStepLabel
import org.token.english.core.designsystem.AppSpacing
import org.token.english.core.designsystem.LocalAppExtendedColors
import org.token.english.core.designsystem.labelFa
import org.token.english.core.designsystem.component.AppCard
import org.token.english.core.designsystem.component.AppEmptyState
import org.token.english.core.designsystem.component.AppLinearProgress
import org.token.english.core.designsystem.component.CorrectBanner
import org.token.english.core.designsystem.component.EnglishText
import org.token.english.core.designsystem.component.IncorrectBanner
import org.token.english.core.designsystem.component.InfoBanner
import org.token.english.core.designsystem.component.OutlinedActionButton
import org.token.english.core.designsystem.component.isolateEmbeddedLtr
import org.token.english.core.designsystem.component.PrimaryButton
import org.token.english.core.designsystem.component.SectionHeader
import org.token.english.core.designsystem.component.StepChange
import org.token.english.core.designsystem.component.stepChange
import org.token.english.di.lessonViewModelFactory
import org.token.english.domain.engine.SpeakingVerdict
import org.token.english.domain.engine.StudyTimePolicy
import org.token.english.domain.model.AnswerChecker
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.GrammarSection
import org.token.english.domain.model.GrammarSectionKind
import org.token.english.domain.model.LearningStage
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
    /**
     * Screen-level seam (checklist v15 B-6): the smoke tests inject a ViewModel
     * built from fakes; production omits this and gets the container-wired one.
     */
    vm: LessonViewModel = viewModel(factory = lessonViewModelFactory(lessonId, focusItemId)),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    // The publisher page is this app's support channel, so the error state can hand
    // the learner a way to report a lesson that will not open.
    val context = LocalContext.current

    // Study time is sampled only while THIS screen is resumed (P7): a lesson
    // left on the back stack never credits a second, and ticking stops the
    // moment the app goes to the background.
    val lifecycleOwner = LocalLifecycleOwner.current
    var isResumed by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    DisposableEffect(lifecycleOwner) {
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                val resumed = source.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                isResumed = resumed
                // Leaving the foreground closes the mic and silences both voices:
                // a recording must never keep running behind the home screen.
                if (!resumed) vm.onAudioInterrupted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            isResumed = false
            // Closing the lesson releases the same hardware the background does.
            vm.onAudioInterrupted()
        }
    }
    LaunchedEffect(isResumed) {
        while (isResumed) {
            delay(StudyTimePolicy.TICK_MILLIS)
            vm.onStudyTick()
        }
    }

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

                // A lesson that cannot be read at all is the critical case of P1-7:
                // the learner cannot study without it. Retry is the first action (the
                // data is local, so it is the same read again), and the publisher page
                // is the second — a dead end with only «بازگشت» left them with nowhere
                // to say it happened. The top bar still offers the way out.
                state.error != null -> AppEmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "مشکلی پیش آمد",
                    subtitle = state.error.orEmpty(),
                    actionText = "تلاش دوباره",
                    onAction = vm::retry,
                    secondaryActionText = "ارتباط با پشتیبانی",
                    onSecondaryAction = { StorePage.open(context) },
                    modifier = Modifier.align(Alignment.Center),
                )

                // The bundle write failed, so this lesson was never written: the
                // read below never ran. This is the state that used to fall through
                // to the spinner under `activeStage == null` and wait forever for a
                // seed nobody would start; the retry now runs the write itself.
                state.contentFailed -> AppEmptyState(
                    icon = Icons.Outlined.ErrorOutline,
                    title = "محتوای درس‌ها آماده نشد",
                    subtitle = "این درس از بستهٔ آفلاین روی همین گوشی خوانده می‌شود و " +
                        "آماده‌سازی آن کامل نشد. این خطا به اینترنت مربوط نیست؛ «تلاش دوباره» " +
                        "همان آماده‌سازی را از نو اجرا می‌کند.",
                    actionText = "تلاش دوباره",
                    onAction = vm::retry,
                    secondaryActionText = "ارتباط با پشتیبانی",
                    onSecondaryAction = { StorePage.open(context) },
                    modifier = Modifier.align(Alignment.Center),
                )

                state.completed -> LessonSummary(
                    correct = state.correctCount,
                    total = state.askedCount,
                    firstAttemptCorrect = state.firstAttemptCorrect,
                    firstAttemptAsked = state.firstAttemptAsked,
                    consolidated = state.isConsolidated,
                    weakAnswers = state.weakAnswers,
                    weakestDimension = state.weakestDimension,
                    onDone = onExit,
                    modifier = Modifier.align(Alignment.Center),
                )

                // One active step, never the whole lesson at once (checklist Phase 2).
                // A teaching step (context / grammar / vocabulary / review) has no
                // exercise to grade; everything else runs this step's own items.
                state.activeStage == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.isTeachingStage -> TeachingContent(state = state, onEvent = vm::onEvent)

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
                text = "تسلط فعلی ${persianPercent(focus.masteryPercent)} — هدف ${persianPercent(focus.targetPercent)}",
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
    // The microphone, asked for once per screen: the speaking card shows the
    // sentence and what was heard, and the shared action row below owns the button.
    // Hoisted here because both need the launcher and the refusal flag.
    var micRefused by remember { mutableStateOf(false) }
    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        micRefused = !granted
        if (granted) onEvent(LessonEvent.StartTake)
    }
    val micGranted: () -> Boolean = {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
    }
    val startTake: () -> Unit = {
        if (micGranted()) {
            onEvent(LessonEvent.StartTake)
        } else {
            // Asked here, not at app start: the microphone belongs to this card.
            micPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    // A new question starts at the top. The card fades in, so it must not fade in at
    // the offset the reader had scrolled the previous question to — the animation would
    // be revealing a line they cannot see the start of.
    val scroll = remember(exercise.id) { ScrollState(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        StageRail(state = state)
        StageHeader(state = state)
        Text(
            // The total grows when a missed exercise is re-queued in this session.
            text = persianStepLabel("تمرین", state.askedCount + 1, state.visibleTotal),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        state.focus?.let { FocusCard(it) }

        // The next question is a step change, not a new screen: a short fade and a
        // few dp of rise, keyed on the exercise id so grading it never re-plays.
        AppCard(modifier = Modifier.stepChange(exercise.id)) {
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

                // P2: recall the English word from its meaning — no options to
                // recognize, so a right answer is evidence of retrieval.
                is Exercise.WordRecall -> {
                    SectionHeader("واژهٔ انگلیسی را بنویسید")
                    Text(
                        text = exercise.prompt,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    AnswerField(
                        value = state.textAnswer,
                        onValueChange = { onEvent(LessonEvent.TextAnswerChanged(it)) },
                        enabled = state.outcome == null,
                        label = "واژهٔ انگلیسی",
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

                // P2: build the whole sentence yourself. No bank, no options — the
                // grader checks the target structure rather than one wording.
                is Exercise.SentenceProduction -> {
                    SectionHeader("جمله را بسازید")
                    Text(
                        text = exercise.prompt,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    exercise.hintFa?.let { hint ->
                        Text(
                            text = hint,
                            // Authored Persian that may open with an English anchor
                            // word: pin RTL so first-strong cannot flip it.
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDirection = androidx.compose.ui.text.style.TextDirection.Rtl,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    AnswerField(
                        value = state.textAnswer,
                        onValueChange = { onEvent(LessonEvent.TextAnswerChanged(it)) },
                        enabled = state.outcome == null,
                        label = "پاسخ انگلیسی",
                    )
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
                    // Speaking practice, graded offline: the model sentence from TTS,
                    // then the learner's own take through the on-device recogniser.
                    // Nothing is recorded to disk, uploaded or sent anywhere — the
                    // verdict is formed on this phone (docs/connectivity.md).
                    SectionHeader("تمرین گفتار")
                    SpeakingCard(
                        modelSentence = exercise.referenceAnswers.firstOrNull().orEmpty(),
                        promptFa = exercise.prompt,
                        isPlayingModel = state.isPlaying,
                        isListening = state.isListening,
                        isPreparing = state.isPreparing,
                        heardPartial = state.heardPartial,
                        heardText = state.heardText,
                        unheard = state.unheard,
                        interrupted = micRefused || state.recordingFailed,
                        onPlayModel = { onEvent(LessonEvent.SpeakText(exercise.referenceAnswers.firstOrNull().orEmpty())) },
                    )
                }
            }

            // Read the outcome once: a second null check on the same state was
            // dead (the compiler said so) and hid which branch actually runs.
            val outcome = state.outcome
            if (outcome != null) {
                // A spoken item has three outcomes, not two: the sentence said back,
                // the sentence said with a slip, and a take the scorer could not match.
                // None of them is a spelling slip, so the A-2 copy never applies here.
                val verdict = outcome.spokenVerdict
                when {
                    exercise is Exercise.Speaking && verdict == null -> InfoBanner(
                        modifier = Modifier.fillMaxWidth(),
                        message = "این جمله بدون سنجش پذیرفته شد — میکروفون یا مدل گفتار در دسترس نبود.",
                    )

                    verdict == SpeakingVerdict.NEAR -> InfoBanner(
                        modifier = Modifier.fillMaxWidth(),
                        message = "نزدیک بود! جمله تقریباً گفته شد: «${state.heardText}»",
                    )

                    verdict == SpeakingVerdict.RETRY -> {
                        IncorrectBanner(
                            modifier = Modifier.fillMaxWidth(),
                            message = "آنچه شنیدم با جملهٔ نمونه هم‌خوان نبود: «${state.heardText}» — " +
                                "دوباره بگو یا ادامه بده؛ جمله: ${outcome.correctAnswer}",
                        )
                        OutlinedActionButton(
                            text = "دوباره بگو",
                            onClick = { onEvent(LessonEvent.RetryTake) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    outcome.correct -> {
                        CorrectBanner(Modifier.fillMaxWidth())
                        // A production item has many valid sentences, so passing
                        // only means *a* valid wording — show the authored model
                        // answer too, pass or fail (checklist B-10).
                        if (exercise is Exercise.SentenceProduction) {
                            ModelAnswer(outcome.correctAnswer)
                        }
                    }

                    else -> {
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
            if (exercise is Exercise.Speaking) {
                // One action row for the take, the way every other item has one: the
                // card holds the sentence and what was heard, the button holds the mic.
                when {
                    state.isListening -> PrimaryButton(
                        text = "پایان پاسخ",
                        onClick = { onEvent(LessonEvent.StopTake) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    state.isPreparing -> PrimaryButton(
                        text = "در حال آماده‌سازی…",
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    state.recordingFailed -> {
                        PrimaryButton(
                            text = "ادامه بدون سنجش",
                            onClick = { onEvent(LessonEvent.ContinueUnassessed) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedActionButton(
                            text = "تلاش دوباره",
                            onClick = startTake,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    else -> PrimaryButton(
                        // A take that produced no words asks for the mic again, not
                        // for a verdict the app does not have.
                        text = if (state.unheard) "دوباره بگو" else "شروع پاسخ",
                        onClick = startTake,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                val canSubmit = when (exercise) {
                    is Exercise.MultipleChoice -> state.selectedOption != null
                    else -> state.textAnswer.isNotBlank()
                }
                PrimaryButton(
                    text = "بررسی پاسخ",
                    onClick = { onEvent(LessonEvent.Submit) },
                    enabled = canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            StageNav(
                state = state,
                onEvent = onEvent,
                label = if (state.remainingCount == 0 && !state.repeatsCurrent) {
                    if (state.hasNextStage) "مرحلهٔ بعد" else "پایان درس"
                } else {
                    "ادامه"
                },
            )
        }
    }
}

/**
 * The eight-step map of the lesson (checklist Phase 2): completed, active,
 * upcoming and not-in-this-lesson are four different states, and the dot row is
 * paired with a text label so the map is never carried by colour alone.
 */
@Composable
private fun StageRail(state: LessonUiState) {
    val stageState = state.stageState ?: return
    val plan = stageState.plan
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        plan.stages.forEach { unit ->
            val done = stageState.isCompleted(unit.stage)
            val skipped = stageState.isSkipped(unit.stage)
            val active = unit.stage == stageState.active
            val color = when {
                done -> MaterialTheme.colorScheme.primary
                active -> MaterialTheme.colorScheme.primary
                skipped -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        if (active || done) color else MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .then(
                        if (active) {
                            Modifier.border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                        } else {
                            Modifier
                        },
                    ),
            )
        }
    }
}

/** Title of the active step, its number, and the lesson's overall percentage. */
@Composable
private fun StageHeader(state: LessonUiState) {
    val stage = state.activeStage ?: return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Text(
                text = persianStepLabel("مرحلهٔ ${stage.labelFa}", state.stageNumber, state.stageTotal),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "پیشرفت درس ${persianPercent(state.lessonPercent)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = persianCountPair(state.stageNumber, state.stageTotal),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * Stage navigation (checklist Phase 2): Previous never erases an answer, the
 * primary action validates the step, and only an **optional** step offers Skip —
 * labelled as a skip, so it can never read as mastery.
 */
@Composable
private fun StageNav(
    state: LessonUiState,
    onEvent: (LessonEvent) -> Unit,
    label: String,
    primaryEnabled: Boolean = true,
    onPrimary: (() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.hasPreviousStage) {
                TextButton(onClick = { onEvent(LessonEvent.Previous) }) {
                    Text("مرحلهٔ قبل")
                }
            }
            PrimaryButton(
                text = label,
                onClick = onPrimary ?: { onEvent(LessonEvent.Next(state.activeStage)) },
                enabled = primaryEnabled,
                modifier = Modifier.weight(1f),
            )
        }
        if (state.activeStage?.required == false) {
            TextButton(
                onClick = { onEvent(LessonEvent.SkipStage) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("رد کردن این مرحله (اختیاری)")
            }
        }
    }
}

/**
 * The authored model answer for a production item, shown whether the learner
 * passed or not (checklist B-10). A passing answer only proves *one* valid
 * wording, so the exemplar still gives the learner something to compare against.
 * Wrapped in [EnglishText] so a raw English sentence stays LTR inside the
 * forced-RTL layout, and paired with a Persian label so the signposting is not
 * carried by colour alone.
 */
@Composable
private fun ModelAnswer(text: String) {
    if (text.isBlank()) return
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        Text(
            text = "پاسخ نمونه",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        EnglishText(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LessonSummary(
    correct: Int,
    total: Int,
    firstAttemptCorrect: Int,
    firstAttemptAsked: Int,
    consolidated: Boolean,
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
            text = "${persianCountPair(correct, total)} پاسخ درست",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        // The mastery gate: first-pass accuracy decides whether the lesson is
        // settled or needs a review. A miss fixed on the repeat is progress, but it
        // is not the same as knowing it the first time — saying so here is what
        // makes the next step (review vs. the next lesson) honest. Icon + text,
        // never colour alone.
        if (firstAttemptAsked > 0) {
            val percent = (firstAttemptCorrect * 100) / firstAttemptAsked
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
            ) {
                Icon(
                    imageVector = if (consolidated) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Filled.Refresh
                    },
                    contentDescription = null,
                    tint = if (consolidated) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Text(
                    text = if (consolidated) {
                        "تثبیت‌شده — ${persianPercent(percent)} درست در تلاش اول"
                    } else {
                        "نیاز به مرور — ${persianPercent(percent)} درست در تلاش اول"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
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

/**
 * A teaching step: the lesson's context, its grammar page, or its vocabulary —
 * whichever step is active, and nothing else (checklist Phase 2/3). The step is
 * completed by its own Continue: a teaching page has nothing to grade, so it is
 * never marked via a test it did not give.
 */
@Composable
private fun TeachingContent(state: LessonUiState, onEvent: (LessonEvent) -> Unit) {
    val stage = state.activeStage ?: return
    // Same rule as a question: the teaching page arrives at its top, because the step
    // below is the thing that just appeared.
    val scroll = remember(stage) { ScrollState(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        StageRail(state = state)
        StageHeader(state = state)
        state.focus?.let { FocusCard(it) }

        // One step at a time, and the step arrives: the rail and the header stay put
        // because they describe the whole lesson, while the page below them is what
        // actually changed. Keyed on the stage, so answering a question on this step
        // never re-plays the entrance.
        StepChange(
            changeKey = stage,
            // The wrapper must not change the layout the step had before it existed.
            modifier = Modifier.fillMaxWidth(),
        ) {
            when (stage) {
                LearningStage.CONTEXT -> ContextCard(state = state)
                LearningStage.GRAMMAR -> state.lesson?.let { GrammarTeachingCards(lesson = it) }
                LearningStage.VOCABULARY -> VocabularyCard(state = state, onEvent = onEvent)
                else -> Unit
            }
        }

        StageNav(state = state, onEvent = onEvent, label = "ادامه")
    }
}

/** Step 1: what this lesson is about, what the learner will be able to do, how long. */
@Composable
private fun ContextCard(state: LessonUiState) {
    val lesson = state.lesson ?: return
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SectionHeader("موقعیت و هدف")
            Text(text = lesson.titleFa, style = MaterialTheme.typography.headlineSmall)
            EnglishText(
                text = lesson.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "در این درس دربارهٔ «${lesson.topic}» حرف می‌زنی و در پایان می‌توانی " +
                    "واژه‌ها و ساختار آن را در یک موقعیت واقعی به کار ببری.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "زمان تقریبی: ${persianMeasure(lesson.estimatedMinutes, "دقیقه")} — " +
                    "${persianCountPair(state.plan?.stages?.size ?: 0, 8)} مرحلهٔ این درس",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Step 3: the lesson's six words with pronunciation, meaning, examples and their gloss. */
@Composable
private fun VocabularyCard(state: LessonUiState, onEvent: (LessonEvent) -> Unit) {
    if (state.vocabulary.isEmpty()) return
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
                        // so the step teaches meaning, not just form.
                        item.examples.forEach { example ->
                            EnglishText(
                                text = example.en,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (example.fa.isNotBlank()) {
                                Text(
                                    text = isolateEmbeddedLtr(example.fa),
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
                            Icons.AutoMirrored.Filled.VolumeUp,
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

/**
 * A-8 stage 1: the lesson's grammar teaching page as one card per section —
 * rule, forms table, bilingual examples, common mistakes of Persian speakers.
 * Sections come from the multi-section format inside `grammarTipFa`; a legacy
 * plain tip parses to a single TIP section and renders like the old card.
 */
@Composable
private fun GrammarTeachingCards(lesson: Lesson) {
    // One tint per kind of content, all from the theme: the rule is the teaching
    // point (brand tint), the table is reference data (info tint), examples are
    // models (elevated surface) and mistakes keep the warning tint. Same layout,
    // different field — a page of identical white boxes gives the eye no map.
    val extended = LocalAppExtendedColors.current
    lesson.grammarSections.forEach { section ->
        when (section.kind) {
            GrammarSectionKind.EXAMPLES -> GrammarExamplesCard(section)
            GrammarSectionKind.TABLE -> GrammarTableCard(section)
            GrammarSectionKind.MISTAKES -> GrammarMistakesCard(section)
            else -> AppCard(
                containerColor = extended.brandTint,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                    SectionHeader(section.title)
                    Text(
                        // Persian prose with embedded English: isolate every
                        // Latin run so periods and quotes can't reorder against
                        // the Persian, and pin RTL so a leading Latin run can't
                        // flip the paragraph (first-strong).
                        text = isolateEmbeddedLtr(section.lines.joinToString("\n")),
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
    AppCard(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
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
    AppCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
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
                        .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
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
                text = isolateEmbeddedLtr(line),
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
