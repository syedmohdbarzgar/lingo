// v53 content, part 2 — C1 topics 28 and 29: stakeholder communication and
// specialist presentations. Read by `v53_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.stakeholders.lesson-01",
      order: 166,
      title: "Stakeholder Communication",
      titleFa: "مدیریت ارتباط با ذی‌نفعان",
      topic: "Communication",
      tip: [
        "## قاعده",
        "در گزارش ارتباط با ذی‌نفعان، پرسش‌ها را غیرمستقیم نقل کن: ترتیب جمله خبری می‌ماند و فعل کمکی به گذشته برمی‌گردد.",
        "The board asked when the pilot would start. — نه «when would the pilot start».",
        "برای پرسش بله/خیر از whether یا if استفاده کن: She asked whether the figures were final.",
        "## جدول",
        "The board asked when the pilot would start. — هیئت‌مدیره پرسید آزمون آزمایشی چه زمانی شروع می‌شود.",
        "She asked whether the figures were final. — او پرسید آیا ارقام نهایی هستند.",
        "They wanted to know how often updates would arrive. — آن‌ها می‌خواستند بدانند به‌روزرسانی‌ها هر چند وقت می‌رسند.",
        "## مثال‌ها",
"Engagement is measured by who turns up. — مشارکت‌جویی با این سنجیده می‌شود که چه کسی می‌آید.",
        "She is our liaison with the ministry. — او رابط ما با وزارتخانه است.",
        "Clear expectations prevent most surprises. — انتظارات روشن بیشتر غافلگیری‌ها را جلوگیری می‌کند.",
        "## خطاهای رایج",
        "در نقل غیرمستقیم ترتیب پرسشی حذف می‌شود: asked when the pilot would start ✓ / asked when would the pilot start ✗.",
        "whether برای پرسش بله/خیر است، نه for چه چیزی: asked whether ✓ / asked for whether ✗.",
        "liaison تلفظ /liˈeɪzn/ دارد و h آن خوانده نمی‌شود.",
      ].join("\n"),
    },
    grammarNode: "grammar.reported-questions",
    vocab: {
      id: "vocab.stakeholder-communication",
      title: "Stakeholder Communication",
      titleFa: "مدیریت ارتباط با ذی‌نفعان",
      prerequisites: ["vocab.executive-communication"],
    },
    words: [
      {
        slug: "engagement", w: "engagement", fa: "مشارکت‌جویی", ipa: "/ɪnˈɡeɪdʒmənt/", pos: "noun",
        def: "how far people take part and stay involved",
        ex: [
          ["Engagement is measured by who turns up.", "مشارکت‌جویی با این سنجیده می‌شود که چه کسی می‌آید."],
          ["The engagement plan lists every audience.", "برنامهٔ مشارکت‌جویی هر مخاطب را فهرست می‌کند."],
        ],
        col: ["stakeholder engagement", "an engagement plan"],
        note: "engagement غیرقابل‌شمارش است در این معنا؛ فعل آن engage with somebody.",
      },
      {
        slug: "liaison", w: "liaison", fa: "رابط", ipa: "/liˈeɪzn/", pos: "noun",
        def: "the person who keeps two groups in contact",
        ex: [
          ["She is our liaison with the ministry.", "او رابط ما با وزارتخانه است."],
          ["A liaison answers before a rumour starts.", "یک رابط پیش از شکل‌گیری شایعه پاسخ می‌دهد."],
        ],
        col: ["act as liaison", "a liaison officer"],
        note: "liaison قابل‌شمارش است و h آن خوانده نمی‌شود: /liˈeɪzn/.",
      },
      {
        slug: "escalation", w: "escalation", fa: "ارجاع به سطح بالاتر", ipa: "/ˌeskəˈleɪʃn/", pos: "noun",
        def: "passing an issue up to someone with more authority",
        ex: [
          ["Escalation is a promise, not a punishment.", "ارجاع به سطح بالاتر یک تعهد است، نه تنبیه."],
          ["The escalation path is printed on the card.", "مسیر ارجاع روی کارت چاپ شده است."],
        ],
        col: ["an escalation path", "avoid escalation"],
        note: "escalation غیرقابل‌شمارش است؛ فعل آن escalate.",
      },
      {
        slug: "expectation", w: "expectation", fa: "انتظار", ipa: "/ˌekspekˈteɪʃn/", pos: "noun",
        def: "what someone believes will happen",
        ex: [
          ["Clear expectations prevent most surprises.", "انتظارات روشن بیشتر غافلگیری‌ها را جلوگیری می‌کند."],
          ["The expectation was a weekly note, not a meeting.", "انتظار یک یادداشت هفتگی بود، نه یک جلسه."],
        ],
        col: ["manage expectations", "set expectations"],
        note: "expectation قابل‌شمارش است و اغلب جمع می‌آید: expectations.",
      },
      {
        slug: "champion", w: "champion", fa: "حامی درون‌سازمانی", ipa: "/ˈtʃæmpiən/", pos: "noun",
        def: "someone inside the organisation who argues for a project",
        ex: [
          ["A champion inside the team keeps the project alive.", "یک حامی درون تیم پروژه را زنده نگه می‌دارد."],
          ["Find a champion before you need one.", "پیش از آنکه به حامی نیاز پیدا کنی، یکی پیدا کن."],
        ],
        col: ["an executive champion", "win a champion"],
        note: "در این معنا champion «حامی» است، نه «قهرمان»؛ فعل آن به معنی «حمایت کردن» هم می‌آید.",
      },
      {
        slug: "cadence", w: "cadence", fa: "آهنگ تکرار", ipa: "/ˈkeɪdns/", pos: "noun",
        def: "the regular rhythm of meetings or updates",
        ex: [
          ["The cadence is one update every fortnight.", "آهنگ تکرار یک به‌روزرسانی هر دو هفته است."],
          ["A monthly cadence beats a daily flood.", "آهنگ ماهانه از سیل روزانه بهتر است."],
        ],
        col: ["a reporting cadence", "set the cadence"],
        note: "cadence قابل‌شمارش است؛ در موسیقی هم به معنی «ضرباهنگ» است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "liaison", gloss: "رابط",
        options: ["رابط", "حامی درون‌سازمانی", "انتظار", "آهنگ تکرار"],
        note: "liaison کسی است که دو گروه را در تماس نگه می‌دارد.",
      },
      {
        k: "mcq", word: "escalation", gloss: "ارجاع به سطح بالاتر",
        options: ["ارجاع به سطح بالاتر", "مشارکت‌جویی", "آهنگ تکرار", "انتظار"],
        note: "escalation بالا بردن یک موضوع به کسی با اختیار بیشتر است.",
      },
      {
        k: "mcq", word: "champion", gloss: "حامی درون‌سازمانی",
        options: ["حامی درون‌سازمانی", "رابط", "مسیر ارجاع", "برنامهٔ ارتباط"],
        note: "champion در این بافت کسی است که از پروژه در سازمان دفاع می‌کند.",
      },
      {
        k: "blank", sentence: "The board asked when the pilot ___ start.",
        accepted: ["would", "will"], skill: "VOCABULARY",
        note: "در نقل غیرمستقیم فعل کمکی به گذشته برمی‌گردد: would start.",
      },
      {
        k: "blank", sentence: "She asked ___ the figures were final.",
        accepted: ["whether", "if"], skill: "VOCABULARY",
        note: "برای پرسش بله/خیر از whether یا if استفاده کن.",
      },
      {
        k: "recall", prompt: "رابط", accepted: ["liaison"],
        note: "رابط liaison است و h آن خوانده نمی‌شود.",
      },
      {
        k: "tr", prompt: "هیئت‌مدیره پرسید که آزمون آزمایشی چه زمانی شروع می‌شود.",
        accepted: [
          "the board asked when the pilot would start",
          "the board asked when the pilot would begin",
        ],
        note: "ترتیب جملهٔ خبری در نقل غیرمستقیم حفظ می‌شود.",
      },
      {
        k: "listen", text: "The board asked when the pilot would start.",
        note: "به would start گوش کنید: گذشته‌سازی در نقل غیرمستقیم.",
      },
      {
        k: "prod", prompt: "بگو او پرسید که آیا ارقام نهایی هستند.",
        mustUse: ["asked", "whether"], minWords: 5,
        accepted: ["She asked whether the figures were final."],
        hint: "با She asked whether… شروع کن.",
        note: "ترتیب لنگرها: asked پیش از whether.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ به‌روزرسانی ذی‌نفعان برنامه", partner: "مدیر برنامه",
        prompt: "بگو هیئت‌مدیره پرسید آزمون آزمایشی چه زمانی شروع می‌شود.",
        question: "What did the board want to know?",
        questionFa: "هیئت‌مدیره چه می‌خواست بداند؟",
        accepted: [
          "The board asked when the pilot would start.",
          "They asked when the pilot would start.",
        ],
        note: "نقل غیرمستقیم: ترتیب خبری و would + مصدر.",
      },
      {
        scene: "جلسهٔ به‌روزرسانی ذی‌نفعان برنامه", partner: "مدیر برنامه",
        prompt: "بگو او پرسید آیا ارقام نهایی هستند.",
        question: "Was anything unclear?",
        questionFa: "چیزی روشن نبود؟",
        accepted: [
          "She asked whether the figures were final.",
          "She asked if the figures were final.",
        ],
        note: "whether و if هر دو برای پرسش بله/خیر درست‌اند.",
      },
      {
        scene: "جلسهٔ به‌روزرسانی ذی‌نفعان برنامه", partner: "مدیر برنامه",
        prompt: "بگو مشارکت‌جویی با این سنجیده می‌شود که چه کسی می‌آید.",
        question: "How do we measure engagement?",
        questionFa: "مشارکت‌جویی را چطور می‌سنجیم؟",
        accepted: [
          "Engagement is measured by who turns up.",
          "We measure engagement by who turns up.",
        ],
        note: "be measured by: با چیزی سنجیده شدن.",
      },
      {
        scene: "جلسهٔ به‌روزرسانی ذی‌نفعان برنامه", partner: "مدیر برنامه",
        prompt: "بگو انتظارات روشن بیشتر غافلگیری‌ها را جلوگیری می‌کند.",
        question: "Why write expectations down?",
        questionFa: "چرا انتظارات را بنویسیم؟",
        accepted: [
          "Clear expectations prevent most surprises.",
          "Because clear expectations prevent most surprises.",
        ],
        note: "expectations جمع است و فعل جمع می‌گیرد.",
      },
      {
        scene: "جلسهٔ به‌روزرسانی ذی‌نفعان برنامه", partner: "مدیر برنامه",
        prompt: "بگو مسیر ارجاع روی کارت چاپ شده است.",
        question: "How does a team raise a problem?",
        questionFa: "تیم چطور مشکل را بالا می‌برد؟",
        accepted: [
          "The escalation path is printed on the card.",
          "It is printed on the card, the escalation path.",
        ],
        note: "the escalation path: مسیر ارجاع، با حرف تعریف.",
      },
      {
        scene: "جلسهٔ به‌روزرسانی ذی‌نفعان برنامه", partner: "مدیر برنامه",
        prompt: "بگو او رابط ما با وزارتخانه است.",
        question: "Who talks to the ministry?",
        questionFa: "چه کسی با وزارتخانه صحبت می‌کند؟",
        accepted: [
          "She is our liaison with the ministry.",
          "Our liaison with the ministry is she.",
        ],
        note: "liaison with: رابط با چه نهادی.",
      },
    ],
  },
  {
    lesson: {
      id: "c1.specialist-presentations.lesson-01",
      order: 167,
      title: "Specialist Presentations",
      titleFa: "ارائهٔ تخصصی",
      topic: "Communication",
      tip: [
        "## قاعده",
        "در ارائهٔ تخصصی، کاری که انجام شده را با مجهول حال کامل می‌گویی تا نتیجه امروز مهم باشد: The results have been validated by an external lab.",
        "ساختار: have/has + been + قسمت سوم فعل؛ فاعل عمل‌کننده با by می‌آید.",
        "## جدول",
        "The results have been validated by an external lab. — نتایج را یک آزمایشگاه بیرونی تأیید کرده است.",
        "The slides have been cut to eight. — اسلایدها به هشت رسیده‌اند.",
        "The method has been described in the appendix. — روش در پیوست شرح داده شده است.",
        "## مثال‌ها",
        "The takeaway fits on one line. — نکتهٔ کلیدی در یک خط جا می‌شود.",
        "Clear signposting saved the talk. — نشانه‌گذاری روشن ارائه را نجات داد.",
        "Jargon hides a weak argument. — اصطلاح تخصصی استدلال ضعیف را پنهان می‌کند.",
        "## خطاهای رایج",
        "در مجهول حال کامل، been لازم است: have been validated ✓ / have validated by ✗.",
        "by فقط برای فاعل عمل‌کننده است: validated by a lab ✓ / validated with a lab (معنای دیگر) ✗.",
        "takeaway و signposting غیرقابل‌شمارشاند؛ a takeaway هم رایج است.",
      ].join("\n"),
    },
    grammarNode: "grammar.present-perfect-passive",
    vocab: {
      id: "vocab.specialist-presentations",
      title: "Specialist Presentations",
      titleFa: "ارائهٔ تخصصی",
      prerequisites: ["vocab.professional-presentations"],
    },
    words: [
      {
        slug: "jargon", w: "jargon", fa: "اصطلاح تخصصی", ipa: "/ˈdʒɑːɡən/", pos: "noun",
        def: "technical words only insiders understand",
        ex: [
          ["Jargon hides a weak argument.", "اصطلاح تخصصی استدلال ضعیف را پنهان می‌کند."],
          ["Every slide mixed jargon with acronyms.", "هر اسلاید اصطلاح تخصصی را با سرواژه‌ها قاطی کرده بود."],
        ],
        col: ["technical jargon", "avoid jargon"],
        note: "jargon غیرقابل‌شمارش است؛ صفت آن jargony در گفتار غیررسمی.",
      },
      {
        slug: "takeaway", w: "takeaway", fa: "نکتهٔ کلیدی", ipa: "/ˈteɪkəweɪ/", pos: "noun",
        def: "the one idea the audience should remember",
        ex: [
          ["The takeaway fits on one line.", "نکتهٔ کلیدی در یک خط جا می‌شود."],
          ["Give the audience one takeaway, not five.", "به مخاطب یک نکتهٔ کلیدی بده، نه پنج تا."],
        ],
        col: ["the main takeaway", "one clear takeaway"],
        note: "takeaway قابل‌شمارش است؛ در انگلیسی بریتانیایی «غذای بیرون‌بر» هم معنی می‌دهد.",
      },
      {
        slug: "signposting", w: "signposting", fa: "نشانه‌گذاری مسیر ارائه", ipa: "/ˈsaɪnpəʊstɪŋ/", pos: "noun",
        def: "words that tell the audience where the talk is going",
        ex: [
          ["Signposting tells the room where you are going.", "نشانه‌گذاری به حاضرین می‌گوید به کجا می‌روی."],
          ["Clear signposting saved the talk.", "نشانه‌گذاری روشن ارائه را نجات داد."],
        ],
        col: ["verbal signposting", "clear signposting"],
        note: "signposting غیرقابل‌شمارش است؛ فعل آن signpost.",
      },
      {
        slug: "walkthrough", w: "walkthrough", fa: "مرور گام‌به‌گام", ipa: "/ˈwɔːkθruː/", pos: "noun",
        def: "a step-by-step run through a method or tool",
        ex: [
          ["The walkthrough takes four minutes.", "مرور گام‌به‌گام چهار دقیقه طول می‌کشد."],
          ["Start with a short walkthrough of the method.", "با یک مرور کوتاه از روش شروع کن."],
        ],
        col: ["a short walkthrough", "give a walkthrough"],
        note: "walkthrough قابل‌شمارش است؛ در نرم‌افزار «آموزش گام‌به‌گام» هم معنی می‌دهد.",
      },
      {
        slug: "disclaimer", w: "disclaimer", fa: "سلب مسئولیت", ipa: "/dɪsˈkleɪmə/", pos: "noun",
        def: "a statement that limits what a claim covers",
        ex: [
          ["The disclaimer sat on the second slide.", "سلب مسئولیت روی اسلاید دوم بود."],
          ["A disclaimer protects nobody if it is unreadable.", "سلب مسئولیت اگر ناخوانا باشد از کسی محافظت نمی‌کند."],
        ],
        col: ["a legal disclaimer", "add a disclaimer"],
        note: "disclaimer قابل‌شمارش است؛ فعل آن disclaim.",
      },
      {
        slug: "visualisation", w: "visualisation", fa: "تجسم داده", ipa: "/ˌvɪʒuəlaɪˈzeɪʃn/", pos: "noun",
        def: "a chart or diagram that makes data visible",
        ex: [
          ["The visualisation replaced three tables.", "تجسم داده جای سه جدول را گرفت."],
          ["A good visualisation answers one question.", "یک تجسم دادهٔ خوب به یک پرسش پاسخ می‌دهد."],
        ],
        col: ["data visualisation", "a simple visualisation"],
        note: "املای بریتانیایی visualisation و آمریکایی visualization؛ در یک فایل یکی را انتخاب کن.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "jargon", gloss: "اصطلاح تخصصی",
        options: ["اصطلاح تخصصی", "نکتهٔ کلیدی", "سلب مسئولیت", "مرور گام‌به‌گام"],
        note: "jargon واژه‌هایی است که فقط اهل فن می‌فهمند.",
      },
      {
        k: "mcq", word: "takeaway", gloss: "نکتهٔ کلیدی",
        options: ["نکتهٔ کلیدی", "نشانه‌گذاری", "تجسم داده", "سلب مسئولیت"],
        note: "takeaway تنها ایده‌ای است که مخاطب باید به یاد بسپارد.",
      },
      {
        k: "mcq", word: "disclaimer", gloss: "سلب مسئولیت",
        options: ["سلب مسئولیت", "تجسم داده", "اصطلاح تخصصی", "مرور گام‌به‌گام"],
        note: "disclaimer دامنهٔ یک ادعا را محدود می‌کند.",
      },
      {
        k: "blank", sentence: "The results ___ been validated by an external lab.",
        accepted: ["have", "had"], skill: "VOCABULARY",
        note: "مجهول حال کامل با have/has been + قسمت سوم فعل ساخته می‌شود.",
      },
      {
        k: "blank", sentence: "The figures have been ___ by the finance team.",
        accepted: ["checked", "confirmed"], skill: "VOCABULARY",
        note: "پس از have been قسمت سوم فعل می‌آید: checked یا confirmed.",
      },
      {
        k: "recall", prompt: "نکتهٔ کلیدی", accepted: ["takeaway"],
        note: "نکتهٔ کلیدی takeaway است و قابل‌شمارش.",
      },
      {
        k: "tr", prompt: "نتایج را یک آزمایشگاه بیرونی تأیید کرده است.",
        accepted: [
          "the results have been validated by an external lab",
          "the results have been checked by an external lab",
        ],
        note: "have been + قسمت سوم فعل + by: مجهول حال کامل.",
      },
      {
        k: "listen", text: "The visualisation replaced three tables.",
        note: "به visualisation گوش کنید: تجسم داده، نه جدول.",
      },
      {
        k: "prod", prompt: "بگو نتایج را یک آزمایشگاه بیرونی تأیید کرده است.",
        mustUse: ["have", "validated"], minWords: 6,
        accepted: ["The results have been validated by an external lab."],
        hint: "با The results have been… شروع کن.",
        note: "ترتیب لنگرها: have پیش از validated.",
      },
    ],
    dialogue: [
      {
        scene: "کنفرانس تخصصی، پیش از ارائهٔ اصلی", partner: "هماهنگ‌کنندهٔ کنفرانس",
        prompt: "بگو نتایج را یک آزمایشگاه بیرونی تأیید کرده است.",
        question: "Is the data checked?",
        questionFa: "داده بررسی شده است؟",
        accepted: [
          "The results have been validated by an external lab.",
          "Yes, the results have been validated by an external lab.",
        ],
        note: "have been validated by: مجهول حال کامل با فاعل عمل‌کننده.",
      },
      {
        scene: "کنفرانس تخصصی، پیش از ارائهٔ اصلی", partner: "هماهنگ‌کنندهٔ کنفرانس",
        prompt: "بگو تجسم داده جای سه جدول را گرفت.",
        question: "How did you cut the slides?",
        questionFa: "اسلایدها را چطور کوتاه کردید؟",
        accepted: [
          "The visualisation replaced three tables.",
          "It replaced three tables, the visualisation.",
        ],
        note: "replace something: جای چیزی را گرفتن.",
      },
      {
        scene: "کنفرانس تخصصی، پیش از ارائهٔ اصلی", partner: "هماهنگ‌کنندهٔ کنفرانس",
        prompt: "بگو نکتهٔ کلیدی در یک خط جا می‌شود.",
        question: "What is your one message?",
        questionFa: "پیام یگانه‌تان چیست؟",
        accepted: [
          "The takeaway fits on one line.",
          "It fits on one line, the takeaway.",
        ],
        note: "fit on one line: در یک خط جا شدن.",
      },
      {
        scene: "کنفرانس تخصصی، پیش از ارائهٔ اصلی", partner: "هماهنگ‌کنندهٔ کنفرانس",
        prompt: "بگو نشانه‌گذاری روشن ارائه را نجات داد.",
        question: "Why did the last talk work?",
        questionFa: "چرا ارائهٔ قبلی گرفت؟",
        accepted: [
          "Clear signposting saved the talk.",
          "Because clear signposting saved the talk.",
        ],
        note: "save the talk: ارائه را از شکست نجات دادن.",
      },
      {
        scene: "کنفرانس تخصصی، پیش از ارائهٔ اصلی", partner: "هماهنگ‌کنندهٔ کنفرانس",
        prompt: "بگو اصطلاح تخصصی استدلال ضعیف را پنهان می‌کند.",
        question: "Can we keep the acronyms?",
        questionFa: "می‌توانیم سرواژه‌ها را نگه داریم؟",
        accepted: [
          "Jargon hides a weak argument.",
          "No, jargon hides a weak argument.",
        ],
        note: "jargon غیرقابل‌شمارش است: hides.",
      },
      {
        scene: "کنفرانس تخصصی، پیش از ارائهٔ اصلی", partner: "هماهنگ‌کنندهٔ کنفرانس",
        prompt: "بگو سلب مسئولیت روی اسلاید دوم بود.",
        question: "Where did you put the disclaimer?",
        questionFa: "سلب مسئولیت را کجا گذاشتید؟",
        accepted: [
          "The disclaimer sat on the second slide.",
          "It sat on the second slide.",
        ],
        note: "on the second slide: روی اسلاید دوم.",
      },
    ],
  },
];
