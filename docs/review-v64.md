# Content audit — bundle v64 (Phase 3)

> The Phase-3 deliverable: what the 192-lesson bundle actually contains, what was wrong with it,
> and what the gate now refuses to ship. Method first, so every number below is reproducible:
> `node scripts/content_audit.mjs` prints the inventory, the mix, the reading and dialogue
> measurements, and the findings; `./gradlew validateContent --rerun` is the blocking gate.
> The audit script is a report, not a gate — it lists things a human should judge (a 26-word
> passage at A1 may be right), while the gate only asserts what can never be right.

## 1. What ships today

192 lessons · 2888 exercises · 1154 words · 233 knowledge items · 36 placement questions.

| Level | Lessons | Exercises | per lesson | Words | Knowledge | Dialogues / turns |
|---|---|---|---|---|---|---|
| A1 | 37 | 499 | 13.5 | 224 | 51 | 37 / 148 |
| A2 | 32 | 488 | 15.3 | 192 | 36 | 32 / 178 |
| B1 | 30 | 466 | 15.5 | 180 | 35 | 30 / 177 |
| B2 | 32 | 502 | 15.7 | 192 | 38 | 32 / 189 |
| C1 | 31 | 478 | 15.4 | 186 | 37 | 31 / 183 |
| C2 | 30 | 455 | 15.2 | 180 | 36 | 30 / 177 |

The shape is even across levels — 30–37 lessons, 15–16 items each, 6 words per lesson, 6 turns per
dialogue (A1's 4 is deliberate: an opening and a close with no middle yet). Nothing here is a gap.

## 2. The exercise mix by level

| Level | multiple_choice | fill_blank | translation | listening | word_recall | sentence_production | speaking |
|---|---|---|---|---|---|---|---|
| A1 | 162 | 67 | 31 | 43 | 24 | 24 | 148 |
| A2 | 133 | 60 | 31 | 32 | 27 | 27 | 178 |
| B1 | 117 | 60 | 30 | 30 | 26 | 26 | 177 |
| B2 | 127 | 66 | 32 | 32 | 28 | 28 | 189 |
| C1 | 121 | 60 | 31 | 31 | 26 | 26 | 183 |
| C2 | 110 | 58 | 30 | 30 | 25 | 25 | 177 |

Multiple choice falls from 32% of A1's items to 24% at C2 while production rises — that is the
intended progression, and it is measured here for the first time.

## 3. Where the content is thin (the real finding)

Authored `skill` tags over all 2888 items:

| Skill | Items | Share |
|---|---|---|
| VOCABULARY | 1231 | 42.6% |
| SPEAKING | 1052 | 36.4% |
| GRAMMAR | 308 | 10.7% |
| LISTENING | 198 | 6.9% |
| READING | 95 | 3.3% |
| WRITING | 4 | 0.1% |

Three consequences, all of them by design rather than accident, and all of them the honest limit of
this bundle:

- **Reading is the thinnest measured skill.** 36 of 192 lessons (19%) carry a real passage with two
  or more comprehension questions — six per level, which is what v63 added. The other 156 lessons
  teach reading only through single-sentence items. Reading mastery therefore moves slowly, and a
  learner at A1 has a passage in 6 of 37 lessons.
- **The passages themselves are short.** Median 40 words, minimum 26 (`a1.articles`), maximum 73;
  18 of the 36 are under 40 words. A 26-word passage is a two-sentence text, not a text a learner
  practises reading strategies on. Not fixed here: rewriting 18 passages is authoring work, and the
  ones that exist are correct — just small.
- **Writing has no dedicated items.** Its evidence comes entirely from `sentence_production`
  (156 items, credited to WRITING alongside the item's own skill by the Phase-1 evidence rule), and
  only 4 items are authored `WRITING`. That is consistent with the engine, not a mismatch — but a
  learner's writing track moves only through typed production, and nothing longer than a sentence is
  ever produced. A paragraph task is a future content shape, not a typo.

The other three skills are healthy: speaking is the most frequent item per lesson, listening exists
in all 192 lessons (mostly one item), and vocabulary carries the bulk as expected at A1–B2.

## 4. Defects found and fixed in v64

Each one is a thing a learner could have hit, and each is now also a gate rule (§5) so it cannot
come back.

| # | Where | What was wrong | Why it mattered | Fix |
|---|---|---|---|---|
| 1 | `a1.spelling.ex.02` | Question *"Which word has a double letter?"* listed `letter` as an option | The stem spells the answer; tapping it was free | Reworded to *"Which word has a repeated consonant?"* |
| 2 | `a1.imperatives` listening | *"Please close the door."* shipped with the identical item in `a1.house-objects` (same text, same accepted answer) | One of the two lessons paid a slot for nothing | Rewritten to *"Please sit down and listen."* with its own phonology note |
| 3 | C2 `corroborates` production | Accepted model *"recent evidence corroborates the hypothesis"* is 5 words under `minWords: 6` | A correct answer was shown as the model after being rejected by the same rule | Model corrected to *"the recent evidence…"* |
| 4 | `a2.symptoms.ex.06` recall | Asked «تب» → `fever`, already asked in A1 | Same question twice across levels; the A2 slot taught nothing new | Now «گلودرد» → `sore throat` |
| 5 | `b1.friendship.ex.06` recall | Asked «همسایه» → `neighbour`, already asked in A1 | Same as #4 | Now «اعتماد کردن» → `trust` |
| 6 | `a2.restaurant.word.menu` / `.bill` | Examples repeated the A1 sentences verbatim | The A2 entry exists to show a harder context; it showed none | A2 examples rewritten (*"The menu is in English and Persian."*, *"The bill came to forty dollars."*) |
| 7 | `a1.spelling.word.name` | Example *"My name is Sara."* repeated the A1 introductions entry | Same as #6 | Now *"Spell your name for me, please."* |

The 70 words that appear at two levels were checked pairwise afterwards: no word repeats an example
across its entries any more.

## 5. What the gate now refuses

Seven new rules in `ContentValidator` — the same object behind `./gradlew validateContent` and
`ContentValidationTest`, so the build-blocking check and the suite cannot drift. Each is pinned by a
test that deliberately breaks one rule in an otherwise valid bundle.

1. **A question cannot contain its own answer.** For a choice item, the correct option must not
   appear inside the stem (normalised, ≥4 characters — shorter and it is a coincidence like `a`).
2. **An accepted model must satisfy the item it belongs to.** Every `accepted` answer needs at least
   `minWords` words; otherwise the learner is shown a model that the item itself would mark wrong.
3. **A listening item must accept what it says.** Some accepted answer must equal the spoken `text`
   after normalisation — otherwise a perfect transcription is graded wrong. Numbers are the usual
   cause: `26` versus `twenty-six`, which normalisation now treats as equal.
4. **No item is shipped twice.** Two exercises with the same type, stem and options are one item
   counted twice (speaking excluded: a service greeting repeated in two scenes is realistic).
5. **No recall question is asked twice.** Same Persian cue and same accepted word = the same
   question, whatever the lesson ids say.
6. **An entry cannot ship the same example twice**, and **the same word cannot show the same example
   at two levels** — both mean one of the promised contexts is empty.
7. **Normalisation is shared.** Case, punctuation, hyphens and apostrophes are stripped before any
   comparison, so `twenty-six`/`twenty six` and `'May'`/`May` never produce a finding. This matters
   more than it sounds: a naive comparison across 2888 items and 2308 example sentences reports
   dozens of "duplicates" that differ only in a hyphen or a final full stop, and a gate that cries
   wolf gets switched off. `normalize` is now one function used by every rule above.

## 6. Reviewed and deliberately kept

- **`a1.alphabet.lesson-01` dialogue** has a one-word learner turn. At A1 the task is spelling a
  name aloud; a single letter is the correct answer, not a truncated reply. The audit flags it so a
  human can re-judge it if the lesson ever grows a richer scene.
- **Nine example sentences are shared by two words** — «please sit down» → `please`/`sit`, «my
  mother is a teacher» → `mother`/`teacher`, «the doctor works in a hospital» → `doctor`/`hospital`,
  and so on. Each pair is one scene teaching two words; a learner meeting both is reinforced, not
  penalised. The gate therefore checks duplicates *within* a word and across a word's entries, not
  across different words.

## 7. Left for the next content pass

1. Reading passages in the remaining 156 lessons, and lengthening the 18 passages under 40 words.
2. A longer writing shape than one sentence (a paragraph task) so the WRITING track has content of
   its own rather than a derived credit.
3. Two listening items per lesson instead of one at A2 and above — listening is 6.9% of the bundle
   while speaking is 36%, and the recogniser already exists on the other side.
4. A1's four-turn dialogue is a floor, not a target; the A2–C2 shape (six turns with a middle) is
   what the guided lesson's conversation step was designed around.
