/**
 * A-7 (one-shot authoring pass): phonology / pronunciation content for the A1
 * lessons — vowel length (تشنگی/کشش واکه‌ها), word sounds (stress, weak
 * syllables) and the /θ/ sound Persian speakers substitute.
 *
 * Three surfaces, all JSON-only (technical spec §61/§63):
 *
 *   1. a `## تلفظ` section appended to each A1 lesson's `grammarTipFa` — the
 *      A-8 parser maps an unknown header to a CUSTOM section, so it renders
 *      as its own teaching card with no Kotlin and no Room migration.
 *   2. three PHONOLOGY knowledge items (skills = LISTENING), chained
 *      vowel-length → word-stress → th-sounds, each covering two A1 lessons.
 *      They are leaves in the graph: nothing depends on them, so they can
 *      never block a lesson from unlocking.
 *   3. one listening drill per A1 lesson whose text foregrounds the target
 *      sounds, so listening practice is what moves their mastery.
 *
 * Evidence plumbing: `KnowledgeEvidence.itemsFor` now unions "nodes whose type
 * matches" with "nodes that explicitly list the skill" — without that, a
 * LISTENING attempt would only ever reach VOCABULARY nodes and phonology would
 * sit at zero mastery forever (A-7 companion change, covered by
 * KnowledgeEngineTest).
 *
 * contentVersion bumps once for this batch: 7 → 8, in all five files.
 * (A-9 bumps separately when it lands — see a9_add_grammar_lessons.mjs.)
 */
import fs from "node:fs";

const DIR = "app/src/main/assets/content";
const NEW_VERSION = 8;

/** `## تلفظ` block appended to each A1 lesson's teaching page. */
const pronunciationSections = {
  "a1.greetings.lesson-01": `## تلفظ
hello: تکیه روی هجای دوم است (he-LLO) و هجای اول یک /ə/ خنثی و کوتاه است، نه /e/.
thank با آوای /θ/ گفته می‌شود — زبان میان دندان‌ها؛ «سَنک» با /s/ غلط است.
please و meet واکهٔ بلند /iː/ دارند؛ کوتاه خواندنشان آن‌ها را به «پلیس» و «میت» نزدیک می‌کند.`,

  "a1.daily-routine.lesson-01": `## تلفظ
wake با واکهٔ دوهجایی /eɪ/ است؛ آن را مثل /e/ کوتاه نخوانید.
usually روی هجای اول تکیه دارد (US-ually) و هجای دوم /jə/ ضعیف است.
breakfast دو هجاست و تکیه روی اول می‌افتد: BREK-fest؛ هجای دوم /ə/ کوتاه است.`,

  "a1.family.lesson-01": `## تلفظ
family سه هجاست و تکیه روی اول است: FA-mi-ly؛ هجای میانی /ə/ خنثی است.
sister و brother هر دو روی هجای اول تکیه دارند و -er آخر کوتاه /ə/ است.
daughter با واکهٔ بلند /ɔː/ و بدون r تلفظ می‌شود؛ کشش واکه را حفظ کنید.`,

  "a1.shopping.lesson-01": `## تلفظ
cheap و expensive هر دو با واکهٔ بلند /iː/ شروع می‌شوند؛ آن را کوتاه نخوانید.
money با /ʌ/ کوتاه تلفظ می‌شود (MUN-ey)، نه /uː/.
در پرسش How much…؟ تکیه روی much می‌افتد و h در how نرم و کوتاه است.`,

  "a1.introductions.lesson-01": `## تلفظ
name با واکهٔ دوهجایی /eɪ/ است؛ آن را با /e/ کوتاه نخوانید.
در What's your name? تکیه روی name می‌افتد و 's به صدای /z/ می‌رسد.
spell و from هر دو واکهٔ کوتاه دارند؛ کشیدن‌شان جمله را سنگین و غیرطبیعی می‌کند.`,

  "a1.weather.lesson-01": `## تلفظ
rain با واکهٔ دوهجایی /eɪ/ است، نه /e/ کوتاه.
hot واکهٔ کوتاه /ɒ/ دارد؛ کشیدن آن تلفظ را خراب می‌کند.
sunny روی هجای اول تکیه دارد (SUN-ny) و هجای دوم /i/ کوتاه است؛ wind با /ɪ/ کوتاه است.`,
};

/**
 * PHONOLOGY graph: deliberately flat — every item has NO prerequisites and is
 * nothing else's prerequisite.
 *
 * A chain (vowel-length → word-stress → th-sounds) would look tidy, but a
 * lesson unlocks only when all of its items' prerequisites are mastered, and
 * these nodes are mastered by listening practice in *other* lessons: the chain
 * would lock a1.daily-routine (lesson order 2) behind a1.introductions (25).
 * Phonology is parallel enrichment, never a gate.
 */
const phonologyItems = [
  {
    id: "phonology.vowel-length",
    type: "PHONOLOGY",
    title: "Vowel length: short and long vowels",
    titleFa: "کشش واکه‌ها: واکهٔ کوتاه و بلند",
    level: "A1",
    prerequisites: [],
    lessons: ["a1.introductions.lesson-01", "a1.weather.lesson-01"],
    skills: ["LISTENING"],
  },
  {
    id: "phonology.word-stress",
    type: "PHONOLOGY",
    title: "Word stress and weak syllables",
    titleFa: "تکیهٔ کلمه و هجاهای ضعیف",
    level: "A1",
    prerequisites: [],
    lessons: ["a1.daily-routine.lesson-01", "a1.family.lesson-01"],
    skills: ["LISTENING"],
  },
  {
    id: "phonology.th-sounds",
    type: "PHONOLOGY",
    title: "The sounds /θ/ and /ð/",
    titleFa: "آوای th: /θ/ و /ð/",
    level: "A1",
    prerequisites: [],
    lessons: ["a1.greetings.lesson-01", "a1.shopping.lesson-01"],
    skills: ["LISTENING"],
  },
];

/**
 * One listening drill per A1 lesson: the learner must HEAR the target sounds
 * to type the sentence back, and the explanation names what to listen for.
 * Accepted answers follow the existing convention (lower-case, no punctuation).
 */
const listeningDrills = [
  {
    id: "a1.greetings.ex.09",
    lessonId: "a1.greetings.lesson-01",
    text: "Nice to meet you, Sara.",
    explanationFa: "به کشش واکهٔ /iː/ در meet و /aɪ/ در nice گوش کن.",
  },
  {
    id: "a1.daily-routine.ex.08",
    lessonId: "a1.daily-routine.lesson-01",
    text: "I wake up at seven and take a shower.",
    explanationFa: "به واکهٔ دوهجایی /eɪ/ در wake و تکیهٔ usually در جمله گوش کن.",
  },
  {
    id: "a1.family.ex.08",
    lessonId: "a1.family.lesson-01",
    text: "My sister lives with our father.",
    explanationFa: "به تکیهٔ هجای اول در sister و father و /ə/ کوتاهِ -er گوش کن.",
  },
  {
    id: "a1.shopping.ex.08",
    lessonId: "a1.shopping.lesson-01",
    text: "This shirt is cheap and that one is expensive.",
    explanationFa: "به /iː/ بلند در cheap و expensive گوش کن.",
  },
  {
    id: "a1.introductions.ex.07",
    lessonId: "a1.introductions.lesson-01",
    text: "My name is Amir and I am from Iran.",
    explanationFa: "به /eɪ/ در name و تکیهٔ جمله روی name گوش کن.",
  },
  {
    id: "a1.weather.ex.07",
    lessonId: "a1.weather.lesson-01",
    text: "It is cold and rainy this morning.",
    explanationFa: "به /eɪ/ در rainy و واکهٔ کوتاه /ɒ/ در hot و /əʊ/ در cold گوش کن.",
  },
];

function load(name) {
  return JSON.parse(fs.readFileSync(`${DIR}/${name}.json`, "utf8"));
}

function save(name, root) {
  fs.writeFileSync(`${DIR}/${name}.json`, JSON.stringify(root, null, 2) + "\n");
}

// --- 1. pronunciation sections inside the A1 teaching pages -------------
const lessonsRoot = load("lessons");
let sectionsAdded = 0;
for (const lesson of lessonsRoot.lessons) {
  const section = pronunciationSections[lesson.id];
  if (!section) continue;
  if ((lesson.grammarTipFa || "").includes("## تلفظ")) continue;
  lesson.grammarTipFa = `${lesson.grammarTipFa}\n${section}`;
  sectionsAdded += 1;
}

// --- 2. PHONOLOGY knowledge items ---------------------------------------
const knowledgeRoot = load("knowledge");
const known = new Set(knowledgeRoot.knowledge.map((k) => k.id));
let itemsAdded = 0;
for (const item of phonologyItems) {
  if (known.has(item.id)) continue;
  knowledgeRoot.knowledge.push(item);
  itemsAdded += 1;
}

// --- 3. listening drills -------------------------------------------------
const exercisesRoot = load("exercises");
const exerciseIds = new Set(exercisesRoot.exercises.map((e) => e.id));
let drillsAdded = 0;
for (const drill of listeningDrills) {
  if (exerciseIds.has(drill.id)) continue;
  const orders = exercisesRoot.exercises
    .filter((e) => e.lessonId === drill.lessonId)
    .map((e) => e.order);
  exercisesRoot.exercises.push({
    id: drill.id,
    lessonId: drill.lessonId,
    type: "listening",
    order: Math.max(0, ...orders) + 1,
    text: drill.text,
    accepted: [drill.text.toLowerCase().replace(/[.,!?]/g, "")],
    explanationFa: drill.explanationFa,
  });
  drillsAdded += 1;
}

// --- one bundle-version bump for this batch ------------------------------
const files = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const roots = {
  lessons: lessonsRoot,
  knowledge: knowledgeRoot,
  exercises: exercisesRoot,
  vocabulary: load("vocabulary"),
  placement: load("placement"),
};
let bumped = 0;
for (const name of files) {
  if (roots[name].contentVersion !== NEW_VERSION) {
    roots[name].contentVersion = NEW_VERSION;
    bumped += 1;
  }
}

for (const name of files) save(name, roots[name]);

console.log(
  `ت فرض sections +${sectionsAdded}, phonology items +${itemsAdded}, ` +
    `listening drills +${drillsAdded}, contentVersion bumped in ${bumped} file(s) → v${NEW_VERSION}`,
);
