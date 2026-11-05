// v45 content, part 2 — B2 topics 19 and 20: professional reports, professional
// presentations. Read by `v45_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.reports.lesson-01",
      order: 134,
      title: "Writing Professional Reports",
      titleFa: "نوشتن گزارش حرفه‌ای",
      topic: "Business",
      tip: [
        "## قاعده",
        "مجهول گذشتهٔ ساده: was/were + قسمت سوم — The data was collected in May.",
        "در گزارش رسمی فاعل اغلب معلوم نیست یا مهم نیست، پس مجهول طبیعی‌تر است.",
        "اگر انجام‌دهنده مهم باشد با by می‌آید: The survey was designed by the research team.",
        "مجهول حال کامل برای نتیجه‌ای که همین حالا مهم است: The figures have been checked.",
        "## جدول",
        "The data was collected in May. — داده‌ها در ماه مه جمع‌آوری شد.",
        "The survey was designed by the research team. — پرسش‌نامه توسط تیم پژوهش طراحی شد.",
        "The figures have been checked twice. — ارقام دو بار بررسی شده‌اند.",
        "## مثال‌ها",
        "The main finding was simple. — یافتهٔ اصلی ساده بود.",
        "The summary fits on one page. — خلاصه در یک صفحه جا می‌شود.",
        "The full table sits in the appendix. — جدول کامل در پیوست است.",
        "## خطاهای رایج",
        "مجهول گذشته was/were + قسمت سوم است: was collect ✗ / was collected ✓.",
        "by فقط وقتی می‌آید که انجام‌دهنده اطلاعات مهمی باشد.",
        "جمع appendix در متن رسمی appendices است.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-simple",
    vocab: {
      id: "vocab.professional-reports",
      title: "Writing Professional Reports",
      titleFa: "گزارش حرفه‌ای",
      prerequisites: ["vocab.charts"],
    },
    words: [
      {
        slug: "finding", w: "finding", fa: "یافته", ipa: "/ˈfaɪndɪŋ/", pos: "noun",
        def: "one result that a report or study shows",
        ex: [
          ["The main finding was simple.", "یافتهٔ اصلی ساده بود."],
          ["Each finding needs a number behind it.", "هر یافته باید عددی پشتش داشته باشد."],
        ],
        col: ["the main finding", "a key finding"],
        note: "finding قابل‌شمارش است و در گزارش‌ها جمع می‌آید: findings.",
      },
      {
        slug: "summary", w: "summary", fa: "خلاصه", ipa: "/ˈsʌməri/", pos: "noun",
        def: "a short version that gives only the main points",
        ex: [
          ["The summary fits on one page.", "خلاصه در یک صفحه جا می‌شود."],
          ["Write the summary last.", "خلاصه را آخر بنویس."],
        ],
        col: ["a short summary", "in summary"],
        note: "summary با in می‌آید: in summary یعنی «در جمع‌بندی».",
      },
      {
        slug: "appendix", w: "appendix", fa: "پیوست", ipa: "/əˈpendɪks/", pos: "noun",
        def: "extra material placed at the end of a report",
        ex: [
          ["The full table sits in the appendix.", "جدول کامل در پیوست است."],
          ["Add the survey to the appendix.", "پرسش‌نامه را به پیوست اضافه کن."],
        ],
        col: ["in the appendix", "see appendix"],
        note: "appendix مفرد است و جمع آن appendices؛ appendix در پزشکی «آپاندیس» است.",
      },
      {
        slug: "draft", w: "draft", fa: "پیش‌نویس", ipa: "/drɑːft/", pos: "noun",
        def: "an early version of a document",
        ex: [
          ["The first draft was twice as long.", "پیش‌نویس اول دو برابر بلندتر بود."],
          ["Send me the draft by Thursday.", "پیش‌نویس را تا پنجشنبه برایم بفرست."],
        ],
        col: ["a first draft", "edit a draft"],
        note: "draft هم اسم است و هم فعل: to draft a report یعنی پیش‌نویس نوشتن.",
      },
      {
        slug: "figure", w: "figure", fa: "رقم؛ نمودار", ipa: "/ˈfɪɡə/", pos: "noun",
        def: "a number, or a numbered chart in a report",
        ex: [
          ["Every figure in the report was checked.", "هر رقمی در گزارش بررسی شد."],
          ["Figure two shows the drop in sales.", "نمودار دو افت فروش را نشان می‌دهد."],
        ],
        col: ["see figure two", "the figures show"],
        note: "در گزارش‌ها Figure با شماره می‌آید: Figure two؛ جمع آن figures.",
      },
      {
        slug: "recommendation", w: "recommendation", fa: "توصیه؛ پیشنهاد", ipa: "/ˌrekəmenˈdeɪʃn/", pos: "noun",
        def: "a suggestion about what should be done",
        ex: [
          ["The report ends with three recommendations.", "گزارش با سه توصیه تمام می‌شود."],
          ["One recommendation was to cut the budget.", "یکی از توصیه‌ها کاهش بودجه بود."],
        ],
        col: ["make a recommendation", "accept a recommendation"],
        note: "recommendation قابل‌شمارش است؛ فعل آن recommend بدون to می‌آید: recommend a plan.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "finding", gloss: "یافته",
        options: ["یافته", "خلاصه", "پیوست", "پیش‌نویس"],
        note: "finding نتیجه‌ای است که گزارش یا پژوهش نشان می‌دهد.",
      },
      {
        k: "mcq", word: "summary", gloss: "خلاصه",
        options: ["خلاصه", "پیوست", "نمودار", "توصیه"],
        note: "summary نسخهٔ کوتاهی است که فقط نکات اصلی را می‌آورد.",
      },
      {
        k: "mcq", word: "appendix", gloss: "پیوست",
        options: ["پیوست", "فهرست", "جدول", "پیش‌نویس"],
        note: "appendix بخش اضافه در انتهای گزارش است.",
      },
      {
        k: "blank", sentence: "The data ___ collected in May.",
        accepted: ["was", "were"], skill: "VOCABULARY",
        note: "data در انگلیسی رسمی هم با was و هم با were می‌آید.",
      },
      {
        k: "blank", sentence: "The figures ___ been checked twice.",
        accepted: ["have", "had"], skill: "VOCABULARY",
        note: "have been برای نتیجهٔ تا امروز؛ had been برای رویداد پیش‌تر.",
      },
      {
        k: "recall", prompt: "پیوست", accepted: ["appendix"],
        note: "پیوست در گزارش appendix است و جمع آن appendices.",
      },
      {
        k: "tr", prompt: "داده‌ها در ماه مه جمع‌آوری شد.",
        accepted: [
          "the data was collected in may",
          "the data were collected in may",
        ],
        note: "مجهول گذشته: was/were + قسمت سوم؛ فاعل معلوم مهم نیست.",
      },
      {
        k: "listen", text: "The summary fits on one page.",
        note: "به fits گوش کنید: the summary مفرد است، پس فعل s می‌گیرد.",
      },
      {
        k: "prod", prompt: "بگو در گزارش چه چیزی بررسی شد.",
        mustUse: ["was", "checked"], minWords: 8,
        accepted: ["Every figure in the report was checked before the meeting."],
        hint: "با Every figure… was checked… جمله را بساز.",
        note: "ترتیب لنگرها: was پیش از checked می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "دفتر مدیر مالی، تحویل گزارش فصلی", partner: "مدیر مالی",
        prompt: "بگو داده‌ها در ماه مه جمع‌آوری شده است.",
        question: "When was the data collected?",
        questionFa: "داده‌ها کِی جمع‌آوری شد؟",
        accepted: [
          "The data was collected in May.",
          "It was collected in May, before the change.",
        ],
        note: "مجهول گذشته: was collected؛ انجام‌دهنده لازم نیست.",
      },
      {
        scene: "دفتر مدیر مالی، تحویل گزارش فصلی", partner: "مدیر مالی",
        prompt: "بگو خلاصه در یک صفحه جا می‌شود.",
        question: "How long is the summary?",
        questionFa: "خلاصه چقدر است؟",
        accepted: [
          "The summary fits on one page.",
          "It fits on a single page.",
        ],
        note: "summary مفرد است: the summary fits ✓.",
      },
      {
        scene: "دفتر مدیر مالی، تحویل گزارش فصلی", partner: "مدیر مالی",
        prompt: "بگو یافتهٔ اصلی ساده بود.",
        question: "What did you find?",
        questionFa: "چه چیزی پیدا کردید؟",
        accepted: [
          "The main finding was simple.",
          "The main finding was that costs had risen.",
        ],
        note: "the main finding الگوی ثابت گزارش است.",
      },
      {
        scene: "دفتر مدیر مالی، تحویل گزارش فصلی", partner: "مدیر مالی",
        prompt: "بگو ارقام دو بار بررسی شده‌اند.",
        question: "Are the numbers reliable?",
        questionFa: "ارقام قابل‌اعتمادند؟",
        accepted: [
          "The figures have been checked twice.",
          "Yes, every figure has been checked twice.",
        ],
        note: "have been checked حال کامل مجهول است و نتیجهٔ تا امروز را می‌رساند.",
      },
      {
        scene: "دفتر مدیر مالی، تحویل گزارش فصلی", partner: "مدیر مالی",
        prompt: "بگو جدول کامل در پیوست است.",
        question: "Where is the full table?",
        questionFa: "جدول کامل کجاست؟",
        accepted: [
          "The full table sits in the appendix.",
          "It is in the appendix at the end.",
        ],
        note: "in the appendix با in می‌آید، نه at.",
      },
      {
        scene: "دفتر مدیر مالی، تحویل گزارش فصلی", partner: "مدیر مالی",
        prompt: "بگو گزارش با سه توصیه تمام می‌شود.",
        question: "Does the report end with actions?",
        questionFa: "گزارش با اقدام مشخص تمام می‌شود؟",
        accepted: [
          "Yes, the report ends with three recommendations.",
          "It ends with a short recommendation.",
        ],
        note: "recommendation قابل‌شمارش است؛ with three recommendations.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.presentations.lesson-01",
      order: 135,
      title: "Professional Presentations",
      titleFa: "ارائه و سخنرانی حرفه‌ای",
      topic: "Business",
      tip: [
        "## قاعده",
        "بند موصولی اسلاید یا بخش را معرفی می‌کند: the slide that shows the trend.",
        "برای شخص who و برای چیز that/which: the colleague who asked the question.",
        "بند غیرتعریف‌کننده توضیح اضافه است و بین دو کاما می‌آید: The last point, which I will repeat, is the ask.",
        "where برای بخش یا مکان: the part where we show the numbers.",
        "## جدول",
        "The slide that shows the trend is slide four. — اسلایدی که روند را نشان می‌دهد اسلاید چهار است.",
        "The colleague who asked the question was right. — همکاری که سؤال پرسید درست می‌گفت.",
        "The last point, which I will repeat, is the ask. — نکتهٔ آخر، که تکرارش می‌کنم، همان درخواست است.",
        "## مثال‌ها",
        "A small gesture can hold attention. — یک حرکت کوچک دست می‌تواند توجه را نگه دارد.",
        "Your pace matters more than your slides. — آهنگ سخن از اسلایدهایت مهم‌تر است.",
        "Confidence comes from rehearsal. — اعتمادبه‌نفس از تمرین می‌آید.",
        "## خطاهای رایج",
        "پس از کاما that نمی‌آید؛ آن‌جا which یا who لازم است.",
        "ضمیر در بند موصولی تکرار نمی‌شود: the slide that I showed it ✗.",
        "هر بند موصولی یک اسم لازم دارد؛ بدون آن جمله ناقص است.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.professional-presentations",
      title: "Professional Presentations",
      titleFa: "ارائهٔ حرفه‌ای",
      prerequisites: ["vocab.presentation"],
    },
    words: [
      {
        slug: "gesture", w: "gesture", fa: "اشاره؛ حرکت دست", ipa: "/ˈdʒestʃə/", pos: "noun",
        def: "a movement of the hand or head that says something",
        ex: [
          ["A small gesture can hold attention.", "یک حرکت کوچک دست می‌تواند توجه را نگه دارد."],
          ["He used gestures to mark each point.", "او با حرکت دست هر نکته را علامت می‌زد."],
        ],
        col: ["a small gesture", "use gestures"],
        note: "gesture قابل‌شمارش است: a gesture / gestures؛ در معناهای دیگر «ژست» هم می‌شود.",
      },
      {
        slug: "pace", w: "pace", fa: "آهنگ؛ سرعت", ipa: "/peɪs/", pos: "noun",
        def: "how fast you speak or move",
        ex: [
          ["Your pace matters more than your slides.", "آهنگ سخن از اسلایدهایت مهم‌تر است."],
          ["Slow the pace at the end.", "در پایان سرعت را کم کن."],
        ],
        col: ["a steady pace", "slow the pace"],
        note: "pace غیرقابل‌شمارش است در این معنا: the pace / a slow pace؛ گام هم معنا می‌دهد.",
      },
      {
        slug: "structure", w: "structure", fa: "ساختار", ipa: "/ˈstrʌktʃə/", pos: "noun",
        def: "the way the parts of a talk are arranged",
        ex: [
          ["A clear structure keeps the audience awake.", "ساختار روشن مخاطب را بیدار نگه می‌دارد."],
          ["The structure is opening, points and close.", "ساختارش این است: شروع، نکته‌ها و بستن."],
        ],
        col: ["a clear structure", "follow a structure"],
        note: "structure هم اسم و هم فعل است: to structure a talk.",
      },
      {
        slug: "outline", w: "outline", fa: "رئوس مطالب", ipa: "/ˈaʊtlaɪn/", pos: "noun",
        def: "a short plan that lists the main parts",
        ex: [
          ["Start with a one-page outline.", "با یک رئوس مطالب یک‌صفحه‌ای شروع کن."],
          ["The outline names every section.", "رئوس مطالب هر بخش را نام می‌برد."],
        ],
        col: ["a one-page outline", "write an outline"],
        note: "outline قابل‌شمارش است؛ فعل آن هم outline است یعنی خلاصه بیان کردن.",
      },
      {
        slug: "attendee", w: "attendee", fa: "شرکت‌کننده", ipa: "/əˌtenˈdiː/", pos: "noun",
        def: "a person who is present at a meeting or talk",
        ex: [
          ["Every attendee got a handout.", "هر شرکت‌کننده یک برگهٔ چاپی گرفت."],
          ["Ask the attendees what they need.", "از شرکت‌کنندگان بپرس چه می‌خواهند."],
        ],
        col: ["every attendee", "a list of attendees"],
        note: "attendee قابل‌شمارش است و از attend ساخته شده؛ attend a meeting (بدون to).",
      },
      {
        slug: "confidence", w: "confidence", fa: "اعتمادبه‌نفس", ipa: "/ˈkɒnfɪdəns/", pos: "noun",
        def: "the feeling that you can do something well",
        ex: [
          ["Confidence comes from rehearsal.", "اعتمادبه‌نفس از تمرین می‌آید."],
          ["She spoke with confidence.", "او با اعتمادبه‌نفس حرف زد."],
        ],
        col: ["speak with confidence", "build confidence"],
        note: "confidence غیرقابل‌شمارش است و با with می‌آید: with confidence.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "gesture", gloss: "اشاره؛ حرکت دست",
        options: ["اشاره؛ حرکت دست", "سکوت", "صدا", "نگاه"],
        note: "gesture حرکتی است که با دست یا سر معنا می‌رساند.",
      },
      {
        k: "mcq", word: "pace", gloss: "آهنگ؛ سرعت",
        options: ["آهنگ؛ سرعت", "بلندی صدا", "ساختار", "محتوا"],
        note: "pace سرعت حرف زدن یا حرکت کردن است.",
      },
      {
        k: "mcq", word: "outline", gloss: "رئوس مطالب",
        options: ["رئوس مطالب", "منبع", "نتیجه‌گیری", "برگهٔ چاپی"],
        note: "outline فهرست کوتاهی از بخش‌های اصلی است.",
      },
      {
        k: "blank", sentence: "The slide ___ shows the trend is slide four.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "در بند تعریف‌کننده هم that و هم which درست است.",
      },
      {
        k: "blank", sentence: "The colleague ___ asked the question was right.",
        accepted: ["who", "that"], skill: "VOCABULARY",
        note: "برای شخص who، و در بند تعریف‌کننده that.",
      },
      {
        k: "recall", prompt: "اعتمادبه‌نفس", accepted: ["confidence"],
        note: "اعتمادبه‌نفس confidence است و با with می‌آید.",
      },
      {
        k: "tr", prompt: "ساختار روشن مخاطب را بیدار نگه می‌دارد.",
        accepted: [
          "a clear structure keeps the audience awake",
          "clear structure keeps the audience awake",
        ],
        note: "the audience اسم جمع‌نما است ولی با فعل مفرد می‌آید.",
      },
      {
        k: "listen", text: "Confidence comes from rehearsal.",
        note: "به comes from گوش کنید: ساختار ثابت confidence comes from…",
      },
      {
        k: "prod", prompt: "یک اسلاید یا همکار را با بند موصولی توصیف کن.",
        mustUse: ["that", "who"], minWords: 9,
        accepted: ["The slide that shows the numbers is for the people who asked."],
        hint: "با The slide that… شروع کن و با … who asked تمام کن.",
        note: "ترتیب لنگرها: that در بند اول و who در بند دوم می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "سالن کنفرانس، نیم ساعت پیش از ارائهٔ اصلی", partner: "همکار ارشد",
        prompt: "بگو اسلایدی که روند را نشان می‌دهد اسلاید چهار است.",
        question: "Which slide has the trend?",
        questionFa: "کدام اسلاید روند را دارد؟",
        accepted: [
          "The slide that shows the trend is slide four.",
          "It is the one that shows the trend.",
        ],
        note: "بند that shows the trend صفت slide است و کاما نمی‌گیرد.",
      },
      {
        scene: "سالن کنفرانس، نیم ساعت پیش از ارائهٔ اصلی", partner: "همکار ارشد",
        prompt: "بگو ساختار روشن مخاطب را بیدار نگه می‌دارد.",
        question: "Why do we need a clear plan?",
        questionFa: "چرا به برنامهٔ روشن نیاز داریم؟",
        accepted: [
          "A clear structure keeps the audience awake.",
          "Because structure keeps people awake.",
        ],
        note: "the audience با فعل مفرد می‌آید: the audience is ✓.",
      },
      {
        scene: "سالن کنفرانس، نیم ساعت پیش از ارائهٔ اصلی", partner: "همکار ارشد",
        prompt: "بگو آهنگ سخن از اسلایدها مهم‌تر است.",
        question: "Should I speak faster?",
        questionFa: "سریع‌تر حرف بزنم؟",
        accepted: [
          "Your pace matters more than your slides.",
          "No, the pace matters more than the slides.",
        ],
        note: "مقایسه با more than انجام می‌شود و pace غیرقابل‌شمارش است.",
      },
      {
        scene: "سالن کنفرانس، نیم ساعت پیش از ارائهٔ اصلی", partner: "همکار ارشد",
        prompt: "بگو اعتمادبه‌نفس از تمرین می‌آید.",
        question: "How do I stop being nervous?",
        questionFa: "چطور دست از استرس بردارم؟",
        accepted: [
          "Confidence comes from rehearsal.",
          "Rehearse twice — confidence comes from rehearsal.",
        ],
        note: "rehearsal اسم و rehearse فعل است؛ از تمرین یعنی from rehearsal.",
      },
      {
        scene: "سالن کنفرانس، نیم ساعت پیش از ارائهٔ اصلی", partner: "همکار ارشد",
        prompt: "بگو هر شرکت‌کننده یک برگهٔ چاپی می‌گیرد.",
        question: "Do we have handouts for everyone?",
        questionFa: "برای همه برگهٔ چاپی داریم؟",
        accepted: [
          "Every attendee gets a handout.",
          "Yes, every attendee will get one.",
        ],
        note: "every attendee مفرد است، پس فعل gets می‌گیرد.",
      },
      {
        scene: "سالن کنفرانس، نیم ساعت پیش از ارائهٔ اصلی", partner: "همکار ارشد",
        prompt: "بگو نکتهٔ آخر را تکرار می‌کنی.",
        question: "What happens at the end?",
        questionFa: "در پایان چه می‌شود؟",
        accepted: [
          "The last point, which I will repeat, is the ask.",
          "I will repeat the last point before the questions.",
        ],
        note: "بند which I will repeat توضیح اضافه است و بین دو کاما می‌آید.",
      },
    ],
  },
];
