// v24 — content quality pass: level fit, thin lessons, and the defects the
// audit turned up. Record of how the change was made; do not re-run it (it would
// overwrite later hand edits to the same rows).
//
// 1. Wrong/stale answers that misled the learner:
//    - b1.work.ex.03 accepted "visited" for "have never ___ to Turkey" (visited
//      never takes "to"); now been/gone, with the aspect difference explained.
//    - b2.interview.ex.02 offered "on" as a distractor, but "get on well with" is
//      just as correct as "get along well with".
//    - b1.plans.ex.03 accepted only "would" where "could" is equally valid.
//    - c2.nuance.ex.07 had two correct options (both glossed «مبهم»).
//    - c2.nuance.ex.04 accepted "slight evidence", an unnatural collocation.
//    - c2.rhetoric.ex.02 used "concierge" as a distractor in a word-form item.
//    - a2.airport.ex.06 offered the malformed option "departed the".
// 2. Level fit at A1: a1.can.ex.06 tested "could" (A2) inside the A1 can lesson.
// 3. Five words were taught twice (same SRS card twice, sometimes at two levels):
//    b1.plans.afford, b2.problems.deadline, b2.media.bias, c2.argumentation.refute.
//    Each is replaced by a level-appropriate word, and c2.argumentation.ex.03
//    moved with its word (rebut). The generated "What does X mean?" items that
//    used a replaced word as a distractor were re-glossed too: b1.plans.ex.07
//    (afford → arrange) and b2.media.ex.07 (bias → coverage).
// 5. Level fit in the placement bundle: the C2 band was testing B2 words
//    ("concise but rather vague", "heavy with irony"). placement.q16 and q17 now
//    target obfuscation and undermine-by-data, so the band actually separates C2.
//    That, the two re-glossed items, the ambiguous/vague distinction and the
//    thin-lesson additions were applied by this file; the placement edits were a
//    follow-up edit after its first run.
// 4. Thin lessons: the eight lessons under eight exercises get two more items.
import fs from "node:fs";
import path from "node:path";

const dir = path.resolve("app/src/main/assets/content");
const read = (name) => JSON.parse(fs.readFileSync(path.join(dir, name), "utf8"));
const write = (name, data) => fs.writeFileSync(path.join(dir, name), JSON.stringify(data, null, 2) + "\n");

const lessons = read("lessons.json");
const vocabulary = read("vocabulary.json");
const exercises = read("exercises.json");
const placement = read("placement.json");
const knowledge = read("knowledge.json");

const VERSION = 24;

const ex = (id) => {
  const found = exercises.exercises.find((e) => e.id === id);
  if (!found) throw new Error(`missing exercise ${id}`);
  return found;
};

// 1 ── answer defects -------------------------------------------------------
{
  const e = ex("b1.work.ex.03");
  e.accepted = ["been", "gone"];
  e.explanationFa =
    "با never و تجربهٔ زندگی حال کامل میآید: have never been to Turkey یعنی «تا حالا ترکیه نرفتهام» " +
    "(رفته و برگشتهام). have gone to یعنی «رفته و الان آنجاست»، پس اینجا been طبیعیتر است؛ " +
    "visited هم هرگز با to نمیآید.";
}

{
  const e = ex("b2.interview.ex.02");
  const i = e.options.indexOf("on");
  if (i === -1) throw new Error("b2.interview.ex.02: 'on' distractor not found");
  // "get on well with" is standard British English — a second correct answer.
  e.options[i] = "over";
}

{
  const e = ex("b1.plans.ex.03");
  e.accepted = ["would", "could"];
  e.explanationFa =
    "شرطی نوع دوم: در جملهٔ نتیجه would + فعل ساده میآید (would pass)؛ could هم درست است چون " +
    "توانایی فرضی را میرساند (میتوانست قبول شود).";
}

{
  const e = ex("c2.nuance.ex.07");
  const i = e.options.indexOf("مبهم، نامشخص");
  if (i === -1) throw new Error("c2.nuance.ex.07: duplicate gloss not found");
  // Two options glossed «مبهم» — the second one was a second correct answer.
  e.options[i] = "صریح و روشن";
}

{
  const e = ex("c2.nuance.ex.04");
  e.accepted = e.accepted.filter((a) => a !== "slight");
  e.explanationFa = "شاهد ناکافی برای ادعای قوی: thin / flimsy / weak evidence؛ «slight» با evidence همنشین طبیعی نیست.";
}

{
  const e = ex("c2.rhetoric.ex.02");
  const i = e.options.indexOf("concierge");
  if (i === -1) throw new Error("c2.rhetoric.ex.02: 'concierge' distractor not found");
  e.options[i] = "concession";
}

{
  const e = ex("a2.airport.ex.06");
  e.question = "The flight to Dubai ______ at 9 p.m. tomorrow.";
  e.questionFa = "پرواز دبی فردا ساعت ۹ شب حرکت میکند.";
  const i = e.options.indexOf("departed the");
  if (i === -1) throw new Error("a2.airport.ex.06: malformed option not found");
  e.options[i] = "departed";
  e.explanationFa =
    "برنامهٔ حرکت (timetable) با حال ساده بیان میشود و فاعل مفرد است: the flight departs. " +
    "departed گذشته است و با tomorrow نمیخواند.";
}

// 2 ── A1 level fit ---------------------------------------------------------
{
  const e = ex("a1.can.ex.06");
  // "could" is A2; the A1 ability lesson practises can/can't only.
  e.question = "______ you play the piano? — Yes, a little.";
  e.questionFa = "میتوانی پیانو بزنی؟ — بله، کمی.";
  e.options = ["Can", "Do", "Are", "Is"];
  e.correctIndex = 0;
  e.explanationFa =
    "توانایی را با can میپرسیم: Can you …? و پاسخ کوتاه Yes, I can. با Do/Are سؤال دربارهٔ " +
    "عادت یا حالت است، نه توانایی.";
}

// 3 ── duplicate vocabulary -------------------------------------------------
const replaceWord = (id, next) => {
  const i = vocabulary.vocabulary.findIndex((w) => w.id === id);
  if (i === -1) throw new Error(`missing word ${id}`);
  vocabulary.vocabulary[i] = { ...next, lessonId: vocabulary.vocabulary[i].lessonId };
};

replaceWord("b1.plans.word.afford", {
  id: "b1.plans.word.arrange",
  word: "arrange",
  translation: "ترتیب دادن، هماهنگ کردن",
  definition: "To plan or organize something",
  pronunciation: "/əˈreɪndʒ/",
  level: "B1",
  partOfSpeech: "verb",
  examples: [
    { en: "We arranged the meeting for Monday.", fa: "جلسه را برای دوشنبه هماهنگ کردیم." },
    { en: "She arranged a taxi to the airport.", fa: "او یک تاکسی تا فرودگاه هماهنگ کرد." },
  ],
  collocations: ["arrange a meeting", "arrange for someone to do"],
  explanationFa:
    "arrange یعنی «هماهنگ کردن و ترتیب دادن» — تصمیم گرفتن بههمراه مشخص کردن جزئیات. " +
    "با intend (قصد داشتن) و afford (توان مالی داشتن) اشتباه نشود.",
});

replaceWord("b2.problems.word.deadline", {
  id: "b2.problems.word.warranty",
  word: "warranty",
  translation: "ضمانت، گارانتی",
  definition: "A written promise to repair or replace a faulty product",
  pronunciation: "/ˈwɒrənti/",
  level: "B2",
  partOfSpeech: "noun",
  examples: [
    { en: "The phone is still under warranty.", fa: "این گوشی هنوز گارانتی دارد." },
    { en: "The warranty covers parts but not labour.", fa: "گارانتی قطعات را پوشش می‌دهد، اما هزینهٔ کار را نه." },
  ],
  collocations: ["under warranty", "warranty period"],
  explanationFa:
    "warranty یعنی ضمانتِ کالا و معمولاً با under warranty میآید؛ guarantee عمومیتر است و " +
    "برای هر وعدهٔ قطعی به کار میرود.",
});

replaceWord("b2.media.word.bias", {
  id: "b2.media.word.coverage",
  word: "coverage",
  translation: "پوشش خبری",
  definition: "The way newspapers, TV or radio report an event",
  pronunciation: "/ˈkʌvərɪdʒ/",
  level: "B2",
  partOfSpeech: "noun",
  examples: [
    { en: "The election received wide media coverage.", fa: "انتخابات پوشش خبری گسترده‌ای داشت." },
    { en: "Live coverage of the match starts at seven.", fa: "پوشش زندهٔ مسابقه ساعت هفت شروع می‌شود." },
  ],
  collocations: ["media coverage", "live coverage"],
  explanationFa:
    "coverage یعنی «پوشش خبری» یک رویداد؛ bias (سوگیری) دربارهٔ جهتگیری خبر است و در درس " +
    "پژوهش میآید. این دو با هم اشتباه نشوند.",
});

replaceWord("c2.argumentation.word.refute", {
  id: "c2.argumentation.word.rebut",
  word: "rebut",
  translation: "رد کردن با استدلال",
  definition: "To answer an argument by showing why it is wrong",
  pronunciation: "/rɪˈbʌt/",
  level: "C2",
  partOfSpeech: "verb",
  examples: [
    { en: "She rebutted every point in the report.", fa: "او به همهٔ نکات گزارش پاسخ داد و آنها را رد کرد." },
    { en: "The lawyer rebutted the accusation in court.", fa: "وکیل در دادگاه این اتهام را رد کرد." },
  ],
  collocations: ["rebut an argument", "rebut a claim"],
  explanationFa:
    "rebut یعنی «با استدلال رد کردن» — پاسخ دادن به یک استدلال و نشان دادن خطای آن؛ از deny " +
    "(انکار کردن) دقیقتر است و refute که در درس فن بیان آمده، بار اثباتی بیشتری دارد.",
});

{
  // The argument lesson's blank moved with its word.
  const e = ex("c2.argumentation.ex.03");
  e.accepted = ["rebut", "rebutted"];
  e.explanationFa =
    "rebut یعنی با استدلال رد کردن: the new data rebut the earlier explanation " +
    "(دادهٔ جمع با فعل بدون s).";
}

// 4 ── thin lessons ---------------------------------------------------------
const mc = (id, lessonId, order, question, questionFa, options, correctIndex, skill, explanationFa) => ({
  id,
  lessonId,
  type: "multiple_choice",
  order,
  question,
  questionFa,
  options,
  correctIndex,
  skill,
  explanationFa,
});

const blanks = (id, lessonId, order, sentence, accepted, skill, explanationFa) => ({
  id,
  lessonId,
  type: "fill_blank",
  order,
  sentence,
  accepted,
  skill,
  explanationFa,
});

const additions = [
  // C1 — register (6 → 8)
  mc(
    "c1.register.ex.07",
    "c1.register.lesson-01",
    7,
    "Which sentence belongs in an academic essay?",
    "کدام جمله به یک مقالهٔ دانشگاهی میآید؟",
    [
      "Kids these days just don't get it.",
      "It is generally accepted that the policy has failed.",
      "I reckon it's a disaster, honestly.",
      "Sounds bad, doesn't it?",
    ],
    1,
    "WRITING",
    "register یعنی سبکِ متناسب با موقعیت؛ در مقالهٔ دانشگاهی لحن غیرشخصی و رسمی است: " +
      "It is generally accepted that … . سه گزینهٔ دیگر محاورهایاند.",
  ),
  blanks(
    "c1.register.ex.08",
    "c1.register.lesson-01",
    8,
    "The results ______ suggest a link, although further work is needed.",
    ["may", "might"],
    "GRAMMAR",
    "hedging یا «تسویهٔ زبانی»: با may/might ادعا محتاطانه بیان میشود و جمله جای اصلاح دارد — " +
      "همان کاری که نویسندهٔ دانشگاهی میکند.",
  ),

  // C1 — cohesion (6 → 8)
  mc(
    "c1.cohesion.ex.07",
    "c1.cohesion.lesson-01",
    7,
    "The plan was rejected; ______, the committee agreed to review it.",
    "این طرح رد شد؛ با این حال، کمیته پذیرفت که آن را بازبینی کند.",
    ["therefore", "nevertheless", "moreover", "for instance"],
    1,
    "GRAMMAR",
    "nevertheless تضاد را نشان میدهد: با وجود رد شدن، بازبینی پذیرفته شد. therefore نتیجه " +
      "میسازد و moreover چیزی به همان جهت اضافه میکند.",
  ),
  mc(
    "c1.cohesion.ex.08",
    "c1.cohesion.lesson-01",
    8,
    "In \"Ana met Sara because she needed help\", the pronoun is a cohesion problem because ______.",
    "در «Ana met Sara because she needed help» ضمیر مشکل انسجامی دارد، چون ______.",
    [
      "the sentence has no subject",
      "it could refer to either woman",
      "because is a conjunction",
      "needed is in the past tense",
    ],
    1,
    "READING",
    "انسجام به مرجع روشنِ ضمیر تکیه دارد؛ وقتی «she» میتواند به هر دو زن برگردد خواننده " +
      "باید حدس بزند — راهحل تکرار نام یا بازنویسی جمله است.",
  ),

  // C1 — phrasal verbs and idioms (7 → 9)
  mc(
    "c1.phrasal-idioms.ex.08",
    "c1.phrasal-idioms.lesson-01",
    8,
    "The deal fell ______ at the last minute, so nobody signed.",
    "قرارداد در آخرین لحظه بینتیجه ماند، پس هیچکس امضا نکرد.",
    ["through", "off", "out", "over"],
    0,
    "VOCABULARY",
    "fall through یعنی «بینتیجه ماندن» (برای قرارداد و نقشه)؛ fell off یعنی از چیزی افتاد و " +
      "fell out یعنی با کسی قهر کرد.",
  ),
  mc(
    "c1.phrasal-idioms.ex.09",
    "c1.phrasal-idioms.lesson-01",
    9,
    "\"Run out of\" means ______.",
    "«run out of» یعنی ______.",
    [
      "to escape from a place",
      "to have no more of something",
      "to meet someone by chance",
      "to postpone something",
    ],
    1,
    "VOCABULARY",
    "run out of یعنی «تمام کردن ذخیره»: We ran out of milk. دیدنِ اتفاقی کسی come across " +
      "است و به تعویق انداختن put off.",
  ),

  // C1 — film and culture (7 → 9)
  mc(
    "c1.culture.ex.08",
    "c1.culture.lesson-01",
    8,
    "What does \"portrayal\" mean?",
    "معنی «portrayal» چیست؟",
    ["شیوهٔ به تصویر کشیدن", "زیرنویس", "کارگردان", "کلیشه"],
    0,
    "VOCABULARY",
    "portrayal از portray (به تصویر کشیدن) میآید: شیوهٔ نمایشِ یک شخصیت یا موقعیت در فیلم و داستان.",
  ),
  mc(
    "c1.culture.ex.09",
    "c1.culture.lesson-01",
    9,
    "Choose the correct sentence:",
    "جملهٔ درست را انتخاب کن:",
    [
      "I wish I would have known earlier.",
      "I wish I had known earlier.",
      "I wish I have known earlier.",
      "I wish I knew known earlier.",
    ],
    1,
    "GRAMMAR",
    "برای پشیمانی از گذشته wish + گذشتهٔ کامل است: I wish I had known. ترکیب would have بعد از " +
      "wish در انگلیسی معیار نادرست است.",
  ),

  // C2 — precision and nuance (7 → 9)
  mc(
    "c2.nuance.ex.08",
    "c2.nuance.lesson-01",
    8,
    "In political contexts the word carries a negative ______, although its dictionary meaning is neutral.",
    "در بافت سیاسی، واژه بار معنایی منفی دارد، هرچند معنای فرهنگنامهایاش خنثی است.",
    ["notation", "connotation", "collocation", "inflection"],
    1,
    "VOCABULARY",
    "connotation یعنی «بار معنایی ضمنی» در برابر denotation (معنای واژگانی). notation " +
      "نشانهگذاری است و collocation همنشینی واژهها.",
  ),
  blanks(
    "c2.nuance.ex.09",
    "c2.nuance.lesson-01",
    9,
    "Her tone was ______: it could be read as either sympathy or mockery.",
    ["equivocal", "ambiguous"],
    "VOCABULARY",
    "equivocal یعنی «دوپهلو و قابل تفسیر دوگانه» — همان ابهامی که نیمهٔ دوم جمله توضیحش میدهد.",
  ),

  // C2 — collocation (6 → 8)
  mc(
    "c2.collocation.ex.07",
    "c2.collocation.lesson-01",
    7,
    "The government ______ a ban on smoking in public places.",
    "دولت ممنوعیت استعمال دخانیات در مکانهای عمومی را اعمال کرد.",
    ["imposed", "did", "placed", "committed"],
    0,
    "VOCABULARY",
    "impose a ban / impose a tax / impose sanctions همنشینی ثابت انگلیسی است؛ make و do با " +
      "ban نمیآیند.",
  ),
  mc(
    "c2.collocation.ex.08",
    "c2.collocation.lesson-01",
    8,
    "The findings are at ______ with the earlier hypothesis.",
    "یافتهها با فرضیهٔ قبلی در تضادند.",
    ["odds", "conflict", "difference", "contrast"],
    0,
    "VOCABULARY",
    "at odds with یعنی «در تضاد با». با conflict و contrast حرف اضافه in است (in conflict " +
      "with) و at difference / at contrast اصلاً وجود ندارد.",
  ),

  // C2 — irony and subtext (6 → 8)
  mc(
    "c2.irony.ex.07",
    "c2.irony.lesson-01",
    7,
    "\"What a fantastic idea,\" she said, rolling her eyes. Her tone is ______.",
    "«چه ایدهٔ فوقالعادهای»، در حالی که چشمهایش را چرخاند. لحنش ______ است.",
    ["sincere", "sarcastic", "formal", "literal"],
    1,
    "READING",
    "sarcasm کنایهای است که ظاهرِ تعریف دارد و معنایش نکوهش؛ چرخاندن چشم نشانهٔ همین تضاد " +
      "میان ظاهر و مقصود است.",
  ),
  mc(
    "c2.irony.ex.08",
    "c2.irony.lesson-01",
    8,
    "Which sentence is an example of hyperbole?",
    "کدام جمله نمونهٔ اغراق (hyperbole) است؟",
    [
      "I've told you a million times.",
      "It's a bit cold today.",
      "Not bad at all.",
      "I might be late.",
    ],
    0,
    "READING",
    "hyperbole یعنی اغراق برای تأکید: «یک میلیون بار» ادعایی غیرواقعی است. «Not bad» در " +
      "معنای «خیلی خوب» کمگویی (understatement) است.",
  ),

  // B1 — money (7 → 8)
  mc(
    "b1.money.ex.08",
    "b1.money.lesson-01",
    8,
    "We need to cut ______ on our spending this month.",
    "این ماه باید از هزینههایمان کم کنیم.",
    ["down", "off", "in", "up"],
    0,
    "VOCABULARY",
    "cut down on یعنی «کاستن از» هزینه یا مصرف؛ cut off یعنی قطع کردن و cut up یعنی تکه کردن.",
  ),
];

for (const item of additions) {
  if (exercises.exercises.some((e) => e.id === item.id)) throw new Error(`duplicate exercise id ${item.id}`);
  exercises.exercises.push(item);
}

// 5 ── one version for all five bundles -------------------------------------
for (const bundle of [lessons, vocabulary, exercises, placement, knowledge]) {
  bundle.contentVersion = VERSION;
}

write("lessons.json", lessons);
write("vocabulary.json", vocabulary);
write("exercises.json", exercises);
write("placement.json", placement);
write("knowledge.json", knowledge);

console.log(
  `v${VERSION}: ${exercises.exercises.length} exercises, ${vocabulary.vocabulary.length} words`,
);
