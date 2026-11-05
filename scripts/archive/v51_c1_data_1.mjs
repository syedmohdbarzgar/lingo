// v51 content, part 1 — C1 topics 14 and 15: research reports and research
// methodology. Read by `v51_c1_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c1.research-reports.lesson-01",
      order: 156,
      title: "Research Reports",
      titleFa: "گزارش‌های پژوهشی",
      topic: "Study",
      tip: [
        "## قاعده",
        "گزارش پژوهشی مجهول‌محور است، چون خودِ کار مهم است نه انجام‌دهنده: The survey was conducted in June.",
        "حال سادهٔ مجهول برای روش: The sample is described in section two.",
        "گذشتهٔ سادهٔ مجهول برای کاری که تمام شده: The data were collected in June.",
        "## جدول",
        "The survey was conducted in June. — پیمایش در ژوئن انجام شد.",
        "The findings are summarised on page two. — یافته‌ها در صفحهٔ دوم خلاصه شده‌اند.",
        "The data were collected in two waves. — داده‌ها در دو موج جمع‌آوری شدند.",
        "## مثال‌ها",
        "The abstract states the main finding in five lines. — چکیده یافتهٔ اصلی را در پنج خط می‌گوید.",
        "One caveat: the sample was small. — یک قید احتیاط: نمونه کوچک بود.",
        "Every report lists its limitations. — هر گزارشی محدودیت‌هایش را فهرست می‌کند.",
        "## خطاهای رایج",
        "برای داده‌ای که جمع شده was/were می‌آید: was collected ✓ / is collected ✗ (اگر کار تمام شده باشد).",
        "caveat گاهی مفرد و گاهی جمع می‌آید: one caveat / several caveats.",
        "findings همیشه جمع است: the findings are، نه the findings is.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-simple",
    vocab: {
      id: "vocab.research-reports",
      title: "Research Reports",
      titleFa: "گزارش‌های پژوهشی",
      prerequisites: ["vocab.professional-reports"],
    },
    words: [
      {
        slug: "abstract", w: "abstract", fa: "چکیده", ipa: "/ˈæbstrækt/", pos: "noun",
        def: "a short summary at the start of a paper",
        ex: [
          ["The abstract states the main finding in five lines.", "چکیده یافتهٔ اصلی را در پنج خط می‌گوید."],
          ["Write the abstract last.", "چکیده را آخر بنویس."],
        ],
        col: ["a short abstract", "write the abstract"],
        note: "abstract در این معنا اسم است و جای تکیه فرق می‌کند؛ صفت آن به معنی «انتزاعی» است.",
      },
      {
        slug: "findings", w: "findings", fa: "یافته‌ها", ipa: "/ˈfaɪndɪŋz/", pos: "noun",
        def: "the results a study reports",
        ex: [
          ["The findings are summarised on page two.", "یافته‌ها در صفحهٔ دوم خلاصه شده‌اند."],
          ["Our findings contradict the earlier study.", "یافته‌های ما با مطالعهٔ پیشین تناقض دارد."],
        ],
        col: ["key findings", "report findings"],
        note: "findings همیشه جمع است: the findings are، نه is.",
      },
      {
        slug: "caveat", w: "caveat", fa: "قید احتیاط", ipa: "/ˈkæviæt/", pos: "noun",
        def: "a warning about a limit on a result",
        ex: [
          ["One caveat: the sample was small.", "یک قید احتیاط: نمونه کوچک بود."],
          ["The caveat is buried in a footnote.", "قید احتیاط در پانویس دفن شده است."],
        ],
        col: ["one important caveat", "add a caveat"],
        note: "caveat قابل‌شمارش است و با on هم می‌آید: a caveat on the result.",
      },
      {
        slug: "limitation", w: "limitation", fa: "محدودیت", ipa: "/ˌlɪmɪˈteɪʃn/", pos: "noun",
        def: "a weakness in how a study was done",
        ex: [
          ["Every report lists its limitations.", "هر گزارشی محدودیت‌هایش را فهرست می‌کند."],
          ["The main limitation was the short follow-up.", "محدودیت اصلی پیگیری کوتاه بود."],
        ],
        col: ["a key limitation", "acknowledge a limitation"],
        note: "limitation قابل‌شمارش است؛ فعل آن limit و صفت آن limited.",
      },
      {
        slug: "peer-review", w: "peer review", fa: "داوری همتا", ipa: "/ˌpɪə rɪˈvjuː/", pos: "noun",
        def: "checking a paper by other experts before publication",
        ex: [
          ["The paper passed peer review in June.", "مقاله در ژوئن داوری همتا را گذراند."],
          ["Peer review caught two errors.", "داوری همتا دو خطا را گرفت."],
        ],
        col: ["pass peer review", "a peer-reviewed journal"],
        note: "peer review یک عبارت است؛ صفت آن با خط تیره: peer-reviewed.",
      },
      {
        slug: "dissemination", w: "dissemination", fa: "انتشار", ipa: "/dɪˌsemɪˈneɪʃn/", pos: "noun",
        def: "spreading results to the people who need them",
        ex: [
          ["Dissemination of the findings came months later.", "انتشار یافته‌ها ماه‌ها بعد انجام شد."],
          ["A short summary helps dissemination.", "یک خلاصهٔ کوتاه به انتشار کمک می‌کند."],
        ],
        col: ["dissemination of results", "wide dissemination"],
        note: "dissemination غیرقابل‌شمارش است؛ فعل آن disseminate.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "abstract", gloss: "چکیده",
        options: ["چکیده", "پانویس", "نمودار", "منبع"],
        note: "abstract خلاصهٔ کوتاه آغاز مقاله است.",
      },
      {
        k: "mcq", word: "caveat", gloss: "قید احتیاط",
        options: ["قید احتیاط", "نتیجه", "فرضیه", "نمونه"],
        note: "caveat هشداری دربارهٔ محدودیت یک نتیجه است.",
      },
      {
        k: "mcq", word: "dissemination", gloss: "انتشار",
        options: ["انتشار", "داوری", "ویرایش", "جمع‌آوری"],
        note: "dissemination رساندن یافته‌ها به کسانی است که به آن نیاز دارند.",
      },
      {
        k: "blank", sentence: "The survey ___ conducted in June.",
        accepted: ["was", "is"], skill: "VOCABULARY",
        note: "مجهول گذشته was conducted؛ اگر گزارش زمان حال را روایت کند is هم می‌آید.",
      },
      {
        k: "blank", sentence: "The findings ___ summarised on page two.",
        accepted: ["are", "were"], skill: "VOCABULARY",
        note: "findings جمع است، پس are/were می‌گیرد.",
      },
      {
        k: "recall", prompt: "محدودیت", accepted: ["limitation"],
        note: "محدودیت limitation است و صفت آن limited.",
      },
      {
        k: "tr", prompt: "یافته‌ها در صفحهٔ دوم خلاصه شده‌اند.",
        accepted: [
          "the findings are summarised on page two",
          "the findings are summarized on page two",
        ],
        note: "مجهول حال ساده: are summarised.",
      },
      {
        k: "listen", text: "The survey was conducted in June.",
        note: "به was conducted گوش کنید: مجهول گذشتهٔ ساده.",
      },
      {
        k: "prod", prompt: "بگو پیمایش در ژوئن انجام شد.",
        mustUse: ["was", "conducted"], minWords: 5,
        accepted: ["The survey was conducted in June."],
        hint: "با The survey was… شروع کن.",
        note: "ترتیب لنگرها: was پیش از conducted.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ نهایی‌سازی گزارش پژوهشی", partner: "سرپرست پژوهش",
        prompt: "بگو یافته‌ها در صفحهٔ دوم خلاصه شده‌اند.",
        question: "Where are the findings?",
        questionFa: "یافته‌ها کجاست؟",
        accepted: [
          "The findings are summarised on page two.",
          "They are summarised on page two.",
        ],
        note: "findings جمع است: are summarised.",
      },
      {
        scene: "جلسهٔ نهایی‌سازی گزارش پژوهشی", partner: "سرپرست پژوهش",
        prompt: "بگو یک قید احتیاط هست: نمونه کوچک بود.",
        question: "Anything to flag?",
        questionFa: "چیزی هست که باید علامت بزنیم؟",
        accepted: [
          "One caveat: the sample was small.",
          "There is one caveat: the sample was small.",
        ],
        note: "one caveat: یک قید احتیاط.",
      },
      {
        scene: "جلسهٔ نهایی‌سازی گزارش پژوهشی", partner: "سرپرست پژوهش",
        prompt: "بگو هر گزارش باید محدودیت‌هایش را فهرست کند.",
        question: "Do we need a limitations section?",
        questionFa: "به بخش محدودیت‌ها نیاز داریم؟",
        accepted: [
          "Every report lists its limitations.",
          "Yes, every report lists its limitations.",
        ],
        note: "every report مفرد است: lists.",
      },
      {
        scene: "جلسهٔ نهایی‌سازی گزارش پژوهشی", partner: "سرپرست پژوهش",
        prompt: "بگو مقاله در ژوئن داوری همتا را گذراند.",
        question: "Has the paper been reviewed?",
        questionFa: "مقاله داوری شده؟",
        accepted: [
          "The paper passed peer review in June.",
          "Yes, it passed peer review in June.",
        ],
        note: "pass peer review: از داوری همتا گذشتن.",
      },
      {
        scene: "جلسهٔ نهایی‌سازی گزارش پژوهشی", partner: "سرپرست پژوهش",
        prompt: "بگو انتشار یافته‌ها ماه‌ها بعد انجام شد.",
        question: "When will others hear about it?",
        questionFa: "دیگران کِی خبردار می‌شوند؟",
        accepted: [
          "Dissemination of the findings came months later.",
          "Dissemination came months later.",
        ],
        note: "dissemination غیرقابل‌شمارش است: came، نه were.",
      },
      {
        scene: "جلسهٔ نهایی‌سازی گزارش پژوهشی", partner: "سرپرست پژوهش",
        prompt: "بگو چکیده را آخر می‌نویسیم.",
        question: "Do we write the abstract first?",
        questionFa: "چکیده را اول می‌نویسیم؟",
        accepted: [
          "We write the abstract last.",
          "No, we write the abstract last.",
        ],
        note: "last در پایان جمله یعنی «آخر»، نه «گذشته».",
      },
    ],
  },
  {
    lesson: {
      id: "c1.research-method.lesson-01",
      order: 157,
      title: "Research Methodology",
      titleFa: "روش تحقیق",
      topic: "Science",
      tip: [
        "## قاعده",
        "برای نقد روشِ گذشته: should have + قسمت سوم — The study should have controlled for age.",
        "برای امکان گذشته: could have + قسمت سوم — The sample could have been larger.",
        "needn't have یعنی «لازم نبود انجام شود»: We needn't have run a second wave.",
        "## جدول",
        "The study should have controlled for age. — مطالعه باید سن را کنترل می‌کرد.",
        "The sample could have been larger. — نمونه می‌توانست بزرگ‌تر باشد.",
        "We needn't have run a second wave. — لازم نبود موج دوم را اجرا کنیم.",
        "## مثال‌ها",
        "The reliability of the measure was low. — پایایی ابزار اندازه‌گیری پایین بود.",
        "Replication matters more than a single result. — تکرارپذیری از یک نتیجهٔ تنها مهم‌تر است.",
        "A longitudinal study takes several years. — مطالعهٔ طولی چند سال طول می‌کشد.",
        "## خطاهای رایج",
        "پس از وجهی have می‌آید: should have controlled ✓ / should controlled ✗.",
        "reliability غیرقابل‌شمارش است؛ صفت آن reliable.",
        "randomised در انگلیسی بریتانیایی با s نوشته می‌شود (آمریکایی: randomized).",
      ].join("\n"),
    },
    grammarNode: "grammar.modals-in-the-past",
    vocab: {
      id: "vocab.research-methodology",
      title: "Research Methodology",
      titleFa: "روش تحقیق",
      prerequisites: ["vocab.statistical-literacy"],
    },
    words: [
      {
        slug: "reliability", w: "reliability", fa: "پایایی", ipa: "/rɪˌlaɪəˈbɪləti/", pos: "noun",
        def: "how consistently a measure gives the same result",
        ex: [
          ["The reliability of the measure was low.", "پایایی ابزار اندازه‌گیری پایین بود."],
          ["Reliability is not the same as validity.", "پایایی با روایی یکی نیست."],
        ],
        col: ["high reliability", "test reliability"],
        note: "reliability غیرقابل‌شمارش است؛ صفت آن reliable.",
      },
      {
        slug: "protocol", w: "protocol", fa: "پروتکل", ipa: "/ˈprəʊtəkɒl/", pos: "noun",
        def: "the written plan for how a study is run",
        ex: [
          ["The protocol should have been recorded.", "پروتکل باید ثبت می‌شد."],
          ["Every site followed the same protocol.", "هر سایت همان پروتکل را رعایت کرد."],
        ],
        col: ["follow a protocol", "the study protocol"],
        note: "protocol قابل‌شمارش است؛ در فناوری هم به معنی «قرارداد ارتباطی» است.",
      },
      {
        slug: "replication", w: "replication", fa: "تکرارپذیری", ipa: "/ˌreplɪˈkeɪʃn/", pos: "noun",
        def: "repeating a study to check its result",
        ex: [
          ["Replication matters more than a single result.", "تکرارپذیری از یک نتیجهٔ تنها مهم‌تر است."],
          ["A failed replication is still a useful result.", "تکرارپذیری ناموفق هم نتیجهٔ مفیدی است."],
        ],
        col: ["a replication study", "fail to replicate"],
        note: "replication غیرقابل‌شمارش است؛ فعل آن replicate.",
      },
      {
        slug: "randomised", w: "randomised", fa: "تصادفی‌سازی‌شده", ipa: "/ˈrændəmaɪzd/", pos: "adjective",
        def: "assigned to groups by chance, not by choice",
        ex: [
          ["A randomised sample reduces selection bias.", "نمونهٔ تصادفی‌سازی‌شده سوگیری انتخاب را کم می‌کند."],
          ["The trial was randomised and double-blind.", "آزمون تصادفی‌سازی‌شده و دوسوکور بود."],
        ],
        col: ["a randomised trial", "randomly assigned"],
        note: "randomised صفت است؛ قید آن randomly و اسم آن randomisation.",
      },
      {
        slug: "longitudinal", w: "longitudinal", fa: "طولی", ipa: "/ˌlɒndʒɪˈtjuːdɪnl/", pos: "adjective",
        def: "following the same people over many years",
        ex: [
          ["A longitudinal study takes several years.", "مطالعهٔ طولی چند سال طول می‌کشد."],
          ["The longitudinal data showed a slow shift.", "داده‌های طولی تغییری کند را نشان دادند."],
        ],
        col: ["a longitudinal study", "longitudinal data"],
        note: "longitudinal صفت است و متضاد cross-sectional.",
      },
      {
        slug: "confound", w: "confound", fa: "عامل مخدوش‌کننده", ipa: "/kənˈfaʊnd/", pos: "noun",
        def: "a hidden factor that distorts a result",
        ex: [
          ["Age was a confound the authors missed.", "سن عاملی مخدوش‌کننده بود که نویسندگان ندیدند."],
          ["Every confound was listed in the appendix.", "هر عامل مخدوش‌کننده در پیوست فهرست شده بود."],
        ],
        col: ["a hidden confound", "control for confounds"],
        note: "confound در این معنا اسم است؛ فعل آن به معنی «گیج کردن» است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "reliability", gloss: "پایایی",
        options: ["پایایی", "روایی", "نمونه", "فرضیه"],
        note: "reliability یعنی یک ابزار چقدر پایدار همان نتیجه را می‌دهد.",
      },
      {
        k: "mcq", word: "replication", gloss: "تکرارپذیری",
        options: ["تکرارپذیری", "تصادفی‌سازی", "پرسشنامه", "پیوست"],
        note: "replication تکرار یک مطالعه برای بررسی نتیجه است.",
      },
      {
        k: "mcq", word: "longitudinal", gloss: "طولی",
        options: ["طولی", "عرضی", "آزمایشی", "مقطعی"],
        note: "longitudinal یعنی همان افراد را در طول چند سال دنبال کردن.",
      },
      {
        k: "blank", sentence: "The study ___ have controlled for age.",
        accepted: ["should", "could"], skill: "VOCABULARY",
        note: "should have نقد روش و could have امکان گذشته است.",
      },
      {
        k: "blank", sentence: "The sample ___ have been larger.",
        accepted: ["should", "could"], skill: "VOCABULARY",
        note: "نمونه می‌توانست/باید بزرگ‌تر باشد: could have / should have been.",
      },
      {
        k: "recall", prompt: "تکرارپذیری", accepted: ["replication"],
        note: "تکرارپذیری replication است و فعل آن replicate.",
      },
      {
        k: "tr", prompt: "مطالعه باید سن را کنترل می‌کرد.",
        accepted: [
          "the study should have controlled for age",
          "the study should have controlled age",
        ],
        note: "should have + قسمت سوم برای نقد گذشته.",
      },
      {
        k: "listen", text: "The study should have controlled for age.",
        note: "به should have controlled گوش کنید: نقد روش گذشته.",
      },
      {
        k: "prod", prompt: "بگو مطالعه باید سن را کنترل می‌کرد.",
        mustUse: ["should", "have"], minWords: 6,
        accepted: ["The study should have controlled for age."],
        hint: "با The study should have… شروع کن.",
        note: "ترتیب لنگرها: should پیش از have.",
      },
    ],
    dialogue: [
      {
        scene: "بازبینی روش یک مطالعه", partner: "همکار پژوهشی",
        prompt: "بگو مطالعه باید سن را کنترل می‌کرد.",
        question: "What was wrong with the design?",
        questionFa: "طراحی چه ایرادی داشت؟",
        accepted: [
          "The study should have controlled for age.",
          "It should have controlled for age.",
        ],
        note: "should have controlled: نقد کاری که انجام نشد.",
      },
      {
        scene: "بازبینی روش یک مطالعه", partner: "همکار پژوهشی",
        prompt: "بگو پایایی ابزار اندازه‌گیری پایین بود.",
        question: "How good was the measure?",
        questionFa: "آن ابزار چقدر خوب بود؟",
        accepted: [
          "The reliability of the measure was low.",
          "Reliability was low.",
        ],
        note: "reliability غیرقابل‌شمارش است: was low.",
      },
      {
        scene: "بازبینی روش یک مطالعه", partner: "همکار پژوهشی",
        prompt: "بگو پروتکل باید ثبت می‌شد.",
        question: "Was there a written protocol?",
        questionFa: "پروتکل مکتوبی بود؟",
        accepted: [
          "The protocol should have been recorded.",
          "No, the protocol was never recorded.",
        ],
        note: "should have been recorded: مجهول گذشته با وجهی.",
      },
      {
        scene: "بازبینی روش یک مطالعه", partner: "همکار پژوهشی",
        prompt: "بگو تکرارپذیری از یک نتیجهٔ تنها مهم‌تر است.",
        question: "Why repeat the study?",
        questionFa: "چرا مطالعه را تکرار کنیم؟",
        accepted: [
          "Replication matters more than a single result.",
          "Because replication matters more than one result.",
        ],
        note: "matters more than: مهم‌تر بودن از.",
      },
      {
        scene: "بازبینی روش یک مطالعه", partner: "همکار پژوهشی",
        prompt: "بگو نمونهٔ تصادفی‌سازی‌شده سوگیری انتخاب را کم می‌کند.",
        question: "Why randomise?",
        questionFa: "چرا تصادفی‌سازی؟",
        accepted: [
          "A randomised sample reduces selection bias.",
          "Because a randomised sample reduces bias.",
        ],
        note: "a randomised sample مفرد است: reduces.",
      },
      {
        scene: "بازبینی روش یک مطالعه", partner: "همکار پژوهشی",
        prompt: "بگو مطالعهٔ طولی چند سال طول می‌کشد.",
        question: "How long will the study run?",
        questionFa: "مطالعه چقدر طول می‌کشد؟",
        accepted: [
          "A longitudinal study takes several years.",
          "It takes several years.",
        ],
        note: "a longitudinal study مفرد: takes.",
      },
    ],
  },
];
