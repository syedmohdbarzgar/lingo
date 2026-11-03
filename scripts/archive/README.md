# Archived content batches

Every `*.mjs` in this folder is a **one-shot batch that has already been applied**
to the JSON bundle. They are kept only as the record of *how* a change was made —
the JSON files are the source of truth, not these scripts.

**Never re-run one.** Each script guards itself against a second run, but the guard
only knows the state it was written against: a later hand edit would be silently
overwritten by an unconditional re-run, and the bundle would go backwards.

The two things you still run from `scripts/` after a content edit:

```bash
node scripts/balance_answer_positions.mjs   # restore correct-answer balance
./gradlew validateContent                   # content gate (also runs in preBuild)
```

then the content tests with `--rerun` (they read the assets through `File`, so
Gradle treats them as up to date otherwise).

| Batch | What it applied |
|---|---|
| `enrich_content.mjs` | The original bundle generator (vocabulary, exercises, placement, knowledge) |
| `a1_explanations*.mjs`, `a1_vocab_explanations.mjs` | `explanationFa` for the A1 lessons and every vocabulary word (A-1) |
| `a2_equivalents.mjs` | Second accepted answers for reviewed blanks |
| `a7_phonology_a1.mjs` | A1 `## تلفظ` sections, PHONOLOGY nodes, guided listening drills (A-7) |
| `a8_expand_grammar_tips.mjs` | Multi-section `grammarTipFa` format (A-8) |
| `a9_add_grammar_lessons.mjs`, `a9_lesson_exercises.mjs`, `a9_topup_exercises.mjs` | The A1/A2 grammar-gap lessons and their practice (A-9) |
| `p0_fix_a1a2_content.mjs` | The A1/A2 correctness pass: same-lesson meaning distractors, two wrong examples (P0-9) |
| `p1_foundation_lessons.mjs` | The alphabet, spelling and numbers lessons + their nodes (A-7) |
| `p1_tag_placement_skills.mjs` | Authored `skill` on every placement question (P1-1) |
| `p1_tag_blank_skills.mjs` | Authored `skill` on every fill-in-the-blank (A-2b) |
| `p1_grammar_topup.mjs` | Grammar practice for the topics the honest blank tagging left short of the six-exercise bar (A-8) |
| `p1_bilingual_examples.mjs` | Two bilingual {en, fa} example sentences per word; bundle 18 → 19 (A-10) |
| `p1_reading_and_meaning.mjs` | One 3–5-sentence reading passage + three comprehension questions per level and an En→Fa "choose the meaning" item; one reading item per placement band; bundle 19 → 20 (A-5, A-6) |
| `p2_production_recall.mjs` | The retrieval/production shapes: two `word_recall` and two `sentence_production` items per level, on lessons that already teach the word or the structure; bundle 20 → 21 (v8 P2) |
| `p3_typed_placement.mjs` | The typed placement item: the A1-band MCQ `placement.q20` swapped for a `fill_blank` in the same slot (`placement.t01`), bundle 21 → 22 (A-5/A-6 round) |
| `v24_content_quality.mjs` | The v24 quality pass: seven misleading answers fixed, an A1 item de-levelled, four twice-taught words replaced, and two extra items for every lesson under eight exercises (467 → 482) |
| `v27_a1_syllabus.mjs` | The first batch of the 180-topic syllabus: six A1 lessons (countries, age & birthday, days & months, time, colours, classroom objects) with their words, nine exercises each and their knowledge nodes; bundle 26 → 27 (48 → 54 lessons) |
| `v28_a1_syllabus.mjs` (+ `_data.mjs`, `_data2.mjs`) | The rest of the A1 syllabus: sixteen lessons (jobs, belongings, home & rooms, household objects, food & drinks, fruit & vegetables, clothes, body parts, animals, hobbies, places in town, directions, public transport, café & restaurant, describing people, where I live) — **A1 now ships all 30 topics**; the batch derives the nine exercises of each lesson from its authored words/blanks, so the data files hold only hand-written content; bundle 27 → 28 (54 → 70 lessons) |
| `v30_dialogue_spread.mjs` (+ `_data.mjs`) | A dialogue turn for every lesson that had none: one authored exchange (English question, Persian gloss, graded reply + equivalents, task, note) added to the 28 v27/v28 A1 and v29 A2 lessons, so conversation practice is not confined to five lessons; bundle 29 → 30 (12 → 40 speaking turns, 772 exercises) |
| `v29_a2_travel.mjs` (+ `_data.mjs`) | A2 starts shipping: six travel and staying lessons (hotel booking, plane ticket, hotel check-in, train & bus, travel information, holiday plans) with six words, nine exercises and a knowledge node each, linked to the grammar node that teaches their structure; bundle 28 → 29 (70 → 76 lessons) |
| `v32_a2_batch.mjs` (+ `_data.mjs`) | A2 topics 8, 9, 10, 12, 13 and 15–19: buying clothes, returning an item, payments, symptoms, the pharmacy, childhood memories, celebrations, invitations, phone conversations and simple emails — six words, nine exercises and a vocabulary node each, each lesson joined to its grammar node; bundle 31 → 32 (76 → 86 lessons, 772 → 862 exercises) |
| `v33_lesson_dialogues.mjs` (+ `v33_data_a1_low.mjs`, `v33_data_a1_high.mjs`, `v33_data_a2.mjs`, `v33_data_b.mjs`, `v33_data_c.mjs`) | A four-turn dialogue in **every** lesson, in the situation that lesson teaches, with the partner's role and the scene authored beside the turns; the 40 old one-line turns were replaced rather than kept; bundle 32 → 33 (862 → 1166 exercises, 40 → 344 speaking turns) |
| `v33_speech_fixes.mjs` | Second pass of the same batch: every answer the bundled recogniser cannot hear (personal names, digits, letter-by-letter spelling) rewritten after `SpeechVocabularyTest` flagged it; counts unchanged |
| `v35_dialogue_extend.mjs` (+ `v35_dialogue_data_1.mjs`, `v35_dialogue_data_2.mjs`) | A2–C2 conversations grow from four turns to five or six (+90 turns across 58 lessons), continuing the same exchange with the same partner; the new turn inherits the lesson's scene and role, and the runner rejects an answer with a digit, a proper noun inside the sentence or an echo of the question; A1 stays at four; bundle 34 → 35 (1283 → 1373 exercises, 380 → 470 speaking turns) |
| `v34_a2_batch.mjs` (+ `v34_a2_data_1.mjs`, `v34_a2_data_2.mjs`, `v34_a2_data_3.mjs`) | A2 topics 21–27, 29 and 30 — renting a home, household problems, future plans, comparing, opinions, asking for help, sports, work and everyday problems — **A2 now ships all 30 topics**; six words, nine exercises, a four-turn dialogue and a vocabulary node each, every lesson joined to its grammar node; the runner holds the validity self-checks (word-in-example, two accepted blanks, anchor floor, no echo turn, recall cue is Persian) so a data typo fails before the JSON is written; bundle 33 → 34 (86 → 95 lessons, 1166 → 1283 exercises) |
