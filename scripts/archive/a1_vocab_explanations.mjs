// A-1 for vocabulary: every word gets a short authored Persian usage note, shown
// in the review session after a miss (the SRS queue is vocabulary-only, so a word
// is what "explain a miss" has to explain there).
//
// Run:  node scripts/a1_vocab_explanations.mjs
import { readFileSync, writeFileSync } from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const NEW_VERSION = 12;

const explanations = {
  // ---- A1 greetings -------------------------------------------------------
  "a1.greetings.word.hello": "درودِ غیررسمی و روزمره؛ صبح‌ها Good morning و هنگام خداحافظی Goodbye می‌گوییم.",
  "a1.greetings.word.good-morning": "تنها برای صبح تا حدود ظهر؛ بعد از آن Good afternoon یا Good evening می‌آید.",
  "a1.greetings.word.thank-you": "برای تشکر؛ با very much شدت می‌گیرد: Thank you very much.",
  "a1.greetings.word.please": "واژهٔ ادب؛ در درخواست و پذیرش می‌آید: Yes, please.",
  "a1.greetings.word.goodbye": "خداحافظی؛ غیررسمی‌اش Bye و خداحافظی شبانه Good night است.",
  "a1.greetings.word.nice-to-meet-you": "عبارت ثابتِ نخستین آشنایی؛ پاسخش Nice to meet you too است.",

  // ---- A1 daily routine ---------------------------------------------------
  "a1.daily-routine.word.wake-up": "بیدار شدن از خواب؛ اگر خودت از رختخواب بیرون بیایی get up می‌گوییم.",
  "a1.daily-routine.word.breakfast": "صبحانه؛ با have/make بیاید: have breakfast.",
  "a1.daily-routine.word.usually": "قید تکرار؛ پیش از فعل اصلی و پس از فعل to be: I usually walk.",
  "a1.daily-routine.word.shower": "دوش گرفتن؛ فعلش take یا have است، نه do: take a shower.",
  "a1.daily-routine.word.commute": "رفتن به سر کار؛ با go to + مکان ساخته می‌شود.",
  "a1.daily-routine.word.evening": "عصر تا پیش از خواب؛ با in the evening می‌آید.",

  // ---- A1 family ----------------------------------------------------------
  "a1.family.word.mother": "مادر؛ غیررسمی‌اش mum (بریتانیا) یا mom (آمریکا) است.",
  "a1.family.word.father": "پدر؛ غیررسمی‌اش dad است.",
  "a1.family.word.sister": "خواهر؛ با older/younger تفاوت سنی را می‌رساند.",
  "a1.family.word.brother": "برادر؛ جمعش brothers است.",
  "a1.family.word.son": "پسرِ فرزند؛ با boy که پسر به‌طور کلی است فرق دارد.",
  "a1.family.word.daughter": "دخترِ فرزند؛ دقیق‌تر از girl است.",

  // ---- A2 airport ---------------------------------------------------------
  "a2.airport.word.passport": "گذرنامه؛ در فرودگاه با passport control می‌آید.",
  "a2.airport.word.boarding-pass": "کارت پرواز؛ پیش از سوار شدن به هواپیما نشان داده می‌شود.",
  "a2.airport.word.flight": "پرواز؛ با flight number (شمارهٔ پرواز) می‌آید.",
  "a2.airport.word.gate": "دروازهٔ سوار شدن به هواپیماست، نه درِ ورودی ساختمان.",
  "a2.airport.word.luggage": "چمدان و بار؛ غیرقابل‌شمارش است، پس luggages نمی‌گوییم.",
  "a2.airport.word.departure": "خروج و حرکت؛ نقطهٔ مقابلش arrival (ورود) است.",

  // ---- A2 restaurant ------------------------------------------------------
  "a2.restaurant.word.menu": "فهرست غذا؛ در رستوران برای انتخاب غذا به آن نگاه می‌کنی.",
  "a2.restaurant.word.waiter": "پیشخدمتِ مرد؛ برای زن waitress به کار می‌رود.",
  "a2.restaurant.word.order": "سفارش دادن؛ با order food یعنی سفارش غذا دادن.",
  "a2.restaurant.word.delicious": "خوشمزه؛ برای غذا و نوشیدنی، نه برای شخص.",
  "a2.restaurant.word.bill": "صورت‌حساب؛ در آمریکا به آن check می‌گویند.",
  "a2.restaurant.word.reservation": "رزرو میز؛ با make a reservation می‌آید.",

  // ---- A1 shopping --------------------------------------------------------
  "a1.shopping.word.price": "قیمت؛ با price tag (برچسب قیمت) هم می‌آید.",
  "a1.shopping.word.cheap": "ارزان؛ نقطهٔ مقابلش expensive است.",
  "a1.shopping.word.money": "پول؛ غیرقابل‌شمارش است، پس moneys نمی‌گوییم.",
  "a1.shopping.word.pay": "پرداخت کردن؛ با pay for + چیز می‌آید: pay for the ticket.",
  "a1.shopping.word.discount": "تخفیف؛ با get a discount می‌آید.",
  "a1.shopping.word.kilo": "کیلوگرم؛ با a kilo of می‌آید: a kilo of apples.",

  // ---- A2 directions ------------------------------------------------------
  "a2.directions.word.straight": "مستقیم؛ go straight ahead عبارت ثابتِ «جلوی مستقیم برو» است.",
  "a2.directions.word.corner": "گوشه و چهارراه؛ on the corner یعنی سرِ نبش.",
  "a2.directions.word.traffic-light": "چراغ راهنما؛ با at the traffic light می‌آید.",
  "a2.directions.word.opposite": "روبرو؛ با across from هم‌معنی است.",
  "a2.directions.word.block": "بلوک شهری، فاصلهٔ میان دو چهارراه؛ one block away.",
  "a2.directions.word.intersection": "تقاطع؛ رسمی‌تر از crossroads است.",

  // ---- A2 health ----------------------------------------------------------
  "a2.health.word.headache": "سردرد؛ با have a headache می‌آید.",
  "a2.health.word.medicine": "دارو؛ غیرقابل‌شمارش است، پس medicines نمی‌گوییم.",
  "a2.health.word.fever": "تب؛ با have a fever می‌آید.",
  "a2.health.word.prescription": "نسخهٔ پزشک؛ با write یا fill a prescription.",
  "a2.health.word.recover": "بهبود یافتن؛ با recover from + بیماری می‌آید.",
  "a2.health.word.pain": "درد؛ با in pain یعنی «در درد» می‌آید.",

  // ---- B1 work ------------------------------------------------------------
  "b1.work.word.career": "مسیر شغلی درازمدت؛ با job که شغل فعلی است فرق دارد.",
  "b1.work.word.deadline": "مهلت؛ با meet a deadline و miss a deadline می‌آید.",
  "b1.work.word.colleague": "همکار؛ رابطهٔ حرفه‌ای را می‌رساند.",
  "b1.work.word.apply": "درخواست دادن؛ با apply for + شغل می‌آید.",
  "b1.work.word.salary": "حقوق ثابت ماهانه؛ wage دستمزد ساعتی است.",
  "b1.work.word.qualification": "مدرک و صلاحیت تحصیلی یا حرفه‌ای.",

  // ---- B1 news ------------------------------------------------------------
  "b1.news.word.headline": "تیتر اصلی خبر؛ با make headlines (شاخص شدن) هم می‌آید.",
  "b1.news.word.report": "گزارش؛ هم اسم است و هم فعل.",
  "b1.news.word.source": "منبع خبر؛ با reliable source می‌آید.",
  "b1.news.word.announce": "اعلام کردنِ رسمی؛ با announce a decision.",
  "b1.news.word.journalist": "روزنامه‌نگار؛ با reporter هم‌معنی است.",
  "b1.news.word.broadcast": "پخش کردن برنامه؛ هم اسم و هم فعل است.",

  // ---- B1 plans -----------------------------------------------------------
  "b1.plans.word.decide": "تصمیم گرفتن؛ با decide on/about + اسم می‌آید.",
  "b1.plans.word.intend": "قصد داشتن؛ با intend to + فعل ساده می‌آید.",
  "b1.plans.word.schedule": "برنامهٔ زمانی؛ behind schedule یعنی عقب از برنامه.",
  "b1.plans.word.afford": "توان مالی داشتن؛ با can/can't afford to + فعل می‌آید.",
  "b1.plans.word.probably": "به احتمال زیاد؛ پیش از فعل اصلی می‌آید.",
  "b1.plans.word.option": "گزینه؛ با keep your options open هم می‌آید.",

  // ---- B1 experiences -----------------------------------------------------
  "b1.experiences.word.journey": "سفرِ طولانی و مسیر؛ سفر کوتاه trip است.",
  "b1.experiences.word.memorable": "به‌یادماندنی؛ از ریشهٔ memory ساخته شده است.",
  "b1.experiences.word.souvenir": "یادگاری سفر؛ با bring back souvenirs می‌آید.",
  "b1.experiences.word.explore": "کاوش کردن؛ برای مکان و ایده هر دو به کار می‌رود.",
  "b1.experiences.word.adventure": "ماجراجویی؛ با go on an adventure می‌آید.",
  "b1.experiences.word.abroad": "در خارج از کشور؛ قید است و با in/to به کار نمی‌رود.",

  // ---- B2 technology ------------------------------------------------------
  "b2.technology.word.innovation": "نوآوری؛ از فعل innovate ساخته شده است.",
  "b2.technology.word.obsolete": "منسوخ؛ با become obsolete می‌آید.",
  "b2.technology.word.privacy": "حریم خصوصی؛ غیرقابل‌شمارش است.",
  "b2.technology.word.algorithm": "الگوریتم؛ مجموعه‌ای از دستورهای محاسباتی برای حل یک مسئله.",
  "b2.technology.word.device": "دستگاه و ابزار الکترونیکی؛ با mobile device.",
  "b2.technology.word.artificial": "مصنوعی؛ مقابل natural و با artificial intelligence.",

  // ---- B2 environment -----------------------------------------------------
  "b2.environment.word.pollution": "آلودگی؛ غیرقابل‌شمارش است، پس pollutions نمی‌گوییم.",
  "b2.environment.word.sustainable": "پایدار؛ sustainable development یعنی توسعهٔ پایدار.",
  "b2.environment.word.emission": "انتشار آلاینده؛ معمولاً جمع می‌آید: carbon emissions.",
  "b2.environment.word.conserve": "حفاظت و صرفه‌جویی کردن؛ با conserve energy.",
  "b2.environment.word.hazard": "خطر و عامل زیان‌آور؛ با health hazard.",
  "b2.environment.word.renewable": "تجدیدپذیر؛ با renewable energy.",

  // ---- B2 interview -------------------------------------------------------
  "b2.interview.word.candidate": "داوطلب و کاندیدا؛ با candidate for + جایگاه می‌آید.",
  "b2.interview.word.strength": "نقطهٔ قوت؛ جمعش strengths در مصاحبهٔ کاری رایج است.",
  "b2.interview.word.experience-noun": "تجربه؛ در معنای تجربهٔ کاری غیرقابل‌شمارش است.",
  "b2.interview.word.achievement": "دستاورد؛ از فعل achieve ساخته شده است.",
  "b2.interview.word.responsible": "مسئول؛ با responsible for + کار می‌آید.",
  "b2.interview.word.eligible": "واجد شرایط؛ با eligible for می‌آید.",

  // ---- B2 media -----------------------------------------------------------
  "b2.media.word.advertising": "تبلیغات به‌عنوان صنعت؛ یک آگهیِ مشخص advertisement است.",
  "b2.media.word.audience": "مخاطبان؛ گروهی که محتوا را می‌بینند یا می‌شنوند.",
  "b2.media.word.campaign": "کمپین؛ با launch a campaign می‌آید.",
  "b2.media.word.bias": "سوگیری؛ با political bias.",
  "b2.media.word.slogan": "شعار تبلیغاتی؛ جمله‌ای کوتاه و به‌یادماندنی.",
  "b2.media.word.influence-verb": "تأثیر گذاشتن؛ هم فعل است و هم اسم.",

  // ---- C1 academic --------------------------------------------------------
  "c1.academic.word.hypothesis": "فرضیه؛ با test a hypothesis می‌آید.",
  "c1.academic.word.methodology": "روش‌شناسی؛ نظام روش‌های یک پژوهش.",
  "c1.academic.word.evidence": "شواهد؛ غیرقابل‌شمارش است، پس evidences نمی‌گوییم.",
  "c1.academic.word.cite": "ارجاع دادن؛ با cite a source می‌آید.",
  "c1.academic.word.notation": "نمادگذاری قراردادی؛ مانند mathematical notation.",
  "c1.academic.word.substantial": "چشمگیر و قابل توجه؛ با substantial evidence.",

  // ---- C1 persuasion ------------------------------------------------------
  "c1.persuasion.word.counterargument": "استدلال متقابل؛ پاسخ به استدلال مخالف.",
  "c1.persuasion.word.persuasive": "قانع‌کننده؛ از فعل persuade ساخته شده است.",
  "c1.persuasion.word.contention": "ادعای اصلی؛ با central contention می‌آید.",
  "c1.persuasion.word.undermine": "تضعیف کردن؛ با undermine confidence.",
  "c1.persuasion.word.concede": "به‌ناچار پذیرفتن؛ با concede a point می‌آید.",
  "c1.persuasion.word.rhetoric": "بلاغت و سخنوری؛ گاه بار منفی دارد: empty rhetoric.",

  // ---- C1 culture ---------------------------------------------------------
  "c1.culture.word.cinematic": "سینمایی؛ کیفیتی که یادآور فیلم است.",
  "c1.culture.word.subtitle": "زیرنویس؛ معمولاً جمع می‌آید: subtitles.",
  "c1.culture.word.portrayal": "به تصویر کشیدن؛ از فعل portray ساخته شده است.",
  "c1.culture.word.stereotype": "کلیشه؛ با break a stereotype می‌آید.",
  "c1.culture.word.director": "کارگردان؛ با film director.",
  "c1.culture.word.narrative": "روایت؛ ساختار داستان‌گویی.",

  // ---- C1 phrasal idioms --------------------------------------------------
  "c1.phrasal-idioms.word.give-up": "دست کشیدن؛ با give up on + شخص یا چیز می‌آید.",
  "c1.phrasal-idioms.word.put-off": "به تعویق انداختن؛ با put off until.",
  "c1.phrasal-idioms.word.come-across": "اتفاقی پیدا کردن؛ با come across as یعنی «به نظر رسیدن».",
  "c1.phrasal-idioms.word.get-away-with": "بی‌مجازات ماندن؛ با get away with it.",
  "c1.phrasal-idioms.word.run-out-of": "تمام شدن موجودی؛ با run out of time.",
  "c1.phrasal-idioms.word.bring-up": "پیش کشیدن موضوع؛ با bring up a topic.",

  // ---- C2 nuance ----------------------------------------------------------
  "c2.nuance.word.ambiguous": "مبهم و دوپهلو؛ از آن دو معنا برداشت می‌شود.",
  "c2.nuance.word.subtle": "ظریف و پنهان؛ با subtle difference.",
  "c2.nuance.word.connotation": "بار معنایی ضمنی؛ می‌تواند مثبت یا منفی باشد.",
  "c2.nuance.word.vague": "مبهم و نامشخص؛ اغلب بار منفی دارد.",
  "c2.nuance.word.distinction": "تمایز؛ با draw a distinction می‌آید.",
  "c2.nuance.word.imply": "تلویحاً بیان کردن؛ گوینده imply می‌کند و شنونده infer.",

  // ---- C2 rhetoric --------------------------------------------------------
  "c2.rhetoric.word.eloquence": "فصاحت و بلاغت؛ از صفت eloquent ساخته شده است.",
  "c2.rhetoric.word.refute": "رد کردن با دلیل؛ قوی‌تر از deny است.",
  "c2.rhetoric.word.concision": "ایجاز؛ کوتاه‌گویی همراه با روشنی.",
  "c2.rhetoric.word.irony": "کنایه؛ تضاد میان ظاهر و معنای واقعی.",
  "c2.rhetoric.word.arrogance": "تکبر؛ صفتش arrogant است.",
  "c2.rhetoric.word.persuade": "قانع کردن؛ با persuade somebody to do.",

  // ---- C2 science ---------------------------------------------------------
  "c2.science.word.empirical": "تجربی؛ مبتنی بر مشاهده و آزمایش، نه نظریهٔ محض.",
  "c2.science.word.corroborate": "تأیید کردن با شواهد مستقل.",
  "c2.science.word.speculation": "حدس و گمان؛ با pure speculation.",
  "c2.science.word.variable": "متغیر؛ در پژوهش چیزی که تغییر می‌کند.",
  "c2.science.word.precise": "دقیق؛ با precise wording.",
  "c2.science.word.phenomenon": "پدیده؛ جمعش phenomena است.",

  // ---- C2 literature ------------------------------------------------------
  "c2.literature.word.narrator": "راوی داستان؛ با unreliable narrator.",
  "c2.literature.word.protagonist": "قهرمان اصلی؛ نقطهٔ مقابلش antagonist است.",
  "c2.literature.word.allegory": "تمثیل؛ داستانی با معنای نمادینِ زیرین.",
  "c2.literature.word.foreshadowing": "پیش‌درآمد؛ نشانه‌ای از رویداد آیندهٔ داستان.",
  "c2.literature.word.imagery": "تصویرسازی زبانی؛ با vivid imagery.",
  "c2.literature.word.metaphor": "استعاره؛ با extended metaphor.",

  // ---- A1 introductions ---------------------------------------------------
  "a1.introductions.word.name": "نام؛ با first name و full name می‌آید.",
  "a1.introductions.word.friend": "دوست؛ با make friends یعنی «دوست پیدا کردن».",
  "a1.introductions.word.meet": "ملاقات کردن؛ با meet for the first time.",
  "a1.introductions.word.new": "جدید؛ با brand new یعنی «کاملاً نو».",
  "a1.introductions.word.work": "کار کردن؛ با work as + شغل می‌آید.",
  "a1.introductions.word.live": "زندگی کردن؛ با live in + مکان می‌آید.",

  // ---- A1 weather ---------------------------------------------------------
  "a1.weather.word.rain": "باران؛ غیرقابل‌شمارش است، با heavy rain می‌آید.",
  "a1.weather.word.sunny": "آفتابی؛ با a sunny day می‌آید.",
  "a1.weather.word.cloud": "ابر؛ صفتش cloudy (ابری) است.",
  "a1.weather.word.wind": "باد؛ با strong wind می‌آید.",
  "a1.weather.word.season": "فصل سال؛ با the rainy season هم می‌آید.",
  "a1.weather.word.warm": "گرم و مطبوع؛ گرمای شدید hot است.",

  // ---- A2 hobbies ---------------------------------------------------------
  "a2.hobbies.word.hobby": "سرگرمی؛ با take up a hobby می‌آید.",
  "a2.hobbies.word.collect": "جمع‌آوری کردن؛ با collect stamps.",
  "a2.hobbies.word.painting": "نقاشی؛ هم به فعالیت و هم به اثر گفته می‌شود.",
  "a2.hobbies.word.photography": "عکاسی؛ با take up photography.",
  "a2.hobbies.word.member": "عضو؛ با become a member.",
  "a2.hobbies.word.spare-time": "وقت آزاد؛ با in my spare time می‌آید.",

  // ---- A2 city life -------------------------------------------------------
  "a2.city-life.word.neighbourhood": "محله؛ با quiet neighbourhood.",
  "a2.city-life.word.crowded": "شلوغ؛ با crowded street.",
  "a2.city-life.word.traffic": "ترافیک؛ غیرقابل‌شمارش است، با heavy traffic.",
  "a2.city-life.word.convenient": "راحت و در دسترس؛ با convenient for.",
  "a2.city-life.word.park": "پارک؛ با public park.",
  "a2.city-life.word.subway": "مترو؛ در بریتانیا underground یا tube می‌گویند.",

  // ---- B1 money -----------------------------------------------------------
  "b1.money.word.budget": "بودجه؛ با stick to a budget.",
  "b1.money.word.save": "پس‌انداز کردن؛ با save up for می‌آید.",
  "b1.money.word.expense": "هزینه؛ معمولاً جمع می‌آید: living expenses.",
  "b1.money.word.afford": "توان مالی داشتن؛ با can't afford.",
  "b1.money.word.debt": "بدهی؛ با pay off a debt.",
  "b1.money.word.income": "درآمد؛ نقطهٔ مقابلش expense (هزینه) است.",

  // ---- B1 health lifestyle ------------------------------------------------
  "b1.health-lifestyle.word.balanced-diet": "رژیم غذایی متعادل؛ با eat a balanced diet.",
  "b1.health-lifestyle.word.exercise": "ورزش؛ هم اسم است و هم فعل.",
  "b1.health-lifestyle.word.stress": "استرس؛ با reduce stress.",
  "b1.health-lifestyle.word.sleep": "خواب؛ غیرقابل‌شمارش، با get enough sleep.",
  "b1.health-lifestyle.word.habit": "عادت؛ با break/form a habit می‌آید.",
  "b1.health-lifestyle.word.skip": "جا انداختن؛ با skip a meal.",

  // ---- B2 problems --------------------------------------------------------
  "b2.problems.word.complaint": "شکایت؛ با make a complaint.",
  "b2.problems.word.refund": "بازپرداخت پول؛ با ask for a refund.",
  "b2.problems.word.resolve": "حل کردن مشکل؛ با resolve an issue.",
  "b2.problems.word.deadline": "مهلت؛ با meet a deadline.",
  "b2.problems.word.delay": "تأخیر؛ با without delay می‌آید.",
  "b2.problems.word.compensate": "جبران کردن؛ با compensate for.",

  // ---- B2 research --------------------------------------------------------
  "b2.research.word.evidence": "شواهد؛ غیرقابل‌شمارش است و s نمی‌گیرد.",
  "b2.research.word.hypothesis": "فرضیه؛ با test یا reject a hypothesis.",
  "b2.research.word.sample": "نمونه؛ با sample size.",
  "b2.research.word.indicate": "نشان دادن؛ با results indicate.",
  "b2.research.word.reliable": "قابل‌اعتماد؛ با reliable source.",
  "b2.research.word.bias": "سوگیری؛ با avoid bias.",

  // ---- C1 inference -------------------------------------------------------
  "c1.inference.word.imply": "تلویحاً رساندن؛ گوینده imply می‌کند.",
  "c1.inference.word.infer": "استنباط کردن؛ شنونده infer می‌کند.",
  "c1.inference.word.assumption": "فرض و پیش‌فرض؛ با make an assumption.",
  "c1.inference.word.ambiguity": "ابهام؛ با remove ambiguity.",
  "c1.inference.word.overstate": "اغراق کردن؛ نقطهٔ مقابلش understate است.",
  "c1.inference.word.unspoken": "ناگفته؛ با unspoken rule.",

  // ---- C1 register --------------------------------------------------------
  "c1.register.word.register": "سبک زبانی؛ میزان رسمی‌بودن متناسب با موقعیت.",
  "c1.register.word.formal": "رسمی؛ نقطهٔ مقابلش informal و colloquial است.",
  "c1.register.word.colloquial": "محاوره‌ای؛ مناسب گفتار غیررسمی.",
  "c1.register.word.nominalisation": "اسم‌سازی؛ تبدیل فعل به اسم مانند decide → decision.",
  "c1.register.word.hedge": "تلطیف کردن ادعا؛ با may و tends to.",
  "c1.register.word.courtesy": "ادب و نزاکت؛ با common courtesy.",

  // ---- C1 cohesion --------------------------------------------------------
  "c1.cohesion.word.cohesion": "انسجام متن؛ پیوند میان جمله‌ها.",
  "c1.cohesion.word.discourse-marker": "نشانگر گفتمانی؛ رابطهٔ میان ایده‌ها را نشان می‌دهد.",
  "c1.cohesion.word.antecedent": "مرجع پیشین؛ اسمی که ضمیر به آن برمی‌گردد.",
  "c1.cohesion.word.ellipsis": "حذف واژه‌های تکراری که خواننده می‌تواند تأمین کند.",
  "c1.cohesion.word.paraphrase": "بازگویی؛ همان معنا با کلمات خودت.",
  "c1.cohesion.word.transition": "گذار میان بخش‌های متن.",

  // ---- C2 argumentation ---------------------------------------------------
  "c2.argumentation.word.fallacy": "مغالطه؛ خطای استدلالی که نتیجه را بی‌اعتبار می‌کند.",
  "c2.argumentation.word.concede": "امتیاز دادن در بحث؛ پذیرفتن جزئی از حرف مخالف.",
  "c2.argumentation.word.refute": "رد کردن با دلیل.",
  "c2.argumentation.word.premise": "مقدمهٔ منطقی؛ پایه‌ای که نتیجه بر آن سوار است.",
  "c2.argumentation.word.assertion": "ادعا؛ گزاره‌ای که هنوز اثبات نشده است.",
  "c2.argumentation.word.straw-man": "مغالطهٔ آدم‌کاهی؛ حمله به نسخهٔ ضعیف‌شدهٔ حرف مخالف.",

  // ---- C2 collocation -----------------------------------------------------
  "c2.collocation.word.collocation": "هم‌نشینی واژه‌ها؛ ترکیب طبیعی که natives به کار می‌برند.",
  "c2.collocation.word.collocate": "هم‌نشین شدن؛ با collocate with.",
  "c2.collocation.word.delexical-verb": "فعل سبک؛ معنای اصلی در اسم است، مانند take a shower.",
  "c2.collocation.word.lexical": "واژگانی؛ مربوط به واژه‌ها و گنجینهٔ واژگان.",
  "c2.collocation.word.idiomatic": "اصطلاحی و طبیعی؛ با idiomatic English.",
  "c2.collocation.word.precision": "دقت؛ با with precision.",

  // ---- C2 irony -----------------------------------------------------------
  "c2.irony.word.understatement": "کم‌گویی؛ کوچک‌نمایی عامدانه برای تأکید.",
  "c2.irony.word.litotes": "کم‌گویی با نفیِ نقیض، مانند not uncommon.",
  "c2.irony.word.deadpan": "با چهرهٔ بی‌احساس؛ طنزِ خشک.",
  "c2.irony.word.subtext": "معنای پنهانِ زیر متن.",
  "c2.irony.word.self-deprecating": "خودکوچک‌کننده؛ طنز به خود.",
  "c2.irony.word.facetious": "شوخ‌طبع اما نامناسبِ موقعیت.",

  // ---- A1 articles --------------------------------------------------------
  "a1.articles.word.umbrella": "چتر؛ با take an umbrella.",
  "a1.articles.word.apple": "سیب؛ با apple juice.",
  "a1.articles.word.orange": "پرتقال؛ با orange juice.",
  "a1.articles.word.hour": "ساعت (۶۰ دقیقه)؛ h در تلفظ خوانده نمی‌شود، پس an hour.",
  "a1.articles.word.sun": "خورشید؛ یکتاست، پس با the می‌آید: the sun.",
  "a1.articles.word.hotel": "هتل؛ با book a hotel.",

  // ---- A1 plurals ---------------------------------------------------------
  "a1.plurals.word.child": "کودک؛ جمعش بی‌قاعده است: children.",
  "a1.plurals.word.man": "مرد؛ جمعش بی‌قاعده است: men.",
  "a1.plurals.word.woman": "زن؛ جمعش بی‌قاعده است: women.",
  "a1.plurals.word.foot": "پا؛ جمعش بی‌قاعده است: feet.",
  "a1.plurals.word.leaf": "برگ؛ جمعش با تغییر f به v ساخته می‌شود: leaves.",
  "a1.plurals.word.box": "جعبه؛ جمعش با es ساخته می‌شود: boxes.",

  // ---- A1 pronouns --------------------------------------------------------
  "a1.pronouns.word.friend": "دوست؛ با make friends یعنی «دوست پیدا کردن».",
  "a1.pronouns.word.teacher": "معلم؛ با English teacher.",
  "a1.pronouns.word.sister": "خواهر؛ با younger sister.",
  "a1.pronouns.word.brother": "برادر؛ با twin brother.",
  "a1.pronouns.word.doctor": "پزشک؛ با see a doctor.",
  "a1.pronouns.word.student": "دانش‌آموز یا دانشجو؛ با exchange student.",

  // ---- A1 can -------------------------------------------------------------
  "a1.can.word.swim": "شنا کردن؛ با go swimming.",
  "a1.can.word.drive": "رانندگی کردن؛ با drive a car.",
  "a1.can.word.speak": "صحبت کردن؛ با speak English.",
  "a1.can.word.cook": "آشپزی کردن؛ هم اسم (آشپز) و هم فعل.",
  "a1.can.word.play": "بازی کردن یا نواختن ساز؛ با play the piano.",
  "a1.can.word.run": "دویدن؛ با go for a run.",

  // ---- A1 imperatives -----------------------------------------------------
  "a1.imperatives.word.stop": "توقف کردن؛ هم فعل است و هم اسم (ایستگاه).",
  "a1.imperatives.word.wait": "منتظر ماندن؛ با wait a moment.",
  "a1.imperatives.word.sit": "نشستن؛ با sit down.",
  "a1.imperatives.word.open": "باز کردن؛ با open the door.",
  "a1.imperatives.word.close": "بستن؛ با close your eyes.",
  "a1.imperatives.word.listen": "گوش دادن؛ با listen to + چیز می‌آید.",

  // ---- A1 question words --------------------------------------------------
  "a1.question-words.word.what": "چه و چه‌چیز؛ با what time و what kind of.",
  "a1.question-words.word.where": "کجا؛ با where from.",
  "a1.question-words.word.when": "کی و چه‌وقت؛ با since when.",
  "a1.question-words.word.why": "چرا؛ پاسخش با because می‌آید.",
  "a1.question-words.word.who": "چه کسی؛ برای پرسش از فاعل به کار می‌رود.",
  "a1.question-words.word.how": "چگونه؛ با how about و how come.",

  // ---- A2 past simple -----------------------------------------------------
  "a2.past-simple.word.go": "رفتن؛ گذشته‌اش بی‌قاعده است: went.",
  "a2.past-simple.word.buy": "خریدن؛ گذشته‌اش بی‌قاعده است: bought.",
  "a2.past-simple.word.see": "دیدن؛ گذشته‌اش بی‌قاعده است: saw.",
  "a2.past-simple.word.eat": "خوردن؛ گذشته‌اش بی‌قاعده است: ate.",
  "a2.past-simple.word.take": "گرفتن و برداشتن؛ گذشته‌اش بی‌قاعده است: took.",
  "a2.past-simple.word.write": "نوشتن؛ گذشته‌اش بی‌قاعده است: wrote.",
};

const read = (name) => JSON.parse(readFileSync(`${DIR}/${name}.json`, "utf8"));
const write = (name, data) => writeFileSync(`${DIR}/${name}.json`, JSON.stringify(data, null, 2) + "\n");

const vocabulary = read("vocabulary");
const knownIds = new Set(vocabulary.vocabulary.map((v) => v.id));
const unknown = Object.keys(explanations).filter((id) => !knownIds.has(id));
if (unknown.length) throw new Error(`explanations target unknown words: ${unknown.join(", ")}`);

let added = 0;
for (const word of vocabulary.vocabulary) {
  const text = explanations[word.id];
  if (!text) continue;
  if (word.explanationFa && word.explanationFa.trim()) continue;
  word.explanationFa = text;
  added++;
}

const uncovered = vocabulary.vocabulary.filter((w) => !(w.explanationFa ?? "").trim()).map((w) => w.id);
if (uncovered.length) throw new Error(`words still without an explanation: ${uncovered.join(", ")}`);

for (const name of FILES) {
  const data = name === "vocabulary" ? vocabulary : read(name);
  data.contentVersion = NEW_VERSION;
  write(name, data);
}

const total = vocabulary.vocabulary.length;
const withExp = vocabulary.vocabulary.filter((w) => (w.explanationFa ?? "").trim()).length;
console.log(`A-1 vocab: added ${added}; ${withExp}/${total} words explain their use.`);
console.log(`contentVersion → ${NEW_VERSION} in ${FILES.join(", ")}.`);
