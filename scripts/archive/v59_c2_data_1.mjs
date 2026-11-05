// v59 content — C2 syllabus topics 23 (speaking to specialist audiences) and
// 24 (narrative analysis). Read by `v59_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.specialist-audiences.lesson-01",
      order: 186,
      title: "Speaking to Specialist Audiences",
      titleFa: "سخنرانی برای مخاطبان تخصصی",
      topic: "Presentations",
      tip: [
        "## قاعده",
        "وقتی جمله با قید منفی یا نیمه‌منفی (not only, rarely, seldom, hardly) آغاز شود، جای فعل و فاعل عوض می‌شود و فعل کمکی پیش از فاعل می‌آید: Not only does the method scale…",
        "اگر فعل کمکی در جمله نباشد، do/does/did را می‌آوریم و فعل اصلی به مصدر بی‌to برمی‌گردد: Rarely does a talk change a habit.",
        "این ساخت Emphasis می‌سازد: شنوندهٔ متخصص سنگینیِ ادعا را حس می‌کند، پس آن را برای یک جمله در هر بخش نگه دار.",
        "## جدول",
        "Not only does the recap help the specialists, it also helps the beginners. — جمع‌بندی نه‌تنها به متخصص‌ها کمک می‌کند، به تازه‌کارها هم کمک می‌کند.",
        "Rarely does a talk change how a specialist works. — به‌ندرت پیش می‌آید که یک سخنرانی شیوهٔ کار متخصص را عوض کند.",
        "Seldom have I seen a plenary so quiet. — کم پیش آمده نشست همگانی‌ای این‌قدر ساکت ببینم.",
        "## مثال‌ها",
        "The talk opens with a slide a layperson can read. — سخنرانی با اسلایدی آغاز می‌شود که یک غیرمتخصص هم می‌تواند بخواند.",
        "A practitioner hears the method and asks how it works on Monday. — متخصصِ در عمل روش را می‌شنود و می‌پرسد دوشنبه چطور کار می‌کند.",
        "Abstraction hides the cost that the audience came to hear about. — انتزاع هزینه‌ای را پنهان می‌کند که مخاطب آمده بود بشنود.",
        "A run-through with two colleagues catches the half-built slide. — اجرای تمرینی با دو همکار، اسلاید نیمه‌کاره را لو می‌دهد.",
        "## خطاهای رایج",
        "Not only the method scales, … ✗ — پس از not only در آغاز جمله، فعل کمکی لازم است: Not only does the method scale…",
        "Rarely a talk changes … ✗ — با rarely در آغاز، جای فاعل و کمکی عوض می‌شود: Rarely does a talk change…",
        "plenary را با seminar یکی نگیر: plenary نشست همگانیِ همهٔ حاضران است، seminar گروه کوچک‌تر.",
      ].join("\n"),
    },
    grammarNode: "grammar.inversion-negative-adverbials",
    vocab: {
      id: "vocab.specialist-audiences",
      title: "Speaking to Specialist Audiences",
      titleFa: "سخنرانی برای مخاطبان تخصصی",
      prerequisites: ["vocab.specialist-presentations"],
    },
    words: [
      {
        slug: "layperson", w: "layperson", fa: "غیرمتخصص", ipa: "/ˈleɪpɜːsn/", pos: "noun",
        def: "someone who has not been trained in the subject being discussed",
        ex: [
          ["The talk opens with a slide a layperson can read.", "سخنرانی با اسلایدی آغاز می‌شود که یک غیرمتخصص هم می‌تواند بخواند."],
          ["One layperson in the room asks the question everyone else is hiding.", "یک غیرمتخصص در سالن همان سؤالی را می‌پرسد که بقیه پنهان می‌کنند."],
        ],
        col: ["explain it to a layperson", "the layperson in the room"],
        note: "جمع آن laypeople است؛ متضادش specialist یا expert است.",
      },
      {
        slug: "plenary", w: "plenary", fa: "نشست همگانی", ipa: "/ˈpliːnəri/", pos: "noun",
        def: "a session that every attendee of a conference joins, not one parallel group",
        ex: [
          ["The plenary ends the morning with one question.", "نشست همگانی صبح را با یک پرسش به پایان می‌برد."],
          ["Nothing said in the plenary stays inside the room.", "هیچ‌چیز از آنچه در نشست همگانی گفته می‌شود در همان اتاق نمی‌ماند."],
        ],
        col: ["a plenary session", "speak in the plenary"],
        note: "صفت و اسم هر دو plenary است؛ حرف اضافه‌اش in است: in the plenary.",
      },
      {
        slug: "practitioner", w: "practitioner", fa: "متخصصِ در عمل", ipa: "/prækˈtɪʃənər/", pos: "noun",
        def: "someone who does the work the talk is about, not someone who only studies it",
        ex: [
          ["A practitioner hears the method and asks how it works on Monday.", "متخصصِ در عمل روش را می‌شنود و می‌پرسد دوشنبه چطور کار می‌کند."],
          ["The practitioner's question is always about the second week.", "پرسش متخصصِ در عمل همیشه دربارهٔ هفتهٔ دوم است."],
        ],
        col: ["a practising practitioner", "what a practitioner needs"],
        note: "practitioner با researcher جفت می‌شود: یکی عمل می‌کند، دیگری مطالعه.",
      },
      {
        slug: "abstraction", w: "abstraction", fa: "انتزاع", ipa: "/æbˈstrækʃn/", pos: "noun",
        def: "an idea stated without the concrete case that would make it land",
        ex: [
          ["Abstraction hides the cost that the audience came to hear about.", "انتزاع هزینه‌ای را پنهان می‌کند که مخاطب آمده بود بشنود."],
          ["One concrete number is worth three abstractions.", "یک عدد عینی به اندازهٔ سه انتزاع ارزش دارد."],
        ],
        col: ["avoid abstraction", "a level of abstraction"],
        note: "abstraction از abstract می‌آید؛ کنتراستش با example یا concrete case ساخته می‌شود.",
      },
      {
        slug: "recap", w: "recap", fa: "جمع‌بندی کوتاه", ipa: "/ˈriːkæp/", pos: "noun",
        def: "a short return to the main points at the end of a section or talk",
        ex: [
          ["The recap gives the specialists one line they can quote.", "جمع‌بندی به متخصص‌ها یک جمله می‌دهد که می‌توانند نقل کنند."],
          ["A recap of thirty seconds closes every section.", "جمع‌بندیِ سی‌ثانیه‌ای هر بخش را می‌بندد."],
        ],
        col: ["a quick recap", "recap the main points"],
        note: "recap هم اسم است هم فعل؛ فعلش مفعول مستقیم می‌گیرد: recap the points.",
      },
      {
        slug: "run-through", w: "run-through", fa: "اجرای تمرینی", ipa: "/ˈrʌnθruː/", pos: "noun",
        def: "a full rehearsal of a talk before the real one",
        ex: [
          ["A run-through with two colleagues catches the half-built slide.", "اجرای تمرینی با دو همکار، اسلاید نیمه‌کاره را لو می‌دهد."],
          ["The run-through ran four minutes long.", "اجرای تمرینی چهار دقیقه طولانی‌تر شد."],
        ],
        col: ["do a run-through", "a final run-through"],
        note: "اسم مرکب با خط پیوند نوشته می‌شود؛ فعلش جدا می‌آید: run through the slides.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "layperson", gloss: "غیرمتخصص",
        options: ["غیرمتخصص", "متخصصِ در عمل", "انتزاع", "جمع‌بندی کوتاه"],
        note: "layperson کسی است که در آن رشته آموزش تخصصی ندیده است.",
      },
      {
        k: "mcq", word: "plenary", gloss: "نشست همگانی",
        options: ["نشست همگانی", "اجرای تمرینی", "انتزاع", "متخصصِ در عمل"],
        note: "plenary نشستی است که همهٔ حاضران کنفرانس در آن شرکت می‌کنند.",
      },
      {
        k: "mcq", word: "run-through", gloss: "اجرای تمرینی",
        options: ["اجرای تمرینی", "جمع‌بندی کوتاه", "نشست همگانی", "غیرمتخصص"],
        note: "run-through تمرین کامل سخنرانی پیش از اجرای اصلی است.",
      },
      {
        k: "blank", sentence: "Not only does the method scale, but it also ___ the cost.",
        accepted: ["cuts", "reduces"], skill: "GRAMMAR",
        note: "پس از not only does، فعل دوم هم به شکل سوم‌شخص مفرد می‌آید: cuts یا reduces.",
      },
      {
        k: "blank", sentence: "Rarely ___ a talk change how a specialist works.",
        accepted: ["does", "can"], skill: "GRAMMAR",
        note: "با rarely در آغاز جمله، فعل کمکی پیش از فاعل می‌آید: does یا can.",
      },
      {
        k: "recall", prompt: "غیرمتخصص", accepted: ["layperson"],
        note: "غیرمتخصص layperson است؛ جمعش laypeople.",
      },
      {
        k: "tr", prompt: "این سخنرانی برای غیرمتخصص‌ها هم روشن است.",
        accepted: [
          "the talk is clear even to a layperson",
          "this talk is clear even to a layperson",
        ],
        note: "clear to somebody: روشن برای کسی؛ even برای تأکید می‌آید.",
      },
      {
        k: "listen", text: "Not only does the recap help the specialists, it also helps the beginners.",
        note: "به جای فعل کمکی گوش کنید: does پیش از فاعل، نشانهٔ inversion است.",
      },
      {
        k: "prod", prompt: "بگو سخنرانی با جمع‌بندی کوتاهی پایان می‌یابد که متخصص‌ها می‌توانند نقل کنند.",
        mustUse: ["recap"], minWords: 8,
        accepted: ["The talk ends with a recap that the specialists can quote."],
        hint: "با The talk ends with a recap… شروع کن.",
        note: "a recap that …: بند موصولی تعریفی، چون جمع‌بندی را مشخص می‌کند.",
      },
    ],
    dialogue: [
      {
        scene: "ارائه در کنفرانس تخصصی", partner: "پژوهشگر ارشد",
        prompt: "بگو این سخنرانی برای متخصص‌هاست، نه برای غیرمتخصص.",
        question: "Who is this talk for?",
        questionFa: "این سخنرانی برای چه کسانی است؟",
        accepted: [
          "It is for specialists, not for a layperson.",
          "The talk is for specialists and for one layperson in the room.",
        ],
        note: "not for a layperson در پایان، مخاطب را با حذف فعل روشن می‌کند.",
      },
      {
        scene: "ارائه در کنفرانس تخصصی", partner: "پژوهشگر ارشد",
        prompt: "بگو جمع‌بندی به همه نقطهٔ شروع مشترک می‌دهد.",
        question: "Why do you start with a recap?",
        questionFa: "چرا با جمع‌بندی شروع می‌کنی؟",
        accepted: [
          "A recap gives everyone the same starting point.",
          "I start with a recap so the room begins together.",
        ],
        note: "so + جمله برای هدف؛ starting point ترکیب ثابت است.",
      },
      {
        scene: "ارائه در کنفرانس تخصصی", partner: "پژوهشگر ارشد",
        prompt: "بگو انتزاع پس از یک نمونهٔ عینی می‌آید.",
        question: "Will you show the abstraction in the first minute?",
        questionFa: "انتزاع را در دقیقهٔ اول نشان می‌دهی؟",
        accepted: [
          "The abstraction comes after one concrete case.",
          "I keep the abstraction until the second slide.",
        ],
        note: "come after در برابر keep until: ترتیب زمانی را با حرف اضافه نشان بده.",
      },
      {
        scene: "ارائه در کنفرانس تخصصی", partner: "پژوهشگر ارشد",
        prompt: "بگو با دو همکار اجرای تمرینی کرده‌اید.",
        question: "Have you rehearsed the ending?",
        questionFa: "پایان را تمرین کرده‌ای؟",
        accepted: [
          "We did a run-through with two colleagues.",
          "The run-through fixed the last slide.",
        ],
        note: "do a run-through ترکیب ثابت است، نه make a run-through.",
      },
      {
        scene: "ارائه در کنفرانس تخصصی", partner: "پژوهشگر ارشد",
        prompt: "بگو پس از سخنرانی نشست همگانی برگزار می‌شود.",
        question: "Is there a plenary after your talk?",
        questionFa: "پس از سخنرانی تو نشست همگانی هست؟",
        accepted: [
          "There is a plenary after the talk.",
          "The plenary starts after my session.",
        ],
        note: "there is برای وجود؛ بعد از آن حرف تعریف a می‌آید.",
      },
      {
        scene: "ارائه در کنفرانس تخصصی", partner: "پژوهشگر ارشد",
        prompt: "بگو متخصصِ در عمل باید با یک قدم قابل‌اجرا بیرون برود.",
        question: "What should a practitioner take away?",
        questionFa: "متخصصِ در عمل باید چه چیزی با خود ببرد؟",
        accepted: [
          "A practitioner should leave with one step to try.",
          "One step that a practitioner can try this week.",
        ],
        note: "take away یعنی با خود بردن از یک جلسه؛ پاسخ با همان معنا بازنویسی می‌شود.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.narrative.lesson-01",
      order: 187,
      title: "Narrative Analysis",
      titleFa: "تحلیل روایت",
      topic: "Literature",
      tip: [
        "## قاعده",
        "برای رویدادی که پیش از نقطهٔ مرجعِ روایت رخ داده است، past perfect می‌آید: had + past participle.",
        "وقتی دو رویداد گذشته را کنار هم می‌گذاری، رویداد قدیمی‌تر past perfect می‌گیرد و رویداد تازه‌تر past simple می‌ماند: The narrator had hidden the letter before the police arrived.",
        "با by the time و before، مرز زمانی روشن می‌شود و past perfect دیگر اختیاری نیست.",
        "## جدول",
        "The narrator had already chosen the ending before the letter arrived. — راوی پیش از رسیدن نامه، پایان را انتخاب کرده بود.",
        "By the time the reader reaches the last page, the question has been answered. — تا خواننده به صفحهٔ آخر برسد، پرسش پاسخ گرفته است.",
        "She had left the village twice before the story begins. — پیش از آغاز داستان، دو بار روستا را ترک کرده بود.",
        "## مثال‌ها",
        "The chronology breaks in the second chapter. — ترتیب زمانی در فصل دوم به هم می‌ریزد.",
        "The focaliser is a child who does not understand the argument. — کانونِ دید کودکی است که بحث را نمی‌فهمد.",
        "The backstory explains why she leaves in the last chapter. — پیشینه توضیح می‌دهد چرا در فصل آخر می‌رود.",
        "The denouement answers the question the first chapter asked. — گره‌گشایی به پرسشی پاسخ می‌دهد که فصل اول پرسیده بود.",
        "## خطاهای رایج",
        "The narrator had hid the letter … ✗ — قسمت سوم hide، hidden است: had hidden.",
        "Before the police arrived, the narrator hid the letter. — درست است، اما اگر ترتیب زمانی مقصود باشد، had hidden روشن‌تر است.",
        "chronology را با timeline خلط نکن: chronology ترتیب روایت است، timeline نمودار زمانی.",
      ].join("\n"),
    },
    grammarNode: "grammar.past-perfect",
    vocab: {
      id: "vocab.narrative-analysis",
      title: "Narrative Analysis",
      titleFa: "تحلیل روایت",
      prerequisites: ["vocab.storytelling"],
    },
    words: [
      {
        slug: "diegesis", w: "diegesis", fa: "جهانِ روایت", ipa: "/ˌdaɪəˈdʒiːsɪs/", pos: "noun",
        def: "the world inside the story, as opposed to the comments around it",
        ex: [
          ["The narrator's comments stay outside the diegesis.", "توضیح‌های راوی بیرون از جهانِ روایت می‌مانند."],
          ["Everything inside the diegesis is told by one voice.", "هرچه درون جهانِ روایت است با یک صدا گفته می‌شود."],
        ],
        col: ["inside the diegesis", "outside the diegesis"],
        note: "متضاد آن در نقد، extra-diegetic است: بیرون از جهان داستان.",
      },
      {
        slug: "focaliser", w: "focaliser", fa: "کانونِ دید", ipa: "/ˈfəʊkəlaɪzə/", pos: "noun",
        def: "the character whose eyes and knowledge shape what the reader is shown",
        ex: [
          ["The focaliser is a child who does not understand the argument.", "کانونِ دید کودکی است که بحث را نمی‌فهمد."],
          ["A new focaliser arrives in the third part of the novel.", "در بخش سوم رمان، کانونِ دید تازه‌ای می‌آید."],
        ],
        col: ["a shift of focaliser", "the main focaliser"],
        note: "focaliser با narrator یکی نیست: راوی می‌گوید، focaliser می‌بیند.",
      },
      {
        slug: "denouement", w: "denouement", fa: "گره‌گشایی", ipa: "/deɪˈnuːmɒ̃/", pos: "noun",
        def: "the final part where the complications are resolved",
        ex: [
          ["The denouement answers the question the first chapter asked.", "گره‌گشایی به پرسشی پاسخ می‌دهد که فصل اول پرسیده بود."],
          ["A long denouement can undo a tight plot.", "گره‌گشاییِ طولانی می‌تواند طرحی فشرده را خراب کند."],
        ],
        col: ["a quiet denouement", "after the denouement"],
        note: "denouement پایانِ پس از اوج است؛ climax خودِ نقطهٔ اوج نام دارد.",
      },
      {
        slug: "chronology", w: "chronology", fa: "ترتیب زمانی", ipa: "/krəˈnɒlədʒi/", pos: "noun",
        def: "the order in which events are told, which may differ from the order they happened",
        ex: [
          ["The chronology breaks in the second chapter.", "ترتیب زمانی در فصل دوم به هم می‌ریزد."],
          ["A broken chronology makes the reader work harder.", "ترتیب زمانیِ شکسته خواننده را بیشتر به کار می‌گیرد."],
        ],
        col: ["a broken chronology", "keep the chronology"],
        note: "chronology ترتیبِ روایت است؛ اگر مقصود ترتیب واقعی رویدادهاست، chronological order بگو.",
      },
      {
        slug: "trope", w: "trope", fa: "کلیشهٔ روایی", ipa: "/trəʊp/", pos: "noun",
        def: "a familiar pattern of story or image that readers recognise at once",
        ex: [
          ["The trope of the journey returns at the end.", "کلیشهٔ روایی سفر در پایان برمی‌گردد."],
          ["A trope becomes a cliché when nobody questions it.", "کلیشهٔ روایی وقتی کلیشهٔ کهنه می‌شود که کسی آن را نپرسد."],
        ],
        col: ["a familiar trope", "subvert a trope"],
        note: "trope الگوی شناخته‌شده است؛ cliché همان الگو وقتی دیگر اثری ندارد.",
      },
      {
        slug: "backstory", w: "backstory", fa: "پیشینه", ipa: "/ˈbækstɔːri/", pos: "noun",
        def: "the history before the story starts that explains the characters",
        ex: [
          ["The backstory explains why she leaves in the last chapter.", "پیشینه توضیح می‌دهد چرا در فصل آخر می‌رود."],
          ["Too much backstory stops the present story.", "پیشینهٔ بیش از حد، داستانِ اکنون را متوقف می‌کند."],
        ],
        col: ["a long backstory", "fill in the backstory"],
        note: "backstory پیش از آغاز روایت است؛ flashback صحنه‌ای است که در میانهٔ روایت به گذشته می‌رود.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "denouement", gloss: "گره‌گشایی",
        options: ["گره‌گشایی", "کانونِ دید", "پیشینه", "ترتیب زمانی"],
        note: "denouement بخش پایانی است که گره‌های داستان باز می‌شوند.",
      },
      {
        k: "mcq", word: "focaliser", gloss: "کانونِ دید",
        options: ["کانونِ دید", "جهانِ روایت", "گره‌گشایی", "کلیشهٔ روایی"],
        note: "focaliser شخصیتی است که از دریچهٔ او روایت دیده می‌شود.",
      },
      {
        k: "mcq", word: "trope", gloss: "کلیشهٔ روایی",
        options: ["کلیشهٔ روایی", "پیشینه", "جهانِ روایت", "گره‌گشایی"],
        note: "trope الگوی آشنای روایت است که خواننده بی‌درنگ می‌شناسد.",
      },
      {
        k: "blank", sentence: "The narrator ___ the letter before the police arrived.",
        accepted: ["had hidden", "had already hidden"], skill: "GRAMMAR",
        note: "رویداد قدیمی‌تر با past perfect می‌آید: had hidden یا had already hidden.",
      },
      {
        k: "blank", sentence: "By the time the letter arrives, the narrator ___ the ending.",
        accepted: ["has already chosen", "has chosen"], skill: "GRAMMAR",
        note: "by the time + حال ساده، جملهٔ اصلی را present perfect می‌کند.",
      },
      {
        k: "recall", prompt: "گره‌گشایی", accepted: ["denouement"],
        note: "گره‌گشایی denouement است؛ همان بخش پس از اوج.",
      },
      {
        k: "tr", prompt: "راوی پیش از رسیدن نامه، پایان را انتخاب کرده بود.",
        accepted: [
          "the narrator had chosen the ending before the letter arrived",
          "the narrator had already chosen the ending before the letter arrived",
        ],
        note: "had chosen برای رویداد قدیمی‌تر و arrived برای رویداد بعدی.",
      },
      {
        k: "listen", text: "The denouement answers the question the first chapter asked.",
        note: "به the question the first chapter asked گوش کنید: ضمیر موصولی حذف‌شده است.",
      },
      {
        k: "prod", prompt: "بگو ترتیب زمانی در فصل دوم به هم می‌ریزد.",
        mustUse: ["chronology"], minWords: 6,
        accepted: ["The chronology breaks in the second chapter."],
        hint: "با The chronology breaks… شروع کن.",
        note: "break در این معنا لازم است و مفعول نمی‌گیرد: the chronology breaks.",
      },
    ],
    dialogue: [
      {
        scene: "کارگاه تحلیل روایت", partner: "مدرس ادبیات",
        prompt: "بگو کانونِ دید کودک است، نه نویسنده.",
        question: "Who tells the story?",
        questionFa: "چه کسی داستان را روایت می‌کند؟",
        accepted: [
          "The focaliser is a child, not the author.",
          "A child is the focaliser of the story.",
        ],
        note: "not the author در پایان، تفاوت را با حذف فعل روشن می‌کند.",
      },
      {
        scene: "کارگاه تحلیل روایت", partner: "مدرس ادبیات",
        prompt: "بگو ترتیب زمانی در فصل دوم می‌شکند.",
        question: "Does the chronology stay straight?",
        questionFa: "ترتیب زمانی مستقیم می‌ماند؟",
        accepted: [
          "The chronology breaks in the second chapter.",
          "The chronology jumps back twice.",
        ],
        note: "jump back یعنی روایت به گذشته برمی‌گردد.",
      },
      {
        scene: "کارگاه تحلیل روایت", partner: "مدرس ادبیات",
        prompt: "بگو پیشینه دلیل رفتن او را توضیح می‌دهد.",
        question: "What does the backstory explain?",
        questionFa: "پیشینه چه چیزی را توضیح می‌دهد؟",
        accepted: [
          "The backstory explains why she leaves.",
          "The backstory holds the reason she leaves.",
        ],
        note: "explain why + جمله؛ reason در انگلیسی حرف اضافه لازم ندارد.",
      },
      {
        scene: "کارگاه تحلیل روایت", partner: "مدرس ادبیات",
        prompt: "بگو گره‌گشایی پس از نامه می‌آید.",
        question: "Where does the denouement come?",
        questionFa: "گره‌گشایی کجا می‌آید؟",
        accepted: [
          "The denouement comes on the last page.",
          "The denouement arrives after the letter.",
        ],
        note: "come on the last page: با صفحه و کاغذ، on به کار می‌رود.",
      },
      {
        scene: "کارگاه تحلیل روایت", partner: "مدرس ادبیات",
        prompt: "بگو همان کلیشهٔ روایی دو بار به کار رفته است.",
        question: "Is the trope of the journey used again?",
        questionFa: "کلیشهٔ روایی سفر دوباره به کار رفته؟",
        accepted: [
          "The trope of the journey returns at the end.",
          "It is the same trope, used twice.",
        ],
        note: "the trope of the journey: اضافهٔ معنایی برای نام بردن الگو.",
      },
      {
        scene: "کارگاه تحلیل روایت", partner: "مدرس ادبیات",
        prompt: "بگو پانوشت‌ها بیرون از جهانِ روایت می‌مانند.",
        question: "What is outside the diegesis?",
        questionFa: "چه چیزی بیرون از جهانِ روایت است؟",
        accepted: [
          "The comments of the narrator stay outside the diegesis.",
          "The footnotes sit outside the diegesis.",
        ],
        note: "outside در برابر inside: حرف اضافهٔ مکانِ انتزاعی.",
      },
    ],
  },
];
