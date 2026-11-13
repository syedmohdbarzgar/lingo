#!/usr/bin/env node
/**
 * v80 — a second `writing` item per level.
 *
 * v79 shipped the type with one text per level, in the lesson whose topic was most
 * obviously a piece of writing. That left the format skippable: six lessons out of
 * 192, and a later authoring pass could drop one without anything noticing. This
 * batch adds a second text in **another** lesson of each level, chosen so the item
 * asks for a different structure than its sibling (a routine, a past event, an
 * experience, a report paragraph, an argument, a narrative analysis), and each is
 * written from its lesson's own word list and grammar tip — nothing invented.
 *
 * Run once: `node scripts/archive/v80_writing_extend.mjs`. Idempotent per lesson (a
 * lesson that already asks for a text is left alone) and it bumps `contentVersion`
 * to 80 in all five bundle files.
 */
import fs from 'node:fs'
import path from 'node:path'

const DIR = path.join(process.cwd(), 'app/src/main/assets/content')
const VERSION = 80

const ITEMS = [
  {
    lessonId: 'a1.daily-routine.lesson-01',
    difficulty: 2,
    prompt: 'روزت را در دو جمله بنویس: صبح چه می‌کنی و بعد چه کار می‌کنی.',
    explanationFa: 'برای کار تکراری حال ساده می‌آید: I usually wake up at seven.',
    hintFa: 'با usually و فعل حال ساده جمله بساز.',
    mustUse: ['usually', 'wake up'],
    minWords: 8,
    minSentences: 2,
    accepted: ['I usually wake up at seven. Then I have breakfast and go to work.'],
  },
  {
    lessonId: 'a2.past-simple.lesson-01',
    difficulty: 2,
    prompt: 'دیروزت را در دو جمله بنویس: چه کاری کردی و کجا رفتی.',
    explanationFa: 'رویداد تمام‌شدهٔ گذشته با شکل گذشتهٔ فعل: went, bought, saw, ate.',
    hintFa: 'فعل گذشتهٔ نامنظم را به یاد بیاور: went, bought, saw.',
    mustUse: ['yesterday', 'went'],
    minWords: 10,
    minSentences: 2,
    accepted: ['Yesterday I went to the market with my sister. We bought some fruit and then we ate lunch.'],
  },
  {
    lessonId: 'b1.experiences.lesson-01',
    difficulty: 3,
    prompt: 'یک تجربهٔ سفر را در دو یا سه جمله بنویس: کجا رفته‌ای و چه چیزی برایت ماند.',
    explanationFa: 'تجربهٔ بدون زمان مشخص با حال کامل: I have travelled abroad twice.',
    hintFa: 'حال کامل برای تجربه، و never برای چیزی که هرگز نکرده‌ای.',
    mustUse: ['have', 'memorable'],
    minWords: 16,
    minSentences: 2,
    accepted: [
      'I have travelled abroad twice. My most memorable journey was a week of exploring in Turkey, and I brought a small souvenir home.',
    ],
  },
  {
    lessonId: 'b2.reports.lesson-01',
    difficulty: 3,
    prompt: 'یک بند کوتاه از یک گزارش بنویس: یافته، روشِ گردآوری داده، و یک توصیه.',
    explanationFa: 'روش گردآوری با مجهول گذشتهٔ ساده: The data was collected in May.',
    hintFa: 'مجهول گذشتهٔ ساده: was/were + قسمت سوم فعل.',
    mustUse: ['was collected', 'recommendation'],
    minWords: 16,
    minSentences: 3,
    accepted: [
      'The data was collected in May from three regional offices. The main finding is a steady decline in response rates. Our recommendation is to shorten the survey.',
    ],
  },
  {
    lessonId: 'c1.argumentation.lesson-01',
    difficulty: 3,
    prompt: 'یک ادعا و پاسخ به یک نقد را در دو جمله بنویس؛ جمله را با ساختار تأکیدی آغاز کن.',
    explanationFa: 'تأکید با آغاز منفی و جابه‌جایی فعل کمکی: Not only does the study fail to …',
    hintFa: 'Not only does … but …، و burden of proof برای بار اثبات.',
    mustUse: ['not only', 'burden of proof'],
    minWords: 18,
    minSentences: 2,
    accepted: [
      'Not only does the study fail to establish a link, but its sample was also small. The burden of proof therefore lies with the claim, not the critique.',
    ],
  },
  {
    lessonId: 'c2.narrative.lesson-01',
    difficulty: 3,
    prompt: 'یک بند کوتاه تحلیلی دربارهٔ یک روایت بنویس: چیزی که پیش از شروع رخ داده و یک تروپ.',
    explanationFa: 'رویداد پیش از نقطهٔ مرجع با past perfect: had already been established.',
    hintFa: 'برای رویداد پیش از نقطهٔ مرجع: had + past participle.',
    mustUse: ['had', 'trope'],
    minWords: 16,
    minSentences: 2,
    accepted: [
      'The backstory had already been established before the first chapter opened. The novel uses that trope deliberately, so the denouement reads as inevitable rather than sudden.',
    ],
  },
]

const read = (file) => JSON.parse(fs.readFileSync(path.join(DIR, file), 'utf8'))
const write = (file, data) =>
  fs.writeFileSync(path.join(DIR, file), `${JSON.stringify(data, null, 2)}\n`)

const bundle = read('exercises.json')
const ids = new Set(bundle.exercises.map((e) => e.id))
let added = 0

for (const item of ITEMS) {
  // One text per lesson: a lesson that already asks for one is left alone, so the
  // script can be re-run without stacking duplicate prompts on the same topic.
  if (bundle.exercises.some((e) => e.lessonId === item.lessonId && e.type === 'writing')) {
    console.log(`skip ${item.lessonId} (already asks for a text)`)
    continue
  }
  const mine = bundle.exercises.filter((e) => e.lessonId === item.lessonId)
  const maxEx = mine.reduce((best, e) => {
    const m = /\.ex\.(\d+)$/.exec(e.id)
    return m ? Math.max(best, Number(m[1])) : best
  }, 0)
  const maxOrder = mine.reduce((best, e) => Math.max(best, e.order ?? 0), 0)
  const id = `${item.lessonId.replace('.lesson-01', '')}.ex.${String(maxEx + 1).padStart(2, '0')}`
  if (ids.has(id)) {
    console.log(`skip ${id} (already in the bundle)`)
    continue
  }
  const lastIndex = bundle.exercises.reduce(
    (best, e, i) => (e.lessonId === item.lessonId ? i : best),
    -1,
  )
  bundle.exercises.splice(lastIndex + 1, 0, {
    id,
    lessonId: item.lessonId,
    type: 'writing',
    order: maxOrder + 1,
    difficulty: item.difficulty,
    prompt: item.prompt,
    hintFa: item.hintFa,
    mustUse: item.mustUse,
    minWords: item.minWords,
    minSentences: item.minSentences,
    accepted: item.accepted,
    skill: 'WRITING',
    explanationFa: item.explanationFa,
  })
  ids.add(id)
  added += 1
  console.log(`+ ${id} (order ${maxOrder + 1}, difficulty ${item.difficulty})`)
}

bundle.contentVersion = VERSION
write('exercises.json', bundle)

for (const file of ['lessons.json', 'vocabulary.json', 'placement.json', 'knowledge.json']) {
  const data = read(file)
  data.contentVersion = VERSION
  write(file, data)
}

console.log(`\n${added} writing item(s) added; contentVersion ${VERSION} in all five files.`)
