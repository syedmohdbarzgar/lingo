# Known design debt (accepted, tracked here)

> Moved out of the root `AGENTS.md` (checklist v10). Everything here is a deliberate,
> accepted trade-off, not a bug to fix silently.

- **Settled (B-1): mastery by dimension now crosses sessions (Room v6).** `dimension_mastery`
  stores the per-dimension fold as answers arrive, and a lesson session seeds its `MasteryProfile`
  from those rows before the first exercise — so the selector and the weakest-dimension read work
  from observed evidence instead of only after an in-session answer. The table arrives empty via
  `5->6` (learner state: survives re-seeding, cleared only by reset), rows naming a dimension this
  build does not know are ignored on read, and `DatabaseUpgradeTest` executes the step against a
  real SQLite engine. An untouched dimension is still *absent*, never 0 — "no evidence" keeps its
  meaning.
- **The companion key was pinned from a sideloaded build — since verified against the store
  build (N-1, Oct 2026).** `CompanionApp.EXPECTED_SIGNING_SHA256` holds the Zaribar 3.4.10
  certificate (`CN=wolfandmoon`), read from the sideloaded APK on the device. The comparison
  itself is done: the APK Cafe Bazaar publishes for `org.token.zaribar` (versionCode 16, pulled
  through the store's download-info API) carries the **same** V2 digest, so store installs pass
  the check. A future key change would still fail closed (no free access — never a bypass), so
  re-read the digest from the store build before each release — the procedure is in
  [`release.md`](release.md). The Myket/Play builds of Zaribar (if any) remain unverified: both
  stores' copies can only be pulled from a device that installed them there. **Settled (N-2):** where the Bazaar link lives. Every download button used to sit in the
  installed state (Home, paywall, settings), where it offered the learner what they already had; the
  link now belongs to the not-installed state, and one pure rule — `CompanionOffer.isOffered` —
  decides it for the paywall, the Settings subscription card and Home's free-route card, so a device
  holding the grant can never be shown the offer again.
- **The free trial was removed, but its DataStore keys remain.** The 7-day trial needed a local
  elapsed-time clock (`TrialClock`, its checkpoint fields and the once-a-minute write); removing the
  feature deleted the clock, the tick and the countdown UI, while `trialStartedAt` and the three
  `trial_*` preference keys stay in the persisted record untouched. That is deliberate: dropping the
  keys would mean a DataStore migration for a value no code reads any more, and an existing install
  must open exactly as before. **Nothing grants access from them** — precedence is
  SUBSCRIPTION → COMPANION_APP → LOCKED in `EntitlementPolicy`, and a fresh install is locked.
- Myket has no real subscriptions: expiry is local and re-derived from the owned purchase time —
  re-prove purchase through support if a user claims a lost subscription. A refund or revocation
  happens in the store and cannot be observed offline, so it is neither granted nor renewed: access
  is strictly bounded by the last reconciled expiry (`SubscriptionRecoveryTest` pins that a revoked
  purchase loses access once that mirror runs out). A server would be needed to revoke on demand.
- `bazaarRsaKey` unset → Poolakey verification disabled; the build falls back to
  `SecurityCheck.Disable`. Set it in `keystore.properties` before publishing to Bazaar (the
  Bazaar release tasks already refuse to assemble without it — same gate as the companion digest).
- Room is at `version = 6` with exported schemas (`app/schemas`). Schema changes must ship a
  migration **and** be added to `AppContainer.database` — Room throws at open time when a path from
  the installed version is missing, so a forgotten `addMigrations` crashes upgrading installs.
- Due counts capture `now` at collection time; a long-running session won't see newly-due items
  until the ViewModel is recreated.
- The daily reminder is pinned to 19:00 local (`DailyReminder.REMINDER_HOUR`); the time is not
  learner-configurable yet (checklist B-4).
- The ALMOST hint is suppressed for GRAMMAR items but cannot tell "one edit away from a *different*
  word" (A-2c/A-2b: `affect`/`effect`) from a typo without a dictionary — on a vocabulary target the
  learner is still told to check the spelling. Accepted: the banner shows the correct answer
  alongside it, so the word itself is never ambiguous.
- **Production grading is structural, not grammatical (B-10 residual).** A typed sentence passes
  when it carries every anchor in order and reuses ≥ 50% of one exemplar's words
  (`AnswerChecker.PRODUCTION_MIN_EXEMPLAR_OVERLAP`; `DocCodeConsistencyTest` pins this number to
  the code, checklist v15 B-12 — the doc had drifted to "60%"); a scramble that
  keeps the *anchors* in order, or a wrong **non-anchor** form (`goed`), can still pass, and a
  valid variant far from both authored exemplars is failed. The two named negatives (scramble at
  full overlap, wrong graded verb form) are already pinned in `ProductionRecallTest`; closing the
  rest needs more accepted variants per item (content) or a grammar judge (out of scope offline).
  Accepted for now.
- **Study time is credited by an interaction-based ticker (P7 rework, see
  [`learner-model.md`](learner-model.md)).** Each resumed lesson/review screen samples a tick every
  30 s and writes it only while the learner acted within the 4-minute idle cap
  (`StudyTimePolicy`); the old "credited on close, best effort" rule is gone. Residual trade-offs,
  **both accepted at v14 B-11 (user-confirmed)**: an unattended screen keeps earning for up to the
  4-minute cap before stopping (the cap stands as a product decision), and history earned under
  the old counting is left untouched — noted once in the next release
  ([`release.md`](release.md)).
- **Conversation practice ships its recogniser inside the APK.** The Vosk model is ~40 MB of
  assets (`scripts/fetch_vosk_model.sh`; not committed, but packaged), so the APK grows to roughly
  83 MB (measured on the unsigned `googlePlay` release APK, Oct 2026 — 67.6 MB of that is the model
  tree; the debug APKs are ~107 MB) and every install pays for a recogniser whether or not the
  learner ever opens the screen. The release gate in `app/build.gradle.kts` refuses to build
  `assemble*`/`bundle*` `Release` when that tree is fetched-but-missing, so a release cannot ship the
  dead screen by accident.
  Accepted for now because a downloadable model (Play Asset Delivery, a content package) needs the
  server infrastructure the connectivity policy keeps suspended. Alternatives when the size starts
  to hurt: split the model into (small) and (large) flavors, or ship it as a Play Asset Pack on the
  `googlePlay` flavor only. The recognition itself is speech-to-text on the model's vocabulary —
  it grades a whole sentence, not per-word pronunciation (see [`connectivity.md`](connectivity.md)).
  Two consequences are accepted on purpose: **a verdict is a text match, not a pronunciation score**
  («نزدیک» means the words were there, never that the sounds were right), and **a take the recogniser
  did not hear is not a language error** — an empty take is reported as «چیزی نشنیدم» and never
  recorded as a wrong answer. Teaching actual pronunciation needs phoneme-level scoring (a different
  engine, or a cloud service the connectivity policy keeps suspended).
- **The speech rate is one engine-wide constant.** `TtsAudioPlayer` sets 0.75× for every English
  utterance in the app (native 1.0× was reported as unintelligible on a first A1 ear). It is not a
  learner setting: a slower/faster control needs a preference, a settings row and a copy pass, and a
  half-done speed setting is worse than one predictable pace. Revisit when the settings screen gets a
  «سرعت صدا» row for both TTS and partner lines.
- **Recognition fairness: the engine half shipped, one device-level item did not (Oct 2026
  audit).** The scorer now grades every accepted answer, folds recogniser homophones, forgives one
  edit on a long word, strips `please`/`thank you`, rejects a take that repeats the partner, and the
  recogniser is constrained to the turn's own sentences (`ConversationScorer.speechGrammar` via
  `SpeechBus.grammar`). The service now ends a take by itself after 4 s of silence once it has
  heard something (long enough that a three-second pause for thought still keeps the sentence), and a take invalidated by a STOP or a newer START while the model loads can no
  longer open a second recorder (the take-generation counter in `SpeechRecognitionService`).
  **Still deliberately not done:** `VoskManager.release()` on screen exit — ~300 MB of native model
  memory lives until process death, and releasing it pays a reload on the next visit, so it needs a
  device measurement rather than a guess. The backup rule now excludes `filesDir/vosk/` — that one
  was data loss, not a trade-off: the extracted model alone exceeded Auto Backup's 25 MB cap and
  cancelled the whole backup.
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
- Persian UI strings are inline in composables instead of `strings.xml` (single-locale MVP) —
  extract when a second locale lands.
