// P1-1: tag every placement question with the skill it actually measures, so the
// test can be read as a per-skill assessment instead of a single CEFR level.
//   node scripts/p1_tag_placement_skills.mjs
//
// Only the `skill` field is added and `contentVersion` is bumped 13 -> 14 across
// all five bundle files (validateContent requires them to agree). Everything else
// is written back verbatim with the same 2-space formatting the bundle uses.

import { readFileSync, writeFileSync } from "node:fs";

const dir = "app/src/main/assets/content";
const read = (f) => JSON.parse(readFileSync(`${dir}/${f}`, "utf8"));
const write = (f, o) => writeFileSync(`${dir}/${f}`, JSON.stringify(o, null, 2) + "\n");

// Vocabulary/lexis questions: greetings, meaning, opposites, collocation.
const vocabulary = new Set([
  "placement.q01", // choose the correct greeting
  "placement.q16", // concise but rather ______
  "placement.q17", // heavy with ______
  "placement.q18", // sources ______ the hypothesis
  "placement.q19", // what does "apple" mean
  "placement.q21", // opposite of "cheap"
  "placement.q23", // what does "reliable" mean
  "placement.q26", // decided to ______ the decision
  "placement.q28", // closest in meaning to "ambiguous"
  "placement.q29", // deliberately ______
  "placement.q30", // too ______ to support a conviction
]);

const placement = read("placement.json");
let grammar = 0;
for (const q of placement.questions) {
  const skill = vocabulary.has(q.id) ? "VOCABULARY" : "GRAMMAR";
  if (skill === "GRAMMAR") grammar += 1;
  // Rebuild so `skill` sits next to `level` (stable, readable key order).
  const tagged = { id: q.id, type: q.type, level: q.level, skill, question: q.question };
  if (q.questionFa) tagged.questionFa = q.questionFa;
  tagged.options = q.options;
  tagged.correctIndex = q.correctIndex;
  Object.assign(q, tagged);
  // Drop any key that is no longer part of the canonical shape.
  for (const k of Object.keys(q)) if (!(k in tagged)) delete q[k];
}
write("placement.json", placement);

const version = read("lessons.json").contentVersion + 1;
for (const f of ["lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json"]) {
  const o = read(f);
  o.contentVersion = version;
  write(f, o);
}

console.log(
  `tagged ${placement.questions.length} placement questions ` +
    `(${vocabulary.size} VOCABULARY, ${grammar} GRAMMAR); contentVersion ${version - 1} -> ${version}`,
);
