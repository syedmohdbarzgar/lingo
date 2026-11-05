// v58 content — C2 syllabus topics 21 (meaning-based translation) and 22
// (diplomatic communication). Read by `v58_c2_batch.mjs`; never run on its own.
export default [
  {
    lesson: {
      id: "c2.translation.lesson-01",
      order: 184,
      title: "Meaning-based Translation",
      titleFa: "ترجمهٔ مفهومی",
      topic: "Translation",
      tip: [
        "## قاعده",
        "وقتی کنشگر مهم نیست، مجهول را با فعل کمکی بساز: modal + be + past participle.",
        "The pun cannot be reproduced in Persian. — جناس را نمی‌توان در فارسی بازتولید کرد.",
        "با have to و must هم همین ساختار می‌آید: The joke has to be explained in a footnote / Meaning must be kept.",
        "## جدول",
        "The pun cannot be reproduced. — جناس بازتولیدشدنی نیست.",
        "The joke has to be explained in a footnote. — شوخی باید در پانویس توضیح داده شود.",
        "The title must be kept in the original. — عنوان باید به زبان اصلی بماند.",
        "## مثال‌ها",
        "A transliteration keeps the sound and loses the sense. — آوانویسی صدا را نگه می‌دارد و معنا را از دست می‌دهد.",
        "Domestication makes the text familiar to the reader. — بومی‌سازی متن را برای خواننده آشنا می‌کند.",
        "Foreignization keeps the source culture visible. — بیگانه‌سازی فرهنگ مبدأ را دیدنی نگه می‌دارد.",
        "## خطاهای رایج",
        "The pun cannot reproduce ✗ — مجهول لازم است: cannot be reproduced.",
        "The joke has to explained ✗ — be جا افتاده: has to be explained.",
        "literalism را با «سواد» اشتباه نگیر؛ ریشهٔ آن literal («لفظی») است.",
      ].join("\n"),
    },
    grammarNode: "grammar.passive-with-modals",
    vocab: {
      id: "vocab.meaning-translation",
      title: "Meaning-based Translation",
      titleFa: "ترجمهٔ مفهومی",
      prerequisites: ["vocab.semantic-nuance"],
    },
    words: [
      {
        slug: "transliteration", w: "transliteration", fa: "آوانویسی", ipa: "/ˌtrænzlɪtəˈreɪʃn/", pos: "noun",
        def: "writing a word's sounds in another alphabet instead of translating it",
        ex: [
          ["A transliteration keeps the sound and loses the sense.", "آوانویسی صدا را نگه می‌دارد و معنا را از دست می‌دهد."],
          ["The name needs a transliteration, not a translation.", "این نام آوانویسی می‌خواهد، نه ترجمه."],
        ],
        col: ["a Latin transliteration", "the transliteration of a name"],
        note: "transliteration صدا را با الفبای دیگر می‌نویسد؛ translation معنا را منتقل می‌کند.",
      },
      {
        slug: "literalism", w: "literalism", fa: "ترجمهٔ تحت‌اللفظی", ipa: "/ˈlɪtərəlɪzəm/", pos: "noun",
        def: "translating word by word and ignoring how the target language speaks",
        ex: [
          ["Literalism produces sentences no native speaker would say.", "ترجمهٔ تحت‌اللفظی جمله‌هایی می‌سازد که هیچ گویشور بومی نمی‌گوید."],
          ["The literalism is visible in every other line.", "تحت‌اللفظی بودن در هر دو خط یک‌بار دیده می‌شود."],
        ],
        col: ["strict literalism", "avoid literalism"],
        note: "literalism از literal («لفظی») می‌آید؛ متضادش free یا idiomatic translation است.",
      },
      {
        slug: "domestication", w: "domestication", fa: "بومی‌سازی", ipa: "/dəˌmestɪˈkeɪʃn/", pos: "noun",
        def: "a translation strategy that makes the text read as if written for the target culture",
        ex: [
          ["Domestication makes the text familiar to the reader.", "بومی‌سازی متن را برای خواننده آشنا می‌کند."],
          ["Domestication hides where the story came from.", "بومی‌سازی پنهان می‌کند که داستان از کجا آمده است."],
        ],
        col: ["a strategy of domestication", "domestication of the setting"],
        note: "در مطالعات ترجمه، domestication یعنی متن به خواننده نزدیک می‌شود؛ متضادش foreignization.",
      },
      {
        slug: "foreignization", w: "foreignization", fa: "بیگانه‌سازی", ipa: "/ˌfɒrənaɪˈzeɪʃn/", pos: "noun",
        def: "a translation strategy that keeps the source culture visible to the reader",
        ex: [
          ["Foreignization keeps the source culture visible.", "بیگانه‌سازی فرهنگ مبدأ را دیدنی نگه می‌دارد."],
          ["Foreignization asks the reader to travel.", "بیگانه‌سازی از خواننده می‌خواهد که سفر کند."],
        ],
        col: ["a foreignizing translation", "foreignization of names"],
        note: "foreignization با افشای تفاوت فرهنگی همراه است؛ متضادش domestication است.",
      },
      {
        slug: "adaptation", w: "adaptation", fa: "اقتباس", ipa: "/ˌædæpˈteɪʃn/", pos: "noun",
        def: "a free version of a text reshaped for a new audience or medium",
        ex: [
          ["This version is an adaptation, not a translation.", "این نسخه اقتباس است، نه ترجمه."],
          ["The adaptation moves the story to a modern city.", "اقتباس داستان را به شهری امروزی می‌برد."],
        ],
        col: ["a stage adaptation", "an adaptation of a novel"],
        note: "adaptation آزادتر از ترجمه است؛ faithfulness در آن به اثر مبدأ سنجیده می‌شود، نه به واژه‌ها.",
      },
      {
        slug: "interpreter", w: "interpreter", fa: "مترجم شفاهی", ipa: "/ɪnˈtɜːprɪtə(r)/", pos: "noun",
        def: "a person who translates speech from one language to another as it happens",
        ex: [
          ["An interpreter translates speech; a translator works on text.", "مترجم شفاهی گفتار را ترجمه می‌کند؛ مترجم کتبی روی متن کار می‌کند."],
          ["The interpreter kept pace with the speaker.", "مترجم شفاهی هم‌گام با گوینده پیش رفت."],
        ],
        col: ["a court interpreter", "work as an interpreter"],
        note: "interpreter برای گفتار و translator برای نوشتار؛ در فارسی اغلب هر دو «مترجم» خوانده می‌شوند.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "literalism", gloss: "ترجمهٔ تحت‌اللفظی",
        options: ["ترجمهٔ تحت‌اللفظی", "آوانویسی", "بومی‌سازی", "اقتباس"],
        note: "literalism ترجمهٔ کلمه‌به‌کلمه است که به شیوهٔ زبان مقصد توجه نمی‌کند.",
      },
      {
        k: "mcq", word: "domestication", gloss: "بومی‌سازی",
        options: ["بومی‌سازی", "بیگانه‌سازی", "آوانویسی", "مترجم شفاهی"],
        note: "domestication متن را به فرهنگ خواننده نزدیک می‌کند.",
      },
      {
        k: "mcq", word: "foreignization", gloss: "بیگانه‌سازی",
        options: ["بیگانه‌سازی", "اقتباس", "بومی‌سازی", "ترجمهٔ تحت‌اللفظی"],
        note: "foreignization تفاوت فرهنگ مبدأ را در متن نگه می‌دارد.",
      },
      {
        k: "blank", sentence: "The pun cannot ___ in Persian.",
        accepted: ["be reproduced", "be rendered"], skill: "GRAMMAR",
        note: "پس از modal، مجهول با be + p.p. ساخته می‌شود.",
      },
      {
        k: "blank", sentence: "The joke has to ___ in a footnote.",
        accepted: ["be explained", "be glossed"], skill: "GRAMMAR",
        note: "has to be + p.p.: ناچاری به انجام کاری در ساختار مجهول.",
      },
      {
        k: "recall", prompt: "بومی‌سازی", accepted: ["domestication"],
        note: "بومی‌سازی domestication است.",
      },
      {
        k: "tr", prompt: "طنز کلامی را نمی‌توان عیناً بازتولید کرد.",
        accepted: [
          "the pun cannot be reproduced exactly",
          "a pun cannot be reproduced exactly",
        ],
        note: "cannot be + p.p. با قید exactly در پایان.",
      },
      {
        k: "listen", text: "A transliteration keeps the sound and loses the sense.",
        note: "به transliteration گوش کنید: آوانویسی در برابر ترجمه.",
      },
      {
        k: "prod", prompt: "بگو ترجمهٔ تحت‌اللفظی جمله‌هایی می‌سازد که هیچ گویشور بومی نمی‌گوید.",
        mustUse: ["literalism"], minWords: 7,
        accepted: ["Literalism produces sentences no native speaker would say."],
        hint: "با Literalism produces… شروع کن.",
        note: "no native speaker would say: بند موصولی بدون that، با would برای فرض.",
      },
    ],
    dialogue: [
      {
        scene: "جلسهٔ ترجمهٔ متن ادبی", partner: "سرویراستار ترجمه",
        prompt: "بگو طنز کلامی را نمی‌توان عیناً بازتولید کرد.",
        question: "Can you keep the pun?",
        questionFa: "می‌توانی جناس را نگه داری؟",
        accepted: [
          "The pun cannot be reproduced exactly.",
          "It cannot be reproduced, only explained.",
        ],
        note: "cannot be + p.p. ناتوانی ساختاری را نشان می‌دهد، نه ناتوانی مترجم.",
      },
      {
        scene: "جلسهٔ ترجمهٔ متن ادبی", partner: "سرویراستار ترجمه",
        prompt: "بگو نام را آوانویسی می‌کنیم، نه ترجمه.",
        question: "Do we translate the name?",
        questionFa: "نام را ترجمه می‌کنیم؟",
        accepted: [
          "We transliterate the name, not translate it.",
          "The name is transliterated.",
        ],
        note: "transliterate فعل است: آوانویسی کردن؛ نه با ترجمه یکی است.",
      },
      {
        scene: "جلسهٔ ترجمهٔ متن ادبی", partner: "سرویراستار ترجمه",
        prompt: "بگو ترجمهٔ تحت‌اللفظی طبیعی به گوش نمی‌آید.",
        question: "Why not translate it word for word?",
        questionFa: "چرا کلمه‌به‌کلمه ترجمه نکنیم؟",
        accepted: [
          "Literalism does not sound natural.",
          "A literal version sounds foreign.",
        ],
        note: "sound + صفت یعنی «به گوش رسیدن، به نظر آمدن».",
      },
      {
        scene: "جلسهٔ ترجمهٔ متن ادبی", partner: "سرویراستار ترجمه",
        prompt: "بگو بومی‌سازی متن را برای خوانندهٔ فارسی آشنا می‌کند.",
        question: "Should we localise it?",
        questionFa: "بومی‌اش کنیم؟",
        accepted: [
          "Domestication makes the text familiar to the reader.",
          "Domesticating it makes it feel local.",
        ],
        note: "make + مفعول + صفت: makes the text familiar.",
      },
      {
        scene: "جلسهٔ ترجمهٔ متن ادبی", partner: "سرویراستار ترجمه",
        prompt: "بگو بیگانه‌سازی فرهنگ مبدأ را دیدنی نگه می‌دارد.",
        question: "And the other option?",
        questionFa: "و گزینهٔ دیگر؟",
        accepted: [
          "Foreignization keeps the source culture visible.",
          "Foreignization leaves the text foreign.",
        ],
        note: "keep + مفعول + صفت: keeps the culture visible.",
      },
      {
        scene: "جلسهٔ ترجمهٔ متن ادبی", partner: "سرویراستار ترجمه",
        prompt: "بگو این نسخه اقتباس است، نه ترجمه.",
        question: "Is this still a translation?",
        questionFa: "این هنوز ترجمه است؟",
        accepted: [
          "This version is an adaptation, not a translation.",
          "It is an adaptation of the story.",
        ],
        note: "an adaptation of: حرف اضافهٔ ثابت of است.",
      },
    ],
  },
  {
    lesson: {
      id: "c2.diplomacy.lesson-01",
      order: 185,
      title: "Diplomatic Communication",
      titleFa: "ارتباطات دیپلماتیک",
      topic: "Communication",
      tip: [
        "## قاعده",
        "برای فرضی محترمانه و دور از قطعیت، were to + مصدر ساده به کار ببر: If we were to raise the issue now, it would look like pressure.",
        "در جملهٔ نتیجه would یا could می‌آید: If the wording were softer, the other side could accept it.",
        "با وارونگی، if حذف می‌شود و were به آغاز جمله می‌آید: Were the talks to fail, both sides would lose.",
        "## جدول",
        "If we were to raise the issue now, it would look like pressure. — اگر حالا موضوع را مطرح کنیم، فشار به نظر می‌رسد.",
        "If the wording were softer, the other side could accept it. — اگر عبارت نرم‌تر بود، طرف مقابل می‌توانست بپذیرد.",
        "Were the talks to fail, both sides would lose. — اگر مذاکرات شکست بخورد، هر دو طرف بازنده‌اند.",
        "## مثال‌ها",
        "Tact is the difference between a warning and an insult. — ظرافت در گفتار تفاوت میان هشدار و توهین است.",
        "A non-paper lets an idea circulate without a signature. — سند غیررسمی می‌گذارد ایده بی‌امضا بچرخد.",
        "The backchannel lets both sides talk without a record. — کانال غیررسمی می‌گذارد دو طرف بی‌سند گفتگو کنند.",
        "## خطاهای رایج",
        "If we would raise the issue ✗ — در بند شرط would نمی‌آید: If we raised / If we were to raise.",
        "Were the talks to failing ✗ — پس از to مصدر ساده می‌آید: to fail.",
        "tact را با «تاکت» در موسیقی اشتباه نگیر؛ اینجا «ظرافت در رفتار» است.",
      ].join("\n"),
    },
    grammarNode: "grammar.tentative-conditionals",
    vocab: {
      id: "vocab.diplomatic-communication",
      title: "Diplomatic Communication",
      titleFa: "ارتباطات دیپلماتیک",
      prerequisites: ["vocab.intercultural"],
    },
    words: [
      {
        slug: "tact", w: "tact", fa: "ظرافت در گفتار", ipa: "/tækt/", pos: "noun",
        def: "the skill of raising a difficult subject without giving offence",
        ex: [
          ["Tact is the difference between a warning and an insult.", "ظرافت در گفتار تفاوت میان هشدار و توهین است."],
          ["She raised the delay with tact.", "او تأخیر را با ظرافت مطرح کرد."],
        ],
        col: ["show tact", "a matter of tact"],
        note: "tact غیرقابل‌شمارش است؛ صفتش tactful و متضادش tactless.",
      },
      {
        slug: "broach", w: "broach", fa: "مطرح کردن موضوع حساس", ipa: "/brəʊtʃ/", pos: "verb",
        def: "to bring up a sensitive subject for discussion",
        ex: [
          ["He waited for the coffee to arrive before he broached the delay.", "صبر کرد تا قهوه برسد، بعد تأخیر را مطرح کرد."],
          ["Nobody wanted to broach the question of money.", "هیچ‌کس نمی‌خواست موضوع پول را باز کند."],
        ],
        col: ["broach a subject", "broach the issue with someone"],
        note: "broach با موضوع می‌آید: broach the subject with a colleague، نه broach a colleague.",
      },
      {
        slug: "non-paper", w: "non-paper", fa: "سند غیررسمی", ipa: "/ˌnɒn ˈpeɪpə(r)/", pos: "noun",
        def: "an informal document that carries no official status or signature",
        ex: [
          ["A non-paper lets an idea circulate without a signature.", "سند غیررسمی می‌گذارد ایده بی‌امضا بچرخد."],
          ["The delegation circulated a non-paper before the meeting.", "هیئت پیش از جلسه یک سند غیررسمی پخش کرد."],
        ],
        col: ["circulate a non-paper", "an unsigned non-paper"],
        note: "non-paper را هیچ طرفی نمی‌تواند به‌عنوان تعهد نقل کند؛ همین کارکردش است.",
      },
      {
        slug: "backchannel", w: "backchannel", fa: "کانال غیررسمی", ipa: "/ˈbæk tʃænl/", pos: "noun",
        def: "a private, informal route of contact alongside the official one",
        ex: [
          ["The backchannel lets both sides talk without a record.", "کانال غیررسمی می‌گذارد دو طرف بی‌سند گفتگو کنند."],
          ["They used a backchannel to test the proposal.", "برای آزمودن پیشنهاد از کانال غیررسمی استفاده کردند."],
        ],
        col: ["open a backchannel", "through the backchannel"],
        note: "backchannel در کنار مسیر رسمی کار می‌کند و تعهدی نمی‌سازد.",
      },
      {
        slug: "deniability", w: "deniability", fa: "قابلیت انکار", ipa: "/dɪˌnaɪəˈbɪləti/", pos: "noun",
        def: "the possibility of denying that something was said or agreed",
        ex: [
          ["Both sides need deniability.", "هر دو طرف به قابلیت انکار نیاز دارند."],
          ["The vague wording gives the minister deniability.", "عبارت مبهم به وزیر امکان انکار می‌دهد."],
        ],
        col: ["plausible deniability", "a measure of deniability"],
        note: "deniability غیرقابل‌شمارش است؛ عبارت رایج plausible deniability («امکان انکار باورپذیر»).",
      },
      {
        slug: "mediator", w: "mediator", fa: "میانجی", ipa: "/ˈmiːdieɪtə(r)/", pos: "noun",
        def: "a neutral person who helps two sides reach an agreement",
        ex: [
          ["The mediator kept both delegations in the room.", "میانجی هر دو هیئت را در اتاق نگه داشت."],
          ["A mediator can say what neither side can.", "میانجی می‌تواند چیزی بگوید که هیچ‌یک از دو طرف نمی‌تواند."],
        ],
        col: ["act as a mediator", "a neutral mediator"],
        note: "mediator بی‌طرف است؛ negotiator نمایندهٔ یکی از طرف‌هاست.",
      },
    ],
    exercises: [
      {
        k: "mcq", word: "tact", gloss: "ظرافت در گفتار",
        options: ["ظرافت در گفتار", "قابلیت انکار", "میانجی", "کانال غیررسمی"],
        note: "tact مهارت گفتن حرف سخت بدون رنجاندن است.",
      },
      {
        k: "mcq", word: "broach", gloss: "مطرح کردن موضوع حساس",
        options: ["مطرح کردن موضوع حساس", "سند غیررسمی", "میانجی", "قابلیت انکار"],
        note: "broach یعنی موضوعی حساس را برای گفتگو باز کردن.",
      },
      {
        k: "mcq", word: "deniability", gloss: "قابلیت انکار",
        options: ["قابلیت انکار", "کانال غیررسمی", "ظرافت در گفتار", "سند غیررسمی"],
        note: "deniability یعنی امکان انکار اینکه چیزی گفته یا توافق شده است.",
      },
      {
        k: "blank", sentence: "If the wording ___ softer, the other side could accept it.",
        accepted: ["were", "was"], skill: "GRAMMAR",
        note: "در شرط غیرواقعی were (و در گفتار was) می‌آید؛ would در بند شرط نمی‌آید.",
      },
      {
        k: "blank", sentence: "If we were to raise it, it ___ look like pressure.",
        accepted: ["would", "could"], skill: "GRAMMAR",
        note: "در جملهٔ نتیجه would یا could + مصدر ساده.",
      },
      {
        k: "recall", prompt: "ظرافت در گفتار", accepted: ["tact"],
        note: "ظرافت در گفتار tact است.",
      },
      {
        k: "tr", prompt: "او صبر کرد تا قهوه برسد، بعد تأخیر را مطرح کرد.",
        accepted: [
          "he waited for the coffee before he broached the delay",
          "he waited until the coffee arrived before broaching the delay",
        ],
        note: "broach the delay: موضوع تأخیر را باز کردن؛ before + حال ساده.",
      },
      {
        k: "listen", text: "A non-paper lets an idea circulate without a signature.",
        note: "به non-paper گوش کنید: سندی بی‌امضا و غیررسمی.",
      },
      {
        k: "prod", prompt: "بگو ظرافت در گفتار تفاوت میان هشدار و توهین است.",
        mustUse: ["tact"], minWords: 8,
        accepted: ["Tact is the difference between a warning and an insult."],
        hint: "با Tact is the difference between… شروع کن.",
        note: "the difference between A and B: ساختار ثابت مقایسهٔ دو چیز.",
      },
    ],
    dialogue: [
      {
        scene: "مذاکرهٔ محرمانه", partner: "کاردار سفارت",
        prompt: "بگو بیانیهٔ رسمی فقط آنچه هر دو طرف می‌پذیرند می‌گوید.",
        question: "Can we say more in the statement?",
        questionFa: "می‌توانیم در بیانیه بیشتر بگوییم؟",
        accepted: [
          "The statement says only what both sides can accept.",
          "We can state only what both sides accept.",
        ],
        note: "only what both sides can accept: بند موصولی به‌عنوان مفعول؛ در پاسخ گفتاری communiqué را با statement عوض کن تا موتور گفتار آن را بشنود.",
      },
      {
        scene: "مذاکرهٔ محرمانه", partner: "کاردار سفارت",
        prompt: "بگو موضوع را خصوصی مطرح کنیم، نه در مطبوعات.",
        question: "Where do we raise it?",
        questionFa: "کجا مطرحش کنیم؟",
        accepted: [
          "We should broach it in private, not in the press.",
          "Let us broach the issue privately.",
        ],
        note: "in private در برابر in the press: تقابل دو بافت.",
      },
      {
        scene: "مذاکرهٔ محرمانه", partner: "کاردار سفارت",
        prompt: "بگو ظرافت تفاوت میان هشدار و توهین است.",
        question: "Was that too blunt?",
        questionFa: "خیلی صریح بود؟",
        accepted: [
          "Tact is the difference between a warning and an insult.",
          "That was blunt, not tactful.",
        ],
        note: "blunt («صریح و بی‌پرده») در برابر tactful.",
      },
      {
        scene: "مذاکرهٔ محرمانه", partner: "کاردار سفارت",
        prompt: "بگو سند غیررسمی امضا ندارد.",
        question: "Is the paper official?",
        questionFa: "این سند رسمی است؟",
        accepted: [
          "The non-paper carries no signature.",
          "It is a non-paper, so it is not official.",
        ],
        note: "carry a signature: داشتن امضا؛ با no منفی می‌شود.",
      },
      {
        scene: "مذاکرهٔ محرمانه", partner: "کاردار سفارت",
        prompt: "بگو کانال غیررسمی اجازهٔ گفتگو می‌دهد.",
        question: "How do we talk without a record?",
        questionFa: "بی‌سند چطور حرف بزنیم؟",
        accepted: [
          "The backchannel lets both sides talk.",
          "We can use the backchannel.",
        ],
        note: "let + مفعول + مصدر بدون to: lets both sides talk.",
      },
      {
        scene: "مذاکرهٔ محرمانه", partner: "کاردار سفارت",
        prompt: "بگو قابلیت انکار برای هر دو طرف لازم است.",
        question: "Why the vague wording?",
        questionFa: "چرا عبارت مبهم؟",
        accepted: [
          "Both sides need deniability.",
          "The wording gives both sides deniability.",
        ],
        note: "give + مفعول + مفعول: gives both sides deniability.",
      },
    ],
  },
];
