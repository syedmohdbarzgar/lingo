// v55 content — C2 syllabus topics 4 (sociolinguistics) and 5 (discourse
// analysis). Read by `v55_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.sociolinguistics.lesson-01",
      order: 170,
      title: "Sociolinguistics",
      titleFa: "زبان‌شناسی اجتماعی",
      topic: "Language",
      tip: [
        "## قاعده",
        "وقتی دربارهٔ شمار گویندگان حرف می‌زنی، فعل با هستهٔ عبارت مطابقت می‌کند: The number of speakers has fallen (نه have).",
        "با a number of همیشه فعل جمع می‌آید: A number of dialects are disappearing.",
        "برای تغییرات زبانی، گذشتهٔ ساده با used to طبیعی‌تر است: People used to say…",
        "## جدول",
        "The number of speakers has fallen sharply. — شمار گویندگان به‌شدت کم شده است.",
        "A number of dialects are disappearing fast. — شماری از گونه‌ها به‌سرعت از میان می‌روند.",
        "## مثال‌ها",
        "Her idiolect is full of half-finished sentences. — گویش فردی او پر از جمله‌های نیمه‌تمام است.",
        "The sociolect of the port workers has its own words for shifts. — گونهٔ گروهی کارگران بندر واژه‌های خودش را برای نوبت‌های کاری دارد.",
        "Diglossia keeps the written form formal and the spoken form local. — دوزبان‌گونگی شکل نوشتاری را رسمی و شکل گفتاری را محلی نگه می‌دارد.",
        "## خطاهای رایج",
        "The number of speakers have fallen ✗ — هستهٔ عبارت number است، پس has.",
        "A number of dialects is disappearing ✗ — با a number of فعل جمع می‌آید.",
        "sociolect را با accent یکی نگیر: sociolect به گروه اجتماعی اشاره دارد، accent به تلفظ.",
      ].join("\n"),
    },
    grammarNode: "grammar.subject-verb-agreement",
    vocab: {
      id: "vocab.sociolinguistics",
      title: "Sociolinguistics",
      titleFa: "زبان‌شناسی اجتماعی",
      prerequisites: ["vocab.semantic-precision"],
    },
    words: [
      {
        slug: "idiolect", w: "idiolect", fa: "گویش فردی", ipa: "/ˈɪdiəlekt/", pos: "noun",
        def: "the speech habits of one individual person",
        ex: [
          ["Her idiolect is full of half-finished sentences.", "گویش فردی او پر از جمله‌های نیمه‌تمام است."],
          ["An idiolect shifts slowly across a lifetime.", "گویش فردی در طول یک عمر به‌کندی تغییر می‌کند."],
        ],
        col: ["a personal idiolect", "recognise someone's idiolect"],
        note: "idiolect فقط به یک فرد تعلق دارد؛ sociolect به یک گروه اجتماعی.",
      },
      {
        slug: "sociolect", w: "sociolect", fa: "گونهٔ گروه اجتماعی", ipa: "/ˈsəʊsiəlekt/", pos: "noun",
        def: "the variety of a language used by a social group",
        ex: [
          ["The sociolect of the port workers has its own words for shifts.", "گونهٔ گروهی کارگران بندر واژه‌های خودش را برای نوبت‌های کاری دارد."],
          ["A sociolect marks belonging more than geography does.", "گونهٔ گروهی بیش از جغرافیا نشانهٔ تعلق است."],
        ],
        col: ["a working-class sociolect", "a regional sociolect"],
        note: "sociolect از socio- («اجتماعی») و lect («گونهٔ زبانی») ساخته شده است.",
      },
      {
        slug: "code-switching", w: "code-switching", fa: "تغییر زبان در گفتار", ipa: "/ˈkəʊd ˌswɪtʃɪŋ/", pos: "noun",
        def: "moving between two languages or varieties inside one conversation",
        ex: [
          ["Code-switching at home and at work is not confusion.", "تغییر زبان در خانه و محل کار نشانهٔ سردرگمی نیست."],
          ["Her code-switching signals who she is talking to.", "تغییر زبان او نشان می‌دهد با چه کسی حرف می‌زند."],
        ],
        col: ["constant code-switching", "code-switching between languages"],
        note: "با خط تیره نوشته می‌شود؛ فعل آن code-switch است.",
      },
      {
        slug: "diglossia", w: "diglossia", fa: "دوزبان‌گونگی", ipa: "/daɪˈɡlɒsiə/", pos: "noun",
        def: "a situation where two varieties of one language divide formal and everyday use",
        ex: [
          ["Diglossia keeps the written form formal and the spoken form local.", "دوزبان‌گونگی شکل نوشتاری را رسمی و شکل گفتاری را محلی نگه می‌دارد."],
          ["Arabic is the textbook case of diglossia.", "عربی نمونهٔ کلاسیک دوزبان‌گونگی است."],
        ],
        col: ["a case of diglossia", "stable diglossia"],
        note: "diglossia غیرقابل‌شمارش است؛ صفت آن diglossic.",
      },
      {
        slug: "vernacular", w: "vernacular", fa: "زبان محلی", ipa: "/vəˈnækjələ(r)/", pos: "noun",
        def: "the everyday local variety of a language",
        ex: [
          ["The vernacular of the market is a full system, not broken English.", "زبان محلی بازار یک نظام کامل است، نه انگلیسی شکسته."],
          ["He wrote the letter in the vernacular, not in Latin.", "او نامه را به زبان محلی نوشت، نه به لاتین."],
        ],
        col: ["the local vernacular", "in the vernacular"],
        note: "vernacular صفت هم می‌شود: vernacular poetry.",
      },
      {
        slug: "hypercorrection", w: "hypercorrection", fa: "تصحیح افراطی", ipa: "/ˌhaɪpəkəˈrekʃn/", pos: "noun",
        def: "an over-corrected form that is not actually standard",
        ex: [
          ["Hypercorrection makes people say between you and I.", "تصحیح افراطی باعث می‌شود مردم بگویند between you and I."],
          ["Her hypercorrection appeared only in formal meetings.", "تصحیح افراطی او فقط در جلسه‌های رسمی بروز می‌کرد."],
        ],
        col: ["a classic hypercorrection", "hypercorrection under pressure"],
        note: "hypercorrection نتیجهٔ تلاش برای «مؤدبانه‌تر» حرف زدن است، نه بی‌سوادی.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "idiolect", gloss: "گویش فردی",
        options: ["گویش فردی", "گونهٔ گروه اجتماعی", "دوزبان‌گونگی", "تصحیح افراطی"],
        note: "idiolect شیوهٔ گفتار یک فرد است، نه یک گروه.",
      },
      {
        k: "mcq", word: "sociolect", gloss: "گونهٔ گروه اجتماعی",
        options: ["گونهٔ گروه اجتماعی", "زبان محلی", "تغییر زبان در گفتار", "گویش فردی"],
        note: "sociolect گونه‌ای است که یک گروه اجتماعی به کار می‌برد.",
      },
      {
        k: "mcq", word: "vernacular", gloss: "زبان محلی",
        options: ["زبان محلی", "تصحیح افراطی", "دوزبان‌گونگی", "گونهٔ گروه اجتماعی"],
        note: "vernacular زبان روزمرهٔ همان منطقه است.",
      },
      {
        k: "blank", sentence: "A number of dialects ___ disappearing fast.",
        accepted: ["are", "were"], skill: "GRAMMAR",
        note: "با a number of فعل جمع می‌آید: are disappearing.",
      },
      {
        k: "blank", sentence: "The number of speakers ___ fallen sharply.",
        accepted: ["has", "had"], skill: "GRAMMAR",
        note: "هستهٔ عبارت the number است، پس فعل مفرد: has fallen.",
      },
      {
        k: "recall", prompt: "گونهٔ گروه اجتماعی", accepted: ["sociolect"],
        note: "گونهٔ گروه اجتماعی sociolect است.",
      },
      {
        k: "tr", prompt: "دوزبان‌گونگی شکل نوشتاری را رسمی نگه می‌دارد.",
        accepted: [
          "diglossia keeps the written form formal",
          "diglossia keeps the written form formal and the spoken form local",
        ],
        note: "diglossia + keeps: فعل مفرد برای اسم غیرقابل‌شمارش.",
      },
      {
        k: "listen", text: "Her idiolect is full of half-finished sentences.",
        note: "به idiolect گوش کنید: شیوهٔ گفتار یک فرد.",
      },
      {
        k: "prod", prompt: "بگو تغییر زبان در خانه و محل کار نشانهٔ سردرگمی نیست.",
        mustUse: ["code-switching"], minWords: 7,
        accepted: ["Code-switching at home and at work is not confusion."],
        hint: "با Code-switching at home… شروع کن.",
        note: "code-switching در آغاز جمله با حرف بزرگ می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "کارگاه زبان‌شناسی اجتماعی", partner: "پژوهشگر زبان",
        prompt: "بگو تصحیح افراطی باعث می‌شود مردم شکل نادرست را به کار ببرند.",
        question: "Why do people say between you and I?",
        questionFa: "چرا مردم می‌گویند between you and I؟",
        accepted: [
          "Hypercorrection makes people use the wrong form.",
          "It is hypercorrection, not ignorance.",
        ],
        note: "hypercorrection: تلاش برای رسمی‌تر حرف زدن که به شکل نادرست می‌رسد.",
      },
      {
        scene: "کارگاه زبان‌شناسی اجتماعی", partner: "پژوهشگر زبان",
        prompt: "بگو زبان محلی بازار یک نظام کامل است، نه انگلیسی شکسته.",
        question: "Is the market vernacular bad English?",
        questionFa: "زبان محلی بازار انگلیسی بد است؟",
        accepted: [
          "The vernacular of the market is a full system.",
          "It is a full system, not broken English.",
        ],
        note: "vernacular با the می‌آید: the vernacular of the market.",
      },
      {
        scene: "کارگاه زبان‌شناسی اجتماعی", partner: "پژوهشگر زبان",
        prompt: "بگو گونهٔ گروهی بیش از جغرافیا نشانهٔ تعلق است.",
        question: "What does a sociolect mark?",
        questionFa: "گونهٔ گروهی نشانهٔ چیست؟",
        accepted: [
          "A sociolect marks belonging more than geography does.",
          "It marks belonging, not just region.",
        ],
        note: "more than geography does: مقایسه با does به‌جای تکرار فعل.",
      },
      {
        scene: "کارگاه زبان‌شناسی اجتماعی", partner: "پژوهشگر زبان",
        prompt: "بگو دوزبان‌گونگی شکل نوشتاری را رسمی نگه می‌دارد.",
        question: "What does diglossia keep?",
        questionFa: "دوزبان‌گونگی چه چیزی را نگه می‌دارد؟",
        accepted: [
          "Diglossia keeps the written form formal.",
          "It keeps the written form formal and the spoken form local.",
        ],
        note: "keep + مفعول + صفت: keeps the written form formal.",
      },
      {
        scene: "کارگاه زبان‌شناسی اجتماعی", partner: "پژوهشگر زبان",
        prompt: "بگو هر گوینده گویش فردی خودش را دارد.",
        question: "Do all speakers of a dialect sound the same?",
        questionFa: "همهٔ گویندگان یک گونه یک‌جور حرف می‌زنند؟",
        accepted: [
          "Every speaker has their own idiolect.",
          "An idiolect is unique to each speaker.",
        ],
        note: "each speaker با مفرد می‌آید، اما their برای پرهیز از جنسیت رایج است.",
      },
      {
        scene: "کارگاه زبان‌شناسی اجتماعی", partner: "پژوهشگر زبان",
        prompt: "بگو تغییر زبان نشانهٔ سردرگمی نیست.",
        question: "Is code-switching a sign of confusion?",
        questionFa: "تغییر زبان نشانهٔ سردرگمی است؟",
        accepted: [
          "Code-switching is not a sign of confusion.",
          "Switching codes is not confusion.",
        ],
        note: "برای نفی، not a sign of confusion طبیعی‌تر از not confusion است.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.discourse.lesson-01",
      order: 171,
      title: "Discourse Analysis",
      titleFa: "تحلیل گفتمان",
      topic: "Language",
      tip: [
        "## قاعده",
        "برای برجسته کردن بخشی از جمله، آن را با وارونگی جلو می‌آوریم: Not only does the pronoun point back, but it also sets the topic.",
        "بعد از قید منفی در آغاز جمله، فعل کمکی پیش از فاعل می‌آید: Rarely does a paragraph hold together without it.",
        "اگر فعل کمکی در جمله نباشد، do/does/did می‌آید: Seldom do readers notice the shift.",
        "## جدول",
        "Not only does the pronoun point back, but it also sets the topic. — ضمیر نه‌تنها به عقب اشاره می‌کند، بلکه موضوع را هم تعیین می‌کند.",
        "Rarely does a paragraph hold together without coherence. — به‌ندرت پاراگرافی بدون انسجام معنا می‌دهد.",
        "## مثال‌ها",
        "Anaphora holds the paragraph together when the pronoun points back. — ارجاع پس‌رو پاراگراف را به هم می‌چسباند وقتی ضمیر به عقب اشاره می‌کند.",
        "The presupposition that the reader agrees does most of the work here. — پیش‌فرضِ اینکه خواننده موافق است بیشتر کار را انجام می‌دهد.",
        "Turn-taking in that interview was controlled by the host. — نوبت‌گیری در آن مصاحبه در اختیار مجری بود.",
        "## خطاهای رایج",
        "Not only the pronoun points back ✗ — پس از not only در آغاز جمله وارونگی لازم است.",
        "Rarely a paragraph holds together ✗ — با rarely در آغاز، does می‌آید.",
        "cohesion و coherence یکی نیستند: cohesion ابزار زبانی است، coherence منطق متن.",
      ].join("\n"),
    },
    grammarNode: "grammar.inversion-negative-adverbials",
    vocab: {
      id: "vocab.discourse-analysis",
      title: "Discourse Analysis",
      titleFa: "تحلیل گفتمان",
      prerequisites: ["discourse.narrative-voice-and-ambiguity"],
    },
    words: [
      {
        slug: "anaphora", w: "anaphora", fa: "ارجاع پس‌رو", ipa: "/əˈnæfərə/", pos: "noun",
        def: "referring back to something already mentioned",
        ex: [
          ["Anaphora holds the paragraph together when the pronoun points back.", "ارجاع پس‌رو پاراگراف را به هم می‌چسباند وقتی ضمیر به عقب اشاره می‌کند."],
          ["Anaphora fails when two possible nouns compete.", "ارجاع پس‌رو وقتی شکست می‌خورد که دو اسم ممکن با هم رقابت کنند."],
        ],
        col: ["pronominal anaphora", "resolve an anaphora"],
        note: "anaphora غیرقابل‌شمارش است؛ متضاد آن cataphora (اشاره به جلو) است.",
      },
      {
        slug: "presupposition", w: "presupposition", fa: "پیش‌فرض نهفته", ipa: "/ˌpriːsʌpəˈzɪʃn/", pos: "noun",
        def: "something a sentence takes for granted",
        ex: [
          ["The presupposition that the reader agrees does most of the work here.", "پیش‌فرضِ اینکه خواننده موافق است بیشتر کار را انجام می‌دهد."],
          ["A loaded question hides its presupposition.", "پرسش جهت‌دار پیش‌فرض خودش را پنهان می‌کند."],
        ],
        col: ["a hidden presupposition", "challenge a presupposition"],
        note: "presupposition با فعل presuppose می‌آید: the sentence presupposes that…",
      },
      {
        slug: "topicalisation", w: "topicalisation", fa: "برجسته‌سازی موضوع", ipa: "/ˌtɒpɪkəlaɪˈzeɪʃn/", pos: "noun",
        def: "putting an element first so it reads as the topic",
        ex: [
          ["Topicalisation puts the object first for emphasis.", "برجسته‌سازی موضوع مفعول را برای تأکید اول می‌آورد."],
          ["Topicalisation is common in headlines and in speech.", "برجسته‌سازی موضوع در تیترها و در گفتار رایج است."],
        ],
        col: ["object topicalisation", "heavy topicalisation"],
        note: "املای بریتانیایی topicalisation؛ در انگلیسی آمریکایی topicalization.",
      },
      {
        slug: "turn-taking", w: "turn-taking", fa: "نوبت‌گیری در گفتگو", ipa: "/ˈtɜːn ˌteɪkɪŋ/", pos: "noun",
        def: "the system that decides who speaks next",
        ex: [
          ["Turn-taking in that interview was controlled by the host.", "نوبت‌گیری در آن مصاحبه در اختیار مجری بود."],
          ["Overlapping speech breaks the usual turn-taking.", "حرف زدن هم‌زمان نوبت‌گیری معمول را می‌شکند."],
        ],
        col: ["smooth turn-taking", "break the turn-taking"],
        note: "عبارت دوکلمه‌ای با خط تیره؛ در جمله‌های رسمی می‌تواند بدون خط تیره بیاید.",
      },
      {
        slug: "intertextuality", w: "intertextuality", fa: "بافت‌مندی متنی", ipa: "/ˌɪntətekstʃuˈæləti/", pos: "noun",
        def: "the way one text echoes or answers other texts",
        ex: [
          ["Intertextuality makes the advert read like a news report.", "بافت‌مندی متنی باعث می‌شود آگهی مثل یک گزارش خبری خوانده شود."],
          ["Intertextuality rewards readers who know the source.", "بافت‌مندی متنی خوانندگانی را پاداش می‌دهد که منبع را می‌شناسند."],
        ],
        col: ["deliberate intertextuality", "a case of intertextuality"],
        note: "intertextuality غیرقابل‌شمارش است؛ صفت آن intertextual.",
      },
      {
        slug: "coherence", w: "coherence", fa: "انسجام معنایی", ipa: "/kəʊˈhɪərəns/", pos: "noun",
        def: "the logic that makes a text hang together",
        ex: [
          ["Coherence fails when the second paragraph answers a question nobody asked.", "انسجام معنایی وقتی می‌شکند که پاراگراف دوم به پرسشی پاسخ دهد که کسی نپرسیده است."],
          ["Cohesion is grammar; coherence is argument.", "انسجام زبانی دستور است؛ انسجام معنایی استدلال."],
        ],
        col: ["lose coherence", "restore coherence"],
        note: "coherence با cohesion فرق دارد: اولی منطق متن است، دومی ابزارهای زبانی.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "anaphora", gloss: "ارجاع پس‌رو",
        options: ["ارجاع پس‌رو", "پیش‌فرض نهفته", "نوبت‌گیری در گفتگو", "انسجام معنایی"],
        note: "anaphora اشارهٔ ضمیر یا واژه به چیزی است که پیش‌تر آمده است.",
      },
      {
        k: "mcq", word: "presupposition", gloss: "پیش‌فرض نهفته",
        options: ["پیش‌فرض نهفته", "برجسته‌سازی موضوع", "بافت‌مندی متنی", "ارجاع پس‌رو"],
        note: "presupposition چیزی است که جمله آن را مسلّم می‌گیرد.",
      },
      {
        k: "mcq", word: "coherence", gloss: "انسجام معنایی",
        options: ["انسجام معنایی", "نوبت‌گیری در گفتگو", "پیش‌فرض نهفته", "برجسته‌سازی موضوع"],
        note: "coherence منطق و پیوستگی معنایی متن است.",
      },
      {
        k: "blank", sentence: "Not only ___ the pronoun point back, but it also sets the topic.",
        accepted: ["does", "did"], skill: "GRAMMAR",
        note: "پس از not only در آغاز جمله، فعل کمکی پیش از فاعل می‌آید.",
      },
      {
        k: "blank", sentence: "Rarely ___ a paragraph hold together without coherence.",
        accepted: ["does", "did"], skill: "GRAMMAR",
        note: "با rarely در آغاز جمله، do/does/did لازم است.",
      },
      {
        k: "recall", prompt: "پیش‌فرض نهفته", accepted: ["presupposition"],
        note: "پیش‌فرض نهفته presupposition است.",
      },
      {
        k: "tr", prompt: "نوبت‌گیری در آن مصاحبه در اختیار مجری بود.",
        accepted: [
          "turn-taking in that interview was controlled by the host",
          "the turn-taking in that interview was controlled by the host",
        ],
        note: "was controlled by: مجهول گذشته با by برای عامل.",
      },
      {
        k: "listen", text: "Turn-taking in that interview was controlled by the host.",
        note: "به turn-taking گوش کنید: نوبت‌گیری در گفتگو.",
      },
      {
        k: "prod", prompt: "بگو انسجام معنایی وقتی می‌شکند که پاراگراف دوم به پرسشی بی‌ربط پاسخ دهد.",
        mustUse: ["coherence"], minWords: 8,
        accepted: ["Coherence fails when the second paragraph answers a question nobody asked."],
        hint: "با Coherence fails when… شروع کن.",
        note: "coherence در آغاز جمله با حرف بزرگ می‌آید و فعل مفرد می‌گیرد.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ تحلیل متن", partner: "تحلیلگر گفتمان",
        prompt: "بگو ضمیر نه‌تنها به عقب اشاره می‌کند، بلکه موضوع را هم تعیین می‌کند.",
        question: "What does the pronoun do here?",
        questionFa: "ضمیر اینجا چه کاری انجام می‌دهد؟",
        accepted: [
          "Not only does the pronoun point back, but it also sets the topic.",
          "It points back and it sets the topic.",
        ],
        note: "پس از not only وارونگی: does the pronoun point back.",
      },
      {
        scene: "جلسهٔ تحلیل متن", partner: "تحلیلگر گفتمان",
        prompt: "بگو پیش‌فرضِ اینکه خواننده موافق است بیشتر کار را انجام می‌دهد.",
        question: "Where is the argument actually made?",
        questionFa: "استدلال واقعاً کجا ساخته می‌شود؟",
        accepted: [
          "The presupposition that the reader agrees does most of the work.",
          "It is the presupposition, not the evidence.",
        ],
        note: "the presupposition that…: بند موصولی تعریفی برای روشن کردن پیش‌فرض.",
      },
      {
        scene: "جلسهٔ تحلیل متن", partner: "تحلیلگر گفتمان",
        prompt: "بگو نوبت‌گیری در آن مصاحبه در اختیار مجری بود.",
        question: "Who controlled the interview?",
        questionFa: "چه کسی مصاحبه را کنترل می‌کرد؟",
        accepted: [
          "Turn-taking in that interview was controlled by the host.",
          "The host controlled the turn-taking.",
        ],
        note: "مجهول گذشته: was controlled by the host.",
      },
      {
        scene: "جلسهٔ تحلیل متن", partner: "تحلیلگر گفتمان",
        prompt: "بگو انسجام معنایی دستور نیست، استدلال است.",
        question: "Is coherence a grammar problem?",
        questionFa: "انسجام معنایی مشکل دستور است؟",
        accepted: [
          "Coherence is argument, not grammar.",
          "Cohesion is grammar; coherence is argument.",
        ],
        note: "برای تقابل، ساختار X, not Y روشن‌ترین است.",
      },
      {
        scene: "جلسهٔ تحلیل متن", partner: "تحلیلگر گفتمان",
        prompt: "بگو ارجاع پس‌رو وقتی شکست می‌خورد که دو اسم ممکن با هم رقابت کنند.",
        question: "When does the reference fail?",
        questionFa: "ارجاع چه زمانی شکست می‌خورد؟",
        accepted: [
          "Anaphora fails when two possible nouns compete.",
          "It fails when two nouns could both be meant.",
        ],
        note: "when + حال ساده برای شرط کلی: fails when two nouns compete.",
      },
      {
        scene: "جلسهٔ تحلیل متن", partner: "تحلیلگر گفتمان",
        prompt: "بگو بافت‌مندی متنی باعث می‌شود آگهی مثل گزارش خبری خوانده شود.",
        question: "Why does the advert feel like news?",
        questionFa: "چرا آگهی مثل خبر به نظر می‌رسد؟",
        accepted: [
          "Intertextuality makes the advert read like a news report.",
          "It is intertextuality: it echoes a news report.",
        ],
        note: "make + مفعول + فعل ساده: makes the advert read.",
      },
    ],
  },
];
