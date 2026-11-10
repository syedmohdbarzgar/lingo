# Known design debt (accepted, tracked here)

> Moved out of the root `AGENTS.md` (checklist v10). Everything here is a deliberate,
> accepted trade-off, not a bug to fix silently.

- **Settled (Phase 2): the step resumes on the question, not the index.** `lesson_attempt` (Room v9)
  records every graded answer with its step, and `StageResume` opens a reopened step on the first
  question with no recorded answer; `lesson_state.stageCompletedAt` records when each step was
  finished (first completion wins, so a revisit cannot rewrite it). What is still *not* stored is a
  rendered answer sheet: "Previous" reopens a completed step from its first exercise rather than
  replaying the answers given inside it — the step's completion and its recorded answers are kept,
  but the screen does not pre-fill them, because a completed step is shown to *re-read*, and a
  finished step's answers would tempt a re-tap that the claim (`question_attempt`) would refuse.
  A revisit's answer is claimed like any other: re-answering the *same* exercise on the same day
  writes nothing to mastery, and `firstTries` refuses to recount it as a first pass.

- **`question_attempt` is the claim, `lesson_attempt` is the history (Phase 1 + 2, Room v9).**
  `question_attempt` keeps one row per exercise, overwritten: its only question is "has this
  exercise already moved the learner today", and a second row per exercise would have to be summed
  anyway. The history lives in `lesson_attempt`, append-only and unbounded — one row per answer for
  the life of the install, since a learner answering 30 questions a day is ~11k rows a year and every
  read is `WHERE lessonId = ?` against an index-free table that size handles fine. Revisit if a
  screen ever reads the log across all lessons (a "what did I get wrong yesterday" view), which
  would want an index on `at` and probably a retention window.

- **One graded result is one transaction; the dimension fold stays outside it (checklist 0.1).**
  Five flows write several learner rows per result and commit them as one Room transaction through
  `ProgressRepository.inTransaction`: a lesson answer (the claim, the skill meter, the curriculum
  nodes and the answer's history), a review grade (the card's next schedule, the attempt log and the
  meter), a conversation turn (its claim, the graph and the track — plus, on the last one, the
  dialogue's finished mark), finishing a lesson (the
  completion stamp, its words entering the review queue and the cursor reset) and the placement test
  (one fold per answer into its skill meter). A crash therefore leaves the result undone rather than
  half-recorded — the question still unanswered, the card still due, the turn still unclaimed, the
  lesson still unfinished with its words unqueued, the profile uncalibrated rather than partly so
  (`AnswerTransactionTest`, `EvidenceTransactionTest`, `LessonCompletionTransactionTest` and
  `PlacementSeedTransactionTest` prove the rollback on Room, and `TransactionCoverageTest` fails when
  a use case writes several tables without the seam). The follow-up write that folds the answer into `dimension_mastery` is deliberately *not* in
  that transaction: it is session input for the next session's selector rather than evidence about
  what was learned, and losing it costs
  one stale seed — the next answer in that dimension folds again — while a contradiction between the
  model and the history cannot happen. Folding it in would mean the answer's transaction keys a
  dimension, which the ViewModel owns, not the repository.

- **Settled (B-1): mastery by dimension now crosses sessions (Room v6).** `dimension_mastery`
  stores the per-dimension fold as answers arrive, and a lesson session seeds its `MasteryProfile`
  from those rows before the first exercise — so the selector and the weakest-dimension read work
  from observed evidence instead of only after an in-session answer. The table arrives empty via
  `5->6` (learner state: survives re-seeding, cleared only by reset), rows naming a dimension this
  build does not know are ignored on read, and `DatabaseUpgradeTest` executes the step against a
  real SQLite engine. An untouched dimension is still *absent*, never 0 — "no evidence" keeps its
  meaning.
- **Two nodes can never claim *long-term* mastery (Room v7).** `isLongTermMastered` requires two
  confirming exercise formats, and `phonology.spelling-aloud` / `phonology.number-sounds` are only
  exercised through translation in the current bundle, so their mask can never hold a second bit.
  They are still *secured* (the planner's bar) and fully learnable — only the longer claim stays out
  of reach until their lessons gain a second format. Recorded here so it reads as a content gap,
  not a gate bug. The same migration leaves `firstCorrectAt` NULL on legacy rows instead of
  inventing a date; each row fills it on its next correct answer.
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
- Room is at `version = 10` (`lesson_attempt`, `question_attempt`, `conversation_done`) with exported schemas
  (`app/schemas`). Schema changes must ship a
  migration; registration is no longer a step anyone can forget, because `AppContainer` takes
  `AppDatabase.ALL_MIGRATIONS` as a whole (`RoomMigrationTest` fails if a hand-picked list comes
  back). That was not always so: v60 listed the steps one by one, v7→v8 was added to the database
  and not to the list, and every *upgrading* install — not a fresh one — crashed at open time with
  «A migration from 7 to 8 was required but not found». The lesson: a migration path is only
  exercised by an install that already holds data.
- **The review screen re-reads on resume, not on a ticker.** Home's due count has had a minute
  ticker since the fixed-`now` pass, and it rebuilds only when the number actually differs. The
  review screen's two reads (the card queue and `GetDueKnowledgeReviewsUseCase`) run in `init` and
  now again whenever the screen is resumed — returning from the focused drill, or from another tab —
  which is when they can actually have gone stale. Three limits are deliberate. A resume never
  touches a **session in progress** (a read that lands late must not yank the learner back to card 0,
  and the graph half is not grafted onto a running queue), and it never shows a spinner — the current
  content stays until the fresh read lands. And a screen left continuously in the foreground does
  **not** re-read: a queue that reorders and regrows under the learner's eyes while they read it is
  worse than a count that waits for the next visit. The residual follows from that: a card that
  becomes due while the summary is on screen appears when the screen is next opened, not before.
  The finished-session screen used to be the other dead end — the cards were done, the graph still
  owed reviews, and the list was two phases back — so `ReviewEvent.ShowDue` (and a «مرور هدفمند»
  action shown only when nodes are actually owed) returns to the summary.
- **Settled (B-4): the reminder hour is a setting.** 19:00 is only the default
  (`AppSettings.DEFAULT_REMINDER_HOUR`); Settings has a «ساعت یادآوری» row that opens a
  `TimePicker`, and the stored value is `daily_reminder_hour` in the settings record. The alarm is
  scheduled from the record in one place — `EnglishApp`'s settings collector — so turning it on and
  changing the hour both reach the alarm, and the post-reboot path re-reads the same hour instead
  of the constant. Out-of-range values are clamped in the repository and again in
  `DailyReminder.nextReminderMillis`, because `Calendar` accepts hour 25 by rolling it into the
  next day (`DailyReminderTest` pins both). Still not configurable, deliberately: the *minute*
  (a reminder at 19:07 buys nothing a whole hour does not) and a per-weekday schedule (reminders
  are daily by definition here).
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
  tree; the three-ABI debug APKs are ~98 MiB since the Phase-4 ABI trimming, before that ~107 MB)
  and every install pays for a recogniser whether or not the learner ever opens the screen. Three
  ABIs of `libvosk.so` plus JNA are ~30 MB of that — a release bundle with ABI splits would deliver
  one, and is the first thing to reach for if content growth pushes the fat APK past a store's
  100 MB ceiling. Also still open from Phase 4: the 16 KB alignment is verified, but the
  recogniser itself (model load, a spoken take, a verdict) has never run on hardware from this
  checkout — `scripts/verify_device.sh` is the check to run on the next attached phone. The release gate in `app/build.gradle.kts` refuses to build
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
  `SpeechBus.grammar`). The service now ends a take by itself: ~1.2 s after a transcript that already
  *is* one of the turn's accepted answers (`SpeechBus.acceptedAnswer`, the scorer's own verdict), and
  4 s of silence otherwise once it has heard something (long enough that a three-second pause for
  thought still keeps the sentence). A take invalidated by a STOP or a newer START while the model
  loads can no longer open a second recorder, and a take the learner ended still publishes
  `isListening = false` — both from the take-generation counter in `SpeechRecognitionService` (which
  owns the mic) and the newest-START rule (which owns the screen state). The phone check that found
  the latter bug: «پایان پاسخ» closed the mic but the screen sat on «در حال صحبت…» forever.
  **Still deliberately not done:** `VoskManager.release()` on screen exit — ~300 MB of native model
  memory lives until process death, and releasing it pays a reload on the next visit, so it needs a
  device measurement rather than a guess. The backup rule now excludes `filesDir/vosk/` — that one
  was data loss, not a trade-off: the extracted model alone exceeded Auto Backup's 25 MB cap and
  cancelled the whole backup.
- **A database Room cannot open is rebuilt only on the learner's word (content recovery, P1).**
  The seeding path fails loudly instead of crashing: every failure below `AppContainer.seedContent()`
  becomes `ContentStatus.FAILED`, so the lesson, placement and conversation screens render a named,
  re-seeding error rather than a spinner nobody can leave (`ContentRecoveryTest` pins it with a path
  SQLite cannot open). The failure is then *classified* — `ContentFailure` — because the two cases
  need different answers. An unreadable asset or a parse that threw is `OTHER`: the retry is the right
  move and a rebuild would throw away a readable progress file for nothing. Room refusing to open
  `english.db` at all is `DATABASE_UNREADABLE`: there the retry is honest but terminal, so Home — and
  only Home — offers a third action, «ساخت دوبارهٔ پایگاه داده», behind a confirmation that names what
  is lost.
  What the repair does, and does not do: it **moves** the file (with its `-wal`/`-shm` companions)
  into `noBackupFilesDir/corrupt-database/` and restarts the process so Room builds a fresh database —
  every repository holds a DAO from the closed handle, so re-wiring them in place would be a second,
  larger change. It never deletes: the damaged file is off the backup path (it neither travels to
  another phone nor counts against the 25 MB quota) and still reachable by support with `run-as`. If
  nothing could be moved, `repairUnreadableDatabase()` returns false, the app is *not* restarted, and
  the card says the file could not be set aside — a rebuild that silently claims success is worse than
  one that admits it failed. A relaunch that cannot be queued (no launcher activity) is the third
  outcome, and it is neither of those: the file *was* rebuilt, so the card drops the retry and the
  rebuild — both would be dead buttons once this process's handle is closed and the file is gone —
  and asks the learner to close and reopen the app, which is the same promise the confirmation made. The accepted trade-off is the one the confirmation spells out: the new
  database is empty, so completed lessons, review schedules, streak and answered history do not come
  back, and the lessons and words are re-seeded from the bundle. The alternative — `fallbackToDestructiveMigration`
  or an automatic rebuild — was rejected because it would destroy progress without asking, and a
  backup that restores a broken file would then loop. One test-side limit: Robolectric opens a garbage
  `english.db` as a fresh database, so a *corrupt file* cannot be reproduced in the JVM suite; what the
  suite proves is the path it cannot open, the classification, the move-aside and the successful seed
  of the container that follows. Real corruption (bad restore, killed write, full disk) still needs a
  device, where the expected behaviour is this card and this confirmation.
- **Closed: the finished-dialogue set moved out of the excluded file (Room v9→v10).** Auto Backup
  excludes `datastore/` from cloud backup and device-to-device transfer
  (`backup_rules.xml` for API < 31, `data_extraction_rules.xml` for 31+), and that exclusion stays —
  `app_settings.preferences_pb` carries the subscription mirror and the legacy trial checkpoint, and
  a restored entitlement would be a purchase that never happened. The mistake was living there:
  `conversations_done` is learner progress, not an entitlement, so a restore kept every half of the
  same event (the completed lesson in `lesson_state`, its answers in `lesson_attempt`, the turns'
  claims in `question_attempt`) and dropped only the “this dialogue is done” flag — Home then owed a
  dialogue the learner had already practised and `canOpenLesson` kept the next lesson shut. The mark
  now lives in its own table, `conversation_done`, restored with the rest of the progress.
  Two consequences worth knowing. First, the mark is written **inside the final turn's transaction**
  (`RecordConversationTurnUseCase`), so a crash can no longer leave the turn claimed and the dialogue
  unmarked. Second, the migration is additive and empty: the old preference key is read **once** on
  the first read of the stream (`ConversationRepositoryImpl.importLegacyOnce`) and then removed, so an
  upgrading install keeps every dialogue it has finished. That import is the residual trade-off: the
  rows carry no date (there is no honest one to carry), `reset()` clears the legacy key with the
  table so a wipe cannot be undone by the import, and a failed import retries on the next read
  rather than blocking the screen — the table, not the old key, is the authority. The exclusion must
  still never be loosened to "fix" a future progress bug: a restored subscription is worse than a
  re-practised sentence.
- **The splash hand-off is ported to `overrideActivityTransition`; the visual comparison is still
  owed on a phone.** Until this pass the fade was declared only by `overridePendingTransition(R.anim.fade_in,
  R.anim.fade_out)` in the finishing splash, which API 34 deprecated. The replacement is not a rename:
  its *open* override belongs to the activity being **entered**, so the pair is now declared by
  `MainActivity.onCreate` (`OVERRIDE_TRANSITION_OPEN`: enter for Home, exit for the splash) and only
  below 34 does the splash still declare it — doing both would put two overrides on one transition.
  The decision lives in `SplashHandOff.usesNewApi`, and `SplashHandOffTest` pins the 33/34 boundary,
  which is the half a compiler cannot check. It is a boolean rather than an enum because the call
  sites have to *guard* the new API and lint only recognises a guard it can see through: a plain
  `sdk >= 34` helper produced two findings (`NewApi` on the call, `InlinedApi` on the constant) from
  correct code, and `@ChecksSdkIntAtLeast(parameter = 0, api = 34)` is the contract that makes the
  helper readable. The alternative — an `@Suppress("NewApi")` or a budget entry — would have hidden
  a real check for a self-inflicted reason; the annotation leaves the check on. Verified: the lint
  report has no `NewApi`/`InlinedApi` entry and the budget still shows its seven.
  What is **not** proven yet: the pixels. The fade is the frame the device run already had to fix (the
  black gap between the starting window and Compose's first frame), and no host-JVM test can watch a
  transition, so the old behaviour was kept as the fallback rather than removed. The check still owed
  is the one this entry used to prescribe: record the hand-off with `scripts/install_debug.sh` on an
  attached phone before and after, and compare for a black frame or a dropped fade on an API 34+
  device (some OEM builds ignore an open override and fall back to the platform's default animation —
  acceptable, but it has to be *seen* rather than assumed). The pre-34 call is kept under
  `@Suppress("DEPRECATION")` behind the version guard; that suppression is the version gate itself,
  not a way to silence a finding.
  A forced full recompile of the main sources (`compileBazaarDebugKotlin --rerun`) now reports **no
  Kotlin warnings at all** — the last one was this deprecation — which is a measurement, not a claim.
- **A review card is recall evidence, never a format confirmation (review→graph pass).**
  `SubmitReviewUseCase` now folds a grade into the reviewed word's curriculum node inside the same
  transaction, so the graph finally sees the practice a mature learner spends most of their time on.
  What it deliberately does **not** do is set a bit in `distinctTypesCorrect`: the mask is the
  *transfer* claim (`KnowledgeGates.MIN_FORMATS_TO_SECURE`), and a card is one word recalled, not an
  exercise shape. A `REVIEW` format would have let a single card buy the second format the gate
  wants — "proven two ways" about one way — so `recordRecall` passes `format = null` and the engine
  records mastery and the node's own schedule only. Consequence, accepted: a learner who only
  reviews can raise a node's mastery but can never make it *secured*; the two formats must still
  come from lesson exercises. The other direction is now served (review→graph, second pass):
  `GetDueKnowledgeReviewsUseCase` lists the due, unsecured nodes and the review screen opens each
  one as the focused drill. Two choices are accepted debt rather than intended design. First, the
  queue is still `review_item` over vocabulary — a due node is **not** queued as a card, so serving it
  costs one tap into its lesson and the node's exercises never appear inside the card session; a
  queue that mixes the two would need a card type that carries an exercise. Second, the read probes
  the focused drill for at most `MAX_PROBES` (8) of the most overdue nodes and shows `DEFAULT_LIMIT`
  (3) rows: each probe re-reads the graph through `GetFocusPlanUseCase`, so the bound is what keeps a
  tab switch a short read rather than a scan of the bundle. A learner with more than three due nodes
  sees three — the rest wait, and they are the *most* overdue that can actually be drilled.
- **A card credits its lesson's vocabulary node(s), which is two for three lessons.** Now that a card
  is attributed by type alone (`KnowledgeEvidence.itemsForCard` — see
  [`docs/learner-model.md`](learner-model.md), which also explains why the by-skill and whole-lesson
  passes are wrong here), the remaining granularity question is decided by the content graph: nodes
  are **topic**-granular (6–8 words each), a word carries no node id, and — since v65 gave the last 35
  their node — all 192 lessons own vocabulary nodes. 189 own exactly one, so a card moves exactly its
  topic. `c1.phrasal-idioms`,
  `c2.nuance` and `c2.collocation` own two (`vocab.phrasal-verbs` + `vocab.idioms-and-figurative`,
  `vocab.semantic-precision` + `vocab.connotation`, `vocab.collocation-strength` +
  `vocab.delexical-verbs`), so reviewing «give up» also moves `vocab.idioms-and-figurative`. That is a
  real over-credit, bounded to those three lessons and to nodes of the same type; it is accepted
  because the alternative is a per-word node id in the bundle (1154 words), and because the graph
  itself says those lessons teach both topics. The narrower fix would be a `nodeId` on a word, which
  is a schema change to `vocabulary.json` and earns its own content version.
- **The error taxonomy reads the log, not a stored label (roadmap).** `lesson_attempt` records
  which exercise was asked and whether it was right; it does **not** record the skill, so
  `GetMissProfileUseCase` joins the id to the authored exercise and classifies by
  `AnswerChecker.skillOf`. Three limits follow, all accepted. First, **a review card can only be
  labelled vocabulary or grammar**: a card carries its item's content type and no exercise, so
  `MissTaxonomy.classify(cards)` can never produce comprehension or production however the card goes.
  That is a coarse label, not a wrong one (a recall card does test a word or a rule), and the
  alternative was worse — keeping review out meant the mix stopped moving once a learner's practice
  moved to the queue, which is what a mature learner's week looks like. The finer fix is a card type
  that carries its exercise (the same thing the focused drill lacks). Second, the profile is a
  **window** (`MissTaxonomy.WINDOW = 200` newest answers) rather than the archive, because the point
  is what to fix now; lesson answers and cards are **pooled and then trimmed** to that window, so a
  burst of review cannot crowd the lessons out (or the reverse) the way two windows would. The window
  is a `LIMIT` over an unindexed `lesson_attempt` scan plus a `review_attempt ⋈ review_item` read —
  both tables are small by design (tens of thousands of rows at most, see the `lesson_attempt` entry
  above), so a single-screen read is fine. Third, the use case **caches each lesson's exercises for
  the process** (bounded at 64 lessons): content is static once seeded, and the read runs on every
  mastery emission — without it one answer would re-parse the bundle's exercises for every lesson in
  the window. A re-seed mid-process would leave stale ids in that cache; the effect is a miss that
  silently is not classified, never a wrong label. **The adaptive half now ships (miss-driven plan
  pass)**: the planner reorders within one priority by the weakest category and names the gap on the
  action that answers it (see [`learner-model.md`](learner-model.md)). What it still cannot do is
  change *difficulty*, which is what the roadmap item literally asked for: an exercise carries no
  authored difficulty, so "harder vocabulary" has nothing to sort by, and the honest substitute is
  urgency (which node, in which order) rather than level. That is why the steering stays inside the
  plan and never touches exercise selection — until the bundle carries a difficulty, a "harder" item
  would be a guess dressed as adaptation. Two smaller limits are accepted with it: the ordering only
  bites where two actions share a priority (a broken prerequisite still wins, by design), and the
  profile read on Home's plan path is best-effort — a failure logs nothing and steers nothing.
- **The lint budget is a decision list, not a clean zero (P2 triage, Nov 2026).** Seven warnings are
  accepted, and each one is written into `scripts/lint_budget.mjs` **with its reason**; the CI `lint`
  job runs that script after the lint task and fails when the report disagrees with the list in any
  direction — a new rule (nobody decided), more of a rule than allowed (an old finding growing), or
  fewer (a stale entry: the budget has to shrink when something is fixed, or the slack becomes
  permission to regress). The seven, and what each one actually means:
  `CustomSplashScreen` is the real finding — a hand-drawn launch screen, deliberate, with its two open
  halves in the entry above. `AndroidGradlePluginVersion` ×2 (Gradle 9.8.2, AGP 9.5.1) and
  `NewerVersionAvailable` ×3 (jna 5.19.1, the two Kotlin plugins at 2.4.21 vs 2.2.10) are freshness,
  not risk: AGP 9 ships built-in Kotlin, so Gradle, AGP and Kotlin move together in one toolchain pass
  and never as a side effect of a feature commit. `UseTomlInstead` is a **false positive and must not
  be "fixed"**: `app/build.gradle.kts` writes `net.java.dev.jna:jna:${libs.versions.jna.get()}@aar`,
  so the version *does* come from the catalog — the coordinate is spelled out only because a catalog
  entry cannot carry the `@aar` packaging, and the catalog form resolves to the jar and breaks the
  native Vosk loader at runtime. Keeping the list short is the point: the pass that took
  `UnusedResources`, `ObsoleteSdkInt`, `UseKtx` and `EmptySuperCall` to zero did not add them here.
- **Motion is asserted as decisions, not frames (motion pass).** `AppMotionTest` runs the tab rule
  and the reveal's window arithmetic on the JVM, and `AppNavGraph`'s transitions cannot run outside an
  emulator — so nothing in CI measures a transition, a cold start or a dropped frame: there is no
  `benchmark` module, no `JankStats` and no `FrameTimingMetric`, and the `device` job is a launch
  smoke. What this pass can honestly claim is work *removed*, not a measured frame rate: the focus
  plan stops loading every candidate lesson's exercises on Home's first frame (it used to parse the
  whole bundle on a fresh install for an answer that was always `null`), the page reveal is one
  `Animatable` per page instead of two per card plus a coroutine each, and a tab move no longer
  animates two full screens horizontally. The check still owed is a profiler run on a phone — frame
  time on Home's first frame and across a tab change — recorded beside the splash hand-off that is
  already owed (the entry above). If it is ever automated it belongs in a `benchmark` module, not the
  unit suite: a timing assertion in CI is a flake that reports a pass.
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
- Persian UI strings are inline in composables instead of `strings.xml` (single-locale MVP) —
  extract when a second locale lands.
