// Conversation dialogues, A1 (second half).
//
// Shape: [lessonId, sceneFa, partnerFa, turns[]]
// turn:  [question, questionFa, taskFa, answers[], tipFa]

export const a1High = [
  ["a1.time.lesson-01", "ایستگاه قطار", "مسافر", [
    ["Excuse me, what time is it?", "ببخشید، ساعت چند است؟", "بگو ساعت نه و ربع است.", ["It's a quarter past nine.", "It's nine fifteen."], "quarter past = یک ربع بعد از ساعت."],
    ["Thanks. When does the next train leave?", "ممنون. قطار بعدی چه زمانی حرکت می‌کند؟", "بگو ساعت ده حرکت می‌کند.", ["It leaves at ten o'clock.", "The next train leaves at ten."], "زمان حرکت را با at + ساعت بگو."],
    ["How long does the trip take?", "سفر چقدر طول می‌کشد؟", "بگو حدود دو ساعت طول می‌کشد.", ["It takes about two hours.", "The trip takes about two hours."], "مدت زمان با take می‌آید: it takes two hours."],
    ["And when does it arrive?", "و چه زمانی می‌رسد؟", "بگو ساعت دوازده و ده دقیقه می‌رسد.", ["It arrives at ten past twelve.", "It gets there at ten past twelve."], "arrive at + ساعت برای برنامه‌های حمل‌ونقل."],
  ]],

  ["a1.colors.lesson-01", "فروشگاه رنگ", "فروشنده", [
    ["Hello! What colour are you looking for?", "سلام! دنبال چه رنگی هستید؟", "بگو دنبال آبی روشن هستی.", ["I'm looking for light blue.", "Light blue, please."], "رنگ‌های روشن و تیره با light/dark می‌آیند."],
    ["We have this blue, and a darker one.", "این آبی را داریم و یک آبی تیره‌تر.", "بگو آبی روشن را بیشتر دوست داری.", ["I prefer the light blue one.", "I like the lighter blue better."], "برای ترجیح از prefer یا like better استفاده کن."],
    ["Do you need it for a wall or for furniture?", "برای دیوار می‌خواهید یا برای مبلمان؟", "بگو برای دیوار اتاقت است.", ["It's for my bedroom wall.", "I need it for the wall of my room."], "برای چیزی که مالکش را می‌گویی از my استفاده کن."],
    ["How many litres do you need?", "چند لیتر لازم دارید؟", "بگو پنج لیتر لازم داری.", ["I need five litres, please.", "Five litres is enough."], "برای مقادیر از مقدار + of فقط با اسم غیرشمارشی استفاده کن."],
  ]],

  ["a1.classroom.lesson-01", "کلاس زبان، شروع درس", "معلم", [
    ["Please open your book to page twenty. Is your book open?", "کتابتان را صفحهٔ بیست باز کنید. کتابت باز است؟", "بگو بله باز است و بگو مداد هم داری.", ["Yes, it's open. I have my pencil and notebook.", "Yes, my book is open on page twenty."], "برای حالت اشیا از is/are استفاده کن."],
    ["Good. Where is your bag?", "خوب. کیفت کجاست؟", "بگو کیفت زیر صندلی است.", ["My bag is under the chair.", "It's under my chair, next to the door."], "حروف اضافهٔ مکان: under, on, next to."],
    ["Can you pass me the whiteboard marker?", "می‌توانی ماژیک وایت‌برد را به من بدهی؟", "بگو بله و بگو روی میز است.", ["Yes, here you are. It's on the table.", "Sure. It's on the desk."], "هنگام دادن چیزی Here you are می‌گویند."],
    ["Thank you. Do you have a dictionary?", "ممنون. دیکشنری داری؟", "بگو نه داری و می‌توانی از گوشی استفاده کنی.", ["No, I don't, but I can use my phone.", "I don't have one. Can I use my phone?"], "جواب کوتاه منفی: No, I don't."],
  ]],

  ["a1.jobs.lesson-01", "مهمانی خانوادگی", "خویشاوند", [
    ["Your cousin told me you have a new job. What do you do?", "پسرخاله‌ات گفت شغل تازه‌ای پیدا کردی. چه کاری می‌کنی؟", "بگو در یک بیمارستان کار می‌کنی و پرستاری.", ["I'm a nurse. I work in a hospital.", "I work as a nurse in a big hospital."], "شغل با a/an: a nurse, a teacher, an engineer."],
    ["That's a hard job. What time do you start?", "شغل سختی است. چه ساعتی شروع می‌کنی؟", "بگو ساعت هفت صبح شروع می‌کنی.", ["I start at seven in the morning.", "My shift starts at seven."], "at برای ساعت، in the morning برای بخش روز."],
    ["Do you like working there?", "از کار کردن آنجا لذت می‌بری؟", "بگو بله، مردم را دوست داری.", ["Yes, I do. I like helping people.", "I like it a lot because I can help people."], "برای علاقه از like + فعل ing استفاده کن."],
    ["Where does your brother work?", "برادرت کجا کار می‌کند؟", "بگو در یک شرکت مهندسی کار می‌کند.", ["He works in an engineering company.", "He works for a big company."], "با he فعل s می‌گیرد: he works."],
  ]],

  ["a1.belongings.lesson-01", "کلاس، آخر وقت", "همکلاسی", [
    ["Whose phone is this? Is it yours?", "این گوشی مال کیست؟ مال توست؟", "بگو نه، مال تو نیست و مال مریم است.", ["No, it isn't mine. It might be hers.", "It's not mine. It belongs to someone else."], "ملکیت: mine/yours در مقابل Maryam's."],
    ["And this blue notebook?", "و این دفتر آبی؟", "بگو مال توست.", ["That's mine.", "The blue notebook is mine."], "برای تأیید مالکیت کافی است بگویی It's mine."],
    ["You have a nice watch. Is it new?", "ساعت قشنگی داری. نو است؟", "بگو نه، قدیمی است اما هنوز کار می‌کند.", ["No, it's old, but it still works.", "It's quite old, but I like it."], "برای چیز قدیمی اما سالم از still works استفاده کن."],
    ["I think I left my keys here. Have you seen them?", "فکر می‌کنم کلیدهایم را اینجا جا گذاشتم. ندیدی؟", "بگو روی میز نزدیک در دیده‌ای.", ["Yes, they're on the table near the door.", "I saw them on the desk by the window."], "برای جفت اشیا از they استفاده کن: keys are."],
  ]],

  ["a1.home-rooms.lesson-01", "خانهٔ یک دوست، ورودی", "میزبان", [
    ["Welcome to our flat! This is the living room.", "به آپارتمان ما خوش آمدی! این اتاق نشیمن است.", "تعریف کن: اتاق روشن و بزرگ است.", ["It's very bright and big.", "What a nice room. It's so bright."], "برای تعریف از What a + اسم استفاده کن."],
    ["Thanks. How many rooms does your house have?", "ممنون. خانه‌ات چند اتاق دارد؟", "بگو دو اتاق و یک آشپزخانه دارد.", ["It has two bedrooms and one kitchen.", "My house has two rooms and a small kitchen."], "با it برای خانه از has استفاده کن."],
    ["Do you have a balcony?", "بالکن داری؟", "بگو بله، بالکن کوچکی رو به حیاط داری.", ["Yes, I have a small balcony facing the garden.", "Yes, there's a small balcony."], "facing برای جهت‌گیری مکان است."],
    ["Where do you usually study?", "معمولاً کجا درس می‌خوانی؟", "بگو در اتاقت کنار پنجره درس می‌خوانی.", ["I usually study in my room, next to the window.", "In my bedroom, at the desk near the window."], "مکان با in + اتاق می‌آید."],
  ]],

  ["a1.house-objects.lesson-01", "خانه، آشپزخانه", "همخانه", [
    ["Can you help me? I can't find the kettle.", "می‌توانی کمکم کنی؟ کتری را پیدا نمی‌کنم.", "بگو در کابینت بالای سینک است.", ["It's in the cupboard above the sink.", "Look in the cupboard, above the sink."], "برای راهنمایی از Look in استفاده کن."],
    ["And where do we keep the plates?", "و بشقاب‌ها را کجا نگه می‌داریم؟", "بگو در کشوی سمت راست هستند.", ["They're in the drawer on the right.", "We keep them in the right drawer."], "برای جفت اشیا از they استفاده کن."],
    ["We also need a chair for the guest.", "یک صندلی هم برای مهمان لازم داریم.", "بگو صندلی اضافه در انبار است.", ["There's an extra chair in the storage room.", "We have one more chair in the basement."], "برای وجود داشتن از There's استفاده کن."],
    ["Do you know where the toolbox is?", "می‌دانی جعبه‌ابزار کجاست؟", "بگو نه، فکر می‌کنی زیر تخت است.", ["I don't know, but I think it's under the bed.", "Maybe it's under the bed. I'm not sure."], "برای حدس از I think یا maybe استفاده کن."],
  ]],

  ["a1.food-drinks.lesson-01", "رستوران کوچک، وقت ناهار", "پیشخدمت", [
    ["Good afternoon! What would you like to eat?", "بعدازظهر بخیر! چه چیزی میل دارید؟", "بگو مرغ با برنج می‌خواهی.", ["I'd like chicken with rice, please.", "Can I have chicken and rice?"], "برای سفارش مؤدبانه از I'd like استفاده کن."],
    ["And what would you like to drink?", "و چه می‌نوشید؟", "بگو یک دوغ و یک لیوان آب می‌خواهی.", ["A glass of water and some doogh, please.", "Just water, please."], "نوشیدنی‌های غیرشمارشی با a glass of/bottle of می‌آیند."],
    ["Do you want a salad with that?", "با آن سالاد هم می‌خواهید؟", "بگو نه ممنون، سیر هستی.", ["No, thank you. I'm not very hungry.", "No, thanks, that's enough."], "برای رد کردن مؤدبانه: No, thank you."],
    ["Anything for dessert?", "برای دسر چیزی؟", "بگو بله، بستنی می‌خواهی.", ["Yes, please. I'd like some ice cream.", "Ice cream, please."], "dessert (دسر) با desert (بیابان) اشتباه نشود."],
  ]],

  ["a1.fruits-vegetables.lesson-01", "میوه‌فروشی محله", "میوه‌فروش", [
    ["Good morning! What can I get you today?", "صبح بخیر! امروز چه چیزی برایتان بیاورم؟", "بگو یک کیلو سیب می‌خواهی.", ["A kilo of apples, please.", "I'd like a kilo of apples."], "برای مقدار از a kilo of استفاده کن."],
    ["These apples are very sweet. Do you need bananas too?", "این سیب‌ها خیلی شیرین‌اند. موز هم لازم دارید؟", "بگو نه، اما دو عدد لیمو می‌خواهی.", ["No, but I need two lemons, please.", "No bananas, thanks. Just two lemons."], "میوه‌های شمارشی با عدد می‌آیند: two lemons."],
    ["We have fresh tomatoes. How many?", "گوجهٔ تازه داریم. چند تا؟", "بگو نیم کیلو گوجه می‌خواهی.", ["Half a kilo of tomatoes, please.", "Half a kilo, please."], "half a kilo یعنی نیم کیلو."],
    ["Anything else? The cucumbers are good today.", "چیز دیگری؟ خیارهای امروز خوب هستند.", "بگو نه، همین کافی است و قیمت را بپرس.", ["That's all, thanks. How much is it?", "No, that's everything. How much?"], "برای پرسیدن قیمت: How much is it?"],
  ]],

  ["a1.clothes.lesson-01", "پوشاک‌فروشی", "فروشنده", [
    ["Hello! Can I help you?", "سلام! می‌توانم کمکتان کنم؟", "بگو دنبال یک پیراهن برای مهمانی هستی.", ["Yes, I'm looking for a shirt for a party.", "I'd like a shirt, please. Something formal."], "برای هدف از for + مناسبت استفاده کن."],
    ["What size are you?", "چه سایزی هستید؟", "بگو سایز متوسط.", ["I'm a medium.", "Medium, I think."], "سایز با am می‌آید: I'm a medium."],
    ["We have this in blue and in white. Which one do you prefer?", "این را آبی و سفید داریم. کدام را ترجیح می‌دهید؟", "بگو سفید را ترجیح می‌دهی.", ["I prefer the white one.", "The white one, please."], "to prefer + the white one، نه a white one."],
    ["Would you like to try it on?", "می‌خواهید امتحانش کنید؟", "بگو بله و بپرس پرو اتاق کجاست.", ["Yes, please. Where is the fitting room?", "Yes. Where can I try it on?"], "try on یعنی پوشیدن برای امتحان."],
  ]],

  ["a1.body.lesson-01", "درمانگاه، پذیرش", "پرستار", [
    ["What's the problem today?", "امروز چه مشکلی دارید؟", "بگو سر و گلویت درد می‌کند.", ["I have a headache and a sore throat.", "My head hurts and my throat is sore."], "درد + عضو بدن با have: I have a headache."],
    ["Do you have a fever?", "تب دارید؟", "بگو فکر می‌کنی تب داری.", ["Yes, I think I have a fever.", "I'm not sure, but I feel hot."], "تب با have a fever می‌آید."],
    ["Show me where it hurts.", "نشان دهید کجا درد می‌کند.", "بگو پشتت درد می‌کند.", ["My back hurts here.", "It hurts in my back, right here."], "برای درد از hurt استفاده کن: my back hurts."],
    ["How long have you felt like this?", "چند وقت است این‌طوری هستید؟", "بگو از دیروز.", ["Since yesterday.", "I've felt like this since yesterday."], "since برای نقطهٔ شروع، for برای مدت."],
  ]],

  ["a1.animals.lesson-01", "پارک، با یک دوست", "دوست", [
    ["Look at that dog! Do you have any pets?", "آن سگ را ببین! حیوان خانگی داری؟", "بگو بله، یک گربه داری.", ["Yes, I have a cat.", "I have one cat at home."], "pet یعنی حیوان خانگی."],
    ["What's it like? Is it big or small?", "چه شکلی است؟ بزرگ است یا کوچک؟", "بگو کوچک و سفید است.", ["She's small and white.", "It's a small white cat."], "برای حیوان خانگی از it یا he/she استفاده کن."],
    ["Did you have any animals when you were a child?", "در کودکی حیوان داشتید؟", "بگو یک لاک‌پشت داشتی.", ["Yes, I had a turtle.", "I had a turtle when I was a child."], "گذشتهٔ have می‌شود had."],
    ["I love horses. Have you ever ridden one?", "من عاشق اسب‌ها هستم. تا حالا سوار شده‌ای؟", "بگو نه، سوار نشدی اما دوست داری.", ["No, I've never ridden a horse, but I'd like to.", "No, never. I'd love to try."], "برای تجربهٔ نکرده: I've never + past participle."],
  ]],

  ["a1.hobbies.lesson-01", "کافه با یک دوست", "دوست", [
    ["What do you do in your free time?", "وقت آزادت را چه می‌کنی؟", "بگو کتاب می‌خوانی و عکاسی می‌کنی.", ["I like reading and taking photos.", "I usually read books and take photos."], "پس از like فعل ing می‌آید: reading, taking."],
    ["Are you good at photography?", "در عکاسی خوب هستی؟", "بگو تازه شروع کرده‌ای.", ["I'm just starting, but I enjoy it.", "Not yet, but I'm learning."], "تازه‌آغاز بودن: I'm just starting."],
    ["Do you play any instruments?", "ساز هم می‌زنی؟", "بگو کمی گیتار می‌زنی.", ["I play the guitar a little.", "I can play the guitar, but not very well."], "ساز با the می‌آید: play the guitar."],
    ["What about weekends? Do you go hiking?", "آخر هفته‌ها چی؟ کوهنوردی می‌روی؟", "بگو گاهی با دوستانت کوه می‌روی.", ["Sometimes I go hiking with my friends.", "Yes, we go to the mountains every month."], "go + ing برای فعالیت: go hiking, go swimming."],
  ]],

  ["a1.places-town.lesson-01", "خیابان مرکزی شهر", "رهگذر", [
    ["Excuse me, I'm new here. Is there a bank near this street?", "ببخشید، من تازه‌وارد هستم. نزدیک این خیابان بانک هست؟", "بگو بله، یک بانک روبه‌روی داروخانه هست.", ["Yes, there's a bank opposite the pharmacy.", "There is a bank on this street, next to the bakery."], "برای وجود داشتن: there is / there are."],
    ["And where can I buy a SIM card?", "و کجا می‌توانم سیم‌کارت بخرم؟", "بگو در مغازهٔ گوشی کنار پارک.", ["At the phone shop next to the park.", "You can buy one at the shop near the park."], "برای امکان از can استفاده کن."],
    ["Is there a big supermarket around here?", "این اطراف سوپرمارکت بزرگ هست؟", "بگو نه، فقط یک مغازهٔ کوچک هست.", ["No, but there's a small shop on the corner.", "There isn't a big one, but there's a small shop."], "منفی وجودی: there isn't."],
    ["Thank you. Is the town centre far from here?", "ممنون. مرکز شهر از اینجا دور است؟", "بگو نه، ده دقیقه پیاده است.", ["No, it's about ten minutes on foot.", "It's not far. Ten minutes' walk."], "مدت پیاده‌روی: ten minutes on foot."],
  ]],

  ["a1.directions.lesson-01", "جلوی بانک، پیاده‌رو", "عابر محلی", [
    ["Excuse me, where is the nearest bank?", "ببخشید، نزدیک‌ترین بانک کجاست؟", "بگو از این خیابان مستقیم برو.", ["Go straight down this street.", "Go along this street and you'll see it."], "دستور مسیر با فعل ساده شروع می‌شود."],
    ["Straight, and then?", "مستقیم، و بعد؟", "بگو بعد از چراغ راهنمایی به راست بپیچد.", ["Then turn right after the traffic lights.", "Turn right at the lights."], "turn right/left بدون حرف اضافه."],
    ["Is it far?", "دور است؟", "بگو حدود پنج دقیقه پیاده است.", ["Not at all. It's about five minutes on foot.", "It's a five-minute walk."], "برای مسافت کوتاه: a five-minute walk."],
    ["And where is the post office?", "و ادارهٔ پست کجاست؟", "بگو کنار داروخانه، سمت چپ است.", ["It's next to the pharmacy, on your left.", "The post office is on the left, opposite the bakery."], "on your left/right برای جهتی از دید شنونده است."],
  ]],

  ["a1.transport.lesson-01", "ایستگاه مترو", "کارمند مترو", [
    ["Where do you want to go?", "می‌خواهید کجا بروید؟", "بگو می‌خواهی به میدان انقلاب بروی.", ["I want to go to the main square.", "How can I get to the main square?"], "برای پرسیدن مسیر: How can I get to ...?"],
    ["You need the red line. Do you have a card?", "به خط قرمز نیاز دارید. کارت دارید؟", "بگو نه، می‌خواهی یک کارت بخری.", ["No, I don't. I'd like to buy a card.", "No, where can I get one?"], "برای خرید بلیت از I'd like to buy استفاده کن."],
    ["It's five stops from here. Where do you change?", "از اینجا پنج ایستگاه است. کجا خط عوض می‌کنید؟", "بگو در ایستگاه بعدی خط عوض می‌کنی.", ["I change at the next station.", "I need to change lines at the next stop."], "change lines یعنی خط عوض کردن."],
    ["The train is arriving now. Which exit should I take?", "قطار دارد می‌رسد. از کدام خروجی بروم؟", "بگو خروجی دو نزدیک‌تر است.", ["Take exit number two.", "Exit two is closer to the square."], "خروجی با take: take exit two."],
  ]],

  ["a1.cafe-restaurant.lesson-01", "کافهٔ کوچک، بعدازظهر", "گارسون", [
    ["Good afternoon. What can I get you?", "بعدازظهر بخیر. چه چیزی برایتان بیاورم؟", "بگو یک قهوه و یک کیک می‌خواهی.", ["A coffee and a piece of cake, please.", "Can I have a coffee and some cake?"], "Can I have برای سفارش است."],
    ["Would you like milk in your coffee?", "با قهوه‌تان شیر می‌خواهید؟", "بگو بله، کمی شیر می‌خواهی.", ["Yes, a little milk, please.", "Yes, please, with some milk."], "a little برای مقدار کم غیرشمارشی است."],
    ["Here you are. Would you like a table by the window?", "بفرمایید. میز کنار پنجره می‌خواهید؟", "بگو بله، بیرون را دوست داری تماشا کنی.", ["Yes, please. I like watching the street.", "Yes, that would be nice."], "پس از like فعل ing می‌آید."],
    ["The bill, whenever you are ready.", "هر وقت آماده بودید، صورت‌حساب.", "صورت‌حساب را بخواه و بپرس کارت می‌گیرند.", ["Could I have the bill, please? Can I pay by card?", "The bill, please. Do you take cards?"], "Could I have ... please مؤدبانه‌ترین شکل درخواست است."],
  ]],

  ["a1.describing-people.lesson-01", "دانشگاه، راهروی دانشکده", "همکلاسی", [
    ["Do you know the girl in the blue coat?", "دختری که کت آبی دارد را می‌شناسی؟", "بگو بله، او مهساست، هم‌کلاسی‌ات.", ["Yes, that's the girl from my class.", "That's my classmate."], "توصیف با is: she is + صفت."],
    ["What does she look like?", "چهره‌اش چگونه است؟", "بگو موی تیره و بلند دارد.", ["She has long dark hair and brown eyes.", "She's tall and has long dark hair."], "برای ظاهر از have برای مو و چشم استفاده کن."],
    ["Is she friendly?", "آدم خوش‌برخوردی است؟", "بگو بله، بسیار مهربان و پرانرژی است.", ["Yes, she's very friendly and energetic.", "She is. She's kind and talks to everyone."], "صفت پس از is می‌آید، نه پس از has."],
    ["And what about her brother?", "و برادرش؟", "بگو برادرش بلندقد و کم‌حرف است.", ["Her brother is tall and quite quiet.", "He's very tall and doesn't talk much."], "برای تفاوت دو نفر از وصف متضاد استفاده کن."],
  ]],

  ["a1.where-i-live.lesson-01", "خوابگاه، اتاق جدید", "هم‌اتاقی", [
    ["So, where do you live now?", "خب، الان کجا زندگی می‌کنی؟", "بگو در مرکز تهران زندگی می‌کنی.", ["I live in the centre of the city.", "I live near the main square."], "برای محل زندگی از live in + شهر استفاده کن."],
    ["Is it a busy area?", "محلهٔ شلوغی است؟", "بگو بله، خیلی شلوغ است اما دوستش داری.", ["Yes, it's very busy, but I like it.", "It's busy, but there are lots of shops."], "برای استفاده از but پس از صفت منفی، مثبت اضافه کن."],
    ["How long does it take to get to your work?", "تا محل کارت چقدر طول می‌کشد؟", "بگو حدود نیم ساعت طول می‌کشد.", ["It takes about half an hour by metro.", "About thirty minutes."], "مدت زمان: half an hour."],
    ["Would you like to live in a quieter place?", "دوست داری جای خلوت‌تری زندگی کنی؟", "بگو گاهی آری، اما اینجا برای شروع خوب است.", ["Sometimes, yes, but this place is good for now.", "Maybe later. For now, I'm happy here."], "برای تردید از sometimes یا maybe استفاده کن."],
  ]],
];
