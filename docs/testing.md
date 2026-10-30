# Testing

> Moved out of the root `AGENTS.md` (checklist v10). The everyday gate commands are also
> repeated in the root file; this is the full inventory and how the suite is meant to grow.

- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`, 444 tests / 57 classes as of
  the dialogue pass — a fresh install is locked with no trial (`TrialClockTest` went
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
  mutation fixtures proving each rule fails), `DialogueContentTest` (the dialogue turns the practice
  screen consumes: every `speaking` item still parses, a `question` is English and always carries a
  Persian `questionFa`, a question is never its own answer, and the authored spread still reaches
  A1, A2, B1 and B2), `ReadingContentTest`
  (A-5 passages + meaning items + placement reading), `KnowledgeEngineTest`
  (evidence attribution), `GrammarTipTest` (multi-section lesson tips), `PhonologyCoverageTest`
  (A1 pronunciation coverage), `GrammarCoverageTest` (six exercises per grammar topic),
  `RoomMigrationTest` (migration ↔ exported schema, no gaps) + `DatabaseUpgradeTest` (the same
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
  repeats a word ("go straight … go straight and turn left") is still the target said back),
  `ReviewLifecycleTest` (pinned SRS lifecycle numbers), `LearningSimulationTest`.
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
  `ConversationPracticeViewModelTest` (turns wait for the content seeder instead of reading an empty
  table; only speaking items become turns, in lesson order, each carrying its lesson title; an
  authored `question` becomes the partner's line with its Persian gloss while an echo item has none;
  the partner opens the conversation and can repeat itself on request; a partial is shown live and
  never graded mid-take; **a sentence paused mid-way is graded as the whole sentence** (the sequence
  that used to wipe the first half); a finished take is graded exactly once; a take with no
  recognised words is `unheard`, not a wrong answer; a take broken by the recogniser is not graded
  at all; retry wipes the previous feedback and restarts the mic; next records the exchange, speaks
  the following question, and a NEAR answer can still move on so one hard sentence is never a dead
  end; leaving stops the microphone), `SettingsViewModelTest` (each switch persists, reset clears progress *and* knowledge state, a
  settings write never wipes the access status), `ProgressViewModelTest` (the three tracks reach the
  state and an empty bundle yields no tracks rather than empty bars), `PlacementViewModelTest` (adaptive stop after
  an unwinnable band, the band-based level, C2 on a perfect run, mastery calibration, and the
  onboarding write), `VocabularyViewModelTest` (all words load; the query matches the English word
  case-insensitively and the Persian meaning; a blank query clears the filter; no matches is an
  empty list, not an error) and  `OnboardingViewModelTest` (skipping the test pins A1 and finishes
  onboarding) — the billing double is `FakeBillingGateway` in the same file. They need
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
  `ConversationPracticeViewModel` (the turns, when a take is graded, what an unheard take means) and
  `DialogueContentTest` (the authored turns) run as normal unit tests with a fake `SpeechState` flow.
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
   «چیزی نشنیدم», never «درست نبود», and no verdict is recorded.
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
