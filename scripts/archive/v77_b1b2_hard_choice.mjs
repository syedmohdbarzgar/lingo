#!/usr/bin/env node
/**
 * v77: gives every B1 and B2 lesson whose choices were all glosses a hard-end choice item.
 *
 * v76 finished the bottom of the scale; the same measurement leaves 42 lessons here (20 B1,
 * 22 B2) whose whole `multiple_choice` population is «What does X mean?» at 1. Their hard
 * item sat in a fill-in-the-blank, a translation or a production, and
 * `AdaptiveExerciseSelector` raises the level **within a format** — so recognition could not
 * be steered in those lessons at all. This is the shape v70 gave C1 and v75 gave C2.
 *
 * Each item is one exercise at difficulty 3 — the hard end both levels already use (13 items
 * at 3 in B1, 68 in B2) — and it tests **the lesson's own grammar node** in the lesson's own
 * vocabulary, with four grammatical options where the context decides: the first conditional's
 * present-in-the-if-clause, the past perfect before a past event, `had better` + bare
 * infinitive, `the more … the less …`, the present perfect passive with `since`, the inverted
 * tentative conditional, the non-defining `which`, the relative `whose`. Skill is `GRAMMAR`:
 * what the learner has to read is the structure, and the exercise is evidence for the grammar
 * node the lesson declares.
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
  // ── B1: first conditional, passive, perfect, comparatives, modals of advice ─────────────
  {
    lessonId: 'b1.agreeing.lesson-01',
    question: 'If you ______ with me, say so now; we can still change the plan.',
    questionFa: 'شرطی نوع اول: در جملهٔ if فعل حال ساده می‌آید. کدام صورت درست است؟',
    options: ['disagree', 'will disagree', 'disagreed', 'had disagreed'],
    explanationFa: 'در شرطی نوع اول، جملهٔ if حال ساده می‌گیرد و تنها جملهٔ اصلی will دارد: If you disagree …, we can … . will disagree و had disagreed ساختار را می‌شکنند و disagreed گذشته است، پس با now نمی‌آید.',
  },
  {
    lessonId: 'b1.banking.lesson-01',
    question: 'A transfer fee of five dollars ______ on every withdrawal from this account.',
    questionFa: 'مجهول ساده: کارمند بانک فاعل نیست، خودِ کار انجام می‌شود.',
    options: ['is charged', 'charges', 'has charged', 'is charging'],
    explanationFa: 'کاربه‌کار مجهول ساده است: is charged یعنی «گرفته می‌شود». charges و has charged فاعلِ انسانی می‌خواهند و is charging یعنی «در حال گرفتن است»، که با قاعدهٔ همیشگی بانک نمی‌خواند.',
  },
  {
    lessonId: 'b1.charts.lesson-01',
    question: 'The average is ______ of the three figures, so the trend is upward.',
    questionFa: 'برترینِ سه عدد: صفت برتر با of the three چه صورتی می‌گیرد؟',
    options: ['the highest', 'higher', 'the higher', 'highest'],
    explanationFa: 'با of the three مقایسه میان بیش از دو چیز است، پس صفت برترین با the می‌آید: the highest. higher مقایسهٔ دو چیز است و highest بدون the ناقص می‌ماند.',
  },
  {
    lessonId: 'b1.complaints.lesson-01',
    question: 'If the replacement ______ damaged too, we will escalate the complaint to your supervisor.',
    questionFa: 'شرطی نوع اول با صفت: پس از if چه فعلی می‌آید؟',
    options: ['arrives', 'will arrive', 'arrived', 'had arrived'],
    explanationFa: 'جملهٔ if حال ساده است (arrives) و جملهٔ اصلی with will escalate می‌آید؛ will arrive در جملهٔ if و arrived و had arrived گذشته‌اند و با نتیجهٔ آینده نمی‌خوانند.',
  },
  {
    lessonId: 'b1.cultural-differences.lesson-01',
    question: 'You had better ______ your host before you sit down; it is the custom here.',
    questionFa: 'had better با کدام شکل فعل می‌آید؟',
    options: ['greet', 'to greet', 'greeting', 'greeted'],
    explanationFa: 'had better فعل ساده و بی‌to می‌گیرد: had better greet. to greet و greeting ساختار را می‌شکنند و greeted گذشته است، در حالی که این جمله دربارهٔ کار درست در همین لحظه است.',
  },
  {
    lessonId: 'b1.emotions.lesson-01',
    question: 'If you feel lonely, you should ______ a friend rather than keep it to yourself.',
    questionFa: 'should با کدام شکل فعل می‌آید؟',
    options: ['express', 'to express', 'expressing', 'expressed'],
    explanationFa: 'پس از should فعل ساده می‌آید: should express. to express و expressing نادرست‌اند و expressed جمله را به گذشته می‌برد.',
  },
  {
    lessonId: 'b1.environment.lesson-01',
    question: 'Plastic ______ much more carefully than it is today, or the damage cannot be undone.',
    questionFa: 'مجهول با فعل وجهی: کدام ترتیب درست است؟',
    options: ['must be recycled', 'must recycle', 'must have recycled', 'must recycling'],
    explanationFa: 'مجهول با وجهی چنین ساخته می‌شود: must + be + گذشتهٔ فعل: must be recycled. must recycle فاعلِ فعال می‌خواهد، must have recycled دربارهٔ گذشته است و must recycling ساختار ندارد.',
  },
  {
    lessonId: 'b1.formal-emails.lesson-01',
    question: 'If the recipient ______ your tone too formal, the enquiry would be read as a complaint.',
    questionFa: 'شرطی نوع دوم: جملهٔ if چه زمانی می‌گیرد؟',
    options: ['found', 'finds', 'will find', 'has found'],
    explanationFa: 'نتیجهٔ جمله with would be read آمده، پس شرطی نوع دوم است و جملهٔ if گذشتهٔ ساده می‌گیرد: found. finds و has found شرطی نوع اول می‌سازند و will find در جملهٔ if نمی‌آید.',
  },
  {
    lessonId: 'b1.friendship.lesson-01',
    question: 'We ______ neighbours for ten years, and she has never once let me down.',
    questionFa: 'با for ten years کدام زمان می‌آید؟',
    options: ['have been', 'are', 'were', 'had been'],
    explanationFa: 'بازه‌ای که تا حالا ادامه دارد حال کامل می‌خواهد: have been … for ten years. are و were بازهٔ گذشته را نشان نمی‌دهند و had been به گذشتهٔ پیش از گذشتهٔ دیگری اشاره دارد که اینجا نیست.',
  },
  {
    lessonId: 'b1.goals.lesson-01',
    question: 'If you ______ your long-term goal as a series of milestones, it stops feeling impossible.',
    questionFa: 'شرطی نوع اول: صفر یا واقعی؛ کدام فعل در جملهٔ if؟',
    options: ['see', 'will see', 'saw', 'had seen'],
    explanationFa: 'شرطی نوع اول در جملهٔ if حال ساده دارد: If you see … . will see در جملهٔ if نمی‌آید و saw و had seen شرط را به گذشته می‌برند.',
  },
  {
    lessonId: 'b1.meetings.lesson-01',
    question: 'You had better ______ the minutes before the next meeting, or nobody will remember the vote.',
    questionFa: 'had better چه شکلی از فعل می‌گیرد؟',
    options: ['read', 'to read', 'reading', 'have read'],
    explanationFa: 'had better + فعل ساده: had better read. to read و reading نادرست‌اند و have read جمله را به گذشتهٔ کامل می‌برد، در حالی که هنوز جلسهٔ بعدی نیامده است.',
  },
  {
    lessonId: 'b1.new-skills.lesson-01',
    question: 'If you ______ every day, you will see progress by the end of the course.',
    questionFa: 'شرطی نوع اول: کدام صورت در جای خالی؟',
    options: ['practise', 'will practise', 'practised', 'had practised'],
    explanationFa: 'در شرطی نوع اول، جملهٔ if حال ساده است و will فقط در جملهٔ اصلی می‌آید: If you practise …, you will see … . سه گزینهٔ دیگر یا will را به جملهٔ if می‌برند یا شرط را گذشته می‌کنند.',
  },
  {
    lessonId: 'b1.personal-background.lesson-01',
    question: 'I ______ experience in this field since I graduated, so the qualification is only part of my background.',
    questionFa: 'با since + گذشته، کدام زمان می‌آید؟',
    options: ['have gained', 'gain', 'gained', 'had gained'],
    explanationFa: 'since I graduated نقطهٔ شروع در گذشته است و تا حالا ادامه دارد، پس حال کامل: have gained. gain و gained این ادامه را نشان نمی‌دهند و had gained به گذشتهٔ پیش از گذشته اشاره دارد.',
  },
  {
    lessonId: 'b1.presentation.lesson-01',
    question: 'I ______ the slides twice tonight, because the overview is still too long.',
    questionFa: 'بیان قصد برای امشب: be going to یا صورت‌های ناقص؟',
    options: ['am going to rehearse', 'go to rehearse', 'am rehearse', 'going rehearse'],
    explanationFa: 'قصدی که پیش از این تصمیم گرفته شده با be going to + فعل ساده می‌آید: am going to rehearse. سه گزینهٔ دیگر be یا to را جا می‌اندازند و ساختار ناقص می‌سازند.',
  },
  {
    lessonId: 'b1.social-media.lesson-01',
    question: 'She ______ that post three times since Monday, and it has gone viral.',
    questionFa: 'با since Monday کدام زمان؟',
    options: ['has shared', 'shared', 'shares', 'had shared'],
    explanationFa: 'since Monday تا حالا ادامه دارد و نتیجه‌اش دیده می‌شود، پس حال کامل: has shared. shared زمان مشخص گذشته می‌خواهد و had shared پیش از گذشتهٔ دیگری است.',
  },
  {
    lessonId: 'b1.storytelling.lesson-01',
    question: 'By the time we reached the ending, the main character ______, and the plot still made no sense.',
    questionFa: 'پیش از رویدادی در گذشته: کدام زمان؟',
    options: ['had already changed', 'has already changed', 'already changed', 'was already changing'],
    explanationFa: 'کاری که پیش از reached رخ داده بود گذشتهٔ کامل می‌خواهد: had already changed. has already changed به حالا وصل می‌شود و already changed ترتیب دو رویداد را نشان نمی‌دهد.',
  },
  {
    lessonId: 'b1.technology.lesson-01',
    question: 'The battery is at five per cent, so I ______ the device before the update.',
    questionFa: 'قصد: be going to در برابر صورت‌های ناقص.',
    options: ['am going to charge', 'am charge', 'going to charge', 'charge to'],
    explanationFa: 'be going to + فعل ساده قصد را می‌گوید: am going to charge. am charge و going to charge فعل کمکی را جا می‌اندازند و charge to ترتیب را برمی‌گرداند.',
  },
  {
    lessonId: 'b1.time-management.lesson-01',
    question: 'The deadline was yesterday, so you ______ the urgent task before you left.',
    questionFa: 'سرزنش دربارهٔ کارِ انجام‌نشده در گذشته: کدام ساختار؟',
    options: ['should have finished', 'must have finished', 'needn\u2019t have finished', 'should finish'],
    explanationFa: 'should have + گذشتهٔ فعل سرزنش است: کار انجام نشده و باید انجام می‌شد. must have finished یعنی «حتماً تمام کرده‌ای» (استنباط) و needn\u2019t have finished یعنی «لازم نبود»، که هر دو با «مهلت دیروز» در تناقض‌اند؛ should finish به آینده اشاره دارد.',
  },
  {
    lessonId: 'b1.travel-problems.lesson-01',
    question: 'We ______ for the connection when the airline cancelled the flight.',
    questionFa: 'کاری در جریان که رویدادی آن را قطع کرد: کدام زمان؟',
    options: ['were waiting', 'waited', 'have waited', 'had waiting'],
    explanationFa: 'کاری که در جریان بود و رویداد دیگری آن را قطع کرد گذشتهٔ استمراری است: were waiting … when … cancelled. waited ترتیب را از دست می‌دهد، have waited به حالا وصل است و had waiting ساختار ندارد.',
  },
  {
    lessonId: 'b1.university.lesson-01',
    question: 'Every assignment ______ by two tutors before the end of term.',
    questionFa: 'مجهول ساده با فاعلِ غیرشخصی: کدام صورت؟',
    options: ['is marked', 'marks', 'is marking', 'has marking'],
    explanationFa: 'نمره‌دهی را استادان انجام می‌دهند، نه خودِ تمرین، پس مجهول است: is marked. marks و is marking فعال‌اند (assignment نمی‌تواند نمره بدهد) و has marking ساختار ندارد.',
  },
  // ── B2: deduction, wish, perfect, agreement, relatives, passives, conditionals ──────────
  {
    lessonId: 'b2.ai.lesson-01',
    question: 'The output is far too accurate for that tiny dataset, so it ______ trained on data nobody disclosed.',
    questionFa: 'استنباط قطعی دربارهٔ گذشته: must یا can\u2019t؟',
    options: ['must have been', 'must be', 'can\u2019t have been', 'should have been'],
    explanationFa: '«too accurate» شاهدِ قطعی است، پس استنباط مثبت دربارهٔ گذشته: must have been trained. can\u2019t have been خلاف شاهد است، must be دربارهٔ حالا و should have been نصیحت.',
  },
  {
    lessonId: 'b2.career-decisions.lesson-01',
    question: 'I wish I ______ the notice period before I signed; the appraisal is next week.',
    questionFa: 'افسوس دربارهٔ گذشته: wish با کدام زمان؟',
    options: ['had checked', 'checked', 'have checked', 'would check'],
    explanationFa: 'wish + گذشتهٔ کامل افسوس دربارهٔ کاری در گذشته است: I wish I had checked. checked به حال اشاره می‌کند و would check آرزوی تغییر رفتار دیگری است، نه کارِ خودِ گوینده در گذشته.',
  },
  {
    lessonId: 'b2.crisis.lesson-01',
    question: 'The spokesperson had barely finished the statement ______ the first questions came.',
    questionFa: 'ساختار had barely … ; کدام حرف ربط؟',
    options: ['when', 'than', 'that', 'then'],
    explanationFa: 'الگوی had barely … when … یعنی «به‌سختی … که …». than با مقایسه می‌آید، that جملهٔ پیرو نمی‌سازد و then قید است نه ربط.',
  },
  {
    lessonId: 'b2.economy.lesson-01',
    question: 'Neither the tariff nor the currency controls ______ enough to stop the recession.',
    questionFa: 'مطابقت فعل با neither … nor: فعل با کدام اسم هماهنگ می‌شود؟',
    options: ['were', 'was', 'has', 'is'],
    explanationFa: 'در neither … nor فعل با نزدیک‌ترین اسم هماهنگ می‌شود و اینجا controls جمع است: were. was و is با جمع نمی‌خوانند و has هم به سوم‌شخص مفرد نیاز دارد.',
  },
  {
    lessonId: 'b2.entrepreneurship.lesson-01',
    question: 'The prototype ______ we used in the pitch leaked the revenue figures.',
    questionFa: 'ضمیر نسبی برای شیء: کدام‌ها درست‌اند و کدام در این جمله؟',
    options: ['that', 'who', 'whose', 'what'],
    explanationFa: 'برای شیء هم that و هم which به‌کار می‌رود و اینجا only that آمده است. who برای انسان است، whose مالکیت می‌خواهد و what پس از اسم نمی‌آید.',
  },
  {
    lessonId: 'b2.ethics.lesson-01',
    question: 'The disclosure stayed confidential, so the team ______ to say nothing until the review was over.',
    questionFa: 'الزام در گذشته: کدام صورت با to می‌آید؟',
    options: ['had', 'must', 'should', 'need'],
    explanationFa: 'had to + فعل ساده الزام در گذشته است: had to say nothing. must و should با to نمی‌آیند و need بدون to در جملهٔ مثبت نادرست است.',
  },
  {
    lessonId: 'b2.finance.lesson-01',
    question: 'If only we ______ the dividend instead of reinvesting it, the portfolio would not be so exposed.',
    questionFa: 'if only برای افسوس گذشته: کدام زمان؟',
    options: ['had taken', 'took', 'have taken', 'would take'],
    explanationFa: 'if only + گذشتهٔ کامل افسوس دربارهٔ گذشته است و نتیجه‌اش would not be: If only we had taken. took به حالا اشاره می‌کند و have taken با if only نمی‌آید.',
  },
  {
    lessonId: 'b2.idioms.lesson-01',
    question: 'It was ______ unusual thing to say in that office, but he meant every word.',
    questionFa: 'حرف تعریف پیش از صدای واکه‌ای: کدام؟',
    options: ['an', 'a', 'the', 'some'],
    explanationFa: 'unusual با صدای واکه آغاز می‌شود، پس an: an unusual thing. a با صدای صامت می‌آید، the شنوندهٔ آشنا می‌خواهد و some با اسم مفردِ قابل‌شمار این‌جا نمی‌نشیند.',
  },
  {
    lessonId: 'b2.intercultural.lesson-01',
    question: 'She asked me ______ the small talk was expected before the meeting.',
    questionFa: 'نقل سؤال بله/خیر: کدام ربط؟',
    options: ['whether', 'that', 'if that', 'was'],
    explanationFa: 'سؤال بله/خیر در نقل قول with whether (یا if) می‌آید و ترتیب جمله خبری می‌شود: whether the small talk was expected. that خبر را نقل می‌کند نه پرسش را، if that دو ربط را روی هم می‌گذارد و was جای فعل اصلی را می‌گیرد.',
  },
  {
    lessonId: 'b2.media-literacy.lesson-01',
    question: 'That headline ______ as satire, not as news: the outlet has admitted it was misleading.',
    questionFa: 'مجهول با وجهی: کدام ترتیب درست است؟',
    options: ['should be read', 'should read', 'should have read', 'should be reading'],
    explanationFa: 'مجهول با should چنین است: should be read یعنی «باید طنز خوانده شود». should read فاعلِ فعال می‌خواهد، should have read گذشته را سرزنش می‌کند و should be reading استمرارِ بی‌دلیل است.',
  },
  {
    lessonId: 'b2.mental-health.lesson-01',
    question: 'You had better ______ to someone about the burnout before the insomnia gets worse.',
    questionFa: 'had better چه شکلی از فعل می‌گیرد؟',
    options: ['talk', 'to talk', 'talking', 'talked'],
    explanationFa: 'had better + فعل ساده: had better talk. to talk و talking ساختار را می‌شکنند و talked گذشته است، در حالی که هشدار دربارهٔ آیندهٔ نزدیک است.',
  },
  {
    lessonId: 'b2.negotiation.lesson-01',
    question: 'Were we to offer a concession now, we ______ all our leverage in the next round.',
    questionFa: 'شرطی خیالیِ معکوس: جملهٔ اصلی چه صورتی می‌گیرد؟',
    options: ['would lose', 'will lose', 'lost', 'had lost'],
    explanationFa: 'Were we to offer شرطی خیالی است (به‌جای If we offered)، پس جملهٔ اصلی would + فعل ساده می‌گیرد: would lose. will lose شرط را واقعی می‌کند و lost و had lost زمان را می‌شکنند.',
  },
  {
    lessonId: 'b2.persuasion.lesson-01',
    question: 'The more a testimonial looks like an advert, ______ it appeals to the target consumer.',
    questionFa: 'الگوی the more … the less: کدام گزینه؟',
    options: ['the less', 'less', 'the least', 'lesser'],
    explanationFa: 'الگوی موازی the more … the less … است و هر دو نیمه with the می‌آیند: the less it appeals. less بدون the ناقص است، the least برترین است نه موازی، و lesser صفت است نه قید.',
  },
  {
    lessonId: 'b2.phrasal-verbs.lesson-01',
    question: 'They suggested ______ the complaint before the takeover, but nobody carried it out.',
    questionFa: 'suggest با کدام شکل فعل می‌آید؟',
    options: ['looking into', 'to look into', 'look into', 'looked into'],
    explanationFa: 'suggest + -ing می‌گیرد: suggested looking into. to look into و look into ساختار را می‌شکنند و looked into جمله را گذشته و ناقص می‌کند.',
  },
  {
    lessonId: 'b2.presentations.lesson-01',
    question: 'The outline, ______ was prepared by an attendee, had no structure at all.',
    questionFa: 'بند نسبیِ غیرتعیین‌کننده: کدام ضمیر؟',
    options: ['which', 'that', 'who', 'what'],
    explanationFa: 'در بند غیرتعیین‌کننده (پس از ویرگول) that نمی‌آید و ضمیر which است: The outline, which was prepared … . that فقط در بند تعیین‌کننده می‌آید، who برای انسان است و what پس از اسم نمی‌نشیند.',
  },
  {
    lessonId: 'b2.privacy.lesson-01',
    question: 'Since the breach, every request ______ and no tracking script has been allowed to run.',
    questionFa: 'مجهول حال کامل با since: کدام صورت؟',
    options: ['has been verified', 'was verified', 'is verified', 'has verified'],
    explanationFa: 'since the breach بازه‌ای تا حالاست و کار روی خودِ request انجام شده، پس حال کامل مجهول: has been verified. was verified و is verified بازه را از دست می‌دهند و has verified فعال است.',
  },
  {
    lessonId: 'b2.projects.lesson-01',
    question: 'The scope ______ twice since the estimate, and the timeline has not moved.',
    questionFa: 'حال کامل مجهول با since: کدام؟',
    options: ['has been changed', 'was changed', 'has changed', 'is changed'],
    explanationFa: 'دامنه را کسی تغییر داده و بازه تا حالاست: has been changed. was changed و is changed بازه را نشان نمی‌دهند و has changed فعال است (دامنه خودش تغییر نمی‌کند).',
  },
  {
    lessonId: 'b2.renewables.lesson-01',
    question: '______ the subsidy withdrawn, the solar grid would collapse within a year.',
    questionFa: 'شرطی خیالیِ معکوس بدون if: کدام فعل کمکی؟',
    options: ['Were', 'Was', 'Had', 'Should'],
    explanationFa: 'شرطی خیالی را می‌توان با معکوس‌سازی نوشت: Were the subsidy withdrawn = If the subsidy were withdrawn. متن با would collapse آمده، پس Were درست است؛ Had گذشتهٔ کاملِ معکوس می‌سازد و Should احتمالی است، که هیچ‌کدام با would collapse جفت نمی‌شوند.',
  },
  {
    lessonId: 'b2.reports.lesson-01',
    question: 'Each recommendation ______ by the reviewer before the draft is circulated.',
    questionFa: 'مجهول ساده: کدام صورت با each recommendation؟',
    options: ['is checked', 'checks', 'is checking', 'has checking'],
    explanationFa: 'بررسی را بازبین انجام می‌دهد، پس مجهول است و each مفرد فعل مفرد می‌گیرد: is checked. checks و is checking فعال‌اند و has checking ساختار ندارد.',
  },
  {
    lessonId: 'b2.review.lesson-01',
    question: 'The critic ______ review gave the film a low rating missed the twist in the sequel\u2019s screenplay.',
    questionFa: 'ضمیر نسبیِ مالکیت: کدام؟',
    options: ['whose', 'who', 'which', 'that'],
    explanationFa: 'review متعلق به critic است، پس ضمیر مالکیت نسبی whose: the critic whose review … . who فقط برای شخص به‌عنوان فاعل است، which و that مالکیت نمی‌رسانند.',
  },
  {
    lessonId: 'b2.strategy.lesson-01',
    question: 'Every initiative ______ against the same benchmark, or the roadmap means nothing.',
    questionFa: 'مجهول با وجهی: کدام ترتیب؟',
    options: ['must be evaluated', 'must evaluate', 'must have evaluated', 'must be evaluating'],
    explanationFa: 'initiative را دیگران ارزیابی می‌کنند، پس مجهول با وجهی: must be evaluated. must evaluate فاعلِ فعال می‌خواهد، must have evaluated دربارهٔ گذشته است و must be evaluating یعنی «در حال ارزیابی بودن».',
  },
  {
    lessonId: 'b2.workplace-conflict.lesson-01',
    question: 'She wishes she ______ neutral in the mediation instead of taking sides.',
    questionFa: 'wish دربارهٔ گذشته: کدام زمان؟',
    options: ['had stayed', 'stayed', 'has stayed', 'would stay'],
    explanationFa: 'wish + گذشتهٔ کامل افسوس دربارهٔ گذشته است: she wishes she had stayed. stayed به حال اشاره می‌کند، has stayed با wish نمی‌آید و would stay آرزوی رفتار دیگری است.',
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
    difficulty: 3,
    question: item.question,
    questionFa: item.questionFa,
    options: item.options,
    correctIndex: 0,
    skill: 'GRAMMAR',
    explanationFa: item.explanationFa,
  };

  // After the lesson's last item, so the authored order still reads 01..n.
  const lastIndex = bundle.exercises.findLastIndex((e) => e.lessonId === item.lessonId);
  bundle.exercises.splice(lastIndex + 1, 0, exercise);
  added.push(id);
}

writeFileSync(exercisesPath, `${JSON.stringify(bundle, null, 2)}\n`);
console.log(`added ${added.length} hard-end choice items`);
for (const id of added) console.log(`  ${id}`);
