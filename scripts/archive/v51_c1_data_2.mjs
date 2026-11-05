// v51 content, part 2 — C1 topics 16 and 19: interpreting complex data and
// organizational culture. Read by `v51_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.data-interpretation.lesson-01",
      order: 158,
      title: "Interpreting Complex Data",
      titleFa: "تفسیر داده‌های پیچیده",
      topic: "Science",
      tip: [
        "## قاعده",
        "مقایسه با as … as و than: Sales are twice as high as last year.",
        "صفت عالی با the: This is the steepest fall since 2019.",
        "برای سه برابر: three times higher than؛ برای نصف: half as high as.",
        "## جدول",
        "Sales are twice as high as last year. — فروش دو برابر سال پیش است.",
        "This is the steepest fall since 2019. — این تندترین افت از ۲۰۱۹ است.",
        "Revenue is half as high as in March. — درآمد نصف مارس است.",
        "## مثال‌ها",
        "Customer segmentation makes the picture clearer. — بخش‌بندی مشتریان تصویر را روشن‌تر می‌کند.",
        "Seasonality explains part of the fall. — فصلی‌بودن بخشی از افت را توضیح می‌دهد.",
        "We take January as the baseline. — ژانویه را خط پایه می‌گیریم.",
        "## خطاهای رایج",
        "پس از twice/three times ساختار as … as می‌آید: twice as high as ✓ / twice high than ✗.",
        "the steepest fall: صفت عالی همیشه با the.",
        "baseline غیرقابل‌شمارش است؛ در گزارش‌ها a baseline هم دیده می‌شود.",
      ].join("\n"),
    },
    grammarNode: "grammar.comparatives",
    vocab: {
      id: "vocab.data-interpretation",
      title: "Interpreting Complex Data",
      titleFa: "تفسیر داده‌های پیچیده",
      prerequisites: ["vocab.charts"],
    },
    words: [
      {
        slug: "cohort", w: "cohort", fa: "گروه هم‌دوره", ipa: "/ˈkəʊhɔːt/", pos: "noun",
        def: "a group of people who share a starting point",
        ex: [
          ["The cohort was followed for two years.", "گروه هم‌دوره دو سال دنبال شد."],
          ["Each cohort behaves differently in its first month.", "هر گروه هم‌دوره در ماه اولش رفتار متفاوتی دارد."],
        ],
        col: ["the January cohort", "a cohort study"],
        note: "cohort قابل‌شمارش است؛ در آمار یعنی یک گروه که نقطهٔ شروع مشترک دارند.",
      },
      {
        slug: "segmentation", w: "segmentation", fa: "بخش‌بندی", ipa: "/ˌseɡmenˈteɪʃn/", pos: "noun",
        def: "splitting a group into smaller parts that behave alike",
        ex: [
          ["Customer segmentation makes the picture clearer.", "بخش‌بندی مشتریان تصویر را روشن‌تر می‌کند."],
          ["Segmentation by value beat segmentation by city.", "بخش‌بندی بر پایهٔ ارزش از بخش‌بندی شهری بهتر بود."],
        ],
        col: ["customer segmentation", "segment the data"],
        note: "segmentation غیرقابل‌شمارش است؛ فعل آن segment و اسم segment.",
      },
      {
        slug: "baseline", w: "baseline", fa: "خط پایه", ipa: "/ˈbeɪslaɪn/", pos: "noun",
        def: "the starting figure everything is compared to",
        ex: [
          ["We take January as the baseline.", "ژانویه را خط پایه می‌گیریم."],
          ["Without a baseline the chart says nothing.", "بدون خط پایه نمودار هیچ چیزی نمی‌گوید."],
        ],
        col: ["set a baseline", "against the baseline"],
        note: "baseline قابل‌شمارش است و با against می‌آید: against the baseline.",
      },
      {
        slug: "seasonality", w: "seasonality", fa: "فصلی‌بودن", ipa: "/ˌsiːzəˈnæləti/", pos: "noun",
        def: "a regular pattern that repeats each year",
        ex: [
          ["Seasonality explains part of the fall.", "فصلی‌بودن بخشی از افت را توضیح می‌دهد."],
          ["Seasonality makes December hard to read.", "فصلی‌بودن خواندن دسامبر را سخت می‌کند."],
        ],
        col: ["strong seasonality", "adjust for seasonality"],
        note: "seasonality غیرقابل‌شمارش است؛ صفت آن seasonal.",
      },
      {
        slug: "anomaly", w: "anomaly", fa: "ناهنجاری", ipa: "/əˈnɒməli/", pos: "noun",
        def: "a result that does not fit the pattern",
        ex: [
          ["The anomaly is a recording error.", "آن ناهنجاری یک خطای ثبت است."],
          ["Every anomaly was checked by hand.", "هر ناهنجاری دستی بررسی شد."],
        ],
        col: ["a small anomaly", "explain the anomaly"],
        note: "جمع آن anomalies است؛ صفت آن anomalous.",
      },
      {
        slug: "dashboard", w: "dashboard", fa: "داشبورد", ipa: "/ˈdæʃbɔːd/", pos: "noun",
        def: "one screen that shows the key numbers",
        ex: [
          ["The dashboard shows four numbers, not forty.", "داشبورد چهار عدد نشان می‌دهد، نه چهل."],
          ["Nobody opens the dashboard twice.", "هیچ‌کس داشبورد را دو بار باز نمی‌کند."],
        ],
        col: ["a sales dashboard", "open the dashboard"],
        note: "dashboard قابل‌شمارش است؛ در خودرو به معنی «داشبورد» هم به کار می‌رود.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "cohort", gloss: "گروه هم‌دوره",
        options: ["گروه هم‌دوره", "خط پایه", "ناهنجاری", "بخش‌بندی"],
        note: "cohort گروهی از افراد است با یک نقطهٔ شروع مشترک.",
      },
      {
        k: "mcq", word: "seasonality", gloss: "فصلی‌بودن",
        options: ["فصلی‌بودن", "تصادفی بودن", "رشد پایدار", "خطای ثبت"],
        note: "seasonality الگویی است که هر سال تکرار می‌شود.",
      },
      {
        k: "mcq", word: "anomaly", gloss: "ناهنجاری",
        options: ["ناهنجاری", "میانگین", "پیش‌بینی", "شاخص"],
        note: "anomaly نتیجه‌ای است که در الگو نمی‌گنجد.",
      },
      {
        k: "blank", sentence: "This is the steepest fall ___ 2019.",
        accepted: ["since", "in"], skill: "VOCABULARY",
        note: "since 2019 یعنی «از ۲۰۱۹ تا حال»؛ in 2019 خودِ سال را نشان می‌دهد.",
      },
      {
        k: "blank", sentence: "The second quarter was ___ than the first.",
        accepted: ["stronger", "weaker"], skill: "VOCABULARY",
        note: "پس از than صفت تفضیلی می‌آید: stronger/weaker.",
      },
      {
        k: "recall", prompt: "خط پایه", accepted: ["baseline"],
        note: "خط پایه baseline است و با against می‌آید.",
      },
      {
        k: "tr", prompt: "این تندترین افت از ۲۰۱۹ است.",
        accepted: [
          "this is the steepest fall since 2019",
          "this is the sharpest fall since 2019",
        ],
        note: "صفت عالی با the و since برای شروع بازه.",
      },
      {
        k: "listen", text: "This is the steepest fall since 2019.",
        note: "به the steepest fall گوش کنید: صفت عالی با the.",
      },
      {
        k: "prod", prompt: "بگو سه‌ماههٔ دوم از اولی قوی‌تر بود.",
        mustUse: ["than", "first"], minWords: 5,
        accepted: ["The second quarter was stronger than the first."],
        hint: "با The second quarter was stronger… شروع کن.",
        note: "ترتیب لنگرها: than پیش از first.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ بررسی داشبورد فروش", partner: "تحلیلگر داده",
        prompt: "بگو این تندترین افت در سال‌های اخیر است.",
        question: "How bad is the drop?",
        questionFa: "افت چقدر بد است؟",
        accepted: [
          "This is the steepest fall in years.",
          "It is the steepest fall in years.",
        ],
        note: "the steepest: صفت عالی با the.",
      },
      {
        scene: "جلسهٔ بررسی داشبورد فروش", partner: "تحلیلگر داده",
        prompt: "بگو سه‌ماههٔ دوم از اولی قوی‌تر بود.",
        question: "Which quarter was better?",
        questionFa: "کدام سه‌ماهه بهتر بود؟",
        accepted: [
          "The second quarter was stronger than the first.",
          "The second quarter was stronger.",
        ],
        note: "stronger than: صفت تفضیلی با than.",
      },
      {
        scene: "جلسهٔ بررسی داشبورد فروش", partner: "تحلیلگر داده",
        prompt: "بگو بخش‌بندی مشتریان تصویر را روشن‌تر می‌کند.",
        question: "Why segment the data?",
        questionFa: "چرا داده را بخش‌بندی کنیم؟",
        accepted: [
          "Customer segmentation makes the picture clearer.",
          "Because segmentation makes the picture clearer.",
        ],
        note: "segmentation غیرقابل‌شمارش است: makes.",
      },
      {
        scene: "جلسهٔ بررسی داشبورد فروش", partner: "تحلیلگر داده",
        prompt: "بگو فصلی‌بودن بخشی از افت را توضیح می‌دهد.",
        question: "Is the drop real?",
        questionFa: "این افت واقعی است؟",
        accepted: [
          "Seasonality explains part of the fall.",
          "Part of the fall is seasonality.",
        ],
        note: "explain part of: بخشی را توضیح دادن.",
      },
      {
        scene: "جلسهٔ بررسی داشبورد فروش", partner: "تحلیلگر داده",
        prompt: "بگو این ناهنجاری یک خطای ثبت است.",
        question: "What is that spike?",
        questionFa: "آن جهش چیست؟",
        accepted: [
          "The anomaly is a recording error.",
          "It is a recording error, an anomaly.",
        ],
        note: "an anomaly مفرد است: is.",
      },
      {
        scene: "جلسهٔ بررسی داشبورد فروش", partner: "تحلیلگر داده",
        prompt: "بگو خط پایه را با ژانویه می‌گیریم.",
        question: "Which month is the baseline?",
        questionFa: "کدام ماه خط پایه است؟",
        accepted: [
          "We take January as the baseline.",
          "The baseline is January.",
        ],
        note: "take something as the baseline: چیزی را خط پایه گرفتن.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.org-culture.lesson-01",
      order: 159,
      title: "Organizational Culture",
      titleFa: "فرهنگ سازمانی",
      topic: "Work",
      tip: [
        "## قاعده",
        "برای عادت گذشته که دیگر نیست: used to + مصدر — People used to decide in meetings.",
        "would + مصدر هم عادت گذشته را نشان می‌دهد، اما فقط برای کنش‌های تکراری: Every Friday the team would share one lesson.",
        "برای وضع فعلی از حال ساده استفاده کن: Now they decide in chat.",
        "## جدول",
        "People used to decide in meetings. — قبلاً همه در جلسه تصمیم می‌گرفتند.",
        "Every Friday the team would share one lesson. — هر جمعه تیم یک درس را به اشتراک می‌گذاشت.",
        "Now they decide in chat. — حالا در چت تصمیم می‌گیرند.",
        "## مثال‌ها",
        "The hierarchy slows decisions down. — سلسله‌مراتب تصمیم‌ها را کند می‌کند.",
        "Silos keep information inside a team. — جزیره‌های سازمانی اطلاعات را داخل تیم نگه می‌دارند.",
        "Onboarding takes a month. — آشناسازی یک ماه طول می‌کشد.",
        "## خطاهای رایج",
        "در جملهٔ منفی و پرسشی used to به did برمی‌گردد: did not use to ✓ / did not used to ✗.",
        "would برای وضعیت پایدار به کار نمی‌رود: used to know ✓ / would know ✗ (در این معنا).",
        "attrition غیرقابل‌شمارش است؛ عبارت رایج attrition rate.",
      ].join("\n"),
    },
    grammarNode: "grammar.used-to-and-would",
    vocab: {
      id: "vocab.organizational-culture",
      title: "Organizational Culture",
      titleFa: "فرهنگ سازمانی",
      prerequisites: ["vocab.organizational-change"],
    },
    words: [
      {
        slug: "hierarchy", w: "hierarchy", fa: "سلسله‌مراتب", ipa: "/ˈhaɪərɑːki/", pos: "noun",
        def: "the levels of authority in an organisation",
        ex: [
          ["The hierarchy slows decisions down.", "سلسله‌مراتب تصمیم‌ها را کند می‌کند."],
          ["A flat hierarchy sounds better than it works.", "سلسله‌مراتب تخت بهتر از آنچه کار می‌کند به گوش می‌رسد."],
        ],
        col: ["a flat hierarchy", "climb the hierarchy"],
        note: "hierarchy قابل‌شمارش است؛ صفت آن hierarchical.",
      },
      {
        slug: "silo", w: "silo", fa: "جزیرهٔ سازمانی", ipa: "/ˈsaɪləʊ/", pos: "noun",
        def: "a team that keeps information to itself",
        ex: [
          ["Silos keep information inside a team.", "جزیره‌های سازمانی اطلاعات را داخل تیم نگه می‌دارند."],
          ["The silo between sales and support cost a client.", "جزیرهٔ میان فروش و پشتیبانی یک مشتری را از دست داد."],
        ],
        col: ["break down silos", "a silo mentality"],
        note: "silo قابل‌شمارش است؛ اصطلاح رایج break down silos.",
      },
      {
        slug: "ritual", w: "ritual", fa: "آیین سازمانی", ipa: "/ˈrɪtʃuəl/", pos: "noun",
        def: "a repeated habit that holds a group together",
        ex: [
          ["Our Friday ritual still works.", "آیین جمعهٔ ما هنوز کار می‌کند."],
          ["Every ritual needs someone to start it.", "هر آیینی به کسی نیاز دارد که شروعش کند."],
        ],
        col: ["a weekly ritual", "a company ritual"],
        note: "ritual قابل‌شمارش است؛ صفت آن ritualistic.",
      },
      {
        slug: "onboarding", w: "onboarding", fa: "آشناسازی", ipa: "/ˈɒnbɔːdɪŋ/", pos: "noun",
        def: "the process of bringing a new person into the team",
        ex: [
          ["Onboarding takes a month.", "آشناسازی یک ماه طول می‌کشد."],
          ["Good onboarding cuts early attrition.", "آشناسازی خوب ریزش زودهنگام را کم می‌کند."],
        ],
        col: ["an onboarding programme", "onboarding week"],
        note: "onboarding غیرقابل‌شمارش است؛ فعل آن onboard somebody.",
      },
      {
        slug: "attrition", w: "attrition", fa: "ریزش نیرو", ipa: "/əˈtrɪʃn/", pos: "noun",
        def: "people leaving a company faster than they are replaced",
        ex: [
          ["Attrition has doubled since last year.", "ریزش نیرو از سال گذشته دو برابر شده است."],
          ["The attrition rate is highest in support.", "نرخ ریزش در پشتیبانی بالاترین است."],
        ],
        col: ["a high attrition rate", "reduce attrition"],
        note: "attrition غیرقابل‌شمارش است؛ در انگلیسی رسمی «فرسایش تدریجی» هم معنا می‌دهد.",
      },
      {
        slug: "values", w: "values", fa: "ارزش‌ها", ipa: "/ˈvæljuːz/", pos: "noun",
        def: "the principles a company says it lives by",
        ex: [
          ["Our values are printed on every wall.", "ارزش‌های ما روی هر دیوار چاپ شده است."],
          ["Values matter only when they cost something.", "ارزش‌ها فقط وقتی مهم‌اند که هزینه داشته باشند."],
        ],
        col: ["core values", "live by values"],
        note: "values در این معنا جمع است: the values are، نه is.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "hierarchy", gloss: "سلسله‌مراتب",
        options: ["سلسله‌مراتب", "فرهنگ", "آیین", "بازخورد"],
        note: "hierarchy سطوح اختیار در یک سازمان است.",
      },
      {
        k: "mcq", word: "silo", gloss: "جزیرهٔ سازمانی",
        options: ["جزیرهٔ سازمانی", "جلسهٔ هفتگی", "ارزش مشترک", "نرخ ریزش"],
        note: "silo تیمی است که اطلاعات را برای خودش نگه می‌دارد.",
      },
      {
        k: "mcq", word: "attrition", gloss: "ریزش نیرو",
        options: ["ریزش نیرو", "استخدام", "آموزش", "ارتقا"],
        note: "attrition خارج شدن افراد از شرکت سریع‌تر از جایگزینی‌شان است.",
      },
      {
        k: "blank", sentence: "People ___ to decide in meetings; now they decide in chat.",
        accepted: ["used", "would"], skill: "VOCABULARY",
        note: "used to و would هر دو عادت گذشته را می‌رسانند.",
      },
      {
        k: "blank", sentence: "Every Friday the team ___ share one lesson learned.",
        accepted: ["would", "used to"], skill: "VOCABULARY",
        note: "would share عادت تکراری گذشته است؛ used to هم درست است.",
      },
      {
        k: "recall", prompt: "آشناسازی", accepted: ["onboarding"],
        note: "آشناسازی onboarding است و فعل آن onboard.",
      },
      {
        k: "tr", prompt: "قبلاً همه در جلسه تصمیم می‌گرفتند.",
        accepted: [
          "people used to decide in meetings",
          "everyone used to decide in meetings",
        ],
        note: "used to + مصدر برای عادت گذشته که دیگر نیست.",
      },
      {
        k: "listen", text: "People used to decide in meetings; now they decide in chat.",
        note: "به used to decide گوش کنید: عادت گذشته.",
      },
      {
        k: "prod", prompt: "بگو قبلاً همه در جلسه تصمیم می‌گرفتند.",
        mustUse: ["used", "to"], minWords: 5,
        accepted: ["People used to decide in meetings."],
        hint: "با People used to… شروع کن.",
        note: "ترتیب لنگرها: used پیش از to.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ بررسی فرهنگ سازمانی", partner: "مدیر فرهنگ و توسعه",
        prompt: "بگو قبلاً همه در جلسه تصمیم می‌گرفتند.",
        question: "How did decisions work before?",
        questionFa: "قبلاً تصمیم‌ها چطور گرفته می‌شد؟",
        accepted: [
          "People used to decide in meetings.",
          "Everyone used to decide in meetings.",
        ],
        note: "used to + مصدر: عادت گذشته که تمام شده است.",
      },
      {
        scene: "جلسهٔ بررسی فرهنگ سازمانی", partner: "مدیر فرهنگ و توسعه",
        prompt: "بگو سلسله‌مراتب تصمیم‌ها را کند می‌کند.",
        question: "What slows us down?",
        questionFa: "چه چیزی ما را کند می‌کند؟",
        accepted: [
          "The hierarchy slows decisions down.",
          "Hierarchy slows decisions down.",
        ],
        note: "slow something down: کند کردن یک فرایند.",
      },
      {
        scene: "جلسهٔ بررسی فرهنگ سازمانی", partner: "مدیر فرهنگ و توسعه",
        prompt: "بگو جزیره‌های سازمانی اطلاعات را داخل تیم نگه می‌دارند.",
        question: "Why do teams not share?",
        questionFa: "چرا تیم‌ها اطلاعات را به اشتراک نمی‌گذارند؟",
        accepted: [
          "Silos keep information inside a team.",
          "Because silos keep information inside teams.",
        ],
        note: "silos جمع است: keep.",
      },
      {
        scene: "جلسهٔ بررسی فرهنگ سازمانی", partner: "مدیر فرهنگ و توسعه",
        prompt: "بگو آیین جمعهٔ ما هنوز کار می‌کند.",
        question: "Does the Friday ritual still work?",
        questionFa: "آیین جمعه هنوز کار می‌کند؟",
        accepted: [
          "Our Friday ritual still works.",
          "Yes, the Friday ritual still works.",
        ],
        note: "the ritual مفرد است: works.",
      },
      {
        scene: "جلسهٔ بررسی فرهنگ سازمانی", partner: "مدیر فرهنگ و توسعه",
        prompt: "بگو آشناسازی یک ماه طول می‌کشد.",
        question: "How long is onboarding?",
        questionFa: "آشناسازی چقدر طول می‌کشد؟",
        accepted: [
          "Onboarding takes a month.",
          "It takes a month.",
        ],
        note: "onboarding غیرقابل‌شمارش است: takes.",
      },
      {
        scene: "جلسهٔ بررسی فرهنگ سازمانی", partner: "مدیر فرهنگ و توسعه",
        prompt: "بگو ریزش نیرو از سال گذشته دو برابر شده است.",
        question: "Why are people leaving?",
        questionFa: "چرا افراد می‌روند؟",
        accepted: [
          "Attrition has doubled since last year.",
          "Because attrition has doubled since last year.",
        ],
        note: "has doubled: حال کامل با since.",
      },
    ],
  },
];
