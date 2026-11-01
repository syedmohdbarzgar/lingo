#!/usr/bin/env node
/**
 * Speech vocabulary gate (checklist 1.8).
 *
 * A Vosk `Recognizer` built with a constrained grammar silently DROPS a word it does
 * not know: the take can then never be `CORRECT`, no matter how well the learner
 * speaks. This script keeps such a word out of the content.
 *
 * It reads every `speaking` item's accepted answers and checks their words:
 *
 *  1. **Model lexicon, when available.** `tools/vosk-words.txt` — one word per line,
 *     lower case — makes every token a hard failure if absent. Generate it from a
 *     model archive that ships `graph/words.txt`; `vosk-model-small-en-us-0.15`
 *     (the one this app bundles) does **not** ship it — its lexicon lives inside
 *     `graph/HCLr.fst` with no symbol table, which is recorded in docs/debt.md.
 *  2. **Name rule, always.** A token that is capitalised inside the sentence and is
 *     not a weekday, month or an entry of `tools/speech-names-allowed.txt` is a
 *     proper noun a small English model almost never knows (`Karimi` was the real
 *     bug). Fail rather than hope.
 *  3. **Character rule, always.** Digits and non-ASCII characters are not words the
 *     recogniser can produce.
 *
 * Usage:  node scripts/check_speech_vocabulary.mjs [--verbose]
 */
import { readFileSync, existsSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = join(dirname(fileURLToPath(import.meta.url)), '..')
const contentDir = join(root, 'app/src/main/assets/content')
const lexiconPath = join(root, 'tools/vosk-words.txt')
const allowedNamesPath = join(root, 'tools/speech-names-allowed.txt')
const verbose = process.argv.includes('--verbose')

// Capitalised without being names: the word that opens a sentence, and the few
// words English capitalises mid-sentence on purpose.
const NEVER_A_NAME = new Set([
  'i', "i'm", "i'll", "i'd", "i've", 'a', 'an', 'the', 'yes', 'no', 'okay', 'ok',
  'well', 'oh', 'hi', 'hello', 'sure', 'please', 'thanks', 'thank', 'sir', 'madam',
])

const WEEKDAYS_AND_MONTHS = new Set([
  'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday',
  'january', 'february', 'march', 'april', 'may', 'june', 'july', 'august',
  'september', 'october', 'november', 'december',
])

const allowedNames = existsSync(allowedNamesPath)
  ? new Set(
      readFileSync(allowedNamesPath, 'utf8')
        .split('\n')
        .map((line) => line.trim().toLowerCase())
        .filter((line) => line && !line.startsWith('#')),
    )
  : new Set()

const lexicon = existsSync(lexiconPath)
  ? new Set(
      readFileSync(lexiconPath, 'utf8')
        .split('\n')
        .map((line) => line.trim().toLowerCase())
        .filter(Boolean),
    )
  : null

/** Words of one authored answer, with the position kept so "capitalised" can be judged. */
function tokens(text) {
  return text
    .replace(/[\u2018\u2019`\u00b4]/g, "'")
    .split(/\s+/)
    .map((raw, index) => ({ raw, index, word: raw.replace(/[^A-Za-z0-9']/g, '') }))
    .filter((token) => token.word.length > 0)
}

const speaking = []
for (const file of ['exercises.json', 'lessons.json']) {
  const path = join(contentDir, file)
  if (!existsSync(path)) continue
  const bundle = JSON.parse(readFileSync(path, 'utf8'))
  for (const exercise of bundle.exercises ?? []) {
    if (exercise.type !== 'speaking') continue
    const answers = exercise.accepted ?? exercise.answers ?? exercise.referenceAnswers ?? []
    speaking.push({ id: exercise.id, file, answers })
  }
}

const problems = []
let checked = 0

for (const item of speaking) {
  for (const answer of item.answers) {
    const words = tokens(answer)
    for (const token of words) {
      checked += 1
      const lower = token.word.toLowerCase()
      const where = `${item.id}: "${answer}" → "${token.raw}"`

      if (/[0-9]/.test(token.word)) {
        problems.push(`${where} — a digit is not a word the recogniser can produce`)
        continue
      }
      if (/[^\x00-\x7F]/.test(token.raw)) {
        problems.push(`${where} — non-ASCII character in a spoken answer`)
        continue
      }

      const previous = words[token.index - 1]?.raw ?? ''
      const opensSentence = token.index === 0 || /[.?!]$/.test(previous)
      const capitalisedInsideSentence = !opensSentence && /^[A-Z]/.test(token.word)
      const isName = capitalisedInsideSentence &&
        !NEVER_A_NAME.has(lower) &&
        !WEEKDAYS_AND_MONTHS.has(lower) &&
        !allowedNames.has(lower)
      if (isName) {
        problems.push(
          `${where} — proper noun in a spoken answer; a small English model cannot hear it. ` +
            'Reword the answer, or add it to tools/speech-names-allowed.txt with a reason',
        )
      }

      if (lexicon && !lexicon.has(lower)) {
        problems.push(`${where} — not in the Vosk model's word list (tools/vosk-words.txt)`)
      }
    }
  }
}

if (verbose) {
  console.log(`speech answers checked: ${speaking.length} items, ${checked} words`)
  console.log(`model lexicon: ${lexicon ? `${lexicon.size} words from tools/vosk-words.txt` : 'absent (name + character rules only)'}`)
}

if (problems.length > 0) {
  console.error(`\n${problems.length} problem(s) in spoken answers:\n`)
  for (const problem of problems) console.error(`  • ${problem}`)
  console.error('')
  process.exit(1)
}

console.log(`speech vocabulary OK — ${speaking.length} speaking items, ${checked} words`)
