// v58 content — C2 syllabus topics 19 (literary allusions) and 20
// (paraphrasing with precision). Read by `v58_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.allusions.lesson-01",
      order: 182,
      title: "Literary Allusions",
      titleFa: "کنایه‌های ادبی",
      topic: "Literature",
      tip: [
        "## قاعده",
        "برای افزودن توضیحی که حذف‌شدنی است، بند موصولی غیرتعریفی با which/who و دو ویرگول بساز: The epigraph, which quotes Blake, frames the novel.",
        "در بند غیرتعریفی هرگز that نمی‌آید و ضمیر موصولی را نمی‌توان حذف کرد: the motif, which returns in chapter nine, …",
        "می‌توانی به‌جای بند کامل، عبارت وصفی کوتاه بیاوری: the motif, returning in chapter nine, …",
        "## جدول",
        "The epigraph, which quotes Blake, frames the novel. — سرلوحه، که از بلیک نقل شده، رمان را قاب می‌گیرد.",
        "The motif, which returns in chapter nine, carries the theme. — بن‌مایه، که در فصل نهم برمی‌گردد، درون‌مایه را حمل می‌کند.",
        "He borrows the ending, which is a risky homage. — پایان را وام می‌گیرد، که ادای احترامی پرخطر است.",
        "## مثال‌ها",
        "The poem's archetype is the wanderer who comes home changed. — الگوی کهنِ شعر، آواره‌ای است که دگرگون به خانه برمی‌گردد.",
        "A motif returns until the reader stops counting it. — بن‌مایه آنقدر برمی‌گردد تا خواننده دیگر آن را نشمارد.",
        "The pastiche flatters the style it copies. — تقلید سبک، سبکی را که می‌کپی‌د می‌ستاید.",
        "## خطاهای رایج",
        "The epigraph, that quotes Blake, … ✗ — در بند غیرتعریفی that به کار نمی‌رود.",
        "The motif, returns in chapter nine, … ✗ — ضمیر موصولی در بند غیرتعریفی حذف‌شدنی نیست.",
        "archetype را با stereotype یکی نگیر: archetype الگوی اولیه است، stereotype تصویر قالبی.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.literary-allusions",
      title: "Literary Allusions",
      titleFa: "کنایه‌های ادبی",
      prerequisites: ["vocab.idioms-and-figurative"],
    },
    words: [
      {
        slug: "archetype", w: "archetype", fa: "الگوی کهن", ipa: "/ˈɑːkitaɪp/", pos: "noun",
        def: "an original pattern of character or story that later works repeat",
        ex: [
          ["The poem's archetype is the wanderer who comes home changed.", "الگوی کهنِ شعر، آواره‌ای است که دگرگون به خانه برمی‌گردد."],
          ["Every detective story borrows the same archetype.", "هر داستان کارآگاهی همان الگوی کهن را وام می‌گیرد."],
        ],
        col: ["a recurring archetype", "the hero archetype"],
        note: "archetype الگوی اولیه و تکرارشونده است؛ با stereotype («تصویر قالبی») یکی نیست.",
      },
      {
        slug: "motif", w: "motif", fa: "بن‌مایه", ipa: "/məʊˈtiːf/", pos: "noun",
        def: "an image or idea that returns again and again inside one work",
        ex: [
          ["A motif returns until the reader stops counting it.", "بن‌مایه آنقدر برمی‌گردد تا خواننده دیگر آن را نشمارد."],
          ["The motif, which returns in chapter nine, carries the theme.", "بن‌مایه، که در فصل نهم برمی‌گردد، درون‌مایه را حمل می‌کند."],
        ],
        col: ["a recurring motif", "a visual motif"],
        note: "motif درون یک اثر تکرار می‌شود؛ theme پیام کلی اثر است.",
      },
      {
        slug: "epigraph", w: "epigraph", fa: "سرلوحه", ipa: "/ˈepɪɡrɑːf/", pos: "noun",
        def: "a short quotation placed at the start of a book or chapter",
        ex: [
          ["The epigraph, which quotes Blake, frames the novel.", "سرلوحه، که از بلیک نقل شده، رمان را قاب می‌گیرد."],
          ["A good epigraph warns the reader how to read.", "سرلوحهٔ خوب به خواننده هشدار می‌دهد که چگونه بخواند."],
        ],
        col: ["open with an epigraph", "a chapter epigraph"],
        note: "epigraph با epigram («جملهٔ کوتاه و نیش‌دار») و epitaph («سنگ‌نوشته») فرق دارد.",
      },
      {
        slug: "pastiche", w: "pastiche", fa: "تقلید سبک", ipa: "/pæˈstiːʃ/", pos: "noun",
        def: "a work that imitates another writer's style, often as a compliment",
        ex: [
          ["The pastiche flatters the style it copies.", "تقلید سبک، سبکی را که می‌کپی‌د می‌ستاید."],
          ["Her pastiche is so exact that readers miss the joke.", "تقلید سبک او چنان دقیق است که خوانندگان شوخی را نمی‌فهمند."],
        ],
        col: ["a pastiche of a Victorian novel", "write pastiche"],
        note: "pastiche تقلید سبک است؛ اگر با قصد تمسخر باشد، parody نامیده می‌شود.",
      },
      {
        slug: "homage", w: "homage", fa: "ادای احترام هنری", ipa: "/ˈhɒmɪdʒ/", pos: "noun",
        def: "an open, respectful reference to another artist's work",
        ex: [
          ["He borrows the ending, which is a risky homage.", "پایان را وام می‌گیرد، که ادای احترامی پرخطر است."],
          ["The film pays homage to silent comedy.", "فیلم به کمدی صامت ادای احترام می‌کند."],
        ],
        col: ["pay homage to", "an open homage"],
        note: "فعل ثابت آن pay homage to است، نه make homage؛ غیرقابل‌شمارش است.",
      },
      {
        slug: "reworking", w: "reworking", fa: "بازآفرینی", ipa: "/ˌriːˈwɜːkɪŋ/", pos: "noun",
        def: "a new version of an old story that changes its shape or emphasis",
        ex: [
          ["Her reworking keeps the plot and drops the moral.", "بازآفرینی او طرح را نگه می‌دارد و اخلاق را حذف می‌کند."],
          ["The reworking moves the story to a modern city.", "بازآفرینی داستان را به شهری امروزی می‌برد."],
        ],
        col: ["a modern reworking", "a reworking of a classic"],
        note: "reworking از rework می‌آید: بازنویسی با تغییر، نه ترجمه یا خلاصه.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "archetype", gloss: "الگوی کهن",
        options: ["الگوی کهن", "بن‌مایه", "سرلوحه", "بازآفرینی"],
        note: "archetype الگوی اولیه‌ای است که آثار بعدی تکرارش می‌کنند.",
      },
      {
        k: "mcq", word: "motif", gloss: "بن‌مایه",
        options: ["بن‌مایه", "الگوی کهن", "تقلید سبک", "ادای احترام هنری"],
        note: "motif تصویر یا اندیشه‌ای است که در یک اثر تکرار می‌شود.",
      },
      {
        k: "mcq", word: "epigraph", gloss: "سرلوحه",
        options: ["سرلوحه", "بن‌مایه", "بازآفرینی", "تقلید سبک"],
        note: "epigraph نقل‌قول کوتاهی در آغاز کتاب یا فصل است.",
      },
      {
        k: "blank", sentence: "The motif, ___ in chapter nine, carries the theme.",
        accepted: ["which returns", "returning"], skill: "GRAMMAR",
        note: "هر دو ساخت درست است: بند غیرتعریفی با which یا عبارت وصفی با ing.",
      },
      {
        k: "blank", sentence: "The epigraph, ___ from Blake, sets the tone.",
        accepted: ["which comes", "coming"], skill: "GRAMMAR",
        note: "پس از ویرگول، which comes یا coming؛ that هرگز.",
      },
      {
        k: "recall", prompt: "بن‌مایه", accepted: ["motif"],
        note: "بن‌مایه motif است.",
      },
      {
        k: "tr", prompt: "این رمان الگوی کهنِ سفر را بازمی‌آفریند.",
        accepted: [
          "the novel reworks the archetype of the journey",
          "this novel reworks the archetype of the journey",
        ],
        note: "rework یعنی بازآفرینی کردن؛ archetype of the journey: اضافهٔ معنایی.",
      },
      {
        k: "listen", text: "The pastiche flatters the style it copies.",
        note: "به pastiche گوش کنید: تقلید سبک، نه سرقت.",
      },
      {
        k: "prod", prompt: "بگو یک بن‌مایه آنقدر برمی‌گردد تا خواننده دیگر آن را نشمارد.",
        mustUse: ["motif"], minWords: 7,
        accepted: ["A motif returns until the reader stops counting it."],
        hint: "با A motif returns until… شروع کن.",
        note: "until + حال ساده، پایان یافتن یک روند را نشان می‌دهد.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ نقد ادبی", partner: "منتقد ادبی",
        prompt: "بگو این شخصیت الگوی کهنِ بازگشت به خانه را تکرار می‌کند.",
        question: "Is he an original character?",
        questionFa: "شخصیت تازه‌ای است؟",
        accepted: [
          "He repeats the archetype of the returning wanderer.",
          "The character is an archetype, not a portrait.",
        ],
        note: "repeat an archetype: الگو را تکرار کردن، نه شخص را کپی کردن.",
      },
      {
        scene: "جلسهٔ نقد ادبی", partner: "منتقد ادبی",
        prompt: "بگو بن‌مایه در فصل نهم برمی‌گردد.",
        question: "Where does the motif return?",
        questionFa: "بن‌مایه کجا برمی‌گردد؟",
        accepted: [
          "The motif returns in chapter nine.",
          "It comes back in chapter nine.",
        ],
        note: "return in + فصل یا بخش؛ حرف اضافه in است، نه at.",
      },
      {
        scene: "جلسهٔ نقد ادبی", partner: "منتقد ادبی",
        prompt: "بگو سرلوحه از یک شاعر نقل شده است، نه از نویسنده.",
        question: "Who is the epigraph from?",
        questionFa: "سرلوحه از کیست؟",
        accepted: [
          "The epigraph comes from the poet.",
          "The epigraph quotes the poet, not the author.",
        ],
        note: "come from برای منبع نقل‌قول؛ در پاسخ گفتاری نام خاص نیاور، چون شنوندهٔ خودکار آن را نمی‌شنود.",
      },
      {
        scene: "جلسهٔ نقد ادبی", partner: "منتقد ادبی",
        prompt: "بگو این اثر تقلید سبک است، نه سرقت.",
        question: "Isn't this copying?",
        questionFa: "این کپی‌برداری نیست؟",
        accepted: [
          "It is a pastiche, not theft.",
          "The pastiche flatters the style it copies.",
        ],
        note: "not theft در پایان، تقابل را با حذف فعل می‌سازد.",
      },
      {
        scene: "جلسهٔ نقد ادبی", partner: "منتقد ادبی",
        prompt: "بگو ادای احترام آشکار است، نه پنهان.",
        question: "Is the homage open?",
        questionFa: "این ادای احترام آشکار است؟",
        accepted: [
          "The homage is open, not hidden.",
          "It is an open homage.",
        ],
        note: "open در برابر hidden: صفت‌های کوتاه برای تقابل.",
      },
      {
        scene: "جلسهٔ نقد ادبی", partner: "منتقد ادبی",
        prompt: "بگو بازآفرینی طرح را نگه می‌دارد و اخلاق را حذف می‌کند.",
        question: "What changed in the new version?",
        questionFa: "در نسخهٔ تازه چه عوض شد؟",
        accepted: [
          "The reworking keeps the plot and drops the moral.",
          "It keeps the plot but drops the moral.",
        ],
        note: "keep و drop دو فعل متضاد در یک جملهٔ متوازن‌اند.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.paraphrasing.lesson-01",
      order: 183,
      title: "Paraphrasing with Precision",
      titleFa: "بازنویسی با حفظ معنا",
      topic: "Writing",
      tip: [
        "## قاعده",
        "در بازنویسی کنشگر را جابه‌جا کن و مفعول را فاعل جمله کن: The committee rejected the plan → The plan was rejected.",
        "ساختار مجهول: am/is/are/was/were + قسمت سوم فعل؛ کنشگر با by می‌آید و اگر روشن باشد حذف می‌شود.",
        "زمان را نگه دار: گذشتهٔ ساده → was/were + p.p.، حال ساده → is/are + p.p.",
        "## جدول",
        "The plan was rejected by the committee. — طرح از سوی کمیته رد شد.",
        "Her claim is restated in the summary. — ادعای او در خلاصه بازگو می‌شود.",
        "The term is glossed in a footnote. — اصطلاح در پانویس توضیح داده می‌شود.",
        "## مثال‌ها",
        "A rewording that keeps every claim is rare. — بازنویسی‌ای که همهٔ ادعاها را نگه دارد کم پیدا می‌شود.",
        "Fidelity matters more than elegance in a summary. — در خلاصه، وفاداری از زیبایی مهم‌تر است.",
        "A small distortion can reverse the writer's position. — یک تحریف کوچک می‌تواند موضع نویسنده را برگرداند.",
        "## خطاهای رایج",
        "The plan was reject ✗ — قسمت سوم فعل لازم است: rejected.",
        "The plan was rejected by they ✗ — پس از by ضمیر مفعولی می‌آید: by them.",
        "rewording را با «خلاصه» یکی نگیر: بازنویسی طول را تقریباً نگه می‌دارد.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-simple",
    vocab: {
      id: "vocab.precision-paraphrase",
      title: "Paraphrasing with Precision",
      titleFa: "بازنویسی با حفظ معنا",
      prerequisites: ["vocab.academic-writing"],
    },
    words: [
      {
        slug: "rewording", w: "rewording", fa: "بازنویسی", ipa: "/ˌriːˈwɜːdɪŋ/", pos: "noun",
        def: "the same content expressed in different words without shortening it",
        ex: [
          ["A rewording that keeps every claim is rare.", "بازنویسی‌ای که همهٔ ادعاها را نگه دارد کم پیدا می‌شود."],
          ["The rewording is longer than the original.", "بازنویسی از متن اصلی بلندتر است."],
        ],
        col: ["a careful rewording", "the rewording of a claim"],
        note: "rewording همهٔ محتوا را نگه می‌دارد؛ summary آن را کوتاه می‌کند.",
      },
      {
        slug: "fidelity", w: "fidelity", fa: "وفاداری به معنا", ipa: "/fɪˈdeləti/", pos: "noun",
        def: "how closely a copy keeps the meaning of the original",
        ex: [
          ["Fidelity matters more than elegance in a summary.", "در خلاصه، وفاداری از زیبایی مهم‌تر است."],
          ["The translation has high fidelity but a flat style.", "ترجمه وفاداری بالایی دارد، اما سبکی بی‌روح."],
        ],
        col: ["fidelity to the original", "high fidelity"],
        note: "fidelity غیرقابل‌شمارش است؛ حرف اضافهٔ آن to است: fidelity to the source.",
      },
      {
        slug: "equivalence", w: "equivalence", fa: "هم‌معنایی", ipa: "/ɪˈkwɪvələns/", pos: "noun",
        def: "the relation between two expressions that carry the same meaning",
        ex: [
          ["Equivalence between the two phrases is only partial.", "هم‌معنایی این دو عبارت فقط نسبی است."],
          ["Equivalence at word level does not guarantee equivalence of tone.", "هم‌معنایی در سطح واژه، هم‌معنایی لحن را تضمین نمی‌کند."],
        ],
        col: ["a rough equivalence", "equivalence between two terms"],
        note: "equivalence اسم است و صفتش equivalent؛ با equality («برابری») یکی نیست.",
      },
      {
        slug: "distortion", w: "distortion", fa: "تحریف معنا", ipa: "/dɪˈstɔːʃn/", pos: "noun",
        def: "a change in a retelling that alters what the original said",
        ex: [
          ["A small distortion can reverse the writer's position.", "یک تحریف کوچک می‌تواند موضع نویسنده را برگرداند."],
          ["The distortion came from dropping one word: not.", "تحریف از حذف یک واژه آمد: not."],
        ],
        col: ["a serious distortion", "distortion of meaning"],
        note: "distortion تغییر معنادار است، نه خطای دستوری؛ مترادف دقیقش misrepresentation است.",
      },
      {
        slug: "gloss", w: "gloss", fa: "توضیح کوتاه", ipa: "/ɡlɒs/", pos: "noun",
        def: "a short explanation of a difficult word or phrase",
        ex: [
          ["The term is glossed in a footnote.", "این اصطلاح در پانویس توضیح داده می‌شود."],
          ["A gloss should add meaning, not repeat the word.", "توضیح کوتاه باید معنا بیفزاید، نه واژه را تکرار کند."],
        ],
        col: ["a marginal gloss", "add a gloss"],
        note: "gloss در این معنا اسم است؛ فعل gloss over به معنای «سرسری از کنار گذشتن» است.",
      },
      {
        slug: "circumlocution", w: "circumlocution", fa: "دورگویی", ipa: "/ˌsɜːkəmləˈkjuːʃn/", pos: "noun",
        def: "using many words where one or two would say it better",
        ex: [
          ["The sentence turned into circumlocution.", "جمله به دورگویی تبدیل شد."],
          ["Circumlocution hides a weak claim behind long phrases.", "دورگویی ادعای ضعیف را پشت عبارت‌های بلند پنهان می‌کند."],
        ],
        col: ["avoid circumlocution", "a piece full of circumlocution"],
        note: "circumlocution غیرقابل‌شمارش است؛ در فارسی «دورگویی» یا «اطناب» گفته می‌شود.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "rewording", gloss: "بازنویسی",
        options: ["بازنویسی", "تحریف معنا", "دورگویی", "وفاداری به معنا"],
        note: "rewording همان محتوا را با واژه‌های دیگر و بدون کوتاه‌کردن می‌آورد.",
      },
      {
        k: "mcq", word: "fidelity", gloss: "وفاداری به معنا",
        options: ["وفاداری به معنا", "هم‌معنایی", "توضیح کوتاه", "بازنویسی"],
        note: "fidelity یعنی چقدر نسخهٔ تازه معنای اصل را نگه داشته است.",
      },
      {
        k: "mcq", word: "distortion", gloss: "تحریف معنا",
        options: ["تحریف معنا", "دورگویی", "وفاداری به معنا", "توضیح کوتاه"],
        note: "distortion تغییری است که معنای اصل را جابه‌جا می‌کند.",
      },
      {
        k: "blank", sentence: "The committee's claim ___ restated without loss in the summary.",
        accepted: ["was", "is"], skill: "GRAMMAR",
        note: "مجهول با was/is + p.p.: ادعا بازگو می‌شود.",
      },
      {
        k: "blank", sentence: "Her position ___ reversed by a careless rewording.",
        accepted: ["was", "got"], skill: "GRAMMAR",
        note: "get + p.p. در انگلیسی گفتاری همان مجهول را می‌سازد: got reversed.",
      },
      {
        k: "recall", prompt: "وفاداری به معنا", accepted: ["fidelity"],
        note: "وفاداری به معنا fidelity است.",
      },
      {
        k: "tr", prompt: "یک بازنویسی بی‌دقت می‌تواند موضع نویسنده را برگرداند.",
        accepted: [
          "a careless rewording can reverse the writer's position",
          "careless rewording can reverse the writer's position",
        ],
        note: "reverse در نقش فعل یعنی «برگرداندن، وارونه کردن».",
      },
      {
        k: "listen", text: "A small distortion can reverse the writer's position.",
        note: "به distortion گوش کنید: تحریف معنا، نه خطای نگارشی.",
      },
      {
        k: "prod", prompt: "بگو در خلاصه، وفاداری از زیبایی مهم‌تر است.",
        mustUse: ["fidelity"], minWords: 8,
        accepted: ["Fidelity matters more than elegance in a summary."],
        hint: "با Fidelity matters more than… شروع کن.",
        note: "matters more than: مقایسه با more than و بدون اسم مصدر.",
      },
    ],
    dialogue: [
      {
        scene: "کارگاه بازنویسی", partner: "ویراستار دانشگاهی",
        prompt: "بگو بازنویسی باید همهٔ ادعاها را نگه دارد.",
        question: "Can I shorten the claim?",
        questionFa: "می‌توانم ادعا را کوتاه کنم؟",
        accepted: [
          "A rewording must keep every claim.",
          "The rewording should keep every claim.",
        ],
        note: "keep every claim: مفعول مستقیم، بدون حرف اضافه.",
      },
      {
        scene: "کارگاه بازنویسی", partner: "ویراستار دانشگاهی",
        prompt: "بگو وفاداری از زیبایی مهم‌تر است.",
        question: "Which matters more?",
        questionFa: "کدام مهم‌تر است؟",
        accepted: [
          "Fidelity matters more than elegance.",
          "Fidelity, not elegance, comes first.",
        ],
        note: "matter در نقش فعل یعنی «مهم بودن».",
      },
      {
        scene: "کارگاه بازنویسی", partner: "ویراستار دانشگاهی",
        prompt: "بگو آن جمله موضع نویسنده را برگردانده است.",
        question: "Is this still the same view?",
        questionFa: "این هنوز همان دیدگاه است؟",
        accepted: [
          "That sentence reverses the writer's position.",
          "The rewording distorts the position.",
        ],
        note: "reverse با موضع و روند می‌آید؛ distort با معنا.",
      },
      {
        scene: "کارگاه بازنویسی", partner: "ویراستار دانشگاهی",
        prompt: "بگو اصطلاح تخصصی را با یک توضیح کوتاه روشن کن.",
        question: "How do I handle the technical term?",
        questionFa: "با اصطلاح تخصصی چه کنم؟",
        accepted: [
          "Gloss the technical term in a short phrase.",
          "Add a gloss for the technical term.",
        ],
        note: "gloss در نقش فعل: توضیح کوتاه افزودن.",
      },
      {
        scene: "کارگاه بازنویسی", partner: "ویراستار دانشگاهی",
        prompt: "بگو جمله آنقدر طولانی شد که به دورگویی رسید.",
        question: "Why is this sentence so long?",
        questionFa: "این جمله چرا این‌قدر بلند است؟",
        accepted: [
          "The sentence turned into circumlocution.",
          "It is circumlocution, not paraphrase.",
        ],
        note: "turn into یعنی «تبدیل شدن به»؛ با اسم انتزاعی.",
      },
      {
        scene: "کارگاه بازنویسی", partner: "ویراستار دانشگاهی",
        prompt: "بگو دو عبارت هم‌معنا نیستند، فقط نزدیک‌اند.",
        question: "Are these two words the same?",
        questionFa: "این دو واژه یکی هستند؟",
        accepted: [
          "The two phrases are not equivalent, only close.",
          "They are close, not equivalent.",
        ],
        note: "equivalent صفت است؛ not equivalent یعنی هم‌معنا نبودن.",
      },
    ],
  },
];
