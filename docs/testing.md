# Testing

> Moved out of the root `AGENTS.md` (checklist v10). The everyday gate commands are also
> repeated in the root file; this is the full inventory and how the suite is meant to grow.

- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`, 426 tests / 56 classes as of
  the conversation-practice pass — a fresh install is locked with no trial (`TrialClockTest` went
  with the clock; `StorePageTest` guards the per-flavor «ارتباط با ناشر» store link;
  `ConversationScorerTest` pins the «درست / نزدیک / تلاش دوباره» verdicts): `ReviewSchedulerTest`,
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
  mutation fixtures proving each rule fails), `ReadingContentTest`
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
  NEAR, a reorder is NEAR not CORRECT, an unrelated sentence and a silent take are RETRY, and the
  substring trap — "the" inside "other" — never counts as a keyword hit),
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
  `ConversationPracticeViewModelTest` (targets wait for the content seeder instead of reading an
  empty table; only speaking items become targets, in lesson order; a
  partial shows while speaking and the final take is graded once; a repeated emission is not
  re-graded while a blank take clears the old verdict; retry wipes the previous feedback; next
  advances and then reports the session finished; the model sentence is spoken on request; leaving
  stops the microphone), `SettingsViewModelTest` (each switch persists, reset clears progress *and* knowledge state, a
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
- **Conversation practice is engine-tested, transport-untested.** `ConversationScorer` (the verdict)
  and `ConversationPracticeViewModel` (when a take is graded, when it is cleared, what an empty
  bundle shows) run as normal unit tests with a fake `SpeechState` flow. Everything below the
  ViewModel — `AudioRecord`, `Recognizer`, the `microphone` foreground service, and whether a given
  phone actually ships the model — is verified **only on a real device**, because none of it exists
  on the host JVM. `ConversationScorerTest` is the whole automated net for the feature; do not read a
  green suite as "recognition works".
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
3. **Live text.** Say a target sentence at normal speed: partial text appears while speaking (not
   only at the end), the final line lands at the sentence's end, and each partial replaces the last
   instead of accumulating.
4. **Verdicts.** Say the sentence exactly (CORRECT / «درست»), say it with one content word missing or
   wrong (NEAR / «نزدیک»), say something unrelated or go silent (RETRY / «تلاش دوباره»). Each verdict
   shows icon **and** text, and «ضبط دوباره» clears the previous verdict before the next take.
5. **Screen-off take.** Start a take, turn the screen off, say the sentence, turn it back on: the
   notification is present while recording, the take is graded, and stopping from the notification
   ends it without leaving the mic open (the status-bar mic indicator must disappear).
6. **Backgrounding.** Leave the app mid-take (Home button): recording continues only while the
   foreground notification is up; swiping the task away or leaving the screen stops it.
7. **Missing model.** Temporarily remove the assets directory, rebuild, tap the mic: the screen
   reports the model is missing («مدل تشخیص گفتار روی این نسخه نصب نیست») and stays usable — the rest
   of the app is unaffected.
8. **No network.** Airplane mode on: 1–7 behave identically. If anything differs, an online call
   crept into the feature and the offline rule is broken.
