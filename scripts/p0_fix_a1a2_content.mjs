/**
 * P0-9 — final correctness pass over the A1/A2 content (and one knock-on fix
 * outside A1/A2 that the same audit surfaced).
 *
 * Findings this script fixes:
 *
 * 1. `scripts/enrich_content.mjs` generated one "What does X mean?" choice per
 *    lesson with `vocabMc()`. Its three distractors were picked from a global
 *    cursor over ALL vocabulary, so an A1 question could offer a C2 gloss and a
 *    C2 question could offer «صبح خوش» — the wrong options were trivially
 *    identifiable and often from a different CEFR level. Distractors now come
 *    from the lesson's own vocabulary, which is what the item claims to test.
 * 2. `a1.daily-routine.ex.07` duplicated the hand-authored
 *    `a1.daily-routine.ex.01` (same question, same answer). The generated item
 *    is retargeted to the lesson's first still-untested word.
 * 3. Two vocabulary examples did not contain their word at all
 *    (`price`, `discount`) — every other "missing word" in the audit turned out
 *    to be a legitimate inflection (irregular past, irregular plural, separable
 *    phrasal verb) which `VocabularyContentTest` now allowlists explicitly.
 *
 * Correct-answer positions are deliberately untouched: only the non-correct
 * option slots are rewritten, so `ContentDistributionTest`'s balance invariant
 * still holds. The bundle `contentVersion` is bumped because the JSON changed.
 */
import fs from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];

const read = (name) => JSON.parse(fs.readFileSync(`${DIR}/${name}.json`, "utf8"));
const write = (name, doc) =>
  fs.writeFileSync(`${DIR}/${name}.json`, JSON.stringify(doc, null, 2) + "\n");

const MEANING = /^What does "(.+)" mean\?$/;

/** Authored Persian note for the one item whose target word changes. */
const retargetedExplanation = {
  "a1.daily-routine.ex.07":
    "breakfast یعنی صبحانه — نخستین وعدهٔ روز، پیش از رفتن به سر کار.",
};

/** Vocabulary examples that genuinely never named their word. */
const exampleFix = {
  "a1.shopping.word.price": "The price of this bag is too high.",
  "a1.shopping.word.discount": "There is a big discount on shoes today.",
};

const vocabularyDoc = read("vocabulary");
const exercisesDoc = read("exercises");

const vocabulary = vocabularyDoc.vocabulary;
const exercises = exercisesDoc.exercises;

const wordsByLesson = {};
for (const w of vocabulary) (wordsByLesson[w.lessonId] = wordsByLesson[w.lessonId] || []).push(w);

/** Glosses another meaning choice in the same lesson already tests. */
function claimedGlosses(lessonId, except) {
  const claimed = new Set();
  for (const other of exercises) {
    if (other === except || other.lessonId !== lessonId) continue;
    if (other.type !== "multiple_choice") continue;
    if (!MEANING.test(other.question || "")) continue;
    claimed.add(other.options[other.correctIndex]);
  }
  return claimed;
}

let fixedOptions = 0;
let retargeted = 0;
for (const e of exercises) {
  if (!/\.ex\.07$/.test(e.id) || e.type !== "multiple_choice") continue;
  const match = MEANING.exec(e.question || "");
  if (!match) continue;

  const words = wordsByLesson[e.lessonId] || [];
  // First lesson word whose gloss is not already the answer of another item.
  const claimed = claimedGlosses(e.lessonId, e);
  const target = words.find((w) => !claimed.has(w.translation));
  if (!target) throw new Error(`${e.id}: lesson has no untested word`);

  const distractors = [];
  for (const w of words) {
    if (w.translation === target.translation) continue;
    if (distractors.includes(w.translation)) continue;
    distractors.push(w.translation);
  }
  if (distractors.length < 3) throw new Error(`${e.id}: only ${distractors.length} distractors`);

  const changedTarget = target.word !== match[1];
  e.question = `What does "${target.word}" mean?`;
  e.questionFa = `معنی «${target.word}» چیست؟`;
  // Keep every option slot where it was — only the strings move.
  let d = 0;
  e.options = e.options.map((_, i) => (i === e.correctIndex ? target.translation : distractors[d++]));
  if (changedTarget) {
    retargeted += 1;
    e.explanationFa = retargetedExplanation[e.id] ?? `${target.word} یعنی ${target.translation}.`;
  }
  fixedOptions += 1;
}

let fixedExamples = 0;
for (const w of vocabulary) {
  const replacement = exampleFix[w.id];
  if (!replacement) continue;
  if (!w.examples || w.examples.length === 0) throw new Error(`${w.id}: no example to replace`);
  w.examples = [replacement, ...w.examples.slice(1)];
  fixedExamples += 1;
}

const docs = Object.fromEntries(FILES.map((n) => [n, read(n)]));
docs.exercises = exercisesDoc;
docs.vocabulary = vocabularyDoc;

const before = docs.lessons.contentVersion;
for (const name of FILES) {
  if (docs[name].contentVersion !== before) {
    throw new Error(`${name}.json declares a different contentVersion (${docs[name].contentVersion})`);
  }
  docs[name].contentVersion = before + 1;
}
for (const name of FILES) write(name, docs[name]);

console.log(`rebuilt ${fixedOptions} meaning choices (${retargeted} retargeted)`);
console.log(`rewrote ${fixedExamples} vocabulary examples`);
console.log(`contentVersion ${before} -> ${before + 1}`);
