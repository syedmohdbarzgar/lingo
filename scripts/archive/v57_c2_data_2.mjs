// v57 content — C2 syllabus topics 17 (pragmatics) and 18 (culture-specific
// expressions). Read by `v57_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.pragmatics.lesson-01",
      order: 180,
      title: "Pragmatics",
      titleFa: "کاربردشناسی زبان",
      topic: "Language",
      tip: [
        "## قاعده",
        "پرسش مستقیم را در نقل قول به پرسش غیرمستقیم بدل کن: \"Is the answer final?\" → She asked whether the answer was final.",
        "در پرسش غیرمستقیم ترتیب فاعل و فعل برنمی‌گردد: He asked when the meeting would start (نه would the meeting start).",
        "برای بله/خیر از whether یا if استفاده کن؛ برای پرسش‌های wh همان واژهٔ پرسشی را نگه دار.",
        "## جدول",
        "She asked whether the answer was final. — او پرسید آیا پاسخ قطعی است.",
        "He asked when the meeting would start. — او پرسید جلسه کِی شروع می‌شود.",
        "They wanted to know if we had understood the implicature. — می‌خواستند بدانند آیا معنای ضمنی را گرفته‌ایم.",
        "## مثال‌ها",
        "The implicature is that the answer is no. — معنای ضمنی این است که پاسخ خیر است.",
        "The illocutionary force of that sentence is a warning. — نیروی کنشی آن جمله یک هشدار است.",
        "The uptake was silence, which is an answer. — واکنش سکوت بود، و سکوت هم پاسخ است.",
        "## خطاهای رایج",
        "She asked whether was the answer final ✗ — در پرسش غیرمستقیم جابه‌جایی فاعل و فعل نداریم.",
        "He asked when would the meeting start ✗ — would پس از فاعل می‌آید.",
        "implicature با implication را یکی نگیر: اولی در گفتگو ساخته می‌شود، دومی لازمهٔ منطقی است.",
      ].join("\n"),
    },
    grammarNode: "grammar.reported-questions",
    vocab: {
      id: "vocab.pragmatics",
      title: "Pragmatics",
      titleFa: "کاربردشناسی زبان",
      prerequisites: ["vocab.discourse-analysis"],
    },
    words: [
      {
        slug: "implicature", w: "implicature", fa: "معنای ضمنی", ipa: "/ˈɪmplɪkətʃə(r)/", pos: "noun",
        def: "a meaning the speaker suggests without saying it",
        ex: [
          ["The implicature is that the answer is no.", "معنای ضمنی این است که پاسخ خیر است."],
          ["Her implicature survived the polite wording.", "معنای ضمنی حرفش از دل تعارف سالم بیرون آمد."],
        ],
        col: ["conversational implicature", "cancel an implicature"],
        note: "implicature چیزی است که شنونده از گفته می‌سازد؛ با implication («لازمه») یکی نیست.",
      },
      {
        slug: "deixis", w: "deixis", fa: "اشاره‌گری", ipa: "/ˈdeɪksɪs/", pos: "noun",
        def: "the way words like here, now and you point to the situation of speaking",
        ex: [
          ["Deixis makes the note unreadable out of context.", "اشاره‌گری باعث می‌شود یادداشت بیرون از بافت ناخوانا باشد."],
          ["The deixis shifts when the speaker moves.", "وقتی گوینده جابه‌جا شود، اشاره‌گری هم جابه‌جا می‌شود."],
        ],
        col: ["spatial deixis", "deictic expressions"],
        note: "deixis اسم است و صفتش deictic؛ here، now و you نمونه‌های کلاسیک آن‌اند.",
      },
      {
        slug: "illocutionary", w: "illocutionary", fa: "کنشی (نیروی گفتار)", ipa: "/ˌɪləˈkjuːʃənəri/", pos: "adjective",
        def: "describing what an utterance does rather than what it says",
        ex: [
          ["The illocutionary force of that sentence is a warning.", "نیروی کنشی آن جمله یک هشدار است."],
          ["A question can carry an illocutionary request.", "یک پرسش می‌تواند درخواستی کنشی در خود داشته باشد."],
        ],
        col: ["illocutionary force", "illocutionary act"],
        note: "سه لایهٔ گفتار: locutionary (گفتن)، illocutionary (انجام دادن)، perlocutionary (اثر گذاشتن).",
      },
      {
        slug: "maxim", w: "maxim", fa: "اصل گفتگو", ipa: "/ˈmæksɪm/", pos: "noun",
        def: "one of the unspoken rules that keep a conversation cooperative",
        ex: [
          ["The maxim of relevance explains the odd reply.", "اصل تناسب موضوع پاسخ عجیب را توضیح می‌دهد."],
          ["He broke the maxim of quantity with a two-page answer.", "او با پاسخی دو صفحه‌ای اصل کمیت را شکست."],
        ],
        col: ["the maxim of quantity", "break a maxim"],
        note: "maximهای گرایس: quantity، quality، relation و manner؛ شکستنشان معنای ضمنی می‌سازد.",
      },
      {
        slug: "indirectness", w: "indirectness", fa: "غیرمستقیم‌گویی", ipa: "/ˌɪndəˈrektnəs/", pos: "noun",
        def: "the habit of expressing a request or refusal without stating it plainly",
        ex: [
          ["Indirectness protects both speakers in a refusal.", "غیرمستقیم‌گویی در رد کردن، از هر دو طرف محافظت می‌کند."],
          ["Too much indirectness reads as evasion.", "غیرمستقیم‌گویی بیش از حد شبیه طفره رفتن می‌شود."],
        ],
        col: ["politeness and indirectness", "a strategy of indirectness"],
        note: "indirectness در فرهنگ‌های مختلف حد متفاوتی دارد؛ در انگلیسی کاری، یک لایه کافی است.",
      },
      {
        slug: "uptake", w: "uptake", fa: "واکنش شنونده", ipa: "/ˈʌpteɪk/", pos: "noun",
        def: "the way a listener takes up and responds to what was said",
        ex: [
          ["The uptake was silence, which is an answer.", "واکنش سکوت بود، و سکوت هم پاسخ است."],
          ["A joke depends on its uptake.", "یک شوخی به واکنش شنونده بند است."],
        ],
        col: ["the uptake of a suggestion", "get little uptake"],
        note: "uptake در کاربردشناسی «واکنش گیرنده» است، نه «جذب»؛ غیرقابل‌شمارش است.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "implicature", gloss: "معنای ضمنی",
        options: ["معنای ضمنی", "اشاره‌گری", "اصل گفتگو", "واکنش شنونده"],
        note: "implicature معنایی است که گوینده می‌رساند، بی‌آنکه بگوید.",
      },
      {
        k: "mcq", word: "deixis", gloss: "اشاره‌گری",
        options: ["اشاره‌گری", "غیرمستقیم‌گویی", "معنای ضمنی", "نیروی کنشی"],
        note: "deixis یعنی واژه‌هایی مانند here و now که به موقعیت گفتار اشاره می‌کنند.",
      },
      {
        k: "mcq", word: "illocutionary", gloss: "نیروی کنشی",
        options: ["نیروی کنشی", "واکنش شنونده", "اصل گفتگو", "اشاره‌گری"],
        note: "illocutionary force یعنی کاری که جمله با گفتنش انجام می‌دهد، مانند هشدار دادن.",
      },
      {
        k: "blank", sentence: "She asked whether the answer ___ final.",
        accepted: ["was", "would be"], skill: "GRAMMAR",
        note: "در پرسش غیرمستقیم فعل به گذشته برمی‌گردد: was یا would be.",
      },
      {
        k: "blank", sentence: "He asked when the meeting ___ start.",
        accepted: ["would", "might"], skill: "GRAMMAR",
        note: "پس از asked، will به would بدل می‌شود؛ ترتیب فاعل و فعل عوض نمی‌شود.",
      },
      {
        k: "recall", prompt: "معنای ضمنی", accepted: ["implicature"],
        note: "معنای ضمنی implicature است.",
      },
      {
        k: "tr", prompt: "نیروی کنشی آن جمله یک هشدار است.",
        accepted: [
          "the illocutionary force of that sentence is a warning",
          "the illocutionary force of the sentence is a warning",
        ],
        note: "of that sentence: اشاره به جمله‌ای که همین حالا گفته شد.",
      },
      {
        k: "listen", text: "The implicature is that the answer is no.",
        note: "به implicature گوش کنید: معنای ضمنی از دل جمله بیرون می‌آید.",
      },
      {
        k: "prod", prompt: "بگو معنای ضمنی این است که پاسخ خیر است.",
        mustUse: ["implicature"], minWords: 8,
        accepted: ["The implicature is that the answer is no."],
        hint: "با The implicature is that… شروع کن.",
        note: "that-clause پس از is، محتوای معنای ضمنی را می‌آورد.",
      },
    ],
    dialogue: [
      {
        scene: "تحلیل یک گفتگوی کاری", partner: "تحلیلگر گفتگو",
        prompt: "بگو معنای ضمنی این است که پاسخ خیر است.",
        question: "So is it a yes?",
        questionFa: "پس جواب بله است؟",
        accepted: [
          "The implicature is that the answer is no.",
          "What she implies is no.",
        ],
        note: "imply فعل است و implicature اسم؛ معنای ضمنی را با اسمش دقیق‌تر می‌گویی.",
      },
      {
        scene: "تحلیل یک گفتگوی کاری", partner: "تحلیلگر گفتگو",
        prompt: "بگو واژه‌های اشاره‌ای یادداشت را بیرون از بافت ناخوانا می‌کنند.",
        question: "Why can't I follow this note?",
        questionFa: "چرا این یادداشت را نمی‌فهمم؟",
        accepted: [
          "Deixis makes the note unreadable out of context.",
          "The deictic words need context.",
        ],
        note: "make + مفعول + صفت: makes the note unreadable.",
      },
      {
        scene: "تحلیل یک گفتگوی کاری", partner: "تحلیلگر گفتگو",
        prompt: "بگو نیروی کنشی آن جمله یک هشدار است.",
        question: "Was that a warning?",
        questionFa: "آن یک هشدار بود؟",
        accepted: [
          "The illocutionary force is a warning.",
          "Its illocutionary force is a warning.",
        ],
        note: "force در این ترکیب «نیرو»ی کارکردی است، نه نیروی فیزیکی.",
      },
      {
        scene: "تحلیل یک گفتگوی کاری", partner: "تحلیلگر گفتگو",
        prompt: "بگو او اصل کمیت را با پاسخ دو صفحه‌ای شکست.",
        question: "Did he answer briefly?",
        questionFa: "کوتاه پاسخ داد؟",
        accepted: [
          "He broke the maxim of quantity.",
          "He broke the maxim of quantity with a long answer.",
        ],
        note: "break a maxim یعنی از قاعدهٔ گفتگو بیرون زدن؛ با پاسخ بلند، اصل کمیت شکسته می‌شود.",
      },
      {
        scene: "تحلیل یک گفتگوی کاری", partner: "تحلیلگر گفتگو",
        prompt: "بگو تعارف بیش از حد شبیه طفره رفتن می‌شود.",
        question: "Why not say it directly?",
        questionFa: "چرا مستقیم نگویی؟",
        accepted: [
          "Too much indirectness reads as evasion.",
          "Indirectness here sounds like evasion.",
        ],
        note: "read as و sound like هر دو «به نظر رسیدن» را با متمم اسمی می‌آورند.",
      },
      {
        scene: "تحلیل یک گفتگوی کاری", partner: "تحلیلگر گفتگو",
        prompt: "بگو واکنش سکوت بود و سکوت هم پاسخ است.",
        question: "How did she react?",
        questionFa: "او چه واکنشی داد؟",
        accepted: [
          "The uptake was silence.",
          "She said nothing, and silence is an uptake.",
        ],
        note: "silence غیرقابل‌شمارش است؛ a silence فقط برای یک سکوت مشخص به کار می‌رود.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.culture-expressions.lesson-01",
      order: 181,
      title: "Culture-specific Expressions",
      titleFa: "اصطلاحات فرهنگی",
      topic: "Culture",
      tip: [
        "## قاعده",
        "حرف تعریف را با اسم غیرقابل‌شمارش و مفهوم کلی حذف کن: An idiom is the hardest part of a culture to borrow.",
        "وقتی از نمونهٔ مشخص حرف می‌زنی، the بیاور: The proverb loses its force in translation.",
        "اسم‌های قابل‌شمارش مفرد هرگز بی‌حرف نمی‌آیند؛ a calque یا the calque، نه calque تنها.",
        "## جدول",
        "An idiom is the hardest part of a culture to borrow. — اصطلاح سخت‌ترین بخش فرهنگ برای قرض گرفتن است.",
        "The proverb loses its force in translation. — ضرب‌المثل در ترجمه نیرویش را از دست می‌دهد.",
        "Yoga is a loanword that kept its spelling. — yoga وامواژه‌ای است که املایش حفظ شده.",
        "## مثال‌ها",
        "The title is an allusion to a nursery rhyme. — عنوان تلمیحی به یک ترانهٔ کودکانه است.",
        "The calque sounds natural until you hear the original. — ترجمهٔ ساختاری طبیعی به نظر می‌رسد تا وقتی اصل را بشنوی.",
        "Culture shock came at the dinner table, not at the airport. — شوک فرهنگی سر میز شام آمد، نه در فرودگاه.",
        "## خطاهای رایج",
        "Idiom is hardest part of culture ✗ — اسم مفرد قابل‌شمارش حرف تعریف می‌خواهد: An idiom is the hardest part.",
        "He made allusion to a rhyme ✗ — make an allusion to، با حرف تعریف نامعین.",
        "allusion را با illusion («توهم») اشتباه نگیر؛ ریشه یکی نیست.",
      ].join("\n"),
    },
    grammarNode: "grammar.articles-a-an-the",
    vocab: {
      id: "vocab.culture-expressions",
      title: "Culture-specific Expressions",
      titleFa: "اصطلاحات فرهنگی",
      prerequisites: ["vocab.intercultural"],
    },
    words: [
      {
        slug: "allusion", w: "allusion", fa: "تلمیح", ipa: "/əˈluːʒn/", pos: "noun",
        def: "an indirect reference to a text, person or event the reader is expected to know",
        ex: [
          ["The title is an allusion to a nursery rhyme.", "عنوان تلمیحی به یک ترانهٔ کودکانه است."],
          ["Without the allusion the joke still works.", "بدون تلمیح هم شوخی کار می‌کند."],
        ],
        col: ["a literary allusion", "make an allusion to"],
        note: "allusion با illusion («توهم») اشتباه نشود؛ فعلش allude to است.",
      },
      {
        slug: "proverb", w: "proverb", fa: "ضرب‌المثل", ipa: "/ˈprɒvɜːb/", pos: "noun",
        def: "a fixed sentence that gives advice or states a common truth",
        ex: [
          ["The proverb loses its force when translated word for word.", "ضرب‌المثل با ترجمهٔ کلمه‌به‌کلمه نیرویش را از دست می‌دهد."],
          ["Every culture has a proverb about patience.", "هر فرهنگی ضرب‌المثلی دربارهٔ صبر دارد."],
        ],
        col: ["an old proverb", "as the proverb says"],
        note: "proverb یک جملهٔ کامل ثابت است؛ idiom بخشی از جمله است، نه جمله.",
      },
      {
        slug: "loanword", w: "loanword", fa: "وامواژه", ipa: "/ˈləʊnwɜːd/", pos: "noun",
        def: "a word taken from another language and used without translation",
        ex: [
          ["Yoga is a loanword that kept its spelling.", "yoga وامواژه‌ای است که املایش حفظ شده."],
          ["The loanword changed meaning after it arrived.", "این وامواژه پس از ورود معنایش عوض شد."],
        ],
        col: ["a loanword from Arabic", "borrow a loanword"],
        note: "loanword واژه را قرض می‌گیرد؛ calque ساختار را، و همین تفاوت کلیدی است.",
      },
      {
        slug: "calque", w: "calque", fa: "ترجمهٔ ساختاری", ipa: "/kælk/", pos: "noun",
        def: "a phrase built by translating another language's words one by one",
        ex: [
          ["Skyscraper is a calque in some languages, not a loanword.", "در برخی زبان‌ها skyscraper ترجمهٔ ساختاری است، نه وامواژه."],
          ["The calque sounds natural until you hear the original.", "ترجمهٔ ساختاری طبیعی به نظر می‌رسد تا وقتی اصل را بشنوی."],
        ],
        col: ["a calque from French", "translate as a calque"],
        note: "calque از فرانسهٔ calquer («رونوشت کردن») می‌آید؛ در فارسی «گرته‌برداری» هم گفته می‌شود.",
      },
      {
        slug: "culture-shock", w: "culture shock", fa: "شوک فرهنگی", ipa: "/ˈkʌltʃə ʃɒk/", pos: "noun",
        def: "the disorientation of meeting an unfamiliar way of life",
        ex: [
          ["Culture shock came at the dinner table, not at the airport.", "شوک فرهنگی سر میز شام آمد، نه در فرودگاه."],
          ["Culture shock fades, but the habits stay.", "شوک فرهنگی محو می‌شود، اما عادت‌ها می‌مانند."],
        ],
        col: ["reverse culture shock", "suffer culture shock"],
        note: "culture shock غیرقابل‌شمارش است؛ a culture shock فقط برای یک تجربهٔ مشخص به کار می‌رود.",
      },
      {
        slug: "idiom", w: "idiom", fa: "اصطلاح", ipa: "/ˈɪdiəm/", pos: "noun",
        def: "a fixed expression whose meaning cannot be worked out from its words",
        ex: [
          ["An idiom is the hardest part of a culture to borrow.", "اصطلاح سخت‌ترین بخش فرهنگ برای قرض گرفتن است."],
          ["The idiom means something no single word covers.", "این اصطلاح معنایی دارد که هیچ واژهٔ تنهایی آن را پوشش نمی‌دهد."],
        ],
        col: ["a common idiom", "translate an idiom"],
        note: "idiom با proverb فرق دارد: idiom جزء جمله است، proverb خودش جمله‌ای کامل.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "allusion", gloss: "تلمیح",
        options: ["تلمیح", "وامواژه", "ترجمهٔ ساختاری", "ضرب‌المثل"],
        note: "allusion اشارهٔ غیرمستقیم به متنی آشناست.",
      },
      {
        k: "mcq", word: "loanword", gloss: "وامواژه",
        options: ["وامواژه", "ترجمهٔ ساختاری", "شوک فرهنگی", "اصطلاح"],
        note: "loanword واژه‌ای است که از زبانی دیگر بدون ترجمه وارد شده است.",
      },
      {
        k: "mcq", word: "calque", gloss: "ترجمهٔ ساختاری",
        options: ["ترجمهٔ ساختاری", "تلمیح", "وامواژه", "ضرب‌المثل"],
        note: "calque ترجمهٔ کلمه‌به‌کلمهٔ یک عبارت از زبان دیگر است.",
      },
      {
        k: "blank", sentence: "___ proverb loses its force when translated word for word.",
        accepted: ["the", "a"], skill: "GRAMMAR",
        note: "هر دو حرف تعریف درست است: a برای یک ضرب‌المثل نمونه، the برای ضرب‌المثل مشخص.",
      },
      {
        k: "blank", sentence: "Yoga is ___ loanword that kept its spelling.",
        accepted: ["a", "the"], skill: "GRAMMAR",
        note: "اسم مفرد قابل‌شمارش حرف تعریف می‌خواهد؛ a loanword یا the loanword.",
      },
      {
        k: "recall", prompt: "وامواژه", accepted: ["loanword"],
        note: "وامواژه loanword است.",
      },
      {
        k: "tr", prompt: "این عنوان تلمیحی به یک ترانهٔ کودکانه است.",
        accepted: [
          "the title is an allusion to a nursery rhyme",
          "this title is an allusion to a nursery rhyme",
        ],
        note: "an allusion to: حرف اضافهٔ ثابت to است، نه of.",
      },
      {
        k: "listen", text: "The proverb loses its force when translated word for word.",
        note: "به word for word گوش کنید: ترجمهٔ کلمه‌به‌کلمه.",
      },
      {
        k: "prod", prompt: "بگو در برخی زبان‌ها «آسمان‌خراش» ترجمهٔ ساختاری است، نه وامواژه.",
        mustUse: ["calque"], minWords: 8,
        accepted: ["Skyscraper is a calque in some languages, not a loanword."],
        hint: "با Skyscraper is a calque in some languages… شروع کن.",
        note: "not a loanword در پایان، تقابل دو مفهوم را روشن می‌کند.",
      },
    ],
    dialogue: [
      {
        scene: "کارگاه ترجمهٔ اصطلاحات", partner: "مترجم فرهنگی",
        prompt: "بگو عنوان تلمیحی به یک ترانهٔ کودکانه است.",
        question: "Where is the title from?",
        questionFa: "این عنوان از کجاست؟",
        accepted: [
          "The title is an allusion to a nursery rhyme.",
          "It alludes to a nursery rhyme.",
        ],
        note: "allude to فعل است؛ allusion to اسم. هر دو با to می‌آیند.",
      },
      {
        scene: "کارگاه ترجمهٔ اصطلاحات", partner: "مترجم فرهنگی",
        prompt: "بگو ضرب‌المثل با ترجمهٔ تحت‌اللفظی نیرویش را از دست می‌دهد.",
        question: "Should we translate it literally?",
        questionFa: "تحت‌اللفظی ترجمه کنیم؟",
        accepted: [
          "The proverb loses its force in translation.",
          "A literal translation kills the proverb.",
        ],
        note: "lose its force: ضمیر ملکی its به proverb برمی‌گردد و حرف تعریف را تکرار نمی‌کند.",
      },
      {
        scene: "کارگاه ترجمهٔ اصطلاحات", partner: "مترجم فرهنگی",
        prompt: "بگو yoga وامواژه است و املایش عوض نشد.",
        question: "Is yoga a native word?",
        questionFa: "yoga واژهٔ بومی است؟",
        accepted: [
          "Yoga is a loanword that kept its spelling.",
          "It is a loanword, not a native word.",
        ],
        note: "that kept its spelling: بند موصولی تعریفی با that، نه which.",
      },
      {
        scene: "کارگاه ترجمهٔ اصطلاحات", partner: "مترجم فرهنگی",
        prompt: "بگو این عبارت ترجمهٔ ساختاری است، نه وامواژه.",
        question: "So it is borrowed?",
        questionFa: "پس وام گرفته شده؟",
        accepted: [
          "The phrase is a calque, not a loanword.",
          "It is a calque from another language.",
        ],
        note: "not a loanword در تقابل، حرف تعریف نامعین را تکرار می‌کند.",
      },
      {
        scene: "کارگاه ترجمهٔ اصطلاحات", partner: "مترجم فرهنگی",
        prompt: "بگو شوک فرهنگی سر میز شام آمد، نه در فرودگاه.",
        question: "When did it hit you?",
        questionFa: "کِی به تو زد؟",
        accepted: [
          "The culture shock came at the dinner table.",
          "It came at the dinner table, not at the airport.",
        ],
        note: "at the dinner table و at the airport: هر دو با at، برای مکان مشخص.",
      },
      {
        scene: "کارگاه ترجمهٔ اصطلاحات", partner: "مترجم فرهنگی",
        prompt: "بگو اصطلاح سخت‌ترین بخش فرهنگ برای قرض گرفتن است.",
        question: "Why is it hard to translate?",
        questionFa: "چرا ترجمه‌اش سخت است؟",
        accepted: [
          "An idiom is the hardest part of a culture to borrow.",
          "Idioms resist translation.",
        ],
        note: "the hardest part to borrow: مصدر پس از صفت عالی می‌آید، بدون for.",
      },
    ],
  },
];
