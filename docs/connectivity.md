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
after a reboot, at the hour the learner chose rather than the shipped 19:00 — B-4), `RECORD_AUDIO`, `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_MICROPHONE` (the practice
service's `microphone` type — targetSdk 37 requires both the declaration and the type passed to
`startForeground`, and drops the type silently if only one is present; the type is passed from
**API 30** up, the version the constant was added in, so an Android 10 device gets 0 = "use the
manifest's declaration" instead of a type number its platform does not define) and
`DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (system-generated, not a real app permission).

The reminder's copy follows the data it can actually see (`DailyReminder.copyFor`): it states the
due count, says there is nothing to review when the count is zero, and claims neither when the count
cannot be read. A local notification is the one screen the app shows unasked, so it is also the one
place a reassuring sentence would be a lie the learner cannot check. Its count is a local Room read
no network — the same rule the rest of the learning core lives by.

`RECORD_AUDIO` serves **two local-only** surfaces, and neither ever uploads audio:

1. **The lesson's `speaking` item** (see `docs/content-pipeline.md`): the take goes through the same
   on-device recogniser as a dialogue turn and is graded by the same engine. The same PCM is also
   written to a WAV in the cache dir, so the learner can hear back the exact words that were graded;
   that file is deleted when the step or the screen is left, and the next take overwrites it. The card
   asks for the mic itself, so no other screen triggers the prompt. A device that cannot hear a take
   (no model, no mic, refusal) downgrades the item to the older listen-and-compare card, which credits
   nothing.
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

**What the microphone path keeps, and what deletes it** (the P0 privacy checklist asks this
explicitly, so here is the answer read off the code rather than the store copy):

| Artefact | Where it lives | Lifetime |
|---|---|---|
| The raw PCM take (conversation practice) | never written — read from `AudioRecord`, fed to the in-process `Recognizer`, then dropped | the take |
| The recognised text | memory only (`SpeechBus`, the screen state) | the take / the turn |
| The verdict of a turn | `conversation_turn`-style evidence row: lesson id, exercise id, verdict, a `spoken` flag, a timestamp — **no transcript** | reset |
| The lesson's spoken take (audio) | `cacheDir/speech_take.wav` — the PCM the recogniser heard | deleted when the step is left (`SpeechBus.reset`), replaced by the next take, and the OS may clear the cache |
| A voice note an **older build** left behind | `cacheDir/voice_take.m4a` | nothing writes it any more; reset still deletes it, and the OS may clear it |
| The extracted Vosk model | `filesDir/vosk/…` | a copy of the APK asset — not user data, excluded from backup, never deleted |

Nothing on that path calls the network: the app has **no HTTP client of its
own** (only the billing SDKs talk to a store), so an upload was never a
one-line change away. No learner content — no transcript, no typed answer, no
voice file path — is written to `Log`; the log lines in `feature/` name the
operation that failed and nothing else.

Deletion is `Settings → «پاک‌سازی پیشرفت و شروع دوباره»`, and it now covers the
four places learner data actually lives: the Room tables
(`ProgressRepository.reset()` + `KnowledgeRepository.reset()`), the finished
dialogues (`ConversationRepository.reset()` — a set in the settings preferences
file, which the Room wipe could not reach and which used to keep the lesson gate
open for lessons the wipe had just erased), and the recorded take
(`VoiceNote.discardTake()`). The settings themselves (level, theme, goal, sound,
reminder) and the subscription mirror survive: the learner asked to restart the
course, not to reset the app. `SettingsViewModelTest` pins both halves.

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
  in the learning core depends on it. The same page is the **support path out of a critical error**:
  Home's and Lesson's failure states offer «تلاش دوباره» first and «ارتباط با پشتیبانی» second, so a
  learner whose content will not load is never left with a dead end (checklist P1-7).

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
