/**
 * Authored source for bundle v28 (A1 syllabus completion) — lessons 11–16 of 16.
 * Split from the first file only because of its size; the runner merges both.
 */

export const more = [
  {
    slug: "places-town",
    topic: "City",
    title: "Places in Town",
    titleFa: "مکان‌های شهر",
    minutes: 6,
    rule:
      "برای گفتن «رفتن به جایی» از go to استفاده می‌کنیم: go to school, go to the bank.\n" +
      "با نام مکان‌های عمومی the می‌آید: the bank, the hospital, the market.\n" +
      "برای گفتن نزدیکی از next to یا near استفاده می‌کنیم: The bank is next to the shop.",
    table: [
      ["a shop", "مغازه"],
      ["a school", "مدرسه"],
      ["a park", "پارک"],
      ["a hospital", "بیمارستان"],
      ["a bank", "بانک"],
      ["a market", "بازار"],
    ],
    examples: [
      ["I go to school every morning.", "من هر صبح به مدرسه می‌روم."],
      ["The bank is next to the shop.", "بانک کنار مغازه است."],
      ["Children play in the park.", "بچه‌ها در پارک بازی می‌کنند."],
    ],
    errors: [
      "«go to home» غلط است؛ go home درست است.",
      "در انگلیسی بریتانیایی shop و در آمریکایی store رایج است.",
    ],
    pron:
      "hospital تکیه روی هجای اول دارد: /ˈhɒspɪtl/.\n" +
      "bank با /æ/ کوتاه و /ŋk/: /bæŋk/.\n" +
      "park واکهٔ بلند /ɑːr/ دارد: /pɑːrk/.",
    phonology: "phonology.word-stress",
    listenNote: "park با /ɑːr/ بلند است؛ next با /kst/ پایانی و hospital تکیه روی اول",
    recallWord: 4,
    listenWord: 2,
    produceWord: 3,
    words: [
      ["shop", "مغازه", "a place where you buy things", "/ʃɒp/", "noun", "The shop opens at nine.", "مغازه ساعت نه باز می‌شود.", "I buy bread at the shop.", "نان را از مغازه می‌خرم.", "a small shop", "go to the shop", "shop در انگلیسی بریتانیایی مغازه است؛ در آمریکایی store."],
      ["school", "مدرسه", "a place where children learn", "/skuːl/", "noun", "My children go to school at eight.", "بچه‌های من ساعت هشت به مدرسه می‌روند.", "The school is near our house.", "مدرسه نزدیک خانهٔ ما است.", "go to school", "a big school", "go to school یعنی رفتن برای درس خواندن."],
      ["park", "پارک", "a green place with trees for walking", "/pɑːrk/", "noun", "Children play in the park.", "بچه‌ها در پارک بازی می‌کنند.", "The park is next to the hospital.", "پارک کنار بیمارستان است.", "in the park", "a small park", "park هم اسم است هم فعل (پارک کردن)."],
      ["hospital", "بیمارستان", "a place where doctors help sick people", "/ˈhɒspɪtl/", "noun", "The doctor works in a hospital.", "پزشک در بیمارستان کار می‌کند.", "The hospital is very big.", "بیمارستان خیلی بزرگ است.", "in hospital", "go to the hospital", "in hospital یعنی بستری بودن؛ in the hospital یعنی داخل ساختمان."],
      ["bank", "بانک", "a place that keeps your money", "/bæŋk/", "noun", "The bank is next to the shop.", "بانک کنار مغازه است.", "I go to the bank on Monday.", "دوشنبه‌ها به بانک می‌روم.", "go to the bank", "a bank account", "bank هم بانک است هم کنارهٔ رود."],
      ["market", "بازار", "a place with many small shops", "/ˈmɑːrkɪt/", "noun", "We buy fruit at the market.", "میوه را از بازار می‌خریم.", "The market is open on Friday.", "بازار جمعه‌ها باز است.", "at the market", "a busy market", "market بازار است؛ supermarket فروشگاه بزرگ."],
    ],
    blanks: [
      ["I buy food at the ___.", ["shop", "market"], "خرید غذا در shop و market هر دو رایج است."],
      ["The ___ is closed on Sunday.", ["shop", "bank", "market"], "این مکان‌ها می‌توانند یکشنبه تعطیل باشند."],
    ],
    translation: [
      "مدرسهٔ ما نزدیک خانه است.",
      ["our school is near our house", "our school is near the house"],
      "برای نزدیکی near می‌آید و مالکیت با our بیان می‌شود.",
    ],
    productionPrompt: "پزشک در بیمارستان کار می‌کند.",
    productionHint: "برای گفتن محل کار: works in + a + مکان.",
  },
  {
    slug: "directions",
    topic: "City",
    title: "Asking for Directions",
    titleFa: "آدرس پرسیدن و مسیرها",
    minutes: 6,
    rule:
      "برای پرسیدن مسیر از Where is ...? یا How do I get to ...? استفاده می‌کنیم.\n" +
      "پاسخ کوتاه و امری است: Go straight, then turn left.\n" +
      "برای فاصله: It is near here. / It is far from here.",
    table: [
      ["left", "چپ"],
      ["right", "راست"],
      ["straight", "مستقیم"],
      ["near", "نزدیک"],
      ["far", "دور"],
      ["a corner", "گوشه و چهارراه"],
    ],
    examples: [
      ["Excuse me, where is the bank?", "ببخشید، بانک کجاست؟"],
      ["Go straight and turn left.", "مستقیم برو و به چپ بپیچ."],
      ["The shop is on the corner.", "مغازه سر گوشه است."],
    ],
    errors: [
      "«turn to left» غلط است؛ turn left درست است.",
      "near به to نیاز ندارد: near the school، نه near to the school.",
    ],
    pron:
      "left با /e/ کوتاه: /left/.\n" +
      "right با /aɪ/: /raɪt/.\n" +
      "straight با /str/ شروع می‌شود: /streɪt/.",
    phonology: "phonology.w-h-sounds",
    listenNote: "far با /ɑːr/ بلند است و station با /steɪʃn/ خوانده می‌شود",
    recallWord: 5,
    listenWord: 4,
    produceWord: 3,
    meaningWords: [0, 1, 4],
    words: [
      ["left", "چپ", "the side opposite right", "/left/", "adverb", "Turn left at the corner.", "سر چهارراه به چپ بپیچ.", "The bank is on the left.", "بانک در سمت چپ است.", "turn left", "on the left", "on the left یعنی در سمت چپ؛ turn left یعنی به چپ بپیچ."],
      ["right", "راست", "the side opposite left", "/raɪt/", "adverb", "Turn right after the school.", "بعد از مدرسه به راست بپیچ.", "The park is on the right.", "پارک در سمت راست است.", "turn right", "on the right", "right هم «راست» است هم «درست»: the right answer."],
      ["straight", "مستقیم", "in a line, not turning", "/streɪt/", "adverb", "Go straight for two hundred metres.", "دویست متر مستقیم برو.", "The road is straight.", "جاده مستقیم است.", "go straight", "straight ahead", "straight با /str/ شروع می‌شود و به معنای «مستقیم» است."],
      ["near", "نزدیک", "not far", "/nɪr/", "preposition", "The shop is near here.", "مغازه اینجا نزدیک است.", "My house is near the park.", "خانهٔ من نزدیک پارک است.", "near here", "near the school", "near بدون to می‌آید: near the bank."],
      ["far", "دور", "a long way away", "/fɑːr/", "adjective", "The station is far from here.", "ایستگاه از اینجا دور است.", "Is it far?", "دور است؟", "far from", "not far", "far با from می‌آید: far from the city."],
      ["corner", "گوشه و چهارراه", "the place where two streets meet", "/ˈkɔːrnər/", "noun", "The shop is on the corner.", "مغازه سر گوشه است.", "Turn left at the corner.", "سر چهارراه به چپ بپیچ.", "on the corner", "around the corner", "corner یعنی گوشهٔ خیابان یا چهارراه."],
    ],
    blanks: [
      ["Turn ___ at the corner.", ["left", "right"], "در چهارراه هم به چپ و هم به راست می‌شود پیچید."],
      ["Go ___ for two hundred metres.", ["straight", "ahead"], "straight و ahead هر دو به معنای «مستقیم» هستند."],
    ],
    translation: [
      "بانک در سمت چپ است.",
      ["the bank is on the left", "the bank is on the left side"],
      "برای گفتن سمت از on the left استفاده می‌کنیم.",
    ],
    productionPrompt: "مغازه اینجا نزدیک است.",
    productionHint: "near بدون حرف اضافهٔ to می‌آید.",
  },
  {
    slug: "transport",
    topic: "Travel",
    title: "Public Transportation",
    titleFa: "حمل‌ونقل عمومی",
    minutes: 5,
    rule:
      "برای گفتن وسیلهٔ سفر از by یا take استفاده می‌کنیم: I go to work by bus. / I take the bus.\n" +
      "با by حرف تعریف نمی‌آید: by bus, by train.\n" +
      "با take حرف تعریف می‌آید: take the bus, take the train.",
    table: [
      ["a bus", "اتوبوس"],
      ["a train", "قطار"],
      ["a taxi", "تاکسی"],
      ["a station", "ایستگاه قطار"],
      ["a ticket", "بلیت"],
      ["a bus stop", "ایستگاه اتوبوس"],
    ],
    examples: [
      ["I take the bus to work.", "من با اتوبوس به سر کار می‌روم."],
      ["The train leaves at eight.", "قطار ساعت هشت حرکت می‌کند."],
      ["You can buy a ticket at the station.", "می‌توانی از ایستگاه بلیت بخری."],
    ],
    errors: [
      "«I go by the bus» غلط است؛ by bus بدون the می‌آید.",
      "ایستگاه اتوبوس stop است و ایستگاه قطار station.",
    ],
    pron:
      "bus با /ʌ/ کوتاه: /bʌs/.\n" +
      "train با /treɪn/ و /tr/.\n" +
      "ticket تکیه روی هجای اول: /ˈtɪkɪt/.",
    phonology: "phonology.final-consonants",
    listenNote: "train با /tr/ و station با /steɪʃn/ خوانده می‌شود",
    recallWord: 5,
    listenWord: 3,
    produceWord: 1,
    words: [
      ["bus", "اتوبوس", "a big vehicle many people use", "/bʌs/", "noun", "I take the bus to work.", "من با اتوبوس به سر کار می‌روم.", "The bus stops near my house.", "اتوبوس نزدیک خانهٔ من می‌ایستد.", "take the bus", "by bus", "by bus بدون the می‌آید؛ take the bus با the."],
      ["train", "قطار", "a long vehicle that runs on rails", "/treɪn/", "noun", "The train leaves at eight.", "قطار ساعت هشت حرکت می‌کند.", "We go to the city by train.", "ما با قطار به شهر می‌رویم.", "by train", "catch a train", "train قطار است؛ metro مترو."],
      ["taxi", "تاکسی", "a car you pay to travel in", "/ˈtæksi/", "noun", "I take a taxi when I am late.", "وقتی دیرم با تاکسی می‌روم.", "The taxi is in front of the station.", "تاکسی جلوی ایستگاه است.", "take a taxi", "a taxi driver", "taxi را در انگلیسی آمریکایی cab هم می‌گویند."],
      ["station", "ایستگاه قطار", "the place where trains stop", "/ˈsteɪʃn/", "noun", "The train station is very big.", "ایستگاه قطار خیلی بزرگ است.", "I wait for my friend at the station.", "در ایستگاه منتظر دوستم می‌مانم.", "at the station", "a train station", "station ایستگاه قطار یا مترو است."],
      ["ticket", "بلیت", "a paper that lets you travel", "/ˈtɪkɪt/", "noun", "I buy a ticket for the train.", "برای قطار بلیت می‌خرم.", "Where is my ticket?", "بلیتم کجاست؟", "buy a ticket", "a bus ticket", "ticket بلیت است؛ در انگلیسی آمریکایی جریمه هم معنی می‌دهد."],
      ["stop", "ایستگاه اتوبوس", "the place where a bus stops", "/stɒp/", "noun", "The bus stop is next to the shop.", "ایستگاه اتوبوس کنار مغازه است.", "I get off at the next stop.", "در ایستگاه بعدی پیاده می‌شوم.", "a bus stop", "get off at the stop", "stop ایستگاه اتوبوس است و station ایستگاه قطار."],
    ],
    blanks: [
      ["I take the ___ to work.", ["bus", "train", "taxi"], "برای رفتن به سر کار هر سه وسیله ممکن است."],
      ["I wait for the bus at the bus ___.", ["station", "stop"], "ایستگاه اتوبوس را هم stop و هم station می‌گویند."],
    ],
    translation: [
      "من با اتوبوس به کار می‌روم.",
      ["i go to work by bus", "i take the bus to work"],
      "برای وسیلهٔ سفر هم by + وسیله و هم take + the + وسیله درست است.",
    ],
    productionPrompt: "قطار ساعت هشت حرکت می‌کند.",
    productionHint: "برای برنامهٔ زمانی از at + ساعت استفاده کنید.",
  },
  {
    slug: "cafe-restaurant",
    topic: "Food",
    title: "At a Café and Restaurant",
    titleFa: "در کافه و رستوران",
    minutes: 6,
    rule:
      "برای سفارش مؤدبانه از I would like یا Can I have ...? استفاده می‌کنیم.\n" +
      "برای دیدن منو: Can I see the menu, please?\n" +
      "در پایان می‌گوییم: Can we have the bill, please?",
    table: [
      ["a menu", "منو"],
      ["a waiter", "پیشخدمت"],
      ["coffee", "قهوه"],
      ["a sandwich", "ساندویچ"],
      ["the bill", "صورت‌حساب"],
      ["an order", "سفارش"],
    ],
    examples: [
      ["Can I see the menu, please?", "می‌توانم منو را ببینم؟"],
      ["The waiter brings our coffee.", "پیشخدمت قهوهٔ ما را می‌آورد."],
      ["Can we have the bill, please?", "می‌توانیم صورت‌حساب را بگیریم؟"],
    ],
    errors: [
      "«I want a coffee please» بی‌ادب به نظر می‌رسد؛ I would like a coffee, please بهتر است.",
      "bill در انگلیسی بریتانیایی و check در آمریکایی رایج است.",
    ],
    pron:
      "coffee تکیه روی هجای اول: /ˈkɒfi/.\n" +
      "menu با /ˈmenjuː/ خوانده می‌شود.\n" +
      "order با /ˈɔːrdər/ و /d/ میان دو واکه آغاز می‌شود.",
    phonology: "phonology.vowel-length",
    listenNote: "order با /ɔːr/ بلند و take your در گفتار روان /teɪkjər/ شنیده می‌شود",
    recallWord: 4,
    listenWord: 5,
    produceWord: 1,
    words: [
      ["menu", "منو", "the list of food and drinks", "/ˈmenjuː/", "noun", "Can I see the menu, please?", "می‌توانم منو را ببینم؟", "The menu is on the table.", "منو روی میز است.", "see the menu", "a lunch menu", "menu صورت غذاست؛ در فارسی «منو» گفته می‌شود."],
      ["waiter", "پیشخدمت", "a person who brings your food", "/ˈweɪtər/", "noun", "The waiter brings our coffee.", "پیشخدمت قهوهٔ ما را می‌آورد.", "The waiter is very friendly.", "پیشخدمت خیلی خوش‌برخورد است.", "ask the waiter", "a friendly waiter", "waiter برای مرد و waitress برای زن است؛ واژهٔ خنثی server هم هست."],
      ["coffee", "قهوه", "a hot drink made from beans", "/ˈkɒfi/", "noun", "I drink coffee in the morning.", "من صبح‌ها قهوه می‌نوشم.", "Two coffees, please.", "دو قهوه، لطفاً.", "a cup of coffee", "black coffee", "coffee غیرقابل‌شمارش است، ولی در کافه two coffees می‌گویند."],
      ["sandwich", "ساندویچ", "two pieces of bread with food inside", "/ˈsænwɪtʃ/", "noun", "I would like a cheese sandwich.", "یک ساندویچ پنیر می‌خواهم.", "My sandwich is very big.", "ساندویچ من خیلی بزرگ است.", "a cheese sandwich", "a chicken sandwich", "sandwich با /sæn/ شروع می‌شود."],
      ["bill", "صورت‌حساب", "the paper that says what you must pay", "/bɪl/", "noun", "Can we have the bill, please?", "می‌توانیم صورت‌حساب را بگیریم؟", "The bill is on the table.", "صورت‌حساب روی میز است.", "pay the bill", "the bill, please", "bill در بریتانیا و check در آمریکا رایج است."],
      ["order", "سفارش", "the food you ask for in a restaurant", "/ˈɔːrdər/", "noun", "Can I take your order?", "سفارش شما را بگیرم؟", "My order is a coffee and a sandwich.", "سفارش من یک قهوه و یک ساندویچ است.", "take an order", "place an order", "order هم اسم است هم فعل: order a coffee."],
    ],
    blanks: [
      ["I would like a ___ and a sandwich.", ["coffee", "water", "tea"], "همهٔ این نوشیدنی‌ها با a و در کنار sandwich می‌آیند."],
      ["Could we have the ___, please?", ["menu", "bill"], "در آغاز منو و در پایان صورت‌حساب خواسته می‌شود."],
    ],
    translation: [
      "می‌توانم منو را ببینم؟",
      ["can i see the menu", "can i see the menu please", "may i see the menu please"],
      "برای درخواست مؤدبانه از Can I ...? با please استفاده می‌کنیم.",
    ],
    productionPrompt: "پیشخدمت قهوهٔ ما را می‌آورد.",
    productionHint: "برای سوم‌شخص مفرد به فعل s اضافه می‌کنیم: brings.",
  },
  {
    slug: "describing-people",
    topic: "People",
    title: "Describing People",
    titleFa: "معرفی دوستان و توصیف افراد",
    minutes: 6,
    rule:
      "صفت پیش از اسم یا پس از is/are می‌آید: a tall woman / She is tall.\n" +
      "صفت در انگلیسی هرگز s جمع نمی‌گیرد: They are tall.\n" +
      "برای توصیف ظاهر از has got استفاده می‌کنیم: He has got short hair.",
    table: [
      ["tall", "قدبلند"],
      ["short", "کوتاه‌قد"],
      ["young", "جوان"],
      ["old", "پیر"],
      ["friendly", "خوش‌برخورد"],
      ["kind", "مهربان"],
    ],
    examples: [
      ["My brother is very tall.", "برادرم خیلی قدبلند است."],
      ["She is a kind teacher.", "او معلم مهربانی است."],
      ["He has got short hair.", "او موهای کوتاه دارد."],
    ],
    errors: [
      "«She is talls» غلط است؛ صفت هرگز s نمی‌گیرد.",
      "friendly با -ly تمام می‌شود ولی صفت است، نه قید: a friendly man.",
    ],
    pron:
      "tall با /ɔːl/: /tɔːl/.\n" +
      "young با /j/ و /ʌŋ/: /jʌŋ/.\n" +
      "kind با /aɪ/: /kaɪnd/.",
    phonology: "phonology.word-stress",
    listenNote: "grandfather تکیه روی هجای اول دارد و old با /oʊld/ خوانده می‌شود",
    recallWord: 5,
    listenWord: 3,
    produceWord: 4,
    words: [
      ["tall", "قدبلند", "having a long body", "/tɔːl/", "adjective", "My brother is very tall.", "برادرم خیلی قدبلند است.", "The tall man is my teacher.", "مرد قدبلند معلم من است.", "very tall", "a tall building", "tall برای انسان و ساختمان است؛ high برای کوه و قیمت."],
      ["short", "کوتاه‌قد", "not tall; not long", "/ʃɔːrt/", "adjective", "My sister is short.", "خواهرم کوتاه‌قد است.", "He has got short hair.", "او موهای کوتاه دارد.", "short hair", "a short walk", "short هم «کوتاه» است هم «کوتاه‌قد»."],
      ["young", "جوان", "not old", "/jʌŋ/", "adjective", "My brother is young.", "برادرم جوان است.", "The young man works in a shop.", "مرد جوان در مغازه کار می‌کند.", "very young", "a young man", "young با /j/ شروع می‌شود، مثل «ی» فارسی."],
      ["old", "پیر", "having lived a long time", "/oʊld/", "adjective", "My grandfather is old.", "پدربزرگ من پیر است.", "This is an old house.", "این خانهٔ قدیمی است.", "an old man", "old friends", "old هم برای سن است هم برای قدمت چیزها."],
      ["friendly", "خوش‌برخورد", "kind and easy to talk to", "/ˈfrendli/", "adjective", "Our neighbour is very friendly.", "همسایهٔ ما خیلی خوش‌برخورد است.", "She has a friendly smile.", "او لبخندی مهربان دارد.", "a friendly person", "very friendly", "friendly صفت است: She is friendly، نه She is friendlyly."],
      ["kind", "مهربان", "caring about other people", "/kaɪnd/", "adjective", "My teacher is very kind.", "معلم من خیلی مهربان است.", "It is kind of you to help.", "از کمکت ممنون، خیلی مهربانی.", "a kind person", "be kind to", "kind مهربان است؛ kind of یعنی «نوعی»."],
    ],
    blanks: [
      ["My grandfather is very ___.", ["old", "kind"], "پدربزرگ هم پیر است و هم می‌تواند مهربان باشد."],
      ["My little sister is very ___.", ["young", "kind"], "خواهر کوچک هم جوان است و هم مهربان."],
    ],
    translation: [
      "خواهرم خیلی قدبلند است.",
      ["my sister is very tall", "my sister is tall"],
      "صفت پس از is می‌آید و very شدت آن را بیشتر می‌کند.",
    ],
    productionPrompt: "همسایهٔ ما خیلی خوش‌برخورد است.",
    productionHint: "صفت پس از is می‌آید و s نمی‌گیرد.",
  },
  {
    slug: "where-i-live",
    topic: "Home",
    title: "Where I Live",
    titleFa: "معرفی محل زندگی",
    minutes: 6,
    rule:
      "برای گفتن محل زندگی از I live in استفاده می‌کنیم: I live in a big city.\n" +
      "با خیابان in نمی‌آید؛ on می‌آید: I live on this street.\n" +
      "برای توصیف محل صفت پس از is می‌آید: Our street is quiet.",
    table: [
      ["a city", "شهر"],
      ["a village", "روستا"],
      ["a street", "خیابان"],
      ["a neighbour", "همسایه"],
      ["quiet", "ساکت"],
      ["busy", "شلوغ"],
    ],
    examples: [
      ["I live in a small city.", "من در شهر کوچکی زندگی می‌کنم."],
      ["We live on a quiet street.", "ما در خیابانی ساکت زندگی می‌کنیم."],
      ["My neighbour is a teacher.", "همسایهٔ من معلم است."],
    ],
    errors: [
      "«I live in this street» با نشانی غلط است؛ I live on this street درست است.",
      "city شهری بزرگ است و town شهر کوچک‌تر.",
    ],
    pron:
      "city با /s/ شروع می‌شود، نه /k/: /ˈsɪti/.\n" +
      "village با /v/ و /ɪlɪdʒ/: /ˈvɪlɪdʒ/.\n" +
      "quiet دو هجا دارد: /ˈkwaɪət/.",
    phonology: "phonology.w-h-sounds",
    listenNote: "busy با /z/ خوانده می‌شود و s آن شنیده نمی‌شود",
    recallWord: 3,
    listenWord: 5,
    produceWord: 1,
    words: [
      ["city", "شهر", "a big town with many people", "/ˈsɪti/", "noun", "I live in a big city.", "من در شهر بزرگی زندگی می‌کنم.", "The city is very busy at night.", "شهر شب‌ها خیلی شلوغ است.", "a big city", "city life", "city شهری بزرگ است؛ town کوچک‌تر."],
      ["village", "روستا", "a very small town in the country", "/ˈvɪlɪdʒ/", "noun", "My grandmother lives in a small village.", "مادربزرگ من در روستای کوچکی زندگی می‌کند.", "The village is near the river.", "روستا نزدیک رودخانه است.", "a small village", "village life", "village از town کوچک‌تر است."],
      ["street", "خیابان", "a road with houses or shops", "/striːt/", "noun", "We live on a quiet street.", "ما در خیابانی ساکت زندگی می‌کنیم.", "The street is full of shops.", "خیابان پر از مغازه است.", "on a street", "a quiet street", "با street حرف اضافهٔ on می‌آید: on this street."],
      ["neighbour", "همسایه", "a person who lives near you", "/ˈneɪbər/", "noun", "My neighbour is a teacher.", "همسایهٔ من معلم است.", "Our neighbours are very friendly.", "همسایه‌های ما خیلی خوش‌برخورد هستند.", "my next-door neighbour", "a good neighbour", "در انگلیسی آمریکایی neighbor نوشته می‌شود."],
      ["quiet", "ساکت", "with little noise", "/ˈkwaɪət/", "adjective", "Our street is very quiet at night.", "خیابان ما شب‌ها خیلی ساکت است.", "Please be quiet.", "لطفاً ساکت باش.", "very quiet", "a quiet street", "quiet را با quite اشتباه نگیرید؛ quite یعنی «کاملاً»."],
      ["busy", "شلوغ", "full of people or work", "/ˈbɪzi/", "adjective", "The city is busy in the morning.", "شهر صبح‌ها شلوغ است.", "I am very busy today.", "من امروز خیلی سرم شلوغ است.", "a busy street", "very busy", "busy هم برای مکان است هم برای شخص."],
    ],
    blanks: [
      ["I live in a small ___ near the sea.", ["city", "village"], "هم شهر کوچک و هم روستا می‌تواند کنار دریا باشد."],
      ["Our ___ is very quiet at night.", ["street", "village", "city"], "خیابان، روستا و شهر می‌توانند شب‌ها ساکت باشند."],
    ],
    translation: [
      "من در یک شهر بزرگ زندگی می‌کنم.",
      ["i live in a big city", "i live in a large city"],
      "برای محل زندگی live in می‌آید و صفت پیش از اسم قرار می‌گیرد.",
    ],
    productionPrompt: "مادربزرگ من در یک روستای کوچک زندگی می‌کند.",
    productionHint: "برای سوم‌شخص مفرد به فعل s اضافه کنید: lives.",
  },
];
