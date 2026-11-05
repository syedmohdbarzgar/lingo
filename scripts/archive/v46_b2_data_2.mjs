// v46 content, part 2 — B2 topics 23 and 24: common English idioms, common
// phrasal verbs. Read by `v46_b2_batch.mjs`; never run on its own.
//
// Both lessons teach multi-word entries (`once in a blue moon`, `put up with`);
// `VocabularyContentTest` only requires the parts of the head phrase to appear
// in each example, so separable verbs (`sorted out`, `put … with`) pass.
export default [
  {
    lesson: {
      id: "b2.idioms.lesson-01",
      order: 138,
      title: "Common English Idioms",
      titleFa: "اصطلاحات رایج انگلیسی",
      topic: "Language",
      tip: [
        "## قاعده",
        "حرف تعریف بخشی از خود اصطلاح است و عوض نمی‌شود: once in a blue moon (نه the blue moon).",
        "فعل اصطلاح صرف می‌شود ولی شکل اصطلاح ثابت می‌ماند: she bit the bullet.",
        "معنای اصطلاح از جمع واژه‌هایش درنمی‌آید، پس آن را یک واحد حفظ کن.",
        "برای پرسیدن معنا: What does it mean when someone says …؟",
        "## جدول",
        "He calls once in a blue moon. — خیلی به‌ندرت زنگ می‌زند.",
        "You have to bite the bullet and apologise. — باید دندان روی جگر بگذاری و عذرخواهی کنی.",
        "I feel under the weather today. — امروز حالم خوش نیست.",
        "## مثال‌ها",
        "A joke can break the ice at a meeting. — یک شوخی می‌تواند در جلسه یخ را بشکند.",
        "Cheap shoes cost more in the long run. — کفش ارزان در بلندمدت گران‌تر تمام می‌شود.",
        "Let us make sure we are on the same page. — مطمئن شویم یک حرف می‌زنیم.",
        "## خطاهای رایج",
        "حرف تعریف را عوض نکن: once in the blue moon ✗.",
        "ترجمهٔ واژه‌به‌واژه معنای اصطلاح را نمی‌دهد.",
        "on the same page همیشه با the می‌آید، نه a.",
      ].join("\n"),
    },
    grammarNode: "grammar.articles-a-an-the",
    vocab: {
      id: "vocab.common-idioms",
      title: "Common English Idioms",
      titleFa: "اصطلاحات رایج",
      prerequisites: ["vocab.idioms-and-figurative"],
    },
    words: [
      {
        slug: "once-in-a-blue-moon", w: "once in a blue moon", fa: "خیلی به‌ندرت", ipa: "/wʌns ɪn ə bluː muːn/", pos: "phrase",
        def: "very rarely",
        ex: [
          ["He calls once in a blue moon.", "خیلی به‌ندرت زنگ می‌زند."],
          ["Such chances come once in a blue moon.", "چنین فرصت‌هایی خیلی به‌ندرت پیش می‌آید."],
        ],
        col: ["happen once in a blue moon", "call once in a blue moon"],
        note: "حرف تعریف a بخشی از اصطلاح است؛ the blue moon غلط است.",
      },
      {
        slug: "bite-the-bullet", w: "bite the bullet", fa: "دندان روی جگر گذاشتن", ipa: "/baɪt ðə ˈbʊlɪt/", pos: "phrase",
        def: "to accept something difficult and do it",
        ex: [
          ["You have to bite the bullet and apologise.", "باید دندان روی جگر بگذاری و عذرخواهی کنی."],
          ["They decided to bite the bullet and pay.", "آن‌ها تصمیم گرفتند دندان روی جگر بگذارند و پول را بدهند."],
        ],
        col: ["bite the bullet and pay", "decide to bite the bullet"],
        note: "فعل bite صرف می‌شود (bit the bullet) ولی the bullet ثابت می‌ماند.",
      },
      {
        slug: "break-the-ice", w: "break the ice", fa: "یخ را شکستن؛ شروع کردن گفتگو", ipa: "/breɪk ði aɪs/", pos: "phrase",
        def: "to make people feel relaxed when they first meet",
        ex: [
          ["A joke can break the ice at a meeting.", "یک شوخی می‌تواند در جلسه یخ را بشکند."],
          ["He told a story to break the ice.", "او برای شکستن یخ یک داستان تعریف کرد."],
        ],
        col: ["break the ice with a joke", "help break the ice"],
        note: "پیش از واژهٔ صدادار the به ði خوانده می‌شود: the ice.",
      },
      {
        slug: "under-the-weather", w: "under the weather", fa: "حال‌وهوای خوبی نداشتن", ipa: "/ˈʌndə ðə ˈweðə/", pos: "phrase",
        def: "slightly ill",
        ex: [
          ["I feel under the weather today.", "امروز حالم خوش نیست."],
          ["She was under the weather all week.", "او تمام هفته حال‌وهوای خوبی نداشت."],
        ],
        col: ["feel under the weather", "a bit under the weather"],
        note: "برای بیماری خفیف به کار می‌رود، نه بیماری جدی؛ با feel می‌آید.",
      },
      {
        slug: "in-the-long-run", w: "in the long run", fa: "در بلندمدت", ipa: "/ɪn ðə lɒŋ rʌn/", pos: "phrase",
        def: "over a long period of time",
        ex: [
          ["Cheap shoes cost more in the long run.", "کفش ارزان در بلندمدت گران‌تر تمام می‌شود."],
          ["In the long run, practice beats talent.", "در بلندمدت تمرین از استعداد جلو می‌زند."],
        ],
        col: ["in the long run", "cheaper in the long run"],
        note: "متضاد آن in the short term است؛ همیشه با the می‌آید.",
      },
      {
        slug: "on-the-same-page", w: "on the same page", fa: "هم‌نظر بودن؛ یک حرف زدن", ipa: "/ɒn ðə seɪm peɪdʒ/", pos: "phrase",
        def: "agreeing about what to do",
        ex: [
          ["Let us make sure we are on the same page.", "مطمئن شویم یک حرف می‌زنیم."],
          ["The team was never on the same page.", "تیم هیچ‌وقت هم‌نظر نبود."],
        ],
        col: ["be on the same page", "get everyone on the same page"],
        note: "با be و get می‌آید؛ همیشه the دارد، نه a.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "once in a blue moon", gloss: "خیلی به‌ندرت",
        options: ["خیلی به‌ندرت", "هر روز", "همیشه", "به‌زودی"],
        note: "once in a blue moon یعنی تقریباً هیچ‌وقت، خیلی به‌ندرت.",
      },
      {
        k: "mcq", word: "bite the bullet", gloss: "دندان روی جگر گذاشتن",
        options: ["دندان روی جگر گذاشتن", "شکایت کردن", "فرار کردن", "شوخی کردن"],
        note: "bite the bullet یعنی کار سختی را پذیرفتن و انجام دادن.",
      },
      {
        k: "mcq", word: "under the weather", gloss: "حال‌وهوای خوبی نداشتن",
        options: ["حال‌وهوای خوبی نداشتن", "هوای بارانی", "سفر کوتاه", "خواب زیاد"],
        note: "under the weather یعنی کمی بیمار بودن.",
      },
      {
        k: "blank", sentence: "The team was never on the same ___.",
        accepted: ["page", "wavelength"], skill: "VOCABULARY",
        note: "on the same page و on the same wavelength هر دو هم‌نظری را می‌رسانند.",
      },
      {
        k: "blank", sentence: "Cheap shoes cost more in the long ___.",
        accepted: ["run", "term"], skill: "VOCABULARY",
        note: "in the long run و in the long term هر دو بلندمدت را می‌رسانند.",
      },
      {
        k: "recall", prompt: "در بلندمدت", accepted: ["in the long run"],
        note: "در بلندمدت in the long run است؛ متضادش in the short term.",
      },
      {
        k: "tr", prompt: "او خیلی به‌ندرت زنگ می‌زند.",
        accepted: [
          "he calls once in a blue moon",
          "he only calls once in a blue moon",
        ],
        note: "once in a blue moon با a می‌آید، نه the.",
      },
      {
        k: "listen", text: "You have to bite the bullet and apologise.",
        note: "به bullet گوش کنید: اصطلاح با the bullet ثابت است.",
      },
      {
        k: "prod", prompt: "یک عادت را با اصطلاح در بلندمدت توصیف کن.",
        mustUse: ["in", "long"], minWords: 8,
        accepted: ["In the long run, saving a little every month is easier."],
        hint: "با In the long run… شروع کن.",
        note: "ترتیب لنگرها: in پیش از long می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ کاری، پیش از شروع، برای شکستن یخ", partner: "همکار تازه",
        prompt: "بگو یک شوخی می‌تواند یخ جلسه را بشکند.",
        question: "How do we start the meeting?",
        questionFa: "جلسه را چطور شروع کنیم؟",
        accepted: [
          "A joke can break the ice at a meeting.",
          "Let us break the ice with a joke.",
        ],
        note: "break the ice یعنی فضای اول کار را گرم کردن.",
      },
      {
        scene: "جلسهٔ کاری، پیش از شروع، برای شکستن یخ", partner: "همکار تازه",
        prompt: "بگو امروز حالت خوش نیست.",
        question: "Are you feeling alright?",
        questionFa: "حالت خوب است؟",
        accepted: [
          "I feel under the weather today.",
          "Not great — I am a bit under the weather.",
        ],
        note: "under the weather برای بیماری خفیف است.",
      },
      {
        scene: "جلسهٔ کاری، پیش از شروع، برای شکستن یخ", partner: "همکار تازه",
        prompt: "بگو او خیلی به‌ندرت زنگ می‌زند.",
        question: "Does he call often?",
        questionFa: "زیاد زنگ می‌زند؟",
        accepted: [
          "He calls once in a blue moon.",
          "No, only once in a blue moon.",
        ],
        note: "once in a blue moon با a می‌آید و معنایش «تقریباً هیچ‌وقت» است.",
      },
      {
        scene: "جلسهٔ کاری، پیش از شروع، برای شکستن یخ", partner: "همکار تازه",
        prompt: "بگو باید یک تصمیم سخت بگیریم و ادامه بدهیم.",
        question: "Should we delay the decision?",
        questionFa: "تصمیم را عقب بیندازیم؟",
        accepted: [
          "We have to bite the bullet and decide.",
          "No, let us bite the bullet and decide today.",
        ],
        note: "bite the bullet یعنی کار سخت را پذیرفتن؛ فعل صرف می‌شود، بقیه ثابت است.",
      },
      {
        scene: "جلسهٔ کاری، پیش از شروع، برای شکستن یخ", partner: "همکار تازه",
        prompt: "بگو مطمئن شویم یک حرف می‌زنیم.",
        question: "Is everyone clear on the plan?",
        questionFa: "همه با برنامه روشن‌اند؟",
        accepted: [
          "Let us make sure we are on the same page.",
          "We need to get everyone on the same page.",
        ],
        note: "on the same page همیشه the دارد.",
      },
      {
        scene: "جلسهٔ کاری، پیش از شروع، برای شکستن یخ", partner: "همکار تازه",
        prompt: "بگو کفش ارزان در بلندمدت گران‌تر تمام می‌شود.",
        question: "Why buy the expensive option?",
        questionFa: "چرا گزینهٔ گران‌تر را بخریم؟",
        accepted: [
          "Cheap shoes cost more in the long run.",
          "Because cheap shoes cost more in the long run.",
        ],
        note: "in the long run یعنی در بلندمدت؛ متضادش in the short term.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.phrasal-verbs.lesson-01",
      order: 139,
      title: "Common Phrasal Verbs",
      titleFa: "افعال عبارتی پرکاربرد",
      topic: "Language",
      tip: [
        "## قاعده",
        "فعل عبارتی از فعل + حرف اضافه یا قید ساخته می‌شود و معنا از جمع آن دو می‌آید.",
        "بسیاری از این فعل‌ها مفعول را بین دو بخش هم می‌پذیرند: sort the problem out / sort out the problem.",
        "با ضمیر، مفعول همیشه وسط می‌آید: sort it out ✓ / sort out it ✗.",
        "اگر فعل عبارتی با حرف اضافه تمام شود، فعل بعدی gerund می‌گیرد: put up with waiting.",
        "## جدول",
        "The manager will look into the complaint. — مدیر شکایت را بررسی می‌کند.",
        "We need to carry out the survey in May. — باید نظرسنجی را در ماه مه انجام دهیم.",
        "I cannot put up with the noise. — نمی‌توانم سر و صدا را تحمل کنم.",
        "## مثال‌ها",
        "They plan to take over two rivals. — آن‌ها قصد دارند دو رقیب را تصاحب کنند.",
        "I will sort out the schedule today. — امروز برنامه را مرتب می‌کنم.",
        "Old vans break down in cold weather. — وَن‌های قدیمی در هوای سرد خراب می‌شوند.",
        "## خطاهای رایج",
        "با ضمیر مفعول بین دو بخش می‌آید: sort it out ✓ / sort out it ✗.",
        "carry out با مفعول مستقیم می‌آید: carry out the survey ✓.",
        "break down برای ماشین و سیستم است، نه برای انسان.",
      ].join("\n"),
    },
    grammarNode: "grammar.gerund-after-verbs",
    vocab: {
      id: "vocab.common-phrasal-verbs",
      title: "Common Phrasal Verbs",
      titleFa: "افعال عبارتی",
      prerequisites: ["vocab.phrasal-verbs"],
    },
    words: [
      {
        slug: "look-into", w: "look into", fa: "بررسی کردن", ipa: "/lʊk ˈɪntuː/", pos: "phrase",
        def: "to examine a problem or a complaint",
        ex: [
          ["The manager will look into the complaint.", "مدیر شکایت را بررسی می‌کند."],
          ["We are looking into the numbers.", "ما در حال بررسی اعداد هستیم."],
        ],
        col: ["look into a complaint", "look into the matter"],
        note: "look into با مفعول مستقیم می‌آید، بدون at: look into the complaint.",
      },
      {
        slug: "carry-out", w: "carry out", fa: "انجام دادن؛ اجرا کردن", ipa: "/ˈkæri aʊt/", pos: "phrase",
        def: "to do a plan, a test or an order",
        ex: [
          ["We need to carry out the survey in May.", "باید نظرسنجی را در ماه مه انجام دهیم."],
          ["The team will carry out a review.", "تیم یک بازبینی انجام می‌دهد."],
        ],
        col: ["carry out a survey", "carry out a plan"],
        note: "carry out با کار و برنامه می‌آید؛ carry on یعنی ادامه دادن.",
      },
      {
        slug: "take-over", w: "take over", fa: "تصاحب کردن؛ تحویل گرفتن", ipa: "/teɪk ˈəʊvə/", pos: "phrase",
        def: "to take control of something",
        ex: [
          ["They plan to take over two rivals.", "آن‌ها قصد دارند دو رقیب را تصاحب کنند."],
          ["A new manager will take over in June.", "مدیر تازه در ژوئن کار را تحویل می‌گیرد."],
        ],
        col: ["take over a company", "take over from someone"],
        note: "take over from somebody یعنی جانشین کسی شدن.",
      },
      {
        slug: "sort-out", w: "sort out", fa: "مرتب کردن؛ حل کردن", ipa: "/sɔːt aʊt/", pos: "phrase",
        def: "to solve a problem or put things in order",
        ex: [
          ["I will sort out the schedule today.", "امروز برنامه را مرتب می‌کنم."],
          ["We sorted out the problem quickly.", "ما مشکل را سریع حل کردیم."],
        ],
        col: ["sort out a problem", "sort out the details"],
        note: "sort out جداشدنی است: sort it out ✓ / sort out it ✗.",
      },
      {
        slug: "put-up-with", w: "put up with", fa: "تحمل کردن", ipa: "/pʊt ʌp wɪð/", pos: "phrase",
        def: "to accept something annoying without complaining",
        ex: [
          ["I cannot put up with the noise.", "نمی‌توانم سر و صدا را تحمل کنم."],
          ["She puts up with a long commute.", "او رفت‌وآمد طولانی را تحمل می‌کند."],
        ],
        col: ["put up with noise", "cannot put up with something"],
        note: "put up with جداشدنی نیست؛ مفعول بعد از with می‌آید.",
      },
      {
        slug: "break-down", w: "break down", fa: "خراب شدن؛ از هم پاشیدن", ipa: "/breɪk daʊn/", pos: "phrase",
        def: "to stop working, or to fail completely",
        ex: [
          ["Old vans break down in cold weather.", "وَن‌های قدیمی در هوای سرد خراب می‌شوند."],
          ["The system can break down under pressure.", "سیستم زیر فشار می‌تواند از کار بیفتد."],
        ],
        col: ["break down on the road", "break down into parts"],
        note: "break down برای ماشین و سیستم است؛ برای انسان به معناي از پا افتادن هم می‌آید.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "look into", gloss: "بررسی کردن",
        options: ["بررسی کردن", "نادیده گرفتن", "جست‌وجو در اینترنت", "ترک کردن"],
        note: "look into یعنی موضوعی را بررسی کردن، بدون at.",
      },
      {
        k: "mcq", word: "put up with", gloss: "تحمل کردن",
        options: ["تحمل کردن", "قبول نکردن", "بلند شدن", "پوشیدن"],
        note: "put up with یعنی چیزی آزاردهنده را بدون شکایت تحمل کردن.",
      },
      {
        k: "mcq", word: "break down", gloss: "خراب شدن",
        options: ["خراب شدن", "ساختن", "توقف کوتاه", "شکستن قانون"],
        note: "break down برای ماشین و سیستم یعنی از کار افتادن.",
      },
      {
        k: "blank", sentence: "The manager will ___ into the complaint.",
        accepted: ["look", "check"], skill: "VOCABULARY",
        note: "look into و check into هر دو بررسی کردن را می‌رسانند.",
      },
      {
        k: "blank", sentence: "They plan to take ___ two rivals.",
        accepted: ["over", "on"], skill: "VOCABULARY",
        note: "take over یعنی تصاحب کردن؛ take on یعنی پذیرفتن رقیب یا کار.",
      },
      {
        k: "recall", prompt: "تحمل کردن", accepted: ["put up with"],
        note: "تحمل کردن put up with است و مفعول بعد از with می‌آید.",
      },
      {
        k: "tr", prompt: "نمی‌توانم سر و صدا را تحمل کنم.",
        accepted: [
          "i cannot put up with the noise",
          "i can not put up with the noise",
        ],
        note: "put up with سه‌بخشی است و مفعول بعد از with می‌آید.",
      },
      {
        k: "listen", text: "We need to carry out the survey in May.",
        note: "به carry out گوش کنید: یک واحد معنایی به معنی انجام دادن.",
      },
      {
        k: "prod", prompt: "بگو چه چیزی را نمی‌توانی تحمل کنی.",
        mustUse: ["put", "up"], minWords: 8,
        accepted: ["I cannot put up with the noise from the street."],
        hint: "با I cannot put up with… شروع کن.",
        note: "ترتیب لنگرها: put پیش از up می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "دفتر مدیر پروژه، هفتهٔ شلوغ", partner: "مدیر پروژه",
        prompt: "بگو مدیر شکایت را بررسی می‌کند.",
        question: "Who is handling the complaint?",
        questionFa: "چه کسی شکایت را پیگیری می‌کند؟",
        accepted: [
          "The manager will look into the complaint.",
          "The manager is looking into it now.",
        ],
        note: "look into با مفعول مستقیم می‌آید، بدون at.",
      },
      {
        scene: "دفتر مدیر پروژه، هفتهٔ شلوغ", partner: "مدیر پروژه",
        prompt: "بگو باید نظرسنجی را در ماه مه انجام دهیم.",
        question: "When is the survey?",
        questionFa: "نظرسنجی کِی است؟",
        accepted: [
          "We need to carry out the survey in May.",
          "The survey will be carried out in May.",
        ],
        note: "carry out با کار و برنامه می‌آید.",
      },
      {
        scene: "دفتر مدیر پروژه، هفتهٔ شلوغ", partner: "مدیر پروژه",
        prompt: "بگو امروز برنامه را مرتب می‌کنی.",
        question: "When will the schedule be ready?",
        questionFa: "برنامه کِی آماده می‌شود؟",
        accepted: [
          "I will sort out the schedule today.",
          "I will sort it out today.",
        ],
        note: "با ضمیر مفعول وسط می‌آید: sort it out ✓.",
      },
      {
        scene: "دفتر مدیر پروژه، هفتهٔ شلوغ", partner: "مدیر پروژه",
        prompt: "بگو نمی‌توانی سر و صدا را تحمل کنی.",
        question: "Is the noise a problem?",
        questionFa: "سر و صدا مشکل است؟",
        accepted: [
          "I cannot put up with the noise.",
          "Yes, I cannot put up with it any more.",
        ],
        note: "put up with سه‌بخشی است و جداشدنی نیست.",
      },
      {
        scene: "دفتر مدیر پروژه، هفتهٔ شلوغ", partner: "مدیر پروژه",
        prompt: "بگو مدیر تازه در ژوئن کار را تحویل می‌گیرد.",
        question: "Who takes over next month?",
        questionFa: "ماه بعد چه کسی کار را تحویل می‌گیرد؟",
        accepted: [
          "A new manager will take over in June.",
          "The new manager takes over from June.",
        ],
        note: "take over from somebody یعنی جانشین کسی شدن.",
      },
      {
        scene: "دفتر مدیر پروژه، هفتهٔ شلوغ", partner: "مدیر پروژه",
        prompt: "بگو سیستم زیر فشار از کار می‌افتد.",
        question: "Will the system hold?",
        questionFa: "سیستم دوام می‌آورد؟",
        accepted: [
          "The system can break down under pressure.",
          "It might break down if the load grows.",
        ],
        note: "break down برای سیستم یعنی از کار افتادن.",
      },
    ],
  },
];
