// v56 content — C2 syllabus topics 11 (advanced persuasion) and 12 (advanced
// academic writing). Read by `v56_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.advanced-persuasion.lesson-01",
      order: 174,
      title: "Advanced Persuasion",
      titleFa: "فنون اقناع پیشرفته",
      topic: "Debate",
      tip: [
        "## قاعده",
        "برای برجسته کردن مزیت یک پیشنهاد، جمله را با not only آغاز کن و وارونگی بساز: Not only does the plan cut costs, but it also wins votes.",
        "با rarely و seldom هم فعل کمکی پیش از فاعل می‌آید: Rarely does an audience remember a table of averages.",
        "این ساختار سه جزء دارد: قید منفی + فعل کمکی + فاعل + فعل اصلی.",
        "## جدول",
        "Not only does the plan cut costs, but it also wins votes. — طرح نه‌تنها هزینه را کم می‌کند، بلکه رأی هم می‌آورد.",
        "Rarely does an audience remember a table of averages. — مخاطب به‌ندرت جدولی از میانگین‌ها را به یاد می‌آورد.",
        "## مثال‌ها",
        "Her ethos came from years in the field, not from slides. — اعتبار او از سال‌ها کار میدانی می‌آمد، نه از اسلاید.",
        "The pathos of the story did the work the data could not. — شور داستان کاری را کرد که داده نمی‌توانست.",
        "Repetition raises salience more than accuracy does. — تکرار برجستگی را بیشتر از دقت بالا می‌برد.",
        "## خطاهای رایج",
        "Not only the plan cuts costs ✗ — پس از not only در آغاز جمله وارونگی لازم است.",
        "Not only does the plan cuts costs ✗ — بعد از does فعل ساده می‌آید: cut.",
        "logos را با لوگو اشتباه نگیر: logos استدلال است، نه نشان تجاری.",
      ].join("\n"),
    },
    grammarNode: "grammar.inversion-negative-adverbials",
    vocab: {
      id: "vocab.advanced-persuasion",
      title: "Advanced Persuasion",
      titleFa: "فنون اقناع پیشرفته",
      prerequisites: ["discourse.rhetorical-emphasis"],
    },
    words: [
      {
        slug: "ethos", w: "ethos", fa: "اعتبار گوینده", ipa: "/ˈiːθɒs/", pos: "noun",
        def: "the credibility a speaker earns with an audience",
        ex: [
          ["Her ethos came from years in the field, not from slides.", "اعتبار او از سال‌ها کار میدانی می‌آمد، نه از اسلاید."],
          ["An argument loses its ethos when the source is hidden.", "استدلال وقتی منبع پنهان باشد اعتبارش را از دست می‌دهد."],
        ],
        col: ["build ethos", "an argument with ethos"],
        note: "ethos غیرقابل‌شمارش است و در بلاغت کلاسیک هم‌ردیف pathos و logos.",
      },
      {
        slug: "pathos", w: "pathos", fa: "شور عاطفی", ipa: "/ˈpeɪθɒs/", pos: "noun",
        def: "the appeal to the audience's feelings",
        ex: [
          ["The pathos of the story did the work the data could not.", "شور داستان کاری را کرد که داده نمی‌توانست."],
          ["Pathos without evidence collapses on a second reading.", "شور بی‌شاهد در خوانش دوم فرو می‌ریزد."],
        ],
        col: ["a moment of pathos", "balance pathos and logos"],
        note: "pathos غیرقابل‌شمارش است؛ صفت pathetic در انگلیسی امروز بار منفی دارد.",
      },
      {
        slug: "logos", w: "logos", fa: "استدلال منطقی", ipa: "/ˈlɒɡɒs/", pos: "noun",
        def: "the appeal to reason and evidence",
        ex: [
          ["Logos carried the second half of the speech.", "استدلال منطقی نیمهٔ دوم سخنرانی را پیش برد."],
          ["Without logos, pathos sounds like a sales pitch.", "بدون استدلال، شور عاطفی شبیه تبلیغ فروش می‌شود."],
        ],
        col: ["appeal to logos", "logos and evidence"],
        note: "logos در بلاغت «استدلال» است، نه «لوگو»ی برند.",
      },
      {
        slug: "salience", w: "salience", fa: "برجستگی ذهنی", ipa: "/ˈseɪliəns/", pos: "noun",
        def: "the quality of standing out in someone's attention",
        ex: [
          ["The salience of one number made the rest invisible.", "برجستگی یک عدد بقیه را نامرئی کرد."],
          ["Repetition raises salience more than accuracy does.", "تکرار برجستگی را بیشتر از دقت بالا می‌برد."],
        ],
        col: ["raise salience", "the salience of a claim"],
        note: "salience از صفت salient می‌آید؛ در روان‌شناسی «برجستگی ذهنی» است.",
      },
      {
        slug: "inoculation", w: "inoculation", fa: "واکسینه‌سازی مخاطب", ipa: "/ɪˌnɒkjuˈleɪʃn/", pos: "noun",
        def: "naming a weak counter-argument first so it loses force later",
        ex: [
          ["Inoculation works: name the weak claim before your opponent does.", "واکسینه‌سازی کار می‌کند: ادعای ضعیف را پیش از حریف نام ببر."],
          ["Her inoculation against the attack came in the first minute.", "واکسینه‌سازی او در برابر حمله در دقیقهٔ اول آمد."],
        ],
        col: ["inoculation against a claim", "the inoculation effect"],
        note: "در اقناع، inoculation یعنی مخاطب را با طرح ضعیف‌ترین استدلال حریف «واکسینه» کردن.",
      },
      {
        slug: "anecdote", w: "anecdote", fa: "حکایت شخصی", ipa: "/ˈænɪkdəʊt/", pos: "noun",
        def: "a short personal story used to make a point",
        ex: [
          ["One anecdote can outweigh a table of averages in a speech.", "یک حکایت در سخنرانی می‌تواند از جدولی از میانگین‌ها سنگین‌تر شود."],
          ["The anecdote was memorable and, strictly, irrelevant.", "حکایت به‌یادماندنی بود و دقیق بگوییم بی‌ربط."],
        ],
        col: ["a telling anecdote", "open with an anecdote"],
        note: "anecdote شاهد تجربی است، نه داده؛ در نوشتار آکادمیک جای محدودی دارد.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "ethos", gloss: "اعتبار گوینده",
        options: ["اعتبار گوینده", "شور عاطفی", "برجستگی ذهنی", "حکایت شخصی"],
        note: "ethos اعتباری است که گوینده نزد مخاطب به دست می‌آورد.",
      },
      {
        k: "mcq", word: "pathos", gloss: "شور عاطفی",
        options: ["شور عاطفی", "استدلال منطقی", "اعتبار گوینده", "واکسینه‌سازی مخاطب"],
        note: "pathos توسل به احساسات مخاطب است.",
      },
      {
        k: "mcq", word: "salience", gloss: "برجستگی ذهنی",
        options: ["برجستگی ذهنی", "حکایت شخصی", "استدلال منطقی", "شور عاطفی"],
        note: "salience یعنی چیزی در توجه مخاطب برجسته شود.",
      },
      {
        k: "blank", sentence: "Not only ___ the plan cut costs, but it also wins votes.",
        accepted: ["does", "did"], skill: "GRAMMAR",
        note: "پس از not only در آغاز جمله، فعل کمکی پیش از فاعل می‌آید.",
      },
      {
        k: "blank", sentence: "Rarely ___ an audience remember a table of averages.",
        accepted: ["does", "did"], skill: "GRAMMAR",
        note: "با rarely در آغاز جمله، does و فعل ساده می‌آید.",
      },
      {
        k: "recall", prompt: "اعتبار گوینده", accepted: ["ethos"],
        note: "اعتبار گوینده ethos است.",
      },
      {
        k: "tr", prompt: "برجستگی یک عدد بقیه را نامرئی کرد.",
        accepted: [
          "the salience of one number made the rest invisible",
          "the salience of one number made the rest invisible to us",
        ],
        note: "make + مفعول + صفت: made the rest invisible.",
      },
      {
        k: "listen", text: "Logos carried the second half of the speech.",
        note: "به logos گوش کنید: استدلال منطقی در برابر pathos.",
      },
      {
        k: "prod", prompt: "بگو یک حکایت می‌تواند از جدولی از میانگین‌ها سنگین‌تر شود.",
        mustUse: ["anecdote"], minWords: 8,
        accepted: ["One anecdote can outweigh a table of averages in a speech."],
        hint: "با One anecdote can… شروع کن.",
        note: "outweigh یعنی «سنگین‌تر بودن از»، بدون than.",
      },
    ],
    dialogue: [
      {
        scene: "کارگاه اقناع", partner: "مشاور ارتباطات",
        prompt: "بگو طرح نه‌تنها هزینه را کم می‌کند، بلکه رأی هم می‌آورد.",
        question: "Why lead with cost?",
        questionFa: "چرا با هزینه شروع کنیم؟",
        accepted: [
          "Not only does the plan cut costs, but it also wins votes.",
          "It cuts costs and it also wins votes.",
        ],
        note: "پس از not only وارونگی: does the plan cut.",
      },
      {
        scene: "کارگاه اقناع", partner: "مشاور ارتباطات",
        prompt: "بگو مخاطب به‌ندرت جدولی از میانگین‌ها را به یاد می‌آورد.",
        question: "Should we show the full table?",
        questionFa: "کل جدول را نشان بدهیم؟",
        accepted: [
          "Rarely does an audience remember a table of averages.",
          "Audiences rarely remember a table of averages.",
        ],
        note: "نسخهٔ بدون وارونگی هم درست است، ولی تأکید کمتری دارد.",
      },
      {
        scene: "کارگاه اقناع", partner: "مشاور ارتباطات",
        prompt: "بگو اعتبار او از سال‌ها کار میدانی می‌آمد، نه از اسلاید.",
        question: "Where does her credibility come from?",
        questionFa: "اعتبار او از کجا می‌آید؟",
        accepted: [
          "Her ethos came from years in the field, not from slides.",
          "It came from the field, not from slides.",
        ],
        note: "not from slides: تقابل با not from.",
      },
      {
        scene: "کارگاه اقناع", partner: "مشاور ارتباطات",
        prompt: "بگو شور داستان کاری را کرد که داده نمی‌توانست.",
        question: "Did the numbers land?",
        questionFa: "اعداد اثر کردند؟",
        accepted: [
          "The pathos of the story did the work the data could not.",
          "The story did what the data could not.",
        ],
        note: "the work the data could not (do): حذف فعل تکراری در پایان.",
      },
      {
        scene: "کارگاه اقناع", partner: "مشاور ارتباطات",
        prompt: "بگو تکرار برجستگی را بیشتر از دقت بالا می‌برد.",
        question: "Why repeat the figure?",
        questionFa: "چرا عدد را تکرار می‌کنیم؟",
        accepted: [
          "Repetition raises salience more than accuracy does.",
          "Repetition makes it stand out more than accuracy does.",
        ],
        note: "more than accuracy does: مقایسه با does به‌جای تکرار فعل.",
      },
      {
        scene: "کارگاه اقناع", partner: "مشاور ارتباطات",
        prompt: "بگو ادعای ضعیف را پیش از حریف نام ببر.",
        question: "How do we handle their main objection?",
        questionFa: "با ایراد اصلی آن‌ها چه کنیم؟",
        accepted: [
          "Name the weak claim before your opponent does.",
          "Inoculation works: name it first.",
        ],
        note: "before your opponent does: حذف نام‌بردن دوباره از فعل.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.academic-writing.lesson-01",
      order: 175,
      title: "Advanced Academic Writing",
      titleFa: "نگارش دانشگاهی پیشرفته",
      topic: "Language",
      tip: [
        "## قاعده",
        "در نوشتار آکادمیک، کاری که پیش‌تر انجام شده با مجهول حال کامل بیان می‌شود: Studies have been conducted on both sides of the question.",
        "ساختار: have/has been + قسمت سوم فعل؛ کنشگر معمولاً حذف می‌شود چون مهم نیست.",
        "برای چیزی که پیش از یک نقطهٔ گذشته کامل شده: had been + قسمت سوم فعل — The caveat had been placed in a footnote.",
        "## جدول",
        "Studies have been conducted on both sides of the question. — پژوهش‌هایی در هر دو سوی مسئله انجام شده است.",
        "The caveat has been placed in a footnote. — قید احتیاط در پانویس گذاشته شده است.",
        "## مثال‌ها",
        "The thesis has to be arguable, not obvious. — پایان‌نامه/ادعای اصلی باید قابل بحث باشد، نه بدیهی.",
        "Every citation has to match the reference list exactly. — هر ارجاع باید دقیقاً با فهرست منابع بخواند.",
        "Her precis of the report ran to one page. — چکیدهٔ او از گزارش به یک صفحه رسید.",
        "## خطاهای رایج",
        "Studies have conducted ✗ — مجهول لازم است: have been conducted.",
        "The caveat has placed in a footnote ✗ — has been placed.",
        "thesis با فعل مفرد می‌آید؛ جمع آن theses است، نه thesisها.",
      ].join("\n"),
    },
    grammarNode: "grammar.present-perfect-passive",
    vocab: {
      id: "vocab.academic-writing",
      title: "Advanced Academic Writing",
      titleFa: "نگارش دانشگاهی پیشرفته",
      prerequisites: ["discourse.evidence-and-claims"],
    },
    words: [
      {
        slug: "thesis", w: "thesis", fa: "ادعای اصلی", ipa: "/ˈθiːsɪs/", pos: "noun",
        def: "the central claim an academic text argues for",
        ex: [
          ["The thesis has to be arguable, not obvious.", "ادعای اصلی باید قابل بحث باشد، نه بدیهی."],
          ["Every paragraph should serve the thesis.", "هر پاراگراف باید در خدمت ادعای اصلی باشد."],
        ],
        col: ["state a thesis", "a contestable thesis"],
        note: "جمع آن theses است و با /ˈθiːsiːz/ خوانده می‌شود.",
      },
      {
        slug: "citation", w: "citation", fa: "ارجاع", ipa: "/saɪˈteɪʃn/", pos: "noun",
        def: "a reference that shows where a claim comes from",
        ex: [
          ["Every citation has to match the reference list exactly.", "هر ارجاع باید دقیقاً با فهرست منابع بخواند."],
          ["A missing citation turns a summary into a claim of your own.", "ارجاعِ جاافتاده خلاصه را به ادعای خودت تبدیل می‌کند."],
        ],
        col: ["a full citation", "citation style"],
        note: "citation لزوماً «نقل قول» نیست؛ ارجاع به منبع هم هست.",
      },
      {
        slug: "footnote", w: "footnote", fa: "پانویس", ipa: "/ˈfʊtnəʊt/", pos: "noun",
        def: "a note printed at the bottom of a page",
        ex: [
          ["The caveat sits in a footnote, where nobody reads it.", "قید احتیاط در پانویس نشسته، جایی که کسی نمی‌خواند."],
          ["A footnote can carry the qualification the sentence cannot.", "پانویس می‌تواند قیدی را حمل کند که جمله نمی‌تواند."],
        ],
        col: ["in a footnote", "a long footnote"],
        note: "footnote در پای صفحه است؛ endnote در پایان فصل یا کتاب.",
      },
      {
        slug: "exegesis", w: "exegesis", fa: "تفسیر متن", ipa: "/ˌeksɪˈdʒiːsɪs/", pos: "noun",
        def: "a close critical interpretation of a text",
        ex: [
          ["The chapter is an exegesis of a single paragraph.", "این فصل تفسیری بر یک پاراگراف است."],
          ["Her exegesis separates what the author says from what the author assumes.", "تفسیر او آنچه نویسنده می‌گوید را از آنچه فرض می‌گیرد جدا می‌کند."],
        ],
        col: ["a close exegesis", "biblical exegesis"],
        note: "جمع آن exegeses؛ این واژه برای تفسیر دقیق متون به کار می‌رود، نه ترجمه.",
      },
      {
        slug: "monograph", w: "monograph", fa: "تک‌نگاشت", ipa: "/ˈmɒnəɡrɑːf/", pos: "noun",
        def: "a book-length study of a single subject",
        ex: [
          ["The monograph is the only full study of the dialect.", "این تک‌نگاشت تنها پژوهش کامل دربارهٔ آن گونه است."],
          ["He turned the dissertation into a monograph.", "او رساله را به تک‌نگاشت تبدیل کرد."],
        ],
        col: ["a scholarly monograph", "publish a monograph"],
        note: "monograph «کتاب پژوهشی» است، نه مقاله؛ mono- یعنی «یک/تنها».",
      },
      {
        slug: "precis", w: "precis", fa: "چکیده", ipa: "/ˈpreɪsiː/", pos: "noun",
        def: "a short summary that keeps the argument's shape",
        ex: [
          ["Write a precis before you disagree with the chapter.", "پیش از مخالفت با فصل، چکیده‌ای بنویس."],
          ["Her precis of the report ran to one page.", "چکیدهٔ او از گزارش به یک صفحه رسید."],
        ],
        col: ["write a precis", "a one-page precis"],
        note: "املای دقیق precis است (بدون اکسان)؛ گاهی précis هم نوشته می‌شود.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "thesis", gloss: "ادعای اصلی",
        options: ["ادعای اصلی", "پانویس", "تک‌نگاشت", "تفسیر متن"],
        note: "thesis ادعای مرکزی یک متن آکادمیک است.",
      },
      {
        k: "mcq", word: "citation", gloss: "ارجاع",
        options: ["ارجاع", "چکیده", "پانویس", "ادعای اصلی"],
        note: "citation نشان می‌دهد یک ادعا از کجا آمده است.",
      },
      {
        k: "mcq", word: "monograph", gloss: "تک‌نگاشت",
        options: ["تک‌نگاشت", "تفسیر متن", "ارجاع", "چکیده"],
        note: "monograph پژوهشی کتاب‌طول دربارهٔ یک موضوع است.",
      },
      {
        k: "blank", sentence: "Studies ___ been conducted on both sides of the question.",
        accepted: ["have", "had"], skill: "GRAMMAR",
        note: "مجهول حال کامل: have been conducted.",
      },
      {
        k: "blank", sentence: "The caveat ___ been placed in a footnote.",
        accepted: ["has", "had"], skill: "GRAMMAR",
        note: "فاعل مفرد: has been placed.",
      },
      {
        k: "recall", prompt: "تک‌نگاشت", accepted: ["monograph"],
        note: "تک‌نگاشت monograph است.",
      },
      {
        k: "tr", prompt: "هر ارجاع باید دقیقاً با فهرست منابع بخواند.",
        accepted: [
          "every citation has to match the reference list exactly",
          "every citation must match the reference list exactly",
        ],
        note: "has to / must برای الزام؛ match exactly یعنی دقیقاً بخواند.",
      },
      {
        k: "listen", text: "The thesis has to be arguable, not obvious.",
        note: "به thesis گوش کنید: ادعای اصلی که باید قابل بحث باشد.",
      },
      {
        k: "prod", prompt: "بگو ادعای اصلی باید قابل بحث باشد، نه بدیهی.",
        mustUse: ["thesis"], minWords: 6,
        accepted: ["The thesis has to be arguable, not obvious."],
        hint: "با The thesis has to be… شروع کن.",
        note: "arguable, not obvious: تقابل دو صفت با not.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ راهنمایی پایان‌نامه", partner: "استاد راهنما",
        prompt: "بگو ادعای اصلی باید قابل بحث باشد، نه بدیهی.",
        question: "Is my thesis strong enough?",
        questionFa: "ادعای اصلی‌ام به‌اندازهٔ کافی قوی است؟",
        accepted: [
          "The thesis has to be arguable, not obvious.",
          "It has to be arguable, not obvious.",
        ],
        note: "has to be: الزام با شکل مفرد فاعل.",
      },
      {
        scene: "جلسهٔ راهنمایی پایان‌نامه", partner: "استاد راهنما",
        prompt: "بگو پژوهش‌هایی در هر دو سوی مسئله انجام شده است.",
        question: "Has anyone studied both sides?",
        questionFa: "کسی هر دو سوی مسئله را بررسی کرده است؟",
        accepted: [
          "Studies have been conducted on both sides of the question.",
          "Yes, studies have been conducted on both sides.",
        ],
        note: "have been conducted: مجهول حال کامل با فاعل جمع.",
      },
      {
        scene: "جلسهٔ راهنمایی پایان‌نامه", partner: "استاد راهنما",
        prompt: "بگو هر ارجاع باید با فهرست منابع یکی باشد.",
        question: "Do the citations matter that much?",
        questionFa: "ارجاع‌ها این‌قدر مهم‌اند؟",
        accepted: [
          "Every citation has to match the reference list.",
          "Yes, every citation has to match exactly.",
        ],
        note: "every + اسم مفرد با فعل مفرد: has to match.",
      },
      {
        scene: "جلسهٔ راهنمایی پایان‌نامه", partner: "استاد راهنما",
        prompt: "بگو قید احتیاط در پانویس گذاشته شده است.",
        question: "Where did the caveat go?",
        questionFa: "قید احتیاط کجا رفت؟",
        accepted: [
          "The caveat has been placed in a footnote.",
          "It has been placed in a footnote.",
        ],
        note: "in a footnote: حرف اضافهٔ in برای پانویس.",
      },
      {
        scene: "جلسهٔ راهنمایی پایان‌نامه", partner: "استاد راهنما",
        prompt: "بگو پیش از مخالفت با فصل، چکیده‌ای بنویس.",
        question: "How should I start the critique?",
        questionFa: "نقد را چطور شروع کنم؟",
        accepted: [
          "Write a precis before you disagree with the chapter.",
          "Start with a precis, then disagree.",
        ],
        note: "before + حال ساده برای ترتیب زمانی: before you disagree.",
      },
      {
        scene: "جلسهٔ راهنمایی پایان‌نامه", partner: "استاد راهنما",
        prompt: "بگو این فصل تفسیری بر یک پاراگراف است.",
        question: "What is the chapter actually doing?",
        questionFa: "این فصل در واقع چه کاری می‌کند؟",
        accepted: [
          "The chapter is an exegesis of a single paragraph.",
          "It is an exegesis of one paragraph.",
        ],
        note: "an exegesis of: حرف اضافهٔ of برای موضوع تفسیر.",
      },
    ],
  },
];
