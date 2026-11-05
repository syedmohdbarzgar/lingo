// v55 content — C2 syllabus topics 8 (philosophical texts) and 9
// (interpreting complex speeches). Read by `v55_c2_batch.mjs`; never run alone.
export default [
  {
    lesson: {
      id: "c2.philosophy.lesson-01",
      order: 172,
      title: "Philosophical Texts",
      titleFa: "تحلیل متون فلسفی",
      topic: "Books",
      tip: [
        "## قاعده",
        "برای پیوند گذشته و حال از شرطی مختلط استفاده کن: If the premise had held, the conclusion would follow now.",
        "وقتی شرط حال است و نتیجه در گذشته، ساختار برعکس می‌شود: If the argument were valid, the objection would have failed.",
        "would have + قسمت سوم فعل فقط برای نتیجه‌ای است که در گذشته رخ نداده است.",
        "## جدول",
        "If the premise had held, the conclusion would follow now. — اگر مقدمه برجا مانده بود، نتیجه اکنون درست می‌شد.",
        "If the argument were valid, the objection would have failed. — اگر استدلال معتبر بود، ایراد وارد نمی‌شد.",
        "## مثال‌ها",
        "Epistemology asks what would count as evidence here. — شناخت‌شناسی می‌پرسد چه چیزی اینجا شاهد به حساب می‌آید.",
        "'Either it happens or it does not' is a tautology. — «یا رخ می‌دهد یا نمی‌دهد» یک همان‌گویی است.",
        "Scepticism is a method, not a mood. — شک روش است، نه حال‌وهوا.",
        "## خطاهای رایج",
        "If the premise had held, the conclusion would have followed now ✗ — نتیجه در حال است، پس would follow.",
        "would of followed ✗ — شکل نوشتاری would have است، نه would of.",
        "tautology را با paradox یکی نگیر: همان‌گویی همیشه درست است، تناقض‌نما در ظاهر متناقض.",
      ].join("\n"),
    },
    grammarNode: "grammar.mixed-conditionals",
    vocab: {
      id: "vocab.philosophical-texts",
      title: "Philosophical Texts",
      titleFa: "تحلیل متون فلسفی",
      prerequisites: ["discourse.logical-fallacies"],
    },
    words: [
      {
        slug: "epistemology", w: "epistemology", fa: "شناخت‌شناسی", ipa: "/ɪˌpɪstəˈmɒlədʒi/", pos: "noun",
        def: "the branch of philosophy about knowledge and justification",
        ex: [
          ["Epistemology asks what would count as evidence here.", "شناخت‌شناسی می‌پرسد چه چیزی اینجا شاهد به حساب می‌آید."],
          ["Her epistemology is pragmatic: knowledge is what survives testing.", "شناخت‌شناسی او عمل‌گراست: دانش چیزی است که آزمون را پشت سر می‌گذارد."],
        ],
        col: ["a question in epistemology", "naturalised epistemology"],
        note: "epistemology غیرقابل‌شمارش است؛ صفت آن epistemic به معنی «شناختی».",
      },
      {
        slug: "ontology", w: "ontology", fa: "هستی‌شناسی", ipa: "/ɒnˈtɒlədʒi/", pos: "noun",
        def: "the branch of philosophy about what exists",
        ex: [
          ["The ontology of the paper is deliberately minimal.", "هستی‌شناسی این مقاله عامدانه کمینه است."],
          ["Questions about ontology come before questions about method.", "پرسش‌های هستی‌شناختی پیش از پرسش‌های روشی می‌آیند."],
        ],
        col: ["a thin ontology", "commit to an ontology"],
        note: "ontology غیرقابل‌شمارش است؛ صفت آن ontological.",
      },
      {
        slug: "tautology", w: "tautology", fa: "همان‌گویی", ipa: "/tɔːˈtɒlədʒi/", pos: "noun",
        def: "a statement that is true only because it repeats itself",
        ex: [
          ["'Either it happens or it does not' is a tautology.", "«یا رخ می‌دهد یا نمی‌دهد» یک همان‌گویی است."],
          ["A tautology can be true and still say nothing.", "همان‌گویی می‌تواند درست باشد و هیچ نگوید."],
        ],
        col: ["a logical tautology", "dismiss it as a tautology"],
        note: "tautology قابل‌شمارش است؛ صفت آن tautological.",
      },
      {
        slug: "teleology", w: "teleology", fa: "غایت‌شناسی", ipa: "/ˌtiːliˈɒlədʒi/", pos: "noun",
        def: "explaining something by the purpose it serves",
        ex: [
          ["Teleology survives in biology as shorthand, not as a cause.", "غایت‌شناسی در زیست‌شناسی به‌عنوان کوتاه‌نویسی می‌ماند، نه علت."],
          ["He read purpose into the process and called it teleology.", "او به فرایند هدف نسبت داد و آن را غایت‌شناسی خواند."],
        ],
        col: ["a teleological explanation", "reject teleology"],
        note: "teleology غیرقابل‌شمارش است؛ صفت آن teleological.",
      },
      {
        slug: "dialectic", w: "dialectic", fa: "دیالکتیک", ipa: "/ˌdaɪəˈlektɪk/", pos: "noun",
        def: "reasoning that moves through a claim and its opposite",
        ex: [
          ["The dialectic moves from the claim to its denial and back.", "دیالکتیک از ادعا به انکار آن و بازگشت حرکت می‌کند."],
          ["Her essay is a dialectic between duty and freedom.", "مقالهٔ او دیالکتیکی میان وظیفه و آزادی است."],
        ],
        col: ["a Hegelian dialectic", "the dialectic of means and ends"],
        note: "dialectic قابل‌شمارش است؛ شکل صفتی آن dialectical.",
      },
      {
        slug: "scepticism", w: "scepticism", fa: "شک‌گرایی", ipa: "/ˈskeptɪsɪzəm/", pos: "noun",
        def: "doubting claims until they are justified",
        ex: [
          ["Scepticism is a method, not a mood.", "شک روش است، نه حال‌وهوا."],
          ["His scepticism about the data was justified.", "شک او دربارهٔ داده‌ها بجا بود."],
        ],
        col: ["healthy scepticism", "philosophical scepticism"],
        note: "املای بریتانیایی scepticism؛ در انگلیسی آمریکایی skepticism.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "epistemology", gloss: "شناخت‌شناسی",
        options: ["شناخت‌شناسی", "هستی‌شناسی", "غایت‌شناسی", "همان‌گویی"],
        note: "epistemology مطالعهٔ دانش و توجیه آن است.",
      },
      {
        k: "mcq", word: "tautology", gloss: "همان‌گویی",
        options: ["همان‌گویی", "دیالکتیک", "شک‌گرایی", "شناخت‌شناسی"],
        note: "tautology جمله‌ای است که فقط با تکرار خودش درست است.",
      },
      {
        k: "mcq", word: "teleology", gloss: "غایت‌شناسی",
        options: ["غایت‌شناسی", "هستی‌شناسی", "شک‌گرایی", "دیالکتیک"],
        note: "teleology توضیح یک چیز با هدف و کارکرد آن است.",
      },
      {
        k: "blank", sentence: "If the premise had held, the conclusion ___ follow now.",
        accepted: ["would", "could"], skill: "GRAMMAR",
        note: "شرط در گذشته و نتیجه در حال: would + فعل ساده.",
      },
      {
        k: "blank", sentence: "If the argument were valid, the objection ___ have failed.",
        accepted: ["would", "could"], skill: "GRAMMAR",
        note: "شرط در حال و نتیجه در گذشته: would have + قسمت سوم فعل.",
      },
      {
        k: "recall", prompt: "شناخت‌شناسی", accepted: ["epistemology"],
        note: "شناخت‌شناسی epistemology است.",
      },
      {
        k: "tr", prompt: "شک روش است، نه حال‌وهوا.",
        accepted: [
          "scepticism is a method not a mood",
          "skepticism is a method not a mood",
        ],
        note: "برای تقابل، ساختار X is a method, not a mood روشن است.",
      },
      {
        k: "listen", text: "The ontology of the paper is deliberately minimal.",
        note: "به ontology گوش کنید: هستی‌شناسی، یعنی اینکه متن چه چیزهایی را موجود می‌داند.",
      },
      {
        k: "prod", prompt: "بگو دیالکتیک از ادعا به انکار آن و بازگشت حرکت می‌کند.",
        mustUse: ["dialectic"], minWords: 8,
        accepted: ["The dialectic moves from the claim to its denial and back."],
        hint: "با The dialectic moves… شروع کن.",
        note: "dialectic با فعل مفرد moves می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "حلقهٔ خواندن فلسفه", partner: "استاد فلسفه",
        prompt: "بگو اگر مقدمه برجا مانده بود، نتیجه اکنون درست می‌شد.",
        question: "Does the conclusion still follow?",
        questionFa: "نتیجه هنوز به دست می‌آید؟",
        accepted: [
          "If the premise had held, the conclusion would follow now.",
          "The conclusion would follow if the premise had held.",
        ],
        note: "شرطی مختلط: گذشته در شرط، حال در نتیجه.",
      },
      {
        scene: "حلقهٔ خواندن فلسفه", partner: "استاد فلسفه",
        prompt: "بگو شک روش است، نه حال‌وهوا.",
        question: "Is he being difficult on purpose?",
        questionFa: "او عمداً سخت‌گیر است؟",
        accepted: [
          "Scepticism is a method, not a mood.",
          "It is a method, not a mood.",
        ],
        note: "scepticism با فعل مفرد is می‌آید.",
      },
      {
        scene: "حلقهٔ خواندن فلسفه", partner: "استاد فلسفه",
        prompt: "بگو همان‌گویی می‌تواند درست باشد و هیچ نگوید.",
        question: "Is that sentence informative?",
        questionFa: "آن جمله اطلاعاتی دارد؟",
        accepted: [
          "A tautology can be true and still say nothing.",
          "It is a tautology: it says nothing new.",
        ],
        note: "can be true and still say nothing: تقابل با and still.",
      },
      {
        scene: "حلقهٔ خواندن فلسفه", partner: "استاد فلسفه",
        prompt: "بگو غایت‌شناسی در زیست‌شناسی کوتاه‌نویسی است، نه علت.",
        question: "Does biology still use purpose?",
        questionFa: "زیست‌شناسی هنوز از هدف استفاده می‌کند؟",
        accepted: [
          "Teleology survives as shorthand, not as a cause.",
          "In biology it is shorthand, not a cause.",
        ],
        note: "as shorthand, not as a cause: تقابل دو نقش با as.",
      },
      {
        scene: "حلقهٔ خواندن فلسفه", partner: "استاد فلسفه",
        prompt: "بگو هستی‌شناسی این مقاله عامدانه کمینه است.",
        question: "How much does the paper assume exists?",
        questionFa: "مقاله چه اندازه موجودیت فرض می‌کند؟",
        accepted: [
          "The ontology of the paper is deliberately minimal.",
          "It keeps its ontology minimal.",
        ],
        note: "deliberately minimal: قید + صفت برای تأکید بر انتخاب آگاهانه.",
      },
      {
        scene: "حلقهٔ خواندن فلسفه", partner: "استاد فلسفه",
        prompt: "بگو دیالکتیک از ادعا به انکار آن و بازگشت حرکت می‌کند.",
        question: "How is the essay organised?",
        questionFa: "مقاله چگونه سازمان یافته است؟",
        accepted: [
          "The dialectic moves from the claim to its denial and back.",
          "It moves from the claim to its denial and back.",
        ],
        note: "from X to Y and back: الگوی حرکت رفت‌وبرگشتی.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.speeches.lesson-01",
      order: 173,
      title: "Interpreting Complex Speeches",
      titleFa: "تفسیر سخنرانی‌های پیچیده",
      topic: "Language",
      tip: [
        "## قاعده",
        "در تفسیر سخنرانی، مجهول با افعال وجهی می‌آید تا مسئولیت جمله معلوم نباشد: The main claim should be summarised first.",
        "ساختار ثابت است: modal + be + قسمت سوم فعل — must be checked, can be interpreted, should be noted.",
        "برای کاری که انجام نشده اما لازم بوده: should have been + قسمت سوم فعل — The figures should have been verified.",
        "## جدول",
        "The main claim should be summarised first. — ادعای اصلی باید نخست خلاصه شود.",
        "A verbatim record must be checked against the recording. — متن کلمه‌به‌کلمه باید با ضبط مقابله شود.",
        "## مثال‌ها",
        "Her oratory carried the room without a single slide. — سخنوری او سالن را بدون یک اسلاید همراه کرد.",
        "Interpreting the gist matters more than matching every word. — رساندن نکتهٔ اصلی مهم‌تر از تطبیق هر واژه است.",
        "His inflection turned a statement into a warning. — آهنگ صدای او یک جملهٔ خبری را به هشدار تبدیل کرد.",
        "## خطاهای رایج",
        "The claim should summarise first ✗ — مجهول لازم است: should be summarised.",
        "The figures should be verified yesterday ✗ — برای گذشتهٔ ازدست‌رفته: should have been verified.",
        "gist با the می‌آید: the gist of the speech، نه a gist.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-with-modals",
    vocab: {
      id: "vocab.interpreting-speeches",
      title: "Interpreting Complex Speeches",
      titleFa: "تفسیر سخنرانی‌های پیچیده",
      prerequisites: ["vocab.specialist-presentations"],
    },
    words: [
      {
        slug: "oratory", w: "oratory", fa: "سخنوری", ipa: "/ˈɒrətri/", pos: "noun",
        def: "the skill of speaking formally to an audience",
        ex: [
          ["Her oratory carried the room without a single slide.", "سخنوری او سالن را بدون یک اسلاید همراه کرد."],
          ["Oratory is judged by what the audience remembers.", "سخنوری را با چیزی می‌سنجند که مخاطب به یاد می‌آورد."],
        ],
        col: ["a master of oratory", "political oratory"],
        note: "oratory غیرقابل‌شمارش است؛ گویندهٔ آن orator نام دارد.",
      },
      {
        slug: "extempore", w: "extempore", fa: "بی‌آماده‌سازی", ipa: "/ɪkˈstempəri/", pos: "adjective",
        def: "spoken without preparation or notes",
        ex: [
          ["His reply was extempore and still exact.", "پاسخ او بی‌آماده‌سازی و در همان حال دقیق بود."],
          ["An extempore answer can reveal more than a script.", "پاسخ بی‌آماده‌سازی می‌تواند بیش از یک متن از پیش نوشته را آشکار کند."],
        ],
        col: ["an extempore speech", "speak extempore"],
        note: "extempore هم صفت است هم قید؛ مترادف رایج آن impromptu است.",
      },
      {
        slug: "gist", w: "gist", fa: "نکتهٔ اصلی", ipa: "/dʒɪst/", pos: "noun",
        def: "the main point of what was said",
        ex: [
          ["Interpreting the gist matters more than matching every word.", "رساندن نکتهٔ اصلی مهم‌تر از تطبیق هر واژه است."],
          ["She caught the gist before the interpreter finished.", "او پیش از پایان کار مترجم نکتهٔ اصلی را گرفت."],
        ],
        col: ["get the gist", "the gist of the argument"],
        note: "gist با the می‌آید: the gist of the speech.",
      },
      {
        slug: "verbatim", w: "verbatim", fa: "کلمه‌به‌کلمه", ipa: "/vɜːˈbeɪtɪm/", pos: "adjective",
        def: "word for word, exactly as said",
        ex: [
          ["A verbatim record of a speech is not the same as its meaning.", "متن کلمه‌به‌کلمهٔ یک سخنرانی با معنای آن یکی نیست."],
          ["He quoted the closing line verbatim.", "او جملهٔ پایانی را کلمه‌به‌کلمه نقل کرد."],
        ],
        col: ["a verbatim transcript", "quote it verbatim"],
        note: "verbatim صفت و قید است؛ برای ترجمهٔ کلمه‌به‌کلمه هم به کار می‌رود.",
      },
      {
        slug: "inflection", w: "inflection", fa: "آهنگ صدا", ipa: "/ɪnˈflekʃn/", pos: "noun",
        def: "the rise and fall of the voice in speech",
        ex: [
          ["His inflection turned a statement into a warning.", "آهنگ صدای او یک جملهٔ خبری را به هشدار تبدیل کرد."],
          ["A flat inflection can make irony sound like fact.", "آهنگ یکنواخت می‌تواند طعنه را شبیه واقعیت کند."],
        ],
        col: ["a rising inflection", "flat inflection"],
        note: "inflection اینجا آهنگ گفتار است؛ در دستور به معنی «صرف واژه» هم می‌آید.",
      },
      {
        slug: "rendition", w: "rendition", fa: "برگردان (اجرا)", ipa: "/renˈdɪʃn/", pos: "noun",
        def: "a version of a speech or text given in another language or form",
        ex: [
          ["The interpreter's rendition kept the irony intact.", "برگردان مترجم طعنه را دست‌نخورده نگه داشت."],
          ["Her rendition of the closing lines drew applause.", "اجرای او از سطرهای پایانی تشویق برانگیخت."],
        ],
        col: ["a faithful rendition", "the interpreter's rendition"],
        note: "rendition قابل‌شمارش است؛ فعل آن render به معنی «ارائه دادن» است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "oratory", gloss: "سخنوری",
        options: ["سخنوری", "نکتهٔ اصلی", "آهنگ صدا", "کلمه‌به‌کلمه"],
        note: "oratory مهارت سخن گفتن رسمی برای مخاطب است.",
      },
      {
        k: "mcq", word: "gist", gloss: "نکتهٔ اصلی",
        options: ["نکتهٔ اصلی", "برگردان (اجرا)", "بی‌آماده‌سازی", "سخنوری"],
        note: "gist جان کلام است، نه واژه‌های دقیق.",
      },
      {
        k: "mcq", word: "verbatim", gloss: "کلمه‌به‌کلمه",
        options: ["کلمه‌به‌کلمه", "آهنگ صدا", "نکتهٔ اصلی", "برگردان (اجرا)"],
        note: "verbatim یعنی عیناً با همان واژه‌ها.",
      },
      {
        k: "blank", sentence: "The main claim ___ be summarised first.",
        accepted: ["should", "must"], skill: "GRAMMAR",
        note: "مجهول با وجهی: modal + be + قسمت سوم فعل.",
      },
      {
        k: "blank", sentence: "A verbatim record ___ be checked against the recording.",
        accepted: ["must", "should"], skill: "GRAMMAR",
        note: "must be checked: الزام در برابر ضبط اصلی.",
      },
      {
        k: "recall", prompt: "نکتهٔ اصلی", accepted: ["gist"],
        note: "نکتهٔ اصلی gist است.",
      },
      {
        k: "tr", prompt: "برگردان او طعنه را دست‌نخورده نگه داشت.",
        accepted: [
          "her rendition kept the irony intact",
          "the rendition kept the irony intact",
        ],
        note: "keep + مفعول + صفت: kept the irony intact.",
      },
      {
        k: "listen", text: "His inflection turned a statement into a warning.",
        note: "به inflection گوش کنید: آهنگ صدا که معنا را عوض می‌کند.",
      },
      {
        k: "prod", prompt: "بگو رساندن نکتهٔ اصلی مهم‌تر از تطبیق هر واژه است.",
        mustUse: ["gist"], minWords: 8,
        accepted: ["Interpreting the gist matters more than matching every word."],
        hint: "با Interpreting the gist… شروع کن.",
        note: "the gist با حرف تعریف می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "سالن کنفرانس، غرفهٔ تفسیر هم‌زمان", partner: "مترجم کنفرانس",
        prompt: "بگو ادعای اصلی باید نخست خلاصه شود.",
        question: "Where do I start with this speech?",
        questionFa: "برای این سخنرانی از کجا شروع کنم؟",
        accepted: [
          "The main claim should be summarised first.",
          "Start with the main claim: it should be summarised first.",
        ],
        note: "should be summarised: مجهول با وجهی برای توصیه.",
      },
      {
        scene: "سالن کنفرانس، غرفهٔ تفسیر هم‌زمان", partner: "مترجم کنفرانس",
        prompt: "بگو رساندن نکتهٔ اصلی مهم‌تر از تطبیق هر واژه است.",
        question: "Should I copy every word?",
        questionFa: "باید هر واژه را عیناً بردارم؟",
        accepted: [
          "Interpreting the gist matters more than matching every word.",
          "The gist matters more than every single word.",
        ],
        note: "matters more than: مقایسهٔ اهمیت با more than.",
      },
      {
        scene: "سالن کنفرانس، غرفهٔ تفسیر هم‌زمان", partner: "مترجم کنفرانس",
        prompt: "بگو آهنگ صدای او یک جملهٔ خبری را به هشدار تبدیل کرد.",
        question: "Why did the audience react that way?",
        questionFa: "چرا مخاطب آن‌طور واکنش داد؟",
        accepted: [
          "His inflection turned a statement into a warning.",
          "It was his inflection: a warning, not a statement.",
        ],
        note: "turn X into Y: تبدیل کردن چیزی به چیز دیگر.",
      },
      {
        scene: "سالن کنفرانس، غرفهٔ تفسیر هم‌زمان", partner: "مترجم کنفرانس",
        prompt: "بگو متن کلمه‌به‌کلمه با ضبط مقابله شود.",
        question: "Do we trust the transcript?",
        questionFa: "به متن پیاده‌شده اعتماد کنیم؟",
        accepted: [
          "A verbatim record must be checked against the recording.",
          "The transcript must be checked against the recording.",
        ],
        note: "checked against: مقابله با منبع اصلی.",
      },
      {
        scene: "سالن کنفرانس، غرفهٔ تفسیر هم‌زمان", partner: "مترجم کنفرانس",
        prompt: "بگو برگردان او طعنه را دست‌نخورده نگه داشت.",
        question: "Did the humour survive translation?",
        questionFa: "طعنه در ترجمه سالم ماند؟",
        accepted: [
          "Her rendition kept the irony intact.",
          "The irony stayed intact in her rendition.",
        ],
        note: "intact صفت است و بعد از مفعول می‌آید: kept the irony intact.",
      },
      {
        scene: "سالن کنفرانس، غرفهٔ تفسیر هم‌زمان", partner: "مترجم کنفرانس",
        prompt: "بگو سخنوری او سالن را بدون یک اسلاید همراه کرد.",
        question: "How did she hold the room?",
        questionFa: "چطور سالن را همراه کرد؟",
        accepted: [
          "Her oratory carried the room without a single slide.",
          "It was her oratory, not her slides.",
        ],
        note: "without a single slide: تأکید با single در جملهٔ منفی.",
      },
    ],
  },
];
