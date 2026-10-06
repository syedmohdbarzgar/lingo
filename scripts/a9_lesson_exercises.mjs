/**
 * A-9 part 1: exercises for the seven new lessons + phonology coverage for the
 * six new A1 lessons.
 *
 * Per lesson: six grammar-targeting exercises (multiple_choice tagged
 * `skill: GRAMMAR`, or fill_blank which defaults to GRAMMAR), one guided
 * listening drill (`explanationFa` naming what to hear — PhonologyCoverageTest
 * requires it for A1) and one vocabulary choice.
 *
 * Phonology: A-7's coverage test demands every A1 lesson sit under a PHONOLOGY
 * knowledge item, so the new lessons join the existing nodes and two nodes are
 * added for the topics only they teach — final consonant sounds (plural
 * endings, imperative releases) and the /w/ and /h/ of question words. Nodes
 * stay leaves: never a prerequisite of anything.
 *
 * No version bump here — a9_add_grammar_lessons.mjs already took the batch to v9.
 */
import fs from "node:fs";

const DIR = "app/src/main/assets/content";

/** PHONOLOGY coverage for the new A1 lessons (flat graph, LISTENING evidence). */
const phonologyAdditions = [
  {
    id: "phonology.final-consonants",
    type: "PHONOLOGY",
    title: "Final consonant sounds: /s/, /z/, /ɪz/ and released stops",
    titleFa: "آوای پایانی: جمع‌ها و آزادسازی صامت‌ها",
    level: "A1",
    prerequisites: [],
    lessons: ["a1.plurals.lesson-01", "a1.imperatives.lesson-01"],
    skills: ["LISTENING"],
  },
  {
    id: "phonology.w-h-sounds",
    type: "PHONOLOGY",
    title: "The sounds /w/ and /h/ in question words",
    titleFa: "آوای /w/ و /h/ در کلمات پرسشی",
    level: "A1",
    prerequisites: [],
    lessons: ["a1.question-words.lesson-01"],
    skills: ["LISTENING"],
  },
];

/** Existing phonology nodes that now also cover new lessons. */
const phonologyLessonAdds = {
  "phonology.vowel-length": ["a1.articles.lesson-01"],
  "phonology.word-stress": ["a1.pronouns.lesson-01", "a1.can.lesson-01"],
};

const exercises = [
  // --- a1.articles: a / an / the ----------------------------------------
  mc("a1.articles.ex.01", "a1.articles.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["She is a university student.", "She is an university student.", "She is the university student.", "She is student a university."], 0),
  fb("a1.articles.ex.02", "a1.articles.lesson-01", 2, "There is ___ egg and some bread in the fridge.", ["an"]),
  mc("a1.articles.ex.03", "a1.articles.lesson-01", 3, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["The sun rises in the east.", "A sun rises in the east.", "Sun rises in the east.", "The sun rise in the east."], 0),
  mc("a1.articles.ex.04", "a1.articles.lesson-01", 4, "Which sentence is correct?", "کدام جمله درست است؟",
    ["I drink water every day.", "I drink the water every day.", "I drink a water every day.", "I drink waters every day."], 0),
  fb("a1.articles.ex.05", "a1.articles.lesson-01", 5, "Please close ___ door. It's cold in here.", ["the"]),
  mc("a1.articles.ex.06", "a1.articles.lesson-01", 6, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["I bought a book. The book is on the table.", "I bought a book. A book is on the table.",
     "I bought the book. A book is on the table.", "I bought book. The book is on the table."], 0),
  ls("a1.articles.ex.07", "a1.articles.lesson-01", 7, "The hotel is next to the bank.",
    "به تلفظ the با صدای /ðə/ ضعیف و تکیهٔ hotel گوش کن."),
  mc("a1.articles.ex.08", "a1.articles.lesson-01", 8, 'What does "umbrella" mean?', "معنی «umbrella» چیست؟",
    ["چتر", "کلاه", "بادبزن", "کیف"], 0),

  // --- a1.plurals --------------------------------------------------------
  mc("a1.plurals.ex.01", "a1.plurals.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["There are three children in the park.", "There are three childs in the park.",
     "There is three children in the park.", "There are three child in the park."], 0),
  fb("a1.plurals.ex.02", "a1.plurals.lesson-01", 2, "There are twenty ___ in the class.", ["children", "students"]),
  mc("a1.plurals.ex.03", "a1.plurals.lesson-01", 3, "Which sentence is correct?", "کدام جمله درست است؟",
    ["She bought two boxes of tea.", "She bought two box of tea.", "She buyed two boxes of tea.", "She bought two boxes teas."], 0),
  mc("a1.plurals.ex.04", "a1.plurals.lesson-01", 4, "Choose the correct plural of “leaf”:", "جمع درست «leaf» کدام است؟",
    ["leaves", "leafs", "leafes", "leave"], 0),
  mc("a1.plurals.ex.05", "a1.plurals.lesson-01", 5, "Choose the correct plural of “woman”:", "جمع درست «woman» کدام است؟",
    ["women", "womans", "womens", "womanes"], 0),
  fb("a1.plurals.ex.06", "a1.plurals.lesson-01", 6, "How many ___ does your brother have?", ["children", "kids"]),
  ls("a1.plurals.ex.07", "a1.plurals.lesson-01", 7, "There are three children in the park.",
    "به پایانِ /n/ در children و تکیهٔ جمله روی three گوش کن."),
  mc("a1.plurals.ex.08", "a1.plurals.lesson-01", 8, 'What does "box" mean?', "معنی «box» چیست؟",
    ["جعبه", "بطری", "کیسه", "قفسه"], 0),

  // --- a1.pronouns -------------------------------------------------------
  mc("a1.pronouns.ex.01", "a1.pronouns.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["He helps his sister every day.", "Him helps his sister every day.",
     "He help his sister every day.", "His helps him sister every day."], 0),
  fb("a1.pronouns.ex.02", "a1.pronouns.lesson-01", 2, "The teacher asked ___ to open our books.", ["us"]),
  mc("a1.pronouns.ex.03", "a1.pronouns.lesson-01", 3, "Which sentence is correct?", "کدام جمله درست است؟",
    ["I gave the book to her.", "I gave the book to she.", "I gave the book to hers.", "Me gave the book to her."], 0),
  mc("a1.pronouns.ex.04", "a1.pronouns.lesson-01", 4, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["He and I are here.", "Me and him are here.", "Him and me are here.", "He and me are here."], 0),
  mc("a1.pronouns.ex.05", "a1.pronouns.lesson-01", 5, "Which sentence is correct?", "کدام جمله درست است؟",
    ["We met them at the station.", "We met they at the station.", "Us met them at the station.", "We met their at the station."], 0),
  mc("a1.pronouns.ex.06", "a1.pronouns.lesson-01", 6, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["Please give it to me.", "Please give it to I.", "Please give it to mine.", "Please me give it to you."], 0),
  ls("a1.pronouns.ex.07", "a1.pronouns.lesson-01", 7, "We met them at the station.",
    "به /ðəm/ کوتاه در them و تکیهٔ فعلِ met گوش کن."),
  mc("a1.pronouns.ex.08", "a1.pronouns.lesson-01", 8, 'What does "teacher" mean?', "معنی «teacher» چیست؟",
    ["معلم", "دانش‌آموز", "مدیر", "دانشمند"], 0),

  // --- a1.can ------------------------------------------------------------
  mc("a1.can.ex.01", "a1.can.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["She can drive a car.", "She cans drive a car.", "She can drives a car.", "She can to drive a car."], 0),
  fb("a1.can.ex.02", "a1.can.lesson-01", 2, "I ___ play the guitar — it's too difficult.", ["can't", "cannot"]),
  mc("a1.can.ex.03", "a1.can.lesson-01", 3, "Choose the correct form:", "شکل درست را انتخاب کنید:",
    ["I can swim.", "I can to swim.", "I can swims.", "I cans swim."], 0),
  mc("a1.can.ex.04", "a1.can.lesson-01", 4, "Choose the correct question:", "پرسش درست را انتخاب کنید:",
    ["Can you speak English?", "Do you can speak English?", "You can speak English?", "Can speak you English?"], 0),
  fb("a1.can.ex.05", "a1.can.lesson-01", 5, "We ___ arrive before six, so let's leave at five.", ["can"]),
  mc("a1.can.ex.06", "a1.can.lesson-01", 6, "Which sentence is correct?", "کدام جمله درست است؟",
    ["I could run fast when I was young.", "I can run fast when I was young.",
     "I could to run fast when I was young.", "I cans run fast when I was young."], 0),
  ls("a1.can.ex.07", "a1.can.lesson-01", 7, "My father can cook very well.",
    "به /kən/ ضعیفِ can در جملهٔ خبری و تکیهٔ cook گوش کن."),
  mc("a1.can.ex.08", "a1.can.lesson-01", 8, 'What does "swim" mean?', "معنی «swim» چیست؟",
    ["شنا کردن", "غواصی", "دویدن", "پیاده‌روی"], 0),

  // --- a1.imperatives ----------------------------------------------------
  mc("a1.imperatives.ex.01", "a1.imperatives.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["Don't run in the corridor.", "Don't runs in the corridor.", "Don't to run in the corridor.", "Not run in the corridor."], 0),
  fb("a1.imperatives.ex.02", "a1.imperatives.lesson-01", 2, "___ the door, please. It's cold outside.", ["close", "shut"]),
  mc("a1.imperatives.ex.03", "a1.imperatives.lesson-01", 3, "Which sentence is correct?", "کدام جمله درست است؟",
    ["Open your books, please.", "You open your books, please.", "Opens your books, please.", "To open your books, please."], 0),
  mc("a1.imperatives.ex.04", "a1.imperatives.lesson-01", 4, "Choose the correct polite request:", "درخواست مؤدبانهٔ درست را انتخاب کنید:",
    ["Please wait here.", "Please to wait here.", "You please waiting here.", "Please waits here."], 0),
  fb("a1.imperatives.ex.05", "a1.imperatives.lesson-01", 5, "Don't ___ the wire. It's dangerous.", ["touch", "hold"]),
  mc("a1.imperatives.ex.06", "a1.imperatives.lesson-01", 6, "Choose the correct negative:", "جملهٔ منفی درست را انتخاب کنید:",
    ["Don't touch the wire.", "Don't to touch the wire.", "Not touch the wire.", "Doesn't touch the wire."], 0),
  ls("a1.imperatives.ex.07", "a1.imperatives.lesson-01", 7, "Please close the door.",
    "به تکیهٔ close و آزادسازیِ پایانیِ /r/ در door گوش کن."),
  mc("a1.imperatives.ex.08", "a1.imperatives.lesson-01", 8, 'What does "stop" mean?', "معنی «stop» چیست؟",
    ["توقف کردن", "شروع کردن", "منتظر ماندن", "ادامه دادن"], 0),

  // --- a1.question-words -------------------------------------------------
  mc("a1.question-words.ex.01", "a1.question-words.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["Where are you from?", "Where you are from?", "You are from where?", "Where from you are?"], 0),
  fb("a1.question-words.ex.02", "a1.question-words.lesson-01", 2, "___ does the train leave? — At six.", ["when", "what time"]),
  mc("a1.question-words.ex.03", "a1.question-words.lesson-01", 3, "Choose the correct question:", "پرسش درست را انتخاب کنید:",
    ["What does this mean?", "What means this?", "How means this?", "This means what?"], 0),
  mc("a1.question-words.ex.04", "a1.question-words.lesson-01", 4, "Which question asks about a reason?", "کدام پرسش دلیل را می‌پرسد؟",
    ["Why are you late?", "Where are you late?", "When are you late?", "Who are you late?"], 0),
  fb("a1.question-words.ex.05", "a1.question-words.lesson-01", 5, "___ do you live? — In Tehran.", ["where"]),
  mc("a1.question-words.ex.06", "a1.question-words.lesson-01", 6, "Choose the correct question word:", "کلمهٔ پرسشی درست را انتخاب کنید:",
    ["How many students are in your class?", "How much students are in your class?",
     "How old students are in your class?", "How long students are in your class?"], 0),
  ls("a1.question-words.ex.07", "a1.question-words.lesson-01", 7, "What time does the train leave?",
    "به تکیهٔ what و time و آهنگ بالاروندهٔ پایان پرسش گوش کن."),
  mc("a1.question-words.ex.08", "a1.question-words.lesson-01", 8, 'What does "why" mean?', "معنی «why» چیست؟",
    ["چرا", "کجا", "کی", "چگونه"], 0),

  // --- a2.past-simple ----------------------------------------------------
  mc("a2.past-simple.ex.01", "a2.past-simple.lesson-01", 1, "Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
    ["We went to the coast last weekend.", "We go to the coast last weekend.",
     "We have went to the coast last weekend.", "We were go to the coast last weekend."], 0),
  fb("a2.past-simple.ex.02", "a2.past-simple.lesson-01", 2, "She ___ a new phone yesterday.", ["bought", "got"]),
  mc("a2.past-simple.ex.03", "a2.past-simple.lesson-01", 3, "Choose the correct negative:", "جملهٔ منفی درست را انتخاب کنید:",
    ["I didn't see your message.", "I didn't saw your message.", "I don't saw your message.", "I didn't seen your message."], 0),
  mc("a2.past-simple.ex.04", "a2.past-simple.lesson-01", 4, "Choose the correct question:", "پرسش درست را انتخاب کنید:",
    ["Did she leave?", "Did she left?", "Did she leaving?", "Does she leave yesterday?"], 0),
  fb("a2.past-simple.ex.05", "a2.past-simple.lesson-01", 5, "He ___ an email last night.", ["wrote", "sent"]),
  mc("a2.past-simple.ex.06", "a2.past-simple.lesson-01", 6, "Choose the correct past form:", "شکل گذشتهٔ درست را انتخاب کنید:",
    ["They bought a new car.", "They buyed a new car.", "They buy a new car yesterday.", "They has bought a new car."], 0),
  ls("a2.past-simple.ex.07", "a2.past-simple.lesson-01", 7, "We went to the coast last weekend.",
    "به کششِ /eɪ/ در coast و تکیهٔ weekend گوش کن."),
  mc("a2.past-simple.ex.08", "a2.past-simple.lesson-01", 8, 'What does "buy" mean?', "معنی «buy» چیست؟",
    ["خریدن", "فروختن", "چیدن", "پرداخت کردن"], 0),
];

function mc(id, lessonId, order, question, questionFa, options, correctIndex, skill = "GRAMMAR") {
  return { id, lessonId, type: "multiple_choice", order, question, questionFa, options, correctIndex, skill };
}

function fb(id, lessonId, order, sentence, accepted) {
  return { id, lessonId, type: "fill_blank", order, sentence, accepted };
}

function ls(id, lessonId, order, text, explanationFa) {
  return { id, lessonId, type: "listening", order, text, accepted: [text.toLowerCase().replace(/[.,!?]/g, "")], explanationFa };
}

function load(name) {
  return JSON.parse(fs.readFileSync(`${DIR}/${name}.json`, "utf8"));
}

function save(name, root) {
  fs.writeFileSync(`${DIR}/${name}.json`, JSON.stringify(root, null, 2) + "\n");
}

// --- phonology coverage ---------------------------------------------------
const knowledgeRoot = load("knowledge");
const byId = new Map(knowledgeRoot.knowledge.map((k) => [k.id, k]));
let nodesAdded = 0;
let linksAdded = 0;

for (const node of phonologyAdditions) {
  if (byId.has(node.id)) continue;
  knowledgeRoot.knowledge.push(node);
  byId.set(node.id, node);
  nodesAdded += 1;
}
for (const [id, lessonIds] of Object.entries(phonologyLessonAdds)) {
  const node = byId.get(id);
  if (!node) throw new Error(`missing knowledge item ${id}`);
  for (const lessonId of lessonIds) {
    if (!node.lessons.includes(lessonId)) {
      node.lessons.push(lessonId);
      linksAdded += 1;
    }
  }
}

// --- exercises ------------------------------------------------------------
const exercisesRoot = load("exercises");
const existing = new Set(exercisesRoot.exercises.map((e) => e.id));
let added = 0;
for (const exercise of exercises) {
  if (existing.has(exercise.id)) continue;
  exercisesRoot.exercises.push(exercise);
  added += 1;
}

save("knowledge", knowledgeRoot);
save("exercises", exercisesRoot);

console.log(
  `phonology nodes +${nodesAdded}, lesson links +${linksAdded}, exercises +${added} ` +
    `(total ${exercisesRoot.exercises.length}, knowledge ${knowledgeRoot.knowledge.length})`,
);
