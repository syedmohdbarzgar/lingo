#!/usr/bin/env node
/**
 * v78: every word a lesson ships is asked in that lesson's own practice.
 *
 * A word could be taught and never be asked where it was taught: the vocabulary entry promises two
 * bilingual examples (A-10) and `VocabularyContentTest` holds it to that, but nothing held the
 * *exercises* to anything — 133 words across 75 lessons were met only in the word list and then not
 * again until the review queue, which is the wrong place for a first retrieval.
 *
 * The item added for each one is the format the bundle already uses for exactly this task:
 * `word_recall` (the Persian meaning, name the English word, no options anywhere), at difficulty 2
 * like every other recall. Nothing is invented — the cue is the entry's own authored `translation`
 * and the explanation is its authored `explanationFa`, so the words a lesson teaches are asked in the
 * words its author wrote.
 *
 * The one collision the global "no recall asked twice" rule would see (`b2.technology`'s
 * «حریم خصوصی» is already asked by another lesson) gets a cue that names its own context instead.
 *
 * Run once, then move to scripts/archive/ — never re-run a content batch.
 */
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const contentDir = join(dirname(dirname(fileURLToPath(import.meta.url))), 'app', 'src', 'main', 'assets', 'content');
const exercisesPath = join(contentDir, 'exercises.json');
const vocabularyPath = join(contentDir, 'vocabulary.json');

/** A cue that would repeat another lesson's recall verbatim. */
const cueOverrides = {
  'b2.technology.word.privacy': 'حریم خصوصی (اطلاعات شخصی)',
};

/** Irregular forms the prefix rule cannot see — the same list `VocabularyContentTest` carries. */
const irregularForms = {
  'a2.past-simple.word.go': 'went',
  'a2.past-simple.word.buy': 'bought',
  'a2.past-simple.word.see': 'saw',
  'a2.past-simple.word.eat': 'ate',
  'a2.past-simple.word.take': 'took',
  'a2.past-simple.word.write': 'wrote',
  'a1.plurals.word.woman': 'women',
  'a1.body.word.tooth': 'teeth',
  'b1.work.word.apply': 'applied',
  'c2.nuance.word.imply': 'implies',
  'c1.inference.word.imply': 'implied',
  'a2.childhood.word.grow-up': 'grew up',
  'a2.childhood.word.memory': 'memories',
  'a2.emails.word.reply': 'replied',
  'c1.phrasal-idioms.word.give-up': 'gave up',
  'c1.phrasal-idioms.word.come-across': 'came across',
  'c1.phrasal-idioms.word.run-out-of': 'ran out of',
};

const tokens = (text) => text.toLowerCase().replace(/\u2019/g, "'").match(/[a-z'-]+/g) ?? [];

/** The English a learner reads or writes in one exercise, with the Persian cues left out. */
function englishOf(exercise) {
  const parts = [];
  for (const [key, value] of Object.entries(exercise)) {
    if (/Fa$/.test(key) || key === 'prompt') continue;
    if (typeof value === 'string') parts.push(value);
    else if (Array.isArray(value)) parts.push(...value.filter((entry) => typeof entry === 'string'));
  }
  return parts.join(' ');
}

const uses = (text, word) => {
  const parts = tokens(word);
  if (parts.length === 0) return false;
  const seen = tokens(text);
  return parts.every((part) => seen.some((token) => token === part || token.startsWith(part)));
};

const bundle = JSON.parse(readFileSync(exercisesPath, 'utf8'));
const vocabulary = JSON.parse(readFileSync(vocabularyPath, 'utf8')).vocabulary;

const byLesson = new Map();
for (const exercise of bundle.exercises) {
  if (!byLesson.has(exercise.lessonId)) byLesson.set(exercise.lessonId, []);
  byLesson.get(exercise.lessonId).push(exercise);
}

const isPractised = (items, entry) => {
  if (items.length === 0) return false;
  if (uses(items.map(englishOf).join(' '), entry.word)) return true;
  const irregular = irregularForms[entry.id];
  if (irregular && items.some((item) => JSON.stringify(item).toLowerCase().includes(irregular))) return true;
  return items.some(
    (item) =>
      Array.isArray(item.accepted) &&
      item.accepted.some((answer) => String(answer).toLowerCase() === entry.word.toLowerCase()),
  );
};

const uncovered = vocabulary.filter((entry) => !isPractised(byLesson.get(entry.lessonId) ?? [], entry));
const added = [];
/** Per lesson: the highest drill number and order used so far, so each recall counts up. */
const counters = new Map();

for (const entry of uncovered) {
  const topic = entry.lessonId.replace('.lesson-01', '');
  const items = byLesson.get(entry.lessonId) ?? [];
  const state = counters.get(entry.lessonId) ?? {
    drill: Math.max(
      0,
      ...items
        .map((item) => /^ex\.(\d+)$/.exec(item.id.slice(topic.length + 1)))
        .filter(Boolean)
        .map((match) => Number(match[1])),
    ),
    order: Math.max(...items.map((item) => item.order ?? 0)),
  };
  state.drill += 1;
  state.order += 1;
  counters.set(entry.lessonId, state);

  const id = `${topic}.ex.${String(state.drill).padStart(2, '0')}`;
  if (bundle.exercises.some((exercise) => exercise.id === id)) throw new Error(`id already taken: ${id}`);

  added.push({
    id,
    lessonId: entry.lessonId,
    type: 'word_recall',
    order: state.order,
    difficulty: 2,
    prompt: cueOverrides[entry.id] ?? entry.translation,
    accepted: [entry.word],
    skill: 'VOCABULARY',
    explanationFa: entry.explanationFa,
  });
}

for (const recall of added) {
  const lastIndex = bundle.exercises.findLastIndex((exercise) => exercise.lessonId === recall.lessonId);
  bundle.exercises.splice(lastIndex + 1, 0, recall);
}

writeFileSync(exercisesPath, `${JSON.stringify(bundle, null, 2)}\n`);
console.log(`added ${added.length} recall items across ${counters.size} lessons`);
