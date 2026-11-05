// v50 content, part 2 — C1 topics 17 and 18: organizational change and risk
// management. Read by `v50_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.org-change.lesson-01",
      order: 154,
      title: "Organizational Change",
      titleFa: "مدیریت تغییر سازمانی",
      topic: "Business",
      tip: [
        "## قاعده",
        "برای تغییری که از گذشته تا حال ادامه دارد، مجهول حال کامل: has/have been + قسمت سوم — The plant has been closed since March.",
        "برای دو چیز: have been + قسمت سوم — Two teams have been merged into one.",
        "اگر فاعل مهم باشد با by می‌آید: The plan was approved by the board.",
        "## جدول",
        "The plant has been closed since March. — کارخانه از مارس بسته شده است.",
        "Two teams have been merged into one. — دو تیم در یکی ادغام شده‌اند.",
        "The plan has been approved by the board. — طرح را هیئت‌مدیره تأیید کرده است.",
        "## مثال‌ها",
        "The downsizing has lowered morale. — کوچک‌سازی روحیه را پایین آورده است.",
        "Organizational inertia is the biggest barrier. — رخوت سازمانی بزرگ‌ترین مانع است.",
        "Without buy-in, change does not last. — بدون همراه‌سازی، تغییر دوام نمی‌آورد.",
        "## خطاهای رایج",
        "پس از have/has حتماً been می‌آید: has been closed ✓ / has closed ✗ (آن یعنی «بسته کرده است»).",
        "buy-in با خط تیره نوشته می‌شود و غیرقابل‌شمارش است.",
        "resistance با to می‌آید: resistance to change.",
      ].join("\n"),
    },
    grammarNode: "grammar.present-perfect-passive",
    vocab: {
      id: "vocab.organizational-change",
      title: "Organizational Change",
      titleFa: "مدیریت تغییر سازمانی",
      prerequisites: ["vocab.leadership"],
    },
    words: [
      {
        slug: "restructuring", w: "restructuring", fa: "بازساختاردهی", ipa: "/ˌriːˈstrʌktʃərɪŋ/", pos: "noun",
        def: "changing how a company is organised",
        ex: [
          ["The restructuring starts with the finance team.", "بازساختاردهی از تیم مالی شروع می‌شود."],
          ["A second restructuring would cost more than it saves.", "بازساختاردهی دوم بیشتر از صرفه‌جویی‌اش هزینه دارد."],
        ],
        col: ["a major restructuring", "the restructuring plan"],
        note: "restructuring هم اسم است و هم قسمت سوم فعل restructure.",
      },
      {
        slug: "downsizing", w: "downsizing", fa: "کوچک‌سازی", ipa: "/ˈdaʊnsaɪzɪŋ/", pos: "noun",
        def: "cutting jobs to make a company smaller",
        ex: [
          ["The downsizing has lowered morale.", "کوچک‌سازی روحیه را پایین آورده است."],
          ["Downsizing saved money and lost experience.", "کوچک‌سازی پول ذخیره کرد و تجربه را از دست داد."],
        ],
        col: ["a downsizing round", "announce downsizing"],
        note: "downsizing غیرقابل‌شمارش است؛ فعل آن downsize.",
      },
      {
        slug: "merger", w: "merger", fa: "ادغام", ipa: "/ˈmɜːdʒə/", pos: "noun",
        def: "two companies joining to become one",
        ex: [
          ["The merger was announced in April.", "ادغام در آوریل اعلام شد."],
          ["A merger of equals is rare in practice.", "ادغام برابر در عمل کم پیش می‌آید."],
        ],
        col: ["a merger with a rival", "approve a merger"],
        note: "merger قابل‌شمارش است؛ فعل آن merge با into: merge into one.",
      },
      {
        slug: "inertia", w: "inertia", fa: "رخوت سازمانی", ipa: "/ɪˈnɜːʃə/", pos: "noun",
        def: "a reluctance to change that keeps things as they are",
        ex: [
          ["Organizational inertia is the biggest barrier.", "رخوت سازمانی بزرگ‌ترین مانع است."],
          ["Inertia kept the old process alive for years.", "رخوت فرایند قدیمی را سال‌ها زنده نگه داشت."],
        ],
        col: ["organizational inertia", "overcome inertia"],
        note: "inertia غیرقابل‌شمارش است؛ در فیزیک هم به معنی «لَختی» است.",
      },
      {
        slug: "resistance", w: "resistance", fa: "مقاومت", ipa: "/rɪˈzɪstəns/", pos: "noun",
        def: "people refusing to accept a change",
        ex: [
          ["Resistance is strong in two units.", "مقاومت در دو واحد شدید است."],
          ["Resistance to change faded after the first month.", "مقاومت در برابر تغییر بعد از ماه اول کم شد."],
        ],
        col: ["resistance to change", "meet resistance"],
        note: "resistance با to می‌آید؛ فعل آن resist و شخص مقاوم resist somebody.",
      },
      {
        slug: "buy-in", w: "buy-in", fa: "همراه‌سازی", ipa: "/ˈbaɪɪn/", pos: "noun",
        def: "agreement and support from the people affected",
        ex: [
          ["Without buy-in, change does not last.", "بدون همراه‌سازی، تغییر دوام نمی‌آورد."],
          ["Buy-in took longer than the design did.", "همراه‌سازی بیشتر از خود طراحی طول کشید."],
        ],
        col: ["win buy-in", "senior buy-in"],
        note: "buy-in غیرقابل‌شمارش است و با خط تیره نوشته می‌شود؛ فعل آن win buy-in.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "restructuring", gloss: "بازساختاردهی",
        options: ["بازساختاردهی", "استخدام", "بازنشستگی", "آموزش"],
        note: "restructuring تغییر چگونگی سازمان‌دهی یک شرکت است.",
      },
      {
        k: "mcq", word: "downsizing", gloss: "کوچک‌سازی",
        options: ["کوچک‌سازی", "ادغام", "گسترش", "برون‌سپاری"],
        note: "downsizing کم‌کردن نیرو برای کوچک‌تر کردن شرکت است.",
      },
      {
        k: "mcq", word: "inertia", gloss: "رخوت سازمانی",
        options: ["رخوت سازمانی", "خلاقیت", "شفافیت", "بهره‌وری"],
        note: "inertia بی‌میلی به تغییر است که وضع را همان‌طور نگه می‌دارد.",
      },
      {
        k: "blank", sentence: "The plant ___ been closed since March.",
        accepted: ["has", "had"], skill: "VOCABULARY",
        note: "مجهول حال کامل: has been closed؛ had برای گذشتهٔ کامل.",
      },
      {
        k: "blank", sentence: "Two teams ___ been merged into one.",
        accepted: ["have", "had"], skill: "VOCABULARY",
        note: "فاعل جمع است: have been merged.",
      },
      {
        k: "recall", prompt: "همراه‌سازی", accepted: ["buy-in"],
        note: "همراه‌سازی buy-in است و با win می‌آید.",
      },
      {
        k: "tr", prompt: "کارخانه از مارس بسته شده است.",
        accepted: [
          "the plant has been closed since march",
          "the plant has been shut since march",
        ],
        note: "has been + قسمت سوم: مجهول حال کامل.",
      },
      {
        k: "listen", text: "Two teams have been merged into one.",
        note: "به have been merged گوش کنید: مجهول حال کامل.",
      },
      {
        k: "prod", prompt: "بگو کارخانه از مارس بسته شده است.",
        mustUse: ["has", "been"], minWords: 6,
        accepted: ["The plant has been closed since March."],
        hint: "با The plant has been… شروع کن.",
        note: "ترتیب لنگرها: has پیش از been.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ اعلام تغییر سازمانی", partner: "مدیر منابع انسانی",
        prompt: "بگو کارخانه از مارس بسته شده است.",
        question: "What has happened to the plant?",
        questionFa: "کارخانه چه شده است؟",
        accepted: [
          "The plant has been closed since March.",
          "It has been shut since March.",
        ],
        note: "has been closed: مجهول حال کامل با since.",
      },
      {
        scene: "جلسهٔ اعلام تغییر سازمانی", partner: "مدیر منابع انسانی",
        prompt: "بگو دو تیم در یکی ادغام شده‌اند.",
        question: "What about the teams?",
        questionFa: "تیم‌ها چه شدند؟",
        accepted: [
          "Two teams have been merged into one.",
          "They have been merged into one.",
        ],
        note: "merge into one: ادغام شدن در یکی.",
      },
      {
        scene: "جلسهٔ اعلام تغییر سازمانی", partner: "مدیر منابع انسانی",
        prompt: "بگو کوچک‌سازی روحیه را پایین آورده است.",
        question: "How is morale?",
        questionFa: "روحیه چطور است؟",
        accepted: [
          "The downsizing has lowered morale.",
          "Morale has dropped since the downsizing.",
        ],
        note: "lower morale: روحیه را پایین آوردن.",
      },
      {
        scene: "جلسهٔ اعلام تغییر سازمانی", partner: "مدیر منابع انسانی",
        prompt: "بگو رخوت سازمانی بزرگ‌ترین مانع است.",
        question: "What blocks the change?",
        questionFa: "چه چیزی مانع تغییر است؟",
        accepted: [
          "Organizational inertia is the biggest barrier.",
          "Inertia is the biggest barrier.",
        ],
        note: "the biggest barrier: صفت عالی با the.",
      },
      {
        scene: "جلسهٔ اعلام تغییر سازمانی", partner: "مدیر منابع انسانی",
        prompt: "بگو مقاومت در دو واحد شدید است.",
        question: "Is everyone on board?",
        questionFa: "همه همراه‌اند؟",
        accepted: [
          "Resistance is strong in two units.",
          "No, resistance is strong in two units.",
        ],
        note: "resistance to change: مقاومت در برابر تغییر.",
      },
      {
        scene: "جلسهٔ اعلام تغییر سازمانی", partner: "مدیر منابع انسانی",
        prompt: "بگو بدون همراه‌سازی تغییر دوام نمی‌آورد.",
        question: "Why spend time on buy-in?",
        questionFa: "چرا برای همراه‌سازی وقت بگذاریم؟",
        accepted: [
          "Without buy-in, change does not last.",
          "Because without buy-in the change will not last.",
        ],
        note: "without + اسم: بدون همراه‌سازی.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.risk.lesson-01",
      order: 155,
      title: "Risk Management",
      titleFa: "مدیریت ریسک",
      topic: "Business",
      tip: [
        "## قاعده",
        "شرطی خیالی برای ریسک: If the supplier failed, we would lose two weeks.",
        "برای نتیجهٔ گذشته‌ای که رخ نداده: If we had hedged earlier, the loss would have been smaller.",
        "could و might در هر دو نیمه ممکن است: If we diversified, we might sleep better.",
        "## جدول",
        "If the supplier failed, we would lose two weeks. — اگر تأمین‌کننده از کار بیفتد، دو هفته از دست می‌دهیم.",
        "If we had hedged earlier, the loss would have been smaller. — اگر زودتر پوشش داده بودیم، ضرر کمتر بود.",
        "If we diversified, we might sleep better. — اگر تنوع می‌دادیم، ممکن بود راحت‌تر بخوابیم.",
        "## مثال‌ها",
        "Our exposure is concentrated in one supplier. — مواجههٔ ما در یک تأمین‌کننده متمرکز است.",
        "Mitigation is cheaper than recovery. — کاهش ریسک از جبران خسارت ارزان‌تر است.",
        "Diversification raises resilience. — تنوع‌بخشی تاب‌آوری را بالا می‌برد.",
        "## خطاهای رایج",
        "در نیمهٔ دوم شرطی از will استفاده نکن: we would lose ✓ / we will lose ✗.",
        "exposure با to می‌آید: exposure to a single market.",
        "mitigation غیرقابل‌شمارش است؛ فعل آن mitigate.",
      ].join("\n"),
    },
    grammarNode: "grammar.tentative-conditionals",
    vocab: {
      id: "vocab.risk-management",
      title: "Risk Management",
      titleFa: "مدیریت ریسک",
      prerequisites: ["vocab.strategy-evaluation"],
    },
    words: [
      {
        slug: "exposure", w: "exposure", fa: "مواجهه", ipa: "/ɪkˈspəʊʒə/", pos: "noun",
        def: "how much you could lose from one risk",
        ex: [
          ["Our exposure is concentrated in one supplier.", "مواجههٔ ما در یک تأمین‌کننده متمرکز است."],
          ["Every new market adds exposure.", "هر بازار تازه مواجهه را بیشتر می‌کند."],
        ],
        col: ["exposure to a market", "limit exposure"],
        note: "exposure با to می‌آید و در مالی به معنی «ریسک در معرض» است.",
      },
      {
        slug: "mitigation", w: "mitigation", fa: "کاهش ریسک", ipa: "/ˌmɪtɪˈɡeɪʃn/", pos: "noun",
        def: "action that makes a risk smaller",
        ex: [
          ["Mitigation is cheaper than recovery.", "کاهش ریسک از جبران خسارت ارزان‌تر است."],
          ["The mitigation plan was approved in a day.", "طرح کاهش ریسک در یک روز تصویب شد."],
        ],
        col: ["a mitigation plan", "risk mitigation"],
        note: "mitigation غیرقابل‌شمارش است؛ فعل آن mitigate با مفعول مستقیم.",
      },
      {
        slug: "insurance", w: "insurance", fa: "بیمه", ipa: "/ɪnˈʃʊərəns/", pos: "noun",
        def: "a contract that pays you if something goes wrong",
        ex: [
          ["Insurance covers part of the risk.", "بیمه بخشی از ریسک را پوشش می‌دهد."],
          ["The insurance did not cover lost orders.", "بیمه سفارش‌های از دست رفته را پوشش نمی‌داد."],
        ],
        col: ["take out insurance", "insurance against loss"],
        note: "insurance غیرقابل‌شمارش است؛ فعل آن insure و شخص insured است.",
      },
      {
        slug: "appetite", w: "appetite", fa: "اشتها", ipa: "/ˈæpɪtaɪt/", pos: "noun",
        def: "how much risk an organisation is willing to accept",
        ex: [
          ["Our risk appetite is low.", "حد پذیرش ریسک ما پایین است."],
          ["The board's appetite for risk has shrunk.", "اشتهای هیئت‌مدیره برای ریسک کم شده است."],
        ],
        col: ["risk appetite", "an appetite for risk"],
        note: "در مدیریت ریسک عبارت ثابت risk appetite یعنی «حد پذیرش ریسک».",
      },
      {
        slug: "diversification", w: "diversification", fa: "تنوع‌بخشی", ipa: "/daɪˌvɜːsɪfɪˈkeɪʃn/", pos: "noun",
        def: "spreading money or work across several areas",
        ex: [
          ["Diversification raises resilience.", "تنوع‌بخشی تاب‌آوری را بالا می‌برد."],
          ["Diversification diluted the brand as well as the risk.", "تنوع‌بخشی هم ریسک را کم کرد و هم برند را رقیق."],
        ],
        col: ["portfolio diversification", "a diversification strategy"],
        note: "diversification غیرقابل‌شمارش است؛ فعل آن diversify با into.",
      },
      {
        slug: "resilience", w: "resilience", fa: "تاب‌آوری", ipa: "/rɪˈzɪliəns/", pos: "noun",
        def: "the ability to recover quickly from a shock",
        ex: [
          ["Resilience matters more than speed.", "تاب‌آوری از سرعت مهم‌تر است."],
          ["Two suppliers gave the plant real resilience.", "دو تأمین‌کننده به کارخانه تاب‌آوری واقعی داد."],
        ],
        col: ["build resilience", "operational resilience"],
        note: "resilience غیرقابل‌شمارش است؛ صفت آن resilient.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "exposure", gloss: "مواجهه",
        options: ["مواجهه", "سود", "قرارداد", "بیمه"],
        note: "exposure نشان می‌دهد از یک ریسک چقدر ممکن است از دست بدهی.",
      },
      {
        k: "mcq", word: "mitigation", gloss: "کاهش ریسک",
        options: ["کاهش ریسک", "افزایش سود", "گزارش مالی", "بازبینی"],
        note: "mitigation کاری است که ریسک را کوچک‌تر می‌کند.",
      },
      {
        k: "mcq", word: "resilience", gloss: "تاب‌آوری",
        options: ["تاب‌آوری", "سرعت", "دقت", "هزینه"],
        note: "resilience توان بازیابی سریع پس از یک ضربه است.",
      },
      {
        k: "blank", sentence: "If the supplier failed, we ___ lose two weeks.",
        accepted: ["would", "might"], skill: "VOCABULARY",
        note: "شرطی خیالی: نتیجه با would یا might می‌آید، نه will.",
      },
      {
        k: "blank", sentence: "If we had hedged earlier, the loss ___ have been smaller.",
        accepted: ["would", "might"], skill: "VOCABULARY",
        note: "نتیجهٔ گذشته: would have / might have + قسمت سوم.",
      },
      {
        k: "recall", prompt: "بیمه", accepted: ["insurance"],
        note: "بیمه insurance است و فعل آن insure.",
      },
      {
        k: "tr", prompt: "اگر تأمین‌کننده از کار بیفتد، دو هفته از دست می‌دهیم.",
        accepted: [
          "if the supplier failed we would lose two weeks",
          "if the supplier fails we will lose two weeks",
        ],
        note: "شرطی نوع دوم: failed … would lose.",
      },
      {
        k: "listen", text: "If the supplier failed, we would lose two weeks.",
        note: "به failed و would lose گوش کنید: شرطی خیالی.",
      },
      {
        k: "prod", prompt: "بگو اگر تأمین‌کننده از کار بیفتد، دو هفته از دست می‌دهیم.",
        mustUse: ["if", "would"], minWords: 8,
        accepted: ["If the supplier failed, we would lose two weeks."],
        hint: "با If the supplier failed… شروع کن.",
        note: "ترتیب لنگرها: if در شرط و would در نتیجه.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ کمیتهٔ ریسک", partner: "مدیر ریسک",
        prompt: "بگو اگر تأمین‌کننده از کار بیفتد، دو هفته از دست می‌دهیم.",
        question: "What is our biggest exposure?",
        questionFa: "بزرگ‌ترین مواجههٔ ما چیست؟",
        accepted: [
          "If the supplier failed, we would lose two weeks.",
          "Our biggest exposure is a supplier failure.",
        ],
        note: "شرطی خیالی: failed … would lose.",
      },
      {
        scene: "جلسهٔ کمیتهٔ ریسک", partner: "مدیر ریسک",
        prompt: "بگو مواجههٔ ما در یک تأمین‌کننده متمرکز است.",
        question: "Where is the risk concentrated?",
        questionFa: "ریسک کجا متمرکز است؟",
        accepted: [
          "Our exposure is concentrated in one supplier.",
          "It is concentrated in one supplier.",
        ],
        note: "exposure be concentrated in: در یک جا متمرکز بودن.",
      },
      {
        scene: "جلسهٔ کمیتهٔ ریسک", partner: "مدیر ریسک",
        prompt: "بگو کاهش ریسک از جبران خسارت ارزان‌تر است.",
        question: "Why invest in mitigation?",
        questionFa: "چرا روی کاهش ریسک سرمایه‌گذاری کنیم؟",
        accepted: [
          "Mitigation is cheaper than recovery.",
          "Because mitigation is cheaper than recovery.",
        ],
        note: "cheaper than: مقایسهٔ دو اسم.",
      },
      {
        scene: "جلسهٔ کمیتهٔ ریسک", partner: "مدیر ریسک",
        prompt: "بگو بیمه بخشی از ریسک را پوشش می‌دهد.",
        question: "Does insurance cover it?",
        questionFa: "بیمه پوشش می‌دهد؟",
        accepted: [
          "Insurance covers part of the risk.",
          "Yes, insurance covers part of the risk.",
        ],
        note: "insurance غیرقابل‌شمارش است: covers، نه cover.",
      },
      {
        scene: "جلسهٔ کمیتهٔ ریسک", partner: "مدیر ریسک",
        prompt: "بگو حد پذیرش ریسک ما پایین است.",
        question: "How much risk will the board accept?",
        questionFa: "هیئت‌مدیره چقدر ریسک می‌پذیرد؟",
        accepted: [
          "Our risk appetite is low.",
          "The board's appetite for risk is low.",
        ],
        note: "risk appetite: حد پذیرش ریسک.",
      },
      {
        scene: "جلسهٔ کمیتهٔ ریسک", partner: "مدیر ریسک",
        prompt: "بگو تنوع‌بخشی تاب‌آوری را بالا می‌برد.",
        question: "How do we become more resilient?",
        questionFa: "چطور تاب‌آورتر می‌شویم؟",
        accepted: [
          "Diversification raises resilience.",
          "Diversification makes us more resilient.",
        ],
        note: "resilient صفت و resilience اسم است.",
      },
    ],
  },
];
