# Connectivity policy — what is ACTIVE vs SUSPENDED

> Moved out of the root `AGENTS.md` (checklist v10). Billing (the one online surface)
> lives in [`docs/billing.md`](billing.md).

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
`lintBazaarDebug` fails with `MissingPermission`), `RECEIVE_BOOT_COMPLETED` (the reminder is re-armed
after a reboot), `RECORD_AUDIO`, `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_MICROPHONE` (the practice
service's `microphone` type — targetSdk 37 requires both the declaration and the type passed to
`startForeground`, and drops the type silently if only one is present) and
`DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (system-generated, not a real app permission).

`RECORD_AUDIO` serves **two local-only** features, and neither ever uploads audio:

1. **A voice note inside a lesson speaking item** (see `docs/content-pipeline.md`): the take is
   written to the app cache dir and played back against the on-device TTS model. The card asks for
   the mic itself, so no other screen triggers the prompt, and a refusal downgrades the item to
   listen-and-compare instead of breaking the lesson.
2. **Conversation practice** (`feature/conversation/`): an on-device Vosk recogniser transcribes the
   learner's take and a pure engine (`domain/engine/ConversationScorer.kt`) grades it. The model is
   the only bundled binary — `assets/vosk/vosk-model-small-en-us-0.15`, fetched by
   `scripts/fetch_vosk_model.sh` and copied to internal storage on first use (a `uuid` marker
   certifies the copy). No recogniser service, no network call: with the phone in airplane mode the
   feature works exactly the same, and a device without the asset reports «مدل تشخیص گفتار روی این
   نسخه نصب نیست» instead of failing silently. Recording runs in a `microphone`-typed foreground
   service so a whole sentence survives a screen-off take. The three-flavor release-prep pass (P3, Oct 2026) confirmed this set in each merged
manifest and that each APK dex contains only its own billing SDK (`ir/cafebazaar/poolakey`,
`ir/myket/billingclient`, `com/android/billingclient` — zero cross-flavor leakage).

## Active (must keep working offline)

- Bundled A1–C2 lessons, vocabulary, exercises, placement test (assets/content)
- Lesson flow, answer checking, skill mastery
- Spaced-repetition review queue (SM-2-lite)
- Daily plan, daily goal, streaks, progress stats
- Listening via **on-device Android TTS** (no audio files, no network)
- Conversation practice: say lesson sentences back, graded by the **on-device Vosk recogniser**
  (`feature/conversation/`, model bundled as an asset — no server, no recogniser API)
- Vocabulary browser + search
- Theme (system/light/dark), sound toggle, daily goal, reset
- Subscription purchase/restore via the store (the only online surface today)
- Opening the app's own page in that store — Settings → «درباره و قوانین» → «ارتباط با ناشر»
  (`core/support/StorePage.kt`). A plain `ACTION_VIEW` to the flavor's documented deep link with the
  public store page as fallback: the app itself makes no request, needs no permission, and nothing
  in the learning core depends on it.

## Suspended — needs backend/server infrastructure (do NOT implement yet)

These are blocked by infrastructure, not by the offline rule. Building them means standing up and
operating a service first, so they stay out of scope until that exists.

| Feature | Why it's suspended | Where the seam is |
|---|---|---|
| AI conversation / AI feedback | Needs a backend (model hosting + API) | Future: `ConversationEngine` interface (technical spec §38) |
| Cloud sync, auth, backup | Needs a backend + accounts | `id/updatedAt/syncState` fields are **not** added yet; keep repositories local-only |
| Downloadable content packages | Needs a server + signed content validation | `ContentSeeder` is keyed by the JSON `contentVersion` — the upgrade hook already exists |
| Analytics upload | Needs a collection endpoint **and** a privacy decision (opt-in, minimal, anonymous) | No analytics module exists. Intentional. |

If a task requires one of these, **stop and confirm with the user first**.

## Unblocked by the connectivity policy — not built yet

These do **not** need a backend, so they may be built whenever they are prioritised:

| Feature | State | Where the app hooks in |
|---|---|---|
| Pronunciation evaluation (per-word scoring) | Recogniser built; scoring is still sentence-level | `ConversationScorer` grades a whole take — per-word feedback needs a phoneme/duration signal Vosk does not give |
| Remote crash reporting | Not implemented | Pick a provider deliberately — it is a network + privacy decision, not a default |

Completed and removed from this list: B1–C2 content (shipped in the **v5 bundle** — 6 lessons per
level A1–B2, 7 for C1/C2) and daily review reminders (now **active**, `reminder/DailyReminder.kt`).
