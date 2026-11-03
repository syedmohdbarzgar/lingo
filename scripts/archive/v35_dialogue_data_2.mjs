// v35 content, part 2 — the B1–C2 conversations grow to five or six turns.
// Each entry continues the lesson's existing exchange from its last turn; the
// scene and the partner are inherited by the runner.
export default [
  {
    lessonId: "b1.work.lesson-01",
    turns: [
      {
        prompt: "بخواه هر ماه بازخورد صادقانه بدهند.",
        question: "Perfect. Is there anything you need from me?",
        questionFa: "عالی. چیزی از من لازم داری؟",
        accepted: [
          "Yes, I would like honest feedback each month.",
          "One thing: a short note after each client meeting.",
        ],
        note: "honest feedback یعنی «بازخورد صادقانه»؛ a short note «یادداشت کوتاه» است.",
      },
      {
        prompt: "بگو ممنون، قدرش را می‌دانی.",
        question: "That's fair. I'll schedule it.",
        questionFa: "منصفانه است. برنامه‌اش را می‌گذارم.",
        accepted: [
          "Thank you. I appreciate that.",
          "That will help a lot. Thanks for listening.",
        ],
        note: "I appreciate that یعنی «قدرش را می‌دانم»؛ schedule یعنی «زمان‌بندی کردن».",
      },
    ],
  },
  {
    lessonId: "b1.news.lesson-01",
    turns: [
      {
        prompt: "بگو وزارت دارایی تا یک ماه برنامه را منتشر می‌کند.",
        question: "One last thing: is there a timetable for the tax cut?",
        questionFa: "یک سؤال آخر: برای کاهش مالیات جدول زمانی هست؟",
        accepted: [
          "The finance ministry will publish one within a month.",
          "Yes, but I would rather not promise a specific date.",
        ],
        note: "timetable یعنی «جدول زمانی»؛ within a month یعنی «تا یک ماه».",
      },
    ],
  },
  {
    lessonId: "b1.plans.lesson-01",
    turns: [
      {
        prompt: "بگو بله و بلیت قابل تغییر بگیرد.",
        question: "Good. Shall I book the flights now?",
        questionFa: "خوب. حالا بلیت‌ها را رزرو کنم؟",
        accepted: [
          "Yes, and choose the flexible ticket.",
          "Go ahead, but please take the refundable one.",
        ],
        note: "flexible ticket یعنی «بلیت قابل تغییر»؛ go ahead یعنی «بله، انجام بده».",
      },
      {
        prompt: "بگو هتل را امشب مرتب می‌کنی.",
        question: "Done. I'll send you the confirmation.",
        questionFa: "انجام شد. تأییدیه را برایت می‌فرستم.",
        accepted: [
          "Great. I'll sort the hotel tonight.",
          "Thanks. I'll book the hotel after dinner.",
        ],
        note: "sort the hotel یعنی «هتل را مرتب کردن»؛ tonight یعنی «امشب».",
      },
    ],
  },
  {
    lessonId: "b1.experiences.lesson-01",
    turns: [
      {
        prompt: "بگو همیشه یک نسخه از مدارک همراه داشته باشند.",
        question: "What advice would you give a new traveller?",
        questionFa: "به یک مسافر تازه چه توصیه‌ای می‌کنی؟",
        accepted: [
          "Always keep a copy of your documents.",
          "Give yourself an extra day and a backup plan.",
        ],
        note: "keep a copy یعنی «یک نسخه نگه داشتن»؛ backup plan یعنی «نقشهٔ جایگزین».",
      },
    ],
  },
  {
    lessonId: "b1.money.lesson-01",
    turns: [
      {
        prompt: "بگو روز حقوق انتقال خودکار می‌گذاری.",
        question: "Do that, and put the money into savings automatically.",
        questionFa: "این کار را بکن و پول را خودکار به پس‌انداز ببر.",
        accepted: [
          "I'll set up a transfer on payday.",
          "Good idea. I'll move it the day I get paid.",
        ],
        note: "set up a transfer یعنی «انتقال را تنظیم کردن»؛ payday یعنی «روز حقوق».",
      },
      {
        prompt: "بگو سه ماه بعد بودجه را بازبینی می‌کنی.",
        question: "Then review the budget in three months.",
        questionFa: "بعد سه ماه بودجه را بازبینی کن.",
        accepted: [
          "I'll put it in my calendar.",
          "I'll review it at the end of the quarter.",
        ],
        note: "put it in my calendar یعنی «در تقویمم می‌گذارم»؛ review یعنی «بازبینی کردن».",
      },
    ],
  },
  {
    lessonId: "b1.health-lifestyle.lesson-01",
    turns: [
      {
        prompt: "بگو گوشی را در اتاق دیگر می‌گذاری.",
        question: "Start with one evening and build from there.",
        questionFa: "با یک شب شروع کن و از همان‌جا ادامه بده.",
        accepted: [
          "I'll leave my phone in another room.",
          "One evening is doable. I'll try it tonight.",
        ],
        note: "build from there یعنی «از همان‌جا گسترش بده»؛ doable یعنی «شدنی».",
      },
    ],
  },
  {
    lessonId: "b2.technology.lesson-01",
    turns: [
      {
        prompt: "بگو با یک مهارت و یک ساعت در هفته شروع کنند.",
        question: "What would you say to a worker who feels too old to retrain?",
        questionFa: "به کارگری که فکر می‌کند برای یادگیری دوباره پیر است چه می‌گویی؟",
        accepted: [
          "Start small: one skill, one hour a week.",
          "Age matters less than a routine you can keep.",
        ],
        note: "retrain یعنی «دوباره آموزش دیدن»؛ a routine you can keep یعنی «روالی که بتوانی حفظ کنی».",
      },
      {
        prompt: "بگو ممنون از دعوت.",
        question: "That's a good place to end. Thank you.",
        questionFa: "جای خوبی برای پایان است. ممنون.",
        accepted: [
          "Thanks for having me.",
          "Thank you. It was a pleasure to talk.",
        ],
        note: "Thanks for having me یعنی «ممنون که دعوتم کردید»؛ a pleasure «افتخار» است.",
      },
    ],
  },
  {
    lessonId: "b2.environment.lesson-01",
    turns: [
      {
        prompt: "بگو بله و خودت با همسایه‌ها صحبت می‌کنی.",
        question: "That's measurable in three months. Shall we start with a pilot street?",
        questionFa: "در سه ماه قابل اندازه‌گیری است. با یک خیابان آزمایشی شروع کنیم؟",
        accepted: [
          "Yes, and I'll speak to the neighbours.",
          "Good idea. I can organise the first meeting.",
        ],
        note: "pilot street یعنی «خیابان آزمایشی»؛ organise یعنی «هماهنگ کردن».",
      },
    ],
  },
  {
    lessonId: "b2.interview.lesson-01",
    turns: [
      {
        prompt: "بپرس این سنجه چند وقت یک بار بازبینی می‌شود.",
        question: "That's a good question. Success is measured by client retention.",
        questionFa: "سؤال خوبی است. موفقیت با حفظ مشتری سنجیده می‌شود.",
        accepted: [
          "And how often is that reviewed?",
          "That makes sense. Who sets the targets?",
        ],
        note: "retention یعنی «حفظ و نگه‌داشتن»؛ reviewed یعنی «بازبینی می‌شود».",
      },
      {
        prompt: "بگو نه سؤال دیگری نداری و تشکر کن.",
        question: "Every quarter. Do you have another question?",
        questionFa: "هر سه ماه. سؤال دیگری داری؟",
        accepted: [
          "No, thank you. This has been very helpful.",
          "That is all from me. Thank you for your time.",
        ],
        note: "quarter یعنی «سه‌ماهه»؛ That is all from me یعنی «سؤالی ندارم».",
      },
    ],
  },
  {
    lessonId: "b2.media.lesson-01",
    turns: [
      {
        prompt: "بگو مخاطب را کسانی بگذاریم که قبلاً خریدند.",
        question: "Which audience should we use for the test?",
        questionFa: "برای آزمون از کدام مخاطب استفاده کنیم؟",
        accepted: [
          "Let's use the people who already bought.",
          "Start with existing customers, not new ones.",
        ],
        note: "audience یعنی «مخاطب»؛ existing customers یعنی «مشتریان فعلی».",
      },
    ],
  },
  {
    lessonId: "b2.problems.lesson-01",
    turns: [
      {
        prompt: "بخواه کد رهگیری هم بفرستند.",
        question: "Of course. I've marked your order as urgent.",
        questionFa: "البته. سفارش شما را فوری علامت زدم.",
        accepted: [
          "Thank you. Could you also send a tracking number?",
          "That helps. Will I get an email when it ships?",
        ],
        note: "tracking number یعنی «کد رهگیری»؛ urgent یعنی «فوری».",
      },
      {
        prompt: "بگو عالی است و تشکر کن.",
        question: "I'll send it as soon as it ships.",
        questionFa: "به‌محض ارسال می‌فرستم.",
        accepted: [
          "Perfect. Thanks for sorting it out.",
          "Great, I'll watch for it tomorrow.",
        ],
        note: "as soon as it ships یعنی «به‌محض ارسال»؛ sorting it out یعنی «حلش کردی».",
      },
    ],
  },
  {
    lessonId: "b2.research.lesson-01",
    turns: [
      {
        prompt: "بگو تغییرات را در حاشیه علامت می‌زنی.",
        question: "Send it to me with a short summary of changes.",
        questionFa: "با خلاصه‌ای از تغییرات برایم بفرست.",
        accepted: [
          "I'll highlight the changes in the margin.",
          "I'll add a summary page and mark every edit.",
        ],
        note: "in the margin یعنی «در حاشیه»؛ highlight یعنی «برجسته کردن».",
      },
    ],
  },
  {
    lessonId: "c1.academic.lesson-01",
    turns: [
      {
        prompt: "بگو بخش کوتاهی دربارهٔ نمونه‌گیری اضافه می‌کنی.",
        question: "Good. Add one paragraph on limitations.",
        questionFa: "خوب. یک بند دربارهٔ محدودیت‌ها اضافه کن.",
        accepted: [
          "I'll add a short section on sampling.",
          "I'll state the limits of the sample explicitly.",
        ],
        note: "limitations یعنی «محدودیت‌ها»؛ sampling یعنی «نمونه‌گیری».",
      },
      {
        prompt: "بگو سه‌شنبه بعدازظهر می‌فرستی.",
        question: "That should satisfy the reviewers.",
        questionFa: "این باید داوران را قانع کند.",
        accepted: [
          "Thank you. I'll send it Tuesday afternoon.",
          "I hope so. I'll make the change today.",
        ],
        note: "reviewers یعنی «داوران»؛ satisfy یعنی «قانع کردن».",
      },
    ],
  },
  {
    lessonId: "c1.persuasion.lesson-01",
    turns: [
      {
        prompt: "بگو هر دو سناریو را با فرض‌ها مدل می‌کنی.",
        question: "Bring a revised timeline too, with the holiday risk.",
        questionFa: "جدول زمانی بازنگری‌شده را هم با ریسک تعطیلات بیاور.",
        accepted: [
          "I'll model both scenarios and label the assumptions.",
          "I'll show the holiday risk against the launch date.",
        ],
        note: "assumptions یعنی «فرض‌ها»؛ model a scenario یعنی «سناریو را شبیه‌سازی کردن».",
      },
    ],
  },
  {
    lessonId: "c1.culture.lesson-01",
    turns: [
      {
        prompt: "بگو فیلم معنای کتاب را عوض می‌کند.",
        question: "Do you think the book is better?",
        questionFa: "فکر می‌کنی کتاب بهتر است؟",
        accepted: [
          "I think the film changes what the book means.",
          "Better is the wrong question; they do different things.",
        ],
        note: "changes what the book means یعنی «معنای کتاب را تغییر می‌دهد».",
      },
      {
        prompt: "بگو هفتهٔ بعد درباره‌اش حرف بزنید.",
        question: "We should discuss it over coffee.",
        questionFa: "باید درباره‌اش با قهوه صحبت کنیم.",
        accepted: [
          "Let's do that next week.",
          "Good idea. I'll bring my notes.",
        ],
        note: "Let's do that یعنی «بیا همین کار را بکنیم»؛ over coffee یعنی «همراه قهوه».",
      },
    ],
  },
  {
    lessonId: "c1.phrasal-idioms.lesson-01",
    turns: [
      {
        prompt: "بگو نگران نباشد و روی مهلت می‌ایستی.",
        question: "Thanks. And don't let the client steamroll us.",
        questionFa: "ممنون. نگذار مشتری ما را زیر بگیرد.",
        accepted: [
          "Don't worry, I'll hold the line on the deadline.",
          "I won't back down on the date.",
        ],
        note: "steamroll یعنی «با فشار زیر گرفتن»؛ hold the line یعنی «روی موضع ماندن».",
      },
    ],
  },
  {
    lessonId: "c1.inference.lesson-01",
    turns: [
      {
        prompt: "بگو متن را واقعیت‌محور نگه می‌داری و از واژهٔ تأخیر استفاده نمی‌کنی.",
        question: "Good. Copy the client, but keep the tone neutral.",
        questionFa: "خوب. مشتری را در جریان بگذار، اما لحن را خنثی نگه دار.",
        accepted: [
          "I'll keep it factual and avoid the word delay.",
          "Neutral tone, clear dates, no promises beyond the plan.",
        ],
        note: "factual یعنی «واقعیت‌محور»؛ neutral tone یعنی «لحن خنثی».",
      },
      {
        prompt: "بگو ساعت یازده در صندوق ایمیلش است.",
        question: "Exactly. Let's see it before noon.",
        questionFa: "دقیقاً. تا ظهر ببینیمش.",
        accepted: [
          "It'll be in your inbox by eleven.",
          "You'll have it well before noon.",
        ],
        note: "inbox یعنی «صندوق ایمیل»؛ well before یعنی «خیلی قبل از».",
      },
    ],
  },
  {
    lessonId: "c1.register.lesson-01",
    turns: [
      {
        prompt: "بگو پاراگراف اول هنوز زیادی رسمی است.",
        question: "Does that cover the whole email?",
        questionFa: "این کل ایمیل را پوشش می‌دهد؟",
        accepted: [
          "Almost; the first paragraph is still too formal.",
          "Most of it. The closing is too casual, though.",
        ],
        note: "Still too formal یعنی «هنوز زیادی رسمی»؛ closing یعنی «پایان نامه».",
      },
    ],
  },
  {
    lessonId: "c1.cohesion.lesson-01",
    turns: [
      {
        prompt: "بگو اول جمله‌های موضوعی را بازنویسی می‌کنی.",
        question: "Then check that each paragraph opens with a claim.",
        questionFa: "بعد بررسی کن هر بند با یک ادعا شروع شود.",
        accepted: [
          "I'll rewrite the topic sentences first.",
          "Good point. I'll make each opening sentence do work.",
        ],
        note: "topic sentence یعنی «جملهٔ موضوعی»؛ claim یعنی «ادعا».",
      },
      {
        prompt: "بگو تا ساعت هشت می‌فرستی.",
        question: "Good. Send it tonight, and we'll review in the morning.",
        questionFa: "خوب. امشب بفرست، صبح مرور می‌کنیم.",
        accepted: [
          "I'll send it by eight.",
          "You'll have it tonight, before nine.",
        ],
        note: "by eight یعنی «تا ساعت هشت»؛ مرور کردن review است.",
      },
    ],
  },
  {
    lessonId: "c2.nuance.lesson-01",
    turns: [
      {
        prompt: "بگو بخش تعاریف را هم یکدست می‌کنی.",
        question: "Then check the definitions section for the same problem.",
        questionFa: "بعد بخش تعاریف را برای همین مشکل بررسی کن.",
        accepted: [
          "I'll make fair value consistent throughout.",
          "Good catch. I'll align the definitions with the main clause.",
        ],
        note: "consistent throughout یعنی «در همه‌جا یکدست»؛ align یعنی «هم‌راستا کردن».",
      },
      {
        prompt: "بگو نسخهٔ نهایی را فردا می‌فرستی.",
        question: "Good. That should survive legal review.",
        questionFa: "خوب. این باید از بازبینی حقوقی بگذرد.",
        accepted: [
          "I'll send the final version tomorrow.",
          "I'll double-check the cross-references before it goes out.",
        ],
        note: "legal review یعنی «بازبینی حقوقی»؛ cross-references یعنی «ارجاع‌های متقابل».",
      },
    ],
  },
  {
    lessonId: "c2.rhetoric.lesson-01",
    turns: [
      {
        prompt: "بگو اول پذیرفتن هزینه، خوانندهٔ مخالف را خلع سلاح می‌کند.",
        question: "Now write the same sentence for a hostile reader.",
        questionFa: "حالا همان جمله را برای خوانندهٔ مخالف بنویس.",
        accepted: [
          "Acknowledging the cost first would disarm them.",
          "I'd concede the expense, then show what it buys.",
        ],
        note: "hostile reader یعنی «خوانندهٔ مخالف»؛ disarm یعنی «خلع سلاح کردن».",
      },
    ],
  },
  {
    lessonId: "c2.science.lesson-01",
    turns: [
      {
        prompt: "بگو استدلال می‌کنی که جامعهٔ آماری کامل و کوچک است.",
        question: "Will the reviewers accept a small sample?",
        questionFa: "داوران نمونهٔ کوچک را می‌پذیرند؟",
        accepted: [
          "I'll argue that the population is exhausted.",
          "I'll report the power calculation alongside it.",
        ],
        note: "exhausted population یعنی «جامعهٔ کاملاً پوشش‌داده‌شده»؛ power calculation «محاسبهٔ توان» است.",
      },
      {
        prompt: "بگو نامهٔ پاسخ به داوران را از قبل آماده می‌کنی.",
        question: "Convincing them may take a while.",
        questionFa: "قانع کردنشان ممکن است طول بکشد.",
        accepted: [
          "I'll prepare a response letter in advance.",
          "Then I'll send the data alongside the reply.",
        ],
        note: "response letter یعنی «نامهٔ پاسخ به داوران»؛ in advance یعنی «از پیش».",
      },
    ],
  },
  {
    lessonId: "c2.literature.lesson-01",
    turns: [
      {
        prompt: "بگو اول هشدار می‌دهی و آن بخش‌ها را بلند می‌خوانی.",
        question: "How would you handle the violent scenes?",
        questionFa: "صحنه‌های خشونت‌آمیز را چطور مدیریت می‌کنی؟",
        accepted: [
          "I'd warn the class first and read those parts aloud.",
          "I'd frame them with context and let the silence work.",
        ],
        note: "warn the class یعنی «به کلاس هشدار دادن»؛ aloud یعنی «بلند».",
      },
    ],
  },
  {
    lessonId: "c2.argumentation.lesson-01",
    turns: [
      {
        prompt: "بگو برآورد ماه‌های اول ضعیف بود.",
        question: "And what would you concede on cost?",
        questionFa: "دربارهٔ هزینه چه چیزی را می‌پذیری؟",
        accepted: [
          "The early months were badly estimated.",
          "Our cost forecast was optimistic, and I accept that.",
        ],
        note: "concede یعنی «پذیرفتنِ نقد»؛ optimistic forecast یعنی «پیش‌بینی خوش‌بینانه».",
      },
      {
        prompt: "بگو ممنون از مناظرهٔ جدی.",
        question: "That's a decent note to end on.",
        questionFa: "پایان قابل قبولی است.",
        accepted: [
          "Thanks for a sharp debate.",
          "Thank you. It was a fair exchange.",
        ],
        note: "sharp debate یعنی «مناظرهٔ جدی و تیز»؛ fair exchange یعنی «تبادل منصفانه».",
      },
    ],
  },
  {
    lessonId: "c2.collocation.lesson-01",
    turns: [
      {
        prompt: "بگو make و take را در جای درست به‌کار می‌بری.",
        question: "Also check the verbs with decision.",
        questionFa: "فعل‌های همراه decision را هم بررسی کن.",
        accepted: [
          "I'll use make and take where they belong.",
          "Good catch: we make a decision, and we take a step.",
        ],
        note: "collocation یعنی «هم‌نشینی واژه»؛ where they belong یعنی «در جای خودشان».",
      },
    ],
  },
  {
    lessonId: "c2.irony.lesson-01",
    turns: [
      {
        prompt: "بگو همان ترفند است با لحنی نرم‌تر.",
        question: "What about interesting when they mean wrong?",
        questionFa: "دربارهٔ «جالب» وقتی منظورش «غلط» است چه؟",
        accepted: [
          "That's the same trick with a softer voice.",
          "It's politeness doing the work of disagreement.",
        ],
        note: "softer voice یعنی «لحن نرم‌تر»؛ doing the work of یعنی «کارِ … را انجام دادن».",
      },
      {
        prompt: "بگو فقط اگر اسمش را «راهنمای بی‌ادبی مؤدبانه» بگذاریم.",
        question: "We should write a list one day.",
        questionFa: "یک روز باید فهرستی بنویسیم.",
        accepted: [
          "Only if we call it a guide to polite rudeness.",
          "Deal, as long as every entry has a straight version too.",
        ],
        note: "as long as یعنی «به شرطی که»؛ straight version یعنی «نسخهٔ صریح».",
      },
    ],
  },
];
