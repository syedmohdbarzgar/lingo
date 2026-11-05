// v43 content, part 2 — B2 topics 8 and 9: artificial intelligence, digital
// privacy and security. Read by `v43_b2_batch.mjs`; never run on its own.
//
// `reliable` was already taught in `b2.research`, so the AI lesson teaches
// `efficient` instead — the duplicate multiple-choice check runs on the word.
export default [
  {
    lesson: {
      id: "b2.ai.lesson-01",
      order: 126,
      title: "Artificial Intelligence",
      titleFa: "هوش مصنوعی در زندگی و کار",
      topic: "Technology",
      tip: [
        "## قاعده",
        "برای حدس زدن از روی شواهد: must (حتماً) / might و could (شاید) / can't (نمی‌تواند).",
        "این افعال در این معنا شکل گذشته و آینده نمی‌گیرند؛ زمان جمله را از فعل بعدی می‌فهمیم: It might be a bug.",
        "برای حدس دربارهٔ گذشته: must have + قسمت سوم / might have + قسمت سوم / can't have + قسمت سوم.",
        "can't در این معنا مخالف must است، نه مخالف can: That can't be right یعنی «حتماً درست نیست».",
        "## جدول",
        "That must be a mistake. — این حتماً اشتباه است.",
        "The model might be making the same mistake again. — شاید مدل دوباره همان اشتباه را می‌کند.",
        "The system must have been trained on old data. — سیستم حتماً با داده‌های قدیمی آموزش دیده است.",
        "## مثال‌ها",
        "Automation changed how the team works. — خودکارسازی شیوهٔ کار تیم را عوض کرد.",
        "The dataset is small, so the accuracy is low. — داده‌ها کم است، پس دقت پایین است.",
        "The output is easy to read. — خروجی خواندنش آسان است.",
        "## خطاهای رایج",
        "must در این معنا گذشته نمی‌شود: It musted ✗ — برای گذشته must have done ✓.",
        "can't مخالف must است: That can't be right = That must be wrong.",
        "accuracy غیرقابل‌شمارش است: high accuracy ✓ / a high accuracy ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-of-deduction",
    vocab: {
      id: "vocab.artificial-intelligence",
      title: "Artificial Intelligence",
      titleFa: "هوش مصنوعی",
      prerequisites: ["vocab.technology"],
    },
    words: [
      {
        slug: "automation", w: "automation", fa: "خودکارسازی", ipa: "/ˌɔːtəˈmeɪʃn/", pos: "noun",
        def: "using machines to do work without people",
        ex: [
          ["Automation changed how the team works.", "خودکارسازی شیوهٔ کار تیم را عوض کرد."],
          ["The factory saved money through automation.", "کارخانه با خودکارسازی پول ذخیره کرد."],
        ],
        col: ["office automation", "increase automation"],
        note: "automation غیرقابل‌شمارش است؛ automate فعل و automated صفت آن است.",
      },
      {
        slug: "dataset", w: "dataset", fa: "مجموعه داده", ipa: "/ˈdeɪtəset/", pos: "noun",
        def: "a large group of information used for training or testing",
        ex: [
          ["The dataset is small, so the accuracy is low.", "داده‌ها کم است، پس دقت پایین است."],
          ["They cleaned the dataset before training the model.", "آن‌ها پیش از آموزش مدل مجموعه داده را پاک‌سازی کردند."],
        ],
        col: ["a large dataset", "clean a dataset"],
        note: "dataset قابل‌شمارش است: a dataset / two datasets.",
      },
      {
        slug: "accuracy", w: "accuracy", fa: "دقت", ipa: "/ˈækjərəsi/", pos: "noun",
        def: "how correct or exact something is",
        ex: [
          ["The dataset is small, so the accuracy is low.", "داده‌ها کم است، پس دقت پایین است."],
          ["We measured the accuracy of the answers.", "ما دقت پاسخ‌ها را اندازه گرفتیم."],
        ],
        col: ["high accuracy", "measure accuracy"],
        note: "accuracy غیرقابل‌شمارش است؛ صفت آن accurate است.",
      },
      {
        slug: "ethical", w: "ethical", fa: "اخلاقی", ipa: "/ˈeθɪkl/", pos: "adjective",
        def: "morally right or acceptable",
        ex: [
          ["The team asked whether the tool was ethical.", "تیم پرسید آیا این ابزار اخلاقی است."],
          ["An ethical use of data needs clear consent.", "استفادهٔ اخلاقی از داده به رضایت روشن نیاز دارد."],
        ],
        col: ["an ethical question", "ethical use of data"],
        note: "ethical صفت است و ethics اسم؛ متضاد آن unethical است.",
      },
      {
        slug: "efficient", w: "efficient", fa: "کارآمد", ipa: "/ɪˈfɪʃnt/", pos: "adjective",
        def: "working well without wasting time or money",
        ex: [
          ["The new tool is faster and more efficient.", "ابزار جدید سریع‌تر و کارآمدتر است."],
          ["Automation made the process efficient.", "خودکارسازی فرایند را کارآمد کرد."],
        ],
        col: ["highly efficient", "an efficient process"],
        note: "efficient صفت است؛ اسم آن efficiency و قید آن efficiently است.",
      },
      {
        slug: "output", w: "output", fa: "خروجی", ipa: "/ˈaʊtpʊt/", pos: "noun",
        def: "what a system or person produces",
        ex: [
          ["The output is easy to read.", "خروجی خواندنش آسان است."],
          ["They checked the output before sending it.", "آن‌ها پیش از فرستادن، خروجی را بررسی کردند."],
        ],
        col: ["check the output", "high output"],
        note: "output هم اسم و هم فعل است؛ متضاد آن input است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "automation", gloss: "خودکارسازی",
        options: ["خودکارسازی", "هوش مصنوعی", "رمزنگاری", "پردازش"],
        note: "automation انجام کار با ماشین به‌جای انسان است.",
      },
      {
        k: "mcq", word: "dataset", gloss: "مجموعه داده",
        options: ["مجموعه داده", "پایگاه اطلاعات", "شبکهٔ عصبی", "خروجی"],
        note: "dataset مجموعه‌ای بزرگ از داده است که مدل با آن آموزش می‌بیند.",
      },
      {
        k: "mcq", word: "accuracy", gloss: "دقت",
        options: ["دقت", "سرعت", "حافظه", "هزینه"],
        note: "accuracy میزان درستی و دقیق بودن است.",
      },
      {
        k: "blank", sentence: "The system ___ have been trained on old data.",
        accepted: ["must", "might"], skill: "VOCABULARY",
        note: "must have و might have هر دو حدس دربارهٔ گذشته‌اند؛ must مطمئن‌تر است.",
      },
      {
        k: "blank", sentence: "That ___ be a mistake — the numbers are impossible.",
        accepted: ["must", "can't"], skill: "VOCABULARY",
        note: "must یعنی حتماً؛ can't به همین معنا هم می‌آید: That can't be a mistake.",
      },
      {
        k: "recall", prompt: "دقت", accepted: ["accuracy"],
        note: "دقت در اندازه‌گیری accuracy است و صفت آن accurate.",
      },
      {
        k: "tr", prompt: "این نتیجه حتماً درست نیست.",
        accepted: [
          "this result cannot be right",
          "this result must be wrong",
        ],
        note: "can't be right مخالف must be right است، نه مخالف can be right.",
      },
      {
        k: "listen", text: "The model might be making the same mistake again.",
        note: "به might be making گوش کنید: might + استمراری یعنی حدس دربارهٔ حال.",
      },
      {
        k: "prod", prompt: "دربارهٔ یک خروجی هوش مصنوعی حدس بزن و دلیلش را بگو.",
        mustUse: ["might", "because"], minWords: 8,
        accepted: ["The model might be wrong because the dataset is old."],
        hint: "با … might be … because … شروع کن.",
        note: "ترتیب لنگرها را رعایت کن: might پیش از because می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ هفتگی تیم محصول دربارهٔ یک ابزار جدید", partner: "همکار محصول",
        prompt: "بگو ابزار جدید سریع‌تر و کارآمدتر است.",
        question: "How is the new tool performing?",
        questionFa: "ابزار جدید چطور کار می‌کند؟",
        accepted: [
          "It is faster and more efficient than the old one.",
          "Much better — the process is more efficient now.",
        ],
        note: "more efficient صفت بلند است، پس با more مقایسه می‌شود.",
      },
      {
        scene: "جلسهٔ هفتگی تیم محصول دربارهٔ یک ابزار جدید", partner: "همکار محصول",
        prompt: "بگو دقت پایین است چون داده‌ها کم است.",
        question: "Why are the answers sometimes wrong?",
        questionFa: "چرا پاسخ‌ها گاهی غلط است؟",
        accepted: [
          "The dataset is small, so the accuracy is low.",
          "Because the dataset is too small.",
        ],
        note: "so رابطهٔ نتیجه را می‌رساند؛ accuracy غیرقابل‌شمارش است.",
      },
      {
        scene: "جلسهٔ هفتگی تیم محصول دربارهٔ یک ابزار جدید", partner: "همکار محصول",
        prompt: "بگو شاید مدل دوباره همان اشتباه را می‌کند.",
        question: "What do you think is happening here?",
        questionFa: "فکر می‌کنی چه اتفاقی می‌افتد؟",
        accepted: [
          "The model might be making the same mistake again.",
          "It could be repeating the same mistake.",
        ],
        note: "might be + ing حدس دربارهٔ کاری است که همین حالا جریان دارد.",
      },
      {
        scene: "جلسهٔ هفتگی تیم محصول دربارهٔ یک ابزار جدید", partner: "همکار محصول",
        prompt: "بگو خروجی خواندنش آسان است.",
        question: "Is the output useful for the team?",
        questionFa: "خروجی برای تیم مفید است؟",
        accepted: [
          "Yes, the output is easy to read.",
          "Yes, the output is clear and useful.",
        ],
        note: "the output مفرد است: the output is ✓ / the output are ✗.",
      },
      {
        scene: "جلسهٔ هفتگی تیم محصول دربارهٔ یک ابزار جدید", partner: "همکار محصول",
        prompt: "بگو این حتماً اشتباه است چون عددها ممکن نیست.",
        question: "Are these numbers real?",
        questionFa: "این عددها واقعی است؟",
        accepted: [
          "That must be a mistake because the numbers are impossible.",
          "It must be wrong — nobody can have that many users.",
        ],
        note: "must be برای حدس قوی از روی شواهد؛ because دلیل را می‌آورد.",
      },
      {
        scene: "جلسهٔ هفتگی تیم محصول دربارهٔ یک ابزار جدید", partner: "همکار محصول",
        prompt: "بگو باید مطمئن شویم استفاده از داده اخلاقی است.",
        question: "Should we ship it this week?",
        questionFa: "این هفته منتشرش کنیم؟",
        accepted: [
          "First we should check that the use of data is ethical.",
          "Not yet — I want to be sure it is ethical.",
        ],
        note: "check that + جمله برای بررسی یک شرط؛ ethical صفت است.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.privacy.lesson-01",
      order: 127,
      title: "Digital Privacy and Security",
      titleFa: "حریم خصوصی و امنیت دیجیتال",
      topic: "Technology",
      tip: [
        "## قاعده",
        "مجهول حال کامل: has/have been + قسمت سوم — My account has been hacked.",
        "وقتی انجام‌دهنده معلوم نیست یا مهم نیست، مجهول طبیعی‌تر است: The data has been deleted.",
        "به‌جای by us معمولاً کسی را نمی‌آوریم؛ اگر لازم شد، by + فاعل: has been checked by the team.",
        "برای کارِ ناتمام از حال کامل مجهول استفاده نکن؛ آن برای نتیجه‌ای است که همین حالا اثر دارد.",
        "## جدول",
        "My account has been hacked. — حسابم هک شده است.",
        "Your data has been encrypted. — داده‌هایت رمزگذاری شده است.",
        "The password has been changed without my consent. — رمز بدون رضایت من عوض شده است.",
        "## مثال‌ها",
        "Encryption keeps messages private. — رمزنگاری پیام‌ها را خصوصی نگه می‌دارد.",
        "The breach affected two million users. — نشت اطلاعات دو میلیون کاربر را درگیر کرد.",
        "Tracking follows what you do online. — ردیابی کارهایت را در اینترنت دنبال می‌کند.",
        "## خطاهای رایج",
        "حال کامل مجهول has been + قسمت سوم است، نه has be: has be changed ✗.",
        "consent غیرقابل‌شمارش است: without my consent ✓ / without my consents ✗.",
        "phishing با p نوشته می‌شود و از fishing (ماهیگیری) گرفته شده است.",
      ].join("\n"),
    },
    grammarNode: "grammar.present-perfect-passive",
    vocab: {
      id: "vocab.digital-privacy",
      title: "Digital Privacy and Security",
      titleFa: "حریم خصوصی دیجیتال",
      prerequisites: ["vocab.social-media"],
    },
    words: [
      {
        slug: "encryption", w: "encryption", fa: "رمزنگاری", ipa: "/ɪnˈkrɪpʃn/", pos: "noun",
        def: "turning information into a code so others cannot read it",
        ex: [
          ["Encryption keeps messages private.", "رمزنگاری پیام‌ها را خصوصی نگه می‌دارد."],
          ["The app uses encryption for every message.", "برنامه برای هر پیام از رمزنگاری استفاده می‌کند."],
        ],
        col: ["strong encryption", "use encryption"],
        note: "encryption غیرقابل‌شمارش است؛ encrypt فعل و encrypted صفت آن است.",
      },
      {
        slug: "breach", w: "breach", fa: "نشت اطلاعات؛ نقض", ipa: "/briːtʃ/", pos: "noun",
        def: "an occasion when private information is taken or shared",
        ex: [
          ["The breach affected two million users.", "نشت اطلاعات دو میلیون کاربر را درگیر کرد."],
          ["After the breach they changed every password.", "بعد از نشت، همهٔ رمزها را عوض کردند."],
        ],
        col: ["a data breach", "after the breach"],
        note: "breach قابل‌شمارش است و اغلب با data می‌آید: a data breach.",
      },
      {
        slug: "consent", w: "consent", fa: "رضایت", ipa: "/kənˈsent/", pos: "noun",
        def: "permission that someone gives you",
        ex: [
          ["The password has been changed without my consent.", "رمز بدون رضایت من عوض شده است."],
          ["They need your consent before using your photos.", "آن‌ها پیش از استفاده از عکس‌هایت به رضایتت نیاز دارند."],
        ],
        col: ["without consent", "give consent"],
        note: "consent غیرقابل‌شمارش است؛ give consent و without consent ترکیب‌های ثابت‌اند.",
      },
      {
        slug: "tracking", w: "tracking", fa: "ردیابی", ipa: "/ˈtrækɪŋ/", pos: "noun",
        def: "following and recording what someone does",
        ex: [
          ["Tracking follows what you do online.", "ردیابی کارهایت را در اینترنت دنبال می‌کند."],
          ["I turned off tracking in the app.", "من ردیابی را در برنامه خاموش کردم."],
        ],
        col: ["turn off tracking", "online tracking"],
        note: "tracking غیرقابل‌شمارش است؛ track فعل و trackable صفت آن است.",
      },
      {
        slug: "phishing", w: "phishing", fa: "فیشینگ", ipa: "/ˈfɪʃɪŋ/", pos: "noun",
        def: "tricking people with fake messages to steal their details",
        ex: [
          ["Phishing messages often look real.", "پیام‌های فیشینگ اغلب واقعی به نظر می‌رسند."],
          ["He lost his account to a phishing email.", "حسابش را به خاطر یک ایمیل فیشینگ از دست داد."],
        ],
        col: ["a phishing email", "be caught by phishing"],
        note: "phishing غیرقابل‌شمارش است و با p نوشته می‌شود؛ phishing email رایج‌ترین ترکیب است.",
      },
      {
        slug: "verify", w: "verify", fa: "راستی‌آزمایی کردن", ipa: "/ˈverɪfaɪ/", pos: "verb",
        def: "to check that something is true",
        ex: [
          ["Always verify the sender before you reply.", "پیش از پاسخ، همیشه فرستنده را راستی‌آزمایی کن."],
          ["You should verify every new device.", "باید هر دستگاه تازه را راستی‌آزمایی کنی."],
        ],
        col: ["verify the sender", "verify your identity"],
        note: "verify با مفعول مستقیم می‌آید، نه with: verify the sender ✓ / verify with the sender ✗.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "encryption", gloss: "رمزنگاری",
        options: ["رمزنگاری", "ردیابی", "پشتیبان‌گیری", "رضایت"],
        note: "encryption تبدیل اطلاعات به کد است تا دیگران آن را نخوانند.",
      },
      {
        k: "mcq", word: "breach", gloss: "نشت اطلاعات",
        options: ["نشت اطلاعات", "خرابی دستگاه", "بازیابی رمز", "نسخهٔ پشتیبان"],
        note: "breach در امنیت یعنی اطلاعات خصوصی بیرون رفته باشد.",
      },
      {
        k: "mcq", word: "consent", gloss: "رضایت",
        options: ["رضایت", "محدودیت", "اخطار", "تنظیمات"],
        note: "consent اجازه‌ای است که خود فرد می‌دهد.",
      },
      {
        k: "blank", sentence: "My account ___ been hacked twice this year.",
        accepted: ["has", "had"], skill: "VOCABULARY",
        note: "has been hacked حال کامل مجهول است؛ had been برای رویداد پیش‌تر است.",
      },
      {
        k: "blank", sentence: "Tracking ___ been turned off in the app.",
        accepted: ["has", "had"], skill: "VOCABULARY",
        note: "turn off در مجهول to off را می‌شکند: has been turned off.",
      },
      {
        k: "recall", prompt: "رمزنگاری", accepted: ["encryption"],
        note: "رمزنگاری encryption است و فعل آن encrypt.",
      },
      {
        k: "tr", prompt: "رمز من بدون رضایت من عوض شده است.",
        accepted: [
          "my password has been changed without my consent",
          "my password was changed without my consent",
        ],
        note: "without my consent یعنی بدون اجازهٔ من؛ consent غیرقابل‌شمارش است.",
      },
      {
        k: "listen", text: "My password has been changed without my consent.",
        note: "به has been changed گوش کنید: مجهول حال کامل با has.",
      },
      {
        k: "prod", prompt: "بگو چه چیزی برای امنیت حسابت اتفاق افتاده است.",
        mustUse: ["has", "been"], minWords: 8,
        accepted: ["My account has been locked since the breach."],
        hint: "با My account has been… شروع کن.",
        note: "ترتیب لنگرها: has پیش از been می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "پشتیبانی امنیتی بانک، پس از یک ورود مشکوک", partner: "کارشناس امنیت",
        prompt: "بگو حسابت هک شده است.",
        question: "What happened to your account?",
        questionFa: "برای حسابت چه شد؟",
        accepted: [
          "My account has been hacked.",
          "Someone has been using my account.",
        ],
        note: "has been hacked حال کامل مجهول است؛ انجام‌دهنده مهم نیست.",
      },
      {
        scene: "پشتیبانی امنیتی بانک، پس از یک ورود مشکوک", partner: "کارشناس امنیت",
        prompt: "بگو رمزت بدون رضایتت عوض شده است.",
        question: "Did you change your password?",
        questionFa: "رمزت را خودت عوض کردی؟",
        accepted: [
          "No, my password has been changed without my consent.",
          "I did not. It was changed without my consent.",
        ],
        note: "without my consent یعنی بی‌اجازه؛ به هیچ حرف اضافه‌ای نیاز ندارد.",
      },
      {
        scene: "پشتیبانی امنیتی بانک، پس از یک ورود مشکوک", partner: "کارشناس امنیت",
        prompt: "بگو یک ایمیل فیشینگ فرستاده شده بود.",
        question: "Did you click anything unusual?",
        questionFa: "چیز غیرعادی‌ای کلیک کردی؟",
        accepted: [
          "A phishing email has been sent to me.",
          "Yes, I got a phishing email that looked real.",
        ],
        note: "a phishing email قابل‌شمارش است و با a می‌آید.",
      },
      {
        scene: "پشتیبانی امنیتی بانک، پس از یک ورود مشکوک", partner: "کارشناس امنیت",
        prompt: "بگو داده‌هایت رمزگذاری شده‌اند.",
        question: "Is my information safe now?",
        questionFa: "اطلاعاتم حالا امن است؟",
        accepted: [
          "Your data has been encrypted.",
          "Yes, everything has been encrypted.",
        ],
        note: "data جمع است ولی با فعل مفرد هم می‌آید: data has been encrypted.",
      },
      {
        scene: "پشتیبانی امنیتی بانک، پس از یک ورود مشکوک", partner: "کارشناس امنیت",
        prompt: "بگو ردیابی را در برنامه خاموش کرده‌ای.",
        question: "Do you want to keep location tracking on?",
        questionFa: "می‌خواهی ردیابی مکان روشن بماند؟",
        accepted: [
          "No, I have turned tracking off.",
          "I turned the tracking off in the app.",
        ],
        note: "turn off دو بخشی است: ضمیر بین آن‌ها می‌آید — turned it off.",
      },
      {
        scene: "پشتیبانی امنیتی بانک، پس از یک ورود مشکوک", partner: "کارشناس امنیت",
        prompt: "بگو همیشه فرستنده را راستی‌آزمایی می‌کنی.",
        question: "Will you check emails more carefully?",
        questionFa: "ایمیل‌ها را دقیق‌تر بررسی می‌کنی؟",
        accepted: [
          "Yes, I always verify the sender.",
          "I will verify every sender from now on.",
        ],
        note: "verify مفعول مستقیم می‌گیرد: verify the sender.",
      },
    ],
  },
];
