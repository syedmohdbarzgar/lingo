// v54 content — the two B2 syllabus topics that never shipped: 29 (career
// decisions) and 30 (evaluating different viewpoints). Read by
// `v54_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.career-decisions.lesson-01",
      order: 144,
      title: "Career Decisions",
      titleFa: "تصمیم‌گیری دربارهٔ آیندهٔ شغلی",
      topic: "Work",
      tip: [
        "## قاعده",
        "برای پشیمانی از کاری که در گذشته انجام شده از wish + گذشتهٔ کامل استفاده کن: I wish I had asked more questions before accepting.",
        "برای وضعیتی که همین حالا دوست داری متفاوت باشد، wish + گذشتهٔ ساده می‌آید: I wish the notice period were shorter.",
        "If only همان معنی را با تأکید بیشتر می‌رساند: If only we had more time before deciding.",
        "## جدول",
        "I wish I had asked more questions before accepting. — کاش پیش از پذیرفتن، سؤال‌های بیشتری پرسیده بودم.",
        "I wish the notice period were shorter. — کاش مدت اخطار کوتاه‌تر بود.",
        "If only we had more time before deciding. — ای کاش پیش از تصمیم‌گیری وقت بیشتری داشتیم.",
        "## مثال‌ها",
        "Teaching was her vocation, not a job. — تدریس رسالت او بود، نه یک شغل.",
        "A sabbatical gave him time to retrain. — مرخصی مطالعاتی به او وقت داد تا مهارت تازه بیاموزد.",
        "He went freelance last spring. — بهار گذشته مستقل شد.",
        "## خطاهای رایج",
        "برای پشیمانی از گذشته گذشتهٔ کامل لازم است: wish I had asked ✓ / wish I asked (برای گذشته) ✗.",
        "پس از wish فعل گذشتهٔ ساده می‌آید، نه حال: I wish the notice period were shorter ✓.",
        "notice period غیرقابل‌شمارش نیست؛ با a یا the می‌آید: my notice period is one month.",
      ].join("\n"),
    },
    grammarNode: "grammar.wish-and-regret",
    vocab: {
      id: "vocab.career-decisions",
      title: "Career Decisions",
      titleFa: "تصمیم‌گیری دربارهٔ آیندهٔ شغلی",
      prerequisites: ["vocab.leadership"],
    },
    words: [
      {
        slug: "vocation", w: "vocation", fa: "رسالت شغلی", ipa: "/vəʊˈkeɪʃn/", pos: "noun",
        def: "work someone feels they were meant to do",
        ex: [
          ["Teaching was her vocation, not a job.", "تدریس رسالت او بود، نه یک شغل."],
          ["He found his vocation late, in a repair workshop.", "او دیر رسالتش را یافت، در یک کارگاه تعمیر."],
        ],
        col: ["find your vocation", "a sense of vocation"],
        note: "vocation قابل‌شمارش است؛ صفت آن vocational به معنی «حرفه‌ای/فنی».",
      },
      {
        slug: "appraisal", w: "appraisal", fa: "ارزیابی عملکرد", ipa: "/əˈpreɪzl/", pos: "noun",
        def: "the regular review of how someone is doing at work",
        ex: [
          ["The appraisal is on Friday.", "ارزیابی عملکرد روز جمعه است."],
          ["Her appraisal praised the handover notes.", "ارزیابی او یادداشت‌های تحویل کار را ستود."],
        ],
        col: ["an annual appraisal", "a performance appraisal"],
        note: "appraisal قابل‌شمارش است؛ برای ارزش‌گذاری مالی هم به کار می‌رود.",
      },
      {
        slug: "sabbatical", w: "sabbatical", fa: "مرخصی مطالعاتی", ipa: "/səˈbætɪkl/", pos: "noun",
        def: "a long paid or unpaid break from work to study or rest",
        ex: [
          ["A sabbatical gave him time to retrain.", "مرخصی مطالعاتی به او وقت داد تا مهارت تازه بیاموزد."],
          ["She took a sabbatical after ten years.", "او پس از ده سال مرخصی مطالعاتی گرفت."],
        ],
        col: ["take a sabbatical", "a six-month sabbatical"],
        note: "sabbatical قابل‌شمارش است و اغلب با take می‌آید.",
      },
      {
        slug: "freelance", w: "freelance", fa: "مستقل (پیمانکاری)", ipa: "/ˈfriːlɑːns/", pos: "adjective",
        def: "working for yourself rather than for one employer",
        ex: [
          ["Freelance work pays better but is less steady.", "کار مستقل درآمد بهتری دارد اما پایدارتر نیست."],
          ["He went freelance last spring.", "بهار گذشته مستقل شد."],
        ],
        col: ["go freelance", "freelance work"],
        note: "freelance صفت و قید است؛ فعل آن go freelance رایج‌تر از freelancing است.",
      },
      {
        slug: "pivot", w: "pivot", fa: "چرخش مسیر شغلی", ipa: "/ˈpɪvət/", pos: "noun",
        def: "a deliberate change of direction in a career or business",
        ex: [
          ["The pivot cost her a title, not her skills.", "آن چرخش عنوان شغلی‌اش را گرفت، نه مهارت‌هایش را."],
          ["A pivot at forty is normal now.", "چرخش مسیر در چهل‌سالگی حالا عادی است."],
        ],
        col: ["a career pivot", "pivot into a new field"],
        note: "pivot در این معنا «تغییر جهت آگاهانه» است؛ فعل آن هم pivot است.",
      },
      {
        slug: "notice-period", w: "notice period", fa: "مدت اخطار", ipa: "/ˈnəʊtɪs ˌpɪəriəd/", pos: "noun",
        def: "the time you must work after saying you will leave",
        ex: [
          ["My notice period is one month.", "مدت اخطار من یک ماه است."],
          ["The notice period was the hardest part to negotiate.", "مدت اخطار سخت‌ترین بخش برای مذاکره بود."],
        ],
        col: ["a three-month notice period", "serve your notice period"],
        note: "عبارت دوکلمه‌ای است؛ «اخطار» در فارسی اینجا فقط «مدت» را می‌رساند، نه هشدار.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "vocation", gloss: "رسالت شغلی",
        options: ["رسالت شغلی", "ارزیابی عملکرد", "مدت اخطار", "چرخش مسیر شغلی"],
        note: "vocation کاری است که آدم حس می‌کند برای آن ساخته شده است.",
      },
      {
        k: "mcq", word: "appraisal", gloss: "ارزیابی عملکرد",
        options: ["ارزیابی عملکرد", "مرخصی مطالعاتی", "کار مستقل", "رسالت شغلی"],
        note: "appraisal بررسی دوره‌ای عملکرد کاری است.",
      },
      {
        k: "mcq", word: "sabbatical", gloss: "مرخصی مطالعاتی",
        options: ["مرخصی مطالعاتی", "چرخش مسیر شغلی", "مدت اخطار", "ارزیابی عملکرد"],
        note: "sabbatical وقفهٔ بلندمدت از کار برای یادگیری یا استراحت است.",
      },
      {
        k: "blank", sentence: "I wish I ___ the offer before I signed it.",
        accepted: ["had read", "had reviewed"], skill: "VOCABULARY",
        note: "برای پشیمانی از گذشته: wish + گذشتهٔ کامل.",
      },
      {
        k: "blank", sentence: "If only we ___ more time before deciding.",
        accepted: ["had", "had had"], skill: "VOCABULARY",
        note: "had برای آرزوی حال و had had برای پشیمانی از گذشته؛ هر دو با if only درست‌اند.",
      },
      {
        k: "recall", prompt: "رسالت شغلی", accepted: ["vocation"],
        note: "رسالت شغلی vocation است و صفت آن vocational.",
      },
      {
        k: "tr", prompt: "کاش پیش از پذیرفتن، سؤال‌های بیشتری پرسیده بودم.",
        accepted: [
          "i wish i had asked more questions before accepting",
          "if only i had asked more questions before accepting",
        ],
        note: "wish + گذشتهٔ کامل: پشیمانی از کاری که انجام شده است.",
      },
      {
        k: "listen", text: "My notice period is one month.",
        note: "به notice period گوش کنید: مدت اخطار پس از استعفا.",
      },
      {
        k: "prod", prompt: "بگو کاش پیش از امضا کردن، پیشنهاد را خوانده بودم.",
        mustUse: ["wish", "had"], minWords: 6,
        accepted: ["I wish I had read the offer before signing."],
        hint: "با I wish I had… شروع کن.",
        note: "ترتیب لنگرها: wish پیش از had.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ مشاورهٔ مسیر شغلی", partner: "مشاور مسیر شغلی",
        prompt: "بگو کاش پیش از پذیرفتن، سؤال‌های بیشتری پرسیده بودم.",
        question: "What would you do differently?",
        questionFa: "چه کاری را متفاوت انجام می‌دادی؟",
        accepted: [
          "I wish I had asked more questions before accepting.",
          "If only I had asked more questions before accepting.",
        ],
        note: "wish + had + قسمت سوم فعل: پشیمانی از گذشته.",
      },
      {
        scene: "جلسهٔ مشاورهٔ مسیر شغلی", partner: "مشاور مسیر شغلی",
        prompt: "بگو تدریس رسالت او بود، نه یک شغل.",
        question: "Why did she stay in teaching?",
        questionFa: "چرا او در تدریس ماند؟",
        accepted: [
          "Teaching was her vocation, not a job.",
          "It was her vocation, not a job.",
        ],
        note: "vocation با was می‌آید: اسم مفرد قابل‌شمارش.",
      },
      {
        scene: "جلسهٔ مشاورهٔ مسیر شغلی", partner: "مشاور مسیر شغلی",
        prompt: "بگو کار مستقل درآمد بهتری دارد اما پایدارتر نیست.",
        question: "Is freelancing a good idea?",
        questionFa: "کار مستقل ایدهٔ خوبی است؟",
        accepted: [
          "Freelance work pays better but is less steady.",
          "It pays better but is less steady.",
        ],
        note: "less steady: کم‌پایدارتر، با less + صفت.",
      },
      {
        scene: "جلسهٔ مشاورهٔ مسیر شغلی", partner: "مشاور مسیر شغلی",
        prompt: "بگو مدت اخطار من یک ماه است.",
        question: "How long must you stay?",
        questionFa: "چقدر باید بمانی؟",
        accepted: [
          "My notice period is one month.",
          "The notice period is one month.",
        ],
        note: "notice period با is می‌آید: اسم مفرد.",
      },
      {
        scene: "جلسهٔ مشاورهٔ مسیر شغلی", partner: "مشاور مسیر شغلی",
        prompt: "بگو ارزیابی عملکرد روز جمعه است.",
        question: "When is your review?",
        questionFa: "ارزیابی‌ات چه زمانی است؟",
        accepted: [
          "The appraisal is on Friday.",
          "My appraisal is on Friday.",
        ],
        note: "on + روز هفته: on Friday.",
      },
      {
        scene: "جلسهٔ مشاورهٔ مسیر شغلی", partner: "مشاور مسیر شغلی",
        prompt: "بگو چرخش مسیر در چهل‌سالگی حالا عادی است.",
        question: "Am I too old to change field?",
        questionFa: "برای تغییر رشته دیر نیست؟",
        accepted: [
          "A pivot at forty is normal now.",
          "A career pivot is normal now.",
        ],
        note: "at forty: در چهل‌سالگی، با at.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.viewpoints.lesson-01",
      order: 145,
      title: "Evaluating Different Viewpoints",
      titleFa: "تحلیل دیدگاه‌های مخالف",
      topic: "Debate",
      tip: [
        "## قاعده",
        "برای مشخص کردن اینکه دقیقاً کدام دیدگاه را می‌سنجی، بند موصولی تعریفی بیاور: The view that the policy is unfair deserves an answer.",
        "بند غیرتعریفی با کاما و who/which می‌آید: The colleague, who had raised it twice, was right.",
        "در جملهٔ غیرتعریفی that نمی‌آید: The colleague, who had raised it… ✓ / The colleague, that had raised it… ✗.",
        "## جدول",
        "The view that the policy is unfair deserves an answer. — دیدگاهی که سیاست را ناعادلانه می‌داند پاسخ می‌خواهد.",
        "The colleague who raised it was right. — همکاری که آن را مطرح کرد درست می‌گفت.",
        "The argument that we cannot afford it has not been tested. — این استدلال که توان مالی نداریم آزموده نشده است.",
        "## مثال‌ها",
        "From her standpoint the risk is real. — از دیدگاه او ریسک واقعی است.",
        "Every claim needs a counterpoint. — هر ادعایی به یک نقطهٔ مقابل نیاز دارد.",
        "A one-sided summary convinces nobody. — خلاصهٔ یک‌طرفه هیچ‌کس را قانع نمی‌کند.",
        "## خطاهای رایج",
        "that در بند غیرتعریفی بعد از کاما نمی‌آید: , who raised it ✓ / , that raised it ✗.",
        "standpoint با from می‌آید: from her standpoint.",
        "one-sided صفت است؛ اسم آن one-sidedness است و کم به کار می‌رود.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.evaluating-viewpoints",
      title: "Evaluating Different Viewpoints",
      titleFa: "تحلیل دیدگاه‌های مخالف",
      prerequisites: ["vocab.debating"],
    },
    words: [
      {
        slug: "standpoint", w: "standpoint", fa: "دیدگاه", ipa: "/ˈstændpɔɪnt/", pos: "noun",
        def: "the position from which someone judges an issue",
        ex: [
          ["From her standpoint the risk is real.", "از دیدگاه او ریسک واقعی است."],
          ["A standpoint is not the same as a bias.", "دیدگاه با سوگیری یکی نیست."],
        ],
        col: ["from my standpoint", "a legal standpoint"],
        note: "standpoint با from می‌آید و با viewpoint هم‌معناست.",
      },
      {
        slug: "counterpoint", w: "counterpoint", fa: "نقطهٔ مقابل", ipa: "/ˈkaʊntəpɔɪnt/", pos: "noun",
        def: "an opposing idea set against a claim",
        ex: [
          ["Every claim needs a counterpoint.", "هر ادعایی به یک نقطهٔ مقابل نیاز دارد."],
          ["The counterpoint came from the finance team.", "نقطهٔ مقابل از تیم مالی آمد."],
        ],
        col: ["offer a counterpoint", "as a counterpoint"],
        note: "counterpoint قابل‌شمارش است؛ در موسیقی هم به معنی «نقطهٔ مقابل ملودیک» است.",
      },
      {
        slug: "cherry-pick", w: "cherry-pick", fa: "گزینش گزینشی شواهد", ipa: "/ˈtʃeri pɪk/", pos: "verb",
        def: "to pick only the evidence that supports you",
        ex: [
          ["You cannot cherry-pick the two best months.", "نمی‌توانی فقط دو ماه بهترین را گلچین کنی."],
          ["The report cherry-picked its examples.", "گزارش مثال‌هایش را گزینشی چیده بود."],
        ],
        col: ["cherry-pick the data", "accused of cherry-picking"],
        note: "cherry-pick با خط تیره نوشته می‌شود؛ اسم آن cherry-picking.",
      },
      {
        slug: "misrepresentation", w: "misrepresentation", fa: "ارائهٔ نادرست", ipa: "/ˌmɪsˌreprɪzenˈteɪʃn/", pos: "noun",
        def: "describing someone's position in a false way",
        ex: [
          ["That is a misrepresentation of the report.", "این ارائهٔ نادرستی از آن گزارش است."],
          ["Misrepresentation weakens a good case.", "ارائهٔ نادرست یک استدلال خوب را ضعیف می‌کند."],
        ],
        col: ["a deliberate misrepresentation", "correct a misrepresentation"],
        note: "misrepresentation غیرقابل‌شمارش است؛ فعل آن misrepresent.",
      },
      {
        slug: "one-sided", w: "one-sided", fa: "یک‌طرفه", ipa: "/ˌwʌn ˈsaɪdɪd/", pos: "adjective",
        def: "showing only one part of the story",
        ex: [
          ["A one-sided summary convinces nobody.", "خلاصهٔ یک‌طرفه هیچ‌کس را قانع نمی‌کند."],
          ["The memo was one-sided from the start.", "آن یادداشت از ابتدا یک‌طرفه بود."],
        ],
        col: ["a one-sided account", "one-sided coverage"],
        note: "one-sided صفت است و پیش از اسم می‌آید؛ مقابل آن fair-minded است.",
      },
      {
        slug: "fair-minded", w: "fair-minded", fa: "بی‌طرف و منصف", ipa: "/ˌfeə ˈmaɪndɪd/", pos: "adjective",
        def: "willing to judge both sides on their merits",
        ex: [
          ["A fair-minded reader will still disagree.", "خوانندهٔ منصف باز هم ممکن است مخالف باشد."],
          ["She is fair-minded about the other side.", "او دربارهٔ طرف مقابل منصف است."],
        ],
        col: ["a fair-minded assessment", "fair-minded readers"],
        note: "fair-minded صفت مرکب است و با خط تیره نوشته می‌شود.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "standpoint", gloss: "دیدگاه",
        options: ["دیدگاه", "نقطهٔ مقابل", "ارائهٔ نادرست", "گزینش گزینشی شواهد"],
        note: "standpoint موقعیتی است که از آن دربارهٔ یک موضوع داوری می‌شود.",
      },
      {
        k: "mcq", word: "cherry-pick", gloss: "گزینش گزینشی شواهد",
        options: ["گزینش گزینشی شواهد", "ارائهٔ نادرست", "نقطهٔ مقابل", "ارزیابی منصفانه"],
        note: "cherry-pick یعنی فقط شواهد موافق را انتخاب کردن.",
      },
      {
        k: "mcq", word: "one-sided", gloss: "یک‌طرفه",
        options: ["یک‌طرفه", "منصف", "دیدگاه", "نقطهٔ مقابل"],
        note: "one-sided فقط یک بخش روایت را نشان می‌دهد.",
      },
      {
        k: "blank", sentence: "The view ___ the policy is unfair deserves an answer.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "بند موصولی تعریفی برای «view» با that یا which می‌آید.",
      },
      {
        k: "blank", sentence: "The colleague ___ raised it was right.",
        accepted: ["who", "that"], skill: "VOCABULARY",
        note: "برای شخص در بند تعریفی، who و that هر دو درست‌اند.",
      },
      {
        k: "recall", prompt: "یک‌طرفه", accepted: ["one-sided"],
        note: "یک‌طرفه one-sided است و با خط تیره نوشته می‌شود.",
      },
      {
        k: "tr", prompt: "دیدگاهی که سیاست را ناعادلانه می‌داند پاسخ می‌خواهد.",
        accepted: [
          "the view that the policy is unfair deserves an answer",
          "the view which says the policy is unfair deserves an answer",
        ],
        note: "the view that…: بند موصولی که محتوای دیدگاه را می‌گوید.",
      },
      {
        k: "listen", text: "A one-sided summary convinces nobody.",
        note: "به one-sided گوش کنید: صفت مرکب با خط تیره.",
      },
      {
        k: "prod", prompt: "بگو دیدگاهی که سیاست را ناعادلانه می‌داند پاسخ می‌خواهد.",
        mustUse: ["view", "that"], minWords: 7,
        accepted: ["The view that the policy is unfair deserves an answer."],
        hint: "با The view that the policy… شروع کن.",
        note: "ترتیب لنگرها: view پیش از that.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ تحریریه، سنجش دیدگاه مخالف", partner: "سردبیر",
        prompt: "بگو دیدگاهی که سیاست را ناعادلانه می‌داند پاسخ می‌خواهد.",
        question: "Why answer the critics at all?",
        questionFa: "چرا به منتقدان پاسخ بدهیم؟",
        accepted: [
          "The view that the policy is unfair deserves an answer.",
          "The view that says the policy is unfair deserves an answer.",
        ],
        note: "the view that…: بند موصولی پس از اسم.",
      },
      {
        scene: "جلسهٔ تحریریه، سنجش دیدگاه مخالف", partner: "سردبیر",
        prompt: "بگو از دیدگاه او ریسک واقعی است.",
        question: "Why does she object?",
        questionFa: "چرا او اعتراض دارد؟",
        accepted: [
          "From her standpoint the risk is real.",
          "The risk is real from her standpoint.",
        ],
        note: "from her standpoint: از دیدگاه او.",
      },
      {
        scene: "جلسهٔ تحریریه، سنجش دیدگاه مخالف", partner: "سردبیر",
        prompt: "بگو هر ادعایی به یک نقطهٔ مقابل نیاز دارد.",
        question: "Is the draft balanced?",
        questionFa: "پیش‌نویس متوازن است؟",
        accepted: [
          "Every claim needs a counterpoint.",
          "No, every claim needs a counterpoint.",
        ],
        note: "every + اسم مفرد + فعل مفرد: needs.",
      },
      {
        scene: "جلسهٔ تحریریه، سنجش دیدگاه مخالف", partner: "سردبیر",
        prompt: "بگو نمی‌توانی فقط دو ماه بهترین را گلچین کنی.",
        question: "Can we quote the good months only?",
        questionFa: "می‌توانیم فقط ماه‌های خوب را نقل کنیم؟",
        accepted: [
          "You cannot cherry-pick the two best months.",
          "No, you cannot cherry-pick the best months.",
        ],
        note: "cherry-pick: فقط شواهد موافق را چیدن.",
      },
      {
        scene: "جلسهٔ تحریریه، سنجش دیدگاه مخالف", partner: "سردبیر",
        prompt: "بگو این ارائهٔ نادرستی از آن گزارش است.",
        question: "Did we misquote them?",
        questionFa: "آیا حرف آن‌ها را نادرست نقل کردیم؟",
        accepted: [
          "That is a misrepresentation of the report.",
          "Yes, it is a misrepresentation of the report.",
        ],
        note: "a misrepresentation of: ارائهٔ نادرست چه چیزی.",
      },
      {
        scene: "جلسهٔ تحریریه، سنجش دیدگاه مخالف", partner: "سردبیر",
        prompt: "بگو خوانندهٔ منصف باز هم ممکن است مخالف باشد.",
        question: "Will readers accept this?",
        questionFa: "خوانندگان این را می‌پذیرند؟",
        accepted: [
          "A fair-minded reader will still disagree.",
          "A fair-minded reader may still disagree.",
        ],
        note: "fair-minded پیش از اسم: خوانندهٔ منصف.",
      },
    ],
  },
];
