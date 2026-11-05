// v57 content — C2 syllabus topics 15 (critical argument analysis) and 16
// (English dialect variation). Read by `v57_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.argument-analysis.lesson-01",
      order: 178,
      title: "Critical Argument Analysis",
      titleFa: "تحلیل انتقادی استدلال‌ها",
      topic: "Debate",
      tip: [
        "## قاعده",
        "برای نقد کاری که در گذشته باید انجام می‌شد، از should have + past participle استفاده کن: The author should have defined the term.",
        "با must have و could have درجهٔ اطمینان را نشان بده: The review must have missed the premise / could have noticed the equivocation.",
        "این ساختار همیشه سه جزء دارد: فعل کمکی + have + قسمت سوم فعل؛ هرگز should has یا must to نیست.",
        "## جدول",
        "The author should have defined the term. — نویسنده باید اصطلاح را تعریف می‌کرد.",
        "The review must have missed the premise. — نقد حتماً مقدمه را جا انداخته است.",
        "The reviewer could have tested the inference. — داور می‌توانست استنتاج را بیازماید.",
        "## مثال‌ها",
        "Soundness needs both a valid form and true premises. — درستی استدلال هم به شکل معتبر نیاز دارد و هم به مقدمه‌های راست.",
        "Circularity hides when the premise restates the conclusion. — دور استدلال وقتی پنهان می‌ماند که مقدمه همان نتیجه را تکرار کند.",
        "The principle of charity answers the strongest version. — اصل خیرخواهی به قوی‌ترین نسخهٔ استدلال پاسخ می‌دهد.",
        "## خطاهای رایج",
        "The author should has defined the term ✗ — پس از should همیشه have می‌آید.",
        "The review must have miss the premise ✗ — پس از have قسمت سوم فعل لازم است.",
        "valid را با sound یکی نگیر: استدلال می‌تواند معتبر باشد و باز هم نادرست بماند.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-in-the-past",
    vocab: {
      id: "vocab.critical-argument",
      title: "Critical Argument Analysis",
      titleFa: "تحلیل انتقادی استدلال‌ها",
      prerequisites: ["discourse.logical-fallacies"],
    },
    words: [
      {
        slug: "soundness", w: "soundness", fa: "درستی استدلال", ipa: "/ˈsaʊndnəs/", pos: "noun",
        def: "the quality of an argument that is both valid and built on true premises",
        ex: [
          ["Soundness needs both a valid form and true premises.", "درستی استدلال هم به شکل معتبر نیاز دارد و هم به مقدمه‌های راست."],
          ["The critique attacks soundness, not the form.", "این نقد به درستی استدلال حمله می‌کند، نه به شکل آن."],
        ],
        col: ["test an argument for soundness", "formal soundness"],
        note: "soundness با validity فرق دارد: validity فقط شکل است، soundness مقدمه‌های راست را هم می‌خواهد.",
      },
      {
        slug: "equivocation", w: "equivocation", fa: "چندپهلویی یک واژه", ipa: "/ɪˌkwɪvəˈkeɪʃn/", pos: "noun",
        def: "the trick of using one word in two different senses inside one argument",
        ex: [
          ["The equivocation sits in the word bank: a river here, money there.", "چندپهلویی در واژهٔ bank است: اینجا رود، آنجا پول."],
          ["Equivocation is easy to miss when the term looks technical.", "وقتی اصطلاح فنی به نظر می‌رسد، چندپهلویی به‌سادگی دیده نمی‌شود."],
        ],
        col: ["a subtle equivocation", "expose an equivocation"],
        note: "equivocation اسم است و از صفت equivocal می‌آید؛ در فارسی «مغالطهٔ اشتراک لفظ» هم گفته می‌شود.",
      },
      {
        slug: "circularity", w: "circularity", fa: "دور استدلال", ipa: "/ˌsɜːkjəˈlærəti/", pos: "noun",
        def: "the fault of proving a claim with a premise that already assumes it",
        ex: [
          ["Circularity hides when the premise restates the conclusion.", "دور استدلال وقتی پنهان می‌ماند که مقدمه همان نتیجه را تکرار کند."],
          ["The definition has a circularity problem.", "این تعریف مشکل دور استدلال دارد."],
        ],
        col: ["argue in a circle", "circularity in the definition"],
        note: "circularity اسم است؛ صفت آن circular و عبارت رایج argue in a circle است.",
      },
      {
        slug: "quantifier", w: "quantifier", fa: "سور", ipa: "/ˈkwɒntɪfaɪə(r)/", pos: "noun",
        def: "a word such as all, some or none that fixes how much a claim covers",
        ex: [
          ["The argument fails on the quantifier: some is not all.", "استدلال روی سور شکست می‌خورد: «بعضی» یعنی «همه» نیست."],
          ["Moving the quantifier changes the whole claim.", "جابه‌جا کردن سور کل ادعا را عوض می‌کند."],
        ],
        col: ["a universal quantifier", "the scope of a quantifier"],
        note: "در منطق quantifier را «سور» می‌نامند: all و every سور عمومی‌اند، some و few سور وجودی.",
      },
      {
        slug: "charity", w: "charity", fa: "خوش‌بینانه‌ترین خوانش", ipa: "/ˈtʃærəti/", pos: "noun",
        def: "the habit of reading an argument in its strongest, not its weakest, form",
        ex: [
          ["Charity means you answer the strongest version, not the weakest.", "خیرخواهی یعنی به قوی‌ترین نسخه پاسخ بدهی، نه به ضعیف‌ترین."],
          ["The principle of charity saves you from straw men.", "اصل خیرخواهی تو را از آدم‌های کاهی نجات می‌دهد."],
        ],
        col: ["the principle of charity", "read a claim with charity"],
        note: "در نقد استدلال charity «خوش‌بینانه‌ترین خوانش» است، نه «خیریه»؛ مترادف رایجش generous reading است.",
      },
      {
        slug: "inference", w: "inference", fa: "استنتاج", ipa: "/ˈɪnfərəns/", pos: "noun",
        def: "a step that moves from evidence to a conclusion",
        ex: [
          ["The inference is valid but the premise is false.", "استنتاج معتبر است، اما مقدمه نادرست است."],
          ["Every inference in the paragraph needs a warrant.", "هر استنتاج در این پاراگراف به یک مجوز نیاز دارد."],
        ],
        col: ["draw an inference", "a valid inference"],
        note: "inference با implication فرق دارد: اولی «گامی که خواننده می‌سازد»، دومی «چیزی که از متن لازم می‌آید».",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "soundness", gloss: "درستی استدلال",
        options: ["درستی استدلال", "دور استدلال", "سور", "استنتاج"],
        note: "soundness یعنی استدلال هم شکل معتبر دارد و هم مقدمه‌های راست.",
      },
      {
        k: "mcq", word: "equivocation", gloss: "چندپهلویی یک واژه",
        options: ["چندپهلویی یک واژه", "خوش‌بینانه‌ترین خوانش", "دور استدلال", "استنتاج"],
        note: "equivocation یعنی یک واژه در دو معنا در همان استدلال به کار برود.",
      },
      {
        k: "mcq", word: "quantifier", gloss: "سور",
        options: ["سور", "استنتاج", "درستی استدلال", "چندپهلویی یک واژه"],
        note: "quantifier واژه‌ای مانند all یا some است که دامنهٔ ادعا را تعیین می‌کند.",
      },
      {
        k: "blank", sentence: "The author ___ have defined the term before using it.",
        accepted: ["should", "must"], skill: "GRAMMAR",
        note: "should have + p.p. نقد کار انجام‌نشده در گذشته است.",
      },
      {
        k: "blank", sentence: "The review ___ have noticed the equivocation in the first paragraph.",
        accepted: ["should", "could"], skill: "GRAMMAR",
        note: "could have + p.p. یعنی «می‌توانست ولی نکرد»؛ should have بار سرزنش دارد.",
      },
      {
        k: "recall", prompt: "دور استدلال", accepted: ["circularity"],
        note: "دور استدلال circularity است.",
      },
      {
        k: "tr", prompt: "هر استنتاج در پاراگراف به یک مجوز نیاز دارد.",
        accepted: [
          "every inference in the paragraph needs a warrant",
          "every inference in the paragraph needs a warrant to stand",
        ],
        note: "needs a warrant: اسم مفرد قابل‌شمارش با حرف تعریف نامعین.",
      },
      {
        k: "listen", text: "The inference is valid but the premise is false.",
        note: "به valid و false گوش کنید: استدلال معتبر می‌تواند مقدمهٔ نادرست داشته باشد.",
      },
      {
        k: "prod", prompt: "بگو درستی استدلال هم به شکل معتبر نیاز دارد و هم به مقدمه‌های راست.",
        mustUse: ["soundness"], minWords: 8,
        accepted: ["Soundness needs both a valid form and true premises."],
        hint: "با Soundness needs… شروع کن.",
        note: "both … and هر دو جزء را با هم می‌آورد؛ needs both A and B.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ نقد استدلال", partner: "داور منطق",
        prompt: "بگو استدلال معتبر است، اما مقدمه‌اش نادرست است.",
        question: "So is the argument good?",
        questionFa: "پس استدلال خوب است؟",
        accepted: [
          "The argument is valid, but the premise is false.",
          "It is valid without being sound.",
        ],
        note: "valid بودن شکل است؛ sound بودن مقدمه‌های راست را هم می‌خواهد.",
      },
      {
        scene: "جلسهٔ نقد استدلال", partner: "داور منطق",
        prompt: "بگو نویسنده باید اصطلاح را پیش از به‌کار بردنش تعریف می‌کرد.",
        question: "What is missing in the opening?",
        questionFa: "در آغاز چه چیزی کم است؟",
        accepted: [
          "The author should have defined the term first.",
          "They should have defined the term before using it.",
        ],
        note: "should have + p.p. نقد کار انجام‌نشده در گذشته است.",
      },
      {
        scene: "جلسهٔ نقد استدلال", partner: "داور منطق",
        prompt: "بگو چندپهلویی در واژهٔ bank است.",
        question: "Where is the equivocation?",
        questionFa: "چندپهلویی کجاست؟",
        accepted: [
          "The equivocation is in the word bank.",
          "It sits in the word bank.",
        ],
        note: "اسم انتزاعی با حرف تعریف معین و متمم in the word bank.",
      },
      {
        scene: "جلسهٔ نقد استدلال", partner: "داور منطق",
        prompt: "بگو سور «همه» را نمی‌توان «بعضی» خواند.",
        question: "Can we read all as some?",
        questionFa: "می‌توان «همه» را «بعضی» خواند؟",
        accepted: [
          "You cannot read a universal quantifier as some.",
          "No: all is not some.",
        ],
        note: "read A as B یعنی «A را B خواندن»؛ cannot در اینجا اجازهٔ منطقی را رد می‌کند.",
      },
      {
        scene: "جلسهٔ نقد استدلال", partner: "داور منطق",
        prompt: "بگو اصل خیرخواهی یعنی به قوی‌ترین نسخه پاسخ بدهیم.",
        question: "Why answer the strongest version?",
        questionFa: "چرا به قوی‌ترین نسخه پاسخ بدهیم؟",
        accepted: [
          "Charity means we answer the strongest version.",
          "Because charity asks for the strongest reading.",
        ],
        note: "means + جملهٔ کامل: یعنی «معنایش این است که…».",
      },
      {
        scene: "جلسهٔ نقد استدلال", partner: "داور منطق",
        prompt: "بگو استنتاج معتبر است، اما مقدمه کافی نیست.",
        question: "Is the inference sound?",
        questionFa: "استنتاج درست است؟",
        accepted: [
          "The inference is valid, but the premise is not enough.",
          "The inference is valid; the premise fails.",
        ],
        note: "not enough در برابر enough: کافی نبودن مقدمه، نه نادرست بودن آن.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.dialects.lesson-01",
      order: 179,
      title: "English Dialect Variation",
      titleFa: "تفاوت‌های گویشی انگلیسی",
      topic: "Language",
      tip: [
        "## قاعده",
        "برای عادت‌های گذشتهٔ دور از used to استفاده کن: People used to say that the r was always sounded.",
        "would هم همین عادت را نشان می‌دهد، اما فقط با فعل کنشی و در بافت روایت: In that village they would speak patois at home.",
        "برای وضعیتی که در گذشته بود و دیگر نیست، used to است، نه would: The town used to have two dialects.",
        "## جدول",
        "People used to say the r was always sounded. — مردم می‌گفتند r همیشه تلفظ می‌شود.",
        "In that village they would speak patois at home. — در آن روستا در خانه patois حرف می‌زدند.",
        "The town used to have two dialects. — شهر قبلاً دو گویش داشت.",
        "## مثال‌ها",
        "The isogloss runs just north of the river. — مرز گویشی درست شمال رود می‌گذرد.",
        "A creole has native speakers; a pidgin does not. — کرئول گویشور بومی دارد؛ پیدگین ندارد.",
        "The patois survives in the fishing villages. — این گویش محلی در روستاهای ماهیگیری زنده مانده است.",
        "## خطاهای رایج",
        "People would used to say ✗ — used to و would را با هم نیاور.",
        "The town would have two dialects ✗ — برای وضعیت پایدار گذشته used to لازم است.",
        "پیدگین را با pigeon («کبوتر») اشتباه نگیر؛ ریشهٔ واژه متفاوت است.",
      ].join("\n"),
    },
    grammarNode: "grammar.used-to-and-would",
    vocab: {
      id: "vocab.dialect-variation",
      title: "English Dialect Variation",
      titleFa: "تفاوت‌های گویشی انگلیسی",
      prerequisites: ["vocab.sociolinguistics"],
    },
    words: [
      {
        slug: "isogloss", w: "isogloss", fa: "مرز گویشی", ipa: "/ˈaɪsəɡlɒs/", pos: "noun",
        def: "a line on a map that marks where one language feature stops and another begins",
        ex: [
          ["The isogloss runs just north of the river.", "مرز گویشی درست شمال رود می‌گذرد."],
          ["Two isoglosses cross in that valley.", "دو مرز گویشی در آن دره یکدیگر را قطع می‌کنند."],
        ],
        col: ["draw an isogloss", "an isogloss boundary"],
        note: "isogloss خطی است که یک ویژگی زبانی را از دیگری جدا می‌کند؛ جمع آن isoglosses است.",
      },
      {
        slug: "pidgin", w: "pidgin", fa: "زبان آمیختهٔ تماسی", ipa: "/ˈpɪdʒɪn/", pos: "noun",
        def: "a simplified contact language with no native speakers",
        ex: [
          ["A pidgin grows where two traders need one shared language.", "پیدگین آنجا رشد می‌کند که دو تاجر به یک زبان مشترک نیاز دارند."],
          ["The pidgin borrowed its words from three languages.", "این زبان آمیخته واژه‌هایش را از سه زبان وام گرفت."],
        ],
        col: ["a trade pidgin", "pidgin English"],
        note: "pidgin گویشور بومی ندارد؛ اگر کودکانی با آن بزرگ شوند، به creole تبدیل می‌شود.",
      },
      {
        slug: "creole", w: "creole", fa: "کرئول", ipa: "/ˈkriːəʊl/", pos: "noun",
        def: "a contact language that has become the first language of a community",
        ex: [
          ["A creole has native speakers; a pidgin does not.", "کرئول گویشور بومی دارد؛ پیدگین ندارد."],
          ["The creole kept the grammar and replaced the vocabulary.", "کرئول دستور را نگه داشت و واژگان را عوض کرد."],
        ],
        col: ["a French-based creole", "creole speakers"],
        note: "creole از pidgin زاده می‌شود، وقتی نسل بعدی آن را زبان اول خود می‌گیرد.",
      },
      {
        slug: "rhotic", w: "rhotic", fa: "r-تلفظ‌کن", ipa: "/ˈrəʊtɪk/", pos: "adjective",
        def: "pronouncing the r sound after a vowel, as in car or farm",
        ex: [
          ["Scottish English is rhotic; most of England is not.", "انگلیسی اسکاتلندی r پس از واکه را تلفظ می‌کند؛ بیشتر انگلستان نه."],
          ["The rhotic accent sounds the r in car.", "لهجهٔ r-تلفظ‌کن r را در car می‌شنواند."],
        ],
        col: ["a rhotic accent", "rhotic and non-rhotic varieties"],
        note: "rhotic از نام حرف rho در یونانی می‌آید؛ متضادش non-rhotic است.",
      },
      {
        slug: "patois", w: "patois", fa: "گویش محلی", ipa: "/ˈpætwɑː/", pos: "noun",
        def: "the regional speech of a place, often rural and unofficial",
        ex: [
          ["The patois survives in the fishing villages.", "این گویش محلی در روستاهای ماهیگیری زنده مانده است."],
          ["He switched into patois with his grandmother.", "با مادربزرگش به گویش محلی حرف زد."],
        ],
        col: ["local patois", "speak patois"],
        note: "patois بار تحقیرآمیز تاریخی دارد؛ در متن رسمی بهتر است variety یا regional speech بنویسی.",
      },
      {
        slug: "twang", w: "twang", fa: "طنین تودماغی لهجه", ipa: "/twæŋ/", pos: "noun",
        def: "a strong nasal quality in someone's accent",
        ex: [
          ["You can hear the twang after one sentence.", "بعد از یک جمله طنین تودماغی لهجه را می‌شنوی."],
          ["The twang marks region, not class.", "این طنین نشانهٔ منطقه است، نه طبقه."],
        ],
        col: ["a nasal twang", "a local twang"],
        note: "twang دربارهٔ طنین تودماغی است، نه هر لهجه‌ای؛ با twang به معنای «صدای زه» فرق دارد.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "pidgin", gloss: "زبان آمیختهٔ تماسی",
        options: ["زبان آمیختهٔ تماسی", "کرئول", "مرز گویشی", "گویش محلی"],
        note: "pidgin زبانی ساده‌شده برای ارتباط دو گروه است و گویشور بومی ندارد.",
      },
      {
        k: "mcq", word: "creole", gloss: "کرئول",
        options: ["کرئول", "زبان آمیختهٔ تماسی", "r-تلفظ‌کن", "طنین تودماغی لهجه"],
        note: "creole زبانی است که از پیدگین زاده شده و زبان اول یک جامعه شده است.",
      },
      {
        k: "mcq", word: "rhotic", gloss: "r-تلفظ‌کن",
        options: ["r-تلفظ‌کن", "گویش محلی", "مرز گویشی", "کرئول"],
        note: "rhotic یعنی لهجه‌ای که r پس از واکه را تلفظ می‌کند.",
      },
      {
        k: "blank", sentence: "People ___ say that the r was always sounded.",
        accepted: ["used to", "would"], skill: "GRAMMAR",
        note: "used to برای عادت گذشته؛ would هم در روایت همین کار را می‌کند.",
      },
      {
        k: "blank", sentence: "In that village they ___ speak patois at home.",
        accepted: ["used to", "would"], skill: "GRAMMAR",
        note: "speak فعل کنشی است، پس هم used to و هم would درست است.",
      },
      {
        k: "recall", prompt: "زبان آمیختهٔ تماسی", accepted: ["pidgin"],
        note: "زبان آمیخته pidgin است.",
      },
      {
        k: "tr", prompt: "کرئول گویشور بومی دارد؛ پیدگین ندارد.",
        accepted: [
          "a creole has native speakers a pidgin does not",
          "a creole has native speakers but a pidgin does not",
        ],
        note: "برای مقایسهٔ دو جمله، تکرار فعل کمکی do کافی است: a pidgin does not.",
      },
      {
        k: "listen", text: "The isogloss runs just north of the river.",
        note: "به isogloss گوش کنید: مرز گویشی، نه مرز جغرافیایی.",
      },
      {
        k: "prod", prompt: "بگو مرز گویشی درست شمال رود می‌گذرد.",
        mustUse: ["isogloss"], minWords: 7,
        accepted: ["The isogloss runs just north of the river."],
        hint: "با The isogloss runs… شروع کن.",
        note: "run در مورد خط و جاده یعنی «کشیده شدن»؛ just north of یعنی درست شمال.",
      },
    ],
    dialogue: [
      {
        scene: "میز گرد گویش‌شناسی", partner: "پژوهشگر گویش",
        prompt: "بگو مرز گویشی درست شمال رود می‌گذرد.",
        question: "Where does the boundary run?",
        questionFa: "این مرز کجا می‌گذرد؟",
        accepted: [
          "The isogloss runs just north of the river.",
          "It runs just north of the river.",
        ],
        note: "just north of یعنی «درست شمالِ»؛ بدون حرف اضافهٔ اضافی.",
      },
      {
        scene: "میز گرد گویش‌شناسی", partner: "پژوهشگر گویش",
        prompt: "بگو آن زبان آمیخته گویشور بومی ندارد.",
        question: "Does the pidgin have native speakers?",
        questionFa: "این پیدگین گویشور بومی دارد؟",
        accepted: [
          "The pidgin has no native speakers.",
          "No, nobody speaks it as a first language.",
        ],
        note: "has no + اسم جمع، معادل منفیِ has any است.",
      },
      {
        scene: "میز گرد گویش‌شناسی", partner: "پژوهشگر گویش",
        prompt: "بگو کرئول از پیدگین زاده شد.",
        question: "How did the creole form?",
        questionFa: "کرئول چطور شکل گرفت؟",
        accepted: [
          "The creole grew out of a pidgin.",
          "A pidgin became a creole.",
        ],
        note: "grow out of یعنی «از چیزی زاده شدن»؛ become تغییر وضعیت را نشان می‌دهد.",
      },
      {
        scene: "میز گرد گویش‌شناسی", partner: "پژوهشگر گویش",
        prompt: "بگو اسکاتلندی r را پس از واکه تلفظ می‌کند.",
        question: "Is Scottish English rhotic?",
        questionFa: "انگلیسی اسکاتلندی r-تلفظ‌کن است؟",
        accepted: [
          "Scottish English is rhotic.",
          "Yes, it sounds the r after a vowel.",
        ],
        note: "sound در نقش فعل یعنی «تلفظ کردن، شنواندن».",
      },
      {
        scene: "میز گرد گویش‌شناسی", partner: "پژوهشگر گویش",
        prompt: "بگو مردم آن روستا با مادربزرگ‌هایشان patois حرف می‌زدند.",
        question: "Did they still speak it at home?",
        questionFa: "در خانه هنوز آن را حرف می‌زدند؟",
        accepted: [
          "They used to speak patois with their grandmothers.",
          "They would speak patois at home.",
        ],
        note: "used to و would هر دو عادت گذشته را می‌رسانند؛ would روایی‌تر است.",
      },
      {
        scene: "میز گرد گویش‌شناسی", partner: "پژوهشگر گویش",
        prompt: "بگو این طنین تودماغی نشانهٔ منطقه است، نه طبقه.",
        question: "What does the twang mark?",
        questionFa: "این طنین نشانهٔ چیست؟",
        accepted: [
          "The twang marks region, not class.",
          "It is a marker of region, not of class.",
        ],
        note: "not class در پایان، بخش دوم تقابل را کوتاه می‌کند: region, not class.",
      },
    ],
  },
];
