// v50 content, part 1 — C1 topics 10 and 11: statistical literacy and critical
// media analysis. Read by `v50_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.statistics.lesson-01",
      order: 152,
      title: "Statistical Literacy",
      titleFa: "سواد آماری",
      topic: "Science",
      tip: [
        "## قاعده",
        "data در انگلیسی آکادمیک جمع است: The data show a small effect.",
        "اما در گفتار روزمره data اغلب مفرد گرفته می‌شود؛ در این درس شکل جمع را می‌آموزیم.",
        "با neither of و each of فعل مفرد می‌آید: Neither of the samples was significant.",
        "## جدول",
        "The data show that the effect is small. — داده‌ها نشان می‌دهند که اثر کوچک است.",
        "Neither of the samples was significant. — هیچ‌کدام از نمونه‌ها معنادار نبود.",
        "A number of studies suggest the opposite. — شماری از پژوهش‌ها خلاف آن را نشان می‌دهند.",
        "## مثال‌ها",
        "Correlation is not causation. — همبستگی به معنی علیت نیست.",
        "One outlier pulled the average up. — یک دادهٔ پرت میانگین را بالا کشید.",
        "Use the median when the data are skewed. — وقتی داده چولگی دارد از میانه استفاده کن.",
        "## خطاهای رایج",
        "the data shows در نوشتار آکادمیک ضعیف شمرده می‌شود؛ the data show ترجیح دارد.",
        "significant در آمار یعنی «معنادار»، نه «مهم»؛ برای اهمیت از important استفاده کن.",
        "median و mean را با هم عوض نکن: median میانه و mean میانگین است.",
      ].join("\n"),
    },
    grammarNode: "grammar.subject-verb-agreement",
    vocab: {
      id: "vocab.statistical-literacy",
      title: "Statistical Literacy",
      titleFa: "سواد آماری",
      prerequisites: ["vocab.charts"],
    },
    words: [
      {
        slug: "correlation", w: "correlation", fa: "همبستگی", ipa: "/ˌkɒrəˈleɪʃn/", pos: "noun",
        def: "a relationship between two things that change together",
        ex: [
          ["Correlation is not causation.", "همبستگی به معنی علیت نیست."],
          ["The correlation was strong but the sample was small.", "همبستگی قوی بود ولی نمونه کوچک بود."],
        ],
        col: ["a strong correlation", "correlation between two variables"],
        note: "correlation با between می‌آید؛ فعل آن correlate.",
      },
      {
        slug: "outlier", w: "outlier", fa: "دادهٔ پرت", ipa: "/ˈaʊtlaɪə/", pos: "noun",
        def: "a value far away from the rest of the data",
        ex: [
          ["One outlier pulled the average up.", "یک دادهٔ پرت میانگین را بالا کشید."],
          ["The outlier was a typing error.", "دادهٔ پرت یک خطای تایپ بود."],
        ],
        col: ["remove an outlier", "a single outlier"],
        note: "outlier قابل‌شمارش است؛ در آمار به داده‌ای دور از بقیه گفته می‌شود.",
      },
      {
        slug: "significance", w: "significance", fa: "معناداری", ipa: "/sɪɡˈnɪfɪkəns/", pos: "noun",
        def: "the likelihood that a result is not due to chance",
        ex: [
          ["Nobody checked the significance of the result.", "هیچ‌کس معناداری نتیجه را بررسی نکرد."],
          ["Statistical significance is not practical importance.", "معناداری آماری همان اهمیت عملی نیست."],
        ],
        col: ["statistical significance", "test for significance"],
        note: "significance غیرقابل‌شمارش است؛ صفت آن significant در آمار یعنی «معنادار».",
      },
      {
        slug: "variance", w: "variance", fa: "پراکندگی", ipa: "/ˈveəriəns/", pos: "noun",
        def: "how far values spread around the average",
        ex: [
          ["Variance across the three sites was huge.", "پراکندگی میان سه سایت بسیار زیاد بود."],
          ["We report variance, not just the mean.", "ما پراکندگی را هم گزارش می‌کنیم، نه فقط میانگین را."],
        ],
        col: ["high variance", "explain the variance"],
        note: "variance غیرقابل‌شمارش است؛ صفت آن variable و فعل آن vary.",
      },
      {
        slug: "distribution", w: "distribution", fa: "توزیع", ipa: "/ˌdɪstrɪˈbjuːʃn/", pos: "noun",
        def: "the way values are spread across a range",
        ex: [
          ["The distribution is skewed to the right.", "توزیع به سمت راست چولگی دارد."],
          ["Income distribution changed little in a decade.", "توزیع درآمد در یک دهه کم تغییر کرد."],
        ],
        col: ["a normal distribution", "the distribution of values"],
        note: "distribution قابل‌شمارش است؛ فعل آن distribute.",
      },
      {
        slug: "median", w: "median", fa: "میانه", ipa: "/ˈmiːdiən/", pos: "noun",
        def: "the middle value when the data are ordered",
        ex: [
          ["The median is lower than the mean.", "میانه از میانگین کمتر است."],
          ["Use the median when the data are skewed.", "وقتی داده چولگی دارد از میانه استفاده کن."],
        ],
        col: ["the median income", "the median value"],
        note: "median هم اسم است و هم صفت: median income یعنی درآمد میانه.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "correlation", gloss: "همبستگی",
        options: ["همبستگی", "علیت", "میانگین", "نمونه"],
        note: "correlation رابطهٔ دو چیزی است که با هم تغییر می‌کنند، نه رابطهٔ علت و معلول.",
      },
      {
        k: "mcq", word: "outlier", gloss: "دادهٔ پرت",
        options: ["دادهٔ پرت", "میانه", "خطای نمونه", "نمودار"],
        note: "outlier مقداری است که از بقیهٔ داده‌ها دور افتاده است.",
      },
      {
        k: "mcq", word: "significance", gloss: "معناداری",
        options: ["معناداری", "دقت", "سرعت", "حجم"],
        note: "significance احتمال آن است که نتیجه تصادفی نباشد.",
      },
      {
        k: "blank", sentence: "The data ___ that the effect is small.",
        accepted: ["show", "suggest"], skill: "VOCABULARY",
        note: "data در نوشتار آکادمیک جمع است، پس show/suggest می‌گیرد.",
      },
      {
        k: "blank", sentence: "Neither of the samples ___ significant.",
        accepted: ["was", "is"], skill: "VOCABULARY",
        note: "neither of با اسم جمع هم فعل مفرد می‌گیرد: neither was.",
      },
      {
        k: "recall", prompt: "میانه", accepted: ["median"],
        note: "میانه median است و میانگین mean.",
      },
      {
        k: "tr", prompt: "همبستگی به معنی علیت نیست.",
        accepted: [
          "correlation is not causation",
          "correlation does not mean causation",
        ],
        note: "عبارت ثابت: correlation is not causation.",
      },
      {
        k: "listen", text: "The data show that the effect is small.",
        note: "به data show گوش کنید: فعل جمع، نه shows.",
      },
      {
        k: "prod", prompt: "بگو داده‌ها نشان می‌دهند که اثر کوچک است.",
        mustUse: ["data", "show"], minWords: 6,
        accepted: ["The data show that the effect is small."],
        hint: "با The data show that… شروع کن.",
        note: "ترتیب لنگرها: data پیش از show می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ بررسی یک گزارش آماری", partner: "آمارگر ارشد",
        prompt: "بگو همبستگی به معنی علیت نیست.",
        question: "So the policy caused the change?",
        questionFa: "پس سیاست باعث آن تغییر شد؟",
        accepted: [
          "Correlation is not causation.",
          "No, correlation is not causation.",
        ],
        note: "عبارت ثابت پژوهش: correlation is not causation.",
      },
      {
        scene: "جلسهٔ بررسی یک گزارش آماری", partner: "آمارگر ارشد",
        prompt: "بگو داده‌ها نشان می‌دهند که اثر کوچک است.",
        question: "What do the numbers say?",
        questionFa: "اعداد چه می‌گویند؟",
        accepted: [
          "The data show that the effect is small.",
          "The data suggest that the effect is small.",
        ],
        note: "data جمع است: the data show.",
      },
      {
        scene: "جلسهٔ بررسی یک گزارش آماری", partner: "آمارگر ارشد",
        prompt: "بگو یک دادهٔ پرت میانگین را بالا کشید.",
        question: "Why is the average so high?",
        questionFa: "چرا میانگین این‌قدر بالاست؟",
        accepted: [
          "One outlier pulled the average up.",
          "Because one outlier pulled the average up.",
        ],
        note: "pull something up: بالا کشیدن یک عدد.",
      },
      {
        scene: "جلسهٔ بررسی یک گزارش آماری", partner: "آمارگر ارشد",
        prompt: "بگو هیچ‌کدام از نمونه‌ها معنادار نبود.",
        question: "Were the samples significant?",
        questionFa: "نمونه‌ها معنادار بودند؟",
        accepted: [
          "Neither of the samples was significant.",
          "No, neither of the samples was significant.",
        ],
        note: "neither of با فعل مفرد: neither was.",
      },
      {
        scene: "جلسهٔ بررسی یک گزارش آماری", partner: "آمارگر ارشد",
        prompt: "بگو توزیع به سمت راست چولگی دارد.",
        question: "Is the distribution normal?",
        questionFa: "توزیع نرمال است؟",
        accepted: [
          "The distribution is skewed to the right.",
          "No, the distribution is skewed to the right.",
        ],
        note: "skewed to the right: چولگی به سمت راست.",
      },
      {
        scene: "جلسهٔ بررسی یک گزارش آماری", partner: "آمارگر ارشد",
        prompt: "بگو وقتی داده چولگی دارد از میانه استفاده می‌کنیم.",
        question: "Which average do we report?",
        questionFa: "کدام شاخص مرکزی را گزارش کنیم؟",
        accepted: [
          "We use the median when the data are skewed.",
          "Use the median when the data are skewed.",
        ],
        note: "the data are skewed: باز هم فعل جمع.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.media-analysis.lesson-01",
      order: 153,
      title: "Critical Media Analysis",
      titleFa: "تحلیل انتقادی رسانه",
      topic: "Media",
      tip: [
        "## قاعده",
        "بند موصولی تعریف‌کننده با that یا which می‌آید و اطلاعات لازم را اضافه می‌کند: The report that omitted the figure was published on Monday.",
        "بند موصولی غیرتعریف‌کننده با کاما و only which/who می‌آید (نه that): The editor, who approved the story, has resigned.",
        "برای شخص who/that و برای چیز which/that به کار می‌رود.",
        "## جدول",
        "The report that omitted the figure was published on Monday. — گزارشی که عدد را حذف کرده بود دوشنبه منتشر شد.",
        "The editor who approved the story has resigned. — سردبیری که خبر را تأیید کرده بود استعفا داد.",
        "The line, which sounded clever, hid the cost. — آن جمله که هوشمندانه به نظر می‌رسید هزینه را پنهان می‌کرد.",
        "## مثال‌ها",
        "This kind of spin destroys trust. — این نوع چرخش خبری اعتماد را از بین می‌برد.",
        "Gatekeeping decides what gets covered. — دروازه‌بانی تعیین می‌کند چه چیزی پوشش داده شود.",
        "The paywall limits access to the archive. — دیوار پرداخت دسترسی به آرشیو را محدود می‌کند.",
        "## خطاهای رایج",
        "پس از کاما that نمی‌آید: the editor, who approved it, ✓ / the editor, that approved it, ✗.",
        "soundbite قابل‌شمارش است: a soundbite, two soundbites.",
        "omission غیرقابل‌شمارش است؛ فعل آن omit و با مفعول مستقیم: omit a figure.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.critical-media-analysis",
      title: "Critical Media Analysis",
      titleFa: "تحلیل انتقادی رسانه",
      prerequisites: ["vocab.media-literacy"],
    },
    words: [
      {
        slug: "spin", w: "spin", fa: "چرخش خبری", ipa: "/spɪn/", pos: "noun",
        def: "a way of presenting news that favours one side",
        ex: [
          ["This kind of spin destroys trust.", "این نوع چرخش خبری اعتماد را از بین می‌برد."],
          ["The interview was pure spin from start to finish.", "آن مصاحبه از اول تا آخر چرخش خبری محض بود."],
        ],
        col: ["political spin", "put a spin on a story"],
        note: "spin در این معنا غیرقابل‌شمارش است؛ فعل آن هم to spin a story است.",
      },
      {
        slug: "omission", w: "omission", fa: "حذف", ipa: "/əˈmɪʃn/", pos: "noun",
        def: "something left out on purpose or by mistake",
        ex: [
          ["An omission of one figure can still mislead.", "حذف یک عدد هم می‌تواند گمراه‌کننده باشد."],
          ["The omission was never explained.", "آن حذف هرگز توضیح داده نشد."],
        ],
        col: ["a serious omission", "by omission"],
        note: "omission قابل‌شمارش و غیرقابل‌شمارش هر دو به کار می‌رود؛ فعل آن omit.",
      },
      {
        slug: "echo", w: "echo", fa: "هم‌صدایی", ipa: "/ˈekəʊ/", pos: "noun",
        def: "hearing the same opinion repeated back to you",
        ex: [
          ["An echo chamber makes a weak claim feel obvious.", "هم‌صدایی باعث می‌شود یک ادعای ضعیف بدیهی به نظر برسد."],
          ["The echo of one headline filled the timeline.", "هم‌صدایی یک تیتر کل تایم‌لاین را پر کرد."],
        ],
        col: ["an echo chamber", "the echo of a claim"],
        note: "echo هم اسم است و هم فعل؛ عبارت رایج echo chamber یعنی اتاق پژواک.",
      },
      {
        slug: "soundbite", w: "soundbite", fa: "جملهٔ کوتاه و شعارگونه", ipa: "/ˈsaʊndbaɪt/", pos: "noun",
        def: "a short memorable line made for broadcast",
        ex: [
          ["The soundbite makes the issue too simple.", "این جملهٔ کوتاه مسئله را بیش از حد ساده می‌کند."],
          ["One soundbite replaced ten minutes of evidence.", "یک جملهٔ کوتاه جای ده دقیقه شواهد را گرفت."],
        ],
        col: ["a catchy soundbite", "reduce a debate to soundbites"],
        note: "soundbite قابل‌شمارش است و معمولاً بار منفی دارد: تقلیل بحث به شعار.",
      },
      {
        slug: "gatekeeping", w: "gatekeeping", fa: "دروازه‌بانی", ipa: "/ˈɡeɪtkiːpɪŋ/", pos: "noun",
        def: "the power to decide which stories reach the public",
        ex: [
          ["Gatekeeping decides what gets covered.", "دروازه‌بانی تعیین می‌کند چه چیزی پوشش داده شود."],
          ["Gatekeeping moved from editors to algorithms.", "دروازه‌بانی از سردبیرها به الگوریتم‌ها منتقل شد."],
        ],
        col: ["editorial gatekeeping", "algorithmic gatekeeping"],
        note: "gatekeeping غیرقابل‌شمارش است؛ شخص آن gatekeeper.",
      },
      {
        slug: "paywall", w: "paywall", fa: "دیوار پرداخت", ipa: "/ˈpeɪwɔːl/", pos: "noun",
        def: "a block that makes readers pay to read an article",
        ex: [
          ["The paywall limits access to the archive.", "دیوار پرداخت دسترسی به آرشیو را محدود می‌کند."],
          ["Readers hit the paywall after two paragraphs.", "خوانندگان بعد از دو پاراگراف به دیوار پرداخت می‌رسند."],
        ],
        col: ["behind a paywall", "a strict paywall"],
        note: "paywall قابل‌شمارش است؛ عبارت ثابت behind a paywall.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "spin", gloss: "چرخش خبری",
        options: ["چرخش خبری", "تیتر", "مصاحبه", "آگهی"],
        note: "spin یعنی روایتی که خبر را به نفع یک طرف می‌چیند.",
      },
      {
        k: "mcq", word: "omission", gloss: "حذف",
        options: ["حذف", "افزایش", "تکرار", "ترجمه"],
        note: "omission چیزی است که آگاهانه یا از اشتباه جا افتاده است.",
      },
      {
        k: "mcq", word: "gatekeeping", gloss: "دروازه‌بانی",
        options: ["دروازه‌بانی", "ویرایش", "تبلیغ", "بایگانی"],
        note: "gatekeeping قدرت تصمیم دربارهٔ این است که چه خبری به مردم برسد.",
      },
      {
        k: "blank", sentence: "The report ___ omitted the figure was published on Monday.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "بند موصولی تعریف‌کننده: that یا which، بدون کاما.",
      },
      {
        k: "blank", sentence: "The editor ___ approved the story has resigned.",
        accepted: ["who", "that"], skill: "VOCABULARY",
        note: "برای شخص who یا that می‌آید.",
      },
      {
        k: "recall", prompt: "دیوار پرداخت", accepted: ["paywall"],
        note: "دیوار پرداخت paywall است و با behind می‌آید.",
      },
      {
        k: "tr", prompt: "گزارشی که عدد را حذف کرده بود روز دوشنبه منتشر شد.",
        accepted: [
          "the report that omitted the figure was published on monday",
          "the report which omitted the figure was published on monday",
        ],
        note: "بند موصولی تعریف‌کننده با that یا which، بدون کاما.",
      },
      {
        k: "listen", text: "The report that omitted the figure was published on Monday.",
        note: "به that omitted گوش کنید: بند موصولی تعریف‌کننده.",
      },
      {
        k: "prod", prompt: "بگو گزارشی که عدد را حذف کرده بود روز دوشنبه منتشر شد.",
        mustUse: ["that", "omitted"], minWords: 8,
        accepted: ["The report that omitted the figure was published on Monday."],
        hint: "با The report that… شروع کن.",
        note: "ترتیب لنگرها: that پیش از omitted می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ تحریریه پس از یک اشتباه خبری", partner: "سردبیر",
        prompt: "بگو گزارشی که عدد را حذف کرده بود روز دوشنبه منتشر شد.",
        question: "When did the story run?",
        questionFa: "آن خبر کِی منتشر شد؟",
        accepted: [
          "The report that omitted the figure was published on Monday.",
          "The report which omitted the figure ran on Monday.",
        ],
        note: "بند موصولی تعریف‌کننده: that یا which، بدون کاما.",
      },
      {
        scene: "جلسهٔ تحریریه پس از یک اشتباه خبری", partner: "سردبیر",
        prompt: "بگو این چرخش خبری اعتماد را از بین می‌برد.",
        question: "Was it spin?",
        questionFa: "چرخش خبری بود؟",
        accepted: [
          "This kind of spin destroys trust.",
          "Yes, it was spin, and it destroys trust.",
        ],
        note: "spin غیرقابل‌شمارش است: this kind of spin destroys.",
      },
      {
        scene: "جلسهٔ تحریریه پس از یک اشتباه خبری", partner: "سردبیر",
        prompt: "بگو حذف یک عدد هم می‌تواند گمراه‌کننده باشد.",
        question: "Was anything left out?",
        questionFa: "چیزی جا افتاده بود؟",
        accepted: [
          "An omission of one figure can still mislead.",
          "Yes, an omission can still mislead.",
        ],
        note: "an omission: حذف یک چیز مشخص قابل‌شمارش است.",
      },
      {
        scene: "جلسهٔ تحریریه پس از یک اشتباه خبری", partner: "سردبیر",
        prompt: "بگو این جملهٔ کوتاه مسئله را بیش از حد ساده می‌کند.",
        question: "Why do they use that line?",
        questionFa: "چرا آن جمله را به کار می‌برند؟",
        accepted: [
          "The soundbite makes the issue too simple.",
          "Because the soundbite makes the issue too simple.",
        ],
        note: "the soundbite مفرد است: makes.",
      },
      {
        scene: "جلسهٔ تحریریه پس از یک اشتباه خبری", partner: "سردبیر",
        prompt: "بگو دروازه‌بانی تعیین می‌کند چه چیزی پوشش داده شود.",
        question: "Who decides what we cover?",
        questionFa: "چه کسی تصمیم می‌گیرد چه چیزی پوشش داده شود؟",
        accepted: [
          "Gatekeeping decides what gets covered.",
          "Gatekeeping decides what is covered.",
        ],
        note: "gatekeeping غیرقابل‌شمارش است و فعل مفرد می‌گیرد.",
      },
      {
        scene: "جلسهٔ تحریریه پس از یک اشتباه خبری", partner: "سردبیر",
        prompt: "بگو دیوار پرداخت دسترسی به آرشیو را محدود می‌کند.",
        question: "Why do readers stop at the article?",
        questionFa: "چرا خوانندگان همان‌جا می‌مانند؟",
        accepted: [
          "The paywall limits access to the archive.",
          "Because the paywall limits access.",
        ],
        note: "access to something: دسترسی به یک چیز.",
      },
    ],
  },
];
