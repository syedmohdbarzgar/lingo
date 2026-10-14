# Testing

> Moved out of the root `AGENTS.md` (checklist v10). The everyday gate commands are also
> repeated in the root file; this is the full inventory and how the suite is meant to grow.

- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`, 344 tests / 47 classes as of
  the v10 B-5/B-10/A-2c pass): `ReviewSchedulerTest`,
  `DomainEngineTest` (mastery/answer checking/planner), `TimeUtilTest` (streaks),
  `EntitlementPolicyTest` (trial/subscription/companion gating) + `CompanionAppTest`
  (companion package id + Bazaar links + digest shape) + `CompanionSignatureTest`
  (certificate normalization/matching, and the release gate's own view of the constant:
  `declaredValue`/`declarationIsConfigured` are asserted against the real source so a
  valid digest can never silently start failing the release build) + `SubscriptionRecoveryTest` (renewal
  boundaries, reinstall restore, store-truth reconciliation, and Myket's fixed-length
  `flatPeriodExpiry` reinstall restore — 30/365 flat days from the owned purchase time), `KnowledgeGraphTest` (curriculum
  graph), `ContentSeederTest` / `ContentDistributionTest` (content pipeline) /
  `VocabularyContentTest` (example-contains-word + same-lesson distractors), `ReadingContentTest`
  (A-5 passages + meaning items + placement reading), `KnowledgeEngineTest`
  (evidence attribution), `GrammarTipTest` (multi-section lesson tips), `PhonologyCoverageTest`
  (A1 pronunciation coverage), `GrammarCoverageTest` (six exercises per grammar topic),
  `RoomMigrationTest` (migration ↔ exported schema, no gaps) + `DatabaseUpgradeTest` (the same
  chain *executed* on a learner-filled v1 database: every value survives, added columns take their
  documented defaults, the graph tables arrive empty — needs the test-only `org.xerial:sqlite-jdbc`,
  because it upgrades with a real SQLite engine rather than inspecting the schemas),
  `BidiTextTest` (BiDi direction rule + no Persian in English exercise fields), `PlacementAssessmentTest` (per-skill read from the
  placement answers), `LearningPathJourneyTest` (install → placement →
  lesson → miss/retry → complete → review → progress, on the real bundle, incl. placement
  calibrating mastery), plus the
  adaptive layer —
  `PrerequisiteEngineTest`, `AdaptiveLearningPlannerTest` (never exceeds the daily target),
  `MasteryProfileEngineTest`, `AdaptiveExerciseSelectorTest`, `RemediationEngineTest`
  (incl. the evidence filter, "an unassessed dimension must not reorder a lesson", and "a first
  correct answer does not fix a weak node"),
  `FocusPlanTest` (the weak-spot decision: one node, its evidence subset, its blocked dependents),
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
  `ReviewLifecycleTest` (pinned SRS lifecycle numbers), `LearningSimulationTest`.
- **ViewModel tests (B-6)** sit on top of `FakeRepositories.kt` (in-memory repositories + a
  `RecordingSpeaker`), so a screen's ViewModel is exercised with no Android, no Room and no device:
  `HomeViewModelTest` (installing/removing the companion app flips access to COMPANION_APP / LOCKED;
  the engine's weak spot reaches the state), `LessonViewModelTest` (a miss is re-asked exactly once
  and the session still ends; a device without an English voice reports `audioUnavailable`; a fresh
  lesson keeps the authored order while an untested dimension is front-loaded; a focused session
  drills only the node's evidence; cancelling the scope mid-`getFocusPlan` loads nothing — B-9),
  `ReviewViewModelTest` (GOOD records the schedule and finishes; AGAIN re-queues the item in the
  same session), `PaywallViewModelTest` (priced/unpriced plans, unreachable store, purchase
  cancelled/failed/succeeded, a checkout that throws, restore active/none/store-failure, the
  companion grant, and the storefront reaching the state so the wording can match the market),
  `SettingsViewModelTest` (each switch persists, reset clears progress *and* knowledge state, a
  settings write never wipes the access status), `ProgressViewModelTest` (the three tracks reach the
  state and an empty bundle yields no tracks rather than empty bars), `PlacementViewModelTest` (adaptive stop after
  an unwinnable band, the band-based level, C2 on a perfect run, mastery calibration, and the
  onboarding write), `VocabularyViewModelTest` (all words load; the query matches the English word
  case-insensitively and the Persian meaning; a blank query clears the filter; no matches is an
  empty list, not an error) and `OnboardingViewModelTest` (skipping the test pins A1 and finishes
  onboarding) — the billing double is `FakeBillingGateway` in the same file. They need
  `testOptions { unitTests.isReturnDefaultValues = true }` (the ViewModels read `android.os.SystemClock`)
  and `Dispatchers.setMain(UnconfinedTestDispatcher())`. Future ViewModel tests: keep the fake layer
  growing rather than mocking, and derive display-shuffled indices from the VM state — never assume an
  option index is wrong.
- `.github/workflows/ci.yml` runs the same two gates (validate + unit tests) plus `lintBazaarDebug`
  and the three flavor assemblies (the last only on schedule/manual dispatch/tags).
- Content tests read the assets through `File`, so Gradle cannot see them as inputs — after a
  content edit run them with `--rerun` or they silently report UP-TO-DATE.
- There is **no instrumented suite**: the Android Studio template was removed rather than kept
  as a false signal (it asserted nothing and ran in no CI). UI changes rely on manual device
  verification, as above.
- When you change engine logic, extend these tests first; UI changes rely on manual verification
  (no emulator CI in this environment).
