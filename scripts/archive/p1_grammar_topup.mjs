// Puts 13 grammar topics back above the six-exercise practice bar (A-8) after the
// A-2b blank tagging made the count honest.
//
//   node scripts/p1_grammar_topup.mjs
//
// Until now an untagged fill-in-the-blank counted as GRAMMAR practice, so some
// topics only cleared the bar because of that default (and two of them were
// word-choice blanks that should never have counted). Tagging them correctly left
// 13 topics at 4–5 exercises, so this batch adds real form-focused practice
// instead of letting a fallback define the bar.
//
// Appends one grammar multiple-choice item per lesson (two where two topics were
// short), newest order values, and bumps contentVersion 16 → 17.

import { readFileSync, writeFileSync } from "node:fs";

const dir = "app/src/main/assets/content";
const read = (f) => JSON.parse(readFileSync(`${dir}/${f}`, "utf8"));
const write = (f, o) => writeFileSync(`${dir}/${f}`, JSON.stringify(o, null, 2) + "\n");

const additions = [
  {
    lessonId: "a1.daily-routine.lesson-01",
    items: [
      {
        question: "She ______ to work every day.",
        questionFa: "او هر روز به سر کار می‌رود.",
        options: ["goes", "go", "going", "gone"],
        correctIndex: 0,
        explanationFa:
          "با he/she/it فعل s می‌گیرد: She goes to work. شکل سادهٔ go فقط برای I/you/we/they درست است.",
      },
      {
        question: "They ______ breakfast at seven.",
        questionFa: "آن‌ها ساعت هفت صبحانه می‌خورند.",
        options: ["have", "has", "having", "haves"],
        correctIndex: 0,
        explanationFa:
          "با they شکل سادهٔ فعل می‌آید: They have breakfast. فقط he/she/it شکل has می‌گیرد.",
      },
    ],
  },
  {
    lessonId: "a1.shopping.lesson-01",
    items: [
      {
        question: "______ money do you have?",
        questionFa: "چقدر پول داری؟",
        options: ["How much", "How many", "How long", "How often"],
        correctIndex: 0,
        explanationFa:
          "money غیرقابل‌شمارش است، پس How much می‌آید؛ How many فقط با اسم قابل‌شمارش جمع می‌آید (How many books).",
      },
    ],
  },
  {
    lessonId: "a2.restaurant.lesson-01",
    items: [
      {
        question: "I would like ______ coffee, please.",
        questionFa: "لطفاً یک قهوه می‌خواهم.",
        options: ["some", "any", "a", "many"],
        correctIndex: 0,
        explanationFa:
          "در جملهٔ مثبت و درخواست مؤدبانه از some استفاده می‌کنیم؛ any در جملهٔ منفی و پرسشی می‌آید و coffee غیرقابل‌شمارش است.",
      },
    ],
  },
  {
    lessonId: "a2.health.lesson-01",
    items: [
      {
        question: "You ______ stay in bed if you feel dizzy.",
        questionFa: "اگر سرگیجه داری، بهتر است در رختخواب بمانی.",
        options: ["should", "should to", "are should", "shoulding"],
        correctIndex: 0,
        explanationFa:
          "برای توصیه از should + فعل ساده استفاده می‌کنیم: You should stay in bed. after a modal the verb never takes to or -ing.",
      },
      {
        question: "I ______ to take this medicine twice a day.",
        questionFa: "باید این دارو را روزی دو بار بخورم.",
        options: ["have", "has", "am", "do"],
        correctIndex: 0,
        explanationFa:
          "برای اجبار از have to استفاده می‌کنیم و با I شکل have می‌آید: I have to take it. شکل has فقط برای he/she/it است.",
      },
    ],
  },
  {
    lessonId: "b1.plans.lesson-01",
    items: [
      {
        question: "She ______ going to start a new course next month.",
        questionFa: "او ماه آینده قرار است دورهٔ جدیدی شروع کند.",
        options: ["is", "are", "be", "were"],
        correctIndex: 0,
        explanationFa:
          "در ساختار going to فعل be با نهاد هم‌آهنگ می‌شود: She is going to start. با she شکل are یا were غلط است.",
      },
    ],
  },
  {
    lessonId: "b2.environment.lesson-01",
    items: [
      {
        question: "The temperature ______ rise if emissions continue.",
        questionFa: "اگر انتشار گازها ادامه یابد، دما ممکن است بالا برود.",
        options: ["might", "mights", "might to", "is might"],
        correctIndex: 0,
        explanationFa:
          "برای احتمال از فعل وجهی might + فعل ساده استفاده می‌کنیم: might rise. فعل وجهی s و to نمی‌گیرد.",
      },
    ],
  },
  {
    lessonId: "b2.interview.lesson-01",
    items: [
      {
        question: "He asked me ______ I had any questions.",
        questionFa: "او از من پرسید که آیا سؤالی دارم.",
        options: ["whether", "that", "what", "if that"],
        correctIndex: 0,
        explanationFa:
          "در نقل قول یک پرسش بله/خیر از whether (یا if) استفاده می‌کنیم و جمله به ترتیب خبری می‌آید: whether I had any questions.",
      },
    ],
  },
  {
    lessonId: "b2.media.lesson-01",
    items: [
      {
        question: "She ______ have long hair when she was a child.",
        questionFa: "او در کودکی موهای بلند داشت.",
        options: ["used to", "use to", "uses to", "is used to"],
        correctIndex: 0,
        explanationFa:
          "برای عادت گذشته از used to + فعل ساده استفاده می‌کنیم: She used to have long hair. is used to معنی «عادت داشتن به چیزی» را می‌دهد، نه عادت گذشته.",
      },
    ],
  },
];

const exercises = read("exercises.json");
if (exercises.exercises.some((x) => x.id === "a1.daily-routine.ex.12")) {
  console.error("grammar top-up already applied — nothing to do");
  process.exit(1);
}

// The A-9 batch already added `ex.10`/`ex.11` to some of these lessons, so the id
// index is allocated from what is actually there, not from a hardcoded guess.
const maxOrder = new Map();
const maxIndex = new Map();
for (const row of exercises.exercises) {
  maxOrder.set(row.lessonId, Math.max(maxOrder.get(row.lessonId) ?? 0, row.order ?? 0));
  const suffix = Number(row.id.split(".ex.")[1]);
  if (Number.isFinite(suffix)) {
    maxIndex.set(row.lessonId, Math.max(maxIndex.get(row.lessonId) ?? 0, suffix));
  }
}

const added = [];
for (const { lessonId, items } of additions) {
  let order = maxOrder.get(lessonId) ?? 0;
  let index = maxIndex.get(lessonId) ?? 0;
  const prefix = lessonId.replace(/\.lesson-\d+$/, "");
  for (const item of items) {
    order += 1;
    index += 1;
    added.push({
      id: `${prefix}.ex.${String(index).padStart(2, "0")}`,
      lessonId,
      type: "multiple_choice",
      order,
      question: item.question,
      questionFa: item.questionFa,
      options: item.options,
      correctIndex: item.correctIndex,
      skill: "GRAMMAR",
      explanationFa: item.explanationFa,
    });
  }
}

exercises.exercises = [...exercises.exercises, ...added];
write("exercises.json", exercises);

const from = read("lessons.json").contentVersion;
for (const file of ["lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json"]) {
  const o = read(file);
  o.contentVersion = from + 1;
  write(file, o);
}

console.log(`added ${added.length} grammar exercises across ${additions.length} lessons; contentVersion ${from} -> ${from + 1}`);
