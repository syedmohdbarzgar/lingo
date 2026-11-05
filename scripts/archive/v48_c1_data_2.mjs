// v48 content, part 2 — C1 topics 3 and 4: public policy and international
// relations. Read by `v48_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.policy.lesson-01",
      order: 146,
      title: "Public Policy",
      titleFa: "سیاست‌گذاری عمومی",
      topic: "Social",
      tip: [
        "## قاعده",
        "برای نقد گذشته از should have + قسمت سوم استفاده می‌کنیم: The ministry should have consulted the unions first.",
        "could have یعنی «می‌شد» و may have یعنی «ممکن است»؛ needn't have یعنی «لازم نبود».",
        "این ساختار همیشه دربارهٔ گذشته است، پس با now نمی‌آید.",
        "## جدول",
        "The ministry should have consulted the unions first. — وزارت باید اول با اتحادیه‌ها مشورت می‌کرد.",
        "Parliament could have acted sooner. — پارلمان می‌توانست زودتر اقدام کند.",
        "They needn't have published the draft so early. — لازم نبود پیش‌نویس را این‌قدر زود منتشر کنند.",
        "## مثال‌ها",
        "The legislation goes to parliament next month. — قانون ماه آینده به پارلمان می‌رود.",
        "A referendum is planned for the spring. — همه‌پرسی برای بهار برنامه‌ریزی شده است.",
        "Enforcement is the weakest part of the plan. — اجرا ضعیف‌ترین بخش این طرح است.",
        "## خطاهای رایج",
        "پس از وجهی have می‌آید: should have consulted ✓ / should consulted ✗.",
        "needn't have یعنی کاری انجام شد ولی لازم نبود؛ didn't need to یعنی کاری انجام نشد چون لازم نبود.",
        "legislation غیرقابل‌شمارش است: the legislation is ✓ / the legislations are ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-in-the-past",
    vocab: {
      id: "vocab.public-policy",
      title: "Public Policy",
      titleFa: "سیاست‌گذاری عمومی",
      prerequisites: ["vocab.professional-ethics"],
    },
    words: [
      {
        slug: "legislation", w: "legislation", fa: "قانون‌گذاری؛ قانون", ipa: "/ˌledʒɪsˈleɪʃn/", pos: "noun",
        def: "the laws a government makes",
        ex: [
          ["The legislation goes to parliament next month.", "قانون ماه آینده به پارلمان می‌رود."],
          ["New legislation rarely arrives on time.", "قانون تازه به‌ندرت سر وقت می‌رسد."],
        ],
        col: ["pass legislation", "new legislation"],
        note: "legislation غیرقابل‌شمارش است؛ یک قانون مشخص a law و جمع آن laws است.",
      },
      {
        slug: "referendum", w: "referendum", fa: "همه‌پرسی", ipa: "/ˌrefəˈrendəm/", pos: "noun",
        def: "a direct vote by the public on one question",
        ex: [
          ["A referendum is planned for the spring.", "همه‌پرسی برای بهار برنامه‌ریزی شده است."],
          ["The referendum split the country in two.", "همه‌پرسی کشور را دو نیم کرد."],
        ],
        col: ["hold a referendum", "call a referendum"],
        note: "جمع آن referendums یا referenda است؛ با hold یا call می‌آید.",
      },
      {
        slug: "enforcement", w: "enforcement", fa: "اجرا", ipa: "/ɪnˈfɔːsmənt/", pos: "noun",
        def: "making people obey a law or rule",
        ex: [
          ["Enforcement is the weakest part of the plan.", "اجرا ضعیف‌ترین بخش این طرح است."],
          ["Weak enforcement made the rules meaningless.", "اجرای ضعیف قواعد را بی‌معنا کرد."],
        ],
        col: ["law enforcement", "weak enforcement"],
        note: "enforcement غیرقابل‌شمارش است؛ فعل آن enforce با مفعول مستقیم می‌آید: enforce a law.",
      },
      {
        slug: "consultation", w: "consultation", fa: "مشورت", ipa: "/ˌkɒnsəlˈteɪʃn/", pos: "noun",
        def: "asking people for their views before deciding",
        ex: [
          ["Consultation with constituencies takes time.", "مشورت با حوزه‌های انتخابیه زمان می‌برد."],
          ["A short consultation changed the final text.", "یک مشورت کوتاه متن نهایی را تغییر داد."],
        ],
        col: ["public consultation", "consultation with unions"],
        note: "consultation با with می‌آید؛ فعل آن consult و با مفعول مستقیم: consult the unions.",
      },
      {
        slug: "constituency", w: "constituency", fa: "حوزهٔ انتخابیه", ipa: "/kənˈstɪtjuənsi/", pos: "noun",
        def: "the area a politician represents",
        ex: [
          ["Every constituency wants the new hospital.", "هر حوزهٔ انتخابیه بیمارستان تازه را می‌خواهد."],
          ["A marginal constituency decides the election.", "یک حوزهٔ انتخابیهٔ حساس نتیجهٔ انتخابات را تعیین می‌کند."],
        ],
        col: ["a marginal constituency", "the local constituency"],
        note: "constituency قابل‌شمارش است؛ به رأی‌دهندگان یک حوزه هم اشاره دارد.",
      },
      {
        slug: "bipartisan", w: "bipartisan", fa: "دوحزبی", ipa: "/ˌbaɪˈpɑːtɪzən/", pos: "adjective",
        def: "supported by both main political parties",
        ex: [
          ["The plan has bipartisan support.", "این طرح حمایت دوحزبی دارد."],
          ["A bipartisan deal is rare these days.", "توافق دوحزبی این روزها کم‌یاب است."],
        ],
        col: ["bipartisan support", "a bipartisan deal"],
        note: "bipartisan صفت است و پیش از اسم می‌آید: bipartisan support؛ اسم آن bipartisanship است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "legislation", gloss: "قانون‌گذاری",
        options: ["قانون‌گذاری", "بودجه", "همه‌پرسی", "مجازات"],
        note: "legislation قانون‌هایی است که دولت می‌سازد و غیرقابل‌شمارش است.",
      },
      {
        k: "mcq", word: "referendum", gloss: "همه‌پرسی",
        options: ["همه‌پرسی", "دادگاه", "قانون", "کمپین"],
        note: "referendum رأی مستقیم مردم به یک پرسش است.",
      },
      {
        k: "mcq", word: "constituency", gloss: "حوزهٔ انتخابیه",
        options: ["حوزهٔ انتخابیه", "کمیته", "وزارت", "بودجه"],
        note: "constituency ناحیه‌ای است که یک سیاستمدار نمایندگی می‌کند.",
      },
      {
        k: "blank", sentence: "The ministry ___ have consulted the unions first.",
        accepted: ["should", "could"], skill: "VOCABULARY",
        note: "should have نقد گذشته و could have «می‌شد» است؛ هر دو با قسمت سوم می‌آیند.",
      },
      {
        k: "blank", sentence: "Parliament ___ have acted sooner; the crisis was visible for months.",
        accepted: ["should", "could"], skill: "VOCABULARY",
        note: "برای گذشته should have / could have + قسمت سوم.",
      },
      {
        k: "recall", prompt: "دوحزبی", accepted: ["bipartisan"],
        note: "دوحزبی bipartisan است و با support می‌آید.",
      },
      {
        k: "tr", prompt: "دولت باید پیش از رأی‌گیری با مردم مشورت می‌کرد.",
        accepted: [
          "the government should have consulted the public before the vote",
          "the government should have consulted people before the vote",
        ],
        note: "should have + قسمت سوم: نقد کاری که انجام نشد.",
      },
      {
        k: "listen", text: "The ministry should have consulted the unions first.",
        note: "به should have consulted گوش کنید؛ have در گفتار کوتاه می‌شود.",
      },
      {
        k: "prod", prompt: "بگو وزارت باید اول با اتحادیه‌ها مشورت می‌کرد.",
        mustUse: ["should", "have"], minWords: 7,
        accepted: ["The ministry should have consulted the unions first."],
        hint: "با The ministry should have… شروع کن.",
        note: "ترتیب لنگرها: should پیش از have می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ کمیتهٔ سیاست‌گذاری", partner: "مشاور ارشد سیاست",
        prompt: "بگو وزارت باید اول با اتحادیه‌ها مشورت می‌کرد.",
        question: "What went wrong with the rollout?",
        questionFa: "در اجرا چه اشتباهی شد؟",
        accepted: [
          "The ministry should have consulted the unions first.",
          "It should have consulted the unions before the rollout.",
        ],
        note: "should have + قسمت سوم نقد گذشته است.",
      },
      {
        scene: "جلسهٔ کمیتهٔ سیاست‌گذاری", partner: "مشاور ارشد سیاست",
        prompt: "بگو قانون ماه آینده به پارلمان می‌رود.",
        question: "When does the bill reach parliament?",
        questionFa: "طرح کِی به پارلمان می‌رسد؟",
        accepted: [
          "The legislation goes to parliament next month.",
          "The legislation will reach parliament next month.",
        ],
        note: "legislation غیرقابل‌شمارش است، پس فعل مفرد می‌گیرد.",
      },
      {
        scene: "جلسهٔ کمیتهٔ سیاست‌گذاری", partner: "مشاور ارشد سیاست",
        prompt: "بگو همه‌پرسی برای بهار برنامه‌ریزی شده است.",
        question: "Will the public vote on it?",
        questionFa: "مردم درباره‌اش رأی می‌دهند؟",
        accepted: [
          "A referendum is planned for the spring.",
          "Yes, a referendum is planned for the spring.",
        ],
        note: "a referendum مفرد است: is planned ✓.",
      },
      {
        scene: "جلسهٔ کمیتهٔ سیاست‌گذاری", partner: "مشاور ارشد سیاست",
        prompt: "بگو اجرا ضعیف‌ترین بخش این طرح است.",
        question: "Where is the weak point?",
        questionFa: "نقطهٔ ضعف کجاست؟",
        accepted: [
          "Enforcement is the weakest part of the plan.",
          "The weakest part of the plan is enforcement.",
        ],
        note: "enforcement غیرقابل‌شمارش است و فعل مفرد می‌گیرد.",
      },
      {
        scene: "جلسهٔ کمیتهٔ سیاست‌گذاری", partner: "مشاور ارشد سیاست",
        prompt: "بگو مشورت با حوزه‌های انتخابیه زمان می‌برد.",
        question: "Why the delay?",
        questionFa: "چرا این تأخیر؟",
        accepted: [
          "Consultation with constituencies takes time.",
          "Because consultation with the constituencies takes time.",
        ],
        note: "consultation with somebody: مشورت با یک گروه.",
      },
      {
        scene: "جلسهٔ کمیتهٔ سیاست‌گذاری", partner: "مشاور ارشد سیاست",
        prompt: "بگو این طرح حمایت دوحزبی دارد.",
        question: "Will it pass?",
        questionFa: "تصویب می‌شود؟",
        accepted: [
          "It has bipartisan support.",
          "The plan has bipartisan support.",
        ],
        note: "support غیرقابل‌شمارش است، پس has می‌گیرد.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.international.lesson-01",
      order: 147,
      title: "International Relations",
      titleFa: "روابط بین‌الملل",
      topic: "Social",
      tip: [
        "## قاعده",
        "برای حدس قوی دربارهٔ گذشته: must have + قسمت سوم — They must have misread the signals.",
        "برای حدس ضعیف یا احتمال: may have / might have + قسمت سوم — The talks may have failed for a simpler reason.",
        "برای «غیرممکن بودن»: can't have + قسمت سوم — They can't have ignored the warning twice.",
        "## جدول",
        "They must have misread the signals from the summit. — آن‌ها باید نشانه‌های نشست را اشتباه خوانده باشند.",
        "The talks may have failed for a simpler reason. — ممکن است مذاکرات به دلیل ساده‌تری شکست خورده باشد.",
        "They can't have ignored the warning twice. — ممکن نیست هشدار را دو بار نادیده گرفته باشند.",
        "## مثال‌ها",
        "The sovereignty of both countries is respected. — حاکمیت هر دو کشور محترم است.",
        "The treaty has not been signed yet. — پیمان‌نامه هنوز امضا نشده است.",
        "The envoy returns to the capital next week. — فرستاده هفتهٔ آینده به پایتخت برمی‌گردد.",
        "## خطاهای رایج",
        "پس از وجهی حتماً have می‌آید: must have misread ✓ / must misread ✗.",
        "mustn't have در این معنا به کار نمی‌رود؛ برای «ممکن نیست» can't have می‌گوییم.",
        "treaty قابل‌شمارش است: a treaty, two treaties (نه treatys).",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-of-deduction-past",
    vocab: {
      id: "vocab.international-relations",
      title: "International Relations",
      titleFa: "روابط بین‌الملل",
      prerequisites: ["vocab.intercultural"],
    },
    words: [
      {
        slug: "sovereignty", w: "sovereignty", fa: "حاکمیت", ipa: "/ˈsɒvrənti/", pos: "noun",
        def: "a country's right to govern itself",
        ex: [
          ["The sovereignty of both countries is respected.", "حاکمیت هر دو کشور محترم است."],
          ["No treaty can remove national sovereignty.", "هیچ پیمان‌نامه‌ای نمی‌تواند حاکمیت ملی را از بین ببرد."],
        ],
        col: ["national sovereignty", "respect sovereignty"],
        note: "sovereignty غیرقابل‌شمارش است؛ صفت آن sovereign است.",
      },
      {
        slug: "treaty", w: "treaty", fa: "پیمان‌نامه", ipa: "/ˈtriːti/", pos: "noun",
        def: "a formal agreement between countries",
        ex: [
          ["The treaty has not been signed yet.", "پیمان‌نامه هنوز امضا نشده است."],
          ["Both sides described the treaty as a first step.", "هر دو طرف پیمان‌نامه را گامی نخست خواندند."],
        ],
        col: ["sign a treaty", "a peace treaty"],
        note: "treaty قابل‌شمارش است و جمع آن treaties؛ با sign می‌آید.",
      },
      {
        slug: "sanction", w: "sanction", fa: "تحریم", ipa: "/ˈsæŋkʃn/", pos: "noun",
        def: "a penalty imposed on a country to change its behaviour",
        ex: [
          ["The sanctions will be reviewed next week.", "تحریم‌ها هفتهٔ آینده بازبینی می‌شوند."],
          ["One sanction hit the banks hardest.", "یک تحریم بیشترین ضربه را به بانک‌ها زد."],
        ],
        col: ["impose sanctions", "lift sanctions"],
        note: "در این معنا بیشتر جمع می‌آید: sanctions؛ با impose و lift همراه است.",
      },
      {
        slug: "multilateral", w: "multilateral", fa: "چندجانبه", ipa: "/ˌmʌltiˈlætərəl/", pos: "adjective",
        def: "involving several countries",
        ex: [
          ["The solution has to be multilateral.", "راه‌حل باید چندجانبه باشد."],
          ["A multilateral framework took ten years to build.", "ساختن چارچوبی چندجانبه ده سال طول کشید."],
        ],
        col: ["a multilateral agreement", "multilateral talks"],
        note: "متضاد آن unilateral (یک‌جانبه) است؛ bilateral یعنی دوجانبه.",
      },
      {
        slug: "deterrence", w: "deterrence", fa: "بازدارندگی", ipa: "/dɪˈterəns/", pos: "noun",
        def: "stopping an action by making it costly",
        ex: [
          ["Deterrence only works if the threat is credible.", "بازدارندگی فقط وقتی کار می‌کند که تهدید باورپذیر باشد."],
          ["The strategy relies on deterrence, not dialogue.", "این استراتژی به بازدارندگی تکیه دارد، نه گفت‌وگو."],
        ],
        col: ["a policy of deterrence", "credible deterrence"],
        note: "deterrence غیرقابل‌شمارش است؛ فعل آن deter somebody from something.",
      },
      {
        slug: "envoy", w: "envoy", fa: "فرستادهٔ دیپلماتیک", ipa: "/ˈenvɔɪ/", pos: "noun",
        def: "a person sent to represent their country",
        ex: [
          ["The envoy returns to the capital next week.", "فرستاده هفتهٔ آینده به پایتخت برمی‌گردد."],
          ["A special envoy was sent to open talks.", "فرستادهٔ ویژه‌ای برای آغاز مذاکرات فرستاده شد."],
        ],
        col: ["a special envoy", "send an envoy"],
        note: "envoy قابل‌شمارش است؛ special envoy یعنی فرستادهٔ ویژه.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "sovereignty", gloss: "حاکمیت",
        options: ["حاکمیت", "پیمان‌نامه", "تحریم", "سفارت"],
        note: "sovereignty حق یک کشور برای ادارهٔ خودش است.",
      },
      {
        k: "mcq", word: "treaty", gloss: "پیمان‌نامه",
        options: ["پیمان‌نامه", "فرستاده", "بازدارندگی", "مذاکره"],
        note: "treaty توافق رسمی میان کشورها است.",
      },
      {
        k: "mcq", word: "envoy", gloss: "فرستادهٔ دیپلماتیک",
        options: ["فرستادهٔ دیپلماتیک", "وزیر", "تحریم", "همه‌پرسی"],
        note: "envoy کسی است که کشورش او را برای نمایندگی می‌فرستد.",
      },
      {
        k: "blank", sentence: "They ___ have misread the signals from the summit.",
        accepted: ["must", "may"], skill: "VOCABULARY",
        note: "must have حدس قوی و may have احتمال ضعیف‌تر است.",
      },
      {
        k: "blank", sentence: "The talks ___ have failed for a simpler reason.",
        accepted: ["may", "might"], skill: "VOCABULARY",
        note: "may have و might have هر دو احتمال دربارهٔ گذشته‌اند.",
      },
      {
        k: "recall", prompt: "بازدارندگی", accepted: ["deterrence"],
        note: "بازدارندگی deterrence است و فعل آن deter.",
      },
      {
        k: "tr", prompt: "آن‌ها باید نشانه‌ها را اشتباه خوانده باشند.",
        accepted: [
          "they must have misread the signals",
          "they must have misread the signal",
        ],
        note: "must have + قسمت سوم: حدس قوی دربارهٔ گذشته.",
      },
      {
        k: "listen", text: "They must have misread the signals from the summit.",
        note: "به must have misread گوش کنید: حدس دربارهٔ گذشته.",
      },
      {
        k: "prod", prompt: "بگو آن‌ها باید نشانه‌های نشست را اشتباه خوانده باشند.",
        mustUse: ["must", "have"], minWords: 7,
        accepted: ["They must have misread the signals from the summit."],
        hint: "با They must have… شروع کن.",
        note: "ترتیب لنگرها: must پیش از have می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "نشست خبری پس از مذاکرات دوجانبه", partner: "سخنگوی وزارت خارجه",
        prompt: "بگو آن‌ها باید نشانه‌های نشست را اشتباه خوانده باشند.",
        question: "Why did the talks break down?",
        questionFa: "چرا مذاکرات شکست خورد؟",
        accepted: [
          "They must have misread the signals from the summit.",
          "They must have misread the signals.",
        ],
        note: "must have + قسمت سوم حدس قوی دربارهٔ گذشته است.",
      },
      {
        scene: "نشست خبری پس از مذاکرات دوجانبه", partner: "سخنگوی وزارت خارجه",
        prompt: "بگو حاکمیت هر دو کشور محترم است.",
        question: "What is the basis of the agreement?",
        questionFa: "مبنای توافق چیست؟",
        accepted: [
          "The sovereignty of both countries is respected.",
          "It respects the sovereignty of both states.",
        ],
        note: "the sovereignty of both countries مفرد است: is respected.",
      },
      {
        scene: "نشست خبری پس از مذاکرات دوجانبه", partner: "سخنگوی وزارت خارجه",
        prompt: "بگو پیمان‌نامه هنوز امضا نشده است.",
        question: "Has the treaty been signed?",
        questionFa: "پیمان‌نامه امضا شده؟",
        accepted: [
          "The treaty has not been signed yet.",
          "No, the treaty is not signed yet.",
        ],
        note: "yet در جملهٔ منفی می‌آید: has not been signed yet.",
      },
      {
        scene: "نشست خبری پس از مذاکرات دوجانبه", partner: "سخنگوی وزارت خارجه",
        prompt: "بگو تحریم‌ها هفتهٔ آینده بازبینی می‌شوند.",
        question: "What about the sanctions?",
        questionFa: "تحریم‌ها چه می‌شوند؟",
        accepted: [
          "The sanctions will be reviewed next week.",
          "They will be reviewed next week.",
        ],
        note: "sanctions جمع است، پس will be reviewed.",
      },
      {
        scene: "نشست خبری پس از مذاکرات دوجانبه", partner: "سخنگوی وزارت خارجه",
        prompt: "بگو راه‌حل باید چندجانبه باشد.",
        question: "Can two countries solve this alone?",
        questionFa: "دو کشور می‌توانند تنهایی حلش کنند؟",
        accepted: [
          "The solution has to be multilateral.",
          "No, the solution must be multilateral.",
        ],
        note: "multilateral صفت است و پس از be می‌آید.",
      },
      {
        scene: "نشست خبری پس از مذاکرات دوجانبه", partner: "سخنگوی وزارت خارجه",
        prompt: "بگو فرستاده هفتهٔ آینده به پایتخت بازمی‌گردد.",
        question: "When does the envoy return?",
        questionFa: "فرستاده کِی برمی‌گردد؟",
        accepted: [
          "The envoy returns to the capital next week.",
          "He returns to the capital next week.",
        ],
        note: "the envoy مفرد است: returns، نه return.",
      },
    ],
  },
];
