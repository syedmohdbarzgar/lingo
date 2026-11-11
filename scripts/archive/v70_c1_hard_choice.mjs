#!/usr/bin/env node
/**
 * v70: gives every C1 lesson a hard-end choice item.
 *
 * The C1 difficulty pass (v69) left 22 of the level's 31 lessons with no
 * multiple-choice item at 3 — their axis lived only in a blank or a reading
 * question. `AdaptiveExerciseSelector` raises the level *within a format*, so a
 * lesson with no hard choice could not raise recognition at all: the drill
 * stayed flat however consolidated the meter was.
 *
 * Each item added here is one exercise: four grammatical options where the
 * context decides, which is the shape the level's own hard items already had
 * (`c1.persuasion.ex.01`'s four inversion orders, `c1.statistics.ex.05`'s
 * agreement). Error-spotting items ("choose the correct sentence") are
 * deliberately not used: they were judged 2 in v69, because three of the four
 * options break the structure outright and only one is grammatical.
 *
 * Run once, then move to scripts/archive/ — never re-run a content batch.
 */
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const contentDir = join(dirname(dirname(fileURLToPath(import.meta.url))), 'app', 'src', 'main', 'assets', 'content');
const exercisesPath = join(contentDir, 'exercises.json');

/** id suffix is the lesson's own topic prefix; `order` is appended after its last item. */
const items = [
  {
    lessonId: 'c1.phrasal-idioms.lesson-01',
    question: 'The talks fell ___ at the last minute.',
    questionFa: 'مذاکرات در آخرین لحظه شکست خورد.',
    options: ['through', 'off', 'out', 'over'],
    skill: 'VOCABULARY',
    explanationFa: 'fall through یعنی انجام نشدن و شکست خوردن؛ fall off و fall out و fall over این معنی را نمی‌دهند.',
  },
  {
    lessonId: 'c1.inference.lesson-01',
    question: 'The lights are on and the door is unlocked. They ___ have gone away for the week.',
    questionFa: 'چراغ‌ها روشن و در باز است. آن‌ها نمی‌توانند یک هفته رفته باشند.',
    options: ["can't", 'must', 'should', 'might'],
    skill: 'GRAMMAR',
    explanationFa: 'شاهدِ جمله خلاف رفتن را نشان می‌دهد، پس deduction منفی می‌سازیم: can\'t have gone.',
  },
  {
    lessonId: 'c1.executive.lesson-01',
    question: 'Under no circumstances ___ the figures leave this room.',
    questionFa: 'تحت هیچ شرایطی این ارقام نباید از این اتاق بیرون برود.',
    options: ['should', 'they should', 'should they', 'do'],
    skill: 'GRAMMAR',
    explanationFa: 'عبارت منفیِ آغاز جمله وارونگی می‌سازد: should the figures leave (نه they should).',
  },
  {
    lessonId: 'c1.negotiation.lesson-01',
    question: '___ we settled in March, we would be in a stronger position now.',
    questionFa: 'اگر در مارس توافق کرده بودیم، الان در موقعیت قوی‌تری بودیم.',
    options: ['Had', 'Have', 'Should', 'Were'],
    skill: 'GRAMMAR',
    explanationFa: 'شرطی وارونهٔ گذشته: Had we settled = If we had settled.',
  },
  {
    lessonId: 'c1.international.lesson-01',
    question: 'Seldom ___ the two sides agreed so quickly as they did this week.',
    questionFa: 'به‌ندرت پیش آمده دو طرف به این سرعت توافق کنند.',
    options: ['have', 'has', 'did', 'they have'],
    skill: 'GRAMMAR',
    explanationFa: 'Seldom در آغاز جمله وارونگی می‌سازد و با as they did this week به حال کامل نیاز دارد: Seldom have the two sides agreed.',
  },
  {
    lessonId: 'c1.economy.lesson-01',
    question: 'Not only ___ inflation rise, but unemployment also grew.',
    questionFa: 'نه‌تنها تورم بالا رفت، بیکاری هم زیاد شد.',
    options: ['did', 'has', 'was', 'it did'],
    skill: 'GRAMMAR',
    explanationFa: 'Not only در آغاز جمله کمکی پیش از فاعل می‌آورد: Not only did inflation rise.',
  },
  {
    lessonId: 'c1.tech-ethics.lesson-01',
    question: 'Auditors ___ approve each release; a human review is enough.',
    questionFa: 'بازرسان لازم نیست هر انتشار را تأیید کنند؛ بازبینی انسانی کافی است.',
    options: ['do not have to', 'must not', 'should not', 'cannot'],
    skill: 'GRAMMAR',
    explanationFa: 'نداشتن الزام با do not have to گفته می‌شود؛ must not ممنوعیت است و با «بازبینی انسانی کافی است» نمی‌خواند.',
  },
  {
    lessonId: 'c1.philosophy.lesson-01',
    question: 'Were the premise false, the conclusion ___ collapse.',
    questionFa: 'اگر مقدمه نادرست بود، نتیجه فرو می‌ریخت.',
    options: ['would', 'will', 'should', 'had'],
    skill: 'GRAMMAR',
    explanationFa: 'Were + فاعل شرطی خیالی می‌سازد و جواب با would می‌آید.',
  },
  {
    lessonId: 'c1.cognitive-bias.lesson-01',
    question: 'Had the panel known the base rate, it ___ have anchored on the first figure.',
    questionFa: 'اگر پنل نرخ پایه را می‌دانست، روی نخستین عدد لنگر نمی‌انداخت.',
    options: ["wouldn't", "mustn't", "shouldn't", "couldn't"],
    skill: 'GRAMMAR',
    explanationFa: 'شرطی مختلط: گذشتهٔ خیالی (Had known) با نتیجهٔ خیالی would not have anchored.',
  },
  {
    lessonId: 'c1.media-analysis.lesson-01',
    question: 'The editor, ___ approved the story, has resigned.',
    questionFa: 'ویرایشگری که خبر را تأیید کرده بود استعفا داد.',
    options: ['who', 'that', 'which', 'whom'],
    skill: 'GRAMMAR',
    explanationFa: 'بند توضیحیِ غیرمحدودکننده پس از ویرگول با who می‌آید؛ that در آن جایز نیست.',
  },
  {
    lessonId: 'c1.org-change.lesson-01',
    question: 'By the time the announcement came, the plant ___ closed for six weeks.',
    questionFa: 'وقتی اطلاعیه آمد، کارخانه شش هفته بود که بسته شده بود.',
    options: ['had been', 'has been', 'was', 'is'],
    skill: 'GRAMMAR',
    explanationFa: 'گذشتهٔ کامل مجهول برای کاری که پیش از رویداد دیگری کامل شده بود: had been closed.',
  },
  {
    lessonId: 'c1.risk.lesson-01',
    question: '___ the supplier fail, we would lose two weeks.',
    questionFa: 'اگر تأمین‌کننده از کار بیفتد، دو هفته از دست می‌دهیم.',
    options: ['Should', 'If', 'Were', 'Had'],
    skill: 'GRAMMAR',
    explanationFa: 'شرطی رسمی با وارونگی: Should the supplier fail = If the supplier failed.',
  },
  {
    lessonId: 'c1.research-reports.lesson-01',
    question: 'The survey, ___ findings are summarised on page two, was conducted in June.',
    questionFa: 'پیمایشی که یافته‌هایش در صفحهٔ دوم خلاصه شده در ژوئن انجام شد.',
    options: ['whose', 'which', 'that', 'of which'],
    skill: 'GRAMMAR',
    explanationFa: 'مالکیت در بند موصولی با whose بیان می‌شود: whose findings.',
  },
  {
    lessonId: 'c1.research-method.lesson-01',
    question: 'The protocol should have been recorded, ___?',
    questionFa: 'پروتکل باید ثبت می‌شد، نه؟',
    options: ["shouldn't it", "didn't it", "wasn't it", "hasn't it"],
    skill: 'GRAMMAR',
    explanationFa: 'پرسش ضمیمهٔ should have been با shouldn\'t ساخته می‌شود، نه با didn\'t.',
  },
  {
    lessonId: 'c1.data-interpretation.lesson-01',
    question: 'The second quarter was ___ stronger than the first.',
    questionFa: 'سه‌ماههٔ دوم به‌مراتب از اولی قوی‌تر بود.',
    options: ['considerably', 'considerable', 'consider', 'considering'],
    skill: 'GRAMMAR',
    explanationFa: 'درجهٔ قید considerably است؛ considerable صفت است و پیش از stronger نمی‌آید.',
  },
  {
    lessonId: 'c1.research-ethics.lesson-01',
    question: 'The data ___ have been anonymised before the audit; the names were still in the file.',
    questionFa: 'داده‌ها باید پیش از ممیزی ناشناس می‌شدند؛ نام‌ها هنوز در فایل بود.',
    options: ['should', 'must', 'can', 'may'],
    skill: 'GRAMMAR',
    explanationFa: 'سرزنشِ کاری که انجام نشده با should have been گفته می‌شود؛ must have been ادعای قطعیت می‌کند که با شاهد جمله نمی‌خواند.',
  },
  {
    lessonId: 'c1.diversity-inclusion.lesson-01',
    question: 'The policy, ___ excludes part-time staff, is not inclusive.',
    questionFa: 'سیاستی که کارکنان پاره‌وقت را حذف می‌کند فراگیر نیست.',
    options: ['which', 'that', 'who', 'what'],
    skill: 'GRAMMAR',
    explanationFa: 'بند توضیحی غیرمحدودکننده با which می‌آید و that در آن جایز نیست.',
  },
  {
    lessonId: 'c1.business-sustainability.lesson-01',
    question: '___ the packaging compostable, the line would qualify.',
    questionFa: 'اگر بسته‌بندی تجزیه‌پذیر بود، خط تولید واجد شرایط می‌شد.',
    options: ['Were', 'Was', 'If', 'Had'],
    skill: 'GRAMMAR',
    explanationFa: 'Were + گروه اسمی/صفت شرطی خیالی می‌سازد: Were the packaging compostable.',
  },
  {
    lessonId: 'c1.semantic-nuance.lesson-01',
    question: 'The second word is ___ less loaded than the first.',
    questionFa: 'واژهٔ دوم به‌مراتب بار عاطفی کمتری از اولی دارد.',
    options: ['far', 'very', 'too', 'so'],
    skill: 'GRAMMAR',
    explanationFa: 'درجهٔ مقایسه با far less تقویت می‌شود؛ very less در انگلیسی معیار نیست.',
  },
  {
    lessonId: 'c1.argumentation.lesson-01',
    question: 'The claim would have been stronger ___ the authors qualified it.',
    questionFa: 'ادعا قوی‌تر می‌بود اگر نویسندگان آن را مشروط می‌کردند.',
    options: ['had', 'if had', 'would', 'were'],
    skill: 'GRAMMAR',
    explanationFa: 'شرطی وارونهٔ گذشته: had the authors qualified it = if the authors had qualified it.',
  },
  {
    lessonId: 'c1.proposals.lesson-01',
    question: 'We will not start in May ___ the client approves the scope.',
    questionFa: 'اگر مشتری محدوده را تأیید نکند، در ماه مه شروع نمی‌کنیم.',
    options: ['unless', 'if', 'whether', 'until'],
    skill: 'GRAMMAR',
    explanationFa: 'با فعل منفی، شرط استثنا با unless گفته می‌شود.',
  },
  {
    lessonId: 'c1.stakeholders.lesson-01',
    question: 'The board asked when ___.',
    questionFa: 'هیئت‌مدیره پرسید آزمون آزمایشی چه زمانی شروع می‌شود.',
    options: ['the pilot would start', 'would the pilot start', 'the pilot will start', 'will the pilot start'],
    skill: 'GRAMMAR',
    explanationFa: 'در سؤال نقل‌شده ترتیب جمله خبری است و فعل به گذشتهٔ نقل برمی‌گردد: when the pilot would start.',
  },
];

const bundle = JSON.parse(readFileSync(exercisesPath, 'utf8'));
const added = [];

for (const item of items) {
  const own = bundle.exercises.filter((e) => e.lessonId === item.lessonId);
  if (own.length === 0) throw new Error(`no exercises for ${item.lessonId}`);
  const maxOrder = Math.max(...own.map((e) => e.order ?? 0));
  const topic = item.lessonId.replace('.lesson-01', '');
  // The id keeps the `ex.NN` shape of the lesson's other drills: the highest existing
  // drill number plus one, not the next `order` (orders also count dialogue turns).
  const drillNumbers = own
    .map((e) => /^ex\.(\d+)$/.exec(e.id.slice(topic.length + 1))?.[1])
    .filter(Boolean)
    .map((n) => Number(n));
  const suffix = String(Math.max(0, ...drillNumbers) + 1).padStart(2, '0');
  const id = `${topic}.ex.${suffix}`;
  if (bundle.exercises.some((e) => e.id === id)) throw new Error(`id already taken: ${id}`);

  const isPhrasal = item.skill === 'VOCABULARY';
  const exercise = {
    id,
    lessonId: item.lessonId,
    type: 'multiple_choice',
    order: maxOrder + 1,
    difficulty: 3,
    question: item.question,
    questionFa: item.questionFa,
    options: item.options,
    correctIndex: 0,
    explanationFa: item.explanationFa,
    skill: item.skill,
  };
  // Vocabulary items carry `question` too; keep one field order for the whole file.
  if (isPhrasal) exercise.skill = 'VOCABULARY';

  // After the lesson's last item, so the authored order still reads 01..n.
  const lastIndex = bundle.exercises.findLastIndex((e) => e.lessonId === item.lessonId);
  bundle.exercises.splice(lastIndex + 1, 0, exercise);
  added.push(id);
}

writeFileSync(exercisesPath, `${JSON.stringify(bundle, null, 2)}\n`);
console.log(`added ${added.length} hard-end choice items`);
for (const id of added) console.log(`  ${id}`);
