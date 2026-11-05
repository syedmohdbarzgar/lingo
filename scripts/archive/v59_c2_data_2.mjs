// v59 content — C2 syllabus topics 25 (reasoning under uncertainty) and 26
// (communication in sensitive situations). Read by `v59_c2_batch.mjs`; never run
// on its own.
export default [
  {
    lesson: {
      id: "c2.uncertainty.lesson-01",
      order: 188,
      title: "Reasoning Under Uncertainty",
      titleFa: "استدلال در شرایط عدم قطعیت",
      topic: "Reasoning",
      tip: [
        "## قاعده",
        "وقتی دربارهٔ احتمال حرف می‌زنیم، درجهٔ اطمینان را با فعل وجهی نشان می‌دهیم، نه با قید مطلق: must برای نتیجهٔ قطعی، might/could برای احتمال، may برای احتمال رسمی‌تر.",
        "must به معنای «حتماً» استنباط است، نه اجبار؛ اجبار با have to می‌آید: That must be a rounding error (استنباط) در برابر You have to check it (اجبار).",
        "برای رد کردن احتمال، can't be به کار می‌رود: The figure can't be final while two totals disagree.",
        "## جدول",
        "The estimate might be too low, but the report does not say why. — برآورد ممکن است خیلی کم باشد، اما گزارش نمی‌گوید چرا.",
        "That must be a rounding error, because the totals do not match. — آن باید خطای گرد کردن باشد، چون جمع‌ها نمی‌خوانند.",
        "The posterior could move again after the next survey. — برآورد پسین ممکن است پس از نظرسنجی بعدی دوباره تغییر کند.",
        "## مثال‌ها",
        "I give the forecast low credence until the data arrive. — تا داده‌ها نرسند، به پیش‌بینی درجهٔ باور کمی می‌دهم.",
        "The prior came from last year's survey. — باور پیشین از نظرسنجی سال گذشته آمده بود.",
        "We check calibration against past forecasts. — همترازی پیش‌بینی‌ها را با پیش‌بینی‌های گذشته می‌سنجیم.",
        "The robustness of the result means it holds when one assumption changes. — استحکام نتیجه یعنی با تغییر یک فرض هم برجا می‌ماند.",
        "## خطاهای رایج",
        "It must to be a rounding error. ✗ — پس از must فعل بدون to می‌آید: must be.",
        "It maybe is a rounding error. ✗ — maybe قید است؛ برای احتمال از may be یا might be استفاده کن.",
        "credence را با confidence یکی نگیر: credence درجهٔ باور به یک گزاره است، confidence اعتماد به خودِ توانایی.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-of-deduction",
    vocab: {
      id: "vocab.reasoning-uncertainty",
      title: "Reasoning Under Uncertainty",
      titleFa: "استدلال در شرایط عدم قطعیت",
      prerequisites: ["vocab.discourse-analysis"],
    },
    words: [
      {
        slug: "credence", w: "credence", fa: "درجهٔ باور", ipa: "/ˈkriːdns/", pos: "noun",
        def: "how strongly you hold a belief, expressed as a degree rather than a yes or no",
        ex: [
          ["I give the forecast low credence until the data arrive.", "تا داده‌ها نرسند، به پیش‌بینی درجهٔ باور کمی می‌دهم."],
          ["A claim gains credence when a second method agrees.", "یک ادعا وقتی روش دومی هم آن را تأیید کند، درجهٔ باور می‌گیرد."],
        ],
        col: ["give credence to", "low credence"],
        note: "فعل ثابت آن give credence to است؛ غیرقابل‌شمارش است، پس a credence نمی‌گوییم.",
      },
      {
        slug: "prior", w: "prior", fa: "باور پیشین", ipa: "/ˈpraɪə/", pos: "noun",
        def: "the belief you hold before the new evidence arrives",
        ex: [
          ["The prior came from last year's survey.", "باور پیشین از نظرسنجی سال گذشته آمده بود."],
          ["A weak prior is easy to overturn.", "باور پیشینِ ضعیف به‌سادگی واژگون می‌شود."],
        ],
        col: ["a strong prior", "set the prior"],
        note: "prior در آمار اسم است؛ در کاربرد روزمره صفت به معنای «پیشین»: prior experience.",
      },
      {
        slug: "posterior", w: "posterior", fa: "باور پسین", ipa: "/pɒˈstɪəriə/", pos: "noun",
        def: "the updated belief after the new evidence has been weighed",
        ex: [
          ["The posterior could move again after the next survey.", "باور پسین ممکن است پس از نظرسنجی بعدی دوباره تغییر کند."],
          ["The posterior is wide, so the decision waits.", "باور پسین گسترده است، پس تصمیم می‌ماند."],
        ],
        col: ["a wide posterior", "update the posterior"],
        note: "posterior جفتِ prior است: یکی پیش از شواهد، دیگری پس از آن.",
      },
      {
        slug: "calibration", w: "calibration", fa: "همترازی پیش‌بینی‌ها", ipa: "/ˌkælɪˈbreɪʃn/", pos: "noun",
        def: "how closely the confidence you state matches how often you are right",
        ex: [
          ["We check calibration against past forecasts.", "همترازی پیش‌بینی‌ها را با پیش‌بینی‌های گذشته می‌سنجیم."],
          ["Good calibration matters more than a bold call.", "همترازی خوب از پیش‌بینی جسورانه مهم‌تر است."],
        ],
        col: ["check calibration", "poor calibration"],
        note: "calibration دربارهٔ هم‌خوانیِ اطمینانِ اعلام‌شده با دقت واقعی است، نه دقت تنها.",
      },
      {
        slug: "robustness", w: "robustness", fa: "استحکام نتیجه", ipa: "/rəʊˈbʌstnəs/", pos: "noun",
        def: "the quality of a result that survives a change in its assumptions",
        ex: [
          ["The robustness of the result means it holds when one assumption changes.", "استحکام نتیجه یعنی با تغییر یک فرض هم برجا می‌ماند."],
          ["We test robustness by removing the weakest input.", "استحکام را با حذف ضعیف‌ترین ورودی می‌سنجیم."],
        ],
        col: ["test robustness", "the robustness of the estimate"],
        note: "صفت آن robust است؛ robustness با stability فرق دارد: stability ثبات در زمان است.",
      },
      {
        slug: "expected value", w: "expected value", fa: "ارزش انتظاری", ipa: "/ɪkˈspektɪd ˈvæljuː/", pos: "noun",
        def: "the average outcome you would get if the same choice were repeated many times",
        ex: [
          ["The expected value is small, but the downside is large.", "ارزش انتظاری کوچک است، اما ریسک زیان بزرگ است."],
          ["A high expected value does not remove the risk.", "ارزش انتظاریِ بالا ریسک را از میان برنمی‌دارد."],
        ],
        col: ["a positive expected value", "calculate the expected value"],
        note: "اسم مرکب است و با حرف تعریف the می‌آید: the expected value of the bet.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "credence", gloss: "درجهٔ باور",
        options: ["درجهٔ باور", "باور پیشین", "ارزش انتظاری", "استحکام نتیجه"],
        note: "credence شدتِ باور به یک گزاره را به‌صورت درجه نشان می‌دهد.",
      },
      {
        k: "mcq", word: "calibration", gloss: "همترازی پیش‌بینی‌ها",
        options: ["همترازی پیش‌بینی‌ها", "باور پسین", "استحکام نتیجه", "باور پیشین"],
        note: "calibration می‌سنجد اطمینانِ اعلام‌شده چقدر با دقت واقعی می‌خواند.",
      },
      {
        k: "mcq", word: "robustness", gloss: "استحکام نتیجه",
        options: ["استحکام نتیجه", "ارزش انتظاری", "درجهٔ باور", "همترازی پیش‌بینی‌ها"],
        note: "robustness یعنی نتیجه با تغییر فرض‌ها هم برجا می‌ماند.",
      },
      {
        k: "blank", sentence: "The estimate ___ be too low, but the report does not say why.",
        accepted: ["might", "could"], skill: "GRAMMAR",
        note: "برای احتمال، might یا could؛ must ادعای قطعیت می‌کند.",
      },
      {
        k: "blank", sentence: "That ___ be a rounding error, because the totals do not match.",
        accepted: ["must", "can only"], skill: "GRAMMAR",
        note: "استنباط قطعی با must؛ can only هم همان قطعیت را با تأکید می‌آورد.",
      },
      {
        k: "recall", prompt: "درجهٔ باور", accepted: ["credence"],
        note: "درجهٔ باور credence است؛ فعلش give credence to.",
      },
      {
        k: "tr", prompt: "برآورد نخست ممکن است بیش از حد خوش‌بینانه باشد.",
        accepted: [
          "the first estimate might be too optimistic",
          "the initial estimate might be too optimistic",
        ],
        note: "might be برای احتمال؛ too optimistic یعنی بیش از حد خوش‌بین.",
      },
      {
        k: "listen", text: "A robust estimate does not move when one assumption changes.",
        note: "به does not move گوش کنید: استحکام نتیجه با تغییر یک فرض سنجیده می‌شود.",
      },
      {
        k: "prod", prompt: "بگو استحکام نتیجه یعنی با تغییر یک فرض هم برجا می‌ماند.",
        mustUse: ["robustness"], minWords: 8,
        accepted: ["The robustness of the result means it holds when one assumption changes."],
        hint: "با The robustness of the result means… شروع کن.",
        note: "hold در این معنا لازم است: نتیجه «برجا می‌ماند».",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ بررسی مدل", partner: "تحلیل‌گر ارشد",
        prompt: "بگو تا داده‌ها نرسند، به پیش‌بینی درجهٔ باور کمی می‌دهی.",
        question: "How much credence do you give the forecast?",
        questionFa: "چه‌قدر به پیش‌بینی باور داری؟",
        accepted: [
          "I give it low credence until the data arrive.",
          "My credence in the forecast is low.",
        ],
        note: "give it low credence: ضمیر مفعول پیش از اسم می‌آید.",
      },
      {
        scene: "جلسهٔ بررسی مدل", partner: "تحلیل‌گر ارشد",
        prompt: "بگو باور پیشین از نظرسنجی سال گذشته آمده است.",
        question: "Where did your prior come from?",
        questionFa: "باور پیشین تو از کجا آمده؟",
        accepted: [
          "The prior came from last year's survey.",
          "Our prior is based on two earlier studies.",
        ],
        note: "be based on برای منبع یک باور؛ come from هم درست است.",
      },
      {
        scene: "جلسهٔ بررسی مدل", partner: "تحلیل‌گر ارشد",
        prompt: "بگو باور پسین با داده‌های تازه تغییر کرد ولی هنوز نامطمئن است.",
        question: "Is the posterior still uncertain?",
        questionFa: "باور پسین هنوز نامطمئن است؟",
        accepted: [
          "The posterior moved, but it stays uncertain.",
          "The posterior is still wide after the new data.",
        ],
        note: "wide برای بازهٔ نامطمئن؛ still جای but را در جملهٔ دوم می‌گیرد.",
      },
      {
        scene: "جلسهٔ بررسی مدل", partner: "تحلیل‌گر ارشد",
        prompt: "بگو همترازی را با پیش‌بینی‌های گذشته می‌سنجید.",
        question: "How do you check your calibration?",
        questionFa: "همترازی را چطور می‌سنجید؟",
        accepted: [
          "We check calibration against past forecasts.",
          "Calibration is checked every quarter.",
        ],
        note: "check something against something: سنجیدن در برابر مرجع.",
      },
      {
        scene: "جلسهٔ بررسی مدل", partner: "تحلیل‌گر ارشد",
        prompt: "بگو استحکام کم است، چون همه‌چیز به یک فرض بند است.",
        question: "How robust is the conclusion?",
        questionFa: "نتیجه چقدر استوار است؟",
        accepted: [
          "The robustness is weak, because one assumption carries it.",
          "The conclusion shows little robustness.",
        ],
        note: "little robustness یعنی استحکام کم؛ با few اشتباه نشود، چون غیرقابل‌شمارش است.",
      },
      {
        scene: "جلسهٔ بررسی مدل", partner: "تحلیل‌گر ارشد",
        prompt: "بگو باید بازه را بگوییم، نه یک عدد تنها.",
        question: "What should we tell the board?",
        questionFa: "به هیئت‌مدیره چه بگوییم؟",
        accepted: [
          "We should give the range, not one number.",
          "Tell them the expected value and the range.",
        ],
        note: "the range در برابر one number: عدم قطعیت را با بازه نشان بده.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.sensitive.lesson-01",
      order: 189,
      title: "Communication in Sensitive Situations",
      titleFa: "ارتباطات در شرایط حساس",
      topic: "Communication",
      tip: [
        "## قاعده",
        "برای پیشنهادِ محترمانه و فرضی از second conditional استفاده کن: If + past simple, would + base verb.",
        "در این ساخت were برای همهٔ ضمیرها رسمی‌تر است، اما was در گفتار روزمره پذیرفته است: If I were in your position…",
        "این ساخت فاصله می‌سازد و ادعا را نرم می‌کند؛ در موقعیت حساس همان فاصله لازم است، چون توصیه را به گزینه تبدیل می‌کند.",
        "## جدول",
        "If I were in your position, I would wait a week. — اگر جای تو بودم، یک هفته صبر می‌کردم.",
        "If the family heard the news today, would they want the details? — اگر خانواده امروز خبر را بشنود، آیا جزئیات را می‌خواهد؟",
        "If we said nothing, the silence would speak for us. — اگر چیزی نمی‌گفتیم، سکوت به‌جای ما حرف می‌زد.",
        "## مثال‌ها",
        "We open with condolence, not with facts. — با تسلیت آغاز می‌کنیم، نه با واقعیت‌ها.",
        "Discretion means the name stays out of the email. — احتیاط در افشا یعنی نام در ایمیل نمی‌آید.",
        "Safeguarding goes to the trained officer. — صیانت از افراد آسیب‌پذیر به افسر آموزش‌دیده سپرده می‌شود.",
        "A face-saving line would soften it. — یک جملهٔ آبرومندانه آن را نرم می‌کند.",
        "Candour helps only after the family has been heard. — صراحت تنها پس از شنیده شدن خانواده سود می‌دهد.",
        "## خطاهای رایج",
        "If I would be in your position… ✗ — در بند if فعل وجهی نمی‌آید: If I were in your position…",
        "If the family hears the news, would they want the details? ✗ — در بند if حال ساده با would در جملهٔ اصلی هم‌نشین نمی‌شود؛ past simple لازم است.",
        "discretion را با secrecy یکی نگیر: discretion انتخاب آگاهانهٔ سکوت است، secrecy پنهان‌کاری.",
      ].join("\n"),
    },
    grammarNode: "grammar.second-conditional",
    vocab: {
      id: "vocab.sensitive-communication",
      title: "Communication in Sensitive Situations",
      titleFa: "ارتباطات در شرایط حساس",
      prerequisites: ["vocab.workplace-conflict"],
    },
    words: [
      {
        slug: "condolence", w: "condolence", fa: "تسلیت", ipa: "/kənˈdəʊləns/", pos: "noun",
        def: "an expression of sympathy to someone who has lost a person close to them",
        ex: [
          ["We open with condolence, not with facts.", "با تسلیت آغاز می‌کنیم، نه با واقعیت‌ها."],
          ["A short note of condolence is better than a long speech.", "یادداشت کوتاه تسلیت از سخنرانی طولانی بهتر است."],
        ],
        col: ["offer condolences", "a letter of condolence"],
        note: "در تسلیت رسمی جمع آن رایج است: to offer condolences.",
      },
      {
        slug: "de-escalate", w: "de-escalate", fa: "از تنش کاستن", ipa: "/ˌdiːˈeskəleɪt/", pos: "verb",
        def: "to lower the tension in a conflict instead of pushing it higher",
        ex: [
          ["We de-escalate by lowering our voices.", "با پایین آوردن صدایمان از تنش می‌کاهیم."],
          ["A short pause can de-escalate a meeting.", "یک مکث کوتاه می‌تواند تنش جلسه را کم کند."],
        ],
        col: ["de-escalate a conflict", "try to de-escalate"],
        note: "خط پیوند آن ثابت است؛ متضادش escalate («شدت دادن») است.",
      },
      {
        slug: "discretion", w: "discretion", fa: "احتیاط در افشا", ipa: "/dɪˈskreʃn/", pos: "noun",
        def: "the good judgement to keep information private when sharing it would cause harm",
        ex: [
          ["Discretion means the name stays out of the email.", "احتیاط در افشا یعنی نام در ایمیل نمی‌آید."],
          ["Use discretion when the detail is not yours to share.", "وقتی جزئیات مال تو نیست، احتیاط کن."],
        ],
        col: ["use discretion", "at your discretion"],
        note: "at your discretion یعنی به صلاحدید تو؛ غیرقابل‌شمارش است.",
      },
      {
        slug: "safeguarding", w: "safeguarding", fa: "صیانت از افراد آسیب‌پذیر", ipa: "/ˈseɪfɡɑːdɪŋ/", pos: "noun",
        def: "the duty to protect people who cannot protect themselves in a situation",
        ex: [
          ["Safeguarding goes to the trained officer.", "صیانت از افراد آسیب‌پذیر به افسر آموزش‌دیده سپرده می‌شود."],
          ["Safeguarding comes before the reputation of the team.", "صیانت از افراد آسیب‌پذیر بر اعتبار تیم مقدم است."],
        ],
        col: ["a safeguarding concern", "the safeguarding lead"],
        note: "در محیط کار و مدرسه اصطلاح ثابت است؛ فعلش safeguard است.",
      },
      {
        slug: "face-saving", w: "face-saving", fa: "حفظ آبرو", ipa: "/ˈfeɪs seɪvɪŋ/", pos: "adjective",
        def: "designed to let someone keep their dignity while a problem is corrected",
        ex: [
          ["A face-saving line would soften it.", "یک جملهٔ آبرومندانه آن را نرم می‌کند."],
          ["The face-saving version blames the process, not the person.", "نسخهٔ آبرومندانه فرایند را مقصر می‌داند، نه شخص را."],
        ],
        col: ["a face-saving option", "a face-saving exit"],
        note: "صفت است و پیش از اسم می‌آید؛ خودِ عبارت lose face یعنی آبرو از دست دادن.",
      },
      {
        slug: "candour", w: "candour", fa: "صراحت", ipa: "/ˈkændə/", pos: "noun",
        def: "honest, direct speech about something difficult, offered without cruelty",
        ex: [
          ["Candour helps only after the family has been heard.", "صراحت تنها پس از شنیده شدن خانواده سود می‌دهد."],
          ["Her candour made the bad news easier to carry.", "صراحت او حمل خبر بد را آسان‌تر کرد."],
        ],
        col: ["refreshing candour", "speak with candour"],
        note: "املای آمریکایی آن candor است؛ غیرقابل‌شمارش است، پس a candour نمی‌گوییم.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "condolence", gloss: "تسلیت",
        options: ["تسلیت", "صراحت", "حفظ آبرو", "احتیاط در افشا"],
        note: "condolence ابراز همدردی با کسی است که عزیزی را از دست داده است.",
      },
      {
        k: "mcq", word: "discretion", gloss: "احتیاط در افشا",
        options: ["احتیاط در افشا", "صیانت از افراد آسیب‌پذیر", "از تنش کاستن", "تسلیت"],
        note: "discretion یعنی با قضاوت درست، اطلاعات را محرمانه نگه داشتن.",
      },
      {
        k: "mcq", word: "safeguarding", gloss: "صیانت از افراد آسیب‌پذیر",
        options: ["صیانت از افراد آسیب‌پذیر", "حفظ آبرو", "صراحت", "از تنش کاستن"],
        note: "safeguarding وظیفهٔ محافظت از کسانی است که خودشان نمی‌توانند از خود دفاع کنند.",
      },
      {
        k: "blank", sentence: "If I ___ in your position, I would wait a week.",
        accepted: ["were", "was"], skill: "GRAMMAR",
        note: "در second conditional، were رسمی‌تر و was در گفتار روزمره پذیرفته است.",
      },
      {
        k: "blank", sentence: "If the family ___ the news today, would they want the details?",
        accepted: ["heard", "received"], skill: "GRAMMAR",
        note: "در بند if فعل گذشته می‌آید: heard یا received، نه hears.",
      },
      {
        k: "recall", prompt: "تسلیت", accepted: ["condolence"],
        note: "تسلیت condolence است؛ در تسلیت رسمی جمع آن رایج است.",
      },
      {
        k: "tr", prompt: "اگر جای تو بودم، پیش از هر تصمیمی با او حرف می‌زدم.",
        accepted: [
          "if i were in your position i would talk to him before any decision",
          "if i were you i would talk to him before any decision",
        ],
        note: "If I were you / in your position: هر دو ساخت ثابت‌اند.",
      },
      {
        k: "listen", text: "Candour helps only after the family has been heard.",
        note: "به has been heard گوش کنید: present perfect passive، شنیده شدن پیش از صراحت.",
      },
      {
        k: "prod", prompt: "بگو صراحت تنها پس از شنیده شدن خانواده سود می‌دهد.",
        mustUse: ["candour"], minWords: 8,
        accepted: ["Candour helps only after the family has been heard."],
        hint: "با Candour helps only after… شروع کن.",
        note: "only after در آغاز متمم، ترتیب را برجسته می‌کند.",
      },
    ],
    dialogue: [
      {
        scene: "اتاق جلسهٔ بحران", partner: "مدیر منابع انسانی",
        prompt: "بگو با تسلیت آغاز می‌کنیم، نه با واقعیت‌ها.",
        question: "How do we open the conversation?",
        questionFa: "گفت‌وگو را چطور آغاز می‌کنیم؟",
        accepted: [
          "We open with condolence, not with facts.",
          "We start with condolence and then the facts.",
        ],
        note: "open with: آغاز کردن با چیزی؛ not with facts تقابل را می‌سازد.",
      },
      {
        scene: "اتاق جلسهٔ بحران", partner: "مدیر منابع انسانی",
        prompt: "بگو احتیاط در افشا یعنی نام در ایمیل نمی‌آید.",
        question: "Should we name the colleague who reported it?",
        questionFa: "نام همکاری را که گزارش داده ببریم؟",
        accepted: [
          "We use discretion and leave the name out.",
          "Discretion means the name stays out of the email.",
        ],
        note: "leave something out یعنی حذف کردن؛ stay out of یعنی بیرون ماندن.",
      },
      {
        scene: "اتاق جلسهٔ بحران", partner: "مدیر منابع انسانی",
        prompt: "بگو با پایین آوردن صدا از تنش می‌کاهیم.",
        question: "How do we keep the meeting calm?",
        questionFa: "جلسه را چطور آرام نگه می‌داریم؟",
        accepted: [
          "We de-escalate by lowering our voices.",
          "A short pause helps us de-escalate.",
        ],
        note: "by + gerund روش را بیان می‌کند: de-escalate by lowering…",
      },
      {
        scene: "اتاق جلسهٔ بحران", partner: "مدیر منابع انسانی",
        prompt: "بگو صیانت از افراد آسیب‌پذیر به افسر آموزش‌دیده سپرده می‌شود.",
        question: "Who handles safeguarding?",
        questionFa: "صیانت از افراد آسیب‌پذیر را چه کسی بر عهده دارد؟",
        accepted: [
          "Safeguarding goes to the trained officer.",
          "The safeguarding lead takes that part.",
        ],
        note: "go to somebody برای مسئولیت؛ lead در این معنا یعنی مسئول اصلی.",
      },
      {
        scene: "اتاق جلسهٔ بحران", partner: "مدیر منابع انسانی",
        prompt: "بگو یک جملهٔ آبرومندانه آن را نرم می‌کند.",
        question: "Is the note too blunt?",
        questionFa: "یادداشت زیادی صریح نیست؟",
        accepted: [
          "It needs a face-saving opening.",
          "A face-saving line would soften it.",
        ],
        note: "would soften آن را نرم می‌کند؛ فرضی، پس مؤدبانه‌تر از will است.",
      },
      {
        scene: "اتاق جلسهٔ بحران", partner: "مدیر منابع انسانی",
        prompt: "بگو اگر آن‌ها نه بگویند، همان را با صراحت می‌نویسی.",
        question: "What if they refuse to talk?",
        questionFa: "اگر نخواهند حرف بزنند چه؟",
        accepted: [
          "If they said no, I would write it down with candour.",
          "If they refused, we would offer another time.",
        ],
        note: "با If + past simple، پاسخ would می‌گیرد؛ این ساخت پیشنهاد را نرم می‌کند.",
      },
    ],
  },
];
