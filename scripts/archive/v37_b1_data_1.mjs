// v37 content, part 1 — B1 topics 6 and 7: international travel problems and
// cultural differences. Read by `v37_b1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b1.travel-problems.lesson-01",
      order: 100,
      title: "International Travel Problems",
      titleFa: "سفرهای خارجی و مشکلات سفر",
      topic: "Travel",
      tip: [
        "## قاعده",
        "برای تعریف مشکل سفر گذشتهٔ ساده و استمراری را با هم می‌آوریم: I was waiting at the gate when they cancelled the flight.",
        "برای پرواز و بار از مجهول استفاده می‌کنیم: The flight was delayed، My luggage was lost.",
        "برای شکایت مؤدبانه: Could you tell me why the flight is delayed?",
        "برای درخواست پول یا جبران: I would like a refund و Can I claim money for the delay?",
        "## جدول",
        "My flight was cancelled. — پروازم لغو شد.",
        "I missed my connection. — پرواز اتصالی‌ام را از دست دادم.",
        "My luggage is missing. — چمدانم گم شده است.",
        "The airline offered me a hotel. — شرکت هواپیمایی هتل به من پیشنهاد داد.",
        "## مثال‌ها",
        "There was a long delay at the airport. — در فرودگاه تأخیر طولانی بود.",
        "Where do I claim my luggage? — چمدانم را کجا تحویل بگیرم؟",
        "The refund arrived after two weeks. — بازپرداخت بعد از دو هفته رسید.",
        "## خطاهای رایج",
        "luggage غیرقابل‌شمارش است: luggage، نه luggages؛ برای شمارش a piece of luggage می‌گوییم.",
        "cancel در مجهول cancelled می‌شود؛ my flight cancelled غلط است.",
        "connection در سفر «پرواز یا قطار اتصالی» است، نه فقط «ارتباط».",
      ].join("\n"),
    },
    grammarNode: "grammar.past-continuous",
    vocab: {
      id: "vocab.travel-problems",
      title: "International Travel Problems",
      titleFa: "مشکلات سفر",
      prerequisites: ["vocab.holiday-plans"],
    },
    words: [
      {
        slug: "delay", w: "delay", fa: "تأخیر", ipa: "/dɪˈleɪ/", pos: "noun",
        def: "a time when something happens later than planned",
        ex: [
          ["There was a long delay at the airport.", "در فرودگاه تأخیر طولانی بود."],
          ["They delay the flight when the weather is bad.", "وقتی هوا بد است پرواز را به تأخیر می‌اندازند."],
        ],
        col: ["a long delay", "a flight delay"],
        note: "delay هم اسم است هم فعل: a two-hour delay / they delayed the flight.",
      },
      {
        slug: "cancel", w: "cancel", fa: "لغو کردن", ipa: "/ˈkænsl/", pos: "verb",
        def: "to say that a plan or a flight will not happen",
        ex: [
          ["The airline had to cancel my flight.", "شرکت هواپیمایی مجبور شد پروازم را لغو کند."],
          ["They cancel flights in bad weather.", "در هوای بد پروازها را لغو می‌کنند."],
        ],
        col: ["cancel a flight", "cancel a booking"],
        note: "گذشته و قسمت سوم cancel، cancelled است؛ در مجهول: my flight was cancelled.",
      },
      {
        slug: "luggage", w: "luggage", fa: "چمدان و بار", ipa: "/ˈlʌɡɪdʒ/", pos: "noun",
        def: "the bags you take with you when you travel",
        ex: [
          ["My luggage did not arrive.", "بارم نرسید."],
          ["You can leave your luggage at the hotel.", "می‌توانی بارت را در هتل بگذاری."],
        ],
        col: ["hand luggage", "a piece of luggage"],
        note: "luggage غیرقابل‌شمارش است: luggage، نه luggages؛ برای شمارش a piece of luggage.",
      },
      {
        slug: "connection", w: "connection", fa: "پرواز یا قطار اتصالی", ipa: "/kəˈnekʃn/", pos: "noun",
        def: "the next plane or train you take to reach your destination",
        ex: [
          ["I missed my connection in Istanbul.", "پرواز اتصالی‌ام را در استانبول از دست دادم."],
          ["The connection was only twenty minutes.", "پرواز اتصالی فقط بیست دقیقه فاصله داشت."],
        ],
        col: ["miss a connection", "a tight connection"],
        note: "connection در سفر یعنی سرویس بعدی که شما را به مقصد می‌رساند.",
      },
      {
        slug: "claim", w: "claim", fa: "مطالبه کردن؛ تحویل گرفتن", ipa: "/kleɪm/", pos: "verb",
        def: "to ask for money or for something that belongs to you",
        ex: [
          ["You can claim money for the delay.", "می‌توانی برای تأخیر پول مطالبه کنی."],
          ["Where do I claim my luggage?", "بارم را کجا تحویل بگیرم؟"],
        ],
        col: ["claim a refund", "claim your luggage"],
        note: "claim در فرودگاه یعنی «بار را تحویل گرفتن» و در بیمه یعنی «مطالبه کردن».",
      },
      {
        slug: "refund", w: "refund", fa: "بازپرداخت", ipa: "/ˈriːfʌnd/", pos: "noun",
        def: "money you get back when you pay for something you did not get",
        ex: [
          ["I asked for a refund.", "درخواست بازپرداخت کردم."],
          ["The refund arrived after two weeks.", "بازپرداخت بعد از دو هفته رسید."],
        ],
        col: ["ask for a refund", "a full refund"],
        note: "refund اسم است با تکیه روی بخش اول؛ فعل آن to refund با تکیه روی بخش دوم خوانده می‌شود.",
      },
    ],
    exercises: [
      {
        // `delay` is already asked in a2.everyday-problems.ex.03, and two identical
        // meaning items would test nothing new — this slot names the lesson's other
        // travel noun instead (the word is still taught and reviewed).
        k: "mcq", word: "connection", gloss: "پرواز یا قطار اتصالی",
        options: ["پرواز یا قطار اتصالی", "تأخیر", "بازپرداخت", "چمدان و بار"],
        note: "connection «سرویس اتصالی» است؛ تأخیر delay و بازپرداخت refund است.",
      },
      {
        k: "mcq", word: "luggage", gloss: "چمدان و بار",
        options: ["بلیت", "چمدان و بار", "گذرنامه", "صندلی"],
        note: "luggage «بار و چمدان» است و غیرقابل‌شمارش می‌ماند.",
      },
      {
        k: "mcq", word: "refund", gloss: "بازپرداخت",
        options: ["بازپرداخت", "جریمه", "تخفیف", "بیمه"],
        note: "refund «بازپرداخت» است؛ جریمه fine و تخفیف discount است.",
      },
      {
        k: "blank", sentence: "The flight was ___ because of the weather.",
        accepted: ["cancelled", "delayed"], skill: "GRAMMAR",
        note: "در مجهول was + قسمت سوم فعل می‌آید: was cancelled یا was delayed.",
      },
      {
        k: "blank", sentence: "I asked for a ___ because the flight was late.",
        accepted: ["refund", "discount"], skill: "VOCABULARY",
        note: "بعد از a اسم مفرد می‌آید: a refund یا a discount.",
      },
      {
        k: "recall", prompt: "چمدان و بار", accepted: ["luggage"],
        note: "بار سفر luggage است و غیرقابل‌شمارش می‌ماند.",
      },
      {
        k: "tr", prompt: "پروازم لغو شد و دو ساعت منتظر ماندم.",
        accepted: ["my flight was cancelled and i waited for two hours"],
        note: "لغو شدن پرواز مجهول است (was cancelled) و انتظار گذشتهٔ ساده: waited.",
      },
      {
        k: "listen", text: "My luggage did not arrive.",
        note: "به luggage /ˈlʌɡɪdʒ/ گوش کنید: /ɡ/ وسط و /dʒ/ پایان، بدون /ɡ/ دوم.",
      },
      {
        k: "prod", prompt: "بگو پروازت با تأخیر انجام شد.",
        mustUse: ["was", "delayed"], minWords: 5,
        accepted: ["my flight was delayed for three hours"],
        hint: "با My flight was delayed… شروع کن.",
        note: "مجهول گذشتهٔ ساده: was delayed + مدت با for.",
      },
    ],
    dialogue: [
      {
        scene: "باجهٔ شرکت هواپیمایی", partner: "کارمند شرکت هواپیمایی",
        prompt: "بگو پروازت لغو شده است.",
        question: "Good morning. How can I help you?",
        questionFa: "صبح بخیر. چه کمکی می‌توانم بکنم؟",
        accepted: [
          "My flight was cancelled this morning.",
          "They cancelled my flight this morning.",
        ],
        note: "this morning زمان مشخص است و گذشتهٔ ساده می‌خواهد.",
      },
      {
        scene: "باجهٔ شرکت هواپیمایی", partner: "کارمند شرکت هواپیمایی",
        prompt: "بگو بلیت را داری.",
        question: "Do you have your ticket with you?",
        questionFa: "بلیت‌تان همراهتان است؟",
        accepted: [
          "Yes, I have my ticket here.",
          "Yes, here is my ticket.",
        ],
        note: "Here is my ticket یعنی «بفرمایید، بلیت من».",
      },
      {
        scene: "باجهٔ شرکت هواپیمایی", partner: "کارمند شرکت هواپیمایی",
        prompt: "بپرس پرواز بعدی چه زمانی است.",
        question: "I am sorry about that. Do you want another flight today?",
        questionFa: "متأسفم. امروز پرواز دیگری می‌خواهید؟",
        accepted: [
          "When is the next flight?",
          "Could you tell me when the next flight is?",
        ],
        note: "در پرسش غیرمستقیم ترتیب جمله عوض می‌شود: when the next flight is.",
      },
      {
        scene: "باجهٔ شرکت هواپیمایی", partner: "کارمند شرکت هواپیمایی",
        prompt: "بگو دیر است و هتل می‌خواهی.",
        question: "The next flight is at six this evening. Is that okay?",
        questionFa: "پرواز بعدی ساعت شش عصر است. مناسب است؟",
        accepted: [
          "That is too late. Can you give me a hotel?",
          "It is too late for me. I need a hotel.",
        ],
        note: "too late یعنی «خیلی دیر»؛ very late شدت را می‌گوید، نه ناکافی بودن را.",
      },
      {
        scene: "باجهٔ شرکت هواپیمایی", partner: "کارمند شرکت هواپیمایی",
        prompt: "بگو قبول می‌کنی و بازپرداخت هم می‌خواهی.",
        question: "We can offer you a hotel and a meal. Would you like that?",
        questionFa: "می‌توانیم هتل و غذا بدهیم. می‌پسندید؟",
        accepted: [
          "Yes, please. And I would like a refund too.",
          "Yes, thank you. Can I also ask for a refund?",
        ],
        note: "would like شکل مؤدبانهٔ want است و با to می‌آید: I would like a refund.",
      },
      {
        scene: "باجهٔ شرکت هواپیمایی", partner: "کارمند شرکت هواپیمایی",
        prompt: "بگو شمارهٔ رزرو را داری.",
        question: "Certainly. Please give me your booking number.",
        questionFa: "حتماً. لطفاً شمارهٔ رزرو را بدهید.",
        accepted: [
          "I have my booking number here.",
          "Here is my booking number.",
        ],
        note: "booking number یعنی «شمارهٔ رزرو»؛ عدد را ننویس، فقط بگو که همراهش داری.",
      },
    ],
  },
  {
    lesson: {
      id: "b1.cultural-differences.lesson-01",
      order: 101,
      title: "Cultural Differences",
      titleFa: "فرهنگ‌ها و تفاوت‌های فرهنگی",
      topic: "Culture",
      tip: [
        "## قاعده",
        "برای گفتن رسم و عادت یک فرهنگ از حال ساده و usually استفاده می‌کنیم: People usually take off their shoes.",
        "برای نصیحت دربارهٔ آداب: You should wait for the host و You ought not to be late.",
        "برای مقایسهٔ دو فرهنگ: In my country we greet people with a handshake.",
        "برای پرسیدن با احتیاط: Is it polite to ask about the family?",
        "## جدول",
        "It is polite to say thank you. — تشکر کردن مؤدبانه است.",
        "A handshake is a common greeting. — دست دادن یک سلام رایج است.",
        "Every country has its own customs. — هر کشوری رسوم خودش را دارد.",
        "You might offend people if you arrive late. — اگر دیر برسی ممکن است کسی را برنجانی.",
        "## مثال‌ها",
        "Our host cooked a big meal. — میزبان ما غذای مفصلی پخت.",
        "I noticed one difference straight away. — یک تفاوت را همان لحظه متوجه شدم.",
        "The host explained the custom to us. — میزبان رسم را برای ما توضیح داد.",
        "## خطاهای رایج",
        "custom «رسم» است و costume «لباس نمایشی»؛ این دو را قاطی نکنید.",
        "polite صفت است: it is polite، نه it is politely.",
        "offend بدون to می‌آید: offend someone، نه offend to someone.",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-advice-ought",
    vocab: {
      id: "vocab.cultural-differences",
      title: "Cultural Differences",
      titleFa: "تفاوت‌های فرهنگی",
      prerequisites: ["vocab.travel-problems"],
    },
    words: [
      {
        slug: "custom", w: "custom", fa: "رسم و رسوم", ipa: "/ˈkʌstəm/", pos: "noun",
        def: "something people in a country or group usually do",
        ex: [
          ["It is a custom to take off your shoes.", "رسم است که کفش‌ها را دربیاوری."],
          ["Every country has its own customs.", "هر کشوری رسوم خودش را دارد."],
        ],
        col: ["a local custom", "follow a custom"],
        note: "custom «رسم» است؛ costume «لباس نمایشی» است و ربطی به آداب ندارد.",
      },
      {
        slug: "polite", w: "polite", fa: "مؤدب", ipa: "/pəˈlaɪt/", pos: "adjective",
        def: "showing good manners towards other people",
        ex: [
          ["It is polite to say thank you.", "تشکر کردن مؤدبانه است."],
          ["People here are polite to visitors.", "مردم اینجا با مهمان‌ها مؤدب‌اند."],
        ],
        col: ["polite to someone", "a polite answer"],
        note: "polite صفت است؛ قید آن politely می‌شود: she answered politely.",
      },
      {
        slug: "greeting", w: "greeting", fa: "سلام و خوشامد", ipa: "/ˈɡriːtɪŋ/", pos: "noun",
        def: "the words or actions you use when you meet someone",
        ex: [
          ["A handshake is a common greeting.", "دست دادن یک سلام رایج است."],
          ["Their greeting is a kiss on the cheek.", "سلام آن‌ها بوسه‌ای روی گونه است."],
        ],
        col: ["a friendly greeting", "a formal greeting"],
        note: "greeting از فعل greet می‌آید؛ greet someone یعنی «کسی را سلام کردن».",
      },
      {
        slug: "difference", w: "difference", fa: "تفاوت", ipa: "/ˈdɪfrəns/", pos: "noun",
        def: "the way two things are not the same",
        ex: [
          ["There is a big difference between the two cultures.", "بین این دو فرهنگ تفاوت بزرگی است."],
          ["I noticed one difference straight away.", "یک تفاوت را همان لحظه متوجه شدم."],
        ],
        col: ["a big difference", "notice a difference"],
        note: "difference با between می‌آید: the difference between A and B.",
      },
      {
        slug: "offend", w: "offend", fa: "رنجاندن", ipa: "/əˈfend/", pos: "verb",
        def: "to make someone feel hurt or upset",
        ex: [
          ["You might offend people if you are late.", "اگر دیر برسی ممکن است مردم را برنجانی."],
          ["He did not want to offend his host.", "او نمی‌خواست میزبانش را برنجاند."],
        ],
        col: ["offend someone", "easily offended"],
        note: "offend بدون to می‌آید: offend his host؛ صفت آن offended «رنجیده» است.",
      },
      {
        slug: "host", w: "host", fa: "میزبان", ipa: "/həʊst/", pos: "noun",
        def: "the person who invites you and takes care of you",
        ex: [
          ["Our host cooked a big meal.", "میزبان ما غذای مفصلی پخت."],
          ["The host explained the custom to us.", "میزبان رسم را برای ما توضیح داد."],
        ],
        col: ["our host", "thank the host"],
        note: "host میزبان است و guest مهمان؛ میزبان مؤنث hostess نام دارد.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "custom", gloss: "رسم و رسوم",
        options: ["زبان", "رسم و رسوم", "قانون", "جشن"],
        note: "custom «رسم» است؛ قانون law و جشن celebration است.",
      },
      {
        k: "mcq", word: "polite", gloss: "مؤدب",
        options: ["مؤدب", "صبور", "ساکت", "کنجکاو"],
        note: "polite «مؤدب» است؛ متضاد آن rude «بی‌ادب» است.",
      },
      {
        k: "mcq", word: "host", gloss: "میزبان",
        options: ["مهمان", "میزبان", "همسایه", "راهنما"],
        note: "host میزبان است و مهمان guest؛ همسایه neighbour است.",
      },
      {
        k: "blank", sentence: "It is ___ to take off your shoes before you go in.",
        accepted: ["polite", "customary"], skill: "VOCABULARY",
        note: "polite و customary هر دو صفت‌اند و با it is می‌آیند: it is polite / it is customary.",
      },
      {
        k: "blank", sentence: "Every country has its own ___.",
        accepted: ["customs", "traditions"], skill: "VOCABULARY",
        note: "بعد از its own اسم مفرد یا جمع می‌آید؛ customs و traditions هر دو جمع‌اند و درست‌اند.",
      },
      {
        k: "recall", prompt: "تفاوت", accepted: ["difference"],
        note: "تفاوت difference است و با between می‌آید.",
      },
      {
        k: "tr", prompt: "در کشور من دست دادن یک سلام معمولی است.",
        accepted: ["in my country a handshake is a normal greeting"],
        note: "handshake «دست دادن» است و greeting «سلام»؛ هر دو اسم‌اند.",
      },
      {
        k: "listen", text: "A handshake is a common greeting.",
        note: "به handshake /ˈhændʃeɪk/ گوش کنید: تکیه روی بخش اول و /ʃ/ در وسط.",
      },
      {
        k: "prod", prompt: "بگو رسم است که کفش‌ها را دربیاوری.",
        mustUse: ["custom", "take"], minWords: 8,
        accepted: ["it is a custom to take off your shoes"],
        hint: "با It is a custom to… شروع کن.",
        note: "الگوی it is a custom to + فعل ساده رسم را بیان می‌کند.",
      },
    ],
    dialogue: [
      {
        scene: "خانهٔ میزبان", partner: "میزبان",
        prompt: "بپرس کفش‌هایت را کجا بگذاری.",
        question: "Welcome. Please come in.",
        questionFa: "خوش آمدید. لطفاً داخل شوید.",
        accepted: [
          "Thank you. Where should I leave my shoes?",
          "Thanks. Should I take off my shoes here?",
        ],
        note: "should برای پرسیدن کار درست به‌کار می‌رود: where should I leave…?",
      },
      {
        scene: "خانهٔ میزبان", partner: "میزبان",
        prompt: "بگو در کشور تو هم همین رسم است.",
        question: "You can leave them by the door. In our culture we take them off.",
        questionFa: "می‌توانید کنار در بگذارید. در فرهنگ ما کفش‌ها را درمی‌آوریم.",
        accepted: [
          "We do the same in my country.",
          "It is the same in my country.",
        ],
        note: "the same یعنی «همان چیز» و با in my country می‌آید.",
      },
      {
        scene: "خانهٔ میزبان", partner: "میزبان",
        prompt: "با تشکر چای را قبول کن.",
        question: "Would you like some tea?",
        questionFa: "چای میل دارید؟",
        accepted: [
          "Yes, please. Thank you.",
          "Yes, I would love some tea.",
        ],
        note: "would love شکل گرم‌تر would like است.",
      },
      {
        scene: "خانهٔ میزبان", partner: "میزبان",
        prompt: "بگو ساعت شام اینجا دیرتر از خانه است.",
        question: "Is there anything you find different here?",
        questionFa: "چیزی اینجا برایتان متفاوت است؟",
        accepted: [
          "The dinner time is much later than at home.",
          "People eat much later here.",
        ],
        note: "much later یعنی «خیلی دیرتر»؛ much شدت مقایسه را زیاد می‌کند.",
      },
      {
        scene: "خانهٔ میزبان", partner: "میزبان",
        prompt: "بگو آداب محلی را یاد می‌گیری.",
        question: "You are right. We usually eat at nine.",
        questionFa: "درست است. ما معمولاً ساعت نه غذا می‌خوریم.",
        accepted: [
          "I am learning the local customs.",
          "I want to learn your customs.",
        ],
        note: "local customs یعنی «رسوم محلی»؛ custom جمع بسته می‌شود.",
      },
      {
        scene: "خانهٔ میزبان", partner: "میزبان",
        prompt: "بگو با کمال میل برای شام می‌مانی.",
        question: "That is kind of you. Would you like to stay for dinner?",
        questionFa: "لطف دارید. برای شام می‌مانید؟",
        accepted: [
          "I would love to stay, thank you.",
          "Yes, thank you, I would like that.",
        ],
        note: "would love to + فعل ساده یعنی «با کمال میل».",
      },
    ],
  },
];
