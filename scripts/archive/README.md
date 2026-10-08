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
