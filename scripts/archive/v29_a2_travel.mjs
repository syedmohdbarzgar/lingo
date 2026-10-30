#!/usr/bin/env node
/**
 * Bundle v29 — the first six A2 syllabus topics (travel and staying).
 *
 * docs/curriculum.md lists 30 topics at A2 and the bundle shipped seven lessons
 * (one of them a grammar supplement). This batch adds topics 1 and 3–7 — Booking
 * a Hotel, Buying a Plane Ticket, Hotel Check-in, Traveling by Train and Bus,
 * Asking About Travel Information, Planning a Holiday — as **complete** lessons:
 * a sectioned teaching page, six words with two bilingual examples each, nine
 * exercises (three meaning choices, two blanks with two accepted answers, recall,
 * translation, guided listening and free production) and a vocabulary knowledge
 * node. Each lesson joins the grammar node that teaches its structure, so the
 * graph credits the polite request / question-word / will practice it contains.
 *
 * Applied once, then archived: the JSON is the source of truth from here on.
 * A-12 — never re-run this file, it would overwrite later hand edits.
 */

import fs from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { lessons as authored } from "./v29_a2_travel_data.mjs";

const CONTENT = join(dirname(fileURLToPath(import.meta.url)), "../../app/src/main/assets/content");
const VERSION = 29;

const read = (file) => JSON.parse(fs.readFileSync(join(CONTENT, file), "utf8"));
const write = (file, value) =>
  fs.writeFileSync(join(CONTENT, file), JSON.stringify(value, null, 2) + "\n");

/** What each lesson must already know: the A1 vocabulary node it builds on. */
const PREREQUISITES = {
  "hotel-booking": ["vocab.places-town"],
  "plane-ticket": ["vocab.transport"],
  "hotel-checkin": ["vocab.places-town"],
  "train-bus": ["vocab.transport"],
  "travel-info": ["vocab.transport"],
  "holiday-plans": ["vocab.places-town"],
};

/** The grammar structure the lesson practises, and the node that already teaches it. */
const GRAMMAR_NODE = {
  "hotel-booking": "grammar.would-like-politeness",
  "plane-ticket": "grammar.would-like-politeness",
  "hotel-checkin": "grammar.would-like-politeness",
  "travel-info": "grammar.question-words",
  "holiday-plans": "grammar.future-will",
};

const slugWord = (word) => word.toLowerCase().replace(/[^a-z0-9]+/g, "-");
const lower = (text) => text.trim().replace(/[.!?]+$/, "").toLowerCase();

/** Function words a production anchor must not be: they carry no structure. */
const STOP_WORDS = new Set(
  (
    "a an the is are am was were be been being in on at of to and or but my your our their his her " +
    "its it i you he she we they this that these those there here very for with some any from by " +
    "not no do does did have has had can will would like"
  ).split(" "),
);

/**
 * Two content words of the exemplar, in the order they appear. The production
 * grader requires the anchors in order and a word floor the exemplar reaches, so
 * both are derived from the sentence the author actually wrote.
 */
function anchorsOf(sentence) {
  const words = sentence.replace(/[.,!?]/g, "").split(/\s+/).filter(Boolean);
  const content = words.filter(
    (word) => !STOP_WORDS.has(word.toLowerCase()) && word.replace(/[^A-Za-z]/g, "").length > 2,
  );
  return content.slice(0, 2).map((word) => word.toLowerCase().replace(/[^a-z']/g, ""));
}

function tipFor(lesson) {
  const section = (title, body) => `## ${title}\n${body}`;
  return [
    section("قاعده", lesson.rule),
    section("جدول", lesson.table.map(([en, fa]) => `${en} — ${fa}`).join("\n")),
    section("مثال‌ها", lesson.examples.map(([en, fa]) => `${en} — ${fa}`).join("\n")),
    section("خطاهای رایج", lesson.errors.join("\n")),
  ].join("\n");
}

function wordEntries(lessonId, lesson) {
  return lesson.words.map((w) => {
    const [word, fa, definition, ipa, pos, en1, fa1, en2, fa2, c1, c2, note] = w;
    return {
      id: `${lessonId.replace(".lesson-01", "")}.word.${slugWord(word)}`,
      word,
      translation: fa,
      definition,
      pronunciation: ipa,
      level: "A2",
      partOfSpeech: pos,
      examples: [
        { en: en1, fa: fa1 },
        { en: en2, fa: fa2 },
      ],
      collocations: [c1, c2],
      lessonId,
      explanationFa: note,
    };
  });
}

function exercisesFor(lessonId, lesson) {
  const words = lesson.words;
  const glosses = words.map((w) => w[1]);
  const targets = lesson.meaningWords;
  const distractorsFor = (index) =>
    [1, 2, 3].map((step) => (index + step) % words.length);
  const meaning = (index, distractors, order) => {
    const options = [glosses[index], ...distractors.map((d) => glosses[d])];
    return {
      id: `${lessonId.replace(".lesson-01", "")}.ex.0${order}`,
      lessonId,
      type: "multiple_choice",
      order,
      question: `What does "${words[index][0]}" mean?`,
      questionFa: `معنی «${words[index][0]}» چیست؟`,
      options,
      correctIndex: 0,
      skill: "VOCABULARY",
      explanationFa:
        `${words[index][0]} یعنی «${glosses[index]}». گزینه‌های دیگر معنی ` +
        `${distractors.map((d) => words[d][0]).join("، ")} هستند.`,
    };
  };

  const blanks = lesson.blanks.map(([sentence, accepted, explanation, skill], i) => ({
    id: `${lessonId.replace(".lesson-01", "")}.ex.0${i + 4}`,
    lessonId,
    type: "fill_blank",
    order: i + 4,
    sentence,
    accepted,
    skill: skill ?? "VOCABULARY",
    explanationFa: explanation,
  }));

  const recallWord = words[lesson.recallWord];
  const recall = {
    id: `${lessonId.replace(".lesson-01", "")}.ex.06`,
    lessonId,
    type: "word_recall",
    order: 6,
    prompt: recallWord[1],
    accepted: [recallWord[0]],
    skill: "VOCABULARY",
    explanationFa: `${recallWord[0]} یعنی «${recallWord[1]}»؛ ${recallWord[11]}`,
  };

  const [translationPrompt, translationAccepted, translationExplanation] = lesson.translation;
  const translation = {
    id: `${lessonId.replace(".lesson-01", "")}.ex.07`,
    lessonId,
    type: "translation",
    order: 7,
    prompt: translationPrompt,
    accepted: translationAccepted,
    skill: "VOCABULARY",
    explanationFa: translationExplanation,
  };

  const listenWord = words[lesson.listenWord];
  const listening = {
    id: `${lessonId.replace(".lesson-01", "")}.ex.08`,
    lessonId,
    type: "listening",
    order: 8,
    text: listenWord[5],
    accepted: [lower(listenWord[5])],
    skill: "LISTENING",
    explanationFa: `جمله را بشنوید و همان را بنویسید: ${listenWord[5]} — ${lesson.listenNote}`,
  };

  const produceWord = words[lesson.produceWord];
  const exemplar = lower(produceWord[5]);
  const anchors = anchorsOf(produceWord[5]);
  const production = {
    id: `${lessonId.replace(".lesson-01", "")}.ex.09`,
    lessonId,
    type: "sentence_production",
    order: 9,
    prompt: lesson.productionPrompt,
    mustUse: anchors,
    minWords: produceWord[5].split(/\s+/).length,
    accepted: [exemplar],
    hintFa: lesson.productionHint,
    skill: "VOCABULARY",
    explanationFa:
      `الگوی جمله: ${produceWord[5]} — ${produceWord[10]} را به کار ببر و ` +
      `${anchors.join(" و ")} را در جمله بیاور.`,
  };

  return [
    meaning(targets[0], distractorsFor(targets[0]), 1),
    meaning(targets[1], distractorsFor(targets[1]), 2),
    meaning(targets[2], distractorsFor(targets[2]), 3),
    ...blanks,
    recall,
    translation,
    listening,
    production,
  ];
}

const lessonsJson = read("lessons.json");
const vocabularyJson = read("vocabulary.json");
const exercisesJson = read("exercises.json");
const knowledgeJson = read("knowledge.json");

const taken = new Set([
  ...lessonsJson.lessons.map((l) => l.id),
  ...vocabularyJson.vocabulary.map((v) => v.id),
  ...exercisesJson.exercises.map((e) => e.id),
  ...knowledgeJson.knowledge.map((k) => k.id),
]);

/** The same meaning question must not exist twice (VocabularyContentTest). */
const askedQuestions = new Set(
  exercisesJson.exercises
    .filter((e) => e.type === "multiple_choice")
    .map((e) =>
      `${e.question}|${e.options[e.correctIndex]}`.toLowerCase(),
    ),
);

let order = Math.max(...lessonsJson.lessons.map((l) => l.order));
let added = 0;

for (const lesson of authored) {
  const lessonId = `a2.${lesson.slug}.lesson-01`;
  const nodeId = `vocab.${lesson.slug}`;
  if (taken.has(lessonId) || taken.has(nodeId)) {
    throw new Error(`${lessonId} / ${nodeId} already exists — bundle v29 is already applied`);
  }
  const prerequisites = PREREQUISITES[lesson.slug];
  if (!prerequisites) throw new Error(`no prerequisites authored for ${lesson.slug}`);
  order += 1;

  lessonsJson.lessons.push({
    id: lessonId,
    level: "A2",
    title: lesson.title,
    titleFa: lesson.titleFa,
    topic: lesson.topic,
    estimatedMinutes: lesson.minutes,
    order,
    grammarTipFa: tipFor(lesson),
  });

  const words = wordEntries(lessonId, lesson);
  vocabularyJson.vocabulary.push(...words);
  const exercises = exercisesFor(lessonId, lesson);
  exercises.forEach((exercise) => {
    if (exercise.type !== "multiple_choice") return;
    const key = `${exercise.question}|${exercise.options[exercise.correctIndex]}`.toLowerCase();
    if (askedQuestions.has(key)) {
      throw new Error(`${exercise.id}: the bundle already asks «${key}» — pick another word`);
    }
    askedQuestions.add(key);
  });
  exercisesJson.exercises.push(...exercises);

  knowledgeJson.knowledge.push({
    id: nodeId,
    type: "VOCABULARY",
    title: lesson.title,
    titleFa: lesson.titleFa,
    level: "A2",
    prerequisites,
    lessons: [lessonId],
    skills: ["VOCABULARY", "LISTENING"],
  });

  const grammarId = GRAMMAR_NODE[lesson.slug];
  if (grammarId) {
    const grammar = knowledgeJson.knowledge.find((k) => k.id === grammarId);
    if (!grammar) throw new Error(`missing grammar node ${grammarId}`);
    if (!grammar.lessons.includes(lessonId)) grammar.lessons.push(lessonId);
  }
  added += 1;
}

lessonsJson.contentVersion = VERSION;
vocabularyJson.contentVersion = VERSION;
exercisesJson.contentVersion = VERSION;
knowledgeJson.contentVersion = VERSION;
const placementJson = read("placement.json");
placementJson.contentVersion = VERSION;

write("lessons.json", lessonsJson);
write("vocabulary.json", vocabularyJson);
write("exercises.json", exercisesJson);
write("knowledge.json", knowledgeJson);
write("placement.json", placementJson);

console.log(
  `v${VERSION}: +${added} A2 lessons → ${lessonsJson.lessons.length} lessons, ` +
    `${vocabularyJson.vocabulary.length} words, ${exercisesJson.exercises.length} exercises, ` +
    `${knowledgeJson.knowledge.length} knowledge items`,
);
