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
  **inside the JSON** — there is no Kotlin constant to bump (technical spec §63). A seed that throws
  is recorded as `ContentStatus.FAILED` rather than logged and dropped, and every screen that reads
  the bundle (`Home`, the lesson, the placement test, conversation practice) renders that failure as
  a named, re-seeding retry — so a broken seed cannot present itself as an empty curriculum, an
  empty lesson or an empty conversation. A retry after `FAILED` re-runs the write before the read,
  and a read that throws is a separate, also-retryable state (the data is local: neither is a
  connectivity error).
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
- **Every exercise carries an authored `difficulty` (v66).** 1..3, **relative to its own
  lesson's level**, never a ranking across levels: A1/A2 tops out at 2 (nothing there is hard *for
  A1*), C1/C2 starts at 2 (nothing at C level is a first encounter), and B1/B2 uses the full scale.
  The adaptive selector reads it to raise or lower the level inside a dimension — easy end while the
  meter is shaky, hard end once it is consolidated, authored order when there is no evidence.
  `validateContent` refuses a missing value or one outside 1..3; `ContentValidationTest` also checks
  each shipped item against its own level's band. The first values were seeded from the item's
  format band (recognition 1, guided production 2, free production 3) bounded by its lesson's level,
  by [`scripts/author_difficulty.mjs`](../scripts/author_difficulty.mjs), which now only fills a
  missing value — from v66 the number is edited by hand like any other authored judgement.
  **A level that has been read item by item keeps its judgement in
  [`scripts/difficulty/<level>.json`](../scripts/difficulty/b1.json)** (applied after the seed, so a
  `--force` re-seed cannot silently undo it). B1 was the first (v67) and it showed what the seed gets
  wrong in both directions: inference items are not the easy end merely because they are multiple
  choice, and a one-clause typed answer is not the hard end merely because it is production. B2
  followed (v68) and found the third error: **a `fill_blank` is not the easy end merely because it is
  recognition** — at B2 a blank that tests a structure (passive, perfect, conditional, relative,
  reported speech, quantifier agreement) is the hard end, while a blank that tests a word is the easy
  end, and the seed had called every blank in the level a 1. 124 of B2's 502 items were re-judged
  (111 changed); the level moved from 502/0/0 to 139/295/68 across 1/2/3, and 10 of its 32 choice
  lessons now disagree about difficulty — six of them inside their own multiple-choice drills
  (`b2.technology`, `b2.environment`, `b2.interview`, `b2.media`, `b2.problems`, `b2.research`) and
  four through the paired reading questions (`b2.leadership`, `b2.education`, `b2.debate`,
  `b2.viewpoints`).
  C1 was judged next (v69), and its floor changes the question: nothing at C1 is a first encounter,
  so the choice is between *typical C1* and *the hard end of C1*, not between easy and hard. Three
  groups are the hard end — the inversion and mixed-conditional structures the level is built on
  (`Rarely has the board seen…`, `Not only does the premise fail…`, `Under no circumstances should…`,
  `Had the sample been larger`, `If we had settled in March, we would be…now`), the reading questions
  that ask for the author's purpose or the consequence rather than a fact in the passage, and the
  metalanguage items that ask which named device a sentence uses — plus the speaking turns that
  demand an inversion out loud. 63 of C1's 478 items were re-judged (59 changed) and the 22
  one-structure sentence productions came down to 2, the rule B1's pass set. C1 moved from 452/26 to
  437/41 across 2/3, with 9 of its 31 choice lessons disagreeing about difficulty.
  That 9 exposed the other half of the problem: the selector raises the level **within a format**, so
  a lesson whose hardest item was a blank or a reading question could not raise recognition at all.
  v70 gave each of the remaining 22 lessons one authored choice item — four grammatical options
  where the context decides, never "choose the correct sentence" (three of those four options break
  the structure outright, and v69 judged that shape a 2) — so C1 is 437/63 and **  every one of its 31 lessons** now has a hard choice item. 2888 → 2910 exercises.
  C2 was judged last (v71) and follows C1's shape — floor 2, so *typical C2 against the hard end of
  C2*: the inversion and mixed conditional the level is built on, the quantifier-phrase agreement
  trap, the reading questions that ask what a passage implies, and the metalanguage items where a
  near-synonym would be wrong. 61 of its 455 items were re-judged (55 changed) and 18 one-clause
  productions came down to 2. C2 is 411/65 from 430/25, and all 30 of its 30 choice lessons disagree
  about difficulty. v75 finished the level the way v70 finished C1: the 21 lessons whose hardest item
  sat in a blank or a production got one authored hard choice item each, written in the level's own
  hard-end shape (four grammatical options where the context decides, and the mixed conditional, the
  inverted negative adverbial and the agreement trap among them), so 3159 exercises today. Neither C
  level now leaves recognition unable to rise.
  A1 and A2 were judged last (v72), and their band of 1..2 makes the question the simplest one the
  scale allows: *first encounter, or one step past it*. The 2 goes to a blank that tests a structure,
  to a choice where two options are grammatical and the context decides, to a choice that reads a
  sentence or a passage rather than a word, to a listening line with two clauses or a place/time
  phrase, and to the request whose politeness is the point; the 1 stays with a word's meaning, a
  spelling, a number, a single-clause line or a sentence whose wrong options are mangled. 97 A1 and
  105 A2 items were raised, which is what gives the two levels their axis: A1 is 175/324, A2 120/368,
  and 17 of A1's 30 and 11 of A2's 30 choice lessons now disagree about difficulty. Every level in the
  bundle has been read item by item, so a level whose choice items all share one number is seeded
  again — `ContentDistributionTest` fails the build when it sees that.
  The same half-gap v69 left in C1 and v71 in C2 was widest here, and v76 closed it: 41 lessons
  (20 A1, 21 A2) shipped only «What does X mean?» choices, every one authored 1, so the selector
  could neither come down (nothing below) nor step up (nothing above) — at the two levels where a
  beginner most needs both. Each got one item at 2 in which **the sentence decides the word**: four
  of the lesson's own words of the same part of speech, one of which the context picks out
  (`The ______ says "meow"` → cat, `I am crying because I am cutting an ______` → onion,
  `Our ______ trains the team twice a week` → coach, `I ______ tea to coffee` → prefer). All 37 A1
  and 32 A2 choice lessons now disagree, the shape v70 and v75 gave C1 and C2.
  v77 closed the same gap at B1 and B2 — the last 42 lessons (20 B1, 22 B2) whose whole choice
  population was a gloss at 1, with the hard item living in a blank, a translation or a production.
  Their hard end is a **structure** rather than a harder word, so each new item at 3 reads the
  lesson's own grammar node in the lesson's own vocabulary, with four grammatical options where the
  context decides: the first conditional's present-in-the-`if`-clause (`If you disagree, say so
  now`), the simple passive (`A transfer fee is charged`), `had better` + bare infinitive, the past
  perfect before a past event, `must be recycled` against `must recycle`, `the more … the less …`,
  the present-perfect passive with `since` (`has been verified`), the inverted tentative conditional
  (`Were the subsidy withdrawn`) and the non-defining `which`. Every one of those items is
  `GRAMMAR`, because what has to be read is the structure. **No lesson in the bundle is flat any
  more**: all 192 have a within-format difficulty axis, which `ContentDistributionTest` asserts by
  name rather than by level count.
- **A prerequisite is taught at or before the node that needs it (v74).** A prerequisite is not a
  suggestion: `PrerequisiteEngine` answers an unmet one with a `REMEDIATE` action, so a
  prerequisite introduced *later* in the path sends the learner up a level instead of back to one
  item. Two B2 vocabulary nodes taught in `b2.idioms` and `b2.phrasal-verbs` required the two C1
  nodes of `c1.phrasal-idioms` — a whole level after their own lessons. The honest target is the
  node before it in the chain (the shape v65 gave every other level), so they now follow
  `vocab.film-reviews` and `vocab.common-idioms`. A node's `level` is the same kind of claim about
  place: it is where the node is **introduced**, so two grammar nodes whose first lesson was a B1
  one are B1, not B2. `ContentDistributionTest` fails the build on either slip.
- **Every knowledge node is confirmable in two exercise formats (v73).** `KnowledgeGates` refuses to
  secure a node that one format confirmed, so a node whose lessons only ever ask it one way can never
  be secured — it stays in the plan however well the learner knows it. Seven nodes were in that
  state and the tags, not the content, were the bug: five A1 vocabulary nodes were credited only by
  their lesson's listening item, because its «What does X mean?» choice was tagged `GRAMMAR` while
  `AnswerChecker.heuristicMc` reads a Persian-option question as vocabulary; two B2 discourse nodes
  were credited only by a spoken dialogue turn, because their typed translation carried a
  `VOCABULARY` override while `skillOf`'s own default for a translation is `WRITING`. So when a node
  never moves, read its lessons' `skill` tags before concluding the level teaches too little —
  `ContentDistributionTest` now counts the formats that can credit every node and fails under
  `MIN_FORMATS_TO_SECURE` (2).
- **CEFR bundle (contentVersion 75):** 192 lessons (**A1 = 37 — all 30 syllabus topics plus the
  seven A1 grammar supplements**, **A2 = 32 — all 30 syllabus topics (topic 28 ships as
  `a2.hobbies`) plus `a2.restaurant`, `a2.directions` and the `a2.past-simple` supplement**, **B1 = 30
  — all 30 syllabus topics (topic 2 ships as `b1.work`, 12 as `b1.experiences`, 21 as `b1.money`,
  28 as `b1.plans`)**, **B2 = 32 — all 30 syllabus topics** (5, 8, 11 and 17 from the v5
  bundle, then v42's negotiation, project, leadership and entrepreneurship lessons, v43's finance,
  economy, artificial-intelligence and privacy lessons, v44's renewables, education, mental-health
  and media-literacy lessons, v45's persuasion, crisis, report and presentation lessons, v46's
  debate, review, idiom and phrasal-verb lessons, v47's intercultural, workplace-conflict,
  strategy and ethics lessons and v54's career-decisions and viewpoints lessons) plus two outside
  the list, `b2.interview`/`b2.research`**, **C1 = 31 — all 30 syllabus topics
  (8, 12, 13, 23, 25 and 30 ship from the v5 bundle) plus `c1.phrasal-idioms`, v48's executive-communication,
  advanced-negotiation, public-policy and international-relations lessons, v49's economic-analysis,
  technology-ethics, philosophy-and-reasoning and cognitive-bias lessons, v50's statistical-literacy,
  media-analysis, organizational-change and risk-management lessons, v51's research-reports,
  research-methodology, data-interpretation and organizational-culture lessons, v52's
  research-ethics, diversity-inclusion, business-sustainability and semantic-nuance lessons and
  v53's argumentation, proposals, stakeholder-communication and specialist-presentations
  lessons**, **C2 = 30 — all 30 syllabus topics** (1, 2, 3, 6, 7, 10 and 29 from the v5 bundle
  plus v55's sociolinguistics, discourse-analysis, philosophical-texts and speeches lessons,
  v56's persuasion, academic-writing, editing and synthesis lessons, v57's argument-analysis,
  dialects, pragmatics and culture-expressions lessons, v58's allusions, paraphrasing,
  translation and diplomacy lessons, v59's specialist-audiences, narrative, uncertainty and
  sensitive-communication lessons and v60's policy, legal-text and audience-design lessons)),
  3159 exercises
  (8–19/lesson + **reading evidence in 43 lessons: 95 READING items, of which 84 sit on a shared
  3–5 sentence passage in 36 lessons (six per level) — two comprehension questions in the 30 v63
  lessons, four in the six original ones
  and 11 are single-sentence "what does this mean?" items** + meaning items
  + A1 pronunciation drills + P2 recall/production
  items + **a dialogue in every lesson: five or six turns at A2–C2, four at A1 — 1052 speaking
  turns**), 1154 vocabulary entries
  (6/lesson, 8 in the numbers lesson),
  268 knowledge items, placement = 36 questions in six graded bands of 6 (A1→C2, ordered by
  difficulty) — 35 choice plus **one typed `fill_blank`** in the A1 band (`placement.t01`), so the
  test samples production and not recognition alone. Grammar points per level follow the British Council / EQUALS Core Inventory grammar
  tables (verified against examenglish.com/CEFR, Oct 2026): e.g. B1 = 2nd/3rd conditional + reported
  speech + simple passive; C1 = inversion + mixed conditionals + modals in the past; C2 =
  nuance/precision vocabulary.
- **Vocabulary depth (A-10):** every word carries **two** example sentences, each a bilingual object
  `{"en": …, "fa": …}` authored in `vocabulary.json` (1154 words × 2 = 2308 English sentences, all
  with Persian). The shape lives inside the existing `VocabularyEntity.examplesJson` TEXT column —
  the column type never changed, `ContentParser`/`RepositoriesImpl` accept the legacy plain-string
  shape, and `ContentSeeder` clear+inserts every row on the version bump — so **no Room schema
  migration was ever needed for this**: the column type did not change (Room was at `version = 5`
  when the two-example shape landed, and is at **8** today for unrelated reasons — see
  [`debt.md`](debt.md)), and `RoomMigrationTest` keeps proving the existing path. `validateContent` and `VocabularyContentTest`
  both require ≥ 2 examples per word with a non-empty Persian `fa`, and the word-in-example rule is
  checked **per example**, not per entry.
- **A `writing` item is a short connected text, and its floors are the rule (v79; anchors are
  phrases since v81).** The last
  format the exercise model had room for. It is deliberately not `sentence_production` with a bigger
  `minWords`: a produced sentence is graded on anchors **in order** because the order *is* the
  structure under test, while a text is graded on its anchors **anywhere** (a paragraph's connective
  opens the second sentence), a `minWords` floor **and** a `minSentences` floor — so one run-on that
  happens to carry every anchor is not the two sentences that were asked for. **One anchor is still
  one phrase**: separate anchors may sit in any sentence and in any order, but the words inside an
  anchor must be adjacent in that order, so `was collected` is the passive and «the survey was
  useful … we collected the forms» is not. That is why an anchor is never a bare auxiliary: `have`
  was met by possession and `had` by a plain past, so the two perfect-tense items now anchor the
  phrase itself (`have travelled`, `had been`) — a single word that only *marks* a tense is not
  evidence of one. The validator holds the
  item to the same bar it holds the learner to: anchors must exist and be English, `minWords` may not
  go below 8, `minSentences` may not go below 2, and the authored exemplar must itself reach both
  floors, because it is the model shown after a miss. Nothing checks whether the prose is *good* —
  offline and deterministic, like every other grader here, and the limit is named in
  [`debt.md`](debt.md). Twelve ship — two per level, in different lessons and asking for different
  structures (a routine, a past event, an experience, a report paragraph, an argument, a narrative
  analysis beside the v79 six), each built from the lesson's own words and
  its own grammar tip; the type is meant to grow by later authoring, not by a bulk pass, because a
  text is the hardest thing a lesson asks (difficulty 3 wherever the level's band allows it). It is
  its own evidence format (`WRITING_TEXT`) and credits the `PRODUCTION` dimension.
- **A word a lesson ships is asked in that lesson (v78).** The examples rule keeps the word in
  context; nothing held the *exercises* to anything, and 133 words across 75 lessons were taught and
  then not asked again until the review queue — the wrong place for a first retrieval. Each now has
  a `word_recall` in its own lesson (the Persian meaning, name the English word, no options), built
  from the entry's own authored `translation` and `explanationFa`, so nothing was invented: 1154
  words, and `VocabularyContentTest` fails the build when a lesson ships a word its exercises never
  ask. "Asked" is deliberately the weaker claim — the word appears (inflection-tolerant, with the
  irregular allowlist) in an exercise's English **or** is one of its accepted answers; requiring a
  sentence about every word would have been 133 hand-authored context items for no pedagogical gain
  the examples do not already give.
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
- **Every lesson's words are nodes too (v65):** a review card is credited to its lesson's node of
  the card's own type and to nothing else (`KnowledgeEvidence.itemsForCard`), so a lesson that ships
  six words with no VOCABULARY node grades its cards while moving no node — 35 lessons were in that
  state (the seven A1/A2 grammar supplements plus `a2.airport`, `b1.plans`, `b2.environment` and the
  rest of the topic rows the v5 graph covered with grammar nodes whose `skills` list `VOCABULARY`).
  Each now owns **one** vocabulary node: `id` is a topic slug (`vocab.airport-travel`,
  `vocab.question-words`), `title`/`titleFa` are the lesson's own, `skills` are the corpus'
  `["VOCABULARY", "LISTENING"]` pair, and `prerequisites` names the vocabulary node of the lesson
  before it in the same level (a level's first lesson anchors on the last node of the level below), so
  the graph stays one chained path with no new roots. All 192 lessons now own a vocabulary node — 189
  one, three two. `ContentDistributionTest` fails the build when a lesson ships words with no node;
  `validateContent` reports the 268.
- **A vocabulary node names the words it teaches (v82):** those three lessons own two vocabulary
  nodes each, and `itemsForCard` used to credit both for one word — reviewing «subtle» raised
  `vocab.semantic-precision` *and* `vocab.connotation`. A node can now carry a `words` list authored
  from its own lesson's vocabulary (`vocab.semantic-precision` → `ambiguous`, `subtle`, `vague`,
  `distinction`; `vocab.connotation` → `connotation`, `imply`), and a card resolves its own word from
  its id so it moves the one node that names it. Two rules follow, both in `ContentValidator`: a
  named word must be shipped by one of that node's lessons, and for a lesson with two vocabulary
  nodes every word must be claimed by exactly **one** of them — none means the card moves nothing,
  two means the old double credit is back. A node may legitimately claim nothing:
  `vocab.idioms-and-figurative` shares the phrasal-verbs lesson, where every shipped word is a
  phrasal verb. `words` is meaningless on a GRAMMAR/DISCOURSE/PHONOLOGY node and is refused there;
  `ContentDistributionTest` holds the shipped bundle to all of it.
- **Practice bar (A-8):** every GRAMMAR knowledge item needs **at least six grammar-targeting
  exercises** across its lessons, counted with `AnswerChecker.skillOf` — the same rule the mastery
  engine uses, so a vocabulary-heavy lesson does not count as grammar practice. `GrammarCoverageTest`
  fails the build below the bar; add exercises to that topic's lessons.
- **Every exercise names its skill (v63):** `exercise.skill` is now authored on **all 2910 items**;
  the bundle's last 147 untagged ones (52 choice, 41 translation, 54 listening) got theirs, following
  the corpus' own convention (translation is VOCABULARY, listening is LISTENING, a choice item is
  READING with a passage and VOCABULARY/GRAMMAR otherwise). The tag is not cosmetic: it is what
  attributes an answer to a Progress track and what `KnowledgeEvidence` uses to pick the nodes it
  moves, so an untagged item fell back to "evidence for every node of the lesson" — the wildcard
  that tagged content now replaces.
- **Semantic validation (v64, Phase 3):** the gate used to check structure only — ids, ranges, types,
  coverage — so an item could be well-formed and still unusable. Seven rules now check meaning, all
  in `ContentValidator` (so `validateContent` and `ContentValidationTest` share them) and each pinned
  by a test that breaks exactly one:
  1. **a choice item's stem cannot contain its correct option** (normalised, ≥ 4 characters) — the
     question would spell the answer;
  2. **every `accepted` answer must meet the item's own `minWords`** — otherwise the model shown
     after a miss is an answer the item would reject;
  3. **a `listening` item must accept what it says** — one `accepted` answer has to equal the spoken
     `text` after normalisation (`26` = `twenty-six`), or a perfect transcription is marked wrong;
  4. **no item is shipped twice** — same type, stem and options in two lessons is one item paid for
     twice (speaking excluded: a repeated service greeting in two scenes is realistic);
  5. **no recall question is asked twice** — same cue and same accepted word, whatever the ids;
  6. **no entry repeats its own example**, and **no word shows the same example at two levels**
     (70 words are taught twice, and the higher level exists to show a harder context);
  7. comparisons run on a shared `normalize` (case, punctuation, hyphens, apostrophes stripped), so
     `twenty-six`/`twenty six` and `'May'`/`May` never invent a finding.
  `scripts/content_audit.mjs` is the reporting counterpart: it measures the bundle and lists what a
  human should judge (short passages, a one-word dialogue turn), while the gate asserts only what
  can never be right. The v64 audit itself is in [`review-v64.md`](review-v64.md).
- **Explain a miss (A-1):** every grammar and fill-in-the-blank exercise carries a one/two-sentence
  `explanationFa` saying *why* the answer is right (and, for the common wrong option, why it is not).
  Coverage is **every one of the 3159 exercises** (grammar/fill-blank core plus vocabulary meaning,
  translation and listening items). `ExplanationCoverageTest` enforces 100% on the core set and on
  the whole bundle (checklist A-1's floor of 90% was passed, so the bar is now the invariant);
  `validateContent` prints any exercise without one as a non-blocking warning. A new exercise of any
  type ships with its explanation. Every **vocabulary** word additionally carries an authored
  Persian `explanationFa` (a short usage note): the SRS queue is vocabulary-only, so the review
  session shows it after a miss, exactly as the lesson screen does. It is a vocabulary column
  (`VocabularyEntity.explanationFa`, present since Room v5 and unchanged at v8) and a new word without one fails
  `ExplanationCoverageTest`.
- **A conversation per lesson (v33, lengthened in v35):** every lesson ships a `speaking` dialogue
  that makes one exchange in the situation the lesson teaches — **four turns at A1, five or six at
  A2–C2** (v35 added 90 turns, because four lines prove a reply but do not feel like a conversation;
  A1 keeps four since its replies are single clauses) — the check-in desk for `a2.airport`, the doctor's
  surgery for `a2.symptoms`, the interviewer for `b2.interview` — between the learner and the person
  that lesson puts them opposite. A turn carries the partner's English line (`question`), its Persian
  gloss (`questionFa`), the graded replies (`accepted`), a Persian task (`prompt`) and a usage note
  (`explanationFa`); `sceneFa` and `partnerFa` name the situation and the role, and the practice
  screen shows them above the exchange. `validateContent` fails a lesson below four turns, a turn
  without `sceneFa`/`partnerFa`, and a question repeated as its own answer; `DialogueContentTest`
  checks the same rules against the shipped bundle, and `SpeechVocabularyTest` keeps every spoken
  answer inside what the bundled recogniser can hear (no personal names, digits or letter-by-letter
  spelling — `tools/speech-names-allowed.txt` holds the reviewed exceptions). Dialogue turns are not
  lesson exercises: `LessonViewModel` filters them out, because they belong to the conversation
  screen, which asks the question, listens and grades the reply.
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
  run: `balance_answer_positions.mjs`, `content_audit.mjs`, `install_debug.sh` and `release_check.sh`.
  After any content
  edit run `node scripts/balance_answer_positions.mjs`, then `validateContent` + the content tests
  with `--rerun`.
- Supported exercise types: `multiple_choice`, `fill_blank`, `word_recall`, `translation`,
  `sentence_production`, `listening`, `speaking`.
  - `speaking` feeds the lesson's speaking step **and** conversation practice, and both are graded
    offline now: without a `question` the item is a say-it-back turn on the lesson screen, and the
    recogniser scores it with `ConversationScorer` against the first `accepted` sentence — which is
    why that answer must be English, and why a take that only partly matches is still a hit
    (`NEAR`). Two optional fields turn the item into a dialogue turn instead of an echo:
    - `question` — the partner's line in English, spoken by the device before the learner answers.
      English only (TTS speaks it) and never identical to the answer (that would be an echo).
    - `questionFa` — the Persian gloss of `question`. Required **whenever** `question` is present:
      the learner must know what was asked; a gloss without a question is rejected too.
    Without `question` the item stays a repeat-the-model turn. `validateContent` enforces all of
    this, the validator also requires a Persian `prompt`, at least one English `accepted` answer and
    a declared `skill`. Both speaking surfaces move mastery through the recogniser: the lesson's own
    card writes the take as a normal answer (`SubmitExerciseUseCase`, credited to its own `skill`),
    and a dialogue turn credits `SPEAKING` when spoken and `WRITING` when typed
    (`RecordConversationTurnUseCase`). A `RETRY` take writes nothing on either surface.
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
- **Neither variety of English is a wrong answer (P1-4).** The bundle teaches British English
  (`neighbour`, `practise`, `favourite`, `grey`), so before this a US learner who spelled correctly
  was shown a miss. `AnswerChecker` now folds a curated US/UK table word-by-word on **both** sides
  of the comparison (`readings`), so `neighbour` ≡ `neighbor`, `organise` ≡ `organize`, `grey` ≡
  `gray`, `travelling` ≡ `traveling`, `licence` ≡ `license`, inside a sentence and next to a
  contraction too. Only same-meaning pairs are listed (and every inflection explicitly), so it
  cannot turn a different word into the right one — and it is a matching rule, not a spell-check:
  `practiceing` is still `ALMOST`, never `CORRECT`.
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
