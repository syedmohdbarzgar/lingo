// v46 content, part 1 — B2 topics 21 and 22: debating and discussion, reviewing
// films and books. Read by `v46_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.debate.lesson-01",
      order: 136,
      title: "Debating and Discussion",
      titleFa: "بحث و مناظره",
      topic: "Communication",
      tip: [
        "## قاعده",
        "برای پذیرفتن فرض طرف مقابل و بعد رد کردنش: Even if + گذشتهٔ ساده، would — Even if that were true, the plan would still fail.",
        "شرطی نوع دوم فرض غیرواقعی را می‌سازد: If we cut the budget, the service would suffer.",
        "رد مؤدبانه با عبارت‌های ثابت: I see your point, but … / That may be so, however …",
        "برای برگشتن به موضوع اصلی: To come back to the main question, …",
        "## جدول",
        "Even if that were true, the cost would still be too high. — حتی اگر آن درست باشد، هزینه باز هم زیادی است.",
        "If we cut the budget, the service would suffer. — اگر بودجه را کم کنیم، خدمات آسیب می‌بیند.",
        "That may be so, however the data says otherwise. — ممکن است چنین باشد، اما داده چیز دیگری می‌گوید.",
        "## مثال‌ها",
        "Her argument rests on one weak assumption. — استدلال او بر یک فرض ضعیف تکیه دارد.",
        "He conceded the point and moved on. — او نکته را پذیرفت و ادامه داد.",
        "The debate ended without a clear winner. — مناظره بدون برندهٔ روشن تمام شد.",
        "## خطاهای رایج",
        "پس از even if در فرض غیرواقعی گذشتهٔ ساده می‌آید: even if it were true ✓.",
        "however با کاما و در آغاز جملهٔ تازه می‌آید؛ در میانهٔ جمله but طبیعی‌تر است.",
        "argument در این درس «استدلال» است، نه «دعوا».",
      ].join("\n"),
    },
    grammarNode: "grammar.tentative-conditionals",
    vocab: {
      id: "vocab.debating",
      title: "Debating and Discussion",
      titleFa: "مناظره",
      prerequisites: ["vocab.social-issues"],
    },
    words: [
      {
        slug: "argument", w: "argument", fa: "استدلال", ipa: "/ˈɑːɡjumənt/", pos: "noun",
        def: "a set of reasons that supports one side",
        ex: [
          ["Her argument rests on one weak assumption.", "استدلال او بر یک فرض ضعیف تکیه دارد."],
          ["His argument was clear but incomplete.", "استدلال او روشن بود اما ناقص."],
        ],
        col: ["a strong argument", "support an argument"],
        note: "argument در این درس «استدلال» است؛ در گفتار روزمره «دعوا» هم معنا می‌دهد.",
      },
      {
        slug: "rebuttal", w: "rebuttal", fa: "ردیه؛ پاسخ نقضی", ipa: "/rɪˈbʌtl/", pos: "noun",
        def: "a reply that shows why the other side is wrong",
        ex: [
          ["Her rebuttal lasted ninety seconds.", "ردیهٔ او نود ثانیه طول کشید."],
          ["A good rebuttal answers the strongest point.", "یک ردیّهٔ خوب به قوی‌ترین نکته پاسخ می‌دهد."],
        ],
        col: ["a short rebuttal", "give a rebuttal"],
        note: "rebuttal قابل‌شمارش است؛ فعل آن rebut و صفت آن rebuttable است.",
      },
      {
        slug: "stance", w: "stance", fa: "موضع", ipa: "/stɑːns/", pos: "noun",
        def: "the position you take on an issue",
        ex: [
          ["The team never changed its stance.", "تیم هرگز موضعش را عوض نکرد."],
          ["She explained her stance in one sentence.", "او موضعش را در یک جمله توضیح داد."],
        ],
        col: ["take a stance", "a clear stance"],
        note: "stance با on می‌آید: a stance on the issue؛ take a stance یعنی موضع گرفتن.",
      },
      {
        slug: "moderator", w: "moderator", fa: "مجری مناظره", ipa: "/ˈmɒdəreɪtə/", pos: "noun",
        def: "the person who keeps a debate fair and on time",
        ex: [
          ["The moderator stopped him after a minute.", "مجری بعد از یک دقیقه او را متوقف کرد."],
          ["A good moderator keeps both sides on time.", "مجری خوب هر دو طرف را در وقت نگه می‌دارد."],
        ],
        col: ["the debate moderator", "ask the moderator"],
        note: "moderator از فعل moderate می‌آید: to moderate a debate یعنی مناظره را اداره کردن.",
      },
      {
        slug: "motion", w: "motion", fa: "موضوع رأی‌گیری؛ طرح", ipa: "/ˈməʊʃn/", pos: "noun",
        def: "the formal question a debate is about",
        ex: [
          ["The motion was about free transport.", "موضوع رأی‌گیری حمل‌ونقل رایگان بود."],
          ["They argued for and against the motion.", "آن‌ها له و علیه طرح بحث کردند."],
        ],
        col: ["propose a motion", "speak for the motion"],
        note: "motion در مناظره «موضوع رأی‌گیری» است؛ در فیزیک «حرکت» معنا می‌دهد.",
      },
      {
        slug: "heckle", w: "heckle", fa: "پارازیت کردن؛ هو کردن", ipa: "/ˈhekl/", pos: "verb",
        def: "to interrupt a speaker with rude comments",
        ex: [
          ["The audience began to heckle the speaker.", "حضار شروع کردند به پارازیت دادن به سخنران."],
          ["Nobody should heckle a student debate.", "هیچ‌کس نباید در مناظرهٔ دانشجویی پارازیت بیندازد."],
        ],
        col: ["heckle a speaker", "be heckled"],
        note: "heckle با مفعول مستقیم می‌آید؛ اسم آن heckler یعنی پارازیت‌انداز.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "argument", gloss: "استدلال",
        options: ["استدلال", "دعوا", "موضع", "ردیه"],
        note: "argument مجموعهٔ دلایلی است که یک طرف را تقویت می‌کند.",
      },
      {
        k: "mcq", word: "rebuttal", gloss: "ردیه؛ پاسخ نقضی",
        options: ["ردیه؛ پاسخ نقضی", "موضوع رأی‌گیری", "سؤال تماشاگر", "خلاصهٔ پایانی"],
        note: "rebuttal پاسخ کوتاهی است که نشان می‌دهد طرف مقابل اشتباه می‌کند.",
      },
      {
        k: "mcq", word: "stance", gloss: "موضع",
        options: ["موضع", "مجری", "رأی", "قانون"],
        note: "stance موضعی است که فرد در یک موضوع می‌گیرد.",
      },
      {
        k: "blank", sentence: "Even if that ___ true, the cost would still be too high.",
        accepted: ["were", "was"], skill: "VOCABULARY",
        note: "even if با گذشتهٔ ساده/فرضی می‌آید؛ were رسمی‌تر است.",
      },
      {
        k: "blank", sentence: "If we cut the budget, the service ___ suffer.",
        accepted: ["would", "will"], skill: "VOCABULARY",
        note: "would نتیجهٔ فرض غیرواقعی است؛ will نتیجهٔ واقعی.",
      },
      {
        k: "recall", prompt: "موضع", accepted: ["stance"],
        note: "موضع در بحث stance است و با take می‌آید.",
      },
      {
        k: "tr", prompt: "حتی اگر آن درست باشد، برنامه باز هم شکست می‌خورد.",
        accepted: [
          "even if that were true, the plan would still fail",
          "even if that is true, the plan would still fail",
        ],
        note: "even if فرض را می‌پذیرد و would نتیجهٔ غیرواقعی را می‌رساند.",
      },
      {
        k: "listen", text: "Her argument rests on one weak assumption.",
        note: "به rests on گوش کنید: استدلال بر یک چیز تکیه کردن.",
      },
      {
        k: "prod", prompt: "فرض طرف مقابل را بپذیر و بعد ردش کن.",
        mustUse: ["if", "would"], minWords: 10,
        accepted: ["Even if that were true, the plan would still cost too much."],
        hint: "با Even if… شروع کن و با would ادامه بده.",
        note: "ترتیب لنگرها: if پیش از would می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "مسابقهٔ مناظرهٔ دانشگاه، تیم مقابل روبه‌رو", partner: "مناظره‌گر مقابل",
        prompt: "بگو حتی اگر آن درست باشد هزینه باز هم زیاد است.",
        question: "Why do you oppose the motion?",
        questionFa: "چرا با این طرح مخالفی؟",
        accepted: [
          "Even if that were true, the cost would still be too high.",
          "Because even if it were cheaper, the cost would still be too high.",
        ],
        note: "even if + گذشته و would نتیجه: فرض حریف را می‌پذیریم و باز رد می‌کنیم.",
      },
      {
        scene: "مسابقهٔ مناظرهٔ دانشگاه، تیم مقابل روبه‌رو", partner: "مناظره‌گر مقابل",
        prompt: "بگو استدلال او بر یک فرض ضعیف تکیه دارد.",
        question: "What is wrong with their case?",
        questionFa: "اشکال استدلال آن‌ها چیست؟",
        accepted: [
          "Her argument rests on one weak assumption.",
          "Their argument rests on a weak assumption.",
        ],
        note: "rest on یعنی تکیه داشتن؛ argument اینجا استدلال است.",
      },
      {
        scene: "مسابقهٔ مناظرهٔ دانشگاه، تیم مقابل روبه‌رو", partner: "مناظره‌گر مقابل",
        prompt: "بگو موضوع رأی‌گیری حمل‌ونقل رایگان بود.",
        question: "What exactly are we debating?",
        questionFa: "دقیقاً دربارهٔ چه بحث می‌کنیم؟",
        accepted: [
          "The motion was about free transport.",
          "The motion is free transport for students.",
        ],
        note: "the motion مفرد است و با about می‌آید.",
      },
      {
        scene: "مسابقهٔ مناظرهٔ دانشگاه، تیم مقابل روبه‌رو", partner: "مناظره‌گر مقابل",
        prompt: "بگو مجری بعد از یک دقیقه او را متوقف کرد.",
        question: "Why did they stop me?",
        questionFa: "چرا متوقفت کردند؟",
        accepted: [
          "The moderator stopped him after a minute.",
          "The moderator gave you one minute and stopped you.",
        ],
        note: "moderator فاعل جمله است: the moderator stopped…",
      },
      {
        scene: "مسابقهٔ مناظرهٔ دانشگاه، تیم مقابل روبه‌رو", partner: "مناظره‌گر مقابل",
        prompt: "بگو حضار شروع کردند به پارازیت دادن.",
        question: "Was the audience fair?",
        questionFa: "حضار منصف بودند؟",
        accepted: [
          "No, the audience began to heckle the speaker.",
          "Some of the audience started to heckle.",
        ],
        note: "begin to heckle یعنی شروع کردن به پارازیت؛ heckle فعل است.",
      },
      {
        scene: "مسابقهٔ مناظرهٔ دانشگاه، تیم مقابل روبه‌رو", partner: "مناظره‌گر مقابل",
        prompt: "بگو مناظره بدون برندهٔ روشن تمام شد.",
        question: "Who won?",
        questionFa: "چه کسی برد؟",
        accepted: [
          "The debate ended without a clear winner.",
          "Nobody — it ended without a clear winner.",
        ],
        note: "without a clear winner یعنی بدون برندهٔ روشن.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.review.lesson-01",
      order: 137,
      title: "Reviewing Films and Books",
      titleFa: "نقد فیلم و کتاب",
      topic: "Culture",
      tip: [
        "## قاعده",
        "بند موصولی اثر را توصیف می‌کند: a film that keeps you guessing.",
        "برای شخص who و برای چیز that/which: the actor who plays the father.",
        "بند غیرتعریف‌کننده توضیح اضافه است و بین دو کاما می‌آید: The ending, which nobody expected, divides viewers.",
        "در نقد، ترکیب صفت + اسم نظر را دقیق می‌کند: a slow first act / a brilliant cast.",
        "## جدول",
        "It is a film that keeps you guessing. — فیلمی است که تا آخر حدس می‌زنی.",
        "The actor who plays the father is excellent. — بازیگری که نقش پدر را دارد عالی است.",
        "The ending, which nobody expected, divides viewers. — پایان، که کسی انتظارش را نداشت، بیننده‌ها را دو دسته می‌کند.",
        "## مثال‌ها",
        "The cast carries the whole film. — بازیگران کل فیلم را روی دوش می‌کشند.",
        "The screenplay is sharper than the book. — فیلم‌نامه از کتاب تیزتر است.",
        "One twist near the end changes everything. — یک پیچش نزدیک پایان همه چیز را عوض می‌کند.",
        "## خطاهای رایج",
        "پس از کاما that نمی‌آید؛ آن‌جا which لازم است.",
        "cast اسم جمع‌نماست و با فعل مفرد و جمع هر دو می‌آید؛ در این درس مفرد.",
        "critic نقدنویس است، نه «منتقد بودن» به‌طور کلی.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.film-reviews",
      title: "Reviewing Films and Books",
      titleFa: "نقد فیلم و کتاب",
      prerequisites: ["vocab.storytelling"],
    },
    words: [
      {
        slug: "cast", w: "cast", fa: "بازیگران (گروه)", ipa: "/kɑːst/", pos: "noun",
        def: "all the actors in a film or play",
        ex: [
          ["The cast carries the whole film.", "بازیگران کل فیلم را روی دوش می‌کشند."],
          ["The cast was chosen in two weeks.", "گروه بازیگران در دو هفته انتخاب شد."],
        ],
        col: ["a strong cast", "the whole cast"],
        note: "cast در این معنا اسم جمع است و شکل مفرد و جمعش یکی است.",
      },
      {
        slug: "screenplay", w: "screenplay", fa: "فیلم‌نامه", ipa: "/ˈskriːnpleɪ/", pos: "noun",
        def: "the written text a film is made from",
        ex: [
          ["The screenplay is sharper than the book.", "فیلم‌نامه از کتاب تیزتر است."],
          ["She wrote the screenplay in a year.", "او فیلم‌نامه را در یک سال نوشت."],
        ],
        col: ["write a screenplay", "a tight screenplay"],
        note: "screenplay قابل‌شمارش است؛ script نسخهٔ اجرایی همان متن است.",
      },
      {
        slug: "twist", w: "twist", fa: "پیچش داستانی", ipa: "/twɪst/", pos: "noun",
        def: "an unexpected change in a story",
        ex: [
          ["One twist near the end changes everything.", "یک پیچش نزدیک پایان همه چیز را عوض می‌کند."],
          ["The twist works because it was prepared.", "آن پیچش کار می‌کند چون از قبل آماده شده بود."],
        ],
        col: ["a plot twist", "an unexpected twist"],
        note: "twist در نقد «پیچش» است؛ plot twist رایج‌ترین ترکیب آن است.",
      },
      {
        slug: "sequel", w: "sequel", fa: "دنباله (فیلم یا کتاب)", ipa: "/ˈsiːkwəl/", pos: "noun",
        def: "a film or book that continues an earlier story",
        ex: [
          ["The sequel is better than the first film.", "دنباله از فیلم اول بهتر است."],
          ["A sequel was announced last month.", "ماه پیش دنبالهٔ آن اعلام شد."],
        ],
        col: ["a sequel to the film", "announce a sequel"],
        note: "sequel با to می‌آید: a sequel to the film.",
      },
      {
        slug: "critic", w: "critic", fa: "منتقد؛ نقدنویس", ipa: "/ˈkrɪtɪk/", pos: "noun",
        def: "a person who writes judgements about art",
        ex: [
          ["One critic called it the year's best film.", "یک منتقد آن را بهترین فیلم سال خواند."],
          ["Critics were kinder than the audience.", "منتقدان از تماشاگران مهربان‌تر بودند."],
        ],
        col: ["a film critic", "critics praised it"],
        note: "critic منتقد است و criticism نقد؛ criticize فعل آن است.",
      },
      {
        slug: "rating", w: "rating", fa: "امتیاز", ipa: "/ˈreɪtɪŋ/", pos: "noun",
        def: "a score that shows how good something is",
        ex: [
          ["The film has a low rating online.", "این فیلم امتیاز پایینی در اینترنت دارد."],
          ["Ratings rarely match a good review.", "امتیازها به‌ندرت با یک نقد خوب یکی می‌شوند."],
        ],
        col: ["a high rating", "audience ratings"],
        note: "rating قابل‌شمارش است؛ rate فعل و rated صفت آن است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "cast", gloss: "بازیگران (گروه)",
        options: ["بازیگران (گروه)", "فیلم‌نامه", "صحنه", "امتیاز"],
        note: "cast همهٔ بازیگران یک فیلم یا نمایش است.",
      },
      {
        k: "mcq", word: "screenplay", gloss: "فیلم‌نامه",
        options: ["فیلم‌نامه", "زیرنویس", "کارگردان", "دنباله"],
        note: "screenplay متن نوشته‌شده‌ای است که فیلم از آن ساخته می‌شود.",
      },
      {
        k: "mcq", word: "twist", gloss: "پیچش داستانی",
        options: ["پیچش داستانی", "صحنهٔ پایانی", "دنباله", "نقد"],
        note: "twist تغییر غیرمنتظره‌ای در داستان است.",
      },
      {
        k: "blank", sentence: "It is a film ___ keeps you guessing.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "در بند تعریف‌کننده هم that و هم which درست است.",
      },
      {
        k: "blank", sentence: "The actor ___ plays the father is excellent.",
        accepted: ["who", "that"], skill: "VOCABULARY",
        note: "برای شخص who، و در بند تعریف‌کننده that.",
      },
      {
        k: "recall", prompt: "فیلم‌نامه", accepted: ["screenplay"],
        note: "فیلم‌نامه screenplay است و قابل‌شمارش.",
      },
      {
        k: "tr", prompt: "فیلمی است که تا آخر حدس می‌زنی.",
        accepted: [
          "it is a film that keeps you guessing",
          "it is a film which keeps you guessing",
        ],
        note: "بند that keeps you guessing صفت film است.",
      },
      {
        k: "listen", text: "The ending, which nobody expected, divides viewers.",
        note: "به کاماها گوش کنید: بند غیرتعریف‌کننده بین دو کاما می‌آید.",
      },
      {
        k: "prod", prompt: "یک فیلم را با بند موصولی توصیف کن.",
        mustUse: ["who", "that"], minWords: 9,
        accepted: ["The actor who plays the father is the best thing in a film that is too long."],
        hint: "با The actor who… شروع کن و با … a film that… ادامه بده.",
        note: "ترتیب لنگرها: who در بند اول و that در بند دوم می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "کافه، بحث دربارهٔ فیلمی که دیروز دیده‌اید", partner: "دوست",
        prompt: "بگو فیلمی است که تا آخر حدس می‌زنی.",
        question: "What did you think of it?",
        questionFa: "چه نظری درباره‌اش داری؟",
        accepted: [
          "It is a film that keeps you guessing.",
          "I liked it — it is a film that keeps you guessing.",
        ],
        note: "بند that keeps you guessing صفت film است و کاما نمی‌گیرد.",
      },
      {
        scene: "کافه، بحث دربارهٔ فیلمی که دیروز دیده‌اید", partner: "دوست",
        prompt: "بگو بازیگران کل فیلم را روی دوش می‌کشند.",
        question: "Was the acting good?",
        questionFa: "بازی‌ها خوب بود؟",
        accepted: [
          "The cast carries the whole film.",
          "Yes, the cast carries the whole film.",
        ],
        note: "the cast اسم جمع است و در این جمله فعل مفرد می‌گیرد.",
      },
      {
        scene: "کافه، بحث دربارهٔ فیلمی که دیروز دیده‌اید", partner: "دوست",
        prompt: "بگو پایان هیچ‌کس انتظارش را نداشت.",
        question: "Did you see the ending coming?",
        questionFa: "پایانش را حدس زده بودی؟",
        accepted: [
          "The ending, which nobody expected, changes the story.",
          "No, the ending was a complete surprise.",
        ],
        note: "بند which nobody expected توضیح اضافه است و بین دو کاما می‌آید.",
      },
      {
        scene: "کافه، بحث دربارهٔ فیلمی که دیروز دیده‌اید", partner: "دوست",
        prompt: "بگو فیلم‌نامه از کتاب تیزتر است.",
        question: "Is the book better?",
        questionFa: "کتاب بهتر است؟",
        accepted: [
          "No, the screenplay is sharper than the book.",
          "The screenplay is sharper, in my opinion.",
        ],
        note: "sharper than ساخت مقایسهٔ صفت کوتاه است.",
      },
      {
        scene: "کافه، بحث دربارهٔ فیلمی که دیروز دیده‌اید", partner: "دوست",
        prompt: "بگو یک منتقد آن را بهترین فیلم سال خواند.",
        question: "Did people like it?",
        questionFa: "مردم دوستش داشتند؟",
        accepted: [
          "One critic called it the year's best film.",
          "A critic called it the best film of the year.",
        ],
        note: "call something + صفت یعنی آن را چیزی خواندن.",
      },
      {
        scene: "کافه، بحث دربارهٔ فیلمی که دیروز دیده‌اید", partner: "دوست",
        prompt: "بگو دنباله از فیلم اول بهتر است.",
        question: "Should I watch the sequel?",
        questionFa: "دنباله را ببینم؟",
        accepted: [
          "The sequel is better than the first film.",
          "Watch it — the sequel is better than the first film.",
        ],
        note: "sequel با to هم می‌آید: a sequel to the first film.",
      },
    ],
  },
];
