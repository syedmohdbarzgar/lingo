// v44 content, part 1 — B2 topics 12 and 13: renewable energy, education
// systems. Read by `v44_b2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "b2.renewables.lesson-01",
      order: 128,
      title: "Renewable Energy",
      titleFa: "انرژی‌های تجدیدپذیر",
      topic: "Environment",
      tip: [
        "## قاعده",
        "شرطی نوع دوم برای وضعیت غیرواقعی در حال یا آینده: If + گذشتهٔ ساده، would + مصدر.",
        "در نیمهٔ if هرگز would نمی‌آید: If we invested more, bills would fall.",
        "unless خودش منفی است، پس فعل بعدش مثبت می‌آید: Unless we act, the grid stays dirty.",
        "برای پیشنهاد مؤدبانه هم به کار می‌رود: It would help if we started this year.",
        "## جدول",
        "If we invested in solar, bills would fall. — اگر روی خورشیدی سرمایه‌گذاری می‌کردیم، قبض‌ها کم می‌شد.",
        "Unless we cut fossil fuels, the grid would stay dirty. — اگر سوخت‌های فسیلی را کم نکنیم، شبکهٔ برق آلوده می‌ماند.",
        "What would happen if the subsidy ended? — اگر یارانه تمام شود چه می‌شود؟",
        "## مثال‌ها",
        "Solar panels now cover the school roof. — پنل‌های خورشیدی حالا سقف مدرسه را پوشانده‌اند.",
        "A single turbine can power two hundred homes. — یک توربین می‌تواند دویست خانه را برق بدهد.",
        "The grid was built for coal, not for wind. — شبکهٔ برق برای زغال ساخته شده، نه برای باد.",
        "## خطاهای رایج",
        "would در نیمهٔ if نمی‌آید: If we would invest ✗ / If we invested ✓.",
        "پس از unless منفی نمی‌آید: unless we do not act ✗.",
        "fossil fuel معمولاً جمع می‌آید: fossil fuels ✓.",
      ].join("\n"),
    },
    grammarNode: "grammar.tentative-conditionals",
    vocab: {
      id: "vocab.renewable-energy",
      title: "Renewable Energy",
      titleFa: "انرژی تجدیدپذیر",
      prerequisites: ["vocab.environment"],
    },
    words: [
      {
        slug: "solar", w: "solar", fa: "خورشیدی", ipa: "/ˈsəʊlə/", pos: "adjective",
        def: "relating to the sun and its power",
        ex: [
          ["Solar panels now cover the school roof.", "پنل‌های خورشیدی حالا سقف مدرسه را پوشانده‌اند."],
          ["Solar power is cheaper than it was.", "انرژی خورشیدی از قبل ارزان‌تر شده است."],
        ],
        col: ["solar panels", "solar power"],
        note: "solar صفت است و پیش از اسم می‌آید: solar power ✓ / power solar ✗.",
      },
      {
        slug: "turbine", w: "turbine", fa: "توربین", ipa: "/ˈtɜːbaɪn/", pos: "noun",
        def: "a machine that turns wind or water into power",
        ex: [
          ["A single turbine can power two hundred homes.", "یک توربین می‌تواند دویست خانه را برق بدهد."],
          ["The turbine turns all night when it is windy.", "توربین وقتی باد می‌آید تمام شب می‌چرخد."],
        ],
        col: ["a wind turbine", "install a turbine"],
        note: "turbine قابل‌شمارش است؛ wind turbine رایج‌ترین ترکیب آن است.",
      },
      {
        slug: "fossil", w: "fossil", fa: "فسیلی", ipa: "/ˈfɒsl/", pos: "adjective",
        def: "made from ancient plants and animals, like coal and oil",
        ex: [
          ["Burning fossil fuels warms the planet.", "سوزاندن سوخت‌های فسیلی زمین را گرم می‌کند."],
          ["The country still depends on fossil fuels.", "کشور هنوز به سوخت‌های فسیلی وابسته است."],
        ],
        col: ["fossil fuels", "burn fossil fuels"],
        note: "fossil به‌تنهایی «سنگواره» است؛ در این درس با fuels می‌آید و جمع است.",
      },
      {
        slug: "grid", w: "grid", fa: "شبکهٔ برق", ipa: "/ɡrɪd/", pos: "noun",
        def: "the network that carries electricity to homes",
        ex: [
          ["The grid was built for coal, not for wind.", "شبکهٔ برق برای زغال ساخته شده، نه برای باد."],
          ["Solar farms feed power into the grid.", "مزارع خورشیدی برق را به شبکه می‌فرستند."],
        ],
        col: ["the national grid", "feed the grid"],
        note: "grid در این معنا شبکهٔ توزیع برق است و با the می‌آید.",
      },
      {
        slug: "subsidy", w: "subsidy", fa: "یارانه", ipa: "/ˈsʌbsədi/", pos: "noun",
        def: "money a government pays to make something cheaper",
        ex: [
          ["The subsidy made solar cheaper.", "یارانه خورشیدی را ارزان‌تر کرد."],
          ["A subsidy on panels helped many families.", "یارانهٔ پنل‌ها به بسیاری از خانواده‌ها کمک کرد."],
        ],
        col: ["a government subsidy", "cut a subsidy"],
        note: "subsidy قابل‌شمارش است و با on می‌آید: a subsidy on panels.",
      },
      {
        slug: "efficiency", w: "efficiency", fa: "بازدهی؛ کارایی", ipa: "/ɪˈfɪʃnsi/", pos: "noun",
        def: "how well power or time is used",
        ex: [
          ["Modern panels have higher efficiency.", "پنل‌های امروزی بازدهی بالاتری دارند."],
          ["The report measures energy efficiency.", "گزارش بازدهی انرژی را می‌سنجد."],
        ],
        col: ["energy efficiency", "improve efficiency"],
        note: "efficiency غیرقابل‌شمارش است؛ صفت آن efficient است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "solar", gloss: "خورشیدی",
        options: ["خورشیدی", "فسیلی", "آبی", "هسته‌ای"],
        note: "solar یعنی مربوط به خورشید؛ fossil یعنی فسیلی.",
      },
      {
        k: "mcq", word: "turbine", gloss: "توربین",
        options: ["توربین", "شبکهٔ برق", "یارانه", "مبدل"],
        note: "turbine ماشینی است که باد یا آب را به برق تبدیل می‌کند.",
      },
      {
        k: "mcq", word: "subsidy", gloss: "یارانه",
        options: ["یارانه", "مالیات", "سرمایه", "بیمه"],
        note: "subsidy پولی است که دولت برای ارزان‌شدن چیزی می‌پردازد.",
      },
      {
        k: "blank", sentence: "If governments ___ more in solar, prices would fall.",
        accepted: ["invested", "spent"], skill: "VOCABULARY",
        note: "نیمهٔ شرط شرطی نوع دوم گذشتهٔ ساده است: invested / spent.",
      },
      {
        k: "blank", sentence: "Unless we cut fossil fuels, the grid ___ stay dirty.",
        accepted: ["would", "will"], skill: "VOCABULARY",
        note: "unless می‌تواند با نتیجهٔ would یا will بیاید؛ would غیرواقعی و will واقعی است.",
      },
      {
        k: "recall", prompt: "شبکهٔ برق", accepted: ["grid"],
        note: "شبکهٔ توزیع برق the grid است.",
      },
      {
        k: "tr", prompt: "اگر بیشتر روی انرژی خورشیدی سرمایه‌گذاری می‌کردیم، قبض‌ها کمتر می‌شد.",
        accepted: [
          "if we invested more in solar energy, the bills would be lower",
          "if we invested more in solar power, our bills would fall",
        ],
        note: "شرط غیرواقعی: گذشتهٔ ساده در نیمهٔ if و would + مصدر در نتیجه.",
      },
      {
        k: "listen", text: "If we invested in solar, bills would fall.",
        note: "به invested در نیمهٔ if گوش کنید: گذشتهٔ ساده نشانهٔ شرطی نوع دوم است.",
      },
      {
        k: "prod", prompt: "یک پیشنهاد غیرواقعی برای انرژی شهرت بده.",
        mustUse: ["if", "would"], minWords: 9,
        accepted: ["If the city used solar power, the bills would be lower."],
        hint: "با If the city… شروع کن و با would تمام کن.",
        note: "ترتیب لنگرها: if می‌آید و would در نیمهٔ نتیجه بعد از آن.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ شورای شهر دربارهٔ انرژی، پشت یک میز پر از نمودار", partner: "کارشناس انرژی",
        prompt: "بگو اگر روی خورشیدی سرمایه‌گذاری می‌کردیم قبض‌ها کم می‌شد.",
        question: "Why are you proposing solar panels?",
        questionFa: "چرا پنل خورشیدی پیشنهاد می‌کنی؟",
        accepted: [
          "If we invested in solar, the bills would fall.",
          "Because solar power would cut our bills.",
        ],
        note: "شرط غیرواقعی با if + گذشته و would + مصدر.",
      },
      {
        scene: "جلسهٔ شورای شهر دربارهٔ انرژی، پشت یک میز پر از نمودار", partner: "کارشناس انرژی",
        prompt: "بگو یارانه پنل‌ها قیمت را پایین آورده است.",
        question: "Is solar affordable for families?",
        questionFa: "خورشیدی برای خانواده‌ها مقرون‌به‌صرفه است؟",
        accepted: [
          "The subsidy has made solar cheaper.",
          "Yes, the subsidy brought the price down.",
        ],
        note: "subsidy قابل‌شمارش است: the subsidy / a subsidy on panels.",
      },
      {
        scene: "جلسهٔ شورای شهر دربارهٔ انرژی، پشت یک میز پر از نمودار", partner: "کارشناس انرژی",
        prompt: "بگو شبکهٔ برق برای زغال ساخته شده نه برای باد.",
        question: "Can the grid handle wind power?",
        questionFa: "شبکهٔ برق باد را تحمل می‌کند؟",
        accepted: [
          "The grid was built for coal, not for wind.",
          "Not yet — the grid was designed for coal.",
        ],
        note: "the grid مفرد است: the grid was built ✓.",
      },
      {
        scene: "جلسهٔ شورای شهر دربارهٔ انرژی، پشت یک میز پر از نمودار", partner: "کارشناس انرژی",
        prompt: "بگو یک توربین می‌تواند دویست خانه را برق بدهد.",
        question: "How much power does one turbine make?",
        questionFa: "یک توربین چقدر برق تولید می‌کند؟",
        accepted: [
          "One turbine can power two hundred homes.",
          "Enough for a small neighbourhood.",
        ],
        note: "power در این جمله فعل است: to power homes یعنی خانه‌ها را برق‌رسانی کردن.",
      },
      {
        scene: "جلسهٔ شورای شهر دربارهٔ انرژی، پشت یک میز پر از نمودار", partner: "کارشناس انرژی",
        prompt: "بگو بازدهی پنل‌های جدید بالاتر است.",
        question: "Are the new panels better?",
        questionFa: "پنل‌های جدید بهترند؟",
        accepted: [
          "Yes, modern panels have higher efficiency.",
          "The new panels are more efficient.",
        ],
        note: "efficiency اسم و efficient صفت است؛ اینجا مقایسه با higher انجام می‌شود.",
      },
      {
        scene: "جلسهٔ شورای شهر دربارهٔ انرژی، پشت یک میز پر از نمودار", partner: "کارشناس انرژی",
        prompt: "بگو اگر یارانه تمام شود چه اتفاقی می‌افتد.",
        question: "What happens if the subsidy ends?",
        questionFa: "اگر یارانه تمام شود چه می‌شود؟",
        accepted: [
          "If the subsidy ended, many families would stop.",
          "We would need another plan if it ended.",
        ],
        note: "پاسخ کوتاه هم ساختار شرطی نوع دوم را نگه می‌دارد.",
      },
    ],
  },
  {
    lesson: {
      id: "b2.education.lesson-01",
      order: 129,
      title: "Education Systems",
      titleFa: "نظام‌های آموزشی",
      topic: "Education",
      tip: [
        "## قاعده",
        "بند موصولی تعریف‌کننده اطلاعاتِ لازم می‌دهد و با کاما نمی‌آید: students who learn by doing.",
        "بند موصولی غیرتعریف‌کننده فقط توضیح اضافه است و بین دو کاما می‌نشیند: The exam, which nobody liked, was cancelled.",
        "that فقط در بند تعریف‌کننده می‌آید: the system that fails them ✓ / the system, that fails them ✗.",
        "where برای مکان و when برای زمان: a school where children decide.",
        "## جدول",
        "The system that tests memory is not working. — سیستمی که حافظه را می‌سنجد کار نمی‌کند.",
        "The teacher, who had taught for years, disagreed. — معلم، که سال‌ها درس داده بود، مخالفت کرد.",
        "Students learn best in classes where they can talk. — دانش‌آموزان در کلاس‌هایی که می‌توانند حرف بزنند بهتر یاد می‌گیرند.",
        "## مثال‌ها",
        "The curriculum is the same for every school. — برنامهٔ درسی برای همهٔ مدرسه‌ها یکسان است.",
        "Tuition has doubled in ten years. — شهریه در ده سال دو برابر شده است.",
        "A scholarship covered her whole degree. — بورس تحصیلی تمام دورهٔ او را پوشش داد.",
        "## خطاهای رایج",
        "پس از کاما that نمی‌آید؛ آن‌جا which یا who لازم است.",
        "ضمیر در بند موصولی تکرار نمی‌شود: the book that I read it ✗.",
        "جمع curriculum در متن‌های رسمی curricula است.",
      ].join("\n"),
    },
    grammarNode: "grammar.relative-clauses",
    vocab: {
      id: "vocab.education-systems",
      title: "Education Systems",
      titleFa: "نظام آموزشی",
      prerequisites: ["vocab.university"],
    },
    words: [
      {
        slug: "curriculum", w: "curriculum", fa: "برنامهٔ درسی", ipa: "/kəˈrɪkjələm/", pos: "noun",
        def: "the subjects and content taught in a school",
        ex: [
          ["The curriculum is the same for every school.", "برنامهٔ درسی برای همهٔ مدرسه‌ها یکسان است."],
          ["Teachers helped design the new curriculum.", "معلمان در طراحی برنامهٔ درسی تازه کمک کردند."],
        ],
        col: ["the national curriculum", "design a curriculum"],
        note: "curriculum مفرد است و جمع آن curricula می‌شود.",
      },
      {
        slug: "tuition", w: "tuition", fa: "شهریه", ipa: "/tjuˈɪʃn/", pos: "noun",
        def: "money you pay for teaching",
        ex: [
          ["Tuition has doubled in ten years.", "شهریه در ده سال دو برابر شده است."],
          ["She pays her tuition monthly.", "او شهریه‌اش را ماهانه می‌پردازد."],
        ],
        col: ["pay tuition", "tuition fees"],
        note: "tuition در انگلیسی بریتانیایی «آموزش» هم معنا می‌دهد؛ در این درس «شهریه» است و غیرقابل‌شمارش.",
      },
      {
        slug: "scholarship", w: "scholarship", fa: "بورس تحصیلی", ipa: "/ˈskɒləʃɪp/", pos: "noun",
        def: "money given to a student to pay for study",
        ex: [
          ["A scholarship covered her whole degree.", "بورس تحصیلی تمام دورهٔ او را پوشش داد."],
          ["He won a scholarship to study abroad.", "او بورس تحصیل در خارج را برد."],
        ],
        col: ["win a scholarship", "a full scholarship"],
        note: "scholarship با win یا receive می‌آید، نه با take.",
      },
      {
        slug: "enrol", w: "enrol", fa: "ثبت‌نام کردن", ipa: "/ɪnˈrəʊl/", pos: "verb",
        def: "to put your name on a course or school list",
        ex: [
          ["Students enrol online in August.", "دانش‌آموزان در اوت آنلاین ثبت‌نام می‌کنند."],
          ["She enrolled in a design course.", "او در یک دورهٔ طراحی ثبت‌نام کرد."],
        ],
        col: ["enrol in a course", "enrol online"],
        note: "enrol با in می‌آید: enrol in a course؛ اسم آن enrolment است.",
      },
      {
        slug: "literacy", w: "literacy", fa: "سواد", ipa: "/ˈlɪtərəsi/", pos: "noun",
        def: "the ability to read and write",
        ex: [
          ["Adult literacy has improved since the reform.", "سواد بزرگسالان از زمان اصلاحات بهتر شده است."],
          ["The programme targets basic literacy.", "این برنامه سواد پایه را هدف گرفته است."],
        ],
        col: ["adult literacy", "basic literacy"],
        note: "literacy غیرقابل‌شمارش است؛ digital literacy یعنی سواد دیجیتال.",
      },
      {
        slug: "assessment", w: "assessment", fa: "ارزیابی", ipa: "/əˈsesmənt/", pos: "noun",
        def: "a check of how much someone has learned",
        ex: [
          ["Assessment happens twice a year.", "ارزیابی سالی دو بار انجام می‌شود."],
          ["The new assessment is less stressful.", "ارزیابی تازه فشار کمتری دارد."],
        ],
        col: ["continuous assessment", "a formal assessment"],
        note: "assessment قابل‌شمارش است: an assessment / two assessments.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "curriculum", gloss: "برنامهٔ درسی",
        options: ["برنامهٔ درسی", "شهریه", "بورس تحصیلی", "کارنامه"],
        note: "curriculum مجموعهٔ درس‌ها و محتوایی است که آموزش داده می‌شود.",
      },
      {
        k: "mcq", word: "tuition", gloss: "شهریه",
        options: ["شهریه", "بورس تحصیلی", "ثبت‌نام", "ارزیابی"],
        note: "tuition پولی است که برای آموزش پرداخت می‌شود.",
      },
      {
        k: "mcq", word: "scholarship", gloss: "بورس تحصیلی",
        options: ["بورس تحصیلی", "وام دانشجویی", "شهریه", "کارآموزی"],
        note: "scholarship پولی است که برای تحصیل به دانشجو داده می‌شود و بازپرداخت ندارد.",
      },
      {
        k: "blank", sentence: "The system ___ tests memory is not working.",
        accepted: ["that", "which"], skill: "VOCABULARY",
        note: "در بند تعریف‌کننده هم that و هم which درست است.",
      },
      {
        k: "blank", sentence: "Students learn best in classes ___ they can talk.",
        accepted: ["where", "in which"], skill: "VOCABULARY",
        note: "برای مکان where یا in which؛ هر دو یک معنا می‌دهند.",
      },
      {
        k: "recall", prompt: "شهریه", accepted: ["tuition"],
        note: "شهریهٔ تحصیل tuition است و غیرقابل‌شمارش.",
      },
      {
        k: "tr", prompt: "سیستمی که فقط حافظه را می‌سنجد کار نمی‌کند.",
        accepted: [
          "a system that only tests memory does not work",
          "the system that tests memory is not working",
        ],
        note: "بند that tests memory صفت جمله است و کاما نمی‌گیرد.",
      },
      {
        k: "listen", text: "Students learn best in classes where they can talk.",
        note: "به where گوش کنید: بند موصولی مکان را به classes وصل می‌کند.",
      },
      {
        k: "prod", prompt: "یک ویژگی مدرسهٔ خوب را با بند موصولی توصیف کن.",
        mustUse: ["that", "who"], minWords: 9,
        accepted: ["A school that listens to students is better for the teachers who work there."],
        hint: "با A school that… شروع کن و با … who… ادامه بده.",
        note: "ترتیب لنگرها: that در بند اول و who در بند دوم می‌آید.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ اولیای مدرسه دربارهٔ برنامهٔ درسی تازه", partner: "مدیر مدرسه",
        prompt: "بگو سیستمی که فقط حافظه را می‌سنجد کار نمی‌کند.",
        question: "What is wrong with the current system?",
        questionFa: "سیستم فعلی چه اشکالی دارد؟",
        accepted: [
          "The system that tests memory is not working.",
          "A system that only tests memory teaches nothing.",
        ],
        note: "بند that tests memory تعریف‌کننده است، پس کاما نمی‌گیرد.",
      },
      {
        scene: "جلسهٔ اولیای مدرسه دربارهٔ برنامهٔ درسی تازه", partner: "مدیر مدرسه",
        prompt: "بگو شهریه در ده سال دو برابر شده است.",
        question: "Why are families worried?",
        questionFa: "چرا خانواده‌ها نگران‌اند؟",
        accepted: [
          "Because tuition has doubled in ten years.",
          "Tuition has doubled, and salaries have not.",
        ],
        note: "tuition غیرقابل‌شمارش است، پس فعل مفرد has می‌گیرد.",
      },
      {
        scene: "جلسهٔ اولیای مدرسه دربارهٔ برنامهٔ درسی تازه", partner: "مدیر مدرسه",
        prompt: "بگو بورس تحصیلی تمام دورهٔ یک دانشجو را پوشش داده است.",
        question: "Do we support students who cannot pay?",
        questionFa: "از دانش‌آموزانی که توان پرداخت ندارند پشتیبانی می‌کنیم؟",
        accepted: [
          "Yes, a scholarship covered her whole degree.",
          "One scholarship paid for the full course.",
        ],
        note: "a scholarship با win یا receive می‌آید.",
      },
      {
        scene: "جلسهٔ اولیای مدرسه دربارهٔ برنامهٔ درسی تازه", partner: "مدیر مدرسه",
        prompt: "بگو دانش‌آموزان در کلاس‌هایی که می‌توانند حرف بزنند بهتر یاد می‌گیرند.",
        question: "What should the new classrooms look like?",
        questionFa: "کلاس‌های تازه چه شکلی باشند؟",
        accepted: [
          "Students learn best in classes where they can talk.",
          "We need rooms where students can work together.",
        ],
        note: "where بند موصولی مکان را می‌سازد: classes where…",
      },
      {
        scene: "جلسهٔ اولیای مدرسه دربارهٔ برنامهٔ درسی تازه", partner: "مدیر مدرسه",
        prompt: "بگو ارزیابی سالی دو بار انجام می‌شود.",
        question: "How often do you assess the students?",
        questionFa: "چند وقت یک بار دانش‌آموزان را ارزیابی می‌کنید؟",
        accepted: [
          "Assessment happens twice a year.",
          "We run an assessment every six months.",
        ],
        note: "how often پرسش تکرار است؛ twice a year یعنی سالی دو بار.",
      },
      {
        scene: "جلسهٔ اولیای مدرسه دربارهٔ برنامهٔ درسی تازه", partner: "مدیر مدرسه",
        prompt: "بگو معلمی که سال‌ها درس داده بود مخالفت کرد.",
        question: "Did the teachers agree with the plan?",
        questionFa: "معلم‌ها با طرح موافق بودند؟",
        accepted: [
          "The teacher, who had taught for years, disagreed.",
          "Not all of them — one teacher who had taught for years disagreed.",
        ],
        note: "بند who had taught for years توضیح اضافه است و بین دو کاما می‌آید.",
      },
    ],
  },
];
