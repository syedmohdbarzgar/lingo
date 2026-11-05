// v48 content, part 1 — C1 topics 1 and 2: executive communication and
// advanced negotiation. Read by `v48_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.executive.lesson-01",
      order: 144,
      title: "Executive Communication",
      titleFa: "ارتباطات مدیریتی",
      topic: "Business",
      tip: [
        "## قاعده",
        "پس از قید منفی در آغاز جمله، فعل کمکی پیش از فاعل می‌آید: Rarely have we seen such a clear mandate.",
        "Not only … but also ساختار پرکاربرد دیگری است: Not only did the board reject the plan, but it also cut the budget.",
        "Under no circumstances و on no account هم وارونگی می‌خواهند: Under no circumstances should the message cascade without a briefing.",
        "بدون قید منفی در ابتدا، جملهٔ عادی است: We have rarely seen such a clear mandate.",
        "## جدول",
        "Rarely have we seen such a clear mandate. — بهندرت چنین اختیار روشنی دیده‌ایم.",
        "Not only did the board reject the plan, but it also cut the budget. — هیئت‌مدیره نه‌تنها طرح را رد کرد، بودجه را هم کم کرد.",
        "Under no circumstances should the message cascade without a briefing. — تحت هیچ شرایطی پیام نباید بدون یک جلسهٔ توجیهی پایین برود.",
        "## مثال‌ها",
        "The board gave the CEO a clear mandate to restructure. — هیئت‌مدیره اختیار روشنی برای بازسازی به مدیرعامل داد.",
        "Alignment across the three teams took two weeks. — هم‌سویی سه تیم دو هفته طول کشید.",
        "The morning briefing lasts fifteen minutes. — جلسهٔ توجیهی صبحگاهی پانزده دقیقه طول می‌کشد.",
        "## خطاهای رایج",
        "با وارونگی، شکل پرسشی فعل می‌آید: did the board reject ✓ / the board did reject ✗.",
        "mandate با to می‌آید: a mandate to restructure.",
        "alignment غیرقابل‌شمارش است: there is no alignment ✓ / there are no alignments ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.inversion-negative-adverbials",
    vocab: {
      id: "vocab.executive-communication",
      title: "Executive Communication",
      titleFa: "ارتباطات مدیریتی",
      prerequisites: ["vocab.strategy-evaluation"],
    },
    words: [
      {
        slug: "mandate", w: "mandate", fa: "اختیار؛ حکم", ipa: "/ˈmændeɪt/", pos: "noun",
        def: "the official authority to do something",
        ex: [
          ["The board gave the chief executive a clear mandate to restructure.", "هیئت‌مدیره اختیار روشنی برای بازسازی به مدیرعامل داد."],
          ["Her mandate runs until next spring.", "اختیار او تا بهار آینده ادامه دارد."],
        ],
        col: ["a clear mandate", "a mandate to reform"],
        note: "mandate با to می‌آید: a mandate to restructure؛ فعل آن هم to mandate است.",
      },
      {
        slug: "alignment", w: "alignment", fa: "هم‌سویی", ipa: "/əˈlaɪnmənt/", pos: "noun",
        def: "agreement between people, plans or teams",
        ex: [
          ["Alignment across the three teams took two weeks.", "هم‌سویی سه تیم دو هفته طول کشید."],
          ["There is still no alignment on the budget.", "هنوز هیچ هم‌سویی‌ای دربارهٔ بودجه وجود ندارد."],
        ],
        col: ["strategic alignment", "alignment on the budget"],
        note: "alignment غیرقابل‌شمارش است؛ فعل آن align و با with می‌آید: align with the strategy.",
      },
      {
        slug: "briefing", w: "briefing", fa: "جلسهٔ توجیهی", ipa: "/ˈbriːfɪŋ/", pos: "noun",
        def: "a short meeting that gives people the facts they need",
        ex: [
          ["The morning briefing lasts fifteen minutes.", "جلسهٔ توجیهی صبحگاهی پانزده دقیقه طول می‌کشد."],
          ["She wrote a two-page briefing for the chair.", "او یک جلسهٔ توجیهی دو صفحه‌ای برای رئیس نوشت."],
        ],
        col: ["a morning briefing", "hold a briefing"],
        note: "briefing قابل‌شمارش است؛ فعل آن brief است: brief the team.",
      },
      {
        slug: "articulate", w: "articulate", fa: "روشن بیان کردن", ipa: "/ɑːˈtɪkjuleɪt/", pos: "verb",
        def: "to express an idea clearly",
        ex: [
          ["He articulates the strategy in one sentence.", "او استراتژی را در یک جمله روشن بیان می‌کند."],
          ["Directors must articulate why the change matters.", "مدیران باید روشن بگویند چرا این تغییر مهم است."],
        ],
        col: ["articulate a vision", "articulate clearly"],
        note: "articulate فعل است با مفعول مستقیم؛ صفت آن هم articulate (به معنی خوش‌بیان) است.",
      },
      {
        slug: "directive", w: "directive", fa: "دستورالعمل", ipa: "/dɪˈrektɪv/", pos: "noun",
        def: "an official instruction from a person in authority",
        ex: [
          ["A short directive went out to all managers.", "یک دستورالعمل کوتاه برای همهٔ مدیران فرستاده شد."],
          ["The directive was ignored at two sites.", "دستورالعمل در دو سایت نادیده گرفته شد."],
        ],
        col: ["issue a directive", "a clear directive"],
        note: "directive اسم است و با issue می‌آید: issue a directive؛ خودِ کلمه صفت هم می‌شود (a directive message).",
      },
      {
        slug: "cascade", w: "cascade", fa: "مرحله‌به‌مرحله منتقل کردن", ipa: "/kæˈskeɪd/", pos: "verb",
        def: "to pass information down through the levels of an organisation",
        ex: [
          ["We cascade the message through team leaders.", "پیام را از طریق سرپرست‌های تیم پایین می‌فرستیم."],
          ["News cascades quickly in a small company.", "در یک شرکت کوچک خبر سریع پایین می‌رود."],
        ],
        col: ["cascade a message", "cascade down"],
        note: "cascade در این معنا فعل است و با through می‌آید: cascade through managers.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "mandate", gloss: "اختیار", 
        options: ["اختیار", "بودجه", "جلسه", "گزارش"],
        note: "mandate اختیار رسمی برای انجام کاری است.",
      },
      {
        k: "mcq", word: "briefing", gloss: "جلسهٔ توجیهی",
        options: ["جلسهٔ توجیهی", "قرارداد", "دستورالعمل", "مهلت"],
        note: "briefing جلسهٔ کوتاهی است که واقعیت‌های لازم را می‌دهد.",
      },
      {
        k: "mcq", word: "alignment", gloss: "هم‌سویی",
        options: ["هم‌سویی", "رقابت", "سود", "تأخیر"],
        note: "alignment توافق میان آدم‌ها یا برنامه‌ها است.",
      },
      {
        k: "blank", sentence: "Rarely ___ the board seen such a clear mandate.",
        accepted: ["has", "had"], skill: "VOCABULARY",
        note: "پس از قید منفی فعل کمکی پیش از فاعل می‌آید؛ has حال و had گذشته است.",
      },
      {
        k: "blank", sentence: "Under no circumstances ___ the message cascade without a briefing.",
        accepted: ["should", "must"], skill: "VOCABULARY",
        note: "با under no circumstances فعل وجهی پیش از فاعل می‌آید.",
      },
      {
        k: "recall", prompt: "دستورالعمل", accepted: ["directive"],
        note: "دستورالعمل رسمی directive است و با issue می‌آید.",
      },
      {
        k: "tr", prompt: "بهندرت چنین پیام روشنی دیده‌ایم.",
        accepted: [
          "rarely have we seen such a clear message",
          "never have we seen such a clear message",
        ],
        note: "قید منفی در آغاز جمله فعل کمکی را پیش از فاعل می‌آورد.",
      },
      {
        k: "listen", text: "Rarely has the board seen such a clear mandate.",
        note: "به Rarely has the board گوش کنید: وارونگی، not rarely the board has.",
      },
      {
        k: "prod", prompt: "با ساختار وارونه بگو بهندرت چنین استراتژی روشنی دیده‌ایم.",
        mustUse: ["rarely", "have"], minWords: 7,
        accepted: ["Rarely have we seen such a clear strategy."],
        hint: "با Rarely have we… شروع کن.",
        note: "ترتیب لنگرها: rarely پیش از have می‌آید و فاعل بعد از فعل کمکی.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ هفتگی کمیتهٔ اجرایی", partner: "مدیرعامل",
        prompt: "بگو بهندرت چنین پیام روشنی دیده‌ایم.",
        question: "How clear was the message?",
        questionFa: "پیام چقدر روشن بود؟",
        accepted: [
          "Rarely have we seen such a clear message.",
          "Never have we had a clearer message.",
        ],
        note: "قید منفی در آغاز، فعل کمکی را پیش از فاعل می‌آورد.",
      },
      {
        scene: "جلسهٔ هفتگی کمیتهٔ اجرایی", partner: "مدیرعامل",
        prompt: "بگو هیئت‌مدیره اختیار روشنی برای بازسازی به مدیرعامل داد.",
        question: "What did the board give her?",
        questionFa: "هیئت‌مدیره چه چیزی به او داد؟",
        accepted: [
          "The board gave the chief executive a clear mandate to restructure.",
          "The board has given her a clear mandate.",
        ],
        note: "give somebody something؛ mandate با to می‌آید. پاسخ گفتاری «chief executive» می‌گوید، چون بازشناس گفتار «CEO» را نمی‌شنود.",
      },
      {
        scene: "جلسهٔ هفتگی کمیتهٔ اجرایی", partner: "مدیرعامل",
        prompt: "بگو پیام را از طریق سرپرست‌های تیم پایین می‌فرستیم.",
        question: "How does the message reach everyone?",
        questionFa: "پیام چطور به همه می‌رسد؟",
        accepted: [
          "We cascade the message through team leaders.",
          "The message cascades through the team leaders.",
        ],
        note: "cascade with through: از مسیر سرپرست‌ها پایین می‌رود.",
      },
      {
        scene: "جلسهٔ هفتگی کمیتهٔ اجرایی", partner: "مدیرعامل",
        prompt: "بگو جلسهٔ توجیهی صبحگاهی پانزده دقیقه طول می‌کشد.",
        question: "How long is the morning briefing?",
        questionFa: "جلسهٔ توجیهی صبح چند دقیقه است؟",
        accepted: [
          "The morning briefing lasts fifteen minutes.",
          "It lasts fifteen minutes.",
        ],
        note: "last به معنی «طول کشیدن» است، نه «آخرین».",
      },
      {
        scene: "جلسهٔ هفتگی کمیتهٔ اجرایی", partner: "مدیرعامل",
        prompt: "بگو هنوز هیچ هم‌سویی‌ای دربارهٔ بودجه وجود ندارد.",
        question: "Is everyone agreed on the budget?",
        questionFa: "همه دربارهٔ بودجه هم‌نظرند؟",
        accepted: [
          "There is still no alignment on the budget.",
          "No, there is still no alignment on the budget.",
        ],
        note: "alignment غیرقابل‌شمارش است، پس there is می‌گیرد.",
      },
      {
        scene: "جلسهٔ هفتگی کمیتهٔ اجرایی", partner: "مدیرعامل",
        prompt: "بگو مدیران باید روشن بگویند چرا این تغییر مهم است.",
        question: "What do the directors need to do?",
        questionFa: "مدیران چه باید بکنند؟",
        accepted: [
          "Directors must articulate why the change matters.",
          "They must articulate why the change matters.",
        ],
        note: "articulate با مفعول مستقیم می‌آید، بدون about.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.negotiation.lesson-01",
      order: 145,
      title: "Advanced Negotiation",
      titleFa: "مذاکره‌های پیچیده",
      topic: "Business",
      tip: [
        "## قاعده",
        "شرطی مختلط دو زمان را در هم می‌آمیزد: گذشته در شرط، حال در نتیجه — If we had settled in March, we would be in a stronger position now.",
        "برعکسش هم رایج است: حال در شرط، گذشته در نتیجه — If they were serious, they would have signed the draft last week.",
        "برای احتمال گذشته و نتیجهٔ حال: If we had conceded earlier, we might be in a stronger position now.",
        "## جدول",
        "If we had settled in March, we would be in a stronger position now. — اگر در مارس توافق کرده بودیم، الان در موقعیت قوی‌تری بودیم.",
        "If they were serious, they would have signed the draft last week. — اگر جدی بودند، هفتهٔ پیش پیش‌نویس را امضا می‌کردند.",
        "If we had conceded earlier, we might be in a stronger position now. — اگر زودتر کوتاه آمده بودیم، ممکن بود الان در موقعیت بهتری باشیم.",
        "## مثال‌ها",
        "The talks have reached a deadlock over price. — مذاکرات بر سر قیمت به بن‌بست خورده است.",
        "We have a clear walkaway point. — ما نقطهٔ ترک مذاکرهٔ روشنی داریم.",
        "If they do not move, we will go to arbitration. — اگر تکان نخورند، به داوری می‌رویم.",
        "## خطاهای رایج",
        "در نیمهٔ اولِ مختلط، had + قسمت سوم می‌آید نه would have: If we had settled ✓ / If we would have settled ✗.",
        "در نیمهٔ دومِ مختلط، would + مصدر ساده است، نه will: we would be ✓ / we will be ✗.",
        "walkaway در این معنا اسم است و یک واژه: a clear walkaway point.",
      ].join("\n"),
    },
    grammarNode: "grammar.mixed-conditionals",
    vocab: {
      id: "vocab.advanced-negotiation",
      title: "Advanced Negotiation",
      titleFa: "مذاکره‌های پیچیده",
      prerequisites: ["vocab.workplace-conflict"],
    },
    words: [
      {
        slug: "deadlock", w: "deadlock", fa: "بن‌بست", ipa: "/ˈdedlɒk/", pos: "noun",
        def: "a point where neither side will move",
        ex: [
          ["The talks have reached a deadlock over price.", "مذاکرات بر سر قیمت به بن‌بست خورده است."],
          ["A third party is needed to break the deadlock.", "برای شکستن بن‌بست به طرف ثالث نیاز است."],
        ],
        col: ["break a deadlock", "reach a deadlock"],
        note: "deadlock با reach می‌آید و با break شکسته می‌شود؛ فعل آن هم to deadlock است.",
      },
      {
        slug: "walkaway", w: "walkaway", fa: "نقطهٔ ترک مذاکره", ipa: "/ˈwɔːkəweɪ/", pos: "noun",
        def: "the point beyond which you will not go",
        ex: [
          ["We have a clear walkaway point.", "ما نقطهٔ ترک مذاکرهٔ روشنی داریم."],
          ["Never share your walkaway with the other side.", "هرگز نقطهٔ ترک مذاکره‌ات را با طرف مقابل در میان نگذار."],
        ],
        col: ["a clear walkaway", "the walkaway price"],
        note: "walkaway در این معنا یک واژه است و نه walk away (که فعل است).",
      },
      {
        slug: "arbitration", w: "arbitration", fa: "داوری", ipa: "/ˌɑːbɪˈtreɪʃn/", pos: "noun",
        def: "settling a dispute through a neutral third person",
        ex: [
          ["If they do not move, we will go to arbitration.", "اگر تکان نخورند، به داوری می‌رویم."],
          ["Both sides accepted arbitration in the end.", "هر دو طرف در پایان داوری را پذیرفتند."],
        ],
        col: ["go to arbitration", "binding arbitration"],
        note: "arbitration غیرقابل‌شمارش است؛ شخص داور arbitrator و فعل آن arbitrate است.",
      },
      {
        slug: "brinkmanship", w: "brinkmanship", fa: "ریسک تا مرز بحران", ipa: "/ˈbrɪŋkmənʃɪp/", pos: "noun",
        def: "pushing a situation to the edge of disaster to win",
        ex: [
          ["Brinkmanship worked once and then stopped working.", "ریسک تا مرز بحران یک بار جواب داد و بعد دیگر جواب نداد."],
          ["Their brinkmanship scared off two investors.", "ریسک تا مرز بحرانشان دو سرمایه‌گذار را ترساند."],
        ],
        col: ["pure brinkmanship", "a game of brinkmanship"],
        note: "brinkmanship غیرقابل‌شمارش است؛ به رفتار خطرناکی اشاره دارد که تا لبهٔ بحران پیش می‌رود.",
      },
      {
        slug: "contingency", w: "contingency", fa: "برنامهٔ جایگزین", ipa: "/kənˈtɪndʒənsi/", pos: "noun",
        def: "a plan for something that might happen",
        ex: [
          ["We have a contingency for every outcome.", "برای هر نتیجه‌ای برنامهٔ جایگزین داریم."],
          ["The contingency plan was never needed.", "برنامهٔ جایگزین هرگز لازم نشد."],
        ],
        col: ["a contingency plan", "plan for contingencies"],
        note: "contingency قابل‌شمارش است؛ contingency plan یعنی برنامهٔ جایگزین.",
      },
      {
        slug: "stalemate", w: "stalemate", fa: "وضعیت بی‌برنده", ipa: "/ˈsteɪlmeɪt/", pos: "noun",
        def: "a position where no side can win",
        ex: [
          ["This stalemate suits nobody.", "این وضعیت بی‌برنده به نفع هیچ‌کس نیست."],
          ["After three weeks the talks ended in stalemate.", "بعد از سه هفته مذاکرات با وضعیت بی‌برنده تمام شد."],
        ],
        col: ["end in stalemate", "break the stalemate"],
        note: "stalemate با end in می‌آید؛ deadlock بیشتر بر «توقف» و stalemate بر «بی‌برنده بودن» تأکید دارد.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "deadlock", gloss: "بن‌بست",
        options: ["بن‌بست", "توافق", "امتیاز", "داوری"],
        note: "deadlock جایی است که هیچ طرفی تکان نمی‌خورد.",
      },
      {
        k: "mcq", word: "brinkmanship", gloss: "ریسک تا مرز بحران",
        options: ["ریسک تا مرز بحران", "سود تضمین‌شده", "قرارداد محرمانه", "مهلت قانونی"],
        note: "brinkmanship فشار آوردن تا لبهٔ بحران برای برنده شدن است.",
      },
      {
        k: "mcq", word: "contingency", gloss: "برنامهٔ جایگزین",
        options: ["برنامهٔ جایگزین", "قیمت پایه", "شاهد", "غرامت"],
        note: "contingency برنامه‌ای برای چیزی است که ممکن است پیش بیاید.",
      },
      {
        k: "blank", sentence: "If we had settled in March, we ___ be in a stronger position now.",
        accepted: ["would", "might"], skill: "VOCABULARY",
        note: "نتیجهٔ حالِ شرطی مختلط با would یا might می‌آید، نه will.",
      },
      {
        k: "blank", sentence: "If they were serious, they ___ have signed the draft last week.",
        accepted: ["would", "might"], skill: "VOCABULARY",
        note: "شرط حال و نتیجهٔ گذشته: would have + قسمت سوم.",
      },
      {
        k: "recall", prompt: "داوری", accepted: ["arbitration"],
        note: "داوری arbitration است و شخص داور arbitrator.",
      },
      {
        k: "tr", prompt: "اگر در مارس توافق کرده بودیم، الان در موقعیت قوی‌تری بودیم.",
        accepted: [
          "if we had settled in march we would be in a stronger position now",
          "if we had settled in march, we would be in a stronger position now",
        ],
        note: "شرط گذشته و نتیجهٔ حال: had settled … would be.",
      },
      {
        k: "listen", text: "If we had settled in March, we would be in a stronger position now.",
        note: "به had settled و would be گوش کنید: دو زمان متفاوت در یک جمله.",
      },
      {
        k: "prod", prompt: "بگو اگر در مارس توافق کرده بودیم، الان در موقعیت قوی‌تری بودیم.",
        mustUse: ["if", "would"], minWords: 9,
        accepted: ["If we had settled in March we would be in a stronger position now."],
        hint: "با If we had… شروع کن و با we would be… ادامه بده.",
        note: "ترتیب لنگرها: if در آغاز و would در نتیجه می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "مذاکرهٔ قرارداد با تأمین‌کننده", partner: "مدیر خرید",
        prompt: "بگو اگر در مارس توافق کرده بودیم، الان در موقعیت قوی‌تری بودیم.",
        question: "Why are we under pressure now?",
        questionFa: "چرا الان تحت فشاریم؟",
        accepted: [
          "If we had settled in March, we would be in a stronger position now.",
          "Because if we had settled in March, we would be stronger now.",
        ],
        note: "شرط گذشته با نتیجهٔ حال: had settled … would be.",
      },
      {
        scene: "مذاکرهٔ قرارداد با تأمین‌کننده", partner: "مدیر خرید",
        prompt: "بگو مذاکرات بر سر قیمت به بن‌بست خورده است.",
        question: "Where do the talks stand?",
        questionFa: "مذاکرات کجاست؟",
        accepted: [
          "The talks have reached a deadlock over price.",
          "We are in a deadlock over the price.",
        ],
        note: "reach a deadlock over چیزی: بن‌بست بر سر یک موضوع.",
      },
      {
        scene: "مذاکرهٔ قرارداد با تأمین‌کننده", partner: "مدیر خرید",
        prompt: "بگو ما نقطهٔ ترک مذاکرهٔ روشنی داریم.",
        question: "What is our limit?",
        questionFa: "حد ما کجاست؟",
        accepted: [
          "We have a clear walkaway point.",
          "Our walkaway is clear and fixed.",
        ],
        note: "walkaway اینجا اسم است: the walkaway, not walk away.",
      },
      {
        scene: "مذاکرهٔ قرارداد با تأمین‌کننده", partner: "مدیر خرید",
        prompt: "بگو اگر تکان نخورند، به داوری می‌رویم.",
        question: "What if they do not move?",
        questionFa: "اگر تکان نخورند چه؟",
        accepted: [
          "If they do not move, we will go to arbitration.",
          "Then we will take it to arbitration.",
        ],
        note: "go to arbitration یعنی به داوری رفتن.",
      },
      {
        scene: "مذاکرهٔ قرارداد با تأمین‌کننده", partner: "مدیر خرید",
        prompt: "بگو برای هر نتیجه‌ای برنامهٔ جایگزین داریم.",
        question: "Do we have a plan B?",
        questionFa: "برنامهٔ دوم داریم؟",
        accepted: [
          "We have a contingency for every outcome.",
          "Yes, we have a contingency plan for every outcome.",
        ],
        note: "contingency for something: برنامهٔ جایگزین برای یک احتمال.",
      },
      {
        scene: "مذاکرهٔ قرارداد با تأمین‌کننده", partner: "مدیر خرید",
        prompt: "بگو این وضعیت بی‌برنده به نفع هیچ‌کس نیست.",
        question: "Is the stalemate in their interest?",
        questionFa: "این بن‌بست به نفع آن‌هاست؟",
        accepted: [
          "This stalemate suits nobody.",
          "No, the stalemate suits nobody.",
        ],
        note: "suit somebody یعنی به نفع کسی بودن؛ اینجا nobody با فعل مثبت می‌آید.",
      },
    ],
  },
];
