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

Current bundle: **76 lessons** (A1 = 37, A2 = 13, B1 = 6, B2 = 6, C1 = 7, C2 = 7). **A1 is complete**: all
30 syllabus topics ship, plus the seven A1 grammar supplements (spelling, articles, plurals, pronouns,
`can`, imperatives, question words). The six topics marked *new* shipped in v27; the sixteen marked
*v28* completed the level. A2 started in v29 with its first six syllabus topics (1 and 3–7), all
travel and staying.

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
| 8 | خرید لباس و انتخاب اندازه | Buying Clothes | — |
| 9 | مرجوع کردن کالا | Returning an Item | — |
| 10 | پرداخت و روش‌های پرداخت | Payments and Payment Methods | — |
| 11 | مراجعه به پزشک | Visiting a Doctor | ✅ `a2.health.lesson-01` |
| 12 | بیان علائم بیماری | Describing Symptoms | — |
| 13 | داروخانه و خرید دارو | At the Pharmacy | — |
| 14 | صحبت دربارهٔ گذشته | Talking About the Past | ✅ `a2.past-simple.lesson-01` |
| 15 | تعریف خاطرات کودکی | Childhood Memories | — |
| 16 | جشن‌ها و مناسبت‌ها | Celebrations and Special Occasions | — |
| 17 | دعوت کردن و پاسخ به دعوت | Invitations | — |
| 18 | تماس تلفنی | Phone Conversations | — |
| 19 | نوشتن ایمیل ساده | Writing Simple Emails | — |
| 20 | زندگی در شهر و روستا | City and Country Life | ✅ `a2.city-life.lesson-01` |
| 21 | اجارهٔ خانه | Renting a Home | — |
| 22 | مشکلات خانه | Household Problems | — |
| 23 | برنامه‌ریزی برای آینده | Future Plans | — |
| 24 | مقایسهٔ افراد و اشیا | Comparing People and Things | — |
| 25 | بیان نظر و ترجیح | Opinions and Preferences | — |
| 26 | درخواست کمک و اجازه | Asking for Help and Permission | — |
| 27 | ورزش و فعالیت بدنی | Sports and Exercise | — |
| 28 | سینما و سرگرمی | Movies and Entertainment | ✅ `a2.hobbies.lesson-01` (سرگرمی) |
| 29 | محیط کار و وظایف شغلی | Work and Job Responsibilities | — |
| 30 | حل مشکلات روزمره | Solving Everyday Problems | — |

Also shipping at A2 outside this list: `a2.restaurant.lesson-01`, `a2.directions.lesson-01`.

## 🟣 B1 — متوسط (Intermediate)

Goal: ارتباط مستقل در موقعیت‌های آشنا و بیان تجربه‌ها و نظرات.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | معرفی کامل سوابق شخصی و تحصیلی | Personal Background | — |
| 2 | مصاحبهٔ شغلی | Job Interviews | ✅ `b1.work.lesson-01` (کار و حرفه) |
| 3 | جست‌وجوی شغل | Job Hunting | — |
| 4 | تحصیل در دانشگاه | University Life | — |
| 5 | یادگیری مهارت‌های جدید | Learning New Skills | — |
| 6 | سفرهای خارجی و مشکلات سفر | International Travel Problems | — |
| 7 | فرهنگ‌ها و تفاوت‌های فرهنگی | Cultural Differences | — |
| 8 | دوستی و روابط اجتماعی | Friendship and Relationships | — |
| 9 | بیان احساسات و حالات روحی | Feelings and Emotions | — |
| 10 | حل اختلاف و مذاکره | Resolving Conflicts | — |
| 11 | بیان موافقت و مخالفت | Agreeing and Disagreeing | — |
| 12 | شرح یک تجربهٔ مهم | Describing an Important Experience | ✅ `b1.experiences.lesson-01` |
| 13 | داستان‌گویی | Storytelling | — |
| 14 | رسانه‌ها و اخبار | Media and News | ✅ `b1.news.lesson-01` |
| 15 | فناوری در زندگی روزمره | Technology in Daily Life | — |
| 16 | شبکه‌های اجتماعی | Social Media | — |
| 17 | محیط زیست و آلودگی | Environment and Pollution | — |
| 18 | سلامت و سبک زندگی | Health and Lifestyle | ✅ `b1.health-lifestyle.lesson-01` |
| 19 | عادت‌های غذایی | Eating Habits | — |
| 20 | مدیریت زمان | Time Management | — |
| 21 | برنامه‌ریزی مالی شخصی | Personal Finance | ✅ `b1.money.lesson-01` (پول و بودجه) |
| 22 | خدمات بانکی | Banking Services | — |
| 23 | شکایت و درخواست بازپرداخت | Complaints and Refunds | — |
| 24 | نوشتن نامه و ایمیل رسمی | Formal Letters and Emails | — |
| 25 | ارائهٔ یک موضوع کوتاه | Giving a Short Presentation | — |
| 26 | شرکت در جلسه | Participating in Meetings | — |
| 27 | توصیف نمودارها و اطلاعات | Describing Charts and Data | — |
| 28 | پیش‌بینی و احتمال | Predictions and Possibilities | ✅ `b1.plans.lesson-01` (برنامه‌ها و تصمیم‌ها) |
| 29 | اهداف و آرزوهای آینده | Future Goals and Ambitions | — |
| 30 | بحث دربارهٔ مسائل اجتماعی | Discussing Social Issues | — |

## 🟠 B2 — متوسط رو به پیشرفته (Upper-intermediate)

Goal: بیان دقیق‌تر نظرات، درک متون پیچیده‌تر و تعامل مؤثر در محیط کار و اجتماع.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | مذاکره در محیط کار | Workplace Negotiations | — |
| 2 | مدیریت پروژه | Project Management | — |
| 3 | رهبری و مدیریت تیم | Leadership and Teamwork | — |
| 4 | کارآفرینی و راه‌اندازی کسب‌وکار | Entrepreneurship | — |
| 5 | بازاریابی و تبلیغات | Marketing and Advertising | ✅ `b2.media.lesson-01` (رسانه و تبلیغات) |
| 6 | مدیریت مالی و سرمایه‌گذاری | Finance and Investment | — |
| 7 | اقتصاد جهانی | The Global Economy | — |
| 8 | فناوری‌های نوظهور | Emerging Technologies | ✅ `b2.technology.lesson-01` |
| 9 | هوش مصنوعی در زندگی و کار | Artificial Intelligence | — |
| 10 | حریم خصوصی و امنیت دیجیتال | Digital Privacy and Security | — |
| 11 | تغییرات اقلیمی | Climate Change | ✅ `b2.environment.lesson-01` |
| 12 | انرژی‌های تجدیدپذیر | Renewable Energy | — |
| 13 | نظام‌های آموزشی | Education Systems | — |
| 14 | سلامت روان | Mental Health | — |
| 15 | رسانه و سواد رسانه‌ای | Media Literacy | — |
| 16 | تبلیغات و تأثیرگذاری بر مخاطب | Persuasion and Advertising | — |
| 17 | حل مسئله و تصمیم‌گیری | Problem-solving and Decision-making | ✅ `b2.problems.lesson-01` |
| 18 | مدیریت بحران | Crisis Management | — |
| 19 | نوشتن گزارش حرفه‌ای | Writing Professional Reports | — |
| 20 | ارائه و سخنرانی حرفه‌ای | Professional Presentations | — |
| 21 | بحث و مناظره | Debating and Discussion | — |
| 22 | نقد فیلم و کتاب | Reviewing Films and Books | — |
| 23 | اصطلاحات رایج انگلیسی | Common English Idioms | — |
| 24 | افعال عبارتی پرکاربرد | Common Phrasal Verbs | — |
| 25 | ارتباط بین‌فرهنگی | Intercultural Communication | — |
| 26 | حل اختلاف در محیط کار | Workplace Conflict Resolution | — |
| 27 | طراحی و ارزیابی استراتژی | Strategy and Evaluation | — |
| 28 | اخلاق حرفه‌ای | Professional Ethics | — |
| 29 | تصمیم‌گیری دربارهٔ آیندهٔ شغلی | Career Decisions | — |
| 30 | تحلیل دیدگاه‌های مخالف | Evaluating Different Viewpoints | — |

Also shipping at B2 outside this list: `b2.interview.lesson-01`, `b2.research.lesson-01`.

## 🔴 C1 — پیشرفته (Advanced)

Goal: استفادهٔ دقیق، منعطف و حرفه‌ای از زبان در موضوعات پیچیده.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | ارتباطات مدیریتی | Executive Communication | — |
| 2 | مذاکره‌های پیچیده | Advanced Negotiation | — |
| 3 | سیاست‌گذاری عمومی | Public Policy | — |
| 4 | روابط بین‌الملل | International Relations | — |
| 5 | تحلیل اقتصادی | Economic Analysis | — |
| 6 | اخلاق در فناوری | Technology Ethics | — |
| 7 | فلسفه و استدلال | Philosophy and Reasoning | — |
| 8 | تفکر انتقادی | Critical Thinking | ✅ `c1.inference.lesson-01` (خواندن بین خطوط) |
| 9 | خطاهای شناختی | Cognitive Biases | — |
| 10 | سواد آماری | Statistical Literacy | — |
| 11 | تحلیل انتقادی رسانه | Critical Media Analysis | — |
| 12 | اقناع و فنون بلاغی | Persuasion and Rhetoric | ✅ `c1.persuasion.lesson-01` |
| 13 | نوشتن مقالهٔ تحلیلی | Analytical Essay Writing | ✅ `c1.academic.lesson-01` (نوشتار آکادمیک) |
| 14 | گزارش‌های پژوهشی | Research Reports | — |
| 15 | روش تحقیق | Research Methodology | — |
| 16 | تفسیر داده‌های پیچیده | Interpreting Complex Data | — |
| 17 | مدیریت تغییر سازمانی | Organizational Change | — |
| 18 | مدیریت ریسک | Risk Management | — |
| 19 | فرهنگ سازمانی | Organizational Culture | — |
| 20 | اخلاق در پژوهش | Research Ethics | — |
| 21 | تنوع و شمول | Diversity and Inclusion | — |
| 22 | پایداری کسب‌وکار | Business Sustainability | — |
| 23 | زبان رسمی و غیررسمی | Formal and Informal Registers | ✅ `c1.register.lesson-01` |
| 24 | ظرافت‌های معنایی واژگان | Semantic Nuance | — |
| 25 | استعاره و کنایه | Metaphor and Irony | ✅ `c1.culture.lesson-01` (سینما و فرهنگ) |
| 26 | استدلال و پاسخ به نقد | Argumentation and Rebuttal | — |
| 27 | نگارش پیشنهادهای حرفه‌ای | Professional Proposals | — |
| 28 | مدیریت ارتباط با ذی‌نفعان | Stakeholder Communication | — |
| 29 | ارائهٔ تخصصی | Specialist Presentations | — |
| 30 | تحلیل متون پیچیده | Analyzing Complex Texts | ✅ `c1.cohesion.lesson-01` (انسجام متن) |

Also shipping at C1 outside this list: `c1.phrasal-idioms.lesson-01`.

## ⚫ C2 — تسلط پیشرفته (Proficiency)

Goal: درک و تولید زبان بسیار دقیق، ظریف و متناسب با موقعیت.

| # | موضوع | Topic | Status |
|---|-------|-------|--------|
| 1 | ظرافت‌های معنایی و انتخاب واژه | Lexical Precision | ✅ `c2.nuance.lesson-01` |
| 2 | ابهام و چندمعنایی | Ambiguity and Polysemy | ✅ `c2.collocation.lesson-01` (هم‌نشینی و دقت) |
| 3 | طنز و کنایهٔ پیچیده | Advanced Humor and Irony | ✅ `c2.irony.lesson-01` |
| 4 | زبان‌شناسی اجتماعی | Sociolinguistics | — |
| 5 | تحلیل گفتمان | Discourse Analysis | — |
| 6 | سبک‌شناسی ادبی | Literary Stylistics | ✅ `c2.rhetoric.lesson-01` (سبک و بلاغت) |
| 7 | نقد ادبی | Literary Criticism | ✅ `c2.literature.lesson-01` (ادبیات و معنا) |
| 8 | تحلیل متون فلسفی | Philosophical Texts | — |
| 9 | تفسیر سخنرانی‌های پیچیده | Interpreting Complex Speeches | — |
| 10 | مناظرهٔ تخصصی | Expert Debate | ✅ `c2.argumentation.lesson-01` (استدلال و مغالطه) |
| 11 | فنون اقناع پیشرفته | Advanced Persuasion | — |
| 12 | نگارش دانشگاهی پیشرفته | Advanced Academic Writing | — |
| 13 | ویرایش و بازنویسی حرفه‌ای | Professional Editing and Revision | — |
| 14 | خلاصه‌سازی متون تخصصی | Synthesizing Specialized Texts | — |
| 15 | تحلیل انتقادی استدلال‌ها | Critical Argument Analysis | — |
| 16 | تفاوت‌های گویشی انگلیسی | English Dialect Variation | — |
| 17 | کاربردشناسی زبان | Pragmatics | — |
| 18 | اصطلاحات فرهنگی | Culture-specific Expressions | — |
| 19 | کنایه‌های ادبی | Literary Allusions | — |
| 20 | بازنویسی با حفظ معنا | Paraphrasing with Precision | — |
| 21 | ترجمهٔ مفهومی | Meaning-based Translation | — |
| 22 | ارتباطات دیپلماتیک | Diplomatic Communication | — |
| 23 | سخنرانی برای مخاطبان تخصصی | Speaking to Specialist Audiences | — |
| 24 | تحلیل روایت | Narrative Analysis | — |
| 25 | استدلال در شرایط عدم قطعیت | Reasoning Under Uncertainty | — |
| 26 | ارتباطات در شرایط حساس | Communication in Sensitive Situations | — |
| 27 | نقد سیاست‌ها و تصمیم‌ها | Policy and Decision Critique | — |
| 28 | تحلیل متون حقوقی | Legal Text Analysis | — |
| 29 | نقد علمی | Scientific Critique | ✅ `c2.science.lesson-01` (علم و کشف) |
| 30 | انتخاب سبک زبانی متناسب با مخاطب | Audience-sensitive Language Use | — |

## Authoring the next batch

1. **Pick the next topics in order** (A1 is done, and A2 has shipped 1–7 and 11/14/20/28; the next A2
   topics are 8, 9, 10, 12, 13 and 15–19).
   A topic is a *lesson*, never a half-lesson.
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
