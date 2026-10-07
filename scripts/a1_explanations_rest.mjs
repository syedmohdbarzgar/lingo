// A-1 follow-through (checklist v2): the grammar/fill-blank backlog is closed in
// `a1_explanations.mjs`; this batch covers everything else so **every exercise in
// the bundle explains its answer** — vocabulary meaning questions, Persian→English
// translations and listening comprehension.
//
// Run:  node scripts/a1_explanations_rest.mjs
import { readFileSync, writeFileSync } from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const NEW_VERSION = 11;

const explanations = {
  // ---- vocabulary meaning questions ---------------------------------------
  "a1.greetings.ex.01": "thank you برای تشکر است؛ ببخشید sorry و لطفاً please است.",
  "a1.greetings.ex.02": "Good morning فقط برای صبح است؛ Good night هنگام خواب و Good evening شب به کار می‌رود.",
  "a1.greetings.ex.06": "please واژهٔ ادب در درخواست است؛ Give me بدون please خودخواهانه و بی‌ادبانه شنیده می‌شود.",
  "a1.daily-routine.ex.01": "wake up یعنی بیدار شدن؛ صبحانه خوردن have breakfast است.",
  "a1.family.ex.01": "sister یعنی خواهر؛ برادر brother است.",
  "a2.airport.ex.01": "boarding pass کارت پرواز است که پیش از سوار شدن نشان داده می‌شود.",
  "a2.airport.ex.02": "departure یعنی حرکت/پرواز (خروج)؛ نقطهٔ مقابل آن arrival (ورود) است.",
  "a2.restaurant.ex.01": "bill صورت‌حساب رستوران است؛ menu فهرست غذاست.",
  "a2.restaurant.ex.02": "درخواست مؤدبانه با Can we have …, please? ساخته می‌شود؛ Give the bill! امری و بی‌ادبانه است.",
  "a1.greetings.ex.07": "hello یعنی سلام — رایج‌ترین درودِ غیررسمی برای شروع گفت‌وگو.",
  "a1.daily-routine.ex.07": "wake up یعنی بیدار شدن.",
  "a1.family.ex.07": "mother یعنی مادر؛ نقطهٔ مقابلش father (پدر) است.",
  "a1.shopping.ex.07": "price یعنی قیمت، بهایی که برای خرید می‌پردازی.",
  "a2.airport.ex.07": "passport یعنی گذرنامه.",
  "a2.restaurant.ex.07": "menu یعنی فهرست غذا.",
  "a2.directions.ex.07": "straight یعنی مستقیم.",
  "a2.health.ex.07": "headache یعنی سردرد.",
  "b1.work.ex.07": "career یعنی حرفه و مسیر شغلی، نه شغل روزمزد.",
  "b1.news.ex.07": "headline یعنی تیتر اصلی خبر.",
  "b1.plans.ex.07": "decide یعنی تصمیم گرفتن.",
  "b1.experiences.ex.07": "journey یعنی سفر و مسیر.",
  "b2.technology.ex.07": "innovation یعنی نوآوری.",
  "b2.environment.ex.07": "pollution یعنی آلودگی.",
  "b2.interview.ex.07": "candidate یعنی داوطلب/کاندیدا.",
  "b2.media.ex.07": "advertising یعنی تبلیغات.",
  "c1.academic.ex.07": "hypothesis یعنی فرضیه — گزاره‌ای که با آزمایش آزموده می‌شود.",
  "c1.persuasion.ex.07": "counterargument یعنی استدلال متقابل، پاسخ به استدلال مقابل.",
  "c1.culture.ex.07": "cinematic یعنی سینمایی — کیفیتی که یادآور فیلم است.",
  "c1.phrasal-idioms.ex.07": "give up یعنی دست کشیدن و رها کردن.",
  "c2.nuance.ex.07": "ambiguous یعنی مبهم و دوپهلو.",
  "c2.rhetoric.ex.07": "eloquence یعنی بلاغت و فصاحت در سخن.",
  "c2.science.ex.07": "empirical یعنی تجربی و مبتنی بر مشاهده، نه نظری محض.",
  "c2.literature.ex.07": "narrator یعنی راوی داستان.",
  "a1.introductions.ex.02": "name یعنی نام؛ برای پرسیدن نام What is your name? می‌گوییم.",
  "a1.weather.ex.01": "«ابرهای خاکستری در آسمان» آب‌وهوای cloudy (ابری) را می‌رساند.",
  "a1.weather.ex.02": "season یعنی فصل سال: بهار، تابستان، پاییز و زمستان.",
  "a2.hobbies.ex.02": "hobby یعنی سرگرمی، نه شغل.",
  "a2.city-life.ex.02": "crowded یعنی شلوغ؛ متضادش quiet (خلوت) است.",
  "a2.city-life.ex.06": "مترو برای رفتن به مرکز شهر «مناسب و راحت» است، پس convenient می‌آید.",
  "b1.money.ex.06": "income یعنی درآمد (پولی که وارد می‌شود)؛ expense نقطهٔ مقابل آن است.",
  "b1.health-lifestyle.ex.02": "a balanced diet یعنی رژیم غذایی متعادل، نه رژیم سخت.",
  "b2.problems.ex.02": "refund یعنی بازپرداخت پول.",
  "b2.research.ex.02": "bias یعنی سوگیری — انحرافی که نتیجه را ناعادلانه می‌کند.",
  "c1.register.ex.02": "register در زبان‌شناسی یعنی سبک زبانی متناسب با موقعیت.",
  "c1.register.ex.06": "تبدیل decide (فعل) به decision (اسم) اسم‌سازی یا nominalisation است.",
  "c1.cohesion.ex.02": "discourse marker واژه‌ای است که رابطهٔ میان ایده‌ها را نشان می‌دهد، مانند however.",
  "c1.cohesion.ex.06": "حذف واژه‌های تکراری که خواننده می‌تواند تأمین کند ellipsis است.",
  "c2.argumentation.ex.06": "جملهٔ قاطع و بدون اثبات assertion است.",
  "c2.collocation.ex.02": "collocation یعنی هم‌نشینی طبیعی واژه‌ها.",
  "c2.irony.ex.01": "توصیف طوفان با «کمی باد» کم‌گویی (understatement) است.",
  "c2.irony.ex.02": "not uncommon در معنای fairly common نمونهٔ litotes است: کم‌گویی با نفیِ متضاد.",

  // ---- Persian → English translation --------------------------------------
  "a1.greetings.ex.04": "عبارت ثابت Nice to meet you برای ابراز خوش‌حالی از آشنایی است.",
  "a1.daily-routine.ex.04": "قید تکرار usually پیش از فعل اصلی می‌آید: I usually wake up.",
  "a1.family.ex.04": "با فاعل I فعل have بدون s می‌آید: I have a brother.",
  "a2.airport.ex.04": "درخواست مؤدبانه با please + فعل ساده: Please show me your passport.",
  "a2.restaurant.ex.04": "this soup مفرد است، پس فعل is می‌گیرد: This soup is delicious.",
  "a1.shopping.ex.05": "پرسش قیمت با How much is …? ساخته می‌شود.",
  "a2.directions.ex.05": "opposite/across from یعنی «روبروی»: The bus stop is opposite the bank.",
  "a2.health.ex.05": "قید yesterday جمله را گذشته می‌کند، پس فعل گذشتهٔ ساده است: I went.",
  "b1.work.ex.05": "three years ago نقطهٔ زمانی گذشته است و فعل گذشتهٔ ساده می‌گیرد: I left.",
  "b1.news.ex.05": "مجهول گذشته: was written by … — کتاب مفعولِ عمل است.",
  "b1.plans.ex.05": "شرطی نوع دوم: if + گذشتهٔ ساده، نتیجه با would + فعل ساده.",
  "b1.experiences.ex.05": "عملی که پیش از رویداد گذشته کامل شده بود با ماضی بعید می‌آید: had left.",
  "b2.technology.ex.05": "اثر تا امروز ادامه دارد، پس حال کامل: has transformed.",
  "b2.environment.ex.05": "It seems that + جمله برای «به نظر می‌رسد»؛ تغییر در جریان است: is changing.",
  "b2.interview.ex.05": "نقل‌قول غیرمستقیم: he asked why I was interested… — فعل یک پله عقب می‌رود.",
  "b2.media.ex.05": "wish + ماضی بعید برای حسرت گذشته: I wish I had watched…",
  "c1.academic.ex.05": "مجهول گذشته: was questioned — فاعل خودِ فرضیه است.",
  "c1.persuasion.ex.05": "should never have + قسمت سوم برای پشیمانی: I should never have missed…",
  "c1.culture.ex.05": "if only + ماضی بعید برای حسرت گذشته: if only I had started earlier.",
  "c1.phrasal-idioms.ex.05": "turn down یعنی «رد کردن»: We turned down the offer.",
  "c2.nuance.ex.05": "subtle difference/distinction یعنی «تفاوت ظریف».",
  "c2.rhetoric.ex.05": "so + صفت + that برای بیان نتیجه: so eloquent that it captivated everyone.",
  "c2.science.ex.05": "فاعل this hypothesis مفرد است، پس فعل s می‌گیرد: needs more evidence.",
  "c2.literature.ex.05": "ironic یعنی کنایه‌آمیز — صفت مناسب برای توصیف پایان داستان.",
  "a1.introductions.ex.04": "My name is … ساختار ثابت معرفی نام است.",
  "a1.weather.ex.04": "صفت پس از فعل be می‌آید: The weather is cold today.",
  "a2.hobbies.ex.04": "in my spare time یعنی «در وقت آزادم».",
  "a2.city-life.ex.04": "quiet/calm یعنی آرام؛ فاعل this neighbourhood مفرد است.",
  "b1.money.ex.04": "pay off یعنی «تسویه کردن» بدهی: paid off his debt.",
  "b1.health-lifestyle.ex.04": "قید تکرار every day در پایان جمله می‌آید: I exercise every day.",
  "b2.problems.ex.04": "مجهول گذشته: was resolved within two days.",
  "b2.research.ex.04": "the evidence غیرقابل‌شمارش و مفرد است: does not support.",
  "c1.inference.ex.04": "must have + قسمت سوم برای استنتاج قطعی در گذشته: must have forgotten.",
  "c1.register.ex.04": "مجهول گذشته و بی‌طرف: was criticised — مناسب سبک رسمی.",
  "c1.cohesion.ex.04": "however برای تضاد میان دو جمله می‌آید.",
  "c2.argumentation.ex.04": "follow from یعنی «نتیجه گرفتن از»: does not follow from the premises.",
  "c2.collocation.ex.04": "معنا از هم‌نشینی می‌آید، پس با not from the words alone تأکید می‌شود.",
  "c2.irony.ex.04": "subtext یعنی «معنای پنهان»: the subtext of this scene is that…",

  // ---- listening comprehension --------------------------------------------
  "a1.greetings.ex.05": "عبارت تشکر: Thank you for your help — تکیهٔ جمله روی help است.",
  "a1.daily-routine.ex.05": "به /w/ در watch و واکهٔ بلند /iː/ در evening گوش کن.",
  "a1.family.ex.05": "به /ɑː/ در father و /æ/ در bank گوش کن.",
  "a2.airport.ex.05": "gate twelve با تکیه روی twelve و پیوند صداها تلفظ می‌شود.",
  "a2.restaurant.ex.05": "در گفتار سریع can به /kən/ ضعیف تبدیل می‌شود.",
  "a1.shopping.ex.06": "به تکیهٔ supermarket و پیوند until nine گوش کن.",
  "a2.directions.ex.06": "به /ɜː/ در turning و تکیهٔ second گوش کن.",
  "a2.health.ex.06": "به /p/ در pill و /br/ در breakfast گوش کن.",
  "b1.work.ex.06": "در گفتار سریع have به /əv/ ضعیف تبدیل می‌شود.",
  "b1.news.ex.06": "مجهول آینده در گفتار: will be opened با تکیه روی opened.",
  "b1.plans.ex.06": "going to در گفتار سریع به gonna تبدیل می‌شود.",
  "b1.experiences.ex.06": "به پیوند watching TV و تکیهٔ lights گوش کن.",
  "b2.technology.ex.06": "به تکیهٔ intelligence و industry گوش کن.",
  "b2.environment.ex.06": "به /riːˈsaɪklɪŋ/ و can ضعیف گوش کن.",
  "b2.interview.ex.06": "to be in charge of یعنی «مسئول بودن» و در گفتار پیوسته تلفظ می‌شود.",
  "b2.media.ex.06": "has been در گفتار به /həz bɪn/ ضعیف می‌شود.",
  "c1.academic.ex.06": "be subject to یعنی «مورد ... قرار گرفتن»؛ تکیه روی considerable است.",
  "c1.persuasion.ex.06": "شرطی نوع سوم با وارونگی: Had they known … در آغاز جمله.",
  "c1.culture.ex.06": "ساختار مقایسهٔ منفی: not as good as I had expected.",
  "c1.phrasal-idioms.ex.06": "end up یعنی «سر از جایی درآوردن».",
  "c2.nuance.ex.06": "deliberately یعنی «عمداً»؛ تکیهٔ جمله روی vague است.",
  "c2.rhetoric.ex.06": "mask یعنی «پنهان کردن»؛ تکیه روی striking است.",
  "c2.science.ex.06": "corroborate یعنی «تأیید کردن» — یافته‌ها فرضیهٔ اولیه را تأیید می‌کنند.",
  "c2.literature.ex.06": "ambiguity یعنی «ابهام»؛ تکیهٔ جمله روی explores است.",
  "a1.introductions.ex.05": "به /aɪ/ در I و تکیهٔ Iran گوش کن.",
  "a1.weather.ex.05": "به /eɪ/ در raining و /ɪ/ کوتاه در wind گوش کن.",
  "a2.hobbies.ex.05": "به /tʃ/ در chess و تکیهٔ member گوش کن.",
  "a2.city-life.ex.05": "heavy traffic یعنی «ترافیک سنگین» و در گفتار پیوسته تلفظ می‌شود.",
  "b1.money.ex.05": "به تکیهٔ biggest و expense گوش کن.",
  "b1.health-lifestyle.ex.05": "به /ɑː/ در adults و پیوند eight hours گوش کن.",
  "b2.problems.ex.05": "has not been در گفتار سریع به hasn't been تبدیل می‌شود.",
  "b2.research.ex.05": "به تکیهٔ suggest و شکل جمعِ data گوش کن.",
  "c1.inference.ex.05": "به /ɪmˈplaɪz/ در implies و تکیهٔ serious گوش کن.",
  "c1.register.ex.05": "at your earliest convenience عبارت رسمیِ «در اسرع وقت» است.",
  "c1.cohesion.ex.05": "far from conclusive یعنی «به‌هیچ‌وجه قاطع نیست».",
  "c2.argumentation.ex.05": "concede یعنی «پذیرفتن» و move on to یعنی «پرداختن به».",
  "c2.collocation.ex.05": "a strong collocation یعنی هم‌نشینی طبیعی واژه‌ها که برای natives طبیعی به گوش می‌رسد.",
  "c2.irony.ex.05": "self-deprecating یعنی طنز به خود؛ put everyone at ease یعنی «همه را آرام کردن».",
};

const read = (name) => JSON.parse(readFileSync(`${DIR}/${name}.json`, "utf8"));
const write = (name, data) => writeFileSync(`${DIR}/${name}.json`, JSON.stringify(data, null, 2) + "\n");

const exercises = read("exercises");
const knownIds = new Set(exercises.exercises.map((e) => e.id));
const unknown = Object.keys(explanations).filter((id) => !knownIds.has(id));
if (unknown.length) throw new Error(`explanations target unknown exercises: ${unknown.join(", ")}`);

let added = 0;
for (const ex of exercises.exercises) {
  const text = explanations[ex.id];
  if (!text) continue;
  if (ex.explanationFa && ex.explanationFa.trim()) continue; // keep authored text
  ex.explanationFa = text;
  added++;
}

for (const name of FILES) {
  const data = name === "exercises" ? exercises : read(name);
  data.contentVersion = NEW_VERSION;
  write(name, data);
}

const total = exercises.exercises.length;
const withExp = exercises.exercises.filter((e) => e.explanationFa && e.explanationFa.trim()).length;
console.log(`A-1: added ${added} explanations; ${withExp}/${total} exercises now explain a miss.`);
console.log(`contentVersion → ${NEW_VERSION} in ${FILES.join(", ")}.`);
