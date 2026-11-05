// v52 content, part 2 — C1 topics 22 and 24: business sustainability and
// semantic nuance. Read by `v52_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.business-sustainability.lesson-01",
      order: 162,
      title: "Business Sustainability",
      titleFa: "پایداری کسب‌وکار",
      topic: "Business",
      tip: [
        "## قاعده",
        "برای نتیجهٔ فرضی و غیرواقعی از شرطی نوع دوم استفاده کن: If + گذشتهٔ ساده، would + مصدر.",
        "If we repaired products, we would cut waste.",
        "با were برای همهٔ ضمیرها رسمی‌تر است: If the packaging were compostable, the line would qualify.",
        "## جدول",
        "If we repaired products, we would cut waste. — اگر محصولات را تعمیر می‌کردیم، پسماند کمتری داشتیم.",
        "If the packaging were compostable, the whole line would qualify. — اگر بسته‌بندی قابل کمپوست بود، کل خط واجد شرایط می‌شد.",
        "If procurement asked for an audit, suppliers would move faster. — اگر خرید تقاضای ممیزی می‌کرد، تأمین‌کنندگان سریع‌تر حرکت می‌کردند.",
        "## مثال‌ها",
        "Our footprint fell after the switch to rail. — رد پای محیطی ما پس از روی آوردن به ریل کم شد.",
        "The lifecycle of the battery is eight years. — چرخهٔ عمر باتری هشت سال است.",
        "A circular model keeps materials in use. — مدل چرخه‌ای مواد را در استفاده نگه می‌دارد.",
        "## خطاهای رایج",
        "در شرطی نوع دوم، فعل جملهٔ if گذشته است: If we repaired… ✓ / If we repair… would ✗ (برای فرض).",
        "would پس از کاما می‌آید، نه در جملهٔ if.",
        "footprint قابل‌شمارش است؛ عبارت رایج carbon footprint.",
      ].join("\n"),
    },
    grammarNode: "grammar.tentative-conditionals",
    vocab: {
      id: "vocab.business-sustainability",
      title: "Business Sustainability",
      titleFa: "پایداری کسب‌وکار",
      prerequisites: ["vocab.strategy-evaluation"],
    },
    words: [
      {
        slug: "footprint", w: "footprint", fa: "رد پا (محیطی)", ipa: "/ˈfʊtprɪnt/", pos: "noun",
        def: "the environmental cost of an activity",
        ex: [
          ["Our footprint fell after the switch to rail.", "رد پای محیطی ما پس از روی آوردن به ریل کم شد."],
          ["The carbon footprint of the office is on the wall.", "رد پای کربن دفتر روی دیوار نوشته شده است."],
        ],
        col: ["carbon footprint", "reduce our footprint"],
        note: "footprint قابل‌شمارش است؛ در این معنا معمولاً با carbon می‌آید.",
      },
      {
        slug: "lifecycle", w: "lifecycle", fa: "چرخهٔ عمر", ipa: "/ˈlaɪfsaɪkl/", pos: "noun",
        def: "the whole life of a product, from making to disposal",
        ex: [
          ["The lifecycle of the battery is eight years.", "چرخهٔ عمر باتری هشت سال است."],
          ["Every lifecycle stage has a cost.", "هر مرحله از چرخهٔ عمر هزینه‌ای دارد."],
        ],
        col: ["lifecycle assessment", "at the end of its lifecycle"],
        note: "lifecycle قابل‌شمارش است؛ در انگلیسی آمریکایی life cycle هم نوشته می‌شود.",
      },
      {
        slug: "circular", w: "circular", fa: "چرخه‌ای", ipa: "/ˈsɜːkjələ/", pos: "adjective",
        def: "designed so materials are reused, not thrown away",
        ex: [
          ["A circular model keeps materials in use.", "مدل چرخه‌ای مواد را در استفاده نگه می‌دارد."],
          ["Circular design starts with the screws.", "طراحی چرخه‌ای از پیچ‌ها شروع می‌شود."],
        ],
        col: ["a circular economy", "circular design"],
        note: "circular صفت است؛ اسم آن circularity.",
      },
      {
        slug: "biodegradable", w: "biodegradable", fa: "تجزیه‌پذیر", ipa: "/ˌbaɪəʊdɪˈɡreɪdəbl/", pos: "adjective",
        def: "able to break down naturally",
        ex: [
          ["The new cups are biodegradable.", "لیوان‌های جدید تجزیه‌پذیرند."],
          ["Biodegradable does not always mean compostable.", "تجزیه‌پذیر همیشه به معنی قابل کمپوست نیست."],
        ],
        col: ["biodegradable packaging", "fully biodegradable"],
        note: "biodegradable با compostable یکی نیست؛ دومی به شرایط کمپوست نیاز دارد.",
      },
      {
        slug: "stewardship", w: "stewardship", fa: "سرپرستی مسئولانه", ipa: "/ˈstjuːədʃɪp/", pos: "noun",
        def: "taking care of something you do not own",
        ex: [
          ["Stewardship of water matters more than price.", "سرپرستی مسئولانهٔ آب از قیمت مهم‌تر است."],
          ["The board talks about stewardship every spring.", "هیئت‌مدیره هر بهار دربارهٔ سرپرستی مسئولانه صحبت می‌کند."],
        ],
        col: ["environmental stewardship", "stewardship of resources"],
        note: "stewardship غیرقابل‌شمارش است؛ اسم شخص steward.",
      },
      {
        slug: "procurement", w: "procurement", fa: "خرید و تأمین", ipa: "/prəˈkjʊəmənt/", pos: "noun",
        def: "the department and process that buys what a company needs",
        ex: [
          ["Procurement now asks every supplier for an audit.", "واحد خرید حالا از هر تأمین‌کننده ممیزی می‌خواهد."],
          ["Procurement is where sustainability becomes real.", "خرید و تأمین جایی است که پایداری واقعی می‌شود."],
        ],
        col: ["procurement policy", "head of procurement"],
        note: "procurement غیرقابل‌شمارش است؛ در انگلیسی تجاری به معنی «تدارکات» است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "footprint", gloss: "رد پا (محیطی)",
        options: ["رد پا (محیطی)", "چرخهٔ عمر", "سرپرستی مسئولانه", "خرید و تأمین"],
        note: "footprint هزینهٔ محیطی یک فعالیت است، معمولاً carbon footprint.",
      },
      {
        k: "mcq", word: "lifecycle", gloss: "چرخهٔ عمر",
        options: ["چرخهٔ عمر", "تجزیه‌پذیری", "بسته‌بندی", "ممیزی"],
        note: "lifecycle کل عمر محصول از ساخت تا دورریز است.",
      },
      {
        k: "mcq", word: "stewardship", gloss: "سرپرستی مسئولانه",
        options: ["سرپرستی مسئولانه", "خرید و تأمین", "رد پا", "طراحی چرخه‌ای"],
        note: "stewardship مراقبت از چیزی است که مالک آن نیستی.",
      },
      {
        k: "blank", sentence: "If we repaired products, we ___ cut waste.",
        accepted: ["would", "could"], skill: "VOCABULARY",
        note: "در شرطی نوع دوم، جملهٔ نتیجه would/could + مصدر می‌گیرد.",
      },
      {
        k: "blank", sentence: "If the packaging ___ compostable, the line would qualify.",
        accepted: ["were", "was"], skill: "VOCABULARY",
        note: "were برای همهٔ ضمیرها صورت رسمی‌تر است؛ was در گفتار رایج است.",
      },
      {
        k: "recall", prompt: "چرخهٔ عمر", accepted: ["lifecycle", "life cycle"],
        note: "چرخهٔ عمر lifecycle است و در آمریکایی life cycle هم نوشته می‌شود.",
      },
      {
        k: "tr", prompt: "اگر محصولات را تعمیر می‌کردیم، پسماند کمتری داشتیم.",
        accepted: [
          "if we repaired products we would cut waste",
          "if we repaired products we could cut waste",
        ],
        note: "شرطی نوع دوم: if + گذشتهٔ ساده، would + مصدر.",
      },
      {
        k: "listen", text: "A circular model keeps materials in use.",
        note: "به circular model گوش کنید: طراحی چرخه‌ای، نه خطی.",
      },
      {
        k: "prod", prompt: "بگو اگر بسته‌بندی قابل بازیافت بود، خط تولید واجد شرایط می‌شد.",
        mustUse: ["if", "would"], minWords: 8,
        accepted: ["If the packaging were recyclable, the line would qualify."],
        hint: "با If the packaging were… شروع کن.",
        note: "ترتیب لنگرها: if پیش از would.",
      },
    ],
    dialogue: [
      {
        scene: "بازبینی پایداری در جلسهٔ هیئت‌مدیره", partner: "مدیر پایداری",
        prompt: "بگو اگر محصولات را تعمیر می‌کردیم، پسماند کمتری داشتیم.",
        question: "What would repair change?",
        questionFa: "تعمیر چه چیزی را عوض می‌کرد؟",
        accepted: [
          "If we repaired products, we would cut waste.",
          "We would cut waste if we repaired products.",
        ],
        note: "شرطی نوع دوم برای نتیجهٔ فرضی: repaired + would cut.",
      },
      {
        scene: "بازبینی پایداری در جلسهٔ هیئت‌مدیره", partner: "مدیر پایداری",
        prompt: "بگو رد پای محیطی ما پس از روی آوردن به ریل کم شد.",
        question: "Did the rail switch help?",
        questionFa: "تغییر به ریل کمک کرد؟",
        accepted: [
          "Our footprint fell after the switch to rail.",
          "Yes, our footprint fell after the switch to rail.",
        ],
        note: "fall/fall/fell: کم شدن؛ fell گذشتهٔ ساده است.",
      },
      {
        scene: "بازبینی پایداری در جلسهٔ هیئت‌مدیره", partner: "مدیر پایداری",
        prompt: "بگو چرخهٔ عمر باتری هشت سال است.",
        question: "How long does the battery last?",
        questionFa: "باتری چقدر دوام دارد؟",
        accepted: [
          "The lifecycle of the battery is eight years.",
          "Eight years is the lifecycle of the battery.",
        ],
        note: "lifecycle مفرد است: is.",
      },
      {
        scene: "بازبینی پایداری در جلسهٔ هیئت‌مدیره", partner: "مدیر پایداری",
        prompt: "بگو واحد خرید حالا از هر تأمین‌کننده ممیزی می‌خواهد.",
        question: "Who controls suppliers now?",
        questionFa: "الان چه کسی تأمین‌کنندگان را کنترل می‌کند؟",
        accepted: [
          "Procurement now asks every supplier for an audit.",
          "Procurement asks every supplier for an audit.",
        ],
        note: "ask somebody for something: از کسی چیزی خواستن.",
      },
      {
        scene: "بازبینی پایداری در جلسهٔ هیئت‌مدیره", partner: "مدیر پایداری",
        prompt: "بگو سرپرستی مسئولانهٔ آب از قیمت مهم‌تر است.",
        question: "Why not simply buy cheaper water?",
        questionFa: "چرا آب ارزان‌تر نخریم؟",
        accepted: [
          "Stewardship of water matters more than price.",
          "Stewardship of water is more important than price.",
        ],
        note: "stewardship of: سرپرستی چه چیزی.",
      },
      {
        scene: "بازبینی پایداری در جلسهٔ هیئت‌مدیره", partner: "مدیر پایداری",
        prompt: "بگو طراحی چرخه‌ای از پیچ‌ها شروع می‌شود.",
        question: "Where does circular design begin?",
        questionFa: "طراحی چرخه‌ای از کجا شروع می‌شود؟",
        accepted: [
          "Circular design starts with the screws.",
          "It starts with the screws.",
        ],
        note: "start with: با چیزی شروع شدن.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.semantic-nuance.lesson-01",
      order: 163,
      title: "Semantic Nuance",
      titleFa: "ظرافت‌های معنایی واژگان",
      topic: "Language",
      tip: [
        "## قاعده",
        "دو واژهٔ هم‌معنا همیشه قابل جانشینی نیستند؛ بار معنایی (connotation) تفاوت می‌سازد.",
        "برای مقایسهٔ نرمی و تندی واژه‌ها از صفت تفضیلی استفاده کن: softer than، less loaded than، more neutral than.",
        "برای شدت بیشتر: far/much + تفضیلی — This option is far less loaded than that one.",
        "## جدول",
        "The second word is softer than the first. — واژهٔ دوم از اولی ملایم‌تر است.",
        "This option is much less loaded than that one. — این گزینه بار منفی بسیار کمتری از آن یکی دارد.",
        "The report is as neutral as the brief allows. — گزارش تا جایی که شرح کار اجازه می‌دهد بی‌طرف است.",
        "## مثال‌ها",
        "Every nuance matters in a legal letter. — هر تفاوت ظریف در نامهٔ حقوقی مهم است.",
        "Downsizing is a euphemism for cutting jobs. — کوچک‌سازی تعبیری خوش‌بینانه برای تعدیل نیرو است.",
        "The label was pejorative and unfair. — آن برچسب تحقیرآمیز و ناعادلانه بود.",
        "## خطاهای رایج",
        "denotation با connotation یکی نیست: denotation معنی فرهنگ‌نامه‌ای است، connotation بار احساسی.",
        "euphemism قابل‌شمارش است: a euphemism / two euphemisms.",
        "hyperbole غیرقابل‌شمارش است؛ فعل آن exaggerate است.",
      ].join("\n"),
    },
    grammarNode: "grammar.comparatives",
    vocab: {
      id: "vocab.semantic-nuance",
      title: "Semantic Nuance",
      titleFa: "ظرافت‌های معنایی واژگان",
      prerequisites: ["discourse.register-and-formality"],
    },
    words: [
      {
        slug: "nuance", w: "nuance", fa: "تفاوت ظریف", ipa: "/ˈnjuːɑːns/", pos: "noun",
        def: "a small difference in meaning or tone",
        ex: [
          ["Every nuance matters in a legal letter.", "هر تفاوت ظریف در نامهٔ حقوقی مهم است."],
          ["Translation loses the nuance of the original.", "ترجمه تفاوت ظریف متن اصلی را از دست می‌دهد."],
        ],
        col: ["a subtle nuance", "lose the nuance"],
        note: "nuance قابل‌شمارش است؛ صفت آن nuanced.",
      },
      {
        slug: "denotation", w: "denotation", fa: "معنی صریح", ipa: "/ˌdiːnəʊˈteɪʃn/", pos: "noun",
        def: "the literal, dictionary meaning of a word",
        ex: [
          ["The denotation is the same; the tone is not.", "معنی صریح یکی است؛ لحن یکی نیست."],
          ["A denotation never explains why a word stings.", "معنی صریح هرگز توضیح نمی‌دهد چرا یک واژه آزرده می‌کند."],
        ],
        col: ["literal denotation", "denotation versus connotation"],
        note: "denotation غیرقابل‌شمارش است؛ مقابل آن connotation.",
      },
      {
        slug: "euphemism", w: "euphemism", fa: "تعبیر خوش‌بینانه", ipa: "/ˈjuːfəmɪzəm/", pos: "noun",
        def: "a mild word used for something unpleasant",
        ex: [
          ["Downsizing is a euphemism for cutting jobs.", "کوچک‌سازی تعبیری خوش‌بینانه برای تعدیل نیرو است."],
          ["The euphemism fooled nobody in the room.", "آن تعبیر خوش‌بینانه هیچ‌کس در اتاق را فریب نداد."],
        ],
        col: ["a polite euphemism", "hide behind a euphemism"],
        note: "euphemism قابل‌شمارش است؛ صفت آن euphemistic.",
      },
      {
        slug: "hyperbole", w: "hyperbole", fa: "اغراق", ipa: "/haɪˈpɜːbəli/", pos: "noun",
        def: "a deliberate exaggeration that is not meant literally",
        ex: [
          ["The claim was hyperbole, not a lie.", "آن ادعا اغراق بود، نه دروغ."],
          ["Hyperbole weakens a report that needs evidence.", "اغراق گزارشی را که به شاهد نیاز دارد ضعیف می‌کند."],
        ],
        col: ["rhetorical hyperbole", "no hyperbole"],
        note: "hyperbole غیرقابل‌شمارش است؛ تلفظ یونانی آن /haɪˈpɜːbəli/ است.",
      },
      {
        slug: "pejorative", w: "pejorative", fa: "تحقیرآمیز", ipa: "/pɪˈdʒɒrətɪv/", pos: "adjective",
        def: "expressing disapproval or contempt",
        ex: [
          ["The label was pejorative and unfair.", "آن برچسب تحقیرآمیز و ناعادلانه بود."],
          ["Use the neutral term, not the pejorative one.", "واژهٔ بی‌طرف را به کار ببر، نه واژهٔ تحقیرآمیز را."],
        ],
        col: ["a pejorative term", "used pejoratively"],
        note: "pejorative صفت است؛ قید آن pejoratively.",
      },
      {
        slug: "synonym", w: "synonym", fa: "هم‌معنا", ipa: "/ˈsɪnənɪm/", pos: "noun",
        def: "a word that means nearly the same as another",
        ex: [
          ["No synonym fits every context.", "هیچ هم‌معنایی در هر بافتی جا نمی‌شود."],
          ["The synonym is warmer than the original.", "آن هم‌معنا از واژهٔ اصلی گرم‌تر است."],
        ],
        col: ["a close synonym", "a synonym for"],
        note: "synonym با for می‌آید: a synonym for this word.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "nuance", gloss: "تفاوت ظریف",
        options: ["تفاوت ظریف", "معنی صریح", "اغراق", "هم‌معنا"],
        note: "nuance تفاوت کوچک در معنی یا لحن است.",
      },
      {
        k: "mcq", word: "euphemism", gloss: "تعبیر خوش‌بینانه",
        options: ["تعبیر خوش‌بینانه", "تحقیر", "اغراق", "هم‌معنا"],
        note: "euphemism واژه‌ای ملایم برای معنایی ناخوشایند است.",
      },
      {
        k: "mcq", word: "hyperbole", gloss: "اغراق",
        options: ["اغراق", "تفاوت ظریف", "معنی صریح", "درشتی لحن"],
        note: "hyperbole اغراق عامدانه است و نباید تحت‌اللفظی فهمیده شود.",
      },
      {
        k: "blank", sentence: "This option is ___ loaded than that one.",
        accepted: ["less", "much less", "far less"], skill: "VOCABULARY",
        note: "قید شدت با تفضیلی می‌آید: less / much less / far less + loaded than.",
      },
      {
        k: "blank", sentence: "Chosen words are often ___ neutral than they look.",
        accepted: ["less", "more"], skill: "VOCABULARY",
        note: "صفت تفضیلی با than: less neutral یا more neutral.",
      },
      {
        k: "recall", prompt: "تفاوت ظریف", accepted: ["nuance"],
        note: "تفاوت ظریف nuance است و صفت آن nuanced.",
      },
      {
        k: "tr", prompt: "این گزینه از آن یکی بار منفی کمتری دارد.",
        accepted: [
          "this option is less loaded than that one",
          "this option is much less loaded than that one",
        ],
        note: "less loaded than: بار منفی کمتر داشتن.",
      },
      {
        k: "listen", text: "The second word is softer than the first.",
        note: "به softer than گوش کنید: تفضیلی برای مقایسهٔ نرمی واژه.",
      },
      {
        k: "prod", prompt: "بگو واژهٔ دوم از اولی ملایم‌تر است.",
        mustUse: ["softer", "than"], minWords: 5,
        accepted: ["The second word is softer than the first."],
        hint: "با The second word is softer… شروع کن.",
        note: "ترتیب لنگرها: softer پیش از than.",
      },
    ],
    dialogue: [
      {
        scene: "ویرایش متن با سرویراستار", partner: "سرویراستار",
        prompt: "بگو واژهٔ دوم از اولی ملایم‌تر است.",
        question: "Which of the two words should we use?",
        questionFa: "از بین این دو واژه کدام را به کار ببریم؟",
        accepted: [
          "The second word is softer than the first.",
          "The second one is softer than the first.",
        ],
        note: "softer than: تفضیلی برای مقایسهٔ بار معنایی.",
      },
      {
        scene: "ویرایش متن با سرویراستار", partner: "سرویراستار",
        prompt: "بگو هر تفاوت ظریف در نامهٔ حقوقی مهم است.",
        question: "Does this small difference matter?",
        questionFa: "این تفاوت کوچک مهم است؟",
        accepted: [
          "Every nuance matters in a legal letter.",
          "Yes, every nuance matters in a legal letter.",
        ],
        note: "every + اسم مفرد + فعل مفرد: matters.",
      },
      {
        scene: "ویرایش متن با سرویراستار", partner: "سرویراستار",
        prompt: "بگو کوچک‌سازی تعبیری خوش‌بینانه برای تعدیل نیرو است.",
        question: "Why not write downsizing?",
        questionFa: "چرا ننویسیم کوچک‌سازی؟",
        accepted: [
          "Downsizing is a euphemism for cutting jobs.",
          "Because downsizing is a euphemism for cutting jobs.",
        ],
        note: "a euphemism for: تعبیری خوش‌بینانه برای چه چیزی.",
      },
      {
        scene: "ویرایش متن با سرویراستار", partner: "سرویراستار",
        prompt: "بگو آن ادعا اغراق بود، نه دروغ.",
        question: "Is the claim a lie?",
        questionFa: "آن ادعا دروغ است؟",
        accepted: [
          "The claim was hyperbole, not a lie.",
          "No, the claim was hyperbole, not a lie.",
        ],
        note: "hyperbole غیرقابل‌شمارش است: was.",
      },
      {
        scene: "ویرایش متن با سرویراستار", partner: "سرویراستار",
        prompt: "بگو آن برچسب تحقیرآمیز و ناعادلانه بود.",
        question: "What was wrong with the label?",
        questionFa: "آن برچسب چه اشکالی داشت؟",
        accepted: [
          "The label was pejorative and unfair.",
          "It was pejorative and unfair.",
        ],
        note: "pejorative صفت است و پیش از اسم می‌آید.",
      },
      {
        scene: "ویرایش متن با سرویراستار", partner: "سرویراستار",
        prompt: "بگو معنی صریح یکی است، لحن یکی نیست.",
        question: "Are the two words the same?",
        questionFa: "این دو واژه یکی‌اند؟",
        accepted: [
          "The denotation is the same; the tone is not.",
          "The denotation is the same, but the tone is not.",
        ],
        note: "denotation معنی صریح است، نه بار احساسی.",
      },
    ],
  },
];
