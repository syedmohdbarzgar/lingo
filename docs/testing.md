# Testing

> Moved out of the root `AGENTS.md` (checklist v10). The everyday gate commands are also
> repeated in the root file; this is the full inventory and how the suite is meant to grow.

- **The evidence rule (Phase 1)** is pinned in two new classes. `EvidenceRuleTest`: one answer per
  exercise per day moves the model — a repeat writes nothing (neither the skill meter nor the
  graph), a *different* exercise in the same shape still counts (a remediation block's questions are
  not taps), the same exercise the next day counts again, a corrected miss counts while a miss after
  a hit does not punish twice, a fresh use case over the same stored claim writes nothing (so a
  restart cannot re-earn it), and a `sentence_production` credits `WRITING` alongside its own skill
  while a multiple choice never does. `LessonMasteryTest`: finished steps are not mastery —
  not-started versus in-progress, completed versus mastered, one weak node holding a lesson back, a
  lesson the graph does not know that can never be mastered, and a mastered count that can never
  exceed the completed one. `ProgressViewModelTest` adds the same distinction through the real
  ViewModel, `ConversationPracticeViewModelTest` adds the spoken-versus-typed routing plus the
  retry/unheard cases, and `DatabaseUpgradeTest` expects `question_attempt` and `lesson_attempt`
  among the tables a v1 install gains (empty) and `lesson_state.stageCompletedAt` to arrive as `''`
  for a lesson finished before the column existed, so the `8->9` step is executed against real
  SQLite like every other.

- **Learning persistence (Phase 2)** is pinned in `LearningPersistenceTest`: the pure rule first
  (`StageResume` resumes on the first question with no recorded answer, a fully answered step stays
  on its last question instead of running past the end, an empty step has nowhere to resume), then
  the wiring — a half-done step reopens on the unanswered question even when the saved cursor points
  at the first one, a lesson with no recorded answers keeps its saved cursor, an answer recorded
  under another step cannot move this step's cursor, every graded answer is appended with its step,
  answer text and verdict (a miss stays a miss), finishing a step stamps the time and the stamp
  survives a restart, and revisiting a finished step does not rewrite when it was finished.
- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`, 580 tests / 67 classes as of
  the Phase-3 content pass, whose eight new cases live in `ContentValidationTest` (no new class: one
  fixture per new semantic rule plus a guard that a hyphenated answer is never a finding); the
  load-failure pass's new class is `LoadFailureTest` (a throwing repository leaves a screen
  `failed`, never spinning, and retry re-runs the load), and on the content side the v63 batch
  reversed one assertion in `RemediationJourneyTest`: the listening item it pinned as wildcard
  evidence now authors `LISTENING`, so it stays out of a grammar block. The v61 C2 review pass added
  two cases: a hyphenated answer (`twenty-five`,
  `decision-making`) and a quoted word (`'May'`), both of which used to leave the recogniser's
  grammar, so no amount of correct speech could be graded `CORRECT`) — the v60 guided-lesson pass
  carrying the eight-step lesson (`GuidedLessonTest`: a lesson opens on its
  context step with no exercise yet, Continue walks the teaching steps and records each one,
  practice does not advance until its exercise is answered, Previous reopens a done step without
  erasing progress, an optional step can be skipped and is never counted as mastery, a required
  step offers no skip, a restart resumes on the saved step with its bookkeeping, a Continue queued
  against a step the session already left is ignored so a double tap cannot skip a card, and the
  review step is the summary that completes the lesson) — the v54 B2 batch — the secured/retest gate (the recognition-endpointing pass's early-end and
  take-ownership rules are pinned in `ConversationPracticeViewModelTest`; `KnowledgeEngineTest` holds
  `KnowledgeGates.isSecured` to its four edges and `AdaptiveLearningPlannerTest` walks the
  same-day → next-day-retest → secured story) — a fresh install is locked with no trial (`TrialClockTest` went
  with the clock; `StorePageTest` guards the per-flavor «ارتباط با ناشر» store link;
  `ConversationScorerTest` pins the «درست / نزدیک / تلاش دوباره» verdicts;
  `DialogueContentTest` holds the authored dialogue turns to their authoring rules): `ReviewSchedulerTest`,
  `DomainEngineTest` (mastery/answer checking/planner), `TimeUtilTest` (streaks),
  `StudyTimePolicyTest` (study time is interaction-based: a tick inside the idle cap credits, past
  it — and on a backwards clock — credits nothing),
  `EntitlementPolicyTest` (subscription/companion gating; a fresh install is locked — no trial) +
  `CompanionAppTest`
  (companion package id + Bazaar links + digest shape) + `CompanionOfferTest`
  (one case per state of the free-route offer: hidden on a device that already holds the
  grant, hidden for a paying subscriber, shown to a learner without the companion, and — N-3 —
  still shown as an *update* for a device holding an untrusted build) +
  `CompanionSignatureTest`
  (certificate normalization/matching, and the release gate's own view of the constant:
  `declaredValue`/`declarationIsConfigured` are asserted against the real source so a
  valid digest can never silently start failing the release build) + `PlayStoreLinkGateTest`
  (v15 N-4: the `EXTERNAL_COMPANION_OFFER` flag is declared by all three flavors, every offer
  surface and `CompanionApp.openDownloadPage` read it, and `docs/release.md` names it — so
  flipping the Play gate reaches the screens and cannot be forgotten) + `DocCodeConsistencyTest`
  (v15 B-12: `docs/debt.md` states the production overlap floor the code actually grades with;
  the doc had drifted to "60%" while the constant said 0.5) + `SubscriptionRecoveryTest` (renewal
  boundaries, reinstall restore, store-truth reconciliation, and Myket's fixed-length
  `flatPeriodExpiry` reinstall restore — 30/365 flat days from the owned purchase time), `KnowledgeGraphTest` (curriculum
  graph), `ContentSeederTest` / `ContentDistributionTest` (content pipeline) /
  `VocabularyContentTest` (example-contains-word + same-lesson distractors), `ContentValidationTest`
  (the content gate itself: the shared buildSrc `ContentValidator` on the real bundle, plus
  mutation fixtures proving each rule fails — and, since v64, one fixture per semantic rule: an item
  whose stem spells its own answer, an `accepted` model under the item's own `minWords`, a listening
  item that accepts something other than what it says, the same item and the same recall question
  shipped twice, an entry repeating its own example, and the same word showing the same example at
  two levels — each beside a negative case that a hyphen (`twenty-six` / `twenty six`) must never be
  a finding), `DialogueContentTest` (the dialogue turns the practice
  screen consumes: every `speaking` item still parses, a `question` is English and always carries a
  Persian `questionFa`, a question is never its own answer, **every lesson has at least four turns**,
  every turn names its `sceneFa` and the partner's `partnerFa`, and the authored spread still reaches
  A1, A2, B1 and B2), `DailyGoalCopyTest` (the Home ring's «از ۱۵ دقیقه»: the number is direction-
  isolated, its space before «دقیقه» is non-breaking so the device cannot split the pair across a
  line or reorder it, and the phrase stays an RTL paragraph), `SpeechVocabularyTest` (every spoken
  answer stays inside what the bundled
  recogniser can hear: no proper noun outside the reviewed allowlist, no digit, no non-ASCII),
  `ReadingContentTest`
  (A-5 passages + meaning items + placement reading), `KnowledgeEngineTest`
  (evidence attribution), `GrammarTipTest` (multi-section lesson tips), `PhonologyCoverageTest`
  (A1 pronunciation coverage), `GrammarCoverageTest` (six exercises per grammar topic),
  `RoomMigrationTest` (migration ↔ exported schema, no gaps, plus — v60 — that
  `AppContainer` registers `ALL_MIGRATIONS` wholesale instead of a hand-picked list that once omitted
  a step and crashed every upgrading install) + `DatabaseUpgradeTest` (the same
  chain *executed* on a learner-filled v1 database: every value survives, added columns take their
  documented defaults, the graph tables arrive empty — needs the test-only `org.xerial:sqlite-jdbc`,
  because it upgrades with a real SQLite engine rather than inspecting the schemas),
  `BidiTextTest` (BiDi direction rule + no Persian in English exercise fields), `PlacementAssessmentTest` (per-skill read from the
  placement answers) + `PlacementTypedItemTest` (the typed placement item: parses as the 36th
  question, sits in the A1 band, grades through AnswerChecker, and the screen records it), `LearningPathJourneyTest` (install → placement →
  lesson → miss/retry → complete → review → progress, on the real bundle, incl. placement
  calibrating mastery), plus the
  adaptive layer —
  `PrerequisiteEngineTest`, `AdaptiveLearningPlannerTest` (never exceeds the daily target; a met
  goal collapses the plan to the review block and invents no work when nothing is due),
  `MasteryProfileEngineTest`, `AdaptiveExerciseSelectorTest`, `RemediationEngineTest`
  (incl. the evidence filter, "an unassessed dimension must not reorder a lesson", and "a first
  correct answer does not fix a weak node"),
  `FocusPlanTest` (the weak-spot decision: one node, its evidence subset, its blocked dependents —
  and, v15 B-1, that a node with a single matching exercise still yields a short honest block:
  exactly that exercise, a real reassess bar, stated attempts),
  `SkillProfileEngineTest` (P2: the three tracks fold curriculum + review evidence, mastery is read
  rather than recomputed, the status is evidence-gated so a single correct answer cannot read as
  consolidated, and — on the **real bundle** — all three tracks are funded while the four nodes two
  tracks claim are asserted exhaustively), `ProductionRecallTest` (P2 + v9 B-10: the two new shapes evidence RECALL and PRODUCTION, recall keeps the
  one-edit spelling hint, production credits an original sentence but not one whose anchors are out
  of order, below the word floor, or a filler that merely carries the anchors without reusing the
  target sentence) + `ProductionRecallContentTest` (P2 on the real bundle: every
  authored item parses, the cue is Persian and the answers English, both shapes exist at all six
  levels, and each item's own anchors credit its own exemplar through the structural branch),
  `RemediationJourneyTest` (that same decision evaluated on the **real bundle**: a learner who misses
  a lesson's grammar drills gets a block for exactly that node, error isolation leaves the lesson's
  PHONOLOGY node untouched, a foundational node reports its real dependent count, correct practice
  closes the block, and the plan's bar is asserted **equal** to the engine's reassess bar — the two
  were unified at 0.5 in v9 B-1, and `RemediationEngineTest` proves a first correct answer no longer
  fixes a weak node),
  `ConversationScorerTest` (conversation practice grading: an identical sentence and one with added
  filler are CORRECT, dropping an article stays CORRECT while dropping or swapping a content word is
  NEAR, a reorder is NEAR not CORRECT, an unrelated sentence and a silent take are RETRY, the
  substring trap — "the" inside "other" — never counts as a keyword hit, a contraction is compared
  against its expanded form so "I'm going" matches an authored "I am going", and a false start that
  repeats a word ("go straight … go straight and turn left") is still the target said back; plus the
  recognition-fairness rules of the audit: the **best of the authored answers** decides (a reply the
  bundle lists is never a miss), `please` / `thank you` is stripped so "just water" answers "Just
  water, thank you", recogniser homophones (to/too/two, four/for) are folded on both sides, a single
  edit on a long word is forgiven while short words must match exactly, a take that repeats the
  partner's question is not an answer — and the speech grammar lists every accepted phrase plus
  `[unk]`),
  `ReviewLifecycleTest` (pinned SRS lifecycle numbers), `LearningSimulationTest`.
- **The guided lesson (v60)** splits `LessonViewModelTest`'s territory in two: `GuidedLessonTest`
  drives the eight-step walk itself (see above), while `LessonViewModelTest` keeps the exercise
  rules — the requeue-once-on-a-miss, the TTS-unavailable fallback, the authored order, a focused
  session's evidence subset and the P7 study ticks. `ScreenSmokeTest` renders the Lesson screen
  through the same fakes, so the stage header, rail and Previous/Continue controls are exercised on
  the host JVM (Robolectric) rather than only on a device.
- **ViewModel tests (B-6)** sit on top of `FakeRepositories.kt` (in-memory repositories + a
  `RecordingSpeaker`), so a screen's ViewModel is exercised with no Android, no Room and no device:
  `HomeViewModelTest` (installing/removing the companion app flips access to COMPANION_APP / LOCKED;
  the engine's weak spot reaches the state),  `LessonViewModelTest` (a miss is re-asked exactly once
  and the session still ends; a device without an English voice reports `audioUnavailable`; a fresh
  lesson keeps the authored order while an untested dimension is front-loaded; a focused session
  drills only the node's evidence; cancelling the scope mid-`getFocusPlan` loads nothing — B-9;
  study time follows interactions, so an idle lesson stops crediting — P7),
  `ReviewViewModelTest` (GOOD records the schedule and finishes; AGAIN re-queues the item in the
  same session; study ticks credit only while the learner is recently active and never while the
  app is backgrounded), `PaywallViewModelTest` (priced/unpriced plans, unreachable store, purchase
  cancelled/failed/succeeded, a checkout that throws, restore active/none/store-failure, the
  companion grant, and the storefront reaching the state so the wording can match the market),
  `ConversationGateTest` (the lesson gate: a lesson still in progress owes nothing; the first
  completed lesson without its dialogue blocks the next one; finishing the conversation opens it;
  the oldest owed conversation wins; a lesson with no authored dialogue never gates),
  `ConversationPracticeViewModelTest` (turns wait for the content seeder instead of reading an empty
  table; a session runs ONE lesson's dialogue rather than a pool of every lesson, the requested
  lesson wins, the scene and the partner's role come from the authored turn; finishing every turn
  records the conversation while a skipped turn leaves it unrecorded; an
  authored `question` becomes the partner's line with its Persian gloss while an echo item has none;
  the partner opens the conversation and can repeat itself on request; a partial is shown live and
  never graded mid-take; **a sentence paused mid-way is graded as the whole sentence** (the sequence
  that used to wipe the first half); a finished take is graded exactly once; a take with no
  recognised words is `unheard`, not a wrong answer; a take broken by the recogniser is not graded
  at all; retry wipes the previous feedback and restarts the mic; next records the exchange, speaks
  the following question, and a NEAR answer can still move on so one hard sentence is never a dead
  end; an authored alternative reply grades CORRECT rather than NEAR; the recogniser's grammar holds
  the current turn's own sentences and `[unk]`; a repeated question is REJECTED as an answer; leaving
  stops the microphone; **the keyboard route is the same gate** — a typed answer is graded
  CORRECT/NEAR/RETRY by the same scorer and becomes the recorded transcript, a blank field grades
  nothing, a late recogniser emission cannot overwrite or out-grade a typed answer, typed answers
  alone finish and record the conversation, and retry by voice clears the typed verdict), `SettingsViewModelTest` (each switch persists, reset clears progress *and* knowledge state, a
  settings write never wipes the access status), `ProgressViewModelTest` (the three tracks reach the
  state and an empty bundle yields no tracks rather than empty bars), `PlacementViewModelTest` (adaptive stop after
  an unwinnable band, the band-based level, C2 on a perfect run, mastery calibration, and the
  onboarding write), `VocabularyViewModelTest` (all words load; the query matches the English word
  case-insensitively and the Persian meaning; a blank query clears the filter; no matches is an
  empty list, not an error) and  `OnboardingViewModelTest` (skipping the test pins A1 and finishes
  onboarding), `LoadFailureTest` (v63's failure surfaces: the `catchLoadFailure` operator reports a
  throwing flow and still lets a `CancellationException` through, and a repository that throws on
  read leaves Progress, Vocabulary, Review and Settings in `failed` — not `loading`, and not an
  empty collection the screen would present as "nothing here yet" — with `retry()` re-running the
  same read and clearing it; Settings also asserts it has no `settings` to show, because default
  switches rendered as stored values is a wrong answer stated as fact. Home's own case, the failed
  bundle write and its re-seed, is in `HomeViewModelTest` because it needs the container's seed
  hook) — the billing double is `FakeBillingGateway` in the same file. They need
  `testOptions { unitTests.isReturnDefaultValues = true }` (the ViewModels read `android.os.SystemClock`)
  and `Dispatchers.setMain(UnconfinedTestDispatcher())`. Future ViewModel tests: keep the fake layer
  growing rather than mocking, and derive display-shuffled indices from the VM state — never assume an
  option index is wrong.
- **Screen smoke tests (v15 B-6)** — `ScreenSmokeTest`, one Compose assertion per main screen
  (Home, Lesson, Review, Paywall), running on the **host JVM through Robolectric 4.17** (no emulator
  exists in this environment). Each screen takes its ViewModel through the `vm` parameter seam added
  in v15 (production omits it and gets the container-wired one, exactly as before), built from the
  same `FakeRepositories.kt` layer, so the tests reach what the ViewModel tests cannot: the screen
  renders its state, Home shows the N-3 "update from Bazaar" copy for an untrusted companion, and
  the paywall really renders the free-route card next to the price. They need
  `unitTests.isIncludeAndroidResources = true`, `ui-test-junit4` (v2 rule) + `ui-test-manifest`
  (debugImplementation) and `@Config(sdk = [35])`.
- `.github/workflows/ci.yml` runs the same two gates (validate + unit tests) plus `lintBazaarDebug`
  and the three flavor assemblies (the last only on schedule/manual dispatch/tags).
- Content tests read the assets through `File`, so Gradle cannot see them as inputs — after a
  content edit run them with `--rerun` or they silently report UP-TO-DATE.
- There is **no instrumented suite**: the Android Studio template was removed rather than kept
  as a false signal (it asserted nothing and ran in no CI). UI changes get their baseline from the
  Robolectric screen smoke tests above; beyond that, rely on manual device verification, as below.
- **Conversation practice is engine-tested, transport-untested.** `ConversationScorer` (the verdict),
  `ConversationPracticeViewModel` (the turns, when a take is graded, what an unheard take means, the
  grammar it hands the recogniser) and `DialogueContentTest` (the authored turns) run as normal unit
  tests with a fake `SpeechState` flow. The grammar string is asserted here; whether Vosk *honours*
  it, and how much it actually improves recognition, is device-only.
  Everything below the ViewModel — `AudioRecord`, `Recognizer`, the per-take segment accumulation
  that makes a pause survivable, the `microphone` foreground service, and whether a given phone
  actually ships the model — is verified **only on a real device**, because none of it exists on the
  host JVM. Do not read a green suite as "recognition works".
- When you change engine logic, extend these tests first; UI changes rely on manual verification
  (no emulator CI in this environment).

## Manual device pass — conversation practice

Everything here needs a phone, a voice and a microphone; none of it is reachable from the JVM. Run it
before calling a conversation-practice change done:

1. **Model install.** Fresh install (`adb uninstall org.token.english` first), open the practice
   screen, tap the mic. First take shows the preparing spinner, not an error; the model appears under
   `/data/data/org.token.english/files/vosk/<model>/` with a `uuid` file
   (`adb shell run-as org.token.english ls files/vosk`). A second take does **not** re-copy or
   re-load it (watch logcat for a second `Model(...)` construction — there must not be one).
2. **Permission.** Deny `RECORD_AUDIO` at the prompt → the screen says the mic is blocked and offers
   the settings route; nothing crashes. Grant it from Settings and the take works without a restart.
3. **Live text.** Answer at normal speed: partial text appears while speaking (not only at the end)
   and each partial replaces the previous *partial*.
4. **Pauses keep the sentence.** Say the first half of a long turn (use a B2 interview turn), pause
   three seconds, then say the second half. The take keeps both halves — the transcript grows instead
   of restarting — and the whole sentence is graded. This is the fix for the reported «اگر مکث کنم
   جمله پاک می‌شود»; if the first half disappears, `SpeechRecognitionService` is publishing one
   segment instead of the accumulated take.
5. **Verdicts.** Answer exactly the model (CORRECT / «درست»), answer with one content word missing or
   wrong (NEAR / «نزدیک»), answer something unrelated (RETRY / «تلاش دوباره»). Each verdict shows icon **and**
   text, «تلاش دوباره» clears the previous verdict before the next take, and «ادامهٔ گفتگو» is
   available even after a NEAR so one hard sentence is never a dead end.
6. **Silence is not a wrong answer.** Start a take and say nothing, then stop: the copy must be
   «چیزی نشنیدم», never «درست نبود», and no verdict is recorded. The mic stays open while nothing has
   been recognised (an untouched take is never auto-ended); once something is recognised, ~4 s of
   quiet ends the take and grades it without a tap — the verdict must appear on its own.
7. **Dialogue.** The partner's line is spoken when a turn starts, shows its Persian gloss under the
   English, and «شنیدن دوباره» replays it. A correct reply advances to the next turn, and the
   transcript above the live turn lists what was said before.
8. **Speech rate.** Listen to a partner line: English is spoken at a learner's pace (TTS
   `LEARNER_SPEECH_RATE`, 0.75×), not native speed — the reported «نمی‌فهمم» was a rate set too high.
9. **Screen-off take.** Start a take, turn the screen off, say the answer, turn it back on: the
   notification is present while recording, the take is graded, and stopping from the notification
   ends it without leaving the mic open (the status-bar mic indicator must disappear).
10. **Backgrounding.** Leave the app mid-take (Home button): recording continues only while the
    foreground notification is up; swiping the task away or leaving the screen stops it.
11. **Missing model.** Temporarily remove the assets directory, rebuild, tap the mic: the screen
    reports the model is missing («مدل گفتار پیدا نشد») and stays usable — the rest of the app is
    unaffected.
12. **No network.** Airplane mode on: 1–11 behave identically. If anything differs, an online call
    crept into the feature and the offline rule is broken.
