// P2 — sentence production and no-option word recall.
//
// WHY: every vocabulary item in the bundle up to now was multiple choice, so the
// mastery model could only ever observe RECOGNITION, and the only exercise that
// demanded a whole sentence of the learner were `translation` items (21 of which
// even shipped a word bank). The P2 checklist asks for two new shapes:
//
//   * `word_recall`         — name the English word from its Persian meaning, no
//                             options anywhere, so a right answer is retrieval
//                             (MasteryDimension.RECALL) rather than recognition.
//   * `sentence_production` — build the whole sentence yourself from a Persian
//                             cue; graded on the target structure via `mustUse`
//                             anchors + a `minWords` floor, so an original correct
//                             sentence is not failed for not matching one wording
//                             (MasteryDimension.PRODUCTION, the typed counterpart
//                             of the suspended speaking item).
//
// One shot: run once, then never again (AGENTS.md A-12) — the JSON is the source
// of truth and re-running would overwrite later hand edits.
//
//   node scripts/archive/p2_production_recall.mjs
//
// Bundle 20 → 21. 12 word_recall + 12 sentence_production, two of each per level,
// appended to lessons that already teach the word or the structure.

import { readFileSync, writeFileSync } from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const NEW_VERSION = 21;

const read = (name) => JSON.parse(readFileSync(`${DIR}/${name}.json`, "utf8"));
const write = (name, obj) => writeFileSync(`${DIR}/${name}.json`, JSON.stringify(obj, null, 2) + "\n", "utf8");

// --- the authored items -----------------------------------------------------
// `order` and `id` are derived from the lesson below, never hand-written: an id that
// does not match the lesson's existing prefix would not be parsed into the lesson,
// and a duplicate order would fail `validateContent`.

const wordRecalls = [
  {
    lesson: "a1.family.lesson-01",
    word: "mother",
    prompt: "مادر",
    accepted: ["mother"],
    explanationFa: "mother یعنی «مادر»؛ father پدر و sister خواهر است.",
  },
  {
    lesson: "a1.family.lesson-01",
    word: "father",
    prompt: "پدر",
    accepted: ["father"],
    explanationFa: "father یعنی «پدر»؛ mother مادر و brother برادر است.",
  },
  {
    lesson: "a2.health.lesson-01",
    word: "headache",
    prompt: "سردرد",
    accepted: ["headache", "headaches"],
    explanationFa: "headache از head و ache ساخته شده: درد سر.",
  },
  {
    lesson: "a2.health.lesson-01",
    word: "fever",
    prompt: "تب",
    accepted: ["fever"],
    explanationFa: "fever یعنی «تب»؛ با temperature که «دما» است اشتباه نشود.",
  },
  {
    lesson: "b1.work.lesson-01",
    word: "colleague",
    prompt: "همکار",
    accepted: ["colleague", "colleagues"],
    explanationFa: "colleague یعنی «همکار»؛ coworker هم رایج است اما در درس colleague آمده.",
  },
  {
    lesson: "b1.work.lesson-01",
    word: "deadline",
    prompt: "مهلت",
    accepted: ["deadline", "deadlines"],
    explanationFa: "deadline یعنی آخرین زمانی که کار باید تحویل شود.",
  },
  {
    lesson: "b2.problems.lesson-01",
    word: "refund",
    prompt: "بازپرداخت",
    accepted: ["refund", "refunds"],
    explanationFa: "refund پولی است که برای کالا یا خدمات مرجوعی برمی‌گردانند.",
  },
  {
    lesson: "b2.problems.lesson-01",
    word: "complaint",
    prompt: "شکایت",
    accepted: ["complaint", "complaints"],
    explanationFa: "complaint اسم است؛ شکل فعلی آن complain است.",
  },
  {
    lesson: "c1.persuasion.lesson-01",
    word: "persuasive",
    prompt: "قانع‌کننده",
    accepted: ["persuasive"],
    explanationFa: "persuasive صفت است؛ persuade فعل و persuasion اسم آن است.",
  },
  {
    lesson: "c1.persuasion.lesson-01",
    word: "counterargument",
    prompt: "استدلال متقابل",
    accepted: ["counterargument", "counter argument", "counter-argument"],
    explanationFa: "counterargument یعنی استدلالی که در برابر ادعای اصلی مطرح می‌شود.",
  },
  {
    lesson: "c2.argumentation.lesson-01",
    word: "fallacy",
    prompt: "مغالطه",
    accepted: ["fallacy", "fallacies"],
    explanationFa: "fallacy استدلالی نادرست است که درست به نظر می‌رسد.",
  },
  {
    lesson: "c2.argumentation.lesson-01",
    word: "premise",
    prompt: "مقدمه (منطقی)",
    accepted: ["premise", "premises"],
    explanationFa: "premise گزارهٔ پایه‌ای است که نتیجه بر آن بنا می‌شود.",
  },
];

const sentenceProductions = [
  {
    lesson: "a1.daily-routine.lesson-01",
    prompt: "من معمولاً ساعت هفت صبحانه می‌خورم.",
    mustUse: ["usually", "breakfast"],
    minWords: 5,
    accepted: ["i usually have breakfast at seven", "i usually eat breakfast at seven o'clock"],
    hintFa: "قید تکرار (usually) پیش از فعل اصلی می‌آید.",
    skill: "GRAMMAR",
    explanationFa: "قیدهای تکرار مثل usually پیش از فعل اصلی می‌آیند، نه پیش از فاعل.",
  },
  {
    lesson: "a1.daily-routine.lesson-01",
    prompt: "او هر روز به سر کار می‌رود.",
    mustUse: ["goes", "work"],
    minWords: 5,
    accepted: ["she goes to work every day", "he goes to work every day"],
    hintFa: "سوم‌شخص مفرد: فعل -s می‌گیرد.",
    skill: "GRAMMAR",
    explanationFa: "در سوم‌شخص مفرد فعل -s می‌گیرد؛ go به goes تبدیل می‌شود.",
  },
  {
    lesson: "a2.past-simple.lesson-01",
    prompt: "دیروز یک کتاب خریدم.",
    mustUse: ["bought", "yesterday"],
    minWords: 4,
    accepted: ["i bought a book yesterday"],
    hintFa: "زمان گذشته: buy → bought.",
    skill: "GRAMMAR",
    explanationFa: "buy فعل بی‌قاعده است و گذشتهٔ آن bought می‌شود، نه buyed.",
  },
  {
    lesson: "a2.past-simple.lesson-01",
    prompt: "او دیروز به مدرسه نرفت.",
    mustUse: ["didn't", "go"],
    minWords: 4,
    accepted: ["she didn't go to school yesterday", "he didn't go to school yesterday"],
    hintFa: "منفی گذشته: didn't + شکل سادهٔ فعل.",
    skill: "GRAMMAR",
    explanationFa: "بعد از didn't فعل به شکل ساده می‌آید؛ went در این جمله نادرست است.",
  },
  {
    lesson: "b1.plans.lesson-01",
    prompt: "اگر فردا باران بیاید، در خانه می‌مانیم.",
    mustUse: ["if", "will"],
    minWords: 7,
    accepted: ["if it rains tomorrow we will stay at home"],
    hintFa: "شرطی نوع اول: if + حال ساده، و در جملهٔ اصلی will.",
    skill: "GRAMMAR",
    explanationFa: "در شرطی نوع اول پس از if حال ساده می‌آید و نتیجه با will ساخته می‌شود.",
  },
  {
    lesson: "b1.plans.lesson-01",
    prompt: "اگر من جای تو بودم، آن پیشنهاد را قبول می‌کردم.",
    mustUse: ["if", "would"],
    minWords: 7,
    accepted: ["if i were you i would accept that offer", "if i were you i would take that job"],
    hintFa: "شرطی نوع دوم: if + گذشتهٔ ساده، would + مصدر.",
    skill: "GRAMMAR",
    explanationFa: "شرطی نوع دوم موقعیت خیالی را می‌رساند: if + گذشتهٔ ساده و would در جملهٔ اصلی.",
  },
  {
    lesson: "b2.environment.lesson-01",
    prompt: "او ممکن است الان در دفتر باشد.",
    mustUse: ["may", "be"],
    minWords: 5,
    accepted: ["he may be in the office now", "she may be in the office now"],
    hintFa: "استنباط با may: بعد از آن شکل سادهٔ فعل می‌آید.",
    skill: "GRAMMAR",
    explanationFa: "برای استنباط احتمالی از may + شکل سادهٔ فعل استفاده می‌کنیم: may be.",
  },
  {
    lesson: "b2.environment.lesson-01",
    prompt: "آلودگی هوا در شهرهای بزرگ افزایش یافته است.",
    mustUse: ["has", "increased"],
    minWords: 6,
    accepted: ["air pollution has increased in large cities", "air pollution has increased in big cities"],
    hintFa: "حال کامل: has + قسمت سوم فعل.",
    skill: "GRAMMAR",
    explanationFa: "در حال کامل پس از has قسمت سوم فعل می‌آید: increased.",
  },
  {
    lesson: "c1.academic.lesson-01",
    prompt: "او نه تنها دیر آمد، بلکه مدارک را هم فراموش کرد.",
    mustUse: ["not only", "did"],
    minWords: 8,
    accepted: ["not only did he arrive late but he also forgot the documents"],
    hintFa: "وارونگی با not only در آغاز جمله: did + فاعل + فعل.",
    skill: "GRAMMAR",
    explanationFa: "قرار گرفتن not only در آغاز جمله وارونگی می‌سازد: did he arrive.",
  },
  {
    lesson: "c1.academic.lesson-01",
    prompt: "شواهد جدید این فرضیه را تأیید می‌کند.",
    // The anchor is the third-person form the exemplars actually use. Anchoring the
    // base form here would make the item's own answers uncapable of satisfying its own
    // anchors — the exact mistake ProductionRecallContentTest exists to catch.
    mustUse: ["evidence", "corroborates"],
    minWords: 6,
    accepted: ["the new evidence corroborates the hypothesis", "recent evidence corroborates the hypothesis"],
    hintFa: "corroborate یعنی با شواهد تأیید کردن؛ با فاعل سوم‌شخص -s می‌گیرد.",
    skill: "WRITING",
    explanationFa: "corroborate یعنی چیزی را با شواهد پشتیبانی و تأیید کردن.",
  },
  {
    lesson: "c2.rhetoric.lesson-01",
    prompt: "نه تنها استدلالش ضعیف بود، بلکه مقدماتش هم نادرست بود.",
    mustUse: ["not only", "but"],
    minWords: 8,
    accepted: ["not only was his argument weak but his premises were also flawed"],
    hintFa: "وارونگی تأکیدی با not only … but.",
    skill: "GRAMMAR",
    explanationFa: "وارونگی پس از not only در آغاز جمله، لحن را تأکیدی و رسمی می‌کند.",
  },
  {
    lesson: "c2.rhetoric.lesson-01",
    prompt: "اگر او آن موقع گوش داده بود، الان اینجا نبودیم.",
    mustUse: ["if", "would"],
    minWords: 8,
    accepted: ["if she had listened we would not be here now", "if he had listened we would not be here now"],
    hintFa: "شرطی آمیخته: if + گذشتهٔ کامل، و نتیجه در زمان حال با would.",
    skill: "GRAMMAR",
    explanationFa: "در شرطی آمیخته شرط در گذشته (had listened) و نتیجه در حال (would not be) است.",
  },
];

// --- build ------------------------------------------------------------------

const exercisesFile = read("exercises");
const lessonsById = new Map(read("lessons").lessons.map((l) => [l.id, l]));
const exercises = exercisesFile.exercises;
const existingIds = new Set(exercises.map((e) => e.id));

/** Next `order` in a lesson, and the id prefix its existing items use. */
function nextSlot(lessonId) {
  const own = exercises.filter((e) => e.lessonId === lessonId);
  if (own.length === 0) throw new Error(`no exercises in ${lessonId} — wrong lesson id?`);
  const maxOrder = Math.max(...own.map((e) => e.order || 0));
  const prefix = own[0].id.slice(0, own[0].id.indexOf(".ex."));
  return { maxOrder, prefix, count: own.length };
}

const counters = new Map();
function slotFor(lessonId) {
  if (!counters.has(lessonId)) {
    const { maxOrder, prefix } = nextSlot(lessonId);
    counters.set(lessonId, { order: maxOrder, prefix });
  }
  const slot = counters.get(lessonId);
  slot.order += 1;
  return { order: slot.order, prefix: slot.prefix };
}

const added = [];

for (const item of wordRecalls) {
  if (!lessonsById.has(item.lesson)) throw new Error(`unknown lesson ${item.lesson}`);
  const { order, prefix } = slotFor(item.lesson);
  const id = `${prefix}.ex.${String(order).padStart(2, "0")}`;
  if (existingIds.has(id)) throw new Error(`duplicate id ${id}`);
  existingIds.add(id);
  added.push({
    id,
    lessonId: item.lesson,
    type: "word_recall",
    order,
    prompt: item.prompt,
    accepted: item.accepted,
    skill: "VOCABULARY",
    explanationFa: item.explanationFa,
  });
}

for (const item of sentenceProductions) {
  if (!lessonsById.has(item.lesson)) throw new Error(`unknown lesson ${item.lesson}`);
  const { order, prefix } = slotFor(item.lesson);
  const id = `${prefix}.ex.${String(order).padStart(2, "0")}`;
  if (existingIds.has(id)) throw new Error(`duplicate id ${id}`);
  existingIds.add(id);
  const anchorWords = item.mustUse.reduce((n, a) => n + a.trim().split(/\s+/).length, 0);
  if (item.minWords < anchorWords) {
    throw new Error(`${id}: minWords ${item.minWords} < anchor words ${anchorWords}`);
  }
  added.push({
    id,
    lessonId: item.lesson,
    type: "sentence_production",
    order,
    prompt: item.prompt,
    mustUse: item.mustUse,
    minWords: item.minWords,
    accepted: item.accepted,
    hintFa: item.hintFa,
    skill: item.skill,
    explanationFa: item.explanationFa,
  });
}

exercises.push(...added);
exercisesFile.exercises = exercises;

// Every bundle file declares the same version (validateContent + ContentDistributionTest).
for (const name of FILES) {
  const root = read(name);
  root.contentVersion = NEW_VERSION;
  if (name === "exercises") root.exercises = exercises;
  write(name, root);
}

const recall = added.filter((e) => e.type === "word_recall").length;
const production = added.filter((e) => e.type === "sentence_production").length;
console.log(
  `bundle ${NEW_VERSION}: +${added.length} exercises (${recall} word_recall, ${production} sentence_production), ` +
    `total ${exercises.length}`,
);
