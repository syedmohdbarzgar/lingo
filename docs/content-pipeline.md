# Content pipeline

> Moved out of the root `AGENTS.md` (checklist v10). The **rules** live here; the
> "which bundle gained what" history lives in [`docs/CHANGELOG-content.md`](CHANGELOG-content.md),
> and the one-shot batches that applied each change are catalogued in
> [`scripts/archive/README.md`](../scripts/archive/README.md).

- **The plan and the bundle are separate things.** [`curriculum.md`](curriculum.md) is the product
  curriculum — 30 topics per CEFR band, 180 in total — and records which topics actually ship; the
  JSON bundle is what the app can teach today. A topic ships only as a complete lesson (teaching
  page, six words with two bilingual examples, recall/listening/production items, a knowledge node),
  never as a stub, and the bundle is never padded to match the plan.
- Source of truth: `app/src/main/assets/content/{lessons,vocabulary,exercises,placement,knowledge}.json`.
  `knowledge.json` is the **curriculum graph**: one node per learnable unit, with
  `prerequisites` (ids of nodes to learn first), the `lessons` that teach it, its CEFR `level` and
  the `skills` it trains. `KnowledgeGraph` (pure, in `domain/engine`) orders and unlocks it; it is
  seeded into the `knowledge_item` table. Repositories/UI must not hard-code curriculum order.
- `ContentSeeder` runs at app start; re-seeds when the JSON `contentVersion` changes (settings key
  `content_version`). Content tables: `lesson`, `vocabulary`, `exercise`. The version is authored
  **inside the JSON** — there is no Kotlin constant to bump (technical spec §63).
  The five assets are **parsed concurrently** (one coroutine per file on `Dispatchers.Default`) and
  inserted in the same single transaction; the bundle keeps growing (1.5× from v27 to v28), so the
  decode — not the insert — is the stage that grows with it. Measured on the JVM: 87 ms → 41 ms for
  the parse stage at v28. The seeder logs `seeded v<version> in <n> ms` on every seed so growth stays
  visible: a fresh install of v28 seeds in ~1.4 s on a POCO X3.
- **`./gradlew validateContent`** (wired into `preBuild`; rules in `buildSrc/…/content/ContentValidator.kt`,
  task shell `ValidateContentTask`) is the content gate. It checks unique ids,
  dangling `lessonId` references, valid CEFR levels and exercise types (placement allows choice
  or typed — never audio or speaking), `correctIndex` in range,
  `fill_blank` blank markers, non-empty accepted answers, duplicate words within a lesson, matching
  bundle versions, and full lesson coverage (every lesson needs vocabulary, exercises and at least
  one knowledge item). For the graph it also checks that prerequisite ids resolve and that **no
  cycle** exists. It reports **all** problems at once, not just the first. Run it after every
  content edit.
- **CEFR bundle (contentVersion 29):** 76 lessons (**A1 = 37 — all 30 syllabus topics plus the
  seven A1 grammar supplements**, A2 = 13 — seven earlier lessons plus the six travel topics of
  v29, B1/B2 = 6 each, C1/C2 = 7 each), 744 exercises
  (8–19/lesson + reading passages + meaning items + A1 pronunciation drills + P2 recall/production
  items + the 12 dialogue turns), 458 vocabulary entries (6/lesson, 8 in the numbers lesson),
  117 knowledge items, placement = 36 questions in six graded bands of 6 (A1→C2, ordered by
  difficulty) — 35 choice plus **one typed `fill_blank`** in the A1 band (`placement.t01`), so the
  test samples production and not recognition alone. Grammar points per level follow the British Council / EQUALS Core Inventory grammar
  tables (verified against examenglish.com/CEFR, Oct 2026): e.g. B1 = 2nd/3rd conditional + reported
  speech + simple passive; C1 = inversion + mixed conditionals + modals in the past; C2 =
  nuance/precision vocabulary.
- **Vocabulary depth (A-10):** every word carries **two** example sentences, each a bilingual object
  `{"en": …, "fa": …}` authored in `vocabulary.json` (326 words × 2 = 652 English sentences, all
  with Persian). The shape lives inside the existing `VocabularyEntity.examplesJson` TEXT column —
  the column type never changed, `ContentParser`/`RepositoriesImpl` accept the legacy plain-string
  shape, and `ContentSeeder` clear+inserts every row on the version bump — so **no Room schema
  migration is needed**: Room stays at `version = 5` and `RoomMigrationTest` keeps proving the
  existing path (a migration would rewrite nothing). `validateContent` and `VocabularyContentTest`
  both require ≥ 2 examples per word with a non-empty Persian `fa`, and the word-in-example rule is
  checked **per example**, not per entry.
- **Foundations at the very front (A-7):** the alphabet, spelling a name and numbers 0–100 come
  **before** Greetings, so a complete beginner meets letters before words. Three A1 lessons
  (`a1.alphabet.lesson-01`, `a1.spelling.lesson-01`, `a1.numbers.lesson-01`, orders 1–3 with every
  other lesson shifted by three), 20 words, 24 exercises and 6 knowledge items (`vocab.alphabet`,
  `vocab.spelling`, `vocab.numbers` + a PHONOLOGY node per lesson), and `vocab.greetings` lists
  `vocab.alphabet` as a prerequisite.
- **A1/A2 gaps closed (A-9):** articles (a/an/the), plural spelling, subject/object pronouns, can
  for ability, imperatives and question words each got their own A1 lesson (tip + 6 words + 6
  grammar exercises + phonology coverage + a guided listening drill), and the past simple is
  introduced at **A2** (`a2.past-simple.lesson-01`, `grammar.past-simple` level B1 → A2) with
  `b1.work` still teaching it as a review.
- **Content correctness pass (P0-9):** the bundle was audited end-to-end and two silent defects
  fixed, both now enforced by `VocabularyContentTest`:
  (a) the generated "What does X mean?" choices (one per lesson at `*.ex.07`) drew their distractors
  from a global cursor, so an A1 item could offer a C2 gloss — distractors now come from the lesson's
  own vocabulary; (b) two vocabulary examples (`a1.shopping.word.price`, `…word.discount`) never
  named their word. Every vocabulary example must now contain its word (inflection-tolerant:
  `blocks` illustrates `block`, `children` illustrates `child`, separable phrasal verbs may split) —
  the genuinely irregular forms live in an explicit allowlist that fails the build when it rots.
- **Practice bar (A-8):** every GRAMMAR knowledge item needs **at least six grammar-targeting
  exercises** across its lessons, counted with `AnswerChecker.skillOf` — the same rule the mastery
  engine uses, so a vocabulary-heavy lesson does not count as grammar practice. `GrammarCoverageTest`
  fails the build below the bar; add exercises to that topic's lessons.
- **Explain a miss (A-1):** every grammar and fill-in-the-blank exercise carries a one/two-sentence
  `explanationFa` saying *why* the answer is right (and, for the common wrong option, why it is not).
  Coverage is **every one of the 744 exercises** (grammar/fill-blank core plus vocabulary meaning,
  translation and listening items). `ExplanationCoverageTest` enforces 100% on the core set and on
  the whole bundle (checklist A-1's floor of 90% was passed, so the bar is now the invariant);
  `validateContent` prints any exercise without one as a non-blocking warning. A new exercise of any
  type ships with its explanation. Every **vocabulary** word additionally carries an authored
  Persian `explanationFa` (a short usage note): the SRS queue is vocabulary-only, so the review
  session shows it after a miss, exactly as the lesson screen does. It is a vocabulary column
  (`VocabularyEntity.explanationFa`, Room v5) and a new word without one fails
  `ExplanationCoverageTest`.
- Exercise mix is intentionally varied: `multiple_choice`, `fill_blank`, `word_recall`,
  `translation`, `sentence_production`, `listening`, and authored `skill` tags (VOCABULARY / GRAMMAR /
  READING / WRITING) so mastery is not dominated by recognition questions. Correct-answer positions
  stay balanced — `scripts/balance_answer_positions.mjs` restores the invariant and
  `ContentDistributionTest` enforces it.
- Placement scoring is **band-based** (`ScorePlacementUseCase`): walks bands bottom-up, band passes
  at ≥2/3 with ≥60% cumulative accuracy, stops at first failed band, floor A1. Question `level` tags
  in placement.json are documentation; scoring uses array order + `ScorePlacementUseCase.DEFAULT_BAND_SIZE = 6`
  (six bands × 6 = 36 questions).
- **Reading comprehension and meaning items (A-5).** `Exercise.MultipleChoice` carries an optional
  `passage` (English, rendered above the question by `LessonScreen`/`PlacementScreen`). Six lessons
  — one per level A1→C2 — hold a 3–5-sentence passage with three comprehension questions that each
  repeat the passage, so a re-queued question is still answerable alone; each of those lessons also
  gets one English→Persian "What does this sentence mean?" item. `ReadingContentTest` enforces the
  passage shape (3–5 sentences, English, reused by ≥2 questions), one passage per level, Persian
  options on the meaning items, and the placement reading questions.
- **Placement is also a skill assessment (P1-1).** Every placement question carries an authored
  `skill` (11 VOCABULARY, 19 GRAMMAR, 6 READING — the three axes the test actually measures; no fake
  tags). `PlacementAssessmentEngine` (pure) returns the overall level from the same band scorer plus
  a per-skill accuracy read, `strengths` and weakest-first `focusSkills`; `AssessPlacementUseCase`
  then replays the answers through `ProgressRepository.applyAttempt` so skill mastery starts
  calibrated instead of blank — never by writing mastery directly, so the EWMA stays single-source.
  The result screen shows the per-skill breakdown; unmeasured skills are never listed.
- IDs are **stable dotted strings** (`a1.airport.word.passport`, `placement.q01`) — never renumber.
- `exercise.payloadJson` stores the authored JSON verbatim; `ContentParser.exerciseFromPayload` maps
  it to the sealed `Exercise`. Unknown types are skipped, never crash a lesson.
- Adding content = JSON only (no Kotlin changes), per technical spec §61/§63.
- **The JSON files are the source of truth, not the scripts (A-12).** Applied batches live in
  `scripts/archive/` (see its README) purely as the record of *how* a change was made — **never
  re-run one**, it would overwrite later hand edits. `scripts/` itself holds only what you still
  run: `balance_answer_positions.mjs`, `install_debug.sh` and `release_check.sh`. After any content
  edit run `node scripts/balance_answer_positions.mjs`, then `validateContent` + the content tests
  with `--rerun`.
- Supported exercise types: `multiple_choice`, `fill_blank`, `word_recall`, `translation`,
  `sentence_production`, `listening`, `speaking`.
  - `speaking` feeds **conversation practice** (the lesson screen's own card still records and
    compares, self-assessed). The first `accepted` sentence is the reply the learner is graded
    against by the on-device recogniser (`ConversationScorer`), so it must be English. Two optional
    fields turn the item into a dialogue turn instead of an echo:
    - `question` — the partner's line in English, spoken by the device before the learner answers.
      English only (TTS speaks it) and never identical to the answer (that would be an echo).
    - `questionFa` — the Persian gloss of `question`. Required **whenever** `question` is present:
      the learner must know what was asked; a gloss without a question is rejected too.
    Without `question` the item stays a repeat-the-model turn. `validateContent` enforces all of
    this, the validator also requires a Persian `prompt`, at least one English `accepted` answer and
    a declared `skill`, and the app never moves mastery from speaking practice.
- **Retrieval and production, not just recognition (P2).** Two shapes close the hole that every
  vocabulary item was multiple choice and the only "say something" item (`translation`) shipped a
  word bank in 21 of its 41 items:
  - `word_recall` — the Persian meaning, name the English word, **no options anywhere**. It is the
    vocabulary-side evidence for `MasteryDimension.RECALL` (skill VOCABULARY).
  - `sentence_production` — build the whole sentence from a Persian cue, free typing only. It is the
    typed counterpart of the suspended `speaking` item and therefore the second source of
    `MasteryDimension.PRODUCTION`.

  The production grader deliberately does **not** demand one memorised wording:
  `AnswerChecker.isCorrect` passes an answer that matches an exemplar in `accepted` **or** that uses
  every `mustUse` anchor **in the authored order**, reaches the author's `minWords` floor, **and**
  reuses at least half of one authored exemplar's words (`PRODUCTION_MIN_EXEMPLAR_OVERLAP`, v9 B-10).
  Anchors are matched whole-word and contraction-expanded (`didn't` is satisfied by `did not`), and
  order is part of the contract — a sentence that merely contains both anchor words somewhere is a
  different structure and must not pass. The overlap gate closes the hole the anchor walk left: a
  filler string like `usually breakfast five words here` carries the anchors at the right length but
  shares almost none of the target sentence, so it is not credited. The anchors are never
  adjacency-checked, because the authored pairs are not adjacent in their own exemplars
  (`usually … have breakfast`, `if … we will`). There is no model and no network in this: it checks
  that the target form was actually attempted, and the exemplar stays the answer shown after a miss.
  `validateContent` requires the anchors, the floor (≥3 and ≥ the anchor word count) and the skill
  tag, and rejects a cue that leaks English letters (the cue is the meaning, so an English cue would
  both flip the RTL paragraph and hand over the answer). `ProductionRecallContentTest` then checks
  the authored items — including that **each item's anchors can credit its own exemplar**, asserted
  with the item's `accepted` replaced by a decoy the answer cannot exact-match, so only the structural
  branch can pass it (v9 B-10: emptying `accepted` would now make the overlap gate reject everything).
  That is what catches an anchor in the wrong order or a floor the exemplar cannot reach; asking the
  item as authored would be vacuous, because an exemplar match short-circuits the structural rule.
- **Every `fill_blank` must carry an authored `skill` (A-2b)** — `validateContent` rejects an
  untagged one. The heuristic default (GRAMMAR) used to decide whether a near-miss was reported as a
  spelling slip, which is a pedagogical call the author has to make: word-choice blanks are
  `VOCABULARY` (a typo gets the hint), form blanks are `GRAMMAR` (no hint). Tagging them honestly
  left 13 grammar topics under the six-exercise bar, so `scripts/archive/p1_grammar_topup.mjs` added
  real form practice rather than letting a fallback define the bar.
- Typed-answer grading (A-2): digits normalize to words (0–20 + tens), so `3` ≡ `three`; a single
  word ≥ 5 letters that is one edit from an accepted answer is graded wrong but shown as
  «تقریباً درست — املای کلمه را بررسی کن» (`AnswerChecker.grade`). ALMOST is a **spelling** signal,
  so it never fires on a GRAMMAR item (A-2b): `listen` vs `listens` is a missing `-s`, not a typo,
  and the lesson shows a plain miss plus the item's explanation. Every single-answer `fill_blank`
  must appear with a reason in `ContentDistributionTest.reviewedSingleAnswerBlanks`; new ones fail
  the test until equivalents are added or justified.
- **A dot/comma between digits is a decimal (A-2b):** `normalize` protects it before stripping
  punctuation, so `3.5` can never collapse into `35`; a sentence-final dot still goes (`3.` ≡
  `three`), and a digit run glued to letters or to a separator (`1st`, `20th`, `3.5`) is left as
  typed instead of becoming `thirtyth`.
- Phonology / pronunciation (A-7, A1 only): each A1 lesson teaches a `## تلفظ` section in its
  `grammarTipFa` (vowel length, word stress, word sounds — an unknown header parses to a CUSTOM card,
  so no schema change), is covered by a PHONOLOGY knowledge item, and has a guided listening drill
  (`listening` + `explanationFa` naming what to hear). PHONOLOGY nodes list the `LISTENING` skill and
  are **never prerequisites** of other items: `KnowledgeEvidence.itemsFor` unions type-matches with
  explicit skill tags so listening moves them, but gating a lesson on pronunciation mastery would
  strand learners. `PhonologyCoverageTest` enforces all of it.
