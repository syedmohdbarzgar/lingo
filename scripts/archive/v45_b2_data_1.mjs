// v45 content, part 1 — B2 topics 16 and 18: persuasion and advertising, crisis
// management. Read by `v45_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.persuasion.lesson-01",
      order: 132,
      title: "Persuasion and Advertising",
      titleFa: "تبلیغات و تأثیرگذاری بر مخاطب",
      topic: "Media",
      tip: [
        "## قاعده",
        "مقایسهٔ برابری: as + صفت + as — The new advert is as clear as the old one.",
        "مقایسهٔ نابرابر: صفت کوتاه + er + than، یا more + صفت بلند + than — cheaper than / more effective than.",
        "برترین: the + est یا the most — the best price on the market.",
        "دو برابر و چند برابر: twice as fast as / three times as many as.",
        "## جدول",
        "The new model is cheaper than the old one. — مدل تازه از قبلی ارزان‌تر است.",
        "It is twice as fast as the old model. — دو برابر مدل قدیمی سریع است.",
        "This is the most common claim in advertising. — این رایج‌ترین ادعا در تبلیغات است.",
        "## مثال‌ها",
        "The consumer sees the price, not the cost. — مصرف‌کننده قیمت را می‌بیند، نه هزینهٔ واقعی را.",
        "A strong brand does not need to shout. — یک برند قوی لازم نیست داد بزند.",
        "One testimonial can sell more than a slogan. — یک توصیهٔ مشتری می‌تواند بیشتر از یک شعار بفروشد.",
        "## خطاهای رایج",
        "as … as کامل است: as good as ✓ / as good than ✗.",
        "صفت بلند er نمی‌گیرد: more effective ✓ / effectiver ✗.",
        "برترین صفت با the می‌آید: the strongest claim ✓ / strongest claim ✗ در جملهٔ کامل.",
      ].join("\n"),
    },
    grammarNode: "grammar.comparatives",
    vocab: {
      id: "vocab.persuasion",
      title: "Persuasion and Advertising",
      titleFa: "اقناع و تبلیغات",
      prerequisites: ["vocab.negotiations"],
    },
    words: [
      {
        slug: "consumer", w: "consumer", fa: "مصرف‌کننده", ipa: "/kənˈsjuːmə/", pos: "noun",
        def: "a person who buys goods or services",
        ex: [
          ["The consumer sees the price, not the cost.", "مصرف‌کننده قیمت را می‌بیند، نه هزینهٔ واقعی را."],
          ["Consumers compare two products before buying.", "مصرف‌کنندگان پیش از خرید دو محصول را مقایسه می‌کنند."],
        ],
        col: ["the average consumer", "consumer behaviour"],
        note: "consumer قابل‌شمارش است؛ customer کسی است که از یک فروشگاه مشخص می‌خرد.",
      },
      {
        slug: "brand", w: "brand", fa: "برند؛ نشان تجاری", ipa: "/brænd/", pos: "noun",
        def: "the name and image a company sells under",
        ex: [
          ["A strong brand does not need to shout.", "یک برند قوی لازم نیست داد بزند."],
          ["The brand changed its logo last year.", "برند سال پیش لوگویش را عوض کرد."],
        ],
        col: ["a strong brand", "build a brand"],
        note: "brand هم اسم و هم فعل است: to brand a product یعنی برند گذاشتن روی آن.",
      },
      {
        slug: "endorse", w: "endorse", fa: "تأیید کردن؛ تبلیغ کردن برای", ipa: "/ɪnˈdɔːs/", pos: "verb",
        def: "to say publicly that you support a product or person",
        ex: [
          ["A famous actor endorsed the drink.", "یک بازیگر مشهور نوشیدنی را تأیید کرد."],
          ["Companies pay athletes to endorse their shoes.", "شرکت‌ها به ورزشکاران پول می‌دهند تا کفششان را تبلیغ کنند."],
        ],
        col: ["endorse a product", "a celebrity endorsement"],
        note: "endorse با مفعول مستقیم می‌آید؛ اسم آن endorsement است.",
      },
      {
        slug: "appeal", w: "appeal", fa: "جذابیت داشتن؛ فراخوان", ipa: "/əˈpiːl/", pos: "verb",
        def: "to seem attractive or interesting to someone",
        ex: [
          ["The advert appeals to young buyers.", "این تبلیغ برای خریداران جوان جذاب است."],
          ["Its main appeal is the low price.", "جذابیت اصلی‌اش قیمت پایین است."],
        ],
        col: ["appeal to young buyers", "broad appeal"],
        note: "appeal to somebody یعنی برای کسی جذاب بودن؛ با for فرق دارد.",
      },
      {
        slug: "target", w: "target", fa: "هدف گرفتن؛ مخاطب هدف", ipa: "/ˈtɑːɡɪt/", pos: "verb",
        def: "to aim a product or message at a group",
        ex: [
          ["The campaign targets students.", "این کمپین دانشجویان را هدف گرفته است."],
          ["They target parents with small children.", "آن‌ها والدین با کودکان خردسال را هدف می‌گیرند."],
        ],
        col: ["target a group", "the target audience"],
        note: "target هم اسم است (the target) و هم فعل؛ مخاطب هدف the target audience است.",
      },
      {
        slug: "testimonial", w: "testimonial", fa: "توصیهٔ مشتری", ipa: "/ˌtestɪˈməʊniəl/", pos: "noun",
        def: "a short statement from a customer saying a product is good",
        ex: [
          ["One testimonial can sell more than a slogan.", "یک توصیهٔ مشتری می‌تواند بیشتر از یک شعار بفروشد."],
          ["The site shows testimonials from real users.", "سایت توصیه‌های کاربران واقعی را نشان می‌دهد."],
        ],
        col: ["a customer testimonial", "collect testimonials"],
        note: "testimonial قابل‌شمارش است؛ testimony شهادت رسمی در دادگاه است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "consumer", gloss: "مصرف‌کننده",
        options: ["مصرف‌کننده", "فروشنده", "سرمایه‌گذار", "تولیدکننده"],
        note: "consumer کسی است که کالا یا خدمت می‌خرد و مصرف می‌کند.",
      },
      {
        k: "mcq", word: "brand", gloss: "برند؛ نشان تجاری",
        options: ["برند؛ نشان تجاری", "قیمت", "تخفیف", "شعار"],
        note: "brand نام و تصویری است که یک شرکت با آن شناخته می‌شود.",
      },
      {
        k: "mcq", word: "testimonial", gloss: "توصیهٔ مشتری",
        options: ["توصیهٔ مشتری", "شعار تبلیغاتی", "قرارداد", "نظرسنجی"],
        note: "testimonial جملهٔ کوتاهی از یک مشتری است که محصول را تأیید می‌کند.",
      },
      {
        k: "blank", sentence: "The new model is ___ than the old one.",
        accepted: ["cheaper", "faster"], skill: "VOCABULARY",
        note: "صفت تک‌هجایی er می‌گیرد و با than می‌آید.",
      },
      {
        k: "blank", sentence: "The new advert is twice as ___ as the old one.",
        accepted: ["fast", "effective"], skill: "VOCABULARY",
        note: "بین as و as صفت ساده می‌آید، بدون er و بیشتر.",
      },
      {
        k: "recall", prompt: "مصرف‌کننده", accepted: ["consumer"],
        note: "مصرف‌کننده consumer است؛ برای خریدار یک فروشگاه customer می‌گوییم.",
      },
      {
        k: "tr", prompt: "این تبلیغ برای خریداران جوان جذاب است.",
        accepted: [
          "this advert appeals to young buyers",
          "the advert appeals to young buyers",
        ],
        note: "appeal to somebody یعنی برای او جذاب بودن.",
      },
      {
        k: "listen", text: "One testimonial can sell more than a slogan.",
        note: "به more than گوش کنید: مقایسهٔ نابرابر با more.",
      },
      {
        k: "prod", prompt: "یک ادعای تبلیغاتی با مقایسه بگو.",
        mustUse: ["twice", "as"], minWords: 8,
        accepted: ["Our new phone is twice as fast as the old one."],
        hint: "با … is twice as … as… شروع کن.",
        note: "ترتیب لنگرها: twice پیش از as می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ آژانس تبلیغات با مشتری، روی یک میز پر از طرح‌های اولیه", partner: "مدیر بازاریابی",
        prompt: "بگو تبلیغ برای خریداران جوان جذاب است.",
        question: "Who is this campaign for?",
        questionFa: "این کمپین برای چه کسانی است؟",
        accepted: [
          "The advert appeals to young buyers.",
          "It appeals to students and young buyers.",
        ],
        note: "appeal to somebody یعنی برای او جذاب بودن.",
      },
      {
        scene: "جلسهٔ آژانس تبلیغات با مشتری، روی یک میز پر از طرح‌های اولیه", partner: "مدیر بازاریابی",
        prompt: "بگو یک توصیهٔ مشتری می‌تواند بیشتر از یک شعار بفروشد.",
        question: "Why not use a slogan?",
        questionFa: "چرا شعار نه؟",
        accepted: [
          "One testimonial can sell more than a slogan.",
          "Because a testimonial is more believable than a slogan.",
        ],
        note: "more believable than ساخت مقایسهٔ صفت بلند است.",
      },
      {
        scene: "جلسهٔ آژانس تبلیغات با مشتری، روی یک میز پر از طرح‌های اولیه", partner: "مدیر بازاریابی",
        prompt: "بگو برند قوی لازم نیست داد بزند.",
        question: "Should we shout more in the copy?",
        questionFa: "باید در متن بیشتر داد بزنیم؟",
        accepted: [
          "A strong brand does not need to shout.",
          "No — a strong brand can stay quiet.",
        ],
        note: "does not need to shout یعنی لازم نیست فریاد بزند.",
      },
      {
        scene: "جلسهٔ آژانس تبلیغات با مشتری، روی یک میز پر از طرح‌های اولیه", partner: "مدیر بازاریابی",
        prompt: "بگو این مدل دو برابر مدل قدیمی سریع است.",
        question: "How does the new model compare?",
        questionFa: "مدل تازه در مقایسه چطور است؟",
        accepted: [
          "It is twice as fast as the old model.",
          "Twice as fast, and slightly cheaper.",
        ],
        note: "twice as + صفت + as ساخت مقایسهٔ برابر یا چندبرابر است.",
      },
      {
        scene: "جلسهٔ آژانس تبلیغات با مشتری، روی یک میز پر از طرح‌های اولیه", partner: "مدیر بازاریابی",
        prompt: "بگو مصرف‌کننده قیمت را می‌بیند نه هزینهٔ واقعی را.",
        question: "Why do customers still complain?",
        questionFa: "چرا مشتری‌ها هنوز شکایت می‌کنند؟",
        accepted: [
          "The consumer sees the price, not the cost.",
          "Because the consumer only sees the price.",
        ],
        note: "the consumer برای اشارهٔ کلی به مصرف‌کننده با the می‌آید.",
      },
      {
        scene: "جلسهٔ آژانس تبلیغات با مشتری، روی یک میز پر از طرح‌های اولیه", partner: "مدیر بازاریابی",
        prompt: "بگو این رایج‌ترین ادعا در تبلیغات است.",
        question: "Is this claim too strong?",
        questionFa: "این ادعا زیادی قوی نیست؟",
        accepted: [
          "It is the most common claim in advertising.",
          "Yes, it is the most common claim of all.",
        ],
        note: "the most + صفت بلند برترین را می‌سازد.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.crisis.lesson-01",
      order: 133,
      title: "Crisis Management",
      titleFa: "مدیریت بحران",
      topic: "Business",
      tip: [
        "## قاعده",
        "گذشتهٔ کامل برای کاری است که پیش از یک رویداد گذشته تمام شده بود: had + قسمت سوم.",
        "با before، after و by the time ترتیب روشن می‌شود: By the time we called, the story had spread.",
        "در نقل خبر، گذشتهٔ کامل نشان می‌دهد کدام اتفاق اول افتاده است.",
        "گذشتهٔ کامل استمراری برای کاری که پیش از رویداد ادامه داشت: We had been waiting for hours.",
        "## جدول",
        "By the time we called, the story had spread. — تا ما زنگ زدیم، خبر پخش شده بود.",
        "Nobody had prepared a statement. — هیچ‌کس بیانیه‌ای آماده نکرده بود.",
        "We had already apologised when the article appeared. — وقتی مقاله منتشر شد، ما قبلاً عذرخواهی کرده بودیم.",
        "## مثال‌ها",
        "The crisis started with one tweet. — بحران با یک توییت شروع شد.",
        "A spokesperson read the statement. — سخنگو بیانیه را خواند.",
        "Panic makes every answer worse. — وحشت هر پاسخی را بدتر می‌کند.",
        "## خطاهای رایج",
        "گذشتهٔ کامل با did نمی‌آید: had prepared ✓ / did had prepare ✗.",
        "had + قسمت سوم است، نه گذشتهٔ ساده: had went ✗ / had gone ✓.",
        "by the time با گذشتهٔ کامل جفت می‌شود، نه با حال کامل.",
      ].join("\n"),
    },
    grammarNode: "grammar.past-perfect",
    vocab: {
      id: "vocab.crisis-management",
      title: "Crisis Management",
      titleFa: "مدیریت بحران",
      prerequisites: ["vocab.project-management"],
    },
    words: [
      {
        slug: "crisis", w: "crisis", fa: "بحران", ipa: "/ˈkraɪsɪs/", pos: "noun",
        def: "a time when a serious problem must be solved fast",
        ex: [
          ["The crisis started with one tweet.", "بحران با یک توییت شروع شد."],
          ["The crisis was over in a week.", "بحران در یک هفته تمام شد."],
        ],
        col: ["handle a crisis", "a crisis meeting"],
        note: "crisis مفرد است و جمع آن crises با e نوشته می‌شود.",
      },
      {
        slug: "spokesperson", w: "spokesperson", fa: "سخنگو", ipa: "/ˈspəʊkspɜːsn/", pos: "noun",
        def: "the person who speaks officially for an organisation",
        ex: [
          ["A spokesperson read the statement.", "سخنگو بیانیه را خواند."],
          ["The spokesperson refused to give a name.", "سخنگو از گفتن نام خودداری کرد."],
        ],
        col: ["a company spokesperson", "speak through a spokesperson"],
        note: "spokesperson شکل بی‌طرف است؛ spokesman و spokeswoman هم به کار می‌روند.",
      },
      {
        slug: "response", w: "response", fa: "پاسخ؛ واکنش", ipa: "/rɪˈspɒns/", pos: "noun",
        def: "what you say or do after something happens",
        ex: [
          ["The response came four hours too late.", "واکنش چهار ساعت دیر آمد."],
          ["Their response to the story was calm.", "واکنش آن‌ها به خبر آرام بود."],
        ],
        col: ["a quick response", "in response to something"],
        note: "response قابل‌شمارش است: a response / two responses؛ فعل آن respond است.",
      },
      {
        slug: "damage", w: "damage", fa: "آسیب؛ خسارت", ipa: "/ˈdæmɪdʒ/", pos: "noun",
        def: "harm done to people, property or trust",
        ex: [
          ["The damage was mostly to trust.", "آسیب بیشتر به اعتماد بود."],
          ["Late replies do real damage.", "پاسخ‌های دیر واقعاً آسیب می‌زنند."],
        ],
        col: ["do damage", "repair the damage"],
        note: "damage غیرقابل‌شمارش است: a lot of damage ✓ / many damages ✗.",
      },
      {
        slug: "panic", w: "panic", fa: "وحشت‌زدگی", ipa: "/ˈpænɪk/", pos: "noun",
        def: "sudden strong fear that stops clear thinking",
        ex: [
          ["Panic makes every answer worse.", "وحشت هر پاسخی را بدتر می‌کند."],
          ["There was no panic in the room.", "در اتاق هیچ وحشتی نبود."],
        ],
        col: ["in a panic", "cause panic"],
        note: "panic غیرقابل‌شمارش است؛ فعل آن به شکل panicked و panicking نوشته می‌شود.",
      },
      {
        slug: "reassure", w: "reassure", fa: "دلگرم کردن", ipa: "/ˌriːəˈʃʊə/", pos: "verb",
        def: "to make someone feel less worried",
        ex: [
          ["The manager reassured the staff.", "مدیر کارکنان را دلگرم کرد."],
          ["We need to reassure customers today.", "امروز باید به مشتری‌ها اطمینان بدهیم."],
        ],
        col: ["reassure customers", "a reassuring message"],
        note: "reassure با مفعول مستقیم می‌آید: reassure the staff ✓ / reassure to the staff ✗.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "crisis", gloss: "بحران",
        options: ["بحران", "جلسه", "بیانیه", "خسارت"],
        note: "crisis زمانی است که یک مشکل جدی باید سریع حل شود.",
      },
      {
        k: "mcq", word: "spokesperson", gloss: "سخنگو",
        options: ["سخنگو", "مدیرعامل", "روزنامه‌نگار", "مشتری"],
        note: "spokesperson به‌طور رسمی از طرف سازمان حرف می‌زند.",
      },
      {
        k: "mcq", word: "reassure", gloss: "دلگرم کردن",
        options: ["دلگرم کردن", "تهدید کردن", "انکار کردن", "اطلاع دادن"],
        note: "reassure یعنی نگرانی کسی را کم کردن.",
      },
      {
        k: "blank", sentence: "By the time we called, the story ___ spread.",
        accepted: ["had", "had already"], skill: "VOCABULARY",
        note: "by the time با گذشتهٔ کامل می‌آید: had spread.",
      },
      {
        k: "blank", sentence: "Nobody ___ prepared a statement before the call.",
        accepted: ["had", "has"], skill: "VOCABULARY",
        note: "had بر گذشتهٔ پیش‌تر تأکید دارد؛ has نتیجه را به حال وصل می‌کند.",
      },
      {
        k: "recall", prompt: "سخنگو", accepted: ["spokesperson"],
        note: "سخنگو spokesperson است و شکل بی‌طرف همین واژه است.",
      },
      {
        k: "tr", prompt: "تا ما زنگ زدیم، خبر پخش شده بود.",
        accepted: [
          "by the time we called, the story had spread",
          "the story had spread by the time we called",
        ],
        note: "گذشتهٔ کامل had spread نشان می‌دهد خبر پیش از تماس پخش شده بود.",
      },
      {
        k: "listen", text: "Nobody had prepared a statement.",
        note: "به had prepared گوش کنید: گذشتهٔ کامل، نه گذشتهٔ ساده.",
      },
      {
        k: "prod", prompt: "بگو تیم پیش از پخش خبر چه کرده بود.",
        mustUse: ["had", "before"], minWords: 9,
        accepted: ["The team had prepared a response before the story spread."],
        hint: "با The team had… before… جمله را بساز.",
        note: "ترتیب لنگرها: had پیش از before می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "اتاق بحران شرکت، دو ساعت بعد از انتشار یک خبر", partner: "مدیر روابط عمومی",
        prompt: "بگو تا ما زنگ زدیم خبر پخش شده بود.",
        question: "When did you find out?",
        questionFa: "کِی خبردار شدید؟",
        accepted: [
          "By the time we called, the story had spread.",
          "We found out after the story had spread.",
        ],
        note: "by the time + گذشتهٔ کامل ترتیب دو رویداد را نشان می‌دهد.",
      },
      {
        scene: "اتاق بحران شرکت، دو ساعت بعد از انتشار یک خبر", partner: "مدیر روابط عمومی",
        prompt: "بگو هیچ‌کس بیانیه‌ای آماده نکرده بود.",
        question: "Was there a statement ready?",
        questionFa: "بیانیه‌ای آماده بود؟",
        accepted: [
          "No, nobody had prepared a statement.",
          "Nothing had been prepared before the call.",
        ],
        note: "nobody + had prepared ساخت گذشتهٔ کامل منفی است.",
      },
      {
        scene: "اتاق بحران شرکت، دو ساعت بعد از انتشار یک خبر", partner: "مدیر روابط عمومی",
        prompt: "بگو واکنش چهار ساعت دیر آمد.",
        question: "How bad was the timing?",
        questionFa: "زمان‌بندی چقدر بد بود؟",
        accepted: [
          "The response came four hours too late.",
          "We were four hours late with the response.",
        ],
        note: "the response قابل‌شمارش است؛ too late یعنی بیش از حد دیر.",
      },
      {
        scene: "اتاق بحران شرکت، دو ساعت بعد از انتشار یک خبر", partner: "مدیر روابط عمومی",
        prompt: "بگو آسیب بیشتر به اعتماد بوده است.",
        question: "What is the real damage?",
        questionFa: "آسیب واقعی چیست؟",
        accepted: [
          "The damage was mostly to trust.",
          "Most of the damage is to trust, not sales.",
        ],
        note: "damage غیرقابل‌شمارش است: the damage was ✓ / the damages were ✗.",
      },
      {
        scene: "اتاق بحران شرکت، دو ساعت بعد از انتشار یک خبر", partner: "مدیر روابط عمومی",
        prompt: "بگو وحشت هر پاسخی را بدتر می‌کند.",
        question: "Should we answer everything now?",
        questionFa: "همه چیز را همین حالا جواب بدهیم؟",
        accepted: [
          "Panic makes every answer worse.",
          "No, panic makes every answer worse.",
        ],
        note: "panic غیرقابل‌شمارش است، پس فعل مفرد makes می‌گیرد.",
      },
      {
        scene: "اتاق بحران شرکت، دو ساعت بعد از انتشار یک خبر", partner: "مدیر روابط عمومی",
        prompt: "بگو امروز باید به مشتری‌ها اطمینان بدهیم.",
        question: "What do we do next?",
        questionFa: "قدم بعدی چیست؟",
        accepted: [
          "We need to reassure customers today.",
          "First we should reassure our customers.",
        ],
        note: "reassure با مفعول مستقیم می‌آید، نه با to.",
      },
    ],
  },
];
