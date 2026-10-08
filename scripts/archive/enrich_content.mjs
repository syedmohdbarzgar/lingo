#!/usr/bin/env node
/**
 * Enriches the bundled curriculum (idempotent — skips anything that exists):
 *
 *  1. lessons.json: per-lesson grammarTipFa (intro stage + wrong-answer tips)
 *     and realistic estimatedMinutes for the 6-exercise + intro format.
 *  2. exercises.json:
 *     - expands accepted answers for the five single-answer translations,
 *     - adds word banks for the four A1 translations (lighter production),
 *     - appends one vocabulary MC per lesson (guarantees every lesson practises
 *       its own words — exercise↔vocab binding),
 *     - appends one READING comprehension MC to the first lesson of each level
 *       (READINg skill becomes trainable).
 *  3. bumps contentVersion in every file (drives re-seeding).
 *
 * Run: node scripts/enrich_content.mjs && node scripts/balance_answer_positions.mjs
 */

import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const contentDir = join(root, "app/src/main/assets/content");
const BUNDLE_VERSION = 4;

const read = (name) => JSON.parse(readFileSync(join(contentDir, name), "utf8"));
const write = (name, data) =>
  writeFileSync(join(contentDir, name), JSON.stringify(data, null, 2) + "\n", "utf8");

// ---------------------------------------------------------------- lessons

const grammarTips = {
  "a1.greetings.lesson-01":
    "سلام‌ها به زمان روز بستگی دارند: Good morning تا ظهر، Good afternoon عصر، Good evening غروب — و Good night فقط هنگام خداحافظی شب است.",
  "a1.daily-routine.lesson-01":
    "برای کارهای تکراری از زمان حال ساده استفاده کن: I go to work — با he/she به فعل s می‌چسبد: She goes.",
  "a1.family.lesson-01":
    "داشتن اعضا با have / have got: I have got two brothers — در پرسش، Do you have…? و در پاسخ کوتاه Yes, I do.",
  "a1.shopping.lesson-01":
    "قیمت را با How much is…? (مفرد) و How much are…? (جمع) بپرس؛ اعداد ترتیبی بعد از اسم می‌آیند: twenty dollars.",
  "a2.airport.lesson-01":
    "در فرودگاه اغلب با need to / have to سر و کار داریم: You have to show your passport — و زمان آینده برای برنامه‌ها: The flight will depart.",
  "a2.restaurant.lesson-01":
    "سفارش مودبانه با I'd like… و پیشنهاد با Would you like…؟ — برای غیرقابل شمارش از some/any استفاده کن: some water.",
  "a2.directions.lesson-01":
    "صفت‌های مکانی بعد از فعل می‌آیند: turn left، go straight، it's next to the bank — حرف اضافه مکان را از on/at/in دقیق انتخاب کن.",
  "a2.health.lesson-01":
    "درد را با have got بیان کن: I have got a headache — و توصیه با should: You should rest / You shouldn't smoke.",
  "b1.work.lesson-01":
    "وضعیت فعلی با زمان حال استمراری (I'm working) و تجربه‌ی کاری بدون زمان مشخص با حال کامل (I have worked in…).",
  "b1.news.lesson-01":
    "خبر اغلب مجهول است: The law was announced yesterday — و برای نقل خبر از say/report با گذشته استفاده کن.",
  "b1.plans.lesson-01":
    "قصد از قبل‌پذیرفته‌شده با be going to و تصمیم لحظه‌ای با will — شرطی نوع اول: If it rains, I will stay home.",
  "b1.experiences.lesson-01":
    "تجربه بدون زمان مشخص با حال کامل: I have visited Turkey — با ever/never برای پرسش و for/since برای مدت.",
  "b2.technology.lesson-01":
    "مقایسه با more و the most: more efficient، the most useful — و امکان‌سنجی با can/could.",
  "b2.environment.lesson-01":
    "مجهول پیشرفته: It is believed that… — و انتقاد به گذشته با should have + قسمت سوم: You should have recycled it.",
  "b2.interview.lesson-01":
    "پاسخ تجربه را با ساختار STAR بده (موقعیت، کار، نتیجه) و نقل‌قول غیرمستقیم را با He said that… گزارش کن.",
  "b2.media.lesson-01":
    "used to برای عادتِ گذشته است و would فقط برای تکرارهای عادی گذشته — هر دو گذشته را نشان می‌دهند نه حال را.",
  "c1.academic.lesson-01":
    "نوشتن رسمی از مجهول بهره می‌برد: It is argued that… — و اتصال‌دهنده‌های منطقی مثل however، therefore، furthermore ساختار را نگه می‌دارند.",
  "c1.persuasion.lesson-01":
    "تأکید بلاغی با وارونه‌سازی: Had I known… / Not only did he disagree… — که جمله را رسمی‌تر و نیرومندتر می‌کند.",
  "c1.culture.lesson-01":
    "وصاف رویدادها با گذشته استمراری: I was watching the film when… — و تفاوت would با usedTo در روایت.",
  "c1.phrasal-idioms.lesson-01":
    "فعل‌های عبارتی قابل ترجمه‌ی لفظی نیستند: give up یعنی دست کشیدن — حرف اضافه معنی را عوض می‌کند.",
  "c2.nuance.lesson-01":
    "هر هم‌معنای بار خودش را دارد: tiny کوچک‌تر از small است، rather یعنی تا حدی و hardly یعنی به‌ندرت — انتخاب واژه = انتخاب دقت.",
  "c2.rhetoric.lesson-01":
    "کنایه با وارونه‌سازی ساخته می‌شود: Little did he know… — و تکرار هدفمند واژه‌ها ضرباهنگ سخن را می‌سازد.",
  "c2.science.lesson-01":
    "زبان علمی مجهول و دقیق است: The phenomenon was observed… — و تعریف با X refers to… / X is defined as…",
  "c2.literature.lesson-01":
    "معنا از بافت می‌آید نه لفظ مستقیم: tone لحن راوی و irony کنایه است — همیشه بپرس گوینده چه می‌داند که خواننده نمی‌داند.",
};

const minutesByLevel = { A1: 5, A2: 6, B1: 7, B2: 8, C1: 9, C2: 10 };

function enrichLessons() {
  const data = read("lessons.json");
  let changed = 0;
  for (const lesson of data.lessons) {
    const tip = grammarTips[lesson.id];
    if (tip && lesson.grammarTipFa !== tip) {
      lesson.grammarTipFa = tip;
      changed++;
    }
    const minutes = minutesByLevel[lesson.level];
    if (minutes && lesson.estimatedMinutes !== minutes) {
      lesson.estimatedMinutes = minutes;
      changed++;
    }
  }
  data.contentVersion = BUNDLE_VERSION;
  write("lessons.json", data);
  console.log(`lessons.json: ${changed} lesson edits, contentVersion=${BUNDLE_VERSION}`);
}

// ------------------------------------------------------------- exercises

const acceptedExpansions = {
  "a1.greetings.ex.04": ["nice to meet you", "it's nice to meet you", "pleased to meet you"],
  "a2.restaurant.ex.04": ["this soup is delicious", "this soup is tasty"],
  "a2.health.ex.05": [
    "i went to the doctor yesterday",
    "i went to the doctors yesterday",
    "yesterday i went to the doctor",
  ],
  "a1.shopping.ex.05": [
    "how much is this bag",
    "how much does this bag cost",
    "what's the price of this bag",
  ],
  "b1.work.ex.05": [
    "i left this company three years ago",
    "i left the company three years ago",
    "i quit this company three years ago",
  ],
};

const wordBanks = {
  "a1.greetings.ex.04": ["nice", "to", "meet", "you", "see", "glad", "hello"],
  "a1.daily-routine.ex.04": [
    "i", "usually", "wake", "up", "in", "the", "morning", "at", "early", "never",
  ],
  "a1.family.ex.04": ["i", "have", "one", "brother", "two", "sister", "a", "the"],
  "a1.shopping.ex.05": [
    "how", "much", "is", "this", "bag", "what", "the", "price", "of", "cost", "does",
  ],
};

// One vocabulary MC per lesson — guaranteed to practise the lesson's own words.
function vocabMc(lesson, lessonVocab, allVocab, lessonIndex) {
  const target = lessonVocab[0];
  const distractors = [];
  let cursor = (lessonIndex * 6 + 23) % allVocab.length;
  while (distractors.length < 3) {
    const candidate = allVocab[cursor % allVocab.length];
    cursor += 7;
    if (candidate.lessonId === lesson.id) continue;
    if (candidate.translation === target.translation) continue;
    if (distractors.includes(candidate.translation)) continue;
    distractors.push(candidate.translation);
  }
  const prefix = lesson.id.replace(/\.lesson-\d+$/, "");
  return {
    id: `${prefix}.ex.07`,
    lessonId: lesson.id,
    type: "multiple_choice",
    order: 7,
    question: `What does "${target.word}" mean?`,
    questionFa: `معنی «${target.word}» چیست؟`,
    options: [target.translation, ...distractors],
    correctIndex: 0,
    skill: "VOCABULARY",
  };
}

// Hand-authored READING comprehension per level (first lesson of each level).
const readingMc = [
  {
    lessonId: "a1.greetings.lesson-01",
    id: "a1.greetings.ex.08",
    question: 'Read: "Nice to meet you." What does it mean?',
    questionFa: "این جمله چه معنی‌ای دارد؟",
    options: [
      "از آشنایی شما خوشوقتم",
      "خداحافظ، شب بخیر",
      "لطفاً آهسته صحبت کنید",
      "ممنون، کمکم می‌کنید",
    ],
    explanationFa: "جمله‌ای برای شروع آشنایی — معنی از کل جمله به‌دست می‌آید، نه از تک‌تک واژه‌ها.",
  },
  {
    lessonId: "a2.airport.lesson-01",
    id: "a2.airport.ex.08",
    question: 'Read: "Please proceed to gate twelve." What should you do?',
    questionFa: "با دیدن این جمله در فرودگاه چه باید بکنید؟",
    options: [
      "به سمت دروازهٔ پرواز بروید",
      "چمدان خود را تحویل دهید",
      "بلیت را پاره کنید",
      "در صف گذرنامه بمانید",
    ],
    explanationFa: "proceed یعنی «ادامه بده، برو جلو» — gate هم محل سوار شدن به هواپیماست.",
  },
  {
    lessonId: "b1.work.lesson-01",
    id: "b1.work.ex.08",
    question: 'Read: "The position requires five years of experience." What is true?',
    questionFa: "کدام گزینه درست است؟",
    options: [
      "این موقعیت شغلی پنج سال تجربه می‌خواهد",
      "این شرکت پنج سال قدمت دارد",
      "پنج نفر استخدام می‌شوند",
      "حقوق بعد از پنج سال اضافه می‌شود",
    ],
    explanationFa: "requires یعنی «لازم دارد» — فاعل جمله خود موقعیت شغلی است، نه شرکت.",
  },
  {
    lessonId: "b2.technology.lesson-01",
    id: "b2.technology.ex.08",
    question: 'Read: "The app drains the battery within hours." What happens?',
    questionFa: "چه اتفاقی می‌افتد؟",
    options: [
      "برنامه باتری را در عرض چند ساعت خالی می‌کند",
      "برنامه باتری را شارژ می‌کند",
      "برنامه بعد از چند ساعت بسته می‌شود",
      "برنامه از اینترنت زیادی استفاده می‌کند",
    ],
    explanationFa: "drain از ریشهٔ «تخلیه کردن» است — subject فعال این کار، خود برنامه است.",
  },
  {
    lessonId: "c1.academic.lesson-01",
    id: "c1.academic.ex.08",
    question: 'Read: "The findings challenge the prevailing assumption." What is claimed?',
    questionFa: "چه ادعایی مطرح شده است؟",
    options: [
      "یافته‌ها فرض رایج را به چالش می‌کشند",
      "یافته‌ها فرض رایج را تأیید می‌کنند",
      "یافته‌ها هنوز منتشر نشده‌اند",
      "فرض رایج بر یافته‌ها مقدم است",
    ],
    explanationFa: "prevailing یعنی «حاکم، رایج» و challenge در نوشتار آکادمیک یعنی «به چالش کشیدن»، نه دعوت به مبارزه.",
  },
  {
    lessonId: "c2.literature.lesson-01",
    id: "c2.literature.ex.08",
    question: 'Read: "The narrator\'s tone belies the tale\'s optimism." What is meant?',
    questionFa: "منظور از این جمله چیست؟",
    options: [
      "لحن راوی با خوش‌بینی داستان در تضاد است",
      "راوی داستان را باور دارد",
      "داستان کاملاً بدبینانه است",
      "راوی از نتیجهٔ داستان آگاه است",
    ],
    explanationFa: "belies یعنی «نقض می‌کند، خلافِ چیزی را نشان می‌دهد» — کلید در فعل است، نه در واژه‌های مجاور.",
  },
];

function enrichExercises() {
  const data = read("exercises.json");
  const lessons = read("lessons.json").lessons;
  const vocab = read("vocabulary.json").vocabulary;
  const byLesson = {};
  vocab.forEach((v) => {
    (byLesson[v.lessonId] = byLesson[v.lessonId] || []).push(v);
  });

  let edits = 0;
  for (const exercise of data.exercises) {
    const accepted = acceptedExpansions[exercise.id];
    if (accepted && JSON.stringify(exercise.accepted) !== JSON.stringify(accepted)) {
      exercise.accepted = accepted;
      edits++;
    }
    const bank = wordBanks[exercise.id];
    if (bank && JSON.stringify(exercise.bank) !== JSON.stringify(bank)) {
      exercise.bank = bank;
      edits++;
    }
  }

  const ids = new Set(data.exercises.map((e) => e.id));
  let added = 0;
  lessons.forEach((lesson, index) => {
    const lessonVocab = byLesson[lesson.id] || [];
    const mcId = lesson.id.replace(/\.lesson-\d+$/, "") + ".ex.07";
    if (lessonVocab.length > 0 && !ids.has(mcId)) {
      data.exercises.push(vocabMc(lesson, lessonVocab, vocab, index));
      ids.add(mcId);
      added++;
    }
  });
  for (const reading of readingMc) {
    if (ids.has(reading.id)) continue;
    data.exercises.push({
      id: reading.id,
      lessonId: reading.lessonId,
      type: "multiple_choice",
      order: 8,
      question: reading.question,
      questionFa: reading.questionFa,
      options: reading.options,
      correctIndex: 0,
      skill: "READING",
      explanationFa: reading.explanationFa,
    });
    ids.add(reading.id);
    added++;
  }

  data.contentVersion = BUNDLE_VERSION;
  write("exercises.json", data);
  console.log(`exercises.json: ${edits} edits, ${added} added, total=${data.exercises.length}`);
}

function bumpOthers() {
  for (const name of ["vocabulary.json", "placement.json"]) {
    const data = read(name);
    data.contentVersion = BUNDLE_VERSION;
    write(name, data);
  }
  console.log(`vocabulary.json + placement.json contentVersion=${BUNDLE_VERSION}`);
}

enrichLessons();
enrichExercises();
bumpOthers();
