#!/usr/bin/env node
/**
 * Bundle v28 — completes the A1 syllabus.
 *
 * The curriculum plan (docs/curriculum.md) wants 30 topics at A1; 14 of them plus
 * the seven grammar supplements shipped by v27. This batch adds the 16 remaining
 * syllabus topics as **complete** lessons — teaching page, six words with two
 * bilingual examples each, nine exercises (three meaning choices, two word-choice
 * blanks with two accepted answers, recall, translation, guided listening and free
 * production), a vocabulary knowledge node and a phonology node listing.
 *
 * Applied once, then archived: the JSON is the source of truth from here on.
 * A-12 — never re-run this file, it would overwrite later hand edits.
 */

import fs from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { lessons as firstHalf } from "./v28_a1_syllabus_data.mjs";
import { more as secondHalf } from "./v28_a1_syllabus_data2.mjs";

const authored = [...firstHalf, ...secondHalf];

const CONTENT = join(dirname(fileURLToPath(import.meta.url)), "../../app/src/main/assets/content");
const VERSION = 28;

const read = (file) => JSON.parse(fs.readFileSync(join(CONTENT, file), "utf8"));
const write = (file, value) =>
  fs.writeFileSync(join(CONTENT, file), JSON.stringify(value, null, 2) + "\n");

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
    section("تلفظ", lesson.pron),
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
      level: "A1",
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
  // Three meaning questions per lesson. The targets are lesson-specific where a
  // shared word already has that question elsewhere in the bundle
  // (VocabularyContentTest rejects an identical question+answer pair).
  const targets = lesson.meaningWords ?? [0, 2, 4];
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

  const blanks = lesson.blanks.map(([sentence, accepted, explanation], i) => ({
    id: `${lessonId.replace(".lesson-01", "")}.ex.0${i + 4}`,
    lessonId,
    type: "fill_blank",
    order: i + 4,
    sentence,
    accepted,
    skill: "VOCABULARY",
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
    explanationFa:
      `جمله را بشنوید و همان را بنویسید: ${listenWord[5]} — ${lesson.listenNote}`,
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
      `الگوی جمله: ${produceWord[5]} — ${produceWord[10]} و ${produceWord[0]} را با هم در یک جمله بیاورید.`,
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

let order = Math.max(...lessonsJson.lessons.map((l) => l.order));
let added = 0;

for (const lesson of authored) {
  const lessonId = `a1.${lesson.slug}.lesson-01`;
  const nodeId = `vocab.${lesson.slug}`;
  if (taken.has(lessonId) || taken.has(nodeId)) {
    throw new Error(`${lessonId} / ${nodeId} already exists — bundle v28 is already applied`);
  }
  order += 1;

  lessonsJson.lessons.push({
    id: lessonId,
    level: "A1",
    title: lesson.title,
    titleFa: lesson.titleFa,
    topic: lesson.topic,
    estimatedMinutes: lesson.minutes,
    order,
    grammarTipFa: tipFor(lesson),
  });

  vocabularyJson.vocabulary.push(...wordEntries(lessonId, lesson));
  exercisesJson.exercises.push(...exercisesFor(lessonId, lesson));

  knowledgeJson.knowledge.push({
    id: nodeId,
    type: "VOCABULARY",
    title: lesson.title,
    titleFa: lesson.titleFa,
    level: "A1",
    prerequisites: ["vocab.alphabet"],
    lessons: [lessonId],
    skills: ["VOCABULARY", "LISTENING"],
  });

  const phonology = knowledgeJson.knowledge.find((k) => k.id === lesson.phonology);
  if (!phonology) throw new Error(`missing phonology node ${lesson.phonology}`);
  if (!phonology.lessons.includes(lessonId)) phonology.lessons.push(lessonId);
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
  `v${VERSION}: +${added} A1 lessons → ${lessonsJson.lessons.length} lessons, ` +
    `${vocabularyJson.vocabulary.length} words, ${exercisesJson.exercises.length} exercises, ` +
    `${knowledgeJson.knowledge.length} knowledge items`,
);
