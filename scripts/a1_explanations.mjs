// A-1 (checklist v2): Persian explanations after a mistake.
// Adds `explanationFa` to every exercise the production rule classifies as
// GRAMMAR (AnswerChecker.skillOf) — all explicit GRAMMAR tags plus every
// fill_blank. Idempotent: existing authored explanations are left untouched.
//
// Run:  node scripts/a1_explanations.mjs
import { readFileSync, writeFileSync } from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const NEW_VERSION = 10;

/** id → one/two-sentence Persian explanation of why the answer is right. */
const explanations = {
  // ---- A1 greetings -------------------------------------------------------
  "a1.greetings.ex.03": "برای پرسیدن حال از how are you? استفاده می‌شود؛ ترتیب ثابت how + are + you است.",

  // ---- A1 daily routine ---------------------------------------------------
  "a1.daily-routine.ex.02": "در زمان حال ساده با فاعل I فعل پایه می‌آید، بدون s: I usually drink coffee.",
  "a1.daily-routine.ex.03": "برای کارهای روزمره زمان حال ساده به کار می‌رود: I go to work — فعل am با فعل اصلی جمع نمی‌شود.",
  "a1.daily-routine.ex.06": "هم‌نشینی درست take a shower است؛ اسم قابل‌شمارش مفرد با a می‌آید.",
  "a1.daily-routine.ex.09": "فاعل سوم‌شخص مفرد (my brother) در حال ساده s می‌گیرد: works.",
  "a1.daily-routine.ex.10": "با she در حال ساده فعل s/‑es می‌گیرد: she goes.",
  "a1.daily-routine.ex.11": "منفی حال ساده برای they با don't و فعل ساده ساخته می‌شود، نه doesn't و نه s روی فعل.",

  // ---- A1 family ----------------------------------------------------------
  "a1.family.ex.02": "همسرِ مادر (mother's husband) پدر است؛ بقیهٔ گزینه‌ها نسبت خانوادگی را اشتباه می‌گویند.",
  "a1.family.ex.03": "مادر در انگلیسی mother است و با my مالکیت نشان داده می‌شود.",
  "a1.family.ex.06": "با فاعل they فعل have بدون s می‌آید و have با are جمع نمی‌شود: They have one son.",

  // ---- A2 airport ---------------------------------------------------------
  "a2.airport.ex.03": "over there یعنی «آنجا، آن‌طرف»؛ there قید مکان است.",
  "a2.airport.ex.06": "برای برنامهٔ زمان‌بندی‌شده حال ساده به کار می‌رود و فاعل مفرد the flight فعل s می‌گیرد: departs.",

  // ---- A2 restaurant ------------------------------------------------------
  "a2.restaurant.ex.03": "بعد از would like to مصدر بدون to می‌آید: would like to order chicken.",
  "a2.restaurant.ex.06": "رزرو میز در رستوران reservation است؛ بقیهٔ گزینه‌ها با بافت رستوران نمی‌خوانند.",
  "a2.restaurant.ex.08": "ساختار درست would like + اسم است؛ to پیش از like می‌آید، نه پیش از اسم.",
  "a2.restaurant.ex.09": "بعد از would like مصدر با to می‌آید: would like to order.",
  "a2.restaurant.ex.10": "در جملهٔ منفی any می‌آید و هیچ دو منفی‌ای (isn't + no) جمع نمی‌شود: isn't any sugar.",
  "a2.restaurant.ex.11": "در جملهٔ منفی با don't از any استفاده می‌شود، نه some و نه منفی مضاعف no.",

  // ---- A1 shopping --------------------------------------------------------
  "a1.shopping.ex.01": "فاعل جمع است (these shoes)، پس فعل are می‌آید و much برای مقدار غیرقابل‌شمارش به کار می‌رود.",
  "a1.shopping.ex.02": "a برای اسم قابل‌شمارش مفرد می‌آید: a kilo of apples.",
  "a1.shopping.ex.03": "«فقط پنج پوند» قیمت پایین را نشان می‌دهد، پس صفت مناسب cheap است.",
  "a1.shopping.ex.04": "water غیرقابل‌شمارش است، پس how much می‌آید نه how many.",
  "a1.shopping.ex.09": "how many با اسم جمع و فعل جمع می‌آید: How many shoes are in the box?",
  "a1.shopping.ex.10": "this shirt مفرد است، پس فعل is می‌آید؛ how much برای پرسیدن قیمت به کار می‌رود.",

  // ---- A2 directions ------------------------------------------------------
  "a2.directions.ex.01": "go straight ahead عبارت ثابتِ «مستقیم جلو برو» است.",
  "a2.directions.ex.02": "between برای «بین دو چیز» به کار می‌رود و با and می‌آید.",
  "a2.directions.ex.03": "در انگلیسی بریتانیایی turn right at the second traffic light رایج است؛ at نقطهٔ مشخص را نشان می‌دهد.",
  "a2.directions.ex.04": "walk down/along this street یعنی «در این خیابان پیش برو».",
  "a2.directions.ex.08": "بین دو مکان با between … and می‌آید؛ among برای بیش از دو چیز است.",
  "a2.directions.ex.09": "at برای نقطهٔ مشخص (چراغ راهنما) به کار می‌رود، نه on/in/to.",

  // ---- A2 health ----------------------------------------------------------
  "a2.health.ex.01": "بعد از have to فعل ساده می‌آید: have to take this medicine.",
  "a2.health.ex.02": "جمله گذشته است (stayed)، پس فعل to be نیز گذشته می‌آید و با she می‌شود was.",
  "a2.health.ex.03": "نیاز به آسپرین نشانهٔ headache (سردرد) است.",
  "a2.health.ex.04": "بعد از should فعل ساده می‌آید: should drink plenty of water.",
  "a2.health.ex.08": "should + فعل ساده می‌آید، بدون to و بدون ing.",
  "a2.health.ex.09": "منفی should با shouldn't + فعل ساده ساخته می‌شود و فعل s نمی‌گیرد.",

  // ---- B1 work ------------------------------------------------------------
  "b1.work.ex.01": "since 2019 نقطهٔ شروع است و با حال کامل می‌آید: have worked.",
  "b1.work.ex.02": "در نقل‌قول غیرمستقیم گذشته فعل یک پله عقب می‌رود: finish → had finished.",
  "b1.work.ex.03": "در تجربهٔ زندگی با never از حال کامل استفاده می‌شود: have never been to Turkey.",
  "b1.work.ex.04": "پرسش گذشتهٔ ساده با did + فاعل + فعل ساده ساخته می‌شود: When did you apply?",
  "b1.work.ex.09": "since + نقطهٔ شروع (2021) با حال کامل می‌آید؛ for برای طول مدت است.",

  // ---- B1 news ------------------------------------------------------------
  "b1.news.ex.01": "مجهول گذشتهٔ ساده: was/were + قسمت سوم فعل — قانون مفعولِ عمل است، نه فاعل.",
  "b1.news.ex.02": "due to + اسم (the heavy rain) رابطهٔ علت را نشان می‌دهد؛ although با یک جملهٔ کامل می‌آید.",
  "b1.news.ex.03": "مجهول گذشتهٔ ساده: was built.",
  "b1.news.ex.04": "the news مفرد است و برای برنامهٔ عادتی حال ساده به کار می‌رود: is broadcast.",
  "b1.news.ex.08": "قسمت سوم فعل find در معنای «بنیان نهادن» founded است؛ was founding غلط است.",
  "b1.news.ex.09": "مجهول گذشتهٔ ساده: was built/was completed.",

  // ---- B1 plans -----------------------------------------------------------
  "b1.plans.ex.01": "شرطی نوع دوم: در جملهٔ if فعل گذشتهٔ ساده و در نتیجه would + فعل ساده می‌آید.",
  "b1.plans.ex.02": "this time next week آیندهٔ در جریان را می‌رساند: will be lying.",
  "b1.plans.ex.03": "شرطی نوع دوم در جملهٔ نتیجه would + فعل ساده می‌آید: would pass.",
  "b1.plans.ex.04": "برنامهٔ آینده با be going to + فعل ساده: going to take.",
  "b1.plans.ex.08": "برای برنامهٔ آینده be going to + فعل ساده می‌آید و بخش go حذف نمی‌شود.",
  "b1.plans.ex.09": "شرطی نوع دوم: if + گذشتهٔ ساده، نتیجه با would + فعل ساده (would travel).",

  // ---- B1 experiences -----------------------------------------------------
  "b1.experiences.ex.01": "by the time + گذشته، فعل بعدی ماضی بعید می‌شود: had started.",
  "b1.experiences.ex.02": "while با گذشتهٔ استمراری می‌آید: was cooking، چون عمل ادامه داشت که تلفن زنگ زد.",
  "b1.experiences.ex.03": "had already left ماضی بعید است، برای عملی که پیش از رویدادِ گذشته انجام شده بود.",
  "b1.experiences.ex.04": "گذشتهٔ استمراری با was/were + ing ساخته می‌شود: were you doing.",
  "b1.experiences.ex.08": "عملی در جریان (was + ing) که فعل دیگری آن را قطع می‌کند.",
  "b1.experiences.ex.09": "قبل از رویداد گذشته، فعل مقدم با ماضی بعید می‌آید: had finished.",

  // ---- B2 technology ------------------------------------------------------
  "b2.technology.ex.01": "برای اشاره به یک شیء (the device) از that استفاده می‌شود، نه who که برای انسان است.",
  "b2.technology.ex.02": "data غیرقابل‌شمارش و مفرد است، پس مجهول با is stored می‌آید.",
  "b2.technology.ex.03": "ضمیر موصولی برای شیء that/which است: the company that I work for.",
  "b2.technology.ex.04": "مجهول با be + قسمت سوم فعل: can be used.",
  "b2.technology.ex.09": "برای شخص از who استفاده می‌شود، نه which/whom.",
  "b2.technology.ex.10": "پس از فعل وجهی (must) مجهول با be + قسمت سوم ساخته می‌شود: must be finished.",

  // ---- B2 environment -----------------------------------------------------
  "b2.environment.ex.01": "استنتاج منطقی از شواهد (بوی بد) با must بیان می‌شود: there must be a factory.",
  "b2.environment.ex.02": "برای «غیرممکن بودن» از can't have + قسمت سوم استفاده می‌شود: can't have missed.",
  "b2.environment.ex.03": "بعد از might فعل ساده می‌آید: might rise.",
  "b2.environment.ex.04": "شرطی نوع دوم: if + گذشتهٔ ساده، نتیجه با would + فعل ساده.",
  "b2.environment.ex.08": "بعد از must فعل ساده می‌آید؛ must to/been/being درست نیست.",
  "b2.environment.ex.09": "شرطی نوع دوم با were برای همهٔ فاعل‌ها و would + فعل ساده در نتیجه.",

  // ---- B2 interview -------------------------------------------------------
  "b2.interview.ex.01": "در نقل‌قول غیرمستقیم گذشته، فعل یک پله عقب می‌رود: find → had found.",
  "b2.interview.ex.02": "get along with یعنی «کنار آمدن و رابطهٔ خوب داشتن».",
  "b2.interview.ex.03": "بعد از look forward to همیشه ing می‌آید: hearing from you.",
  "b2.interview.ex.04": "مصاحبهٔ کوتاه پیش از استخدام interview است.",
  "b2.interview.ex.08": "در نقل‌قول غیرمستقیم فاعل و فعل جابه‌جا نمی‌شوند و فعل به ماضی بعید می‌رود.",
  "b2.interview.ex.09": "در نقل‌قول غیرمستقیم زمان فعل عقب می‌رود: will start → would start.",

  // ---- B2 media -----------------------------------------------------------
  "b2.media.ex.01": "wish + گذشتهٔ ساده برای حسرت در زمان حال: I wish I had more time.",
  "b2.media.ex.02": "used to + فعل ساده عادت گذشته را نشان می‌دهد؛ is used to معنی دیگری دارد.",
  "b2.media.ex.03": "آیندهٔ کامل: will have + قسمت سوم فعل (completed).",
  "b2.media.ex.04": "هدف آگهی، معرفی و فروش محصول است: to promote a new product.",
  "b2.media.ex.08": "عادت گذشته با used to + فعل ساده بیان می‌شود؛ هرگز used to + ing نمی‌آید.",
  "b2.media.ex.09": "عادت گذشته با used to/would + فعل ساده: used to play.",

  // ---- C1 academic --------------------------------------------------------
  "c1.academic.ex.01": "پس از عبارت منفی در آغاز جمله (Not until …) فعل و فاعل جابه‌جا می‌شوند: did I notice.",
  "c1.academic.ex.02": "پس از فعل وجهی (must) مجهول با be + قسمت سوم ساخته می‌شود: must be interpreted.",
  "c1.academic.ex.03": "شرطی نوع سوم با وارونگی Had + قسمت سوم: نتیجه would have been.",
  "c1.academic.ex.04": "data جمع است، پس مجهول جمع می‌آید: the data were collected.",

  // ---- C1 persuasion ------------------------------------------------------
  "c1.persuasion.ex.01": "Rarely در آغاز جمله وارونگی می‌سازد: Rarely have I heard.",
  "c1.persuasion.ex.02": "should have + قسمت سوم برای سرزنش کاری که انجام نشده: should have told me.",
  "c1.persuasion.ex.03": "must have + قسمت سوم برای استنتاج قوی از شواهد: must have forgotten.",
  "c1.persuasion.ex.04": "Little در آغاز جمله وارونگی می‌سازد: Little did he know.",
  "c1.persuasion.ex.08": "should have + قسمت سوم فعل برای پشیمانی از کار انجام‌نشده.",
  "c1.persuasion.ex.09": "needn't have + قسمت سوم یعنی کار انجام شد اما بیهوده بود.",

  // ---- C1 culture ---------------------------------------------------------
  "c1.culture.ex.01": "شرطی نوع سوم: if + ماضی بعید، نتیجه با would have + قسمت سوم.",
  "c1.culture.ex.02": "پس از if only برای آرزو، گذشتهٔ ساده می‌آید و were برای همهٔ فاعل‌ها به کار می‌رود.",
  "c1.culture.ex.03": "wish + گذشتهٔ ساده برای آرزو در زمان حال: I wish I knew.",
  "c1.culture.ex.04": "as if + گذشتهٔ ساده گویای امری خلاف واقع است.",

  // ---- C1 phrasal idioms --------------------------------------------------
  "c1.phrasal-idioms.ex.01": "put off یعنی «به تعویق انداختن»؛ سایر ترکیب‌های put معنی متفاوتی دارند.",
  "c1.phrasal-idioms.ex.02": "make out یعنی «فهمیدن و تشخیص دادن».",
  "c1.phrasal-idioms.ex.03": "give up یعنی «رها کردن، کنار گذاشتن»: gave up her old job.",
  "c1.phrasal-idioms.ex.04": "turn on the lights یعنی «چراغ را روشن کردن».",

  // ---- C2 nuance ----------------------------------------------------------
  "c2.nuance.ex.01": "ambiguous یعنی «مبهم و دوپهلو» — همان چیزی که با «هیچ‌کس نفهمید چه می‌گوید» می‌آید.",
  "c2.nuance.ex.02": "distinction یعنی «تفاوت و تمایز» و با between می‌آید.",
  "c2.nuance.ex.03": "تضادِ «concise but» واژهٔ vague (مبهم) را می‌طلبد.",
  "c2.nuance.ex.04": "شاهد ناکافی برای ادعای قوی: too slight/flimsy.",

  // ---- C2 rhetoric --------------------------------------------------------
  "c2.rhetoric.ex.01": "refute یعنی «رد کردن استدلال»؛ بقیهٔ گزینه‌ها با logic نمی‌خوانند.",
  "c2.rhetoric.ex.02": "بعد از صفت ملکی its اسم می‌آید: its concision (فشردگی).",
  "c2.rhetoric.ex.03": "Never در آغاز جمله وارونگی می‌سازد: Never have I witnessed.",
  "c2.rhetoric.ex.04": "نتیجهٔ سخنرانی فصیح، مبهوت و بی‌سخن ماندنِ شنونده است: left the audience speechless.",

  // ---- C2 science ---------------------------------------------------------
  "c2.science.ex.01": "consistent with یعنی «سازگار با»؛ resistant/insistent این معنی را با with نمی‌دهند.",
  "c2.science.ex.02": "بعد از حرف اضافهٔ of اسم می‌آید: the subject of speculation.",
  "c2.science.ex.03": "پژوهش بیشتر برای تأیید نظریه است: to confirm/substantiate the theory.",
  "c2.science.ex.04": "نتیجهٔ قابل بازتولید با اختلاف‌های جزئی: replicated.",

  // ---- C2 literature ------------------------------------------------------
  "c2.literature.ex.01": "پایانِ بازِ رمان با ambiguous توصیف می‌شود؛ definite نقطهٔ مقابل آن است.",
  "c2.literature.ex.02": "لحنِ متناقض با ستایش ظاهری ironic است.",
  "c2.literature.ex.03": "depict/portray یعنی «به تصویر کشیدن»: depicts the plight of the urban poor.",
  "c2.literature.ex.04": "allegory یعنی «تمثیل» — تفسیر نمادین شکست شخصیت اصلی.",

  // ---- A1 introductions ---------------------------------------------------
  "a1.introductions.ex.03": "Nice to meet you عبارت ثابتِ آشنایی است.",
  "a1.introductions.ex.08": "بعد از This is صفت ملکی می‌آید: my book — نه I/mine/me.",
  "a1.introductions.ex.09": "صفت ملکی her پیش از اسم می‌آید و فعل با name مفرد، is است.",
  "a1.introductions.ex.10": "صفت ملکی our + اسم می‌آید؛ فعل با teacher سوم‌شخص مفرد s می‌گیرد: speaks.",

  // ---- A1 weather ---------------------------------------------------------
  "a1.weather.ex.08": "برای بارش در همین لحظه حال استمراری به کار می‌رود: is raining.",
  "a1.weather.ex.09": "حال استمراری: is + ing — خوابِ کودک همین حالا در جریان است.",
  "a1.weather.ex.10": "حال استمراری با فاعل I: am studying.",
  "a1.weather.ex.11": "she + is + ing ساختار حال استمراری است.",
  "a1.weather.ex.12": "حال استمراری با am + ing؛ get dressed فعل مرکب به معنای «لباس پوشیدن» است.",

  // ---- A2 hobbies ---------------------------------------------------------
  "a2.hobbies.ex.03": "بعد از want مصدر با to می‌آید: wants to learn.",
  "a2.hobbies.ex.07": "بعد از enjoy همیشه ing می‌آید: enjoy reading.",
  "a2.hobbies.ex.08": "بعد از decide مصدر با to می‌آید: decided to travel.",
  "a2.hobbies.ex.09": "بعد از like و hate هر دو ing می‌آید: likes swimming / hates running.",

  // ---- A2 city life -------------------------------------------------------
  "a2.city-life.ex.03": "با اسم جمع (two parks) فعل جمع are می‌آید.",
  "a2.city-life.ex.07": "many parks جمع است، پس there are می‌آید.",
  "a2.city-life.ex.08": "a new library مفرد است، پس there is می‌آید.",
  "a2.city-life.ex.09": "صفات بلند (expensive) با more مقایسه می‌شوند: more expensive than.",
  "a2.city-life.ex.10": "worse خودش صفت تفضیلی بی‌قاعده است و more نمی‌گیرد.",

  // ---- B1 money -----------------------------------------------------------
  "b1.money.ex.07": "for + طول مدت (two years) با حال کامل می‌آید، نه since که برای نقطهٔ شروع است.",

  // ---- B1 health lifestyle ------------------------------------------------
  "b1.health-lifestyle.ex.07": "بعد از ought مصدر با to می‌آید و فعل ساده می‌ماند: ought to eat.",
  "b1.health-lifestyle.ex.08": "بعد از had better فعل ساده می‌آید، بدون to و بدون ing.",
  "b1.health-lifestyle.ex.09": "ought to + فعل ساده: ought to walk.",
  "b1.health-lifestyle.ex.10": "بعد از had better فعل ساده می‌آید: had better wear a coat.",

  // ---- B2 problems --------------------------------------------------------
  "b2.problems.ex.07": "مجهول حال کامل: has been + قسمت سوم فعل — the issue has been resolved.",
  "b2.problems.ex.08": "فاعل جمع است و مجهول حال کامل با have been + قسمت سوم ساخته می‌شود: have been sent.",
  "b2.problems.ex.09": "فاعل جمع (three complaints) با have been + قسمت سوم می‌آید: have been logged.",
  "b2.problems.ex.10": "مجهول حال کامل با since: has been repaired.",
  "b2.problems.ex.11": "مجهول حال کامل: has been approved — درخواست تأیید شده است.",

  // ---- B2 research --------------------------------------------------------
  "b2.research.ex.07": "Mathematics اسم مفرد به‌شمار می‌آید، پس فعل is می‌گیرد.",
  "b2.research.ex.08": "the news مفرد است، پس the news is surprising.",
  "b2.research.ex.09": "everybody مفرد است و فعل s می‌گیرد: everybody knows.",
  "b2.research.ex.10": "traffic غیرقابل‌شمارش و مفرد است: there is a lot of traffic.",

  // ---- C1 inference -------------------------------------------------------
  "c1.inference.ex.07": "must have + قسمت سوم برای استنتاج گذشته: must have missed.",
  "c1.inference.ex.08": "can't have + قسمت سوم برای غیرممکن دانستن رویداد گذشته: can't have finished.",
  "c1.inference.ex.09": "might have + قسمت سوم برای احتمال در گذشته: might have forgotten.",
  "c1.inference.ex.10": "must have + قسمت سوم ساختار ثابت استنتاج گذشته است: must have left.",

  // ---- A1 articles --------------------------------------------------------
  "a1.articles.ex.01": "university با صدای صامتی /j/ آغاز می‌شود، پس a می‌آید نه an.",
  "a1.articles.ex.02": "egg با صدای واکه آغاز می‌شود، پس an می‌آید.",
  "a1.articles.ex.03": "برای چیزهای یکتا (خورشید) the می‌آید و فعل با sun مفرد s می‌گیرد.",
  "a1.articles.ex.04": "water غیرقابل‌شمارش و در معنای کلی است، پس بدون حرف تعریف می‌آید.",
  "a1.articles.ex.05": "منظور دربِ همان اتاقِ مشخص است، پس the door درست است.",
  "a1.articles.ex.06": "بار اول برای معرفی a می‌آید و بار دوم برای چیزِ معلوم the: a book … The book.",
  "a1.articles.ex.08": "umbrella یعنی «چتر»؛ بقیهٔ گزینه‌ها واژه‌های مرتبط اما نادرست‌اند.",

  // ---- A1 plurals ---------------------------------------------------------
  "a1.plurals.ex.01": "جمع بی‌قاعدهٔ child، children است، نه childs؛ و فاعل جمع فعل are می‌گیرد.",
  "a1.plurals.ex.02": "برای شمارش افراد جمع بی‌قاعدهٔ child به کار می‌رود: children.",
  "a1.plurals.ex.03": "جمع box با es ساخته می‌شود: boxes of tea.",
  "a1.plurals.ex.04": "leaf با تغییر f به v جمع بسته می‌شود: leaves.",
  "a1.plurals.ex.05": "جمع بی‌قاعدهٔ woman، women است.",
  "a1.plurals.ex.06": "پس از how many اسم جمع می‌آید: how many children.",
  "a1.plurals.ex.08": "box یعنی «جعبه»؛ جمعِ آن boxes می‌شود.",

  // ---- A1 pronouns --------------------------------------------------------
  "a1.pronouns.ex.01": "فاعل he است و فعل s می‌گیرد؛ ملکیت هم با his بیان می‌شود.",
  "a1.pronouns.ex.02": "پس از فعل asked ضمیر مفعولی می‌آید: asked us.",
  "a1.pronouns.ex.03": "پس از حرف اضافهٔ to ضمیر مفعولی می‌آید: to her.",
  "a1.pronouns.ex.04": "فاعل مرکب هر دو ضمیرِ فاعلی می‌گیرد: He and I are here.",
  "a1.pronouns.ex.05": "them ضمیر مفعولیِ سوم‌شخص جمع است: We met them.",
  "a1.pronouns.ex.06": "پس از to ضمیر مفعولی می‌آید: give it to me.",
  "a1.pronouns.ex.08": "teacher یعنی «معلم».",

  // ---- A1 can -------------------------------------------------------------
  "a1.can.ex.01": "can هرگز s نمی‌گیرد و پس از آن فعل ساده می‌آید: can drive.",
  "a1.can.ex.02": "ناتوانی با can't + فعل ساده بیان می‌شود.",
  "a1.can.ex.03": "بعد از can فعل ساده و بدون to می‌آید: can swim.",
  "a1.can.ex.04": "پرسش با can: can + فاعل + فعل ساده.",
  "a1.can.ex.05": "توانایی را با can نشان می‌دهیم: we can arrive.",
  "a1.can.ex.06": "توانایی در گذشته با could + فعل ساده: could run.",
  "a1.can.ex.08": "swim یعنی «شنا کردن».",

  // ---- A1 imperatives -----------------------------------------------------
  "a1.imperatives.ex.01": "امر منفی با Don't + فعل ساده ساخته می‌شود.",
  "a1.imperatives.ex.02": "جملهٔ امری با فعل ساده در ابتدا می‌آید: Close the door.",
  "a1.imperatives.ex.03": "امر مثبت با فعل ساده می‌آید، بدون فاعل و بدون s.",
  "a1.imperatives.ex.04": "درخواست مؤدبانه: please + فعل ساده (please wait).",
  "a1.imperatives.ex.05": "پس از Don't فعل ساده می‌آید: don't touch.",
  "a1.imperatives.ex.06": "امر منفی با Don't + فعل ساده: Don't touch the wire.",
  "a1.imperatives.ex.08": "stop یعنی «توقف کردن».",

  // ---- A1 question words --------------------------------------------------
  "a1.question-words.ex.01": "پرسش wh با واژهٔ پرسشی + فعل + فاعل ساخته می‌شود: Where are you from?",
  "a1.question-words.ex.02": "پرسش زمان با when یا what time مطرح می‌شود.",
  "a1.question-words.ex.03": "پرسش با do/does ساخته می‌شود: What does this mean?",
  "a1.question-words.ex.04": "why علت را می‌پرسد.",
  "a1.question-words.ex.05": "پرسش مکان با where مطرح می‌شود.",
  "a1.question-words.ex.06": "how many + اسم جمع + فعل جمع می‌آید.",
  "a1.question-words.ex.08": "why یعنی «چرا».",

  // ---- A2 past simple -----------------------------------------------------
  "a2.past-simple.ex.01": "گذشتهٔ سادهٔ بی‌قاعدهٔ go، went است: We went.",
  "a2.past-simple.ex.02": "گذشتهٔ buy، bought است.",
  "a2.past-simple.ex.03": "منفی گذشته با didn't + فعل ساده ساخته می‌شود: didn't see.",
  "a2.past-simple.ex.04": "پرسش گذشته: Did + فاعل + فعل ساده.",
  "a2.past-simple.ex.05": "گذشتهٔ write، wrote است.",
  "a2.past-simple.ex.06": "گذشتهٔ buy، bought است و با yesterday می‌آید.",
  "a2.past-simple.ex.08": "buy یعنی «خریدن»؛ گذشتهٔ آن bought است.",

  // ---- lexical fill-blanks (tagged VOCABULARY, still a typed gap) ---------
  "a1.weather.ex.03": "«رفتن به ساحل» بافت آب‌وهوای آفتابی/گرم را می‌طلبد: It is sunny today.",
  "b1.money.ex.03": "afford یعنی «توان مالی داشتن»؛ با cannot معنیِ «امسال پول ماشین نو را نداریم» را می‌دهد.",
  "b1.health-lifestyle.ex.03": "reduce/lower stress یعنی «کاهش دادنِ استرس» — کمکِ ورزش به همین کار است.",
  "b2.problems.ex.03": "make a complaint هم‌نشینی درستِ «شکایت کردن» است؛ comment (نظر) بار شکایت ندارد.",
  "b2.research.ex.03": "sample یعنی «نمونه»؛ نمونهٔ کوچک نتیجهٔ غیرقابل‌اعتماد می‌دهد.",
  "c1.inference.ex.03": "imply یعنی «به‌طور غیرمستقیم رساندن»؛ سکوتِ او همین کار را کرد، نه اینکه صریح بگوید.",
  "c1.register.ex.03": "hedge یعنی «کلام را محتاطانه/مشروط کردن» با may و tends to — نشانهٔ سبک آکادمیک.",
  "c1.cohesion.ex.03": "antecedent یعنی «مرجعِ ضمیر» — اسمی که it به آن برمی‌گردد.",
  "c2.argumentation.ex.03": "refute یعنی «رد کردن»؛ دادهٔ تازه توضیح قبلی را رد می‌کند.",
  "c2.collocation.ex.03": "take responsibility هم‌نشینی درستِ «مسئولیت پذیرفتن» است؛ do/make در این ترکیب نمی‌آید.",
  "c2.irony.ex.03": "understatement یعنی «کم‌گویی»؛ گفتن not bad در معنای very good نمونهٔ آن است.",
};

const read = (name) => JSON.parse(readFileSync(`${DIR}/${name}.json`, "utf8"));
const write = (name, data) => writeFileSync(`${DIR}/${name}.json`, JSON.stringify(data, null, 2) + "\n");

// 1. apply explanations (idempotent: existing authored text is never touched)
const exercises = read("exercises");
const knownIds = new Set(exercises.exercises.map((e) => e.id));
const unknown = Object.keys(explanations).filter((id) => !knownIds.has(id));
if (unknown.length) throw new Error(`explanations target unknown exercises: ${unknown.join(", ")}`);

let added = 0;
let already = 0;
for (const ex of exercises.exercises) {
  const text = explanations[ex.id];
  if (!text) continue;
  if (ex.explanationFa && ex.explanationFa.trim()) {
    already++; // keep authored text
    continue;
  }
  ex.explanationFa = text;
  added++;
}

// 2. bump the bundle version in all five files
for (const name of FILES) {
  const data = name === "exercises" ? exercises : read(name);
  data.contentVersion = NEW_VERSION;
  write(name, data);
}

const total = exercises.exercises.length;
const withExp = exercises.exercises.filter((e) => e.explanationFa && e.explanationFa.trim()).length;
console.log(`A-1: added ${added} explanations; ${withExp}/${total} exercises now explain a miss.`);
console.log(`contentVersion → ${NEW_VERSION} in ${FILES.join(", ")}.`);
