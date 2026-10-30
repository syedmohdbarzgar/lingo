/**
 * Authored source for bundle v28 (A1 syllabus completion) — lessons 1–8 of 16.
 * Every field here is hand-written content: the runner only assembles the JSON
 * shapes (lesson, six words, nine exercises, knowledge nodes).
 */

export const lessons = [
  {
    slug: "jobs",
    topic: "Work",
    title: "Jobs and Occupations",
    titleFa: "شغل‌ها و حرفه‌ها",
    minutes: 6,
    rule:
      "برای پرسیدن شغل از What do you do? یا What is your job? استفاده می‌کنیم.\n" +
      "برای گفتن شغل، a یا an پیش از نام شغل می‌آید: I am a teacher.\n" +
      "نام شغل در انگلیسی با حرف کوچک نوشته می‌شود، مگر در آغاز جمله.",
    table: [
      ["a teacher", "معلم"],
      ["a doctor", "پزشک"],
      ["a nurse", "پرستار"],
      ["a driver", "راننده"],
      ["a cook", "آشپز"],
      ["a farmer", "کشاورز"],
    ],
    examples: [
      ["My sister is a nurse.", "خواهرم پرستار است."],
      ["He works in a school.", "او در مدرسه کار می‌کند."],
      ["What do you do?", "شغل شما چیست؟"],
    ],
    errors: [
      "حرف تعریف را جا نیندازید: I am teacher غلط است؛ I am a teacher درست است.",
      "نام شغل را با حرف بزرگ ننویسید؛ فقط در آغاز جمله بزرگ می‌شود.",
    ],
    pron:
      "در teacher تکیه روی هجای اول است: /ˈtiːtʃər/.\n" +
      "در doctor پایانهٔ -or کوتاه و بی‌تکیه است: /ˈdɒktər/.\n" +
      "در nurse واکهٔ /ɜː/ کشیده است: /nɜːrs/.",
    phonology: "phonology.word-stress",
    listenNote: "nurse با /nɜːrs/ کشیده شروع می‌شود و helps با /s/ پایانی می‌آید",
    recallWord: 1,
    listenWord: 2,
    produceWord: 5,
    meaningWords: [1, 3, 5],
    words: [
      ["teacher", "معلم", "a person who teaches in a school", "/ˈtiːtʃər/", "noun", "My mother is a teacher.", "مادرم معلم است.", "The teacher writes on the board.", "معلم روی تخته می‌نویسد.", "an English teacher", "a good teacher", "teacher از teach ساخته شده؛ پسوند -er یعنی «کسی که ... می‌کند»."],
      ["doctor", "پزشک", "a person who helps sick people", "/ˈdɒktər/", "noun", "The doctor works in a hospital.", "پزشک در بیمارستان کار می‌کند.", "My doctor is very kind.", "پزشک من خیلی مهربان است.", "see a doctor", "a family doctor", "go to the doctor یعنی به پزشک مراجعه کردن."],
      ["nurse", "پرستار", "a person who cares for sick people", "/nɜːrs/", "noun", "The nurse helps the doctor.", "پرستار به پزشک کمک می‌کند.", "My aunt is a nurse in a big hospital.", "خالهٔ من در یک بیمارستان بزرگ پرستار است.", "a nurse in a hospital", "a kind nurse", "nurse هم اسم است هم فعل: to nurse یعنی پرستاری کردن."],
      ["driver", "راننده", "a person who drives a car or bus", "/ˈdraɪvər/", "noun", "The bus driver starts work at six.", "رانندهٔ اتوبوس ساعت شش کار را شروع می‌کند.", "My father is a taxi driver.", "پدرم رانندهٔ تاکسی است.", "a bus driver", "a taxi driver", "driver از drive (راندن) ساخته شده است."],
      ["cook", "آشپز", "a person who makes food", "/kʊk/", "noun", "The cook makes soup every day.", "آشپز هر روز سوپ درست می‌کند.", "My uncle is a cook in a restaurant.", "عموی من در یک رستوران آشپز است.", "a good cook", "work as a cook", "cook هم فعل است (پختن) هم اسم (آشپز)."],
      ["farmer", "کشاورز", "a person who works on a farm", "/ˈfɑːrmər/", "noun", "The farmer gets up early.", "کشاورز زود بیدار می‌شود.", "My grandfather is a farmer with ten cows.", "پدربزرگ من کشاورزی با ده گاو است.", "a young farmer", "work as a farmer", "farmer از farm (مزرعه) می‌آید."],
    ],
    blanks: [
      ["My mother is a ___ at the hospital.", ["doctor", "nurse"], "هم doctor و هم nurse در بیمارستان کار می‌کنند؛ هر دو پیش از خود a می‌گیرند."],
      ["A ___ works in a kitchen.", ["cook", "chef"], "cook و chef هر دو آشپز هستند؛ واژهٔ دیگری با این معنا در این درس نیست."],
    ],
    translation: [
      "پدر من رانندهٔ تاکسی است.",
      ["my father is a taxi driver", "my dad is a taxi driver"],
      "برای گفتن شغل از am/is/are + a + نام شغل استفاده می‌کنیم.",
    ],
    productionPrompt: "پدربزرگ من کشاورزی است که ده گاو دارد.",
    productionHint: "برای توصیف شغل: is a + نام شغل.",
  },
  {
    slug: "belongings",
    topic: "Everyday objects",
    title: "Personal Belongings",
    titleFa: "وسایل شخصی",
    minutes: 5,
    rule:
      "برای گفتن اینکه چیزی مال ماست از my استفاده می‌کنیم: my bag.\n" +
      "برای پرسیدن مالکیت می‌گوییم Is this your bag? و پاسخ کوتاه Yes, it is. یا No, it isn't.\n" +
      "پیش از واژه‌ای که با صدای صدادار شروع می‌شود an می‌آید: an umbrella.",
    table: [
      ["a bag", "کیف"],
      ["a key", "کلید"],
      ["a phone", "تلفن همراه"],
      ["a wallet", "کیف پول"],
      ["a watch", "ساعت مچی"],
      ["an umbrella", "چتر"],
    ],
    examples: [
      ["This is my bag.", "این کیف من است."],
      ["Is this your key?", "این کلید توست؟"],
      ["My phone is in my bag.", "تلفن من در کیفم است."],
    ],
    errors: [
      "«a umbrella» غلط است؛ چون umbrella با صدای صدادار شروع می‌شود: an umbrella.",
      "watch ساعت مچی است؛ clock ساعت دیواری است.",
    ],
    pron:
      "key یک هجاست و /kiː/ خوانده می‌شود؛ حرف e صدای /iː/ می‌دهد.\n" +
      "watch با /w/ شروع می‌شود و t آن شنیده نمی‌شود: /wɒtʃ/.\n" +
      "wallet تکیه روی هجای اول دارد: /ˈwɒlɪt/.",
    phonology: "phonology.final-consonants",
    listenNote: "key با /iː/ بلند است و opens با /z/ پایانی خوانده می‌شود",
    recallWord: 3,
    listenWord: 1,
    produceWord: 4,
    words: [
      ["bag", "کیف", "something you carry things in", "/bæɡ/", "noun", "My bag is on the chair.", "کیف من روی صندلی است.", "I put my books in my bag.", "کتاب‌هایم را در کیفم می‌گذارم.", "a school bag", "in my bag", "bag هم کیف دستی است هم کیف مدرسه."],
      ["key", "کلید", "a small metal thing that opens a door", "/kiː/", "noun", "This key opens the front door.", "این کلید درِ ورودی را باز می‌کند.", "I keep my key in my pocket.", "کلیدم را در جیبم نگه می‌دارم.", "my house key", "a small key", "key یک هجا دارد و با /iː/ کشیده خوانده می‌شود."],
      ["phone", "تلفن همراه", "a thing you use to call people", "/foʊn/", "noun", "My phone is in my bag.", "تلفن من در کیفم است.", "I call my mother on my phone.", "با تلفنم به مادرم زنگ می‌زنم.", "a mobile phone", "answer the phone", "phone کوتاه‌شدهٔ telephone است."],
      ["wallet", "کیف پول", "a small bag for money and cards", "/ˈwɒlɪt/", "noun", "My wallet is in my jacket.", "کیف پولم در کاپشنم است.", "There is no money in my wallet.", "پولی در کیف پولم نیست.", "open my wallet", "a brown wallet", "wallet برای پول و کارت است و از bag کوچک‌تر."],
      ["watch", "ساعت مچی", "a small clock you wear on your arm", "/wɒtʃ/", "noun", "My watch is five minutes fast.", "ساعت مچی من پنج دقیقه جلوست.", "He looks at his watch.", "او به ساعت مچی‌اش نگاه می‌کند.", "wear a watch", "a new watch", "watch هم اسم است (ساعت مچی) هم فعل (تماشا کردن)."],
      ["umbrella", "چتر", "a thing you open in the rain", "/ʌmˈbrelə/", "noun", "Take an umbrella with you.", "چتری با خودت ببر.", "My umbrella is next to the door.", "چتر من کنار در است.", "an umbrella", "open an umbrella", "پیش از umbrella حرف تعریف an می‌آید، چون با صدای صدادار شروع می‌شود."],
    ],
    blanks: [
      ["I put my ___ in my bag.", ["phone", "key", "wallet", "watch"], "هر چهار واژه از وسایل شخصی این درس‌اند و در کیف جا می‌شوند."],
      ["I look at my ___ to see the time.", ["watch", "phone"], "برای دیدن ساعت، هم watch و هم phone را می‌توان نگاه کرد."],
    ],
    translation: [
      "کلید من در کیفم است.",
      ["my key is in my bag", "my keys are in my bag"],
      "برای مالکیت my و برای مکان in می‌آید: in my bag.",
    ],
    productionPrompt: "ساعت مچی من پنج دقیقه جلوست.",
    productionHint: "برای مالکیت از my استفاده کنید و is را جا نیندازید.",
  },
  {
    slug: "home-rooms",
    topic: "Home",
    title: "Home and Rooms",
    titleFa: "خانه و اتاق‌ها",
    minutes: 6,
    rule:
      "خانهٔ خودمان را با my house یا our flat می‌گوییم و پیش از آن a می‌آید: a house, a flat.\n" +
      "برای گفتن اینکه چیزی کجاست از in, on, next to استفاده می‌کنیم: in the kitchen, next to the bedroom.\n" +
      "There is برای یک چیز و There are برای چند چیز می‌آید: There are three rooms.",
    table: [
      ["a house", "خانه"],
      ["a room", "اتاق"],
      ["a kitchen", "آشپزخانه"],
      ["a bedroom", "اتاق خواب"],
      ["a bathroom", "حمام"],
      ["a garden", "حیاط و باغ"],
    ],
    examples: [
      ["We live in a small house.", "ما در خانهٔ کوچکی زندگی می‌کنیم."],
      ["My bedroom is next to the bathroom.", "اتاق خواب من کنار حمام است."],
      ["The children play in the garden.", "بچه‌ها در حیاط بازی می‌کنند."],
    ],
    errors: [
      "«We live in house» غلط است؛ a house درست است.",
      "بعد از next حرف to می‌آید: next to the door، نه next the door.",
    ],
    pron:
      "house با /haʊ/ خوانده می‌شود و جمع آن houses پایانهٔ /zɪz/ می‌گیرد: /ˈhaʊzɪz/.\n" +
      "kitchen با /tʃ/ در میان است: /ˈkɪtʃɪn/.\n" +
      "garden تکیه روی هجای اول دارد: /ˈɡɑːrdn/.",
    phonology: "phonology.vowel-length",
    listenNote: "have با /h/ و /æ/ کوتاه می‌آید و garden تکیه روی هجای اول دارد",
    recallWord: 4,
    listenWord: 5,
    produceWord: 2,
    words: [
      ["house", "خانه", "a building where people live", "/haʊs/", "noun", "Our house is near the park.", "خانهٔ ما نزدیک پارک است.", "The house has three rooms.", "این خانه سه اتاق دارد.", "a big house", "my house", "house ساختمان مسکونی است؛ home یعنی محل زندگی."],
      ["room", "اتاق", "a part of a house with walls", "/ruːm/", "noun", "My room is small but clean.", "اتاق من کوچک اما تمیز است.", "There is a bed in my room.", "در اتاقم یک تخت هست.", "a big room", "my room", "room هم اتاق است هم «فضا»: no room in the bag."],
      ["kitchen", "آشپزخانه", "the room where you cook", "/ˈkɪtʃɪn/", "noun", "We cook in the kitchen.", "ما در آشپزخانه غذا می‌پزیم.", "My mother is in the kitchen.", "مادرم در آشپزخانه است.", "in the kitchen", "a small kitchen", "kitchen آشپزخانه است؛ chicken مرغ."],
      ["bedroom", "اتاق خواب", "the room where you sleep", "/ˈbedruːm/", "noun", "My bedroom is upstairs.", "اتاق خواب من در طبقهٔ بالا است.", "The bedroom has a big window.", "اتاق خواب یک پنجرهٔ بزرگ دارد.", "my bedroom", "a small bedroom", "bedroom از bed + room ساخته شده است."],
      ["bathroom", "حمام", "the room where you wash", "/ˈbɑːθruːm/", "noun", "The bathroom is next to my bedroom.", "حمام کنار اتاق خواب من است.", "There is a towel in the bathroom.", "حوله‌ای در حمام است.", "in the bathroom", "a clean bathroom", "bathroom جای شست‌وشو است؛ toilet فقط دستشویی."],
      ["garden", "حیاط و باغ", "the land behind or around a house", "/ˈɡɑːrdn/", "noun", "We have a small garden.", "ما یک حیاط کوچک داریم.", "The children play in the garden.", "بچه‌ها در حیاط بازی می‌کنند.", "in the garden", "a small garden", "garden در انگلیسی بریتانیایی یعنی حیاط پشت خانه."],
    ],
    blanks: [
      ["Our flat has two ___.", ["rooms", "bedrooms"], "هم rooms و هم bedrooms با two و به‌صورت جمع می‌آیند."],
      ["We eat dinner in the ___.", ["kitchen", "garden"], "شام را هم در آشپزخانه و هم در حیاط می‌شود خورد."],
    ],
    translation: [
      "خانهٔ ما نزدیک پارک است.",
      ["our house is near the park", "our house is next to the park"],
      "for توصیف مکان near یا next to به کار می‌رود و house با our می‌آید.",
    ],
    productionPrompt: "ما در آشپزخانه غذا می‌پزیم.",
    productionHint: "برای گفتن «در» یک مکان از in + the + مکان استفاده کنید.",
  },
  {
    slug: "house-objects",
    topic: "Home",
    title: "Household Objects",
    titleFa: "وسایل خانه",
    minutes: 5,
    rule:
      "برای اشاره به چیز مشخص از the استفاده می‌کنیم: the door, the window.\n" +
      "برای گفتن اینکه چیزی روی چیزی است on و برای داخل در چیزی in می‌آید: on the table, in the box.\n" +
      "There is / There are برای معرفی چیز موجود به کار می‌رود: There is a lamp on the table.",
    table: [
      ["a table", "میز"],
      ["a chair", "صندلی"],
      ["a window", "پنجره"],
      ["a door", "در"],
      ["a bed", "تخت"],
      ["a lamp", "چراغ"],
    ],
    examples: [
      ["There is a lamp on the table.", "یک چراغ روی میز است."],
      ["Please close the door.", "لطفاً در را ببند."],
      ["My bed is next to the window.", "تختم کنار پنجره است."],
    ],
    errors: [
      "«There is two chairs» غلط است؛ با جمع باید There are بیاید.",
      "lamp چراغ روشنایی است؛ light هم به کار می‌رود ولی برای نور طبیعی هم می‌آید.",
    ],
    pron:
      "window با /w/ شروع می‌شود و تکیه روی اول دارد: /ˈwɪndoʊ/.\n" +
      "door واکهٔ بلند /ɔːr/ دارد: /dɔːr/.\n" +
      "lamp با /æ/ کوتاه و /mp/ پایانی: /læmp/.",
    phonology: "phonology.final-consonants",
    listenNote: "close با /z/ خوانده می‌شود نه /s/ و door واکهٔ بلند /ɔːr/ دارد",
    recallWord: 4,
    listenWord: 3,
    produceWord: 2,
    words: [
      ["table", "میز", "furniture you eat or work at", "/ˈteɪbl/", "noun", "There is a lamp on the table.", "چراغی روی میز است.", "We eat at the table.", "ما سر میز غذا می‌خوریم.", "on the table", "a small table", "table میز است؛ desk میز کار و مطالعه."],
      ["chair", "صندلی", "furniture you sit on", "/tʃer/", "noun", "The chair is next to the door.", "صندلی کنار در است.", "Please sit on the chair.", "لطفاً روی صندلی بنشین.", "on the chair", "a wooden chair", "chair یک صندلی است؛ جمع آن chairs."],
      ["window", "پنجره", "the glass part of a wall", "/ˈwɪndoʊ/", "noun", "Please open the window.", "لطفاً پنجره را باز کن.", "The window is next to my bed.", "پنجره کنار تختم است.", "open the window", "a big window", "window پنجره است؛ door در."],
      ["door", "در", "the thing you open to enter a room", "/dɔːr/", "noun", "Please close the door.", "لطفاً در را ببند.", "The door is open.", "در باز است.", "open the door", "the front door", "door با واکهٔ بلند /ɔː/ خوانده می‌شود."],
      ["bed", "تخت", "furniture you sleep on", "/bed/", "noun", "My bed is next to the window.", "تختم کنار پنجره است.", "There is a cat on the bed.", "گربه‌ای روی تخت است.", "in bed", "go to bed", "in bed یعنی در رختخواب؛ go to bed یعنی رفتن برای خواب."],
      ["lamp", "چراغ", "a thing that gives light", "/læmp/", "noun", "There is a lamp next to the bed.", "چراغی کنار تخت است.", "Turn on the lamp, please.", "لطفاً چراغ را روشن کن.", "turn on the lamp", "a small lamp", "lamp چراغ روشنایی است؛ chandelier لوستر."],
    ],
    blanks: [
      ["Please open the ___.", ["door", "window"], "هم door و هم window را می‌شود باز کرد؛ هر دو با open می‌آیند."],
      ["Please sit on the ___.", ["chair", "bed"], "نشستن روی chair و bed هر دو ممکن است."],
    ],
    translation: [
      "چراغ روی میز است.",
      ["the lamp is on the table", "there is a lamp on the table"],
      "برای گفتن «روی» از on استفاده می‌کنیم و چیز مشخص با the می‌آید.",
    ],
    productionPrompt: "لطفاً پنجره را باز کن.",
    productionHint: "برای درخواست مؤدبانه: Please + فعل ساده.",
  },
  {
    slug: "food-drinks",
    topic: "Food",
    title: "Food and Drinks",
    titleFa: "غذاها و نوشیدنی‌ها",
    minutes: 6,
    rule:
      "برای گفتن چیزی که می‌خوریم یا می‌نوشیم از I eat / I drink استفاده می‌کنیم.\n" +
      "غذاهای غیرقابل‌شمارش (bread, rice, water, milk) با some می‌آیند: some rice, some water.\n" +
      "برای شمردن آن‌ها از پیمانه استفاده می‌کنیم: a glass of water, a cup of tea.",
    table: [
      ["bread", "نان"],
      ["rice", "برنج"],
      ["water", "آب"],
      ["tea", "چای"],
      ["milk", "شیر"],
      ["an egg", "تخم‌مرغ"],
    ],
    examples: [
      ["I eat bread for breakfast.", "من برای صبحانه نان می‌خورم."],
      ["Can I have a glass of water?", "می‌توانم یک لیوان آب داشته باشم؟"],
      ["She drinks tea with milk.", "او چای را با شیر می‌نوشد."],
    ],
    errors: [
      "«I want breads» غلط است؛ bread غیرقابل‌شمارش است: some bread یا a piece of bread.",
      "«eat water» غلط است؛ آب را می‌نوشند: drink water.",
    ],
    pron:
      "bread با /e/ کوتاه خوانده می‌شود: /bred/.\n" +
      "rice با /aɪ/ است: /raɪs/.\n" +
      "milk با /ɪ/ کوتاه و /lk/ پایانی: /mɪlk/.",
    phonology: "phonology.vowel-length",
    listenNote: "milk با /m/ شروع و با /k/ تمام می‌شود و drinks سوم‌شخص با s می‌آید",
    recallWord: 3,
    listenWord: 4,
    produceWord: 5,
    words: [
      ["bread", "نان", "food made from flour", "/bred/", "noun", "I eat bread for breakfast.", "من برای صبحانه نان می‌خورم.", "Can I have some bread, please?", "می‌شود کمی نان بدید؟", "some bread", "fresh bread", "bread غیرقابل‌شمارش است؛ «یک نان» می‌شود a loaf of bread."],
      ["rice", "برنج", "small white grains you cook", "/raɪs/", "noun", "We eat rice with chicken.", "ما برنج را با مرغ می‌خوریم.", "Please cook some rice.", "لطفاً کمی برنج بپز.", "some rice", "rice and chicken", "rice غیرقابل‌شمارش است؛ برای یک دانه می‌گوییم a grain of rice."],
      ["water", "آب", "the clear drink people need every day", "/ˈwɔːtər/", "noun", "I drink water every day.", "من هر روز آب می‌نوشم.", "Can I have a glass of water?", "می‌توانم یک لیوان آب داشته باشم؟", "a glass of water", "cold water", "water غیرقابل‌شمارش است؛ a glass of water درست است."],
      ["tea", "چای", "a hot drink made from leaves", "/tiː/", "noun", "My father drinks tea in the morning.", "پدرم صبح‌ها چای می‌نوشد.", "Would you like some tea?", "کمی چای میل داری؟", "a cup of tea", "hot tea", "tea با /iː/ کشیده خوانده می‌شود."],
      ["milk", "شیر", "a white drink from cows", "/mɪlk/", "noun", "My son drinks milk at night.", "پسرم شب‌ها شیر می‌نوشد.", "There is milk in the fridge.", "شیر در یخچال هست.", "a glass of milk", "hot milk", "milk نوشیدنی است و با tea هم می‌آید: tea with milk."],
      ["egg", "تخم‌مرغ", "food that comes from a chicken", "/eɡ/", "noun", "I eat an egg for breakfast.", "من برای صبحانه یک تخم‌مرغ می‌خورم.", "There are six eggs in the box.", "شش تخم‌مرغ در جعبه است.", "a boiled egg", "two eggs", "پیش از egg حرف تعریف an می‌آید، چون با صدای صدادار شروع می‌شود."],
    ],
    blanks: [
      ["I would like a glass of ___.", ["water", "milk"], "هم water و هم milk با a glass of می‌آیند."],
      ["I eat ___ for breakfast.", ["bread", "rice", "egg"], "نان، برنج و تخم‌مرغ هر سه صبحانهٔ رایج‌اند و با eat می‌آیند."],
    ],
    translation: [
      "من هر روز آب می‌نوشم.",
      ["i drink water every day", "i drink water everyday"],
      "برای عادت روزانه از every day با فعل سادهٔ drink استفاده می‌کنیم.",
    ],
    productionPrompt: "من برای صبحانه یک تخم‌مرغ می‌خورم.",
    productionHint: "پیش از صدای صدادار a به an تبدیل می‌شود: an egg.",
  },
  {
    slug: "fruits-vegetables",
    topic: "Food",
    title: "Fruits and Vegetables",
    titleFa: "میوه‌ها و سبزیجات",
    minutes: 5,
    rule:
      "میوه‌ها و سبزیجات قابل‌شمارش‌اند: an apple, two apples.\n" +
      "جمع آن‌ها با s یا es ساخته می‌شود: potatoes, tomatoes, onions.\n" +
      "برای گفتن دوست داشتن از I like + جمع استفاده می‌کنیم: I like bananas.",
    table: [
      ["an apple", "سیب"],
      ["an orange", "پرتقال"],
      ["a banana", "موز"],
      ["a tomato", "گوجه‌فرنگی"],
      ["a potato", "سیب‌زمینی"],
      ["an onion", "پیاز"],
    ],
    examples: [
      ["I eat an apple every day.", "من هر روز یک سیب می‌خورم."],
      ["We put tomatoes in the salad.", "ما در سالاد گوجه‌فرنگی می‌ریزیم."],
      ["My mother buys onions at the market.", "مادرم از بازار پیاز می‌خرد."],
    ],
    errors: [
      "«two tomato» غلط است؛ جمع آن tomatoes با es است.",
      "پیش از apple و orange و onion حرف تعریف an می‌آید، نه a.",
    ],
    pron:
      "tomato سه هجا دارد و تکیه روی دوم است: /təˈmeɪtoʊ/.\n" +
      "potato تکیه روی هجای دوم دارد: /pəˈteɪtoʊ/.\n" +
      "orange با /ˈɔːr/ شروع می‌شود و /ɪndʒ/ تمام می‌شود.",
    phonology: "phonology.word-stress",
    listenNote: "tomato تکیه روی هجای دوم دارد و a پیش از آن کوتاه خوانده می‌شود",
    recallWord: 1,
    listenWord: 3,
    produceWord: 2,
    words: [
      ["apple", "سیب", "a round red or green fruit", "/ˈæpl/", "noun", "I eat an apple every day.", "من هر روز یک سیب می‌خورم.", "This apple is very sweet.", "این سیب خیلی شیرین است.", "a red apple", "an apple a day", "پیش از apple حرف تعریف an می‌آید."],
      ["orange", "پرتقال", "a round orange fruit", "/ˈɔːrɪndʒ/", "noun", "Would you like an orange?", "یک پرتقال میل داری؟", "I drink orange juice in the morning.", "صبح‌ها آب‌پرتقال می‌نوشم.", "orange juice", "a sweet orange", "orange هم میوه است هم رنگ؛ معنی را از جمله بفهمید."],
      ["banana", "موز", "a long yellow fruit", "/bəˈnɑːnə/", "noun", "I eat a banana after lunch.", "من بعد از ناهار یک موز می‌خورم.", "Bananas are yellow.", "موزها زرد هستند.", "a ripe banana", "banana bread", "banana سه هجا دارد و تکیه روی دوم است."],
      ["tomato", "گوجه‌فرنگی", "a soft red fruit used in salad", "/təˈmeɪtoʊ/", "noun", "We put tomatoes in the salad.", "ما در سالاد گوجه‌فرنگی می‌ریزیم.", "A tomato is red.", "گوجه‌فرنگی قرمز است.", "a red tomato", "tomato salad", "جمع آن tomatoes است، با es."],
      ["potato", "سیب‌زمینی", "a round vegetable that grows underground", "/pəˈteɪtoʊ/", "noun", "My mother cooks potatoes for dinner.", "مادرم برای شام سیب‌زمینی می‌پزد.", "A potato grows under the ground.", "سیب‌زمینی زیر خاک رشد می‌کند.", "boiled potatoes", "a big potato", "جمع آن potatoes است، با es."],
      ["onion", "پیاز", "a round vegetable with a strong taste", "/ˈʌnjən/", "noun", "The onion makes me cry.", "پیاز مرا به گریه می‌اندازد.", "We cut the onion into small pieces.", "پیاز را به تکه‌های کوچک می‌بریم.", "a red onion", "cut the onion", "onion با صدای صدادار شروع می‌شود: an onion."],
    ],
    blanks: [
      ["I eat an ___ every day.", ["apple", "orange"], "هر دو واژه با صدای صدادار شروع می‌شوند و پس از an می‌آیند."],
      ["We put ___ in the salad.", ["tomato", "onion", "potato"], "این سه سبزی همه در سالاد می‌آیند."],
    ],
    translation: [
      "من هر روز یک سیب می‌خورم.",
      ["i eat an apple every day", "i eat one apple every day"],
      "برای کار روزانه every day و برای یک عدد a/an به کار می‌رود.",
    ],
    productionPrompt: "من بعد از ناهار یک موز می‌خورم.",
    productionHint: "برای گفتن «بعد از» از after + وعدهٔ غذایی استفاده کنید.",
  },
  {
    slug: "clothes",
    topic: "Everyday objects",
    title: "Clothes",
    titleFa: "لباس‌ها",
    minutes: 5,
    rule:
      "برای گفتن اینکه چیزی را پوشیده‌ایم از wear یا have on استفاده می‌کنیم: I am wearing a blue shirt.\n" +
      "بعضی واژه‌های لباس همیشه جمع‌اند: trousers, shoes. برای یک عدد می‌گوییم a pair of trousers.\n" +
      "برای پوشیدن در یک لحظه put on به کار می‌رود: Put on your coat.",
    table: [
      ["a shirt", "پیراهن"],
      ["trousers", "شلوار"],
      ["shoes", "کفش"],
      ["a coat", "پالتو"],
      ["a hat", "کلاه"],
      ["a dress", "پیراهن زنانه"],
    ],
    examples: [
      ["He is wearing a white shirt.", "او یک پیراهن سفید پوشیده است."],
      ["My shoes are new.", "کفش‌های من نو هستند."],
      ["Put on your coat; it is cold.", "پالتویت را بپوش؛ هوا سرد است."],
    ],
    errors: [
      "«a trousers» غلط است؛ می‌گوییم a pair of trousers یا trousers.",
      "shoes جمع است؛ برای یک کفش a shoe می‌گوییم.",
    ],
    pron:
      "shoes با /ʃ/ شروع می‌شود: /ʃuːz/.\n" +
      "clothes صدای th آن تقریباً حذف می‌شود: /kloʊðz/.\n" +
      "trousers با /traʊ/ و تکیه روی اول: /ˈtraʊzərz/.",
    phonology: "phonology.final-consonants",
    listenNote: "shirt با /ʃ/ شروع می‌شود و /ɜːr/ دارد؛ wearing با /w/",
    recallWord: 4,
    listenWord: 0,
    produceWord: 1,
    words: [
      ["shirt", "پیراهن", "clothes for the top of your body", "/ʃɜːrt/", "noun", "He is wearing a white shirt.", "او یک پیراهن سفید پوشیده است.", "My shirt is on the chair.", "پیراهنم روی صندلی است.", "a white shirt", "wear a shirt", "shirt پیراهن است؛ T-shirt تی‌شرت."],
      ["trousers", "شلوار", "clothes for your legs", "/ˈtraʊzərz/", "noun", "These trousers are too long.", "این شلوار خیلی بلند است.", "I wear trousers to work.", "من سر کار شلوار می‌پوشم.", "a pair of trousers", "black trousers", "trousers همیشه جمع است؛ برای یک عدد a pair of trousers می‌گوییم."],
      ["shoes", "کفش", "things you wear on your feet", "/ʃuːz/", "noun", "My shoes are new.", "کفش‌های من نو هستند.", "Take off your shoes at the door.", "کنار در کفش‌هایت را دربیاور.", "a pair of shoes", "new shoes", "shoes جمع است؛ یک کفش را a shoe می‌گوییم."],
      ["coat", "پالتو", "a warm thing you wear outside", "/koʊt/", "noun", "Put on your coat; it is cold.", "پالتویت را بپوش؛ هوا سرد است.", "My coat is black.", "پالتوی من مشکی است.", "a warm coat", "put on a coat", "coat لباس بیرونی است."],
      ["hat", "کلاه", "something you wear on your head", "/hæt/", "noun", "He wears a hat in the sun.", "او زیر آفتاب کلاه می‌گذارد.", "My hat is on the table.", "کلاهم روی میز است.", "wear a hat", "a big hat", "hat کلاه لبه‌دار است؛ cap کلاه بی‌لبه."],
      ["dress", "پیراهن زنانه", "clothes a woman wears", "/dres/", "noun", "She is wearing a red dress.", "او یک پیراهن قرمز پوشیده است.", "Her dress is very long.", "پیراهنش خیلی بلند است.", "a red dress", "a long dress", "dress لباس زنانه است؛ لباس رسمی مردانه suit است."],
    ],
    blanks: [
      ["I wear a ___ when it is cold.", ["coat", "hat"], "هم coat و هم hat در هوای سرد پوشیده می‌شوند."],
      ["She is wearing a red ___.", ["dress", "shirt", "coat", "hat"], "همهٔ این‌ها را می‌شود پوشید و صفت red پیش از آن‌ها می‌آید."],
    ],
    translation: [
      "کفش‌های من نو هستند.",
      ["my shoes are new", "my shoes are new ones"],
      "shoes جمع است، پس فعل are می‌آید.",
    ],
    productionPrompt: "این شلوار خیلی بلند است.",
    productionHint: "برای اشاره به جمع از these + are استفاده کنید.",
  },
  {
    slug: "body",
    topic: "Health",
    title: "Parts of the Body",
    titleFa: "اعضای بدن",
    minutes: 5,
    rule:
      "اعضای زوج بدن جمع می‌آیند: eyes, ears, hands, feet.\n" +
      "برای گفتن درد از have got a + اسم استفاده می‌کنیم: I have got a headache.\n" +
      "چند جمع بی‌قاعده را حفظ کنید: foot → feet و tooth → teeth.",
    table: [
      ["a head", "سر"],
      ["a hand", "دست"],
      ["an eye", "چشم"],
      ["an ear", "گوش"],
      ["a foot", "پا"],
      ["a tooth", "دندان"],
    ],
    examples: [
      ["Wash your hands before dinner.", "پیش از شام دست‌هایت را بشور."],
      ["My eyes are blue.", "چشم‌های من آبی هستند."],
      ["I have got a toothache.", "دندانم درد می‌کند."],
    ],
    errors: [
      "«two foots» غلط است؛ جمع foot می‌شود feet.",
      "«a ear» غلط است؛ an ear درست است.",
    ],
    pron:
      "tooth واکهٔ /uː/ دارد: /tuːθ/ و جمع آن teeth با /iː/ است.\n" +
      "foot /fʊt/ با /ʊ/ کوتاه و feet /fiːt/ با /iː/ بلند.\n" +
      "ear با /ɪr/ شروع می‌شود و ر با زبان انگلیسی خوانده نمی‌شود.",
    phonology: "phonology.th-sounds",
    listenNote: "foot با /ʊ/ کوتاه است و hurts با /ɜːr/ و s سوم‌شخص می‌آید",
    recallWord: 5,
    listenWord: 4,
    produceWord: 1,
    words: [
      ["head", "سر", "the top part of your body", "/hed/", "noun", "My head hurts.", "سرم درد می‌کند.", "Put your hat on your head.", "کلاهت را روی سرت بگذار.", "shake your head", "a headache", "headache یعنی سردرد و با a می‌آید."],
      ["hand", "دست", "the part you use to hold things", "/hænd/", "noun", "Wash your hands before dinner.", "پیش از شام دست‌هایت را بشور.", "My left hand is cold.", "دست چپم سرد است.", "shake hands", "raise your hand", "hand هم دست است هم کمک کردن: give me a hand."],
      ["eye", "چشم", "the part you see with", "/aɪ/", "noun", "My eyes are blue.", "چشم‌های من آبی هستند.", "Close your eyes, please.", "لطفاً چشمانت را ببند.", "open your eyes", "blue eyes", "جمع eye می‌شود eyes؛ پیش از آن an می‌آید."],
      ["ear", "گوش", "the part you hear with", "/ɪr/", "noun", "My ear hurts.", "گوشم درد می‌کند.", "He has small ears.", "او گوش‌های کوچکی دارد.", "my left ear", "big ears", "پیش از ear حرف تعریف an می‌آید."],
      ["foot", "پا", "the part at the end of your leg", "/fʊt/", "noun", "My foot hurts.", "پایم درد می‌کند.", "I stand on one foot.", "روی یک پا می‌ایستم.", "on foot", "my right foot", "جمع foot می‌شود feet؛ on foot یعنی پیاده."],
      ["tooth", "دندان", "the white part in your mouth", "/tuːθ/", "noun", "My tooth hurts.", "دندانم درد می‌کند.", "Brush your teeth at night.", "شب‌ها دندان‌هایت را مسواک بزن.", "brush your teeth", "a white tooth", "جمع tooth می‌شود teeth، نه tooths."],
    ],
    blanks: [
      ["I see with my ___.", ["eyes", "eye"], "با چشم دیده می‌شود؛ هم شکل مفرد و هم جمع درست است."],
      ["I hear with my ___.", ["ears", "ear"], "شنیدن با گوش است؛ هم ear و هم ears پذیرفته می‌شود."],
    ],
    translation: [
      "دندان من درد می‌کند.",
      ["my tooth hurts", "i have got a toothache", "i have a toothache"],
      "برای درد دندان هم hurt و هم toothache به کار می‌رود.",
    ],
    productionPrompt: "دست‌هایت را پیش از شام بشور.",
    productionHint: "جملهٔ امری با فعل ساده می‌آید و فاعل نمی‌خواهد.",
  },
  {
    slug: "animals",
    topic: "Animals",
    title: "Animals",
    titleFa: "حیوانات",
    minutes: 5,
    rule:
      "برای گفتن توانایی از can استفاده می‌کنیم: A bird can fly.\n" +
      "پس از can همیشه فعل ساده می‌آید، بدون s و to.\n" +
      "برای توصیف حیوان از is/are + صفت استفاده می‌کنیم: The dog is big.",
    table: [
      ["a dog", "سگ"],
      ["a cat", "گربه"],
      ["a bird", "پرنده"],
      ["a horse", "اسب"],
      ["a cow", "گاو"],
      ["a fish", "ماهی"],
    ],
    examples: [
      ["The dog is in the garden.", "سگ در حیاط است."],
      ["A bird can fly.", "پرنده می‌تواند پرواز کند."],
      ["We have two cats and one dog.", "ما دو گربه و یک سگ داریم."],
    ],
    errors: [
      "«A bird can flies» غلط است؛ پس از can فعل ساده می‌آید.",
      "جمع fish همان fish است: two fish.",
    ],
    pron:
      "cow با /aʊ/ خوانده می‌شود: /kaʊ/.\n" +
      "horse واکهٔ بلند /ɔːr/ دارد: /hɔːrs/.\n" +
      "bird با /ɜːr/ است: /bɜːrd/ و r آن شنیده می‌شود.",
    phonology: "phonology.vowel-length",
    listenNote: "cat با /æ/ کوتاه است و drinks با /dr/ شروع می‌شود و s سوم‌شخص دارد",
    recallWord: 5,
    listenWord: 1,
    produceWord: 3,
    words: [
      ["dog", "سگ", "an animal that often lives with people", "/dɔːɡ/", "noun", "The dog is in the garden.", "سگ در حیاط است.", "My dog sleeps next to my bed.", "سگم کنار تختم می‌خوابد.", "a big dog", "walk the dog", "dog سگ است؛ puppy توله‌سگ."],
      ["cat", "گربه", "a small animal that says meow", "/kæt/", "noun", "The cat is on the chair.", "گربه روی صندلی است.", "My cat drinks milk.", "گربهٔ من شیر می‌نوشد.", "a black cat", "feed the cat", "cat گربه است؛ kitten بچه‌گربه."],
      ["bird", "پرنده", "an animal with wings", "/bɜːrd/", "noun", "A bird can fly.", "پرنده می‌تواند پرواز کند.", "The bird is in the tree.", "پرنده در درخت است.", "a small bird", "birds sing", "bird پرنده است؛ جمع آن birds."],
      ["horse", "اسب", "a big animal people ride", "/hɔːrs/", "noun", "The horse runs very fast.", "اسب خیلی تند می‌دود.", "My uncle has a big horse.", "عمویم یک اسب بزرگ دارد.", "ride a horse", "a brown horse", "horse اسب است؛ donkey خر."],
      ["cow", "گاو", "a big farm animal that gives milk", "/kaʊ/", "noun", "The cow gives us milk.", "گاو به ما شیر می‌دهد.", "There are ten cows on the farm.", "ده گاو در مزرعه است.", "a big cow", "milk a cow", "cow گاو ماده است؛ bull گاو نر."],
      ["fish", "ماهی", "an animal that lives in water", "/fɪʃ/", "noun", "The fish is in the water.", "ماهی در آب است.", "We eat fish on Friday.", "ما جمعه‌ها ماهی می‌خوریم.", "a small fish", "fish and rice", "جمع fish همان fish است؛ fishes برای گونه‌های مختلف."],
    ],
    blanks: [
      ["The ___ is sleeping on the bed.", ["cat", "dog"], "گربه و سگ هر دو روی تخت می‌خوابند."],
      ["The ___ can swim.", ["fish", "dog"], "ماهی و سگ هر دو شنا می‌کنند."],
    ],
    translation: [
      "گربه روی صندلی است.",
      ["the cat is on the chair", "a cat is on the chair"],
      "برای گفتن «روی» از on استفاده می‌کنیم و فاعل مفرد با is می‌آید.",
    ],
    productionPrompt: "اسب خیلی تند می‌دود.",
    productionHint: "برای توصیف با قید: فعل + very + قید.",
  },
  {
    slug: "hobbies",
    topic: "Free time",
    title: "Likes and Hobbies",
    titleFa: "علایق و سرگرمی‌ها",
    minutes: 6,
    rule:
      "برای گفتن علاقه از like, love و enjoy استفاده می‌کنیم.\n" +
      "پس از این فعل‌ها فعل با -ing می‌آید: I like reading. I enjoy cooking.\n" +
      "برای پرسیدن علاقه می‌گوییم What do you like doing? و پاسخ کوتاه می‌دهیم.",
    table: [
      ["a hobby", "سرگرمی"],
      ["music", "موسیقی"],
      ["a sport", "ورزش"],
      ["a film", "فیلم"],
      ["a book", "کتاب"],
      ["a game", "بازی"],
    ],
    examples: [
      ["My hobby is reading.", "سرگرمی من کتاب خواندن است."],
      ["I like listening to music.", "من دوست دارم موسیقی گوش کنم."],
      ["We watch a film every Friday.", "ما هر جمعه یک فیلم می‌بینیم."],
    ],
    errors: [
      "«I like read» غلط است؛ I like reading درست است.",
      "music را با the نیاورید: I like music، نه I like the music.",
    ],
    pron:
      "hobby تکیه روی هجای اول دارد: /ˈhɒbi/.\n" +
      "music با /ˈmjuː/ شروع می‌شود: /ˈmjuːzɪk/.\n" +
      "film با /f/ شروع و /lm/ تمام می‌شود.",
    phonology: "phonology.word-stress",
    listenNote: "film با /f/ شروع می‌شود و /lm/ پایانی دارد؛ funny تکیه روی اول",
    recallWord: 0,
    listenWord: 3,
    produceWord: 4,
    meaningWords: [1, 3, 5],
    words: [
      ["hobby", "سرگرمی", "something you do for fun in your free time", "/ˈhɒbi/", "noun", "My hobby is reading.", "سرگرمی من کتاب خواندن است.", "What is your hobby?", "سرگرمی تو چیست؟", "my favourite hobby", "a new hobby", "hobby کاری است که در اوقات فراغت برای لذت انجام می‌دهیم."],
      ["music", "موسیقی", "sounds made by instruments or voices", "/ˈmjuːzɪk/", "noun", "I listen to music every day.", "من هر روز موسیقی گوش می‌دهم.", "The music is very loud.", "موسیقی خیلی بلند است.", "listen to music", "live music", "پیش از music حرف تعریف نمی‌آید: I like music."],
      ["sport", "ورزش", "a game with physical activity", "/spɔːrt/", "noun", "My favourite sport is football.", "ورزش موردعلاقهٔ من فوتبال است.", "He plays a sport every weekend.", "او هر آخر هفته یک ورزش انجام می‌دهد.", "play a sport", "a popular sport", "sport در انگلیسی بریتانیایی هم مفرد هم جمع می‌آید."],
      ["film", "فیلم", "a story you watch on a screen", "/fɪlm/", "noun", "We watch a film every Friday.", "ما هر جمعه یک فیلم می‌بینیم.", "This film is very funny.", "این فیلم خیلی خنده‌دار است.", "watch a film", "a good film", "film در انگلیسی بریتانیایی و movie در آمریکایی رایج است."],
      ["book", "کتاب", "pages with words you read", "/bʊk/", "noun", "I read a book before bed.", "من پیش از خواب یک کتاب می‌خوانم.", "This book is about animals.", "این کتاب دربارهٔ حیوانات است.", "read a book", "a good book", "book هم اسم است هم فعل (رزرو کردن): book a table."],
      ["game", "بازی", "something you play with rules", "/ɡeɪm/", "noun", "We play a game after dinner.", "ما بعد از شام یک بازی می‌کنیم.", "My favourite game is chess.", "بازی موردعلاقهٔ من شطرنج است.", "play a game", "a computer game", "game با g سخت خوانده می‌شود: /ɡeɪm/."],
    ],
    blanks: [
      ["In my free time I play a ___ with my friends.", ["game", "sport"], "هم game و هم sport را با play می‌آوریم."],
      ["I watch a ___ on TV.", ["film", "game"], "فیلم و مسابقه هر دو روی تلویزیون دیده می‌شوند."],
    ],
    translation: [
      "من هر روز موسیقی گوش می‌دهم.",
      ["i listen to music every day", "i listen to music everyday"],
      "با music حرف تعریف نمی‌آید و listen به to نیاز دارد: listen to music.",
    ],
    productionPrompt: "من پیش از خواب یک کتاب می‌خوانم.",
    productionHint: "برای گفتن «پیش از» از before + اسم استفاده کنید.",
  },
];
