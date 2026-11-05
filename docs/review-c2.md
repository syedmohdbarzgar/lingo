# C2 review — the v60 bundle, read at v61

> A read-through of all 30 C2 lessons against three axes — **English correctness**, **Persian
> quality**, **compatibility with the speech engine** — plus a depth audit against the standard a
> v55–v60 lesson ships. This is a review record, not a rulebook: the rules are in
> [`content-pipeline.md`](content-pipeline.md), the topic list in [`curriculum.md`](curriculum.md),
> and the bundle history in [`CHANGELOG-content.md`](CHANGELOG-content.md) (this pass is **60 → 61**).

## What was in scope

| | Count |
|---|---:|
| C2 lessons | 30 |
| Exercises | 445 — 100 multiple choice, 58 fill-blank, 30 translation, 30 listening, 25 recall, 25 production, **177 dialogue turns** |
| Vocabulary entries | 180 (six per lesson), each with two bilingual examples — 360 |
| Teaching pages | 30, every one carrying `## قاعده`, `## جدول`, `## مثال‌ها`, `## خطاهای رایج` |

Two kinds of check ran. **Mechanical** ones swept every string field of those items (including each
option, each accepted answer, each example and each collocation) for the defects a regex can name:
article agreement, doubled words, Arabic `ي`/`ك` in Persian text, double spaces, a space before
punctuation, doubled ZWNJ, digits or proper nouns in a spoken answer. **Read** ones covered the four
thinnest lessons line by line — `c2.literature`, `c2.collocation`, `c2.rhetoric`,
`c2.argumentation` — because a grammar or collocation error cannot be found by any regex.

## English

The mechanical sweep came back clean: **no** `a`/`an` disagreement, **no** doubled word, and **no**
digit or capitalised proper noun inside a spoken answer anywhere in C2 (`SpeechVocabularyTest`
already enforces the last one for the whole bundle). The read-through found one grammar defect and
one explanation that contradicted its own answer key.

| Item | Defect | Now |
|---|---|---|
| `c2.literature.ex.04` | `Critics saw the ending as an ___ …` accepted `metaphor` and `foreshadowing`. Both are *right answers marked correct from a learner who just wrote **an metaphor*** — in the one lesson whose subject is precision | Accepts `allegory` only; recorded in `ContentDistributionTest.reviewedSingleAnswerBlanks` as an article-frame blank, and the explanation says why |
| `c2.collocation.ex.03` | Accepted `take` **and** `accept`, but explained only `take responsibility` — the learner who typed `accept` was told a rule about a different verb | Explanation names both; `do`/`make` remain the error it warns against |

Both were C2-only. The article sweep ran over all 371 blanks in the bundle and found exactly one
other hit, in the opposite direction: `a1.daily-routine.ex.06` accepts `a shower` after its own
`a ___`, so a learner who types the article twice is marked correct rather than told. That is
leniency in an A1 blank, not an error taught as correct, and it is left for an A1 pass — noted here
so it is not lost.

## Persian

Clean on every mechanical axis: **zero** Arabic `ي`/`ك`, **zero** double spaces, **zero** spaces
before punctuation, **zero** doubled ZWNJ in 30 lessons. (The only Latin commas in Persian fields are
inside quoted English examples, which is correct.)

The read-through found **five dialogue tasks written as telegram-style fragments** rather than the
full sentences every v55–v60 lesson uses. They were shipped in the original v5 C2 lessons and were
never revisited when the later batches settled the style:

| Item | Was | Now |
|---|---|---|
| `c2.literature.dlg.01` | `بگو فاصله و ابهام می‌سازد.` — no subject; reads "say distance and ambiguity makes" | `بگو این فاصله‌ها ابهام می‌سازند، نه سرگردانی.` |
| `c2.literature.dlg.02` | `تفکیک کن: ابهام عامدانه.` — verbless | `فرق بگذار: ابهام عامدانه است، نه سهل‌انگاری.` |
| `c2.literature.dlg.04` | `بگو بله، با هشدار و زمینه.` | `بگو بله، ولی با هشدار و زمینه.` |
| `c2.rhetoric.dlg.02` | `تفکیک کن: ابزار در خدمت معنا یا جای آن.` | `فرق بگذار: ابزار در خدمت معناست یا جای آن را گرفته است.` |
| `c2.rhetoric.dlg.04` | `بگو نسخهٔ میانه را، با دلیل.` | `بگو نسخهٔ میانه را انتخاب می‌کنی و دلیلش را می‌گویی.` |
| `c2.argumentation.dlg.06` | `بگو ممنون از مناظرهٔ جدی.` — a bare phrase, not a task | `بگو از مناظرهٔ جدی ممنونم.` |

## Speech engine — the finding that mattered

The C2 read-through is what exposed it, but the defect was in the engine, not the content, and it
affected every level.

`ConversationScorer` builds the Vosk grammar from the accepted answers, so a token the recogniser
cannot hear never reaches the learner. Its word cleaner merged a hyphen instead of splitting it
(`twenty-five` → `twentyfive`) and kept a quote mark glued to a word (`'May'` → `'may'`). Neither
string exists in any English lexicon, so the recogniser dropped them — and a turn containing a
dropped token can never be graded `CORRECT`, however well the learner says it:

| Answer shape | Answers affected | Of them C2 |
|---|---:|---:|
| Hyphenated (`decision-making`, `trade-off`, `de-escalate`, `meta-analysis`) | 41 | 20 |
| Word in quotes (`'May' leaves discretion open`, `'a pressing problem'`) | 24 | 11 |

(One answer carries both shapes, so the two rows are counts by shape, not a partition: 64 distinct
authored answers were ungradable, 30 of them in C2.)

A hyphen is how English *writes* two words, and the recogniser hears two; a quote at either edge of a
word is punctuation, not part of it. `cleanTokens` now splits hyphens and trims edge apostrophes, so
both halves stay hearable and grading compares words with words (`twenty-five` and "twenty five" are
the same answer). `ConversationScorerTest` pins both — a hyphenated answer and a quoted one — and
`SpeechVocabularyTest` tokenises the same way, so the checker and the engine cannot drift apart again.

Two related facts worth knowing, neither changed:

- Apostrophes *inside* a word are still kept (`it's`, `minutes'`), because the scorer expands
  contractions before the grammar is built and the recogniser is asked for the expanded form.
- The bundled `vosk-model-small-en-us-0.15` ships no `graph/words.txt`, so "is this token in the
  model's lexicon?" is still not answerable offline — see [`debt.md`](debt.md). The name/digit rule
  is what CI can enforce today; the hyphen fix removes the class of answer that was silently dead
  regardless of the lexicon.

## Depth — the standard, and who falls short

A v55–v60 lesson ships **nine core exercises** — three meaning choices, two blanks, one recall, one
translation, one listening, one production — plus a six-turn dialogue, six words and twelve examples.
All 30 C2 lessons meet the vocabulary and dialogue halves. Six do not meet the exercise half:

| Lesson | Core | Composition | Missing against the standard |
|---|---:|---|---|
| `c2.literature` | 8 | mc4 · fb2 · tr1 · li1 | `word_recall`, `sentence_production` |
| `c2.collocation` | 8 | mc5 · fb1 · tr1 · li1 | `word_recall`, `sentence_production` |
| `c2.irony` | 8 | mc5 · fb1 · tr1 · li1 | `word_recall`, `sentence_production` |
| `c2.argumentation` | 8 | mc3 · fb1 · wr2 · tr1 · li1 | `sentence_production` |
| `c2.nuance` | 9 | mc4 · fb3 · tr1 · li1 | `word_recall`, `sentence_production` |
| `c2.rhetoric` | 9 | mc3 · fb2 · tr1 · li1 · prod2 | `word_recall` |
| `c2.science` | 11 | mc7 · fb2 · tr1 · li1 | `word_recall`, `sentence_production` |

`c2.science` is worth a note of its own: eleven exercises, but seven of them are the same shape
(five-option meaning choice). Volume without variety trains recognition only.

Why it matters beyond tidiness: `sentence_production` and `word_recall` are the two item types that
feed ProductionRecall — free production with anchors, and active recall from a Persian prompt. A
lesson without them is a lesson the production engine can never schedule, so a C2 learner meets it
as recognition practice while the rest of the path asks for production.

### Proposals — ready to author

Each row is a complete item, written to satisfy the gates: anchors appear in the exemplar **in
order**, `minWords` is at least the anchor word count, the prompt is Persian, and the recalled word
is one the lesson already teaches (so no new headword is needed for the recall item).

**`c2.literature`** — one recall, one production:

```json
{ "type": "word_recall", "prompt": "تمثیل اخلاقی", "accepted": ["allegory"],
  "explanationFa": "allegory داستانی است که معنای پنهان اخلاقی یا سیاسی دارد." }

{ "type": "sentence_production",
  "prompt": "نویسنده با تصاویرِ آتش و یخ، سرنوشت شخصیت اصلی را پیشاپیش نشان می‌دهد.",
  "mustUse": ["imagery", "fire"], "minWords": 9,
  "accepted": ["the author uses imagery of fire and ice to foreshadow the protagonist's fate"],
  "hintFa": "imagery (تصویرسازی) و fire را در یک جملهٔ کامل به کار ببر.",
  "explanationFa": "imagery ابزار پیش‌بینی است: تصویر آتش و یخ، سرنوشت را پیش از روایت نشان می‌دهد." }
```

**`c2.collocation`** — one recall, one production:

```json
{ "type": "word_recall", "prompt": "هم‌نشینی طبیعی واژه‌ها", "accepted": ["collocation"],
  "explanationFa": "collocation یعنی هم‌نشینی طبیعی واژه‌ها، نه قاعدهٔ گرامری." }

{ "type": "sentence_production",
  "prompt": "ترجمهٔ تحت‌اللفظی هم‌نشینی را می‌شکند و جمله غیرطبیعی می‌شود.",
  "mustUse": ["collocation", "unnatural"], "minWords": 9,
  "accepted": ["a literal translation breaks the collocation and leaves the sentence sounding unnatural"],
  "hintFa": "collocation و unnatural را در یک جمله به هم وصل کن.",
  "explanationFa": "هم‌نشینی با ترجمهٔ واژه‌به‌واژه می‌شکند و نتیجه غیرطبیعی می‌شود." }
```

**`c2.irony`** — one recall, one production:

```json
{ "type": "word_recall", "prompt": "کنایه", "accepted": ["irony"],
  "explanationFa": "irony یعنی گفتن خلافِ آنچه منظور است." }

{ "type": "sentence_production",
  "prompt": "لحن طعنه‌آمیزش کاری کرد که توهین شبیه تعریف به گوش برسد.",
  "mustUse": ["sardonic", "compliment"], "minWords": 9,
  "accepted": ["his sardonic tone made the insult sound like a compliment"],
  "hintFa": "sardonic (طعنه‌آمیز) و compliment را کنار هم بگذار.",
  "explanationFa": "طنز تلخ، تعریف و توهین را جابه‌جا می‌کند — همان کاری که irony می‌کند." }
```

**`c2.argumentation`** — one production (it already carries two recalls):

```json
{ "type": "sentence_production",
  "prompt": "او ادعای من را تحریف کرد و بعد به آن حمله کرد؛ این مغالطهٔ آدم‌کاهی است.",
  "mustUse": ["distort", "fallacy"], "minWords": 9,
  "accepted": ["he distorted my claim and then attacked it which is a straw man fallacy"],
  "hintFa": "distort و fallacy را در یک جمله به کار ببر و نام مغالطه را بیاور.",
  "explanationFa": "تحریف ادعای مخالف و حمله به آن نسخهٔ ساختگی، مغالطهٔ straw man است." }
```

**`c2.nuance`** and **`c2.science`** need the same two items each; **`c2.rhetoric`** needs only the
recall (`prompt` «ایجاز» → `concision`, which it already teaches, and its two production items
already cover the grammar of the page).

### Proposals — two more words for the four thinnest lessons

The syllabus is complete (180/180), so the only way to deepen is to give an existing lesson more
material. Six words is the house standard and stays the floor; these are the lessons where a 8-word
lesson buys the most, and every headword below is **fresh** — checked against the whole bundle
(`vocabulary.json` teaches it nowhere, and no meaning item asks it):

| Lesson | Headword | Persian | Why this one |
|---|---|---|---|
| `c2.literature` | `conceit` | تمثیل گسترده، تشبیه پرتناقض | The lesson teaches `metaphor`; `conceit` is the extended metaphor that literary criticism actually names — and it is not `concept` |
| `c2.literature` | `verisimilitude` | باورپذیریِ داستان | Next to `allegory` and `imagery`, the third axis a critic argues on |
| `c2.collocation` | `delexical` | رنگ‌باخته (فعلِ بی‌معنای مستقل) | The lesson's own meaning item asks it (`have a shower`) without ever teaching the term |
| `c2.collocation` | `idiomaticity` | اصطلاح‌گونگی | Names the property the lesson is about, and pairs with `natural`/`unnatural` |
| `c2.irony` | `sardonic` | طعنه‌آمیز، تلخ | `deadpan` and `litotes` are already taught; `sardonic` is the gap, and the proposed production item uses it |
| `c2.irony` | `tongue-in-cheek` | شوخ‌طبعانه و نه جدی | A hyphenated multi-word entry is fine for a *word*; the new scorer splits it for speech, so it stays speakable |
| `c2.argumentation` | `whataboutism` | مغالطهٔ «شما هم» | The counterpart of `straw man` that C2 debate actually meets |
| `c2.argumentation` | `false dilemma` | دوگانهٔ کاذب | Completes the fallacy set beside `straw man` and `equivocation` |

## What was deliberately left alone

- **`c2.literature.ex.03`** accepts `evokes` beside `depicts`/`portrays`. `evoke the plight` is real
  usage, if not the tightest collocation; a wrong-but-idiomatic answer is not a defect to delete.
- **Three-answer blanks** (`c2.nuance.ex.03/.04`, `c2.rhetoric.ex.04`, `c2.science.ex.03/.04`) are
  legitimate: every accepted answer is grammatical in the frame and means the same thing. The
  distribution skews to two by authoring habit, not by rule.
- **The exercise gap itself.** Closing it means new authored content, and the proposal above is
  deliberately not applied in v61 — v61 is the defect pass, with counts unchanged (192 lessons, 1154
  words, 2828 exercises, 233 knowledge items).
