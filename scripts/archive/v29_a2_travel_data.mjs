/**
 * Authored source for bundle v29 — the first six A2 syllabus topics (travel and
 * staying), topics 1 and 3–7 of docs/curriculum.md.
 *
 * Every field here is hand-written content: the runner only assembles the JSON
 * shapes (lesson, six words, nine exercises, knowledge nodes).
 *
 * Word tuple order (same as the v28 batch):
 *   [word, fa, definition, ipa, pos, en1, fa1, en2, fa2, collocation1,
 *    collocation2, usageNote]
 */

export const lessons = [
  {
    slug: "hotel-booking",
    topic: "Travel",
    title: "Booking a Hotel",
    titleFa: "رزرو هتل",
    minutes: 7,
    rule:
      "برای رزرو از I'd like to book… یا Could I book…? استفاده می‌کنیم؛ would like همیشه با to می‌آید.\n" +
      "برای پرسیدن موجود بودن اتاق: Is a double room available?\n" +
      "برای قیمت: How much is it per night? و برای تأیید نهایی will به کار می‌رود: I'll take it.",
    table: [
      ["I'd like to book a room.", "می‌خواهم اتاق رزرو کنم."],
      ["Is a double room available?", "اتاق دو تخته موجود است؟"],
      ["How much is it per night?", "شبی چند است؟"],
      ["I'll take it.", "همان را می‌گیرم."],
    ],
    examples: [
      ["I would like to book a room for two nights.", "می‌خواهم برای دو شب اتاق رزرو کنم."],
      ["A double room is available from Friday.", "اتاق دو تخته از جمعه موجود است."],
      ["The deposit is fifty dollars.", "ودیعه پنجاه دلار است."],
    ],
    errors: [
      "بعد از would like همیشه to می‌آید: «I'd like book» غلط است، «I'd like to book» درست.",
      "هزینهٔ هر شب با per night گفته می‌شود، نه با in night.",
      "برای درخواست مؤدبانه فعل کمکی پیش از فاعل می‌آید: «Could I book…?» نه «I could book?».",
    ],
    recallWord: 0,
    listenWord: 4,
    produceWord: 3,
    meaningWords: [0, 2, 5],
    listenNote: "per /pər/ در per night کوتاه و بی‌تکیه است و sixty /ˈsɪksti/ تکیه روی هجای اول دارد",
    words: [
      ["reservation", "رزرو", "an arrangement to keep a room for you", "/ˌrezərˈveɪʃn/", "noun", "I have a reservation for two nights.", "برای دو شب رزرو دارم.", "You can make a reservation online.", "می‌توانید آنلاین رزرو کنید.", "make a reservation", "a reservation for two nights", "reservation از فعل reserve می‌آید؛ make a reservation یعنی رزرو کردن."],
      ["available", "موجود", "free to be used or booked", "/əˈveɪləbl/", "adjective", "A single room is available tonight.", "امشب یک اتاق یک‌نفره موجود است.", "No double rooms are available in July.", "در ژوئیه هیچ اتاق دو تختهٔ موجودی نیست.", "rooms are available", "available tonight", "available صفت است و پیش از آن فعل be می‌آید: the room is available."],
      ["reserve", "رزرو کردن", "to keep something for later use", "/rɪˈzɜːrv/", "verb", "I would like to reserve a table for four.", "می‌خواهم میزی برای چهار نفر رزرو کنم.", "Please reserve the room until Monday.", "لطفاً اتاق را تا دوشنبه رزرو کنید.", "reserve a room", "reserve in advance", "reserve کمی رسمی‌تر از book است؛ هر دو «رزرو کردن» هستند."],
      ["double room", "اتاق دو تخته", "a room with a bed for two people", "/ˈdʌbl ruːm/", "noun", "We need a double room, not a single.", "اتاق دو تخته لازم داریم، نه یک‌نفره.", "The double room has a view of the sea.", "اتاق دو تخته منظرهٔ دریا دارد.", "a double room for two nights", "book a double room", "double room اتاق دو تخته است؛ single room اتاق یک‌نفره."],
      ["per night", "به‌ازای هر شب", "for each night of a stay", "/pər naɪt/", "phrase", "The room costs sixty euros per night.", "اتاق شبی شصت یورو است.", "Breakfast is included per night at this hotel.", "صبحانه در این هتل برای هر شب شامل است.", "cost sixty euros per night", "price per night", "per یعنی «به‌ازای»؛ per night یعنی برای هر شب، در جمله پس از مبلغ می‌آید."],
      ["deposit", "ودیعه", "money paid now as part of the whole price", "/dɪˈpɒzɪt/", "noun", "We ask for a small deposit by card.", "ما ودیعهٔ کمی با کارت می‌خواهیم.", "Your deposit is returned at the end of your stay.", "ودیعهٔ شما در پایان اقامت برگردانده می‌شود.", "pay a deposit", "a deposit of fifty dollars", "deposit پولی است که پیش می‌پردازید و در پایان اقامت برمی‌گردد."],
    ],
    blanks: [
      ["___ I book a room for tonight, please?", ["Could", "May"], "درخواست مؤدبانه با Could I…? یا May I…? و فعل ساده ساخته می‌شود، نه با can you to.", "GRAMMAR"],
      ["The room costs eighty dollars ___ night.", ["per", "a"], "per night و a night هر دو یعنی «شبی»؛ a در این ساختار یعنی per.", "VOCABULARY"],
    ],
    translation: [
      "می‌خواهم برای دو شب یک اتاق دو تخته رزرو کنم.",
      ["i would like to book a double room for two nights", "i'd like to book a double room for two nights", "i want to book a double room for two nights"],
      "would like + to + فعل ساده؛ مدت اقامت با for two nights می‌آید.",
    ],
    productionPrompt: "ما به یک اتاق دو تخته نیاز داریم، نه یک‌نفره.",
    productionHint: "از need a … not a … استفاده کن.",
  },
  {
    slug: "plane-ticket",
    topic: "Travel",
    title: "Buying a Plane Ticket",
    titleFa: "خرید بلیت هواپیما",
    minutes: 7,
    rule:
      "برای خرید بلیت می‌گوییم I'd like a ticket to … یا A ticket to Rome, please.\n" +
      "نوع بلیت را with one-way (یک‌طرفه) و return (رفت‌وبرگشت) مشخص می‌کنیم.\n" +
      "برای برنامهٔ پرواز حال ساده به کار می‌رود: The flight leaves at nine.",
    table: [
      ["a one-way ticket", "بلیت یک‌طرفه"],
      ["a return ticket", "بلیت رفت‌وبرگشت"],
      ["I'd like a ticket to Rome.", "یک بلیت به رم می‌خواهم."],
      ["The flight leaves at nine.", "پرواز ساعت نه حرکت می‌کند."],
    ],
    examples: [
      ["I would like a return ticket to London.", "یک بلیت رفت‌وبرگشت به لندن می‌خواهم."],
      ["Which airline are you flying with?", "با کدام شرکت هواپیمایی پرواز می‌کنید؟"],
      ["The fare includes one suitcase.", "کرایه شامل یک چمدان است."],
    ],
    errors: [
      "luggage غیرقابل‌شمارش است: «luggages» غلط است، «a piece of luggage» درست.",
      "برای هدف سفر حرف اضافهٔ to می‌آید: «a ticket to Rome»، نه «a ticket for Rome».",
      "return یک اسم است؛ برای خود عمل برگشتن فعل return به کار می‌رود: I return on Sunday.",
    ],
    recallWord: 4,
    listenWord: 2,
    produceWord: 0,
    meaningWords: [0, 2, 5],
    listenNote: "one-way /ˌwʌnˈweɪ/ تکیه‌اش روی way است و ticket /ˈtɪkɪt/ تکیه روی هجای اول",
    words: [
      ["flight", "پرواز", "a journey by plane", "/flaɪt/", "noun", "My flight leaves at seven in the morning.", "پرواز من ساعت هفت صبح حرکت می‌کند.", "The flight to Istanbul is full.", "پرواز به استانبول پر است.", "catch a flight", "a direct flight", "flight خود سفر هوایی است؛ شمارهٔ آن هم با همان واژه می‌آید: flight 302."],
      ["airline", "شرکت هواپیمایی", "a company that flies planes", "/ˈeəlaɪn/", "noun", "Which airline are you flying with?", "با کدام شرکت هواپیمایی پرواز می‌کنید؟", "The airline cancelled our flight.", "شرکت هواپیمایی پرواز ما را لغو کرد.", "a cheap airline", "fly with an airline", "airline از air و line ساخته شده و نام شرکت‌های هوایی با همین واژه می‌آید."],
      ["one-way", "یک‌طرفه", "a ticket for travel in one direction only", "/ˌwʌnˈweɪ/", "adjective", "A one-way ticket to Rome, please.", "یک بلیت یک‌طرفه به رم، لطفاً.", "Is this ticket one-way or return?", "این بلیت یک‌طرفه است یا رفت‌وبرگشت؟", "a one-way ticket", "one-way fare", "one-way صفت است و پیش از ticket می‌آید؛ در آمریکا به‌جای آن single هم گفته می‌شود."],
      ["return", "رفت‌وبرگشت", "a ticket for going and coming back", "/rɪˈtɜːrn/", "noun", "I need a return ticket to London.", "من بلیت رفت‌وبرگشت به لندن لازم دارم.", "The return is cheaper than two one-way tickets.", "بلیت رفت‌وبرگشت از دو بلیت یک‌طرفه ارزان‌تر است.", "a return ticket", "the return journey", "return در بریتانیا بلیت رفت‌وبرگشت است؛ معادل آمریکایی آن round trip است."],
      ["luggage", "بار سفر", "the bags you travel with", "/ˈlʌɡɪdʒ/", "noun", "How much luggage can I take?", "چقدر بار می‌توانم ببرم؟", "My luggage is too heavy for the cabin.", "بار من برای کابین سنگین است.", "hand luggage", "check in your luggage", "luggage غیرقابل‌شمارش است؛ برای شمارش می‌گوییم two pieces of luggage."],
      ["fare", "کرایه", "the money you pay for a journey", "/feər/", "noun", "The fare includes one suitcase.", "کرایه شامل یک چمدان است.", "Air fares go up in summer.", "کرایهٔ پرواز در تابستان بالا می‌رود.", "a cheap fare", "pay the fare", "fare هزینهٔ سفر است، نه خود بلیت؛ بلیت همان ticket است."],
    ],
    blanks: [
      ["___ I have a window seat, please?", ["Could", "Can"], "درخواست مؤدبانه با Could I…? یا Can I…? و فعل ساده می‌آید: Could I have…?", "GRAMMAR"],
      ["Air ___ rise in the summer holidays.", ["fares", "prices"], "fare کرایهٔ سفر است و price قیمت کلی؛ هر دو در این جمله درست‌اند.", "VOCABULARY"],
    ],
    translation: [
      "یک بلیت رفت‌وبرگشت به لندن می‌خواهم.",
      ["i would like a return ticket to london", "i'd like a return ticket to london", "i want a return ticket to london"],
      "would like بدون to پیش از اسم می‌آید: would like a ticket؛ مقصد با to می‌آید.",
    ],
    productionPrompt: "چقدر بار می‌توانم ببرم؟",
    productionHint: "پرسش مقدار با How much و اسم غیرقابل‌شمارش.",
  },
  {
    slug: "hotel-checkin",
    topic: "Travel",
    title: "Hotel Check-in",
    titleFa: "ورود به هتل و تحویل اتاق",
    minutes: 6,
    rule:
      "در پذیرش با I have a reservation under the name … شروع می‌کنیم؛ under the name یعنی «به نام».\n" +
      "درخواست مؤدبانه با Could I have…? ساخته می‌شود: Could I have a room on a quiet floor?\n" +
      "زمان تخلیه را با checkout is at … می‌گوییم.",
    table: [
      ["I have a reservation under the name Karimi.", "به نام کریمی رزرو دارم."],
      ["Could I have my key card?", "می‌توانم کارت کلیدم را بگیرم؟"],
      ["Where is the lift?", "آسانسور کجاست؟"],
      ["Checkout is at eleven.", "تخلیهٔ اتاق ساعت یازده است."],
    ],
    examples: [
      ["I have a reservation under the name Ahmadi.", "به نام احمدی رزرو دارم."],
      ["Could I have a room on a quiet floor?", "می‌توانم اتاقی در طبقهٔ آرام داشته باشم؟"],
      ["Breakfast is served from seven to ten.", "صبحانه از هفت تا ده سرو می‌شود."],
    ],
    errors: [
      "برای نامی که رزرو به آن است under the name می‌گوییم، نه with the name.",
      "could I…? درخواست است و could you…? خواهش؛ جای آن‌ها را عوض نکن.",
      "checkout زمان تخلیه است و check-in زمان تحویل اتاق؛ این دو را با هم اشتباه نگیر.",
    ],
    recallWord: 3,
    listenWord: 5,
    produceWord: 5,
    meaningWords: [1, 2, 5],
    listenNote: "checkout /ˈtʃekaʊt/ تکیه روی هجای اول است و eleven /ɪˈlevn/ با /ɪ/ کوتاه شروع می‌شود",
    words: [
      ["check-in", "پذیرش", "the desk where you register at a hotel", "/ˈtʃek ɪn/", "noun", "Check-in starts at two in the afternoon.", "پذیرش از ساعت دو بعدازظهر شروع می‌شود.", "Please go to check-in on the left.", "لطفاً به پذیرش سمت چپ بروید.", "hotel check-in", "at check-in", "check-in اسم است؛ check in بدون خط تیره فعل است: we check in at two."],
      ["reception", "میز پذیرش", "the front desk of a hotel", "/rɪˈsepʃn/", "noun", "The reception is open all night.", "پذیرش تمام شب باز است.", "Leave your key at reception.", "کلید را در پذیرش بگذارید.", "the hotel reception", "call reception", "reception همان میز پذیرش است؛ check-in فرایند ثبت‌نام هنگام ورود."],
      ["key card", "کارت کلید", "a card that opens your room", "/ˈkiː kɑːrd/", "noun", "Here is your key card for room 402.", "این کارت کلید اتاق ۴۰۲ شماست.", "The key card stopped working this morning.", "کارت کلید امروز صبح از کار افتاد.", "your key card", "a key card for room 402", "key card در بیشتر هتل‌ها جای کلید فلزی را گرفته است."],
      ["lift", "آسانسور", "a machine that carries people between floors", "/lɪft/", "noun", "The lift is next to reception.", "آسانسور کنار پذیرش است.", "Take the lift to the fifth floor.", "با آسانسور به طبقهٔ پنجم بروید.", "take the lift", "the lift is out of order", "lift در بریتانیا و elevator در آمریکا؛ هر دو آسانسور هستند."],
      ["breakfast", "صبحانه", "the first meal of the day", "/ˈbrekfəst/", "noun", "Breakfast is served from seven to ten.", "صبحانه از هفت تا ده سرو می‌شود.", "Is breakfast included in the price?", "صبحانه در قیمت هست؟", "breakfast is included", "have breakfast", "breakfast با have می‌آید: have breakfast، نه take breakfast."],
      ["checkout", "تخلیهٔ اتاق", "the time you leave a hotel room", "/ˈtʃekaʊt/", "noun", "Checkout is at eleven in the morning.", "تخلیهٔ اتاق ساعت یازده صبح است.", "Can I have a late checkout?", "می‌توانم تخلیهٔ دیرتر داشته باشم؟", "late checkout", "checkout time", "checkout پایان اقامت است؛ مقابل آن check-in زمان تحویل اتاق است."],
    ],
    blanks: [
      ["I have a ___ under the name Karimi.", ["reservation", "booking"], "reservation و booking هر دو یعنی رزرو؛ under the name یعنی به نام."],
      ["___ I have a room on a quiet floor, please?", ["Could", "Can"], "درخواست مؤدبانه با Could I…? یا Can I…? و فعل ساده ساخته می‌شود.", "GRAMMAR"],
    ],
    translation: [
      "صبحانه در قیمت اتاق هست؟",
      ["is breakfast included in the price", "does the price include breakfast", "is breakfast included in the room price"],
      "برای پرسش از شامل بودن چیزی در قیمت از is … included in the price استفاده می‌کنیم.",
    ],
    productionPrompt: "تخلیهٔ اتاق ساعت یازده صبح است.",
    productionHint: "از Checkout is at + ساعت استفاده کن.",
  },
  {
    slug: "train-bus",
    topic: "Travel",
    title: "Traveling by Train and Bus",
    titleFa: "سفر با قطار و اتوبوس",
    minutes: 7,
    rule:
      "برنامهٔ حرکت با حال ساده گفته می‌شود: The train leaves at 7:15 from platform 3.\n" +
      "برای مسیر بدون تغییر وسیله از direct استفاده می‌کنیم: a direct train to Shiraz.\n" +
      "تأخیر با there is a delay یا the train is delayed بیان می‌شود.",
    table: [
      ["The train leaves at 7:15.", "قطار ساعت ۷:۱۵ حرکت می‌کند."],
      ["It leaves from platform 3.", "از سکوی ۳ حرکت می‌کند."],
      ["Is there a direct train?", "قطار مستقیم هست؟"],
      ["There is a twenty-minute delay.", "بیست دقیقه تأخیر وجود دارد."],
    ],
    examples: [
      ["The train to Isfahan leaves from platform two.", "قطار اصفهان از سکوی دو حرکت می‌کند."],
      ["Is there a direct train to Shiraz?", "قطار مستقیم به شیراز هست؟"],
      ["The delay was because of the snow.", "تأخیر به‌خاطر برف بود."],
    ],
    errors: [
      "برای حرکت از سکو حرف اضافهٔ from می‌آید: leaves from platform two.",
      "برای تأخیر مدت‌دار از a delay of twenty minutes استفاده کن، نه a delay twenty minutes.",
      "direct یعنی بدون تغییر وسیله؛ fast فقط یعنی سریع و ممکن است توقف داشته باشد.",
    ],
    recallWord: 4,
    listenWord: 5,
    produceWord: 0,
    meaningWords: [0, 3, 4],
    listenNote: "coach با /oʊ/ کشیده و every /ˈevri/ با تکیه روی هجای اول تلفظ می‌شود",
    words: [
      ["platform", "سکو", "the place in a station where you get on a train", "/ˈplætfɔːrm/", "noun", "The train to Isfahan leaves from platform two.", "قطار اصفهان از سکوی دو حرکت می‌کند.", "Wait on the platform, not in the hall.", "روی سکو منتظر بمان، نه در سالن.", "platform two", "wait on the platform", "platform شمارهٔ سکو را با عدد می‌گیرد و پیش از عدد می‌آید: platform 3."],
      ["timetable", "جدول زمانی", "a list of the times when trains or buses leave", "/ˈtaɪmteɪbl/", "noun", "The timetable is on the wall.", "جدول زمانی روی دیوار است.", "According to the timetable, the bus leaves at six.", "طبق جدول زمانی، اتوبوس ساعت شش حرکت می‌کند.", "check the timetable", "the winter timetable", "timetable در بریتانیا و schedule در آمریکا؛ هر دو برنامهٔ زمانی هستند."],
      ["delay", "تأخیر", "a time when something happens later than planned", "/dɪˈleɪ/", "noun", "There is a twenty-minute delay.", "بیست دقیقه تأخیر وجود دارد.", "The delay was because of the snow.", "تأخیر به‌خاطر برف بود.", "a long delay", "a delay of twenty minutes", "delay هم اسم است هم فعل: the train was delayed by an hour."],
      ["direct", "مستقیم", "going somewhere without changing trains", "/dəˈrekt/", "adjective", "This bus is direct, so it does not stop in Qom.", "این اتوبوس مستقیم است، پس در قم توقف نمی‌کند.", "There is a direct train to Shiraz every morning.", "هر صبح یک قطار مستقیم به شیراز هست.", "a direct train", "go direct", "direct یعنی بدون تغییر وسیله یا توقف؛ fast یعنی سریع."],
      ["passenger", "مسافر", "a person travelling in a train or bus", "/ˈpæsɪndʒər/", "noun", "Passengers must keep their tickets.", "مسافران باید بلیت‌هایشان را نگه دارند.", "The passenger asked the driver for the next stop.", "مسافر از راننده ایستگاه بعدی را پرسید.", "a passenger train", "the last passenger", "passenger مسافر وسیلهٔ نقلیه است؛ traveller کسی است که سفر می‌کند."],
      ["coach", "اتوبوس بین‌شهری", "a comfortable bus for long journeys", "/koʊtʃ/", "noun", "The coach leaves every two hours.", "این اتوبوس هر دو ساعت حرکت می‌کند.", "We took a coach from Tehran to Rasht.", "با اتوبوس بین‌شهری از تهران به رشت رفتیم.", "by coach", "a coach trip", "coach در بریتانیا اتوبوس بین‌شهری است؛ در آمریکا به آن bus می‌گویند."],
    ],
    blanks: [
      ["The train ___ at 7:15 from platform three.", ["leaves", "departs"], "leaves و departs هر دو برای برنامهٔ حرکت به کار می‌روند و فعل سوم‌شخص مفرد s می‌گیرد."],
      ["There is a ten-minute ___ because of the snow.", ["delay", "wait"], "delay تأخیر رسمی برنامه است و wait انتظار؛ هر دو در این جمله معنا می‌دهند."],
    ],
    translation: [
      "قطار مستقیم به شیراز هست؟",
      ["is there a direct train to shiraz", "is there a direct train for shiraz"],
      "پرسش وجود با Is there + a direct train + to + مقصد ساخته می‌شود.",
    ],
    productionPrompt: "قطار اصفهان از سکوی دو حرکت می‌کند.",
    productionHint: "از The train to … leaves from platform … استفاده کن.",
  },
  {
    slug: "travel-info",
    topic: "Travel",
    title: "Asking About Travel Information",
    titleFa: "پرسیدن اطلاعات سفر",
    minutes: 6,
    rule:
      "برای مدت از How long، برای فاصله از How far و برای انتخاب از Which استفاده می‌کنیم.\n" +
      "ترتیب پرسش: کلمهٔ پرسشی + فعل کمکی + فاعل + فعل اصلی: How long does the journey take?\n" +
      "برای وجود چیزی در نزدیکی: Is there a nearby hotel?",
    table: [
      ["How long does the journey take?", "سفر چقدر طول می‌کشد؟"],
      ["How far is the station?", "ایستگاه چقدر دور است؟"],
      ["Which route is the fastest?", "کدام مسیر سریع‌ترین است؟"],
      ["Is there a nearby pharmacy?", "داروخانهٔ نزدیکی هست؟"],
    ],
    examples: [
      ["The journey takes four hours by train.", "این سفر با قطار چهار ساعت طول می‌کشد."],
      ["Which route is the fastest to the airport?", "کدام مسیر به فرودگاه سریع‌ترین است؟"],
      ["The map is free at the information desk.", "نقشه در میز اطلاعات رایگان است."],
    ],
    errors: [
      "information غیرقابل‌شمارش است: «informations» و «an information» غلط‌اند.",
      "برای مدت از How long استفاده کن، نه How much time در هر جمله‌ای؛ How long طبیعی‌تر است.",
      "nearby پیش از اسم می‌آید: a nearby station، نه a station nearby در همهٔ جمله‌ها.",
    ],
    recallWord: 2,
    listenWord: 0,
    produceWord: 5,
    meaningWords: [1, 3, 4],
    listenNote: "information /ˌɪnfərˈmeɪʃn/ تکیه‌اش روی meɪ است و museum /mjuˈziːəm/ با /mju/ شروع می‌شود",
    words: [
      ["information", "اطلاعات", "facts about something", "/ˌɪnfərˈmeɪʃn/", "noun", "I need some information about the museum.", "کمی اطلاعات دربارهٔ موزه لازم دارم.", "The information desk is near the entrance.", "میز اطلاعات نزدیک ورودی است.", "some information", "the information desk", "information غیرقابل‌شمارش است؛ با some یا the می‌آید و s جمع نمی‌گیرد."],
      ["journey", "سفر", "the act of travelling from one place to another", "/ˈdʒɜːrni/", "noun", "The journey takes four hours by train.", "این سفر با قطار چهار ساعت طول می‌کشد.", "We had a long journey from Tabriz.", "از تبریز سفر طولانی‌ای داشتیم.", "a long journey", "the journey takes four hours", "journey با فعل take می‌آید: the journey takes two hours."],
      ["route", "مسیر", "the way from one place to another", "/ruːt/", "noun", "Which route is the fastest to the airport?", "کدام مسیر به فرودگاه سریع‌ترین است؟", "This route goes along the river.", "این مسیر در طول رودخانه می‌رود.", "the fastest route", "take this route", "route مسیر انتخاب‌شده است؛ road خود جاده است."],
      ["nearby", "نزدیک", "not far from here", "/ˌnɪərˈbaɪ/", "adjective", "Is there a nearby pharmacy?", "داروخانهٔ نزدیکی هست؟", "The nearby station is only ten minutes away.", "ایستگاه نزدیک فقط ده دقیقه فاصله دارد.", "a nearby hotel", "nearby shops", "nearby صفت است و پیش از اسم می‌آید؛ near با to می‌آید: near to the hotel."],
      ["tourist", "گردشگر", "a person who visits a place on holiday", "/ˈtʊərɪst/", "noun", "Many tourists visit this palace in spring.", "بسیاری از گردشگران در بهار به این کاخ می‌آیند.", "The tourist asked how far the beach was.", "گردشگر پرسید ساحل چقدر دور است.", "a tourist map", "tourist season", "tourist هم اسم است هم صفت: tourist office یعنی دفتر گردشگری."],
      ["map", "نقشه", "a drawing of roads and places", "/mæp/", "noun", "Can you show me the station on the map?", "می‌توانید ایستگاه را روی نقشه نشانم بدهید؟", "The map is free at the information desk.", "نقشه در میز اطلاعات رایگان است.", "read the map", "a city map", "map با حرف اضافهٔ on می‌آید: on the map."],
    ],
    blanks: [
      ["The journey ___ about four hours.", ["takes", "lasts"], "takes و lasts هر دو برای مدت سفر درست‌اند و با سوم‌شخص مفرد s می‌آیند.", "GRAMMAR"],
      ["___ route goes to the airport?", ["Which", "What"], "Which برای انتخاب میان چند گزینه و What برای پرسش عمومی‌تر است؛ هر دو درست‌اند.", "GRAMMAR"],
    ],
    translation: [
      "این سفر با قطار چهار ساعت طول می‌کشد.",
      ["the journey takes four hours by train", "the journey by train takes four hours"],
      "مدت سفر با the journey takes + مدت و وسیله با by train گفته می‌شود.",
    ],
    productionPrompt: "می‌توانید ایستگاه را روی نقشه نشانم بدهید؟",
    productionHint: "درخواست با Can you show me … on the map?",
  },
  {
    slug: "holiday-plans",
    topic: "Holidays",
    title: "Planning a Holiday",
    titleFa: "برنامه‌ریزی تعطیلات",
    minutes: 6,
    rule:
      "برای برنامهٔ آینده از will استفاده می‌کنیم: We will stay for a week.\n" +
      "زمان آینده با next week، next summer و on the fifth of August می‌آید.\n" +
      "برای هدف سفر، go + ing به کار می‌رود: go sightseeing، go swimming.",
    table: [
      ["We will stay for a week.", "یک هفته می‌مانیم."],
      ["They will travel abroad next summer.", "تابستان آینده به خارج از کشور سفر می‌کنند."],
      ["We will go sightseeing on the first day.", "روز اول به گردش می‌رویم."],
      ["I will buy a souvenir for my sister.", "برای خواهرم سوغات می‌خرم."],
    ],
    examples: [
      ["Our holiday starts on the fifth of August.", "تعطیلات ما پنجم اوت شروع می‌شود."],
      ["We are planning a trip to the north.", "در حال برنامه‌ریزی سفر به شمال هستیم."],
      ["Sightseeing is cheaper on foot.", "گردش پیاده ارزان‌تر است."],
    ],
    errors: [
      "بعد از will فعل ساده می‌آید: «will stays» غلط است، «will stay» درست.",
      "abroad قید است و to نمی‌گیرد: «go abroad»، نه «go to abroad».",
      "برای گردش go sightseeing می‌گوییم، نه «go to sightseeing».",
    ],
    recallWord: 0,
    listenWord: 2,
    produceWord: 0,
    meaningWords: [1, 3, 5],
    listenNote: "will /wɪl/ در گفتار سریع کوتاه می‌شود و hotel /həʊˈtel/ تکیه‌اش روی tel است",
    words: [
      ["holiday", "تعطیلات", "time when you do not work and travel", "/ˈhɒlədeɪ/", "noun", "Our holiday starts on the fifth of August.", "تعطیلات ما پنجم اوت شروع می‌شود.", "Where are you going for your holiday?", "برای تعطیلات کجا می‌روید؟", "a summer holiday", "go on holiday", "holiday در بریتانیا تعطیلات است؛ معادل آمریکایی آن vacation است."],
      ["trip", "سفر", "a short journey, often for pleasure", "/trɪp/", "noun", "We are planning a trip to the north.", "در حال برنامه‌ریزی سفر به شمال هستیم.", "The trip to the island takes two hours.", "سفر به جزیره دو ساعت طول می‌کشد.", "a day trip", "plan a trip", "trip سفر کوتاه است؛ journey می‌تواند بلند و طولانی باشد."],
      ["stay", "ماندن", "to live somewhere for a short time", "/steɪ/", "verb", "We will stay in a small hotel near the beach.", "در هتل کوچکی نزدیک ساحل می‌مانیم.", "How long will you stay in Shiraz?", "چقدر در شیراز می‌مانید؟", "stay for a week", "stay at a hotel", "stay با at برای هتل و با with برای میزبان می‌آید: stay at a hotel، stay with friends."],
      ["sightseeing", "گردش", "visiting interesting places", "/ˈsaɪtsiːɪŋ/", "noun", "We will go sightseeing on the first day.", "روز اول به گردش می‌رویم.", "Sightseeing is cheaper on foot.", "گردش پیاده ارزان‌تر است.", "go sightseeing", "a sightseeing tour", "sightseeing با فعل go می‌آید: go sightseeing، بدون to."],
      ["souvenir", "سوغات", "something you buy to remember a place", "/ˌsuːvəˈnɪər/", "noun", "I bought a small souvenir for my sister.", "برای خواهرم سوغات کوچکی خریدم.", "Souvenir shops are near the old bridge.", "فروشگاه‌های سوغات نزدیک پل قدیمی هستند.", "buy a souvenir", "a souvenir shop", "souvenir یادگاری سفر است؛ gift هدیهٔ معمولی است."],
      ["abroad", "خارج از کشور", "in or to another country", "/əˈbrɔːd/", "adverb", "They will travel abroad next summer.", "تابستان آینده به خارج از کشور سفر می‌کنند.", "She has never been abroad.", "او هرگز خارج از کشور نبوده.", "travel abroad", "study abroad", "abroad قید است و حرف اضافه نمی‌گیرد: go abroad، نه go to abroad."],
    ],
    blanks: [
      ["They will travel ___ next summer.", ["abroad", "overseas"], "abroad و overseas هر دو «خارج از کشور» هستند و قید مکان‌اند، بدون to."],
      ["I will ___ a souvenir for my sister.", ["buy", "get"], "buy و get هر دو با will و پیش از a souvenir می‌آیند."],
    ],
    translation: [
      "تابستان آینده به خارج از کشور سفر می‌کنیم.",
      ["we will travel abroad next summer", "we'll travel abroad next summer", "next summer we will travel abroad"],
      "will + فعل ساده برای برنامهٔ آینده؛ abroad بدون to می‌آید.",
    ],
    productionPrompt: "تعطیلات ما پنجم اوت شروع می‌شود.",
    productionHint: "از Our holiday starts on + تاریخ استفاده کن.",
  },
];
