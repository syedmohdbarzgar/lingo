/**
 * v27 — A1 syllabus batch 1.
 *
 * The published curriculum plan lists 30 topics per CEFR band. This batch starts
 * the A1 list: countries & nationalities, age & birthday, days & months, time,
 * colours and classroom objects. Every lesson is authored content (6 words with
 * two bilingual examples each, a sectioned teaching page, a guided listening
 * drill and a free-production cue), not a stub — `validateContent` and the
 * content tests decide whether it ships.
 *
 * One-shot batch, kept only as the record of how the bundle reached v27.
 * NEVER re-run it: it appends, so a second run duplicates every row.
 *
 *   node scripts/archive/v27_a1_syllabus.mjs
 */
import fs from "node:fs";
import path from "node:path";

const ROOT = "app/src/main/assets/content";
const read = (name) => JSON.parse(fs.readFileSync(path.join(ROOT, name), "utf8"));
const write = (name, value) => fs.writeFileSync(path.join(ROOT, name), JSON.stringify(value, null, 2) + "\n");

const VERSION = 27;

/** Lesson definitions. `words[2]` is the recall word, `words[0]`/`words[1]`/`words[3]` the meaning items. */
const lessons = [
  {
    id: "a1.countries.lesson-01",
    title: "Countries and Nationalities",
    titleFa: "کشورها و ملیت‌ها",
    topic: "People",
    minutes: 6,
    order: 49,
    tip: [
      "## قاعده",
      "برای پرسیدن ملیت از «Where are you from?» استفاده می‌کنیم و پاسخ با am/is/are + from می‌آید: I am from Iran.",
      "نام کشور، ملیت و زبان همیشه با حرف بزرگ نوشته می‌شود: Iran, Iranian, Persian.",
      "## جدول",
      "country — کشور",
      "nationality — ملیت",
      "language — زبان",
      "capital — پایتخت",
      "city — شهر",
      "map — نقشه",
      "## مثال‌ها",
      "Where are you from? — اهل کجایی؟",
      "She is from Japan. — او اهل ژاپن است.",
      "Paris is the capital of France. — پاریس پایتخت فرانسه است.",
      "## خطاهای رایج",
      "«I am from Iranian» غلط است؛ بعد از from نام کشور می‌آید، نه ملیت: I am from Iran.",
      "حرف بزرگ را فراموش نکنید: English نه english.",
      "## تلفظ",
      "واکهٔ country کوتاه /ʌ/ است، نه /aʊ/: /ˈkʌntri/.",
      "nationality پنج هجا دارد و تکیه روی هجای سوم است: na-tion-AL-i-ty.",
    ].join("\n"),
    words: [
      {
        w: "country", fa: "کشور", def: "A land with its own people and government",
        ipa: "/ˈkʌntri/", pos: "noun",
        ex: [["Iran is a large country.", "ایران کشوری بزرگ است."],
             ["Which country are you from?", "اهل کدام کشور هستی؟"]],
        col: ["a large country", "my country"],
        note: "country برای سرزمین و کشور است؛ city برای شهر و capital برای پایتخت به کار می‌رود.",
      },
      {
        w: "nationality", fa: "ملیت", def: "The country a person legally belongs to",
        ipa: "/ˌnæʃəˈnæləti/", pos: "noun",
        ex: [["My nationality is Iranian.", "ملیت من ایرانی است."],
             ["The form asks for your nationality.", "فرم، ملیت شما را می‌پرسد."]],
        col: ["ask about nationality", "dual nationality"],
        note: "ملیت را با to be می‌گوییم: My nationality is Iranian — نه «I have Iranian nationality» در مکالمهٔ سادهٔ A1.",
      },
      {
        w: "language", fa: "زبان", def: "The words people use to speak and write",
        ipa: "/ˈlæŋɡwɪdʒ/", pos: "noun",
        ex: [["Persian is my first language.", "فارسی زبان اول من است."],
             ["She speaks three languages.", "او سه زبان صحبت می‌کند."]],
        col: ["first language", "speak a language"],
        note: "زبان را با فعل speak می‌آوریم: speak English؛ و «به انگلیسی» می‌شود in English.",
      },
      {
        w: "capital", fa: "پایتخت", def: "The main city of a country",
        ipa: "/ˈkæpɪtl/", pos: "noun",
        ex: [["Tehran is the capital of Iran.", "تهران پایتخت ایران است."],
             ["What is the capital of Japan?", "پایتخت ژاپن چیست؟"]],
        col: ["the capital city", "the capital of"],
        note: "capital همیشه با the می‌آید: the capital of Iran.",
      },
      {
        w: "city", fa: "شهر", def: "A large town",
        ipa: "/ˈsɪti/", pos: "noun",
        ex: [["Tehran is a busy city.", "تهران شهری شلوغ است."],
             ["I live in a small city.", "من در شهری کوچک زندگی می‌کنم."]],
        col: ["a big city", "live in a city"],
        note: "city با c نوشته می‌شود ولی /s/ خوانده می‌شود: /ˈsɪti/.",
      },
      {
        w: "map", fa: "نقشه", def: "A drawing of a place seen from above",
        ipa: "/mæp/", pos: "noun",
        ex: [["Look at the map of Asia.", "به نقشهٔ آسیا نگاه کن."],
             ["I need a map of this city.", "به نقشهٔ این شهر نیاز دارم."]],
        col: ["a world map", "look at the map"],
        note: "map نقشهٔ جغرافیایی است؛ برای نقشهٔ ساختمان یا مسیر شهری معمولاً map هم کافی است.",
      },
    ],
    blanks: [
      {
        s: "Tehran is a very big ______ in Iran.",
        accepted: ["city", "town"],
        skill: "VOCABULARY",
        ex: "«شهر بزرگ» در این جمله city (یا town) است؛ country کشور و capital پایتخت است.",
      },
      {
        s: "She ______ from Japan.",
        accepted: ["is", "comes"],
        skill: "GRAMMAR",
        ex: "با from هم فعل be می‌آید (she is from) و هم come: she comes from Japan.",
      },
    ],
    translation: {
      prompt: "او اهل ژاپن است.",
      accepted: ["she is from japan", "he is from japan", "she comes from japan"],
      bank: ["she", "he", "is", "from", "japan", "iran", "are", "in"],
      ex: "برای ملیت از be + from استفاده می‌کنیم: She is from Japan.",
    },
    production: {
      prompt: "من اهل ایران هستم و به فارسی و انگلیسی صحبت می‌کنم.",
      accepted: ["I am from Iran and I speak Persian and English."],
      mustUse: ["from", "speak"],
      minWords: 6,
      hintFa: "برای مبدأ از from و برای زبان از speak استفاده کن.",
      ex: "دو جمله را با and وصل می‌کنیم: I am from Iran and I speak Persian and English.",
    },
    listening: {
      text: "My country is Iran and my nationality is Iranian.",
      accepted: ["my country is iran and my nationality is iranian"],
      ex: "به تکیهٔ na-tion-AL-i-ty در nationality و /ʌ/ کوتاه در country گوش کن.",
    },
    node: { id: "vocab.countries", title: "Countries and nationalities", titleFa: "کشورها و ملیت‌ها", prereq: ["vocab.alphabet"] },
  },
  {
    id: "a1.age-birthday.lesson-01",
    title: "Age and Birthday",
    titleFa: "سن و تاریخ تولد",
    topic: "People",
    minutes: 6,
    order: 50,
    tip: [
      "## قاعده",
      "سن را با «How old are you?» می‌پرسیم و با be + عدد + years old پاسخ می‌دهیم: I am twenty years old.",
      "تاریخ تولد را با «When is your birthday?» می‌پرسیم: My birthday is in May.",
      "برای «متولد شدن» همیشه گذشته به کار می‌رود: I was born in 2010.",
      "## جدول",
      "age — سن",
      "birthday — تولد",
      "born — متولد شدن",
      "year — سال",
      "old — سال‌دار / پیر",
      "party — مهمانی",
      "## مثال‌ها",
      "How old is your brother? — برادرت چند سال دارد؟",
      "My birthday is on Friday. — تولدم جمعه است.",
      "We had a small party. — ما مهمانی کوچکی داشتیم.",
      "## خطاهای رایج",
      "«I have 20 years» غلط است؛ در انگلیسی سن با be گفته می‌شود: I am 20 years old.",
      "born فعل است، نه صفت: I was born، نه «I am born».",
      "## تلفظ",
      "age مثل «اِیج» با /eɪ/ خوانده می‌شود، نه /æ/.",
      "birthday تکیه روی بخش اول دارد: BIRTH-day.",
    ].join("\n"),
    words: [
      {
        w: "age", fa: "سن", def: "How old a person is",
        ipa: "/eɪdʒ/", pos: "noun",
        ex: [["What is your age?", "سنت چند است؟"],
             ["Children start school at the age of six.", "کودکان در سن شش سالگی به مدرسه می‌روند."]],
        col: ["at the age of", "your age"],
        note: "age در گفت‌وگوی روزمره کم می‌آید؛ معمولاً می‌پرسیم How old are you?",
      },
      {
        w: "birthday", fa: "تولد", def: "The day you were born",
        ipa: "/ˈbɜːrθdeɪ/", pos: "noun",
        ex: [["My birthday is in May.", "تولد من در ماه مه است."],
             ["Happy birthday to you!", "تولدت مبارک!"]],
        col: ["happy birthday", "birthday party"],
        note: "birthday با th نوشته می‌شود؛ /θ/ را با نوک زبان بین دندان‌ها بگویید.",
      },
      {
        w: "born", fa: "متولد شدن", def: "Came into life",
        ipa: "/bɔːrn/", pos: "verb",
        ex: [["I was born in Tehran.", "من در تهران به دنیا آمدم."],
             ["She was born in 2012.", "او در سال ۲۰۱۲ متولد شد."]],
        col: ["be born", "born in"],
        note: "born همیشه با was/were می‌آید: I was born — نه I born.",
      },
      {
        w: "year", fa: "سال", def: "Twelve months",
        ipa: "/jɪr/", pos: "noun",
        ex: [["This year is very busy.", "امسال خیلی شلوغ است."],
             ["He is ten years old.", "او ده سال دارد."]],
        col: ["last year", "every year"],
        note: "year در «years old» جمع می‌شود: ten years old.",
      },
      {
        w: "old", fa: "سال‌دار / پیر", def: "Having lived for many years",
        ipa: "/oʊld/", pos: "adjective",
        ex: [["How old is your sister?", "خواهرت چند سال دارد؟"],
             ["My grandfather is old but strong.", "پدربزرگم پیر است اما قوی."]],
        col: ["how old", "years old"],
        note: "old در پرسش سن فقط نشانهٔ ساختار است: How old are you? یعنی «چند سال داری؟».",
      },
      {
        w: "party", fa: "مهمانی", def: "A happy meeting with friends or family",
        ipa: "/ˈpɑːrti/", pos: "noun",
        ex: [["We had a birthday party.", "ما مهمانی تولد داشتیم."],
             ["Come to my party on Friday.", "جمعه به مهمانی من بیا."]],
        col: ["birthday party", "have a party"],
        note: "party برای مهمانی شاد است؛ برای جلسهٔ کاری meeting می‌گوییم.",
      },
    ],
    blanks: [
      {
        s: "My ______ is on Friday. I am having a small party.",
        accepted: ["birthday", "birthday party"],
        skill: "VOCABULARY",
        ex: "جملهٔ بعد از مهمانی می‌گوید؛ پس واژهٔ تولد (birthday) درست است.",
      },
      {
        s: "I ______ born in Tehran.",
        accepted: ["was"],
        skill: "GRAMMAR",
        ex: "born همیشه با گذشتهٔ be می‌آید و برای I به was نیاز دارد: I was born.",
      },
    ],
    translation: {
      prompt: "من بیست سال دارم.",
      accepted: ["i am twenty years old", "i'm twenty years old"],
      bank: ["i", "am", "twenty", "years", "old", "have", "is", "year"],
      ex: "سن با be گفته می‌شود: I am twenty years old — not I have twenty years.",
    },
    production: {
      prompt: "تولد من در ماه مه است و من در تهران متولد شدم.",
      accepted: ["My birthday is in May and I was born in Tehran."],
      mustUse: ["birthday", "born"],
      minWords: 7,
      hintFa: "برای ماه از in و برای تولد از was born استفاده کن.",
      ex: "دو جمله با and: My birthday is in May and I was born in Tehran.",
    },
    listening: {
      text: "My sister is twelve years old and her birthday is in June.",
      accepted: ["my sister is twelve years old and her birthday is in june"],
      ex: "به /ɜːr/ در birthday و /jɪr/ در years گوش کن؛ هر دو کشیده خوانده می‌شوند.",
    },
    node: { id: "vocab.age-birthday", title: "Age and birthdays", titleFa: "سن و تولد", prereq: ["vocab.numbers"] },
  },
  {
    id: "a1.days-months.lesson-01",
    title: "Days and Months",
    titleFa: "روزهای هفته و ماه‌ها",
    topic: "Time",
    minutes: 6,
    order: 51,
    tip: [
      "## قاعده",
      "روزهای هفته و ماه‌ها همیشه با حرف بزرگ نوشته می‌شوند: Monday, May.",
      "برای روز از on استفاده می‌کنیم (on Monday) و برای ماه از in (in May).",
      "today و tomorrow حرف اضافه نمی‌گیرند: I am busy today.",
      "## جدول",
      "day — روز",
      "week — هفته",
      "month — ماه",
      "weekend — آخر هفته",
      "today — امروز",
      "tomorrow — فردا",
      "## مثال‌ها",
      "I work from Monday to Friday. — از دوشنبه تا جمعه کار می‌کنم.",
      "My birthday is in May. — تولدم در ماه مه است.",
      "See you tomorrow. — فردا می‌بینمت.",
      "## خطاهای رایج",
      "«in Monday» غلط است؛ برای روز on می‌آید: on Monday.",
      "«tomorrow day» وجود ندارد؛ همین tomorrow کافی است.",
      "## تلفظ",
      "واکهٔ day و today کشیده /eɪ/ است: /deɪ/.",
      "month با /ʌ/ کوتاه خوانده می‌شود و th پایانش /θ/ است: /mʌnθ/.",
    ].join("\n"),
    words: [
      {
        w: "day", fa: "روز", def: "Twenty-four hours",
        ipa: "/deɪ/", pos: "noun",
        ex: [["Have a nice day!", "روز خوبی داشته باشی!"],
             ["Monday is my busiest day.", "دوشنبه شلوغ‌ترین روز من است."]],
        col: ["every day", "a nice day"],
        note: "every day یعنی «هر روز»؛ everyday (یک کلمه) صفت است: everyday English.",
      },
      {
        w: "week", fa: "هفته", def: "Seven days",
        ipa: "/wiːk/", pos: "noun",
        ex: [["I study English twice a week.", "هفته‌ای دو بار انگلیسی می‌خوانم."],
             ["Next week we have a test.", "هفتهٔ بعد آزمون داریم."]],
        col: ["next week", "twice a week"],
        note: "week با /iː/ بلند خوانده می‌شود؛ weak («ضعیف») هم همین تلفظ را دارد.",
      },
      {
        w: "month", fa: "ماه", def: "One of the twelve parts of a year",
        ipa: "/mʌnθ/", pos: "noun",
        ex: [["May is my favourite month.", "مه ماه مورد علاقهٔ من است."],
             ["I see her every month.", "هر ماه او را می‌بینم."]],
        col: ["last month", "this month"],
        note: "month با /ʌ/ کوتاه است، نه /aʊ/؛ و th پایانی /θ/ خوانده می‌شود.",
      },
      {
        w: "weekend", fa: "آخر هفته", def: "Saturday and Sunday",
        ipa: "/ˈwiːkend/", pos: "noun",
        ex: [["We go to the park at the weekend.", "آخر هفته به پارک می‌رویم."],
             ["My weekend was quiet.", "آخر هفتهٔ من آرام بود."]],
        col: ["at the weekend", "a long weekend"],
        note: "در انگلیسی بریتانیایی «at the weekend» و در آمریکایی «on the weekend» رایج است.",
      },
      {
        w: "today", fa: "امروز", def: "This day",
        ipa: "/təˈdeɪ/", pos: "adverb",
        ex: [["Today is Monday.", "امروز دوشنبه است."],
             ["I am very tired today.", "امروز خیلی خسته‌ام."]],
        col: ["today is", "later today"],
        note: "today بدون at می‌آید: today، نه «in today».",
      },
      {
        w: "tomorrow", fa: "فردا", def: "The day after today",
        ipa: "/təˈmɑːroʊ/", pos: "adverb",
        ex: [["See you tomorrow!", "فردا می‌بینمت!"],
             ["Tomorrow is a holiday.", "فردا تعطیل است."]],
        col: ["see you tomorrow", "tomorrow morning"],
        note: "تکیه روی هجای آخر است: to-MOR-row.",
      },
    ],
    blanks: [
      {
        s: "I play football ______ Friday.",
        accepted: ["on"],
        skill: "GRAMMAR",
        ex: "برای روزهای هفته از on استفاده می‌کنیم: on Friday.",
      },
      {
        s: "My favourite ______ is May.",
        accepted: ["month", "month of the year"],
        skill: "VOCABULARY",
        ex: "May نام ماه است، پس واژهٔ month درست است.",
      },
    ],
    translation: {
      prompt: "من دوشنبه‌ها کار می‌کنم.",
      accepted: ["i work on monday", "i work on mondays"],
      bank: ["i", "work", "on", "monday", "in", "works", "mondays", "am"],
      ex: "برای روز هفته on و برای کار روزمره حال ساده به کار می‌رود: I work on Monday.",
    },
    production: {
      prompt: "تولد من در ماه مه است و فردا مهمانی دارم.",
      accepted: ["My birthday is in May and I have a party tomorrow."],
      mustUse: ["in", "tomorrow"],
      minWords: 8,
      hintFa: "برای ماه in و برای فردا tomorrow را در جمله بگذار.",
      ex: "دو جمله با and: My birthday is in May and I have a party tomorrow.",
    },
    listening: {
      text: "I work from Monday to Friday and I rest at the weekend.",
      accepted: ["i work from monday to friday and i rest at the weekend"],
      ex: "به /deɪ/ در Monday و Friday و /iː/ در weekend گوش کن.",
    },
    node: { id: "vocab.days-months", title: "Days, weeks and months", titleFa: "روزها و ماه‌ها", prereq: ["vocab.numbers"] },
  },
  {
    id: "a1.time.lesson-01",
    title: "Time and Clock",
    titleFa: "ساعت و زمان",
    topic: "Time",
    minutes: 6,
    order: 52,
    tip: [
      "## قاعده",
      "برای پرسیدن ساعت می‌گوییم «What time is it?» و پاسخ با It is می‌آید: It is seven o'clock.",
      "برای ساعت دقیق از at استفاده می‌کنیم: The class starts at eight.",
      "half past seven یعنی ۷:۳۰ و quarter past seven یعنی ۷:۱۵.",
      "## جدول",
      "time — زمان / ساعت",
      "clock — ساعت دیواری",
      "hour — ساعت (واحد زمان)",
      "minute — دقیقه",
      "early — زود",
      "late — دیر",
      "## مثال‌ها",
      "What time is it? — ساعت چند است؟",
      "I get up at six o'clock. — ساعت شش بیدار می‌شوم.",
      "You are five minutes late. — پنج دقیقه دیر کرده‌ای.",
      "## خطاهای رایج",
      "«It is 7 hours» غلط است؛ ساعت با o'clock گفته می‌شود: It is seven o'clock.",
      "at + ساعت، نه in: at nine o'clock.",
      "## تلفظ",
      "hour با h بی‌صدا شروع می‌شود: /ˈaʊər/ — مثل our.",
      "minute دو هجا دارد و تکیه روی اول است: MIN-ute /ˈmɪnɪt/.",
    ].join("\n"),
    words: [
      {
        w: "time", fa: "زمان / ساعت", def: "The hours and minutes of the day",
        ipa: "/taɪm/", pos: "noun",
        ex: [["What time is it now?", "الان ساعت چند است؟"],
             ["I do not have time today.", "امروز وقت ندارم."]],
        col: ["what time", "have time"],
        note: "time هم «زمان» است و هم «ساعت» در پرسش ساعت: What time is it?",
      },
      {
        w: "clock", fa: "ساعت دیواری", def: "A machine that shows the time",
        ipa: "/klɒk/", pos: "noun",
        ex: [["The clock on the wall is slow.", "ساعت روی دیوار عقب است."],
             ["There is a clock in every classroom.", "در هر کلاس یک ساعت هست."]],
        col: ["alarm clock", "the clock on the wall"],
        note: "clock برای ساعت دیواری و رومیزی است؛ برای ساعت مچی watch می‌گوییم.",
      },
      {
        w: "hour", fa: "ساعت (واحد زمان)", def: "Sixty minutes",
        ipa: "/ˈaʊər/", pos: "noun",
        ex: [["The lesson is one hour long.", "درس یک ساعت طول می‌کشد."],
             ["I waited for two hours.", "دو ساعت صبر کردم."]],
        col: ["an hour", "half an hour"],
        note: "hour با h بی‌صدا شروع می‌شود، پس an hour درست است، نه a hour.",
      },
      {
        w: "minute", fa: "دقیقه", def: "Sixty seconds",
        ipa: "/ˈmɪnɪt/", pos: "noun",
        ex: [["Wait five minutes, please.", "لطفاً پنج دقیقه صبر کنید."],
             ["The train leaves in ten minutes.", "قطار ده دقیقهٔ دیگر حرکت می‌کند."]],
        col: ["five minutes", "in a minute"],
        note: "minute به معنی «دقیقه» /ˈmɪnɪt/ است؛ «کوچک» صفت دیگری است.",
      },
      {
        w: "early", fa: "زود", def: "Before the usual time",
        ipa: "/ˈɜːrli/", pos: "adjective",
        ex: [["I get up early every day.", "هر روز زود بیدار می‌شوم."],
             ["She arrived early for the class.", "او برای کلاس زود رسید."]],
        col: ["get up early", "early morning"],
        note: "early هم صفت است و هم قید: an early train / get up early.",
      },
      {
        w: "late", fa: "دیر", def: "After the usual time",
        ipa: "/leɪt/", pos: "adjective",
        ex: [["Sorry, I am late.", "ببخشید، دیر کردم."],
             ["The bus is ten minutes late.", "اتوبوس ده دقیقه تأخیر دارد."]],
        col: ["be late", "late at night"],
        note: "برای تأخیر از be late استفاده می‌کنیم: I am late — نه I have late.",
      },
    ],
    blanks: [
      {
        s: "The class starts ______ nine o'clock.",
        accepted: ["at"],
        skill: "GRAMMAR",
        ex: "برای ساعت دقیق از at استفاده می‌کنیم: at nine o'clock.",
      },
      {
        s: "I waited for two ______.",
        accepted: ["hours", "hours long"],
        skill: "VOCABULARY",
        ex: "واحد زمان برای دو ساعت hour است و جمع می‌شود: two hours.",
      },
    ],
    translation: {
      prompt: "ساعت هفت است و من دیر کرده‌ام.",
      accepted: ["it is seven o'clock and i am late", "it's seven o'clock and i'm late"],
      bank: ["it", "is", "seven", "o'clock", "and", "i", "am", "late", "at", "hour"],
      ex: "برای ساعت از It is … o'clock و برای تأخیر از am late استفاده می‌کنیم.",
    },
    production: {
      prompt: "من هر روز ساعت شش بیدار می‌شوم و ساعت هشت کلاس دارم.",
      accepted: ["I get up at six every day and I have a class at eight."],
      mustUse: ["at", "every day"],
      minWords: 9,
      hintFa: "ساعت را با at و تکرار روزانه را با every day بیان کن.",
      ex: "دو جمله با and: I get up at six every day and I have a class at eight.",
    },
    listening: {
      text: "The clock says half past seven and the bus is late.",
      accepted: ["the clock says half past seven and the bus is late"],
      ex: "به /klɒk/ در clock و /leɪt/ در late گوش کن؛ واکهٔ late کشیده است.",
    },
    node: { id: "vocab.time", title: "Telling the time", titleFa: "گفتن ساعت", prereq: ["vocab.numbers"] },
  },
  {
    id: "a1.colors.lesson-01",
    title: "Colors",
    titleFa: "رنگ‌ها",
    topic: "Description",
    minutes: 5,
    order: 53,
    tip: [
      "## قاعده",
      "رنگ صفت است و پیش از اسم می‌آید: a red car، نه «a car red».",
      "رنگ‌ها جمع بسته نمی‌شوند: two red bags، نه «two reds bags».",
      "برای پرسیدن رنگ می‌گوییم «What color is …?»: What color is your bag?",
      "## جدول",
      "color — رنگ",
      "red — قرمز",
      "blue — آبی",
      "green — سبز",
      "black — سیاه",
      "white — سفید",
      "## مثال‌ها",
      "My bag is black. — کیف من سیاه است.",
      "She has a red pen. — او یک خودکار قرمز دارد.",
      "What color is your car? — ماشینت چه رنگی است؟",
      "## خطاهای رایج",
      "جای صفت را عوض نکنید: a blue shirt درست است، نه «a shirt blue».",
      "در انگلیسی بریتانیایی colour و در آمریکایی color نوشته می‌شود؛ هر دو درست‌اند.",
      "## تلفظ",
      "blue واکهٔ بلند /uː/ دارد؛ black واکهٔ کوتاه /æ/.",
      "white با /waɪt/ خوانده می‌شود؛ h در آن شنیده نمی‌شود.",
    ].join("\n"),
    words: [
      {
        w: "color", fa: "رنگ", def: "Red, blue, green and so on",
        ipa: "/ˈkʌlər/", pos: "noun",
        ex: [["What is your favorite color?", "رنگ مورد علاقه‌ات چیست؟"],
             ["Blue is a calm color.", "آبی رنگی آرام است."]],
        col: ["favorite color", "a bright color"],
        note: "در انگلیسی بریتانیایی colour نوشته می‌شود؛ تلفظ یکی است.",
      },
      {
        w: "red", fa: "قرمز", def: "The color of blood",
        ipa: "/red/", pos: "adjective",
        ex: [["She has a red coat.", "او پالتوی قرمز دارد."],
             ["The apple is red.", "سیب قرمز است."]],
        col: ["dark red", "a red light"],
        note: "red و read (گذشتهٔ read) هم‌آوا هستند: /red/.",
      },
      {
        w: "blue", fa: "آبی", def: "The color of the sky",
        ipa: "/bluː/", pos: "adjective",
        ex: [["The sky is blue today.", "امروز آسمان آبی است."],
             ["I like blue shoes.", "کفش آبی دوست دارم."]],
        col: ["light blue", "dark blue"],
        note: "blue با /uː/ بلند خوانده می‌شود؛ کوتاه کردنش معنا را عوض نمی‌کند اما طبیعی نیست.",
      },
      {
        w: "green", fa: "سبز", def: "The color of grass",
        ipa: "/ɡriːn/", pos: "adjective",
        ex: [["The trees are green in spring.", "درخت‌ها در بهار سبز هستند."],
             ["She painted the door green.", "او در را سبز رنگ کرد."]],
        col: ["light green", "a green light"],
        note: "green در چراغ راهنما یعنی «اجازهٔ حرکت».",
      },
      {
        w: "black", fa: "سیاه", def: "The darkest color",
        ipa: "/blæk/", pos: "adjective",
        ex: [["He wears black shoes.", "او کفش سیاه می‌پوشد."],
             ["My phone is black.", "گوشی من سیاه است."]],
        col: ["black coffee", "black and white"],
        note: "black coffee یعنی قهوهٔ بدون شیر.",
      },
      {
        w: "white", fa: "سفید", def: "The color of snow",
        ipa: "/waɪt/", pos: "adjective",
        ex: [["The wall is white.", "دیوار سفید است."],
             ["She has a white bag.", "او کیف سفید دارد."]],
        col: ["snow white", "black and white"],
        note: "white با /w/ شروع می‌شود؛ h در تلفظ نمی‌آید.",
      },
    ],
    blanks: [
      {
        s: "The sky is ______ on a clear day.",
        accepted: ["blue"],
        skill: "VOCABULARY",
        ex: "آسمان صاف آبی است: the sky is blue.",
      },
      {
        s: "She has a ______ coat and black shoes.",
        accepted: ["red", "blue", "green", "white"],
        skill: "VOCABULARY",
        ex: "هر رنگی این جا درست است؛ صفت رنگ پیش از اسم می‌آید: a red coat.",
      },
    ],
    translation: {
      prompt: "کیف من سیاه است.",
      accepted: ["my bag is black", "my bag is black."],
      bank: ["my", "bag", "is", "black", "are", "white", "the", "color"],
      ex: "رنگ پس از فعل be می‌آید: My bag is black.",
    },
    production: {
      prompt: "من یک کیف سبز دارم و رنگ مورد علاقه‌ام آبی است.",
      accepted: ["I have a green bag and my favorite color is blue."],
      mustUse: ["green", "color"],
      minWords: 8,
      hintFa: "صفت رنگ را پیش از اسم بگذار و رنگ محبوب را با color بگو.",
      ex: "دو جمله با and: I have a green bag and my favorite color is blue.",
    },
    listening: {
      text: "My new bag is black and my old bag is white.",
      accepted: ["my new bag is black and my old bag is white"],
      ex: "به /æ/ کوتاه در black و /waɪt/ در white گوش کن؛ طول واکه آن‌ها را جدا می‌کند.",
    },
    node: { id: "vocab.colors", title: "Colors", titleFa: "رنگ‌ها", prereq: ["vocab.alphabet"] },
  },
  {
    id: "a1.classroom.lesson-01",
    title: "Classroom Objects",
    titleFa: "اشیای کلاس",
    topic: "School",
    minutes: 5,
    order: 54,
    tip: [
      "## قاعده",
      "برای اشاره به یک چیز نزدیک از this و برای دور از that استفاده می‌کنیم: This is my book.",
      "برای چند چیز these و those می‌آید: These are my pens.",
      "حرف تعریف a پیش از صدای بی‌صدا و an پیش از صدای صدادار می‌آید: a book, an eraser.",
      "## جدول",
      "book — کتاب",
      "pen — خودکار",
      "pencil — مداد",
      "board — تخته",
      "desk — میز",
      "bag — کیف",
      "## مثال‌ها",
      "This is my English book. — این کتاب انگلیسی من است.",
      "The teacher writes on the board. — معلم روی تخته می‌نویسد.",
      "My pencils are in my bag. — مدادهای من در کیفم هستند.",
      "## خطاهای رایج",
      "«a pens» غلط است؛ a فقط با اسم مفرد می‌آید: a pen / pens.",
      "«This are my books» غلط است؛ this با is و these با are می‌آید.",
      "## تلفظ",
      "pencil دو هجا دارد و تکیه روی اول است: PEN-cil /ˈpensl/.",
      "board واکهٔ بلند /ɔː/ دارد و با /d/ تمام می‌شود: /bɔːrd/.",
    ].join("\n"),
    words: [
      {
        w: "book", fa: "کتاب", def: "Pages with words, bound together",
        ipa: "/bʊk/", pos: "noun",
        ex: [["This is my English book.", "این کتاب انگلیسی من است."],
             ["I read a book every night.", "هر شب یک کتاب می‌خوانم."]],
        col: ["read a book", "an English book"],
        note: "book هم اسم است و هم فعل (رزرو کردن): book a table.",
      },
      {
        w: "pen", fa: "خودکار", def: "A tool for writing with ink",
        ipa: "/pen/", pos: "noun",
        ex: [["Can I borrow your pen?", "می‌توانم خودکارت را قرض بگیرم؟"],
             ["This pen is blue.", "این خودکار آبی است."]],
        col: ["a blue pen", "borrow a pen"],
        note: "pen خودکار است و pencil مداد؛ این دو را با هم اشتباه نگیرید.",
      },
      {
        w: "pencil", fa: "مداد", def: "A wooden tool for writing, with graphite inside",
        ipa: "/ˈpensl/", pos: "noun",
        ex: [["Write your name with a pencil.", "نامت را با مداد بنویس."],
             ["My pencil is on the desk.", "مداد من روی میز است."]],
        col: ["a sharp pencil", "pencil case"],
        note: "pencil با c نوشته می‌شود ولی /s/ خوانده می‌شود.",
      },
      {
        w: "board", fa: "تخته", def: "The surface a teacher writes on",
        ipa: "/bɔːrd/", pos: "noun",
        ex: [["Look at the board, please.", "لطفاً به تخته نگاه کنید."],
             ["The answer is on the board.", "پاسخ روی تخته است."]],
        col: ["on the board", "the whiteboard"],
        note: "board در کلاس معمولاً با the می‌آید: look at the board.",
      },
      {
        w: "desk", fa: "میز", def: "A table for writing or studying",
        ipa: "/desk/", pos: "noun",
        ex: [["My books are on the desk.", "کتاب‌های من روی میز هستند."],
             ["There is a lamp on my desk.", "روی میزم یک چراغ است."]],
        col: ["on the desk", "a school desk"],
        note: "desk میز تحریر است؛ table میز غذاخوری یا عمومی.",
      },
      {
        w: "bag", fa: "کیف", def: "Something you carry things in",
        ipa: "/bæɡ/", pos: "noun",
        ex: [["My bag is very heavy today.", "کیفم امروز خیلی سنگین است."],
             ["Put your books in your bag.", "کتاب‌هایت را در کیفت بگذار."]],
        col: ["a school bag", "in my bag"],
        note: "bag در انگلیسی بریتانیایی «کیف» است و در آمریکایی برای کیف دستی purse هم می‌گویند.",
      },
    ],
    blanks: [
      {
        s: "______ is my English book.",
        accepted: ["this", "that"],
        skill: "GRAMMAR",
        ex: "برای اشاره به یک چیز مفرد this (نزدیک) یا that (دور) با is می‌آید.",
      },
      {
        s: "My pens are in my ______.",
        accepted: ["bag", "pencil case", "school bag"],
        skill: "VOCABULARY",
        ex: "خودکارها را در کیف می‌گذاریم: in my bag.",
      },
    ],
    translation: {
      prompt: "این کتاب من است.",
      accepted: ["this is my book", "this is my book."],
      bank: ["this", "is", "my", "book", "are", "these", "mine", "the"],
      ex: "ساختار معرفی: This is my book — this با is می‌آید.",
    },
    production: {
      prompt: "این مداد من است و آن کیف توست.",
      accepted: ["This is my pencil and that is your bag."],
      mustUse: ["this", "that"],
      minWords: 7,
      hintFa: "برای چیز نزدیک this و برای دور that را به کار ببر.",
      ex: "دو جمله با and: This is my pencil and that is your bag.",
    },
    listening: {
      text: "Open your book and write your name on the board.",
      accepted: ["open your book and write your name on the board"],
      ex: "به /bʊk/ کوتاه در book و /bɔːrd/ بلند در board گوش کن.",
    },
    node: { id: "vocab.classroom-objects", title: "Classroom objects", titleFa: "اشیای کلاس", prereq: ["vocab.alphabet"] },
  },
];

// ── bundle surgery ────────────────────────────────────────────────────────────
const lessonsFile = read("lessons.json");
const vocabFile = read("vocabulary.json");
const exercisesFile = read("exercises.json");
const knowledgeFile = read("knowledge.json");
const placementFile = read("placement.json");

const existingLessonIds = new Set(lessonsFile.lessons.map((l) => l.id));
const existingVocabIds = new Set(vocabFile.vocabulary.map((v) => v.id));
const existingExIds = new Set(exercisesFile.exercises.map((e) => e.id));

for (const lesson of lessons) {
  if (existingLessonIds.has(lesson.id)) {
    throw new Error(`${lesson.id} already exists — this batch was already applied`);
  }

  lessonsFile.lessons.push({
    id: lesson.id,
    level: "A1",
    title: lesson.title,
    titleFa: lesson.titleFa,
    topic: lesson.topic,
    estimatedMinutes: lesson.minutes,
    order: lesson.order,
    grammarTipFa: lesson.tip,
  });

  lesson.words.forEach((w, index) => {
    const id = `${lesson.id.split(".lesson")[0]}.word.${w.w.replace(/[^a-z]+/g, "-")}`;
    if (existingVocabIds.has(id)) throw new Error(`duplicate vocabulary id ${id}`);
    existingVocabIds.add(id);
    vocabFile.vocabulary.push({
      id,
      word: w.w,
      translation: w.fa,
      definition: w.def,
      pronunciation: w.ipa,
      level: "A1",
      partOfSpeech: w.pos,
      examples: w.ex.map(([en, fa]) => ({ en, fa })),
      collocations: w.col,
      lessonId: lesson.id,
      explanationFa: w.note,
    });
    void index;
  });

  const prefix = lesson.id.split(".lesson")[0];
  const pushExercise = (suffix, body) => {
    const id = `${prefix}.ex.${suffix}`;
    if (existingExIds.has(id)) throw new Error(`duplicate exercise id ${id}`);
    exercisesFile.exercises.push({ id, lessonId: lesson.id, ...body });
  };

  const meaningItem = (word, order) => ({
    type: "multiple_choice",
    order,
    question: `What does "${word.w}" mean?`,
    questionFa: `معنی «${word.w}» چیست؟`,
    options: (() => {
      const others = lesson.words.filter((x) => x.w !== word.w).map((x) => x.fa);
      const options = [word.fa, others[0], others[1], others[2]];
      // The authoring rule is a fixed authored order here; the balance script
      // rotates the correct index across the bundle afterwards.
      return options;
    })(),
    correctIndex: 0,
    skill: "VOCABULARY",
    explanationFa: `«${word.w}» یعنی «${word.fa}»؛ ${word.note}`,
  });

  pushExercise("01", meaningItem(lesson.words[0], 1));
  pushExercise("02", meaningItem(lesson.words[1], 2));

  const blank = (b, order) => ({
    type: "fill_blank",
    order,
    sentence: b.s,
    accepted: b.accepted,
    skill: b.skill,
    explanationFa: b.ex,
  });
  pushExercise("03", blank(lesson.blanks[0], 3));

  pushExercise("04", {
    type: "word_recall",
    order: 4,
    prompt: lesson.words[2].fa,
    accepted: [lesson.words[2].w],
    skill: "VOCABULARY",
    explanationFa: lesson.words[2].note,
  });

  pushExercise("05", blank(lesson.blanks[1], 5));
  pushExercise("06", meaningItem(lesson.words[3], 6));

  pushExercise("07", {
    type: "translation",
    order: 7,
    prompt: lesson.translation.prompt,
    accepted: lesson.translation.accepted,
    bank: lesson.translation.bank,
    skill: "VOCABULARY",
    explanationFa: lesson.translation.ex,
  });

  pushExercise("08", {
    type: "listening",
    order: 8,
    text: lesson.listening.text,
    accepted: lesson.listening.accepted,
    skill: "LISTENING",
    explanationFa: lesson.listening.ex,
  });

  pushExercise("09", {
    type: "sentence_production",
    order: 9,
    prompt: lesson.production.prompt,
    accepted: lesson.production.accepted,
    mustUse: lesson.production.mustUse,
    minWords: lesson.production.minWords,
    hintFa: lesson.production.hintFa,
    skill: "VOCABULARY",
    explanationFa: lesson.production.ex,
  });

  knowledgeFile.knowledge.push({
    id: lesson.node.id,
    type: "VOCABULARY",
    title: lesson.node.title,
    titleFa: lesson.node.titleFa,
    level: "A1",
    prerequisites: lesson.node.prereq,
    lessons: [lesson.id],
    skills: ["VOCABULARY", "LISTENING"],
  });
}

// Pronunciation has one node per A1 lesson (PhonologyCoverageTest): the new
// lessons are taught by the existing vowel-length node instead of inventing six
// near-duplicate nodes.
const phonology = knowledgeFile.knowledge.find((k) => k.id === "phonology.vowel-length");
if (!phonology) throw new Error("phonology.vowel-length is missing");
for (const lesson of lessons) {
  if (!phonology.lessons.includes(lesson.id)) phonology.lessons.push(lesson.id);
}

for (const file of [lessonsFile, vocabFile, exercisesFile, knowledgeFile, placementFile]) {
  file.contentVersion = VERSION;
}

write("lessons.json", lessonsFile);
write("vocabulary.json", vocabFile);
write("exercises.json", exercisesFile);
write("knowledge.json", knowledgeFile);
write("placement.json", placementFile);

console.log(
  `v${VERSION}: +${lessons.length} lessons, +${lessons.length * 6} words, ` +
    `+${lessons.length * 9} exercises, +${lessons.length} knowledge items`,
);
