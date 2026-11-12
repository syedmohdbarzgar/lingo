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
- **One graded result is one transaction (checklist 0.1)** is pinned on real Room, not on a fake:
  `AnswerTransactionTest` (Robolectric, in-memory database) writes the production shape of an answer —
  the graded exercise plus its history row — inside `ProgressRepository.inTransaction`, throws at the
  worst moment (both writes done, nothing committed) and asserts that all four tables are back to
  empty: no `lesson_attempt`, no `knowledge_state`, no `skill_mastery`, and the day's claim still
  unspent, so the question is unanswered rather than half-recorded. Two more cases keep the happy
  path honest (everything lands together, and a repeated answer adds history while crediting the
  model once). The test was checked against its own regression: dropping the transaction makes the
  rollback case fail. `LessonViewModelTest` adds the wiring — the session submits an answer inside
  exactly one transaction, and a transaction that cannot open still produces a verdict with nothing
  written, instead of swallowing the tap.
  `EvidenceTransactionTest` covers the other two graded results the same way: a **review grade**
  (schedule, attempt log, skill meter) and a **conversation turn** (day's claim, graph, track) each
  roll back whole — the card keeps its old due date and repetition count, the turn stays unclaimed so
  the learner can answer it again, and neither meter moved — with the committed counterparts pinning
  that all three rows do land together. A third case covers the dialogue's **finished mark** (Room
  v10): when the *final* turn's last write dies, `conversation_done` stays empty, so the gate cannot
  ask the learner to sit through an exchange every turn of which was already credited; the committed
  turn writes the mark in that same transaction.
  `LessonCompletionTransactionTest` does the same for **finishing a lesson** (completion stamp,
  the lesson's words entering the review queue, cursor reset): a failing last write leaves the
  lesson unfinished at its old cursor with no card queued, and `PlacementSeedTransactionTest` for
  the **placement test**, whose answers fold into the skill meters — a crash on the third answer
  leaves no meter seeded at all, where a partial seed used to describe a learner the test never
  measured. Both newer classes inject the crash as a
  failing *last* write inside the use case's own transaction (a decorated repository), so what rolls
  the earlier rows back is the production code, not a transaction opened by the test. Neutering
  `inTransaction` fails all four rollback cases (lesson answer, review, turn, completion), so they
  cannot pass by accident. `TransactionCoverageTest` keeps the rule from decaying: it reads the
  repository interfaces for every non-read `suspend fun`, scans `domain/usecase/` for classes that
  call two or more of them, and fails unless each one either wraps the writes in `inTransaction` or
  is registered as caller-wrapped with a reason (`SubmitExerciseUseCase`, whose transaction the
  lesson session opens around the answer's history). A rename of a registered use case fails too, so
  coverage cannot vanish silently, and the behaviour check invokes each owner through a
  transaction-counting repository to prove it really opens exactly one. Both halves were checked
  against a real regression: removing `inTransaction` from a registered use case and adding an
  unregistered multi-write class each fail the class.
- **Content recovery paths (checklist P1).** The seeding path is covered from both ends.
  `ContentSeederTest` holds the failure modes that happen *around* the bundle write: an asset that
  cannot be read writes nothing and stamps no version; a malformed asset (`[]` where the parser wants
  the bundle object) reaches the caller instead of becoming a silent empty bundle; and a crash
  **between** the committed content transaction and its stamp leaves a complete bundle with the old
  version, so the next run finishes the job by replacing, never by appending. The partial-write case
  belongs to the `@Transaction` on `ContentDao.replaceContent`, which Room enforces, not to a fake.
  The outermost failure — **the database file itself cannot be opened** — is `ContentRecoveryTest`
  (Robolectric): the path is made unusable, `AppContainer.seedContent()` returns `false`, and
  `contentStatus` ends at `FAILED` rather than in `LOADING`, so no screen can wait forever on a
  bundle that will never arrive. The test asserts the bundled assets *are* readable first, so it
  cannot pass because of an asset read instead. The failure is classified
  (`ContentFailure.DATABASE_UNREADABLE`) and a retry is pinned to fail the same way — the app never
  replaces a learner's file by itself. Then the repair is proven end to end: the file is moved into
  `noBackupFilesDir/corrupt-database/` (kept, not deleted), the database path is free, and a *second*
  `AppContainer` — the one the restart builds — seeds to `READY`, which is what makes the rebuild a
  way out rather than a second dead end. Robolectric cannot reproduce a genuinely corrupt SQLite
  *file* — a garbage `english.db` was opened as a fresh database there — so the test uses a path
  SQLite cannot open at all; both end at the same `FAILED`, but only a device can produce the real
  corruption.
  Home's half is `HomeViewModelTest` (three cases): the rebuild is offered only when the reason is
  the database and Home has no lessons to show, a successful move restarts the process, and a move
  that moved nothing leaves the button offered, the app running and `repairFailed` set. One
  `ScreenSmokeTest` case pins the copy and the confirmation: the action appears for
  `DATABASE_UNREADABLE`, tapping it opens «پایگاه داده از نو ساخته شود؟», and «انصراف» restarts nothing.
- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`, 728 tests / 79 classes as of
  the B1/B2-hard-choice pass, where the last 42 flat lessons got the item they were missing: v76's own
  measurement left 20 B1 and 22 B2 lessons whose whole choice population was a gloss at 1, with the
  hard item in a blank, a translation or a production — so recognition could not be steered there.
  Each got one item at 3 that reads the lesson's own **grammar** node in the lesson's own vocabulary,
  four grammatical options where the context decides (`A transfer fee is charged`, `had better greet
  your host`, `must be recycled` against `must recycle`, `has been verified` with `since`, `Were the
  subsidy withdrawn`), and `ContentDistributionTest` now asserts **lesson by lesson** that no choice
  lesson is flat — all 192 of them move — and proves the consequence on `b1.banking` and `b2.privacy`
  (2972 → 3014 exercises, bundle v77); no lesson, word or node moved. Before it the A1/A2-choice pass,
  where the bottom of the scale got the axis v72 left half-built: 41 lessons
  (20 A1, 21 A2) shipped only «What does X mean?» choices at 1, so `AdaptiveExerciseSelector` had no
  direction to move in at the two levels a beginner most needs both. Each got one item at 2 in which
  **the sentence decides the word** — four of the lesson's own words of the same part of speech
  (`The ______ says "meow"`, `I am crying because I am cutting an ______`, `Our ______ trains the team
  twice a week`) — and `ContentDistributionTest` now pins **all 37** of A1's and **all 32** of A2's
  choice lessons and proves the consequence on `a1.animals` (a consolidated meter is served the item
  that says meow, a shaky one the gloss) and `a2.work` (the meeting at ten against the gloss)
  (2931 → 2972 exercises, bundle v76); no lesson, word or node moved. Before it the C2-hard-choice pass,
  where the last level whose recognition could not rise got the item it was missing: v71 left 21 of
  C2's 30 lessons with a hard item only in a blank or a production, and
  `AdaptiveExerciseSelector` raises the level **within a format**, so those 21 could not raise
  recognition however consolidated the meter was. Each got one authored choice item at 3, written in
  the level's own hard-end shape — four grammatical options where the context decides, never "which
  sentence is correct" — and `ContentDistributionTest` now pins **all 30** of C2's choice lessons
  (2910 → 2931 exercises, bundle v75); nothing else about the level moved. The judgement of each new
  item is its explanation: `a number of` against `the number of`, the mixed conditional chosen by
  "today", inversion after `Not until`, `may have been read` against `may have read`, `either … or`
  agreement, `used to` for a state against `would` for a habit, `cannot` as deduction against `must
  not` as prohibition. Before it the graph-label pass, where two `ContentDistributionTest` cases count the curriculum graph's own
  claims. One says a prerequisite has to be introduced **at or before** the node that needs it (both
  in the same lesson is the normal case — C1's idiom node chains onto the phrasal-verb node taught
  beside it — so the check is "not later", never "strictly earlier"; position in
  the level-then-order path, plus the level check a reader looks for): `PrerequisiteEngine` answers
  an unmet prerequisite with a `REMEDIATE` action, so a forward one sends the learner to material a
  level up — exactly what `vocab.common-idioms` and `vocab.common-phrasal-verbs` did by requiring
  the two C1 nodes of `c1.phrasal-idioms`, a whole level after their own B2 lessons. They now follow
  `vocab.film-reviews` and each other, which is the chain the rest of the level uses. The other case
  says a node's `level` is the level of the lesson that introduces it, which caught
  `grammar.passive-with-modals` and `grammar.used-to-and-would` calling themselves B2 while their
  first lesson (`b1.environment`, `b1.eating-habits`) was B1. Before it the two-format-reachability pass, where a content case measures the graph's other half:
  `ContentDistributionTest` now runs every shipped exercise through `KnowledgeEvidence.itemsFor` and
  `AnswerChecker.skillOf`/`formatOf` and fails when a knowledge node can be confirmed in fewer than
  `KnowledgeGates.MIN_FORMATS_TO_SECURE` (2) formats — the bar `isSecured` asks for, so a node under
  it can never leave the plan however well it is known. Seven nodes were under it: five A1
  vocabulary nodes whose only credit was a listening item (the lesson's «What does X mean?» choice
  was tagged `GRAMMAR`, though a Persian-option question is vocabulary by the code's own heuristic)
  and two B2 discourse nodes credited only by a spoken turn (their typed translation carried a
  `VOCABULARY` override while `skillOf` defaults a translation to `WRITING`). Seven tags fixed the
  graph; no exercise, lesson, word or node was added, and `GrammarCoverageTest`'s six-exercise bar
  still holds. Before it the mastery-matrix pass, where the selector's two axes are pinned cell by cell: eight
  `AdaptiveExerciseSelectorTest` cases for weak/consolidated × easy/hard (each end decides the level
  inside its band, and neither end filters the lesson — an all-hard or all-easy lesson keeps its
  authored order), mixed dimensions (the weak band ascends, the consolidated one descends), dimensions
  with no evidence (authored order, no invented direction), and the permutation across every meter
  including the 0.84/0.85 boundary. Before it the difficulty-audit pass, where the authored number got a reading that checks it: twelve
  `DifficultyAuditTest` cases cover the engine and its use case — no verdict from one answer, none
  from eight answers in one sitting (`MIN_ATTEMPTS` 8 over `MIN_DAYS` 2), a harder number suggested
  for an A1 item authored 1 and missed half the time, an easier one for a B1 item authored 3 and
  answered always right, nothing suggested below a C1 floor or for a middling 7-of-10 accuracy, the
  thinking-time median dropping a session break, an exercise that left the bundle skipped rather than
  invented, the per-level fold, and the use case joining `lesson_attempt` to the authored difficulty
  and the lesson level. Nothing is written and nothing is recorded for it — the log already held the
  answer, the verdict and the time. Before it the evidence-boundary pass, where the selector learned the difference between "never assessed" and
  "assessed wrong". Three `AdaptiveExerciseSelectorTest` cases: a never-assessed dimension is not
  promoted over a proven gap (reading mastery alone made production at 0f outrank an observed 0.4f
  recall item), a proven 0f is weak *with evidence* and leads from its easy end, and unseen
  dimensions keep authored order among themselves while a gap leads — with a permutation check
  beside them. Before it the A1/A2-difficulty pass, where the last two levels were read item by item
  and their band of 1..2 made the only question "first encounter or one step past it". One
  `ContentDistributionTest` case measures the within-format axis and names where it exists: every
  level's multiple-choice items disagree about difficulty, and since v77 that is asserted **lesson by
  lesson** — all 37 A1, 32 A2, 30 B1, 32 B2, 31 C1 and 30 C2 choice lessons, so the case fails when any
  single lesson ships its choices at one number, because that is what a seeded lesson looks like and it
  cannot reorder its own format. The same case
  proves the consequence on each, and that they sit in different places: on `b1.work` a consolidated
  recognition meter is served its hardest choice item (`ex.02`, reported speech plus past perfect)
  while a shaky one starts at the easy end; on `b2.problems` a consolidated recall meter is served
  `ex.10` (the passive with `since`) while a shaky one starts at the blank that tests a word — at B2
  the structure blank is the hard end and the word blank the easy end, where the seed had called every
  blank easy — and on `c1.statistics`, whose floor is 2, a consolidated recall meter is served
  `ex.05` (`Neither of the samples was significant`, the agreement trap) while a shaky one starts at
  `ex.04` (the plain plural); and on `c2.literature` a consolidated recognition meter is served
  `ex.08` (the narrator's tone read against the passage's optimism) while a shaky one starts at the
  vocabulary item; on `a1.articles` a consolidated recognition meter is served `ex.01` (`She is a
  university student`, the vowel letter that is not a vowel sound) while a shaky one starts at the
  word-meaning item; and on `a2.city-life` the comparative (`Tehran is bigger than Shiraz`) against
  `ex.02` (`What does "crowded" mean?`). All six reorders are permutations. Before it the hard-end-choice pass, where the
  22 C1 lessons whose hardest item was a blank or a reading question got one authored choice item
  each, so recognition can rise in every one of them. Before it the B2-difficulty pass, where the second level was read item by item
  and its axis turned out to sit in the blanks rather than the choices. Before it the difficulty-in-the-bundle pass, where the authored difficulty is checked against the real content
  instead of a fixture. Before it the
  shared-steering pass, where the "which gap is mine" rule left the two comparators that had
  grown their own copy and became `MissTaxonomy.weakestCategory` / `rank` / `gapSuffix` — called by
  both the planner and the review screen's due list. One case pins that a clean window steers nothing
  (five right answers are not a weakness), and one `ReviewViewModelTest` case closes the tab's half of
  the v65 vocabulary-node promise: a card from `a2.airport` (a lesson that used to own only grammar
  nodes) is in the queue the learner sees, and its grade is credited to the word's own lesson, where
  that node lives. Before it the authored-difficulty pass, where the bundle gained what selection was missing — every exercise
  carries a `difficulty` 1..3, relative to its own lesson's level (A1/A2 ≤ 2, C1/C2 ≥ 2). Three
  `AdaptiveExerciseSelectorTest` cases pin the behaviour: a shaky dimension is served from its easy
  end, a consolidated one from its hard end, and a learner with no evidence keeps the authored order
  whatever the numbers say ("no evidence" is not "weak"). Two `ContentValidationTest` fixtures pin
  the gate — a missing value and one outside 1..3 — and a third case reads the shipped bundle and
  refuses any item whose difficulty its own level forbids. Before it the miss-driven drill pass,
  where that steering reached the review screen's due list: four
  `FocusPlanTest` cases — equally overdue nodes reorder toward the weakest category, the gap is named
  on exactly one row, a node owed longer still comes first, and a profile read that throws leaves the
  plain overdue list standing (no row invented, none dropped). Before it the miss-driven-plan pass,
  where the error taxonomy's report finally steers work: the new
  `MissDrivenPlanTest` carries three cases at the use-case level — a vocabulary gap reaches
  `GetTodayPlanUseCase` through `GetMissProfileUseCase` and puts the vocabulary node first with
  «ضعف اخیر: واژگان» on it, an empty log leaves the graph's plan exactly as written, and a profile
  read that throws leaves that same plan standing instead of blanking Home — while
  `AdaptiveLearningPlannerTest` gained four: the reorder among equal priorities, the gap named on
  exactly one action, a clean window (five right answers) steering nothing, and a due review block
  still leading. Before it the motion pass, where the transitions stopped being ten per-route copies of one lambda and the page
  reveal stopped being one timer per card: `isSiblingMove` (the four bottom-bar tabs cross-fade
  instead of sliding) and `PageReveal`'s window arithmetic are pure, and the new `AppMotionTest`
  carries eight cases. Four for the moves: two tabs are siblings, leaving a tab for anything deeper
  is not, coming back from a detail to a tab keeps its direction, and the first screen has no previous
  route so it is never a sibling. Four for the reveal: a settled page is opaque and in place, the
  first frame is invisible, a later slot never overtakes an earlier one mid-clock (the ordering the
  timer-per-card version could lose when a card appeared above another), and a page with more cards
  than slots still settles inside `REVEAL_TOTAL_MS`; the tab rule reaches `AppNavGraph` through
  the `NavHost`'s own transitions, which no unit test can run — only the decision is tested, the
  pixels are a device check. Before it the card-attribution pass, where a review card stopped being credited to its lesson's unrelated
  nodes: `KnowledgeEvidence.itemsForCard` is type-only — no by-skill pass, no whole-lesson fallback —
  which is what the 35 lessons shipping words without a VOCABULARY node needed, since their grammar
  nodes list `VOCABULARY` in `skills` — until v65 shipped those nodes, and `ContentDistributionTest`
  now fails when a lesson ships words and no node. Four cases then closed the attribution
  block: one in `KnowledgeEngineTest` pins that a lesson owing **two** vocabulary nodes is
  credited twice (the documented over-credit in `c1.phrasal-idioms`, `c2.nuance` and
  `c2.collocation`), two in `EvidenceTransactionTest` on real Room pin the grades —
  GOOD, EASY and HARD are three hits on the node, AGAIN is a miss that still reschedules
  the card — and one in `FocusPlanTest` pins the review list's own shape: the row count is
  the screen's three, drawn from the eight most overdue nodes, not the probe budget.
  Three `KnowledgeEngineTest` cases pin the rule (only same-type nodes, never the fallback, the
  by-skill node refused while `itemsFor` still reaches it), one
  `EvidenceTransactionTest` case proves it on real Room (the grammar node stays unwritten while the
  card, the log and the meter still land), `LearningPathJourneyTest` asserts the recall landed on the
  lesson's vocabulary node, and `FocusPlanTest` gained the missing due-node case — a due node with no
  exercise about it is skipped rather than offered as a row that opens a whole lesson — after (the
  miss-taxonomy card pass: three `MissTaxonomyTest` cases for the card labels, hits and the
  dropped grade, plus three `ProgressViewModelTest` cases for the pooled window), after
  the review→graph pass, which added three cases to `EvidenceTransactionTest` — a committed review
  writes the reviewed word's node inside the card's transaction, the node records no exercise format
  (a card is recall, not transfer), and a crash *after* the node write rolls the node back together
  with the card, the log and the meter; a fourth pins the null-lesson path as "graded, nothing to
  attribute". `ReviewViewModelTest` proves the wiring: the grade carries the word's own lesson, and
  `TransactionCoverageTest`'s invocation now passes the graph through the same use case. The same
  pass then closed the other direction, the due node: `FocusPlanTest` gained four cases — a due node in
  the middle band is drilled instead of refused, a secured node is refused even with a past date, the
  due list keeps only nodes whose drill really exists (most overdue first), and a graph inside its
  intervals yields no rows — while `ReviewViewModelTest` pins that a due node is offered *beside* the
  cards (the queue is untouched and the row carries the drill's lesson) and one `ScreenSmokeTest` case
  renders the "مرور هدفمند" row and proves its CTA opens `(lessonId, itemId)`. Three more then pin the
  freshness around it: a resumed summary re-reads both queues, a resume mid-session never resets the
  card the learner is on or grafts the graph half onto a running queue, and the finished session
  returns to the due list (`ShowDue`) instead of stranding it. The next pass then started the
  roadmap's error taxonomy: `MissTaxonomyTest` (new class, nine cases) pins every skill's category
  (writing/speaking are production, not grammar), the per-category counts and the miss-count rule
  for "weakest", a category with no answers staying absent, content the bundle no longer ships being
  skipped, and the five-answer evidence bar. The card half then closed the gap the first pass left:
  a card speaks only for vocabulary or grammar, GOOD/EASY are hits while HARD/AGAIN are not, and a
  card whose item left the queue is dropped instead of guessed — plus three `ProgressViewModelTest`
  cases proving the mix reaches the screen state, that cards alone move it, and that lessons and
  cards are one pooled window (a review-only week no longer freezes the profile). Before it
  the splash-hand-off pass, whose new class `SplashHandOffTest` pins the API 34 boundary of the
  splash → Home fade: below 34 the finishing splash declares it with the deprecated call, from 34 the
  entered activity declares it with `overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, …)`, and no
  screen test can see which side wins — only the decision, which is pure; a third case pins the
  guard against the constant itself (`NEW_API_LEVEL - 1` vs `NEW_API_LEVEL`), so moving the boundary
  cannot leave the two in disagreement. The transition's pixels stay a device check. Before it the database-rebuild pass — five cases, in the classes that already owned the paths: four in
  `HomeViewModelTest` (offered only for the database failure, restart on success, honest failure when
  nothing moved, and a relaunch that cannot be queued leaving a manual-restart message rather than a
  dead button) and one in `ScreenSmokeTest` (the action, its confirmation and the dismiss that does
  nothing); `ContentRecoveryTest`'s existing case grew the classification, the working repair and
  a retry pressed four times that never leaves the failure state.
  Before it the content-recovery pass, where `ContentRecoveryTest` was the new class and
  `ContentSeederTest` gained three failure cases; before that the conversation-mark pass added one case to
  `EvidenceTransactionTest` (on real Room: a crash on a dialogue's final turn leaves the finished
  mark unwritten with the claim, while the committed turn writes both) and taught
  `DatabaseUpgradeTest` to expect the v10 table to arrive empty on an upgraded install —
  before it the content-status pass, whose cases pin that a failed bundle write ends the lesson's, the placement
  test's and the conversation's wait in `contentFailed`, that each retry re-runs the seed, and that a
  dialogue read which throws becomes `loadFailed` with a retry (no new class) —
  then the B-4 reminder pass, whose new class is `DailyReminderTest` (the alarm lands on the chosen hour
  and at the top of it, never in the past and never more than a day out; 19 stays the default; an
  out-of-range hour is clamped instead of being rolled into another day by `Calendar`; and the
  notification's copy follows the data it can see — the real due count through the app's bidi number
  rule, «امروز مروری نداری» at zero, and a claim-free generic line when the count cannot be read)
  plus one case
  in `SettingsViewModelTest` (the reminder hour is persisted and rendered, 19 until changed) — and
  before it the P1-4 fairness pass (US/UK spellings graded equal on both typed and spoken answers),
  then the atomic-evidence pass (above), after the Phase-3 content pass, whose eight new cases live in
  `ContentValidationTest` (no new class: one
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
  (v15 N-4: the `EXTERNAL_COMPANION_OFFER` flag is declared by all three flavors, `googlePlay`
  declares it `false` while `bazaar`/`myket` keep `true`, every offer surface and
  `CompanionApp.openDownloadPage` read it, and `docs/release.md` names it — so the Play build
  cannot link to Bazaar even if a surface forgets the flag, and the split cannot drift) + `DocCodeConsistencyTest`
  (v15 B-12: `docs/debt.md` states the production overlap floor the code actually grades with;
  the doc had drifted to "60%" while the constant said 0.5 — plus, since v65, the Room schema
  number quoted in README and the bundle counts quoted in README *and* both listing languages,
  all computed from the shipped files, because those three facts had drifted to v8 and 2,828;
  and, since the `transactions` job landed, the job list is checked in both directions: every id
  under `jobs:` in `.github/workflows/ci.yml` must appear backticked in AGENTS.md, README and
  `docs/release.md`, and every job name those docs bold must still exist there — so neither a new
  job nor a deleted one can ship while three documents describe a different pipeline; those docs and
  `ci.yml` are inputs of `:app:docGuard`, the filtered run of this one class) +
  `SubscriptionRecoveryTest` (renewal
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
  exactly that exercise, a real reassess bar, stated attempts; review→graph, that an explicitly
  named due node is drilled even in the middle band while a secured one is refused, and that
  `GetDueKnowledgeReviewsUseCase` keeps only due, unsecured, drillable nodes in due order),
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
  partner's question is not an answer, and the speech grammar lists every accepted phrase plus
  `[unk]` — plus P1-4: a take in the other spelling of English (`my favorite color is gray` for an
  authored `My favourite colour is grey.`) is still CORRECT, while the grammar keeps the authored
  spelling so what the learner is asked to say does not change),
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
  study time follows interactions, so an idle lesson stops crediting — P7; a lesson read that
  throws ends in the screen's own error state with a retry that really loads it, a failed
  dimension fold still renders the learner's verdict — P1-7, and a bundle write that failed ends
  the wait in `contentFailed` — not the spinner under `activeStage == null` — with a retry that
  re-runs the seed and then loads),
  `MissTaxonomyTest` (the classification: one category per authored skill, the mix folded from the
  pooled lesson answers and review cards, GOOD/EASY the only hits, a card limited to vocabulary or
  grammar, a grade whose item left the queue dropped, stale content skipped, and the weakest category
  chosen by miss count) and three `ProgressViewModelTest` cases that carry the mix into the screen
  state, move it with cards alone, and prove the two sources share one window,
  `ReviewViewModelTest` (GOOD records the schedule and finishes; AGAIN re-queues the item in the
  same session; a due curriculum node appears beside the cards and carries its drill's lesson;
  a resumed summary re-reads both queues while a resume mid-session leaves the session and its
  revealed card alone; the finished session returns to the due list; study ticks credit only while
  the learner is recently active and never while the app is backgrounded), `PaywallViewModelTest` (priced/unpriced plans, unreachable store, purchase
  cancelled/failed/succeeded, a checkout that throws, restore active/none/store-failure, the
  companion grant, and the storefront reaching the state so the wording can match the market),
  `ConversationGateTest` (the lesson gate: a lesson still in progress owes nothing; the first
  completed lesson without its dialogue blocks the next one; finishing the conversation opens it;
  the oldest owed conversation wins; a lesson with no authored dialogue never gates),
  `ConversationPracticeViewModelTest` (turns wait for the bundle write instead of reading an empty
  table, a failed write is `contentFailed` rather than the empty state and its retry re-seeds, and a
  read that throws lands in `loadFailed` with a retry that loads; a session runs ONE lesson's dialogue rather than a pool of every lesson, the requested
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
  alone finish and record the conversation, and retry by voice clears the typed verdict), `SettingsViewModelTest` (each switch persists, reset clears progress *and* knowledge state *and*
  the finished dialogues the preferences file holds *and* the recorded take — the two halves a Room
  wipe could not reach —, a settings write never wipes the access status), `ProgressViewModelTest` (the three tracks reach the
  state and an empty bundle yields no tracks rather than empty bars), `PlacementViewModelTest` (adaptive stop after
  an unwinnable band, the band-based level, C2 on a perfect run, mastery calibration, the
  onboarding write, a read that throws landing in the empty state with a retry that loads it, and
  a failed bundle write becoming `contentFailed` instead of an empty state that blames a read it
  never ran, with a retry that re-runs the seed), `VocabularyViewModelTest` (all words load; the query matches the English word
  case-insensitively and the Persian meaning; a blank query clears the filter; no matches is an
  empty list, not an error) and  `OnboardingViewModelTest` (skipping the test pins A1 and finishes
  onboarding), `LoadFailureTest` (v63's failure surfaces: the `catchLoadFailure` operator reports a
  throwing flow and still lets a `CancellationException` through, and a repository that throws on
  read leaves Progress, Vocabulary, Review and Settings in `failed` — not `loading`, and not an
  empty collection the screen would present as "nothing here yet" — with `retry()` re-running the
  same read and clearing it; Settings also asserts it has no `settings` to show, because default
  switches rendered as stored values is a wrong answer stated as fact. Home's own case, the failed
  bundle write and its re-seed, is in `HomeViewModelTest` because it needs the container's seed
  hook; the lesson and the placement test do the same through an injected status and seed hook,
  because those two used to wait on a boolean a failed write never flipped) — the billing double is `FakeBillingGateway` in the same file. They need
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
- `.github/workflows/ci.yml` runs three jobs on every push and pull request — `verify` (content
  validation + unit tests + the B-9 grep), `transactions` (the atomic-evidence classes:
  `AnswerTransactionTest`, `EvidenceTransactionTest`, `LessonCompletionTransactionTest`,
  `PlacementSeedTransactionTest`, `TransactionCoverageTest`) and `lint` — plus `assemble` (the three
  flavor APKs) only on a schedule, a manual dispatch or a tag, and only after `transactions`.
- Content tests read the assets through `File`, so Gradle cannot see them as inputs — after a
  content edit run them with `--rerun` or they silently report UP-TO-DATE. The file-reading doc
  guard does not share that rule: `./gradlew :app:docGuard` runs only `DocCodeConsistencyTest`
  (seconds) with `AGENTS.md`, `README.md`, `docs/` and `.github/workflows/ci.yml` as its inputs, and
  `check` depends on it while every `test*UnitTest` run finalizes with it — the wiring is
  discovered, not named, so a new flavor is covered without touching the build script. Three cases in
  the same class read `app/build.gradle.kts` and pin that wiring — the filter names this class, the
  four inputs are declared, an empty classpath throws instead of running nothing, and no variant is
  hard-coded — so the fast path cannot report green after it stopped checking anything.
- There is **no instrumented suite**: the Android Studio template was removed rather than kept
  as a false signal (it asserted nothing and ran in no CI). UI changes get their baseline from the
  Robolectric screen smoke tests above; beyond that, rely on manual device verification, as below.
- **The launch path does run on Android automatically — the `device` CI job** (schedule, manual
  dispatch or tag; see [`release.md`](release.md)). It boots one emulator and runs
  `scripts/verify_device.sh bazaar --require-device`, so what is checked on a real Android runtime is:
  the APK installs, the app starts with airplane mode on, the splash hands off to `MainActivity`, the
  seeder logs its completion line, nothing crashes, and a second cold start does not regress. That is
  a smoke, not coverage: no screen is asserted, and the recogniser, the upgrade path from an installed
  older build and the splash transition still need the manual passes in this file. `--require-device`
  matters — without it the script's no-op-on-a-laptop rule would turn a dead emulator into a green
  job, which is the kind of false signal the instrumented template was deleted for.
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

## Manual device pass — schema v10, restore and the dialogue mark

This is the half of the P0 task that a JVM test cannot reach: `DatabaseUpgradeTest` runs the chain on
real SQLite and `RoomMigrationTest` checks it against the exported schemas, but only an install that
already holds data exercises Room's own open path, and only a real backup round-trip proves what
travels between devices.

1. **Upgrade with real data.** Install the previous build (schema v9), finish a lesson and speak its
   dialogue through to the recap, then install the new build over it (same signing key — `adb install
   -r`, never `adb uninstall`). Home must not offer «گفتگوی این درس را تمرین کن» for that dialogue and
   the next lesson must already be open: the old preference set was imported into the
   `conversation_done` table on the first read. Then confirm the old key is gone rather than merely
   copied — `adb shell run-as org.token.english strings
   files/datastore/app_settings.preferences_pb | grep conversations_done` prints nothing.
2. **A wipe cannot be undone by the import.** Settings → «پاک‌سازی پیشرفت» on a *fresh* install that
   has not opened a lesson yet (so the legacy key was never imported), then open the lesson screen:
   nothing is owed and no old dialogue comes back. `reset()` clears the legacy key with the table.
3. **Backup and restore.** With Auto Backup enabled, `adb shell bmgr backupnow org.token.english`
   (or use device-to-device transfer to a second phone), then restore on the clean device. The
   finished dialogue must stay finished and the completed lesson must still be completed — that is
   the whole point of the move. The subscription must **not** travel: the paywall shows the same
   state as before the restore and no entitlement is granted. If a restored install reads as pro,
   the `datastore/` exclusion (`data_extraction_rules.xml`, `backup_rules.xml`) has been loosened and
   a restore has become a free subscription.

## Manual device pass — rebuilding an unreadable database

The JVM suite skips the one thing that cannot be faked: a genuinely corrupt SQLite **file** (Robolectric
opens garbage as a fresh database). Reproduce it on a debug install and check the whole offer:

1. `adb shell run-as org.token.english rm -f databases/english.db databases/english.db-wal
   databases/english.db-shm`, then create a *directory* where the file should be
   (`mkdir databases/english.db`), and force-stop and reopen the app. Home must show «محتوای درس‌ها
   آماده نشد» with «تلاش دوباره», **«ساخت دوبارهٔ پایگاه داده»** and «ارتباط با پشتیبانی» — the third
   action appears for no other failure, so a bundle-only error must not show it.
2. Tap «ساخت دوبارهٔ پایگاه داده»: a confirmation names what is lost (completed lessons, reviews,
   streak) and says the broken file is kept. «انصراف» leaves the app running and repairs nothing.
3. Confirm: the app should return by itself (launcher intent, then its own process exits) and land on
   a working Home with the bundle re-seeded. Then `adb shell run-as org.token.english ls
   noBackupFilesDir/corrupt-database` still holds the old `english.db.<stamp>` — and
   `adb shell bmgr backupnow org.token.english` must not fail its 25 MB quota because of it.
