# Curriculum — the 180-topic plan

> The product curriculum: **30 topics per CEFR band, 180 in total**, A1 → C2. This file is the
> authoring plan and the honest status of it; the shipped lessons themselves live in
> `app/src/main/assets/content/` and are gated by `validateContent`
> ([`content-pipeline.md`](content-pipeline.md)).
>
> A topic ships only as a **complete** lesson: a sectioned teaching page, six vocabulary words with
> two bilingual examples each, word recall, listening, translation and free-production items — every
> one of them with its Persian explanation — plus its knowledge node and pronunciation coverage.
> A stub lesson is worse than a missing one: it is a promise the app cannot keep.

**Status legend:** `✅ <lessonId>` ships today · `—` queued for a later batch.

Current bundle: **181 lessons** (A1 = 37, A2 = 32, B1 = 30, B2 = 32, C1 = 31, C2 = 19). **A1, A2, B1,
B2 and C1 are complete**: all 30 syllabus topics ship at those five levels, plus the seven A1 grammar supplements
(spelling, articles, plurals, pronouns, `can`, imperatives, question words) and the A2 past-simple
lesson. The six topics marked *new* shipped in v27; the sixteen marked *v28* completed A1. A2 started
in v29 with its first six syllabus topics (1 and 3–7), all travel and staying; v32 added topics 8, 9,
10, 12, 13 and 15–19 — clothes, returns, payments, symptoms, the pharmacy, childhood, celebrations,
invitations, phones and emails; and v34 closed the level with 21–27, 29 and 30 — renting, household
problems, future plans, comparing, opinions, asking for help, sports, work and everyday problems.
**B1 opened in v36** with topics 1, 3, 4 and 5 — personal background, job hunting, university life and
learning new skills — **v37 added 6–9** (travel problems, cultural differences, friendship,
feelings and emotions), **v38 added 10, 11, 13 and 15** (resolving conflicts, agreeing and
disagreeing, storytelling, technology in daily life), **v39 added 16, 17, 19 and 20** (social
media, environment and pollution, eating habits, time management) and **v40 added 22–25** (banking,
complaints and refunds, formal emails, a short presentation); **v41 closed the level** with 26, 27,
29 and 30 (meetings, charts and data, future goals, social issues), so all thirty B1 topics ship.
**B2 opened in v42** with topics 1–4 — workplace negotiations, project management, leadership and
teamwork, entrepreneurship — beside the six v5-era lessons, and **v43 added 6, 7, 9 and 10** (finance
and investment, the global economy, artificial intelligence, digital privacy and security) and
**v44 added 12–15** (renewable energy, education systems, mental health, media literacy) and
**v45 added 16, 18, 19 and 20** (persuasion and advertising, crisis management, professional
reports, professional presentations), **v46 added 21–24** (debating and discussion, reviewing
films and books, common idioms, common phrasal verbs), **v47 added 25–28**
(intercultural communication, workplace conflict resolution, strategy and evaluation, professional
ethics) and **v54 closed the level** with 29 and 30 (career decisions, evaluating different
viewpoints), so all thirty B2 topics ship; `b2.interview`/`b2.research` ship outside the syllabus
list, which is why B2 carries 32 lessons. **C1 opened in v48** with topics 1–4 — executive
communication, advanced negotiation, public policy and international relations — beside its seven
v5-era lessons, and **v49 added 5, 6, 7 and 9** (economic analysis, technology ethics, philosophy
and reasoning, cognitive biases), **v50 added 10, 11, 17 and 18** (statistical literacy, critical
media analysis, organizational change, risk management), **v51 added 14, 15, 16 and 19**
(research reports, research methodology, interpreting complex data, organizational culture),
**v52 added 20, 21, 22 and 24** (research ethics, diversity and inclusion, business sustainability,
semantic nuance) and **v53 closed the level** with 26–29 (argumentation and rebuttal, professional
proposals, stakeholder communication, specialist presentations), so all thirty C1 topics ship.
(Topic 8, critical thinking, already ships as `c1.inference` — reading between the lines — and
topics 12 and 13 as `c1.persuasion` and `c1.academic`, so none of them was repeated.)
**C2 opened in v55** with topics 4, 5, 8 and 9 — sociolinguistics, discourse analysis, philosophical
texts, interpreting complex speeches — beside its seven v5-era lessons, **v56 added 11–14**
(advanced persuasion, advanced academic writing, professional editing and revision, synthesizing
specialized texts) and **v57 added 15–18** (critical argument analysis, English dialect variation,
pragmatics, culture-specific expressions), so nineteen of its thirty topics ship; the other eleven
are queued in the table below.

## 🟢 A1 — مبتدی (Beginner)

Goal: واژگان و عبارت‌های پایه برای ارتباطات روزمره.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | الفبای انگلیسی و تلفظ حروف | The English Alphabet | ✅ `a1.alphabet.lesson-01` |
| 2 | اعداد و شمارش | Numbers and Counting | ✅ `a1.numbers.lesson-01` |
| 3 | نام، نام خانوادگی و معرفی خود | First Name and Last Name | ✅ `a1.introductions.lesson-01` |
| 4 | احوال‌پرسی و خداحافظی | Greetings and Goodbyes | ✅ `a1.greetings.lesson-01` |
| 5 | کشورها و ملیت‌ها | Countries and Nationalities | ✅ `a1.countries.lesson-01` *(new)* |
| 6 | سن و تاریخ تولد | Age and Birthday | ✅ `a1.age-birthday.lesson-01` *(new)* |
| 7 | خانواده و اعضای آن | Family Members | ✅ `a1.family.lesson-01` |
| 8 | شغل‌ها و حرفه‌ها | Jobs and Occupations | ✅ `a1.jobs.lesson-01` *(v28)* |
| 9 | روزهای هفته و ماه‌ها | Days and Months | ✅ `a1.days-months.lesson-01` *(new)* |
| 10 | ساعت و زمان | Time and Clock | ✅ `a1.time.lesson-01` *(new)* |
| 11 | رنگ‌ها | Colors | ✅ `a1.colors.lesson-01` *(new)* |
| 12 | اشیای کلاس و وسایل آموزشی | Classroom Objects | ✅ `a1.classroom.lesson-01` *(new)* |
| 13 | وسایل شخصی | Personal Belongings | ✅ `a1.belongings.lesson-01` *(v28)* |
| 14 | خانه و اتاق‌ها | Home and Rooms | ✅ `a1.home-rooms.lesson-01` *(v28)* |
| 15 | وسایل خانه | Household Objects | ✅ `a1.house-objects.lesson-01` *(v28)* |
| 16 | غذاها و نوشیدنی‌ها | Food and Drinks | ✅ `a1.food-drinks.lesson-01` *(v28)* |
| 17 | میوه‌ها و سبزیجات | Fruits and Vegetables | ✅ `a1.fruits-vegetables.lesson-01` *(v28)* |
| 18 | لباس‌ها | Clothes | ✅ `a1.clothes.lesson-01` *(v28)* |
| 19 | اعضای بدن | Parts of the Body | ✅ `a1.body.lesson-01` *(v28)* |
| 20 | حیوانات | Animals | ✅ `a1.animals.lesson-01` *(v28)* |
| 21 | آب‌وهوا و فصل‌ها | Weather and Seasons | ✅ `a1.weather.lesson-01` |
| 22 | کارهای روزمره | Daily Routines | ✅ `a1.daily-routine.lesson-01` |
| 23 | علایق و سرگرمی‌ها | Likes and Hobbies | ✅ `a1.hobbies.lesson-01` *(v28)* |
| 24 | خرید و پرسیدن قیمت | Shopping and Prices | ✅ `a1.shopping.lesson-01` |
| 25 | مکان‌های شهر | Places in Town | ✅ `a1.places-town.lesson-01` *(v28)* |
| 26 | آدرس پرسیدن و مسیرها | Asking for Directions | ✅ `a1.directions.lesson-01` *(v28)* |
| 27 | حمل‌ونقل عمومی | Public Transportation | ✅ `a1.transport.lesson-01` *(v28)* |
| 28 | در کافه و رستوران | At a Café and Restaurant | ✅ `a1.cafe-restaurant.lesson-01` *(v28)* |
| 29 | معرفی دوستان و توصیف افراد | Describing People | ✅ `a1.describing-people.lesson-01` *(v28)* |
| 30 | معرفی محل زندگی | Where I Live | ✅ `a1.where-i-live.lesson-01` *(v28)* |

Grammar supplements that are **not** syllabus topics but are still required A1 material (and stay):
`a1.spelling.lesson-01`, `a1.articles.lesson-01`, `a1.plurals.lesson-01`, `a1.pronouns.lesson-01`,
`a1.can.lesson-01`, `a1.imperatives.lesson-01`, `a1.question-words.lesson-01`.

## 🔵 A2 — مقدماتی رو به متوسط (Elementary)

Goal: مدیریت موقعیت‌های متداول زندگی و مکالمات ساده.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | رزرو هتل | Booking a Hotel | ✅ `a2.hotel-booking.lesson-01` *(v29)* |
| 2 | در فرودگاه | At the Airport | ✅ `a2.airport.lesson-01` |
| 3 | خرید بلیت هواپیما | Buying a Plane Ticket | ✅ `a2.plane-ticket.lesson-01` *(v29)* |
| 4 | ورود به هتل و تحویل اتاق | Hotel Check-in | ✅ `a2.hotel-checkin.lesson-01` *(v29)* |
| 5 | سفر با قطار و اتوبوس | Traveling by Train and Bus | ✅ `a2.train-bus.lesson-01` *(v29)* |
| 6 | پرسیدن اطلاعات سفر | Asking About Travel Information | ✅ `a2.travel-info.lesson-01` *(v29)* |
| 7 | برنامه‌ریزی تعطیلات | Planning a Holiday | ✅ `a2.holiday-plans.lesson-01` *(v29)* |
| 8 | خرید لباس و انتخاب اندازه | Buying Clothes | ✅ `a2.buying-clothes.lesson-01` *(v32)* |
| 9 | مرجوع کردن کالا | Returning an Item | ✅ `a2.returns.lesson-01` *(v32)* |
| 10 | پرداخت و روش‌های پرداخت | Payments and Payment Methods | ✅ `a2.payments.lesson-01` *(v32)* |
| 11 | مراجعه به پزشک | Visiting a Doctor | ✅ `a2.health.lesson-01` |
| 12 | بیان علائم بیماری | Describing Symptoms | ✅ `a2.symptoms.lesson-01` *(v32)* |
| 13 | داروخانه و خرید دارو | At the Pharmacy | ✅ `a2.pharmacy.lesson-01` *(v32)* |
| 14 | صحبت دربارهٔ گذشته | Talking About the Past | ✅ `a2.past-simple.lesson-01` |
| 15 | تعریف خاطرات کودکی | Childhood Memories | ✅ `a2.childhood.lesson-01` *(v32)* |
| 16 | جشن‌ها و مناسبت‌ها | Celebrations and Special Occasions | ✅ `a2.celebrations.lesson-01` *(v32)* |
| 17 | دعوت کردن و پاسخ به دعوت | Invitations | ✅ `a2.invitations.lesson-01` *(v32)* |
| 18 | تماس تلفنی | Phone Conversations | ✅ `a2.phone.lesson-01` *(v32)* |
| 19 | نوشتن ایمیل ساده | Writing Simple Emails | ✅ `a2.emails.lesson-01` *(v32)* |
| 20 | زندگی در شهر و روستا | City and Country Life | ✅ `a2.city-life.lesson-01` |
| 21 | اجارهٔ خانه | Renting a Home | ✅ `a2.renting.lesson-01` *(v34)* |
| 22 | مشکلات خانه | Household Problems | ✅ `a2.house-problems.lesson-01` *(v34)* |
| 23 | برنامه‌ریزی برای آینده | Future Plans | ✅ `a2.future-plans.lesson-01` *(v34)* |
| 24 | مقایسهٔ افراد و اشیا | Comparing People and Things | ✅ `a2.comparing.lesson-01` *(v34)* |
| 25 | بیان نظر و ترجیح | Opinions and Preferences | ✅ `a2.opinions.lesson-01` *(v34)* |
| 26 | درخواست کمک و اجازه | Asking for Help and Permission | ✅ `a2.asking-help.lesson-01` *(v34)* |
| 27 | ورزش و فعالیت بدنی | Sports and Exercise | ✅ `a2.sports.lesson-01` *(v34)* |
| 28 | سینما و سرگرمی | Movies and Entertainment | ✅ `a2.hobbies.lesson-01` (سرگرمی) |
| 29 | محیط کار و وظایف شغلی | Work and Job Responsibilities | ✅ `a2.work.lesson-01` *(v34)* |
| 30 | حل مشکلات روزمره | Solving Everyday Problems | ✅ `a2.everyday-problems.lesson-01` *(v34)* |

Also shipping at A2 outside this list: `a2.restaurant.lesson-01`, `a2.directions.lesson-01`.

## 🟣 B1 — متوسط (Intermediate)

Goal: ارتباط مستقل در موقعیت‌های آشنا و بیان تجربه‌ها و نظرات.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | معرفی کامل سوابق شخصی و تحصیلی | Personal Background | ✅ `b1.personal-background.lesson-01` *(v36)* |
| 2 | مصاحبهٔ شغلی | Job Interviews | ✅ `b1.work.lesson-01` (کار و حرفه) |
| 3 | جست‌وجوی شغل | Job Hunting | ✅ `b1.job-hunting.lesson-01` *(v36)* |
| 4 | تحصیل در دانشگاه | University Life | ✅ `b1.university.lesson-01` *(v36)* |
| 5 | یادگیری مهارت‌های جدید | Learning New Skills | ✅ `b1.new-skills.lesson-01` *(v36)* |
| 6 | سفرهای خارجی و مشکلات سفر | International Travel Problems | ✅ `b1.travel-problems.lesson-01` *(v37)* |
| 7 | فرهنگ‌ها و تفاوت‌های فرهنگی | Cultural Differences | ✅ `b1.cultural-differences.lesson-01` *(v37)* |
| 8 | دوستی و روابط اجتماعی | Friendship and Relationships | ✅ `b1.friendship.lesson-01` *(v37)* |
| 9 | بیان احساسات و حالات روحی | Feelings and Emotions | ✅ `b1.emotions.lesson-01` *(v37)* |
| 10 | حل اختلاف و مذاکره | Resolving Conflicts | ✅ `b1.conflicts.lesson-01` *(v38)* |
| 11 | بیان موافقت و مخالفت | Agreeing and Disagreeing | ✅ `b1.agreeing.lesson-01` *(v38)* |
| 12 | شرح یک تجربهٔ مهم | Describing an Important Experience | ✅ `b1.experiences.lesson-01` |
| 13 | داستان‌گویی | Storytelling | ✅ `b1.storytelling.lesson-01` *(v38)* |
| 14 | رسانه‌ها و اخبار | Media and News | ✅ `b1.news.lesson-01` |
| 15 | فناوری در زندگی روزمره | Technology in Daily Life | ✅ `b1.technology.lesson-01` *(v38)* |
| 16 | شبکه‌های اجتماعی | Social Media | ✅ `b1.social-media.lesson-01` *(v39)* |
| 17 | محیط زیست و آلودگی | Environment and Pollution | ✅ `b1.environment.lesson-01` *(v39)* |
| 18 | سلامت و سبک زندگی | Health and Lifestyle | ✅ `b1.health-lifestyle.lesson-01` |
| 19 | عادت‌های غذایی | Eating Habits | ✅ `b1.eating-habits.lesson-01` *(v39)* |
| 20 | مدیریت زمان | Time Management | ✅ `b1.time-management.lesson-01` *(v39)* |
| 21 | برنامه‌ریزی مالی شخصی | Personal Finance | ✅ `b1.money.lesson-01` (پول و بودجه) |
| 22 | خدمات بانکی | Banking Services | ✅ `b1.banking.lesson-01` *(v40)* |
| 23 | شکایت و درخواست بازپرداخت | Complaints and Refunds | ✅ `b1.complaints.lesson-01` *(v40)* |
| 24 | نوشتن نامه و ایمیل رسمی | Formal Letters and Emails | ✅ `b1.formal-emails.lesson-01` *(v40)* |
| 25 | ارائهٔ یک موضوع کوتاه | Giving a Short Presentation | ✅ `b1.presentation.lesson-01` *(v40)* |
| 26 | شرکت در جلسه | Participating in Meetings | ✅ `b1.meetings.lesson-01` *(v41)* |
| 27 | توصیف نمودارها و اطلاعات | Describing Charts and Data | ✅ `b1.charts.lesson-01` *(v41)* |
| 28 | پیش‌بینی و احتمال | Predictions and Possibilities | ✅ `b1.plans.lesson-01` (برنامه‌ها و تصمیم‌ها) |
| 29 | اهداف و آرزوهای آینده | Future Goals and Ambitions | ✅ `b1.goals.lesson-01` *(v41)* |
| 30 | بحث دربارهٔ مسائل اجتماعی | Discussing Social Issues | ✅ `b1.social-issues.lesson-01` *(v41)* |

## 🟠 B2 — متوسط رو به پیشرفته (Upper-intermediate)

Goal: بیان دقیق‌تر نظرات، درک متون پیچیده‌تر و تعامل مؤثر در محیط کار و اجتماع.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | مذاکره در محیط کار | Workplace Negotiations | ✅ `b2.negotiation.lesson-01` *(v42)* |
| 2 | مدیریت پروژه | Project Management | ✅ `b2.projects.lesson-01` *(v42)* |
| 3 | رهبری و مدیریت تیم | Leadership and Teamwork | ✅ `b2.leadership.lesson-01` *(v42)* |
| 4 | کارآفرینی و راه‌اندازی کسب‌وکار | Entrepreneurship | ✅ `b2.entrepreneurship.lesson-01` *(v42)* |
| 5 | بازاریابی و تبلیغات | Marketing and Advertising | ✅ `b2.media.lesson-01` (رسانه و تبلیغات) |
| 6 | مدیریت مالی و سرمایه‌گذاری | Finance and Investment | ✅ `b2.finance.lesson-01` *(v43)* |
| 7 | اقتصاد جهانی | The Global Economy | ✅ `b2.economy.lesson-01` *(v43)* |
| 8 | فناوری‌های نوظهور | Emerging Technologies | ✅ `b2.technology.lesson-01` |
| 9 | هوش مصنوعی در زندگی و کار | Artificial Intelligence | ✅ `b2.ai.lesson-01` *(v43)* |
| 10 | حریم خصوصی و امنیت دیجیتال | Digital Privacy and Security | ✅ `b2.privacy.lesson-01` *(v43)* |
| 11 | تغییرات اقلیمی | Climate Change | ✅ `b2.environment.lesson-01` |
| 12 | انرژی‌های تجدیدپذیر | Renewable Energy | ✅ `b2.renewables.lesson-01` *(v44)* |
| 13 | نظام‌های آموزشی | Education Systems | ✅ `b2.education.lesson-01` *(v44)* |
| 14 | سلامت روان | Mental Health | ✅ `b2.mental-health.lesson-01` *(v44)* |
| 15 | رسانه و سواد رسانه‌ای | Media Literacy | ✅ `b2.media-literacy.lesson-01` *(v44)* |
| 16 | تبلیغات و تأثیرگذاری بر مخاطب | Persuasion and Advertising | ✅ `b2.persuasion.lesson-01` *(v45)* |
| 17 | حل مسئله و تصمیم‌گیری | Problem-solving and Decision-making | ✅ `b2.problems.lesson-01` |
| 18 | مدیریت بحران | Crisis Management | ✅ `b2.crisis.lesson-01` *(v45)* |
| 19 | نوشتن گزارش حرفه‌ای | Writing Professional Reports | ✅ `b2.reports.lesson-01` *(v45)* |
| 20 | ارائه و سخنرانی حرفه‌ای | Professional Presentations | ✅ `b2.presentations.lesson-01` *(v45)* |
| 21 | بحث و مناظره | Debating and Discussion | ✅ `b2.debate.lesson-01` *(v46)* |
| 22 | نقد فیلم و کتاب | Reviewing Films and Books | ✅ `b2.review.lesson-01` *(v46)* |
| 23 | اصطلاحات رایج انگلیسی | Common English Idioms | ✅ `b2.idioms.lesson-01` *(v46)* |
| 24 | افعال عبارتی پرکاربرد | Common Phrasal Verbs | ✅ `b2.phrasal-verbs.lesson-01` *(v46)* |
| 25 | ارتباط بین‌فرهنگی | Intercultural Communication | ✅ `b2.intercultural.lesson-01` *(v47)* |
| 26 | حل اختلاف در محیط کار | Workplace Conflict Resolution | ✅ `b2.workplace-conflict.lesson-01` *(v47)* |
| 27 | طراحی و ارزیابی استراتژی | Strategy and Evaluation | ✅ `b2.strategy.lesson-01` *(v47)* |
| 28 | اخلاق حرفه‌ای | Professional Ethics | ✅ `b2.ethics.lesson-01` *(v47)* |
| 29 | تصمیم‌گیری دربارهٔ آیندهٔ شغلی | Career Decisions | ✅ `b2.career-decisions.lesson-01` *(v54)* |
| 30 | تحلیل دیدگاه‌های مخالف | Evaluating Different Viewpoints | ✅ `b2.viewpoints.lesson-01` *(v54)* |

Also shipping at B2 outside this list: `b2.interview.lesson-01`, `b2.research.lesson-01`.

## 🔴 C1 — پیشرفته (Advanced)

Goal: استفادهٔ دقیق، منعطف و حرفه‌ای از زبان در موضوعات پیچیده.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | ارتباطات مدیریتی | Executive Communication | ✅ `c1.executive.lesson-01` *(v48)* |
| 2 | مذاکره‌های پیچیده | Advanced Negotiation | ✅ `c1.negotiation.lesson-01` *(v48)* |
| 3 | سیاست‌گذاری عمومی | Public Policy | ✅ `c1.policy.lesson-01` *(v48)* |
| 4 | روابط بین‌الملل | International Relations | ✅ `c1.international.lesson-01` *(v48)* |
| 5 | تحلیل اقتصادی | Economic Analysis | ✅ `c1.economy.lesson-01` *(v49)* |
| 6 | اخلاق در فناوری | Technology Ethics | ✅ `c1.tech-ethics.lesson-01` *(v49)* |
| 7 | فلسفه و استدلال | Philosophy and Reasoning | ✅ `c1.philosophy.lesson-01` *(v49)* |
| 8 | تفکر انتقادی | Critical Thinking | ✅ `c1.inference.lesson-01` (خواندن بین خطوط) |
| 9 | خطاهای شناختی | Cognitive Biases | ✅ `c1.cognitive-bias.lesson-01` *(v49)* |
| 10 | سواد آماری | Statistical Literacy | ✅ `c1.statistics.lesson-01` *(v50)* |
| 11 | تحلیل انتقادی رسانه | Critical Media Analysis | ✅ `c1.media-analysis.lesson-01` *(v50)* |
| 12 | اقناع و فنون بلاغی | Persuasion and Rhetoric | ✅ `c1.persuasion.lesson-01` |
| 13 | نوشتن مقالهٔ تحلیلی | Analytical Essay Writing | ✅ `c1.academic.lesson-01` (نوشتار آکادمیک) |
| 14 | گزارش‌های پژوهشی | Research Reports | ✅ `c1.research-reports.lesson-01` *(v51)* |
| 15 | روش تحقیق | Research Methodology | ✅ `c1.research-method.lesson-01` *(v51)* |
| 16 | تفسیر داده‌های پیچیده | Interpreting Complex Data | ✅ `c1.data-interpretation.lesson-01` *(v51)* |
| 17 | مدیریت تغییر سازمانی | Organizational Change | ✅ `c1.org-change.lesson-01` *(v50)* |
| 18 | مدیریت ریسک | Risk Management | ✅ `c1.risk.lesson-01` *(v50)* |
| 19 | فرهنگ سازمانی | Organizational Culture | ✅ `c1.org-culture.lesson-01` *(v51)* |
| 20 | اخلاق در پژوهش | Research Ethics | ✅ `c1.research-ethics.lesson-01` *(v52)* |
| 21 | تنوع و شمول | Diversity and Inclusion | ✅ `c1.diversity-inclusion.lesson-01` *(v52)* |
| 22 | پایداری کسب‌وکار | Business Sustainability | ✅ `c1.business-sustainability.lesson-01` *(v52)* |
| 23 | زبان رسمی و غیررسمی | Formal and Informal Registers | ✅ `c1.register.lesson-01` |
| 24 | ظرافت‌های معنایی واژگان | Semantic Nuance | ✅ `c1.semantic-nuance.lesson-01` *(v52)* |
| 25 | استعاره و کنایه | Metaphor and Irony | ✅ `c1.culture.lesson-01` (سینما و فرهنگ) |
| 26 | استدلال و پاسخ به نقد | Argumentation and Rebuttal | ✅ `c1.argumentation.lesson-01` *(v53)* |
| 27 | نگارش پیشنهادهای حرفه‌ای | Professional Proposals | ✅ `c1.proposals.lesson-01` *(v53)* |
| 28 | مدیریت ارتباط با ذی‌نفعان | Stakeholder Communication | ✅ `c1.stakeholders.lesson-01` *(v53)* |
| 29 | ارائهٔ تخصصی | Specialist Presentations | ✅ `c1.specialist-presentations.lesson-01` *(v53)* |
| 30 | تحلیل متون پیچیده | Analyzing Complex Texts | ✅ `c1.cohesion.lesson-01` (انسجام متن) |

Also shipping at C1 outside this list: `c1.phrasal-idioms.lesson-01`.

## ⚫ C2 — تسلط پیشرفته (Proficiency)

Goal: درک و تولید زبان بسیار دقیق، ظریف و متناسب با موقعیت.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | ظرافت‌های معنایی و انتخاب واژه | Lexical Precision | ✅ `c2.nuance.lesson-01` |
| 2 | ابهام و چندمعنایی | Ambiguity and Polysemy | ✅ `c2.collocation.lesson-01` (هم‌نشینی و دقت) |
| 3 | طنز و کنایهٔ پیچیده | Advanced Humor and Irony | ✅ `c2.irony.lesson-01` |
| 4 | زبان‌شناسی اجتماعی | Sociolinguistics | ✅ `c2.sociolinguistics.lesson-01` *(v55)* |
| 5 | تحلیل گفتمان | Discourse Analysis | ✅ `c2.discourse.lesson-01` *(v55)* |
| 6 | سبک‌شناسی ادبی | Literary Stylistics | ✅ `c2.rhetoric.lesson-01` (سبک و بلاغت) |
| 7 | نقد ادبی | Literary Criticism | ✅ `c2.literature.lesson-01` (ادبیات و معنا) |
| 8 | تحلیل متون فلسفی | Philosophical Texts | ✅ `c2.philosophy.lesson-01` *(v55)* |
| 9 | تفسیر سخنرانی‌های پیچیده | Interpreting Complex Speeches | ✅ `c2.speeches.lesson-01` *(v55)* |
| 10 | مناظرهٔ تخصصی | Expert Debate | ✅ `c2.argumentation.lesson-01` (استدلال و مغالطه) |
| 11 | فنون اقناع پیشرفته | Advanced Persuasion | ✅ `c2.advanced-persuasion.lesson-01` *(v56)* |
| 12 | نگارش دانشگاهی پیشرفته | Advanced Academic Writing | ✅ `c2.academic-writing.lesson-01` *(v56)* |
| 13 | ویرایش و بازنویسی حرفه‌ای | Professional Editing and Revision | ✅ `c2.editing.lesson-01` *(v56)* |
| 14 | خلاصه‌سازی متون تخصصی | Synthesizing Specialized Texts | ✅ `c2.synthesis.lesson-01` *(v56)* |
| 15 | تحلیل انتقادی استدلال‌ها | Critical Argument Analysis | ✅ `c2.argument-analysis.lesson-01` *(v57)* |
| 16 | تفاوت‌های گویشی انگلیسی | English Dialect Variation | ✅ `c2.dialects.lesson-01` *(v57)* |
| 17 | کاربردشناسی زبان | Pragmatics | ✅ `c2.pragmatics.lesson-01` *(v57)* |
| 18 | اصطلاحات فرهنگی | Culture-specific Expressions | ✅ `c2.culture-expressions.lesson-01` *(v57)* |
| 19 | کنایه‌های ادبی | Literary Allusions | ✅ `c2.allusions.lesson-01` *(v58)* |
| 20 | بازنویسی با حفظ معنا | Paraphrasing with Precision | ✅ `c2.paraphrasing.lesson-01` *(v58)* |
| 21 | ترجمهٔ مفهومی | Meaning-based Translation | ✅ `c2.translation.lesson-01` *(v58)* |
| 22 | ارتباطات دیپلماتیک | Diplomatic Communication | ✅ `c2.diplomacy.lesson-01` *(v58)* |
| 23 | سخنرانی برای مخاطبان تخصصی | Speaking to Specialist Audiences | ✅ `c2.specialist-audiences.lesson-01` *(v59)* |
| 24 | تحلیل روایت | Narrative Analysis | ✅ `c2.narrative.lesson-01` *(v59)* |
| 25 | استدلال در شرایط عدم قطعیت | Reasoning Under Uncertainty | ✅ `c2.uncertainty.lesson-01` *(v59)* |
| 26 | ارتباطات در شرایط حساس | Communication in Sensitive Situations | ✅ `c2.sensitive.lesson-01` *(v59)* |
| 27 | نقد سیاست‌ها و تصمیم‌ها | Policy and Decision Critique | ✅ `c2.policy.lesson-01` *(v60)* |
| 28 | تحلیل متون حقوقی | Legal Text Analysis | ✅ `c2.legal.lesson-01` *(v60)* |
| 29 | نقد علمی | Scientific Critique | ✅ `c2.science.lesson-01` (علم و کشف) |
| 30 | انتخاب سبک زبانی متناسب با مخاطب | Audience-sensitive Language Use | ✅ `c2.audience.lesson-01` *(v60)* |

## Authoring the next batch

1. **The syllabus is complete.** All six levels ship all 30 topics each (180 of 180) as of v60:
B1, B2 and C1 closed in v41–v54, C2 closed in v60 (1, 2, 3, 6, 7, 10 and 29 from the v5 bundle,
v55's 4, 5, 8, 9, v56's 11–14, v57's 15–18, v58's 19–22, v59's 23–26 and v60's 27, 28 and 30).
New material now means *deepening* an existing topic or shipping a lesson outside the list — not
opening a new syllabus row. A topic is a *lesson*, never a half-lesson. Seven lessons sit outside
the list today: `b2.interview`, `b2.research`, `c1.phrasal-idioms`, `a2.restaurant`, `a2.directions`,
`a2.past-simple` and the seven A1 grammar supplements that make A1 37 lessons. Some rows also ship
under another id — B1's job interview as `b1.work`, personal finance as `b1.money`, plans as
`b1.plans`; A2's hobbies as `a2.hobbies`; C1's critical thinking as `c1.inference`, metaphor as
`c1.culture`, complex texts as `c1.cohesion`; C2's polysemy as `c2.collocation`, stylistics as
`c2.rhetoric`, criticism as `c2.literature`, debate as `c2.argumentation` and scientific critique as
`c2.science`.
2. **Six words** per lesson, each with a Persian meaning, an English definition, IPA, a usage note
   (`explanationFa`), two collocations and **two bilingual examples** — and every example must
   contain its own word.
3. **A sectioned teaching page** (`## قاعده`, `## جدول`, `## مثال‌ها`, `## خطاهای رایج`, plus
   `## تلفظ` at A1 — the pronunciation card and the listening drill are what the phonology node
   moves on).
4. **Nine exercises** per lesson: meaning choice ×3, `fill_blank` ×2 (two accepted answers, or a
   recorded reason in `ContentDistributionTest.reviewedSingleAnswerBlanks`), `word_recall`,
   `translation`, a guided `listening`, and one `sentence_production` whose anchors credit its own
   exemplar.
5. **One vocabulary knowledge node** per lesson, plus the new lesson listed on the phonology node.
   Prerequisites must already exist (a cycle fails validation).
6. Run `node scripts/balance_answer_positions.mjs`, then `./gradlew validateContent --rerun`,
   then the content tests with `--rerun`. Bump `contentVersion` in all five JSON files together and
   add a [`CHANGELOG-content.md`](CHANGELOG-content.md) entry.
