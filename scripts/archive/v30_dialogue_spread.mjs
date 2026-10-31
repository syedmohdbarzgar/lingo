#!/usr/bin/env node
/**
 * Bundle v30 — a dialogue turn for every lesson that had none.
 *
 * Conversation practice shipped with 12 turns, all inside five lessons (A1
 * greetings, A2 restaurant / directions, B1 plans, B2 interview). Every A1 lesson
 * added by v27/v28 and every A2 travel lesson added by v29 therefore had nothing to
 * say: the learner could finish a whole level without ever speaking.
 *
 * This batch adds one authored exchange to each of those 28 lessons — the partner's
 * English question, its Persian gloss, the reply the learner is graded against (plus
 * the natural equivalents), a Persian description of the task and a one-line note.
 *
 * Applied once, then archived: the JSON is the source of truth from here on.
 * A-12 — never re-run this file, it would overwrite later hand edits.
 */

import fs from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { turns } from "./v30_dialogue_spread_data.mjs";

const CONTENT = join(dirname(fileURLToPath(import.meta.url)), "../../app/src/main/assets/content");
const VERSION = 30;

const read = (file) => JSON.parse(fs.readFileSync(join(CONTENT, file), "utf8"));
const write = (file, value) =>
  fs.writeFileSync(join(CONTENT, file), JSON.stringify(value, null, 2) + "\n");

const exercisesJson = read("exercises.json");
const lessonsJson = read("lessons.json");
const vocabularyJson = read("vocabulary.json");
const knowledgeJson = read("knowledge.json");
const placementJson = read("placement.json");

const lessonIds = new Set(lessonsJson.lessons.map((l) => l.id));
const existingIds = new Set(exercisesJson.exercises.map((e) => e.id));

const nextId = (lessonId) => {
  const orders = exercisesJson.exercises
    .filter((e) => e.lessonId === lessonId)
    .map((e) => e.order);
  const order = (orders.length ? Math.max(...orders) : 0) + 1;
  return { order, id: `${lessonId.replace(".lesson-01", "")}.ex.${String(order).padStart(2, "0")}` };
};

let added = 0;
for (const [slug, prompt, question, questionFa, accepted, explanationFa] of turns) {
  const lessonId = `${slug}.lesson-01`;
  if (!lessonIds.has(lessonId)) throw new Error(`no such lesson: ${lessonId}`);
  if (exercisesJson.exercises.some((e) => e.lessonId === lessonId && e.type === "speaking")) {
    throw new Error(`${lessonId} already has a speaking turn — bundle v30 is already applied`);
  }
  const { order, id } = nextId(lessonId);
  if (existingIds.has(id)) throw new Error(`exercise id collision: ${id}`);
  exercisesJson.exercises.push({
    id,
    lessonId,
    order,
    type: "speaking",
    prompt,
    question,
    questionFa,
    accepted,
    skill: "SPEAKING",
    explanationFa,
  });
  existingIds.add(id);
  added += 1;
}

for (const json of [lessonsJson, vocabularyJson, exercisesJson, knowledgeJson, placementJson]) {
  json.contentVersion = VERSION;
}

write("lessons.json", lessonsJson);
write("vocabulary.json", vocabularyJson);
write("exercises.json", exercisesJson);
write("knowledge.json", knowledgeJson);
write("placement.json", placementJson);

console.log(
  `v${VERSION}: +${added} dialogue turns → ${exercisesJson.exercises.length} exercises ` +
    `(${exercisesJson.exercises.filter((e) => e.type === "speaking").length} speaking)`,
);
