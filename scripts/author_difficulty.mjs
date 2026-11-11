#!/usr/bin/env node
/**
 * Seeds the authored `difficulty` (1..3) on every exercise in exercises.json.
 *
 * Run once, on the v65 → v66 content bump. From here the values are **content**:
 * a hand edit is the intended way to change one, and this script only fills what
 * is missing (pass --force to recompute every value, which throws away those edits).
 *
 * The seed rule — two authored facts the bundle already carries, nothing invented:
 *
 *   band    = what the item asks for   recognition → 1, guided production → 2,
 *                                      free production → 3
 *   ceiling = the level of the item's own lesson   A1/A2 → 2, B1 and up → 3
 *   floor   = C1/C2 → 2 (nothing is a "first encounter" at C level)
 *   value   = clamp(band, floor, ceiling)
 *
 * The band is what makes the scale usable inside one lesson — a lesson whose items all
 * share a level still needs an order. The level only bounds it, because the scale is
 * **relative to the lesson's own level**: A1 has no hard item, and C2 has no first
 * encounter. A pure level-based number was the first attempt and it was useless where it
 * mattered most: every C1/C2 item landed on 3, so the level axis could not rise anywhere.
 */
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const contentDir = join(dirname(dirname(fileURLToPath(import.meta.url))), 'app', 'src', 'main', 'assets', 'content');

const exercisesPath = join(contentDir, 'exercises.json');
const lessonsPath = join(contentDir, 'lessons.json');

const force = process.argv.includes('--force');

const CEILING = { A1: 2, A2: 2, B1: 3, B2: 3, C1: 3, C2: 3 };
const FLOOR = { A1: 1, A2: 1, B1: 1, B2: 1, C1: 2, C2: 2 };
const BAND = {
  multiple_choice: 1,
  listening: 1,
  fill_blank: 1,
  word_recall: 2,
  translation: 2,
  speaking: 2,
  sentence_production: 3,
};

const lessons = JSON.parse(readFileSync(lessonsPath, 'utf8')).lessons;
const levelByLesson = new Map(lessons.map((lesson) => [lesson.id, lesson.level]));

const bundle = JSON.parse(readFileSync(exercisesPath, 'utf8'));
const missingLevels = [];
let filled = 0;
let kept = 0;

for (const exercise of bundle.exercises) {
  const level = levelByLesson.get(exercise.lessonId);
  const band = BAND[exercise.type];
  if (!level || !(level in CEILING) || band === undefined) {
    missingLevels.push(`${exercise.id} → lesson ${exercise.lessonId} / ${exercise.type}`);
    continue;
  }
  const seeded = Math.min(CEILING[level], Math.max(FLOOR[level], band));
  if (exercise.difficulty === undefined || force) {
    exercise.difficulty = seeded;
    filled++;
  } else {
    kept++;
  }
}

if (missingLevels.length > 0) {
  console.error(`no authored level for ${missingLevels.length} exercise(s):`);
  for (const line of missingLevels.slice(0, 10)) console.error(`  ${line}`);
  process.exit(1);
}

// Insert `difficulty` after `order` so the field reads next to the other authored
// metadata instead of landing at the end of every object.
const ordered = {
  ...bundle,
  exercises: bundle.exercises.map((exercise) => {
    const { difficulty, ...rest } = exercise;
    const out = {};
    for (const [key, value] of Object.entries(rest)) {
      out[key] = value;
      if (key === 'order') out.difficulty = difficulty;
    }
    if (!('difficulty' in out)) out.difficulty = difficulty;
    return out;
  }),
};

writeFileSync(exercisesPath, `${JSON.stringify(ordered, null, 2)}\n`);

const histogram = new Map();
for (const exercise of ordered.exercises) {
  const level = levelByLesson.get(exercise.lessonId);
  const key = `${level} · ${exercise.difficulty}`;
  histogram.set(key, (histogram.get(key) ?? 0) + 1);
}
console.log(
  `${filled} seeded, ${kept} kept as authored (${force ? '--force' : 'fill-missing'}) — ` +
    `${ordered.exercises.length} exercises`,
);
for (const [key, count] of [...histogram.entries()].sort()) console.log(`  ${key}: ${count}`);
