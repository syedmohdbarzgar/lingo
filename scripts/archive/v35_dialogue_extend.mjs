// v35 — longer conversations at A2 and above.
//
// A four-turn exchange is one opening, one middle and one close: enough to prove
// the learner can answer, not enough to feel like a conversation. Every lesson at
// A2–C2 gains one or two more turns (five or six in total) that continue the same
// exchange, in the same place, with the same person. A1 stays at four: its replies
// are single clauses and a longer scene would be repetition rather than practice.
//
// One-shot batch, already applied — **never re-run**. Content lives in
// `v35_dialogue_data_1.mjs` (A2) and `v35_dialogue_data_2.mjs` (B1–C2).
//
// A new turn inherits `sceneFa`/`partnerFa` from the lesson's first turn, so the
// place and the role cannot drift mid-dialogue, and it is checked against the same
// rules the shipped turns obey: English, no digits, no proper noun inside a
// sentence, and never an echo of the question.
import { readFileSync, writeFileSync } from "node:fs";
import data1 from "./v35_dialogue_data_1.mjs";
import data2 from "./v35_dialogue_data_2.mjs";

const DIR = "app/src/main/assets/content/";
const VERSION = 35;
const EXTENSIONS = [...data1, ...data2];

const read = (name) => JSON.parse(readFileSync(DIR + name, "utf8"));
const write = (name, data) => writeFileSync(DIR + name, JSON.stringify(data, null, 2) + "\n");

const lessons = read("lessons.json");
const exercises = read("exercises.json");
const vocabulary = read("vocabulary.json");
const placement = read("placement.json");
const knowledge = read("knowledge.json");

const speakingByLesson = new Map();
for (const e of exercises.exercises) {
  if (e.type !== "speaking") continue;
  if (!speakingByLesson.has(e.lessonId)) speakingByLesson.set(e.lessonId, []);
  speakingByLesson.get(e.lessonId).push(e);
}
for (const turns of speakingByLesson.values()) turns.sort((a, b) => a.order - b.order);

const alreadyExtended = exercises.exercises.some((e) => e.id.endsWith(".dlg.05"));
if (alreadyExtended) throw new Error("v35 already applied (a .dlg.05 turn exists) — aborting");

const levelOf = new Map(lessons.lessons.map((l) => [l.id, l.level]));
const ALLOWED_CAPITALS = new Set([
  "i", "i'm", "i'll", "i'd", "i've", "okay", "ok",
]);
const DAYS = new Set([
  "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
  "january", "february", "march", "april", "may", "june", "july", "august",
  "september", "october", "november", "december",
]);

/** The recogniser speaks words, not names or numbers (SpeechVocabularyTest). */
function spokenProblems(id, reply) {
  const problems = [];
  if (/\d/.test(reply)) problems.push(`${id}: digit in "${reply}"`);
  let opens = true;
  for (const raw of reply.split(/\s+/)) {
    const word = raw.replace(/[^A-Za-z']/g, "");
    if (word) {
      const lower = word.toLowerCase();
      const day = DAYS.has(lower) || DAYS.has(lower.replace(/s$/, ""));
      if (!opens && /^[A-Z]/.test(word) && !ALLOWED_CAPITALS.has(lower) && !day) {
        problems.push(`${id}: proper noun "${word}" inside a sentence`);
      }
    }
    opens = /[.?!]$/.test(raw);
  }
  return problems;
}

const problems = [];
let added = 0;

for (const entry of EXTENSIONS) {
  const { lessonId, turns } = entry;
  const level = levelOf.get(lessonId);
  if (!level) problems.push(`${lessonId}: not a lesson`);
  if (level === "A1") problems.push(`${lessonId}: A1 stays at four turns`);
  const existing = speakingByLesson.get(lessonId) ?? [];
  if (existing.length < 4) problems.push(`${lessonId}: has only ${existing.length} turns`);

  const first = existing[0];
  if (!first) continue;
  const sceneFa = first.sceneFa;
  const partnerFa = first.partnerFa;
  if (existing.some((t) => t.sceneFa !== sceneFa || t.partnerFa !== partnerFa)) {
    problems.push(`${lessonId}: existing turns disagree on scene/partner`);
  }

  let order = existing.reduce((max, t) => Math.max(max, t.order), 0);
  const startCount = existing.length;

  turns.forEach((t, i) => {
    const id = `${lessonId}.dlg.${String(startCount + i + 1).padStart(2, "0")}`;
    if (t.accepted.length < 2) problems.push(`${id}: needs two replies`);
    t.accepted.forEach((reply) => {
      if (/[\u0600-\u06FF]/.test(reply)) problems.push(`${id}: reply is not English`);
      if (reply.trim().toLowerCase() === t.question.trim().toLowerCase()) {
        problems.push(`${id}: reply echoes the question`);
      }
      problems.push(...spokenProblems(id, reply));
    });
    if (!t.sceneFaPrompt && !t.prompt) problems.push(`${id}: no Persian task`);
    if (!t.note || t.note.length < 15) problems.push(`${id}: explanation too short`);

    exercises.exercises.push({
      id,
      lessonId,
      type: "speaking",
      order: order + i + 1,
      sceneFa,
      partnerFa,
      prompt: t.prompt,
      question: t.question,
      questionFa: t.questionFa,
      accepted: t.accepted,
      skill: "SPEAKING",
      explanationFa: t.note,
    });
    added += 1;
  });

  if (turns.length + startCount > 6) problems.push(`${lessonId}: more than six turns`);
  if (turns.length + startCount < 5) problems.push(`${lessonId}: fewer than five turns`);
}

if (problems.length) {
  throw new Error(`v35 content problems:\n${problems.join("\n")}`);
}

for (const root of [lessons, vocabulary, exercises, placement, knowledge]) {
  root.contentVersion = VERSION;
}

write("lessons.json", lessons);
write("vocabulary.json", vocabulary);
write("exercises.json", exercises);
write("placement.json", placement);
write("knowledge.json", knowledge);

const speaking = exercises.exercises.filter((e) => e.type === "speaking").length;
console.log(
  `v${VERSION}: +${added} dialogue turns across ${EXTENSIONS.length} lessons — ` +
    `${exercises.exercises.length} exercises, ${speaking} speaking turns`,
);
