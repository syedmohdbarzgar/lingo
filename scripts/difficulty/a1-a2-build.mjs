#!/usr/bin/env node
/**
 * Builds `scripts/difficulty/a1.json` and `a2.json` from the item ids the manual read
 * marked as *more than a first encounter*.
 *
 * Run once, on the v71 → v72 content bump; the JSON it writes is the artefact that
 * stays (the same shape as b1/b2/c1/c2). Keep it out of `scripts/archive/`: it is the
 * record of how the two lists were produced, and re-running it is idempotent.
 *
 * The judgement (A1/A2 run 1..2, so the question is only "first encounter or not"):
 *
 *   2 = a blank that tests a structure (a/an/the, at/on, much/many, a verb form)
 *       a choice where two options are grammatical and only the context decides
 *       a choice that reads a sentence or a passage instead of a word
 *       a listening line with two clauses or a place/time phrase
 *       a request whose politeness is the point ("Could I…", "Would you mind…")
 *   1 = one word's meaning, a spelling, a number, a single-clause line,
 *       or a sentence whose wrong options are mangled rather than plausible
 */
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = dirname(dirname(dirname(fileURLToPath(import.meta.url))));
const contentDir = join(root, 'app', 'src', 'main', 'assets', 'content');

const A1 = {
  'a1.greetings': ['ex.03', 'ex.06', 'ex.08'],
  'a1.daily-routine': ['ex.02', 'ex.08', 'ex.10', 'ex.11', 'ex.12', 'ex.13', 'ex.14', 'ex.15', 'ex.16', 'ex.17'],
  'a1.family': ['ex.02'],
  'a1.shopping': ['ex.01', 'ex.02', 'ex.03', 'ex.04', 'ex.06', 'ex.08', 'ex.09', 'ex.10', 'ex.11'],
  'a1.introductions': ['ex.01', 'ex.06', 'ex.07', 'ex.08', 'ex.10'],
  'a1.weather': ['ex.01', 'ex.05', 'ex.06', 'ex.08', 'ex.09', 'ex.10', 'ex.11', 'ex.12'],
  'a1.articles': ['ex.01', 'ex.03', 'ex.04', 'ex.06', 'read.01'],
  'a1.plurals': ['ex.01', 'ex.03', 'ex.04', 'ex.05'],
  'a1.pronouns': ['ex.01', 'ex.02', 'ex.03', 'ex.04', 'ex.05', 'ex.06'],
  'a1.can': ['ex.01', 'ex.02', 'ex.03', 'ex.04', 'ex.06'],
  'a1.imperatives': ['ex.01', 'ex.03', 'ex.04', 'ex.06', 'ex.07'],
  'a1.question-words': ['ex.01', 'ex.03', 'ex.04', 'ex.06', 'ex.07'],
  'a1.alphabet': ['ex.08'],
  'a1.spelling': ['ex.02', 'ex.05'],
  'a1.numbers': ['ex.08'],
  'a1.countries': ['ex.05', 'ex.08'],
  'a1.age-birthday': ['ex.05', 'ex.08'],
  'a1.days-months': ['ex.03', 'ex.08', 'read.01', 'read.02'],
  'a1.time': ['ex.03', 'ex.08'],
  'a1.colors': ['ex.05', 'ex.08'],
  'a1.classroom': ['ex.03', 'ex.08'],
  'a1.jobs': ['ex.04', 'ex.05', 'ex.08'],
  'a1.belongings': ['ex.05'],
  'a1.home-rooms': ['ex.05'],
  'a1.food-drinks': ['read.01', 'read.02'],
  'a1.transport': ['ex.05'],
  'a1.cafe-restaurant': ['ex.05', 'ex.08'],
  'a1.where-i-live': ['ex.08', 'read.01', 'read.02'],
};

const A2 = {
  'a2.airport': ['ex.01', 'ex.03', 'ex.06', 'ex.08'],
  'a2.restaurant': ['ex.02', 'ex.06', 'ex.08', 'ex.09', 'ex.10', 'ex.11', 'ex.12', 'ex.13', 'ex.15', 'ex.16'],
  'a2.directions': ['ex.01', 'ex.02', 'ex.03', 'ex.04', 'ex.06', 'ex.08', 'ex.09', 'read.01', 'read.02'],
  'a2.health': ['ex.01', 'ex.02', 'ex.06', 'ex.08', 'ex.09', 'ex.10', 'ex.11'],
  'a2.hobbies': ['ex.01', 'ex.03', 'ex.06', 'ex.07', 'ex.08', 'ex.09'],
  'a2.city-life': ['ex.01', 'ex.03', 'ex.05', 'ex.06', 'ex.07', 'ex.08', 'ex.09', 'ex.10'],
  'a2.past-simple': ['ex.01', 'ex.02', 'ex.03', 'ex.04', 'ex.05', 'ex.06', 'ex.07'],
  'a2.hotel-booking': ['ex.04', 'ex.05'],
  'a2.plane-ticket': ['ex.04', 'ex.05', 'read.02'],
  'a2.hotel-checkin': ['ex.05'],
  'a2.train-bus': ['ex.04', 'ex.05'],
  'a2.travel-info': ['ex.04', 'ex.05'],
  'a2.holiday-plans': ['ex.08'],
  'a2.buying-clothes': ['ex.04', 'ex.05', 'ex.08'],
  'a2.returns': ['ex.04', 'ex.08'],
  'a2.payments': ['ex.04', 'ex.05', 'read.01', 'read.02'],
  'a2.symptoms': ['ex.05'],
  'a2.pharmacy': ['ex.04', 'ex.05'],
  'a2.childhood': ['ex.04', 'ex.05'],
  'a2.celebrations': ['ex.04', 'ex.08'],
  'a2.invitations': ['ex.04', 'ex.08'],
  'a2.phone': ['ex.04', 'ex.05', 'ex.08'],
  'a2.emails': ['ex.08', 'read.01', 'read.02'],
  'a2.renting': ['ex.04', 'ex.05'],
  'a2.house-problems': ['ex.04', 'ex.08'],
  'a2.future-plans': ['ex.04', 'ex.05'],
  'a2.comparing': ['ex.04', 'ex.05', 'ex.08'],
  'a2.opinions': ['ex.04', 'ex.05', 'ex.08'],
  'a2.asking-help': ['ex.04', 'ex.08'],
  'a2.work': ['ex.04'],
  'a2.everyday-problems': ['ex.05', 'ex.08', 'read.01', 'read.02'],
};

const bundle = JSON.parse(readFileSync(join(contentDir, 'exercises.json'), 'utf8')).exercises;
const lessons = JSON.parse(readFileSync(join(contentDir, 'lessons.json'), 'utf8')).lessons;
const levelByLesson = new Map(lessons.map((lesson) => [lesson.id, lesson.level]));
const byId = new Map(bundle.map((exercise) => [exercise.id, exercise]));

const comments = {
  A1:
    'Hand judgement for A1 (content v72). All 499 items were read. A1 runs 1..2, so the only ' +
    'question is "first encounter or more". 2 = a blank that tests a structure (a/an/the, at/on, ' +
    'much/many, a verb form); a choice where two options are grammatical and the context decides; ' +
    'a choice that reads a sentence or a passage instead of a word; a listening line with two ' +
    'clauses or a place/time phrase. 1 = one word\'s meaning, a spelling, a number, a single-clause ' +
    'line, or a sentence whose wrong options are mangled rather than plausible.',
  A2:
    'Hand judgement for A2 (content v72). All 488 items were read. Same two-value question as A1 ' +
    'with A2\'s own structures: the gerund/infinitive split after enjoy/want/decide, the comparative ' +
    '(including worse), the past simple and its negative/question, there is/are and any, should/' +
    'have to, prefer to, and the polite request ("Could I…", "Would you mind…"). A vocabulary ' +
    'meaning, a plain collocation and a one-clause listening line stay at 1.',
};

for (const [level, map] of [
  ['A1', A1],
  ['A2', A2],
]) {
  const overrides = {};
  for (const [lesson, suffixes] of Object.entries(map)) {
    for (const suffix of suffixes) {
      const id = `${lesson}.${suffix}`;
      const exercise = byId.get(id);
      if (!exercise) throw new Error(`${id} is not in the bundle`);
      if (levelByLesson.get(exercise.lessonId) !== level) throw new Error(`${id} is not ${level}`);
      if (!['multiple_choice', 'fill_blank', 'listening'].includes(exercise.type)) {
        throw new Error(`${id} is a ${exercise.type} — production already sits at 2`);
      }
      if (exercise.difficulty !== 1) throw new Error(`${id} is already ${exercise.difficulty}`);
      overrides[id] = 2;
    }
  }
  const sorted = Object.fromEntries(Object.entries(overrides).sort(([a], [b]) => a.localeCompare(b)));
  const file = join(root, 'scripts', 'difficulty', `${level.toLowerCase()}.json`);
  writeFileSync(file, `${JSON.stringify({ _comment: comments[level], overrides: sorted }, null, 2)}\n`);
  console.log(`${level}: ${Object.keys(sorted).length} items raised to 2 — ${file}`);
}
