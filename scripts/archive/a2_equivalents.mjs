/**
 * One-shot authoring pass for checklist A-2 (equivalent answers).
 *
 * - Appends equivalents to single-answer fill_blank exercises. Appends only:
 *   accepted[0] stays the authored answer that the UI shows as «پاسخ درست».
 * - Bumps contentVersion to 7 in all five bundle files (identical value is
 *   required — validateContent rejects mismatches).
 *
 * Reviewed single-answer blanks that are intentionally NOT given equivalents
 * live in ContentDistributionTest.REVIEWED_SINGLE_ANSWER_BLANKS with reasons.
 */
import fs from "node:fs";

const DIR = "app/src/main/assets/content";

const equivalents = {
  "a1.daily-routine.ex.02": ["have", "take"],
  "a1.family.ex.03": ["mum", "mom"],
  "a1.introductions.ex.03": ["see"],
  "a1.shopping.ex.03": ["inexpensive", "affordable"],
  "a2.restaurant.ex.03": ["have", "get"],
  "a2.directions.ex.03": ["by"],
  "a2.health.ex.04": ["have"],
  "b1.work.ex.03": ["visited"],
  "b1.plans.ex.04": ["start", "do"],
  "b1.money.ex.03": ["buy"],
  "b2.environment.ex.03": ["increase", "go up"],
  "b2.technology.ex.04": ["applied"],
  "b2.interview.ex.04": ["meeting"],
  "b2.media.ex.04": ["advertise", "sell"],
  "b2.problems.ex.03": ["comment"],
  "c1.phrasal-idioms.ex.04": ["switch"],
  "c1.persuasion.ex.04": ["did he realize"],
  "c2.rhetoric.ex.03": ["had"],
};

const path = `${DIR}/exercises.json`;
const raw = fs.readFileSync(path, "utf8");
const data = JSON.parse(raw);
const newline = raw.endsWith("\n") ? "\n" : "";

const byId = new Map(data.exercises.map((e) => [e.id, e]));
let changed = 0;
for (const [id, answers] of Object.entries(equivalents)) {
  const ex = byId.get(id);
  if (!ex) throw new Error(`unknown exercise id: ${id}`);
  if (ex.type !== "fill_blank") throw new Error(`${id} is ${ex.type}, expected fill_blank`);
  const before = ex.accepted.length;
  for (const a of answers) if (!ex.accepted.includes(a)) ex.accepted.push(a);
  if (ex.accepted.length !== before) changed++;
  console.log(`${id}: [${ex.accepted.join(", ")}]`);
}
data.contentVersion = 7;
fs.writeFileSync(path, JSON.stringify(data, null, 2) + newline);
console.log(`equivalents added in ${changed}/${Object.keys(equivalents).length} exercises; exercises.json -> v7`);

for (const f of ["lessons.json", "vocabulary.json", "placement.json", "knowledge.json"]) {
  const p = `${DIR}/${f}`;
  let text = fs.readFileSync(p, "utf8");
  if (!/"contentVersion":\s*6/.test(text)) throw new Error(`${f}: expected contentVersion 6`);
  text = text.replace(/"contentVersion":\s*6/, '"contentVersion": 7');
  fs.writeFileSync(p, text);
  console.log(`${f}: contentVersion 6 -> 7`);
}
