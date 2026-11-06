/**
 * Bundle 62 → 63: reading evidence across the path, and a skill tag on every
 * exercise that had none.
 *
 * Two problems this batch fixes:
 *
 * 1. **Reading was the thinnest of the three tracks Progress shows** (GRAMMAR /
 *    VOCABULARY / READING). It rested on 35 items in 13 of 192 lessons, so the
 *    Reading bar read «هنوز پاسخی در این محور ثبت نشده» for the whole path except
 *    a handful of stops. This batch adds one authored 3–4 sentence passage with two
 *    comprehension questions to five lessons per level — the passage is shared by
 *    both questions, which `ReadingContentTest` requires (a passage used by one
 *    question is a quiz item, not reading practice).
 *
 * 2. **147 exercises carried no `skill` at all** (52 multiple_choice, 41
 *    translation, 54 listening), so an answer on them moved no track. The tags
 *    follow the corpus' own convention, not a new one: translation is VOCABULARY
 *    (all 144 tagged translation items already are), listening is LISTENING, and a
 *    multiple_choice is READING when it carries a passage, VOCABULARY when it asks
 *    what a word means, and GRAMMAR otherwise (the cloze/usage items the corpus
 *    already tags that way).
 *
 * One-shot. It refuses to run twice: any `*.read.01` id means the bundle already
 * has this batch. See scripts/archive/README.md.
 */
import fs from "node:fs";
import path from "node:path";

const DIR = "app/src/main/assets/content/";
const read = (file) => JSON.parse(fs.readFileSync(path.join(DIR, file), "utf8"));
const write = (file, value) =>
  fs.writeFileSync(path.join(DIR, file), JSON.stringify(value, null, 2) + "\n");

const exercisesFile = read("exercises.json");
const exercises = exercisesFile.exercises;

if (exercises.some((e) => /\.read\.\d+$/.test(e.id))) {
  console.error("v63 already applied — nothing to do.");
  process.exit(1);
}

const q = (question, questionFa, options, correct, fa) => ({ question, questionFa, options, correct, fa });

/** One passage + its two questions, per lesson. */
const clusters = [
  // ---------------------------------------------------------------- A1
  {
    lessonId: "a1.alphabet.lesson-01",
    passage:
      "My name is Sara. It starts with the letter S. My brother's name is Omar, and it starts with O. We both write our names with a capital letter.",
    questions: [
      q(
        "Which letter does Sara's name start with?",
        "نام سارا با کدام حرف شروع می‌شود؟",
        ["S", "M", "O", "A"],
        0,
        "جملهٔ دوم را بخوان: «It starts with the letter S» — حرف اولِ نام، حرف S است.",
      ),
      q(
        "How do we write the first letter of a name?",
        "حرف اولِ نام را چگونه می‌نویسیم؟",
        ["با حرف کوچک", "با حرف بزرگ", "اصلاً نمی‌نویسیم", "با عدد"],
        1,
        "capital letter یعنی «حرف بزرگ»؛ در انگلیسی حرف اولِ نام‌ها همیشه بزرگ نوشته می‌شود.",
      ),
    ],
  },
  {
    lessonId: "a1.articles.lesson-01",
    passage:
      "This is an apple and that is a banana. The apple is on the table. I want an egg, but the eggs are in the fridge.",
    questions: [
      q(
        "Which word comes before 'apple' in the first sentence?",
        "در جملهٔ اول پیش از apple کدام واژه آمده است؟",
        ["a", "an", "the", "some"],
        1,
        "apple با صدای صدادار شروع می‌شود، پس an می‌گیرد: an apple.",
      ),
      q(
        "Where are the eggs?",
        "تخم‌مرغ‌ها کجا هستند؟",
        ["روی میز", "در یخچال", "در کیف", "در مغازه"],
        1,
        "جملهٔ آخر می‌گوید «the eggs are in the fridge» — fridge یعنی یخچال.",
      ),
    ],
  },
  {
    lessonId: "a1.days-months.lesson-01",
    passage:
      "My week starts on Saturday. I work from Saturday to Wednesday. On Thursday I study English, and on Friday I visit my family. My birthday is in May.",
    questions: [
      q(
        "When does the speaker study English?",
        "گوینده کِی انگلیسی می‌خواند؟",
        ["پنجشنبه", "شنبه", "چهارشنبه", "جمعه"],
        0,
        "«On Thursday I study English» — Thursday یعنی پنجشنبه.",
      ),
      q(
        "Which month is the speaker's birthday in?",
        "تولد گوینده در کدام ماه است؟",
        ["March", "May", "July", "August"],
        1,
        "جملهٔ آخر: «My birthday is in May» — May یعنی ماه مه (اردیبهشت).",
      ),
    ],
  },
  {
    lessonId: "a1.food-drinks.lesson-01",
    passage:
      "For breakfast I eat bread, cheese and an egg. I drink tea, not coffee. At lunch I have rice with chicken. In the evening I only drink water.",
    questions: [
      q(
        "What does the speaker drink at breakfast?",
        "گوینده در صبحانه چه می‌نوشد؟",
        ["قهوه", "چای", "شیر", "آب"],
        1,
        "«I drink tea, not coffee» — tea یعنی چای؛ قهوه را صریحاً رد می‌کند.",
      ),
      q(
        "What is for lunch?",
        "ناهار چیست؟",
        ["نان و پنیر", "برنج با مرغ", "سوپ", "میوه"],
        1,
        "«At lunch I have rice with chicken» — rice برنج و chicken مرغ است.",
      ),
    ],
  },
  {
    lessonId: "a1.where-i-live.lesson-01",
    passage:
      "I live in a small flat near the park. My flat is on the second floor. There is a bakery under my flat, and a bus stop in front of the building. I walk to work every morning.",
    questions: [
      q(
        "Where is the bakery?",
        "نانوایی کجاست؟",
        ["زیر آپارتمان", "روبروی پارک", "طبقهٔ دوم", "کنار محل کار"],
        0,
        "«a bakery under my flat» — under یعنی زیر.",
      ),
      q(
        "How does the speaker go to work?",
        "گوینده چگونه به سر کار می‌رود؟",
        ["با اتوبوس", "با ماشین", "پیاده", "با دوچرخه"],
        2,
        "«I walk to work every morning» — walk یعنی پیاده رفتن؛ نزدیکی خانه دلیلش است.",
      ),
    ],
  },

  // ---------------------------------------------------------------- A2
  {
    lessonId: "a2.directions.lesson-01",
    passage:
      "Go straight along this street and take the second turning on the right. The bank is next to the pharmacy. If you reach the roundabout, you have gone too far.",
    questions: [
      q(
        "Which turning should you take?",
        "کدام پیچ را باید بگیرید؟",
        ["اولین پیچ سمت چپ", "دومین پیچ سمت راست", "دومین پیچ سمت چپ", "اولین پیچ سمت راست"],
        1,
        "«the second turning on the right» — second دومین و on the right سمت راست است.",
      ),
      q(
        "What does it mean if you reach the roundabout?",
        "اگر به میدان برسید یعنی چه؟",
        ["درست آمده‌اید", "باید برگردید و از اول بروید", "خیلی دور شده‌اید", "به بانک رسیده‌اید"],
        2,
        "«you have gone too far» یعنی از مقصد گذشته‌اید؛ این جمله‌ٔ هشدارِ راهنماست.",
      ),
    ],
  },
  {
    lessonId: "a2.plane-ticket.lesson-01",
    passage:
      "A return ticket to Istanbul costs 180 euros. A single is 100 euros. The morning flight leaves at 7:40, but you must be at the airport two hours before it. Check-in closes forty minutes before departure.",
    questions: [
      q(
        "How much does a single ticket cost?",
        "بلیت یک‌طرفه چند است؟",
        ["۱۸۰ یورو", "۱۰۰ یورو", "۸۰ یورو", "۱۴۰ یورو"],
        1,
        "«A single is 100 euros» — single یعنی یک‌طرفه؛ ۱۸۰ یورو بهای بلیت رفت‌وبرگشت است.",
      ),
      q(
        "When does check-in close?",
        "پذیرش بلیت کِی بسته می‌شود؟",
        ["دو ساعت پیش از پرواز", "چهل دقیقه پیش از پرواز", "هفت و چهل دقیقه", "نیم ساعت پیش از پرواز"],
        1,
        "«Check-in closes forty minutes before departure» — قبل از پرواز، چهل دقیقه.",
      ),
    ],
  },
  {
    lessonId: "a2.payments.lesson-01",
    passage:
      "You can pay by card or in cash. The shop does not accept cheques. If you pay by card, the machine asks for your PIN. Keep the receipt: you need it for a refund.",
    questions: [
      q(
        "Which way of paying is NOT accepted?",
        "کدام روش پرداخت پذیرفته نمی‌شود؟",
        ["کارت", "نقدی", "چک", "پرداخت با دستگاه"],
        2,
        "«The shop does not accept cheques» — cheque یعنی چک.",
      ),
      q(
        "What do you need if you want your money back?",
        "برای پس‌گرفتن پول به چه چیزی نیاز دارید؟",
        ["رمز کارت", "رسید", "تلفن فروشگاه", "مدرک شناسایی"],
        1,
        "«Keep the receipt: you need it for a refund» — receipt رسید و refund بازگرداندن پول است.",
      ),
    ],
  },
  {
    lessonId: "a2.emails.lesson-01",
    passage:
      "Start your email with 'Dear Mr Karimi'. Say why you are writing in the first sentence. Keep it short, and end with 'Best regards'. Read it once more before you send it.",
    questions: [
      q(
        "How should an email to Mr Karimi begin?",
        "ایمیل به آقای کریمی با چه عبارتی شروع می‌شود؟",
        ["Hi Karimi", "Dear Mr Karimi", "Best regards", "To Karimi"],
        1,
        "«Dear + Mr + family name» شکل رسمیِ آغاز ایمیل است؛ Best regards پایان نامه است.",
      ),
      q(
        "What should you do before sending?",
        "پیش از فرستادن چه باید کرد؟",
        ["ایمیل را یک بار دیگر بخوانید", "موضوع را حذف کنید", "ایمیل را بلندتر بنویسید", "نام خود را ننویسید"],
        0,
        "«Read it once more before you send it» — یک بار دیگر خواندن، اشتباه‌ها را پیش از ارسال پیدا می‌کند.",
      ),
    ],
  },
  {
    lessonId: "a2.everyday-problems.lesson-01",
    passage:
      "The tap in my kitchen was leaking, so I called a plumber. He came the same afternoon and changed a small washer. The bill was not as high as I expected.",
    questions: [
      q(
        "Why did the speaker call a plumber?",
        "چرا گوینده لوله‌کش خبر کرد؟",
        ["چون شیر آب چکه می‌کرد", "چون آب گرم نداشت", "چون لوله ترکیده بود", "چون دستشویی خراب بود"],
        0,
        "«The tap was leaking» — leak یعنی چکه کردن؛ tap شیر آب است.",
      ),
      q(
        "How was the bill compared with the speaker's expectation?",
        "صورت‌حساب در مقایسه با انتظار گوینده چگونه بود؟",
        ["بیشتر از انتظار", "دقیقاً همان‌قدر", "کمتر از انتظار", "گوینده چیزی نمی‌گوید"],
        2,
        "«not as high as I expected» یعنی از آنچه فکر می‌کرد، کمتر بود.",
      ),
    ],
  },

  // ---------------------------------------------------------------- B1
  {
    lessonId: "b1.news.lesson-01",
    passage:
      "The city council approved the new metro line on Tuesday. Construction will begin in spring and should take four years. Officials said the line will serve about 200,000 passengers a day. Some residents complain that two neighbourhoods were left out of the plan.",
    questions: [
      q(
        "When will construction begin?",
        "ساخت‌وساز کِی آغاز می‌شود؟",
        ["پاییز", "بهار", "تابستان", "بلافاصله پس از تصویب"],
        1,
        "«Construction will begin in spring» — spring یعنی بهار.",
      ),
      q(
        "What are the residents complaining about?",
        "ساکنان از چه شکایت دارند؟",
        ["هزینهٔ بلیت", "سرعت قطارها", "جا ماندن دو محله از طرح", "مدت ساخت‌وساز"],
        2,
        "«two neighbourhoods were left out of the plan» — left out یعنی جا ماندن، حذف شدن.",
      ),
    ],
  },
  {
    lessonId: "b1.job-hunting.lesson-01",
    passage:
      "Since I lost my job in March, I have applied for more than thirty positions. Two companies invited me for an interview, but neither offered me the job. I now spend two hours a day improving my CV and practising answers.",
    questions: [
      q(
        "How many interviews did the speaker have?",
        "گوینده چند مصاحبه داشت؟",
        ["یکی", "دو", "سی", "هیچ‌کدام"],
        1,
        "«Two companies invited me for an interview» — تعداد شرکت‌ها دو بود.",
      ),
      q(
        "What does the speaker do every day now?",
        "گوینده اکنون هر روز چه می‌کند؟",
        ["کار تمام‌وقت می‌کند", "در دو شرکت کار می‌کند", "روی رزومه و تمرین پاسخ‌ها کار می‌کند", "به دنبال خانه است"],
        2,
        "«improving my CV and practising answers» — رزومه (CV) و تمرین پاسخ‌های مصاحبه.",
      ),
    ],
  },
  {
    lessonId: "b1.conflicts.lesson-01",
    passage:
      "When two colleagues disagree, the first step is to let each side finish without interrupting. Repeating the other person's point shows you have understood it. Only then should you look for a solution that both can accept.",
    questions: [
      q(
        "What is the first step when colleagues disagree?",
        "نخستین گام هنگام اختلاف همکاران چیست؟",
        ["پیدا کردن راه‌حل", "گوش دادن بی‌وقفه به هر دو طرف", "داوری مدیر", "تغییر موضوع"],
        1,
        "«let each side finish without interrupting» — interrupting یعنی میان حرف پریدن.",
      ),
      q(
        "Why repeat the other person's point?",
        "چرا نکتهٔ طرف مقابل را تکرار می‌کنیم؟",
        ["برای تغییر نظر او", "برای طولانی‌تر شدن گفتگو", "برای نشان دادن اینکه فهمیده‌ایم", "برای پایان دادن به بحث"],
        2,
        "«shows you have understood it» — تکرار، نشانهٔ فهم است، نه پذیرش.",
      ),
    ],
  },
  {
    lessonId: "b1.eating-habits.lesson-01",
    passage:
      "Ten years ago, my family ate rice with meat almost every evening. Since my father's heart operation, we have eaten vegetables three times a week. We still eat meat, but much less, and we walk together after dinner.",
    questions: [
      q(
        "What changed after the operation?",
        "پس از عمل جراحی چه تغییر کرد؟",
        ["خانواده گوشت را کنار گذاشت", "سبزیجات سه بار در هفته به برنامه اضافه شد", "آن‌ها دیگر شام نمی‌خورند", "پدرشان آشپزی می‌کند"],
        1,
        "«we have eaten vegetables three times a week» — سه بار در هفته؛ گوشت حذف نشده، کم شده است.",
      ),
      q(
        "What does the family still do?",
        "خانواده هنوز چه کاری می‌کند؟",
        ["هر شب برنج و گوشت می‌خورد", "گوشت می‌خورد، اما کمتر", "پیاده‌روی را کنار گذاشته", "سبزیجات نمی‌خورد"],
        1,
        "«We still eat meat, but much less» — still یعنی هنوز.",
      ),
    ],
  },
  {
    lessonId: "b1.social-issues.lesson-01",
    passage:
      "Rents in the city have risen faster than wages. Many young people now share flats with two or three friends. The mayor has promised to build cheap housing, but the first blocks are not expected before next year.",
    questions: [
      q(
        "Why do many young people share flats?",
        "چرا بسیاری از جوانان آپارتمان مشترک می‌گیرند؟",
        ["چون تنها زندگی کردن را دوست ندارند", "چون اجاره‌ها تندتر از دستمزد بالا رفته‌اند", "چون آپارتمان‌ها خالی مانده‌اند", "چون شهردار این را خواسته است"],
        1,
        "«Rents have risen faster than wages» — علت، فاصلهٔ اجاره و دستمزد است.",
      ),
      q(
        "When are the first cheap housing blocks expected?",
        "نخستین بلوک‌های مسکن ارزان کِی انتظار می‌رود؟",
        ["امسال", "سال آینده", "ماه آینده", "هیچ‌وقت"],
        1,
        "«not expected before next year» — پیش از سال آینده انتظار نمی‌رود.",
      ),
    ],
  },

  // ---------------------------------------------------------------- B2
  {
    lessonId: "b2.interview.lesson-01",
    passage:
      "The interviewer opened with a question I had not prepared: 'Tell me about a decision you regret.' I described a project I had rushed, and what I would do differently. She wrote something down, then asked how I handled the deadline.",
    questions: [
      q(
        "Which question surprised the candidate?",
        "کدام پرسش نامزد را غافلگیر کرد؟",
        ["پرسشی دربارهٔ تصمیمی که از آن پشیمان است", "پرسشی دربارهٔ حقوق", "پرسشی دربارهٔ ساعت کاری", "پرسشی دربارهٔ مدرک تحصیلی"],
        0,
        "«a question I had not prepared» دربارهٔ «a decision you regret» بود — پشیمانی.",
      ),
      q(
        "What did the interviewer ask next?",
        "مصاحبه‌گر سپس چه پرسید؟",
        ["دربارهٔ تحصیلات", "دربارهٔ مدیریت مهلت", "دربارهٔ تیم قبلی", "دربارهٔ سفر"],
        1,
        "«asked how I handled the deadline» — deadline یعنی مهلت نهایی.",
      ),
    ],
  },
  {
    lessonId: "b2.leadership.lesson-01",
    passage:
      "A good team leader does not make every decision. She makes the goal clear, gives each person room to choose the method, and reviews the result rather than the steps. When something fails, she asks what the team learned before she asks who is to blame.",
    questions: [
      q(
        "What does the leader review?",
        "رهبر چه چیزی را بررسی می‌کند؟",
        ["مراحل کار", "نتیجه، نه مراحل", "حضور و غیاب", "ساعت کاری افراد"],
        1,
        "«reviews the result rather than the steps» — result بر steps ترجیح دارد.",
      ),
      q(
        "What does she ask first after a failure?",
        "پس از یک شکست نخست چه می‌پرسد؟",
        ["چه کسی مقصر است", "تیم چه آموخت", "چه‌قدر ضرر داشت", "چه کسی تسلیم شد"],
        1,
        "«asks what the team learned before … who is to blame» — یادگیری پیش از سرزنش.",
      ),
    ],
  },
  {
    lessonId: "b2.education.lesson-01",
    passage:
      "Finland's schools start formal reading later than most countries, yet their results remain strong. Teachers there design their own lessons and are trusted to assess students. Critics argue that the model depends on small classes and well-trained staff, which poorer countries cannot afford.",
    questions: [
      q(
        "What do Finnish teachers do?",
        "معلمان فنلاندی چه می‌کنند؟",
        ["فقط کتاب درسی را می‌خوانند", "خودشان درس طراحی می‌کنند و دانش‌آموز را می‌سنجند", "آزمون سراسری برگزار می‌کنند", "زودتر از همه نوشتن را شروع می‌کنند"],
        1,
        "«design their own lessons and are trusted to assess students» — طراحی و سنجش، هر دو.",
      ),
      q(
        "What is the critics' argument?",
        "استدلال منتقدان چیست؟",
        ["نتیجه‌ها ضعیف است", "خواندن دیر شروع می‌شود", "مدل به کلاس کوچک و معلم ورزیده وابسته است", "معلمان آزادی زیادی ندارند"],
        2,
        "«depends on small classes and well-trained staff» — وابستگی به شرایط پرهزینه.",
      ),
    ],
  },
  {
    lessonId: "b2.debate.lesson-01",
    passage:
      "In a formal debate, each speaker states a claim, supports it with evidence, and answers one question from the other side. Interrupting is not allowed. The judge scores the quality of the reasoning, not the loudness of the speaker.",
    questions: [
      q(
        "What must each speaker do?",
        "هر سخنران باید چه کار کند؟",
        ["فقط ادعا را بگوید", "ادعا، شواهد و پاسخ به یک پرسش", "به همهٔ پرسش‌ها پاسخ دهد", "سخنران مقابل را قطع کند"],
        1,
        "«states a claim, supports it with evidence, and answers one question» — سه کار با هم.",
      ),
      q(
        "What does the judge score?",
        "داور چه چیزی را نمره می‌دهد؟",
        ["بلندی صدا", "سرعت سخن", "کیفیت استدلال", "تعداد کلمات"],
        2,
        "«the quality of the reasoning, not the loudness» — استدلال، نه بلندی صدا.",
      ),
    ],
  },
  {
    lessonId: "b2.viewpoints.lesson-01",
    passage:
      "Before rejecting an argument, summarise it so well that its author would agree with your summary. Then check whether the evidence actually supports the conclusion. Many disagreements turn out to be about values, not facts.",
    questions: [
      q(
        "What should you do before rejecting an argument?",
        "پیش از رد کردن یک استدلال چه باید کرد؟",
        ["شواهد را نادیده گرفت", "خلاصه‌ای نوشت که خودِ نویسنده بپذیرد", "نویسنده را نقد کرد", "به سراغ نتیجه رفت"],
        1,
        "«summarise it so well that its author would agree» — خلاصهٔ منصفانه، پیش‌شرط نقد است.",
      ),
      q(
        "What are many disagreements really about?",
        "بسیاری از اختلاف‌ها در واقع دربارهٔ چیست؟",
        ["واقعیت‌ها", "ارزش‌ها", "آمار", "تعریف واژه‌ها"],
        1,
        "«about values, not facts» — ارزش‌ها، نه واقعیت‌ها.",
      ),
    ],
  },

  // ---------------------------------------------------------------- C1
  {
    lessonId: "c1.persuasion.lesson-01",
    passage:
      "A persuasive speaker rarely begins with the strongest claim. She first establishes common ground, concedes the part of the opposing case that is true, and only then introduces her evidence. The concession costs little and buys the audience's attention for what follows.",
    questions: [
      q(
        "Why does the speaker concede a point?",
        "چرا سخنران بخشی از ادعای طرف مقابل را می‌پذیرد؟",
        ["چون از موضع خود مطمئن نیست", "چون هزینهٔ کمی دارد و توجه مخاطب را می‌خرد", "چون شواهدی ندارد", "چون بحث را می‌خواهد پایان دهد"],
        1,
        "«costs little and buys the audience's attention» — امتیاز کوچک، اعتماد بزرگ‌تر.",
      ),
      q(
        "When does she introduce her evidence?",
        "شواهدش را کِی ارائه می‌کند؟",
        ["در همان جملهٔ اول", "پس از ایجاد زمینهٔ مشترک و امتیاز دادن", "در پایان سخنرانی", "هرگز"],
        1,
        "«and only then introduces her evidence» — only then یعنی تنها پس از آن مراحل.",
      ),
    ],
  },
  {
    lessonId: "c1.policy.lesson-01",
    passage:
      "The proposal would cap short-term rentals at ninety nights a year. Supporters argue that it returns housing to long-term residents; opponents warn that it will reduce tourism revenue and is difficult to enforce. The committee has asked for a costed impact assessment before the second reading.",
    questions: [
      q(
        "What would the cap limit?",
        "این سقف چه چیزی را محدود می‌کند؟",
        ["اجارهٔ کوتاه‌مدت به نود شب در سال", "قیمت هتل‌ها", "تعداد گردشگران", "مدت اجارهٔ بلندمدت"],
        0,
        "«cap short-term rentals at ninety nights a year» — cap یعنی سقف گذاشتن.",
      ),
      q(
        "What has the committee requested?",
        "کمیته چه چیزی خواسته است؟",
        ["قرائت دوم فوری", "ارزیابی اثر همراه با برآورد هزینه", "تعطیلی بازار اجاره", "نظر دادگاه"],
        1,
        "«a costed impact assessment» — ارزیابی اثر با هزینهٔ برآوردشده، پیش از قرائت دوم.",
      ),
    ],
  },
  {
    lessonId: "c1.statistics.lesson-01",
    passage:
      "A survey of 400 readers found that 70% preferred the new layout. The sample was drawn from people who had already subscribed to the magazine, so the result describes subscribers, not readers in general. Reporting it as 'most readers' overstates what the data can support.",
    questions: [
      q(
        "What is the flaw in the claim?",
        "ایراد این ادعا چیست؟",
        ["نمونه فقط مشترکان را نمایندگی می‌کند", "تعداد نمونه بسیار زیاد است", "هفتاد درصد عدد بالایی است", "طرح پرسشنامه نامشخص است"],
        0,
        "«describes subscribers, not readers in general» — نمونه از جمعیت هدف گسترده‌تر گرفته نشده.",
      ),
      q(
        "Which wording would be accurate?",
        "کدام بیان دقیق است؟",
        ["همهٔ خوانندگان", "بیشتر مشترکان", "بیشتر مردم", "هیچ‌کس"],
        1,
        "داده فقط دربارهٔ مشترکان است، پس «بیشتر مشترکان» ادعای درست است.",
      ),
    ],
  },
  {
    lessonId: "c1.org-culture.lesson-01",
    passage:
      "When a company rewards only individual output, employees stop helping each other. The behaviour is rational: helping costs time and earns no credit. Managers who want collaboration must therefore measure it, for example by asking teams what they learned from a colleague.",
    questions: [
      q(
        "Why do employees stop helping each other?",
        "چرا کارمندان از کمک به هم دست می‌کشند؟",
        ["چون کمک پاداش نمی‌گیرد و وقت می‌برد", "چون از هم خوششان نمی‌آید", "چون مدیران منع کرده‌اند", "چون کارشان کم شده است"],
        0,
        "«helping costs time and earns no credit» — credit یعنی اعتبار و پاداش.",
      ),
      q(
        "How can managers measure collaboration?",
        "مدیران همکاری را چگونه می‌سنجند؟",
        ["با شمارش ساعت حضور", "با پرسیدن از تیم‌ها که از همکاران چه آموختند", "با کاهش جلسات", "با مقایسهٔ حقوق"],
        1,
        "«asking teams what they learned from a colleague» — سنجش، نه فرض.",
      ),
    ],
  },
  {
    lessonId: "c1.specialist-presentations.lesson-01",
    passage:
      "Specialists lose audiences for two reasons: they define terms the audience already knows, and they skip the step that links method to conclusion. A single slide that states the question, the method and the finding prevents both. Detail belongs in an appendix, not in the opening.",
    questions: [
      q(
        "What do specialists often skip?",
        "متخصصان اغلب چه چیزی را جا می‌اندازند؟",
        ["تعریف واژه‌ها", "پیوند میان روش و نتیجه", "معرفی خودشان", "فهرست منابع"],
        1,
        "«skip the step that links method to conclusion» — حلقهٔ گمشده میان روش و نتیجه.",
      ),
      q(
        "Where does detail belong?",
        "جزئیات کجا باید باشد؟",
        ["در آغاز ارائه", "در پیوست", "در اسلاید اول", "در پرسش‌وپاسخ شفاهی"],
        1,
        "«Detail belongs in an appendix, not in the opening» — پیوست، نه آغاز.",
      ),
    ],
  },

  // ---------------------------------------------------------------- C2
  {
    lessonId: "c2.nuance.lesson-01",
    passage:
      "The report describes the delay as 'regrettable'. Regrettable acknowledges a problem without admitting fault; 'unacceptable' would assign blame, and 'unfortunate' would suggest chance. The choice of adjective is the report's argument.",
    questions: [
      q(
        "What does 'regrettable' avoid?",
        "واژهٔ «regrettable» از چه چیزی پرهیز می‌کند؟",
        ["پذیرش تقصیر", "اعتراف به وجود مشکل", "ذکر تأخیر", "تعیین زمان"],
        0,
        "«without admitting fault» — fault یعنی تقصیر؛ واژه مشکل را می‌پذیرد، تقصیر را نه.",
      ),
      q(
        "What does the adjective choice reveal?",
        "انتخاب این صفت چه چیزی را آشکار می‌کند؟",
        ["نویسنده اطلاعات ندارد", "استدلال پنهان گزارش", "تأخیر بی‌اهمیت است", "گزارش تاریخ ندارد"],
        1,
        "«The choice of adjective is the report's argument» — بار ارزشی واژه، خودِ استدلال است.",
      ),
    ],
  },
  {
    lessonId: "c2.discourse.lesson-01",
    passage:
      "The speaker uses 'we' eleven times and 'they' only twice. In the transcript, every 'they' appears in a sentence about criticism. The pronoun pattern does the persuasive work that the argument never states openly.",
    questions: [
      q(
        "How is 'they' used?",
        "ضمیر «they» چگونه به کار رفته است؟",
        ["در همهٔ جمله‌ها", "فقط در جمله‌های انتقادی", "فقط در جملهٔ اول", "هرگز"],
        1,
        "«every 'they' appears in a sentence about criticism» — کاربرد ضمیر، منظم و جهت‌دار است.",
      ),
      q(
        "What does the pronoun pattern accomplish?",
        "این الگوی ضمیر چه کاری می‌کند؟",
        ["متقاعدسازی‌ای که آشکارا گفته نمی‌شود", "افزایش دقت آمار", "کوتاه‌کردن متن", "تعریف واژه‌های کلیدی"],
        0,
        "«does the persuasive work that the argument never states openly» — اثر بلاغی پنهان.",
      ),
    ],
  },
  {
    lessonId: "c2.argument-analysis.lesson-01",
    passage:
      "The author attacks the funding body rather than the study's method. This is a circumstantial ad hominem: the funding may indeed be relevant, but relevance is not proof of error. The study could still be sound, and the author has not shown otherwise.",
    questions: [
      q(
        "Which fallacy does the author commit?",
        "نویسنده مرتکب کدام مغالطه شده است؟",
        ["تعمیم شتاب‌زده", "حمله به منبع به‌جای نقد روش", "مصادره به مطلوب", "خلط علت و معلول"],
        1,
        "«attacks the funding body rather than the study's method» — همان ad hominem موقعیتی است.",
      ),
      q(
        "What remains unproven?",
        "چه چیزی اثبات نشده باقی می‌ماند؟",
        ["خطا بودن مطالعه", "منبع تأمین بودجه", "وجود روش در مطالعه", "نیت نویسنده"],
        0,
        "«relevance is not proof of error» — بودنِ ارتباط، خطا بودن را ثابت نمی‌کند.",
      ),
    ],
  },
  {
    lessonId: "c2.translation.lesson-01",
    passage:
      "A literal translation of the proverb loses its rhythm but keeps its meaning. A freer version keeps the rhythm and replaces the image, so the reader feels the effect while the metaphor changes. Which is right depends on whether the reader needs the source culture or the effect.",
    questions: [
      q(
        "What does the freer version sacrifice?",
        "نسخهٔ آزادتر چه چیزی را قربانی می‌کند؟",
        ["ریتم متن", "معنا", "تصویر اصلی (استعاره)", "اثر بر خواننده"],
        2,
        "«replaces the image» — تصویرِ استعاری تغییر می‌کند، هرچند اثر می‌ماند.",
      ),
      q(
        "What decides which version is right?",
        "چه چیزی درستیِ هر نسخه را تعیین می‌کند؟",
        ["طول متن", "نیاز خواننده: فرهنگ مبدأ یا اثر", "نظر مترجم", "زبان مقصد"],
        1,
        "«depends on whether the reader needs the source culture or the effect» — هدف خواننده تعیین‌کننده است.",
      ),
    ],
  },
  {
    lessonId: "c2.audience.lesson-01",
    passage:
      "The same finding reaches three audiences in three registers: 'the trial failed' for a lab, 'the trial did not meet its endpoint' for a journal, and 'the treatment did not work as hoped' for patients. Precision and kindness pull in opposite directions, and the author has to choose.",
    questions: [
      q(
        "How does the patient version differ?",
        "نسخهٔ بیماران چه تفاوتی دارد؟",
        ["دقیق‌تر و فنی‌تر است", "ملایم‌تر است و امید را نگه می‌دارد", "کوتاه‌تر است", "به داده‌های آماری تکیه دارد"],
        1,
        "«did not work as hoped» — لحن ملایم‌تر و همراه با امید، در برابر دقت فنی.",
      ),
      q(
        "What tension does the passage name?",
        "متن کدام تنش را نام می‌برد؟",
        ["سرعت در برابر هزینه", "دقت در برابر ملاحظه", "علم در برابر سیاست", "متن در برابر تصویر"],
        1,
        "«Precision and kindness pull in opposite directions» — دقت و مهربانی، دو جهت مخالف.",
      ),
    ],
  },
];

// --- append the reading items ------------------------------------------------
let added = 0;
for (const cluster of clusters) {
  const lessonExercises = exercises.filter((e) => e.lessonId === cluster.lessonId);
  if (lessonExercises.length === 0) {
    console.error(`lesson not found: ${cluster.lessonId}`);
    process.exit(1);
  }
  const base = cluster.lessonId.split(".").slice(0, 2).join(".");
  let order = Math.max(...lessonExercises.map((e) => e.order));
  cluster.questions.forEach((item, index) => {
    exercises.push({
      id: `${base}.read.0${index + 1}`,
      lessonId: cluster.lessonId,
      type: "multiple_choice",
      order: ++order,
      passage: cluster.passage,
      question: item.question,
      questionFa: item.questionFa,
      options: item.options,
      correctIndex: item.correct,
      skill: "READING",
      explanationFa: item.fa,
    });
    added += 1;
  });
}

// --- tag every exercise that had no skill -----------------------------------
const tagged = { LISTENING: 0, VOCABULARY: 0, GRAMMAR: 0 };
for (const exercise of exercises) {
  if (exercise.skill) continue;
  if (exercise.type === "listening") exercise.skill = "LISTENING";
  else if (exercise.type === "translation") exercise.skill = "VOCABULARY";
  else if (exercise.type === "multiple_choice") {
    if (exercise.passage) exercise.skill = "READING";
    // A meaning question (What does X mean? / What do you show before boarding?)
    // is vocabulary; every other multiple choice here is a usage or cloze item,
    // which the corpus already tags GRAMMAR.
    else if (/^What (does|do)\b/.test(exercise.question)) exercise.skill = "VOCABULARY";
    else exercise.skill = "GRAMMAR";
  } else {
    console.error(`untagged exercise of an unexpected type: ${exercise.id} (${exercise.type})`);
    process.exit(1);
  }
  tagged[exercise.skill] += 1;
}

// --- bundle version ---------------------------------------------------------
// exercises.json is written from the object we mutated above, never re-read:
// a fresh read here would silently drop the new items and tags on the floor.
exercisesFile.contentVersion = 63;
write("exercises.json", exercisesFile);
for (const file of ["lessons.json", "vocabulary.json", "placement.json", "knowledge.json"]) {
  const bundle = read(file);
  bundle.contentVersion = 63;
  write(file, bundle);
}

console.log(`reading items added: ${added} across ${clusters.length} lessons`);
console.log(`skill tags added: ${JSON.stringify(tagged)}`);
console.log(`exercises: ${exercises.length}`);
