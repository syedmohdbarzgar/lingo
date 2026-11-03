// Conversation dialogues, A2: everyday situations with a clear counterpart.
//
// Shape: [lessonId, sceneFa, partnerFa, turns[]]
// turn:  [question, questionFa, taskFa, answers[], tipFa]

export const a2 = [
  ["a2.airport.lesson-01", "فرودگاه، باجهٔ تحویل بار", "مأمور فرودگاه", [
    ["Good morning. May I see your passport and ticket, please?", "صبح بخیر. می‌توانم پاسپورت و بلیتتان را ببینم؟", "پاسپورت و بلیت را بده و بگو پرواز به استانبول داری.", ["Here you are. I'm flying to Istanbul.", "Of course. Here's my passport and my ticket."], "Here you are هنگام دادن مدرک گفته می‌شود."],
    ["Thank you. How many bags are you checking in?", "ممنون. چند چمدان تحویل می‌دهید؟", "بگو دو چمدان تحویل می‌دهی.", ["Two bags, please.", "I'm checking in two bags."], "check in برای بار در فرودگاه است."],
    ["One of them is over the weight limit. Would you like to pay the extra fee?", "یکی از آن‌ها بیش از حد مجاز وزن دارد. می‌خواهید هزینهٔ اضافه را بپردازید؟", "بگو ترجیح می‌دهی چیزی از چمدان را بیرون بیاوری.", ["I'd rather take something out of the bag.", "Is it possible to move some things to my hand luggage?"], "I'd rather برای ترجیح در تصمیم است."],
    ["That's fine. Your gate is B12, and boarding starts at eleven.", "اشکالی ندارد. دروازهٔ شما B12 است و سوار شدن ساعت یازده شروع می‌شود.", "درخواست را تکرار کن و بپرس پرواز تأخیر دارد یا نه.", ["Gate twelve, at eleven. Is the flight on time?", "Thank you. Is there any delay?"], "برای تأیید شنیده‌ات، اطلاعات را تکرار کن."],
  ]],

  ["a2.restaurant.lesson-01", "رستوران، تماس تلفنی برای رزرو", "مسئول رزرو", [
    ["Good evening, Bella Restaurant. How can I help you?", "عصر بخیر، رستوران بلا. چطور می‌توانم کمک کنم؟", "بگو می‌خواهی برای امشب میز رزرو کنی.", ["I'd like to book a table for tonight, please.","Hello, can I make a reservation for this evening?"], "«book a table» همان «make a reservation» است؛ هر دو برای رزرو میز به‌کار می‌روند."],
    ["Of course. For how many people?", "البته. برای چند نفر؟", "بگو برای چهار نفر.", ["For four people, at eight o'clock.", "A table for four, please."], "برای تعداد نفرات از for استفاده کن."],
    ["I'm afraid we're full at eight. Would nine be all right?", "متأسفانه ساعت هشت پر است. ساعت نه مناسب است؟", "بگو ساعت نه خوب است.", ["Nine is fine, thank you.", "Nine works for us, thank you."], "works for us یعنی برای ما مناسب است."],
    ["And could I have a name for the booking?", "و نامی برای رزرو ثبت کنم؟", "اسمت را بگو و شمارهٔ تماس بده.", ["Yes, it's under my family name.", "I'll write it on the form for you."], "برای رزرو معمولاً می‌گویند it's under + نام."],
  ]],

  ["a2.directions.lesson-01", "مرکز شهر، گردشگر گم‌شده", "عابر محلی", [
    ["Sorry to bother you. I'm trying to find the National Museum.", "ببخشید مزاحم شدم. دنبال موزهٔ ملی می‌گردم.", "بگو می‌توانی کمک کنی و بپرس از کجا آمده.", ["Of course, I can help. Where are you coming from?", "No problem. Are you walking or taking the bus?"], "I'm trying to find برای جست‌وجو است."],
    ["I'm coming from the hotel on King Street.", "از هتل خیابان کینگ می‌آیم.", "مسیر را بگو: دو بلوک مستقیم، بعد چپ.", ["Go straight for two blocks, then turn left.", "Walk down this street and take the second left."], "block یعنی بلوک شهری."],
    ["Is it far on foot?", "پیاده دور است؟", "بگو حدود پانزده دقیقه پیاده است.", ["It's about fifteen minutes on foot.", "Not really, around fifteen minutes."], "Not really برای رد کردن ملایم است."],
    ["Thanks a lot. Do you know when the museum closes?", "خیلی ممنون. می‌دانید موزه چه زمانی بسته می‌شود؟", "بگو ساعت شش بسته می‌شود و دوشنبه‌ها تعطیل است.", ["It closes at six, and it's closed on Mondays.", "I think it closes at six. It isn't open on Mondays."], "برای برنامهٔ ثابت از حال ساده استفاده کن."],
  ]],

  ["a2.health.lesson-01", "مطب دکتر", "دکتر", [
    ["Good morning. What seems to be the problem?", "صبح بخیر. مشکل چیست؟", "بگو از دو روز پیش سرفه و تب داری.", ["I've had a cough and a fever since Tuesday.", "I have a bad cough, and I've had a fever for two days."], "for + مدت، since + نقطهٔ شروع."],
    ["Have you taken anything for it?", "چیزی برای آن مصرف کرده‌اید؟", "بگو فقط چای و استامینوفن خورده‌ای.", ["Only paracetamol and some tea.", "I took paracetamol yesterday, but it didn't help."], "گذشتهٔ take می‌شود took."],
    ["Do you have any pain in your chest?", "در قفسهٔ سینه درد دارید؟", "بگو نه، فقط گلویت درد می‌کند.", ["No, but my throat hurts a lot.", "No chest pain, but my throat is very sore."], "برای درد از sore یا hurts استفاده کن."],
    ["I'll prescribe some medicine. Take it twice a day after meals.", "دارو تجویز می‌کنم. روزی دو بار بعد از غذا مصرف کنید.", "تکرار کن و بپرس باید استراحت کنی یا نه.", ["Twice a day after meals. Should I stay home?", "Take it twice a day, after food. Do I need to rest?"], "twice a day = روزی دو بار."],
  ]],

  ["a2.hobbies.lesson-01", "کلاس نقاشی، استراحت", "هم‌کلاسی", [
    ["You're really good at drawing. How long have you been painting?", "در طراحی واقعاً خوب هستی. چند وقت است نقاشی می‌کنی؟", "بگو سه سال است نقاشی می‌کنی.", ["I've been painting for three years.", "I started three years ago."], "for + مدت با حال کامل استمراری."],
    ["What made you start?", "چه چیزی باعث شد شروع کنی؟", "بگو برای آرام شدن شروع کردی.", ["I started because it helps me relax.", "I wanted something calm to do after work."], "دلیل را با because وصل کن."],
    ["Do you paint every day?", "هر روز نقاشی می‌کشی؟", "بگو نه، آخر هفته‌ها بیشتر وقت داری.", ["Not every day. I usually paint at the weekend.", "I paint two or three times a week."], "دو یا سه بار در هفته: two or three times a week."],
    ["Would you like to join our group exhibition next month?", "دوست داری ماه آینده در نمایشگاه گروهی ما شرکت کنی؟", "بگو بله، خوشحال می‌شوی و یک اثر می‌آوری.", ["I'd love to. I can bring one of my paintings.", "Yes, that sounds great. I'll prepare a piece."], "I'd love to برای پذیرش مشتاقانه است."],
  ]],

  ["a2.city-life.lesson-01", "حیاط آپارتمان", "همسایه", [
    ["You moved in last month, right? How do you like the neighbourhood?", "ماه پیش اسباب‌کشی کردید، درست است؟ محله را چطور می‌بینید؟", "بگو دوستش داری چون نزدیک ایستگاه مترو است.", ["I like it. It's close to the metro station.", "It's great, but it's a bit noisy at night."], "برای توصیف محل از it's close to استفاده کن."],
    ["It can be noisy on Fridays. Is the traffic bad for you?", "جمعه‌ها شلوغ می‌شود. ترافیک برایتان بد است؟", "بگو صبح‌ها ترافیک داری، پس زودتر راه می‌افتی.", ["The morning traffic is bad, so I leave early.", "It's heavy in the morning, so I go by metro."], "so نتیجه را نشان می‌دهد."],
    ["Where do you usually buy groceries?", "معمولاً خرید روزانه را کجا می‌کنید؟", "بگو از بازار محلی می‌خری چون تازه‌تر است.", ["I shop at the local market because it's fresher.", "There's a small market on the corner. It's cheaper."], " برای مقایسهٔ دو صفت از -er استفاده کن."],
    ["If you need anything, just knock on my door.", "اگر چیزی لازم داشتید، در بزنید.", "تشکر کن و بگو همسایهٔ خوبی هستی.", ["Thank you, that's very kind of you.", "Thanks a lot. It's nice to have a good neighbour."], "that's very kind of you برای تشکر مؤدبانه است."],
  ]],

  ["a2.past-simple.lesson-01", "کافه، دوشنبه صبح", "همکار", [
    ["How was your weekend?", "آخر هفته چطور بود؟", "بگو خوب بود، به دیدن خانواده رفتی.", ["It was great. I visited my family.", "It was nice. I went to see my parents."], "گذشتهٔ go می‌شود went."],
    ["What did you do there?", "آنجا چه کردید؟", "بگو با هم شام خوردید و فیلم دیدید.", ["We had dinner and watched a film.", "We cooked dinner and watched a movie together."], "افعال باقاعده با -ed: watched, cooked."],
    ["Did you go anywhere on Sunday?", "یکشنبه جایی رفتید؟", "بگو نه، خانه ماندی و استراحت کردی.", ["No, I stayed at home and rested.", "I didn't go out. I stayed home and read."], "برای سؤال گذشته از did استفاده کن و فعل را ساده بگذار."],
    ["I went hiking. It was tiring but beautiful.", "من کوه رفتم. خسته‌کننده بود اما زیبا.", "بگو کاش تو هم می‌رفتی.", ["I wish I had come with you.", "That sounds great. I'd like to join next time."], "برای حسرت گذشته از I wish I had + past participle استفاده کن."],
  ]],

  ["a2.hotel-booking.lesson-01", "تماس با هتل", "پذیرش هتل", [
    ["Good afternoon, Sea View Hotel. How may I help you?", "بعدازظهر بخیر، هتل سی‌ویو. چطور کمکتان کنم؟", "بگو می‌خواهی برای دو شب اتاق رزرو کنی.", ["I'd like to book a room for two nights, please.", "Hello, do you have a double room for two nights?"], "برای مدت اقامت از for two nights استفاده کن."],
    ["Certainly. When would you like to arrive?", "حتماً. چه زمانی می‌خواهید برسید؟", "بگو پنج‌شنبه می‌رسی و یکشنبه می‌روی.", ["I'm arriving on Thursday and leaving on Sunday.", "From Thursday to Sunday, please."], "ورود/خروج با on + روز."],
    ["A single or a double room?", "اتاق یک‌نفره یا دونفره؟", "بگو اتاق دونفره با صبحانه می‌خواهی.", ["A double room with breakfast, please.", "A double room, and is breakfast included?"], "included یعنی در قیمت هست."],
    ["That's 2 million toman per night. Shall I hold the room for you?", "شبی دو میلیون تومان است. اتاق را برایتان نگه دارم؟", "بگو بله، رزرو کن و نام بده.", ["Yes, please. The booking is under my family name.", "Yes, that's fine. Use my family name."], "برای تأیید رزرو از Yes, please استفاده کن."],
  ]],

  ["a2.plane-ticket.lesson-01", "آژانس مسافرتی", "کارمند آژانس", [
    ["Good morning. Where would you like to fly?", "صبح بخیر. به کجا می‌خواهید پرواز کنید؟", "بگو می‌خواهی پرواز برگشت به تهران بخری.", ["I'd like a return ticket to the capital, please.", "I need a flight there and back."], "return ticket = بلیت رفت‌وبرگشت."],
    ["When do you want to travel?", "چه زمانی می‌خواهید سفر کنید؟", "بگو سه‌شنبهٔ آینده بروی و جمعه برگردی.", ["I'd like to leave next Tuesday and come back on Friday.", "Next Tuesday, returning on Friday."], "leave و come back برای حرکت و بازگشت."],
    ["The morning flight is cheaper. Is that OK?", "پرواز صبح ارزان‌تر است. مشکلی نیست؟", "بگو پرواز صبح خوب است و بپرس چقدر طول می‌کشد.", ["The morning flight is fine. How long does it take?", "That's fine. Is it a direct flight?"], "direct flight یعنی بدون توقف."],
    ["About two and a half hours. Would you like a window seat?", "حدود دو ساعت و نیم. صندلی کنار پنجره می‌خواهید؟", "بگو بله، کنار پنجره می‌خواهی.", ["Yes, a window seat, please.", "Window seat if possible, please."], "برای درخواست با احتمال: if possible."],
  ]],

  ["a2.hotel-checkin.lesson-01", "پذیرش هتل، عصر", "پذیرش هتل", [
    ["Good evening. Do you have a reservation?", "عصر بخیر. رزرو دارید؟", "بگو بله، به نام رضایی برای دو شب.", ["Yes, I have a reservation for two nights.", "Yes, the reservation is for two nights."], "under + نام یعنی به آن نام ثبت شده."],
    ["Let me check. Could I see your passport, please?", "اجازه بدهید بررسی کنم. پاسپورتتان را می‌بینم؟", "پاسپورت را بده و بپرس صبحانه ساعت چند است.", ["Here it is. What time is breakfast?", "Of course. By the way, when is breakfast served?"], "by the way برای آوردن موضوع فرعی است."],
    ["Breakfast is from seven to ten. Your room is on the third floor.", "صبحانه از هفت تا ده است. اتاقتان در طبقهٔ سوم است.", "بپرس آسانسور کجاست و وای‌فای رمز دارد یا نه.", ["Where is the lift? And is there internet in the room?", "Thanks. Is there internet in the room?"], "is there برای پرسیدن وجود چیزی است."],
    ["The lift is on your right. Here is the Wi-Fi password.", "آسانسور سمت راست شماست. این هم رمز وای‌فای.", "تشکر کن و بگو تا ساعت شش بیرون می‌روی.", ["Thank you. I'll be out until six.", "Thanks a lot. I'm going out, but I'll be back for dinner."], "until برای پایان یک بازهٔ زمانی است."],
  ]],

  ["a2.train-bus.lesson-01", "باجهٔ فروش بلیت راه‌آهن", "کارمند باجه", [
    ["Good morning. Where are you travelling today?", "صبح بخیر. امروز کجا سفر می‌کنید؟", "بگو به اصفهان می‌روی و بلیت می‌خواهی.", ["To the south, please, one ticket.", "I'd like a ticket to the south."], "a ticket to + مقصد."],
    ["One way or return?", "یک‌طرفه یا رفت‌وبرگشت؟", "بگو رفت‌وبرگشت برای فردا.", ["A return ticket for tomorrow, please.", "Return, for tomorrow morning."], "one way = یک‌طرفه."],
    ["The eight o'clock train is full. There is one at ten.", "قطار ساعت هشت پر است. یکی ساعت ده هست.", "بگو قطار ده خوب است و بپرس واگن چندم است.", ["Ten o'clock is fine. Which platform does it leave from?", "That works. Is there a sleeper car?"], "platform یعنی سکوی ایستگاه."],
    ["Platform three. Do you want a seat by the window?", "سکوی سه. صندلی کنار پنجره می‌خواهید؟", "بگو فرقی نمی‌کند اما اگر ممکن است کنار پنجره.", ["It doesn't matter, but a window seat would be nice.", "Any seat is fine, thank you."], "It doesn't matter یعنی فرقی نمی‌کند."],
  ]],

  ["a2.travel-info.lesson-01", "دفتر اطلاعات گردشگری", "کارمند اطلاعات", [
    ["Welcome! How can I help you today?", "خوش آمدید! امروز چطور کمکتان کنم؟", "بگو دو روز در شهر هستی و چه چیزی ببینی.", ["I'm here for two days. What should I see?", "I have two days. What do you recommend?"], "for two days برای مدت اقامت است."],
    ["I'd start with the old bazaar, then the museum.", "من با بازار قدیمی شروع می‌کنم، بعد موزه.", "بپرس بازار چند ساعت باز است.", ["How long is the bazaar open?", "What time does the bazaar close?"], "how long برای مدت زمان است."],
    ["It's open from nine to six. Would you like a map?", "از نه تا شش باز است. نقشه می‌خواهید؟", "بگو بله نقشه می‌خواهی و بپرس تور هم هست.", ["Yes, please. Do you also have guided tours?", "A map would be great. Is there a tour in English?"], "would be great برای پذیرش مؤدبانه است."],
    ["Yes, there's a walking tour at four. It costs 200 thousand toman.", "بله، ساعت چهار تور پیاده‌روی هست. دویست هزار تومان است.", "بگو ثبت‌نام می‌کنی و بپرس از کجا شروع می‌شود.", ["I'll join it. Where does it start?", "Please book me a place. Where do we meet?"], "join a tour یعنی در تور شرکت کردن."],
  ]],

  ["a2.holiday-plans.lesson-01", "کافه، برنامهٔ تعطیلات", "دوست", [
    ["Have you decided where to go for the holidays?", "تصمیم گرفتی تعطیلات کجا بروی؟", "بگو فکر می‌کنی شمال بروی.", ["I'm thinking of going to the north.", "I haven't decided yet, but maybe the north."], "I'm thinking of + ing برای برنامهٔ نامشخص است."],
    ["That sounds nice. How long will you stay?", "خوب به نظر می‌رسد. چقدر می‌مانی؟", "بگو یک هفته می‌مانی.", ["I'll stay for a week, if the weather is good.", "About a week, from Friday to Friday."], "if شرط را بیان می‌کند."],
    ["Where will you stay?", "کجا اقامت می‌کنی؟", "بگو در یک اقامتگاه کوچک نزدیک جنگل.", ["In a small guesthouse near the forest.", "I'll stay in a guesthouse close to the sea."], "near و close to برای مجاورت است."],
    ["Can I come with you?", "می‌توانم با تو بیایم؟", "بگو خوشحال می‌شوی، اگر جا باشد.", ["Of course! I'd be happy to have company.", "Sure, if there's a free room."], "company یعنی همراه، نه شرکت."],
  ]],

  ["a2.buying-clothes.lesson-01", "پوشاک‌فروشی، اتاق پرو", "فروشنده", [
    ["How does it fit?", "اندازه‌اش چطور است؟", "بگو کوتاه است و یک سایز بزرگ‌تر می‌خواهی.", ["It's a bit short. Do you have a larger size?", "It fits, but I'd like to try one size bigger."], "a bit برای مقدار کم است."],
    ["Here's a large. Would you like a different colour?", "این سایز بزرگ است. رنگ دیگری می‌خواهید؟", "بگو همین آبی خوب است.", ["This blue is fine, thank you.", "I like this colour. I'll take it."], "I'll take it اعلام خرید است."],
    ["The fitting room is over there. Is everything OK?", "اتاق پرو آنجاست. همه‌چیز خوب است؟", "بگو بله، با این یکی راحتی.", ["Yes, this one is comfortable.", "It's perfect. I'll take this one."], "comfortable برای لباس یعنی راحت."],
    ["Would you like to pay by card?", "با کارت پرداخت می‌کنید؟", "بگو بله کارت می‌کشی و رسید می‌خواهی.", ["Yes, by card. Could I have a receipt?", "Card, please. Can I get a receipt?"], "receipt رسید خرید است."],
  ]],

  ["a2.returns.lesson-01", "فروشگاه، میز پشتیبانی", "کارمند فروشگاه", [
    ["Good afternoon. How can I help you?", "بعدازظهر بخیر. چطور کمکتان کنم؟", "بگو این کفش را می‌خواهی برگردانی.", ["I'd like to return these shoes, please.", "I want to return this pair of shoes."], "return یعنی مرجوع کردن."],
    ["Is there a problem with them?", "مشکلی دارند؟", "بگو سایز کوچک است و دو روز پیش خریدی.", ["They're too small, and I bought them two days ago.", "The size is wrong. I bought them on Monday."], "ago برای گذشتهٔ ساده است."],
    ["Do you have the receipt?", "رسید دارید؟", "بگو بله رسید داری و نشان می‌دهی.", ["Yes, here's the receipt.", "I do. Here it is."], "Here's the receipt هنگام ارائهٔ رسید است."],
    ["Would you like a refund or an exchange?", "بازپرداخت می‌خواهید یا تعویض؟", "بگو تعویض می‌خواهی، سایز بزرگ‌تر.", ["I'd prefer an exchange, a bigger size please.", "An exchange would be better, if you have a larger size."], "refund پول برگشتی، exchange تعویض است."],
  ]],

  ["a2.payments.lesson-01", "صندوق فروشگاه", "صندوقدار", [
    ["That's 450 thousand toman, please.", "چهارصد و پنجاه هزار تومان می‌شود.", "بپرس کارت می‌گیرند یا نه.", ["Can I pay by card?", "Do you accept credit cards?"], "accept برای پذیرش پرداخت است."],
    ["Of course. Would you like a bag?", "البته. کیسه می‌خواهید؟", "بگو نه ممنوعیت پلاستیک داریم، خودت آورده‌ای.", ["No, thank you. I brought my own bag.", "No need, I have a bag with me."], "my own یعنی مال خودم."],
    ["The card machine is asking for your PIN.", "دستگاه کارت رمز شما را می‌خواهد.", "بگو وارد کردی و بپرس پرداخت انجام شد یا نه.", ["I've entered it. Did the payment go through?", "Done. Has it worked?"], "go through برای موفقیت تراکنش است."],
    ["It went through. Here's your receipt.", "انجام شد. این هم رسیدتان.", "تشکر کن و بپرس فاکتور را ایمیل می‌کنند یا نه.", ["Thanks. Can you email me the invoice?", "Thank you. Could I get the receipt by email?"], "by email برای روش ارسال است."],
  ]],

  ["a2.symptoms.lesson-01", "درمانگاه، اتاق معاینه", "پزشک", [
    ["What brings you in today?", "امروز چه چیزی شما را آورد؟", "بگو از دیشب حالت تهوع و سرگیجه داری.", ["I've felt sick and dizzy since last night.", "I have a stomach ache and I feel dizzy."], "feel dizzy یعنی سرگیجه داشتن."],
    ["Have you eaten anything unusual?", "چیز غیرعادی خورده‌اید؟", "بگو بیرون غذا خوردی، غذای دریایی.", ["I ate seafood at a restaurant yesterday.", "Yes, I had seafood last night."], "گذشتهٔ eat می‌شود ate."],
    ["Any other symptoms? A headache, perhaps?", "علامت دیگری؟ مثلاً سردرد؟", "بگو سردرد خفیف داری.", ["I have a slight headache as well.", "Just a light headache."], "as well یعنی هم، در انتهای جمله."],
    ["I think it's food poisoning. Drink plenty of water today.", "فکر می‌کنم مسمومیت غذایی است. امروز آب زیاد بنوش.", "بپرس باید دارو مصرف کنی یا نه.", ["Do I need any medicine?", "Should I take anything for it?"], "should برای توصیهٔ پزشکی است."],
  ]],

  ["a2.pharmacy.lesson-01", "داروخانه", "داروساز", [
    ["Good evening. Do you have a prescription?", "عصر بخیر. نسخه دارید؟", "بگو بله نسخه دارم و می‌خواهم پرش کنم.", ["Yes, I have a prescription, please.", "Here's my prescription. Can you fill it?"], "fill a prescription یعنی نسخه را آماده کردن."],
    ["These tablets are twice a day. Are you allergic to anything?", "این قرص‌ها روزی دو بار است. به چیزی حساسیت دارید؟", "بگو به پنی‌سیلین حساسیت داری.", ["Yes, I'm allergic to penicillin.", "I'm allergic to penicillin, but nothing else."], "allergic to + ماده."],
    ["Thanks for telling me. I'll give you an alternative.", "ممنون که گفتید. جایگزین می‌دهم.", "بپرس عوارض دارد یا نه.", ["Does it have any side effects?", "Are there any side effects I should know about?"], "side effects یعنی عوارض جانبی."],
    ["It may make you sleepy. Take it at night.", "ممکن است خواب‌آور باشد. شب مصرف کنید.", "تکرار کن و بپرس با غذا مصرف کنی یا نه.", ["At night, then. Should I take it with food?", "So at night. With or without food?"], "may برای احتمال است."],
  ]],

  ["a2.childhood.lesson-01", "پارک، نیمکت", "دوست دوران کودکی", [
    ["Do you remember our old school?", "مدرسهٔ قدیمی‌مان را یادت هست؟", "بگو بله و بگو معلم ریاضی را یادت هست.", ["Of course! I still remember our maths teacher.", "Yes, I remember it well."], "remember برای خاطره است."],
    ["What did you use to do after school?", "بعد از مدرسه چه می‌کردی؟", "بگو با بچه‌ها فوتبال بازی می‌کردی.", ["We used to play football in the street.", "I used to play football every afternoon."], "used to برای عادت گذشته است."],
    ["Was your family strict about homework?", "خانواده‌ات دربارهٔ تکلیف سخت‌گیر بودند؟", "بگو مادرم سخت‌گیر بود اما مهربان.", ["My mother was strict, but she was kind.", "They wanted me to study first, then play."], "want somebody to do برای اجبار ملایم است."],
    ["Those days were good, weren't they?", "آن روزها خوب بودند، نه؟", "موافقت کن و بگو دلت برایشان تنگ شده.", ["They were. I miss those days.", "Yes, life was simpler then."], "I miss یعنی دلم تنگ شده."],
  ]],

  ["a2.celebrations.lesson-01", "خانهٔ دوست، جشن نوروز", "میزبان", [
    ["Welcome! Come in. Happy New Year!", "خوش آمدی! بیا داخل. عید مبارک!", "تبریک بگو و از دعوت تشکر کن.", ["Same to you. Thanks for inviting me.", "Thank you for inviting me. Same to you."], "تبریک را قبل از تشکر بگو؛ در انگلیسی تشکر بعد از تبریک می‌آید."],
    ["Would you like some tea and sweets?", "چای و شیرینی میل داری؟", "بگو بله، شیرینی خانگی را امتحان می‌کنی.", ["Yes, please. Did you make these yourself?", "I'd love some tea. The sweets look great."], "make yourself برای پختن خانگی است."],
    ["My mother made them this morning.", "مادرم امروز صبح درست کرد.", "بگو خیلی خوشمزه است.", ["They're delicious. Please thank her for me.", "Wow, they're really good."], "delicious برای تعریف از غذا است."],
    ["Do you celebrate with your family too?", "تو هم با خانواده جشن می‌گیری؟", "بگو بله، همهٔ خانواده جمع می‌شوند.", ["Yes, my whole family gets together.", "We always spend the first day together."], "get together یعنی دور هم جمع شدن."],
  ]],

  ["a2.invitations.lesson-01", "تماس با دوست برای دعوت", "دوست", [
    ["Hi! I'm calling about Saturday. Are you free?", "سلام! دربارهٔ شنبه زنگ می‌زنم. وقت داری؟", "بگو بله آزادی و بپرس مناسبت چیست.", ["Yes, I'm free. What's happening on Saturday?", "I think so. What's the occasion?"], "occasion یعنی مناسبت."],
    ["We're having a small dinner at my place. Can you come?", "یک شام کوچک خانهٔ ما داریم. می‌توانی بیایی؟", "بگو با خوشحالی می‌آیی.", ["I'd love to come, thank you.", "That sounds great. Count me in."], "Count me in یعنی من هم هستم."],
    ["Great! It starts at seven. Do you need my address?", "عالی! ساعت هفت شروع می‌شود. آدرس مرا لازم داری؟", "بگو بله آدرس را بفرست.", ["Yes, please send it to me.", "Could you text me the address?"], "text me یعنی پیامک بفرست."],
    ["I'll send it now. Can you bring something to drink?", "الان می‌فرستم. می‌توانی نوشیدنی بیاوری؟", "بگو بله، نوشیدنی و دسر می‌آوری.", ["Sure, I'll bring some juice and a dessert.", "Of course. I'll bring drinks for everyone."], "bring یعنی آوردن به محل میزبان."],
  ]],

  ["a2.phone.lesson-01", "تماس تلفنی با یک شرکت", "منشی", [
    ["Good morning, Delta Company. How can I help you?", "صبح بخیر، شرکت دلتا. چطور کمکتان کنم؟", "بگو می‌خواهی با آقای رضایی صحبت کنی.", ["Could I speak to the manager, please?", "Hello, is the manager available?"], "Could I speak to ... please مؤدبانه است."],
    ["May I ask who's calling?", "اجازه بدهید بپرسم چه کسی تماس گرفته؟", "خودت را معرفی کن و بگو دربارهٔ قرارداد تماس گرفتی.", ["This is the supplier's assistant, about the new contract.", "It's about the new contract, from the supplier."], "I'm calling about برای دلیل تماس است."],
    ["I'm afraid he's in a meeting. Can I take a message?", "متأسفانه در جلسه است. پیغام بگیرم؟", "پیغام بده که بعدازظهر تماس بگیرد.", ["Yes, please ask him to call me this afternoon.", "Could you tell him to call me back after lunch?"], "call back یعنی دوباره تماس گرفتن."],
    ["Certainly. Does he have your number?", "حتماً. شمارهٔ شما را دارد؟", "شماره را بده و تشکر کن.", ["Yes, but let me give it again, slowly.", "He does, but let me repeat it."], "برای اطمینان، شماره را تکرار کن."],
  ]],

  ["a2.emails.lesson-01", "دفتر کار، نوشتن ایمیل", "همکار", [
    ["Are you writing to the supplier?", "داری به تأمین‌کننده ایمیل می‌زنی؟", "بگو بله و می‌خواهی تأخیر را پیگیری کنی.", ["Yes, I'm asking about the delay.", "I am. I need to follow up on the late delivery."], "follow up یعنی پیگیری کردن."],
    ["How will you start it?", "چطور شروعش می‌کنی؟", "بگو با Dear Mr Brown شروع می‌کنی.", ["I'll open with a formal greeting.", "With a greeting and the client's name."], "ایمیل رسمی با Dear + نام شروع می‌شود."],
    ["And how do you finish?", "و چطور تمامش می‌کنی؟", "بگو با Best regards و نامت تمام می‌کنی.", ["I'll finish with 'Best regards' and my name.", "'Kind regards' and then my name."], "Best regards بستن متداول ایمیل است."],
    ["Should we copy the manager?", "مدیر را در جریان بگذاریم؟", "بگو بله، باید در جریان باشد.", ["Yes, I'll copy her on the email.", "Good idea. I'll put her in cc."], "copy somebody on an email = رونوشت دادن."],
  ]],
];
