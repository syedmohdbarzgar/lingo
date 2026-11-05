// v42 content, part 1 — B2 topics 1 and 2: workplace negotiations, project
// management. Read by `v42_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.negotiation.lesson-01",
      order: 120,
      title: "Workplace Negotiations",
      titleFa: "مذاکره در محیط کار",
      topic: "Work",
      tip: [
        "## قاعده",
        "برای پیشنهاد فرضی و محتاطانه: If + گذشتهٔ ساده، would + فعل ساده — If we lowered the price, we would win the contract.",
        "could و might در بخش نتیجه احتمال را نرم می‌کنند — We might lose the client if we waited.",
        "در زبان رسمی به‌جای was از were استفاده می‌شود: If I were in your position, I would wait.",
        "زبان نرم‌کننده در مذاکره: I'm afraid… / It seems to me… / Would you be open to…?",
        "## جدول",
        "If we lowered the price, we would win the contract. — اگر قیمت را کم می‌کردیم، قرارداد را می‌بردیم.",
        "If I were you, I would ask for more time. — جای تو بودم، وقت بیشتری می‌خواستم.",
        "Would you be open to a shorter contract? — با قرارداد کوتاه‌تر موافقید؟",
        "## مثال‌ها",
        "We need to agree on the terms first. — اول باید روی شرایط توافق کنیم.",
        "Both sides made a concession. — هر دو طرف امتیاز دادند.",
        "The proposal was fair and mutual. — پیشنهاد منصفانه و دوجانبه بود.",
        "## خطاهای رایج",
        "در بخش if هرگز would نمی‌آید: If we would lower ✗ / If we lowered ✓.",
        "concession قابل‌شمارش است: a concession / two concessions.",
        "leverage غیرقابل‌شمارش است: have leverage ✓ / have a leverage ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.tentative-conditionals",
    vocab: {
      id: "vocab.negotiations",
      title: "Workplace Negotiations",
      titleFa: "مذاکره در محیط کار",
      prerequisites: ["vocab.job-hunting"],
    },
    words: [
      {
        slug: "terms", w: "terms", fa: "شرایط؛ مفاد", ipa: "/tɜːmz/", pos: "noun",
        def: "the conditions written in an agreement",
        ex: [
          ["We need to agree on the terms first.", "اول باید روی شرایط توافق کنیم."],
          ["The terms of the contract are clear.", "مفاد قرارداد روشن است."],
        ],
        col: ["the terms of a contract", "agree on terms"],
        note: "در این معنا terms همیشه جمع است: the terms of the deal، نه a term.",
      },
      {
        slug: "concession", w: "concession", fa: "امتیاز (در مذاکره)", ipa: "/kənˈseʃn/", pos: "noun",
        def: "something you give up to reach an agreement",
        ex: [
          ["Both sides made a concession.", "هر دو طرف امتیاز دادند."],
          ["A small concession can save the deal.", "یک امتیاز کوچک می‌تواند معامله را نجات دهد."],
        ],
        col: ["make a concession", "a small concession"],
        note: "concession قابل‌شمارش است و با make می‌آید؛ فعل آن concede است.",
      },
      {
        slug: "leverage", w: "leverage", fa: "ابزار فشار؛ برگ برنده", ipa: "/ˈliːvərɪdʒ/", pos: "noun",
        def: "power that helps you get what you want in a deal",
        ex: [
          ["We have little leverage in this negotiation.", "در این مذاکره برگ برندهٔ کمی داریم."],
          ["Price is our main leverage.", "قیمت ابزار فشار اصلی ماست."],
        ],
        col: ["have leverage", "use leverage"],
        note: "leverage غیرقابل‌شمارش است: have leverage ✓ / have a leverage ✗.",
      },
      {
        slug: "mutual", w: "mutual", fa: "دوجانبه", ipa: "/ˈmjuːtʃuəl/", pos: "adjective",
        def: "shared by two or more people",
        ex: [
          ["The proposal was fair and mutual.", "پیشنهاد منصفانه و دوجانبه بود."],
          ["We reached a mutual agreement.", "به توافقی دوجانبه رسیدیم."],
        ],
        col: ["mutual agreement", "mutual benefit"],
        note: "mutual یعنی «هر دو طرف»؛ a mutual agreement یعنی توافقی که هر دو می‌پذیرند.",
      },
      {
        slug: "proposal", w: "proposal", fa: "پیشنهاد مکتوب", ipa: "/prəˈpəʊzl/", pos: "noun",
        def: "a formal plan that you offer to someone",
        ex: [
          ["They sent a written proposal.", "آن‌ها یک پیشنهاد مکتوب فرستادند."],
          ["The proposal was accepted after two meetings.", "پیشنهاد پس از دو جلسه پذیرفته شد."],
        ],
        col: ["send a proposal", "accept a proposal"],
        note: "proposal رسمی و معمولاً مکتوب است؛ offer بیشتر برای قیمت و کالا به کار می‌رود.",
      },
      {
        slug: "agreement", w: "agreement", fa: "توافق‌نامه", ipa: "/əˈɡriːmənt/", pos: "noun",
        def: "an arrangement that both sides accept",
        ex: [
          ["We reached an agreement on Friday.", "جمعه به توافق رسیدیم."],
          ["The agreement covers the next two years.", "توافق‌نامه دو سال آینده را پوشش می‌دهد."],
        ],
        col: ["reach an agreement", "sign an agreement"],
        note: "reach an agreement ترکیب ثابت است؛ در این معنا agreement قابل‌شمارش است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "concession", gloss: "امتیاز (در مذاکره)",
        options: ["امتیاز (در مذاکره)", "قرارداد", "جریمه", "پیشنهاد شغلی"],
        note: "concession یعنی چیزی که برای توافق رها می‌کنی؛ contract قرارداد است.",
      },
      {
        k: "mcq", word: "leverage", gloss: "ابزار فشار؛ برگ برنده",
        options: ["ابزار فشار؛ برگ برنده", "سود", "سهم بازار", "زمان‌بندی"],
        note: "leverage توانی است که موضع تو را در مذاکره قوی می‌کند.",
      },
      {
        k: "mcq", word: "mutual", gloss: "دوجانبه",
        options: ["دوجانبه", "یک‌طرفه", "مشروط", "قطعی"],
        note: "mutual یعنی هر دو طرف سهم دارند؛ متضاد آن one-sided است.",
      },
      {
        k: "blank", sentence: "If we ___ the price, we would win the contract.",
        accepted: ["lowered", "reduced"], skill: "VOCABULARY",
        note: "در شرطی نوع دوم فعل گذشته است؛ lower و reduce هر دو معنا می‌دهند.",
      },
      {
        k: "blank", sentence: "Both sides made a small ___.",
        accepted: ["concession", "compromise"], skill: "VOCABULARY",
        note: "make a concession یا reach a compromise هر دو «امتیاز دادن» را می‌رسانند.",
      },
      {
        k: "recall", prompt: "دوجانبه", accepted: ["mutual"],
        note: "دوجانبه mutual است و با agreement می‌آید.",
      },
      {
        k: "tr", prompt: "اگر جای تو بودم، وقت بیشتری می‌خواستم.",
        accepted: [
          "if i were you, i would ask for more time",
          "if i were in your position, i would ask for more time",
        ],
        note: "If I were you, I would… ساختار ثابت نصیحت است؛ were به‌جای was.",
      },
      {
        k: "listen", text: "If we lowered the price, we would win the contract.",
        note: "به lowered و would گوش کنید: هر دو بخش شرطی نوع دوم گذشته‌اند.",
      },
      {
        k: "prod", prompt: "در مذاکره پیشنهاد بده: اگر شرایط بهتر باشد، قرارداد را امضا می‌کنی.",
        mustUse: ["if", "would"], minWords: 8,
        accepted: ["If the terms were better, we would sign the contract today."],
        hint: "با If the terms were better, we would… شروع کن.",
        note: "شرطی نوع دوم: گذشته در بخش if و would + فعل ساده در نتیجه.",
      },
    ],
    dialogue: [
      {
        scene: "اتاق جلسه، مذاکرهٔ قرارداد", partner: "مدیر فروش شرکت دیگر",
        prompt: "بگو پیشنهادشان گران‌تر از بودجهٔ شماست.",
        question: "What do you think of our proposal?",
        questionFa: "دربارهٔ پیشنهاد ما چه فکری می‌کنید؟",
        accepted: [
          "I am afraid it is above our budget.",
          "The proposal is higher than we expected.",
        ],
        note: "I am afraid… نرم‌کنندهٔ خبر ناخوشایند است و لحن را مؤدبانه نگه می‌دارد.",
      },
      {
        scene: "اتاق جلسه، مذاکرهٔ قرارداد", partner: "مدیر فروش شرکت دیگر",
        prompt: "بگو اگر قیمت پایین‌تر بیاید، قرارداد را امضا می‌کنید.",
        question: "Would you sign today if the price changed?",
        questionFa: "اگر قیمت عوض شود، امروز امضا می‌کنید؟",
        accepted: [
          "If the price were lower, we would sign today.",
          "We would sign if the price came down.",
        ],
        note: "شرطی نوع دوم فرضی است: were lower و would sign.",
      },
      {
        scene: "اتاق جلسه، مذاکرهٔ قرارداد", partner: "مدیر فروش شرکت دیگر",
        prompt: "بگو آماده‌اید یک امتیاز بدهید: تحویل بعد.",
        question: "Can we meet in the middle?",
        questionFa: "می‌توانیم وسط راه به توافق برسیم؟",
        accepted: [
          "We could make one concession on delivery.",
          "Yes, we can offer a later delivery date.",
        ],
        note: "could و can اینجا پیشنهاد ملایم می‌سازند؛ concession با on می‌آید.",
      },
      {
        scene: "اتاق جلسه، مذاکرهٔ قرارداد", partner: "مدیر فروش شرکت دیگر",
        prompt: "بگو شرایط باید برای هر دو طرف منصفانه باشد.",
        question: "What matters most to you?",
        questionFa: "چه چیزی برایتان مهم‌تر است؟",
        accepted: [
          "The terms must be fair and mutual.",
          "We need a mutual agreement, not a one-sided one.",
        ],
        note: "must be قطعیت را می‌رساند؛ mutual با fair هم‌نشین است.",
      },
      {
        scene: "اتاق جلسه، مذاکرهٔ قرارداد", partner: "مدیر فروش شرکت دیگر",
        prompt: "بگو اگر پیشنهاد کتبی بفرستند، بررسی می‌کنید.",
        question: "Shall we send a written proposal?",
        questionFa: "پیشنهاد مکتوب بفرستیم؟",
        accepted: [
          "Yes, and we would review it this week.",
          "Please send the proposal and we will reply soon.",
        ],
        note: "would در نتیجهٔ شرطی ضمنی می‌آید؛ review یعنی بررسی کردن.",
      },
      {
        scene: "اتاق جلسه، مذاکرهٔ قرارداد", partner: "مدیر فروش شرکت دیگر",
        prompt: "بگو به توافق نزدیک شده‌اید.",
        question: "So, do we have a deal?",
        questionFa: "پس توافق کردیم؟",
        accepted: [
          "We are close to an agreement.",
          "Almost, we have one point left.",
        ],
        note: "be close to + اسم؛ have a deal یعنی به توافق رسیدن.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.projects.lesson-01",
      order: 121,
      title: "Project Management",
      titleFa: "مدیریت پروژه",
      topic: "Work",
      tip: [
        "## قاعده",
        "کارهایی که تا حالا انجام شده و فاعل مهم نیست: has/have been + قسمت سوم — The report has been sent to the client.",
        "منفی و سؤال با yet: The timeline has not been approved yet. / Has the budget been approved?",
        "برای مرحلهٔ بعد می‌توان مجهول آینده ساخت: The next step will be discussed on Friday.",
        "برای مسئولیت: Each task has an owner, and every owner reports weekly.",
        "## جدول",
        "The report has been sent to the client. — گزارش برای مشتری فرستاده شده است.",
        "The timeline has not been approved yet. — زمان‌بندی هنوز تأیید نشده است.",
        "Three tasks have been completed this week. — این هفته سه کار تمام شده است.",
        "## مثال‌ها",
        "Each deliverable has an owner. — هر محصول تحویلی یک مسئول دارد.",
        "We need to agree on the scope first. — اول باید روی دامنهٔ کار توافق کنیم.",
        "The stakeholder wants weekly updates. — ذی‌نفع به‌روزرسانی هفتگی می‌خواهد.",
        "## خطاهای رایج",
        "ساختار کامل است: has been + قسمت سوم — has been send ✗ / has been sent ✓.",
        "yet در جملهٔ منفی و سؤالی می‌آید: has not been approved yet.",
        "scope غیرقابل‌شمارش است: the scope of the project ✓ / a scope ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.present-perfect-passive",
    vocab: {
      id: "vocab.project-management",
      title: "Project Management",
      titleFa: "مدیریت پروژه",
      prerequisites: ["vocab.meetings"],
    },
    words: [
      {
        slug: "deliverable", w: "deliverable", fa: "محصول تحویلی", ipa: "/dɪˈlɪvərəbl/", pos: "noun",
        def: "something a project must produce and hand over",
        ex: [
          ["Each deliverable has an owner.", "هر محصول تحویلی یک مسئول دارد."],
          ["The first deliverable is due next month.", "اولین محصول تحویلی ماه بعد موعدش است."],
        ],
        col: ["a key deliverable", "list the deliverables"],
        note: "deliverable اسم است و در مدیریت پروژه یعنی خروجی مشخصی که تحویل داده می‌شود.",
      },
      {
        slug: "timeline", w: "timeline", fa: "زمان‌بندی", ipa: "/ˈtaɪmlaɪn/", pos: "noun",
        def: "the plan that shows when each step happens",
        ex: [
          ["The timeline has not been approved yet.", "زمان‌بندی هنوز تأیید نشده است."],
          ["We need a realistic timeline for the launch.", "برای عرضه به زمان‌بندی واقع‌بینانه نیاز داریم."],
        ],
        col: ["a realistic timeline", "agree on a timeline"],
        note: "timeline یک اسم است: a timeline؛ روی اولین بخش تکیه دارد.",
      },
      {
        slug: "scope", w: "scope", fa: "دامنهٔ کار", ipa: "/skəʊp/", pos: "noun",
        def: "everything a project includes, and what it does not",
        ex: [
          ["We need to agree on the scope first.", "اول باید روی دامنهٔ کار توافق کنیم."],
          ["The scope of the project is too wide.", "دامنهٔ پروژه بیش از حد گسترده است."],
        ],
        col: ["the scope of the project", "outside the scope"],
        note: "scope در این معنا غیرقابل‌شمارش است: the scope of…؛ out of scope یعنی «خارج از دامنه».",
      },
      {
        slug: "stakeholder", w: "stakeholder", fa: "ذی‌نفع", ipa: "/ˈsteɪkhəʊldə/", pos: "noun",
        def: "a person who cares about the result of a project",
        ex: [
          ["The stakeholder wants weekly updates.", "ذی‌نفع به‌روزرسانی هفتگی می‌خواهد."],
          ["We met every stakeholder before the launch.", "قبل از عرضه با هر ذی‌نفع جلسه گذاشتیم."],
        ],
        col: ["a key stakeholder", "meet the stakeholders"],
        note: "stakeholder قابل‌شمارش است؛ در پروژه یعنی کسی که نتیجه به او مربوط است.",
      },
      {
        slug: "dependency", w: "dependency", fa: "وابستگی؛ پیش‌نیاز", ipa: "/dɪˈpendənsi/", pos: "noun",
        def: "a task or part that must finish before another can start",
        ex: [
          ["One dependency is the new server.", "یک وابستگی، سرور جدید است."],
          ["We marked the dependency on the plan.", "وابستگی را روی برنامه علامت زدیم."],
        ],
        col: ["a dependency on", "remove a dependency"],
        note: "dependency با on می‌آید؛ dependent صفت و depend فعل است.",
      },
      {
        slug: "estimate", w: "estimate", fa: "برآورد", ipa: "/ˈestɪmət/", pos: "noun",
        def: "a careful guess of time, cost or size",
        ex: [
          ["The estimate was two weeks.", "برآورد دو هفته بود."],
          ["Can you estimate the cost of the change?", "می‌توانی هزینهٔ تغییر را برآورد کنی؟"],
        ],
        col: ["a rough estimate", "estimate the cost"],
        note: "اسم آن /ˈestɪmət/ و فعل آن /ˈestɪmeɪt/ است؛ rough estimate یعنی برآورد تقریبی.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "deliverable", gloss: "محصول تحویلی",
        options: ["محصول تحویلی", "زمان‌بندی", "بودجه", "جلسه"],
        note: "deliverable خروجی مشخصی است که تحویل داده می‌شود؛ timeline زمان‌بندی است.",
      },
      {
        k: "mcq", word: "scope", gloss: "دامنهٔ کار",
        options: ["دامنهٔ کار", "هزینه", "تیم", "ریسک"],
        note: "scope یعنی آنچه در پروژه هست و آنچه نیست.",
      },
      {
        k: "mcq", word: "stakeholder", gloss: "ذی‌نفع",
        options: ["ذی‌نفع", "پیمانکار", "کارآموز", "مشتری نهایی"],
        note: "stakeholder هر کسی است که نتیجهٔ پروژه به او مربوط است.",
      },
      {
        k: "blank", sentence: "The report has been ___ to the client.",
        accepted: ["sent", "given"], skill: "VOCABULARY",
        note: "مجهول حال کامل: has been + قسمت سوم؛ send → sent.",
      },
      {
        k: "blank", sentence: "The timeline has not been ___ yet.",
        accepted: ["approved", "confirmed"], skill: "VOCABULARY",
        note: "yet با منفی می‌آید؛ approve و confirm هر دو «تأیید کردن»اند.",
      },
      {
        k: "recall", prompt: "زمان‌بندی", accepted: ["timeline"],
        note: "زمان‌بندی timeline است و قابل‌شمارش.",
      },
      {
        k: "tr", prompt: "این هفته سه کار تمام شده است.",
        accepted: [
          "three tasks have been completed this week",
          "three tasks have been finished this week",
        ],
        note: "فاعل جمع → have been + قسمت سوم: have been completed.",
      },
      {
        k: "listen", text: "The report has been sent to the client.",
        note: "به has been sent گوش کنید: حال کامل مجهول، بدون فاعل.",
      },
      {
        k: "prod", prompt: "بگو چه چیزی در پروژه تا حالا انجام شده و چه چیزی هنوز نه.",
        mustUse: ["been", "yet"], minWords: 8,
        accepted: ["The plan has been approved, but the budget has not been checked yet."],
        hint: "با The plan has been… و بعد has not been… yet ادامه بده.",
        note: "has been + قسمت سوم مجهول حال کامل می‌سازد و yet در منفی می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ وضعیت پروژه", partner: "مدیر پروژه",
        prompt: "بگو گزارش برای مشتری فرستاده شده است.",
        question: "Has the report gone to the client?",
        questionFa: "گزارش برای مشتری رفته است؟",
        accepted: [
          "Yes, the report has been sent already.",
          "It has been sent to the client this morning.",
        ],
        note: "has been sent مجهول حال کامل است؛ already در جملهٔ مثبت می‌آید.",
      },
      {
        scene: "جلسهٔ وضعیت پروژه", partner: "مدیر پروژه",
        prompt: "بگو زمان‌بندی هنوز تأیید نشده است.",
        question: "What about the timeline?",
        questionFa: "زمان‌بندی چه شد؟",
        accepted: [
          "The timeline has not been approved yet.",
          "It has not been confirmed yet.",
        ],
        note: "yet با منفی می‌آید و جمله را طبیعی می‌کند.",
      },
      {
        scene: "جلسهٔ وضعیت پروژه", partner: "مدیر پروژه",
        prompt: "بگو دامنهٔ کار باید کوچک‌تر شود.",
        question: "Is the scope still a problem?",
        questionFa: "دامنهٔ کار هنوز مشکل است؟",
        accepted: [
          "Yes, the scope needs to be smaller.",
          "We should reduce the scope this week.",
        ],
        note: "need to be + صفت مجهول است؛ reduce the scope یعنی کم کردن دامنه.",
      },
      {
        scene: "جلسهٔ وضعیت پروژه", partner: "مدیر پروژه",
        prompt: "بگو برآورد اولیه دو هفته بود.",
        question: "How long will the change take?",
        questionFa: "این تغییر چقدر طول می‌کشد؟",
        accepted: [
          "The first estimate was two weeks.",
          "We estimated two weeks at the start.",
        ],
        note: "estimate هم اسم و هم فعل است؛ at the start یعنی در آغاز.",
      },
      {
        scene: "جلسهٔ وضعیت پروژه", partner: "مدیر پروژه",
        prompt: "بگو یک وابستگی به سرور جدید دارید.",
        question: "Are there any dependencies?",
        questionFa: "وابستگی‌ای وجود دارد؟",
        accepted: [
          "Yes, there is a dependency on the new server.",
          "We depend on the new server this week.",
        ],
        note: "dependency on something؛ depend on هم همان رابطه را با فعل می‌گوید.",
      },
      {
        scene: "جلسهٔ وضعیت پروژه", partner: "مدیر پروژه",
        prompt: "بگو ذی‌نفع به‌روزرسانی هفتگی می‌خواهد.",
        question: "How often does the stakeholder want updates?",
        questionFa: "ذی‌نفع هر چند وقت به‌روزرسانی می‌خواهد؟",
        accepted: [
          "The stakeholder wants weekly updates.",
          "Every week, the stakeholder asks for a report.",
        ],
        note: "how often برای تکرار است؛ weekly صفت است: weekly updates.",
      },
    ],
  },
];
