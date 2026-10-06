/**
 * A-9 (one-shot authoring pass): close the A1/A2 grammar gaps flagged by the
 * curriculum audit — articles (a/an/the), plural spelling, subject/object
 * pronouns, can for ability, imperatives, question words — and bring the past
 * simple down to A2 with its own dedicated lesson (it is an A2 point in the
 * British Council / EQUALS Core Inventory tables; b1.work still teaches it as
 * a review).
 *
 * Every new lesson carries the A-8 multi-section `grammarTipFa` format, six
 * vocabulary entries and at least one knowledge item, so `validateContent`
 * coverage (vocabulary + exercises + knowledge per lesson) holds once
 * `a9_more_grammar_exercises.mjs` has added the exercises.
 *
 * contentVersion is bumped once for this whole batch: 8 → 9, in all five
 * files (AGENTS.md §5 — the version is authored inside the JSON). A-7 already
 * took v8, so A-9 needs its own bump for installed apps to re-seed.
 */
import fs from "node:fs";

const DIR = "app/src/main/assets/content";
const NEW_VERSION = 9;

const lessons = [
  {
    id: "a1.articles.lesson-01",
    level: "A1",
    title: "Articles: a, an, the",
    titleFa: "کاربرد a، an و the",
    topic: "Language",
    estimatedMinutes: 5,
    order: 39,
    grammarTipFa: `## قاعده
وقتی از یک چیز ناشناخته حرف می‌زنیم a یا an می‌گذاریم: a book. پیش از صدای مصوت an می‌آید: an apple و an hour (h خاموش است). اگر شنونده دقیقاً بداند منظور کدام چیز است، the می‌گذاریم: the sun، the book on the table. اسم‌های جمع کلی و اسم‌های انتزاعی هیچ حرفی نمی‌خواهند: I like music.
## جدول
a book / a car / a university — یک (پیش از صدای مصوت)
an apple / an orange / an hour — یک (پیش از صدای مصوت)
the sun / the moon / the first floor — همان مشخص
I bought a book. The book is on the table. — بار اول a، بار دوم the
## مثال‌ها
She is a doctor. — او پزشک است.
I need an umbrella. — به چتری احتیاج دارم.
The hotel is next to the bank. — هتل کنار بانک است.
## خطاهای رایج
«I have a apple» غلط است — پیش از apple باید an بیاید.
برای آب، غذا، زبان‌ها و اخبار the نمی‌آید: I drink water.
برای چیزی که برای اولین بار معرفی می‌کنید the به کار نبرید: I saw a dog.`,
  },
  {
    id: "a1.plurals.lesson-01",
    level: "A1",
    title: "Plural Nouns",
    titleFa: "جمع بستن اسم‌ها",
    topic: "Language",
    estimatedMinutes: 5,
    order: 40,
    grammarTipFa: `## قاعده
بیشتر اسم‌ها با s جمع می‌شوند: cat → cats. اسم‌هایی که به s، sh، ch، x یا o ختم شوند es می‌گیرند: box → boxes. اگر اسم به همزه + y ختم شود y به ies تبدیل می‌شود: baby → babies. بعضی اسامی شکل نامنظم دارند: child → children. اسم‌های غیرقابل شمارش جمع ندارند: water، rice، information.
## جدول
cat → cats / box → boxes / watch → watches
baby → babies / city → cities
leaf → leaves / knife → knives
child → children / man → men / foot → feet
## مثال‌ها
There are three children in the park. — سه کودک در پارک هستند.
She bought two boxes of tea. — دو جعبه چای خرید.
The women are waiting outside. — زن‌ها بیرون منتظرند.
## خطاهای رایج
«childs» و «mans» غلط‌اند — children و men درست است.
بعد از عدد همیشه اسم جمع می‌آید: «two child» غلط، «two children» درست.
«informations» و «advices» جمع ندارند: some information، some advice.`,
  },
  {
    id: "a1.pronouns.lesson-01",
    level: "A1",
    title: "Subject and Object Pronouns",
    titleFa: "ضمایر فاعلی و مفعولی",
    topic: "Language",
    estimatedMinutes: 5,
    order: 41,
    grammarTipFa: `## قاعده
ضمیر فاعلی جایگزین فاعل می‌شود و قبل از فعل می‌آید: I، you، he، she، it، we، they. ضمیر مفعولی بعد از فعل یا حرف اضافه می‌آید: me، you، him، her، it، us، them. برای کسی که نمی‌شناسیم هم می‌توانیم از they استفاده کنیم.
## جدول
فاعلی: I / you / he / she / it / we / they
مفعولی: me / you / him / her / it / us / them
I see her. — او را می‌بینم.
She gave the book to me. — کتاب را به من داد.
## مثال‌ها
He helps his sister every day. — هر روز به خواهرش کمک می‌کند.
We met them at the station. — ما آن‌ها را در ایستگاه دیدیم.
The teacher asked us to open our books. — معلم از ما خواست کتاب‌هایمان را باز کنیم.
## خطاهای رایج
«Me and him are here» غلط است — He and I are here درست است.
بعد از حرف اضافه ضمیر فاعلی نیاید: «between you and I» غلط.
«Her is a teacher» غلط — She is a teacher درست است.`,
  },
  {
    id: "a1.can.lesson-01",
    level: "A1",
    title: "can and can't for Ability",
    titleFa: "توانایی با can و can't",
    topic: "Language",
    estimatedMinutes: 5,
    order: 42,
    grammarTipFa: `## قاعده
برای بیان توانایی از can استفاده می‌کنیم و بعد از آن فعل لخت می‌آید: I can swim. منفی آن can't یا cannot است. در پرسش، can قبل از فاعل می‌آید: Can you drive? گذشتهٔ آن could است و برای توانایی در گذشته به کار می‌رود.
## جدول
I can swim. / She can swim. — فعل هیچ‌وقت s نمی‌گیرد
I can't / cannot play the guitar.
Can you speak English? — Yes, I can. / No, I can't.
He could run fast when he was young.
## مثال‌ها
My father can cook very well. — پدرم خیلی خوب آشپزی می‌کند.
We can't arrive before six. — نمی‌توانیم قبل از شش برسیم.
Can you help me with this box? — می‌توانی در این جعبه کمکم کنی؟
## خطاهای رایج
«I can to swim» غلط است — بعد از can فعل لخت می‌آید: I can swim.
«She cans drive» و «He can drives» غلط‌اند — نه s می‌گیرد و نه to.
برای گذشته نگویید «I can swimmed»: I could swim.`,
  },
  {
    id: "a1.imperatives.lesson-01",
    level: "A1",
    title: "Imperatives",
    titleFa: "جمله‌های امری",
    topic: "Language",
    estimatedMinutes: 5,
    order: 43,
    grammarTipFa: `## قاعده
برای دستور، دستورالعمل و علائم راهنمایی از شکل لخت فعل استفاده می‌کنیم: Sit down. برای نهی، همان شکل لخت را با Don't می‌دهیم: Don't run. برای ملایم‌تر کردن حرف please می‌گذاریم و برای پیشنهاد مشترک از Let's بهره می‌گیریم.
## جدول
Open your books. — کتاب‌هایتان را باز کنید.
Don't touch the wire. — به سیم دست نزنید.
Please wait here. — لطفاً اینجا منتظر بمانید.
Let's go home. — بیایید به خانه برویم.
## مثال‌ها
Stop! The light is red. — توقف! چراغ قرمز است.
Listen to the instructions carefully. — به دستورالعمل‌ها دقیق گوش کن.
Close the door, please. — لطفاً در را ببند.
## خطاهای رایج
«Don't goes» غلط است — بعد از don't فعل لخت می‌آید: Don't go.
برای دستور فاعل نیاورید: «You close the door» غلط، Close the door درست.
بعد از Let's فعل با to نمی‌آید: «Let's to go» غلط است.`,
  },
  {
    id: "a1.question-words.lesson-01",
    level: "A1",
    title: "Question Words",
    titleFa: "کلمات پرسشی",
    topic: "Language",
    estimatedMinutes: 5,
    order: 44,
    grammarTipFa: `## قاعده
برای پرسش از چیز، مکان، زمان، دلیل و نحوه، کلمهٔ پرسشی را اول جمله می‌گذاریم و بعد فعل کمکی می‌آید: Where do you live? اگر فعل be باشد ترتیب فرق می‌کند: Where are you from? پاسخ کوتاه با همان ساختار داده می‌شود.
## جدول
What — چه‌چیزی / What is your name?
Where — کجا / Where do you live?
When — کی / When does the class start?
Who — چه کسی / Who is that woman?
Why — چرا / Why are you late?
How — چگونه / How do you pronounce this?
## مثال‌ها
What time does the train leave? — قطار ساعت چند حرکت می‌کند؟
How much is this shirt? — این پیراهن چند است؟
Why are you tired today? — امروز چرا خسته‌ای؟
## خطاهای رایج
«Where you are from?» غلط است — Where are you from?
«What means this?» غلط است — What does this mean?
بعد از how برای اسم‌های شمارشی از many و برای غیرقابل شمارش از much استفاده کنید.`,
  },
  {
    id: "a2.past-simple.lesson-01",
    level: "A2",
    title: "Past Simple",
    titleFa: "زمان گذشتهٔ ساده",
    topic: "Language",
    estimatedMinutes: 6,
    order: 45,
    grammarTipFa: `## قاعده
برای رویداد تمام‌شده در گذشته از شکل گذشتهٔ فعل استفاده می‌کنیم: افعال مرتب ed می‌گیرند (work → worked) و افعال نامنظم شکل ویژه دارند (go → went، see → saw). با he/she/it هم فعل لخت می‌ماند: He worked. در پرسش و منفی از did کمکی می‌گیریم و فعل به شکل لخت برمی‌گردد: Did you see it? I didn't go.
## جدول
I / you / he worked — They didn't work.
go → went / see → saw / buy → bought / write → wrote
Did she leave? — Yes, she did. / No, she didn't.
yesterday / last night / two days ago / in 2019
## مثال‌ها
We went to the coast last weekend. — آخر هفته به ساحل رفتیم.
She bought a new phone yesterday. — دیروز یک گوشی نو خرید.
I didn't see your message. — پیامت را ندیدم.
## خطاهای رایج
«I didn't went» غلط است — بعد از didn't فعل لخت می‌آید: I didn't go.
«He didn't had breakfast» غلط است — He didn't have breakfast.
«Yesterday I go to work» غلط است — Yesterday I went to work.`,
  },
];

/**
 * A-7 coverage for the new A1 lessons: `PhonologyCoverageTest` requires every
 * A1 lesson to teach a `## تلفظ` section, so it is appended to each tip before
 * the lesson is written (the A-8 parser turns the unknown header into its own
 * CUSTOM card).
 */
const pronunciationSections = {
  "a1.articles.lesson-01": `a و an بر اساسِ صدا انتخاب می‌شوند، نه حرف: a book اما an apple — صدای اولِ apple مصوت است.
an hour را با hِ خوانده نشنوید: چون h خاموش است، فقط مصوت می‌آید و an لازم است.
the معمولاً ضعیف /ðə/ تلفظ می‌شود، نه /ðiː/ کشیده؛ کشیدن آن فقط برای تأکید است.`,
  "a1.plurals.lesson-01": `پسوند جمع سه صدا دارد: /s/ در cats، /z/ در dogs و /ɪz/ در boxes — پس از صوت‌های sibilant یک هجای اضافه می‌افتد.
babies با /ˈbeɪbiːz/ خوانده می‌شود و -ies همان /iːz/ است؛ y را حذف نکنید.
foot کوتاه /fʊt/ است و جمعش feet با /iː/ بلند؛ children را هم مثل معدود کوتاه نخوانید.`,
  "a1.pronouns.lesson-01": `him و her در گفتار روزمره ضعیف می‌شوند: /ɪm/ و /hə/ — تکیه روی فعل می‌افتد (I SEE him).
them غالباً /ðəm/ و کوتاه تلفظ می‌شود، نه /ðem/ کشیده.
it و us هر دو تکیه‌ناپذیرند؛ کشیدنشان جمله را می‌شکند.`,
  "a1.can.lesson-01": `can در جملهٔ خبری ضعیف /kən/ است، اما در پرسش و هنگام تأکید کامل /kæn/ تلفظ می‌شود: I CAN swim در برابر I can SWIM.
can't تکیه می‌گیرد چون منفی است: در بریتیش با /ɑː/ باز (کَنت) و در آمریکایی با /æ/.
could کوتاه /kʊd/ است؛ آن را با /uː/ کشیده نخوانید.`,
  "a1.imperatives.lesson-01": `جملهٔ امری کوتاه و پرتحرک است؛ تکیه روی فعل می‌افتد: STOP! نه stopِ آهسته.
در Don't، t اغلب بدون انفجار تلفظ می‌شود و کلمه کوتاه است.
پایان stop و sit و close با آزادسازی صامت‌ها همراه است: p، t و s را کامل بیان کنید.`,
  "a1.question-words.lesson-01": `در what و where و when صدای /w/ با گرد کردن لب‌ها ساخته می‌شود؛ آن را با /v/ اشتباه نگیرید.
در who و how صدای /h/ واضح است و نباید حذف شود.
در پرسش‌های بلند، تکیه روی کلمهٔ پرسشی می‌افتد و آهنگ پایان جمله بالا می‌رود.`,
  "a2.past-simple.lesson-01": `در didn't، t اغلب حذف می‌شود و تکیه روی فعل اصلی می‌افتد: I DIDN'T GO.
شکل‌های گذشته کوتاه‌اند: went /went/ و bought /bɔːt/؛ کشیدن بیش از حدشان غیرطبیعی است.
yesterday روی هجای اول تکیه دارد: YES-ter-day و هجای میانی /t/ نرم است.`,
};

for (const lesson of lessons) {
  const section = pronunciationSections[lesson.id];
  if (section && !lesson.grammarTipFa.includes("## تلفظ")) {
    lesson.grammarTipFa = `${lesson.grammarTipFa}\n## تلفظ\n${section}`;
  }
}

const vocabulary = [
  // --- a1.articles -------------------------------------------------------
  { id: "a1.articles.word.umbrella", word: "umbrella", translation: "چتر", definition: "A folding frame used for protection against rain", pronunciation: "/ʌmˈbrelə/", level: "A1", partOfSpeech: "noun", examples: ["It's raining — take an umbrella."], collocations: ["take an umbrella", "umbrella stand"], lessonId: "a1.articles.lesson-01" },
  { id: "a1.articles.word.apple", word: "apple", translation: "سیب", definition: "A round firm fruit with red, green or yellow skin", pronunciation: "/ˈæp.əl/", level: "A1", partOfSpeech: "noun", examples: ["She eats an apple every day."], collocations: ["apple tree", "apple juice"], lessonId: "a1.articles.lesson-01" },
  { id: "a1.articles.word.orange", word: "orange", translation: "پرتقال", definition: "A round citrus fruit with a thick orange skin", pronunciation: "/ˈɒr.ɪndʒ/", level: "A1", partOfSpeech: "noun", examples: ["I'd like an orange, please."], collocations: ["orange juice", "peel an orange"], lessonId: "a1.articles.lesson-01" },
  { id: "a1.articles.word.hour", word: "hour", translation: "ساعت (۶۰ دقیقه)", definition: "A period of sixty minutes", pronunciation: "/ˈaʊ.ər/", level: "A1", partOfSpeech: "noun", examples: ["The journey takes an hour."], collocations: ["half an hour", "rush hour"], lessonId: "a1.articles.lesson-01" },
  { id: "a1.articles.word.sun", word: "sun", translation: "خورشید", definition: "The star that gives the Earth light and heat", pronunciation: "/sʌn/", level: "A1", partOfSpeech: "noun", examples: ["The sun rises in the east."], collocations: ["in the sun", "sun rise"], lessonId: "a1.articles.lesson-01" },
  { id: "a1.articles.word.hotel", word: "hotel", translation: "هتل", definition: "A building where travellers pay to stay", pronunciation: "/həʊˈtel/", level: "A1", partOfSpeech: "noun", examples: ["We stayed at a hotel by the sea."], collocations: ["hotel room", "book a hotel"], lessonId: "a1.articles.lesson-01" },

  // --- a1.plurals --------------------------------------------------------
  { id: "a1.plurals.word.child", word: "child", translation: "کودک", definition: "A young human who is not yet an adult — plural: children", pronunciation: "/tʃaɪld/", level: "A1", partOfSpeech: "noun", examples: ["There are twenty children in the class."], collocations: ["young child", "child care"], lessonId: "a1.plurals.lesson-01" },
  { id: "a1.plurals.word.man", word: "man", translation: "مرد", definition: "An adult male person — plural: men", pronunciation: "/mæn/", level: "A1", partOfSpeech: "noun", examples: ["The man is waiting outside."], collocations: ["young man", "old man"], lessonId: "a1.plurals.lesson-01" },
  { id: "a1.plurals.word.woman", word: "woman", translation: "زن", definition: "An adult female person — plural: women", pronunciation: "/ˈwʊm.ən/", level: "A1", partOfSpeech: "noun", examples: ["Two women work in this office."], collocations: ["young woman", "women's rights"], lessonId: "a1.plurals.lesson-01" },
  { id: "a1.plurals.word.foot", word: "foot", translation: "پا", definition: "The part of the body at the end of the leg — plural: feet", pronunciation: "/fʊt/", level: "A1", partOfSpeech: "noun", examples: ["My left foot hurts."], collocations: ["on foot", "foot of the hill"], lessonId: "a1.plurals.lesson-01" },
  { id: "a1.plurals.word.leaf", word: "leaf", translation: "برگ", definition: "The flat green part of a plant — plural: leaves", pronunciation: "/liːf/", level: "A1", partOfSpeech: "noun", examples: ["A dry leaf fell from the tree."], collocations: ["autumn leaf", "tea leaf"], lessonId: "a1.plurals.lesson-01" },
  { id: "a1.plurals.word.box", word: "box", translation: "جعبه", definition: "A container with straight sides and a lid", pronunciation: "/bɒks/", level: "A1", partOfSpeech: "noun", examples: ["Put the books in a box."], collocations: ["shoe box", "box office"], lessonId: "a1.plurals.lesson-01" },

  // --- a1.pronouns -------------------------------------------------------
  { id: "a1.pronouns.word.friend", word: "friend", translation: "دوست", definition: "A person you know well and like", pronunciation: "/frend/", level: "A1", partOfSpeech: "noun", examples: ["My friend lives in Tehran."], collocations: ["best friend", "make friends"], lessonId: "a1.pronouns.lesson-01" },
  { id: "a1.pronouns.word.teacher", word: "teacher", translation: "معلم، آموزگار", definition: "A person whose job is to teach", pronunciation: "/ˈtiː.tʃər/", level: "A1", partOfSpeech: "noun", examples: ["Our teacher speaks English slowly."], collocations: ["English teacher", "head teacher"], lessonId: "a1.pronouns.lesson-01" },
  { id: "a1.pronouns.word.sister", word: "sister", translation: "خواهر", definition: "A girl or woman who has the same parents as you", pronunciation: "/ˈsɪs.tər/", level: "A1", partOfSpeech: "noun", examples: ["Her sister works at a hospital."], collocations: ["younger sister", "sister city"], lessonId: "a1.pronouns.lesson-01" },
  { id: "a1.pronouns.word.brother", word: "brother", translation: "برادر", definition: "A boy or man who has the same parents as you", pronunciation: "/ˈbrʌð.ər/", level: "A1", partOfSpeech: "noun", examples: ["He helps his brother with homework."], collocations: ["older brother", "twin brother"], lessonId: "a1.pronouns.lesson-01" },
  { id: "a1.pronouns.word.doctor", word: "doctor", translation: "پزشک، دکتر", definition: "A person trained to treat ill people", pronunciation: "/ˈdɒk.tər/", level: "A1", partOfSpeech: "noun", examples: ["The doctor examined the patient."], collocations: ["see a doctor", "family doctor"], lessonId: "a1.pronouns.lesson-01" },
  { id: "a1.pronouns.word.student", word: "student", translation: "دانش‌آموز، دانشجو", definition: "A person who is studying at a school or university", pronunciation: "/ˈstjuː.dənt/", level: "A1", partOfSpeech: "noun", examples: ["The teacher helped the students."], collocations: ["medical student", "exchange student"], lessonId: "a1.pronouns.lesson-01" },

  // --- a1.can ------------------------------------------------------------
  { id: "a1.can.word.swim", word: "swim", translation: "شنا کردن", definition: "To move through water using your body", pronunciation: "/swɪm/", level: "A1", partOfSpeech: "verb", examples: ["I can swim but I can't dive."], collocations: ["swim well", "go swimming"], lessonId: "a1.can.lesson-01" },
  { id: "a1.can.word.drive", word: "drive", translation: "رانندگی کردن", definition: "To control and move a car", pronunciation: "/draɪv/", level: "A1", partOfSpeech: "verb", examples: ["She can drive a car."], collocations: ["drive a car", "drive home"], lessonId: "a1.can.lesson-01" },
  { id: "a1.can.word.speak", word: "speak", translation: "صحبت کردن", definition: "To say words using your voice", pronunciation: "/spiːk/", level: "A1", partOfSpeech: "verb", examples: ["He can speak three languages."], collocations: ["speak English", "speak up"], lessonId: "a1.can.lesson-01" },
  { id: "a1.can.word.cook", word: "cook", translation: "آشپزی کردن", definition: "To prepare food with heat", pronunciation: "/kʊk/", level: "A1", partOfSpeech: "verb", examples: ["My father can cook very well."], collocations: ["cook dinner", "home cooking"], lessonId: "a1.can.lesson-01" },
  { id: "a1.can.word.play", word: "play", translation: "بازی کردن، نواختن", definition: "To take part in a game or to make music", pronunciation: "/pleɪ/", level: "A1", partOfSpeech: "verb", examples: ["Can you play the guitar?"], collocations: ["play football", "play the piano"], lessonId: "a1.can.lesson-01" },
  { id: "a1.can.word.run", word: "run", translation: "دویدن", definition: "To move fast on foot", pronunciation: "/rʌn/", level: "A1", partOfSpeech: "verb", examples: ["She can run five kilometres."], collocations: ["run fast", "go for a run"], lessonId: "a1.can.lesson-01" },

  // --- a1.imperatives ----------------------------------------------------
  { id: "a1.imperatives.word.stop", word: "stop", translation: "توقف کردن", definition: "To finish moving or doing something", pronunciation: "/stɒp/", level: "A1", partOfSpeech: "verb", examples: ["Stop! The light is red."], collocations: ["stop here", "bus stop"], lessonId: "a1.imperatives.lesson-01" },
  { id: "a1.imperatives.word.wait", word: "wait", translation: "منتظر ماندن", definition: "To stay somewhere until something happens", pronunciation: "/weɪt/", level: "A1", partOfSpeech: "verb", examples: ["Wait for me at the gate."], collocations: ["wait a moment", "can't wait"], lessonId: "a1.imperatives.lesson-01" },
  { id: "a1.imperatives.word.sit", word: "sit", translation: "نشستن", definition: "To rest with your weight on your bottom", pronunciation: "/sɪt/", level: "A1", partOfSpeech: "verb", examples: ["Please sit down."], collocations: ["sit down", "sit here"], lessonId: "a1.imperatives.lesson-01" },
  { id: "a1.imperatives.word.open", word: "open", translation: "باز کردن", definition: "To move something so it is no longer closed", pronunciation: "/ˈəʊ.pən/", level: "A1", partOfSpeech: "verb", examples: ["Open your books, please."], collocations: ["open the door", "open up"], lessonId: "a1.imperatives.lesson-01" },
  { id: "a1.imperatives.word.close", word: "close", translation: "بستن", definition: "To move something so that there is no opening", pronunciation: "/kləʊz/", level: "A1", partOfSpeech: "verb", examples: ["Close the window, please."], collocations: ["close the door", "close your eyes"], lessonId: "a1.imperatives.lesson-01" },
  { id: "a1.imperatives.word.listen", word: "listen", translation: "گوش دادن", definition: "To give your attention to a sound", pronunciation: "/ˈlɪs.ən/", level: "A1", partOfSpeech: "verb", examples: ["Listen to the instructions carefully."], collocations: ["listen to", "listen carefully"], lessonId: "a1.imperatives.lesson-01" },

  // --- a1.question-words -------------------------------------------------
  { id: "a1.question-words.word.what", word: "what", translation: "چه، چه‌چیزی", definition: "Used to ask about a thing or fact", pronunciation: "/wɒt/", level: "A1", partOfSpeech: "pronoun", examples: ["What is your name?"], collocations: ["what time", "what kind of"], lessonId: "a1.question-words.lesson-01" },
  { id: "a1.question-words.word.where", word: "where", translation: "کجا، کدام‌جا", definition: "Used to ask about a place", pronunciation: "/weər/", level: "A1", partOfSpeech: "adverb", examples: ["Where do you live?"], collocations: ["where to", "where from"], lessonId: "a1.question-words.lesson-01" },
  { id: "a1.question-words.word.when", word: "when", translation: "کی، چه‌وقت", definition: "Used to ask about a time", pronunciation: "/wen/", level: "A1", partOfSpeech: "adverb", examples: ["When does the class start?"], collocations: ["since when", "when to"], lessonId: "a1.question-words.lesson-01" },
  { id: "a1.question-words.word.why", word: "why", translation: "چرا، به چه دلیل", definition: "Used to ask about a reason", pronunciation: "/waɪ/", level: "A1", partOfSpeech: "adverb", examples: ["Why are you late?"], collocations: ["why not", "the reason why"], lessonId: "a1.question-words.lesson-01" },
  { id: "a1.question-words.word.who", word: "who", translation: "چه کسی", definition: "Used to ask about a person", pronunciation: "/huː/", level: "A1", partOfSpeech: "pronoun", examples: ["Who is that woman?"], collocations: ["who else", "whoever"], lessonId: "a1.question-words.lesson-01" },
  { id: "a1.question-words.word.how", word: "how", translation: "چگونه، چطور", definition: "Used to ask about manner or degree", pronunciation: "/haʊ/", level: "A1", partOfSpeech: "adverb", examples: ["How do you pronounce this?"], collocations: ["how about", "how come"], lessonId: "a1.question-words.lesson-01" },

  // --- a2.past-simple ----------------------------------------------------
  { id: "a2.past-simple.word.go", word: "go", translation: "رفتن", definition: "Movement verb — past form: went", pronunciation: "/ɡəʊ/", level: "A2", partOfSpeech: "verb", examples: ["We went to the coast last weekend."], collocations: ["go out", "go home"], lessonId: "a2.past-simple.lesson-01" },
  { id: "a2.past-simple.word.buy", word: "buy", translation: "خریدن", definition: "To get something by paying for it — past form: bought", pronunciation: "/baɪ/", level: "A2", partOfSpeech: "verb", examples: ["She bought a new phone yesterday."], collocations: ["buy a ticket", "buy groceries"], lessonId: "a2.past-simple.lesson-01" },
  { id: "a2.past-simple.word.see", word: "see", translation: "دیدن", definition: "To notice something with your eyes — past form: saw", pronunciation: "/siː/", level: "A2", partOfSpeech: "verb", examples: ["I saw an old friend at the market."], collocations: ["see a film", "see you soon"], lessonId: "a2.past-simple.lesson-01" },
  { id: "a2.past-simple.word.eat", word: "eat", translation: "خوردن", definition: "To take food into your mouth — past form: ate", pronunciation: "/iːt/", level: "A2", partOfSpeech: "verb", examples: ["We ate at a small restaurant."], collocations: ["eat out", "eat breakfast"], lessonId: "a2.past-simple.lesson-01" },
  { id: "a2.past-simple.word.take", word: "take", translation: "گرفتن، برداشتن", definition: "To move something with you — past form: took", pronunciation: "/teɪk/", level: "A2", partOfSpeech: "verb", examples: ["The train took two hours."], collocations: ["take a taxi", "take the bus"], lessonId: "a2.past-simple.lesson-01" },
  { id: "a2.past-simple.word.write", word: "write", translation: "نوشتن", definition: "To produce words on paper or a screen — past form: wrote", pronunciation: "/raɪt/", level: "A2", partOfSpeech: "verb", examples: ["He wrote an email last night."], collocations: ["write down", "write an email"], lessonId: "a2.past-simple.lesson-01" },
];

const knowledge = [
  { id: "grammar.articles-a-an-the", type: "GRAMMAR", title: "Articles: a / an / the", titleFa: "کاربرد a، an و the", level: "A1", prerequisites: [], lessons: ["a1.articles.lesson-01"], skills: ["GRAMMAR"] },
  { id: "grammar.plural-nouns", type: "GRAMMAR", title: "Plural nouns (regular and irregular)", titleFa: "جمع بستن اسم‌ها", level: "A1", prerequisites: [], lessons: ["a1.plurals.lesson-01"], skills: ["GRAMMAR"] },
  { id: "grammar.pronouns-subject-object", type: "GRAMMAR", title: "Subject and object pronouns", titleFa: "ضمایر فاعلی و مفعولی", level: "A1", prerequisites: ["grammar.verb-be"], lessons: ["a1.pronouns.lesson-01"], skills: ["GRAMMAR"] },
  { id: "grammar.can-ability", type: "GRAMMAR", title: "can / can't for ability", titleFa: "توانایی با can و can't", level: "A1", prerequisites: ["grammar.verb-be"], lessons: ["a1.can.lesson-01"], skills: ["GRAMMAR"] },
  { id: "grammar.imperatives", type: "GRAMMAR", title: "Imperatives (commands and instructions)", titleFa: "جمله‌های امری", level: "A1", prerequisites: ["grammar.present-simple-affirmative"], lessons: ["a1.imperatives.lesson-01"], skills: ["GRAMMAR"] },
  { id: "grammar.question-words", type: "GRAMMAR", title: "Question words (what / where / when / who / why / how)", titleFa: "کلمات پرسشی", level: "A1", prerequisites: ["grammar.present-simple-affirmative"], lessons: ["a1.question-words.lesson-01"], skills: ["GRAMMAR"] },
];

function load(name) {
  return JSON.parse(fs.readFileSync(`${DIR}/${name}.json`, "utf8"));
}

function save(name, root) {
  fs.writeFileSync(`${DIR}/${name}.json`, JSON.stringify(root, null, 2) + "\n");
}

// --- lessons -------------------------------------------------------------
const lessonsRoot = load("lessons");
const knownLessons = new Set(lessonsRoot.lessons.map((l) => l.id));
let addedLessons = 0;
for (const lesson of lessons) {
  if (knownLessons.has(lesson.id)) continue;
  lessonsRoot.lessons.push(lesson);
  addedLessons += 1;
}

// --- vocabulary ----------------------------------------------------------
const vocabRoot = load("vocabulary");
const knownWords = new Set(vocabRoot.vocabulary.map((v) => v.id));
let addedWords = 0;
for (const item of vocabulary) {
  if (knownWords.has(item.id)) continue;
  vocabRoot.vocabulary.push(item);
  addedWords += 1;
}

// --- knowledge graph -----------------------------------------------------
const knowledgeRoot = load("knowledge");
const knownItems = new Set(knowledgeRoot.knowledge.map((k) => k.id));
let addedItems = 0;
for (const item of knowledge) {
  if (knownItems.has(item.id)) continue;
  knowledgeRoot.knowledge.push(item);
  addedItems += 1;
}

// The past simple is an A2 point: it now has a dedicated A2 lesson and is
// still reviewed in b1.work (one concept, taught twice — like grammar.verb-be).
const pastSimple = knowledgeRoot.knowledge.find((k) => k.id === "grammar.past-simple");
let pastSimpleChanged = false;
if (pastSimple && pastSimple.level !== "A2") {
  pastSimple.level = "A2";
  pastSimpleChanged = true;
}
if (pastSimple && !pastSimple.lessons.includes("a2.past-simple.lesson-01")) {
  pastSimple.lessons.push("a2.past-simple.lesson-01");
  pastSimpleChanged = true;
}

// --- one bundle-version bump for the whole A-9 batch ---------------------
const files = ["lessons", "vocabulary", "exercises", "placement", "knowledge"];
const roots = {
  lessons: lessonsRoot,
  vocabulary: vocabRoot,
  knowledge: knowledgeRoot,
  exercises: load("exercises"),
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
  `lessons +${addedLessons}, vocabulary +${addedWords}, knowledge +${addedItems}` +
    `${pastSimpleChanged ? ", grammar.past-simple → A2 (a2.past-simple.lesson-01)" : ""}` +
    `, contentVersion bumped in ${bumped} file(s) → v${NEW_VERSION}`,
);
