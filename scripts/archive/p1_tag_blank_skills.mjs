// A-2b follow-up: tag every fill-in-the-blank with the skill it really tests.
//
//   node scripts/p1_tag_blank_skills.mjs
//
// `AnswerChecker.skillOf` falls back to GRAMMAR for an untagged blank, and the
// GRAMMAR branch no longer offers the "almost" spelling hint (A-2b). That default
// therefore decided the hint for 67 blanks — including pure word-choice items
// like "My ______ is a teacher." — which is not a decision a fallback should make.
//
// Rule used here (mirrors the app's own heuristic intent):
//   GRAMMAR    → the blank asks for a FORM: an auxiliary, article, pronoun,
//                question word, preposition or an inflected/passive/gerund form.
//   VOCABULARY → the blank asks for a WORD: a noun, adjective or lexical verb the
//                learner picks by meaning.
//
// Inserts `skill` right before `explanationFa` so the diff is one added line per
// exercise, and bumps contentVersion 15 → 16 in all five bundle files.

import { readFileSync, writeFileSync } from "node:fs";

const dir = "app/src/main/assets/content";
const read = (f) => JSON.parse(readFileSync(`${dir}/${f}`, "utf8"));
const write = (f, o) => writeFileSync(`${dir}/${f}`, JSON.stringify(o, null, 2) + "\n");

const grammar = new Set([
  // form words: articles, pronouns, question words, quantifiers, prepositions
  "a1.greetings.ex.03", // Hello, ______ are you? — question word
  "a1.shopping.ex.04", // How ___ water — quantifier
  "a1.articles.ex.02", // a / an
  "a1.articles.ex.05", // the
  "a1.pronouns.ex.02", // object pronoun
  "a1.question-words.ex.02", // when / what time
  "a1.question-words.ex.05", // where
  "a2.directions.ex.03", // preposition at / by
  "a2.directions.ex.04", // preposition down / along
  // modal / tense / agreement forms
  "a1.can.ex.02",
  "a1.can.ex.05",
  "a1.plurals.ex.02", // irregular plural form
  "a1.plurals.ex.06",
  "a1.imperatives.ex.02", // imperative form
  "a1.imperatives.ex.05", // negative imperative
  "a2.past-simple.ex.02", // past simple form
  "a2.past-simple.ex.05",
  "b1.work.ex.03", // present perfect participle
  "b1.work.ex.04", // past-simple auxiliary
  "b1.news.ex.03", // be-passive agreement
  "b1.news.ex.04",
  "b1.news.ex.09", // passive
  "b1.plans.ex.03", // second conditional modal
  "b1.experiences.ex.03", // past perfect auxiliary
  "b1.experiences.ex.04", // past continuous form
  "b1.health-lifestyle.ex.10", // had better + bare infinitive
  "b2.technology.ex.03", // relative pronoun
  "b2.technology.ex.04", // passive participle
  "b2.environment.ex.04", // second conditional verb form
  "b2.interview.ex.03", // look forward to + gerund
  "b2.interview.ex.09", // reported-speech backshift
  "b2.media.ex.03", // future perfect auxiliary
  "b2.media.ex.09", // used to / would
  "b2.problems.ex.10", // perfect passive
  "b2.problems.ex.11",
  "c1.academic.ex.03", // mixed conditional
  "c1.academic.ex.04", // agreement on data
  "c1.persuasion.ex.03", // deduction modal
  "c1.persuasion.ex.04", // inversion
  "c1.culture.ex.03", // wish + past
  "c1.culture.ex.04", // as if + past
  "c2.rhetoric.ex.03", // inversion
]);

const vocabulary = new Set([
  "a1.daily-routine.ex.02", // lexical verb choice
  "a1.daily-routine.ex.06", // content noun
  "a1.family.ex.03",
  "a1.shopping.ex.03", // adjective by meaning
  "a1.spelling.ex.03",
  "a1.spelling.ex.08",
  "a1.numbers.ex.03",
  "a1.numbers.ex.04",
  "a2.airport.ex.03",
  "a2.restaurant.ex.03",
  "a2.health.ex.03",
  "a2.health.ex.04",
  "b1.plans.ex.04",
  "b2.environment.ex.03",
  "b2.interview.ex.04",
  "b2.media.ex.04",
  "c1.phrasal-idioms.ex.03",
  "c1.phrasal-idioms.ex.04",
  "c2.nuance.ex.03",
  "c2.nuance.ex.04",
  "c2.rhetoric.ex.04",
  "c2.science.ex.03",
  "c2.science.ex.04",
  "c2.literature.ex.03",
  "c2.literature.ex.04",
]);

/** Insert `skill` immediately before `explanationFa`, keeping every other key in place. */
function withSkill(row, skill) {
  const out = {};
  for (const [key, value] of Object.entries(row)) {
    if (key === "explanationFa") out.skill = skill;
    out[key] = value;
  }
  if (!("skill" in out)) out.skill = skill;
  return out;
}

const exercises = read("exercises.json");
const blanks = exercises.exercises.filter((x) => x.type === "fill_blank");
const untagged = blanks.filter((x) => !x.skill);
if (untagged.length === 0) {
  console.error("every fill_blank already carries a skill — nothing to do");
  process.exit(1);
}

let tagged = 0;
exercises.exercises = exercises.exercises.map((row) => {
  if (row.type !== "fill_blank" || row.skill) return row;
  const skill = grammar.has(row.id) ? "GRAMMAR" : vocabulary.has(row.id) ? "VOCABULARY" : null;
  if (!skill) throw new Error(`no classification recorded for ${row.id}`);
  tagged += 1;
  return withSkill(row, skill);
});
write("exercises.json", exercises);

const from = read("lessons.json").contentVersion;
for (const file of ["lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json"]) {
  const o = read(file);
  o.contentVersion = from + 1;
  write(file, o);
}

console.log(`tagged ${tagged} fill_blanks (${grammar.size} GRAMMAR, ${vocabulary.size} VOCABULARY); contentVersion ${from} -> ${from + 1}`);
