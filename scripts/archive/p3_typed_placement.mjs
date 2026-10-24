// scripts/archive/p3_typed_placement.mjs — A-5/A-6 round: the typed placement item (one-shot).
//
// The placement test was 100% multiple choice, so it only ever measured
// recognition: a learner could place into B1 while never having produced a
// single word. This batch swaps ONE A1-band MCQ (`placement.q20`, grammar,
// correct answer at slot 0) for a typed `fill_blank` in the same slot, so:
//
//   * the band structure is untouched — still six bands of six (36 total),
//     the scorer, the adaptive stop and every band-index test keep working;
//   * skill counts are untouched — the item is GRAMMAR like the one it
//     replaces (11 VOCABULARY / 19 GRAMMAR / 6 READING stays true);
//   * option-slot balance survives — the remaining 35 MCQs hold 8/9/9/9
//     across the four slots, still every slot covered, none over half.
//
// The A1 choice is deliberate: third-person -s is the first productive form an
// A1 learner owns, so the item reads as a real production signal without
// gating a beginner out of the band they belong in.
//
// bundle 21 → 22 (all five files, per AGENTS.md §5).
//
// Usage: node scripts/archive/p3_typed_placement.mjs
// NEVER re-run against a bundle it was not written for — the guard below only
// knows the state described here (see scripts/archive/README.md).

import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const contentDir = join(dirname(fileURLToPath(import.meta.url)), "../../app/src/main/assets/content");
const files = ["lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json"];

const TYPED_ITEM = {
  id: "placement.t01",
  type: "fill_blank",
  level: "A1",
  sentence: "Sara ___ tea every morning.",
  accepted: ["drinks"],
  skill: "GRAMMAR",
  // A-1: every fill-in-the-blank says why the answer is right — here the
  // third-person -s rule. The screen renders the sentence with its own Persian
  // section header, so no questionFa field is authored (the parser ignores it).
  explanationFa:
    "فاعِل «Sara» سوم‌شخصِ مفرد است و فعل در زمانِ حالِ ساده s می‌گیرد؛ پس صحیح «drinks» است نه «drink».",
};

// --- guard: refuse to run twice ------------------------------------------
const placementPath = join(contentDir, "placement.json");
const placement = JSON.parse(readFileSync(placementPath, "utf8"));
if (placement.questions.some((q) => q.type !== "multiple_choice")) {
  console.error("p3_typed_placement.mjs: placement.json already has a typed item — already applied.");
  process.exit(1);
}
const target = placement.questions.findIndex((q) => q.id === "placement.q20");
if (target < 0) {
  console.error("p3_typed_placement.mjs: placement.q20 not found — the bundle is not the state this batch was written against.");
  process.exit(1);
}
if (placement.questions.length !== 36) {
  console.error(`p3_typed_placement.mjs: expected 36 questions, found ${placement.questions.length}.`);
  process.exit(1);
}

// --- apply: swap the A1-band MCQ for the typed item, in place ------------
placement.questions[target] = TYPED_ITEM;
writeFileSync(placementPath, JSON.stringify(placement, null, 2) + "\n");

// --- bump the bundle version in all five files together (AGENTS.md §5) ---
for (const file of files) {
  const path = join(contentDir, file);
  const raw = readFileSync(path, "utf8");
  if (!raw.includes('"contentVersion": 21')) {
    console.error(`${file}: contentVersion 21 not found — refusing to guess.`);
    process.exit(1);
  }
  writeFileSync(path, raw.replace('"contentVersion": 21', '"contentVersion": 22'));
}

console.log(
  `p3_typed_placement.mjs: replaced placement.q20 with ${TYPED_ITEM.id} at band slot ${target} (band ${Math.floor(target / 6)}); bundle 21 → 22.`,
);
console.log("Next: node scripts/balance_answer_positions.mjs, then ./gradlew validateContent and the content tests with --rerun.");
