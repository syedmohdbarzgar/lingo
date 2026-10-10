# AGENTS.md — Freebuff English Learning App (org.token.english)

> این سند «حقیقت مرجع» برای هر انسان یا ایجنتی است که روی این پروژه کار می‌کند.
> هستهٔ یادگیری **آفلاین-محور** است: بدون سرور و بدون بک‌اند کامل کار می‌کند و باید همیشه بدون
> اینترنت هم کار کند. قابلیت‌های آنلاین مجازند؛ فقط چیزهایی که به **زیرساخت بک‌اند/سرور**
> نیاز دارند معلق می‌مانند (جدول پایین).

---

## 1. What this project is

An offline-first Android app for Persian speakers learning English (CEFR A1–C2):
daily lessons → exercises → spaced-repetition review → progress.

Authoritative specs live in `files/`:

| File | Defines |
|---|---|
| `files/stitch_english_learning_app_design_system/design.md` | Visual design system (colors, type, components, RTL rules) |
| `files/stitch_english_learning_app_design_system/lingua_design_system/DESIGN.md` | Refined token values (CEFR badge bands, option card states, elevation) |
| `files/stitch_english_learning_app_design_system/english_learning_methodology_curriculum_specification.md` | Teaching methodology (Four Strands, retrieval practice, SRS) |
| `files/stitch_english_learning_app_design_system/english_learning_technical_architecture_specification.md` | Target technical architecture |

Specs are **design intent, not proof of implementation** — verify claims against the code.

## 2. Build & verify

**The everyday loop is tests, not APKs.** Compiling all three flavours after every change is slow
and buys nothing — it is only needed at release points or when putting a build on a device:

```bash
./gradlew :app:testBazaarDebugUnitTest   # unit tests + typechecks main sources (the fast gate)
./gradlew validateContent                # content gate (also runs in preBuild); task lives in
                                         #   gradle/content-validation.gradle.kts, applied by :app
```

Hardware check — builds and installs only when a device is actually attached, and exits quietly
(no build at all) when none is:

```bash
scripts/install_debug.sh                 # bazaar (default); or myket | googlePlay
scripts/install_debug.sh --list-devices
```

Release points only:

```bash
./gradlew :app:assembleBazaarDebug      # Cafe Bazaar APK (Poolakey billing)
./gradlew :app:assembleMyketDebug       # Myket APK (myket-billing-client)
./gradlew :app:assembleGooglePlayDebug  # Google Play APK (Play Billing 9)
```

Notes: only per-flavour unit-test tasks exist (`testDebugUnitTest` is not a task under AGP 9's
`onlyEnableUnitTestForTheTestedBuildType`). CI lives in `.github/workflows/ci.yml` — the same two
fast gates (content validation + unit tests) and `lintBazaarDebug` on every push/PR; the three
flavor assemblies only on schedule/manual dispatch/tags. There is no emulator or device job.

### Toolchain facts (verified Oct 2026 — do not "fix" these casually)

- AGP **9.3.3** + Gradle **9.5.0**, `compileSdk 37`, `minSdk 24`, JDK toolchain 25.
- **Built-in Kotlin** (AGP 9 default): *never* apply `org.jetbrains.kotlin.android`.
- KSP `2.2.10-2.0.2` must match the built-in KGP (2.2.10). AGP itself floors KSP to this version.
- `android.disallowKotlinSourceSets=false` in `gradle.properties` is **required** for KSP's generated sources under built-in Kotlin.
- The Compose compiler plugin version **must equal the built-in Kotlin version** (`composeCompiler = "2.2.10"` in `gradle/libs.versions.toml`). If you bump AGP, re-check both KSP and composeCompiler refs.
- Room needs KSP; Room errors like `[MissingType] ... not all of its dependencies could be resolved` almost always mean a **Kotlin compile error elsewhere** (check the sources first, e.g. a doc comment containing `/*` opens a nested comment in Kotlin).
- Kotlin block comments **nest**: never write `path/*.ext` inside `/** ... */`.

## 3. Architecture (single module, layered packages)

One Gradle module (`:app`), package boundaries mirror the future multi-module split:

```text
org.token.english
├── SplashActivity.kt          # Custom launch screen: blank system splash (Theme.English.Starting),
│                              #   waits for content seeding, then hands off to MainActivity
├── MainActivity.kt            # Compose host: theme gate + forced RTL root
├── EnglishApp.kt              # Application → owns AppContainer, seeds content, mirrors sound flag
├── navigation/                # Routes + NavHost + bottom bar (4 tabs)
├── core/
│   ├── designsystem/          # Color/Type/Shape/Spacing/Theme tokens + Skill label map
│   │   └── component/         # AppButton, AppCard, badges, progress, feedback banners, EnglishText
│   ├── billing/               # BillingGateway interface, EntitlementPolicy (pure), ProductIds
│   ├── audio/                 # AudioPlayer interface + TtsAudioPlayer (on-device TTS)
│   └── common/                # TimeUtil (day keys, streaks — no java.time, minSdk 24)
├── domain/                    # PURE KOTLIN — no Android, no Room, no Compose
│   ├── model/                 # Lesson, VocabularyItem, Exercise(sealed), ReviewItem, AnswerChecker…
│   ├── repository/            # Repository interfaces (implemented in data/)
│   ├── engine/                # ReviewScheduler (SM-2-lite), MasteryEngine, LearningPlanner
│   └── usecase/               # SubmitExercise, CompleteLesson, SubmitReview, GetTodayPlan,
│                              #   ScorePlacement + AssessPlacement (P1-1 skill assessment)
├── data/
│   ├── local/                 # Room: entities, DAOs, AppDatabase (version 5, schemas exported)
│   ├── content/               # ContentParser (org.json) + ContentSeeder (assets → Room)
│   └── repository/            # Repository impls + SettingsRepositoryImpl (DataStore)
├── di/                        # Manual DI: AppContainer.kt (+ ViewModelFactories.kt,
│                              #   the ONE place the container meets a ViewModel)
├── feature/<screen>/          # Screen + ViewModel (+ UiState/Event) per feature
│                              #   … incl. paywall/ (subscription purchase & restore)
└── src/{bazaar,myket,googlePlay}/billing/PlatformBillingGateway.kt  # ONE per flavor
```

**Product flavors** (`store` dimension): `bazaar`, `myket`, `googlePlay` — each packages only
its own marketplace billing SDK (see §4a).

**Dependency rule (enforced by review, not tooling yet):**

```text
feature → domain (models/usecases) + core/designsystem + core/common + di (factory fn only)
domain  → nothing Android (pure Kotlin only)
data    → domain (implements interfaces) + Room/DataStore
di      → everything (the only place that wires them together)
```

- UI state pattern: `UiState (StateFlow) ← Event ← ViewModel ← UseCase/Repository`.
  ViewModels never touch Room/DataStore directly, composables never contain business logic.
- **DI is manual** (`AppContainer`): constructor injection per class, container-level wiring.
  Hilt was deliberately skipped — its plugin/KSP interaction under AGP 9 built-in Kotlin adds
  risk with no MVP value. Revisit only if the container grows past ~25 bindings.
- **A ViewModel takes only what it uses (B-6), and `feature/` never imports `di/`.** Each
  ViewModel's constructor lists the repositories, use cases, audio seam (`Speaker`, `AudioPlayer`),
  foreground/companion lambdas and tick intervals it actually needs — no `AppContainer` parameter,
  no service locator. `di/ViewModelFactories.kt` holds one factory per screen and is the single
  place the container meets a ViewModel; screens call `viewModel(factory = homeViewModelFactory(...))`
  and that factory function is the only `di/` symbol `feature/` is allowed to import
  (grep `AppContainer app/src/main/java/org/token/english/feature/` must stay empty).
  Non-zero defaults exist only for tick intervals (tests pass `0` to disable the timer loops), and
  narrow constructors are exactly what makes the first ViewModel tests possible without Android.
- Engines are pure and swappable: `Sm2ReviewScheduler`, `DefaultMasteryEngine`,
  `DefaultLearningPlanner`, plus the knowledge layer — `KnowledgeGraph` (prerequisite ordering and
  unlocking) and `DefaultKnowledgeEngine` (how one graded answer moves a node's mastery and review
  date). The UI must never learn how intervals/mastery are computed (technical spec §19).
- The adaptive layer on top of that graph is also pure and tested: `PrerequisiteEngine`
  (readiness / remediation / dependency depth), `AdaptiveLearningPlanner` (the ordered, justified
  "what to study now and why"), `DefaultMasteryProfileEngine` (mastery split by dimension —
  recognition / recall / comprehension / application / production), and `AdaptiveExerciseSelector`
  (front-loads the exercise formats whose mastery dimension is still weak).
- **The adaptive layer is wired to the UI (checklists B-1/B-6).** `GetTodayPlanUseCase` builds the
  graph from the seeded items, plans, and returns the ordered `LearningAction`s inside
  `TodayPlan.actions`; `HomeScreen` renders them with each action's own `reasonFa` (the "برنامهٔ
  امروز" card). `GetFocusPlanUseCase` then asks `RemediationEngine` for the weak spot — one node,
  why it is worth drilling now, what it blocks, and how it is verified (`FocusPlan`) — and Home
  renders it as the "نقطهٔ ضعف" card whose CTA opens the focused session (`lesson/{lessonId}?focusItemId=`).
- **A focused session (B-1) is the engine's decision, not a screen's.** It shows only the lesson's
  exercises that are evidence about the node (the same `KnowledgeEvidence` rule that writes the
  learner model), keeps their authored order, leads with the engine's reason, and reports the
  node's mastery against `FocusPlan.reassessThreshold`. Only a node the engine itself calls weak
  (mastery below `RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD`, with evidence) gets a block; a node
  the learner never studied is *taught*, not drilled, so `GetFocusPlanUseCase` returns `null` for it.
- **`AdaptiveExerciseSelector` + `DefaultMasteryProfileEngine` steer a session's queue.** Every
  graded answer is folded into a per-dimension `MasteryProfile` (recognition / recall /
  comprehension / application / production) and the next exercise is chosen with the selector, so
  the kinds of knowing still untested lead the session instead of the next authored item. Two rules
  keep it safe: with no evidence the sort is stable, so a first-teach lesson runs exactly as
  authored, and a missed exercise still comes back later in the session (a failed dimension ties
  with an untouched one at 0, and the tie is broken by queue position). The profile is
  **session-scoped** — there is no per-dimension column yet (see §10).
- **Planner time budget (checklist B-2):** review is never dropped for lack of budget — its
  estimate is capped at half the day's target and it always leads — and the remaining actions
  stop at the **first** one that does not fit instead of skipping ahead to a cheaper one. With the
  daily goal already met the plan collapses to the review block; it is never empty.
- **The learner model is per knowledge item, not per exercise or per skill.** Every graded answer
  is attributed to the curriculum nodes its lesson teaches (`KnowledgeEvidence`, a pure tested rule)
  and written to `knowledge_state` with an atomic read-modify-write, exactly like skill mastery.
  `DefaultKnowledgeEngine` reuses `Sm2ReviewScheduler` for intervals/ease and `MasteryEngine` for
  mastery, so "known" means one thing everywhere. Surfaced on the Progress screen.

## 4. Connectivity policy — what is ACTIVE vs SUSPENDED

The app is **offline-first, not offline-only** (product decision, Oct 2026). The learning core
runs entirely from local data (Room + DataStore) and must keep working with no network at all —
that is a hard requirement, not a preference. Online capabilities are otherwise allowed again;
the only things held back are features that require a **backend/server** to be built and operated.

`INTERNET` is currently used only by in-app billing (price lookup, store checkout, subscription
verification — verified in the merged manifest). Any *new* online feature must state which service
it calls and why, must degrade gracefully when offline, and must not block the learning core.
`allowBackup` stays on because data is non-sensitive learning progress.

Merged-manifest permissions per flavor (verified Oct 2026 — re-check after SDK bumps):

| Flavor | App permissions | Notes |
|---|---|---|
| `bazaar` | `INTERNET`, `…PAY_THROUGH_BAZAAR` | Poolakey's market permission |
| `myket` | `INTERNET`, `ir.mservices.market.BILLING` | `AD_ID` is **removed** via `tools:node="remove"` (play-services-ads-identifier comes in transitively; the SDK guards its own ad-id call) |
| `googlePlay` | `INTERNET`, `com.android.vending.BILLING`, `ACCESS_NETWORK_STATE` | last two are declared by Play Billing itself — required, keep |

All flavors also carry `POST_NOTIFICATIONS` (declared in the main manifest — the daily reminder
posts a local notification on API 33+; without the declaration the Settings request is a no-op and
`lintBazaarDebug` fails with `MissingPermission`) and `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`
(system-generated, not a real app permission).

### Active (must keep working offline)

- Bundled A1–C2 lessons, vocabulary, exercises, placement test (assets/content)
- Lesson flow, answer checking, skill mastery
- Spaced-repetition review queue (SM-2-lite)
- Daily plan, daily goal, streaks, progress stats
- Listening via **on-device Android TTS** (no audio files, no network)
- Vocabulary browser + search
- Theme (system/light/dark), sound toggle, daily goal, reset
- Subscription purchase/restore via the store (the only online surface today, §4a)

### Suspended — needs backend/server infrastructure (do NOT implement yet)

These are blocked by infrastructure, not by the offline rule. Building them means standing up and
operating a service first, so they stay out of scope until that exists.

| Feature | Why it's suspended | Where the seam is |
|---|---|---|
| AI conversation / AI feedback | Needs a backend (model hosting + API) | Future: `ConversationEngine` interface (technical spec §38) |
| Cloud sync, auth, backup | Needs a backend + accounts | `id/updatedAt/syncState` fields are **not** added yet; keep repositories local-only |
| Downloadable content packages | Needs a server + signed content validation | `ContentSeeder` is keyed by the JSON `contentVersion` — the upgrade hook already exists |
| Analytics upload | Needs a collection endpoint **and** a privacy decision (opt-in, minimal, anonymous) | No analytics module exists. Intentional. |

If a task requires one of these, **stop and confirm with the user first**.

### Unblocked by the connectivity policy — not built yet

These do **not** need a backend, so they may be built whenever they are prioritised:

| Feature | State | Where the app hooks in |
|---|---|---|
| Speaking / pronunciation evaluation | Domain type exists, UI shows a placeholder | `Exercise.Speaking` + the "غیرفعال" branch in `LessonScreen`; an on-device recognizer needs no server |
| Remote crash reporting | Not implemented | Pick a provider deliberately — it is a network + privacy decision, not a default |

Completed and removed from this list: B1–C2 content (shipped in the **v5 bundle** — 6 lessons per
level A1–B2, 7 for C1/C2) and daily review reminders (now **active**, `reminder/DailyReminder.kt`).

## 4a. Monetization — subscription × 3 marketplaces

Product decision (user-confirmed): **all content is subscription-gated with a 7-day free trial**
(`TrialClock.TRIAL_MILLIS` = `7 * 24h`, consumed against elapsed real time so it survives app
restarts; it lives in local DataStore only, so clearing app data restarts the trial);
two plans — `sub_monthly` (monthly) and `sub_yearly` (yearly). Each release build targets one
market and embeds only that market's billing SDK:

| Flavor | Store | SDK (verified via javap/docs Oct 2026) | Notes |
|---|---|---|---|
| `bazaar` | کافه بازار | `com.github.cafebazaar.Poolakey:poolakey:2.2.0` | Real subscriptions (`subscribeProduct`). Local RSA key via `keystore.properties` → `bazaarRsaKey`; falls back to `SecurityCheck.Disable` until set — **must be set before release**. |
| `myket` | مایکت | `com.github.myketstore:myket-billing-client:1.19` | Myket has **no subscription products** (official KB). Plans are consumables: 30/365-day local expiry, consumed after purchase so renewal can re-buy. Reinstall loses local expiry (documented limitation). Public key → `myketPublicKey`. Its manifest needs `manifestPlaceholders` (`marketApplicationId`/`marketBindAddress`/`marketPermission`, official sample values) — already set in `app/build.gradle.kts`.
| `googlePlay` | Google Play | `com.android.billingclient:billing:9.1.0` | Subs (`SUBS`), first offer, acknowledge + purchase listener, 32-day grace mirrored locally. |

Rules:

- UI/domain only see `core/billing/BillingGateway` — store specifics live in the flavor's
  `PlatformBillingGateway`. Adding a 4th market = new flavor + one file, zero UI changes.
- Access rules are pure & unit-tested: `EntitlementPolicy` (SUBSCRIPTION → COMPANION_APP →
  TRIAL → LOCKED, with an `AccessReason` so the UI never calls a free grant a purchase) in
  `core/billing/Billing.kt`.
- **Free-access companion (product decision, Oct 2026, user-confirmed): while the Zaribar app
  (`org.token.zaribar`) is installed the subscription is free.** The grant follows the install
  and is re-checked on every foreground (`EnglishApp.onActivityResumed`) and whenever Home,
  the paywall or Settings opens — it never outlives the install. Detection is one local
  `PackageManager` lookup (`core/billing/CompanionApp.kt`, offline, no permission); the
  package must stay listed in the manifest `<queries>` block or API 30+ package visibility
  hides it. **The install is only trusted when its signing certificate matches
  `CompanionApp.EXPECTED_SIGNING_SHA256`** (N-1: the package id alone is spoofable by any APK
  that declares it); `CompanionSignature` does the normalization/matching and is unit-tested
  because the Android lookup cannot run on the JVM. While the grant is active, the paywall
  replaces the plans with the free state and a Cafe Bazaar download link
  (`bazaar://details?id=org.token.zaribar`, web fallback) so the learner can keep/restore
  zaribar — shown in every flavor on purpose, since the companion is published on Bazaar only.
- Gating: `AppNavGraph` redirects any non-exempt route to `Routes.PAYWALL` when LOCKED;
  exempt routes are onboarding/placement/settings. Home shows a calm trial-countdown banner
  and — when the companion grant is active — a "subscription is free" banner with the link.
- Store is source of truth; active subscriptions are mirrored locally with a 32-day grace so
  short offline spells don't lock the learner out.
- Release signing: fill `keystore.properties` (`storeFile/storePassword/keyAlias/keyPassword`,
  plus `bazaarRsaKey`, `myketPublicKey`). The file must stay untracked — it holds secrets.
- Do not add any other network features while billing is the sole `INTERNET` user (§4).

## 5. Content pipeline

- Source of truth: `app/src/main/assets/content/{lessons,vocabulary,exercises,placement,knowledge}.json`.
  `knowledge.json` is the **curriculum graph**: one node per learnable unit, with
  `prerequisites` (ids of nodes to learn first), the `lessons` that teach it, its CEFR `level` and
  the `skills` it trains. `KnowledgeGraph` (pure, in `domain/engine`) orders and unlocks it; it is
  seeded into the `knowledge_item` table. Repositories/UI must not hard-code curriculum order.
- `ContentSeeder` runs at app start; re-seeds when the JSON `contentVersion` changes (settings key
  `content_version`). Content tables: `lesson`, `vocabulary`, `exercise`. The version is authored
  **inside the JSON** — there is no Kotlin constant to bump (technical spec §63).
- **`./gradlew validateContent`** (wired into `preBuild`, defined in
  `gradle/content-validation.gradle.kts`) is the content gate. It checks unique ids,
  dangling `lessonId` references, valid CEFR levels and exercise types, `correctIndex` in range,
  `fill_blank` blank markers, non-empty accepted answers, duplicate words within a lesson, matching
  bundle versions, and full lesson coverage (every lesson needs vocabulary, exercises and at least
  one knowledge item). For the graph it also checks that prerequisite ids resolve and that **no
  cycle** exists. It reports **all** problems at once, not just the first. Run it after every
  content edit.
- **CEFR bundle (contentVersion 20):** 48 lessons (A1 = 15, A2 = 7, B1/B2 = 6 each, C1/C2 = 7
  each), 441 exercises (6–11/lesson + reading passages + meaning items + A1 pronunciation drills), 290
  vocabulary entries (6/lesson, 8 in the numbers lesson), 89 knowledge items,
  placement = 36 questions in six graded bands of 6 (A1→C2, ordered by difficulty). Grammar points per level follow
  the British Council / EQUALS Core Inventory grammar tables (verified against examenglish.com/CEFR,
  Oct 2026): e.g. B1 = 2nd/3rd conditional + reported speech + simple passive; C1 = inversion +
  mixed conditionals + modals in the past; C2 = nuance/precision vocabulary.
- **Vocabulary depth (A-10, bundle 18 → 19):** every word carries **two** example sentences, each a
  bilingual object `{"en": …, "fa": …}` authored in `vocabulary.json` (290 words × 2 = 580 English
  sentences, all with Persian). The shape lives inside the existing `VocabularyEntity.examplesJson`
  TEXT column — the column type never changed, `ContentParser`/`RepositoriesImpl` accept the legacy
  plain-string shape, and `ContentSeeder` clear+inserts every row on the version bump — so **no Room
  schema migration is needed**: Room stays at `version = 5` and `RoomMigrationTest` keeps proving the
  existing path (a migration would rewrite nothing). `validateContent` and `VocabularyContentTest`
  both require ≥ 2 examples per word with a non-empty Persian `fa`, and the word-in-example rule is
  now checked **per example**, not per entry. One-shot batch:
  `scripts/archive/p1_bilingual_examples.mjs` (never re-run).
- **Foundations at the very front (A-7):** the alphabet, spelling a name and numbers 0–100 come
  **before** Greetings, so a complete beginner meets letters before words. Three A1 lessons
  (`a1.alphabet.lesson-01`, `a1.spelling.lesson-01`, `a1.numbers.lesson-01`, orders 1–3 with
  every other lesson shifted by three), 20 words, 24 exercises and 6 knowledge items
  (`vocab.alphabet`, `vocab.spelling`, `vocab.numbers` + a PHONOLOGY node per lesson), and
  `vocab.greetings` now lists `vocab.alphabet` as a prerequisite. Applied by
  `scripts/archive/p1_foundation_lessons.mjs`; bundle 14 → 15.
- **A1/A2 gaps closed (A-9):** articles (a/an/the), plural spelling, subject/object pronouns,
  can for ability, imperatives and question words each got their own A1 lesson (tip + 6 words +
  6 grammar exercises + phonology coverage + a guided listening drill), and the past simple is
  now introduced at **A2** (`a2.past-simple.lesson-01`, `grammar.past-simple` level B1 → A2) with
  `b1.work` still teaching it as a review. Placement bands were re-checked afterwards (at the
  time 30 questions, five per level, A1 → C2 in order; A-6 later grew it to six per band).
- **Content correctness pass (P0-9):** the bundle was audited end-to-end and two silent defects
  fixed, both now enforced by `VocabularyContentTest`:
  (a) the generated "What does X mean?" choices (`scripts/archive/enrich_content.mjs` writes one per
  lesson at `*.ex.07`) drew their distractors from a global cursor, so an A1 item could offer a
  C2 gloss — distractors now come from the lesson's own vocabulary;
  (b) two vocabulary examples (`a1.shopping.word.price`, `…word.discount`) never named their word.
  Every vocabulary example must now contain its word (inflection-tolerant: `blocks` illustrates
  `block`, `children` illustrates `child`, separable phrasal verbs may split) — the genuinely
  irregular forms live in an explicit allowlist that fails the build when it rots.
  `scripts/archive/p0_fix_a1a2_content.mjs` is the one-shot batch that applied it; the count stayed
  45/270/383/30/83 (edits in place, the bundle moved 12 → 13). Placement gained its `skill`
  tags in the same style of in-place edit (bundle 13 → 14, P1-1); no counts changed.
- **Practice bar (A-8):** every GRAMMAR knowledge item needs **at least six grammar-targeting
  exercises** across its lessons, counted with `AnswerChecker.skillOf` — the same rule the
  mastery engine uses, so a vocabulary-heavy lesson does not count as grammar practice.
  `GrammarCoverageTest` fails the build below the bar; add exercises to that topic's lessons.
- **Explain a miss (A-1):** every grammar and fill-in-the-blank exercise carries a one/two-sentence
  `explanationFa` saying *why* the answer is right (and, for the common wrong option, why it is not).
  Coverage is now **every one of the 441 exercises** (grammar/fill-blank core plus vocabulary
  meaning, translation and listening items). `ExplanationCoverageTest` enforces 100% on the core
  set and on the whole bundle (checklist A-1's floor of 90% was passed, so the bar is now the
  invariant); `validateContent` prints any exercise without one as a non-blocking warning. A new
  exercise of any type ships with its explanation. Every **vocabulary** word additionally carries
  an authored Persian `explanationFa` (a short usage note): the SRS queue is vocabulary-only, so
  the review session shows it after a miss, exactly as the lesson screen does. It is a vocabulary
  column (`VocabularyEntity.explanationFa`, Room v5) and a new word without one fails
  `ExplanationCoverageTest`.
- Exercise mix is intentionally varied: `multiple_choice`, `fill_blank`, `translation`, `listening`,
  and authored `skill` tags (VOCABULARY / GRAMMAR / READING / WRITING) so mastery is not dominated
  by recognition questions. Correct-answer positions stay balanced —
  `scripts/balance_answer_positions.mjs` restores the invariant and `ContentDistributionTest`
  enforces it.
- Placement scoring is **band-based** (`ScorePlacementUseCase`): walks bands bottom-up, band
  passes at ≥2/3 with ≥60% cumulative accuracy, stops at first failed band, floor A1.
  Question `level` tags in placement.json are documentation; scoring uses array order +
  `ScorePlacementUseCase.DEFAULT_BAND_SIZE = 6` (six bands × 6 = 36 questions).
- **Reading comprehension and meaning items (A-5).** `Exercise.MultipleChoice` carries an optional
  `passage` (English, rendered above the question by `LessonScreen`/`PlacementScreen`). Six lessons
  — one per level A1→C2 — hold a 3–5-sentence passage with three comprehension questions that each
  repeat the passage, so a re-queued question is still answerable alone; each of those lessons also
  gets one English→Persian "What does this sentence mean?" item. `ReadingContentTest` enforces the
  passage shape (3–5 sentences, English, reused by ≥2 questions), one passage per level, Persian
  options on the meaning items, and the placement reading questions. One-shot batch:
  `scripts/archive/p1_reading_and_meaning.mjs` (never re-run).
- **Placement is also a skill assessment (P1-1).** Every placement question carries an authored
  `skill` (11 VOCABULARY, 19 GRAMMAR, 6 READING — the three axes the test actually measures; no fake
  tags). A-6 added one reading item per band and grew each band to six.
  `PlacementAssessmentEngine` (pure) returns the overall level from the same band scorer plus a
  per-skill accuracy read, `strengths` and weakest-first `focusSkills`; `AssessPlacementUseCase`
  then replays the answers through `ProgressRepository.applyAttempt` so skill mastery starts
  calibrated instead of blank — never by writing mastery directly, so the EWMA stays single-source.
  The result screen shows the per-skill breakdown; unmeasured skills are never listed.
- IDs are **stable dotted strings** (`a1.airport.word.passport`, `placement.q01`) — never renumber.
- `exercise.payloadJson` stores the authored JSON verbatim; `ContentParser.exerciseFromPayload`
  maps it to the sealed `Exercise`. Unknown types are skipped, never crash a lesson.
- Adding content = JSON only (no Kotlin changes), per technical spec §61/§63.
- **The JSON files are the source of truth, not the scripts (A-12).** Applied batches live in
  `scripts/archive/` (see its README) purely as the record of *how* a change was made — **never
  re-run one**, it would overwrite later hand edits. `scripts/` itself holds only what you still
  run: `balance_answer_positions.mjs` and `install_debug.sh`. After any content edit run
  `node scripts/balance_answer_positions.mjs`, then `validateContent` + the content tests with
  `--rerun`.
- Supported exercise types: `multiple_choice`, `fill_blank`, `translation`, `listening`
  (`speaking` parses but is suspended).
- **Every `fill_blank` must carry an authored `skill` (A-2b)** — `validateContent` rejects an
  untagged one. The heuristic default (GRAMMAR) used to decide whether a near-miss was reported as
  a spelling slip, which is a pedagogical call the author has to make: word-choice blanks are
  `VOCABULARY` (a typo gets the hint), form blanks are `GRAMMAR` (no hint). Tagging them honestly
  left 13 grammar topics under the six-exercise bar, so `scripts/archive/p1_grammar_topup.mjs`
  added real form practice rather than letting a fallback define the bar.
- Typed-answer grading (A-2): digits normalize to words (0–20 + tens), so `3` ≡ `three`;
  a single word ≥ 5 letters that is one edit from an accepted answer is graded wrong but
  shown as «تقریباً درست — املای کلمه را بررسی کن» (`AnswerChecker.grade`). ALMOST is a
  **spelling** signal, so it never fires on a GRAMMAR item (A-2b): `listen` vs `listens` is a
  missing `-s`, not a typo, and the lesson shows a plain miss plus the item's explanation. Every
  single-answer `fill_blank` must appear with a reason in
  `ContentDistributionTest.reviewedSingleAnswerBlanks`; new ones fail the test until
  equivalents are added or justified.
- **A dot/comma between digits is a decimal (A-2b):** `normalize` protects it before stripping
  punctuation, so `3.5` can never collapse into `35`; a sentence-final dot still goes (`3.` ≡
  `three`), and a digit run glued to letters or to a separator (`1st`, `20th`, `3.5`) is left as
  typed instead of becoming `thirtyth`.
- Phonology / pronunciation (A-7, A1 only): each A1 lesson teaches a `## تلفظ` section in its
  `grammarTipFa` (vowel length, word stress, word sounds — an unknown header parses to a CUSTOM
  card, so no schema change), is covered by a PHONOLOGY knowledge item, and has a guided
  listening drill (`listening` + `explanationFa` naming what to hear). PHONOLOGY nodes list the
  `LISTENING` skill and are **never prerequisites** of other items: `KnowledgeEvidence.itemsFor`
  unions type-matches with explicit skill tags so listening moves them, but gating a lesson on
  pronunciation mastery would strand learners. `PhonologyCoverageTest` enforces all of it.

## 6. Design system rules (from design.md)

- **Never hardcode colors/spacings/radii**: use `MaterialTheme.colorScheme`, `LocalAppExtendedColors`
  (success/warning/info), `AppSpacing`, `MaterialTheme.shapes`.
- Root layout is **forced RTL**; all English content goes through `EnglishText` / `AnswerField`
  (which re-assert LTR). Never put raw English in an RTL layout.
- Feedback is never color-only: always icon + text (`CorrectBanner`/`IncorrectBanner`).
- Touch targets ≥ 48dp (`TouchTargetMin`), primary buttons 48dp high, cards 16dp radius,
  and **cards always span the available width** — `AppCard` fills it itself so no call site
  has to remember (a card sized to its content reads as a chip and breaks the column edge).
- Every feature screen needs empty/loading/error states (design.md §37–39). The learning core
  works offline, so a local failure must never be blamed on the network. Only a genuinely online
  action (e.g. a purchase) may show a connectivity error — and it must be specific about it.
- Streak must never visually outweigh learning progress (design.md §25).

## 7. Security posture

- One permission today (`INTERNET`, billing-only). New online features may add permissions, but
  each one must be documented in the table above with the service it talks to. Today the only
  network code paths are the billing SDKs — grep for `retrofit`/`okhttp`/`URL(` in app sources
  should stay empty otherwise.
- No secrets in the repo: store keys and signing config live in untracked `keystore.properties`.
- Data is non-sensitive learning progress; stored unencrypted in app-private storage.
- If sync ever ships: add Network Security Config, certificate pinning decision, and an ADR first.

## 8. Testing

- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`, 252 tests / 36 classes as of
  the B-1 adaptive-wiring pass): `ReviewSchedulerTest`,
  `DomainEngineTest` (mastery/answer checking/planner), `TimeUtilTest` (streaks),
  `EntitlementPolicyTest` (trial/subscription/companion gating) + `CompanionAppTest`
  (companion package id + Bazaar links + digest shape) + `CompanionSignatureTest`
  (certificate normalization/matching) + `SubscriptionRecoveryTest` (renewal
  boundaries, reinstall restore, store-truth reconciliation), `KnowledgeGraphTest` (curriculum
  graph), `ContentSeederTest` / `ContentDistributionTest` (content pipeline) /
  `VocabularyContentTest` (example-contains-word + same-lesson distractors), `ReadingContentTest`
  (A-5 passages + meaning items + placement reading), `KnowledgeEngineTest`
  (evidence attribution), `GrammarTipTest` (multi-section lesson tips), `PhonologyCoverageTest`
  (A1 pronunciation coverage), `GrammarCoverageTest` (six exercises per grammar topic),
  `RoomMigrationTest` (migration ↔ exported schema, no gaps), `BidiTextTest` (BiDi direction rule
  + no Persian in English exercise fields), `PlacementAssessmentTest` (per-skill read from the
  placement answers), `LearningPathJourneyTest` (install → placement →
  lesson → miss/retry → complete → review → progress, on the real bundle, incl. placement
  calibrating mastery), plus the
  adaptive layer —
  `PrerequisiteEngineTest`, `AdaptiveLearningPlannerTest` (never exceeds the daily target),
  `MasteryProfileEngineTest`, `AdaptiveExerciseSelectorTest`, `RemediationEngineTest`
  (incl. the evidence filter and "an unassessed dimension must not reorder a lesson"),
  `FocusPlanTest` (the weak-spot decision: one node, its evidence subset, its blocked dependents),
  `ReviewLifecycleTest` (pinned SRS lifecycle numbers), `LearningSimulationTest`.
- **ViewModel tests (B-6)** sit on top of `FakeRepositories.kt` (in-memory repositories + a
  `RecordingSpeaker`), so a screen's ViewModel is exercised with no Android, no Room and no device:
  `HomeViewModelTest` (installing/removing the companion app flips access to COMPANION_APP / LOCKED;
  the engine's weak spot reaches the state), `LessonViewModelTest` (a miss is re-asked exactly once
  and the session still ends; a device without an English voice reports `audioUnavailable`; a fresh
  lesson keeps the authored order while an untested dimension is front-loaded; a focused session
  drills only the node's evidence) and `ReviewViewModelTest` (GOOD records the schedule and finishes;
  AGAIN re-queues the item in the same session). They need
  `testOptions { unitTests.isReturnDefaultValues = true }` (the ViewModels read `android.os.SystemClock`)
  and `Dispatchers.setMain(UnconfinedTestDispatcher())`. Future ViewModel tests: keep the fake layer
  growing rather than mocking, and derive display-shuffled indices from the VM state — never assume an
  option index is wrong.
- `.github/workflows/ci.yml` runs the same two gates (validate + unit tests) plus `lintBazaarDebug`
  and the three flavor assemblies (the last only on schedule/manual dispatch/tags).
- Content tests read the assets through `File`, so Gradle cannot see them as inputs — after a
  content edit run them with `--rerun` or they silently report UP-TO-DATE.
- Instrumented test files are still the Android Studio templates (not maintained).
- When you change engine logic, extend these tests first; UI changes rely on manual verification
  (no emulator CI in this environment).

## 9. Conventions & Definition of Done

1. Persian UI strings live inline in composables (single-locale MVP; extraction to
   `strings.xml` is open design debt — do it when adding a second locale).
2. Epoch millis (`Long`) for time — `java.time` needs desugaring at minSdk 24.
3. A feature is done when: unit tests pass, `./gradlew validateContent` passes if content changed,
   empty/loading/error states exist, RTL/LTR verified, no hardcoded design tokens, no undocumented
   permissions, the learning core still works with no network, and this file updated if architecture
   or the suspended list changed. All three flavour builds are checked at release points and on
   hardware via `scripts/install_debug.sh` — **not** after every change.

## 10. Known design debt (accepted, tracked here)

- **Mastery by dimension is session-scoped.** `MasteryProfile` is folded from the answers of the
  session in progress; nothing persists per dimension, so the next session starts blind and the
  selector only adapts once the learner has answered inside that session (B-1). A cross-session
  version needs a per-dimension column/table (Room v6 + migration) and is the natural next step.

- **The companion signing digest is not configured yet** (`CompanionApp.EXPECTED_SIGNING_SHA256`
  is empty), so the free-access grant currently trusts the `org.token.zaribar` package id alone
  and any APK declaring it unlocks the paid tier. Paste the Zaribar release key digest from
  `keytool -printcert -jarfile zaribar.apk` to close N-1; the check is already implemented and
  unit-tested. (A release-time hard failure like `bazaarRsaKey`'s is a possible follow-up.)

- Myket has no real subscriptions: expiry is local and lost on reinstall — re-prove purchase
  through support if a user claims a lost subscription.
- `bazaarRsaKey` unset → Poolakey verification disabled; the build falls back to
  `SecurityCheck.Disable`. Set it in `keystore.properties` before publishing to Bazaar.
- Room is at `version = 5` with exported schemas (`app/schemas`). Schema changes must ship a
  migration **and** be added to `AppContainer.database` — Room throws at open time when a path from
  the installed version is missing, so a forgotten `addMigrations` crashes upgrading installs.
- Due counts capture `now` at collection time; a long-running session won't see newly-due items
  until the ViewModel is recreated.
- The daily reminder is pinned to 19:00 local (`DailyReminder.REMINDER_HOUR`); the time is not
  learner-configurable yet (checklist B-4).
- The ALMOST hint is suppressed for GRAMMAR items but cannot tell "one edit away from a *different*
  word" (A-2b: `affect`/`effect`) from a typo without a dictionary — on a vocabulary target the
  learner is still told to check the spelling. Accepted: the banner shows the correct answer
  alongside it, so the word itself is never ambiguous.
- `studied seconds` are credited on screen close/finish (best effort), not via a foreground timer.
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
