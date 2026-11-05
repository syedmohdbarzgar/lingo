// v56 content — C2 syllabus topics 13 (professional editing and revision) and
// 14 (synthesizing specialized texts). Read by `v56_c2_batch.mjs`; never run alone.
export default [
  {
    lesson: {
      id: "c2.editing.lesson-01",
      order: 176,
      title: "Professional Editing and Revision",
      titleFa: "ویرایش و بازنویسی حرفه‌ای",
      topic: "Language",
      tip: [
        "## قاعده",
        "در ویرایش، خطاهای مطابقت فاعل و فعل بیشتر از همه دیده می‌شوند: هر یک از دو گزارشی که بررسی شد، مفرد است.",
        "با each of و neither of فعل مفرد می‌آید: Each of the reports was checked twice. / Neither of the drafts is ready.",
        "با a number of فعل جمع می‌آید: A number of errors were fixed.",
        "## جدول",
        "Each of the reports was checked twice. — هر یک از گزارش‌ها دو بار بررسی شد.",
        "Neither of the drafts is ready. — هیچ‌یک از پیش‌نویس‌ها آماده نیست.",
        "## مثال‌ها",
        "Redundancy hides in pairs like free gift and past history. — افزون‌گویی در جفت‌واژه‌هایی مثل free gift پنهان می‌شود.",
        "Consistency matters more than which spelling you choose. — یکدستی مهم‌تر از انتخاب املاست.",
        "Proofreading catches the errors that survive editing. — نمونه‌خوانی خطاهایی را می‌گیرد که از ویرایش جان سالم به در برده‌اند.",
        "## خطاهای رایج",
        "Each of the reports were checked ✗ — each of با فعل مفرد می‌آید.",
        "Neither of the drafts are ready ✗ — neither of هم مفرد است.",
        "proofreading و editing یکی نیستند: اولی سطحی و پایانی است، دومی معنا و ساختار را می‌سازد.",
      ].join("\n"),
    },
    grammarNode: "grammar.subject-verb-agreement",
    vocab: {
      id: "vocab.editing-revision",
      title: "Professional Editing and Revision",
      titleFa: "ویرایش و بازنویسی حرفه‌ای",
      prerequisites: ["vocab.semantic-precision"],
    },
    words: [
      {
        slug: "redundancy", w: "redundancy", fa: "افزون‌گویی", ipa: "/rɪˈdʌndənsi/", pos: "noun",
        def: "words that repeat what has already been said",
        ex: [
          ["Redundancy hides in pairs like free gift and past history.", "افزون‌گویی در جفت‌واژه‌هایی مثل free gift و past history پنهان می‌شود."],
          ["Cutting redundancy rarely loses meaning.", "حذف افزون‌گویی به‌ندرت معنا را از دست می‌دهد."],
        ],
        col: ["cut redundancy", "redundant phrasing"],
        note: "در بریتانیا redundancy به «تعدیل نیرو» هم گفته می‌شود؛ اینجا یعنی اطاله و تکرار.",
      },
      {
        slug: "consistency", w: "consistency", fa: "یکدستی", ipa: "/kənˈsɪstənsi/", pos: "noun",
        def: "keeping the same choice all the way through a text",
        ex: [
          ["Consistency matters more than which spelling you choose.", "یکدستی مهم‌تر از انتخاب املاست."],
          ["The edit fixed consistency, not style.", "ویرایش یکدستی را درست کرد، نه سبک را."],
        ],
        col: ["internal consistency", "consistency of terms"],
        note: "consistency یعنی «یکدستی» متن، نه پایداری فیزیکی.",
      },
      {
        slug: "errata", w: "errata", fa: "فهرست اصلاحات", ipa: "/ɪˈrɑːtə/", pos: "noun",
        def: "a published list of corrections to a text",
        ex: [
          ["The errata list runs to two pages.", "فهرست اصلاحات به دو صفحه می‌رسد."],
          ["Errata fix what the first printing got wrong.", "فهرست اصلاحات آنچه چاپ اول غلط گفته را درست می‌کند."],
        ],
        col: ["publish errata", "a sheet of errata"],
        note: "errata جمع است؛ مفرد آن erratum به معنی «یک خطای ثبت‌شده» است.",
      },
      {
        slug: "redraft", w: "redraft", fa: "بازنویسی کردن", ipa: "/ˌriːˈdrɑːft/", pos: "verb",
        def: "to write a new version of a text",
        ex: [
          ["Redraft the paragraph before you defend it.", "پیش از دفاع از پاراگراف، آن را بازنویسی کن."],
          ["She redrafted the opening three times.", "او آغاز متن را سه بار بازنویسی کرد."],
        ],
        col: ["redraft a section", "redraft from scratch"],
        note: "redraft فعل است؛ draft هم اسم «پیش‌نویس» است هم فعل «پیش‌نویس کردن».",
      },
      {
        slug: "proofreading", w: "proofreading", fa: "نمونه‌خوانی", ipa: "/ˈpruːfriːdɪŋ/", pos: "noun",
        def: "the final check for surface errors before publication",
        ex: [
          ["Proofreading catches the errors that survive editing.", "نمونه‌خوانی خطاهایی را می‌گیرد که از ویرایش جان سالم به در برده‌اند."],
          ["Proofreading is not the same as revising.", "نمونه‌خوانی با بازنگری یکی نیست."],
        ],
        col: ["a final proofreading", "careful proofreading"],
        note: "proofreading آخرین مرحله است؛ editing پیش از آن معنا و ساختار را می‌سازد.",
      },
      {
        slug: "house-style", w: "house style", fa: "شیوهٔ نشر", ipa: "/ˈhaʊs staɪl/", pos: "noun",
        def: "the conventions a publisher applies to every text",
        ex: [
          ["House style decides whether you write e-mail or email.", "شیوهٔ نشر تعیین می‌کند e-mail بنویسی یا email."],
          ["House style settles the argument about serial commas.", "شیوهٔ نشر بحث ویرگول سری را تمام می‌کند."],
        ],
        col: ["follow house style", "house style guide"],
        note: "عبارت دوکلمه‌ای با the می‌آید: the house style of the journal.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "redundancy", gloss: "افزون‌گویی",
        options: ["افزون‌گویی", "یکدستی", "نمونه‌خوانی", "فهرست اصلاحات"],
        note: "redundancy تکرار معنایی است که متن را سنگین می‌کند.",
      },
      {
        k: "mcq", word: "errata", gloss: "فهرست اصلاحات",
        options: ["فهرست اصلاحات", "شیوهٔ نشر", "بازنویسی کردن", "افزون‌گویی"],
        note: "errata فهرست چاپ‌شدهٔ اصلاحات است؛ مفرد آن erratum.",
      },
      {
        k: "mcq", word: "house style", gloss: "شیوهٔ نشر",
        options: ["شیوهٔ نشر", "نمونه‌خوانی", "یکدستی", "فهرست اصلاحات"],
        note: "house style قراردادهای یک ناشر برای همهٔ متن‌هایش است.",
      },
      {
        k: "blank", sentence: "Each of the reports ___ checked twice.",
        accepted: ["was", "is"], skill: "GRAMMAR",
        note: "با each of فعل مفرد می‌آید.",
      },
      {
        k: "blank", sentence: "Neither of the drafts ___ ready.",
        accepted: ["is", "was"], skill: "GRAMMAR",
        note: "با neither of فعل مفرد است.",
      },
      {
        k: "recall", prompt: "افزون‌گویی", accepted: ["redundancy"],
        note: "افزون‌گویی redundancy است.",
      },
      {
        k: "tr", prompt: "یکدستی مهم‌تر از انتخاب املاست.",
        accepted: [
          "consistency matters more than which spelling you choose",
          "consistency matters more than the spelling you choose",
        ],
        note: "matters more than: مقایسهٔ اهمیت با more than.",
      },
      {
        k: "listen", text: "Proofreading catches the errors that survive editing.",
        note: "به proofreading گوش کنید: نمونه‌خوانی، آخرین مرحله پیش از چاپ.",
      },
      {
        k: "prod", prompt: "بگو افزون‌گویی را حذف کن؛ به‌ندرت معنا از دست می‌رود.",
        mustUse: ["redundancy"], minWords: 7,
        accepted: ["Cutting redundancy rarely loses meaning, so cut it first."],
        hint: "با Cutting redundancy… شروع کن.",
        note: "cutting redundancy: اسم مصدر به‌عنوان فاعل، با فعل مفرد loses.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ ویرایش با سرویراستار", partner: "سرویراستار",
        prompt: "بگو افزون‌گویی را حذف کن؛ به‌ندرت معنا از دست می‌رود.",
        question: "Can we cut this paragraph?",
        questionFa: "می‌توانیم این پاراگراف را حذف کنیم؟",
        accepted: [
          "Cutting redundancy rarely loses meaning.",
          "Cut the redundancy; it rarely loses meaning.",
        ],
        note: "اسم مصدر در آغاز جمله فاعل است و فعل مفرد می‌گیرد.",
      },
      {
        scene: "جلسهٔ ویرایش با سرویراستار", partner: "سرویراستار",
        prompt: "بگو یکدستی مهم‌تر از انتخاب املاست.",
        question: "Which spelling should we use?",
        questionFa: "کدام املا را انتخاب کنیم؟",
        accepted: [
          "Consistency matters more than which spelling you choose.",
          "Pick one and keep it: consistency matters more.",
        ],
        note: "which spelling you choose: بند موصولی به‌عنوان متمم more than.",
      },
      {
        scene: "جلسهٔ ویرایش با سرویراستار", partner: "سرویراستار",
        prompt: "بگو شیوهٔ نشر تعیین می‌کند e-mail بنویسی یا email.",
        question: "Is e-mail hyphenated here?",
        questionFa: "e-mail خط تیره دارد اینجا؟",
        accepted: [
          "House style decides whether you write e-mail or email.",
          "That is a question of house style.",
        ],
        note: "whether … or: دو گزینه در یک پرسش غیرمستقیم.",
      },
      {
        scene: "جلسهٔ ویرایش با سرویراستار", partner: "سرویراستار",
        prompt: "بگو نمونه‌خوانی خطاهایی را می‌گیرد که از ویرایش جان سالم به در برده‌اند.",
        question: "Why do we need another pass?",
        questionFa: "چرا یک دور دیگر لازم است؟",
        accepted: [
          "Proofreading catches the errors that survive editing.",
          "Because proofreading catches what editing misses.",
        ],
        note: "the errors that survive editing: بند موصولی تعریفی با that.",
      },
      {
        scene: "جلسهٔ ویرایش با سرویراستار", partner: "سرویراستار",
        prompt: "بگو هر یک از گزارش‌ها دو بار بررسی شد.",
        question: "How many checks did the reports get?",
        questionFa: "گزارش‌ها چند بار بررسی شدند؟",
        accepted: [
          "Each of the reports was checked twice.",
          "Each one was checked twice.",
        ],
        note: "each of the reports با فعل مفرد: was checked.",
      },
      {
        scene: "جلسهٔ ویرایش با سرویراستار", partner: "سرویراستار",
        prompt: "بگو پیش از دفاع از پاراگراف، آن را بازنویسی کن.",
        question: "Should I defend this paragraph?",
        questionFa: "از این پاراگراف دفاع کنم؟",
        accepted: [
          "Redraft the paragraph before you defend it.",
          "Redraft it first, then defend it.",
        ],
        note: "before you defend it: حال ساده پس از before برای آینده.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.synthesis.lesson-01",
      order: 177,
      title: "Synthesizing Specialized Texts",
      titleFa: "خلاصه‌سازی متون تخصصی",
      topic: "Language",
      tip: [
        "## قاعده",
        "در خلاصه‌سازی، یافته‌های دیگران را با پرسش نقل‌شده گزارش می‌کنی: The review asked whether the effect was real.",
        "در پرسش غیرمستقیم ترتیب جمله خبری است: asked whether the effect was real (نه was the effect).",
        "برای چیزی که پیش از یک نقطهٔ گذشته رخ داده: asked when the data had been collected.",
        "## جدول",
        "The review asked whether the effect was real. — مرور پرسید آیا اثر واقعی است.",
        "The authors asked when the data had been collected. — نویسندگان پرسیدند داده‌ها چه زمانی گردآوری شده بود.",
        "## مثال‌ها",
        "A synthesis answers a question the sources did not ask. — ترکیب منابع به پرسشی پاسخ می‌دهد که منابع نپرسیده بودند.",
        "The distillation loses the caveats, which is the risk. — فشرده‌سازی قیدهای احتیاط را از دست می‌دهد و خطر همین است.",
        "The taxonomy separates errors of fact from errors of emphasis. — رده‌بندی خطاهای واقعیت را از خطاهای تأکید جدا می‌کند.",
        "## خطاهای رایج",
        "asked whether was the effect real ✗ — در پرسش نقل‌شده ترتیب خبری است.",
        "Whether ترجمهٔ «اگر» نیست؛ در این ساختار «آیا» است.",
        "synthesis و summary یکی نیستند: خلاصه یک متن را کوتاه می‌کند، ترکیب چند متن را به هم می‌بندد.",
      ].join("\n"),
    },
    grammarNode: "grammar.reported-questions",
    vocab: {
      id: "vocab.synthesizing-texts",
      title: "Synthesizing Specialized Texts",
      titleFa: "خلاصه‌سازی متون تخصصی",
      prerequisites: ["vocab.professional-reports"],
    },
    words: [
      {
        slug: "synthesis", w: "synthesis", fa: "ترکیب منابع", ipa: "/ˈsɪnθəsɪs/", pos: "noun",
        def: "an account that combines findings from several sources",
        ex: [
          ["A synthesis answers a question the sources did not ask.", "ترکیب منابع به پرسشی پاسخ می‌دهد که منابع نپرسیده بودند."],
          ["His synthesis of the two studies changed the field.", "ترکیب او از آن دو پژوهش رشته را تغییر داد."],
        ],
        col: ["a synthesis of the evidence", "produce a synthesis"],
        note: "جمع آن syntheses؛ فعل آن synthesize به معنی «ترکیب کردن» است.",
      },
      {
        slug: "distillation", w: "distillation", fa: "فشرده‌سازی", ipa: "/ˌdɪstɪˈleɪʃn/", pos: "noun",
        def: "the reduction of material to its essential content",
        ex: [
          ["The distillation loses the caveats, which is the risk.", "فشرده‌سازی قیدهای احتیاط را از دست می‌دهد و خطر همین است."],
          ["Her distillation of the report fitted on one slide.", "فشرده‌سازی او از گزارش در یک اسلاید جا شد."],
        ],
        col: ["a distillation of the argument", "lose detail in the distillation"],
        note: "distillation استعاره‌ای از تقطیر است: «جانِ مطلب» را نگه داشتن.",
      },
      {
        slug: "cross-reference", w: "cross-reference", fa: "ارجاع متقابل", ipa: "/ˌkrɒs ˈrefrəns/", pos: "noun",
        def: "a pointer from one part of a text to another",
        ex: [
          ["Every cross-reference has to survive the final renumbering.", "هر ارجاع متقابل باید از شماره‌گذاری نهایی جان سالم به در ببرد."],
          ["The cross-reference sends the reader to the appendix.", "ارجاع متقابل خواننده را به پیوست می‌فرستد."],
        ],
        col: ["a cross-reference to the appendix", "check the cross-references"],
        note: "با خط تیره نوشته می‌شود؛ فعل آن cross-refer است.",
      },
      {
        slug: "meta-analysis", w: "meta-analysis", fa: "فراتحلیل", ipa: "/ˌmetə əˈnæləsɪs/", pos: "noun",
        def: "a study that pools the results of other studies",
        ex: [
          ["The meta-analysis found a smaller effect than any single trial.", "فراتحلیل اثری کوچک‌تر از هر آزمون تنها یافت."],
          ["A meta-analysis cannot repair weak primary data.", "فراتحلیل نمی‌تواند دادهٔ ضعیف اولیه را ترمیم کند."],
        ],
        col: ["a meta-analysis of trials", "pooled in a meta-analysis"],
        note: "جمع آن meta-analyses؛ پیشوند meta- یعنی «دربارهٔ خود».",
      },
      {
        slug: "taxonomy", w: "taxonomy", fa: "رده‌بندی", ipa: "/tækˈsɒnəmi/", pos: "noun",
        def: "a system for classifying things into groups",
        ex: [
          ["The taxonomy separates errors of fact from errors of emphasis.", "رده‌بندی خطاهای واقعیت را از خطاهای تأکید جدا می‌کند."],
          ["Every taxonomy hides a judgement.", "هر رده‌بندی داوری‌ای را پنهان می‌کند."],
        ],
        col: ["a taxonomy of errors", "build a taxonomy"],
        note: "جمع آن taxonomies؛ صفت آن taxonomic.",
      },
      {
        slug: "disambiguate", w: "disambiguate", fa: "رفع ابهام کردن", ipa: "/ˌdɪsæmˈbɪɡjueɪt/", pos: "verb",
        def: "to remove ambiguity from a word or passage",
        ex: [
          ["Synthesis has to disambiguate the two senses of risk.", "ترکیب منابع باید دو معنای ریسک را از هم جدا کند."],
          ["The editor disambiguated the pronoun in one line.", "ویراستار ابهام ضمیر را با یک جمله برداشت."],
        ],
        col: ["disambiguate a term", "disambiguate the reference"],
        note: "disambiguate فعل است؛ اسم آن disambiguation.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "synthesis", gloss: "ترکیب منابع",
        options: ["ترکیب منابع", "فشرده‌سازی", "رده‌بندی", "ارجاع متقابل"],
        note: "synthesis چند منبع را در یک روایت به هم می‌بندد.",
      },
      {
        k: "mcq", word: "distillation", gloss: "فشرده‌سازی",
        options: ["فشرده‌سازی", "فراتحلیل", "رفع ابهام کردن", "ترکیب منابع"],
        note: "distillation کاهش متن به جانِ مطلب است.",
      },
      {
        k: "mcq", word: "taxonomy", gloss: "رده‌بندی",
        options: ["رده‌بندی", "ارجاع متقابل", "فشرده‌سازی", "فراتحلیل"],
        note: "taxonomy نظام دسته‌بندی است.",
      },
      {
        k: "blank", sentence: "The review asked whether the effect ___ real.",
        accepted: ["was", "were"], skill: "GRAMMAR",
        note: "پرسش نقل‌شده با ترتیب خبری: the effect was real.",
      },
      {
        k: "blank", sentence: "The authors asked when the data ___ been collected.",
        accepted: ["had", "was"], skill: "GRAMMAR",
        note: "برای گذشتهٔ مقدم، گذشتهٔ کامل: had been collected.",
      },
      {
        k: "recall", prompt: "فراتحلیل", accepted: ["meta-analysis"],
        note: "فراتحلیل meta-analysis است.",
      },
      {
        k: "tr", prompt: "فراتحلیل اثری کوچک‌تر از هر آزمون تنها یافت.",
        accepted: [
          "the meta-analysis found a smaller effect than any single trial",
          "a meta-analysis found a smaller effect than any single trial",
        ],
        note: "smaller … than: مقایسهٔ صفت با than.",
      },
      {
        k: "listen", text: "A synthesis answers a question the sources did not ask.",
        note: "به synthesis گوش کنید: ترکیب منابع، نه خلاصهٔ یکی‌یکی.",
      },
      {
        k: "prod", prompt: "بگو ترکیب منابع به پرسشی پاسخ می‌دهد که منابع نپرسیده بودند.",
        mustUse: ["synthesis"], minWords: 8,
        accepted: ["A synthesis answers a question the sources did not ask."],
        hint: "با A synthesis answers… شروع کن.",
        note: "the sources did not ask: بند موصولی بدون that.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ تدوین مرور نظام‌مند", partner: "هماهنگ‌کنندهٔ مرور",
        prompt: "بگو ترکیب منابع به پرسشی پاسخ می‌دهد که منابع نپرسیده بودند.",
        question: "What is new in our review?",
        questionFa: "چه چیز تازه‌ای در مرور ما هست؟",
        accepted: [
          "A synthesis answers a question the sources did not ask.",
          "It answers a question the sources never asked.",
        ],
        note: "a synthesis (نه the) برای بیان کلی.",
      },
      {
        scene: "جلسهٔ تدوین مرور نظام‌مند", partner: "هماهنگ‌کنندهٔ مرور",
        prompt: "بگو مرور پرسید آیا اثر واقعی است.",
        question: "What did the review actually ask?",
        questionFa: "مرور در واقع چه پرسید؟",
        accepted: [
          "The review asked whether the effect was real.",
          "It asked whether the effect was real.",
        ],
        note: "asked whether + ترتیب خبری، بدون وارونگی.",
      },
      {
        scene: "جلسهٔ تدوین مرور نظام‌مند", partner: "هماهنگ‌کنندهٔ مرور",
        prompt: "بگو نویسندگان پرسیدند داده‌ها چه زمانی گردآوری شده بود.",
        question: "Did they check the timing?",
        questionFa: "زمان گردآوری را بررسی کردند؟",
        accepted: [
          "The authors asked when the data had been collected.",
          "They asked when the data had been collected.",
        ],
        note: "asked when + گذشتهٔ کامل برای اتفاق مقدم.",
      },
      {
        scene: "جلسهٔ تدوین مرور نظام‌مند", partner: "هماهنگ‌کنندهٔ مرور",
        prompt: "بگو فشرده‌سازی قیدهای احتیاط را از دست می‌دهد.",
        question: "Is the short version safe?",
        questionFa: "نسخهٔ کوتاه بی‌خطر است؟",
        accepted: [
          "The distillation loses the caveats, which is the risk.",
          "It loses the caveats, and that is the risk.",
        ],
        note: ", which is the risk: بند غیرتعریفی با which برای اشاره به کل جمله.",
      },
      {
        scene: "جلسهٔ تدوین مرور نظام‌مند", partner: "هماهنگ‌کنندهٔ مرور",
        prompt: "بگو فراتحلیل اثری کوچک‌تر از هر آزمون تنها یافت.",
        question: "What did pooling the trials show?",
        questionFa: "ترکیب آزمون‌ها چه نشان داد؟",
        accepted: [
          "The meta-analysis found a smaller effect than any single trial.",
          "It found a smaller effect than any single trial.",
        ],
        note: "smaller … than: صفت تفضیلی با than.",
      },
      {
        scene: "جلسهٔ تدوین مرور نظام‌مند", partner: "هماهنگ‌کنندهٔ مرور",
        prompt: "بگو رده‌بندی خطاهای واقعیت را از خطاهای تأکید جدا می‌کند.",
        question: "Why do we need a classification?",
        questionFa: "چرا به دسته‌بندی نیاز داریم؟",
        accepted: [
          "The taxonomy separates errors of fact from errors of emphasis.",
          "It separates errors of fact from errors of emphasis.",
        ],
        note: "separate X from Y: جدا کردن دو چیز با from.",
      },
    ],
  },
];
