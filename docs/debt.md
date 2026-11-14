# Known design debt (accepted, tracked here)

> Moved out of the root `AGENTS.md` (checklist v10). Everything here is a deliberate,
> accepted trade-off, not a bug to fix silently.

- **A node card is one question per node per session, and the session is words-then-graph
  (graph-card pass).** The review session now asks a due curriculum node one of its own exercises,
  which is what lets review practice confirm an exercise format at last — but the card is
  deliberately narrow. **One** exercise per node: `NodeCardPicker` picks a single question, so the
  focused drill (the node's whole evidence block) is still the deeper path and still one tap away on
  the summary. **Typed and tapped formats only**: a `listening` item *is* its audio and a `speaking`
  item needs the microphone and a take, so both stay in the lesson and the drill; a card the review
  surface cannot answer would be a dead end. **No `review_item` row and no `ReviewAttempt`**: a
  node's schedule lives in `knowledge_state`, so the card is graded with one verdict rather than the
  four SRS buttons, and its evidence is the lesson's path (the day's claim, the node with its format
  bit, the skill meter). The consequence is accepted: the review log behind review accuracy and the
  miss taxonomy's card half does not see these answers, and giving an exercise a grade in that log
  would need a card type the log does not have — its unit is a reviewed *item*, not a question.
  **The two halves do not interleave**: the word cards are asked first, so a learner with a hundred
  words due may never reach the graph's questions in one session, and nothing reorders across the
  halves by weakness — a due word is what the SRS owes, a due node is a retention probe, and
  interleaving them would make "what is left" depend on how many words happened to be due.

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

- **An abandoned take is refused by policy, and the refusal is tested off-device.**
  `TakeWindow` + `TakeWindowTest` pin *whether* a take may still publish, and a capture loop
  that leaves on abandonment closes the microphone in code, but the two device-only halves are
  not unit-testable here: `cleanup()` discarding `TakeRecorder`'s open writer (that `discard()`
  closes the file rather than leaving a playable WAV) and `SpeechBus.isLive()` skipping a
  `startService` that would otherwise flash a notification. They are read in review and checked
  on a device in `scripts/verify_device.sh`'s manual list, not by an assertion — the same limit
  this file already records for the Vosk recogniser itself.

- **A written text is graded on its shape, not on its prose (bundle v79).** `Exercise.Writing`
  checks three things a phone can check without a model: every authored anchor appears **somewhere**,
  the answer reaches its `minWords`, and it contains at least `minSentences` finished sentences. That
  is what makes an offline grader honest rather than clever, and its limit is the whole of what a
  learner might expect more of: a text that uses `has not been` and `refund` in three sentences of
  nonsense passes, and so does a paragraph about the wrong subject. The same is true of
  `sentence_production`, and the same answer applies — the exemplar is shown after a miss, so the
  learner compares against a real text, and a judgement of quality needs either a language model (a
  backend the connectivity policy keeps suspended) or a teacher. Two smaller limits ship with it: the
  sentence count is punctuation-based, so a learner who writes three comma-spliced clauses and one
  full stop is credited one sentence (the floor asks for finished sentences, which is the honest
  reading of a `.`), and the floor is authored per item rather than derived from the level, because
  how much text a lesson should ask for is a content decision.
- **The difficulty audit reads the log and nothing surfaces it yet (P1).** `DifficultyAudit` +
  `GetDifficultyAuditUseCase` turn the answers `lesson_attempt` already keeps into per-item accuracy,
  thinking time and a bounded suggestion, and sixteen cases in `DifficultyAuditTest` read it on a fake
  repository. Its sample is **one answer per learner-local day** (`TimeUtil.dateKey`), which is both
  the honest unit — a retry after the feedback is not a second observation — and the reason its bar
  is expensive: eight independent answers now means eight different days on the same item, so a
  learner who meets an item twice per session will take weeks to make it eligible. That is a
  deliberate trade against verdicts on grinding, and `MIN_ATTEMPTS` / `MIN_DAYS` stay provisional
  until a real answer log has been read; the report also describes **one learner's own history**, so
  a suggestion is a hint for the next authoring pass, never a population-level difficulty. No screen asks for it: a content-review surface is an authoring tool, not a learner
  screen, and putting a "this item may be mis-rated" row inside the app would hand a judgement to the
  person least able to act on it. Until that surface exists the report is read by its tests, which is
  why the pass ships an engine and a use case rather than a migration — the recording half was
  already there. The second limit is the timing itself: `lesson_attempt.at` gives the gap between one
  answer and the next in the same lesson, so reading time and feedback reading are inside it, and a
  distractible learner and a stuck one cannot be told apart. It is a diagnostic beside the accuracy,
  never a trigger on its own.

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
- **Closed (v73): every node is reachable in two formats, and it is counted rather than assumed.**
  Securing a node needs two confirming exercise formats, so a node a lesson only ever asks one way
  can never leave the plan. The entry here used to name `phonology.spelling-aloud` and
  `phonology.number-sounds` as the two stuck ones — that was read off the lessons instead of counted,
  and it was wrong: both are credited by a listening item and by a spoken dialogue turn, so they
  held two formats all along. Measuring it properly (the rule is `KnowledgeEvidence.itemsFor` over
  each shipped exercise) found **seven** nodes in the state, none of them those two: five A1
  vocabulary nodes (`vocab.irregular-plurals`, `vocab.people-in-my-life`, `vocab.ability-verbs`,
  `vocab.imperative-verbs`, `vocab.question-words`) whose only credit came from the lesson's listening
  item, because its «What does X mean?» choice was tagged `GRAMMAR` while `AnswerChecker.heuristicMc`
  reads a Persian-option question as vocabulary; and two B2 discourse nodes
  (`discourse.complaint-register`, `discourse.hedging`) credited only by a spoken turn, because their
  typed translation carried a `VOCABULARY` override while `skillOf`'s own default for a translation
  is `WRITING`. Seven tags fixed it — no item was added, so the lesson counts, the difficulty numbers
  and `GrammarCoverageTest`'s six-exercise bar are untouched — and `ContentDistributionTest` now
  fails when any node falls under `KnowledgeGates.MIN_FORMATS_TO_SECURE`, so the next such node
  fails the build instead of being discovered by reading. The same migration leaves `firstCorrectAt` NULL on legacy rows instead of
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
- Room is at `version = 11` (`lesson_attempt`, `question_attempt`, `conversation_done`, `knowledge_item.wordsJson`) with exported schemas
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
  recorded as a wrong answer. Word-level feedback (v81) narrows the gap without closing it: the take
  is marked against the model sentence and the content words the device did not hear are named, on
  the scorer's own normalisation and one-edit tolerance, so the feedback and the verdict cannot disagree.
  It is still a **word** the recogniser did or did not transcribe — not a judgement of how well it was
  said — and it inherits the recogniser's own mistakes: a misheard word is reported as a word the
  learner did not say. Teaching actual pronunciation still needs phoneme-level scoring (a different
  engine, or a cloud service the connectivity policy keeps suspended).
- **Settled: the speech rate is a setting, and it is still one value (speech-pace pass).** It used
  to be an engine-wide constant — 0.75× for every English utterance, because native 1.0× was
  reported as unintelligible on a first A1 ear — and a learner whose listening had improved (or
  whose phone's voice reads fast) could not move it. `SpeechPace` now has three steps (آهسته 0.6,
  معمولی 0.75, طبیعی 1.0), stored as `speech_pace` and applied to the live player from `EnglishApp`'s
  settings collector, so the next sentence is at the new speed. Two limits are accepted. First, one
  pace for **everything**: a listening item, a word's pronunciation and the dialogue's partner line
  are the same learning material, so a per-screen speed would let two surfaces disagree and would
  ask the learner to tune the same ear twice. Second, the sound itself is a device check — the JVM
  suite pins which value is stored and which value a bad record falls back to, never that a given
  engine renders 0.6 without slurring, which is why the slowest step stops at that floor and the
  default stays at the rate the app already shipped at.
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
- **The double credit on three two-node lessons is closed; what remains is a card that cannot be
  resolved to a word (v82).** A lesson owning two vocabulary nodes used to credit both for one card
  (`c1.phrasal-idioms`, `c2.nuance`, `c2.collocation`) — reviewing «subtle» raised
  `vocab.semantic-precision` *and* `vocab.connotation`. A node now names the words it teaches
  (`KnowledgeItem.words`), `recordRecall` resolves the card id to its headword and
  `KnowledgeEvidence.itemsForCard` picks the claiming node, so the graph gets one claim. The residual
  is narrower and deliberate: a card whose content id the bundle no longer ships resolves to no word
  and falls back to the lesson's nodes of its type, which is still two on those three lessons — the
  old over-credit, reachable only by a card orphaned by a content change, and the safer of the two
  guesses (a card with no identity is not evidence for "neither"). The authoring side is guarded
  instead of assumed: `ContentValidator` refuses a node naming a word its lessons do not ship and, on
  a two-node lesson, a word claimed by both or by neither. Rejected as the alternative: a `nodeId` on
  each of the 1154 words — 1154 authored links to maintain for three lessons' worth of ambiguity,
  where six node lists do the same job and can be read against the lesson at a glance.
- **The lesson's speaking verdict is real evidence, but the recogniser behind it is not measured
  here (v79, audio v80).** The take is graded offline by Vosk through the dialogue's own seam, and
  the verdict is unit-tested end to end (`FakeSpeech` publishes what the service publishes; the
  WAV writer is read back byte by byte in `TakeAudioTest`). What no test can answer is whether the
  recogniser *earns* that verdict on a phone: word error rate on a Persian speaker's English, the
  first-take latency after the model is already loaded, whether the silence window is long enough
  for a beginner — and, since the audio is kept, that `MediaPlayer` plays the 16 kHz WAV back at the
  right pitch and that writing it does not make the capture loop miss a buffer. That last one is the
  new risk of one-take-does-both: the loop still reads one 100 ms buffer at a time and appends it,
  but a slow disk on a cheap phone could in principle stall the recogniser, so the device check has to
  listen for a dropped word over a long sentence. The lesson's speaking card is therefore a **device
  check**, listed as such in [`release-readiness.md`](release-readiness.md). The dialogue's turn now
  offers its own audio back too (v81), so both screens hold one capture that is judged and hearable;
  the remaining limit is the file itself: a take is one fixed cache path, so a second take overwrites
  the first — which is why playback stops the moment the learner moves to another item.
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
  silently is not classified, never a wrong label.  **The adaptive half now ships (miss-driven plan
  pass)**: the planner reorders within one priority by the weakest category and names the gap on the
  action that answers it, and the review screen's due list breaks its ties the same way
  (miss-driven drill pass) — so the same mix reaches both places that decide what to do next, and
  neither is allowed to reorder across an urgency (see [`learner-model.md`](learner-model.md)).
  **Level now exists inside a session, not in the plan (v66).** Every
  exercise carries an authored `difficulty` (1..3, relative to its own lesson's level), and
  `AdaptiveExerciseSelector` uses it to come down to the easy end while a dimension is shaky and go up
  to the hard end once it is consolidated. What the *plan* still cannot do is choose a harder **lesson**:
  a plan action is a lesson, a lesson carries no difficulty of its own, and deriving one from its
  items would be a guess dressed as adaptation — so the plan keeps steering by urgency (which node,
  in which order) and the level changes only where the learner is actually answering. Two smaller limits are accepted with it: the ordering only
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
