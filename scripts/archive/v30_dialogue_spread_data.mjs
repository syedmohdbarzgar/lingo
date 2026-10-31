/**
 * Authored source for bundle v30 — a dialogue turn for every lesson that has
 * none, so conversation practice is not confined to five lessons.
 *
 * Tuple order:
 *   [lessonSlug, promptFa, questionEn, questionFa, accepted[], explanationFa]
 *
 * The authoring rules `validateContent` and `DialogueContentTest` enforce:
 *  - the question is English (TTS speaks it) and is never the answer itself;
 *  - every question carries its Persian gloss;
 *  - the reply is English, and the bundle's equivalents are listed in `accepted`
 *    so a learner who says the natural short form is not marked wrong.
 */

export const turns = [
  // ---- v27 A1 lessons -------------------------------------------------------
  [
    "a1.countries",
    "بگو اهل کجایی و کجا زندگی می‌کنی.",
    "Where are you from?",
    "اهل کجایی؟",
    ["I am from Iran.", "I'm from Iran."],
    "برای ملیت و کشور از I am from + نام کشور استفاده می‌کنیم، نه I am of.",
  ],
  [
    "a1.age-birthday",
    "سن خودت را بگو.",
    "How old are you?",
    "چند سالت است؟",
    ["I am twenty years old.", "I'm twenty years old.", "I am twenty."],
    "برای سن am/is/are + عدد + years old می‌آید؛ have years old غلط است.",
  ],
  [
    "a1.days-months",
    "بگو امروز چه روزی است.",
    "What day is it today?",
    "امروز چه روزی است؟",
    ["Today is Monday.", "It is Monday.", "It's Monday."],
    "روزهای هفته با حرف بزرگ شروع می‌شوند و برای پرسش روز از What day استفاده می‌کنیم.",
  ],
  [
    "a1.time",
    "ساعت را بگو: هفت و نیم.",
    "Excuse me, what time is it?",
    "ببخشید، ساعت چند است؟",
    ["It is half past seven.", "It's half past seven."],
    "برای نیم ساعت past به کار می‌رود: half past seven؛ it's پیش از ساعت می‌آید.",
  ],
  [
    "a1.colors",
    "رنگ کیف خودت را بگو.",
    "What colour is your bag?",
    "رنگ کیفت چیست؟",
    ["My bag is black.", "It is black.", "It's black."],
    "صفت رنگ بدون حرف تعریف پس از is می‌آید: My bag is black، نه My bag is a black.",
  ],
  [
    "a1.classroom",
    "خودکار بخواه و مؤدبانه تشکر کن.",
    "Can I borrow your pen, please?",
    "می‌توانم خودکارت را قرض بگیرم؟",
    ["Yes, here you are.", "Sure, here you are.", "Yes, of course."],
    "برای دادن چیزی با دست، here you are گفته می‌شود؛ here is it غلط است.",
  ],

  // ---- v28 A1 lessons -------------------------------------------------------
  [
    "a1.jobs",
    "شغل خودت را بگو.",
    "What do you do?",
    "شغلت چیست؟",
    ["I am a teacher.", "I'm a teacher."],
    "پیش از نام شغل a یا an می‌آید: I am a teacher.",
  ],
  [
    "a1.belongings",
    "بگو چتر مال توست.",
    "Is this your umbrella?",
    "این چتر توست؟",
    ["Yes, it is mine.", "Yes, it's mine.", "Yes, it is."],
    "برای مالکیت، mine جای my umbrella می‌آید: It is mine.",
  ],
  [
    "a1.home-rooms",
    "بگو خانه‌ات سه اتاق دارد.",
    "How many rooms are in your home?",
    "خانه‌ات چند اتاق دارد؟",
    ["There are three rooms in my home.", "There are three rooms."],
    "برای شمارش از there are + عدد + جمع استفاده می‌کنیم، نه it has.",
  ],
  [
    "a1.house-objects",
    "بگو کنترل روی میز است.",
    "Where is the remote control?",
    "کنترل کجاست؟",
    ["It is on the table.", "It's on the table."],
    "برای سطح روی چیزی on به کار می‌رود: on the table، نه in the table.",
  ],
  [
    "a1.food-drinks",
    "یک نوشیدنی مؤدبانه سفارش بده.",
    "What would you like to drink?",
    "چه نوشیدنی‌ای میل دارید؟",
    ["I would like some tea, please.", "I'd like some tea.", "Some tea, please."],
    "would like + اسم یا to + فعل؛ برای نوشیدنی غیرقابل‌شمارش some می‌آید.",
  ],
  [
    "a1.fruits-vegetables",
    "میوه انتخاب کن: سیب.",
    "Do you want an apple or a banana?",
    "سیب می‌خواهی یا موز؟",
    ["An apple, please.", "I would like an apple.", "I'd like an apple."],
    "apple با an می‌آید چون با صدای صدادار شروع می‌شود؛ انتخاب با همان اسم کافی است.",
  ],
  [
    "a1.clothes",
    "انداز خودت را بگو.",
    "What size do you wear?",
    "چه اندازه‌ای می‌پوشی؟",
    ["I wear a medium size.", "I wear medium.", "Medium, please."],
    "اندازه پس از wear می‌آید: I wear a medium size؛ size جمع بسته نمی‌شود.",
  ],
  [
    "a1.body",
    "بگو سرت درد می‌کند.",
    "What is the matter? Does your head hurt?",
    "چه شده؟ سرت درد می‌کند؟",
    ["Yes, my head hurts.", "My head hurts."],
    "hurt برای درد بدن به کار می‌رود و با سوم‌شخص مفرد s می‌گیرد: my head hurts.",
  ],
  [
    "a1.animals",
    "بگو یک گربه داری.",
    "Do you have a pet?",
    "حیوان خانگی داری؟",
    ["Yes, I have a cat.", "I have a cat."],
    "برای داشتن pet از have a استفاده می‌کنیم و پیش از حیوان a می‌آید.",
  ],
  [
    "a1.hobbies",
    "بگو از کتاب خواندن لذت می‌بری.",
    "What do you like doing in your free time?",
    "در وقت آزاد چه کاری دوست داری؟",
    ["I like reading books.", "I like to read books."],
    "بعد از like هم -ing و هم to + فعل می‌آید؛ هر دو درست‌اند.",
  ],
  [
    "a1.places-town",
    "بگو داروخانه کنار بانک است.",
    "Where can I buy medicine near here?",
    "نزدیک اینجا کجا می‌توانم دارو بخرم؟",
    ["There is a pharmacy next to the bank.", "The pharmacy is next to the bank."],
    "برای مجاورت next to به کار می‌رود: next to the bank.",
  ],
  [
    "a1.directions",
    "مسیر ادارهٔ پست را توضیح بده: مستقیم برو و سر چراغ به چپ بپیچ.",
    "How do I get to the post office?",
    "چطور به ادارهٔ پست بروم؟",
    ["Go straight and turn left at the traffic light.", "Go straight and turn left."],
    "برای دادن مسیر، فعل امر (go, turn) و ترتیب کارها را بگو: straight، سپس left.",
  ],
  [
    "a1.transport",
    "بگو اتوبوس شمارهٔ پنج به ایستگاه می‌رود.",
    "Which bus goes to the station?",
    "کدام اتوبوس به ایستگاه می‌رود؟",
    ["The number five bus goes to the station.", "Take the number five bus."],
    "شمارهٔ خط با number five می‌آید و فعل go با سوم‌شخص مفرد s می‌گیرد: goes.",
  ],
  [
    "a1.cafe-restaurant",
    "یک ساندویچ سفارش بده.",
    "Are you ready to order?",
    "برای سفارش آماده‌اید؟",
    ["Yes, a chicken sandwich, please.", "Yes, I would like a chicken sandwich.", "I'd like a chicken sandwich."],
    "برای سفارش، نام غذا با a و سپس please کافی و مؤدبانه است.",
  ],
  [
    "a1.describing-people",
    "برادرت را توصیف کن: قدبلند با موی سیاه کوتاه.",
    "What does your brother look like?",
    "برادرت چه شکلی است؟",
    ["He is tall and has short black hair.", "He's tall with short black hair."],
    "برای ظاهر از look like استفاده می‌کنیم؛ صفت پیش از اسم می‌آید: short black hair.",
  ],
  [
    "a1.where-i-live",
    "محل زندگی‌ات را توصیف کن.",
    "Tell me about your town.",
    "دربارهٔ شهرت بگو.",
    ["I live in a small town near the mountains.", "I live in a small town."],
    "برای محل زندگی in به کار می‌رود: live in a town؛ for a town غلط است.",
  ],

  // ---- v29 A2 lessons -------------------------------------------------------
  [
    "a2.hotel-booking",
    "یک اتاق دو تخته برای دو شب رزرو کن.",
    "Good evening. How can I help you?",
    "عصر بخیر. چه کمکی می‌توانم بکنم؟",
    ["I would like to book a double room for two nights.", "I'd like to book a double room for two nights."],
    "برای رزرو از would like + to + book استفاده می‌کنیم و مدت اقامت با for می‌آید.",
  ],
  [
    "a2.plane-ticket",
    "بگو به استانبول پرواز می‌کنی.",
    "Where are you flying to?",
    "به کجا پرواز می‌کنید؟",
    ["I am flying to Istanbul.", "I'm flying to Istanbul."],
    "مقصد پرواز با to می‌آید و زمان حال استمراری آیندهٔ نزدیک را نشان می‌دهد.",
  ],
  [
    "a2.hotel-checkin",
    "بگو رزرو داری، به نام کریمی.",
    "Do you have a reservation?",
    "رزرو دارید؟",
    ["Yes, I have a reservation under the name Karimi.", "Yes, a reservation under the name Karimi."],
    "برای نامی که رزرو به آن است under the name می‌گوییم، نه with the name.",
  ],
  [
    "a2.train-bus",
    "بگو قطار از سکوی دو حرکت می‌کند.",
    "Which platform does the train leave from?",
    "قطار از کدام سکو حرکت می‌کند؟",
    ["It leaves from platform two.", "From platform two."],
    "برای حرکت از سکو from می‌آید: leaves from platform two.",
  ],
  [
    "a2.travel-info",
    "بگو سفر حدود چهار ساعت طول می‌کشد.",
    "How long does the journey take?",
    "سفر چقدر طول می‌کشد؟",
    ["It takes about four hours.", "It takes four hours."],
    "مدت سفر با take می‌آید و تقریب با about بیان می‌شود.",
  ],
  [
    "a2.holiday-plans",
    "بگو تابستان با خانواده به خارج سفر می‌کنی.",
    "What are you going to do in the summer?",
    "تابستان چه کار می‌کنی؟",
    ["I will travel abroad with my family.", "I'm going to travel abroad with my family."],
    "برای برنامهٔ آینده will یا be going to می‌آید؛ abroad قید است و to نمی‌گیرد.",
  ],
];
