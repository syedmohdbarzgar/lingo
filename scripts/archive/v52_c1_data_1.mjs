// v52 content, part 1 — C1 topics 20 and 21: research ethics and diversity &
// inclusion. Read by `v52_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.research-ethics.lesson-01",
      order: 160,
      title: "Research Ethics",
      titleFa: "اخلاق در پژوهش",
      topic: "Science",
      tip: [
        "## قاعده",
        "در متن اخلاق پژوهش، قاعده‌ها را با مجهول + وجهی می‌نویسند، چون قاعده بر همه لازم است نه بر یک نفر: Participants must be told the purpose.",
        "ساختار: must/should/can + be + قسمت سوم فعل — Data should be anonymised before analysis.",
        "## جدول",
        "Participants must be told the purpose of the study. — باید هدف پژوهش به شرکت‌کنندگان گفته شود.",
        "Data should be anonymised before analysis. — داده‌ها باید پیش از تحلیل ناشناس شوند.",
        "Consent must be obtained in writing. — رضایت باید کتبی گرفته شود.",
        "## مثال‌ها",
        "Anonymity protects the people who took part. — ناشناس‌بودن از شرکت‌کنندگان محافظت می‌کند.",
        "Plagiarism ends a research career quickly. — سرقت علمی به‌سرعت به کار پژوهشی پایان می‌دهد.",
        "Misconduct is reported to the committee, not to the funder alone. — تخلف پژوهشی به کمیته گزارش می‌شود، نه فقط به تأمین‌کنندهٔ مالی.",
        "## خطاهای رایج",
        "پس از وجهی، شکل مجهول با be می‌آید: must be anonymised ✓ / must anonymised ✗.",
        "must be told یعنی «باید گفته شود»، نه «باید بگوید».",
        "confidentiality غیرقابل‌شمارش است: confidentiality is، نه are.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-with-modals",
    vocab: {
      id: "vocab.research-ethics",
      title: "Research Ethics",
      titleFa: "اخلاق در پژوهش",
      prerequisites: ["vocab.research-methodology"],
    },
    words: [
      {
        slug: "anonymity", w: "anonymity", fa: "ناشناس‌بودن", ipa: "/ˌænəˈnɪməti/", pos: "noun",
        def: "keeping a person's identity hidden",
        ex: [
          ["Anonymity protects the people who took part.", "ناشناس‌بودن از کسانی که شرکت کردند محافظت می‌کند."],
          ["The survey promises anonymity to every respondent.", "پیمایش به هر پاسخ‌دهنده ناشناس‌بودن را وعده می‌دهد."],
        ],
        col: ["guarantee anonymity", "preserve anonymity"],
        note: "anonymity غیرقابل‌شمارش است؛ صفت آن anonymous.",
      },
      {
        slug: "informed-consent", w: "informed consent", fa: "رضایت آگاهانه", ipa: "/ɪnˌfɔːmd kənˈsent/", pos: "noun",
        def: "agreement given after someone understands the study",
        ex: [
          ["Informed consent was obtained in writing.", "رضایت آگاهانه کتبی گرفته شد."],
          ["Every participant signed the informed consent form.", "هر شرکت‌کننده فرم رضایت آگاهانه را امضا کرد."],
        ],
        col: ["obtain informed consent", "withdraw consent"],
        note: "عبارت دوکلمه‌ای است و با فعل obtain می‌آید: obtain informed consent.",
      },
      {
        slug: "plagiarism", w: "plagiarism", fa: "سرقت علمی", ipa: "/ˈpleɪdʒərɪzəm/", pos: "noun",
        def: "using someone else's work as your own",
        ex: [
          ["Plagiarism ends a research career quickly.", "سرقت علمی به‌سرعت به کار پژوهشی پایان می‌دهد."],
          ["One paragraph of plagiarism cost the author her paper.", "یک پاراگراف سرقت علمی مقاله را از نویسنده گرفت."],
        ],
        col: ["check for plagiarism", "accuse somebody of plagiarism"],
        note: "plagiarism غیرقابل‌شمارش است؛ فعل آن plagiarise.",
      },
      {
        slug: "fabrication", w: "fabrication", fa: "جعل داده", ipa: "/ˌfæbrɪˈkeɪʃn/", pos: "noun",
        def: "inventing results that were never collected",
        ex: [
          ["Fabrication is the most serious kind of misconduct.", "جعل داده جدی‌ترین نوع تخلف پژوهشی است."],
          ["The committee found fabrication in two tables.", "کمیته در دو جدول جعل داده یافت."],
        ],
        col: ["data fabrication", "accusations of fabrication"],
        note: "fabrication غیرقابل‌شمارش است؛ فعل آن fabricate به معنی «ساختن/جعل کردن».",
      },
      {
        slug: "confidentiality", w: "confidentiality", fa: "محرمانگی", ipa: "/ˌkɒnfɪˌdenʃiˈæləti/", pos: "noun",
        def: "keeping private information private",
        ex: [
          ["Confidentiality matters more than the sample size.", "محرمانگی از حجم نمونه مهم‌تر است."],
          ["The team broke confidentiality by naming a school.", "تیم با نام‌بردن از یک مدرسه محرمانگی را شکست."],
        ],
        col: ["breach confidentiality", "a confidentiality agreement"],
        note: "confidentiality غیرقابل‌شمارش است؛ عبارت رایج breach confidentiality.",
      },
      {
        slug: "misconduct", w: "misconduct", fa: "تخلف", ipa: "/ˌmɪsˈkɒndʌkt/", pos: "noun",
        def: "bad behaviour by someone in a trusted role",
        ex: [
          ["Misconduct is reported to the committee, not to the funder alone.", "تخلف پژوهشی به کمیته گزارش می‌شود، نه فقط به تأمین‌کنندهٔ مالی."],
          ["One case of misconduct changed the whole policy.", "یک مورد تخلف تمام سیاست را تغییر داد."],
        ],
        col: ["research misconduct", "report misconduct"],
        note: "misconduct غیرقابل‌شمارش است؛ برای یک مورد می‌گویند a case of misconduct.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "anonymity", gloss: "ناشناس‌بودن",
        options: ["ناشناس‌بودن", "محرمانگی", "رضایت آگاهانه", "جعل داده"],
        note: "anonymity یعنی هویت فرد پنهان بماند؛ confidentiality دربارهٔ اطلاعات است.",
      },
      {
        k: "mcq", word: "plagiarism", gloss: "سرقت علمی",
        options: ["سرقت علمی", "تخلف مالی", "بازبینی همتا", "خطای نمونه‌گیری"],
        note: "plagiarism یعنی کار دیگری را به نام خود آوردن.",
      },
      {
        k: "mcq", word: "misconduct", gloss: "تخلف",
        options: ["تخلف", "محرمانگی", "پایایی", "بازبینی"],
        note: "misconduct رفتار نادرست کسی است که به او اعتماد شده است.",
      },
      {
        k: "blank", sentence: "Data ___ be anonymised before anyone reads it.",
        accepted: ["must", "should"], skill: "VOCABULARY",
        note: "must و should هر دو با be + قسمت سوم فعل، شکل مجهول قاعده‌ای می‌سازند.",
      },
      {
        k: "blank", sentence: "All data should be ___ before analysis.",
        accepted: ["anonymised", "anonymized"], skill: "VOCABULARY",
        note: "پس از should be قسمت سوم فعل می‌آید: anonymised (املای بریتانیایی) یا anonymized.",
      },
      {
        k: "recall", prompt: "ناشناس‌بودن", accepted: ["anonymity"],
        note: "ناشناس‌بودن anonymity است و صفت آن anonymous.",
      },
      {
        k: "tr", prompt: "داده‌ها باید پیش از تحلیل ناشناس شوند.",
        accepted: [
          "the data should be anonymised before analysis",
          "the data must be anonymised before analysis",
          "data should be anonymized before analysis",
        ],
        note: "should be + قسمت سوم فعل: ناشناس شدن داده‌ها.",
      },
      {
        k: "listen", text: "Participants must be told the purpose of the study.",
        note: "به must be told گوش کنید: مجهول با وجهی.",
      },
      {
        k: "prod", prompt: "بگو داده‌ها باید پیش از تحلیل ناشناس شوند.",
        mustUse: ["must", "be"], minWords: 6,
        accepted: ["The data must be anonymised before analysis."],
        hint: "با The data must be… شروع کن.",
        note: "ترتیب لنگرها: must پیش از be.",
      },
    ],
    dialogue: [
      {
        scene: "کمیتهٔ اخلاق پژوهش، پیش از شروع مطالعه", partner: "دبیر کمیتهٔ اخلاق",
        prompt: "بگو باید هدف پژوهش به شرکت‌کنندگان گفته شود.",
        question: "How do we open the session?",
        questionFa: "جلسه را چطور شروع کنیم؟",
        accepted: [
          "Participants must be told the purpose of the study.",
          "They must be told the purpose of the study.",
        ],
        note: "must be told: مجهول با وجهی؛ فاعل عمل‌کننده مهم نیست.",
      },
      {
        scene: "کمیتهٔ اخلاق پژوهش، پیش از شروع مطالعه", partner: "دبیر کمیتهٔ اخلاق",
        prompt: "بگو رضایت آگاهانه کتبی گرفته شد.",
        question: "Is verbal consent enough?",
        questionFa: "رضایت شفاهی کافی است؟",
        accepted: [
          "Informed consent was obtained in writing.",
          "No, informed consent was obtained in writing.",
        ],
        note: "informed consent کلمه‌به‌کلمه معنی «رضایت آگاهانه» می‌دهد.",
      },
      {
        scene: "کمیتهٔ اخلاق پژوهش، پیش از شروع مطالعه", partner: "دبیر کمیتهٔ اخلاق",
        prompt: "بگو داده‌ها باید پیش از تحلیل ناشناس شوند.",
        question: "When do we remove the names?",
        questionFa: "چه زمانی نام‌ها را حذف کنیم؟",
        accepted: [
          "Data should be anonymised before analysis.",
          "The data should be anonymised before analysis.",
        ],
        note: "before analysis: پیش از تحلیل، نه پس از آن.",
      },
      {
        scene: "کمیتهٔ اخلاق پژوهش، پیش از شروع مطالعه", partner: "دبیر کمیتهٔ اخلاق",
        prompt: "بگو محرمانگی از حجم نمونه مهم‌تر است.",
        question: "Is a bigger sample worth it?",
        questionFa: "نمونهٔ بزرگ‌تر ارزشش را دارد؟",
        accepted: [
          "Confidentiality matters more than the sample size.",
          "Confidentiality is more important than the sample size.",
        ],
        note: "matters more than: مهم‌تر بودن از.",
      },
      {
        scene: "کمیتهٔ اخلاق پژوهش، پیش از شروع مطالعه", partner: "دبیر کمیتهٔ اخلاق",
        prompt: "بگو جعل داده جدی‌ترین نوع تخلف پژوهشی است.",
        question: "What worries you most?",
        questionFa: "بیش از همه چه چیزی نگرانتان می‌کند؟",
        accepted: [
          "Fabrication is the most serious kind of misconduct.",
          "Fabrication worries me most.",
        ],
        note: "the most serious kind: صفت عالی با the.",
      },
      {
        scene: "کمیتهٔ اخلاق پژوهش، پیش از شروع مطالعه", partner: "دبیر کمیتهٔ اخلاق",
        prompt: "بگو تیم با نام‌بردن از یک مدرسه محرمانگی را شکست.",
        question: "Why did the last report fail?",
        questionFa: "چرا گزارش قبلی رد شد؟",
        accepted: [
          "The team broke confidentiality by naming a school.",
          "It broke confidentiality by naming a school.",
        ],
        note: "break confidentiality: نقض محرمانگی.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.diversity-inclusion.lesson-01",
      order: 161,
      title: "Diversity and Inclusion",
      titleFa: "تنوع و شمول",
      topic: "Work",
      tip: [
        "## قاعده",
        "برای تعریف یک گروه از بند موصولی که/which استفاده کن: A policy that excludes part-time staff is not inclusive.",
        "بند غیرتعریفی با کاما می‌آید و who/which می‌گیرد: The manager, who joined last year, runs the network.",
        "در جملهٔ غیرتعریفی that به کار نمی‌رود: The manager, who joined… ✓ / The manager, that joined… ✗.",
        "## جدول",
        "A policy that excludes part-time staff is not inclusive. — سیاستی که کارکنان پاره‌وقت را حذف می‌کند فراگیر نیست.",
        "The manager, who joined last year, runs the network. — مدیر که سال پیش آمد، شبکه را می‌گرداند.",
        "Teams that listen to every voice make better decisions. — تیم‌هایی که به همهٔ صداها گوش می‌دهند بهتر تصمیم می‌گیرند.",
        "## مثال‌ها",
        "Representation on the board is still low. — نمایندگی در هیئت‌مدیره هنوز کم است.",
        "One accommodation changed her whole week. — یک سازگاری کل هفتهٔ او را عوض کرد.",
        "Allyship means speaking up when you are not the target. — همراهی یعنی وقتی خودت هدف نیستی هم صدا بلند کنی.",
        "## خطاهای رایج",
        "پس از اسم مفرد، فعل مفرد می‌آید: A policy that excludes… is ✓.",
        "inclusion غیرقابل‌شمارش است؛ inclusive صفت است، نه اسم.",
        "belonging با حرف اضافهٔ of می‌آید: a sense of belonging.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.diversity-inclusion",
      title: "Diversity and Inclusion",
      titleFa: "تنوع و شمول",
      prerequisites: ["vocab.organizational-culture"],
    },
    words: [
      {
        slug: "inclusion", w: "inclusion", fa: "شمول", ipa: "/ɪnˈkluːʒn/", pos: "noun",
        def: "making sure everyone can take part",
        ex: [
          ["Inclusion is a practice, not a statement.", "شمول یک عمل است، نه یک بیانیه."],
          ["Inclusion in meetings starts with the agenda.", "شمول در جلسه‌ها از دستور جلسه شروع می‌شود."],
        ],
        col: ["a culture of inclusion", "inclusion policy"],
        note: "inclusion غیرقابل‌شمارش است؛ صفت آن inclusive.",
      },
      {
        slug: "representation", w: "representation", fa: "نمایندگی", ipa: "/ˌreprɪzenˈteɪʃn/", pos: "noun",
        def: "how far a group is reflected in a body",
        ex: [
          ["Representation on the board is still low.", "نمایندگی در هیئت‌مدیره هنوز کم است."],
          ["Representation improved after the hiring change.", "نمایندگی پس از تغییر شیوهٔ استخدام بهتر شد."],
        ],
        col: ["under-representation", "fair representation"],
        note: "representation غیرقابل‌شمارش است؛ فعل آن represent.",
      },
      {
        slug: "accommodation", w: "accommodation", fa: "سازگاری (تسهیل)", ipa: "/əˌkɒməˈdeɪʃn/", pos: "noun",
        def: "a change that lets someone work on equal terms",
        ex: [
          ["One accommodation changed her whole week.", "یک سازگاری کل هفتهٔ او را عوض کرد."],
          ["A quiet room is a cheap accommodation.", "اتاق بی‌سر‌و‌صدا یک سازگاری ارزان است."],
        ],
        col: ["a reasonable accommodation", "request an accommodation"],
        note: "در این معنا «تسهیل محل کار» است؛ در سفر به معنی «اقامت» هم می‌آید.",
      },
      {
        slug: "microaggression", w: "microaggression", fa: "توهین خُرد", ipa: "/ˌmaɪkrəʊəˈɡreʃn/", pos: "noun",
        def: "a small, repeated remark that excludes someone",
        ex: [
          ["A microaggression is small only to the person who made it.", "توهین خُرد فقط برای گوینده کوچک است."],
          ["She logged every microaggression for a month.", "او یک ماه هر توهین خُرد را ثبت کرد."],
        ],
        col: ["everyday microaggressions", "call out a microaggression"],
        note: "microaggression قابل‌شمارش است و بیشتر جمع می‌آید.",
      },
      {
        slug: "allyship", w: "allyship", fa: "همراهی (حمایت از گروه کم‌بهره)", ipa: "/ˈælaɪʃɪp/", pos: "noun",
        def: "support given by someone who is not in the affected group",
        ex: [
          ["Allyship means speaking up when you are not the target.", "همراهی یعنی وقتی خودت هدف نیستی هم صدا بلند کنی."],
          ["Real allyship costs a meeting, not a hashtag.", "همراهی واقعی هزینه‌اش یک جلسه است، نه یک هشتگ."],
        ],
        col: ["practise allyship", "performative allyship"],
        note: "allyship غیرقابل‌شمارش است؛ اسم شخص ally.",
      },
      {
        slug: "belonging", w: "belonging", fa: "تعلق", ipa: "/bɪˈlɒŋɪŋ/", pos: "noun",
        def: "the feeling of being accepted in a group",
        ex: [
          ["Belonging is measured in the yearly survey.", "تعلق در پیمایش سالانه سنجیده می‌شود."],
          ["Belonging rose when the team changed its ritual.", "تعلق وقتی تیم آیینش را عوض کرد بالا رفت."],
        ],
        col: ["a sense of belonging", "belonging survey"],
        note: "belonging غیرقابل‌شمارش است؛ عبارت رایج a sense of belonging.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "inclusion", gloss: "شمول",
        options: ["شمول", "نمایندگی", "تعلق", "همراهی"],
        note: "inclusion یعنی فراهم‌کردن امکان مشارکت برای همه.",
      },
      {
        k: "mcq", word: "microaggression", gloss: "توهین خُرد",
        options: ["توهین خُرد", "سازگاری", "سیاست استخدام", "بازخورد سالانه"],
        note: "microaggression جملهٔ کوچک و تکراری است که کسی را بیرون می‌گذارد.",
      },
      {
        k: "mcq", word: "accommodation", gloss: "سازگاری (تسهیل)",
        options: ["سازگاری (تسهیل)", "نمایندگی", "تعلق", "همراهی"],
        note: "accommodation تغییری است که مشارکت برابر را ممکن می‌کند.",
      },
      {
        k: "blank", sentence: "A policy ___ excludes part-time staff is not inclusive.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "بند موصولی تعریفی با that یا which می‌آید.",
      },
      {
        k: "blank", sentence: "The room ___ we booked has step-free access.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "برای چیز و در جملهٔ تعریفی، that و which هر دو درست‌اند.",
      },
      {
        k: "recall", prompt: "شمول", accepted: ["inclusion"],
        note: "شمول inclusion است و صفت آن inclusive.",
      },
      {
        k: "tr", prompt: "سیاستی که کارکنان پاره‌وقت را حذف می‌کند فراگیر نیست.",
        accepted: [
          "a policy that excludes part-time staff is not inclusive",
          "a policy which excludes part-time staff is not inclusive",
        ],
        note: "بند موصولی تعریفی با that/which و فعل مطابق با اسم مفرد.",
      },
      {
        k: "listen", text: "Teams that listen to every voice make better decisions.",
        note: "به that listen گوش کنید: فعل بند موصولی با teams مطابق است.",
      },
      {
        k: "prod", prompt: "بگو تیمی که به همهٔ صداها گوش می‌دهد بهتر تصمیم می‌گیرد.",
        mustUse: ["that", "listens"], minWords: 6,
        accepted: ["A team that listens to every voice makes better decisions."],
        hint: "با A team that listens… شروع کن.",
        note: "ترتیب لنگرها: that پیش از listens.",
      },
    ],
    dialogue: [
      {
        scene: "بازبینی سالانهٔ سیاست تنوع و شمول", partner: "مسئول تنوع و شمول",
        prompt: "بگو سیاستی که کارکنان پاره‌وقت را حذف می‌کند فراگیر نیست.",
        question: "What is wrong with the old policy?",
        questionFa: "سیاست قدیمی چه اشکالی دارد؟",
        accepted: [
          "A policy that excludes part-time staff is not inclusive.",
          "The policy that excludes part-time staff is not inclusive.",
        ],
        note: "that excludes: بند موصولی تعریفی برای سیاست.",
      },
      {
        scene: "بازبینی سالانهٔ سیاست تنوع و شمول", partner: "مسئول تنوع و شمول",
        prompt: "بگو نمایندگی در هیئت‌مدیره هنوز کم است.",
        question: "How is the board doing?",
        questionFa: "هیئت‌مدیره چطور است؟",
        accepted: [
          "Representation on the board is still low.",
          "It is still low, representation on the board.",
        ],
        note: "on the board: در هیئت‌مدیره.",
      },
      {
        scene: "بازبینی سالانهٔ سیاست تنوع و شمول", partner: "مسئول تنوع و شمول",
        prompt: "بگو یک سازگاری کل هفتهٔ او را عوض کرد.",
        question: "Did the quiet room help?",
        questionFa: "اتاق بی‌سر‌و‌صدا کمک کرد؟",
        accepted: [
          "One accommodation changed her whole week.",
          "Yes, one accommodation changed her whole week.",
        ],
        note: "accommodation مفرد است: changed.",
      },
      {
        scene: "بازبینی سالانهٔ سیاست تنوع و شمول", partner: "مسئول تنوع و شمول",
        prompt: "بگو تعلق در پیمایش سالانه سنجیده می‌شود.",
        question: "How do we track inclusion?",
        questionFa: "شمول را چطور می‌سنجیم؟",
        accepted: [
          "Belonging is measured in the yearly survey.",
          "We measure belonging in the yearly survey.",
        ],
        note: "belonging با is می‌آید: غیرقابل‌شمارش.",
      },
      {
        scene: "بازبینی سالانهٔ سیاست تنوع و شمول", partner: "مسئول تنوع و شمول",
        prompt: "بگو همراهی یعنی وقتی خودت هدف نیستی هم صدا بلند کنی.",
        question: "What does allyship mean here?",
        questionFa: "همراهی اینجا یعنی چه؟",
        accepted: [
          "Allyship means speaking up when you are not the target.",
          "It means speaking up when you are not the target.",
        ],
        note: "mean + فعل ing: یعنی چه کاری انجام دادن.",
      },
      {
        scene: "بازبینی سالانهٔ سیاست تنوع و شمول", partner: "مسئول تنوع و شمول",
        prompt: "بگو مدیر که سال پیش آمد شبکه را می‌گرداند.",
        question: "Who runs the staff network?",
        questionFa: "چه کسی شبکهٔ کارکنان را می‌گرداند؟",
        accepted: [
          "The manager, who joined last year, runs the network.",
          "The manager who joined last year runs the network.",
        ],
        note: "بند غیرتعریفی با کاما و who؛ در نسخهٔ تعریفی کاما حذف می‌شود.",
      },
    ],
  },
];
