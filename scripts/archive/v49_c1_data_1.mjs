// v49 content, part 1 — C1 topics 5 and 6: economic analysis and technology
// ethics. Read by `v49_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.economy.lesson-01",
      order: 148,
      title: "Economic Analysis",
      titleFa: "تحلیل اقتصادی",
      topic: "Money",
      tip: [
        "## قاعده",
        "شرطی مختلط در تحلیل اقتصادی زیاد است: شرط در گذشته، نتیجه در حال — If the bank had acted sooner, inflation would be lower now.",
        "نتیجهٔ گذشته با حال در شرط: If the economy were stronger, the deficit would have shrunk.",
        "could have و might have برای نتیجهٔ گذشته‌ای که رخ نداد: If the stimulus had come earlier, output might have held.",
        "## جدول",
        "If the bank had acted sooner, inflation would be lower now. — اگر بانک زودتر اقدام کرده بود، تورم الان کمتر بود.",
        "If the economy were stronger, the deficit would have shrunk. — اگر اقتصاد قوی‌تر بود، کسری کم می‌شد.",
        "If the stimulus had come earlier, output might have held. — اگر محرک زودتر آمده بود، تولید ممکن بود حفظ شود.",
        "## مثال‌ها",
        "The deficit widened for a third year. — کسری برای سومین سال بزرگ‌تر شد.",
        "Volatility makes planning harder for importers. — نوسان برنامه‌ریزی را برای واردکنندگان سخت‌تر می‌کند.",
        "Monetary policy is set by the central bank. — سیاست پولی را بانک مرکزی تعیین می‌کند.",
        "## خطاهای رایج",
        "در نیمهٔ شرط should have نیست، had + قسمت سوم است: If the bank had acted ✓ / If the bank should have acted ✗.",
        "deficit قابل‌شمارش است: a deficit, two deficits.",
        "monetary پیش از اسم می‌آید: monetary policy (نه policy monetary).",
      ].join("\n"),
    },
    grammarNode: "grammar.mixed-conditionals",
    vocab: {
      id: "vocab.economic-analysis",
      title: "Economic Analysis",
      titleFa: "تحلیل اقتصادی",
      prerequisites: ["vocab.global-economy"],
    },
    words: [
      {
        slug: "deficit", w: "deficit", fa: "کسری", ipa: "/ˈdefɪsɪt/", pos: "noun",
        def: "the amount by which spending is larger than income",
        ex: [
          ["The deficit widened for a third year.", "کسری برای سومین سال بزرگ‌تر شد."],
          ["A smaller deficit means less borrowing.", "کسری کمتر یعنی استقراض کمتر."],
        ],
        col: ["a budget deficit", "close the deficit"],
        note: "deficit قابل‌شمارش است و متضاد آن surplus است.",
      },
      {
        slug: "stimulus", w: "stimulus", fa: "محرک اقتصادی", ipa: "/ˈstɪmjələs/", pos: "noun",
        def: "government money spent to make the economy grow",
        ex: [
          ["The stimulus kept two thousand firms open.", "محرک اقتصادی دو هزار شرکت را سرپا نگه داشت."],
          ["A stimulus of this size is a one-off.", "محرکی با این اندازه یک‌بارمصرف است."],
        ],
        col: ["a fiscal stimulus", "a stimulus package"],
        note: "جمع آن stimuli است؛ stimulus package یعنی بستهٔ حمایتی.",
      },
      {
        slug: "volatility", w: "volatility", fa: "نوسان", ipa: "/ˌvɒləˈtɪləti/", pos: "noun",
        def: "how quickly prices move up and down",
        ex: [
          ["Volatility in the currency makes planning impossible.", "نوسان ارز برنامه‌ریزی را ناممکن می‌کند."],
          ["Oil volatility worries importers most.", "نوسان نفت بیشتر از همه واردکنندگان را نگران می‌کند."],
        ],
        col: ["market volatility", "price volatility"],
        note: "volatility غیرقابل‌شمارش است؛ صفت آن volatile.",
      },
      {
        slug: "surplus", w: "surplus", fa: "مازاد", ipa: "/ˈsɜːpləs/", pos: "noun",
        def: "the amount by which income is larger than spending",
        ex: [
          ["The budget moved from deficit to surplus.", "بودجه از کسری به مازاد رسید."],
          ["A trade surplus is not the same as growth.", "مازاد تجاری همان رشد نیست."],
        ],
        col: ["a trade surplus", "a budget surplus"],
        note: "surplus قابل‌شمارش است و متضاد deficit.",
      },
      {
        slug: "monetary", w: "monetary", fa: "پولی", ipa: "/ˈmʌnɪtri/", pos: "adjective",
        def: "connected with the money supply and interest rates",
        ex: [
          ["Monetary policy is set by the central bank.", "سیاست پولی را بانک مرکزی تعیین می‌کند."],
          ["Monetary tightening slowed demand.", "انقباض پولی تقاضا را کم کرد."],
        ],
        col: ["monetary policy", "monetary tightening"],
        note: "monetary صفت است و پیش از اسم می‌آید؛ متضاد آن fiscal (مالی دولتی) در سیاست اقتصادی است.",
      },
      {
        slug: "austerity", w: "austerity", fa: "ریاضت اقتصادی", ipa: "/ɒˈsterəti/", pos: "noun",
        def: "strict spending cuts to reduce a deficit",
        ex: [
          ["Austerity cut spending faster than the economy could absorb.", "ریاضت اقتصادی هزینه‌ها را سریع‌تر از توان اقتصاد کم کرد."],
          ["Ten years of austerity reshaped public services.", "ده سال ریاضت اقتصادی خدمات عمومی را بازسازی کرد."],
        ],
        col: ["an austerity programme", "years of austerity"],
        note: "austerity غیرقابل‌شمارش است؛ صفت آن austere.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "deficit", gloss: "کسری",
        options: ["کسری", "مازاد", "تورم", "بهره"],
        note: "deficit وقتی است که هزینه از درآمد بیشتر باشد.",
      },
      {
        k: "mcq", word: "stimulus", gloss: "محرک اقتصادی",
        options: ["محرک اقتصادی", "مالیات", "قرارداد", "بودجهٔ نظامی"],
        note: "stimulus پولی است که دولت برای رشد اقتصاد خرج می‌کند.",
      },
      {
        k: "mcq", word: "volatility", gloss: "نوسان",
        options: ["نوسان", "ثبات", "رشد", "کسری"],
        note: "volatility سرعت بالا و پایین رفتن قیمت‌ها است.",
      },
      {
        k: "blank", sentence: "If the bank had acted sooner, inflation ___ be lower now.",
        accepted: ["would", "could"], skill: "VOCABULARY",
        note: "شرط گذشته و نتیجهٔ حال: would be / could be، نه will be.",
      },
      {
        k: "blank", sentence: "If the stimulus had come earlier, output ___ have held.",
        accepted: ["might", "would"], skill: "VOCABULARY",
        note: "نتیجهٔ گذشته‌ای که رخ نداد: might have / would have + قسمت سوم.",
      },
      {
        k: "recall", prompt: "ریاضت اقتصادی", accepted: ["austerity"],
        note: "ریاضت اقتصادی austerity است و صفت آن austere.",
      },
      {
        k: "tr", prompt: "اگر بانک زودتر اقدام کرده بود، تورم الان کمتر بود.",
        accepted: [
          "if the bank had acted sooner inflation would be lower now",
          "if the bank had acted sooner, inflation would be lower now",
        ],
        note: "had acted برای گذشته و would be برای حال.",
      },
      {
        k: "listen", text: "If the bank had acted sooner, inflation would be lower now.",
        note: "به had acted و would be گوش کنید: دو زمان در یک جمله.",
      },
      {
        k: "prod", prompt: "بگو اگر بانک زودتر اقدام کرده بود، تورم الان کمتر بود.",
        mustUse: ["if", "would"], minWords: 9,
        accepted: ["If the bank had acted sooner, inflation would be lower now."],
        hint: "با If the bank had… شروع کن و با would be… ادامه بده.",
        note: "ترتیب لنگرها: if در شرط و would در نتیجه.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ تحلیل اقتصادی بانک مرکزی", partner: "اقتصاددان ارشد",
        prompt: "بگو اگر بانک زودتر اقدام کرده بود، تورم الان کمتر بود.",
        question: "Why is inflation still high?",
        questionFa: "چرا تورم هنوز بالاست؟",
        accepted: [
          "If the bank had acted sooner, inflation would be lower now.",
          "Because if the bank had acted sooner, inflation would be lower now.",
        ],
        note: "شرط گذشته (had acted) با نتیجهٔ حال (would be).",
      },
      {
        scene: "جلسهٔ تحلیل اقتصادی بانک مرکزی", partner: "اقتصاددان ارشد",
        prompt: "بگو کسری برای سومین سال بزرگ‌تر شد.",
        question: "What happened to the deficit?",
        questionFa: "کسری چه شد؟",
        accepted: [
          "The deficit widened for a third year.",
          "It widened for a third year running.",
        ],
        note: "widen یعنی بزرگ‌تر شدن؛ برای کسری و شکاف به کار می‌رود.",
      },
      {
        scene: "جلسهٔ تحلیل اقتصادی بانک مرکزی", partner: "اقتصاددان ارشد",
        prompt: "بگو محرک اقتصادی دو هزار شرکت را سرپا نگه داشت.",
        question: "Did the stimulus work?",
        questionFa: "محرک اقتصادی کار کرد؟",
        accepted: [
          "The stimulus kept two thousand firms open.",
          "Yes, it kept two thousand firms open.",
        ],
        note: "keep something open: سرپا نگه داشتن.",
      },
      {
        scene: "جلسهٔ تحلیل اقتصادی بانک مرکزی", partner: "اقتصاددان ارشد",
        prompt: "بگو نوسان ارز برنامه‌ریزی را ناممکن می‌کند.",
        question: "Why can firms not plan?",
        questionFa: "چرا شرکت‌ها نمی‌توانند برنامه بریزند؟",
        accepted: [
          "Volatility in the currency makes planning impossible.",
          "Because of the volatility in the currency.",
        ],
        note: "volatility in something: نوسان در یک بازار.",
      },
      {
        scene: "جلسهٔ تحلیل اقتصادی بانک مرکزی", partner: "اقتصاددان ارشد",
        prompt: "بگو سیاست پولی را بانک مرکزی تعیین می‌کند.",
        question: "Who sets monetary policy?",
        questionFa: "سیاست پولی را چه کسی تعیین می‌کند؟",
        accepted: [
          "Monetary policy is set by the central bank.",
          "The central bank sets monetary policy.",
        ],
        note: "monetary پیش از اسم می‌آید: monetary policy.",
      },
      {
        scene: "جلسهٔ تحلیل اقتصادی بانک مرکزی", partner: "اقتصاددان ارشد",
        prompt: "بگو ده سال ریاضت اقتصادی خدمات عمومی را بازسازی کرد.",
        question: "What did austerity do?",
        questionFa: "ریاضت اقتصادی چه کرد؟",
        accepted: [
          "Ten years of austerity reshaped public services.",
          "Austerity reshaped public services for ten years.",
        ],
        note: "years of austerity: austerity غیرقابل‌شمارش است.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.tech-ethics.lesson-01",
      order: 149,
      title: "Technology Ethics",
      titleFa: "اخلاق در فناوری",
      topic: "Technology",
      tip: [
        "## قاعده",
        "مجهول با وجهی: modal + be + قسمت سوم — Data should be deleted after ninety days.",
        "منفی آن: The model must not be used without a human review.",
        "برای گذشته: modal + have been + قسمت سوم — The logs should have been reviewed.",
        "## جدول",
        "Data should be deleted after ninety days. — داده‌ها باید بعد از نود روز حذف شوند.",
        "The model must not be used without a human review. — مدل نباید بدون بازبینی انسانی استفاده شود.",
        "The logs should have been reviewed. — گزارش‌ها باید بازبینی می‌شدند.",
        "## مثال‌ها",
        "Opacity destroys trust faster than any error. — ناشفافیت سریع‌تر از هر خطایی اعتماد را از بین می‌برد.",
        "An autonomous system needs a safeguard. — یک سیستم خودگردان به محافظ نیاز دارد.",
        "The provenance of the data must be recorded. — منشأ داده‌ها باید ثبت شود.",
        "## خطاهای رایج",
        "پس از وجهی be می‌آید: must be recorded ✓ / must recorded ✗.",
        "opacity غیرقابل‌شمارش است؛ صفت آن opaque است.",
        "safeguard قابل‌شمارش است: a safeguard, several safeguards.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-with-modals",
    vocab: {
      id: "vocab.technology-ethics",
      title: "Technology Ethics",
      titleFa: "اخلاق در فناوری",
      prerequisites: ["vocab.professional-ethics"],
    },
    words: [
      {
        slug: "surveillance", w: "surveillance", fa: "پایش", ipa: "/sɜːˈveɪləns/", pos: "noun",
        def: "watching people closely, often without their knowledge",
        ex: [
          ["Facial surveillance in public spaces is not allowed.", "پایش چهره در فضاهای عمومی مجاز نیست."],
          ["Surveillance grew faster than the rules did.", "پایش سریع‌تر از قواعد رشد کرد."],
        ],
        col: ["mass surveillance", "surveillance cameras"],
        note: "surveillance غیرقابل‌شمارش است و با under می‌آید: under surveillance.",
      },
      {
        slug: "autonomous", w: "autonomous", fa: "خودگردان", ipa: "/ɔːˈtɒnəməs/", pos: "adjective",
        def: "able to act without a person controlling it",
        ex: [
          ["An autonomous system needs a safeguard.", "یک سیستم خودگردان به محافظ نیاز دارد."],
          ["Autonomous vehicles still fail in bad weather.", "خودروهای خودگردان هنوز در هوای بد شکست می‌خورند."],
        ],
        col: ["an autonomous system", "autonomous decision-making"],
        note: "autonomous صفت است؛ اسم آن autonomy (خودمختاری).",
      },
      {
        slug: "opacity", w: "opacity", fa: "ناشفافیت", ipa: "/əʊˈpæsəti/", pos: "noun",
        def: "the quality of being impossible to see into or explain",
        ex: [
          ["Opacity destroys trust faster than any error.", "ناشفافیت سریع‌تر از هر خطایی اعتماد را از بین می‌برد."],
          ["The opacity of the model worried the auditors.", "ناشفافیت مدل حسابرسان را نگران کرد."],
        ],
        col: ["algorithmic opacity", "the opacity of the model"],
        note: "opacity غیرقابل‌شمارش است؛ صفت آن opaque و متضاد transparency.",
      },
      {
        slug: "provenance", w: "provenance", fa: "منشأ", ipa: "/ˈprɒvənəns/", pos: "noun",
        def: "the place or source something came from",
        ex: [
          ["The provenance of the data must be recorded.", "منشأ داده‌ها باید ثبت شود."],
          ["Nobody could prove the provenance of the images.", "هیچ‌کس نمی‌توانست منشأ تصاویر را ثابت کند."],
        ],
        col: ["data provenance", "provenance records"],
        note: "provenance غیرقابل‌شمارش است و بیشتر دربارهٔ داده و اثر هنری به کار می‌رود.",
      },
      {
        slug: "safeguard", w: "safeguard", fa: "محافظ", ipa: "/ˈseɪfɡɑːd/", pos: "noun",
        def: "a rule or feature that prevents harm",
        ex: [
          ["An autonomous system needs a safeguard.", "یک سیستم خودگردان به محافظ نیاز دارد."],
          ["Safeguards were added after the first incident.", "محافظ‌ها بعد از نخستین حادثه اضافه شدند."],
        ],
        col: ["a legal safeguard", "add safeguards"],
        note: "safeguard هم اسم است و هم فعل: to safeguard the data.",
      },
      {
        slug: "oversight", w: "oversight", fa: "نظارت", ipa: "/ˈəʊvəsaɪt/", pos: "noun",
        def: "the work of checking that something is done properly",
        ex: [
          ["The model is only used with human oversight.", "مدل فقط با نظارت انسانی استفاده می‌شود."],
          ["Independent oversight made the difference.", "نظارت مستقل تفاوت را ساخت."],
        ],
        col: ["human oversight", "independent oversight"],
        note: "oversight در این معنا غیرقابل‌شمارش است؛ معنای دوم آن «سهو» است و آن معنا قابل‌شمارش.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "surveillance", gloss: "پایش",
        options: ["پایش", "اجازه", "مالکیت", "پشتیبان"],
        note: "surveillance یعنی زیر نظر گرفتن مردم، اغلب بی‌آنکه بدانند.",
      },
      {
        k: "mcq", word: "opacity", gloss: "ناشفافیت",
        options: ["ناشفافیت", "دقت", "سرعت", "هزینه"],
        note: "opacity یعنی نتوان به درون چیزی دید یا توضیحش داد.",
      },
      {
        k: "mcq", word: "safeguard", gloss: "محافظ",
        options: ["محافظ", "قرارداد", "گزارش", "جریمه"],
        note: "safeguard قاعده یا ویژگی‌ای است که جلوی آسیب را می‌گیرد.",
      },
      {
        k: "blank", sentence: "Data ___ be deleted after ninety days.",
        accepted: ["should", "must"], skill: "VOCABULARY",
        note: "مجهول با وجهی: modal + be + قسمت سوم.",
      },
      {
        k: "blank", sentence: "The model ___ not be used without a human review.",
        accepted: ["must", "should"], skill: "VOCABULARY",
        note: "must not ممنوعیت قوی است و should not توصیهٔ قوی؛ هر دو با be می‌آیند.",
      },
      {
        k: "recall", prompt: "خودگردان", accepted: ["autonomous"],
        note: "خودگردان autonomous است و اسم آن autonomy.",
      },
      {
        k: "tr", prompt: "داده‌ها باید بعد از نود روز حذف شوند.",
        accepted: [
          "data should be deleted after ninety days",
          "the data should be deleted after ninety days",
        ],
        note: "should be + قسمت سوم: مجهول وجهی.",
      },
      {
        k: "listen", text: "Data should be deleted after ninety days.",
        note: "به should be deleted گوش کنید: وجهی + be + قسمت سوم.",
      },
      {
        k: "prod", prompt: "بگو داده‌ها باید بعد از نود روز حذف شوند.",
        mustUse: ["should", "be"], minWords: 7,
        accepted: ["Data should be deleted after ninety days."],
        hint: "با Data should be… شروع کن.",
        note: "ترتیب لنگرها: should پیش از be.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ کمیتهٔ اخلاق فناوری", partner: "مدیر داده",
        prompt: "بگو داده‌ها باید بعد از نود روز حذف شوند.",
        question: "How long do we keep user data?",
        questionFa: "دادهٔ کاربر را چقدر نگه می‌داریم؟",
        accepted: [
          "Data should be deleted after ninety days.",
          "It should be deleted after ninety days.",
        ],
        note: "should be + قسمت سوم: مجهول با وجهی.",
      },
      {
        scene: "جلسهٔ کمیتهٔ اخلاق فناوری", partner: "مدیر داده",
        prompt: "بگو پایش چهره در فضاهای عمومی مجاز نیست.",
        question: "What about facial recognition?",
        questionFa: "دربارهٔ تشخیص چهره چه؟",
        accepted: [
          "Facial surveillance in public spaces is not allowed.",
          "Surveillance in public spaces is not allowed.",
        ],
        note: "surveillance غیرقابل‌شمارش است: is not allowed.",
      },
      {
        scene: "جلسهٔ کمیتهٔ اخلاق فناوری", partner: "مدیر داده",
        prompt: "بگو مدل فقط با نظارت انسانی استفاده می‌شود.",
        question: "Can the model run alone?",
        questionFa: "مدل می‌تواند تنها کار کند؟",
        accepted: [
          "The model is only used with human oversight.",
          "No, it is only used with human oversight.",
        ],
        note: "with oversight: با نظارت؛ without oversight یعنی بی‌نظارت.",
      },
      {
        scene: "جلسهٔ کمیتهٔ اخلاق فناوری", partner: "مدیر داده",
        prompt: "بگو منشأ داده‌ها باید ثبت شود.",
        question: "How do we check where the data came from?",
        questionFa: "چطور بررسی می‌کنیم داده از کجا آمده؟",
        accepted: [
          "The provenance of the data must be recorded.",
          "We record the provenance of the data.",
        ],
        note: "record با مفعول مستقیم: record the provenance.",
      },
      {
        scene: "جلسهٔ کمیتهٔ اخلاق فناوری", partner: "مدیر داده",
        prompt: "بگو ناشفافیت سریع‌تر از هر خطایی اعتماد را از بین می‌برد.",
        question: "Why does opacity matter?",
        questionFa: "چرا ناشفافیت مهم است؟",
        accepted: [
          "Opacity destroys trust faster than any error.",
          "Because opacity destroys trust faster than any error.",
        ],
        note: "faster than any error: مقایسه با هر خطایی.",
      },
      {
        scene: "جلسهٔ کمیتهٔ اخلاق فناوری", partner: "مدیر داده",
        prompt: "بگو هر سیستم خودگردان به محافظ نیاز دارد.",
        question: "What about autonomous systems?",
        questionFa: "دربارهٔ سیستم‌های خودگردان چه؟",
        accepted: [
          "Every autonomous system needs a safeguard.",
          "An autonomous system needs a safeguard.",
        ],
        note: "every با اسم مفرد و فعل مفرد می‌آید: every system needs.",
      },
    ],
  },
];
