# AGENTS.md — Freebuff English Learning App (org.token.english)

> این سند «حقیقت مرجع» برای هر انسان یا ایجنتی است که روی این پروژه کار می‌کند.
> اپ **کاملاً آفلاین** است؛ هر قابلیتی که به اینترنت/سرویس ابری نیاز دارد باید «معلق» بماند (جدول پایین).

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

## 2. Build & verify (run these before claiming done)

```bash
./gradlew :app:assembleBazaarDebug      # Cafe Bazaar APK (Poolakey billing)
./gradlew :app:assembleMyketDebug       # Myket APK (myket-billing-client)
./gradlew :app:assembleGooglePlayDebug  # Google Play APK (Play Billing 9)
./gradlew :app:testDebugUnitTest        # runs unit tests (JUnit)
```

All green as of the billing integration (Oct 2026). There is **no CI** in this repo.
Quick smoke (no APK): `./gradlew :app:compileBazaarDebugKotlin :app:compileMyketDebugKotlin :app:compileGooglePlayDebugKotlin`.

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
│   ├── local/                 # Room: entities, DAOs, AppDatabase (version 1, no schema export)
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
- Engines (`Sm2ReviewScheduler`, `DefaultMasteryEngine`, `DefaultLearningPlanner`) are pure and
  swappable: the UI must never learn how intervals/mastery are computed (technical spec §19).

## 4. Offline policy — what is ACTIVE vs SUSPENDED

The app has exactly **one permission: `INTERNET`, used only by in-app billing** (price lookup,
store checkout, subscription verification — verified in the merged manifest). Learning features
must never use the network: no sync, no analytics, no remote content, no crash reporting.
Any new use of `INTERNET` requires an explicit product decision. Data is local-only (Room +
DataStore); `allowBackup` stays on because data is non-sensitive learning progress.

Merged-manifest permissions per flavor (verified Oct 2026 — re-check after SDK bumps):

| Flavor | App permissions | Notes |
|---|---|---|
| `bazaar` | `INTERNET`, `…PAY_THROUGH_BAZAAR` | Poolakey's market permission |
| `myket` | `INTERNET`, `ir.mservices.market.BILLING` | `AD_ID` is **removed** via `tools:node="remove"` (play-services-ads-identifier comes in transitively; the SDK guards its own ad-id call) |
| `googlePlay` | `INTERNET`, `com.android.vending.BILLING`, `ACCESS_NETWORK_STATE` | last two are declared by Play Billing itself — required, keep |

All flavors also carry `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (system-generated, not a
real app permission).

### Active (offline, must keep working)

- Bundled A1–C2 lessons, vocabulary, exercises, placement test (assets/content)
- Lesson flow, answer checking, skill mastery
- Spaced-repetition review queue (SM-2-lite)
- Daily plan, daily goal, streaks, progress stats
- Listening via **on-device Android TTS** (no audio files, no network)
- Vocabulary browser + search
- Theme (system/light/dark), sound toggle, daily goal, reset
- Subscription purchase/restore via the store (the only online surface, §4a)

### Suspended (offline build — do NOT implement while offline)

| Feature | Why it's suspended | Where the seam is |
|---|---|---|
| Speaking / pronunciation evaluation | Needs speech recognition (cloud or GMS) | `Exercise.Speaking` exists in domain; UI shows a "غیرفعال" placeholder in `LessonScreen` |
| AI conversation / AI feedback | Needs a backend | Future: `ConversationEngine` interface (technical spec §38) |
| Cloud sync, auth, backup | Needs a backend | `id/updatedAt/syncState` fields are **not** added yet; keep repositories local-only |
| Downloadable content packages | Needs a server + validation | `ContentSeeder.CONTENT_VERSION` is the upgrade hook; keep single bundled version |
| Analytics upload | Privacy + network | No analytics module exists. Intentional. |
| Daily review reminders | Needs notifications/WorkManager permission flow | Not wired; do not add silently |
| B1–C2 content | **SHIPPED (v2 bundle)** — 4 lessons per level A1–C2 | Content pipeline only; extend via `assets/content/*.json` + `CONTENT_VERSION` bump |

If a task requires one of these, **stop and confirm with the user first** — it is suspended by
explicit decision, not by oversight.

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

- Source of truth: `app/src/main/assets/content/{lessons,vocabulary,exercises,placement}.json`.
- `ContentSeeder` runs at app start; re-seeds when `CONTENT_VERSION` changes (settings key
  `content_version`). Content tables: `lesson`, `vocabulary`, `exercise`.
- **CEFR bundle (contentVersion 2):** 24 lessons (4 per level A1–C2), 144 exercises (6/lesson,
  4 types), 144 vocabulary entries (6/lesson), placement = 18 questions in six graded bands of 3
  (A1→C2, ordered by difficulty). Grammar points per level follow the British Council / EQUALS
  Core Inventory grammar tables (verified against examenglish.com/CEFR, Oct 2026):
  e.g. B1 = 2nd/3rd conditional + reported speech + simple passive; C1 = inversion + mixed
  conditionals + modals in the past; C2 = nuance/precision vocabulary.
- Placement scoring is **band-based** (`ScorePlacementUseCase`): walks bands bottom-up, band
  passes at ≥2/3 with ≥60% cumulative accuracy, stops at first failed band, floor A1.
  Question `level` tags in placement.json are documentation; scoring uses array order + `bandSize=3`.
- IDs are **stable dotted strings** (`a1.airport.word.passport`, `placement.q01`) — never renumber.
- `exercise.payloadJson` stores the authored JSON verbatim; `ContentParser.exerciseFromPayload`
  maps it to the sealed `Exercise`. Unknown types are skipped, never crash a lesson.
- Adding content = JSON only (no Kotlin changes), per technical spec §61/§63.
- Supported exercise types: `multiple_choice`, `fill_blank`, `translation`, `listening`
  (`speaking` parses but is suspended).

## 6. Design system rules (from design.md)

- **Never hardcode colors/spacings/radii**: use `MaterialTheme.colorScheme`, `LocalAppExtendedColors`
  (success/warning/info), `AppSpacing`, `MaterialTheme.shapes`.
- Root layout is **forced RTL**; all English content goes through `EnglishText` / `AnswerField`
  (which re-assert LTR). Never put raw English in an RTL layout.
- Feedback is never color-only: always icon + text (`CorrectBanner`/`IncorrectBanner`).
- Touch targets ≥ 48dp (`TouchTargetMin`), primary buttons 48dp high, cards 16dp radius.
- Every feature screen needs empty/loading/error states (design.md §37–39); offline errors must
  never claim "no internet" — the app simply has no internet.
- Streak must never visually outweigh learning progress (design.md §25).

## 7. Security posture

- Exactly one permission (`INTERNET`, billing-only). No other network code paths: grep for
  `retrofit`/`okhttp`/`URL(` in app sources should stay empty — billing SDKs are the exception.
- No secrets in the repo: store keys and signing config live in untracked `keystore.properties`.
- Data is non-sensitive learning progress; stored unencrypted in app-private storage.
- If sync ever ships: add Network Security Config, certificate pinning decision, and an ADR first.

## 8. Testing

- Unit tests (JUnit, run with `:app:testDebugUnitTest`): `ReviewSchedulerTest`,
  `DomainEngineTest` (mastery/answer checking/planner), `TimeUtilTest` (streaks),
  `EntitlementPolicyTest` (trial/subscription gating).
- Instrumented test files are still the Android Studio templates (not maintained).
- When you change engine logic, extend these tests first; UI changes rely on manual verification
  (no emulator CI in this environment).

## 9. Conventions & Definition of Done

1. Persian UI strings live inline in composables (single-locale MVP; extraction to
   `strings.xml` is open design debt — do it when adding a second locale).
2. Epoch millis (`Long`) for time — `java.time` needs desugaring at minSdk 24.
3. A feature is done when: all three flavor builds + unit tests pass, empty/loading/error
   states exist, RTL/LTR verified, no hardcoded design tokens, no new permissions (billing's
   `INTERNET` is the only one), and this file updated if architecture or the suspended list changed.

## 10. Known design debt (accepted, tracked here)

- Myket has no real subscriptions: expiry is local and lost on reinstall — re-prove purchase
  through support if a user claims a lost subscription.
- `bazaarRsaKey` unset → Poolakey verification disabled; the build falls back to
  `SecurityCheck.Disable`. Set it in `keystore.properties` before publishing to Bazaar.
- Room `version = 1`, `exportSchema = false` — the first schema change needs migrations + schema export.
- Due counts capture `now` at collection time; a long-running session won't see newly-due items
  until the ViewModel is recreated.
- `studied seconds` are credited on screen close/finish (best effort), not via a foreground timer.
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
