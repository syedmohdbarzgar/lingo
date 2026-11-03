#!/usr/bin/env node
/**
 * Bundle v33 — a real conversation for every lesson.
 *
 * Conversation practice used to be one long queue of `speaking` items gathered
 * from the whole bundle: a learner practising "At the Airport" answered a
 * question about colours two turns later, and most lessons had a single turn —
 * a question and its answer, which is a repetition drill, not a dialogue.
 *
 * This batch gives **every one of the 86 lessons** its own dialogue: four
 * exchanges, in the situation the lesson teaches (the check-in desk for the
 * airport lesson, a doctor's surgery for symptoms, a job interview for the
 * interview lesson), between the learner and the person that lesson puts them
 * opposite. The scene and the partner's role travel with the turns as `sceneFa`
 * / `partnerFa`, so the practice screen can name them without inventing them.
 *
 * It also replaces the older one-turn speaking items. Applied once, then
 * archived: the JSON is the source of truth from here on. A-12 — never re-run
 * this file, it would overwrite later hand edits.
 */

import fs from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { a1Low } from "./v33_data_a1_low.mjs";
import { a1High } from "./v33_data_a1_high.mjs";
import { a2 } from "./v33_data_a2.mjs";
import { bLevels } from "./v33_data_b.mjs";
import { cLevels } from "./v33_data_c.mjs";

const CONTENT = join(
  dirname(fileURLToPath(import.meta.url)),
  "../../app/src/main/assets/content",
);
const VERSION = 33;
const MIN_TURNS = 4;

const read = (file) => JSON.parse(fs.readFileSync(join(CONTENT, file), "utf8"));
const write = (file, value) =>
  fs.writeFileSync(join(CONTENT, file), JSON.stringify(value, null, 2) + "\n");

const hasPersian = (text) => /[\u0600-\u06FF]/.test(text);

const dialogues = [...a1Low, ...a1High, ...a2, ...bLevels, ...cLevels];

const lessons = read("lessons.json");
const byId = new Map(lessons.lessons.map((lesson) => [lesson.id, lesson]));
const bundle = read("exercises.json");

// ---- Authoring checks: fail before touching the bundle ----------------------
const seen = new Set();
const errors = [];
for (const [lessonId, scene, partner, turns] of dialogues) {
  if (seen.has(lessonId)) errors.push(`${lessonId}: duplicated dialogue`);
  seen.add(lessonId);
  if (!byId.has(lessonId)) errors.push(`${lessonId}: no such lesson`);
  if (!hasPersian(scene)) errors.push(`${lessonId}: scene is not Persian`);
  if (!hasPersian(partner)) errors.push(`${lessonId}: partner role is not Persian`);
  if (turns.length < MIN_TURNS) {
    errors.push(`${lessonId}: ${turns.length} turns, the floor is ${MIN_TURNS}`);
  }
  turns.forEach((turn, i) => {
    const [question, questionFa, taskFa, answers, tipFa] = turn;
    const where = `${lessonId} turn ${i + 1}`;
    if (!question || hasPersian(question)) errors.push(`${where}: question must be English`);
    if (!hasPersian(questionFa ?? "")) errors.push(`${where}: questionFa must be Persian`);
    if (!hasPersian(taskFa ?? "")) errors.push(`${where}: taskFa must be Persian`);
    if (!Array.isArray(answers) || answers.length === 0) {
      errors.push(`${where}: no accepted answer`);
      return;
    }
    answers.forEach((answer) => {
      if (!answer || hasPersian(answer)) errors.push(`${where}: answer must be English`);
      if (answer.trim().toLowerCase() === question.trim().toLowerCase()) {
        errors.push(`${where}: the answer is the question`);
      }
    });
    if (!hasPersian(tipFa ?? "")) errors.push(`${where}: tipFa must be Persian`);
  });
}
const missing = lessons.lessons.filter((lesson) => !seen.has(lesson.id));
for (const lesson of missing) errors.push(`${lesson.id}: no dialogue authored`);

if (errors.length) {
  console.error(`v33: ${errors.length} authoring problem(s):`);
  errors.forEach((error) => console.error(`  - ${error}`));
  process.exit(1);
}

// ---- Install ---------------------------------------------------------------
const dialogueLessonIds = new Set(dialogues.map(([lessonId]) => lessonId));
const kept = bundle.exercises.filter(
  (exercise) => !(exercise.type === "speaking" && dialogueLessonIds.has(exercise.lessonId)),
);
const removed = bundle.exercises.length - kept.length;

let counter = 0;
const added = [];
for (const [lessonId, scene, partner, turns] of dialogues) {
  const orderBase = kept
    .filter((exercise) => exercise.lessonId === lessonId)
    .reduce((max, exercise) => Math.max(max, exercise.order ?? 0), 0);
  turns.forEach(([question, questionFa, taskFa, answers, tipFa], i) => {
    counter += 1;
    added.push({
      id: `${lessonId}.dlg.${String(i + 1).padStart(2, "0")}`,
      lessonId,
      order: orderBase + i + 1,
      type: "speaking",
      sceneFa: scene,
      partnerFa: partner,
      prompt: taskFa,
      question,
      questionFa,
      accepted: answers,
      skill: "SPEAKING",
      explanationFa: tipFa,
    });
  });
}

bundle.exercises = [...kept, ...added];
write("exercises.json", bundle);

// ---- One version across the bundle -----------------------------------------
for (const file of ["lessons.json", "vocabulary.json", "exercises.json", "knowledge.json", "placement.json"]) {
  const data = read(file);
  data.contentVersion = VERSION;
  write(file, data);
}

const perLesson = {};
for (const exercise of added) perLesson[exercise.lessonId] = (perLesson[exercise.lessonId] ?? 0) + 1;
const turnCounts = Object.values(perLesson);
console.log(
  `v33: ${added.length} dialogue turns across ${dialogueLessonIds.size} lessons ` +
    `(min ${Math.min(...turnCounts)}, max ${Math.max(...turnCounts)}); ` +
    `${removed} older speaking items replaced; bundle version ${VERSION}`,
);
