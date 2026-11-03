// Conversation dialogues, A1 (first half): one lesson = one situation, four turns.
//
// Shape: [lessonId, sceneFa, partnerFa, turns[]]
// turn:  [question, questionFa, taskFa, answers[], tipFa]
//
// The partner is the person the lesson's topic puts the learner opposite: a
// teacher for the alphabet, a shop assistant for shopping, a receptionist at a
// hotel. Answers are spoken, so they stay short enough to say in one breath and
// the first one is the model shown under the cue.

export const a1Low = [
  ["a1.alphabet.lesson-01", "کلاس زبان، جلسهٔ اول", "معلم", [
    ["Let's start with the alphabet. Can you say a word that begins with 'b'?", "با الفبا شروع کنیم. کلمه‌ای می‌گویی که با 'b' شروع شود؟", "یک کلمه که با b شروع می‌شود بگو.", ["Book.", "Banana."], "کلمه را تنها و واضح بگو؛ جملهٔ کامل لازم نیست."],
    ["Good. How many letters are there in the alphabet?", "خوب است. الفبای انگلیسی چند حرف دارد؟", "تعداد حروف الفبا را بگو.", ["Twenty-six letters.", "There are twenty-six."], "عدد را به‌صورت کلمه بگو، نه رقم."],
    ["Now let's spell a word. Does 'apple' begin with a vowel?", "حالا یک کلمه را هجی کنیم. apple با حرف صدادار شروع می‌شود؟", "جواب بده و بگو با چه صدایی شروع می‌شود.", ["Yes, it does.", "Yes, it begins with 'a'."], "صدادارهای انگلیسی a، e، i، o، u هستند."],
    ["Very good. Is 'z' near the end of the alphabet?", "خیلی خوب. حرف z نزدیک پایان الفباست؟", "جواب بده و بگو کجاست.", ["Yes, it's the last letter.", "Yes, it's at the end."], "z آخرین حرف الفبای انگلیسی است."],
  ]],

  ["a1.spelling.lesson-01", "میز پذیرش یک آموزشگاه زبان", "منشی آموزشگاه", [
    ["Good morning. Are you here to register for a class?", "صبح بخیر. برای ثبت‌نام در کلاس آمده‌اید؟", "بله بگو و بگو برای کلاس مقدماتی آمده‌ای.", ["Yes, I'd like to join a beginner class.", "Yes, I want to register for a beginner class."], "برای درخواست مؤدبانه از I'd like to استفاده کن."],
    ["Great. Can I have your last name, please?", "عالی. نام خانوادگی‌تان را می‌گویید؟", "نام خانوادگی‌ات را بگو و بخواه هجی کنی.", ["It's on the form, in block capitals.", "I wrote it on the form for you."], "نام‌های فارسی را برای مخاطب انگلیسی‌زبان هجی کن."],
    ["Thank you. And your phone number?", "ممنون. شمارهٔ تلفن‌تان؟", "شمارهٔ تلفن را رقم‌به‌رقم بگو.", ["It's on the form as well.", "The same number as on the form."], "شماره را در گروه‌های سه‌رقمی بخوان تا طرف مقابل راحت بنویسد."],
    ["Perfect. When would you like to have your first lesson?", "عالی. اولین درس را چه زمانی می‌خواهید؟", "بگو شنبه صبح‌ها وقت داری.", ["Saturday mornings are good for me.", "I'd like a class on Saturday morning, please."], "روزهای هفته با on می‌آیند: on Saturday."],
  ]],

  ["a1.numbers.lesson-01", "کلاس ریاضی، تختهٔ کلاس", "معلم", [
    ["Let's practise numbers. What number comes after nineteen?", "اعداد را تمرین کنیم. بعد از نوزده چه عددی می‌آید؟", "عدد بعدی را بگو.", ["Twenty", "It's twenty."], "۱۳ تا ۱۹ به teen تمام می‌شوند، ۲۰ می‌شود twenty."],
    ["Right. How many students are in this room?", "درست است. در این اتاق چند دانش‌آموز هست؟", "بگو در کلاس دوازده نفر هستیم.", ["There are twelve students in the room.", "Twelve students."], "برای شمارش از There are استفاده کن."],
    ["And how old is your little brother?", "و برادر کوچکت چند سالش است؟", "بگو هشت سالش است.", ["He is eight years old.", "He's eight."], "سن با is می‌آید، نه has."],
    ["One more: what is ten plus fifteen?", "یکی دیگر: ده به‌اضافهٔ پانزده چند است؟", "جواب جمع را بگو.", ["Ten plus fifteen is twenty-five.", "It's twenty-five."], "برای جمع از plus و is استفاده کن."],
  ]],

  ["a1.greetings.lesson-01", "راهروی مدرسه، صبح", "همکلاسی", [
    ["Hi! Good morning. How are you today?", "سلام! صبح بخیر. امروز حالت چطور است؟", "سلام کن و بگو خوبی.", ["Good morning! I'm fine, thank you.", "Hi, I'm fine, thanks."], "جواب کوتاه و مؤدبانه: I'm fine, thank you."],
    ["I'm fine too, thanks. Are you new here?", "من هم خوبم، ممنون. تو تازه‌واردی؟", "بگو بله، هفتهٔ پیش آمدم.", ["Yes, I am. I came here last week.", "Yes, I'm new. I started last week."], "برای تأیید کوتاه از Yes, I am استفاده کن."],
    ["Welcome! I'm Sara. What's your name?", "خوش آمدی! من سارا هستم. اسم تو چیست؟", "خودت را معرفی کن و بگو خوشحالی.", ["Nice to meet you. I'm in your class.", "I'm the new student here. Nice to meet you."], "Nice to meet you پاسخ استاندارد معرفی است."],
    ["Nice to meet you too, Amir. See you tomorrow?", "من هم از آشنایی خوشحالم، امیر. فردا می‌بینمت؟", "خداحافظی کن و بگو فردا می‌بینی‌اش.", ["Yes, see you tomorrow. Goodbye!", "See you tomorrow. Bye!"], "See you tomorrow برای خداحافظی روزانه است."],
  ]],

  ["a1.daily-routine.lesson-01", "آشپزخانه، صبح زود", "همخانه", [
    ["You're up early. What time do you get up every day?", "زود بیدار شدی. هر روز چه ساعتی بیدار می‌شوی؟", "بگو ساعت شش و نیم بیدار می‌شوی.", ["I get up at six thirty every day.", "I usually get up at half past six."], "با he/she فعل s می‌گیرد؛ با I ساده می‌ماند."],
    ["And what do you have for breakfast?", "و برای صبحانه چه می‌خوری؟", "بگو نان و پنیر و چای می‌خوری.", ["I have bread, cheese and tea for breakfast.", "For breakfast I have bread and cheese with tea."], "وعدهٔ غذایی با have می‌آید."],
    ["Do you go to work by bus?", "با اتوبوس به سر کار می‌روی؟", "بگو نه، با مترو می‌روی.", ["No, I don't. I go to work by metro.", "No, I take the metro."], "برای رفت‌وآمد با by + وسیله: by bus, by metro."],
    ["What do you usually do in the evening?", "معمولاً عصرها چه می‌کنی؟", "بگو کتاب می‌خوانی و انگلیسی تمرین می‌کنی.", ["In the evening I read a book and study.", "I usually study in the evening."], "قید تکرار usually قبل از فعل اصلی می‌آید."],
  ]],

  ["a1.family.lesson-01", "خانهٔ پدری، اتاق نشیمن", "دوست خانوادگی", [
    ["Is this a photo of your family?", "این عکس خانواده‌ات است؟", "بله بگو و بگو همه در عکس هستند.", ["Yes, it is. This is my whole family.", "Yes, that's my family in the photo."], "برای اشاره به عکس از This is استفاده کن."],
    ["Who is the woman next to you?", "زنی که کنارت ایستاده کیست؟", "بگو خواهرت است و معلم است.", ["She is my sister. She is a teacher.", "That's my sister. She's a teacher."], "شغل با a/an می‌آید: a teacher."],
    ["And do you have any brothers?", "و برادر هم داری؟", "بگو یک برادر داری که کوچک‌تر است.", ["Yes, I have one brother. He is younger than me.", "I have a younger brother."], "برای مقایسهٔ سن از younger than استفاده کن."],
    ["How often do you all meet together?", "چند وقت یک‌بار همه با هم جمع می‌شوید؟", "بگو هر جمعه همه با هم ناهار می‌خورید.", ["We meet every Friday and have lunch together.", "Every Friday we get together for lunch."], "every + روز، تکرار منظم را نشان می‌دهد."],
  ]],

  ["a1.shopping.lesson-01", "سوپرمارکت محله", "فروشنده", [
    ["Hello! Can I help you find something?", "سلام! می‌توانم در پیدا کردن چیزی کمک کنم؟", "بگو دنبال برنج هستی.", ["Yes, please. I'm looking for rice.", "I'd like some rice, please."], "I'm looking for برای جست‌وجوی کالاست."],
    ["The rice is on the second shelf. How many kilos do you need?", "برنج در قفسهٔ دوم است. چند کیلو لازم داری؟", "بگو دو کیلو می‌خواهی.", ["Two kilos, please.", "I need two kilos."], "مقدار + of فقط با اسم غیرشمارشی: two kilos of rice."],
    ["Anything else? We have fresh bread today.", "چیز دیگری هم می‌خواهی؟ امروز نان تازه داریم.", "بگو یک نان هم می‌خواهی.", ["Yes, one loaf of bread, please.", "And a loaf of bread, please."], "نان را با loaf می‌شمارند: a loaf of bread."],
    ["That's eleven thousand toman in total.", "جمعاً یازده هزار تومان می‌شود.", "قیمت را تکرار کن و بپرس کارت می‌گیرد یا نه.", ["Eleven thousand toman. Can I pay by card?", "That's eleven thousand. Do you take cards?"], "برای پرسیدن پرداخت با کارت از Can I pay by card استفاده کن."],
  ]],

  ["a1.introductions.lesson-01", "جشن تولد یک دوست", "مهمان دیگر", [
    ["I don't think we've met. I'm Reza.", "فکر می‌کنم قبلاً همدیگر را ندیده‌ایم. من رضا هستم.", "خودت را معرفی کن و بگو خوشحالی.", ["Nice to meet you. I'm a friend of the host.", "Hello. I'm a guest here too."], "در معرفی، I'm + اسم کافی است."],
    ["Nice to meet you too. What do you do, Neda?", "من هم از آشنایی خوشحالم. چه کاری می‌کنی، ندا؟", "بگو دانشجویی و معماری می‌خوانی.", ["I'm a student. I study architecture.", "I study architecture at university."], "What do you do? دربارهٔ شغل یا تحصیل است."],
    ["That's interesting! And where are you from?", "چه جالب! اهل کجایی؟", "بگو اهل شیراز هستی.", ["I'm from the south of Iran.", "I come from the south of Iran."], "اهل جایی بودن: I'm from + شهر."],
    ["This is my friend Sara. Sara, this is Neda.", "این دوستم ساراست. سارا، ایشان ندا هستند.", "به سارا سلام کن و بگو از آشنایی خوشحالی.", ["Hi, nice to meet you.", "Hello, it's nice to meet you."], "وقتی کسی معرفی می‌شود، پاسخ کوتاه و مؤدبانه کافی است."],
  ]],

  ["a1.weather.lesson-01", "ایستگاه اتوبوس، صبح", "همسایه", [
    ["Good morning! Cold today, isn't it?", "صبح بخیر! امروز سرد است، نه؟", "موافقت کن و بگو زمستان است.", ["Yes, it's very cold. It's winter now.", "It is. It's really cold this morning."], "برای هوا از it's استفاده کن: it's cold."],
    ["Do you like this weather?", "این هوا را دوست داری؟", "بگو هوای گرم را بیشتر دوست داری.", ["Not really. I prefer warm weather.", "I like summer better than winter."], "برای ترجیح از prefer یا like ... better استفاده کن."],
    ["What's your favourite season?", "فصل مورد علاقه‌ات چیست؟", "بگو بهار را دوست داری چون هوا معتدل است.", ["My favourite season is spring because the weather is nice.", "I like spring best. It isn't too hot or too cold."], "دلیل را با because وصل کن."],
    ["Look, it's starting to rain. Do you have an umbrella?", "نگاه کن، باران شروع شد. چتر داری؟", "بگو نه نداری و باید منتظر بمانی.", ["No, I don't. I'll wait for the bus under the roof.", "I don't have one. I'll stay here until it stops."], "برای تصمیم لحظه‌ای از will استفاده کن."],
  ]],

  ["a1.articles.lesson-01", "کلاس زبان، تمرین گرامر", "معلم", [
    ["Let's practise articles. Can you give me an example with 'an'?", "بیایید حروف تعریف را تمرین کنیم. با an یک مثال می‌دهی؟", "یک مثال با an بگو.", ["An apple is on the table.", "I have an orange."], "an قبل از صدای مصوت می‌آید: an apple, an hour."],
    ["Good. And when do we use 'the'?", "خوب است. چه زمانی از the استفاده می‌کنیم؟", "بگو برای چیزی که مشخص است.", ["We use 'the' when we talk about a specific thing.", "'The' is for something we both know."], "the برای چیزی است که گوینده و شنونده می‌شناسند."],
    ["Correct. Now say: I have ___ umbrella.", "درست است. حالا بگو: I have ___ umbrella.", "جمله را با حرف تعریف درست کامل کن.", ["I have an umbrella.", "I have an umbrella in my bag."], "umbrella با صدای مصوت شروع می‌شود، پس an."],
    ["Perfect. And: she is ___ teacher.", "عالی. و: she is ___ teacher.", "جمله را کامل کن.", ["She is a teacher.", "She is a teacher at my school."], "شغل‌ها با a/an می‌آیند، نه با the."],
  ]],

  ["a1.plurals.lesson-01", "کلاس زبان، تمرین اسم‌ها", "معلم", [
    ["What is the plural of 'child'?", "جمع child چیست؟", "جمع کلمه را بگو.", ["Children", "The plural is children."], "child جمع بی‌قاعده دارد: children."],
    ["And 'box'?", "و box؟", "جمع کلمه را بگو.", ["Boxes", "Boxes, with -es."], "اسم‌های مختوم به x, s, sh, ch با es جمع می‌بندند."],
    ["Now make a sentence: three ___ on the desk.", "حالا جمله بساز: سه ___ روی میز.", "با book جمله بساز.", ["There are three books on the desk.", "Three books are on the desk."], "با جمع، فعل are می‌شود."],
    ["How many people are in the room?", "چند نفر در اتاق هستند؟", "بگو پنج نفر هستند.", ["There are five people in the room.", "Five people."], "people خودش جمع است؛ peoples غلط است."],
  ]],

  ["a1.pronouns.lesson-01", "کلاس زبان، تمرین ضمایر", "معلم", [
    ["Look at this sentence: 'I like Ali.' Can you replace 'Ali' with a pronoun?", "به این جمله نگاه کن: I like Ali. می‌توانی Ali را با ضمیر عوض کنی؟", "جمله را با ضمیر بگو.", ["I like him.", "I like him very much."], "برای مفعول از him استفاده می‌شود، نه he."],
    ["Good. And 'Sara is my friend. I know Sara well.'?", "خوب. و Sara is my friend. I know Sara well.", "جملهٔ دوم را با ضمیر بازنویسی کن.", ["I know her well.", "I know her very well."], "her ضمیر مفعولی ساراست."],
    ["Now: 'My parents are here. ___ are waiting.'", "حالا: My parents are here. ___ are waiting.", "ضمیر فاعلی درست را جای خالی بگذار.", ["They are waiting.", "They're waiting outside."], "برای جمع غیرشخصی از they استفاده کن."],
    ["Last one: 'This is my book. It is ___.'", "آخری: This is my book. It is ___.", "جمله را با ضمیر ملکی کامل کن.", ["It is mine.", "That book is mine."], "mine و my را با هم اشتباه نگیر: my + اسم، mine تنها."],
  ]],

  ["a1.can.lesson-01", "باشگاه محله", "مربی", [
    ["Welcome! Can you swim?", "خوش آمدی! می‌توانی شنا کنی؟", "بگو نه، شنا نمی‌توانی.", ["No, I can't swim.", "No, I can't, but I'd like to learn."], "منفی can می‌شود can't، بدون do."],
    ["That's fine. Can you ride a bike?", "اشکالی ندارد. دوچرخه می‌توانی سوار شوی؟", "بگو بله، از بچگی می‌توانی.", ["Yes, I can. I learned when I was a child.", "Yes, I can ride a bike very well."], "برای توانایی از can + فعل ساده استفاده کن."],
    ["Great. What sports can you play?", "عالی. چه ورزش‌هایی می‌توانی انجام دهی؟", "بگو فوتبال و پینگ‌پنگ می‌توانی.", ["I can play football and table tennis.", "I can play football, but I can't play tennis."], "ورزش‌های توپی با play می‌آیند."],
    ["OK. Can you come on Thursday evening?", "باشه. پنجشنبه عصر می‌توانی بیایی؟", "بگو بله می‌توانی و ساعت شش می‌آیی.", ["Yes, I can. I'll come at six.", "Yes, I can come at six o'clock."], "برای قرار قطعی از will استفاده کن."],
  ]],

  ["a1.imperatives.lesson-01", "آشپزخانه، آشپزی با هم", "دوست", [
    ["We're making an omelette. What should I do first?", "داریم املت درست می‌کنیم. اول چه کار کنم؟", "دستور بده: تخم‌مرغ‌ها را بشکن.", ["First, break the eggs into a bowl.", "Break the eggs, please."], "جملهٔ امری با فعل ساده شروع می‌شود: Break..."],
    ["And then?", "و بعد؟", "بگو نمک و فلفل اضافه کند و هم بزند.", ["Then add some salt and pepper and mix them.", "Add salt and mix it well."], "دستورهای پشت‌سرهم با then وصل می‌شوند."],
    ["Should I use oil?", "روغن بریزم؟", "بگو نه، کره بریزد.", ["No, don't use oil. Use butter.", "Don't use oil. Use a little butter."], "منفی امری با don't می‌آید."],
    ["How long should I cook it?", "چقدر بپزم؟", "بگو سه دقیقه بپزد و زیاد نپزد.", ["Cook it for three minutes, but don't burn it.", "Cook it for three minutes and don't leave the kitchen."], "don't برای هشدار در دستور کار است."],
  ]],

  ["a1.question-words.lesson-01", "کلاس زبان، تمرین پرسش‌ها", "معلم", [
    ["Let's practise question words. Ask me about my name.", "کلمات پرسشی را تمرین کنیم. دربارهٔ اسم من سؤال بپرس.", "با چه کلمه‌ای اسم می‌پرسند؟", ["What is your name?", "What's your name, please?"], "What برای پرسیدن چیز یا نام است."],
    ["Ask me about my home town.", "دربارهٔ شهرم سؤال بپرس.", "با where سؤال بپرس.", ["Where are you from?", "Where do you live?"], "Where مکان را می‌پرسد."],
    ["Ask me about my age.", "دربارهٔ سنم سؤال بپرس.", "با how old سؤال بپرس.", ["How old are you?", "How old is your brother?"], "سن همیشه با how old پرسیده می‌شود."],
    ["Ask me why I am learning English.", "بپرس چرا انگلیسی یاد می‌گیرم.", "با why سؤال بپرس.", ["Why do you study every evening?", "Why do you want to learn this language?"], "Why برای پرسیدن دلیل است."],
  ]],

  ["a1.countries.lesson-01", "همایش دانشجویی، میز ثبت‌نام", "مسئول همایش", [
    ["Welcome! Where are you from?", "خوش آمدید! اهل کجایید؟", "بگو اهل ایران هستی.", ["I'm from Iran.", "I come from Iran."], "برای ملیت لازم نیست the بیاید: from Iran."],
    ["Nice. Which languages do you speak?", "چه خوب. چه زبان‌هایی صحبت می‌کنید؟", "بگو فارسی بلدی و کمی انگلیسی.", ["I speak Persian, and a little English.", "Persian, and some English."], "زبان‌ها با حرف بزرگ نوشته می‌شوند: English, Persian."],
    ["And what nationality is your friend?", "ملیت دوستت چیست؟", "بگو دوستت اهل ترکیه است.", ["He is Turkish. He's from Turkey.", "My friend is Turkish."], "ملیت و کشور فرق دارند: Turkey / Turkish."],
    ["Have you travelled to other countries?", "به کشورهای دیگر سفر کرده‌اید؟", "بگو فقط به ترکیه سفر کردی.", ["I've only travelled to Turkey.", "No, I've never been abroad."], "برای تجربهٔ سفر از have been to استفاده کن."],
  ]],

  ["a1.age-birthday.lesson-01", "کلاس زبان، روز تولد یک همکلاسی", "همکلاسی", [
    ["Happy birthday! How old are you today?", "تولدت مبارک! امروز چند ساله می‌شوی؟", "بگو بیست ساله می‌شوی.", ["I'm twenty today.", "I'm twenty years old today."], "سن با am/is/are می‌آید: I'm twenty."],
    ["When is your birthday exactly?", "دقیقاً چه روزی تولدت است؟", "بگو پنجم مارس است.", ["My birthday is on the fifth of March.", "It's on March fifth."], "تاریخ با on می‌آید: on March fifth."],
    ["Do you have a birthday cake?", "کیک تولد داری؟", "بگو بله، مادرم کیک شکلاتی پخته.", ["Yes, my mother made a chocolate cake.", "Yes, there's a chocolate cake at home."], "گذشتهٔ سادهٔ make می‌شود made."],
    ["What did you wish for?", "چه آرزویی کردی؟", "بگو نمی‌گویی وگرنه برآورده نمی‌شود.", ["I can't tell you, or it won't come true.", "It's a secret! Then it won't come true."], "شوخی رایج انگلیسی برای نگفتن آرزوست."],
  ]],

  ["a1.days-months.lesson-01", "دفتر کار، برنامه‌ریزی هفتگی", "همکار", [
    ["What day is it today?", "امروز چه روزی است؟", "بگو سه‌شنبه است.", ["Today is Tuesday.", "It's Tuesday today."], "روزهای هفته با حرف بزرگ نوشته می‌شوند."],
    ["When is our next meeting?", "جلسهٔ بعدی ما چه زمانی است؟", "بگو دوشنبهٔ آینده ساعت ده.", ["It's next Monday at ten.", "Next Monday, at ten o'clock."], "on برای روز، at برای ساعت: on Monday at ten."],
    ["Which months are the busiest for us?", "کدام ماه‌ها برای ما شلوغ‌ترین‌اند؟", "بگو فروردین و اردیبهشت شلوغ است (March and April).", ["March and April are the busiest months.", "The busiest months are March and April."], "ماه‌ها بدون حرف تعریف می‌آیند، مگر برای دورهٔ خاص."],
    ["And when do you take your holiday?", "و چه زمانی مرخصی می‌گیری؟", "بگو مرداد (August) مرخصی می‌گیری.", ["I take my holiday in August.", "My holiday is in August, for two weeks."], "ماه با in می‌آید: in August."],
  ]],
];
