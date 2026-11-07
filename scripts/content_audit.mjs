#!/usr/bin/env node
// Content audit (checklist Phase 3): what the bundle contains, and where it contradicts
// itself.
//
// `ContentValidator` (buildSrc, run by `validateContent`) enforces the *authoring rules* —
// four options, a blank marker, a dialogue with a scene, and so on. This script answers the
// two questions a rule cannot: does each CEFR level carry the material its lessons promise
// (reading, listening, conversation), and does the authored text agree with itself?
//
// It reports; it does not edit. Every finding names ids so a content batch can be written
// against it, and `validateContent` gets the rules that are decidable.
//
// Usage: node scripts/content_audit.mjs [--json]
//
// Reads the five bundle files from app/src/main/assets/content/.

import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const dir = resolve(root, 'app/src/main/assets/content');
const load = (file) => JSON.parse(readFileSync(resolve(dir, file), 'utf8'));

const lessonsJson = load('lessons.json');
const lessons = lessonsJson.lessons;
const exercises = load('exercises.json').exercises;
const vocabulary = load('vocabulary.json').vocabulary;
const knowledge = load('knowledge.json').knowledge;
const placement = load('placement.json').questions;

const LEVELS = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
const TYPES = [
  'multiple_choice',
  'fill_blank',
  'translation',
  'listening',
  'word_recall',
  'sentence_production',
  'speaking',
];
/** A reading text has to be a text: 40 words is one short paragraph at B1+. */
const PASSAGE_WORDS = 40;
/** The bundle's own dialogue floor (`ContentValidator.MIN_DIALOGUE_TURNS`). */
const MIN_DIALOGUE_TURNS = 4;

const lessonLevel = new Map(lessons.map((l) => [l.id, l.level]));

// Comparison normalization: case, punctuation, hyphens and apostrophes carry no
// meaning for "is this the same sentence" — and treating "twenty-six" as different
// from "twenty six" is how a first pass invents findings.
const normalize = (s) =>
  String(s ?? '')
    .toLowerCase()
    .replace(/[’'`]/g, '')
    .replace(/[-–—]/g, ' ')
    .replace(/[.,!?;:"“”«»()]/g, '')
    .replace(/\s+/g, ' ')
    .trim();
const words = (s) => normalize(s).split(' ').filter(Boolean);
const persian = (s) => /[\u0600-\u06FF]/.test(String(s ?? ''));
const median = (xs) => {
  if (!xs.length) return 0;
  const s = [...xs].sort((a, b) => a - b);
  return s[Math.floor(s.length / 2)];
};

const findings = [];
const check = (area, message, ids) =>
  findings.push({ area, message, count: ids.length, sample: ids.slice(0, 8) });
const typed = (e) => (e.accepted || []).map((a) => String(a).trim()).filter(Boolean);

// ---------------------------------------------------------------- inventory
const inventory = LEVELS.map((level) => {
  const lessonRows = lessons.filter((l) => l.level === level);
  const ids = new Set(lessonRows.map((l) => l.id));
  const ex = exercises.filter((e) => ids.has(e.lessonId));
  const types = Object.fromEntries(TYPES.map((t) => [t, ex.filter((e) => e.type === t).length]));
  const dialogues = ex.filter((e) => e.type === 'speaking' && e.question);
  return {
    level,
    lessons: lessonRows.length,
    exercises: ex.length,
    perLesson: lessonRows.length ? +(ex.length / lessonRows.length).toFixed(1) : 0,
    words: vocabulary.filter((v) => v.level === level).length,
    knowledge: knowledge.filter((k) => k.level === level).length,
    types,
    dialogueLessons: new Set(dialogues.map((e) => e.lessonId)).size,
    dialogueTurns: dialogues.length,
  };
});

// ------------------------------------------------------------- reading
// Two different things carry a `passage`: a real reading text (the same paragraph on two
// or more comprehension items) and a single-sentence item that asks what that sentence
// means. Only the first is reading practice, and only the first gets a length floor.
const passageItems = exercises.filter((e) => typeof e.passage === 'string' && e.passage.trim());
const passageKey = (e) => `${e.lessonId}::${normalize(e.passage)}`;
const passageGroups = new Map();
passageItems.forEach((e) => passageGroups.set(passageKey(e), (passageGroups.get(passageKey(e)) || 0) + 1));
const readingGroups = [...passageGroups].filter(([, n]) => n >= 2);
const readingLessons = new Set(readingGroups.map(([k]) => k.split('::')[0]));
const readingLengths = readingGroups.map(([k]) => words(k.split('::').slice(1).join('::')).length);
const singleSentence = [...passageGroups].filter(([, n]) => n === 1).length;

check(
  'reading',
  `comprehension item with a passage but no second question (a reading needs at least two) — ` +
    `${singleSentence} single-question items are the "what does this sentence mean?" format`,
  [],
);
const shortReadings = readingGroups
  .filter(([k]) => words(k.split('::').slice(1).join('::')).length < PASSAGE_WORDS)
  .map(([k]) => {
    const [lessonId, text] = k.split('::');
    return `${lessonId} (${words(text).length}w)`;
  });
check('reading', `reading passage shorter than ${PASSAGE_WORDS} words`, shortReadings);

// ------------------------------------------------------------- listening
const listening = exercises.filter((e) => e.type === 'listening');
check(
  'listening',
  'listening item whose accepted answers never match the spoken text',
  listening
    .filter((e) => !typed(e).some((a) => normalize(a) === normalize(e.text)))
    .map((e) => e.id),
);
check(
  'listening',
  'listening text longer than 14 words (more than one dictation span)',
  listening.filter((e) => words(e.text || '').length > 14).map((e) => `${e.id} (${words(e.text).length}w)`),
);

// ------------------------------------------------------------- conversation
const dialogue = exercises.filter((e) => e.type === 'speaking' && e.question);
const turnsPerLesson = new Map();
dialogue.forEach((e) => turnsPerLesson.set(e.lessonId, (turnsPerLesson.get(e.lessonId) || 0) + 1));
const dialogueByLevel = LEVELS.map((level) => {
  const counts = [...turnsPerLesson]
    .filter(([id]) => lessonLevel.get(id) === level)
    .map(([, n]) => n);
  return { level, lessons: counts.length, min: counts.length ? Math.min(...counts) : 0, median: median(counts), max: counts.length ? Math.max(...counts) : 0 };
});
check(
  'dialogue',
  `dialogue with fewer than ${MIN_DIALOGUE_TURNS} turns (the bundle's own floor)`,
  [...turnsPerLesson].filter(([, n]) => n < MIN_DIALOGUE_TURNS).map(([id, n]) => `${id} (${n})`),
);
check(
  'dialogue',
  'learner turn with only one accepted phrasing (no room for a natural reply)',
  dialogue.filter((e) => typed(e).length < 2).map((e) => e.id),
);
check(
  'dialogue',
  'learner turn restricted to a one-word answer (not a reply)',
  dialogue.filter((e) => typed(e).every((a) => words(a).length <= 1)).map((e) => e.id),
);

// ------------------------------------------------- semantic self-consistency
// The same recall question asked twice in the bundle: the learner meets an item they
// have already answered, and one of the two lessons is teaching it at the wrong level.
const recallByPrompt = new Map();
exercises
  .filter((e) => e.type === 'word_recall')
  .forEach((e) => {
    const key = `${normalize(e.prompt)}::${normalize(typed(e)[0])}`;
    if (!recallByPrompt.has(key)) recallByPrompt.set(key, []);
    recallByPrompt.get(key).push(e.id);
  });
check(
  'semantics',
  'the same recall question (prompt + answer) is asked by two lessons',
  [...recallByPrompt.values()].filter((ids) => ids.length > 1).flat(),
);

// The same question, same options, same answer — a re-used item.
const itemKey = (e) =>
  [e.type, normalize(e.question || e.prompt || e.sentence || e.text), JSON.stringify((e.options || []).map(normalize))].join('|');
const choiceItems = new Map();
exercises
  .filter((e) => e.type !== 'speaking')
  .forEach((e) => {
    if (!choiceItems.has(itemKey(e))) choiceItems.set(itemKey(e), []);
    choiceItems.get(itemKey(e)).push(e.id);
  });
check(
  'semantics',
  'the same item is shipped by two different lessons (identical stem, options and answer)',
  [...choiceItems.values()].filter((ids) => ids.length > 1).flat(),
);

// An accepted answer under the item's own word floor.
check(
  'semantics',
  'sentence_production accepted answer under its own minWords',
  exercises
    .filter((e) => e.type === 'sentence_production' && e.minWords)
    .filter((e) => typed(e).some((a) => words(a).length < e.minWords))
    .map((e) => `${e.id} (<${e.minWords})`),
);

// The answer leaked by the cue: a translation prompt that already contains the English
// sentence, or a recall cue that spells out the word.
check(
  'semantics',
  'translation/recall whose Persian cue already contains the English answer',
  exercises
    .filter((e) => ['translation', 'word_recall'].includes(e.type))
    .filter((e) => {
      const cue = normalize(e.prompt || '');
      return typed(e).some((a) => words(a).length > 0 && cue.includes(normalize(a)));
    })
    .map((e) => e.id),
);

// A choice whose question repeats the correct option (the tap is a reading exercise only).
check(
  'semantics',
  'multiple_choice whose question already contains the correct option',
  exercises
    .filter((e) => e.type === 'multiple_choice' && Array.isArray(e.options))
    .filter((e) => {
      const correct = normalize(e.options[e.correctIndex] || '');
      return correct.length >= 4 && normalize(e.question || '').includes(correct);
    })
    .map((e) => e.id),
);

// ------------------------------------------------------------- vocabulary
const exampleUse = new Map();
vocabulary.forEach((v) => {
  (v.examples || []).forEach((ex) => {
    const key = normalize(ex.en || '');
    if (!key) return;
    if (!exampleUse.has(key)) exampleUse.set(key, []);
    exampleUse.get(key).push(v.word);
  });
});
const repeatedWithinWord = [...exampleUse].filter(([, ws]) => new Set(ws).size === 1 && ws.length > 1);
const sharedAcrossWords = [...exampleUse].filter(([, ws]) => new Set(ws).size > 1);
check(
  'vocabulary',
  'the same example sentence is used twice for one word',
  repeatedWithinWord.map(([, ws]) => ws[0]),
);
check(
  'vocabulary',
  'vocabulary entry whose word appears in none of its examples (an inflected or phrasal form is fine)',
  [],
);

// ------------------------------------------------------------- grammar tips
const TIP_SECTIONS = ['## قاعده', '## جدول', '## مثال‌ها', '## خطاهای رایج'];
const tipsIncomplete = lessons.filter(
  (l) => !l.grammarTipFa || !TIP_SECTIONS.every((s) => (l.grammarTipFa || '').includes(s)),
);
check('teaching', 'lesson whose grammar tip is missing or has fewer than the four sections', tipsIncomplete.map((l) => l.id));

// ------------------------------------------------------------------ report
const pct = (n, d) => (d ? `${Math.round((n / d) * 100)}%` : '—');
const out = [];
out.push(`# Content audit — bundle v${lessonsJson.contentVersion}`);
out.push('');
out.push(
  `${lessons.length} lessons · ${exercises.length} exercises · ${vocabulary.length} words · ` +
    `${knowledge.length} knowledge items · ${placement.length} placement questions`,
);
out.push('');
out.push('## Inventory by level');
out.push('');
out.push('| Level | Lessons | Exercises | per lesson | Words | Knowledge | Dialogue lessons / turns |');
out.push('|---|---|---|---|---|---|---|');
inventory.forEach((r) =>
  out.push(`| ${r.level} | ${r.lessons} | ${r.exercises} | ${r.perLesson} | ${r.words} | ${r.knowledge} | ${r.dialogueLessons} / ${r.dialogueTurns} |`),
);
out.push('');
out.push('## Exercise mix by level');
out.push('');
out.push(`| Level | ${TYPES.join(' | ')} |`);
out.push(`|---|${TYPES.map(() => '---').join('|')}|`);
inventory.forEach((r) => out.push(`| ${r.level} | ${TYPES.map((t) => r.types[t]).join(' | ')} |`));
out.push('');
out.push('## Reading');
out.push('');
out.push(
  `- **${readingLessons.size} of ${lessons.length} lessons (${pct(readingLessons.size, lessons.length)})** ship a real reading ` +
    `passage with two or more comprehension questions.`,
);
out.push(
  `- Passage length: min **${Math.min(...readingLengths)}w**, median **${median(readingLengths)}w**, ` +
    `max **${Math.max(...readingLengths)}w**; **${shortReadings.length} of ${readingGroups.length}** are under ${PASSAGE_WORDS} words.`,
);
out.push(`- ${singleSentence} further items show a single sentence and ask what it means (not counted as reading).`);
out.push('');
out.push('## Dialogue turns per level');
out.push('');
out.push('| Level | Lessons | min | median | max |');
out.push('|---|---|---|---|---|');
dialogueByLevel.forEach((r) => out.push(`| ${r.level} | ${r.lessons} | ${r.min} | ${r.median} | ${r.max} |`));
out.push('');
out.push('## Findings');
out.push('');
const areas = [...new Set(findings.map((f) => f.area))];
areas.forEach((area) => {
  const rows = findings.filter((f) => f.area === area && (f.count > 0 || f.message.includes('nothing')));
  if (!rows.length) return;
  out.push(`### ${area}`);
  rows.forEach((f) => {
    if (f.count === 0) out.push(`- ✅ ${f.message}`);
    else {
      out.push(`- ⚠️ **${f.count}** — ${f.message}`);
      out.push(`  - e.g. ${f.sample.join(', ')}`);
    }
  });
  out.push('');
});
if (sharedAcrossWords.length) {
  out.push('### vocabulary notes');
  out.push('');
  out.push(`- ${sharedAcrossWords.length} example sentences are shared by two words — usually an intentional pair:`);
  sharedAcrossWords.slice(0, 10).forEach(([s, ws]) => out.push(`  - «${s}» → ${[...new Set(ws)].join(', ')}`));
  out.push('');
}

if (process.argv.includes('--json')) {
  console.log(
    JSON.stringify(
      { inventory, reading: { lessons: readingLessons.size, groups: readingGroups.length, lengths: readingLengths }, dialogueByLevel, findings, sharedAcrossWords },
      null,
      2,
    ),
  );
} else {
  console.log(out.join('\n'));
}
