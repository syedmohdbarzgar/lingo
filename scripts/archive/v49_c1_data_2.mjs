// v49 content, part 2 — C1 topics 7 and 9: philosophy and reasoning, cognitive
// biases. Read by `v49_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.philosophy.lesson-01",
      order: 150,
      title: "Philosophy and Reasoning",
      titleFa: "فلسفه و استدلال",
      topic: "Debate",
      tip: [
        "## قاعده",
        "در استدلال، ساختار وارونه برای تأکید بر نقد به کار می‌آید: Not only does the premise fail, but the conclusion collapses too.",
        "Rarely و seldom هم همین الگو را دارند: Rarely has an argument survived without a counterexample.",
        "اگر جملهٔ دوم با nor ادامه یابد، باز هم وارونگی می‌خواهد: Nor does the evidence support it.",
        "## جدول",
        "Not only does the premise fail, but the conclusion collapses too. — نه‌تنها مقدمه فرو می‌ریزد، نتیجه هم فرومی‌پاشد.",
        "Rarely has an argument survived without a counterexample. — به‌ندرت استدلالی بدون مثال نقض دوام آورده است.",
        "Nor does the evidence support it. — شواهد هم آن را تأیید نمی‌کند.",
        "## مثال‌ها",
        "A valid syllogism can still reach a false conclusion. — یک قیاس معتبر هم می‌تواند به نتیجهٔ نادرست برسد.",
        "One counterexample is enough to break the claim. — یک مثال نقض برای شکستن آن ادعا کافی است.",
        "Validity is not the same as truth. — اعتبار با درستی یکی نیست.",
        "## خطاهای رایج",
        "با وارونگی، فعل کمکی پیش از فاعل می‌آید: Not only does the premise fail ✓ / Not only the premise does fail ✗.",
        "validity غیرقابل‌شمارش است: the validity of the argument.",
        "entail با مفعول مستقیم می‌آید: entails that conclusion (بدون to).",
      ].join("\n"),
    },
    grammarNode: "grammar.inversion-negative-adverbials",
    vocab: {
      id: "vocab.philosophy-reasoning",
      title: "Philosophy and Reasoning",
      titleFa: "فلسفه و استدلال",
      prerequisites: ["vocab.debating"],
    },
    words: [
      {
        slug: "syllogism", w: "syllogism", fa: "قیاس", ipa: "/ˈsɪlədʒɪzəm/", pos: "noun",
        def: "an argument built from two premises and a conclusion",
        ex: [
          ["A valid syllogism can still reach a false conclusion.", "یک قیاس معتبر هم می‌تواند به نتیجهٔ نادرست برسد."],
          ["The syllogism looks neat and proves nothing.", "قیاس مرتب به نظر می‌رسد و چیزی را ثابت نمی‌کند."],
        ],
        col: ["a valid syllogism", "build a syllogism"],
        note: "syllogism قابل‌شمارش است؛ صفت آن syllogistic.",
      },
      {
        slug: "axiom", w: "axiom", fa: "اصل موضوع", ipa: "/ˈæksiəm/", pos: "noun",
        def: "a statement accepted as true without proof",
        ex: [
          ["This axiom cannot be proved.", "این اصل موضوع قابل اثبات نیست."],
          ["The whole model rests on a single axiom.", "کل مدل بر یک اصل موضوع تکیه دارد."],
        ],
        col: ["a basic axiom", "accept an axiom"],
        note: "axiom قابل‌شمارش است؛ صفت آن axiomatic یعنی «بدیهی‌شده».",
      },
      {
        slug: "counterexample", w: "counterexample", fa: "مثال نقض", ipa: "/ˈkaʊntərɪɡzɑːmpl/", pos: "noun",
        def: "a case that shows a general claim is false",
        ex: [
          ["One counterexample is enough to break the claim.", "یک مثال نقض برای شکستن آن ادعا کافی است."],
          ["The counterexample was simple and fatal.", "مثال نقض ساده و کشنده بود."],
        ],
        col: ["find a counterexample", "a single counterexample"],
        note: "counterexample قابل‌شمارش است؛ انگلیسی بریتانیایی با صدای /ɪɡz/ تلفظ می‌شود.",
      },
      {
        slug: "entail", w: "entail", fa: "مستلزم بودن", ipa: "/ɪnˈteɪl/", pos: "verb",
        def: "to make something else necessarily true",
        ex: [
          ["The proposition entails that conclusion.", "این گزاره مستلزم آن نتیجه است."],
          ["A promise entails an obligation.", "هر وعده‌ای مستلزم یک تعهد است."],
        ],
        col: ["entail a conclusion", "entail a cost"],
        note: "entail با مفعول مستقیم می‌آید، نه با to؛ اسم آن entailment است.",
      },
      {
        slug: "validity", w: "validity", fa: "اعتبار", ipa: "/vəˈlɪdəti/", pos: "noun",
        def: "the quality of following correctly from the premises",
        ex: [
          ["Validity is not the same as truth.", "اعتبار با درستی یکی نیست."],
          ["Nobody questioned the validity of the argument.", "هیچ‌کس در اعتبار استدلال تردید نکرد."],
        ],
        col: ["the validity of an argument", "question the validity"],
        note: "validity غیرقابل‌شمارش است؛ صفت آن valid.",
      },
      {
        slug: "proposition", w: "proposition", fa: "گزاره", ipa: "/ˌprɒpəˈzɪʃn/", pos: "noun",
        def: "a statement that can be true or false",
        ex: [
          ["The proposition is either true or false.", "این گزاره یا درست است یا نادرست."],
          ["Every proposition in the proof was checked twice.", "هر گزاره در برهان دو بار بررسی شد."],
        ],
        col: ["a false proposition", "test a proposition"],
        note: "proposition قابل‌شمارش است؛ در منطق با statement فرق دارد و اشاره به محتوا دارد.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "syllogism", gloss: "قیاس",
        options: ["قیاس", "تناقض", "شاهد", "فرضیه"],
        note: "syllogism استدلالی از دو مقدمه و یک نتیجه است.",
      },
      {
        k: "mcq", word: "counterexample", gloss: "مثال نقض",
        options: ["مثال نقض", "تعریف", "اثبات", "نتیجه"],
        note: "counterexample موردی است که نشان می‌دهد ادعای کلی نادرست است.",
      },
      {
        k: "mcq", word: "axiom", gloss: "اصل موضوع",
        options: ["اصل موضوع", "خطا", "پرسش", "برهان"],
        note: "axiom گزاره‌ای است که بدون اثبات پذیرفته می‌شود.",
      },
      {
        k: "blank", sentence: "Not only ___ the premise fail, but the conclusion collapses too.",
        accepted: ["does", "did"], skill: "VOCABULARY",
        note: "پس از not only فعل کمکی پیش از فاعل می‌آید؛ does برای حال و did برای گذشته.",
      },
      {
        k: "blank", sentence: "Rarely ___ an argument survived without a counterexample.",
        accepted: ["has", "had"], skill: "VOCABULARY",
        note: "با rarely هم وارونگی داریم: has/had پیش از فاعل.",
      },
      {
        k: "recall", prompt: "اعتبار", accepted: ["validity"],
        note: "اعتبار validity است و صفت آن valid.",
      },
      {
        k: "tr", prompt: "نه‌تنها مقدمه نادرست است، نتیجه هم فرو می‌ریزد.",
        accepted: [
          "not only is the premise wrong but the conclusion collapses too",
          "not only is the premise false but the conclusion collapses too",
        ],
        note: "not only در آغاز، فعل کمکی را پیش از فاعل می‌آورد.",
      },
      {
        k: "listen", text: "Not only does the premise fail, but the conclusion collapses too.",
        note: "به does the premise گوش کنید: وارونگی پس از not only.",
      },
      {
        k: "prod", prompt: "بگو نه‌تنها مقدمه فرو می‌ریزد، نتیجه هم فرومی‌پاشد.",
        mustUse: ["not", "only"], minWords: 8,
        accepted: ["Not only does the premise fail but the conclusion collapses too."],
        hint: "با Not only does… شروع کن.",
        note: "ترتیب لنگرها: not پیش از only و then فعل کمکی.",
      },
    ],
    dialogue: [
      {
        scene: "سمینار فلسفه و استدلال", partner: "استاد منطق",
        prompt: "بگو نه‌تنها مقدمه نادرست است، نتیجه هم فرو می‌ریزد.",
        question: "Where does the argument break?",
        questionFa: "استدلال کجا می‌شکند؟",
        accepted: [
          "Not only is the premise wrong, the conclusion collapses too.",
          "Not only does the premise fail, but the conclusion collapses too.",
        ],
        note: "پس از not only فعل کمکی پیش از فاعل: is the premise, does the premise.",
      },
      {
        scene: "سمینار فلسفه و استدلال", partner: "استاد منطق",
        prompt: "بگو یک قیاس معتبر هم می‌تواند نتیجهٔ نادرست بدهد.",
        question: "Does a valid syllogism guarantee truth?",
        questionFa: "قیاس معتبر درستی را تضمین می‌کند؟",
        accepted: [
          "A valid syllogism can still reach a false conclusion.",
          "No, a valid syllogism can still reach a false conclusion.",
        ],
        note: "valid صفت و validity اسم است؛ استدلال معتبر می‌تواند نتیجهٔ نادرست بدهد.",
      },
      {
        scene: "سمینار فلسفه و استدلال", partner: "استاد منطق",
        prompt: "بگو این اصل موضوع قابل اثبات نیست.",
        question: "Can you prove the axiom?",
        questionFa: "می‌شود اصل موضوع را اثبات کرد؟",
        accepted: [
          "This axiom cannot be proved.",
          "No, the axiom cannot be proved.",
        ],
        note: "prove در مجهول: cannot be proved.",
      },
      {
        scene: "سمینار فلسفه و استدلال", partner: "استاد منطق",
        prompt: "بگو یک مثال نقض کافی است.",
        question: "How many counterexamples do you need?",
        questionFa: "چند مثال نقض لازم است؟",
        accepted: [
          "One counterexample is enough.",
          "A single counterexample is enough.",
        ],
        note: "enough پس از اسم می‌آید: one counterexample is enough.",
      },
      {
        scene: "سمینار فلسفه و استدلال", partner: "استاد منطق",
        prompt: "بگو اعتبار با درستی یکی نیست.",
        question: "Is validity the same as truth?",
        questionFa: "اعتبار همان درستی است؟",
        accepted: [
          "Validity is not the same as truth.",
          "No, validity and truth are different things.",
        ],
        note: "the same as برای مقایسه: the same as truth.",
      },
      {
        scene: "سمینار فلسفه و استدلال", partner: "استاد منطق",
        prompt: "بگو این گزاره مستلزم آن نتیجه است.",
        question: "Does the proposition commit you to that result?",
        questionFa: "این گزاره شما را به آن نتیجه متعهد می‌کند؟",
        accepted: [
          "The proposition entails that conclusion.",
          "Yes, the proposition entails that conclusion.",
        ],
        note: "entail با مفعول مستقیم: entails that conclusion.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.cognitive-bias.lesson-01",
      order: 151,
      title: "Cognitive Biases",
      titleFa: "خطاهای شناختی",
      topic: "Study",
      tip: [
        "## قاعده",
        "برای توضیح رفتار گذشته با حدس قوی: must have + قسمت سوم — The panel must have anchored on the first figure.",
        "برای احتمال: may have / might have + قسمت سوم — They may have ignored the warning.",
        "برای «ممکن نیست»: can't have + قسمت سوم — They can't have read the whole report in ten minutes.",
        "## جدول",
        "The panel must have anchored on the first figure. — پنل باید روی نخستین عدد لنگر انداخته باشد.",
        "They may have ignored the warning. — ممکن است هشدار را نادیده گرفته باشند.",
        "They can't have read the whole report. — ممکن نیست تمام گزارش را خوانده باشند.",
        "## مثال‌ها",
        "Framing the question changes the answer. — چارچوب‌بندی پرسش پاسخ را تغییر می‌دهد.",
        "Hindsight always makes it look obvious. — بصیرت پس از واقعه همیشه آن را بدیهی نشان می‌دهد.",
        "Overconfidence is the most dangerous bias. — اعتمادبه‌نفس بیش از حد خطرناک‌ترین سوگیری است.",
        "## خطاهای رایج",
        "پس از وجهی have می‌آید: must have anchored ✓ / must anchored ✗.",
        "برای «ممکن نیست» mustn't have نمی‌گوییم؛ can't have درست است.",
        "bias قابل‌شمارش است: a bias, several biases.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-of-deduction-past",
    vocab: {
      id: "vocab.cognitive-biases",
      title: "Cognitive Biases",
      titleFa: "خطاهای شناختی",
      prerequisites: ["vocab.media-literacy"],
    },
    words: [
      {
        slug: "anchoring", w: "anchoring", fa: "لنگراندازی", ipa: "/ˈæŋkərɪŋ/", pos: "noun",
        def: "relying too much on the first number you hear",
        ex: [
          ["Anchoring on the first figure is a common bias.", "لنگراندازی روی نخستین عدد سوگیری رایجی است."],
          ["Anchoring made every later offer look generous.", "لنگراندازی هر پیشنهاد بعدی را سخاوتمندانه نشان داد."],
        ],
        col: ["the anchoring effect", "anchoring on a number"],
        note: "anchoring غیرقابل‌شمارش است؛ فعل آن anchor و با on می‌آید: anchor on a figure.",
      },
      {
        slug: "framing", w: "framing", fa: "چارچوب‌بندی", ipa: "/ˈfreɪmɪŋ/", pos: "noun",
        def: "the way a question is worded, which shapes the answer",
        ex: [
          ["Framing the question changes the answer.", "چارچوب‌بندی پرسش پاسخ را تغییر می‌دهد."],
          ["Framing effects are easy to test.", "اثرهای چارچوب‌بندی به‌راحتی آزمون‌شدنی‌اند."],
        ],
        col: ["framing effects", "the framing of a question"],
        note: "framing غیرقابل‌شمارش است؛ فعل آن frame: frame the question.",
      },
      {
        slug: "hindsight", w: "hindsight", fa: "بصیرت پس از واقعه", ipa: "/ˈhaɪndsaɪt/", pos: "noun",
        def: "understanding an event only after it happened",
        ex: [
          ["Hindsight always makes it look obvious.", "بصیرت پس از واقعه همیشه آن را بدیهی نشان می‌دهد."],
          ["In hindsight, the warning was clear.", "با بصیرت پس از واقعه، هشدار روشن بود."],
        ],
        col: ["in hindsight", "the benefit of hindsight"],
        note: "hindsight غیرقابل‌شمارش است؛ عبارت ثابت in hindsight یعنی «حالا که گذشته را می‌بینیم».",
      },
      {
        slug: "heuristic", w: "heuristic", fa: "میانبر ذهنی", ipa: "/hjʊˈrɪstɪk/", pos: "noun",
        def: "a quick rule of thumb that is usually good enough",
        ex: [
          ["The heuristic works most of the time.", "این میانبر ذهنی بیشتر وقت‌ها کار می‌کند."],
          ["A heuristic is a shortcut, not a proof.", "میانبر ذهنی یک راه کوتاه است، نه یک برهان."],
        ],
        col: ["a simple heuristic", "a rule of thumb"],
        note: "heuristic هم اسم است و هم صفت (a heuristic method)؛ جمع آن heuristics.",
      },
      {
        slug: "overconfidence", w: "overconfidence", fa: "اعتمادبه‌نفس بیش از حد", ipa: "/ˌəʊvəˈkɒnfɪdəns/", pos: "noun",
        def: "believing you are more reliable than you are",
        ex: [
          ["Overconfidence is the most dangerous bias.", "اعتمادبه‌نفس بیش از حد خطرناک‌ترین سوگیری است."],
          ["Overconfidence in the forecast cost them the contract.", "اعتمادبه‌نفس بیش از حد در پیش‌بینی قرارداد را از دستشان برد."],
        ],
        col: ["overconfidence in a forecast", "dangerous overconfidence"],
        note: "overconfidence غیرقابل‌شمارش است؛ صفت آن overconfident.",
      },
      {
        slug: "confirmation", w: "confirmation", fa: "تأییدجویی", ipa: "/ˌkɒnfəˈmeɪʃn/", pos: "noun",
        def: "looking for evidence that supports what you already believe",
        ex: [
          ["We only read the confirmation evidence.", "ما فقط شواهد تأییدکننده را خواندیم."],
          ["Confirmation feels like proof but is not.", "تأییدجویی شبیه برهان است ولی برهان نیست."],
        ],
        col: ["confirmation bias", "seek confirmation"],
        note: "confirmation غیرقابل‌شمارش است؛ عبارت رایج confirmation bias یعنی سوگیری تأییدی.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "anchoring", gloss: "لنگراندازی",
        options: ["لنگراندازی", "پیش‌بینی", "تصمیم", "همکاری"],
        note: "anchoring تکیهٔ بیش از حد بر نخستین عددی است که می‌شنویم.",
      },
      {
        k: "mcq", word: "heuristic", gloss: "میانبر ذهنی",
        options: ["میانبر ذهنی", "خطای محاسبه", "شاهد عینی", "فرضیهٔ علمی"],
        note: "heuristic قاعدهٔ سرانگشتی سریعی است که بیشتر وقت‌ها کافی است.",
      },
      {
        k: "mcq", word: "hindsight", gloss: "بصیرت پس از واقعه",
        options: ["بصیرت پس از واقعه", "پیش‌بینی دقیق", "حافظهٔ ضعیف", "تصمیم گروهی"],
        note: "hindsight درک رخداد است فقط پس از وقوع آن.",
      },
      {
        k: "blank", sentence: "The panel ___ have anchored on the first figure it saw.",
        accepted: ["must", "may"], skill: "VOCABULARY",
        note: "must have حدس قوی و may have احتمال است.",
      },
      {
        k: "blank", sentence: "They ___ have ignored the evidence that contradicted them.",
        accepted: ["must", "may"], skill: "VOCABULARY",
        note: "برای گذشته: modal + have + قسمت سوم.",
      },
      {
        k: "recall", prompt: "چارچوب‌بندی", accepted: ["framing"],
        note: "چارچوب‌بندی framing است و فعل آن frame.",
      },
      {
        k: "tr", prompt: "پنل باید روی نخستین عددی که دید لنگر انداخته باشد.",
        accepted: [
          "the panel must have anchored on the first figure it saw",
          "the panel must have anchored on the first number it saw",
        ],
        note: "must have + قسمت سوم برای حدس قوی دربارهٔ گذشته.",
      },
      {
        k: "listen", text: "The panel must have anchored on the first figure it saw.",
        note: "به must have anchored گوش کنید: حدس دربارهٔ گذشته.",
      },
      {
        k: "prod", prompt: "بگو پنل باید روی نخستین عددی که دید لنگر انداخته باشد.",
        mustUse: ["must", "have"], minWords: 8,
        accepted: ["The panel must have anchored on the first figure it saw."],
        hint: "با The panel must have… شروع کن.",
        note: "ترتیب لنگرها: must پیش از have.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ بازبینی یک تصمیم سرمایه‌گذاری", partner: "تحلیلگر ارشد",
        prompt: "بگو پنل باید روی نخستین عددی که دید لنگر انداخته باشد.",
        question: "Why did they choose that figure?",
        questionFa: "چرا آن عدد را انتخاب کردند؟",
        accepted: [
          "The panel must have anchored on the first figure it saw.",
          "They must have anchored on the first figure they saw.",
        ],
        note: "must have + قسمت سوم: حدس قوی دربارهٔ گذشته.",
      },
      {
        scene: "جلسهٔ بازبینی یک تصمیم سرمایه‌گذاری", partner: "تحلیلگر ارشد",
        prompt: "بگو چارچوب‌بندی پرسش پاسخ را تغییر می‌دهد.",
        question: "Does the wording matter?",
        questionFa: "نحوهٔ بیان پرسش مهم است؟",
        accepted: [
          "Framing the question changes the answer.",
          "Yes, framing changes the answer.",
        ],
        note: "framing با مفعول مستقیم: framing the question.",
      },
      {
        scene: "جلسهٔ بازبینی یک تصمیم سرمایه‌گذاری", partner: "تحلیلگر ارشد",
        prompt: "بگو بصیرت پس از واقعه همیشه آن را بدیهی نشان می‌دهد.",
        question: "Was the outcome obvious?",
        questionFa: "نتیجه بدیهی بود؟",
        accepted: [
          "Hindsight always makes it look obvious.",
          "It is easy to say that in hindsight.",
        ],
        note: "in hindsight: حالا که گذشته را می‌بینیم.",
      },
      {
        scene: "جلسهٔ بازبینی یک تصمیم سرمایه‌گذاری", partner: "تحلیلگر ارشد",
        prompt: "بگو این میانبر ذهنی بیشتر وقت‌ها کار می‌کند.",
        question: "Is that shortcut reliable?",
        questionFa: "آن راه کوتاه قابل اعتماد است؟",
        accepted: [
          "The heuristic works most of the time.",
          "Yes, the heuristic usually works.",
        ],
        note: "most of the time: بیشتر وقت‌ها.",
      },
      {
        scene: "جلسهٔ بازبینی یک تصمیم سرمایه‌گذاری", partner: "تحلیلگر ارشد",
        prompt: "بگو اعتمادبه‌نفس بیش از حد خطرناک‌ترین سوگیری است.",
        question: "Which bias worries you most?",
        questionFa: "کدام سوگیری بیشتر نگرانتان می‌کند؟",
        accepted: [
          "Overconfidence is the most dangerous bias.",
          "Overconfidence worries me most.",
        ],
        note: "the most dangerous: صفت عالی با the.",
      },
      {
        scene: "جلسهٔ بازبینی یک تصمیم سرمایه‌گذاری", partner: "تحلیلگر ارشد",
        prompt: "بگو ما فقط شواهد تأییدکننده را خواندیم.",
        question: "Why did we miss the warning?",
        questionFa: "چرا هشدار را ندیدیم؟",
        accepted: [
          "We only read the confirmation evidence.",
          "Because we only looked for confirmation.",
        ],
        note: "look for confirmation: دنبال تأیید گشتن.",
      },
    ],
  },
];
