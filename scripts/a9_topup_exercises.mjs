/**
 * A-9 part 2: more exercises per grammar topic.
 *
 * The A-8 acceptance bar is six grammar-targeting exercises per GRAMMAR
 * knowledge item, counted with the production rule (`AnswerChecker.skillOf`
 * over the item's teaching lessons). Before this pass 30 topics sat at 1–5,
 * mostly because a lesson's exercise mix was dominated by vocabulary choices.
 *
 * Every shortfall is topped up with exercises that target the lesson's actual
 * grammar point: multiple_choice tagged `skill: GRAMMAR`, plus a few fill_blank
 * items that each accept several equivalent answers (A-2 rule — a lexical or
 * modal swap inside the same structure), so no allowlist entry is needed.
 *
 * Orders continue from each lesson's highest existing order.
 * No version bump — a9_add_grammar_lessons.mjs already took the batch to v9.
 */
import fs from "node:fs";

const DIR = "app/src/main/assets/content";

function mc(question, questionFa, options, correctIndex) {
  return { type: "multiple_choice", question, questionFa, options, correctIndex, skill: "GRAMMAR" };
}

function fb(sentence, accepted) {
  return { type: "fill_blank", sentence, accepted };
}

/** lessonId -> exercises to append, in order. */
const plan = {
  // --- A1: present simple, third person -s ------------------------------
  "a1.daily-routine.lesson-01": [
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["My brother works in a bank.", "My brother work in a bank.", "My brother working in a bank.", "My brother is work in a bank."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["She goes to school by bus.", "She go to school by bus.", "She going to school by bus.", "He go to school by bus."], 0),
    mc("Choose the correct negative:", "جملهٔ منفی درست را انتخاب کنید:",
      ["They don't work at weekends.", "They doesn't work at weekends.", "They don't works at weekends.", "They aren't work at weekends."], 0),
  ],

  // --- A1: possessive adjectives ---------------------------------------
  "a1.introductions.lesson-01": [
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["This is my book.", "This is mine book.", "This is me book.", "This is I book."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["Her name is Sara.", "She name is Sara.", "Hers name is Sara.", "Her name are Sara."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["Our teacher speaks English.", "Us teacher speaks English.", "Ours teacher speaks English.", "Our teacher speak English."], 0),
  ],

  // --- A1: how much / how many -----------------------------------------
  "a1.shopping.lesson-01": [
    mc("Choose the correct question:", "پرسش درست را انتخاب کنید:",
      ["How many shoes are in the box?", "How much shoes are in the box?", "How many shoe are in the box?", "How many shoes is in the box?"], 0),
    mc("Choose the correct question:", "پرسش درست را انتخاب کنید:",
      ["How much is this shirt?", "How many is this shirt?", "How much are this shirt?", "How much are these shirt?"], 0),
  ],

  // --- A1: present continuous ------------------------------------------
  "a1.weather.lesson-01": [
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["It is raining now.", "It raining now.", "It is rain now.", "It are raining now."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["Look! The baby is sleeping.", "Look! The baby sleeps now.", "Look! The baby is sleep.", "Look! The baby are sleeping."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["I am studying English right now.", "I studying English right now.", "I am study English right now.", "I are studying English right now."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["She is working today.", "She working today.", "She is work today.", "She are working today."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["Wait a moment — I am getting dressed.", "Wait a moment — I getting dressed.", "Wait a moment — I am get dressed.", "Wait a moment — I am getting dress."], 0),
  ],

  // --- A2: there is / there are, comparatives ---------------------------
  "a2.city-life.lesson-01": [
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["There are many parks in this city.", "There is many parks in this city.", "There are many park in this city.", "It many parks in this city."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["There is a new library near the school.", "There are a new library near the school.", "There is new library the near school.", "Has a new library near the school."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["This flat is more expensive than ours.", "This flat is expensiver than ours.", "This flat is most expensive than ours.", "This flat is expensive than ours."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["The traffic is worse in the morning.", "The traffic is more worse in the morning.", "The traffic is worst in the morning.", "The traffic are worse in the morning."], 0),
  ],

  // --- A2: prepositions of place ---------------------------------------
  "a2.directions.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["The bank is between the school and the mosque.", "The bank is between from the school and the mosque.", "The bank is among the school and the mosque.", "The bank is in the school and the mosque."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["Go straight and turn left at the traffic lights.", "Go straight and turn left in the traffic lights.", "Go straight and turn left on the traffic lights.", "Go straight and turn left to the traffic lights."], 0),
  ],

  // --- A2: should / shouldn't for advice --------------------------------
  "a2.health.lesson-01": [
    mc("Choose the correct advice:", "توصیهٔ درست را انتخاب کنید:",
      ["You should drink more water.", "You should to drink more water.", "You should drinking more water.", "You should drinks more water."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["You shouldn't eat so much sugar.", "You shouldn't to eat so much sugar.", "You shouldn't eating so much sugar.", "You shouldn't eats so much sugar."], 0),
  ],

  // --- A2: verb + -ing, verb + to-infinitive ---------------------------
  "a2.hobbies.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["I enjoy reading in the evening.", "I enjoy to read in the evening.", "I enjoy read in the evening.", "I enjoying read in the evening."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["They decided to travel in May.", "They decided travel in May.", "They decided traveling in May.", "They decides to travel in May."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["He likes swimming but he hates running.", "He likes to swimming but he hates to running.", "He likes swim but he hates run.", "He liking swimming but he hating running."], 0),
  ],

  // --- A2: would like, some / any --------------------------------------
  "a2.restaurant.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["I would like a cup of tea, please.", "I would to like a cup of tea, please.", "I would like tea a cup of, please.", "I would like to tea a cup, please."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["We would like to order now.", "We would to like to order now.", "We would like order now.", "We would liking to order now."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["There isn't any sugar in the cake.", "There isn't some sugar in the cake.", "There isn't no sugar in the cake.", "There aren't any sugar in the cake."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["I don't have any coins.", "I don't have some coins.", "I don't have no coins.", "I doesn't have any coins."], 0),
  ],

  // --- B1: past continuous, past perfect --------------------------------
  "b1.experiences.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["I was reading when you called.", "I was read when you called.", "I did reading when you called.", "I were reading when you called."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["She had finished her work before the meeting started.", "She had finish her work before the meeting started.", "She has had finished her work before the meeting started.", "She had finished her work before the meeting was starting."], 0),
  ],

  // --- B1: ought to / had better ----------------------------------------
  "b1.health-lifestyle.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["You ought to eat less sugar.", "You ought eat less sugar.", "You ought to eating less sugar.", "You oughts to eat less sugar."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["You had better see a dentist today.", "You had better to see a dentist today.", "You had better seeing a dentist today.", "You had better sees a dentist today."], 0),
    mc("Choose the correct advice:", "توصیهٔ درست را انتخاب کنید:",
      ["You ought to walk to work more often.", "You ought to walking to work more often.", "You ought walking to work more often.", "You oughts to walk to work more often."], 0),
    fb("You had better ___ a coat — it's cold outside.", ["wear", "take", "bring", "put on"]),
  ],

  // --- B1: present perfect with for / since -----------------------------
  "b1.money.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["I have worked here for two years.", "I have worked here since two years.", "I am working here for two years.", "I have worked here two years ago."], 0),
  ],
  "b1.work.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["She has managed the team since 2021.", "She has managed the team for 2021.", "She is managing the team since 2021.", "She has managed the team since three years."], 0),
  ],

  // --- B1: the passive (simple tenses) ----------------------------------
  "b1.news.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["The company was founded in 1990.", "The company was found in 1990.", "The company is founded in 1990.", "The company was founding in 1990."], 0),
    fb("The bridge ___ last year.", ["was built", "was constructed", "was completed"]),
  ],

  // --- B1: be going to, second conditional ------------------------------
  "b1.plans.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["We are going to move next month.", "We going to move next month.", "We are go to move next month.", "We are going move next month."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["If I had time, I would travel more.", "If I had time, I will travel more.", "If I have time, I would travel more.", "If I had time, I would travelled more."], 0),
  ],

  // --- B2: modals of deduction, tentative conditionals -------------------
  "b2.environment.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["It must be the oldest bridge in the region.", "It must to be the oldest bridge in the region.", "It must being the oldest bridge in the region.", "It must been the oldest bridge in the region."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["If I were the manager, I would change the policy.", "If I am the manager, I would change the policy.", "If I were the manager, I will change the policy.", "If I were the manager, I would changed the policy."], 0),
  ],

  // --- B2: reported questions -------------------------------------------
  "b2.interview.lesson-01": [
    mc("Choose the correct reported question:", "گزارش پرسش درست را انتخاب کنید:",
      ["She asked where I had worked before.", "She asked where had I worked before.", "She asked where did I work before.", "She asked that where I had worked before."], 0),
    fb("He asked me what time the meeting ___.", ["started", "began", "would start"]),
  ],

  // --- B2: used to / would ----------------------------------------------
  "b2.media.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["I used to watch this show every week.", "I use to watch this show every week.", "I used to watching this show every week.", "I used watching this show every week."], 0),
    fb("When we were children, we ___ play in the street.", ["used to", "would"]),
  ],

  // --- B2: present perfect passive ---------------------------------------
  "b2.problems.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["The issue has been resolved.", "The issue has resolved.", "The issue have been resolved.", "The issue has been resolving."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["The emails have been sent to the clients.", "The emails has been sent to the clients.", "The emails have been send to the clients.", "The emails have been sending to the clients."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["Three complaints have been logged this week.", "Three complaints has been logged this week.", "Three complaints have been logging this week.", "Three complaints have logged been this week."], 0),
    fb("The road ___ since Monday.", ["has been repaired", "has been fixed", "has been rebuilt"]),
    fb("Your request ___ — you can collect the permit.", ["has been approved", "has been granted", "has been accepted"]),
  ],

  // --- B2: subject–verb agreement with tricky nouns ----------------------
  "b2.research.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["Mathematics is my favourite subject.", "Mathematics are my favourite subject.", "Mathematics was my favourite subjects.", "Mathematics were my favourite subject."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["The news is surprising.", "The news are surprising.", "The news were surprising.", "The newses are surprising."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["Everybody knows the answer.", "Everybody know the answer.", "Everybody are knowing the answer.", "Everybodies know the answer."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["There is a lot of traffic on this road.", "There are a lot of traffic on this road.", "There is a lot of traffics on this road.", "There be a lot of traffic on this road."], 0),
  ],

  // --- B2: relative clauses, passive with modals -------------------------
  "b2.technology.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["The woman who lives next door is a doctor.", "The woman which lives next door is a doctor.", "The woman whom lives next door is a doctor.", "The woman lives who next door is a doctor."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["The report must be finished by Friday.", "The report must finished be by Friday.", "The report must be finish by Friday.", "The report must been finished by Friday."], 0),
  ],

  // --- C1: deduction about the past --------------------------------------
  "c1.inference.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["He must have missed the train.", "He must missed the train.", "He must have miss the train.", "He must to have missed the train."], 0),
    mc("Which sentence is correct?", "کدام جمله درست است؟",
      ["They can't have finished already.", "They can't finished already.", "They can't have finish already.", "They can't have been finish already."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["She might have forgotten about the meeting.", "She might forgotten about the meeting.", "She might have forget about the meeting.", "She might to have forgotten about the meeting."], 0),
    mc("Choose the correct inference:", "نتیجه‌گیری درست را انتخاب کنید:",
      ["They must have left an hour ago — the lights are off.", "They must left an hour ago — the lights are off.", "They must have leave an hour ago — the lights are off.", "They must have leaving an hour ago — the lights are off."], 0),
  ],

  // --- C1: modals in the past --------------------------------------------
  "c1.persuasion.lesson-01": [
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["You should have told me earlier.", "You should told me earlier.", "You should have tell me earlier.", "You should to have told me earlier."], 0),
    mc("Choose the correct sentence:", "جملهٔ درست را انتخاب کنید:",
      ["We needn't have hurried — the train was late anyway.", "We needn't hurried — the train was late anyway.", "We needn't have hurry — the train was late anyway.", "We don't need have hurried — the train was late anyway."], 0),
  ],
};

function load(name) {
  return JSON.parse(fs.readFileSync(`${DIR}/${name}.json`, "utf8"));
}

function save(name, root) {
  fs.writeFileSync(`${DIR}/${name}.json`, JSON.stringify(root, null, 2) + "\n");
}

const exercisesRoot = load("exercises");
const prefixFor = (lessonId) => lessonId.replace(/\.lesson-\d+$/, "");
const usedIds = new Set(exercisesRoot.exercises.map((e) => e.id));

let added = 0;
for (const [lessonId, items] of Object.entries(plan)) {
  const existing = exercisesRoot.exercises.filter((e) => e.lessonId === lessonId);
  if (existing.length === 0) throw new Error(`unknown lesson ${lessonId} — run a9_add_grammar_lessons.mjs first`);
  let order = Math.max(...existing.map((e) => e.order));
  const takenOrders = new Set(existing.map((e) => e.order));
  const prefix = prefixFor(lessonId);

  for (const item of items) {
    order += 1;
    while (takenOrders.has(order)) order += 1;
    takenOrders.add(order);

    let id;
    for (let n = 1; n < 99; n += 1) {
      const candidate = `${prefix}.ex.${String(n).padStart(2, "0")}`;
      if (!usedIds.has(candidate)) {
        id = candidate;
        break;
      }
    }
    usedIds.add(id);

    exercisesRoot.exercises.push({ id, lessonId, order, ...item });
    added += 1;
  }
}

save("exercises", exercisesRoot);

const grammar = exercisesRoot.exercises.filter((e) => e.skill === "GRAMMAR" || e.type === "fill_blank").length;
console.log(`exercises +${added} (total ${exercisesRoot.exercises.length}; grammar-targeting ${grammar})`);
