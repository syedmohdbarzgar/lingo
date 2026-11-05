// v60 content — C2 syllabus topic 30 (audience-sensitive language use). Read by
// `v60_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.audience.lesson-01",
      order: 192,
      title: "Audience-sensitive Language Use",
      titleFa: "انتخاب سبک زبانی متناسب با مخاطب",
      topic: "Communication",
      tip: [
        "## قاعده",
        "برای نشان دادن اینکه دو چیز با هم بالا و پایین می‌روند، ساخت the + comparative, the + comparative می‌آید: The plainer the sentence, the more the reader trusts it.",
        "ترتیب دو نیمه ثابت است: اول شرط، بعد نتیجه؛ هر دو نیمه با the آغاز می‌شوند و فعل در جای خود می‌ماند.",
        "این ساخت برای انتخاب سبک هم به کار می‌آید: هرچه مخاطب تخصصی‌تر، اصطلاح بیشتر و توضیح کمتر.",
        "## جدول",
        "The more technical the term, the fewer readers follow. — هرچه اصطلاح فنی‌تر، خوانندهٔ کمتری دنبال می‌کند.",
        "The shorter the sentence, the better the reader remembers it. — هرچه جمله کوتاه‌تر، خواننده بهتر آن را به یاد می‌آورد.",
        "The closer the example, the less the reader needs a definition. — هرچه نمونه نزدیک‌تر، خواننده کمتر به تعریف نیاز دارد.",
        "## مثال‌ها",
        "A patronising tone loses the reader in the first line. — لحن تحقیرآمیز خواننده را در همان خط اول از دست می‌دهد.",
        "Condescension costs more trust than a hard number does. — نگاه از بالا بیش از یک عدد سخت، اعتماد می‌گیرد.",
        "An inclusive example names the reader's own situation. — نمونهٔ شمول‌گرا موقعیت خودِ خواننده را نام می‌برد.",
        "The notice is written in plain English. — اطلاعیه به انگلیسی ساده نوشته شده است.",
        "If you overexplain, the expert stops reading. — اگر بیش از حد توضیح دهی، متخصص خواندن را رها می‌کند.",
        "## خطاهای رایج",
        "The more technical the term, the readers follow fewer. ✗ — ترتیب the + comparative در نیمهٔ دوم حفظ می‌شود: the fewer readers follow.",
        "patronising را با friendly یکی نگیر: patronising مخاطب را کوچک می‌کند، friendly او را همراه.",
        "plain English یعنی انگلیسی روشن و بی‌پیچش، نه انگلیسی کودکانه.",
      ].join("\n"),
    },
    grammarNode: "grammar.comparatives",
    vocab: {
      id: "vocab.audience-design",
      title: "Audience-sensitive Language Use",
      titleFa: "انتخاب سبک زبانی متناسب با مخاطب",
      prerequisites: ["vocab.specialist-presentations"],
    },
    words: [
      {
        slug: "patronising", w: "patronising", fa: "تحقیرآمیز", ipa: "/ˈpætrənaɪzɪŋ/", pos: "adjective",
        def: "speaking to someone as if they were less able than they are",
        ex: [
          ["A patronising tone loses the reader in the first line.", "لحن تحقیرآمیز خواننده را در همان خط اول از دست می‌دهد."],
          ["The guide sounds patronising to anyone who already knows the term.", "این راهنما برای هر کسی که اصطلاح را می‌داند تحقیرآمیز به گوش می‌رسد."],
        ],
        col: ["a patronising tone", "sound patronising"],
        note: "املای آمریکایی patronizing؛ فعلش patronise یعنی «با حالت برتری رفتار کردن».",
      },
      {
        slug: "condescension", w: "condescension", fa: "نگاه از بالا", ipa: "/ˌkɒndɪˈsenʃn/", pos: "noun",
        def: "the habit of treating other people as socially or intellectually below you",
        ex: [
          ["Condescension costs more trust than a hard number does.", "نگاه از بالا بیش از یک عدد سخت، اعتماد می‌گیرد."],
          ["The footnote reads as condescension, not as help.", "پانوشت به‌جای کمک، نگاه از بالا خوانده می‌شود."],
        ],
        col: ["a hint of condescension", "read as condescension"],
        note: "صفتش condescending است؛ condescension در نوشتار رسمی بار منفی دارد.",
      },
      {
        slug: "inclusive", w: "inclusive", fa: "شمول‌گرا", ipa: "/ɪnˈkluːsɪv/", pos: "adjective",
        def: "written so that every reader in the group can see themselves in it",
        ex: [
          ["An inclusive example names the reader's own situation.", "نمونهٔ شمول‌گرا موقعیت خودِ خواننده را نام می‌برد."],
          ["Inclusive wording costs one extra sentence and saves a complaint.", "واژه‌گزینی شمول‌گرا یک جملهٔ اضافه می‌خواهد و یک شکایت را کم می‌کند."],
        ],
        col: ["inclusive language", "an inclusive example"],
        note: "inclusive of something یعنی شاملِ آن؛ inclusive language اصطلاح ثابت است.",
      },
      {
        slug: "plain English", w: "plain English", fa: "انگلیسی ساده", ipa: "/pleɪn ˈɪŋɡlɪʃ/", pos: "noun",
        def: "clear writing that keeps the hard content but drops the needless complexity",
        ex: [
          ["The notice is written in plain English.", "اطلاعیه به انگلیسی ساده نوشته شده است."],
          ["Plain English is a style, not a simplification.", "انگلیسی ساده یک سبک است، نه ساده‌سازی."],
        ],
        col: ["in plain English", "a plain English guide"],
        note: "اسم مرکب است و حرف تعریف نمی‌گیرد: in plain English، نه in the plain English.",
      },
      {
        slug: "overexplain", w: "overexplain", fa: "بیش از حد توضیح دادن", ipa: "/ˌəʊvərɪkˈspleɪn/", pos: "verb",
        def: "to explain something so fully that the reader feels talked down to",
        ex: [
          ["If you overexplain, the expert stops reading.", "اگر بیش از حد توضیح دهی، متخصص خواندن را رها می‌کند."],
          ["The draft overexplains the method and skips the result.", "پیش‌نویس روش را بیش از حد توضیح می‌دهد و نتیجه را جا می‌گذارد."],
        ],
        col: ["overexplain the obvious", "tend to overexplain"],
        note: "پیشوند over- بار «بیش از حد» را می‌آورد؛ اسمش overexplanation است.",
      },
      {
        slug: "clarity", w: "clarity", fa: "روشنی", ipa: "/ˈklærəti/", pos: "noun",
        def: "the quality of writing that a reader understands on the first pass",
        ex: [
          ["Clarity is a choice about what to leave out.", "روشنی انتخابی است دربارهٔ آنچه حذف می‌شود."],
          ["The edit added clarity and removed two pages.", "ویرایش روشنی افزود و دو صفحه را حذف کرد."],
        ],
        col: ["add clarity", "for clarity"],
        note: "غیرقابل‌شمارش است؛ صفتش clear و فعلش clarify.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "patronising", gloss: "تحقیرآمیز",
        options: ["تحقیرآمیز", "شمول‌گرا", "روشنی", "نگاه از بالا"],
        note: "patronising یعنی با مخاطب طوری حرف زدن که او را کم‌توان‌تر از آنچه هست بدانیم.",
      },
      {
        k: "mcq", word: "condescension", gloss: "نگاه از بالا",
        options: ["نگاه از بالا", "انگلیسی ساده", "بیش از حد توضیح دادن", "روشنی"],
        note: "condescension رفتار از موضع برتر با مخاطب است.",
      },
      {
        k: "mcq", word: "inclusive", gloss: "شمول‌گرا",
        options: ["شمول‌گرا", "تحقیرآمیز", "روشنی", "انگلیسی ساده"],
        note: "inclusive یعنی همهٔ خوانندگان بتوانند خود را در متن ببینند.",
      },
      {
        k: "blank", sentence: "The more technical the term, the ___ readers follow.",
        accepted: ["fewer", "less"], skill: "GRAMMAR",
        note: "ساخت the more…, the fewer/less…؛ هر دو نیمه با the + comparative می‌آیند.",
      },
      {
        k: "blank", sentence: "The shorter the sentence, the ___ the reader remembers it.",
        accepted: ["better", "more"], skill: "GRAMMAR",
        note: "در نیمهٔ دوم هم the + comparative لازم است: the better / the more.",
      },
      {
        k: "recall", prompt: "تحقیرآمیز", accepted: ["patronising"],
        note: "تحقیرآمیز patronising است؛ املای آمریکایی patronizing.",
      },
      {
        k: "tr", prompt: "این اطلاعیه به انگلیسی ساده نوشته شده است.",
        accepted: [
          "the notice is written in plain english",
          "this notice is written in plain english",
        ],
        note: "in plain English: بدون حرف تعریف پیش از plain English.",
      },
      {
        k: "listen", text: "A patronising tone loses the reader in the first line.",
        note: "به patronising گوش کنید: لحنی که مخاطب را کوچک می‌کند.",
      },
      {
        k: "prod", prompt: "بگو اگر بیش از حد توضیح دهی، متخصص خواندن را رها می‌کند.",
        mustUse: ["overexplain"], minWords: 6,
        accepted: ["If you overexplain, the expert stops reading."],
        hint: "با If you overexplain… شروع کن.",
        note: "first conditional برای پیامد قابل پیش‌بینی: If + حال ساده, حال ساده.",
      },
    ],
    dialogue: [
      {
        scene: "کارگاه نگارش برای مخاطب", partner: "سرویراستار",
        prompt: "بگو لحن تحقیرآمیز خواننده را از دست می‌دهد.",
        question: "Is the tone too patronising?",
        questionFa: "لحن زیادی تحقیرآمیز نیست؟",
        accepted: [
          "A patronising tone loses the reader.",
          "It reads as patronising to an expert.",
        ],
        note: "read as یعنی چنین به نظر رسیدن؛ با as می‌آید.",
      },
      {
        scene: "کارگاه نگارش برای مخاطب", partner: "سرویراستار",
        prompt: "بگو نگاه از بالا بیش از یک عدد سخت اعتماد می‌گیرد.",
        question: "Why drop the footnote?",
        questionFa: "چرا پانوشت را حذف کنیم؟",
        accepted: [
          "Condescension costs more trust than a hard number.",
          "Condescension is what the footnote adds.",
        ],
        note: "cost trust یعنی اعتماد را گرفتن؛ cost در این معنا مفعول می‌گیرد.",
      },
      {
        scene: "کارگاه نگارش برای مخاطب", partner: "سرویراستار",
        prompt: "بگو نمونهٔ شمول‌گرا موقعیت خودِ خواننده را نام می‌برد.",
        question: "Who is the example for?",
        questionFa: "این نمونه برای چه کسی است؟",
        accepted: [
          "An inclusive example names the reader's own situation.",
          "The example is inclusive of part-time staff.",
        ],
        note: "inclusive of somebody: شاملِ کسی بودن.",
      },
      {
        scene: "کارگاه نگارش برای مخاطب", partner: "سرویراستار",
        prompt: "بگو انگلیسی ساده یک سبک است، نه ساده‌سازی.",
        question: "Is plain English a simplification?",
        questionFa: "انگلیسی ساده یعنی ساده‌سازی؟",
        accepted: [
          "Plain English is a style, not a simplification.",
          "Plain English keeps the hard content.",
        ],
        note: "not a simplification در پایان، تعریف را با حذف فعل روشن می‌کند.",
      },
      {
        scene: "کارگاه نگارش برای مخاطب", partner: "سرویراستار",
        prompt: "بگو اگر بیش از حد توضیح دهیم، متخصص خواندن را رها می‌کند.",
        question: "Do we overexplain the method?",
        questionFa: "روش را بیش از حد توضیح می‌دهیم؟",
        accepted: [
          "If you overexplain, the expert stops reading.",
          "We overexplain the part they already know.",
        ],
        note: "the part they already know: بند موصولی بدون which، رایج در گفتار.",
      },
      {
        scene: "کارگاه نگارش برای مخاطب", partner: "سرویراستار",
        prompt: "بگو روشنی یعنی انتخاب آنچه حذف می‌شود.",
        question: "What should we cut?",
        questionFa: "چه چیزی را حذف کنیم؟",
        accepted: [
          "Clarity is a choice about what to leave out.",
          "Cut the sentences that repeat the heading.",
        ],
        note: "leave out یعنی حذف کردن؛ what to leave out بند اسمی می‌سازد.",
      },
    ],
  },
];
