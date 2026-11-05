// v44 content, part 2 — B2 topics 14 and 15: mental health, media literacy.
// Read by `v44_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.mental-health.lesson-01",
      order: 130,
      title: "Mental Health",
      titleFa: "سلامت روان",
      topic: "Health",
      tip: [
        "## قاعده",
        "should + مصدر ساده برای توصیه: You should talk to someone.",
        "ought to همان معنا را با لحن رسمی‌تر می‌رساند: You ought to rest.",
        "had better برای هشدار است، چون نتیجهٔ بد در انتظار است: You had better tell your manager. (بدون to)",
        "should have + قسمت سوم برای پشیمانی: I should have asked for help.",
        "## جدول",
        "You should take a proper break. — باید یک استراحت واقعی بکنی.",
        "You ought to speak to a doctor. — بهتر است با پزشک صحبت کنی.",
        "You had better tell someone today. — بهتر است همین امروز به کسی بگویی.",
        "## مثال‌ها",
        "Anxiety often starts with small worries. — اضطراب اغلب با نگرانی‌های کوچک شروع می‌شود.",
        "Burnout takes months to recover from. — بهبود از فرسودگی ماه‌ها طول می‌کشد.",
        "Therapy is not a sign of weakness. — درمان نشانهٔ ضعف نیست.",
        "## خطاهای رایج",
        "had better با to نمی‌آید: had better to rest ✗.",
        "should توصیه است، نه اجبار؛ برای اجبار must لازم است.",
        "anxiety غیرقابل‌شمارش است: a lot of anxiety ✓ / many anxieties ✗.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-advice-ought",
    vocab: {
      id: "vocab.mental-health",
      title: "Mental Health",
      titleFa: "سلامت روان",
      prerequisites: ["vocab.emotions"],
    },
    words: [
      {
        slug: "anxiety", w: "anxiety", fa: "اضطراب", ipa: "/æŋˈzaɪəti/", pos: "noun",
        def: "a strong feeling of worry that is hard to stop",
        ex: [
          ["Anxiety often starts with small worries.", "اضطراب اغلب با نگرانی‌های کوچک شروع می‌شود."],
          ["He felt anxiety before every exam.", "او پیش از هر امتحان اضطراب داشت."],
        ],
        col: ["deal with anxiety", "constant anxiety"],
        note: "anxiety غیرقابل‌شمارش است؛ صفت آن anxious است.",
      },
      {
        slug: "burnout", w: "burnout", fa: "فرسودگی شغلی", ipa: "/ˈbɜːnaʊt/", pos: "noun",
        def: "exhaustion caused by too much work over a long time",
        ex: [
          ["Burnout takes months to recover from.", "بهبود از فرسودگی ماه‌ها طول می‌کشد."],
          ["Two of the team are off with burnout.", "دو نفر از تیم به‌خاطر فرسودگی مرخصی‌اند."],
        ],
        col: ["suffer from burnout", "avoid burnout"],
        note: "burnout غیرقابل‌شمارش است و با with می‌آید: off with burnout.",
      },
      {
        slug: "therapy", w: "therapy", fa: "درمان؛ روان‌درمانی", ipa: "/ˈθerəpi/", pos: "noun",
        def: "treatment for the mind or body, often with a specialist",
        ex: [
          ["Therapy is not a sign of weakness.", "درمان نشانهٔ ضعف نیست."],
          ["She started therapy last spring.", "او بهار گذشته درمان را شروع کرد."],
        ],
        col: ["start therapy", "a year of therapy"],
        note: "therapy غیرقابل‌شمارش است؛ therapist کسی است که آن را انجام می‌دهد.",
      },
      {
        slug: "wellbeing", w: "wellbeing", fa: "سلامت روانی و رفاه", ipa: "/ˌwelˈbiːɪŋ/", pos: "noun",
        def: "the state of feeling healthy and happy",
        ex: [
          ["Sleep is the base of wellbeing.", "خواب پایهٔ سلامت روانی است."],
          ["The company measures staff wellbeing.", "شرکت سلامت کارکنان را می‌سنجد."],
        ],
        col: ["staff wellbeing", "mental wellbeing"],
        note: "wellbeing یک واژه است (well + being)؛ در انگلیسی آمریکایی well-being نوشته می‌شود.",
      },
      {
        slug: "stigma", w: "stigma", fa: "انگ؛ برچسب اجتماعی", ipa: "/ˈstɪɡmə/", pos: "noun",
        def: "a strong feeling of shame attached to something",
        ex: [
          ["There is still a stigma around therapy.", "هنوز انگ اجتماعی دور درمان هست."],
          ["Talking openly reduces the stigma.", "حرف زدن آشکارا آن انگ را کم می‌کند."],
        ],
        col: ["a social stigma", "reduce the stigma"],
        note: "stigma با around یا attached to می‌آید؛ جمع آن stigmas است.",
      },
      {
        slug: "insomnia", w: "insomnia", fa: "بی‌خوابی", ipa: "/ɪnˈsɒmniə/", pos: "noun",
        def: "the condition of not being able to sleep",
        ex: [
          ["Insomnia made everything harder.", "بی‌خوابی همه چیز را سخت‌تر کرده بود."],
          ["He has had insomnia since March.", "او از مارس بی‌خوابی دارد."],
        ],
        col: ["suffer from insomnia", "chronic insomnia"],
        note: "insomnia غیرقابل‌شمارش است و با have یا suffer from می‌آید.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "anxiety", gloss: "اضطراب",
        options: ["اضطراب", "فرسودگی", "بی‌خوابی", "انگ اجتماعی"],
        note: "anxiety حالت نگرانی شدید و پیوسته است.",
      },
      {
        k: "mcq", word: "burnout", gloss: "فرسودگی شغلی",
        options: ["فرسودگی شغلی", "اضطراب", "افسردگی", "بی‌خوابی"],
        note: "burnout از فشار کار طولانی می‌آید، نه از یک روز بد.",
      },
      {
        k: "mcq", word: "stigma", gloss: "انگ؛ برچسب اجتماعی",
        options: ["انگ؛ برچسب اجتماعی", "درمان", "پشتیبانی", "رفاه"],
        note: "stigma برداشت شرم‌آوری است که جامعه به یک موضوع می‌چسباند.",
      },
      {
        k: "blank", sentence: "You ___ take a proper break.",
        accepted: ["should", "ought to"], skill: "VOCABULARY",
        note: "should و ought to هر دو توصیه‌اند؛ ought to با to می‌آید.",
      },
      {
        k: "blank", sentence: "You ___ better tell someone today.",
        accepted: ["had", "'d"], skill: "VOCABULARY",
        note: "had better برای هشدار است؛ در گفتار 'd better هم می‌آید.",
      },
      {
        k: "recall", prompt: "بی‌خوابی", accepted: ["insomnia"],
        note: "بی‌خوابی insomnia است و با have می‌آید.",
      },
      {
        k: "tr", prompt: "باید با یک پزشک صحبت کنی.",
        accepted: [
          "you should speak to a doctor",
          "you ought to speak to a doctor",
        ],
        note: "should و ought to هر دو توصیه‌اند؛ ought to به to نیاز دارد.",
      },
      {
        k: "listen", text: "Therapy is not a sign of weakness.",
        note: "به is not a sign of weakness گوش کنید: ساختار توصیفی منفی است، نه سفارش.",
      },
      {
        k: "prod", prompt: "به همکاری که خسته است یک توصیه کن.",
        mustUse: ["should", "rest"], minWords: 8,
        accepted: ["You should rest before the burnout gets worse."],
        hint: "با You should… شروع کن و rest را در جمله بیاور.",
        note: "ترتیب لنگرها: should پیش از rest می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "اتاق استراحت دفتر، آخر یک هفتهٔ شلوغ", partner: "همکار",
        prompt: "بگو او باید یک استراحت واقعی بکند.",
        question: "I feel tired all the time. What should I do?",
        questionFa: "همیشه خسته‌ام. چه کار کنم؟",
        accepted: [
          "You should take a proper break.",
          "I think you ought to rest for a few days.",
        ],
        note: "should + مصدر ساده؛ ought to به to نیاز دارد.",
      },
      {
        scene: "اتاق استراحت دفتر، آخر یک هفتهٔ شلوغ", partner: "همکار",
        prompt: "بگو بی‌خوابی همه چیز را سخت‌تر کرده است.",
        question: "How are you sleeping?",
        questionFa: "خوابت چطور است؟",
        accepted: [
          "Insomnia has made everything harder.",
          "Not well — the insomnia is getting worse.",
        ],
        note: "insomnia غیرقابل‌شمارش است، پس فعل مفرد می‌گیرد.",
      },
      {
        scene: "اتاق استراحت دفتر، آخر یک هفتهٔ شلوغ", partner: "همکار",
        prompt: "بگو درمان نشانهٔ ضعف نیست.",
        question: "Is it strange to see a therapist?",
        questionFa: "رفتن پیش درمانگر عجیب است؟",
        accepted: [
          "No, therapy is not a sign of weakness.",
          "Not at all — therapy is just support.",
        ],
        note: "a sign of weakness ترکیب ثابتی است که ضعف را کوچک نشان می‌دهد.",
      },
      {
        scene: "اتاق استراحت دفتر، آخر یک هفتهٔ شلوغ", partner: "همکار",
        prompt: "بگو بهتر است همین امروز به مدیر بگوید.",
        question: "Should I tell the manager?",
        questionFa: "به مدیر بگویم؟",
        accepted: [
          "You had better tell the manager today.",
          "Yes, you had better say something now.",
        ],
        note: "had better بدون to می‌آید و هشدار می‌دهد.",
      },
      {
        scene: "اتاق استراحت دفتر، آخر یک هفتهٔ شلوغ", partner: "همکار",
        prompt: "بگو هنوز انگ اجتماعی دور درمان هست.",
        question: "Why do people hide it?",
        questionFa: "چرا آدم‌ها پنهانش می‌کنند؟",
        accepted: [
          "Because there is still a stigma around therapy.",
          "The stigma is still there, so people stay silent.",
        ],
        note: "a stigma around something یعنی انگِ چسبیده به آن موضوع.",
      },
      {
        scene: "اتاق استراحت دفتر، آخر یک هفتهٔ شلوغ", partner: "همکار",
        prompt: "بگو خواب پایهٔ سلامت روانی است.",
        question: "What helps most?",
        questionFa: "بیشتر چه چیزی کمک می‌کند؟",
        accepted: [
          "Sleep is the base of wellbeing.",
          "For me, sleep and regular meals help my wellbeing.",
        ],
        note: "wellbeing غیرقابل‌شمارش است؛ my wellbeing درست است.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.media-literacy.lesson-01",
      order: 131,
      title: "Media Literacy",
      titleFa: "رسانه و سواد رسانه‌ای",
      topic: "Media",
      tip: [
        "## قاعده",
        "مجهول با فعل وجهی: modal + be + قسمت سوم — A claim should be checked.",
        "برای گذشته: modal + have been + قسمت سوم — The story might have been invented.",
        "اگر فاعل مهم باشد با by می‌آید: The post was shared by thousands.",
        "مجهول وقتی طبیعی است که انجام‌دهنده معلوم نیست یا مهم نیست — دقیقاً وضعیت بیشتر خبرهای آنلاین.",
        "## جدول",
        "A claim should be checked before it is shared. — یک ادعا باید پیش از بازنشر بررسی شود.",
        "The photo must have been edited. — عکس حتماً ویرایش شده است.",
        "Headlines can be written to shock. — تیترها می‌توانند برای شوک نوشته شوند.",
        "## مثال‌ها",
        "Clickbait promises more than the article gives. — کلیک‌بیت بیشتر از چیزی که مقاله می‌دهد وعده می‌دهد.",
        "The rumour spread faster than the correction. — شایعه سریع‌تر از تصحیح پخش شد.",
        "A serious outlet names its sources. — یک رسانهٔ جدی منابعش را نام می‌برد.",
        "## خطاهای رایج",
        "پس از وجهی be می‌آید، نه been: must be checked ✓ / must checked ✗.",
        "misleading صفت است: a misleading headline ✓ / a mislead headline ✗.",
        "rumour املای بریتانیایی و rumor املای آمریکایی است؛ هر دو درست‌اند.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-with-modals",
    vocab: {
      id: "vocab.media-literacy",
      title: "Media Literacy",
      titleFa: "سواد رسانه‌ای",
      prerequisites: ["vocab.social-media"],
    },
    words: [
      {
        slug: "misleading", w: "misleading", fa: "گمراه‌کننده", ipa: "/ˌmɪsˈliːdɪŋ/", pos: "adjective",
        def: "giving a wrong idea without saying anything false",
        ex: [
          ["The headline was misleading.", "تیتر گمراه‌کننده بود."],
          ["A misleading chart hides the real trend.", "یک نمودار گمراه‌کننده روند واقعی را پنهان می‌کند."],
        ],
        col: ["a misleading headline", "deliberately misleading"],
        note: "misleading صفت است؛ فعل آن mislead و اسم آن misinformation نیست.",
      },
      {
        slug: "clickbait", w: "clickbait", fa: "کلیک‌بیت", ipa: "/ˈklɪkbeɪt/", pos: "noun",
        def: "a headline written only to make people click",
        ex: [
          ["Clickbait promises more than the article gives.", "کلیک‌بیت بیشتر از آنچه مقاله می‌دهد وعده می‌دهد."],
          ["The site is full of clickbait.", "سایت پر از کلیک‌بیت است."],
        ],
        col: ["pure clickbait", "a clickbait headline"],
        note: "clickbait غیرقابل‌شمارش است و بار منفی دارد.",
      },
      {
        slug: "rumour", w: "rumour", fa: "شایعه", ipa: "/ˈruːmə/", pos: "noun",
        def: "a story people repeat that may not be true",
        ex: [
          ["The rumour spread faster than the correction.", "شایعه سریع‌تر از تصحیح پخش شد."],
          ["They repeated a rumour as if it were a fact.", "آن‌ها شایعه‌ای را طوری تکرار کردند که انگار واقعیت است."],
        ],
        col: ["spread a rumour", "a false rumour"],
        note: "rumour قابل‌شمارش است: a rumour / several rumours؛ املای آمریکایی rumor.",
      },
      {
        slug: "outlet", w: "outlet", fa: "رسانه؛ خبرگزاری", ipa: "/ˈaʊtlet/", pos: "noun",
        def: "a newspaper, site or channel that publishes news",
        ex: [
          ["A serious outlet names its sources.", "یک رسانهٔ جدی منابعش را نام می‌برد."],
          ["Two outlets reported the same number.", "دو رسانه یک عدد را گزارش کردند."],
        ],
        col: ["a news outlet", "a trusted outlet"],
        note: "outlet در این معنا رسانهٔ خبری است؛ در برق «پریز» و در کسب‌وکار «کانال فروش» هم معنا می‌دهد.",
      },
      {
        slug: "propaganda", w: "propaganda", fa: "تبلیغات سیاسی", ipa: "/ˌprɒpəˈɡændə/", pos: "noun",
        def: "information spread to push one political view",
        ex: [
          ["The channel broadcasts propaganda, not news.", "این شبکه تبلیغات سیاسی پخش می‌کند، نه خبر."],
          ["Propaganda works by repeating one idea.", "تبلیغات سیاسی با تکرار یک ایده کار می‌کند."],
        ],
        col: ["state propaganda", "spread propaganda"],
        note: "propaganda غیرقابل‌شمارش است و بار منفی دارد؛ با advertising فرق دارد.",
      },
      {
        slug: "satire", w: "satire", fa: "طنز؛ هجو", ipa: "/ˈsætaɪə/", pos: "noun",
        def: "humour that criticises people or institutions",
        ex: [
          ["Satire is often mistaken for news.", "طنز اغلب با خبر اشتباه گرفته می‌شود."],
          ["The show uses satire to criticise power.", "این برنامه با طنز قدرت را نقد می‌کند."],
        ],
        col: ["political satire", "a piece of satire"],
        note: "satire غیرقابل‌شمارش است؛ صفت آن satirical و نویسندهٔ آن satirist است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "misleading", gloss: "گمراه‌کننده",
        options: ["گمراه‌کننده", "شایعه", "تبلیغات سیاسی", "طنز"],
        note: "misleading یعنی چیزی را طوری نشان دادن که برداشت غلط بدهد.",
      },
      {
        k: "mcq", word: "clickbait", gloss: "کلیک‌بیت",
        options: ["کلیک‌بیت", "تیتر", "منبع", "مقاله"],
        note: "clickbait تیتری است که فقط برای کلیک نوشته می‌شود.",
      },
      {
        k: "mcq", word: "outlet", gloss: "رسانه؛ خبرگزاری",
        options: ["رسانه؛ خبرگزاری", "شبکهٔ اجتماعی", "تبلیغات سیاسی", "شایعه"],
        note: "news outlet یعنی رسانه‌ای که خبر منتشر می‌کند.",
      },
      {
        k: "blank", sentence: "A claim should ___ checked before it is shared.",
        accepted: ["be", "get"], skill: "VOCABULARY",
        note: "پس از وجهی be می‌آید؛ get checked در گفتار هم رایج است.",
      },
      {
        k: "blank", sentence: "The story might ___ been invented.",
        accepted: ["have", "'ve"], skill: "VOCABULARY",
        note: "برای گذشته: might have been + قسمت سوم.",
      },
      {
        k: "recall", prompt: "شایعه", accepted: ["rumour", "rumor"],
        note: "شایعه rumour است (آمریکایی: rumor).",
      },
      {
        k: "tr", prompt: "تیتر گمراه‌کننده بود.",
        accepted: [
          "the headline was misleading",
          "it was a misleading headline",
        ],
        note: "misleading صفت است و پس از was می‌آید.",
      },
      {
        k: "listen", text: "A claim should be checked before it is shared.",
        note: "به should be checked گوش کنید: وجهی + be + قسمت سوم.",
      },
      {
        k: "prod", prompt: "دربارهٔ یک خبر مشکوک یک توصیه بگو.",
        mustUse: ["should", "be"], minWords: 8,
        accepted: ["A rumour should be checked before it is shared."],
        hint: "با A rumour should be… شروع کن.",
        note: "ترتیب لنگرها: should پیش از be می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "کلاس سواد رسانه‌ای، بحث دربارهٔ یک خبر وایرال", partner: "معلم",
        prompt: "بگو تیتر گمراه‌کننده بود.",
        question: "Why did you doubt the headline?",
        questionFa: "چرا به تیتر شک کردی؟",
        accepted: [
          "The headline was misleading.",
          "Because the headline was misleading and the article said something else.",
        ],
        note: "misleading صفت است؛ برای فعل از mislead استفاده کن.",
      },
      {
        scene: "کلاس سواد رسانه‌ای، بحث دربارهٔ یک خبر وایرال", partner: "معلم",
        prompt: "بگو شایعه سریع‌تر از تصحیح پخش شد.",
        question: "How did the story travel so fast?",
        questionFa: "چطور خبر این‌قدر سریع پخش شد؟",
        accepted: [
          "The rumour spread faster than the correction.",
          "People shared the rumour before anyone checked it.",
        ],
        note: "spread در گذشته هم spread است، بدون ed.",
      },
      {
        scene: "کلاس سواد رسانه‌ای، بحث دربارهٔ یک خبر وایرال", partner: "معلم",
        prompt: "بگو عکس حتماً ویرایش شده است.",
        question: "Is the photo real?",
        questionFa: "عکس واقعی است؟",
        accepted: [
          "The photo must have been edited.",
          "It must have been changed before it was posted.",
        ],
        note: "must have been + قسمت سوم حدس قوی دربارهٔ گذشتهٔ مجهول است.",
      },
      {
        scene: "کلاس سواد رسانه‌ای، بحث دربارهٔ یک خبر وایرال", partner: "معلم",
        prompt: "بگو یک ادعا باید پیش از بازنشر بررسی شود.",
        question: "What should we do before sharing?",
        questionFa: "پیش از بازنشر چه باید بکنیم؟",
        accepted: [
          "A claim should be checked before it is shared.",
          "Every claim should be checked first.",
        ],
        note: "should be + قسمت سوم، توصیه در حالت مجهول است.",
      },
      {
        scene: "کلاس سواد رسانه‌ای، بحث دربارهٔ یک خبر وایرال", partner: "معلم",
        prompt: "بگو یک رسانهٔ جدی منابعش را نام می‌برد.",
        question: "How do you judge a news site?",
        questionFa: "یک سایت خبری را چطور قضاوت می‌کنی؟",
        accepted: [
          "A serious outlet names its sources.",
          "I trust outlets that name their sources.",
        ],
        note: "outlet مفرد است و با a می‌آید؛ جمع آن outlets.",
      },
      {
        scene: "کلاس سواد رسانه‌ای، بحث دربارهٔ یک خبر وایرال", partner: "معلم",
        prompt: "بگو طنز اغلب با خبر اشتباه گرفته می‌شود.",
        question: "Why do people believe satire?",
        questionFa: "چرا آدم‌ها طنز را باور می‌کنند؟",
        accepted: [
          "Because satire is often mistaken for news.",
          "Satire looks like news, so people share it.",
        ],
        note: "be mistaken for something یعنی با چیز دیگری اشتباه گرفته شدن.",
      },
    ],
  },
];
