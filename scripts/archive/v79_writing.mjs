#!/usr/bin/env node
/**
 * v79 — the first `writing` items (one short connected text per level).
 *
 * The exercise type ships with the bundle or not at all: a format no lesson asks
 * for is a screen nobody can reach, which is the same false signal as an empty
 * test suite. Each item is authored from the lesson's **own** material — the words
 * its word list teaches and the structure its grammar tip names — so the anchors
 * are graded against something the lesson actually taught:
 *
 *   a1.family          have / has                → introduce your family
 *   a2.hobbies         enjoy + -ing, spare time  → your hobby and why
 *   b1.plans           going to / will + probably → plans for the summer
 *   b2.problems        passive present perfect   → a short formal complaint
 *   c1.statistics      data show …               → describe a finding
 *   c2.academic-writing studies have been …      → claim, background, limit
 *
 * One per level on purpose: a text is the hardest thing a lesson asks (difficulty
 * 3 wherever the level's band allows it), and the type is meant to be extended by
 * later authoring passes, not bulk-generated here.
 *
 * Run once: `node scripts/archive/v79_writing.mjs`. It is idempotent — an item that
 * already exists is left alone — and it bumps `contentVersion` to 79 in all five
 * bundle files, because the seed reads that number.
 */
import fs from 'node:fs'
import path from 'node:path'

const DIR = path.join(process.cwd(), 'app/src/main/assets/content')
const VERSION = 79

const ITEMS = [
  {
    lessonId: 'a1.family.lesson-01',
    explanationFa: 'در جملهٔ دوم با is یا works بگو یکی از اعضای خانواده چه کار می‌کند.',
    difficulty: 2,
    prompt: 'خانواده‌ات را در دو جمله معرفی کن: چند نفر هستند و یکی از آن‌ها چه کار می‌کند.',
    hintFa: 'با have شروع کن و در جملهٔ دوم is یا works را به کار ببر.',
    mustUse: ['have'],
    minWords: 8,
    minSentences: 2,
    accepted: ['I have a small family. My father is a teacher and my sister is a student.'],
  },
  {
    lessonId: 'a2.hobbies.lesson-01',
    explanationFa: 'بعد از enjoy فعل با ing می‌آید؛ دلیل را در جملهٔ دوم با because بگو.',
    difficulty: 2,
    prompt: 'دو جمله دربارهٔ سرگرمی‌ات بنویس: چه کاری دوست داری و در وقت آزادت چه می‌کنی.',
    hintFa: 'بعد از enjoy فعل با ing می‌آید.',
    mustUse: ['enjoy', 'spare time'],
    minWords: 10,
    minSentences: 2,
    accepted: ['I enjoy painting in my spare time. I also collect old stamps because they are beautiful.'],
  },
  {
    lessonId: 'b1.plans.lesson-01',
    explanationFa: 'going to برای قصد از پیش‌پذیرفته‌شده، will + probably برای پیش‌بینی.',
    difficulty: 3,
    prompt: 'برنامهٔ تابستانت را در سه جمله بنویس: چه کاری می‌خواهی بکنی، چه چیزی احتمال دارد، و اگر هوا خوب بود چه می‌کنی.',
    hintFa: 'قصد از پیش‌پذیرفته‌شده با going to، پیش‌بینی با will و احتمال با probably.',
    mustUse: ['going to', 'probably'],
    minWords: 18,
    minSentences: 3,
    accepted: [
      'I am going to take a new course next month. I will probably visit my family in the summer. If the weather is good, we are going to travel.',
    ],
  },
  {
    lessonId: 'b2.problems.lesson-01',
    explanationFa: 'مجهول حال کامل (has not been delivered) و درخواست با I would like a refund.',
    difficulty: 3,
    prompt: 'یک شکایت رسمی کوتاه بنویس: چه چیزی تحویل نشده و چه می‌خواهی.',
    hintFa: 'مجهول حال کامل: has not been …',
    mustUse: ['has not been', 'refund'],
    minWords: 14,
    minSentences: 3,
    accepted: [
      'The order has not been delivered yet. I have contacted support twice, but there is no answer. I would like a full refund.',
    ],
  },
  {
    lessonId: 'c1.statistics.lesson-01',
    explanationFa: 'data با فعل جمع می‌آید و However محدودیت را معرفی می‌کند.',
    difficulty: 3,
    prompt: 'یک یافتهٔ آماری را در دو جمله توصیف کن: چه چیزی دیده شد و چه محدودیتی داشت.',
    hintFa: 'data در نگارش آکادمیک جمع است: The data show …',
    mustUse: ['data', 'correlation'],
    minWords: 16,
    minSentences: 2,
    accepted: [
      'The data show a strong correlation between the two variables. However, three outliers were removed before the test, so the result is only suggestive.',
    ],
  },
  {
    lessonId: 'c2.academic-writing.lesson-01',
    explanationFa: 'پیشینه با Studies have been conducted on … و محدودیت با However.',
    difficulty: 3,
    prompt: 'یک بند کوتاه آکادمیک بنویس: ادعا، پیشینه، و یک محدودیت.',
    hintFa: 'پیشینه با مجهول حال کامل: studies have been conducted on …',
    mustUse: ['have been', 'thesis'],
    minWords: 16,
    minSentences: 2,
    accepted: [
      'Several studies have been conducted on this thesis. However, the sample sizes were small, so the findings remain tentative.',
    ],
  },
]

/** The next free `ex.NN` id and `order` for a lesson. */
function nextSlot(exercises, lessonId) {
  const mine = exercises.filter((e) => e.lessonId === lessonId)
  const maxEx = mine.reduce((best, e) => {
    const m = /\.ex\.(\d+)$/.exec(e.id)
    return m ? Math.max(best, Number(m[1])) : best
  }, 0)
  const maxOrder = mine.reduce((best, e) => Math.max(best, e.order ?? 0), 0)
  return { ex: maxEx + 1, order: maxOrder + 1 }
}

const read = (file) => JSON.parse(fs.readFileSync(path.join(DIR, file), 'utf8'))
const write = (file, data) =>
  fs.writeFileSync(path.join(DIR, file), `${JSON.stringify(data, null, 2)}\n`)

const bundle = read('exercises.json')
const ids = new Set(bundle.exercises.map((e) => e.id))
let added = 0

for (const item of ITEMS) {
  const slot = nextSlot(bundle.exercises, item.lessonId)
  const id = `${item.lessonId.replace('.lesson-01', '')}.ex.${String(slot.ex).padStart(2, '0')}`
  if (ids.has(id)) {
    console.log(`skip ${id} (already in the bundle)`)
    continue
  }
  // Keep the lesson's exercises contiguous: the new item follows the last one
  // authored for that lesson rather than landing at the end of the file.
  const lastIndex = bundle.exercises.reduce(
    (best, e, i) => (e.lessonId === item.lessonId ? i : best),
    -1,
  )
  const exercise = {
    id,
    lessonId: item.lessonId,
    type: 'writing',
    order: slot.order,
    difficulty: item.difficulty,
    prompt: item.prompt,
    hintFa: item.hintFa,
    mustUse: item.mustUse,
    minWords: item.minWords,
    minSentences: item.minSentences,
    accepted: item.accepted,
    explanationFa: item.explanationFa,
    skill: 'WRITING',
  }
  bundle.exercises.splice(lastIndex + 1, 0, exercise)
  ids.add(id)
  added += 1
  console.log(`+ ${id} (order ${slot.order}, difficulty ${item.difficulty})`)
}

bundle.contentVersion = VERSION
write('exercises.json', bundle)

// All five files must declare the same version, or the validator refuses the
// bundle and the seed re-runs forever.
for (const file of ['lessons.json', 'vocabulary.json', 'placement.json', 'knowledge.json']) {
  const data = read(file)
  data.contentVersion = VERSION
  write(file, data)
}

console.log(`\n${added} writing item(s) added; contentVersion ${VERSION} in all five files.`)
