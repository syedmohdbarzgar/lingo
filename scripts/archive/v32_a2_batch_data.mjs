/**
 * Authored source for bundle v32 — the next ten A2 syllabus topics, topics 8, 9,
 * 10, 12, 13 and 15–19 of docs/curriculum.md: Buying Clothes, Returning an Item,
 * Payments and Payment Methods, Describing Symptoms, At the Pharmacy, Childhood
 * Memories, Celebrations and Special Occasions, Invitations, Phone Conversations
 * and Writing Simple Emails.
 *
 * Every field here is hand-written content; the runner only assembles the JSON
 * shapes (lesson, six words, nine exercises, the vocabulary node). Same tuple
 * order as the v29 batch:
 *   [word, fa, definition, ipa, pos, en1, fa1, en2, fa2, collocation1,
 *    collocation2, usageNote]
 *
 * `listenWord` and `produceWord` index that tuple, so the guided listening drill
 * is one of the lesson's own example sentences and the free-production exemplar
 * is another — the anchors and the word floor are derived from the sentence the
 * author actually wrote, which is what keeps ProductionRecallContentTest honest.
 */

export const lessons = [
  {
    slug: "buying-clothes",
    topic: "Shopping",
    title: "Buying Clothes",
    titleFa: "خرید لباس و انتخاب اندازه",
    minutes: 7,
    rule:
      "برای اندازهٔ لباس از What size…? و برای قیمت از How much is/are…? استفاده می‌کنیم.\n" +
      "فعل try on جداشدنی است: try it on درست است، نه try on it.\n" +
      "برای توصیف اندازه از too + صفت استفاده می‌کنیم: too tight، too big، a little loose.\n" +
      "برای تعویض کالا: Can I exchange it for a larger size?",
    table: [
      ["What size are these shoes?", "این کفش‌ها چه اندازه‌ای هستند؟"],
      ["How much is this jacket?", "این کاپشن چند است؟"],
      ["Can I try it on?", "می‌توانم پرو کنم؟"],
      ["It is too tight for me.", "برای من خیلی تنگ است."],
    ],
    examples: [
      ["Do you have this shirt in my size?", "این پیراهن را در اندازهٔ من دارید؟"],
      ["The fitting room is next to the shoes.", "اتاق پرو کنار کفش‌هاست."],
      ["Can I exchange this shirt for a larger one?", "می‌توانم این پیراهن را با یک اندازهٔ بزرگ‌تر تعویض کنم؟"],
    ],
    errors: [
      "try on جدا می‌شود: «try it on» درست است و «try on it» غلط.",
      "برای اندازه از How much استفاده نکن؛ اندازه با What size پرسیده می‌شود.",
      "پیش از صفت اندازه too می‌آید: «this is too big»، نه «this is big too».",
    ],
    recallWord: 4,
    listenWord: 3,
    produceWord: 0,
    meaningWords: [0, 1, 3],
    listenNote: "jeans /dʒiːnz/ همیشه جمع است و tight /taɪt/ با /t/ پایانی واضح گفته می‌شود",
    words: [
      ["size", "اندازه", "how big or small something is", "/saɪz/", "noun", "What size are these shoes?", "این کفش‌ها چه اندازه‌ای هستند؟", "Do you have this shirt in my size?", "این پیراهن را در اندازهٔ من دارید؟", "what size", "in my size", "size برای اندازهٔ لباس و کفش به کار می‌رود و با in می‌آید: in my size."],
      ["fitting room", "اتاق پرو", "a small room where you try clothes on", "/ˈfɪtɪŋ ruːm/", "noun", "The fitting room is next to the shoes.", "اتاق پرو کنار کفش‌هاست.", "There are two fitting rooms here.", "اینجا دو اتاق پرو هست.", "in the fitting room", "a free fitting room", "fitting room اتاقی است که در فروشگاه لباس برای پرو کردن هست."],
      ["try on", "پرو کردن", "to put clothes on to see if they fit", "/traɪ ɒn/", "verb", "Can I try on this jacket?", "می‌توانم این کاپشن را پرو کنم؟", "She is trying on a dress.", "او دارد یک پیراهن را پرو می‌کند.", "try on a jacket", "try it on", "try on جداشدنی است: try it on؛ ضمیر میان فعل و on می‌آید."],
      ["tight", "تنگ", "fitting closely, not loose", "/taɪt/", "adjective", "These jeans are too tight.", "این شلوار جین خیلی تنگ است.", "The shirt feels tight around my shoulders.", "پیراهن دور شانه‌هایم تنگ است.", "too tight", "a tight fit", "tight متضاد loose است؛ برای اندازهٔ کوچک‌تر از تن به کار می‌رود."],
      ["loose", "گشاد", "not tight, with extra room", "/luːs/", "adjective", "This dress is a little loose.", "این پیراهن کمی گشاد است.", "Do you have a loose fit in blue?", "مدل گشادش را در آبی دارید؟", "a loose fit", "too loose", "loose صفت است؛ loose fit یعنی برشی که گشاد می‌نشیند."],
      ["exchange", "تعویض کردن", "to give one thing and get another", "/ɪksˈtʃeɪndʒ/", "verb", "Can I exchange this shirt for a larger one?", "می‌توانم این پیراهن را با یک اندازهٔ بزرگ‌تر تعویض کنم؟", "I want to exchange these shoes.", "می‌خواهم این کفش‌ها را تعویض کنم.", "exchange something for", "exchange it", "exchange با for می‌آید وقتی می‌گوییم با چه چیزی عوض می‌شود."],
    ],
    blanks: [
      ["This jacket is ___ big for me.", ["too", "very"], "هر دو درست‌اند؛ too یعنی «بیش از حد» و very فقط مقدار را شدید می‌کند.", "GRAMMAR"],
      ["Can I try ___ on, please?", ["it", "this"], "با فعل جداشدنی try on ضمیر و اشاره میان فعل و on می‌آیند: try it on، try this on.", "GRAMMAR"],
    ],
    translation: [
      "این شلوار برای من خیلی تنگ است.",
      ["these trousers are too tight for me", "these pants are too tight for me"],
      "trousers در انگلیسی بریتانیایی همیشه جمع است؛ too tight پیش از for me می‌آید.",
    ],
    productionPrompt: "بپرس این کفش‌ها چه اندازه‌ای هستند.",
    productionHint: "پرسش اندازه با What size + are + اسم جمع ساخته می‌شود.",
  },
  {
    slug: "returns",
    topic: "Shopping",
    title: "Returning an Item",
    titleFa: "مرجوع کردن کالا",
    minutes: 7,
    rule:
      "برای برگرداندن کالا از I'd like to return… یا I'd like a refund استفاده می‌کنیم.\n" +
      "برای دلیل مرجوعی صفت را با be می‌آوریم: The zip is broken، The battery is faulty.\n" +
      "برای درخواست مؤدبانه: Could I have a refund, please?\n" +
      "guarantee دورهٔ ضمانت است و replace یعنی کالا را با کالای سالم عوض کردن.",
    table: [
      ["I'd like to return these shoes.", "می‌خواهم این کفش‌ها را برگردانم."],
      ["Do you have the receipt?", "رسید را دارید؟"],
      ["The battery is faulty.", "باتری معیوب است."],
      ["Could I have a refund, please?", "می‌توانم بازپرداخت بگیرم؟"],
    ],
    examples: [
      ["I bought these shoes yesterday and I want to return them.", "این کفش‌ها را دیروز خریدم و می‌خواهم برگردانم."],
      ["The refund takes five days.", "بازپرداخت پنج روز طول می‌کشد."],
      ["We will replace the item for free.", "ما کالا را رایگان جایگزین می‌کنیم."],
    ],
    errors: [
      "refund اسم است و با a می‌آید: «a refund»، نه «refund» تنها در جملهٔ درخواست.",
      "برای معیوب بودن از be استفاده کن: «the zip is broken»، نه «the zip has broken» در این معنی.",
      "receipt با p بی‌صدا نوشته می‌شود: /rɪˈsiːt/.",
    ],
    recallWord: 0,
    listenWord: 0,
    produceWord: 2,
    meaningWords: [0, 2, 3],
    listenNote: "receipt /rɪˈsiːt/ با p بی‌صدا و jacket /ˈdʒækɪt/ با تکیه روی هجای اول",
    words: [
      ["receipt", "رسید", "a paper that shows you paid", "/rɪˈsiːt/", "noun", "Do you have the receipt for this jacket?", "رسید این کاپشن را دارید؟", "Keep the receipt in case you return it.", "رسید را نگه دار در صورتی که بخواهی برگردانی.", "have the receipt", "keep the receipt", "receipt با p بی‌صدا تلفظ می‌شود: /rɪˈsiːt/."],
      ["refund", "بازپرداخت", "money given back for something returned", "/ˈriːfʌnd/", "noun", "I would like a refund, please.", "می‌خواهم بازپرداخت بگیرم.", "The refund takes five days.", "بازپرداخت پنج روز طول می‌کشد.", "ask for a refund", "a full refund", "refund اسم است؛ فعل refund تکیه‌اش روی هجای دوم می‌رود."],
      ["faulty", "معیوب", "not working correctly", "/ˈfɔːlti/", "adjective", "The battery is faulty.", "باتری معیوب است.", "This is the second faulty item.", "این دومین کالای معیوب است.", "a faulty item", "the battery is faulty", "faulty برای کالایی است که از ابتدا درست کار نمی‌کند."],
      ["guarantee", "ضمانت", "a promise to repair or replace a product", "/ˌɡærənˈtiː/", "noun", "The watch has a two-year guarantee.", "این ساعت دو سال ضمانت دارد.", "The guarantee does not cover water damage.", "ضمانت آسیب آبی را پوشش نمی‌دهد.", "a two-year guarantee", "under guarantee", "guarantee دورهٔ ضمانت است؛ under guarantee یعنی هنوز ضمانت دارد."],
      ["replace", "جایگزین کردن", "to give a new thing instead of a broken one", "/rɪˈpleɪs/", "verb", "Can you replace the broken screen?", "می‌توانید صفحهٔ شکسته را جایگزین کنید؟", "We will replace the item for free.", "ما کالا را رایگان جایگزین می‌کنیم.", "replace the item", "replace it for free", "replace یعنی کالای نو جای کالای خراب؛ با with هم می‌آید: replace A with B."],
      ["purchase", "خرید", "something you have bought", "/ˈpɜːtʃəs/", "noun", "Keep your purchase receipt.", "رسید خریدتان را نگه دارید.", "You can return any purchase within thirty days.", "هر خریدی را تا سی روز می‌توانید برگردانید.", "a purchase receipt", "return a purchase", "purchase اسم رسمی‌تر «خرید» است؛ در گفتار روزمره buy رایج‌تر است."],
    ],
    blanks: [
      ["I'd like to ___ these shoes.", ["return", "exchange"], "return یعنی برگرداندن و exchange یعنی تعویض؛ هر دو با I'd like to درست‌اند.", "GRAMMAR"],
      ["The zip is ___.", ["broken", "faulty"], "هر دو صفت‌اند و با is می‌آیند؛ broken از کار افتاده و faulty معیوب است.", "GRAMMAR"],
    ],
    translation: [
      "این کفش‌ها را دیروز خریدم و می‌خواهم برگردانم.",
      ["i bought these shoes yesterday and i want to return them", "i bought these shoes yesterday and i would like to return them"],
      "زمان گذشتهٔ buy بی‌قاعده است (bought) و ضمیر them به shoes برمی‌گردد.",
    ],
    productionPrompt: "بگو باتری معیوب است.",
    productionHint: "برای توصیف کالای خراب از be + faulty استفاده کن.",
  },
  {
    slug: "payments",
    topic: "Shopping",
    title: "Payments and Payment Methods",
    titleFa: "پرداخت و روش‌های پرداخت",
    minutes: 7,
    rule:
      "روش پرداخت با by یا in می‌آید: pay by card، pay in cash.\n" +
      "برای پرسیدن مبلغ: How much is it? و برای جمع کل: What is the total?\n" +
      "change پول خردی است که پس از پرداخت برمی‌گردد؛ Keep the change یعنی پول خرد مال شما.\n" +
      "برای درخواست مؤدبانه: Could I pay by card?",
    table: [
      ["Can I pay by card?", "می‌توانم با کارت پرداخت کنم؟"],
      ["I paid in cash.", "با پول نقد پرداخت کردم."],
      ["The total is twelve dollars.", "جمع کل دوازده دلار است."],
      ["Here is your change.", "این هم پول خرد شما."],
    ],
    examples: [
      ["Do you take card payments?", "پرداخت با کارت قبول می‌کنید؟"],
      ["Please pay in cash at the desk.", "لطفاً در باجه با پول نقد پرداخت کنید."],
      ["My wallet is in my bag.", "کیف پولم در کیفم است."],
    ],
    errors: [
      "با کارت «by card» است، نه «with card» بدون حرف تعریف در این ساختار ثابت.",
      "change غیرقابل‌شمارش است: «some change»، نه «a change» به معنی پول خرد.",
      "total اسم است؛ برای پرسیدن جمع کل از What is the total? استفاده کن.",
    ],
    recallWord: 0,
    listenWord: 3,
    produceWord: 1,
    meaningWords: [0, 3, 4],
    listenNote: "total /ˈtəʊtl/ با /əʊ/ و twelve /twelv/ با /tw/ آغاز می‌شود",
    words: [
      ["cash", "پول نقد", "money in coins and notes", "/kæʃ/", "noun", "I paid in cash at the shop.", "در فروشگاه با پول نقد پرداخت کردم.", "Do you have any cash?", "پول نقد همراهت هست؟", "pay in cash", "any cash", "cash غیرقابل‌شمارش است؛ with cash و in cash هر دو به کار می‌روند."],
      ["card", "کارت", "a small plastic card used to pay", "/kɑːd/", "noun", "Can I pay by card?", "می‌توانم با کارت پرداخت کنم؟", "My card does not work today.", "کارتم امروز کار نمی‌کند.", "pay by card", "a bank card", "card قابل‌شمارش است: a card، two cards."],
      ["change", "پول خرد", "the money you get back after paying", "/tʃeɪndʒ/", "noun", "Here is your change.", "این هم پول خرد شما.", "Keep the change, please.", "پول خرد را نگه دارید.", "keep the change", "some change", "change در این معنی غیرقابل‌شمارش است."],
      ["total", "جمع کل", "the full amount to pay", "/ˈtəʊtl/", "noun", "The total is twelve dollars.", "جمع کل دوازده دلار است.", "The total includes tax.", "جمع کل شامل مالیات است.", "the total is", "the total amount", "total اسم و صفت است؛ the total is… رایج‌ترین ساختار است."],
      ["wallet", "کیف پول", "a small case for money and cards", "/ˈwɒlɪt/", "noun", "My wallet is in my bag.", "کیف پولم در کیفم است.", "He lost his wallet on the bus.", "او کیف پولش را در اتوبوس گم کرد.", "lose a wallet", "in my wallet", "wallet مردانه و زنانه هر دو؛ purse کیف پول کوچک‌تر است."],
      ["amount", "مبلغ", "how much money something is", "/əˈmaʊnt/", "noun", "Please write the amount on the form.", "لطفاً مبلغ را روی فرم بنویسید.", "The amount is too large for cash.", "مبلغ برای پرداخت نقدی زیاد است.", "the amount of money", "a large amount", "amount با اسم غیرقابل‌شمارش می‌آید: an amount of money."],
    ],
    blanks: [
      ["Can I pay ___ card?", ["by", "with"], "by card ساختار ثابت است و with a card هم درست است؛ هر دو پذیرفته می‌شوند.", "GRAMMAR"],
      ["Do you ___ card payments?", ["take", "accept"], "take payments و accept payments هر دو درست‌اند و با سوم‌شخص جمع بدون s می‌آیند.", "GRAMMAR"],
    ],
    translation: [
      "می‌توانم با کارت پرداخت کنم؟",
      ["can i pay by card", "can i pay with a card"],
      "روش پرداخت با pay by + وسیله می‌آید و پرسش با Can I…? ساخته می‌شود.",
    ],
    productionPrompt: "بپرس آیا می‌توانی با کارت پرداخت کنی.",
    productionHint: "پرسش با Can I pay by…? ساخته می‌شود.",
  },
  {
    slug: "symptoms",
    topic: "Health",
    title: "Describing Symptoms",
    titleFa: "بیان علائم بیماری",
    minutes: 7,
    rule:
      "برای درد و بیماری از have استفاده می‌کنیم: I have a headache، She has a fever.\n" +
      "برای دردِ عضوی از pain یا hurt استفاده می‌کنیم: I have a pain in my back، My head hurts.\n" +
      "برای مدت بیماری: for two days، since yesterday.\n" +
      "dizzy صفت است و با feel می‌آید: I feel dizzy.",
    table: [
      ["I have a bad headache.", "سردرد بدی دارم."],
      ["I have a sore throat.", "گلودرد دارم."],
      ["My head hurts and I have a fever.", "سرم درد می‌کند و تب دارم."],
      ["I feel dizzy when I stand up.", "وقتی بلند می‌شوم سرگیجه دارم."],
    ],
    examples: [
      ["This cough is worse at night.", "این سرفه شب‌ها بدتر است."],
      ["The fever went down after the medicine.", "تب بعد از دارو پایین آمد."],
      ["The pain comes and goes.", "درد می‌آید و می‌رود."],
    ],
    errors: [
      "headache یک کلمه است: «head ache» و «headache pain» غلط‌اند.",
      "برای درد از have استفاده کن، نه از be: «I have a fever»، نه «I am fever».",
      "sore throat با sore می‌آید، نه با painful throat در گفتار روزمره.",
    ],
    recallWord: 3,
    listenWord: 2,
    produceWord: 0,
    meaningWords: [1, 2, 4],
    listenNote: "cough /kɒf/ با /f/ پایانی و bad /bæd/ با /æ/ کوتاه گفته می‌شود",
    words: [
      ["headache", "سردرد", "a pain in your head", "/ˈhedeɪk/", "noun", "I have a bad headache.", "سردرد بدی دارم.", "The headache started last night.", "سردرد دیشب شروع شد.", "a bad headache", "have a headache", "headache قابل‌شمارش است: a headache، two headaches."],
      ["sore throat", "گلودرد", "a painful throat", "/sɔː θrəʊt/", "noun", "I have a sore throat.", "گلودرد دارم.", "A sore throat makes it hard to speak.", "گلودرد حرف زدن را سخت می‌کند.", "a sore throat", "have a sore throat", "sore یعنی دردناک؛ sore throat ترکیب ثابت است."],
      ["cough", "سرفه", "to push air out of your throat noisily", "/kɒf/", "noun", "I have a bad cough.", "سرفهٔ بدی دارم.", "This cough is worse at night.", "این سرفه شب‌ها بدتر است.", "a bad cough", "have a cough", "cough هم اسم و هم فعل است؛ تلفظ با /f/ پایانی."],
      ["fever", "تب", "a body temperature that is too high", "/ˈfiːvə/", "noun", "She has a high fever.", "او تب شدیدی دارد.", "The fever went down after the medicine.", "تب بعد از دارو پایین آمد.", "a high fever", "have a fever", "fever قابل‌شمارش است: a fever؛ high fever شدت را نشان می‌دهد."],
      ["dizzy", "سرگیجه", "feeling that everything is turning", "/ˈdɪzi/", "adjective", "I feel dizzy when I stand up.", "وقتی بلند می‌شوم سرگیجه دارم.", "The heat made him dizzy.", "گرما او را سرگیجه‌ای کرد.", "feel dizzy", "get dizzy", "dizzy صفت است؛ با feel یا get می‌آید، نه با have."],
      ["pain", "درد", "the feeling when part of the body hurts", "/peɪn/", "noun", "I have a pain in my back.", "کمرم درد می‌کند.", "The pain comes and goes.", "درد می‌آید و می‌رود.", "a pain in my back", "sharp pain", "pain با in + عضو می‌آید: a pain in my back."],
    ],
    blanks: [
      ["I ___ a bad headache.", ["have", "have got"], "have و have got هر دو در انگلیسی روزمره درست‌اند؛ have got در گفتار بریتانیایی رایج‌تر است.", "GRAMMAR"],
      ["It ___ when I walk.", ["hurts", "aches"], "hurt و ache هر دو برای درد به کار می‌روند و با سوم‌شخص مفرد s می‌گیرند.", "GRAMMAR"],
    ],
    translation: [
      "سرم درد می‌کند و تب دارم.",
      ["my head hurts and i have a fever", "i have a headache and a fever"],
      "درد عضو با my … hurts و تب با have a fever گفته می‌شود.",
    ],
    productionPrompt: "بگو سردرد بدی داری.",
    productionHint: "برای درد و بیماری از have + a + اسم استفاده کن.",
  },
  {
    slug: "pharmacy",
    topic: "Health",
    title: "At the Pharmacy",
    titleFa: "داروخانه و خرید دارو",
    minutes: 7,
    rule:
      "برای گرفتن دارو: I'd like something for a cough.\n" +
      "برای مقدار مصرف: How often should I take it? و Take it twice a day.\n" +
      "برای زمان مصرف از after یا before استفاده می‌کنیم: after meals، before breakfast.\n" +
      "should برای توصیه به کار می‌رود: You should take it after food.",
    table: [
      ["I'd like something for a cough.", "چیزی برای سرفه می‌خواهم."],
      ["How often should I take it?", "هر چند وقت یک بار باید مصرف کنم؟"],
      ["Take it twice a day.", "روزی دو بار مصرف کنید."],
      ["You should take it after meals.", "باید بعد از غذا مصرف کنید."],
    ],
    examples: [
      ["The pharmacist explained how to use it.", "داروساز توضیح داد چطور مصرفش کنم."],
      ["This medicine is only sold with a prescription.", "این دارو فقط با نسخه فروخته می‌شود."],
      ["Do not take a double dose.", "دوز دوبرابر مصرف نکنید."],
    ],
    errors: [
      "medicine غیرقابل‌شمارش است: «some medicine»، نه «a medicine».",
      "برای مقدار مصرف از dose استفاده کن، نه از number.",
      "after meals یعنی پس از غذا؛ before meals یعنی پیش از غذا — این دو را جابه‌جا نکن.",
    ],
    recallWord: 1,
    listenWord: 4,
    produceWord: 0,
    meaningWords: [1, 3, 5],
    listenNote: "pills /pɪlz/ با /z/ پایانی و breakfast /ˈbrekfəst/ با تکیه روی هجای اول",
    words: [
      ["medicine", "دارو", "something you take when you are ill", "/ˈmedsn/", "noun", "Take this medicine twice a day.", "این دارو را روزی دو بار مصرف کنید.", "Keep the medicine away from children.", "دارو را از دسترس کودکان دور نگه دارید.", "take medicine", "some medicine", "medicine غیرقابل‌شمارش است؛ در انگلیسی بریتانیایی /ˈmedsn/ تلفظ می‌شود."],
      ["prescription", "نسخه", "a doctor's written order for medicine", "/prɪˈskrɪpʃn/", "noun", "The pharmacist needs your prescription.", "داروساز نسخهٔ شما را لازم دارد.", "This medicine is only sold with a prescription.", "این دارو فقط با نسخه فروخته می‌شود.", "with a prescription", "write a prescription", "prescription از prescribe می‌آید و با with a می‌آید."],
      ["dose", "دوز", "how much medicine you take at one time", "/dəʊs/", "noun", "The dose is one spoon for adults.", "دوز برای بزرگسالان یک قاشق است.", "Do not take a double dose.", "دوز دوبرابر مصرف نکنید.", "a double dose", "the right dose", "dose مقدار مصرف در هر نوبت است، نه مقدار کل."],
      ["pharmacist", "داروساز", "the person who prepares and sells medicine", "/ˈfɑːməsɪst/", "noun", "The pharmacist explained how to use it.", "داروساز توضیح داد چطور مصرفش کنم.", "Ask the pharmacist about side effects.", "دربارهٔ عوارض از داروساز بپرس.", "ask the pharmacist", "the pharmacist said", "pharmacist شخص داروساز است و pharmacy خود داروخانه."],
      ["pills", "قرص‌ها", "small round pieces of medicine", "/pɪlz/", "noun", "Take two pills after breakfast.", "بعد از صبحانه دو قرص مصرف کنید.", "These pills are for the pain.", "این قرص‌ها برای درد هستند.", "take pills", "two pills a day", "pill یک قرص و pills جمع آن است."],
      ["syrup", "شربت", "a sweet liquid medicine", "/ˈsɪrəp/", "noun", "The syrup is for children.", "این شربت برای کودکان است.", "Shake the syrup before you use it.", "شربت را پیش از مصرف تکان دهید.", "a bottle of syrup", "cough syrup", "syrup غیرقابل‌شمارش است: some syrup."],
    ],
    blanks: [
      ["How ___ should I take this?", ["often", "many times"], "How often پرسش رایج بسامد است و How many times هم درست است، با کمی تفاوت در تأکید.", "GRAMMAR"],
      ["You should take it ___ meals.", ["after", "before"], "هر دو با should درست‌اند؛ after یعنی بعد از غذا و before یعنی پیش از آن.", "GRAMMAR"],
    ],
    translation: [
      "این دارو را روزی دو بار مصرف کنید.",
      ["take this medicine twice a day", "you should take this medicine twice a day"],
      "بسامد با twice a day گفته می‌شود و فعل در جملهٔ دستوری ساده می‌آید.",
    ],
    productionPrompt: "بگو این دارو را روزی دو بار مصرف کن.",
    productionHint: "برای دستور مصرف از فعل ساده + مقدار بسامد استفاده کن.",
  },
  {
    slug: "childhood",
    topic: "Past",
    title: "Childhood Memories",
    titleFa: "تعریف خاطرات کودکی",
    minutes: 7,
    rule:
      "برای گذشتهٔ دور و عادت‌های کودکی از گذشتهٔ ساده استفاده می‌کنیم: I played، we lived.\n" +
      "grow up یعنی بزرگ شدن و گذشتهٔ آن grew up است.\n" +
      "برای زمان گذشته from + سن یا when I was six می‌آید.\n" +
      "برای خاطره: my best memory is… و I have happy memories of…",
    table: [
      ["I grew up in a small town.", "در شهر کوچکی بزرگ شدم."],
      ["We played football every day.", "ما هر روز فوتبال بازی می‌کردیم."],
      ["My family moved when I was six.", "خانواده‌ام وقتی شش ساله بودم اسباب‌کشی کرد."],
      ["My best memory is a trip to the sea.", "بهترین خاطره‌ام سفر به دریا است."],
    ],
    examples: [
      ["I started primary school at six.", "شش سالگی دبستان را شروع کردم."],
      ["The children are in the playground.", "بچه‌ها در زمین بازی هستند."],
      ["I have happy memories of that house.", "از آن خانه خاطرات شادی دارم."],
    ],
    errors: [
      "گذشتهٔ grow بی‌قاعده است: grew up، نه growed up.",
      "playground یک کلمه است و به معنی زمین بازی مدرسه یا محله است.",
      "memory قابل‌شمارش است: a memory، happy memories.",
    ],
    recallWord: 4,
    listenWord: 5,
    produceWord: 1,
    meaningWords: [0, 2, 4],
    listenNote: "neighbourhood /ˈneɪbəhʊd/ سه هجا دارد و grew /ɡruː/ با /uː/ بلند گفته می‌شود",
    words: [
      ["childhood", "کودکی", "the time when you are a child", "/ˈtʃaɪldhʊd/", "noun", "My childhood was very happy.", "کودکی‌ام خیلی شاد بود.", "She spent her childhood in Shiraz.", "او کودکی‌اش را در شیراز گذراند.", "in my childhood", "a happy childhood", "childhood غیرقابل‌شمارش است و با in می‌آید: in my childhood."],
      ["grow up", "بزرگ شدن", "to become an adult", "/ɡrəʊ ʌp/", "verb", "I grew up in a small town.", "در شهر کوچکی بزرگ شدم.", "He wants to be a doctor when he grows up.", "او وقتی بزرگ شود می‌خواهد دکتر شود.", "grow up in a town", "when I grow up", "گذشتهٔ grow بی‌قاعده است: grew up."],
      ["primary school", "دبستان", "the first school for young children", "/ˈpraɪməri skuːl/", "noun", "I started primary school at six.", "شش سالگی دبستان را شروع کردم.", "My primary school was near our house.", "دبستان من نزدیک خانه‌مان بود.", "start primary school", "at primary school", "primary school در انگلیسی بریتانیایی دبستان است؛ در آمریکا elementary school."],
      ["playground", "زمین بازی", "a place where children play", "/ˈpleɪɡraʊnd/", "noun", "We played football in the playground.", "در زمین بازی فوتبال بازی می‌کردیم.", "The children are in the playground.", "بچه‌ها در زمین بازی هستند.", "in the playground", "a school playground", "playground یک کلمه است، نه play ground."],
      ["memory", "خاطره", "something you remember from the past", "/ˈmeməri/", "noun", "My best memory is a trip to the sea.", "بهترین خاطره‌ام سفر به دریا است.", "I have happy memories of that house.", "از آن خانه خاطرات شادی دارم.", "a happy memory", "memories of", "جمع memory می‌شود memories؛ با of می‌آید: memories of school."],
      ["neighbourhood", "محله", "the area around your home", "/ˈneɪbəhʊd/", "noun", "I grew up in a quiet neighbourhood.", "در محلهٔ آرامی بزرگ شدم.", "The neighbourhood has a small park.", "این محله یک پارک کوچک دارد.", "a quiet neighbourhood", "in my neighbourhood", "در انگلیسی آمریکایی neighborhood نوشته می‌شود."],
    ],
    blanks: [
      ["We ___ football every day when I was young.", ["played", "would play"], "گذشتهٔ ساده played و would play هر دو برای عادت گذشته درست‌اند.", "GRAMMAR"],
      ["My family ___ to a new house when I was six.", ["moved", "went"], "moved to و went to هر دو با to می‌آیند؛ move دقیق‌تر است و go هم رایج.", "GRAMMAR"],
    ],
    translation: [
      "من در یک خانهٔ کوچک بزرگ شدم.",
      ["i grew up in a small house", "i grew up in a small home"],
      "grew up گذشتهٔ grow up است و با in + مکان می‌آید.",
    ],
    productionPrompt: "بگو در یک شهر کوچک بزرگ شدی.",
    productionHint: "از grew up in + مکان استفاده کن.",
  },
  {
    slug: "celebrations",
    topic: "Culture",
    title: "Celebrations and Special Occasions",
    titleFa: "جشن‌ها و مناسبت‌ها",
    minutes: 7,
    rule:
      "برای جشن گرفتن از celebrate + مناسبت استفاده می‌کنیم: celebrate New Year.\n" +
      "party با have یا hold می‌آید: We had a big party.\n" +
      "wedding عروسی و anniversary سالگرد است.\n" +
      "برای گذشتهٔ جشن، گذشتهٔ ساده به کار می‌رود: There were thirty guests.",
    table: [
      ["We celebrate New Year with our family.", "سال نو را با خانواده جشن می‌گیریم."],
      ["Their wedding was in June.", "عروسی‌شان در ژوئن بود."],
      ["There were thirty guests.", "سی مهمان بود."],
      ["They celebrated their tenth anniversary.", "دهمین سالگردشان را جشن گرفتند."],
    ],
    examples: [
      ["I bought a gift for my sister.", "برای خواهرم یک هدیه خریدم."],
      ["She blew out the candles on the cake.", "او شمع‌های کیک را فوت کرد."],
      ["How do you celebrate your birthday?", "تولدت را چطور جشن می‌گیری؟"],
    ],
    errors: [
      "celebrate با مفعول مستقیم می‌آید: celebrate New Year، نه celebrate for New Year.",
      "wedding عروسی است، نه همهٔ جشن‌ها؛ مهمانی party است.",
      "anniversary تاریخ تکرارشوندهٔ یک رویداد است، نه خود رویداد.",
    ],
    recallWord: 1,
    listenWord: 2,
    produceWord: 0,
    meaningWords: [0, 3, 5],
    listenNote: "guests /ɡests/ با خوشهٔ /sts/ و thirty /ˈθɜːti/ با /θ/ آغاز می‌شود",
    words: [
      ["celebrate", "جشن گرفتن", "to do something special for an event", "/ˈselɪbreɪt/", "verb", "We celebrate New Year with our family.", "سال نو را با خانواده جشن می‌گیریم.", "How do you celebrate your birthday?", "تولدت را چطور جشن می‌گیری؟", "celebrate a birthday", "celebrate with family", "celebrate فعل است و اسمش celebration."],
      ["wedding", "عروسی", "the ceremony when two people marry", "/ˈwedɪŋ/", "noun", "Their wedding was in June.", "عروسی‌شان در ژوئن بود.", "We danced all night at the wedding.", "در عروسی تا صبح رقصیدیم.", "a wedding party", "at the wedding", "wedding مراسم عروسی است؛ marriage خود پیوند زناشویی."],
      ["guest", "مهمان", "a person you invite to an event", "/ɡest/", "noun", "There were thirty guests at the wedding.", "سی مهمان در عروسی بود.", "Every guest brought a small gift.", "هر مهمان یک هدیهٔ کوچک آورد.", "a wedding guest", "invite guests", "guest قابل‌شمارش است: a guest، many guests."],
      ["gift", "هدیه", "a present you give someone", "/ɡɪft/", "noun", "I bought a gift for my sister.", "برای خواهرم یک هدیه خریدم.", "The gift was wrapped in blue paper.", "هدیه در کاغذ آبی پیچیده شده بود.", "buy a gift", "a birthday gift", "gift با for + شخص می‌آید: a gift for my sister."],
      ["candle", "شمع", "a stick of wax that burns with a flame", "/ˈkændl/", "noun", "One candle is still burning.", "یک شمع هنوز روشن است.", "She blew out the candles on the cake.", "او شمع‌های کیک را فوت کرد.", "blow out a candle", "a birthday candle", "candle قابل‌شمارش است: a candle، two candles."],
      ["anniversary", "سالگرد", "the yearly date of an important event", "/ˌænɪˈvɜːsəri/", "noun", "They celebrated their tenth anniversary.", "دهمین سالگردشان را جشن گرفتند.", "Our anniversary is in May.", "سالگرد ما در ماه مه است.", "a wedding anniversary", "our tenth anniversary", "anniversary تاریخ تکرارشونده است؛ سالگرد ازدواج wedding anniversary."],
    ],
    blanks: [
      ["We ___ a big party last year.", ["had", "held"], "have a party و hold a party هر دو درست‌اند؛ گذشتهٔ hold بی‌قاعده است (held).", "GRAMMAR"],
      ["We will ___ my birthday at home.", ["celebrate", "have"], "celebrate a birthday و have a birthday (party) هر دو رایج‌اند.", "GRAMMAR"],
    ],
    translation: [
      "دیشب مهمان‌های زیادی به خانهٔ ما آمدند.",
      ["many guests came to our house last night", "a lot of guests came to our house last night"],
      "زمان گذشتهٔ come بی‌قاعده است (came) و last night زمان را می‌رساند.",
    ],
    productionPrompt: "بگو سال نو را با خانواده جشن می‌گیرید.",
    productionHint: "celebrate + مناسبت + with + شخص.",
  },
  {
    slug: "invitations",
    topic: "Social",
    title: "Invitations",
    titleFa: "دعوت کردن و پاسخ به دعوت",
    minutes: 6,
    rule:
      "برای دعوت مؤدبانه: Would you like to…? یا How about + فعل ing?\n" +
      "پاسخ مثبت: I'd love to و پاسخ منفی مؤدبانه: I'm sorry, I can't.\n" +
      "برای پرسیدن آمادگی طرف: Are you free on Saturday?\n" +
      "accept یعنی پذیرفتن و refuse یعنی رد کردن دعوت.",
    table: [
      ["Would you like to join us?", "دوست داری به ما ملحق شوی؟"],
      ["Are you free on Saturday?", "شنبه آزادی؟"],
      ["I'd love to come.", "با کمال میل می‌آیم."],
      ["I'm sorry, I can't.", "متأسفم، نمی‌توانم."],
    ],
    examples: [
      ["My friends invited me to dinner.", "دوستانم مرا به شام دعوت کردند."],
      ["She accepted the invitation at once.", "او فوراً دعوت را پذیرفت."],
      ["It is hard to refuse a friend.", "رد کردن دوست سخت است."],
    ],
    errors: [
      "invite با to می‌آید: invite someone to dinner، نه invite someone for dinner در ساختار ثابت.",
      "بعد از Would you like حتماً to می‌آید: Would you like to come.",
      "پاسخ منفی مؤدبانه با I'm sorry آغاز می‌شود، نه با no تنها.",
    ],
    recallWord: 0,
    listenWord: 4,
    produceWord: 5,
    meaningWords: [1, 2, 3],
    listenNote: "sorry /ˈsɒri/ با /ɒ/ و Friday /ˈfraɪdeɪ/ با تکیه روی هجای اول",
    words: [
      ["invite", "دعوت کردن", "to ask someone to come to an event", "/ɪnˈvaɪt/", "verb", "My friends invited me to dinner.", "دوستانم مرا به شام دعوت کردند.", "They invited us to their wedding.", "آن‌ها ما را به عروسی‌شان دعوت کردند.", "invite someone to", "invite a friend", "invite با to + مناسبت می‌آید؛ اسمش invitation است."],
      ["accept", "پذیرفتن", "to say yes to an offer", "/əkˈsept/", "verb", "I am happy to accept your invitation.", "خوشحالم دعوت شما را می‌پذیرم.", "She accepted the invitation at once.", "او فوراً دعوت را پذیرفت.", "accept an invitation", "accept the offer", "accept با invitation و offer می‌آید؛ except یعنی «به‌جز» و متفاوت است."],
      ["refuse", "رد کردن", "to say no to something", "/rɪˈfjuːz/", "verb", "He refused the offer politely.", "او مودبانه پیشنهاد را رد کرد.", "It is hard to refuse a friend.", "رد کردن دوست سخت است.", "refuse an offer", "refuse politely", "refuse با مفعول مستقیم می‌آید و در گفتار روزمره say no هم رایج است."],
      ["available", "در دسترس", "free to meet or to be used", "/əˈveɪləbl/", "adjective", "Are you available on Friday?", "جمعه در دسترس هستی؟", "The hall is available after six.", "سالن بعد از شش در دسترس است.", "be available on", "available after six", "available صفت است و با be می‌آید: I am available."],
      ["sorry", "متأسف", "used to apologise politely", "/ˈsɒri/", "adjective", "I am sorry, I cannot come on Friday.", "متأسفم، جمعه نمی‌توانم بیایم.", "Sorry, I have another plan.", "متأسفم، برنامهٔ دیگری دارم.", "I am sorry", "sorry, I can't", "رد کردن دعوت با I'm sorry آغاز می‌شود تا مؤدبانه باشد."],
      ["join", "ملحق شدن", "to become part of a group or activity", "/dʒɔɪn/", "verb", "Would you like to join us for dinner?", "دوست داری برای شام به ما ملحق شوی؟", "Come and join the game.", "بیا و به بازی ملحق شو.", "join us", "join the game", "join بدون to می‌آید: join us، نه join to us."],
    ],
    blanks: [
      ["Would you ___ to come to dinner?", ["like", "care"], "Would you like to… رایج‌ترین دعوت است و Would you care to… رسمی‌تر و درست است.", "GRAMMAR"],
      ["Are you ___ on Saturday?", ["free", "available"], "free و available هر دو صفت‌اند و با be می‌آیند.", "GRAMMAR"],
    ],
    translation: [
      "دوستانم مرا به شام دعوت کردند.",
      ["my friends invited me to dinner", "my friends invited me for dinner"],
      "invite + مفعول + to + مناسبت؛ گذشتهٔ invite با d است.",
    ],
    productionPrompt: "دعوت کن که به شام به شما ملحق شوند.",
    productionHint: "با Would you like to join…? دعوت کن.",
  },
  {
    slug: "phone",
    topic: "Communication",
    title: "Phone Conversations",
    titleFa: "تماس تلفنی",
    minutes: 6,
    rule:
      "برای درخواست صحبت با کسی: Can I speak to Sara, please?\n" +
      "برای پرسیدن گوینده: Who is calling? یا Who's speaking?\n" +
      "برای انتظار: Please hold on a moment.\n" +
      "برای پیام: Can I leave a message? و The line is busy یعنی خط اشغال است.",
    table: [
      ["Can I speak to Sara, please?", "می‌توانم با سارا صحبت کنم؟"],
      ["Who is calling?", "چه کسی تماس گرفته؟"],
      ["Please hold on a moment.", "لطفاً یک لحظه منتظر بمانید."],
      ["Can I leave a message?", "می‌توانم پیامی بگذارم؟"],
    ],
    examples: [
      ["The line is busy, so I left a voicemail.", "خط اشغال بود، برای همین پیام صوتی گذاشتم."],
      ["I will call you back in ten minutes.", "ده دقیقهٔ دیگر بهت زنگ می‌زنم."],
      ["Your message was very clear.", "پیامت خیلی واضح بود."],
    ],
    errors: [
      "speak to با to می‌آید: speak to Sara، نه speak Sara.",
      "hold on جداشدنی نیست و مفعول نمی‌گیرد.",
      "voicemail یک کلمه است و message با leave می‌آید: leave a message.",
    ],
    recallWord: 2,
    listenWord: 4,
    produceWord: 0,
    meaningWords: [0, 3, 5],
    listenNote: "voicemail /ˈvɔɪsmeɪl/ دو بخش دارد و busy /ˈbɪzi/ با /ɪ/ کوتاه گفته می‌شود",
    words: [
      ["call", "تماس گرفتن", "to phone someone", "/kɔːl/", "verb", "I will call you back in ten minutes.", "ده دقیقهٔ دیگر بهت زنگ می‌زنم.", "She called the office twice.", "او دو بار به دفتر زنگ زد.", "call someone back", "call the office", "call back یعنی دوباره زنگ زدن؛ ضمیر میان call و back می‌آید."],
      ["hold on", "منتظر ماندن", "to wait for a short time", "/həʊld ɒn/", "verb", "Please hold on for a moment.", "لطفاً یک لحظه منتظر بمانید.", "Hold on, I am checking the file.", "یک لحظه، دارم پرونده را بررسی می‌کنم.", "hold on a moment", "please hold on", "hold on در تلفن یعنی «منتظر بمان» و مفعول نمی‌گیرد."],
      ["message", "پیام", "words you send to someone", "/ˈmesɪdʒ/", "noun", "Can I leave a message?", "می‌توانم پیامی بگذارم؟", "Your message was very clear.", "پیامت خیلی واضح بود.", "leave a message", "take a message", "leave a message یعنی پیام گذاشتن و take a message یعنی پیام گرفتن."],
      ["line", "خط", "the telephone connection", "/laɪn/", "noun", "The line is very bad today.", "امروز خط خیلی بد است.", "Please wait, the line is busy.", "لطفاً صبر کنید، خط اشغال است.", "the line is busy", "a bad line", "the line is busy یعنی خط اشغال است؛ برای کیفیت هم می‌آید."],
      ["voicemail", "پیام صوتی", "a recorded message on the phone", "/ˈvɔɪsmeɪl/", "noun", "The line is busy, so I left a voicemail.", "خط اشغال بود، برای همین پیام صوتی گذاشتم.", "Listen to your voicemail after the meeting.", "بعد از جلسه پیام صوتی‌هایت را گوش کن.", "leave a voicemail", "check your voicemail", "voicemail یک کلمه است؛ leave a voicemail یعنی پیام صوتی گذاشتن."],
      ["busy", "مشغول", "not free to talk now", "/ˈbɪzi/", "adjective", "The number is busy.", "شماره اشغال است.", "Sorry, I was busy this morning.", "متأسفم، امروز صبح مشغول بودم.", "the line is busy", "I am busy", "busy هم برای شخص و هم برای خط تلفن به کار می‌رود."],
    ],
    blanks: [
      ["Can I ___ to Sara, please?", ["speak", "talk"], "speak to و talk to هر دو درست‌اند؛ speak در تلفن رسمی‌تر است.", "GRAMMAR"],
      ["Who is ___?", ["calling", "speaking"], "Who is calling? و Who is speaking? هر دو در تلفن پرسش رایج‌اند.", "GRAMMAR"],
    ],
    translation: [
      "می‌توانم پیامی بگذارم؟",
      ["can i leave a message", "could i leave a message"],
      "درخواست مؤدبانه با Can I یا Could I و leave a message ساخته می‌شود.",
    ],
    productionPrompt: "بگو ده دقیقهٔ دیگر زنگ می‌زنی.",
    productionHint: "از call back و in ten minutes استفاده کن.",
  },
  {
    slug: "emails",
    topic: "Writing",
    title: "Writing Simple Emails",
    titleFa: "نوشتن ایمیل ساده",
    minutes: 6,
    rule:
      "هر ایمیل ساده یک subject دارد و با Dear + نام شروع می‌شود.\n" +
      "برای هدف ایمیل: I am writing to ask about… یا I am writing to reply to…\n" +
      "فایل با attach پیوست می‌شود: I attached the file to the email.\n" +
      "پایان ایمیل با Best regards یا Kind regards و نام فرستنده.",
    table: [
      ["Subject: Booking for June", "موضوع: رزرو برای ژوئن"],
      ["Dear Ali,", "علی عزیز،"],
      ["I attached the file to the email.", "فایل را به ایمیل پیوست کردم."],
      ["Best regards, Sara", "با احترام، سارا"],
    ],
    examples: [
      ["Please reply to my email soon.", "لطفاً زودتر به ایمیلم پاسخ بدهید."],
      ["Can you forward this email to Ali?", "می‌توانی این ایمیل را برای علی بفرستی؟"],
      ["The subject of your email was missing.", "موضوع ایمیلت خالی بود."],
    ],
    errors: [
      "reply to لازم است: «reply my email» غلط است، «reply to my email» درست.",
      "attach فعل است و attachment اسم؛ file را attach می‌کنیم.",
      "subject موضوع ایمیل است، نه شخص؛ آن subject (درس) در جمله قاطی نشود.",
    ],
    recallWord: 0,
    listenWord: 5,
    produceWord: 1,
    meaningWords: [0, 1, 3],
    listenNote: "document /ˈdɒkjumənt/ تکیه روی هجای اول دارد و attached /əˈtætʃt/ با /tʃ/ گفته می‌شود",
    words: [
      ["subject", "موضوع", "the title line of an email", "/ˈsʌbdʒɪkt/", "noun", "Write a clear subject in your email.", "در ایمیلت یک موضوع روشن بنویس.", "The subject of your email was missing.", "موضوع ایمیلت خالی بود.", "the subject line", "write a subject", "subject در ایمیل یعنی عنوان پیام؛ با the subject of می‌آید."],
      ["attach", "پیوست کردن", "to add a file to an email", "/əˈtætʃ/", "verb", "I attached the file to the email.", "فایل را به ایمیل پیوست کردم.", "Please attach the photo, not the link.", "لطفاً عکس را پیوست کن، نه لینک را.", "attach a file", "attached to the email", "attach با to می‌آید: attach a file to an email."],
      ["reply", "پاسخ دادن", "to answer a message", "/rɪˈplaɪ/", "verb", "Please reply to my email soon.", "لطفاً زودتر به ایمیلم پاسخ بدهید.", "She replied to every message.", "او به هر پیامی پاسخ داد.", "reply to an email", "reply soon", "reply با to می‌آید: reply to my email."],
      ["regards", "با احترام", "a polite way to end a letter", "/rɪˈɡɑːdz/", "noun", "Best regards, Sara", "با احترام، سارا", "Kind regards to your family.", "به خانواده‌ات سلام برسان.", "best regards", "kind regards", "regards همیشه جمع است و در پایان نامه یا ایمیل می‌آید."],
      ["forward", "ارسال کردن", "to send a message on to someone else", "/ˈfɔːwəd/", "verb", "Can you forward this email to Ali?", "می‌توانی این ایمیل را برای علی بفرستی؟", "I forwarded the message this morning.", "من پیام را امروز صبح فرستادم.", "forward an email", "forward it to", "forward با to + شخص می‌آید: forward it to Ali."],
      ["document", "سند", "a file with written information", "/ˈdɒkjumənt/", "noun", "Please find the document attached to this email.", "سند پیوست این ایمیل است.", "The document is in two pages.", "این سند دو صفحه است.", "attach a document", "the document attached", "document اسم است؛ file عام‌تر و document سند رسمی‌تر."],
    ],
    blanks: [
      ["Please ___ back to me soon.", ["write", "get"], "write back و get back هر دو با back و به معنی پاسخ دادن‌اند.", "GRAMMAR"],
      ["Best ___ , Sara", ["wishes", "regards"], "Best wishes و Best regards هر دو پایان‌بندی رایج ایمیل‌اند.", "GRAMMAR"],
    ],
    translation: [
      "لطفاً به ایمیل من پاسخ بدهید.",
      ["please reply to my email", "please reply to my email soon", "please answer my email"],
      "reply با to می‌آید؛ درخواست با please + فعل ساده ساخته می‌شود.",
    ],
    productionPrompt: "بگو فایل را به ایمیل پیوست کردی.",
    productionHint: "از attach + مفعول + to the email در گذشتهٔ ساده استفاده کن.",
  },
];
