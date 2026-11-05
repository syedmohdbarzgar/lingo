// v43 content, part 1 — B2 topics 6 and 7: finance and investment, the global
// economy. Read by `v43_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.finance.lesson-01",
      order: 124,
      title: "Finance and Investment",
      titleFa: "مدیریت مالی و سرمایه‌گذاری",
      topic: "Money",
      tip: [
        "## قاعده",
        "پشیمانی از گذشته: wish + گذشتهٔ کامل — I wish I had invested earlier.",
        "پشیمانی از وضعیت حال: wish + گذشتهٔ ساده — I wish I had more savings.",
        "if only همان معنا را با تأکید بیشتر می‌رساند: If only we had sold last year.",
        "شرطی نوع سوم برای فرض گذشته: If we had bought in March, we would have doubled our money.",
        "## جدول",
        "I wish I had invested earlier. — کاش زودتر سرمایه‌گذاری کرده بودم.",
        "I wish I had more savings. — کاش پس‌انداز بیشتری داشتم.",
        "If we had bought in March, we would have doubled our money. — اگر در مارس خریده بودیم، پولمان دو برابر می‌شد.",
        "## مثال‌ها",
        "Every asset carries some risk. — هر دارایی ریسکی دارد.",
        "The portfolio is spread across five funds. — سبد سرمایه‌گذاری روی پنج صندوق پخش شده است.",
        "A dividend is paid twice a year. — سود سهام سالی دو بار پرداخت می‌شود.",
        "## خطاهای رایج",
        "برای پشیمانی گذشته گذشتهٔ کامل لازم است: I wish I invested ✗ (این جمله به حال اشاره دارد).",
        "asset و liability متضادند؛ liability بدهی است، نه دارایی.",
        "invest با in می‌آید: invest in shares ✓ / invest on shares ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.wish-and-regret",
    vocab: {
      id: "vocab.finance",
      title: "Finance and Investment",
      titleFa: "مالیه و سرمایه‌گذاری",
      prerequisites: ["vocab.entrepreneurship"],
    },
    words: [
      {
        slug: "asset", w: "asset", fa: "دارایی", ipa: "/ˈæset/", pos: "noun",
        def: "something valuable that you own",
        ex: [
          ["Every asset carries some risk.", "هر دارایی ریسکی دارد."],
          ["The flat is their biggest asset.", "خانه بزرگ‌ترین دارایی آن‌هاست."],
        ],
        col: ["a valuable asset", "sell an asset"],
        note: "asset قابل‌شمارش است؛ متضاد آن liability (بدهی) است.",
      },
      {
        slug: "liability", w: "liability", fa: "بدهی؛ تعهد مالی", ipa: "/ˌlaɪəˈbɪləti/", pos: "noun",
        def: "money that you owe, or a duty that costs you",
        ex: [
          ["A large loan is a liability.", "وام بزرگ یک بدهی است."],
          ["The report lists every liability separately.", "گزارش هر بدهی را جداگانه فهرست می‌کند."],
        ],
        col: ["a financial liability", "reduce liabilities"],
        note: "liability با asset جفت می‌شود؛ در حسابداری جمع آن liabilities رایج است.",
      },
      {
        slug: "portfolio", w: "portfolio", fa: "سبد سرمایه‌گذاری", ipa: "/pɔːtˈfəʊliəʊ/", pos: "noun",
        def: "all the investments that one person holds",
        ex: [
          ["The portfolio is spread across five funds.", "سبد سرمایه‌گذاری روی پنج صندوق پخش شده است."],
          ["She added gold to her portfolio.", "او طلا را به سبد سرمایه‌گذاری‌اش اضافه کرد."],
        ],
        col: ["a balanced portfolio", "build a portfolio"],
        note: "portfolio در این درس «سبد سرمایه‌گذاری» است؛ در هنر «نمونه‌کار» معنا می‌دهد.",
      },
      {
        slug: "dividend", w: "dividend", fa: "سود سهام", ipa: "/ˈdɪvɪdend/", pos: "noun",
        def: "part of a company's profit paid to its shareholders",
        ex: [
          ["A dividend is paid twice a year.", "سود سهام سالی دو بار پرداخت می‌شود."],
          ["The dividend was smaller this year.", "سود سهام امسال کمتر بود."],
        ],
        col: ["pay a dividend", "a small dividend"],
        note: "dividend قابل‌شمارش است و با pay می‌آید؛ shareholder سهام‌دار است.",
      },
      {
        slug: "invest", w: "invest", fa: "سرمایه‌گذاری کردن", ipa: "/ɪnˈvest/", pos: "verb",
        def: "to put money into something to earn more later",
        ex: [
          ["They invest a little every month.", "آن‌ها هر ماه کمی سرمایه‌گذاری می‌کنند."],
          ["He invested in two funds.", "او در دو صندوق سرمایه‌گذاری کرد."],
        ],
        col: ["invest in something", "invest long-term"],
        note: "invest in something؛ اسم آن investment و عامل آن investor است.",
      },
      {
        slug: "risk", w: "risk", fa: "ریسک؛ خطر", ipa: "/rɪsk/", pos: "noun",
        def: "the chance that you lose money or fail",
        ex: [
          ["Every asset carries some risk.", "هر دارایی ریسکی دارد."],
          ["Lower risk usually means lower return.", "ریسک کمتر معمولاً یعنی بازده کمتر."],
        ],
        col: ["take a risk", "low risk"],
        note: "risk هم اسم است و هم فعل (to risk money)؛ take a risk یعنی ریسک کردن.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "asset", gloss: "دارایی",
        options: ["دارایی", "بدهی", "وام", "مالیات"],
        note: "asset چیزی ارزشمند است که مالکش هستی؛ بدهی liability است.",
      },
      {
        k: "mcq", word: "liability", gloss: "بدهی؛ تعهد مالی",
        options: ["بدهی؛ تعهد مالی", "سود سهام", "سبد سرمایه‌گذاری", "درآمد"],
        note: "liability بدهی است و در ترازنامه مقابل asset می‌آید.",
      },
      {
        k: "mcq", word: "dividend", gloss: "سود سهام",
        options: ["سود سهام", "قیمت سهام", "بهرهٔ بانکی", "کارمزد"],
        note: "dividend بخشی از سود شرکت است که به سهام‌دار پرداخت می‌شود.",
      },
      {
        k: "blank", sentence: "I wish I ___ invested earlier.",
        accepted: ["had", "had already"], skill: "VOCABULARY",
        note: "wish + had + قسمت سوم برای پشیمانی گذشته؛ already تأکید را بیشتر می‌کند.",
      },
      {
        k: "blank", sentence: "Every ___ carries some risk.",
        accepted: ["asset", "investment"], skill: "VOCABULARY",
        note: "هر دو اسم با carries some risk معنا می‌دهند؛ asset قابل‌شمارش است.",
      },
      {
        k: "recall", prompt: "سبد سرمایه‌گذاری", accepted: ["portfolio"],
        note: "سبد سرمایه‌گذاری portfolio است و با build می‌آید.",
      },
      {
        k: "tr", prompt: "کاش زودتر سرمایه‌گذاری کرده بودم.",
        accepted: [
          "i wish i had invested earlier",
          "i wish i had invested sooner",
        ],
        note: "wish + گذشتهٔ کامل یعنی حسرت کاری که انجام نشد.",
      },
      {
        k: "listen", text: "If we had bought in March, we would have doubled our money.",
        note: "به would have doubled گوش کنید: شرطی نوع سوم، نتیجهٔ نیمهٔ گذشته.",
      },
      {
        k: "prod", prompt: "از یک تصمیم مالی گذشته‌ات ابراز پشیمانی کن.",
        mustUse: ["wish", "had"], minWords: 7,
        accepted: ["I wish I had invested in that fund last year."],
        hint: "با I wish I had… شروع کن.",
        note: "ترتیب لنگرها در جمله حفظ می‌شود: wish پیش از had می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "دفتر مشاور مالی", partner: "مشاور مالی",
        prompt: "بگو کاش زودتر سرمایه‌گذاری کرده بودی.",
        question: "Have you invested before?",
        questionFa: "تا حالا سرمایه‌گذاری کرده‌ای؟",
        accepted: [
          "I wish I had invested earlier.",
          "No, and I wish I had started sooner.",
        ],
        note: "I wish I had + قسمت سوم حسرت گذشته را می‌رساند.",
      },
      {
        scene: "دفتر مشاور مالی", partner: "مشاور مالی",
        prompt: "بگو پس‌اندازت کم است و ریسک زیاد نمی‌خواهی.",
        question: "What is your main concern?",
        questionFa: "نگرانی اصلی‌ات چیست؟",
        accepted: [
          "I do not want to take a big risk with my savings.",
          "My savings are small, so I want low risk.",
        ],
        note: "take a risk با صفت می‌آید: take a big risk؛ savings جمع است.",
      },
      {
        scene: "دفتر مشاور مالی", partner: "مشاور مالی",
        prompt: "بگو سبدت روی چند صندوق پخش شده است.",
        question: "How is your portfolio built?",
        questionFa: "سبدت چطور چیده شده است؟",
        accepted: [
          "My portfolio is spread across three funds.",
          "It is spread over several funds.",
        ],
        note: "be spread across / over something یعنی پخش شدن روی چند چیز.",
      },
      {
        scene: "دفتر مشاور مالی", partner: "مشاور مالی",
        prompt: "بگو سود سهام سالی دو بار پرداخت می‌شود.",
        question: "How often are dividends paid?",
        questionFa: "سود سهام چند وقت یک بار پرداخت می‌شود؟",
        accepted: [
          "A dividend is paid twice a year.",
          "Dividends are paid every six months.",
        ],
        note: "how often برای تکرار؛ twice a year یعنی سالی دو بار.",
      },
      {
        scene: "دفتر مشاور مالی", partner: "مشاور مالی",
        prompt: "بگو خانه بزرگ‌ترین دارایی‌ات است.",
        question: "Do you own any property?",
        questionFa: "ملکی داری؟",
        accepted: [
          "Yes, my flat is my biggest asset.",
          "Yes, the house is my main asset.",
        ],
        note: "my biggest asset صفت برترین است و با my می‌آید.",
      },
      {
        scene: "دفتر مشاور مالی", partner: "مشاور مالی",
        prompt: "بگو ماهی کمی سرمایه‌گذاری می‌کنی و ممنون هستی.",
        question: "How much can you invest each month?",
        questionFa: "هر ماه چقدر می‌توانی سرمایه‌گذاری کنی؟",
        accepted: [
          "I can invest a little every month, thank you.",
          "About ten per cent of my salary.",
        ],
        note: "invest بدون مفعول هم می‌آید: I invest every month.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.economy.lesson-01",
      order: 125,
      title: "The Global Economy",
      titleFa: "اقتصاد جهانی",
      topic: "Money",
      tip: [
        "## قاعده",
        "the number of + اسم جمع فعل مفرد می‌گیرد: The number of exports has fallen.",
        "a number of + اسم جمع فعل جمع می‌گیرد: A number of countries are raising tariffs.",
        "برخی اسم‌ها مفردند هرچند شبیه جمع‌اند: Economics is a social science. / The news is good.",
        "با درصد و کسر، فعل از اسم بعد از of پیروی می‌کند: Fifty per cent of the budget is gone. / Fifty per cent of the workers are new.",
        "## جدول",
        "The number of exports has fallen. — تعداد صادرات کاهش یافته است.",
        "A number of countries are raising tariffs. — شماری از کشورها تعرفه‌ها را بالا می‌برند.",
        "Economics is a social science. — اقتصاد یک علم اجتماعی است.",
        "## مثال‌ها",
        "Inflation is rising in many markets. — تورم در بسیاری از بازارها در حال افزایش است.",
        "The country entered a recession last year. — کشور سال پیش به رکود رفت.",
        "The currency lost value against the dollar. — ارز در برابر دلار ارزش از دست داد.",
        "## خطاهای رایج",
        "the number of … has ✓ / have ✗.",
        "economics فعل مفرد می‌گیرد: economics is.",
        "tariff قابل‌شمارش است: a tariff / tariffs.",
      ].join("\n"),
    },
    grammarNode: "grammar.subject-verb-agreement",
    vocab: {
      id: "vocab.global-economy",
      title: "The Global Economy",
      titleFa: "اقتصاد جهانی",
      prerequisites: ["vocab.banking"],
    },
    words: [
      {
        slug: "inflation", w: "inflation", fa: "تورم", ipa: "/ɪnˈfleɪʃn/", pos: "noun",
        def: "a general rise in prices over time",
        ex: [
          ["Inflation is rising in many markets.", "تورم در بسیاری از بازارها در حال افزایش است."],
          ["High inflation makes food expensive.", "تورم بالا غذا را گران می‌کند."],
        ],
        col: ["high inflation", "control inflation"],
        note: "inflation غیرقابل‌شمارش است؛ متضاد آن deflation است.",
      },
      {
        slug: "recession", w: "recession", fa: "رکود", ipa: "/rɪˈseʃn/", pos: "noun",
        def: "a period when business activity falls",
        ex: [
          ["The country entered a recession last year.", "کشور سال پیش به رکود رفت."],
          ["A recession usually means fewer jobs.", "رکود معمولاً یعنی کارهای کمتر."],
        ],
        col: ["enter a recession", "a deep recession"],
        note: "recession قابل‌شمارش است و با enter می‌آید: enter a recession.",
      },
      {
        slug: "tariff", w: "tariff", fa: "تعرفه", ipa: "/ˈtærɪf/", pos: "noun",
        def: "a tax on goods coming into a country",
        ex: [
          ["The government raised the tariff on steel.", "دولت تعرفهٔ فولاد را بالا برد."],
          ["New tariffs changed the price of cars.", "تعرفه‌های جدید قیمت خودروها را عوض کرد."],
        ],
        col: ["raise a tariff", "import tariffs"],
        note: "tariff با on می‌آید: a tariff on steel؛ با tax فرق دارد، tariff مخصوص کالای وارداتی است.",
      },
      {
        slug: "currency", w: "currency", fa: "ارز", ipa: "/ˈkʌrənsi/", pos: "noun",
        def: "the money used in a country",
        ex: [
          ["The currency lost value against the dollar.", "ارز در برابر دلار ارزش از دست داد."],
          ["Tourists need local currency.", "گردشگران به ارز محلی نیاز دارند."],
        ],
        col: ["local currency", "a strong currency"],
        note: "currency قابل‌شمارش و غیرقابل‌شمارش است: a currency / foreign currency.",
      },
      {
        slug: "export", w: "export", fa: "صادرات؛ صادر کردن", ipa: "/ˈekspɔːt/", pos: "noun",
        def: "goods sold to another country",
        ex: [
          ["Oil is the country's main export.", "نفت صادرات اصلی کشور است."],
          ["They export machines to Europe.", "آن‌ها ماشین به اروپا صادر می‌کنند."],
        ],
        col: ["main export", "export goods"],
        note: "اسم آن /ˈekspɔːt/ و فعل آن /ɪkˈspɔːt/ است؛ متضاد آن import است.",
      },
      {
        slug: "growth", w: "growth", fa: "رشد اقتصادی", ipa: "/ɡrəʊθ/", pos: "noun",
        def: "an increase in the size of an economy",
        ex: [
          ["Growth was slower than expected.", "رشد کندتر از انتظار بود."],
          ["The report predicts steady growth.", "گزارش رشد پایدار را پیش‌بینی می‌کند."],
        ],
        col: ["economic growth", "steady growth"],
        note: "growth غیرقابل‌شمارش است؛ economic growth رشد اقتصادی است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "inflation", gloss: "تورم",
        options: ["تورم", "رکود", "تعرفه", "ارز"],
        note: "inflation افزایش عمومی قیمت‌هاست؛ رکود recession نام دارد.",
      },
      {
        k: "mcq", word: "tariff", gloss: "تعرفه",
        options: ["تعرفه", "مالیات بر درآمد", "یارانه", "بورس"],
        note: "tariff مالیات بر کالای وارداتی است.",
      },
      {
        k: "mcq", word: "recession", gloss: "رکود",
        options: ["رکود", "تورم", "رشد", "کسری بودجه"],
        note: "recession دورهٔ افت فعالیت اقتصادی است.",
      },
      {
        k: "blank", sentence: "The number of exports ___ fallen this year.",
        accepted: ["has", "had"], skill: "VOCABULARY",
        note: "the number of … فعل مفرد می‌گیرد؛ had برای گذشتهٔ کامل قبل‌تر است.",
      },
      {
        k: "blank", sentence: "A number of countries ___ raising tariffs.",
        accepted: ["are", "were"], skill: "VOCABULARY",
        note: "a number of … فعل جمع می‌گیرد؛ are و were هر دو جمع‌اند.",
      },
      {
        k: "recall", prompt: "تعرفه", accepted: ["tariff"],
        note: "تعرفه tariff است و با on می‌آید.",
      },
      {
        k: "tr", prompt: "تورم در بسیاری از بازارها در حال افزایش است.",
        accepted: [
          "inflation is rising in many markets",
          "inflation is rising in many markets around the world",
        ],
        note: "inflation غیرقابل‌شمارش است، پس فعل مفرد is می‌گیرد.",
      },
      {
        k: "listen", text: "The country entered a recession last year.",
        note: "به entered گوش کنید: enter a recession ترکیب ثابت است.",
      },
      {
        k: "prod", prompt: "دربارهٔ اقتصاد کشورت یک جمله بگو و از عبارت the number of استفاده کن.",
        mustUse: ["number", "has"], minWords: 7,
        accepted: ["The number of exports has fallen this year."],
        hint: "با The number of… has… شروع کن.",
        note: "the number of فعل مفرد می‌گیرد؛ ترتیب number پیش از has است.",
      },
    ],
    dialogue: [
      {
        scene: "کلاس اقتصاد، بحث دربارهٔ تورم", partner: "استاد اقتصاد",
        prompt: "بگو تورم در بسیاری از بازارها بالا می‌رود.",
        question: "What is happening with prices?",
        questionFa: "قیمت‌ها چه می‌شود؟",
        accepted: [
          "Inflation is rising in many markets.",
          "Prices are going up because of inflation.",
        ],
        note: "inflation مفرد و غیرقابل‌شمارش است: inflation is rising.",
      },
      {
        scene: "کلاس اقتصاد، بحث دربارهٔ تورم", partner: "استاد اقتصاد",
        prompt: "بگو کشور سال پیش به رکود رفت.",
        question: "Did the economy grow last year?",
        questionFa: "اقتصاد سال پیش رشد کرد؟",
        accepted: [
          "No, the country entered a recession last year.",
          "It did not grow, and we entered a recession.",
        ],
        note: "enter a recession؛ در پاسخ کوتاه منفی did not کافی است.",
      },
      {
        scene: "کلاس اقتصاد، بحث دربارهٔ تورم", partner: "استاد اقتصاد",
        prompt: "بگو ارز در برابر دلار ارزش از دست داد.",
        question: "What happened to the currency?",
        questionFa: "ارز چه شد؟",
        accepted: [
          "The currency lost value against the dollar.",
          "It fell against the dollar last month.",
        ],
        note: "lose value against something یعنی در برابر آن افت کردن.",
      },
      {
        scene: "کلاس اقتصاد، بحث دربارهٔ تورم", partner: "استاد اقتصاد",
        prompt: "بگو تعداد صادرات کاهش یافته است.",
        question: "How are exports doing?",
        questionFa: "صادرات چطور است؟",
        accepted: [
          "The number of exports has fallen.",
          "Exports have fallen since March.",
        ],
        note: "the number of → has؛ خود exports به‌تنهایی با have می‌آید.",
      },
      {
        scene: "کلاس اقتصاد، بحث دربارهٔ تورم", partner: "استاد اقتصاد",
        prompt: "بگو شماری از کشورها تعرفه‌ها را بالا می‌برند.",
        question: "Why are prices different across countries?",
        questionFa: "چرا قیمت‌ها بین کشورها فرق دارد؟",
        accepted: [
          "A number of countries are raising tariffs.",
          "Because several countries raised tariffs.",
        ],
        note: "a number of + جمع → are؛ raise a tariff یعنی تعرفه را بالا بردن.",
      },
      {
        scene: "کلاس اقتصاد، بحث دربارهٔ تورم", partner: "استاد اقتصاد",
        prompt: "بگو رشد کندتر از انتظار بود.",
        question: "What does the report say about growth?",
        questionFa: "گزارش دربارهٔ رشد چه می‌گوید؟",
        accepted: [
          "Growth was slower than expected.",
          "The report says growth is slowing down.",
        ],
        note: "slower than expected مقایسهٔ کوتاه برای بیان پیش‌بینی نشده است.",
      },
    ],
  },
];
