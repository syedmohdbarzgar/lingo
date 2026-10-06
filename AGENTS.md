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
./gradlew validateContent                # content gate (also runs in preBuild)
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
`onlyEnableUnitTestForTheTestedBuildType`). There is **no CI** in this repo.

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
│   └── usecase/               # SubmitExercise, CompleteLesson, SubmitReview, GetTodayPlan, ScorePlacement
├── data/
│   ├── local/                 # Room: entities, DAOs, AppDatabase (version 4, schemas exported)
│   ├── content/               # ContentParser (org.json) + ContentSeeder (assets → Room)
│   └── repository/            # Repository impls + SettingsRepositoryImpl (DataStore)
├── di/AppContainer.kt         # Manual DI container (+ appViewModelFactory helper)
├── feature/<screen>/          # Screen + ViewModel (+ UiState/Event) per feature
│                              #   … incl. paywall/ (subscription purchase & restore)
└── src/{bazaar,myket,googlePlay}/billing/PlatformBillingGateway.kt  # ONE per flavor
```

**Product flavors** (`store` dimension): `bazaar`, `myket`, `googlePlay` — each packages only
its own marketplace billing SDK (see §4a).

**Dependency rule (enforced by review, not tooling yet):**

```text
feature → domain (models/usecases) + core/designsystem + di
domain  → nothing Android (pure Kotlin only)
data    → domain (implements interfaces) + Room/DataStore
```

- UI state pattern: `UiState (StateFlow) ← Event ← ViewModel ← UseCase/Repository`.
  ViewModels never touch Room/DataStore directly, composables never contain business logic.
- **DI is manual** (`AppContainer`): constructor injection per class, container-level wiring.
  Hilt was deliberately skipped — its plugin/KSP interaction under AGP 9 built-in Kotlin adds
  risk with no MVP value. Revisit only if the container grows past ~25 bindings.
- Engines are pure and swappable: `Sm2ReviewScheduler`, `DefaultMasteryEngine`,
  `DefaultLearningPlanner`, plus the knowledge layer — `KnowledgeGraph` (prerequisite ordering and
  unlocking) and `DefaultKnowledgeEngine` (how one graded answer moves a node's mastery and review
  date). The UI must never learn how intervals/mastery are computed (technical spec §19).
- The adaptive layer on top of that graph is also pure and tested: `PrerequisiteEngine`
  (readiness / remediation / dependency depth), `AdaptiveLearningPlanner` (the ordered, justified
  "what to study now and why"), `DefaultMasteryProfileEngine` (mastery split by dimension —
  recognition / recall / comprehension / application / production), and `AdaptiveExerciseSelector`
  (front-loads the exercise formats whose mastery dimension is still weak). Wired to the UI only
  after the decisions themselves are proven; today they are exercised by their unit tests.
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

All flavors also carry `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (system-generated, not a
real app permission).

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

Product decision (user-confirmed): **all content is subscription-gated with a 24h free trial**;
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
- Access rules are pure & unit-tested: `EntitlementPolicy` (TRIAL → PREMIUM → LOCKED) in
  `core/billing/Billing.kt`.
- Gating: `AppNavGraph` redirects any non-exempt route to `Routes.PAYWALL` when LOCKED;
  exempt routes are onboarding/placement/settings. Home shows a calm trial-countdown banner.
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
- **`./gradlew validateContent`** (wired into `preBuild`) is the content gate. It checks unique ids,
  dangling `lessonId` references, valid CEFR levels and exercise types, `correctIndex` in range,
  `fill_blank` blank markers, non-empty accepted answers, duplicate words within a lesson, matching
  bundle versions, and full lesson coverage (every lesson needs vocabulary, exercises and at least
  one knowledge item). For the graph it also checks that prerequisite ids resolve and that **no
  cycle** exists. It reports **all** problems at once, not just the first. Run it after every
  content edit.
- **CEFR bundle (contentVersion 8):** 38 lessons (A1/A2/B1/B2 = 6 each, C1/C2 = 7 each), 264
  exercises (6/lesson + 12 reading items + 6 A1 pronunciation drills), 228 vocabulary entries
  (6/lesson), 75 knowledge items,
  placement = 30 questions in six graded bands of 5 (A1→C2, ordered by difficulty). Grammar points per level follow
  the British Council / EQUALS Core Inventory grammar tables (verified against examenglish.com/CEFR,
  Oct 2026): e.g. B1 = 2nd/3rd conditional + reported speech + simple passive; C1 = inversion +
  mixed conditionals + modals in the past; C2 = nuance/precision vocabulary.
- Exercise mix is intentionally varied: `multiple_choice`, `fill_blank`, `translation`, `listening`,
  and authored `skill` tags (VOCABULARY / GRAMMAR / READING / WRITING) so mastery is not dominated
  by recognition questions. Correct-answer positions stay balanced —
  `scripts/balance_answer_positions.mjs` restores the invariant and `ContentDistributionTest`
  enforces it.
- Placement scoring is **band-based** (`ScorePlacementUseCase`): walks bands bottom-up, band
  passes at ≥2/3 with ≥60% cumulative accuracy, stops at first failed band, floor A1.
  Question `level` tags in placement.json are documentation; scoring uses array order + `bandSize=3`.
- IDs are **stable dotted strings** (`a1.airport.word.passport`, `placement.q01`) — never renumber.
- `exercise.payloadJson` stores the authored JSON verbatim; `ContentParser.exerciseFromPayload`
  maps it to the sealed `Exercise`. Unknown types are skipped, never crash a lesson.
- Adding content = JSON only (no Kotlin changes), per technical spec §61/§63.
- Supported exercise types: `multiple_choice`, `fill_blank`, `translation`, `listening`
  (`speaking` parses but is suspended).
- Typed-answer grading (A-2): digits normalize to words (0–20 + tens), so `3` ≡ `three`;
  a single word ≥ 5 letters that is one edit from an accepted answer is graded wrong but
  shown as «تقریباً درست — املای کلمه را بررسی کن» (`AnswerChecker.grade`). Every
  single-answer `fill_blank` must appear with a reason in
  `ContentDistributionTest.reviewedSingleAnswerBlanks`; new ones fail the test until
  equivalents are added or justified.
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
- Touch targets ≥ 48dp (`TouchTargetMin`), primary buttons 48dp high, cards 16dp radius.
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

- Unit tests (JUnit, run with `./gradlew :app:testBazaarDebugUnitTest`): `ReviewSchedulerTest`,
  `DomainEngineTest` (mastery/answer checking/planner), `TimeUtilTest` (streaks),
  `EntitlementPolicyTest` (trial/subscription gating), `KnowledgeGraphTest` (curriculum graph),
  `ContentSeederTest` / `ContentDistributionTest` (content pipeline), `KnowledgeEngineTest`
  (evidence attribution), `GrammarTipTest` (multi-section lesson tips), `PhonologyCoverageTest`
  (A1 pronunciation coverage), plus the adaptive layer —
  `PrerequisiteEngineTest`, `AdaptiveLearningPlannerTest`, `MasteryProfileEngineTest`,
  `AdaptiveExerciseSelectorTest`, `ReviewLifecycleTest` (pinned SRS lifecycle numbers).
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

- Myket has no real subscriptions: expiry is local and lost on reinstall — re-prove purchase
  through support if a user claims a lost subscription.
- `bazaarRsaKey` unset → Poolakey verification disabled; the build falls back to
  `SecurityCheck.Disable`. Set it in `keystore.properties` before publishing to Bazaar.
- Room is at `version = 4` with exported schemas (`app/schemas`). Schema changes must ship a
  migration **and** be added to `AppContainer.database` — Room throws at open time when a path from
  the installed version is missing, so a forgotten `addMigrations` crashes upgrading installs.
- Due counts capture `now` at collection time; a long-running session won't see newly-due items
  until the ViewModel is recreated.
- `studied seconds` are credited on screen close/finish (best effort), not via a foreground timer.
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
