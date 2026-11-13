# Release readiness — what is proven, and what still needs a device

> Companion to [`release.md`](release.md) (the ritual) and [`testing.md`](testing.md) (the inventory).
> This file exists because the roadmap's **P2** block cannot be closed on a laptop: every item in it
> needs hardware, an emulator boot or a CI run, and "it compiles" is not evidence. Nothing here is a
> wish list — each row says what the check produces and where that evidence goes.

## What the code already proves (no device needed)

These are green as of bundle **v80** and do not need re-proving for a release unless the area changes:

| Area | Check | Evidence |
|---|---|---|
| Content bundle | `./gradlew validateContent --rerun` | 192 lessons, 1154 words, 3159 exercises (twelve of them `writing` texts), 36 placement questions, 268 nodes, bundle v80 |
| Learning core, engines, ViewModels | `./gradlew :app:testBazaarDebugUnitTest --rerun` | 798 tests / 84 classes, 0 failures (quoted by `DocCodeConsistencyTest`) |
| Lint, against the accepted list | `./gradlew :app:lintBazaarDebug` then `node scripts/lint_budget.mjs` | 7 warnings, all seven named in the budget with their reason (`CustomSplashScreen`, AGP ×2, `NewerVersionAvailable` ×3, `UseTomlInstead`) — none appeared, grew or shrank |
| Room schema and migrations | `RoomMigrationTest`, `DatabaseUpgradeTest` on the JVM | every exported schema 1→10 validates; the v10 `conversation_done` step and its one-time import |
| Atomic evidence | `AnswerTransactionTest`, `EvidenceTransactionTest`, `LessonCompletionTransactionTest`, `PlacementSeedTransactionTest` | a failure inside each write rolls the whole answer back |
| Splash hand-off contract | `SplashHandOffTest` | below API 34 the finishing splash declares it, from 34 the entered activity does — never both |
| The lesson's spoken answer | eleven `LessonViewModelTest` cases over `FakeSpeech` | `CORRECT`/`NEAR` write one transaction and are credited, `RETRY` writes nothing, a silent or broken take is never scored, and the no-recogniser install can leave the step unclaimed |
| The take's own audio | `TakeAudioTest` (six JVM cases) + three playback cases in `LessonViewModelTest` + four in `ConversationPracticeViewModelTest` | a canonical mono 16 kHz WAV whose sizes are patched to what was appended, no file for an empty take, the path reaching the player on both screens, and playback stopping when the item, the turn or the screen moves on |
| Word-level speech feedback | nine `SpokenWordFeedbackTest` cases + three `LessonViewModelTest` and two `ConversationPracticeViewModelTest` cases | the dropped content word is named, a dropped article is not, a reordering is not punished, a contraction / homophone / dialect spelling still counts as heard, one utterance satisfies one repeated word, and both screens show the list under the verdict but never on a hit or a typed answer |
| All three flavors compile | `:app:assembleBazaarDebug`, `:app:assembleMyketDebug`, `:app:assembleGooglePlayDebug` | three debug APKs build from v80 (26 s, 1124 / 1139 / 1182 entries) |
| Each flavor packages only its own store SDK | `unzip -p <apk> 'classes*.dex' \| grep -a -c <package>` | bazaar: `ir/cafebazaar/poolakey` 46, play billing 0, myket 0 · myket: `ir/myket/billingclient` 33 (the SDK's own package), poolakey 0, play billing 0 · googlePlay: `android/billingclient` 7, poolakey 0, myket 0 |
| Each APK ships the bundle the tests read | `unzip -p <apk> assets/content/exercises.json` | all three: `contentVersion` 80, 3159 exercises — the APK cannot ship content the suite did not read |

## What still needs a device, an emulator or CI

| # | Check | Needs | Evidence to record here |
|---|---|---|---|
| 1 | Home first paint and bottom-bar tab switching | real device, Android Studio Profiler (CPU + jank) | frame times for a cold Home and for each of the four tabs; any frame > 16 ms named with the composable responsible |
| 2 | Splash across Android versions | emulators at API 24, 26, 31, 34, 35 (or one device per API family) | for each API: the fade is visible, there is no white flash and no double transition; note that API 34+ is where the contract flips |
| 3 | Three-flavor build in CI | GitHub Actions `assemble` job (runs on a tag, a schedule or a dispatch — not on every push) | the job's green run for the release tag, with all three APKs in its artifacts |
| 4 | Vosk offline conversation **and the lesson's speaking item** | real device, airplane mode on, Vosk model present | first recognition latency, peak memory during a take, memory released after the take ends, and a full dialogue scored with no network — plus one lesson `speaking` take: the transcript it heard, the verdict, that the step's answer appears in Progress as Speaking evidence, that the played-back WAV **is** that take (same words, no pitch shift), and that no word is dropped on a long sentence while the file is being written |
| 5 | Room 9 → 10 on a real install, then Backup/Restore | device with a v9 build installed, then the v10 build over it, then Auto Backup + restore | progress survives the upgrade, `conversation_done` is carried, the finished dialogue is still marked after a restore, and the next lesson stays open |
| 6 | Launch smoke on Android | `scripts/verify_device.sh bazaar --require-device` (the CI `device` job) | install, airplane mode, two timed cold starts, the seed line, and the crash sweep — with `--require-device` so a dead emulator is red, not a green no-op |

## Release readiness criteria (roadmap), restated as checks

- **The difficulty of every level is judged against the real content.** Done in code: all six levels are
  read item by item (`scripts/difficulty/*.json`), and since v77 no lesson is flat either —
  `ContentDistributionTest` fails the build when any single lesson's choice items share one number.
- **The selector never invents weakness where there is no evidence.** Done in code:
  `AdaptiveExerciseSelector` classifies by `isEstablished()` first, and eight cases pin the
  weak/consolidated × easy/hard matrix.
- **A level change is explainable from the learner's performance.** Done in code: `DifficultyAudit`
  reads the answer log with an 8-answer / 2-day bar; it proposes and never writes.
- **Content tests and all three flavors build.** Content tests are green; the flavors build locally
  (row 6 above turns that into a CI fact for the release tag).
- **Device checks are documented.** Rows 1–6 above are the list; each is only "documented" once its
  evidence column is filled.

## How to use this file

Work the table top to bottom at a release point, and replace the evidence cell with what was actually
observed (device model, API level, numbers). An empty cell is an unproven claim: it is not a bug, and
it must not be described as verified. When an item becomes automatable (a benchmark, a CI device
matrix), move it to the first table and delete the row — the point of this document is to get shorter.
