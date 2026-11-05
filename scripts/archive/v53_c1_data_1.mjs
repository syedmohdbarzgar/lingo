// v53 content, part 1 — C1 topics 26 and 27: argumentation & rebuttal and
// professional proposals. Read by `v53_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.argumentation.lesson-01",
      order: 164,
      title: "Argumentation and Rebuttal",
      titleFa: "استدلال و پاسخ به نقد",
      topic: "Debate",
      tip: [
        "## قاعده",
        "برای تأکید بر نقد، جمله را با یک قید منفی آغاز کن و فعل کمکی را پیش از فاعل بیاور: Not only does the study fail to show a link, but it also misreads the data.",
        "ساختار: قید منفی + فعل کمکی + فاعل + فعل اصلی — Rarely is a single study enough.",
        "## جدول",
        "Not only does the study fail to show a link, but it also misreads the data. — پژوهش نه‌تنها پیوندی نشان نمی‌دهد، بلکه داده را هم بد می‌خواند.",
        "Rarely is a single study enough to settle the question. — به‌ندرت یک پژوهش برای حل مسئله کافی است.",
        "Never have I seen a weaker warrant. — هرگز مجوز استدلالی ضعیف‌تر از این ندیده‌ام.",
        "## مثال‌ها",
        "An ad hominem attack answers the person, not the argument. — حملهٔ شخصی به شخص پاسخ می‌دهد، نه به استدلال.",
        "A steelman makes the other side's case better than they did. — تقویت استدلال حریف، ادعای او را بهتر از خودش بیان می‌کند.",
        "The burden of proof sits with the person making the claim. — بار اثبات بر دوش کسی است که ادعا می‌کند.",
        "## خطاهای رایج",
        "پس از Not only در آغاز جمله، ترتیب کمکی/فاعل جابه‌جا می‌شود: does the study fail ✓ / the study fails ✗.",
        "Rarely is a single study… ✓ / Rarely a single study is… ✗.",
        "rejoinder پاسخ متقابل است، نه تکرار ادعا؛ و rebuttal با ad hominem یکی نیست.",
      ].join("\n"),
    },
    grammarNode: "grammar.inversion-negative-adverbials",
    vocab: {
      id: "vocab.argumentation-rebuttal",
      title: "Argumentation and Rebuttal",
      titleFa: "استدلال و پاسخ به نقد",
      prerequisites: ["vocab.debating"],
    },
    words: [
      {
        slug: "ad-hominem", w: "ad hominem", fa: "حملهٔ شخصی", ipa: "/ˌæd ˈhɒmɪnem/", pos: "adjective",
        def: "attacking the person instead of their argument",
        ex: [
          ["An ad hominem attack answers the person, not the argument.", "حملهٔ شخصی به شخص پاسخ می‌دهد، نه به استدلال."],
          ["The reply was ad hominem from the first line.", "آن پاسخ از خط اول حملهٔ شخصی بود."],
        ],
        col: ["an ad hominem attack", "fall back on ad hominem"],
        note: "ad hominem صفت است و پیش از اسم می‌آید: an ad hominem argument.",
      },
      {
        slug: "steelman", w: "steelman", fa: "تقویت استدلال حریف", ipa: "/ˈstiːlmæn/", pos: "verb",
        def: "to state the other side's case in its strongest form",
        ex: [
          ["A steelman makes the other side's case better than they did.", "تقویت استدلال حریف، ادعای او را بهتر از خودش بیان می‌کند."],
          ["Steelman the objection before you answer it.", "پیش از پاسخ، اعتراض را در قوی‌ترین شکلش بیان کن."],
        ],
        col: ["steelman the argument", "a steelman version"],
        note: "steelman فعل و اسم است؛ مقابل آن straw man یعنی «استدلال حریف را ضعیف بازگو کردن».",
      },
      {
        slug: "rejoinder", w: "rejoinder", fa: "پاسخ متقابل", ipa: "/rɪˈdʒɔɪndə/", pos: "noun",
        def: "a reply that answers an objection",
        ex: [
          ["Her rejoinder was shorter than the objection.", "پاسخ متقابل او از اعتراض کوتاه‌تر بود."],
          ["Every rejoinder must answer the point that was made.", "هر پاسخ متقابلی باید به همان نکته‌ای که گفته شد پاسخ دهد."],
        ],
        col: ["a brief rejoinder", "offer a rejoinder"],
        note: "rejoinder قابل‌شمارش است؛ فعل آن rejoin به معنی «پاسخ دادن» در مناظره.",
      },
      {
        slug: "burden-of-proof", w: "burden of proof", fa: "بار اثبات", ipa: "/ˌbɜːdn əv ˈpruːf/", pos: "noun",
        def: "the duty to give evidence for a claim",
        ex: [
          ["The burden of proof sits with the person making the claim.", "بار اثبات بر دوش کسی است که ادعا می‌کند."],
          ["You cannot move the burden of proof onto the reader.", "نمی‌توانی بار اثبات را به گردن خواننده بیندازی."],
        ],
        col: ["carry the burden of proof", "shift the burden of proof"],
        note: "عبارت ثابت است: the burden of proof lies/sits with somebody.",
      },
      {
        slug: "qualify", w: "qualify", fa: "مشروط کردن", ipa: "/ˈkwɒlɪfaɪ/", pos: "verb",
        def: "to limit a claim so it stays true",
        ex: [
          ["You must qualify the claim before anyone attacks it.", "باید ادعا را پیش از هر حمله‌ای مشروط کنی."],
          ["Writers qualify claims so they survive a rebuttal.", "نویسندگان ادعاها را مشروط می‌کنند تا از پاسخ به نقد جان سالم به در ببرند."],
        ],
        col: ["qualify a statement", "a qualified claim"],
        note: "در این معنا qualify یعنی «محدود و مشروط کردن»، نه «واجد شرایط بودن».",
      },
      {
        slug: "warrant", w: "warrant", fa: "مجوز استدلال", ipa: "/ˈwɒrənt/", pos: "noun",
        def: "the link that lets evidence support a claim",
        ex: [
          ["The warrant connects the evidence to the conclusion.", "مجوز استدلال شاهد را به نتیجه پیوند می‌دهد."],
          ["A missing warrant is the weakest point of the paper.", "مجوز استدلال غایب، ضعیف‌ترین نقطهٔ مقاله است."],
        ],
        col: ["a hidden warrant", "challenge the warrant"],
        note: "در استدلال، warrant پیوند دلیل و ادعاست؛ در حقوق فارسی «قرار/حکم» هم ترجمه می‌شود.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "ad hominem", gloss: "حملهٔ شخصی",
        options: ["حملهٔ شخصی", "بار اثبات", "پاسخ متقابل", "تقویت استدلال حریف"],
        note: "ad hominem به شخص حمله می‌کند، نه به استدلال.",
      },
      {
        k: "mcq", word: "steelman", gloss: "تقویت استدلال حریف",
        options: ["تقویت استدلال حریف", "حملهٔ شخصی", "مجوز استدلال", "مشروط کردن ادعا"],
        note: "steelman یعنی ادعای حریف را در قوی‌ترین شکلش بازگو کردن.",
      },
      {
        k: "mcq", word: "burden of proof", gloss: "بار اثبات",
        options: ["بار اثبات", "پاسخ متقابل", "نتیجهٔ منطقی", "مجوز استدلال"],
        note: "burden of proof وظیفهٔ ارائهٔ شاهد برای ادعاست.",
      },
      {
        k: "blank", sentence: "Not only ___ the study fail to show a link, but it also misreads the data.",
        accepted: ["does", "did"], skill: "VOCABULARY",
        note: "پس از Not only در آغاز جمله، فعل کمکی پیش از فاعل می‌آید: does the study fail.",
      },
      {
        k: "blank", sentence: "Rarely ___ a single study enough to settle the question.",
        accepted: ["is", "was"], skill: "VOCABULARY",
        note: "وارونگی با قید منفی: Rarely is a single study enough.",
      },
      {
        k: "recall", prompt: "حملهٔ شخصی", accepted: ["ad hominem"],
        note: "حملهٔ شخصی ad hominem است و پیش از اسم می‌آید.",
      },
      {
        k: "tr", prompt: "این استدلال به‌جای پاسخ، به شخص حمله می‌کند.",
        accepted: [
          "the argument attacks the person instead of answering",
          "this argument attacks the person instead of answering",
          "instead of answering the argument attacks the person",
        ],
        note: "instead of + فعل ing: به‌جای انجام کاری.",
      },
      {
        k: "listen", text: "Rarely is a single study enough to settle the question.",
        note: "به Rarely is گوش کنید: وارونگی پس از قید منفی.",
      },
      {
        k: "prod", prompt: "بگو این پژوهش نه‌تنها پیوندی نشان نمی‌دهد، بلکه داده را هم بد می‌خواند.",
        mustUse: ["not only", "but"], minWords: 8,
        accepted: ["Not only does the study fail to show a link, but it also misreads the data."],
        hint: "با Not only does the study… شروع کن.",
        note: "ترتیب لنگرها: not only پیش از but.",
      },
    ],
    dialogue: [
      {
        scene: "دفاع از مقاله در سمینار، پاسخ به داور", partner: "استاد داور",
        prompt: "بگو این پژوهش نه‌تنها پیوندی نشان نمی‌دهد، بلکه داده را هم بد می‌خواند.",
        question: "What is your strongest objection?",
        questionFa: "قوی‌ترین اعتراض شما چیست؟",
        accepted: [
          "Not only does the study fail to show a link, but it also misreads the data.",
          "The study not only fails to show a link, but it also misreads the data.",
        ],
        note: "با Not only در آغاز جمله، فعل کمکی پیش از فاعل می‌آید.",
      },
      {
        scene: "دفاع از مقاله در سمینار، پاسخ به داور", partner: "استاد داور",
        prompt: "بگو بار اثبات بر دوش کسی است که ادعا می‌کند.",
        question: "Who has to prove the claim?",
        questionFa: "چه کسی باید ادعا را ثابت کند؟",
        accepted: [
          "The burden of proof sits with the person making the claim.",
          "It sits with the person making the claim.",
        ],
        note: "the burden of proof sits with: بار اثبات بر دوش کسی بودن.",
      },
      {
        scene: "دفاع از مقاله در سمینار، پاسخ به داور", partner: "استاد داور",
        prompt: "بگو مجوز استدلال شاهد را به نتیجه پیوند می‌دهد.",
        question: "Where is the weak link?",
        questionFa: "حلقهٔ ضعیف کجاست؟",
        accepted: [
          "The warrant connects the evidence to the conclusion.",
          "The warrant is missing, it connects nothing.",
        ],
        note: "connect something to something: چیزی را به چیزی پیوند دادن.",
      },
      {
        scene: "دفاع از مقاله در سمینار، پاسخ به داور", partner: "استاد داور",
        prompt: "بگو او ادعا را پیش از هر حمله‌ای مشروط کرد.",
        question: "Why did you soften the claim?",
        questionFa: "چرا ادعا را ملایم کردید؟",
        accepted: [
          "She qualified the claim before anyone attacked it.",
          "We qualified the claim before anyone attacked it.",
        ],
        note: "qualify a claim: ادعا را محدود و مشروط کردن.",
      },
      {
        scene: "دفاع از مقاله در سمینار، پاسخ به داور", partner: "استاد داور",
        prompt: "بگو پاسخ متقابل باید به همان نکته پاسخ دهد.",
        question: "How should a reply be built?",
        questionFa: "پاسخ را چطور بسازیم؟",
        accepted: [
          "Every rejoinder must answer the point that was made.",
          "A rejoinder must answer the point that was made.",
        ],
        note: "must answer: باید پاسخ دهد؛ rejoinder مفرد است.",
      },
      {
        scene: "دفاع از مقاله در سمینار، پاسخ به داور", partner: "استاد داور",
        prompt: "بگو به‌ندرت یک پژوهش برای حل مسئله کافی است.",
        question: "Is one study enough?",
        questionFa: "یک پژوهش کافی است؟",
        accepted: [
          "Rarely is a single study enough to settle the question.",
          "No, rarely is a single study enough.",
        ],
        note: "Rarely در آغاز جمله وارونگی می‌سازد: Rarely is….",
      },
    ],
  },
  {
    lesson: {
      id: "c1.proposals.lesson-01",
      order: 165,
      title: "Professional Proposals",
      titleFa: "نگارش پیشنهادهای حرفه‌ای",
      topic: "Business",
      tip: [
        "## قاعده",
        "پیشنهاد حرفه‌ای با تعهد شرطی نوشته می‌شود: If + حال ساده، will + مصدر.",
        "If the client approves the scope, we will start in May.",
        "برای تعهد قطعی‌تر می‌توانی shall یا can را جای will بگذاری — در جملهٔ شرط هیچ‌گاه will نمی‌آید.",
        "## جدول",
        "If the client approves the scope, we will start in May. — اگر مشتری محدوده را تأیید کند، در ماه مه شروع می‌کنیم.",
        "If the budget changes, we will revise the timeline. — اگر بودجه تغییر کند، زمان‌بندی را بازنگری می‌کنیم.",
        "If the pilot succeeds, we will roll it out in two regions. — اگر آزمون موفق باشد، آن را در دو منطقه اجرا می‌کنیم.",
        "## مثال‌ها",
        "The feasibility study takes three weeks. — مطالعهٔ امکان‌سنجی سه هفته طول می‌کشد.",
        "We need sign-off before we order anything. — پیش از هر سفارشی به تأیید نهایی نیاز داریم.",
        "The rationale sits on page one. — منطق پشتوانه در صفحهٔ اول است.",
        "## خطاهای رایج",
        "در جملهٔ if فعل حال ساده است، نه will: If the client approves… ✓ / If the client will approve… ✗.",
        "sign-off با خط تیره نوشته می‌شود و غیرقابل‌شمارش است: we need sign-off.",
        "annex پیوستِ سند است؛ appendix در گزارش علمی رایج‌تر است.",
      ].join("\n"),
    },
    grammarNode: "grammar.first-conditional",
    vocab: {
      id: "vocab.professional-proposals",
      title: "Professional Proposals",
      titleFa: "نگارش پیشنهادهای حرفه‌ای",
      prerequisites: ["vocab.professional-reports"],
    },
    words: [
      {
        slug: "feasibility", w: "feasibility", fa: "امکان‌سنجی", ipa: "/ˌfiːzəˈbɪləti/", pos: "noun",
        def: "how far a plan can actually be done",
        ex: [
          ["The feasibility study takes three weeks.", "مطالعهٔ امکان‌سنجی سه هفته طول می‌کشد."],
          ["Feasibility comes before the budget in a good proposal.", "در یک پیشنهاد خوب، امکان‌سنجی پیش از بودجه می‌آید."],
        ],
        col: ["a feasibility study", "technical feasibility"],
        note: "feasibility غیرقابل‌شمارش است؛ صفت آن feasible.",
      },
      {
        slug: "bid", w: "bid", fa: "پیشنهاد قیمت", ipa: "/bɪd/", pos: "noun",
        def: "an offer to do the work at a stated price",
        ex: [
          ["Our bid was lower but slower.", "پیشنهاد قیمت ما کم‌تر بود اما کندتر."],
          ["Every bid must state what is excluded.", "هر پیشنهاد قیمتی باید بگوید چه چیزی را در بر نمی‌گیرد."],
        ],
        col: ["submit a bid", "win the bid"],
        note: "bid قابل‌شمارش است؛ فعل آن هم bid/bid است (bid-bid-bid).",
      },
      {
        slug: "specification", w: "specification", fa: "مشخصات فنی", ipa: "/ˌspesɪfɪˈkeɪʃn/", pos: "noun",
        def: "the exact technical description of what is wanted",
        ex: [
          ["The specification runs to forty pages.", "مشخصات فنی به چهل صفحه می‌رسد."],
          ["Nobody read the specification before the meeting.", "هیچ‌کس پیش از جلسه مشخصات فنی را نخواند."],
        ],
        col: ["a technical specification", "meet the specification"],
        note: "specification قابل‌شمارش است؛ در گفتار کوتاه‌شدهٔ spec رایج است.",
      },
      {
        slug: "sign-off", w: "sign-off", fa: "تأیید نهایی", ipa: "/ˈsaɪn ɒf/", pos: "noun",
        def: "the final approval that lets work begin",
        ex: [
          ["We need sign-off before we order anything.", "پیش از هر سفارشی به تأیید نهایی نیاز داریم."],
          ["The sign-off came two weeks late.", "تأیید نهایی دو هفته دیر رسید."],
        ],
        col: ["get sign-off", "final sign-off"],
        note: "sign-off غیرقابل‌شمارش است و با خط تیره نوشته می‌شود؛ فعل آن sign off است.",
      },
      {
        slug: "rationale", w: "rationale", fa: "منطق پشتوانه", ipa: "/ˌræʃəˈnɑːl/", pos: "noun",
        def: "the reasoning behind a decision or plan",
        ex: [
          ["The rationale sits on page one.", "منطق پشتوانه در صفحهٔ اول است."],
          ["A proposal without a rationale reads like a price list.", "پیشنهاد بدون منطق پشتوانه مثل فهرست قیمت خوانده می‌شود."],
        ],
        col: ["explain the rationale", "the rationale behind"],
        note: "rationale قابل‌شمارش است و تلفظ آن با /ɑː/ پایان می‌یابد.",
      },
      {
        slug: "annex", w: "annex", fa: "پیوست", ipa: "/ˈæneks/", pos: "noun",
        def: "an extra section attached to a document",
        ex: [
          ["The cost table is in the annex.", "جدول هزینه‌ها در پیوست است."],
          ["Every annex needs its own page number.", "هر پیوستی به شمارهٔ صفحهٔ خودش نیاز دارد."],
        ],
        col: ["see the annex", "annex a document"],
        note: "annex قابل‌شمارش است؛ فعل آن به معنی «ضمیمه کردن» است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "feasibility", gloss: "امکان‌سنجی",
        options: ["امکان‌سنجی", "تأیید نهایی", "پیوست", "پیشنهاد قیمت"],
        note: "feasibility یعنی سنجش اینکه یک طرح واقعاً انجام‌شدنی است یا نه.",
      },
      {
        k: "mcq", word: "sign-off", gloss: "تأیید نهایی",
        options: ["تأیید نهایی", "مشخصات فنی", "منطق پشتوانه", "پیوست"],
        note: "sign-off تأیید نهایی برای شروع کار است.",
      },
      {
        k: "mcq", word: "rationale", gloss: "منطق پشتوانه",
        options: ["منطق پشتوانه", "پیشنهاد قیمت", "امکان‌سنجی", "مشخصات فنی"],
        note: "rationale استدلالی است که پشت یک تصمیم یا طرح می‌ایستد.",
      },
      {
        k: "blank", sentence: "If the client approves the scope, we ___ start in May.",
        accepted: ["will", "can"], skill: "VOCABULARY",
        note: "در شرطی نوع اول، جملهٔ نتیجه will/can + مصدر می‌گیرد.",
      },
      {
        k: "blank", sentence: "If the budget changes, we ___ revise the timeline.",
        accepted: ["will", "shall"], skill: "VOCABULARY",
        note: "will و shall هر دو تعهد آینده را می‌رسانند؛ will رایج‌تر است.",
      },
      {
        k: "recall", prompt: "امکان‌سنجی", accepted: ["feasibility"],
        note: "امکان‌سنجی feasibility است و صفت آن feasible.",
      },
      {
        k: "tr", prompt: "اگر مشتری محدوده را تأیید کند، در ماه مه شروع می‌کنیم.",
        accepted: [
          "if the client approves the scope we will start in may",
          "if the client approves the scope we can start in may",
        ],
        note: "شرطی نوع اول: if + حال ساده، will + مصدر.",
      },
      {
        k: "listen", text: "If the budget changes, we will revise the timeline.",
        note: "به will revise گوش کنید: تعهد در جملهٔ نتیجه.",
      },
      {
        k: "prod", prompt: "بگو اگر بودجه تغییر کند، زمان‌بندی را بازنگری می‌کنیم.",
        mustUse: ["if", "will"], minWords: 7,
        accepted: ["If the budget changes, we will revise the timeline."],
        hint: "با If the budget changes… شروع کن.",
        note: "ترتیب لنگرها: if پیش از will.",
      },
    ],
    dialogue: [
      {
        scene: "ارائهٔ پیشنهاد به مشتری", partner: "مدیر مشتری",
        prompt: "بگو اگر مشتری محدوده را تأیید کند، در ماه مه شروع می‌کنیم.",
        question: "When can you begin?",
        questionFa: "چه زمانی می‌توانید شروع کنید؟",
        accepted: [
          "If the client approves the scope, we will start in May.",
          "We will start in May if the client approves the scope.",
        ],
        note: "if + حال ساده، will + مصدر: تعهد شرطی.",
      },
      {
        scene: "ارائهٔ پیشنهاد به مشتری", partner: "مدیر مشتری",
        prompt: "بگو مطالعهٔ امکان‌سنجی سه هفته طول می‌کشد.",
        question: "How long is the first stage?",
        questionFa: "مرحلهٔ اول چقدر طول می‌کشد؟",
        accepted: [
          "The feasibility study takes three weeks.",
          "It takes three weeks, the feasibility study.",
        ],
        note: "take + مدت: طول کشیدن.",
      },
      {
        scene: "ارائهٔ پیشنهاد به مشتری", partner: "مدیر مشتری",
        prompt: "بگو پیش از هر سفارشی به تأیید نهایی نیاز داریم.",
        question: "Can you order the hardware now?",
        questionFa: "می‌توانید همین حالا سخت‌افزار سفارش دهید؟",
        accepted: [
          "We need sign-off before we order anything.",
          "No, we need sign-off first.",
        ],
        note: "need + اسم: نیاز داشتن به چیزی؛ sign-off بدون a می‌آید.",
      },
      {
        scene: "ارائهٔ پیشنهاد به مشتری", partner: "مدیر مشتری",
        prompt: "بگو منطق پشتوانه در صفحهٔ اول است.",
        question: "Why did you choose this approach?",
        questionFa: "چرا این رویکرد را انتخاب کردید؟",
        accepted: [
          "The rationale sits on page one.",
          "The rationale is on page one.",
        ],
        note: "sit on page one: در صفحهٔ اول قرار داشتن.",
      },
      {
        scene: "ارائهٔ پیشنهاد به مشتری", partner: "مدیر مشتری",
        prompt: "بگو جدول هزینه‌ها در پیوست است.",
        question: "Where are the numbers?",
        questionFa: "اعداد کجاست؟",
        accepted: [
          "The cost table is in the annex.",
          "The table is in the annex.",
        ],
        note: "in the annex: در پیوست.",
      },
      {
        scene: "ارائهٔ پیشنهاد به مشتری", partner: "مدیر مشتری",
        prompt: "بگو پیشنهاد قیمت ما کم‌تر بود اما کندتر.",
        question: "Why did you not win the last bid?",
        questionFa: "چرا در پیشنهاد قبلی برنده نشدید؟",
        accepted: [
          "Our bid was lower but slower.",
          "It was lower but slower, our bid.",
        ],
        note: "lower but slower: تفضیلی و مقایسهٔ دو ویژگی.",
      },
    ],
  },
];
