// scripts/archive/p1_reading_and_meaning.mjs — A-5 + A-6 content batch (one-shot).
//
// A-5 (skill variety):
//   * adds a short reading passage (3–5 sentences) with three comprehension
//     questions to one lesson per CEFR level (A1→C2). Every question carries the
//     authored `passage` (the UI shows it above the question), so a re-queued
//     question is still answerable on its own;
//   * adds one English→Persian "choose the meaning" item per level, reusing a
//     sentence from that level's material.
//
// A-6 (placement honesty / extra skills):
//   * adds one reading-comprehension question to each CEFR band of placement.json,
//     so the per-skill report measures READING alongside grammar and vocabulary.
//     That grows each band from 5 to 6 questions → 6 bands × 6 = 36, matching
//     ScorePlacementUseCase.DEFAULT_BAND_SIZE = 6.
//
// Also bumps contentVersion 19 -> 20 in ALL FIVE content files (identically).
//
// NEVER re-run this script: it would overwrite later hand edits. See
// scripts/archive/README.md. The JSON files are the source of truth.
//
// Run from the repo root:  node scripts/archive/p1_reading_and_meaning.mjs

import fs from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const FROM = 19;
const TO = 20;

const read = (name) => JSON.parse(fs.readFileSync(`${DIR}/${name}.json`, "utf8"));
const write = (name, obj) => fs.writeFileSync(`${DIR}/${name}.json`, JSON.stringify(obj, null, 2) + "\n", "utf8");

// ---------------------------------------------------------------------------
// A-5 — reading passages and meaning items.
//
// `questions` are comprehension items sharing `passage`; `meaning` is the
// English→Persian item. Orders are relative to the lesson's current max order.
// ---------------------------------------------------------------------------
const READING_BLOCKS = [
  {
    lessonId: "a1.daily-routine.lesson-01",
    passage:
      "Hi, I am Sara. I wake up at seven every morning and have breakfast with my family. Then I go to school, and in the evening I do my homework. I go to bed at ten.",
    questions: [
      {
        question: "What time does Sara wake up?",
        questionFa: "سارا چه ساعتی بیدار می‌شود؟",
        options: ["At seven", "At six", "At nine", "At ten"],
        explanationFa: "زمان بیدار شدن در جملهٔ «I wake up at seven every morning» آمده است.",
      },
      {
        question: "Who does Sara have breakfast with?",
        questionFa: "سارا صبحانه را با چه کسی می‌خورد؟",
        options: ["Her family", "Her teacher", "Her friends", "Her doctor"],
        explanationFa: "متن می‌گوید «have breakfast with my family» — یعنی با خانواده‌اش.",
      },
      {
        question: "What does Sara do in the evening?",
        questionFa: "سارا عصرها چه کار می‌کند؟",
        options: ["She does her homework", "She goes to school", "She has breakfast", "She wakes up"],
        explanationFa: "جملهٔ «in the evening I do my homework» کار عصر او را می‌گوید.",
      },
    ],
    meaning: {
      sentence: "I wake up at seven every morning.",
      options: [
        "من هر صبح ساعت هفت بیدار می‌شوم.",
        "من هر شب ساعت هفت می‌خوابم.",
        "من هر صبح ساعت هفت درس می‌خوانم.",
        "من هر صبح ساعت هفت صبحانه می‌خورم.",
      ],
      explanationFa: "«wake up» یعنی «بیدار شدن»، نه خوابیدن؛ «every morning» = «هر صبح».",
    },
  },
  {
    lessonId: "a2.restaurant.lesson-01",
    passage:
      "Last Friday, Ali and his friends went to a new restaurant near the station. Ali ordered grilled chicken, and his friends chose pizza and salad. The food was delicious, but the service was slow, so they waited almost an hour.",
    questions: [
      {
        question: "Where did Ali and his friends go?",
        questionFa: "علی و دوستانش کجا رفتند؟",
        options: ["To a new restaurant", "To the station", "To a hotel", "To a market"],
        explanationFa: "متن می‌گوید «went to a new restaurant near the station» — رستورانی نزدیک ایستگاه.",
      },
      {
        question: "What did Ali order?",
        questionFa: "علی چه چیزی سفارش داد؟",
        options: ["Grilled chicken", "Pizza", "Salad", "Soup"],
        explanationFa: "«Ali ordered grilled chicken» — پیتزا و سالاد انتخاب دوستانش بود.",
      },
      {
        question: "Why did they wait a long time?",
        questionFa: "چرا مدت زیادی منتظر ماندند؟",
        options: ["The service was slow", "The food was cold", "They arrived late", "The restaurant was closed"],
        explanationFa: "«the service was slow» دلیل انتظار طولانی است.",
      },
    ],
    meaning: {
      sentence: "The food was delicious, but the service was slow.",
      options: [
        "غذا خوشمزه بود، اما سرویس کند بود.",
        "غذا سرد بود و سرویس هم بد بود.",
        "غذا گران بود ولی سریع آمد.",
        "غذا خوشمزه نبود اما سرویس خوب بود.",
      ],
      explanationFa: "«delicious» = خوشمزه و «slow» = کند؛ «but» تضاد این دو را نشان می‌دهد.",
    },
  },
  {
    lessonId: "b1.work.lesson-01",
    passage:
      "Mina started a new job last month. At first she was nervous, because the tasks were difficult and the software was new to her. Her manager gave her a week of training, and a colleague helped her every afternoon. By the end of the month, she could finish most tasks on her own.",
    questions: [
      {
        question: "Why was Mina nervous at first?",
        questionFa: "چرا مینا در ابتدا مضطرب بود؟",
        options: [
          "The tasks were difficult and the software was new",
          "She did not like her colleagues",
          "The office was very far away",
          "She had too much free time",
        ],
        explanationFa: "دلیل اضطراب صریحاً در متن آمده: کارها سخت بودند و نرم‌افزار برایش تازه بود.",
      },
      {
        question: "How did her colleague help her?",
        questionFa: "همکارش چگونه به او کمک کرد؟",
        options: [
          "By helping her every afternoon",
          "By giving her a week of training",
          "By doing her tasks for her",
          "By finding her a new job",
        ],
        explanationFa: "آموزش یک‌هفته‌ای کارِ مدیر بود؛ همکار «every afternoon» کمک می‌کرد.",
      },
      {
        question: "What could Mina do by the end of the month?",
        questionFa: "مینا تا پایان ماه چه کاری می‌توانست بکند؟",
        options: [
          "Finish most tasks on her own",
          "Change to a different team",
          "Become the manager",
          "Work without any software",
        ],
        explanationFa: "«she could finish most tasks on her own» یعنی بیشتر کارها را مستقل انجام می‌داد.",
      },
    ],
    meaning: {
      sentence: "Her manager gave her a week of training.",
      options: [
        "مدیرش یک هفته آموزش به او داد.",
        "او یک هفته به مدیرش آموزش داد.",
        "مدیرش یک هفته مرخصی به او داد.",
        "او یک هفته دیرتر شروع کرد.",
      ],
      explanationFa: "فاعلِ جمله «manager» است و مفعول «her»؛ پس مدیر به او آموزش داد.",
    },
  },
  {
    lessonId: "b2.environment.lesson-01",
    passage:
      "Many cities are trying to reduce air pollution by limiting the number of cars in the centre. Some have introduced low-emission zones, where drivers of older vehicles must pay a fee to enter. Supporters say the measure improves public health and encourages people to use buses and bicycles. Critics argue that it mostly affects drivers who cannot afford a newer car, and that public transport must improve first.",
    questions: [
      {
        question: "What are some cities doing to reduce air pollution?",
        questionFa: "برخی شهرها برای کاهش آلودگی هوا چه می‌کنند؟",
        options: [
          "Limiting cars in the centre",
          "Building more car parks",
          "Closing all bicycle lanes",
          "Encouraging older cars to enter",
        ],
        explanationFa: "متن می‌گوید تعداد خودروها در مرکز شهر محدود می‌شود.",
      },
      {
        question: "What must drivers of older vehicles do in a low-emission zone?",
        questionFa: "رانندگان خودروهای قدیمی در منطقهٔ کم‌آلاینده چه باید بکنند؟",
        options: ["Pay a fee to enter", "Leave their car at home", "Buy a bicycle", "Drive only at night"],
        explanationFa: "«drivers of older vehicles must pay a fee to enter» — پرداخت هزینه برای ورود.",
      },
      {
        question: "What do critics of the measure argue?",
        questionFa: "منتقدان این اقدام چه می‌گویند؟",
        options: [
          "Public transport must improve first",
          "Pollution is not a real problem",
          "Older cars are cleaner",
          "Buses cause the pollution",
        ],
        explanationFa: "منتقدان می‌گویند این اقدام بیشتر به رانندگان کم‌توان ضربه می‌زند و حمل‌ونقل عمومی باید اول بهتر شود.",
      },
    ],
    meaning: {
      sentence: "Supporters say the measure improves public health.",
      options: [
        "طرفداران می‌گویند این اقدام سلامت عمومی را بهبود می‌بخشد.",
        "منتقدان می‌گویند این اقدام سلامت عمومی را بدتر می‌کند.",
        "طرفداران می‌گویند این اقدام ترافیک را کم نمی‌کند.",
        "منتقدان می‌گویند این اقدام برای رانندگان ارزان‌تر است.",
      ],
      explanationFa: "«improves public health» = «سلامت عمومی را بهبود می‌بخشد».",
    },
  },
  {
    lessonId: "c1.academic.lesson-01",
    passage:
      "The study set out to examine whether short, regular breaks improve concentration during long periods of study. One group took a five-minute break every half hour, while the other worked without interruption. Although both groups reported similar levels of motivation, the first group made significantly fewer errors in the final test. The authors caution, however, that the sample was small and that the results may not apply to every type of task.",
    questions: [
      {
        question: "What did the study set out to examine?",
        questionFa: "پژوهش قصد داشت چه چیزی را بررسی کند؟",
        options: [
          "Whether short breaks improve concentration",
          "How long students can study without food",
          "Why motivation changes during the day",
          "Which subjects are the most difficult",
        ],
        explanationFa: "هدف پژوهش در جملهٔ اول صریحاً بیان شده است.",
      },
      {
        question: "What did the first group do?",
        questionFa: "گروه اول چه کاری انجام داد؟",
        options: [
          "Took a five-minute break every half hour",
          "Worked without any interruption",
          "Studied only at night",
          "Took a break once a day",
        ],
        explanationFa: "«One group took a five-minute break every half hour» رفتار گروه اول را می‌گوید.",
      },
      {
        question: "Why do the authors caution about the results?",
        questionFa: "چرا نویسندگان دربارهٔ نتایج احتیاط می‌کنند؟",
        options: ["The sample was small", "The students were not motivated", "The test was too easy", "The breaks were too long"],
        explanationFa: "آن‌ها هشدار می‌دهند که «the sample was small» — نمونه کوچک بوده است.",
      },
    ],
    meaning: {
      sentence: "The first group made significantly fewer errors in the final test.",
      options: [
        "گروه اول در آزمون نهایی به‌طور معناداری خطای کمتری داشت.",
        "گروه اول در آزمون نهایی خطای بیشتری داشت.",
        "گروه دوم در آزمون نهایی عملکرد بهتری داشت.",
        "هر دو گروه در آزمون نهایی یکسان بودند.",
      ],
      explanationFa: "«significantly fewer errors» = «خطای معناداری کمتر» برای گروه اول.",
    },
  },
  {
    lessonId: "c2.science.lesson-01",
    passage:
      "Far from being a passive storage system, memory appears to be reconstructive: each time we recall an event, we rebuild it rather than replay a fixed recording. This helps explain why two witnesses can sincerely remember the same scene differently. Sceptics point out that such findings are hard to replicate outside the laboratory, yet the reconstructive view has become the dominant framework in cognitive science.",
    questions: [
      {
        question: "How does the passage describe memory?",
        questionFa: "متن حافظه را چگونه توصیف می‌کند؟",
        options: [
          "As reconstructive rather than a fixed recording",
          "As a passive storage system",
          "As something that never changes",
          "As a laboratory instrument",
        ],
        explanationFa: "متن «reconstructive» بودن حافظه را در برابر «fixed recording» می‌گذارد.",
      },
      {
        question: "What does the reconstructive view help explain?",
        questionFa: "دیدگاه بازسازی‌کننده چه چیزی را توضیح می‌دهد؟",
        options: [
          "Why witnesses remember the same scene differently",
          "Why people forget their childhood",
          "Why memory improves with age",
          "Why laboratories are unreliable",
        ],
        explanationFa: "«two witnesses can sincerely remember the same scene differently» نتیجهٔ همین دیدگاه است.",
      },
      {
        question: "What do sceptics point out?",
        questionFa: "شک‌گرایان به چه نکته‌ای اشاره می‌کنند؟",
        options: [
          "The findings are hard to replicate outside the laboratory",
          "The reconstructive view is dominant",
          "Memory is a fixed recording",
          "Witnesses usually agree",
        ],
        explanationFa: "«such findings are hard to replicate outside the laboratory» ادعای شک‌گرایان است.",
      },
    ],
    meaning: {
      sentence: "Each time we recall an event, we rebuild it rather than replay a fixed recording.",
      options: [
        "هر بار که رویدادی را به یاد می‌آوریم، آن را بازسازی می‌کنیم نه اینکه یک ضبط ثابت را پخش کنیم.",
        "هر بار که رویدادی را فراموش می‌کنیم، آن را بازسازی می‌کنیم.",
        "حافظه مانند یک ضبط ثابت است که هرگز تغییر نمی‌کند.",
        "ما رویدادها را دقیقاً همان‌طور که بودند پخش می‌کنیم.",
      ],
      explanationFa: "«rather than» = «نه اینکه»؛ حافظه بازسازی می‌شود، نه بازپخش یک ضبط ثابت.",
    },
  },
];

// ---------------------------------------------------------------------------
// A-6 — one reading-comprehension question per CEFR band of placement.json.
// ---------------------------------------------------------------------------
const PLACEMENT_READING = [
  {
    level: "A1",
    passage:
      "Tom has a small dog. Every morning he takes it to the park near his house. The dog loves to run, and Tom throws a ball for it.",
    question: "Where does Tom take his dog every morning?",
    questionFa: "تام هر صبح سگش را کجا می‌برد؟",
    options: ["To the park", "To school", "To the market", "To the station"],
  },
  {
    level: "A2",
    passage:
      "Nina wanted to buy a gift for her mother. She went to the shop and chose a blue scarf. It cost twenty dollars, so she paid with her card.",
    question: "What did Nina buy?",
    questionFa: "نینا چه چیزی خرید؟",
    options: ["A blue scarf", "A red hat", "A book", "A pair of shoes"],
  },
  {
    level: "B1",
    passage:
      "The train was delayed because of heavy snow. Many passengers waited on the cold platform. The station staff brought hot tea and explained that the train would arrive in an hour.",
    question: "Why was the train delayed?",
    questionFa: "چرا قطار تأخیر داشت؟",
    options: ["Because of heavy snow", "Because of a strike", "Because of an accident", "Because of a broken engine"],
  },
  {
    level: "B2",
    passage:
      "A recent survey found that most employees prefer working from home two days a week. They said they save time on commuting and feel less stressed. However, some managers worry that teamwork becomes harder.",
    question: "What do most employees prefer?",
    questionFa: "بیشتر کارمندان چه چیزی را ترجیح می‌دهند؟",
    options: [
      "Working from home two days a week",
      "Working from home every day",
      "Working longer hours",
      "Working only in the office",
    ],
  },
  {
    level: "C1",
    passage:
      "Critics of the new policy argue that it places too much emphasis on test scores. They claim that schools may neglect creativity and critical thinking. Supporters respond that standardised results help identify struggling students early.",
    question: "What do critics of the new policy argue?",
    questionFa: "منتقدان سیاست جدید چه استدلالی دارند؟",
    options: [
      "Schools may neglect creativity and critical thinking",
      "Test scores are too low",
      "Policies should change every year",
      "Creativity is easy to measure",
    ],
  },
  {
    level: "C2",
    passage:
      "The author's argument rests on the assumption that readers share her understanding of irony. Where that assumption fails, the text can seem sincere, and its criticism is easily missed. A careful reader, she suggests, must attend to tone as much as to content.",
    question: "What assumption does the author's argument rest on?",
    questionFa: "استدلال نویسنده بر چه فرضی استوار است؟",
    options: [
      "That readers share her understanding of irony",
      "That irony is never used",
      "That readers ignore tone",
      "That the text is sincere",
    ],
  },
];

// ---------------------------------------------------------------------------

const exercises = read("exercises");
const placement = read("placement");

// Guard: never run twice.
const existingExerciseIds = new Set(exercises.exercises.map((e) => e.id));
const existingPlacementIds = new Set(placement.questions.map((q) => q.id));
for (const block of READING_BLOCKS) {
  const max = Math.max(...exercises.exercises.filter((e) => e.lessonId === block.lessonId).map((e) => e.order));
  const next = `${block.lessonId.replace(/\.lesson-\d+$/, "")}.ex.${String(max + 1).padStart(2, "0")}`;
  if (existingExerciseIds.has(next)) {
    throw new Error(`already applied: ${next} exists — do not re-run this batch`);
  }
}
if (placement.questions.some((q) => q.skill === "READING")) {
  throw new Error("placement already carries READING questions — do not re-run this batch");
}

// --- A-5: append reading + meaning items to their lessons -------------------
const added = [];
for (const block of READING_BLOCKS) {
  const prefix = block.lessonId.replace(/\.lesson-\d+$/, "");
  let order = Math.max(
    ...exercises.exercises.filter((e) => e.lessonId === block.lessonId).map((e) => e.order),
  );

  block.questions.forEach((q) => {
    order += 1;
    added.push({
      id: `${prefix}.ex.${String(order).padStart(2, "0")}`,
      lessonId: block.lessonId,
      type: "multiple_choice",
      order,
      passage: block.passage,
      question: q.question,
      questionFa: q.questionFa,
      options: q.options,
      correctIndex: 0,
      skill: "READING",
      explanationFa: q.explanationFa,
    });
  });

  order += 1;
  added.push({
    id: `${prefix}.ex.${String(order).padStart(2, "0")}`,
    lessonId: block.lessonId,
    type: "multiple_choice",
    order,
    passage: block.meaning.sentence,
    question: "What does this sentence mean?",
    questionFa: "این جمله چه معنایی دارد؟",
    options: block.meaning.options,
    correctIndex: 0,
    skill: "READING",
    explanationFa: block.meaning.explanationFa,
  });
}
exercises.exercises.push(...added);

// --- A-6: insert one reading question into each band of five ----------------
const withReading = [];
let band = 0;
placement.questions.forEach((q, i) => {
  withReading.push(q);
  if ((i + 1) % 5 === 0 && band < PLACEMENT_READING.length) {
    const r = PLACEMENT_READING[band];
    withReading.push({
      id: `placement.r${String(band + 1).padStart(2, "0")}`,
      type: "multiple_choice",
      level: r.level,
      passage: r.passage,
      question: r.question,
      questionFa: r.questionFa,
      options: r.options,
      correctIndex: 0,
      skill: "READING",
    });
    band += 1;
  }
});
if (band !== PLACEMENT_READING.length) {
  throw new Error(`expected ${PLACEMENT_READING.length} bands, inserted ${band}`);
}
placement.questions = withReading;

// --- bump the bundle version everywhere ------------------------------------
exercises.contentVersion = TO;
placement.contentVersion = TO;
for (const name of FILES) {
  if (name === "exercises" || name === "placement") continue;
  const obj = read(name);
  if (obj.contentVersion !== FROM) {
    throw new Error(`${name}.json: expected contentVersion ${FROM}, found ${obj.contentVersion}`);
  }
  obj.contentVersion = TO;
  write(name, obj);
}

write("exercises", exercises);
write("placement", placement);

console.log(
  `bundle ${FROM} -> ${TO}: +${added.length} exercises (reading + meaning), ` +
    `placement ${placement.questions.length} questions (${PLACEMENT_READING.length} bands × 6)`,
);
