// v54 — the two B2 syllabus topics that never shipped: 29 (career decisions)
// and 30 (evaluating different viewpoints). This batch **closes B2**: all
// thirty B2 syllabus topics ship, with `b2.interview`/`b2.research` outside the list.
//
// One-shot batch, already applied — **never re-run** (see README in this folder).
// The JSON bundle is the source of truth; this file is the record of how the two
// lessons were authored. Content lives beside it in `v54_b2_data_1.mjs`.
//
// Each lesson ships the full standard: a four-section teaching page, six words
// with two bilingual examples and a usage note, nine exercises (three meaning
// choices, two blanks with two accepted answers, recall, translation, guided
// listening, free production) and a **six-turn dialogue** in the situation the
// lesson teaches, plus its vocabulary knowledge node. Lessons also join the
// grammar node that teaches their structure.
import { readFileSync, writeFileSync } from "node:fs";
import data1 from "./v54_b2_data_1.mjs";


const DIR = "app/src/main/assets/content/";
const VERSION = 54;
const BATCH = [...data1];
const LEVEL = "B2";
const DIALOGUE_TURNS = 6;

const read = (name) => JSON.parse(readFileSync(DIR + name, "utf8"));
const write = (name, data) => writeFileSync(DIR + name, JSON.stringify(data, null, 2) + "\n");

const lessons = read("lessons.json");
const vocabulary = read("vocabulary.json");
const exercises = read("exercises.json");
const placement = read("placement.json");
const knowledge = read("knowledge.json");

// Guard: the batch already ran (or a later hand edit owns these ids).
const firstId = BATCH[0].lesson.id;
if (lessons.lessons.some((l) => l.id === firstId)) {
  throw new Error(`v54 already applied (${firstId} exists) — aborting`);
}

// The two lessons belong at the end of B2, not after C1: they take orders 144 and
// 145 and every later lesson moves down by two so the path still finishes B2
// before C1 begins.
lessons.lessons.forEach((l) => {
  if (l.order >= 144) l.order += 2;
});

const hasPersian = (s) => /[\u0600-\u06FF]/.test(s);

function exerciseFor(block, spec, index) {
  const lessonId = block.lesson.id;
  const base = lessonId.replace(".lesson-01", "");
  const id = `${base}.ex.${String(index + 1).padStart(2, "0")}`;
  const order = index + 1;
  switch (spec.k) {
    case "mcq":
      if (new Set(spec.options).size !== spec.options.length) {
        throw new Error(`${id}: duplicate option`);
      }
      if (spec.options.indexOf(spec.gloss) < 0) throw new Error(`${id}: gloss not in options`);
      return {
        id, lessonId, type: "multiple_choice", order,
        question: `What does "${spec.word}" mean?`,
        questionFa: `معنی «${spec.word}» چیست؟`,
        options: spec.options,
        correctIndex: spec.options.indexOf(spec.gloss),
        skill: "VOCABULARY",
        explanationFa: spec.note,
      };
    case "blank":
      if (!spec.sentence.includes("___")) throw new Error(`${id}: no blank marker`);
      if (spec.accepted.length < 2) throw new Error(`${id}: needs two accepted answers`);
      return {
        id, lessonId, type: "fill_blank", order,
        sentence: spec.sentence, accepted: spec.accepted,
        skill: spec.skill, explanationFa: spec.note,
      };
    case "recall":
      if (/[A-Za-z]/.test(spec.prompt)) throw new Error(`${id}: recall prompt leaks English`);
      return {
        id, lessonId, type: "word_recall", order,
        prompt: spec.prompt, accepted: spec.accepted,
        skill: "VOCABULARY", explanationFa: spec.note,
      };
    case "tr":
      return {
        id, lessonId, type: "translation", order,
        prompt: spec.prompt, accepted: spec.accepted,
        skill: "VOCABULARY", explanationFa: spec.note,
      };
    case "listen":
      return {
        id, lessonId, type: "listening", order,
        text: spec.text, accepted: [spec.text.toLowerCase().replace(/[.,!?]$/, "")],
        skill: "LISTENING", explanationFa: spec.note,
      };
    case "prod": {
      const anchorWords = spec.mustUse.join(" ").split(/\s+/).filter(Boolean).length;
      const exemplarWords = spec.accepted[0].trim().split(/\s+/).length;
      if (spec.minWords < 3 || spec.minWords < anchorWords) {
        throw new Error(`${id}: minWords ${spec.minWords} below the anchor floor`);
      }
      if (exemplarWords < spec.minWords) {
        throw new Error(`${id}: the exemplar (${exemplarWords} words) cannot reach minWords ${spec.minWords}`);
      }
      return {
        id, lessonId, type: "sentence_production", order,
        prompt: spec.prompt, mustUse: spec.mustUse, minWords: spec.minWords,
        accepted: spec.accepted, hintFa: spec.hint,
        skill: "VOCABULARY", explanationFa: spec.note,
      };
    }
    default:
      throw new Error(`${id}: unknown exercise kind ${spec.k}`);
  }
}

function wordFor(block, spec) {
  const lessonId = block.lesson.id;
  const base = lessonId.replace(".lesson-01", "");
  const entry = {
    id: `${base}.word.${spec.slug}`,
    word: spec.w,
    translation: spec.fa,
    definition: spec.def,
    pronunciation: spec.ipa,
    level: LEVEL,
    partOfSpeech: spec.pos,
    examples: spec.ex.map(([en, fa]) => ({ en, fa })),
    collocations: spec.col,
    lessonId,
    explanationFa: spec.note,
  };
  if (entry.examples.length !== 2) throw new Error(`${entry.id}: needs exactly two examples`);
  entry.examples.forEach((ex, i) => {
    if (!ex.en || !hasPersian(ex.fa)) throw new Error(`${entry.id}: example ${i} is not bilingual`);
    const tokens = ex.en.toLowerCase().match(/[a-z'-]+/g) ?? [];
    const parts = spec.w.toLowerCase().match(/[a-z'-]+/g) ?? [];
    if (!parts.every((p) => tokens.some((t) => t === p || t.startsWith(p)))) {
      throw new Error(`${entry.id}: example ${i} never names «${spec.w}»`);
    }
  });
  return entry;
}

function turnsFor(block) {
  const lessonId = block.lesson.id;
  return block.dialogue.map((t, i) => {
    if (t.accepted.length < 2) throw new Error(`${lessonId}: turn ${i + 1} needs two replies`);
    t.accepted.forEach((reply) => {
      if (hasPersian(reply)) throw new Error(`${lessonId}: turn ${i + 1} reply is not English`);
      if (/\d/.test(reply)) throw new Error(`${lessonId}: turn ${i + 1} reply has a digit`);
      if (reply.trim().toLowerCase() === t.question.trim().toLowerCase()) {
        throw new Error(`${lessonId}: turn ${i + 1} is an echo`);
      }
    });
    if (hasPersian(t.question)) throw new Error(`${lessonId}: turn ${i + 1} question is not English`);
    if (!hasPersian(t.questionFa)) throw new Error(`${lessonId}: turn ${i + 1} has no Persian gloss`);
    return {
      id: `${lessonId}.dlg.${String(i + 1).padStart(2, "0")}`,
      lessonId,
      type: "speaking",
      order: 10 + i,
      sceneFa: t.scene,
      partnerFa: t.partner,
      prompt: t.prompt,
      question: t.question,
      questionFa: t.questionFa,
      accepted: t.accepted,
      skill: "SPEAKING",
      explanationFa: t.note,
    };
  });
}

for (const block of BATCH) {
  const lessonId = block.lesson.id;
  if (block.exercises.length !== 9) throw new Error(`${lessonId}: expected 9 exercises`);
  if (block.words.length !== 6) throw new Error(`${lessonId}: expected 6 words`);
  if (block.dialogue.length !== DIALOGUE_TURNS) {
    throw new Error(`${lessonId}: expected ${DIALOGUE_TURNS} dialogue turns`);
  }

  lessons.lessons.push({
    id: lessonId,
    level: LEVEL,
    title: block.lesson.title,
    titleFa: block.lesson.titleFa,
    topic: block.lesson.topic,
    estimatedMinutes: 9,
    order: block.lesson.order,
    grammarTipFa: block.lesson.tip,
  });

  block.words.forEach((w) => vocabulary.vocabulary.push(wordFor(block, w)));
  block.exercises.forEach((e, i) => exercises.exercises.push(exerciseFor(block, e, i)));
  turnsFor(block).forEach((t) => exercises.exercises.push(t));

  knowledge.knowledge.push({
    id: block.vocab.id,
    type: "VOCABULARY",
    title: block.vocab.title,
    titleFa: block.vocab.titleFa,
    level: LEVEL,
    prerequisites: block.vocab.prerequisites,
    lessons: [lessonId],
    skills: ["VOCABULARY", "LISTENING"],
  });

  const grammar = knowledge.knowledge.find((k) => k.id === block.grammarNode);
  if (!grammar) throw new Error(`${lessonId}: unknown grammar node ${block.grammarNode}`);
  if (!grammar.lessons.includes(lessonId)) grammar.lessons.push(lessonId);
}

// Version bump in all five files together (docs/content-pipeline.md).
for (const root of [lessons, vocabulary, exercises, placement, knowledge]) {
  root.contentVersion = VERSION;
}

write("lessons.json", lessons);
write("vocabulary.json", vocabulary);
write("exercises.json", exercises);
write("placement.json", placement);
write("knowledge.json", knowledge);

console.log(
  `v${VERSION}: ${lessons.lessons.length} lessons, ${vocabulary.vocabulary.length} words, ` +
    `${exercises.exercises.length} exercises, ${knowledge.knowledge.length} knowledge items`,
);
