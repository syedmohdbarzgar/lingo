# Content changelog — bundle milestones

> Split out of `AGENTS.md` §5 (checklist v10). The current content **rules** live in
> [`docs/content-pipeline.md`](content-pipeline.md); this file is only the record of which
> `contentVersion` bump shipped what. The one-shot batches that applied each change are
> catalogued in [`scripts/archive/README.md`](../scripts/archive/README.md) — **never re-run one.**

Every content change bumps `contentVersion` **identically in all five** JSON files
(`lessons`, `vocabulary`, `exercises`, `placement`, `knowledge`); the current bundle is **22**.

| Bundle | Change | Batch |
|---|---|---|
| **v5** | B1–C2 content complete — 6 lessons per level A1–B2, 7 for C1/C2 (the "v5 bundle") | `enrich_content.mjs` |
| **12 → 13** | Content correctness pass (P0-9): same-lesson meaning distractors, two vocabulary examples that never named their word. Counts stayed 45/270/383/30/83 (edits in place) | `p0_fix_a1a2_content.mjs` |
| **13 → 14** | Placement questions gained authored `skill` tags (P1-1); no counts changed | `p1_tag_placement_skills.mjs` |
| **14 → 15** | Foundations at the very front (A-7): alphabet, spelling-a-name and numbers 0–100 lessons before Greetings | `p1_foundation_lessons.mjs` |
| **18 → 19** | Bilingual vocabulary examples (A-10): two `{en, fa}` sentences per word | `p1_bilingual_examples.mjs` |
| **19 → 20** | Reading comprehension + En→Fa meaning items (A-5) and one reading item per placement band (A-6) | `p1_reading_and_meaning.mjs` |
| **20 → 21** | Retrieval/production shapes (P2): two `word_recall` and two `sentence_production` items per level, plus the overlap gate is a later code change | `p2_production_recall.mjs` |
| **22 → 23** | Speaking practice goes live: two `speaking` items on the A2 Directions lesson (listen to the TTS model, record, compare) — self-assessed, no recogniser | hand edit (`speaking` items appended; option balance re-run) |
| **21 → 22** | The typed placement item (A-5/A-6 round): the A1-band MCQ `placement.q20` became a `fill_blank` (`placement.t01`) in the same slot — six bands of six and the 11/19/6 skill counts unchanged; option-slot balance re-run | `p3_typed_placement.mjs` |

Also worth remembering: the A1/A2 grammar-gap lessons (A-9) added lessons with no placement change,
and A-6 later grew each placement band to six questions (36 total) after A-9's re-check.
