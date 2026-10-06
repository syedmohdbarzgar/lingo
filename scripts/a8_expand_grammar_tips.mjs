/**
 * A-8 stage 1 (one-shot authoring pass): expand every lesson's `grammarTipFa`
 * from a single ~120-char tip into the multi-section teaching format:
 *
 *   ## قاعده        (2–4 plain-Persian sentences)
 *   ## جدول         (forms / comparison table, English)
 *   ## مثال‌ها      (3–4 lines: «English — فارسی»)
 *   ## خطاهای رایج  (common Persian-speaker mistakes)
 *
 * No schema change: the string is stored exactly as before (option 1 — no Room
 * migration). contentVersion stays 7 (same uncommitted bundle as A-2).
 * The mistake sections are pedagogical guidance pending teacher confirmation.
 */
import fs from "node:fs";

const PATH = "app/src/main/assets/content/lessons.json";

const tips = {
  "a1.greetings.lesson-01": `## قاعده
سلام‌ها به زمان روز بستگی دارند: Good morning تا ظهر، Good afternoon از ظهر تا غروب، Good evening غروب به بعد. Good night فقط هنگام خداحافظی است، نه سلام.
## جدول
Good morning! — صبح بخیر
Good afternoon! — عصر بخیر (تا غروب)
Good evening! — عصر/شب بخیر
Good night! — شب بخیر (خداحافظی)
## مثال‌ها
Good morning, Sara! — صبح بخیر، سارا!
Good evening. How are you? — عصر بخیر. حالت چطور است؟
Good night, see you tomorrow. — شب بخیر، فردا می‌بینمت.
## خطاهای رایج
Good night را به‌جای سلام به کار نبرید — فقط هنگام خداحافظی شب است.
اگر مطمئن نیستید، Hi یا Hello برای هر وقت روز بی‌خطر است.`,

  "a1.daily-routine.lesson-01": `## قاعده
برای کارهای تکراری و روزمره از زمان حال ساده استفاده کن. با he/she/it به فعل s می‌چسبد: She goes to work. در پرسش و منفی، فعل کمکی do/does می‌آید و فعل اصلی لخت می‌شود.
## جدول
I work / You work / He works / She works / They work
Does she work? — Yes, she does.
She doesn't work on Fridays.
## مثال‌ها
I usually wake up at seven. — معمولاً ساعت هفت بیدار می‌شوم.
She goes to work by bus. — او با اتوبوس به سر کار می‌رود.
Do you drink coffee in the morning? — صبح قهوه می‌نوشی؟
## خطاهای رایج
s را برای I/you/we/they نگذارید: «I works» غلط است.
بعد از does، فعل دوباره s نمی‌گیرد: «Does she goes?» غلط است.`,

  "a1.family.lesson-01": `## قاعده
داشتن اعضا و چیزها با have یا have got بیان می‌شود. با he/she/it شکل has می‌آید. پرسش با Do/Does…have? شروع می‌شود و پاسخ کوتاه Yes, I do است.
## جدول
I have / You have / He has / She has / We have
I have got two brothers.
Do you have a sister? — Yes, I do.
## مثال‌ها
I have got two brothers. — دو برادر دارم.
She has a daughter and a son. — او یک دختر و یک پسر دارد.
Do you have any cousins? — عموزاده یا عموزاده‌ای داری؟
## خطاهای رایج
برای he/she نگویید «She have» — «She has» درست است.
فعل داشتن را حذف نکنید: «She two brothers» غلط، «She has two brothers» درست.`,

  "a1.shopping.lesson-01": `## قاعده
قیمت را با How much is…? (مفرد/غیرقابل شمارش) و How much are…? (جمع) بپرس. اعداد ترتیبی بعد از اسم و معمولاً با the می‌آیند: the second floor.
## جدول
How much is this shirt? — این پیراهن چند است؟
How much are these shoes? — این کفش‌ها چندند؟
the first / the second / the third
## مثال‌ها
How much is this jacket? — این کت چند است؟
It's twenty dollars. — بیست دلار است.
The shoes are on the second floor. — کفش‌ها طبقه‌ی دوم است.
## خطاهای رایج
«How much cost?» غلط است — فعل is لازم: «How much is it?»
ترتیبی را قبل از اسم نگذارید: «floor second» غلط، «the second floor» درست.`,

  "a1.introductions.lesson-01": `## قاعده
برای معرفی خود و دیگران از فعل be استفاده کن: I am Mina / This is Ali. صفت‌های ملکی همیشه قبل از اسم می‌آیند و صاحب را نشان می‌دهند: my name، your friend، his sister.
## جدول
I am / You are / He is / She is / It is
my / your / his / her / our / their
## مثال‌ها
My name is Mina. — اسم من مینا است.
This is my friend Ali. — این دوست من علی است.
Her sister is a doctor. — خواهر او دکتر است.
## خطاهای رایج
ملکی با apostrophe نمی‌آید: «she's book» غلط است — «her book» درست.
حذف فعل be اشتباه است: «I Mina» نه، «I am Mina».`,

  "a1.weather.lesson-01": `## قاعده
برای وضعیت لحظه‌ای آب‌وهوا از حال استمراری (be + ing) استفاده کن: It is raining now. برای عادت و کلیت هوا حال ساده به کار می‌رود: It rains a lot in winter.
## جدول
It is raining now. — دارد باران می‌آید (لحظه‌ای)
It is cold today. — امروز سرد است (وضعیت)
It rains a lot in winter. — زمستان زیاد باران می‌بارد (عادت)
## مثال‌ها
Look! It's raining. — ببین! باران می‌آید.
It is very cold today. — امروز خیلی سرد است.
It snows in the mountains. — در کوه‌ها برف می‌بارد.
## خطاهای رایج
فعل be را ing نکنید: «It is raininging» و «It is rain» غلط — درست «It is raining» است.
برای مشاهده‌ی همین حالا از حال ساده نیاید: «Look, it rains» غلط است.`,

  "a2.airport.lesson-01": `## قاعده
اجبار و الزام با need to یا have to بیان می‌شود: You have to show your passport. برای برنامه‌ی از قبل تعیین‌شده از زمان آینده will استفاده کن: The flight will depart at ten.
## جدول
I have to / You have to / He has to / She has to
I need to buy a ticket.
The flight will depart at ten.
## مثال‌ها
You have to show your passport. — باید پاسپورتتان را نشان دهید.
I need to buy a ticket. — باید بلیت بخرم.
The gate will close in twenty minutes. — گیت بیست دقیقه‌ی دیگر بسته می‌شود.
## خطاهای رایج
بعد از have to فعل اصلی لخت می‌آید: «I have to to show» یا «I have to showing» غلط.
برای he/she نگویید «he have to» — «he has to» درست است.`,

  "a2.restaurant.lesson-01": `## قاعده
سفارش مودبانه با I'd like… (= I would like) مطرح کن و پیشنهاد را با Would you like…? بپرس. برای غیرقابل شمارش (water, bread) و جمع‌های ناموجود در جمله از some و در پرسش/منفی از any استفاده کن.
## جدول
I'd like a salad, please.
Would you like some coffee?
We don't have any bread.
Is there any water?
## مثال‌ها
I'd like chicken, please. — می‌خواهم مرغ سفارش بدهم، لطفاً.
Would you like some tea? — چای میل دارید؟
We don't have any more plates. — دیگر بشقابی نداریم.
## خطاهای رایج
I want در رستوران خشن و کودکانه است — «I'd like» مودبانه است.
some در پیشنهاد درست است (Would you like some tea؟) ولی در پرسش معمولی any می‌آید: «Is there any tea?»`,

  "a2.directions.lesson-01": `## قاعده
فعل و صفت مکان بعد از فعل می‌آیند: turn left، go straight. حرف اضافه را دقیق انتخاب کن: on برای سطح و کنار خیابان، at برای نقطه‌ی مشخص، in برای درون.
## جدول
turn left / turn right / go straight
on the left / on the right
at the bank / at the corner
in front of the hotel
## مثال‌ها
Turn right at the second traffic light. — سمت راست بپیچید، سر چراغ دوم.
The bank is on the left. — بانک سمت چپ است.
Go straight for two minutes. — دو دقیقه مستقیم بروید.
## خطاهای رایج
«in the left» غلط است — «on the left» درست.
بعد از go، جهت بدون to می‌آید: «go to straight» غلط، «go straight» درست.`,

  "a2.health.lesson-01": `## قاعده
درد و بیماری را با have got به همراه a بیان کن: I have got a headache. توصیه با should و نهی با shouldn't داده می‌شود و بعد از آن‌ها فعل لخت می‌آید.
## جدول
I have got a headache / a cough / a fever
You should rest.
You shouldn't smoke.
## مثال‌ها
I have got a terrible headache. — سردرد شدیدی دارم.
You should drink plenty of water. — باید آب فراوان بنوشید.
You shouldn't take medicine without advice. — نباید بدون توصیه دارو مصرف کنی.
## خطاهای رایج
حرف تعریف a را با درد حذف نکنید: «I have got headache» غلط — «a headache».
بعد از should فعل لخت می‌آید: «should to rest» غلط است.`,

  "a2.hobbies.lesson-01": `## قاعده
بعضی فعل‌ها بعد از خود ing می‌خواهند (enjoy, love, mind) و بعضی to + فعل (want, need, decide, would like). این دسته‌ها را با شکل کامل خودشان حفظ کن، نه با قاعده‌ی کلی.
## جدول
enjoy + doing / love + doing / mind + doing
want to do / need to do / decide to do / would like to do
## مثال‌ها
I enjoy reading books. — از خواندن کتاب لذت می‌برم.
She wants to learn photography. — او می‌خواهد عکاسی یاد بگیرد.
Do you like swimming? — شنا دوست داری؟
## خطاهای رایج
«I enjoy to read» غلط است — enjoy همیشه ing می‌خواهد.
بعد از want نگذارید ing: «I want going» غلط، «I want to go» درست.`,

  "a2.city-life.lesson-01": `## قاعده
وجود چیزی با there is (مفرد و غیرقابل شمارش) و there are (جمع) بیان می‌شود. برای مقایسه‌ی دو چیز، صفت کوتاه er می‌گیرد و با than وصل می‌شود: bigger than.
## جدول
There is a park. / There are two parks.
big → bigger than / small → smaller than
good → better than / bad → worse than
## مثال‌ها
There are two parks near my house. — نزدیک خانه‌ام دو پارک هست.
Tehran is bigger than Shiraz. — تهران از شیراز بزرگ‌تر است.
Is there a pharmacy here? — اینجا داروخانه هست؟
## خطاهای رایج
برای وجود از «It has» استفاده نکنید — آن مالکیت می‌رساند: «There are two parks» درست.
صفت را با more مقایسه نکنید: «more big» غلط، «bigger» درست.`,

  "b1.work.lesson-01": `## قاعده
وضعیت همین حالا با حال استمراری می‌آید: I'm working. تجربه‌ی کاری بدون زمان مشخص با حال کامل (have/has + قسمت سوم) بیان می‌شود: I have worked in finance for years.
## جدول
I am working now.
I have worked in this field for ten years.
Have you ever worked abroad?
## مثال‌ها
I'm working on a new project. — روی پروژه‌ی جدیدی کار می‌کنم.
I have worked in this field for ten years. — ده سال در این حوزه کار کرده‌ام.
She has never worked abroad. — او هرگز خارج از کشور کار نکرده است.
## خطاهای رایج
سوم شخص را با have نگیرید: «She have worked» غلط — «She has worked».
با زمان مشخص گذشته حال کامل نیاید: «I have seen him yesterday» غلط — «I saw him yesterday».`,

  "b1.news.lesson-01": `## قاعده
خبر اغلب مجهول است چون فاعل مهم نیست: The law was announced yesterday. ساختار مجهول = be + قسمت سوم. برای نقل خبر با say/report از گذشته استفاده کن: The minister said that…
## جدول
Active: People announced the law.
Passive: The law was announced (by people).
was / were + قسمت سوم
## مثال‌ها
The law was announced yesterday. — قانون دیروز اعلام شد.
The results will be published next week. — نتایج هفته‌ی بعد منتشر می‌شود.
The minister said that the plan had changed. — وزیر گفت که طرح تغییر کرده است.
## خطاهای رایج
در مجهول، فعل be را حذف نکنید: «The law announced yesterday» غلط — «was announced».
فاعل مجهول را فقط وقتی اضافه کنید که مهم است (by the minister).`,

  "b1.plans.lesson-01": `## قاعده
قصد از قبل‌پذیرفته‌شده با be going to و تصمیم لحظه‌ای یا پیش‌بینی با will می‌آید. شرطی نوع اول برای نتیجه‌ی واقعی است: If + زمان حال ساده، will + فعل لخت — بعد از if هرگز will نمی‌آید.
## جدول
I am going to study medicine. — قصد دارم پزشکی بخوانم.
It will rain tomorrow. — فردا باران می‌آید.
If it rains, I will stay home.
## مثال‌ها
I'm going to take a course next month. — ماه بعد می‌خواهم دوره‌ای بگذرانم.
If it rains, we will stay home. — اگر باران بیاید، خانه می‌مانیم.
The train will leave at five. — قطار ساعت پنج حرکت می‌کند.
## خطاهای رایج
در بخش شرط بعد از if از will استفاده نکنید: «If it will rain» غلط — «If it rains».
بعد از be، to فراموش نشود: «I am go» غلط — «I am going».`,

  "b1.experiences.lesson-01": `## قاعده
تجربه بدون زمان مشخص با حال کامل ساخته می‌شود: I have visited Turkey. پرسش با ever و پاسخ منفی با never. مدت با for (مدت زمان) و نقطه‌ی شروع با since می‌آید.
## جدول
Have you ever…? — Yes, I have. / No, I never have.
for two years / for a long time
since 2022 / since Monday
## مثال‌ها
I have never been to Japan. — هرگز به ژاپن نرفته‌ام.
She has worked here since 2020. — او از ۲۰۲۰ اینجا کار می‌کند.
We have lived in Tehran for ten years. — ده سال در تهران زندگی کرده‌ایم.
## خطاهای رایج
برای مدت از for و برای نقطه از since استفاده کنید: «for 2020» غلط — «since 2020».
با زمان مشخص گذشته (last week, yesterday) حال کامل نیاید.`,

  "b2.technology.lesson-01": `## قاعده
برای دو چیز more + صفت بلند و برای سه یا بیشتر the most می‌آید. امکان‌سنجی و توانایی با can (حال) و احتمال با could بیان می‌شود؛ هر دو با فعل لخت.
## جدول
more efficient / the most useful / less reliable
can + لخت / could + لخت
## مثال‌ها
This device is more efficient than the old one. — این دستگاه از آن قدیمی کارآمدتر است.
Solar power is the most promising source. — انرژی خورشیدی امیدوارکننده‌ترین منبع است.
It could reduce energy use. — می‌تواند مصرف انرژی را کم کند.
## خطاهای رایج
«more easier» غلط است — یک درجه‌بندی کافی است: «easier».
صفت کوتاه با er است: «more small» غلط، «smaller» درست.`,

  "b2.environment.lesson-01": `## قاعده
در خبر و گزارش از مجهول گزارشی استفاده کن: It is believed that…. برای انتقاد به گذشته و کاری که نشده، should have + قسمت سوم می‌آید: You should have recycled it.
## جدول
It is believed that… / It is reported that…
should have + قسمت سوم (انتقاد به گذشته)
shouldn't have + قسمت سوم (کار اشتباه گذشته)
## مثال‌ها
It is believed that the ice cap is melting. — باور بر این است که یخ‌پوش در حال ذوب است.
You should have recycled this bottle. — باید این بطری را بازیافت می‌کردی.
Plastic should be banned. — پلاستیک باید ممنوع شود.
## خطاهای رایج
بعد از should have قسمت اول فعل نیاید: «should have went» غلط — «should have gone».
در مجهول فاعل نامشخص را با by someone اضافه نکنید.`,

  "b2.interview.lesson-01": `## قاعده
نقل‌قول غیرمستقیم با He said that… ساخته می‌شود و معمولاً زمان یک قدم عقب می‌کشد: «I am busy» → He said he was busy. تجربه را با ساختار STAR تعریف کن: Situation، Task، Action، Result.
## جدول
«I will call you» → He said he would call.
«I work here» → She said she worked there.
## مثال‌ها
He said that the position was still open. — گفت که موقعیت هنوز باز است.
She said she had worked in a team of five. — گفت در تیم پنج‌نفره کار کرده بود.
I described the result and what I had learned. — نتیجه و آنچه آموخته بودم را توضیح دادم.
## خطاهای رایج
زمان نقل‌قول را عقب بکشید: «He said he is busy» غلط — «he was busy».
ضمیر را متناسب تغییر دهید: «I» در نقل‌قول به he/she تبدیل می‌شود.`,

  "b2.media.lesson-01": `## قاعده
used to برای عادت یا وضعیت گذشته است که دیگر ادامه ندارد. would فقط برای تکرارهای عادی گذشته به کار می‌رود، نه وضعیت. هر دو با فعل لخت می‌آیند.
## جدول
I used to watch TV every night. (عادتِ گذشته)
We would play after school. (تکرار گذشته)
He didn't use to smoke. / Did you use to…?
## مثال‌ها
I used to read newspapers every morning. — قبلاً هر صبح روزنامه می‌خواندم.
We would travel by train every summer. — هر تابستان با قطار سفر می‌کردیم.
There used to be a cinema here. — قبلاً اینجا سینما بود.
## خطاهای رایج
بعد از used to فعل ing نمی‌آید: «used to going» غلط — «used to go».
برای وضعیت ثابت گذشته از would استفاده نکنید: «I would live in Tehran» غلط است.`,

  "b2.problems.lesson-01": `## قاعده
شکایت رسمی و مودبانه با مجهول حال کامل بیان می‌شود: The item has not been delivered yet. برای «لازم است» از need + ing (فعال) یا need to be + قسمت سوم (مجهول) استفاده کن — هر دو یک معنا.
## جدول
The item has not been delivered yet.
The order hasn't been processed.
The parcel needs checking. = The parcel needs to be checked.
## مثال‌ها
I'd like to complain about the delay. — می‌خواهم درباره‌ی تأخیر شکایت کنم.
The item has not been delivered yet. — کالا هنوز تحویل داده نشده است.
This document needs checking. — این سند باید بررسی شود.
## خطاهای رایج
در مجهول حال کامل، be و قسمت سوم هر دو لازم‌اند: «has not deliver» غلط — «has not been delivered».
شکایت را با «You are bad!» شروع نکنید — ساختار رسمی مودبانه‌تر است.`,

  "b2.research.lesson-01": `## قاعده
در زبان علمی هیچ‌چیز را قطعی نگو مگر شواهد قطعی باشد. احتیاط زبانی (hedging) با may, might, could, tends to, appears to انجام می‌شود و بخشی از دقت علمی است.
## جدول
may / might / could + لخت
tends to + لخت
It appears that… / The data suggest that…
## مثال‌ها
The data may suggest a link between the two. — داده‌ها ممکن است نشان‌دهنده‌ی پیوندی میان این دو باشند.
This tends to indicate a change in behaviour. — این معمولاً بر تغییری در رفتار دلالت دارد.
The results appear to confirm the theory. — نتایج ظاهراً نظریه را تأیید می‌کنند.
## خطاهای رایج
«This proves that» فقط وقتی درست است که واقعاً اثبات شده باشد.
«Maybe» محاوره است — در نوشتار علمی از may استفاده کنید.`,

  "b1.money.lesson-01": `## قاعده
برای چیزی که در گذشته شروع شده و هنوز ادامه دارد، حال کامل + for (مدت) یا since (نقطه‌ی شروع) به کار می‌رود. فعل باید معنای ادامه بدهد: save, live, work.
## جدول
for two years / for a long time / since 2023 / since Monday
I have saved money for two years.
## مثال‌ها
I have saved money for two years. — دو سال است پس‌انداز می‌کنم.
She has worked here since 2021. — او از ۲۰۲۱ اینجا کار می‌کند.
We haven't paid the rent since March. — از مارس اجاره را نداده‌ایم.
## خطاهای رایج
فعل‌های یک‌باره (buy, start) را با for/since در حال کامل نیاورید: «I have bought it for two years» غلط.
since فقط با نقطه‌ی زمانی می‌آید، for با مدت.`,

  "b1.health-lifestyle.lesson-01": `## قاعده
توصیه با should یا ought to و منع با shouldn't داده می‌شود؛ ought to بعد از خودش to دارد ولی should ندارد. نتیجه‌ی واقعی آینده با شرطی نوع اول می‌آید: If you sleep well, you will feel better.
## جدول
You should exercise. / You ought to rest. / You shouldn't skip breakfast.
If + حال ساده, will + فعل لخت
## مثال‌ها
If you drink enough water, you will feel better. — اگر آب کافی بنوشی، بهتر می‌شوی.
You should walk for half an hour a day. — باید نیم ساعت در روز پیاده‌روی کنی.
You shouldn't skip breakfast. — نباید صبحانه را حذف کنی.
## خطاهای رایج
«should to exercise» غلط است — should بدون to و ought to با to.
بعد از if در شرطی نوع اول از will استفاده نکنید.`,

  "c1.academic.lesson-01": `## قاعده
نوشتن رسمی از مجهول بهره می‌برد تا تمرکز بر ادعا بماند نه نویسنده: It is argued that…. اتصال‌دهنده‌های منطقی (however, therefore, furthermore) رابطه‌ی جملات را نگه می‌دارند و متن را منسجم می‌کنند.
## جدول
It is argued that… / It should be noted that…
However (تضاد) / Therefore (نتیجه) / Furthermore (افزودن) / In contrast (مقایسه)
## مثال‌ها
It is argued that education reduces inequality. — استدلال بر این است که آموزش نابرابری را کاهش می‌دهد.
The sample was small; therefore, the results are limited. — نمونه کوچک بود؛ بنابراین نتایج محدودند.
Furthermore, no control group was used. — افزون بر این، گروه کنترلی به کار نرفت.
## خطاهای رایج
بعد از اتصال‌دهنده‌ی ابتدای جمله ویرگول بگذارید: «Therefore, …» نه «Therefore …».
هر جمله را با However شروع نکنید — تنوع اتصال‌دهنده‌ها لازم است.`,

  "c1.persuasion.lesson-01": `## قاعده
تأکید بلاغی با وارونه‌سازی (inversion) ساخته می‌شود: جای فعل کمکی و فاعل عوض می‌شود. بعد از عبارت منفی یا تأکیدی در ابتدای جمله (Not only, Never, Little, Had…) ساختار وارونه می‌آید و جمله رسمی‌تر و نیرومندتر می‌شود.
## جدول
Had I known… = If I had known…
Never have I seen… / Little did he know…
Not only did he disagree, but he also…
## مثال‌ها
Had I known, I would have acted differently. — اگر می‌دانستم، جور دیگری عمل می‌کردم.
Never have I witnessed such arrogance. — هرگز چنین غروری ندیده بودم.
Little did he know that everything would change. — اصلاً نمی‌دانست که همه‌چیز تغییر خواهد کرد.
## خطاهای رایج
زمان فعل را در وارونه عقب نبرید: «Had I knew» غلط — «Had I known».
وارونه فقط بعد از عبارت تأکیدی/منفی ابتدای جمله می‌آید، نه هر جمله‌ای.`,

  "c1.culture.lesson-01": `## قاعده
در روایت، پس‌زمینه‌ی رویداد با گذشته استمراری (was/were + ing) و رویداد اصلی با گذشته‌ی ساده می‌آید. در روایت، would مانند used to تکرار گذشته را نشان می‌دهد اما وضعیت نمی‌دهد.
## جدول
I was watching the film when the power went off.
When + گذشته ساده (وقفه) / was/were + ing (پس‌زمینه)
In those days, we would meet after school.
## مثال‌ها
I was watching the film when the power went off. — داشتم فیلم می‌دیدم که برق رفت.
She was reading when I called. — داشت کتاب می‌خواند که زنگ زدم.
In those days we would meet after school. — در آن روزها بعد از مدرسه هم را می‌دیدیم.
## خطاهای رایج
«I was know» غلط است — know فعل وضعیتی است و استمراری نمی‌شود: «I knew».
هر دو بخش را استمراری نکنید: «was watching when the power was going off» → «went off».`,

  "c1.phrasal-idioms.lesson-01": `## قاعده
فعل عبارتی = فعل + حرف اضافه که معنایی مستقل و غیرلفظی دارد: give up یعنی دست کشیدن، نه «دادن بالا». حرف اضافه معنی را عوض می‌کند، پس آن را جزء واژه حفظ کن.
## جدول
give up = دست کشیدن / give away = بخشیدن
look after = مراقبت کردن / look forward to = انتظار داشتن
turn on / turn off = روشن / خاموش کردن
## مثال‌ها
He gave up smoking last year. — سال پیش سیگار را کنار گذاشت.
I look forward to hearing from you. — مشتاق شنیدن خبر از شما هستم.
Please turn on the lights. — لطفاً چراغ‌ها را روشن کن.
## خطاهای رایج
معنی را از جمع کردن لغویه‌ها نسازید: give up ≠ «بده بالا».
بعد از look forward to فعل ing می‌آید: «look forward to hear» غلط است.`,

  "c1.register.lesson-01": `## قاعده
نوشتار رسمی فعل را اسم می‌کند (nominalisation): We decided → Our decision was…؛ The company failed → The failure of the company…. ادعاهای نوپا را نرم کن تا رسمی بماند: it could be argued، نه it is obvious.
## جدول
decide → decision / analyze → analysis / improve → improvement
it could be argued that… / it seems that…
## مثال‌ها
Our decision was to postpone the meeting. — تصمیم ما تعویق جلسه بود.
The failure of the plan was unexpected. — شکست طرح غیرمنتظره بود.
It could be argued that costs will rise. — می‌توان استدلال کرد که هزینه‌ها بالا می‌رود.
## خطاهای رایج
در متن رسمی از محاوره پرهیز کنید: «a lot of» → «a considerable amount of».
ادعای نوپا را قطعی نکنید: «obviously» و «clearly» را حذف یا نرم کنید.`,

  "c1.cohesion.lesson-01": `## قاعده
اتصال‌دهنده‌ها رابطه‌ی منطقی جملات‌اند: however تضاد، therefore نتیجه، moreover افزودن. برای ارجاع به‌جای تکرار اسم‌ها از this/that/the former/the latter استفاده کن تا متن منسجم بماند.
## جدول
however (تضاد) / therefore (نتیجه) / moreover (افزودن)
in addition (افزودن) / nevertheless (با این حال)
this / that / the former / the latter
## مثال‌ها
The plan was costly; however, it succeeded. — طرح پرهزینه بود؛ با این حال موفق شد.
Costs rose; therefore, prices followed. — هزینه‌ها بالا رفت؛ بنابراین قیمت‌ها هم بالا رفتند.
The latter approach is safer. — رویکرد دوم ایمن‌تر است.
## خطاهای رایج
but و however را با هم در یک جمله نیاورید — یکی کافی است.
ارجاع مبهم نکنید: «This is wrong» بدون مشخص کردن this به چه چیزی اشاره دارد.`,

  "c1.inference.lesson-01": `## قاعده
برای استنباط درباره‌ی گذشته از must have (قطعیت)، can't have (نفی قطعی) و might/may have (احتمال) با قسمت سوم استفاده کن. این ساختارها شواهد را نتیجه می‌گیرند، نه دیدن خود رویداد.
## جدول
must have + قسمت سوم (قطعیت)
can't have + قسمت سوم (نفی قطعی)
might / may have + قسمت سوم (احتمال)
## مثال‌ها
She must have forgotten the meeting. — حتماً جلسه را فراموش کرده است.
He can't have finished already. — نمی‌تواند هنوز تمام کرده باشد.
They might have missed the train. — شاید قطار را از دست داده باشند.
## خطاهای رایج
بعد از must have قسمت اول فعل نیاید: «must have went» غلط — «must have gone».
این ساختار مخصوص گذشته است — برای استنباط از حال به کار نرود.`,

  "c2.nuance.lesson-01": `## قاعده
هر هم‌معنایی بار معنایی خودش را دارد: tiny کوچک‌تر و حساس‌تر از small است؛ rather یعنی «تا حدی» و hardly یعنی «به‌ندرت». انتخاب واژه یعنی انتخاب دقت — بافت تعیین می‌کند کدام درست است.
## جدول
tiny < small < large
rather (تا حدی) / hardly (به‌ندرت) / quite (کاملاً)
begin (رسمی) / start (عامیانه‌تر)
## مثال‌ها
A tiny mistake changed the result. — یک خطای کوچک نتیجه را عوض کرد.
He hardly spoke at all. — اصلاً حرفی نزد.
The results were rather inconclusive. — نتایج تا حدی قطعی نبودند.
## خطاهای رایج
hardly را با «با سختی» اشتباه نگیرید: «I worked hardly» غلط — «I worked hard».
quite در انگلیسی بریتانیایی «کاملاً» و در آمریکایی گاه «تا حدی» است — بافت را بسنجید.`,

  "c2.rhetoric.lesson-01": `## قاعده
کنایه با وارونه‌سازی ساخته می‌شود: Little did he know… یعنی اصلاً نمی‌دانست. تکرار هدفمند واژه‌ها (repetition) ضرباهنگ سخن را می‌سازد و پیام را به‌یادماندنی می‌کند.
## جدول
Little did he know…
Never had she seen…
Not until … did …
## مثال‌ها
Little did he know that his decision would change everything. — اصلاً نمی‌دانست که تصمیمش همه‌چیز را عوض خواهد کرد.
It was a long night, a long wait, and a longer silence. — شبی دراز بود، انتظاری دراز و سکوتی درازتر.
Never had the room been so quiet. — هرگز اتاق این‌قدر ساکت نبود.
## خطاهای رایج
کنایه را با لحن کاملاً مستقیم ننویسید تا خواننده آن را تحسین واقعی نپندارد.
در وارونه زمان فعل را حفظ کنید: «Little did he knew» غلط است.`,

  "c2.science.lesson-01": `## قاعده
زبان علمی مجهول و دقیق است چون روش مهم‌تر از پژوهشگر است: The phenomenon was observed…. تعریف مفاهیم با X refers to… یا X is defined as… ارائه می‌شود و واحد اندازه‌گیری همیشه ذکر می‌شود.
## جدول
The sample was analyzed.
X refers to… / X is defined as…
… is measured in / is expressed as…
## مثال‌ها
The phenomenon was observed over six months. — پدیده شش ماهه مشاهده شد.
Temperature is measured in degrees Celsius. — دما بر حسب درجه سانتی‌گراد اندازه‌گیری می‌شود.
This term refers to a gradual change. — این اصطلاح به تغییر تدریجی اشاره دارد.
## خطاهای رایج
تعریف را بدون فعل نگذارید: «X, a gradual change» جمله نیست — «X refers to a gradual change».
در مجهول علمی فاعل نامشخص را با by people اضافه نکنید.`,

  "c2.literature.lesson-01": `## قاعده
معنا از بافت می‌آید نه لفظ مستقیم. tone (لحن راوی) با mood (حال‌وهوای متن) فرق دارد و irony یعنی گفتن خلاف آنچه منظور است. همیشه بپرس گوینده چه می‌داند که خواننده نمی‌داند.
## جدول
tone = لحن راوی / mood = حال‌وهوای متن
irony = کنایه / foreshadowing = پیش‌درآمد
figurative = تمثیلی/مجازی
## مثال‌ها
The narrator's tone is calm, but the mood is tense. — لحن راوی آرام است اما حال‌وهوای متن پرتنش.
Saying «what a lovely day» in the rain is irony. — گفتن «چه روز قشنگی» زیر باران کنایه است.
The ending foreshadows a sequel. — پایان‌بندی ادامه‌ی داستان را پیش‌بینی می‌کند.
## خطاهای رایج
tone و mood را یکی نگیرید — یکی نگاه راوی است و دیگری فضای متن.
figurative را «احساسی» ترجمه نکنید — «تمثیلی/مجازی» است.`,

  "c2.argumentation.lesson-01": `## قاعده
استدلال قوی اول امتیاز طرف مقابل را می‌پذیرد (concede) و بعد پاسخ می‌دهد: Granted that X, nevertheless Y. نادیده‌گرفتن مخالف، خودش مغالطه است (straw man).
## جدول
Granted that…, nevertheless…
Admittedly, … / Although…, …
while it is true that…
## مثال‌ها
Granted that costs are high, nevertheless the plan is viable. — درست است که هزینه‌ها بالاست، با این حال طرح عملی است.
Admittedly, the sample was small. — البته نمونه کوچک بود.
Although both views have merit, the second is stronger. — هرچند هر دو دیدگاه ارزش دارند، دومی قوی‌تر است.
## خطاهای رایج
پاسخ را قبل از پذیرش نیاورید — ترتیب concede سپس rebuttal است.
اتصال‌دهنده را دوتا نکنید: «Although…, but…» غلط — یکی کافی است.`,

  "c2.collocation.lesson-01": `## قاعده
واژه‌ها در سطح بالا جفت‌جفت حفظ می‌شوند: make a decision نه do a decision، take responsibility نه get responsibility. ترکیب غلط حتی با گرامر درست، غیرطبیعی و نشانه‌ی سطح پایین است.
## جدول
make a decision / take a break / pay attention
break the rules / heavy rain / strong tea
## مثال‌ها
We made a decision yesterday. — دیروز تصمیمی گرفتیم.
Pay attention to the details. — به جزئیات توجه کن.
The traffic was heavy this morning. — صبح ترافیک سنگین بود.
## خطاهای رایج
«do a decision» و «make homework» غلط‌اند — make/do را بر اساس هم‌نشینی حفظ کنید.
صفت را از زبان مادری وام نگیرید: «strong rain» غلط — «heavy rain».`,

  "c2.irony.lesson-01": `## قاعده
گاهی کم‌گویی از پرگویی قوی‌تر است. litotes یعنی نفی ضد برای تأکید: not uncommon = رایج. کنایه‌ی مودبانه مانند «Not bad for a beginner» تحسین را نرم و چاشنی‌دار می‌کند.
## جدول
not uncommon = رایج / not bad = بد نیست
not least = مهم‌تر از همه
understatement = کم‌گویی
## مثال‌ها
Not bad for a first attempt! — برای اولین بار بد نیست!
The solution is not unlike the original. — راه‌حل بی‌شباهت به نسخه‌ی اصلی نیست.
He is no fool. — او احمق نیست (یعنی عاقل است).
## خطاهای رایج
«not uncommon» را با «not common» اشتباه نگیرید — کاملاً برعکس‌اند.
لحن کنایه را در موقعیت رسمی بدون اطمینان از فهم طرف به کار نبرید.`,
};

const raw = fs.readFileSync(PATH, "utf8");
const roundTrip = JSON.stringify(JSON.parse(raw), null, 2);
if (roundTrip !== raw && roundTrip + "\n" !== raw) {
  throw new Error("lessons.json is not stable under JSON.stringify(data, null, 2) — refusing to rewrite");
}
const newline = raw.endsWith("\n") ? "\n" : "";

const data = JSON.parse(raw);
const ids = new Set(data.lessons.map((l) => l.id));
for (const id of Object.keys(tips)) {
  if (!ids.has(id)) throw new Error(`unknown lesson id: ${id}`);
}
const missing = data.lessons.filter((l) => !(l.id in tips)).map((l) => l.id);
if (missing.length) throw new Error(`missing tips for: ${missing.join(", ")}`);

for (const lesson of data.lessons) lesson.grammarTipFa = tips[lesson.id];
fs.writeFileSync(PATH, JSON.stringify(data, null, 2) + newline);
console.log(`expanded grammarTipFa for ${data.lessons.length} lessons (contentVersion stays ${data.contentVersion})`);
