# AGENTS.md — Lingo English Learning App (org.token.english)

> این سند «حقیقت مرجع» و نقطهٔ ورود است. هستهٔ یادگیری **آفلاین-محور** است و باید همیشه بدون
> اینترنت کار کند؛ فقط چیزهایی که به **زیرساخت بک‌اند/سرور** نیاز دارند معلق می‌مانند.

## Where the detail lives

The root file is deliberately short; the deep material is in `docs/`:

- [`docs/learner-model.md`](docs/learner-model.md) — engines, the adaptive layer, the remediation
  bar, the three skill tracks.
- [`docs/content-pipeline.md`](docs/content-pipeline.md) — the JSON bundle, `validateContent`,
  every authoring rule; [`docs/CHANGELOG-content.md`](docs/CHANGELOG-content.md) — version history;
  [`docs/curriculum.md`](docs/curriculum.md) — the 180-topic syllabus (30 per level) and which
  topics ship today.
- [`docs/billing.md`](docs/billing.md) — subscription × 3 marketplaces, the companion grant, storefront
  wording; [`docs/connectivity.md`](docs/connectivity.md) — the offline rule and permission tables.
- [`docs/design-direction.md`](docs/design-direction.md) — the Xiaomi-inspired design system (tokens,
  shapes, the soft shadow, the entrance motion) and the sources behind it.
- [`docs/release.md`](docs/release.md) — release ritual and toolchain facts;
  [`docs/testing.md`](docs/testing.md) — the test inventory; [`docs/debt.md`](docs/debt.md) — debt.

## Always / Ask first / Never

- **Always:** run the fast gates before calling work done (below); keep the core working offline;
  route English through `EnglishText`/`AnswerField`; add a test with engine logic changes; bump
  `contentVersion` in all five JSON files together on a content change.
- **Ask first:** anything on the *suspended* list, a new network feature/permission, a schema
  migration, or a product decision (gating, pricing).
- **Never:** commit secrets (`keystore.properties`, `*.jks`); hardcode design tokens; put raw English
  in the RTL layout; `runCatching { }` around a suspend call; apply `org.jetbrains.kotlin.android`;
  re-run a `scripts/archive/` batch; add a backend-dependent feature unconfirmed.

## 1. What this project is

An offline-first Android app for Persian speakers learning English (CEFR A1–C2): daily lessons →
exercises → the lesson’s own conversation (which is what opens the next lesson) → spaced-repetition
review → progress. Specs in `files/` are **design intent, not proof**.

## 2. Build & verify

**The everyday loop is tests, not APKs**, and the learning core must work offline:

```bash
./gradlew :app:testBazaarDebugUnitTest   # unit tests + typechecks main sources (the fast gate)
./gradlew validateContent --rerun        # content gate (also runs in preBuild)
./gradlew :app:lintBazaarDebug           # lint (also a CI job)
node scripts/lint_budget.mjs             # the accepted warnings, after a lint run (also in CI)
scripts/install_debug.sh                 # builds+installs only when a device is attached (else no-op)
```

A lint warning is never "just a warning" and never added to `scripts/lint_budget.mjs` to get green:
each accepted one carries its reason there, and the budget fails when a rule appears, grows, or goes
away without the list shrinking (`docs/debt.md` has the decisions).

Release points, the fail-closed gates, the toolchain facts and the CI shape are in
[`docs/release.md`](docs/release.md); the Definition of Done is §9 below. In short, CI runs three
jobs on every push and pull request — **`verify`** (content validation + unit tests + the B-9
`runCatching` grep), **`transactions`** (the atomic-evidence classes, filtered out of the same suite)
and **`lint`** — plus **`assemble`** (the three flavor APKs) and **`device`** (one emulator boot
running `scripts/verify_device.sh bazaar --require-device`: install, airplane mode on, two timed cold
starts, seed line, crash sweep) only on a schedule, a manual dispatch or a tag, after `transactions`.
The `device` job is a launch smoke, not an instrumented suite: it proves the app starts and seeds on
Android, and `--require-device` makes a dead emulator a red job rather than a green no-op.

## 3. Architecture (single module, layered packages)

One Gradle module (`:app`); package boundaries mirror the future multi-module split. Product
flavors (`store` dimension): `bazaar`, `myket`, `googlePlay` — each packages only its own
marketplace billing SDK ([`docs/billing.md`](docs/billing.md)).

```text
org.token.english
├── SplashActivity / MainActivity / EnglishApp   # launch, Compose host, Application
├── navigation/                                  # Routes + NavHost + bottom bar (4 tabs)
├── core/{designsystem,billing,audio,common}     # tokens+components, billing seam, TTS, TimeUtil
├── domain/{model,repository,engine,usecase}/    # PURE KOTLIN — no Android, no Room, no Compose
├── data/{local,content,repository}/             # Room, ContentParser+Seeder, repo impls
├── di/                                          # Manual DI: AppContainer + ViewModelFactories
├── feature/<screen>/                            # Screen + ViewModel (+ UiState/Event) per feature
└── src/{bazaar,myket,googlePlay}/billing/PlatformBillingGateway.kt   # ONE per flavor
```

**Dependency rule (enforced by review, not tooling yet):**

```text
feature → domain (models/usecases) + core/designsystem + core/common + di (factory fn only)
domain  → nothing Android (pure Kotlin only)
data    → domain (implements interfaces) + Room/DataStore
di      → everything (the only place that wires them together)
```

- UI state pattern: `UiState (StateFlow) ← Event ← ViewModel ← UseCase/Repository`. ViewModels never
  touch Room/DataStore directly; composables never contain business logic.
- **DI is manual** (`AppContainer`); Hilt was deliberately skipped under AGP 9 built-in Kotlin.
- **A ViewModel takes only what it uses (B-6), and `feature/` never imports `di/`.** Its constructor
  lists exactly the repositories, use cases, seams and ticks it needs — no `AppContainer`, no service
  locator; `di/ViewModelFactories.kt` is the only `di/` symbol `feature/` may import (`grep
  AppContainer …/feature/` must stay empty); non-zero defaults exist only for ticks.
- Engines are pure and swappable and the adaptive layer is wired to the UI — the full model
  (planner budget, focused sessions, the unified remediation bar, the three skill tracks, the
eight-step guided lesson) is in
  [`docs/learner-model.md`](docs/learner-model.md). The UI never learns how mastery is computed.

## 4. Connectivity — offline-first

The learning core runs from local data and **must keep working with no network**; online features are
allowed, only backend-dependent ones are held back. The offline rule, per-flavor permissions and the
**active / suspended / unblocked** lists are in [`docs/connectivity.md`](docs/connectivity.md). Do
**not** implement anything suspended (AI conversation, sync/auth/backup, content packages, analytics
upload) without confirming first. Billing is the only online surface ([`docs/billing.md`](docs/billing.md)).

## 5. Content pipeline

Content is **JSON only** under `app/src/main/assets/content/` — new lessons/words/exercises need no
Kotlin; `validateContent` is the gate. Rules live in [`docs/content-pipeline.md`](docs/content-pipeline.md),
version history in [`docs/CHANGELOG-content.md`](docs/CHANGELOG-content.md). After a content edit:
`node scripts/balance_answer_positions.mjs`, then `validateContent` + content tests with `--rerun`.

## 6. Design system rules (from design.md)

- **Never hardcode colors/spacings/radii** — use `MaterialTheme.*`, `LocalAppExtendedColors`,
  `AppSpacing`, `MaterialTheme.shapes`. Root layout is **forced RTL**: all English goes through
  `EnglishText`/`AnswerField` (which re-assert LTR), and feedback is never color-only — always
  icon + text (`CorrectBanner`/`IncorrectBanner`).
- Touch targets ≥ 48dp, primary buttons 48dp high, cards **22dp** radius with the soft shadow and
  pill buttons (see [`docs/design-direction.md`](docs/design-direction.md)), and **cards always span the
  available width** (`AppCard` fills it itself). Streak must never outweigh learning progress.
- **Motion comes from `AppMotion`** (`core/designsystem/Motion.kt`) — a route change, an in-screen step
  and the page reveal read their durations there, never a literal in a feature. The graph declares
  its transitions once on the `NavHost`, so both ends of a move are known: a push/pop slides in the
  direction of travel, a move between two bottom-bar tabs only cross-fades. Inside a screen the next
  step uses `stepChange`/`StepChange` keyed on what changed, and a page's cards share one
  `rememberPageReveal()` clock (`component/Reveal.kt`).
- Every feature screen needs empty/loading/error states; a local failure must never be blamed on the
  network — only a genuinely online action may show a connectivity error.

## 7. Security posture

- `INTERNET` is billing-only today; a new online feature may add permissions, but each must be
documented in [`docs/connectivity.md`](docs/connectivity.md). No secrets in the repo — signing
config and store keys live in untracked `keystore.properties`. Data is non-sensitive learning
progress, unencrypted in app-private storage. If sync ever ships: Network Security Config, a
pinning decision and an ADR first.

## 8. Testing

The fast gate is `./gradlew :app:testBazaarDebugUnitTest` (688 tests / 78 classes as of the
vocabulary-node pass, where the 35 lessons that shipped words with no VOCABULARY node got one each —
titled by their own lesson, chained onto the vocabulary node of the lesson before them in their level
(and a level's first lesson onto the level below) — so a card from those lessons finally moves the
node it is evidence about instead of grading into nothing, with `ContentDistributionTest` failing the
build when a lesson ships words and no node. Three cases then closed the attribution block: one in
`KnowledgeEngineTest` for a lesson that owns **two** vocabulary nodes (credited twice — the bounded
over-credit `docs/debt.md` accepts) and two in `EvidenceTransactionTest` on real Room for the grades
(GOOD, EASY and HARD are three hits on the node; AGAIN is a miss that still reschedules the card).
Before it the miss-driven-plan pass, where the error
taxonomy stopped being a report and started steering the day:
`GetTodayPlanUseCase` reads the mix through `GetMissProfileUseCase` and `AdaptiveLearningPlanner`
orders the plan *within one priority* by the weakest category and names the gap on the action that
answers it — never inventing, dropping or re-prioritising one, steering nothing when the window holds
no miss, and degrading to the unsteered plan when the profile cannot be read. Four cases in
`AdaptiveLearningPlannerTest` (the reorder, the one named action, the clean window, review still
leading) and three in the new `MissDrivenPlanTest` (the mix reaches the plan, an empty log changes
nothing, a failed read keeps the graph's plan), with the category's Persian name moved to
`MissTaxonomy.labelFa` so Progress and the plan cannot call one gap two things. Before it the
card-attribution pass, where a review card is evidence only for its lesson's node of its **own**
knowledge type (`KnowledgeEvidence.itemsForCard`): no by-skill pass and no whole-lesson fallback,
because the 35 lessons that shipped words with no VOCABULARY node had grammar nodes that *list*
`VOCABULARY` in `skills`, so both broader rules credited exactly the wrong nodes — reviewing «airport»
raised `grammar.future-will` while the recalled word had no node at all. Three cases in
`KnowledgeEngineTest` (type-only, no fallback, the by-skill node refused while the exercise rule still
reaches it), one in `EvidenceTransactionTest` on real Room (the grammar node is untouched, the card
and its meter are not), an assertion in `LearningPathJourneyTest` that the card's recall lands on its
vocabulary node, and one in `FocusPlanTest` for a due node with no exercise about it; the counts
those four documents quote are now guarded by a case that counts the annotations. Before it the
miss-taxonomy card pass, where the taxonomy stopped being lesson-only: a review card is classified by
its item's content type (vocabulary or grammar — the only two things a recall card can train), GOOD
and EASY are the hits via one `ReviewResult.isSuccess`, a grade whose item left the queue is dropped
rather than guessed, and both sources are pooled and trimmed to one window so a review-only week
keeps the mix moving — three cases in `MissTaxonomyTest` and three in `ProgressViewModelTest`
(cards reach the state, cards alone move it, lessons and cards are one window). `TransactionCoverageTest`
also had to learn that a `recent*` repository method is a read, not a write. Before it the
review→graph pass, where a review grade is finally folded into the reviewed word's curriculum node
inside the card's own transaction — with **no** exercise format, because a card is recall and not the
*transfer* claim the format mask means; `EvidenceTransactionTest` proves the rollback of the node write
and `ReviewViewModelTest` the wiring. The same pass closed the other direction: a due node's own
exercises are now served, listed on the review screen through `GetDueKnowledgeReviewsUseCase` and
opened as the focused drill — four cases in `FocusPlanTest` (due middle band drilled, secured node
refused, the list's filters and order, a graph still inside its intervals), one in
`ReviewViewModelTest` (the node sits beside the cards) and one in `ScreenSmokeTest` (the row and its
CTA). The same pass then made that work stay current: the review summary re-reads both queues on
resume (never mid-session — three `ReviewViewModelTest` cases pin the re-read, the untouched session
and `ShowDue` returning the finished screen to the due list) and the finished screen offers that
return only when nodes are actually owed. Before it, the error-taxonomy pass started the roadmap's
next item: the new `MissTaxonomy` engine classifies each recent lesson answer by its exercise's
skill (vocabulary, grammar, comprehension, production) and `GetMissProfileUseCase` folds the window
into a mix Progress renders — six cases in `MissTaxonomyTest` (the skill mapping, the counts, the
miss-count "weakest" rule, absent categories, stale content, the five-answer bar) and one in
`ProgressViewModelTest` (the mix reaches the state). Before it the splash-hand-off pass, whose new class
`SplashHandOffTest` pins the API 34 boundary of the fade — below
34 the finishing splash declares it, from 34 the entered activity does, and declaring both would
fight over one transition; the pixels are still a device check (`docs/debt.md`). Before it the
database-rebuild pass, where a seed failure is classified — `ContentFailure` — and Home offers the
rebuild only for the one case a retry cannot fix: four cases in `HomeViewModelTest` (offered only
for the database, restart after a move, an honest failure when nothing moved, and a relaunch that
cannot be queued leaving a manual-restart message instead of a dead button) and one in
`ScreenSmokeTest` (the action, its confirmation, and the dismiss that does nothing). Before it the
content-recovery pass, where `ContentSeederTest` covers the failures around the bundle write — an
unreadable asset, a malformed one, a crash between the content transaction and its stamp — and
`ContentRecoveryTest` proves that a database file which cannot be opened ends the seed in `FAILED`
rather than a crash or a `LOADING` no screen can leave, that the failure is `DATABASE_UNREADABLE`,
and that the repair moves the file aside so the next container seeds; before it the
conversation-mark pass, where the finished-dialogue set leaves the backup-excluded preferences file
for Room (`conversation_done`, schema v9→v10) and is written inside the final turn's own
transaction — a case in `EvidenceTransactionTest` proves a crash there leaves the dialogue unmarked,
and `DatabaseUpgradeTest`/`RoomMigrationTest` cover the step and its one-time import of the old
key; before it the
content-status pass, where a failed bundle write stops being an endless spinner or a false empty
state on the lesson, the placement test and conversation practice and becomes a named, re-seeding
retry; before it the
atomic-evidence pass: `AnswerTransactionTest` proves on real Room that a crash inside a lesson
answer rolls back the claim, the graph, the meter and the history together, `EvidenceTransactionTest`
does the same for a review grade and a conversation turn, `LessonCompletionTransactionTest` for
finishing a lesson (stamp, queued words, cursor), `PlacementSeedTransactionTest` for the placement
calibration, `TransactionCoverageTest` fails when a use case writes several tables without
`inTransaction`, and two cases in `LessonViewModelTest` pin
the wiring; before it, the Phase-3 content pass had added eight cases to
`ContentValidationTest` — one fixture per new
semantic rule in `ContentValidator`, plus a guard that a hyphen is never a finding; the Phase-2
persistence pass had added — `EvidenceRuleTest` pins that one answer per exercise per day is evidence,
`LessonMasteryTest` that finished steps are not mastery, and `LearningPersistenceTest` that a
reopened step resumes on the first *unanswered* question, that every graded answer is appended with
its step, and that a finished step's completion time is recorded once and survives a restart;
`LoadFailureTest` pins that a failed
local read becomes a state the screen can show and retry rather than a spinner nobody can leave (and
two cases in `LessonViewModelTest` and one in `PlacementViewModelTest` extend that to the lesson
and the placement test, the two screens whose loads had no guard at all, plus a dimension write
that fails without eating the verdict) —
one active stage at a time across the eight-step lesson, no trial on a fresh
install, the per-flavor «ارتباط با ناشر» store page, the
per-lesson dialogue with its scene and roles, the conversation gate that keeps the next lesson shut
until that dialogue is spoken, and the Vosk verdict engine, whose recogniser half is device-only by
nature); the full inventory and ViewModel/fake conventions are in
[`docs/testing.md`](docs/testing.md). Content tests read assets through `File`, so run them with
`--rerun`. There is no instrumented suite (the template was removed, not kept as a false signal).
`DocCodeConsistencyTest` also reads files: it fails when a `ci.yml` job is not named in this file,
`README.md` and `docs/release.md`, and when a job name those docs bold has already been deleted from
`jobs:` — a CI change cannot ship undocumented in either direction. Those docs and `ci.yml` are
inputs of `:app:docGuard`, a filtered run of that one class on the unit-test classpath: a docs edit
re-runs seconds instead of the whole suite, and `check` plus every `test*UnitTest` run the guard, so
no `--rerun` is needed for a doc change. Three cases in that same class pin the guard's own wiring —
the class it filters on, the four files it watches, the loud failure when no `test*UnitTest` exists,
and the variant-agnostic attachment — so a guard that would run nothing fails instead of reporting
green. The content assets are not declared inputs and
keep their `--rerun` rule.

## 9. Conventions & Definition of Done

1. Persian UI strings live inline in composables (single-locale MVP; extract to `strings.xml` when
   a second locale lands).
2. Epoch millis (`Long`) for time — `java.time` needs desugaring at minSdk 24.
3. A feature is done when: unit tests pass, `validateContent` passes if content changed, empty/
   loading/error states exist, RTL/LTR verified, no hardcoded tokens, no undocumented permissions,
   the core still works offline, and docs are updated if the architecture changed. Flavor builds are
   checked at release points / `scripts/install_debug.sh` — **not** after every change.
4. **Never `runCatching { }` around a suspend call** (B-9): it swallows `CancellationException`. Use
   `runCatchingCancellable` (`core/common/SafeCatching.kt`). CI greps `feature/` and `domain/` for a
   bare `runCatching {`; a genuinely non-suspend use must say so on the same line with
   `runCatching-ok: <reason>`.
5. **Build-script task actions must not read script-level `val`s** — a top-level `val` in
   `*.gradle.kts` is a script-class member and cannot be serialized by the configuration cache. Copy
   it into a local inside `configureEach { }` first (see the release gates in `app/build.gradle.kts`).

## 10. Known design debt

Accepted trade-offs (session-scoped mastery, the companion key pinned from a sideloaded build, the
removed trial's legacy preference keys, Room migration rules, and more) live in
[`docs/debt.md`](docs/debt.md). Read it first.
