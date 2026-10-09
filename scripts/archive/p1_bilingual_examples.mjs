// scripts/archive/p1_bilingual_examples.mjs — A-10 bilingual examples (one-shot).
//
// Rewrites every vocabulary entry from a single plain-string example
//   "examples": ["Sentence."]
// to two authored bilingual examples
//   "examples": [{ "en": "Sentence.", "fa": "…." }, { "en": "Second.", "fa": "…." }]
// where entry 0 keeps the original English sentence (its Persian translation is
// authored here as fa1) and entry 1 is a newly authored example (en2/fa2).
//
// Also bumps contentVersion 18 -> 19 in ALL FIVE content files (identically).
//
// NEVER re-run this script: it would overwrite later hand edits of the
// examples. See scripts/archive/README.md. The JSON files are the source of
// truth from here on.
//
// Run from the repo root:  node scripts/archive/p1_bilingual_examples.mjs

import fs from "node:fs";

const DIR = "app/src/main/assets/content";
const FILES = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const FROM = 18;
const TO = 19;

// word id -> [fa of the original example, second English example, fa of it].
// The second example names the word it illustrates (the content test checks
// every example individually; irregular forms are allowlisted there).
const B = {
  // --- a1.greetings -------------------------------------------------------
  "a1.greetings.word.hello": ["سلام، حال شما چطور است؟", "Hello, it's great to see you.", "سلام، دیدنت عالی است!"],
  "a1.greetings.word.good-morning": ["صبح بخیر، آقای معلم!", "I say good morning to my class every day.", "هر روز به کلاسم صبح بخیر می‌گویم."],
  "a1.greetings.word.thank-you": ["از کمکت متشکرم.", "Thank you for the lovely gift.", "از هدیهٔ قشنگت ممنونم."],
  "a1.greetings.word.please": ["لطفاً بنشینید.", "Please close the door on your way out.", "لطفاً موقع بیرون رفتن در را ببند."],
  "a1.greetings.word.goodbye": ["خداحافظ، فردا می‌بینمت.", "She said goodbye and left the office.", "او خداحافظی کرد و دفتر را ترک کرد."],
  "a1.greetings.word.nice-to-meet-you": ["سلام، من سارا هستم. از آشنایی‌تان خوشوقتم.", "Nice to meet you — I'm Reza.", "از آشنایی‌تان خوشوقتم — من رضا هستم."],

  // --- a1.daily-routine ---------------------------------------------------
  "a1.daily-routine.word.wake-up": ["من ساعت هفت بیدار می‌شوم.", "Please wake me up at six tomorrow.", "لطفاً فردا ساعت شش مرا بیدار کن."],
  "a1.daily-routine.word.breakfast": ["من صبحانه را با خانواده‌ام می‌خورم.", "We had breakfast at a café.", "ما در یک کافه صبحانه خوردیم."],
  "a1.daily-routine.word.usually": ["من معمولاً صبح‌ها قهوه می‌خورم.", "He usually walks to work.", "او معمولاً پیاده به سر کار می‌رود."],
  "a1.daily-routine.word.shower": ["من هر صبح دوش می‌گیرم.", "I take a shower after the gym.", "بعد از ورزش دوش می‌گیرم."],
  "a1.daily-routine.word.commute": ["من با اتوبوس به سر کار می‌روم.", "It takes an hour to go to work.", "به سر کار رفتن یک ساعت طول می‌کشد."],
  "a1.daily-routine.word.evening": ["ما عصرها تلویزیون تماشا می‌کنیم.", "My parents arrive in the evening.", "والدینم عصر می‌رسند."],

  // --- a1.family ----------------------------------------------------------
  "a1.family.word.mother": ["مادرم معلم است.", "My mother calls me every evening.", "مادرم هر شب به من زنگ می‌زند."],
  "a1.family.word.father": ["پدرم در بانک کار می‌کند.", "My father cooks dinner on Fridays.", "پدرم جمعه‌ها شام درست می‌کند."],
  "a1.family.word.sister": ["من یک خواهر دارم.", "My sister is younger than me.", "خواهرم از من کوچک‌تر است."],
  "a1.family.word.brother": ["برادرم ده ساله است.", "My brother plays football well.", "برادرم خوب فوتبال بازی می‌کند."],
  "a1.family.word.son": ["آن‌ها یک پسر دارند.", "Their son studies at university.", "پسرشان در دانشگاه درس می‌خواند."],
  "a1.family.word.daughter": ["دخترش انگلیسی می‌خواند.", "Their daughter is a doctor.", "دخترشان پزشک است."],

  // --- a2.airport ---------------------------------------------------------
  "a2.airport.word.passport": ["لطفاً گذرنامه‌ات را نشانم بده.", "My passport expires next month.", "گذرنامه‌ام ماه بعد منقضی می‌شود."],
  "a2.airport.word.boarding-pass": ["این کارت پرواز من است.", "Please have your boarding pass ready.", "لطفاً کارت پروازتان را آماده نگه دارید."],
  "a2.airport.word.flight": ["پرواز به دبی سه ساعت طول می‌کشد.", "Our flight was delayed by two hours.", "پرواز ما دو ساعت تأخیر داشت."],
  "a2.airport.word.gate": ["گیت ۱۲ آن طرف است.", "We are boarding at gate five.", "ما در گیت پنج سوار می‌شویم."],
  "a2.airport.word.luggage": ["چمدم را گم کردم.", "You can check your luggage at the counter.", "می‌توانید چمدانتان را پیشخوان تحویل دهید."],
  "a2.airport.word.departure": ["حرکت ساعت ۹ شب است.", "The departure board shows a delay.", "تابلوی پروازها تأخیر را نشان می‌دهد."],

  // --- a2.restaurant ------------------------------------------------------
  "a2.restaurant.word.menu": ["لطفاً منو را نشانم بدهید؟", "The menu has both meat and vegetable dishes.", "منو هم غذای گوشتی دارد هم سبزیجات."],
  "a2.restaurant.word.waiter": ["گارسون صورتحساب را آورد.", "The waiter recommended the soup.", "گارسون سوپ را پیشنهاد داد."],
  "a2.restaurant.word.order": ["می‌خواهم مرغ سفارش بدهم.", "We ordered two pizzas.", "ما دو پیتزا سفارش دادیم."],
  "a2.restaurant.word.delicious": ["این سوپ خوشمزه است.", "The kebab was delicious.", "کباب خوشمزه بود."],
  "a2.restaurant.word.bill": ["لطفاً صورتحساب را بیاورید؟", "I paid the bill with a card.", "صورتحساب را با کارت پرداخت کردم."],
  "a2.restaurant.word.reservation": ["من یک رزرو برای دو نفر دارم.", "I made a reservation by phone.", "من با تلفن رزرو کردم."],

  // --- a1.shopping --------------------------------------------------------
  "a1.shopping.word.price": ["قیمت این کیف خیلی بالاست.", "The price of tickets goes up every year.", "قیمت بلیت هر سال بالا می‌رود."],
  "a1.shopping.word.cheap": ["این کفش‌ها خیلی ارزان‌اند.", "We found a cheap hotel near the station.", "ما هتل ارزانی نزدیک ایستگاه پیدا کردیم."],
  "a1.shopping.word.money": ["پول کافی ندارم.", "She saves money for a new laptop.", "او برای لپ‌تاپ جدید پول پس‌انداز می‌کند."],
  "a1.shopping.word.pay": ["می‌توانم با کارت پرداخت کنم؟", "You can pay in cash or by card.", "می‌توانید نقدی یا با کارت پرداخت کنید."],
  "a1.shopping.word.discount": ["امروز روی کفش‌ها تخفیف بزرگی هست.", "Students get a ten percent discount.", "دانشجویان ده درصد تخفیف می‌گیرند."],
  "a1.shopping.word.kilo": ["من یک کیلو سیب لازم دارم.", "This watermelon weighs three kilos.", "این هندوانه سه کیلو وزن دارد."],

  // --- a2.directions ------------------------------------------------------
  "a2.directions.word.straight": ["مستقیم بروید.", "Walk straight and then turn left.", "مستقیم بروید و بعد بپیچید چپ."],
  "a2.directions.word.corner": ["بانک سرِ گوشه است.", "The pharmacy is at the next corner.", "داروخانه گوشهٔ بعدی است."],
  "a2.directions.word.traffic-light": ["سر چراغ دوم بپیچید راست.", "Wait until the traffic light turns green.", "صبر کنید تا چراغ سبز شود."],
  "a2.directions.word.opposite": ["ایستگاه اتوبوس روبروی بانک است.", "The bakery is opposite our school.", "نانوایی روبروی مدرسهٔ ماست."],
  "a2.directions.word.block": ["از اینجا دو خیابان آن‌طرف است.", "Walk three blocks and you will see the park.", "سه خیابان بروید پارک را می‌بینید."],
  "a2.directions.word.intersection": ["سر چهارراه صبر کنید.", "The accident happened at the main intersection.", "تصادف سر چهارراه اصلی اتفاق افتاد."],

  // --- a2.health ----------------------------------------------------------
  "a2.health.word.headache": ["سردرد شدیدی دارم.", "A headache can be a sign of stress.", "سردرد می‌تواند نشانهٔ استرس باشد."],
  "a2.health.word.medicine": ["دارو را بعد از غذا بخور.", "This medicine may make you sleepy.", "این دارو ممکن است خواب‌آور باشد."],
  "a2.health.word.fever": ["بچه تب دارد.", "Stay home if you have a fever.", "اگر تب داری خانه بمان."],
  "a2.health.word.prescription": ["برای این دارو نسخه لازم دارید.", "The doctor wrote a prescription for antibiotics.", "دکتر برای آنتی‌بیوتیک نسخه نوشت."],
  "a2.health.word.recover": ["یک هفته طول کشید تا خوب شوم.", "Most patients recover within two days.", "بیشتر بیماران ظرف دو روز خوب می‌شوند."],
  "a2.health.word.pain": ["کمرم درد می‌کند.", "Call the doctor if the pain continues.", "اگر درد ادامه یافت به دکتر زنگ بزنید."],

  // --- b1.work ------------------------------------------------------------
  "b1.work.word.career": ["او در مهندسی شغلی برای خود ساخت.", "Teaching has been her career for twenty years.", "تدریس بیست سال است شغل اوست."],
  "b1.work.word.deadline": ["باید به مهلت مقرر برسیم.", "The deadline for applications is Friday.", "مهلت ارسال درخواست‌ها جمعه است."],
  "b1.work.word.colleague": ["همکارانم خیلی کمک‌کارند.", "I had lunch with a colleague.", "با یک همکار ناهار خوردم."],
  "b1.work.word.apply": ["هفتهٔ پیش برای این موقعیت درخواست دادم.", "They apply for a visa before travelling.", "آن‌ها قبل از سفر برای ویزا درخواست می‌دهند."],
  "b1.work.word.salary": ["حقوق ماهانه پرداخت می‌شود.", "She asked for a higher salary.", "او حقوق بالاتری خواست."],
  "b1.work.word.qualification": ["برای این نقش صلاحیت‌های لازم را باید داشته باشید.", "A degree is not the only qualification.", "مدرک تحصیلی تنها صلاحیت نیست."],

  // --- b1.news ------------------------------------------------------------
  "b1.news.word.headline": ["این خبر در سراسر جهان تیتر شد.", "The headline oversimplified the facts.", "تیتر، واقعیت‌ها را ساده‌تر از آنچه بود نشان داد."],
  "b1.news.word.report": ["گزارش امروز صبح منتشر شد.", "The report recommends two main changes.", "گزارش دو تغییر اصلی را پیشنهاد می‌کند."],
  "b1.news.word.source": ["روزنامه به منبعی بی‌نام استناد کرد.", "Scientists check every source of the data.", "دانشمندان هر منبع داده را بررسی می‌کنند."],
  "b1.news.word.announce": ["شرکت محصول جدیدی معرفی کرد.", "The school announced the results online.", "مدرسه نتایج را آنلاین اعلام کرد."],
  "b1.news.word.journalist": ["روزنامه‌نگار با شهردار مصاحبه کرد.", "The journalist won a prize for her article.", "روزنامه‌نگار برای مقاله‌اش جایزه گرفت."],
  "b1.news.word.broadcast": ["بازی به‌صورت زنده پخش می‌شود.", "The news is broadcast at nine every night.", "خبر هر شب ساعت نه پخش می‌شود."],

  // --- b1.plans -----------------------------------------------------------
  "b1.plans.word.decide": ["تصمیم گرفتیم با قطار سفر کنیم.", "I can't decide between the two options.", "نمی‌توانم بین دو گزینه تصمیم بگیرم."],
  "b1.plans.word.intend": ["قصد دارم تا جمعه تمام کنم.", "They intend to move abroad next year.", "قصد دارند سال آینده به خارج بروند."],
  "b1.plans.word.schedule": ["قطار سرِ وقت است.", "My schedule is full on Monday.", "برنامهٔ من دوشنبه پر است."],
  "b1.plans.word.afford": ["ما توان خرید ماشین جدید را نداریم.", "We cannot afford to lose this client.", "نمی‌توانیم این مشتری را از دست بدهیم."],
  "b1.plans.word.probably": ["احتمالاً فردا باران می‌آید.", "He is probably at home by now.", "احتمالاً الان خانه است."],
  "b1.plans.word.option": ["دو گزینه داریم: اتوبوس یا قطار.", "The basic option includes breakfast.", "گزینهٔ پایه صبحانه دارد."],

  // --- b1.experiences -----------------------------------------------------
  "b1.experiences.word.journey": ["سفر شش ساعت طول کشید.", "The journey home was quiet.", "سفر برگشت آرام بود."],
  "b1.experiences.word.memorable": ["سفر به‌یادماندنی‌ای بود.", "The wedding night was memorable for everyone.", "شب عروسی برای همه به‌یادماندنی بود."],
  "b1.experiences.word.souvenir": ["از شهر قدیمی یک سوغاتی خریدم.", "She brought souvenirs for her colleagues.", "او برای همکارانش سوغاتی آورد."],
  "b1.experiences.word.explore": ["شهر قدیمی را پیاده گشتیم.", "The children explore every corner of the park.", "بچه‌ها هر گوشهٔ پارک را کشف می‌کنند."],
  "b1.experiences.word.adventure": ["کوه‌نوردی ماجراجویی واقعی بود.", "Losing our train turned into an adventure.", "جا ماندن از قطار تبدیل به یک ماجرا شد."],
  "b1.experiences.word.abroad": ["او یک سال در خارج درس خواند.", "He works abroad for six months a year.", "او شش ماه از سال در خارج کار می‌کند."],

  // --- b2.technology ------------------------------------------------------
  "b2.technology.word.innovation": ["نوآوری دیجیتال بانکداری را تغییر داده است.", "This company funds innovation in health care.", "این شرکت در نوآوری درمانی سرمایه‌گذاری می‌کند."],
  "b2.technology.word.obsolete": ["گوشی‌های قدیمی زود از رده خارج می‌شوند.", "Many skills become obsolete within a decade.", "بسیاری از مهارت‌ها ظرف یک دهه قدیمی می‌شوند."],
  "b2.technology.word.privacy": ["این برنامه نگرانی‌های جدی دربارهٔ حریم خصوصی ایجاد می‌کند.", "The new law strengthens online privacy.", "قانون جدید حریم خصوصی آنلاین را تقویت می‌کند."],
  "b2.technology.word.algorithm": ["الگوریتم ویدیوها را پیشنهاد می‌کند.", "The algorithm sorts the results by relevance.", "الگوریتم نتایج را بر اساس ارتباط مرتب می‌کند."],
  "b2.technology.word.device": ["این وسیله سال پیش اختراع شد.", "This device measures your heart rate.", "این دستگاه ضربان قلبتان را اندازه می‌گیرد."],
  "b2.technology.word.artificial": ["هوش مصنوعی اکنون همه‌جا هست.", "The park has artificial flowers in winter.", "پارک زمستان‌ها گل‌های مصنوعی دارد."],

  // --- b2.environment -----------------------------------------------------
  "b2.environment.word.pollution": ["آلودگی هوا خطر بزرگ سلامتی است.", "The city fights noise pollution with new rules.", "شهر با قوانین جدید علیه آلودگی صوتی می‌جنگد."],
  "b2.environment.word.sustainable": ["به منابع انرژی پایدار نیاز داریم.", "The city invests in sustainable transport.", "شهر در حمل‌ونقل پایدار سرمایه‌گذاری می‌کند."],
  "b2.environment.word.emission": ["کشور متعهد شد انتشار کربن را کاهش دهد.", "Car emission limits are getting stricter.", "محدودیت‌های انتشار خودرو سخت‌گیرانه‌تر می‌شود."],
  "b2.environment.word.conserve": ["در خشکسالی باید آب را صرفه‌جویی کنیم.", "Efforts to conserve energy lower the bill.", "تلاش برای صرفه‌جویی در انرژی هزینه را پایین می‌آورد."],
  "b2.environment.word.hazard": ["سیل در این منطقه خطر است.", "Wet floors are a hazard in kitchens.", "کف خیس در آشپزخانه خطرناک است."],
  "b2.environment.word.renewable": ["انرژی تجدیدپذیر سریع در حال رشد است.", "Renewable sources now power the whole island.", "منابع تجدیدپذیر اکنون کل جزیره را تأمین می‌کنند."],

  // --- b2.interview -------------------------------------------------------
  "b2.interview.word.candidate": ["بیست نامزد برای این موقعیت درخواست دادند.", "Every candidate had a ten-minute interview.", "هر نامزد مصاحبهٔ ده‌دقیقه‌ای داشت."],
  "b2.interview.word.strength": ["کار تیمی بزرگ‌ترین نقطهٔ قوت من است.", "Patience is her main strength with children.", "صبوری نقطهٔ قوت اصلی او با بچه‌هاست."],
  "b2.interview.word.experience-noun": ["او ده سال تجربه دارد.", "The job requires experience with computers.", "این کار تجربه با کامپیوتر می‌خواهد."],
  "b2.interview.word.achievement": ["گرفتن مدرک دستاورد واقعی‌ای بود.", "Finishing the marathon was an achievement.", "تمام کردن ماراتن یک دستاورد بود."],
  "b2.interview.word.responsible": ["من مسئول آموزش کارکنان جدید بودم.", "She is responsible for the budget.", "او مسئول بودجه است."],
  "b2.interview.word.eligible": ["فقط نامزدهای باتجربه واجد شرایط‌اند.", "Under-sixteens are not eligible for the discount.", "کمتر از شانزده سال برای تخفیف واجد شرایط نیستند."],

  // --- b2.media -----------------------------------------------------------
  "b2.media.word.advertising": ["تبلیغات بر خرید ما تأثیر می‌گذارد.", "Advertising on television is very expensive.", "تبلیغات تلویزیونی خیلی گران است."],
  "b2.media.word.audience": ["این برنامه مخاطب زیادی دارد.", "The audience clapped at the end.", "تماشاگران در پایان تشویق کردند."],
  "b2.media.word.campaign": ["کمپین تبلیغاتی موفق بود.", "The health campaign reached two million people.", "کمپین سلامت به دو میلیون نفر رسید."],
  "b2.media.word.bias": ["خوانندگان روزنامه را به جانبداری متهم کردند.", "The newspaper shows a clear bias towards big business.", "روزنامه جانبداری آشکاری از شرکت‌های بزرگ دارد."],
  "b2.media.word.slogan": ["شعار برند اکنون معروف است.", "Can you remember the slogan of the campaign?", "می‌توانید شعار کمپین را به یاد بیاورید؟"],
  "b2.media.word.influence-verb": ["شبکه‌های اجتماعی بر رأی‌دهندگان جوان تأثیر می‌گذارند.", "Her teacher influenced her career choice.", "معلمش بر انتخاب شغل او تأثیر گذاشت."],

  // --- c1.academic --------------------------------------------------------
  "c1.academic.word.hypothesis": ["داده‌ها از فرضیهٔ ما حمایت می‌کنند.", "The experiment proved the hypothesis wrong.", "آزمایش نشان داد فرضیه غلط است."],
  "c1.academic.word.methodology": ["مقاله روش‌شناسی خود را با جزئیات شرح می‌دهد.", "Critics questioned the methodology of the study.", "منتقدان روش‌شناسی این پژوهش را زیر سؤال بردند."],
  "c1.academic.word.evidence": ["برای این ادعا شواهد اندکی هست.", "The police found new evidence at the scene.", "پلیس در صحنه شواهد جدیدی پیدا کرد."],
  "c1.academic.word.cite": ["این پژوهش به تحقیقات قبلی استناد می‌کند.", "You must cite every source you use.", "باید به هر منبعی که استفاده می‌کنید استناد کنید."],
  "c1.academic.word.notation": ["این معادله از نمادهای استاندارد استفاده می‌کند.", "The notation in chapter two is unfamiliar.", "نمادهای فصل دو آشنا نیستند."],
  "c1.academic.word.substantial": ["این پژوهش بودجهٔ قابل توجهی گرفت.", "The bridge needed substantial repairs.", "پل به تعمیرات قابل توجهی نیاز داشت."],

  // --- c1.persuasion ------------------------------------------------------
  "c1.persuasion.word.counterargument": ["او هر اعتراض مخالفی را پیش‌بینی کرد.", "A good essay answers the strongest counterargument.", "مقالهٔ خوب قوی‌ترین استدلال مخالف را پاسخ می‌دهد."],
  "c1.persuasion.word.persuasive": ["او استدلال قانع‌کننده‌ای برای اصلاحات ارائه داد.", "Her speech was persuasive but short.", "سخنرانی او قانع‌کننده اما کوتاه بود."],
  "c1.persuasion.word.contention": ["استدلال اصلی او این است که هزینه‌ها بالا می‌رود.", "That is only a contention, not a proven fact.", "این فقط یک ادعاست، نه واقعیتِ اثبات‌شده."],
  "c1.persuasion.word.undermine": ["این رسوایی اعتماد عمومی را تخریب کرد.", "Late replies undermine your credibility.", "پاسخ‌های دیر هم اعتبار شما را تضعیف می‌کند."],
  "c1.persuasion.word.concede": ["ناچار قبول کرد که او درست می‌گوید.", "The company conceded the mistake after the audit.", "شرکت پس از حسابرسی اشتباه را پذیرفت."],
  "c1.persuasion.word.rhetoric": ["پشت شعارهای پرطمطراق واقعیت‌های اندکی بود.", "The minister's rhetoric impressed the crowd.", "سخنرانی پرطمطراق وزیر جمعیت را تحت تأثیر قرار داد."],

  // --- c1.culture ---------------------------------------------------------
  "c1.culture.word.cinematic": ["فیلم حس سینمایی دارد.", "The desert scenes are truly cinematic.", "صحنه‌های بیابان واقعاً سینمایی‌اند."],
  "c1.culture.word.subtitle": ["من با زیرنویس انگلیسی تماشا کردم.", "The film needs better Persian subtitles.", "این فیلم زیرنویس فارسی بهتری می‌خواهد."],
  "c1.culture.word.portrayal": ["نحوهٔ نمایش جنگ در فیلم واقع‌گرایانه است.", "Critics praised the portrayal of the teacher.", "منتقدان نحوهٔ نشان دادن معلم را تحسین کردند."],
  "c1.culture.word.stereotype": ["شخصیت‌ها از همهٔ کلیشه‌ها دوری می‌کنند.", "The film breaks the stereotype of the villain.", "فیلم کلیشهٔ شخصیت شرور را می‌شکند."],
  "c1.culture.word.director": ["کارگردان برای این فیلم جایزه گرفت.", "The director asked for a second take.", "کارگردان یک برداشت دیگر خواست."],
  "c1.culture.word.narrative": ["فیلم روایتی غیرخطی دارد.", "The narrative shifts between two timelines.", "روایت بین دو خط زمانی جابه‌جا می‌شود."],

  // --- c1.phrasal-idioms --------------------------------------------------
  "c1.phrasal-idioms.word.give-up": ["او سال پیش سیگار را ترک کرد.", "Don't give up before the finish line.", "تا خط پایان تسلیم نشو."],
  "c1.phrasal-idioms.word.put-off": ["آن‌ها جلسه را تا جمعه عقب انداختند.", "Don't put off your homework until tonight.", "کارهایت را به امشب موکول نکن."],
  "c1.phrasal-idioms.word.come-across": ["دیروز به‌طور اتفاقی به یک عکس قدیمی برخوردم.", "I came across a useful article this morning.", "صبح اتفاقی یک مقالهٔ مفید پیدا کردم."],
  "c1.phrasal-idioms.word.get-away-with": ["فکر می‌کرد می‌تواند با دروغ‌گویی فرار کند.", "You can't get away with cheating in this class.", "در این کلاس نمی‌توانی با تقلب فرار کنی."],
  "c1.phrasal-idioms.word.run-out-of": ["صبح شیرمان تمام شد.", "We are running out of time.", "وقتمان دارد تمام می‌شود."],
  "c1.phrasal-idioms.word.bring-up": ["سر شام سیاست را پیش نکش.", "He never brings up his past at parties.", "او هرگز گذشته‌اش را در مهمانی‌ها پیش نمی‌کشد."],

  // --- c2.nuance ----------------------------------------------------------
  "c2.nuance.word.ambiguous": ["پاسخ او عمداً مبهم بود.", "The wording of the contract is ambiguous.", "متن قرارداد مبهم است."],
  "c2.nuance.word.subtle": ["بین این دو کلمه تفاوت ظریفی هست.", "He gave a subtle hint about the price.", "او اشارهٔ ظریفی دربارهٔ قیمت کرد."],
  "c2.nuance.word.connotation": ["این کلمه بار منفی دارد.", "A lion can have a positive connotation.", "واژهٔ شیر می‌تواند بار مثبت داشته باشد."],
  "c2.nuance.word.vague": ["دستورالعمل‌ها به‌طرز ناامیدکننده‌ای مبهم بودند.", "His answer was too vague to be useful.", "پاسخ او بیش از حد مبهم بود."],
  "c2.nuance.word.distinction": ["تفاوت بین دو پیشنهاد قابل توجه است.", "The law makes a clear distinction between the two cases.", "قانون بین دو حالت تمایز روشنی قائل است."],
  "c2.nuance.word.imply": ["گزارش دلالت بر افزایش هزینه‌ها دارد.", "What do you imply by that comment?", "با آن نظر چه منظوری داری؟"],

  // --- c2.rhetoric --------------------------------------------------------
  "c2.rhetoric.word.eloquence": ["او با فصاحت هیئت منصفه را تحت تأثیر قرار داد.", "Her eloquence won the debate for the team.", "فصاحت او مناظره را برای تیمش برد."],
  "c2.rhetoric.word.refute": ["هیچ‌کس نتوانست منطق او را رد کند.", "The data refute that theory.", "داده‌ها آن نظریه را رد می‌کنند."],
  "c2.rhetoric.word.concision": ["از مقاله به خاطر ایجازش تقدیر شده است.", "Good reports are known for their concision.", "گزارش‌های خوب به ایجازشان شناخته می‌شوند."],
  "c2.rhetoric.word.irony": ["طنز تلخ پایان‌بندی از او پنهان ماند.", "She didn't notice the irony in his reply.", "او طنز پاسخ او را متوجه نشد."],
  "c2.rhetoric.word.arrogance": ["غرور و خودبینی او کل تیم را آزار داد.", "The manager's arrogance cost the company its clients.", "غرور مدیر باعث شد شرکت مشتریانش را از دست بدهد."],
  "c2.rhetoric.word.persuade": ["او کمیته را متقاعد کرد رأی‌اش را عوض کند.", "Can you persuade your father to agree?", "می‌توانی پدرت را راضی کنی موافقت کند؟"],

  // --- c2.science ---------------------------------------------------------
  "c2.science.word.empirical": ["این ادعا فاقد پشتوانهٔ تجربی است.", "The theory needs empirical evidence.", "این نظریه به شواهد تجربی نیاز دارد."],
  "c2.science.word.corroborate": ["نتایج فرضیهٔ اولیه را تأیید می‌کنند.", "Two witnesses corroborated his story.", "دو شاهد روایت او را تأیید کردند."],
  "c2.science.word.speculation": ["گمانه‌زنی‌های زیادی دربارهٔ علت شده است.", "Speculation about the merger drove the shares up.", "گمانه‌زنی دربارهٔ ادغام سهام را بالا برد."],
  "c2.science.word.variable": ["دما تنها متغیر بود.", "Repeat the experiment with one variable at a time.", "آزمایش را هر بار با یک متغیر تکرار کنید."],
  "c2.science.word.precise": ["لطفاً اندازه‌گیری دقیق بدهید.", "Be precise about the time of the meeting.", "دربارهٔ ساعت جلسه دقیق باشید."],
  "c2.science.word.phenomenon": ["نورهای شمالی پدیدهٔ طبیعی‌اند.", "The eclipse is a rare phenomenon.", "کسوف پدیدهٔ نادری است."],

  // --- c2.literature ------------------------------------------------------
  "c2.literature.word.narrator": ["راوی غیرقابل اعتماد است.", "The narrator tells the story in the past tense.", "راوی داستان را در زمان گذشته روایت می‌کند."],
  "c2.literature.word.protagonist": ["قهرمان داستان در فصل آخر شکست می‌خورد.", "The protagonist changes between the first and last pages.", "قهرمان داستان بین اولین و آخرین صفحه تغییر می‌کند."],
  "c2.literature.word.allegory": ["منتقدان پایان را تمثیلی از شکست دیدند.", "The forest in the poem is an allegory for the mind.", "جنگل در این شعر تمثیلی از ذهن است."],
  "c2.literature.word.foreshadowing": ["طوفان پیش‌درآمد سقوط قهرمان است.", "The dark opening is a classic piece of foreshadowing.", "آغاز تاریک نمونهٔ کلاسیکی از پیش‌درآمد است."],
  "c2.literature.word.imagery": ["تصویرسازی شعر زنده است.", "The poem uses imagery of fire and ice.", "شعر از تصویرسازی آتش و یخ استفاده می‌کند."],
  "c2.literature.word.metaphor": ["«زندگی سفر است» استعاره‌ای رایج است.", "Calling the sea a hungry animal is a metaphor.", "دریا را حیوان گرسنه خواندن یک استعاره است."],

  // --- a1.introductions ---------------------------------------------------
  "a1.introductions.word.name": ["اسم من سارا است.", "Please say your name slowly.", "لطفاً اسم‌ات را آرام بگو."],
  "a1.introductions.word.friend": ["این دوست من علی است.", "My friend lives next door.", "دوستم همسایهٔ ماست."],
  "a1.introductions.word.meet": ["از آشنایی‌تان خوشوقتم.", "We meet at the station at nine.", "ساعت نه در ایستگاه همدیگر را می‌بینیم."],
  "a1.introductions.word.new": ["او همکار جدید من است.", "I bought a new phone last week.", "هفتهٔ پیش گوشی جدید خریدم."],
  "a1.introductions.word.work": ["من در بیمارستان کار می‌کنم.", "Hard work pays off in the end.", "کار سخت بالاخره جواب می‌دهد."],
  "a1.introductions.word.live": ["من در تهران زندگی می‌کنم.", "My grandparents live in a village.", "پدربزرگ و مادربزرگم در روستا زندگی می‌کنند."],

  // --- a1.weather ---------------------------------------------------------
  "a1.weather.word.rain": ["باران بعدازظهر بند آمد.", "Take an umbrella — rain is expected.", "چتر بردار — باران پیش‌بینی شده است."],
  "a1.weather.word.sunny": ["امروز آفتابی است.", "It will be sunny at the weekend.", "آخر هفته هوا آفتابی خواهد بود."],
  "a1.weather.word.cloud": ["ابرهای تاریک روی شهر است.", "A grey cloud covered the sun.", "ابرهای خاکستری خورشید را پوشاندند."],
  "a1.weather.word.wind": ["امروز باد شدید است.", "The wind blew the doors shut.", "باد درها را بست."],
  "a1.weather.word.season": ["بهار فصل مورد علاقهٔ من است.", "Which season do you like best?", "کدام فصل را بیشتر دوست داری؟"],
  "a1.weather.word.warm": ["هوای فروردین گرم است.", "The water is warm enough to swim.", "آب به اندازه کافی گرم است که بتوان شنا کرد."],

  // --- a2.hobbies ---------------------------------------------------------
  "a2.hobbies.word.hobby": ["عکاسی سرگرمی اصلی من است.", "My hobby keeps me busy at weekends.", "سرگرمی‌ام آخر هفته‌ها مرا سرگرم نگه می‌دارد."],
  "a2.hobbies.word.collect": ["او سکه‌های قدیمی جمع می‌کند.", "I collect postcards from every city.", "من از هر شهری کارت پستال جمع می‌کنم."],
  "a2.hobbies.word.painting": ["او یکشنبه‌ها نقاشی می‌کند.", "His painting hangs in the living room.", "تابلوی نقاشی او در نشیمن آویزان است."],
  "a2.hobbies.word.photography": ["سال پیش کلاس عکاسی رفتم.", "Good photography needs patience.", "عکاسی خوب صبوری می‌خواهد."],
  "a2.hobbies.word.member": ["من عضو باشگاه شطرنج هستم.", "Every member gets a discount card.", "هر عضو کارت تخفیف می‌گیرد."],
  "a2.hobbies.word.spare-time": ["در اوقات فراغت چه می‌کنی؟", "I read in my spare time.", "در اوقات فراغت کتاب می‌خوانم."],

  // --- a2.city-life -------------------------------------------------------
  "a2.city-life.word.neighbourhood": ["محلهٔ ما خیلی آرام است.", "The new bakery opened in our neighbourhood.", "نانوایی جدید در محلهٔ ما باز شد."],
  "a2.city-life.word.crowded": ["بازار جمعه‌ها شلوغ است.", "The train was crowded this morning.", "امروز صبح قطار شلوغ بود."],
  "a2.city-life.word.traffic": ["صبح‌ها ترافیک سنگین است.", "Avoid the centre because the traffic is terrible.", "از مرکز شهر دوری کن چون ترافیک وحشتناک است."],
  "a2.city-life.word.convenient": ["این آپارتمان به ایستگاه نزدیک و راحت است.", "It is convenient to pay by card.", "با کارت پرداخت کردن راحت است."],
  "a2.city-life.word.park": ["هر عصر در پارک قدم می‌زنیم.", "The children played in the park.", "بچه‌ها در پارک بازی کردند."],
  "a2.city-life.word.subway": ["من با مترو به سر کار می‌روم.", "The subway is faster than a bus in rush hour.", "در ساعت شلوجی مترو از اتوبوس سریع‌تر است."],

  // --- b1.money -----------------------------------------------------------
  "b1.money.word.budget": ["ما بودجهٔ ماهانه‌ای برای خوراک تعیین کردیم.", "The project went over its budget.", "پروژه از بودجه‌اش فراتر رفت."],
  "b1.money.word.save": ["من هر ماه مبلغ کمی پس‌انداز می‌کنم.", "I saved enough for a new bike.", "آن‌قدر پس‌انداز کردم که دوچرخهٔ جدید بخرم."],
  "b1.money.word.expense": ["اجاره بزرگ‌ترین هزینهٔ ماست.", "Travel is a small expense for this job.", "سفر برای این کار هزینهٔ کمی است."],
  "b1.money.word.afford": ["امسال توان خرید ماشین جدید را نداریم.", "He couldn't afford the repair bill.", "او از پس هزینهٔ تعمیرات برنمی‌آمد."],
  "b1.money.word.debt": ["او سخت کار می‌کند تا قرضش را بدهد.", "She cleared her debt in two years.", "او قرضش را در دو سال تسویه کرد."],
  "b1.money.word.income": ["درآمد او بعد از ارتقا بیشتر شد.", "A second income helps the family.", "درآمد دوم به خانواده کمک می‌کند."],

  // --- b1.health-lifestyle ------------------------------------------------
  "b1.health-lifestyle.word.balanced-diet": ["رژیم غذایی متعادل میوه و سبزیجات دارد.", "A balanced diet and enough sleep keep you healthy.", "رژیم غذایی متعادل و خواب کافی شما را سالم نگه می‌دارد."],
  "b1.health-lifestyle.word.exercise": ["ورزش منظم استرس را کم می‌کند.", "Doctors recommend exercise three times a week.", "پزشکان ورزش سه بار در هفته را توصیه می‌کنند."],
  "b1.health-lifestyle.word.stress": ["استرس زیاد بر خوابت تأثیر می‌گذارد.", "Deadlines cause a lot of stress at work.", "مهلت‌ها باعث استرس زیاد در کار می‌شوند."],
  "b1.health-lifestyle.word.sleep": ["بیشتر بزرگسالان به هشت ساعت خواب نیاز دارند.", "I sleep better without coffee in the evening.", "بدون قهوهٔ عصر بهتر می‌خوابم."],
  "b1.health-lifestyle.word.habit": ["کتاب خواندن قبل از خواب عادت خوبی است.", "It takes three weeks to build a habit.", "ساختن یک عادت سه هفته طول می‌کشد."],
  "b1.health-lifestyle.word.skip": ["هرگز قبل از امتحان صبحانه را حذف نکن.", "We skipped the last stop and walked home.", "ما از ایستگاه آخر رد شدیم و پیاده به خانه رفتیم."],

  // --- b2.problems --------------------------------------------------------
  "b2.problems.word.complaint": ["ما شکایتی دربارهٔ تأخیر دریافت کردیم.", "The company replied to every complaint.", "شرکت به هر شکایتی پاسخ داد."],
  "b2.problems.word.refund": ["مغازه بازپرداخت کامل پیشنهاد داد.", "You can ask for a refund within thirty days.", "می‌توانید ظرف سی روز بازپرداخت بخواهید."],
  "b2.problems.word.resolve": ["مشکل ظرف دو روز حل شد.", "The two sides resolved the dispute calmly.", "دو طرف اختلاف را با آرامش حل کردند."],
  "b2.problems.word.deadline": ["ما با مهلتی فشرده کار می‌کنیم.", "Miss the deadline and you lose the bonus.", "اگر مهلت را از دست بدهی پاداش را هم از دست می‌دهی."],
  "b2.problems.word.delay": ["در فرودگاه دو ساعت تأخیر بود.", "The delay cost us the connection.", "تأخیر باعث شد پرواز اتصالی را از دست بدهیم."],
  "b2.problems.word.compensate": ["هواپیمایی موافقت کرد به مسافران غرامت بدهد.", "Nothing can compensate for the lost time.", "هیچ‌چی نمی‌تواند زمان از دست رفته را جبران کند."],

  // --- b2.research --------------------------------------------------------
  "b2.research.word.evidence": ["برای این ادعا شواهد اندکی هست.", "The researchers published new evidence.", "پژوهشگران شواهد جدیدی منتشر کردند."],
  "b2.research.word.hypothesis": ["داده‌ها از فرضیهٔ اولیه حمایت می‌کنند.", "If the hypothesis is wrong, the study fails.", "اگر فرضیه غلط باشد، پژوهش شکست می‌خورد."],
  "b2.research.word.sample": ["نمونه برای قابل اعتماد بودن خیلی کوچک بود.", "The survey used a sample of a thousand people.", "این نظرسنجی از نمونهٔ هزار نفری استفاده کرد."],
  "b2.research.word.indicate": ["نتایج روند روشنی نشان می‌دهند.", "High prices indicate low demand.", "قیمت‌های بالا نشانهٔ تقاضای پایین است."],
  "b2.research.word.reliable": ["به داده‌های قابل اعتماد بیشتری نیاز داریم.", "This bus service is reliable in winter.", "این خط اتوبوس در زمستان قابل اعتماد است."],
  "b2.research.word.bias": ["این پژوهش به خاطر جانبداری‌اش انتقاد شد.", "Reporters should check their bias before writing.", "خبرنگاران قبل از نوشتن باید جانبداری‌شان را بررسی کنند."],

  // --- c1.inference -------------------------------------------------------
  "c1.inference.word.imply": ["سکوت او رضایت را نشان می‌داد.", "What do these figures imply about growth?", "این ارقام دربارهٔ رشد چه چیزی را دلالت می‌کنند؟"],
  "c1.inference.word.infer": ["از لحن او دریافتیم که مخالف است.", "We can infer nothing from these results.", "از این نتایج نمی‌توانیم نتیجه‌گیری کنیم."],
  "c1.inference.word.assumption": ["این استدلال بر فرضیه‌ای زیر سؤال استوار است.", "The plan rests on the assumption that rents stay low.", "این طرح بر این فرض استوار است که اجاره‌ها پایین بماند."],
  "c1.inference.word.ambiguity": ["این عبارت‌بندی جا برای ابهام باقی می‌گذارد.", "The ambiguity in the rule confused everyone.", "ابهام در قانون همه را گیج کرد."],
  "c1.inference.word.overstate": ["تأثیر اصلاحات را نمی‌توان بیش از حد بیان کرد.", "Ads often overstate the benefits of a product.", "تبلیغات اغلب فواید محصول را بزرگ‌نمایی می‌کنند."],
  "c1.inference.word.unspoken": ["بین آن‌ها توافقی ضمنی وجود داشت.", "The unspoken rule of the office is to arrive early.", "قانون ضمنی دفتر این است که زود برسی."],

  // --- c1.register --------------------------------------------------------
  "c1.register.word.register": ["این نامه در تمامش از لحن رسمی استفاده می‌کند.", "Switch to an informal register with friends.", "با دوستان از لحن غیررسمی استفاده کن."],
  "c1.register.word.formal": ["از پاسخ رسمی شما سپاسگزار می‌شویم.", "The invitation looks very formal.", "دعوت‌نامه خیلی رسمی به نظر می‌رسد."],
  "c1.register.word.colloquial": ["این دیالوگ پر از عبارت‌های عامیانه است.", "Colloquial English differs from written English.", "انگلیسی عامیانه با انگلیسی نوشتاری فرق دارد."],
  "c1.register.word.nominalisation": ["نوشتن آکادمیک زیاد به اسم‌سازی تکیه دارد.", "Nominalisation makes a text dense and formal.", "اسم‌سازی متن را فشرده و رسمی می‌کند."],
  "c1.register.word.hedge": ["نویسندگان ادعاهایشان را با may و tends to محتاطانه بیان می‌کنند.", "A good report hedges its predictions.", "گزارش خوب پیش‌بینی‌هایش را محتاطانه بیان می‌کند."],
  "c1.register.word.courtesy": ["او لطف کرد و همان روز پاسخ داد.", "He had the courtesy to thank every guest.", "او لطف کرد و از هر مهمانی تشکر کرد."],

  // --- c1.cohesion --------------------------------------------------------
  "c1.cohesion.word.cohesion": ["پیوستگی خوب مقاله را دنبال‌کردنی می‌کند.", "Lack of cohesion confuses the reader.", "نبود پیوستگی خواننده را گیج می‌کند."],
  "c1.cohesion.word.discourse-marker": ["با این حال و بنابراین نشانگرهای گفتاری رایجی‌اند.", "She used a discourse marker to connect the ideas.", "او از نشانگر گفتاری برای وصل کردن ایده‌ها استفاده کرد."],
  "c1.cohesion.word.antecedent": ["این ضمیر در این بند مرجع روشنی ندارد.", "Every pronoun needs a clear antecedent.", "هر ضمیری به مرجع روشن نیاز دارد."],
  "c1.cohesion.word.ellipsis": ["حذف، دیالوگ را طبیعی و سریع نگه می‌دارد.", "English uses ellipsis in short answers.", "انگلیسی در پاسخ‌های کوتاه از حذف استفاده می‌کند."],
  "c1.cohesion.word.paraphrase": ["باید منبع را بازنویسی کنی نه اینکه کپی کنی.", "Paraphrase the quote in your own words.", "نقل‌قول را با کلمات خودت بازنویسی کن."],
  "c1.cohesion.word.transition": ["گذار بین دو بخش ناگهانی است.", "Add a smooth transition between the paragraphs.", "بین بندها یک گذار روان اضافه کن."],

  // --- c2.argumentation ---------------------------------------------------
  "c2.argumentation.word.fallacy": ["حمله به گوینده مغالطهٔ کلاسیک است.", "Confusing correlation with cause is a common fallacy.", "اتحاد همبستگی با علت مغالطهٔ رایجی است."],
  "c2.argumentation.word.concede": ["او این نکته را پذیرفت و به مسئلهٔ اصلی پرداخت.", "The minister conceded that the policy had failed.", "وزیر قبول کرد سیاست شکست خورده است."],
  "c2.argumentation.word.refute": ["داده‌ها توضیح قبلی را رد می‌کنند.", "No study has managed to refute this result.", "هیچ مطالعه‌ای نتوانسته این نتیجه را رد کند."],
  "c2.argumentation.word.premise": ["نتیجه از مقدمات استنباط نمی‌شود.", "The argument is sound if the premise is true.", "استدلال درست است اگر مقدمه درست باشد."],
  "c2.argumentation.word.assertion": ["مقاله به‌جای شواهد ادعا ارائه می‌دهد.", "Bare assertion is not enough to win an argument.", "ادعای خالی برای بردن بحث کافی نیست."],
  "c2.argumentation.word.straw-man": ["این یک «مرغ پوشالی» است؛ هیچ‌کس چنین سیاستی پیشنهاد نداده.", "Your reply builds a straw man out of my argument.", "پاسخ شما از استدلال من یک مرغ پوشالی می‌سازد."],

  // --- c2.collocation -----------------------------------------------------
  "c2.collocation.word.collocation": ["«make a decision» هم‌نشینی قوی است؛ «do a decision» نیست.", "Teachers explain why collocations matter.", "معلمان توضیح می‌دهند چرا هم‌نشینی‌ها اهمیت دارند."],
  "c2.collocation.word.collocate": ["heavily با rain و criticism هم‌نشین می‌شود.", "The verb make collocates with thousands of nouns.", "فعل make با هزاران اسم هم‌نشین می‌شود."],
  "c2.collocation.word.delexical-verb": ["have و take و make فعل‌های غیرمعنایی رایجی‌اند.", "In take a shower, take is a delexical verb.", "در take a shower، فعل take یک فعل غیرمعنایی است."],
  "c2.collocation.word.lexical": ["مقاله دامنهٔ واژگانی تحسین‌برانگیزی نشان می‌دهد.", "A good dictionary lists every lexical item.", "فرهنگ خوب هر واحد واژگانی را فهرست می‌کند."],
  "c2.collocation.word.idiomatic": ["انگلیسی او روان و بسیار اصطلاحی است.", "Try to write more idiomatic sentences.", "سعی کن جمله‌های اصطلاحی‌تری بنویسی."],
  "c2.collocation.word.precision": ["این گزارش با دقت نوشته شده است.", "The operation required surgical precision.", "این عمل به دقت جراحی نیاز داشت."],

  // --- c2.irony -----------------------------------------------------------
  "c2.irony.word.understatement": ["طوفان را «کمی باران» خواندن نمونهٔ خوبی از کم‌نمایی است.", "Saying the exam was not easy is an understatement.", "گفتن امتحان آسان نبود، کم‌نمایی است."],
  "c2.irony.word.litotes": ["«not uncommon» یک litotes معمولی است به جای «fairly common».", "Calling the meal not bad is a common litotes.", "غذا را «نه بد» خواندن litotes رایجی است."],
  "c2.irony.word.deadpan": ["او شوخی را با چهرهٔ بی‌تفاوت گفت.", "Her deadpan delivery made the joke funnier.", "نحوهٔ بی‌تفاوتِ او شوخی را خنده‌دارتر کرد."],
  "c2.irony.word.subtext": ["زیرمتن این صحنه این است که او از قبل می‌داند.", "The subtext of the letter is a threat.", "زیرمتن نامه یک تهدید است."],
  "c2.irony.word.self-deprecating": ["شوخی خودکم‌بینانهٔ او همه را راحت کرد.", "His self-deprecating style wins the audience.", "سبک خودکم‌بینانهٔ او تماشاگران را جذب می‌کند."],
  "c2.irony.word.facetious": ["این حرف شوخی نابجا بود و بد موقع زده شد.", "Don't answer with a facetious remark.", "با یک حرف شوخی‌مآبانه جواب نده."],

  // --- a1.articles --------------------------------------------------------
  "a1.articles.word.umbrella": ["داره بارون می‌اد — چتر بردار.", "I left my umbrella at home.", "چترم را خانه جا گذاشتم."],
  "a1.articles.word.apple": ["او هر روز یک سیب می‌خورد.", "This apple is too sour.", "این سیب خیلی ترش است."],
  "a1.articles.word.orange": ["لطفاً یک پرتقال می‌خواستم.", "Can you peel an orange for me?", "می‌توانی یک پرتقال برایم پوست بگیری؟"],
  "a1.articles.word.hour": ["سفر یک ساعت طول می‌کشد.", "Wait an hour and call me again.", "یک ساعت صبر کن و دوباره زنگ بزن."],
  "a1.articles.word.sun": ["خورشید از مشرق طلوع می‌کند.", "Don't look straight at the sun.", "مستقیم به خورشید نگاه نکن."],
  "a1.articles.word.hotel": ["ما در هتلی کنار دریا اقامت کردیم.", "The hotel offers a free breakfast.", "هتل صبحانهٔ رایگان دارد."],

  // --- a1.plurals ---------------------------------------------------------
  "a1.plurals.word.child": ["کلاس بیست بچه دارد.", "Every child in the street got a balloon.", "هر بچه‌ای در خیابان بادکنکی گرفت."],
  "a1.plurals.word.man": ["آن مرد بیرون منتظر است.", "A tall man opened the door.", "مردی بلندقد در را باز کرد."],
  "a1.plurals.word.woman": ["دو زن در این دفتر کار می‌کنند.", "The woman next door is a doctor.", "زن همسایه پزشک است."],
  "a1.plurals.word.foot": ["پای چپم درد می‌کند.", "The stone hurt my foot.", "سنگ پایم را آسیب زد."],
  "a1.plurals.word.leaf": ["برگ خشکی از درخت افتاد.", "Each leaf turns yellow in autumn.", "هر برگی در پاییز زرد می‌شود."],
  "a1.plurals.word.box": ["کتاب‌ها را در جعبه بگذار.", "This box is too heavy to carry.", "این جعبه برای حمل خیلی سنگین است."],

  // --- a1.pronouns --------------------------------------------------------
  "a1.pronouns.word.friend": ["دوستم در تهران زندگی می‌کند.", "Their friend studies medicine.", "دوستشان پزشکی می‌خواند."],
  "a1.pronouns.word.teacher": ["معلم ما آرام انگلیسی حرف می‌زند.", "The teacher wrote the date on the board.", "معلم تاریخ را روی تخته نوشت."],
  "a1.pronouns.word.sister": ["خواهرش در بیمارستان کار می‌کند.", "My sister teaches math.", "خواهرم ریاضی تدریس می‌کند."],
  "a1.pronouns.word.brother": ["او به برادرش در تکلیف کمک می‌کند.", "Their brother lives abroad.", "برادرشان در خارج زندگی می‌کند."],
  "a1.pronouns.word.doctor": ["دکتر بیمار را معاینه کرد.", "You should see a doctor about that cough.", "برای آن سرفه باید به دکتر مراجعه کنی."],
  "a1.pronouns.word.student": ["معلم به دانش‌آموزان کمک کرد.", "Every student needs a notebook.", "هر دانش‌آموزی دفتر لازم دارد."],

  // --- a1.can -------------------------------------------------------------
  "a1.can.word.swim": ["من شنا بلدم اما شیرجه نه.", "Can you swim across the river?", "می‌توانی از رودخانه شنا کنی؟"],
  "a1.can.word.drive": ["او رانندگی بلد است.", "I can't drive yet.", "هنوز رانندگی یاد نگرفته‌ام."],
  "a1.can.word.speak": ["او سه زبان بلند است.", "Can you speak a little slower?", "می‌توانی کمی آرام‌تر حرف بزنی؟"],
  "a1.can.word.cook": ["پدرم خیلی خوب آشپزی می‌کند.", "I can cook rice but not kebab.", "من برنج درست می‌کنم اما کباب نه."],
  "a1.can.word.play": ["می‌توانی گیتار بزنی؟", "The children can play in the garden.", "بچه‌ها می‌توانند در باغ بازی کنند."],
  "a1.can.word.run": ["او می‌تواند پنج کیلومتر بدوشد.", "I can run faster than my brother.", "می‌توانم سریع‌تر از برادرم بدوهم."],

  // --- a1.imperatives -----------------------------------------------------
  "a1.imperatives.word.stop": ["ایست! چراغ قرمز است.", "Stop talking and open your books.", "حرف نزنید و کتاب‌هاتان را باز کنید."],
  "a1.imperatives.word.wait": ["سر گیت منتظرم باش.", "Please wait two minutes.", "لطفاً دو دقیقه صبر کن."],
  "a1.imperatives.word.sit": ["لطفاً بنشینید.", "Sit next to your sister.", "کنار خواهرت بنشین."],
  "a1.imperatives.word.open": ["لطفاً کتاب‌هاتان را باز کنید.", "Open the window — it's hot in here.", "پنجره را باز کن — اینجا گرم است."],
  "a1.imperatives.word.close": ["لطفاً پنجره را ببند.", "Close your eyes and count to ten.", "چشم‌هایت را ببند و تا ده بشمار."],
  "a1.imperatives.word.listen": ["به دستورالعمل‌ها دقیق گوش بده.", "Listen to the news every morning.", "هر صبح به خبر گوش بده."],

  // --- a1.question-words --------------------------------------------------
  "a1.question-words.word.what": ["اسم تو چیست؟", "What time does the film start?", "فیلم ساعت چند شروع می‌شود؟"],
  "a1.question-words.word.where": ["کجا زندگی می‌کنی؟", "Where did you buy those shoes?", "این کفش‌ها را از کجا خریدی؟"],
  "a1.question-words.word.when": ["کلاس کی شروع می‌شود؟", "When is your birthday?", "تولدت کی است؟"],
  "a1.question-words.word.why": ["چرا دیر آمدی؟", "Why do we have to wait?", "چرا باید صبر کنیم؟"],
  "a1.question-words.word.who": ["آن زن کیست؟", "Who taught you to drive?", "چه کسی به تو رانندگی یاد داد؟"],
  "a1.question-words.word.how": ["این را چطور تلفظ می‌کنی؟", "How far is the station from here?", "ایستگاه از اینجا چقدر فاصله دارد؟"],

  // --- a2.past-simple -----------------------------------------------------
  "a2.past-simple.word.go": ["آخر هفتهٔ پیش به ساحل رفتیم.", "She went home before the rain.", "او قبل از باران به خانه رفت."],
  "a2.past-simple.word.buy": ["دیروز او گوشی جدیدی خرید.", "We bought fresh bread this morning.", "امروز صبح نان تازه خریدیم."],
  "a2.past-simple.word.see": ["در بازار یک دوست قدیمی دیدم.", "They saw a fox near the village.", "نزدیک روستا یک روباه دیدند."],
  "a2.past-simple.word.eat": ["در رستوران کوچکی غذا خوردیم.", "The children ate all the cake.", "بچه‌ها کل کیک را خوردند."],
  "a2.past-simple.word.take": ["قطار دو ساعت طول کشید.", "She took the early flight home.", "او پرواز زودتر را گرفت و به خانه برگشت."],
  "a2.past-simple.word.write": ["دیروز شب یک ایمیل نوشت.", "She wrote a long reply this morning.", "امروز صبح پاسخ بلندی نوشت."],

  // --- a1.alphabet --------------------------------------------------------
  "a1.alphabet.word.alphabet": ["الفبای انگلیسی بیست و شش حرف دارد.", "The Persian alphabet has thirty-two letters.", "الفبای فارسی سی و دو حرف دارد."],
  "a1.alphabet.word.letter": ["حرف A اولین حرف است.", "There is a letter missing in this word.", "در این کلمه یک حرف جا افتاده است."],
  "a1.alphabet.word.capital-letter": ["هر اسمی با حرف بزرگ شروع می‌شود.", "Write your surname with a capital letter.", "نام خانوادگی‌ات را با حرف بزرگ بنویس."],
  "a1.alphabet.word.small-letter": ["آدرس ایمیلت را با حروف کوچک بنویس.", "Use a small letter after a full stop.", "بعد از نقطه از حرف کوچک استفاده کن."],
  "a1.alphabet.word.vowel": ["حرف‌های A و E و I و O و U واکه‌ها هستند.", "The word 'cat' has one vowel.", "کلمهٔ cat یک واکه دارد."],
  "a1.alphabet.word.sound": ["هر حرف صدای خودش را دارد.", "The sound of the bell woke us up.", "صدای زنگ ما را بیدار کرد."],

  // --- a1.spelling --------------------------------------------------------
  "a1.spelling.word.spell": ["لطفاً اسم‌ات را هجی کنی؟", "I spelled my surname wrong on the form.", "نام خانوادگی‌ام را در فرم اشتباه هجی کردم."],
  "a1.spelling.word.name": ["اسم من سارا است.", "Write your name at the top of the page.", "اسم‌ات را بالای صفحه بنویس."],
  "a1.spelling.word.surname": ["نام خانوادگی‌اش احمدی است.", "Please write your surname in full.", "لطفاً نام خانوادگی‌ات را کامل بنویس."],
  "a1.spelling.word.address": ["لطفاً آدرس‌ات را اینجا بنویس.", "My home address is on the form.", "آدرس خانه‌ام روی فرم است."],
  "a1.spelling.word.double": ["اسم من دو تا L دارد.", "The word 'balloon' has a double l.", "کلمهٔ balloon دو تا l پشت سر هم دارد."],
  "a1.spelling.word.email": ["لطفاً برایم ایمیل بزن.", "I check my email every morning.", "هر صبح ایمیلم را چک می‌کنم."],

  // --- a1.numbers ---------------------------------------------------------
  "a1.numbers.word.zero": ["صفر قبل از یک می‌آید.", "The temperature fell to zero last night.", "دما دیروز شب به صفر رسید."],
  "a1.numbers.word.one": ["من یک برادر دارم.", "There is only one exit from the building.", "از ساختمان فقط یک خروجی هست."],
  "a1.numbers.word.two": ["دو به علاوهٔ دو می‌شود چهار.", "Wait two minutes and try again.", "دو دقیقه صبر کن و دوباره امتحان کن."],
  "a1.numbers.word.three": ["روی میز سه کتاب هست.", "My daughter is three years old.", "دخترم سه ساله است."],
  "a1.numbers.word.twelve": ["یک سال دوازده ماه دارد.", "The clock struck twelve.", "ساعت دوازده را زد."],
  "a1.numbers.word.twenty": ["کلاس بیست دانش‌آموز دارد.", "The hotel room costs twenty dollars a night.", "اتاق هتل شبی بیست دلار است."],
  "a1.numbers.word.hundred": ["صد ده تا ده است.", "The book has a hundred pages.", "کتاب صد صفحه دارد."],
  "a1.numbers.word.number": ["شمارهٔ تلفنت چیست؟", "Please say your student number slowly.", "لطفاً شمارهٔ دانشجویی‌ات را آرام بگو."],
};

const read = (n) => JSON.parse(fs.readFileSync(`${DIR}/${n}.json`, "utf8"));
const write = (n, o) => fs.writeFileSync(`${DIR}/${n}.json`, JSON.stringify(o, null, 2) + "\n");

const vocab = read("vocabulary");
const problems = [];
const seen = new Set();
for (const entry of vocab.vocabulary) {
  const rec = B[entry.id];
  if (!rec) {
    problems.push(`no data for ${entry.id}`);
    continue;
  }
  seen.add(entry.id);
  const [fa1, en2, fa2] = rec;
  const first = entry.examples[0];
  const en1 = typeof first === "string" ? first : first?.en;
  if (!en1) problems.push(`${entry.id}: original example missing`);
  for (const [field, value] of [["fa1", fa1], ["en2", en2], ["fa2", fa2]]) {
    if (typeof value !== "string" || value.trim() === "") problems.push(`${entry.id}: blank ${field}`);
  }
  entry.examples = [
    { en: en1, fa: fa1 },
    { en: en2.trim(), fa: fa2 },
  ];
}
for (const id of Object.keys(B)) {
  if (!seen.has(id)) problems.push(`stale data for ${id}`);
}
if (problems.length > 0) {
  console.error(`ABORT — ${problems.length} problem(s):\n${problems.join("\n")}`);
  process.exit(1);
}

write("vocabulary", vocab);
for (const n of FILES) {
  const root = read(n);
  if (root.contentVersion !== FROM) {
    console.error(`ABORT — ${n}.json contentVersion is ${root.contentVersion}, expected ${FROM}`);
    process.exit(1);
  }
  root.contentVersion = TO;
  write(n, root);
}
console.log(`OK — ${vocab.vocabulary.length} words now carry 2 bilingual examples; bundle ${FROM} -> ${TO}`);
