// Conversation dialogues, B1 and B2: the learner argues, negotiates and explains.
//
// Shape: [lessonId, sceneFa, partnerFa, turns[]]
// turn:  [question, questionFa, taskFa, answers[], tipFa]

export const bLevels = [
  ["b1.work.lesson-01", "جلسهٔ ارزیابی سالانه با مدیر", "مدیر تیم", [
    ["Let's talk about your year. What are you most proud of?", "دربارهٔ سالت صحبت کنیم. به چه چیزی بیشتر افتخار می‌کنی؟", "به یک دستاورد مشخص اشاره کن و نتیجه‌اش را بگو.", ["I'm proud of the new support process. We cut waiting time by half.", "The redesign of our support process, which brought complaints down."], "برای دستاورد از 숫자 یا نتیجهٔ عینی استفاده کن."],
    ["That's impressive. What would you like to improve next year?", "تحسین‌برانگیز است. سال بعد چه چیزی را می‌خواهی بهتر کنی؟", "بگو می‌خواهی ارائه‌هایت را قوی‌تر کنی.", ["I'd like to become better at presenting to clients.", "I want to improve my presentation skills."], "I'd like to + فعل ساده برای هدف است."],
    ["We could send you to a training course. Would that help?", "می‌توانیم تو را به دورهٔ آموزشی بفرستیم. کمک می‌کند؟", "بگو مفید است اما زمان می‌خواهی.", ["That would help, if I can arrange my workload around it.", "It would. I'd just need time to plan it properly."], "if برای شرط همراه پیشنهاد است."],
    ["Let's set a review in three months. Does that work for you?", "سه ماه دیگر بازبینی بگذاریم. برایت مناسب است؟", "موافقت کن و بگو هدفت چیست.", ["That works. I'll bring examples of my next two presentations.", "That's fine with me. I'll aim to run two client sessions by then."], "That works for me یعنی با آن موافقم."],
  ]],

  ["b1.news.lesson-01", "گفتگو با خبرنگار در یک نشست مطبوعاتی", "خبرنگار", [
    ["You've said the new policy will help small businesses. How exactly?", "شما گفتید سیاست جدید به کسب‌وکارهای کوچک کمک می‌کند. دقیقاً چگونه؟", "به یک اقدام مشخص اشاره کن و محدودیتش را هم بگو.", ["Mostly through lower taxes, though the effect won't be immediate.", "By simplifying the paperwork, which is what owners complain about most."], "برای پاسخ متعادل از though استفاده کن."],
    ["Critics say it will cost too much. What do you say to that?", "منتقدان می‌گویند هزینه‌اش زیاد است. چه پاسخی دارید؟", "نقد را جدی بگیر و پاسخ متقابل بده.", ["I understand the concern, but the cost of doing nothing is higher.", "That's a fair point, although we've budgeted for it over three years."], "That's a fair point نقد را می‌پذیرد."],
    ["When will people actually see a difference?", "مردم واقعاً چه زمانی تفاوت را می‌بینند؟", "بگو شش ماه تا یک سال طول می‌کشد.", ["Realistically, within six months to a year.", "Some changes start next quarter; the rest will take a year."], "realistically برای واقع‌گرایی است."],
    ["Can I quote you on that?", "می‌توانم از شما نقل قول کنم؟", "بگو بله، اگر دقیق نقل شود.", ["Yes, as long as the wording is accurate.", "You can, but please include the full answer."], "as long as شرط است، نه مقایسه."],
  ]],

  ["b1.plans.lesson-01", "کافه، تصمیم مشترک برای سفر", "دوست", [
    ["So, are we going to book the trip or not?", "خب، بلیت سفر را می‌خریم یا نه؟", "بگو اگر تا جمعه تصمیم نگیریم، قیمت بالا می‌رود.", ["If we don't decide by Friday, the price will go up.", "We should decide soon, otherwise we'll lose the cheap flights."], "شرط نوع اول با if + حال ساده است."],
    ["I'm worried about the hotel cost. What if we stay one night less?", "نگران هزینهٔ هتل هستم. اگر یک شب کمتر بمانیم؟", "بگو ایده خوب است اما مسیر طولانی می‌شود.", ["That could work, although it makes the journey very long.", "It's an option, but we'd arrive home at midnight."], "could برای پیشنهاد نرم است."],
    ["Let's split the cost and book tonight.", "هزینه را تقسیم کنیم و امشب رزرو کنیم.", "موافقت کن و بگو چه کسی رزرو کند.", ["Agreed. I'll book the flights and you book the hotel.", "Deal. I'll do the flights tonight."], "Deal برای توافق نهایی است."],
    ["What if something unexpected happens?", "اگر چیزی پیش‌بینی‌نشده پیش بیاید؟", "بگو بیمهٔ لغو سفر می‌گیری.", ["Then we claim it through travel insurance.", "We'll be covered, because I always take cancellation insurance."], "claim through یعنی از طریق آن مطالبه کردن."],
  ]],

  ["b1.experiences.lesson-01", "هتل، صبحانهٔ کاری", "همسفر کاری", [
    ["You travel a lot for work. What was your hardest trip?", "برای کار زیاد سفر می‌کنی. سخت‌ترین سفرت کدام بود؟", "یک سفر مشخص را تعریف کن و بگو چرا سخت بود.", ["A trip to the mountains where our flight was cancelled twice.", "Once everything went wrong: I lost my bag and missed a meeting."], "برای روایت از گذشتهٔ ساده استفاده کن."],
    ["How did you deal with it?", "چطور از پسش برآمدی؟", "بگو برنامه را تغییر دادی و آنلاین جلسه برگزار کردی.", ["I rearranged the schedule and held the meeting online.", "In the end I rescheduled and worked from the hotel."], "in the end یعنی سرانجام."],
    ["What did you learn from that?", "از آن چه یاد گرفتی؟", "بگو یاد گرفتی همیشه نسخهٔ پشتیبان برنامه داشته باشی.", ["I learned to always have a backup plan.", "Now I keep the important documents online as well."], "learn to + فعل برای مهارت آموخته است."],
    ["Would you do that trip again?", "آن سفر را دوباره می‌رفتی؟", "بگو بله، با اینکه سخت بود ارزشش را داشت.", ["Probably, even though it was exhausting.", "Yes, it taught me more than a smooth trip would."], "even though برای تناقض است."],
  ]],

  ["b1.money.lesson-01", "مشاوره با مشاور مالی", "مشاور مالی", [
    ["Let's look at your budget. How much do you save each month?", "بیایید بودجه‌ات را ببینیم. هر ماه چقدر پس‌انداز می‌کنی؟", "بگو حدود بیست درصد درآمد را پس‌انداز می‌کنی.", ["About twenty percent of my income.", "Roughly a fifth, when nothing unexpected comes up."], "about و roughly برای تقریب‌اند."],
    ["And where does the rest go?", "و بقیه کجا می‌رود؟", "بگو اجاره و خرید روزانه بیشترین سهم را دارد.", ["Most of it goes on rent and everyday shopping.", "Rent takes the biggest part, then food and transport."], "spend on برای هزینه کرد نام."],
    ["Do you have money set aside for emergencies?", "پولی برای شرایط اضطراری کنار گذاشته‌ای؟", "بگو سه ماه هزینهٔ زندگی ذخیره داری.", ["Yes, I have about three months of expenses saved.", "I've started one; it covers two months so far."], "set aside یعنی کنار گذاشتن."],
    ["That's a good start. Could you save a little more by cutting subscriptions?", "شروع خوبی است. می‌توانی با حذف اشتراک‌ها بیشتر پس‌انداز کنی؟", "بگو می‌توانی و دو اشتراک را لغو می‌کنی.", ["I could cancel two subscriptions I hardly use.", "That's possible; I'll review them this week."], "hardly use یعنی تقریباً استفاده نمی‌کنم."],
  ]],

  ["b1.health-lifestyle.lesson-01", "کلاس ورزش، بعد از تمرین", "مربی", [
    ["How often do you exercise?", "چند وقت یک‌بار ورزش می‌کنی؟", "بگو هفته‌ای دو بار، کمتر از قبل.", ["Twice a week, which is less than I used to.", "About twice a week, but I'd like to do more."], "less than I used to یعنی از قبل کمتر."],
    ["What stops you from doing more?", "چه چیزی مانع بیشتر ورزش کردنت می‌شود؟", "بگو کار و نبود وقت.", ["Mostly work. I often finish too late.", "Time, honestly. My evenings are quite short."], "honestly برای صداقت در پاسخ است."],
    ["What about your sleep?", "خوابت چطور است؟", "بگو هفت ساعت می‌خوابی اما کیفیتش خوب نیست.", ["I sleep about seven hours, but not very deeply.", "I get enough hours, although I wake up at night."], "although برای تضاد ملایم است."],
    ["Try walking after dinner and no screens after ten. Could you do that?", "بعد از شام پیاده‌روی کن و بعد از ده گوشی نگاه نکن. می‌توانی؟", "بگو پیاده‌روی را انجام می‌دهی اما گوشی سخت است.", ["The walk is easy, but leaving my phone is harder.", "I can try the walk first and see how it goes."], "I'll try ... first برای شروع تدریجی است."],
  ]],

  ["b2.technology.lesson-01", "ضبط یک پادکست", "مجری پادکست", [
    ["Some people say AI will take most jobs. Do you agree?", "برخی می‌گویند هوش مصنوعی بیشتر شغل‌ها را می‌گیرد. موافقی؟", "بگو تا حدی موافقی اما فقط کارهای تکراری.", ["To some extent, but mainly routine tasks rather than whole jobs.", "Partly. It will change tasks more than it removes roles."], "to some extent برای موافقت نسبی است."],
    ["What should workers do about it?", "کارگرها باید چه کنند؟", "بگو مهارت‌هایی را یاد بگیرند که ماشین نمی‌تواند.", ["They should focus on skills machines are weak at, like judgement.", "Learning to work alongside these tools is the practical answer."], "should برای توصیه است."],
    ["Isn't that easy to say if you already have a good job?", "اگر شغل خوبی داشته باشی، گفتنش آسان نیست؟", "بپذیر و به محدودیت دسترسی اشاره کن.", ["That's a fair challenge. Access to training is uneven.", "You're right that it's easier said than done."], "easier said than done یعنی گفتنش آسان‌تر از انجامش است."],
    ["What gives you hope?", "چه چیزی به تو امید می‌دهد؟", "بگو ابزارهای ارزان آموزش را مثال بزن.", ["Cheap tools that let anyone learn at their own pace.", "The fact that good courses are now almost free."], "The fact that برای مقدمهٔ دلیل است."],
  ]],

  ["b2.environment.lesson-01", "جلسهٔ محله دربارهٔ پسماند", "کارشناس محیط زیست", [
    ["Our neighbourhood recycles very little. Where should we start?", "محلهٔ ما بازیافت بسیار کمی دارد. از کجا شروع کنیم؟", "بگو از تفکیک در خانه شروع کنند.", ["Start at home: separate paper, glass and food waste.", "A simple bin system at home would be the first step."], "would be برای پیشنهاد مؤدبانه است."],
    ["Isn't it expensive to set that up?", "راه‌اندازی‌اش گران نیست؟", "بگو هزینهٔ اولیه دارد اما در طول زمان می‌صرفد.", ["There's an upfront cost, but it pays off within a year.", "Not really, if the city collects separately."], "pay off یعنی به‌صرفه شدن."],
    ["What if people simply don't participate?", "اگر مردم اصلاً شرکت نکنند؟", "بگو آگاهی و مشوق لازم است.", ["Then we need incentives, and clear information about the effect.", "Education first, then small rewards for participation."], "then برای گام بعدی در استدلال است."],
    ["How would we measure success?", "موفقیت را چطور اندازه بگیریم؟", "بگو با وزن پسماند تفکیک‌شده در ماه.", ["By the weight of separated waste each month.", "We could weigh what's collected every month and compare."], "by + فعل ing برای روش اندازه‌گیری است."],
  ]],

  ["b2.interview.lesson-01", "مصاحبهٔ شغلی در یک شرکت", "مصاحبه‌گر", [
    ["Tell me about a time you solved a difficult problem.", "از زمانی بگو که مسئلهٔ دشواری را حل کردی.", "یک موقعیت مشخص را با نتیجه تعریف کن.", ["Last year our system failed before a launch. I traced it in two hours.", "At my last job, I reorganized the schedule to meet a deadline."], "برای تجربهٔ کاری از ساختار موقعیت، اقدام، نتیجه استفاده کن."],
    ["Why did you choose that approach?", "چرا آن روش را انتخاب کردی؟", "بگو چون کم‌ریسک‌ترین راه بود.", ["Because it was the least risky option under time pressure.", "It let us keep working while we fixed the cause."], "because دال را صریح می‌کند."],
    ["What would you do differently now?", "الان چه کار دیگری می‌کردی؟", "بگو از اول مستندسازی می‌کردی.", ["I'd document the process from the start.", "I'd involve the team earlier, not after the diagnosis."], "I'd برای فرض گذشتهٔ ناتمام است."],
    ["Do you have any questions for us?", "سؤالی از ما داری؟", "دو سؤال خوب دربارهٔ تیم و انتظارات بپرس.", ["Yes: how is success measured in this role?", "What does the first three months look like for this team?"], "سؤال خوب مصاحبه دربارهٔ نقش است، نه حقوق در ابتدا."],
  ]],

  ["b2.media.lesson-01", "جلسهٔ کمپین تبلیغاتی", "همکار تبلیغات", [
    ["Our last ad got clicks but no sales. Why do you think that is?", "تبلیغ قبلی کلیک گرفت اما فروش نداشت. به نظرت چرا؟", "بگو پیام با مخاطب هم‌خوان نبود.", ["The message promised excitement, but it didn't explain the product.", "We attracted curiosity rather than buyers."], "rather than برای ترجیح/تفکیک است."],
    ["How should we change it?", "چطور عوضش کنیم؟", "بگو به سود روشن برای کاربر تمرکز کنید.", ["Focus on one clear benefit, and show it in the first five seconds.", "Lead with the problem it solves."], "focus on برای تأکید است."],
    ["Isn't that boring? Emotion sells.", "این خسته‌کننده نیست؟ احساس می‌فروشد.", "بپذیر اما بگو احساس باید به محصول وصل باشد.", ["Emotion works when it's tied to something the product does.", "Fair, as long as the feeling matches the promise."], "as long as شرط است."],
    ["What should we test first?", "اول چه چیزی را تست کنیم؟", "بگو دو نسخهٔ پیام را برای مخاطب مشابه.", ["Let's test two messages with a small audience.", "A simple test on the headline would help most."], "let's برای پیشنهاد مشترک است."],
  ]],

  ["b2.problems.lesson-01", "تماس با پشتیبانی یک فروشگاه آنلاین", "کارمند پشتیبانی", [
    ["Thank you for calling. What's the issue?", "ممنون از تماس. مشکل چیست؟", "بگو سفارشت دو هفته است نرسیده.", ["My order hasn't arrived, and it's been two weeks.", "I'm calling about an order that is still missing."], "hasn't arrived با for/two weeks آمده است."],
    ["I see a delay in our warehouse. I'm sorry about that.", "در انبار تأخیر داریم. عذرخواهی می‌کنم.", "بپرس چه زمانی می‌رسد و بگو برنامه‌ات به‌هم خورده.", ["Can you tell me when it will arrive? I needed it last week.", "I understand, but I planned around the original date."], "I planned around یعنی بر اساس آن برنامه ریختم."],
    ["We can send it tomorrow with express delivery at no extra cost.", "می‌توانیم فردا با ارسال سریع و بدون هزینهٔ اضافه بفرستیم.", "پیشنهاد را بپذیر و بپرس تضمینی هست یا نه.", ["That would help. Is the new date guaranteed?", "I'd accept that, if you can confirm the date in writing."], "if you can confirm شرط قابل قبول است."],
    ["Yes, I'll email you the confirmation now.", "بله، تأییدیه را همین حالا ایمیل می‌کنم.", "تشکر کن و بگو در صورت تأخیر دوباره تماس می‌گیری.", ["Thank you. If it doesn't arrive by Thursday, I'll call again.", "That's fair. I'll wait for the email."], "if + حال ساده در شرط آینده."],
  ]],

  ["b2.research.lesson-01", "دفتر استاد راهنما", "استاد راهنما", [
    ["Your sample is quite small. How do you justify it?", "نمونهٔ شما کوچک است. چطور توجیه می‌کنید؟", "بگو محدودیت دسترسی و روش انتخاب را توضیح می‌دهی.", ["The group is small, but it's the whole population of that department.", "I explain the selection method and the limits it creates."], "justify یعنی توجیه کردن."],
    ["What evidence would change your conclusion?", "چه شواهدی نتیجه‌گیری‌تان را تغییر می‌دهد؟", "بگو اگر گروه بزرگ‌تر نتیجه متفاوت شد.", ["Larger data showing the opposite trend would challenge it.", "If a bigger sample gave the same result, my claim would be stronger."], "would برای شرطی نوع دوم است."],
    ["Have you considered the alternative explanation?", "توضیح جایگزین را در نظر گرفته‌اید؟", "بگو در بخش محدودیت‌ها آمده است.", ["Yes, it's discussed in the limitations section.", "I mention it, but the data can't separate the two yet."], "limitations section بخش محدودیت‌های مقاله است."],
    ["When will you have the revised draft?", "پیش‌نویس اصلاح‌شده کِی آماده می‌شود؟", "بگو تا دو هفته آینده.", ["By the end of next week, if the data check goes smoothly.", "In two weeks, after a second round of analysis."], "by the end of برای مهلت است."],
  ]],
];
