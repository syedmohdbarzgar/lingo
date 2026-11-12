#!/usr/bin/env node
/**
 * v76: gives every A1 and A2 lesson the level-2 choice item its recognition axis was missing.
 *
 * The A1/A2 difficulty pass (v72) judged both levels item by item and found the same gap
 * v69 left in C1 and v71 in C2 — but at the bottom of the scale it is the widest. 41 lessons
 * (20 A1, 21 A2) shipped only «What does X mean?» choices, every one of them authored 1, so
 * `AdaptiveExerciseSelector` could neither come down to the easy end (there was nothing below)
 * nor step up (nothing above): the drill was flat however shaky or consolidated the meter was.
 *
 * Each item added here is one exercise at difficulty 2, in the shape v72 defined for these two
 * levels: **a sentence the learner has to read, where the context decides the word** — not a
 * gloss and not a structure that gives itself away. All four options are the lesson's own words
 * of the same part of speech, so the sentence is the only thing that picks one out
 * (`The ______ says "meow"` → cat; `I am crying because I am cutting an ______` → onion;
 * `Our ______ trains the team twice a week` → coach). Skill stays VOCABULARY: what the item
 * tests is the word, used in a sentence rather than glossed.
 *
 * Run once, then move to scripts/archive/ — never re-run a content batch.
 */
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const contentDir = join(dirname(dirname(fileURLToPath(import.meta.url))), 'app', 'src', 'main', 'assets', 'content');
const exercisesPath = join(contentDir, 'exercises.json');

/** id suffix is the lesson's own topic prefix; `order` is appended after its last item. */
const items = [
  // ── A1: 20 lessons whose only choices were word meanings ────────────────────────────────
  {
    lessonId: 'a1.age-birthday.lesson-01',
    question: 'My sister was ______ in 2010, so she is sixteen today.',
    questionFa: 'خواهرم در سال ۲۰۱۰ به دنیا آمد، پس امروز شانزده ساله است.',
    options: ['born', 'birthday', 'party', 'age'],
    explanationFa: 'born یعنی «به دنیا آمدن»؛ birthday روز تولد، party مهمانی و age سن است. جمله از سالِ تولد می‌گوید، پس only born در جای خالی می‌نشیند.',
  },
  {
    lessonId: 'a1.animals.lesson-01',
    question: 'The ______ says "meow" and sleeps on my bed every night.',
    questionFa: 'این حیوان «میائو» می‌گوید و هر شب روی تخت من می‌خوابد.',
    options: ['cat', 'dog', 'cow', 'fish'],
    explanationFa: 'صدای «meow» مخصوص cat است؛ dog پارس می‌کند، cow صداهای دیگری دارد و fish صدا ندارد.',
  },
  {
    lessonId: 'a1.belongings.lesson-01',
    question: 'It is raining, so take your ______ before you go out.',
    questionFa: 'باران می‌بارد، پس قبل از بیرون رفتن چترت را بردار.',
    options: ['umbrella', 'wallet', 'watch', 'key'],
    explanationFa: 'umbrella یعنی «چتر» و پاسخِ باران است؛ wallet کیف پول، watch ساعت و key کلید است.',
  },
  {
    lessonId: 'a1.body.lesson-01',
    question: 'You hear music with your ______.',
    questionFa: 'تو موسیقی را با گوش می‌شنوی.',
    options: ['ear', 'eye', 'hand', 'foot'],
    explanationFa: 'ear یعنی «گوش» و اندامِ شنیدن است؛ eye می‌بیند، hand لمس می‌کند و foot راه می‌رود.',
  },
  {
    lessonId: 'a1.cafe-restaurant.lesson-01',
    question: 'The meal is finished. Could you bring the ______, please? I want to pay.',
    questionFa: 'غذا تمام شد. می‌شود صورتحساب را بیاورید؟ می‌خواهم پول بدهم.',
    options: ['bill', 'menu', 'coffee', 'sandwich'],
    explanationFa: 'bill یعنی «صورتحساب» و برای پرداخت است؛ menu فهرست غذا، coffee و sandwich خوردنی‌اند.',
  },
  {
    lessonId: 'a1.classroom.lesson-01',
    question: 'The teacher writes the new words on the ______ with a marker.',
    questionFa: 'معلم واژه‌های تازه را با ماژیک روی تخته می‌نویسد.',
    options: ['board', 'desk', 'pencil', 'bag'],
    explanationFa: 'board یعنی «تخته» و جای نوشتن با ماژیک است؛ desk میز، pencil مداد و bag کیف است.',
  },
  {
    lessonId: 'a1.clothes.lesson-01',
    question: 'It is very cold today, so wear a warm ______.',
    questionFa: 'امروز خیلی سرد است، پس یک کاپشن گرم بپوش.',
    options: ['coat', 'hat', 'shoes', 'shirt'],
    explanationFa: 'coat یعنی «کاپشن/پالتو» و لباسِ گرماست؛ hat کلاه، shoes کفش و shirt پیراهن است.',
  },
  {
    lessonId: 'a1.colors.lesson-01',
    question: 'The flag of Iran is green, white and ______.',
    questionFa: 'پرچم ایران سبز، سفید و سرخ است.',
    options: ['red', 'blue', 'black', 'color'],
    explanationFa: 'red یعنی «سرخ» و رنگ سوم پرچم ایران است؛ blue آبی، black سیاه و color خودِ واژهٔ «رنگ» است نه یک رنگ.',
  },
  {
    lessonId: 'a1.countries.lesson-01',
    question: 'Tehran is the ______ of Iran, but it is not the biggest city in the world.',
    questionFa: 'تهران پایتخت ایران است، اما بزرگ‌ترین شهر جهان نیست.',
    options: ['capital', 'country', 'language', 'map'],
    explanationFa: 'capital یعنی «پایتخت»؛ country کشور، language زبان و map نقشه است.',
  },
  {
    lessonId: 'a1.describing-people.lesson-01',
    question: 'My neighbour is very ______: she helps everyone in the building.',
    questionFa: 'همسایهٔ من خیلی مهربان است: به همهٔ اهالی ساختمان کمک می‌کند.',
    options: ['kind', 'tall', 'short', 'young'],
    explanationFa: 'kind یعنی «مهربان» و از کمک کردن فهمیده می‌شود؛ tall قدبلند، short کوتاه و young جوان است.',
  },
  {
    lessonId: 'a1.directions.lesson-01',
    question: 'Go ______ ahead for two hundred metres and you will see the bank.',
    questionFa: 'دویست متر مستقیم برو تا بانک را ببینی.',
    options: ['straight', 'left', 'right', 'far'],
    explanationFa: 'straight ahead یعنی «مستقیم جلو»؛ left چپ و right راست می‌پیچد و far فقط فاصله را می‌گوید.',
  },
  {
    lessonId: 'a1.fruits-vegetables.lesson-01',
    question: 'I am crying because I am cutting an ______.',
    questionFa: 'دارم گریه می‌کنم، چون پیاز می‌برم.',
    options: ['onion', 'apple', 'banana', 'potato'],
    explanationFa: 'onion یعنی «پیاز» و اشک را درمی‌آورد؛ apple سیب، banana موز و potato سیب‌زمینی است.',
  },
  {
    lessonId: 'a1.hobbies.lesson-01',
    question: 'My ______ is photography: I take photos every weekend.',
    questionFa: 'سرگرمی من عکاسی است: هر آخر هفته عکس می‌گیرم.',
    options: ['hobby', 'game', 'film', 'sport'],
    explanationFa: 'hobby یعنی «سرگرمی» و عکاسی نمونهٔ آن است؛ game بازی، film فیلم و sport ورزش است.',
  },
  {
    lessonId: 'a1.home-rooms.lesson-01',
    question: 'We cook and wash the dishes in the ______.',
    questionFa: 'ما در آشپزخانه غذا می‌پزیم و ظرف می‌شوییم.',
    options: ['kitchen', 'bedroom', 'bathroom', 'garden'],
    explanationFa: 'kitchen یعنی «آشپزخانه»؛ bedroom اتاق خواب، bathroom حمام و garden باغ است.',
  },
  {
    lessonId: 'a1.house-objects.lesson-01',
    question: 'Please open the ______; the room is very hot.',
    questionFa: 'لطفاً پنجره را باز کن؛ اتاق خیلی گرم است.',
    options: ['window', 'door', 'table', 'lamp'],
    explanationFa: 'window یعنی «پنجره» و باز کردنش اتاق را خنک می‌کند؛ door در است (باز کردنش هوای تازه نمی‌آورد) و table و lamp اصلاً باز نمی‌شوند.',
  },
  {
    lessonId: 'a1.jobs.lesson-01',
    question: 'He makes food in a restaurant all day. He is a ______.',
    questionFa: 'او تمام روز در رستوران غذا درست می‌کند. او آشپز است.',
    options: ['cook', 'doctor', 'driver', 'farmer'],
    explanationFa: 'cook یعنی «آشپز»؛ doctor پزشک، driver راننده و farmer کشاورز است.',
  },
  {
    lessonId: 'a1.numbers.lesson-01',
    question: 'Ten and ten is ______.',
    questionFa: 'ده به‌اضافهٔ ده می‌شود بیست.',
    options: ['twenty', 'twelve', 'two', 'hundred'],
    explanationFa: 'ده و ده می‌شود twenty یعنی بیست؛ twelve دوازده است و two و hundred عدد دیگری‌اند.',
  },
  {
    lessonId: 'a1.places-town.lesson-01',
    question: 'I need some money, so I am going to the ______.',
    questionFa: 'پول لازم دارم، پس به بانک می‌روم.',
    options: ['bank', 'shop', 'park', 'school'],
    explanationFa: 'bank یعنی «بانک» و جای پول است؛ shop فروشگاه، park پارک و school مدرسه است.',
  },
  {
    lessonId: 'a1.time.lesson-01',
    question: 'The film starts at eight and it is eight now, so we are not early. We are ______.',
    questionFa: 'فیلم ساعت هشت شروع می‌شود و الان ساعت هشت است، پس ما زود نرسیده‌ایم؛ دیر کرده‌ایم.',
    options: ['late', 'early', 'hour', 'minute'],
    explanationFa: 'late یعنی «دیر»؛ جمله خودش early را رد می‌کند، و hour و minute واحد زمان‌اند نه حالت رسیدن.',
  },
  {
    lessonId: 'a1.transport.lesson-01',
    question: 'You buy a ______ before you get on the bus.',
    questionFa: 'قبل از سوار شدن به اتوبوس باید بلیت بخری.',
    options: ['ticket', 'taxi', 'train', 'station'],
    explanationFa: 'ticket یعنی «بلیت» و چیزی است که می‌خری؛ taxi و train وسیله‌اند و station ایستگاه است.',
  },
  // ── A2: 21 lessons in the same state ───────────────────────────────────────────────────
  {
    lessonId: 'a2.asking-help.lesson-01',
    question: 'Could you ______ me your pen for a minute? Mine is empty.',
    questionFa: 'می‌شود یک دقیقه خودکارت را به من قرض بدهی؟ خودکار من تمام شده.',
    options: ['lend', 'borrow', 'allow', 'mind'],
    explanationFa: 'lend یعنی «قرض دادن» (به کسی) و borrow یعنی «قرض گرفتن»؛ چون خودکار مالِ اوست و به من می‌رسد، lend درست است.',
  },
  {
    lessonId: 'a2.buying-clothes.lesson-01',
    question: 'These shoes are too ______: my feet hurt. Do you have a bigger size?',
    questionFa: 'این کفش‌ها خیلی تنگ‌اند: پایم درد می‌گیرد. سایز بزرگ‌تر دارید؟',
    options: ['tight', 'loose', 'similar', 'fashionable'],
    explanationFa: 'tight یعنی «تنگ» و درد پا را توضیح می‌دهد؛ loose گشاد است و سایز بزرگ‌تر نمی‌خواهد.',
  },
  {
    lessonId: 'a2.celebrations.lesson-01',
    question: 'We ______ our anniversary every year with a small family dinner.',
    questionFa: 'ما هر سال سالگرد ازدواجمان را با یک شام کوچک خانوادگی جشن می‌گیریم.',
    options: ['celebrate', 'gift', 'guest', 'candle'],
    explanationFa: 'celebrate یعنی «جشن گرفتن» و فعل جمله است؛ gift هدیه، guest مهمان و candle شمع است.',
  },
  {
    lessonId: 'a2.childhood.lesson-01',
    question: 'I ______ in a small village near the mountains, so I know every street there.',
    questionFa: 'من در روستایی کوچک نزدیک کوه‌ها بزرگ شدم، پس همهٔ کوچه‌هایش را می‌شناسم.',
    options: ['grew up', 'primary school', 'memory', 'playground'],
    explanationFa: 'grew up یعنی «بزرگ شدم» و تنها گزینهٔ فعلی است؛ primary school دبستان، memory خاطره و playground زمین بازی است.',
  },
  {
    lessonId: 'a2.comparing.lesson-01',
    question: 'This chair is more ______ than the old one: I can sit on it all day.',
    questionFa: 'این صندلی از قبلی راحت‌تر است: می‌توانم تمام روز رویش بنشینم.',
    options: ['comfortable', 'popular', 'similar', 'different'],
    explanationFa: 'comfortable یعنی «راحت» و نشستن تمام روز دلیلش است؛ popular محبوب، similar مشابه و different متفاوت است.',
  },
  {
    lessonId: 'a2.future-plans.lesson-01',
    question: 'The manager is away on Monday, so we have to ______ the meeting to Tuesday.',
    questionFa: 'مدیر دوشنبه نیست، پس باید جلسه را به سه‌شنبه موکول کنیم.',
    options: ['postpone', 'arrange', 'decide', 'hope'],
    explanationFa: 'postpone یعنی «به زمانی دیگر موکول کردن»؛ arrange چیدن، decide تصمیم گرفتن و hope امیدوار بودن است — همه دستوری‌اند، اما فقط postpone جابه‌جایی تاریخ را می‌گوید.',
  },
  {
    lessonId: 'a2.holiday-plans.lesson-01',
    question: 'We are going ______ this summer: two weeks in Turkey.',
    questionFa: 'تابستان امسال به خارج می‌رویم: دو هفته در ترکیه.',
    options: ['abroad', 'sightseeing', 'trip', 'stay'],
    explanationFa: 'abroad یعنی «خارج از کشور» و ترکیه نمونهٔ آن است؛ sightseeing گشت‌وگذار، trip سفر (اسم) و stay ماندن است.',
  },
  {
    lessonId: 'a2.hotel-booking.lesson-01',
    question: 'Sorry, no rooms are ______ for that weekend: we are fully booked.',
    questionFa: 'متأسفانه برای آن آخر هفته اتاق خالی نداریم؛ همه رزرو شده‌اند.',
    options: ['available', 'reserve', 'deposit', 'per night'],
    explanationFa: 'available یعنی «موجود/خالی» و با no rooms جملهٔ منفی می‌سازد؛ reserve رزرو کردن و deposit پیش‌پرداخت است.',
  },
  {
    lessonId: 'a2.hotel-checkin.lesson-01',
    question: 'Your room is on the fourth floor. You need this ______ to open the door.',
    questionFa: 'اتاق شما در طبقهٔ چهارم است. برای باز کردن در به این کارت‌کلید نیاز دارید.',
    options: ['key card', 'reception', 'lift', 'breakfast'],
    explanationFa: 'key card یعنی «کارت‌کلید» و در را باز می‌کند؛ reception پذیرش، lift آسانسور و breakfast صبحانه است.',
  },
  {
    lessonId: 'a2.house-problems.lesson-01',
    question: 'The tap is broken and water is everywhere. There is a ______ in the kitchen.',
    questionFa: 'شیر آب خراب است و همه‌جا آب افتاده. در آشپزخانه نشتی داریم.',
    options: ['leak', 'plumber', 'repair', 'switch'],
    explanationFa: 'leak یعنی «نشتی» و آبِ همه‌جا نتیجهٔ آن است؛ plumber لوله‌کش است و کسی را صدا می‌زنیم، repair تعمیر و switch کلید برق است.',
  },
  {
    lessonId: 'a2.invitations.lesson-01',
    question: 'I cannot come on Friday, but I would love to ______ you on Saturday.',
    questionFa: 'جمعه نمی‌توانم بیایم، اما دوست دارم شنبه به شما بپیوندم.',
    options: ['join', 'invite', 'accept', 'refuse'],
    explanationFa: 'join یعنی «به کسی پیوستن»؛ invite دعوت کردن، accept پذیرفتن و refuse رد کردن است — جمله یک دعوت را می‌پذیرد، پس join.',
  },
  {
    lessonId: 'a2.opinions.lesson-01',
    question: 'I ______ tea to coffee, especially in the morning.',
    questionFa: 'من چای را به قهوه ترجیح می‌دهم، مخصوصاً صبح‌ها.',
    options: ['prefer', 'agree', 'enjoy', 'disagree'],
    explanationFa: 'prefer … to … یعنی «ترجیح دادن» و با to می‌آید؛ enjoy به to نیاز ندارد (enjoy tea, not enjoy tea to coffee).',
  },
  {
    lessonId: 'a2.pharmacy.lesson-01',
    question: 'The doctor wrote a ______ for my medicine, and the pharmacist read it.',
    questionFa: 'پزشک برای داروی من نسخه نوشت و داروساز آن را خواند.',
    options: ['prescription', 'dose', 'pills', 'pharmacist'],
    explanationFa: 'prescription یعنی «نسخه» و چیزی است که پزشک می‌نویسد؛ dose مقدار مصرف، pills قرص‌ها و pharmacist خودِ داروساز است.',
  },
  {
    lessonId: 'a2.phone.lesson-01',
    question: 'I am sorry, the ______ is busy. Please hold on a moment.',
    questionFa: 'متأسفم، خط اشغال است. لطفاً یک لحظه منتظر بمانید.',
    options: ['line', 'call', 'message', 'voicemail'],
    explanationFa: 'the line is busy یعنی «خط اشغال است»؛ message پیام و voicemail پیام صوتی است، و call را در این عبارت به‌کار نمی‌برند.',
  },
  {
    lessonId: 'a2.renting.lesson-01',
    question: 'The flat is ______, so we do not need to buy any furniture.',
    questionFa: 'آپارتمان مبله است، پس لازم نیست هیچ مبلمانی بخریم.',
    options: ['furnished', 'lease', 'landlord', 'utility'],
    explanationFa: 'furnished یعنی «مبله» و نبودِ نیاز به خرید مبلمان نتیجهٔ آن است؛ lease قرارداد اجاره، landlord صاحب‌خانه و utility هزینهٔ آب و برق است.',
  },
  {
    lessonId: 'a2.returns.lesson-01',
    question: 'The zip of this bag is broken. I do not want a repair; I want a ______.',
    questionFa: 'زیپ این کیف خراب است. تعمیر نمی‌خواهم؛ پولم را می‌خواهم.',
    options: ['refund', 'receipt', 'guarantee', 'faulty'],
    explanationFa: 'refund یعنی «بازپرداخت پول»؛ receipt رسید، guarantee ضمانت و faulty معیوب است — جمله تعمیر را رد می‌کند، پس پول برمی‌گردد.',
  },
  {
    lessonId: 'a2.sports.lesson-01',
    question: 'Our ______ trains the team twice a week and chooses the players for the match.',
    questionFa: 'مربی ما هفته‌ای دو بار تیم را تمرین می‌دهد و بازیکنان بازی را انتخاب می‌کند.',
    options: ['coach', 'team', 'match', 'gym'],
    explanationFa: 'coach یعنی «مربی» و کسی است که تیم را تمرین می‌دهد؛ team تیم، match مسابقه و gym باشگاه است.',
  },
  {
    lessonId: 'a2.symptoms.lesson-01',
    question: 'I have a ______ and a fever, and it hurts when I swallow.',
    questionFa: 'گلویم درد می‌کند و تب دارم، و موقع قورت دادن درد می‌گیرد.',
    options: ['sore throat', 'headache', 'cough', 'dizzy'],
    explanationFa: 'sore throat یعنی «گلودرد» و قورت دادن را به‌درد می‌آورد؛ headache سردرد است، cough سرفه و dizzy سرگیجه.',
  },
  {
    lessonId: 'a2.train-bus.lesson-01',
    question: 'The train is forty minutes late, so we have a long ______.',
    questionFa: 'قطار چهل دقیقه تأخیر دارد، پس تأخیر طولانی‌ای داریم.',
    options: ['delay', 'platform', 'timetable', 'passenger'],
    explanationFa: 'delay یعنی «تأخیر» و چهل دقیقه توضیح آن است؛ platform سکو، timetable جدول زمانی و passenger مسافر است.',
  },
  {
    lessonId: 'a2.travel-info.lesson-01',
    question: 'Is there a bank ______? I need some cash before the tour starts.',
    questionFa: 'بانکی نزدیک این‌جا هست؟ قبل از شروع تور پول نقد لازم دارم.',
    options: ['nearby', 'route', 'journey', 'information'],
    explanationFa: 'nearby یعنی «نزدیک» و پرسشِ مکان را کامل می‌کند؛ route مسیر، journey سفر و information اطلاعات است.',
  },
  {
    lessonId: 'a2.work.lesson-01',
    question: 'I have a ______ with the manager at ten, so I must prepare the numbers now.',
    questionFa: 'ساعت ده با مدیر جلسه دارم، پس باید همین حالا اعداد را آماده کنم.',
    options: ['meeting', 'deadline', 'task', 'colleague'],
    explanationFa: 'meeting یعنی «جلسه» و با ساعت و مدیر می‌آید؛ deadline مهلت، task وظیفه و colleague همکار است.',
  },
];

const bundle = JSON.parse(readFileSync(exercisesPath, 'utf8'));
const added = [];

for (const item of items) {
  const own = bundle.exercises.filter((e) => e.lessonId === item.lessonId);
  if (own.length === 0) throw new Error(`no exercises for ${item.lessonId}`);
  const maxOrder = Math.max(...own.map((e) => e.order ?? 0));
  const topic = item.lessonId.replace('.lesson-01', '');
  // The id keeps the `ex.NN` shape of the lesson's other drills: the highest existing
  // drill number plus one, not the next `order` (orders also count dialogue turns).
  const drillNumbers = own
    .map((e) => /^ex\.(\d+)$/.exec(e.id.slice(topic.length + 1))?.[1])
    .filter(Boolean)
    .map((n) => Number(n));
  const suffix = String(Math.max(0, ...drillNumbers) + 1).padStart(2, '0');
  const id = `${topic}.ex.${suffix}`;
  if (bundle.exercises.some((e) => e.id === id)) throw new Error(`id already taken: ${id}`);

  const exercise = {
    id,
    lessonId: item.lessonId,
    type: 'multiple_choice',
    order: maxOrder + 1,
    difficulty: 2,
    question: item.question,
    questionFa: item.questionFa,
    options: item.options,
    correctIndex: 0,
    skill: 'VOCABULARY',
    explanationFa: item.explanationFa,
  };

  // After the lesson's last item, so the authored order still reads 01..n.
  const lastIndex = bundle.exercises.findLastIndex((e) => e.lessonId === item.lessonId);
  bundle.exercises.splice(lastIndex + 1, 0, exercise);
  added.push(id);
}

writeFileSync(exercisesPath, `${JSON.stringify(bundle, null, 2)}\n`);
console.log(`added ${added.length} level-2 choice items`);
for (const id of added) console.log(`  ${id}`);
