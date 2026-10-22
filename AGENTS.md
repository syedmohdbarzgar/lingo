# AGENTS.md — Freebuff English Learning App (org.token.english)

> این سند «حقیقت مرجع» و نقطهٔ ورود است. هستهٔ یادگیری **آفلاین-محور** است و باید همیشه بدون
> اینترنت کار کند؛ فقط چیزهایی که به **زیرساخت بک‌اند/سرور** نیاز دارند معلق می‌مانند.

## Where the detail lives

The root file is deliberately short; the deep material is in `docs/`:

- [`docs/learner-model.md`](docs/learner-model.md) — engines, the adaptive layer, the remediation
  bar, the three skill tracks.
- [`docs/content-pipeline.md`](docs/content-pipeline.md) — the JSON bundle, `validateContent`,
  every authoring rule; [`docs/CHANGELOG-content.md`](docs/CHANGELOG-content.md) — version history.
- [`docs/billing.md`](docs/billing.md) — subscription × 3 marketplaces, the companion grant, storefront
  wording; [`docs/connectivity.md`](docs/connectivity.md) — the offline rule and permission tables.
- [`docs/release.md`](docs/release.md) — release ritual and toolchain facts;
  [`docs/testing.md`](docs/testing.md) — the test inventory; [`docs/debt.md`](docs/debt.md) — debt.

## Always / Ask first / Never

- **Always:** run the fast gates before calling work done (below); keep the core working offline;
  route English through `EnglishText`/`AnswerField`; add a test with engine logic changes; bump
  `contentVersion` in all five JSON files together on a content change.
- **Ask first:** anything on the *suspended* list, a new network feature/permission, a schema
  migration, or a product decision (trial length, gating).
- **Never:** commit secrets (`keystore.properties`, `*.jks`); hardcode design tokens; put raw English
  in the RTL layout; `runCatching { }` around a suspend call; apply `org.jetbrains.kotlin.android`;
  re-run a `scripts/archive/` batch; add a backend-dependent feature unconfirmed.

## 1. What this project is

An offline-first Android app for Persian speakers learning English (CEFR A1–C2): daily lessons →
exercises → spaced-repetition review → progress. Specs in `files/` are **design intent, not proof**.

## 2. Build & verify

**The everyday loop is tests, not APKs**, and the learning core must work offline:

```bash
./gradlew :app:testBazaarDebugUnitTest   # unit tests + typechecks main sources (the fast gate)
./gradlew validateContent --rerun        # content gate (also runs in preBuild)
./gradlew :app:lintBazaarDebug           # lint (also a CI job)
scripts/install_debug.sh                 # builds+installs only when a device is attached (else no-op)
```

Release points, the fail-closed gates, the toolchain facts and the CI shape are in
[`docs/release.md`](docs/release.md); the Definition of Done is §9 below.

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
  (planner budget, focused sessions, the unified remediation bar, the three skill tracks) is in
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
- Touch targets ≥ 48dp, primary buttons 48dp high, cards 16dp radius, and **cards always span the
  available width** (`AppCard` fills it itself). Streak must never outweigh learning progress.
- Every feature screen needs empty/loading/error states; a local failure must never be blamed on the
  network — only a genuinely online action may show a connectivity error.

## 7. Security posture

- `INTERNET` is billing-only today; a new online feature may add permissions, but each must be
documented in [`docs/connectivity.md`](docs/connectivity.md). No secrets in the repo — signing
config and store keys live in untracked `keystore.properties`. Data is non-sensitive learning
progress, unencrypted in app-private storage. If sync ever ships: Network Security Config, a
pinning decision and an ADR first.

## 8. Testing

The fast gate is `./gradlew :app:testBazaarDebugUnitTest` (371 tests / 52 classes as of the v15
N-4/N-5/B-12/B-6 pass); the full inventory and ViewModel/fake conventions are in
[`docs/testing.md`](docs/testing.md). Content tests read assets through `File`, so run them with
`--rerun`. There is no instrumented suite (the template was removed, not kept as a false signal).

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
local trial, Room v5 migration rules, and more) live in [`docs/debt.md`](docs/debt.md). Read it first.
