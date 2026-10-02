# English Learning App — Methodology & Curriculum Specification

## 1. هدف پروژه

هدف این پروژه ساخت یک اپلیکیشن اندرویدی آموزش زبان انگلیسی است که به‌جای ارائه مجموعه‌ای از فلش‌کارت، گرامر و تست، یک **سیستم یادگیری زبان** ایجاد کند.

اصول اصلی:

- آموزش مرحله‌ای بر اساس CEFR
- تمرکز هم‌زمان بر چهار مهارت اصلی
- آموزش Vocabulary در بستر جمله و موقعیت
- Retrieval Practice
- Spaced Repetition
- آموزش Grammar در بستر کاربرد
- Listening از سطح کنترل‌شده تا زبان طبیعی
- Speaking از تکرار تا تولید آزاد
- تشخیص نقاط ضعف کاربر
- مسیر یادگیری شخصی‌سازی‌شده
- تفکیک «اتمام درس» از «تسلط واقعی»

---

# 2. چارچوب سطح‌بندی

چارچوب اصلی دوره CEFR است:

| سطح | هدف کلی |
|---|---|
| A1 | ارتباط بسیار پایه در موقعیت‌های روزمره |
| A2 | ارتباط ساده در موضوعات آشنا |
| B1 | ارتباط مستقل در موقعیت‌های معمول |
| B2 | ارتباط نسبتاً روان و دقیق |
| C1 | استفاده پیشرفته و انعطاف‌پذیر |
| C2 | تسلط بسیار بالا |

هر سطح باید به مهارت‌های زیر شکسته شود:

- Vocabulary
- Grammar
- Listening
- Speaking
- Reading
- Writing
- Pronunciation
- Fluency

---

# 3. مدل آموزشی Four Strands

ساختار آموزشی دوره بر چهار حوزه متعادل بنا می‌شود:

## 3.1 Meaning-focused Input

دریافت معنادار زبان:

- Listening
- Reading
- Dialogue
- Story
- محتوای سطح‌بندی‌شده

هدف: فهم پیام، نه ترجمه تک‌تک کلمات.

## 3.2 Meaning-focused Output

تولید معنادار:

- Speaking
- Writing
- پاسخ به سؤال
- ساخت جمله
- مکالمه

هدف: استفاده فعال از زبان.

## 3.3 Language-focused Learning

یادگیری مستقیم:

- Vocabulary
- Grammar
- Pronunciation
- Spelling
- Collocations
- Word Families

## 3.4 Fluency Development

افزایش سرعت و روانی:

- Repetition
- Timed Activities
- Conversation Drills
- Reading Fluency
- Speaking Fluency

---

# 4. اصل مرکزی محصول

> هدف اپلیکیشن نباید «تمام کردن درس» باشد؛ هدف باید «توانایی استفاده از زبان» باشد.

بنابراین:

```text
Lesson Completion != Mastery
```

تسلط باید پس از چند مرحله بازیابی و استفاده مجدد ارزیابی شود.

---

# 5. ساختار هر درس

هر Lesson می‌تواند ساختار زیر را داشته باشد:

```text
Lesson
│
├── Warm-up
├── Vocabulary
├── Listening
├── Comprehension
├── Grammar
├── Pronunciation
├── Speaking
├── Reading
├── Writing
├── Fluency
└── Review
```

## 5.1 Warm-up

فعال‌سازی دانش قبلی کاربر.

مثال:

```text
What do you usually do in the morning?
```

---

# 6. Vocabulary System

Vocabulary باید در بستر استفاده آموزش داده شود.

## اشتباه

```text
apple = سیب
```

## روش پیشنهادی

```text
apple

Pronunciation:
 /ˈæpəl/

Meaning:
سیب

Example:
I eat an apple every morning.

Related:
apple juice
green apple
apples
```

هر واژه باید تا حد امکان دارای این اطلاعات باشد:

- Word
- Translation
- Definition
- Pronunciation
- Audio
- Example
- Part of Speech
- Word Forms
- Related Words
- Collocations
- Difficulty
- CEFR Level
- Review State

---

# 7. Retrieval Practice

کاربر نباید فقط پاسخ را ببیند.

یک واژه باید از مسیرهای مختلف بازیابی شود.

## Example

### English → Persian

```text
expensive
```

کاربر:

```text
گران
```

### Persian → English

```text
گران
```

کاربر:

```text
expensive
```

### Context

```text
This phone is very ______.
```

کاربر:

```text
expensive
```

### Listening

کاربر جمله را می‌شنود و کلمه را تشخیص می‌دهد.

### Production

```text
Make a sentence with "expensive".
```

---

# 8. Spaced Repetition

سیستم مرور باید بر اساس عملکرد کاربر کار کند.

نمونه اولیه:

```text
New
 ↓
Learning
 ↓
Review
 ↓
Known
 ↓
Long-term retention
```

فاصله‌های نمونه:

```text
Day 0
Day 1
Day 3
Day 7
Day 14
Day 30
Day 60
```

این مقادیر باید در نسخه نهایی بر اساس عملکرد کاربر تنظیم شوند.

## داده‌های مورد نیاز

```text
item_id
user_id
last_review
next_review
interval
ease
stability
difficulty
correct_count
wrong_count
```

---

# 9. Grammar

Grammar نباید فقط به فرمول تبدیل شود.

## روش پیشنهادی

### Context

```text
I have finished my work.
```

### Explanation

توضیح کوتاه و قابل فهم.

### Controlled Practice

```text
I ______ my homework.

A) finish
B) finished
C) have finished
```

### Transformation

```text
I have finished my work.
```

کاربر جمله مشابه می‌سازد.

### Production

```text
Say something you have already done today.
```

---

# 10. Listening System

Listening باید مرحله‌ای باشد.

## Level 1

صدای واضح و جمله کوتاه.

## Level 2

سرعت طبیعی‌تر.

## Level 3

جمله داخل مکالمه.

## Level 4

بدون متن.

## Level 5

سؤال مفهومی.

## Level 6

Dictation.

مثال:

```text
Audio:
Where are you going?

Task:
Type what you hear.
```

---

# 11. Speaking System

Speaking باید از مراحل ساده شروع شود:

```text
Listen
 ↓
Repeat
 ↓
Read Aloud
 ↓
Answer
 ↓
Free Speaking
```

معیارهای احتمالی ارزیابی:

- Pronunciation
- Fluency
- Accuracy
- Vocabulary
- Grammar
- Completeness

نکته مهم:

> پاسخ آزاد نباید صرفاً با یک جمله مرجع مقایسه شود.

یک پاسخ می‌تواند چند شکل صحیح داشته باشد.

---

# 12. Pronunciation

برای Pronunciation می‌توان موارد زیر را آموزش داد:

- Phonemes
- Word Stress
- Sentence Stress
- Intonation
- Connected Speech
- Minimal Pairs

مثال:

```text
ship / sheep

ship
sheep
```

کاربر باید تفاوت شنیداری و گفتاری را تمرین کند.

---

# 13. Reading

سه نوع Reading:

## Extensive Reading

متن‌های ساده و نسبتاً طولانی.

هدف:

```text
Understand the message
```

## Intensive Reading

بررسی:

- Vocabulary
- Grammar
- Expressions

## Assisted Reading

کاربر روی کلمه می‌زند و اطلاعات آن را می‌بیند.

---

# 14. Writing

Writing باید مرحله‌ای باشد.

```text
Word
 ↓
Sentence
 ↓
Connected Sentences
 ↓
Paragraph
 ↓
Short Text
```

مثال:

### Level 1

```text
Write one sentence about your family.
```

### Level 2

```text
Write three sentences about your daily routine.
```

### Level 3

```text
Describe your last weekend.
```

---

# 15. Fluency

هدف Fluency استفاده سریع‌تر از مطالب یادگرفته‌شده است.

مثال:

```text
I like coffee.
```

بعد:

```text
I like coffee because it helps me wake up.
```

و سپس:

```text
I usually drink coffee in the morning before I go to work.
```

تمرین‌های Fluency:

- Timed Speaking
- Timed Reading
- Repetition
- Sentence Expansion
- Quick Response
- Conversation Drills

---

# 16. Lesson Example

## Topic: At the Airport

### Vocabulary

```text
passport
boarding pass
flight
departure
arrival
gate
luggage
```

### Dialogue

```text
A: Excuse me, where is gate 12?

B: It's over there, next to the coffee shop.
```

### Listening

کاربر مکالمه را گوش می‌دهد.

### Vocabulary Practice

واژه‌های کلیدی استخراج می‌شوند.

### Grammar

مثلاً:

```text
Where is...?
Where are...?
```

### Speaking

```text
Ask where gate 12 is.
```

### Writing

```text
Write a short airport conversation.
```

### Review

موارد ضعیف وارد Review Queue می‌شوند.

---

# 17. Adaptive Learning

برای هر کاربر یک Learning Profile نگهداری شود.

نمونه:

```text
Vocabulary      72%
Grammar         58%
Listening       41%
Reading         76%
Speaking        37%
Pronunciation   52%
Fluency         34%
```

سیستم باید بر اساس عملکرد، تمرین‌های آینده را تنظیم کند.

مثلاً:

```text
Listening ↓
Speaking ↓
```

پس سهم Listening و Speaking در جلسات بعدی افزایش یابد.

---

# 18. Placement Test

در اولین ورود، کاربر می‌تواند آزمون تعیین سطح بدهد.

آزمون بهتر است شامل:

- Vocabulary
- Grammar
- Reading
- Listening
- در صورت امکان Speaking

خروجی:

```text
Estimated Level: A2
```

اما سطح باید به تفکیک مهارت نیز ذخیره شود:

```text
Vocabulary: A2
Grammar: A1
Reading: A2
Listening: A1
```

---

# 19. Progress System

پیشرفت فقط یک درصد کلی نباشد.

```text
Vocabulary
████████░░ 80%

Listening
██████░░░░ 60%

Speaking
█████░░░░░ 50%

Grammar
███████░░░ 70%

Fluency
████░░░░░░ 40%
```

همچنین:

- Lessons Completed
- Words Learned
- Words Retained
- Listening Minutes
- Speaking Minutes
- Writing Tasks
- Review Accuracy
- Current CEFR Estimate

ثبت شود.

---

# 20. Daily Learning

صفحه Home باید به جای نمایش تعداد زیادی قابلیت، مسیر امروز را مشخص کند.

نمونه:

```text
TODAY

🎯 Daily Goal
15 minutes

📖 Today's Lesson
At the Airport

▶ Start

🔁 Review
18 items

▶ Review

🎧 Listening
5 minutes

▶ Continue

🔥 7 day streak
```

---

# 21. Review Queue

سیستم باید هر روز یک صف مرور تولید کند:

```text
Due Reviews
├── Vocabulary
├── Grammar
├── Listening
├── Pronunciation
└── Phrases
```

موارد ضعیف باید با اولویت بیشتری بازگردند.

---

# 22. MVP پیشنهادی

نسخه اول بهتر است فقط A1 و A2 را پوشش دهد.

## امکانات MVP

- Placement Test
- A1 Curriculum
- A2 Curriculum
- Vocabulary
- Grammar
- Listening
- Reading
- Basic Speaking
- Pronunciation
- Spaced Repetition
- Daily Lesson
- Review Queue
- Progress
- Offline Content

بعد از اعتبارسنجی:

```text
A1
 ↓
A2
 ↓
B1
 ↓
B2
 ↓
C1
 ↓
C2
```

---

# 23. معماری محتوای آموزشی

پیشنهاد برای ساختار داده:

```text
Course
 └── Level
      └── Unit
           └── Lesson
                ├── Vocabulary
                ├── Grammar
                ├── Dialogue
                ├── Listening
                ├── Reading
                ├── Speaking
                ├── Writing
                └── Exercises
```

---

# 24. مدل داده Vocabulary

نمونه:

```json
{
  "id": "word_001",
  "word": "expensive",
  "translation": "گران",
  "level": "A2",
  "partOfSpeech": "adjective",
  "pronunciation": "/ɪkˈspensɪv/",
  "audio": "expensive.mp3",
  "examples": [
    "This phone is expensive."
  ],
  "collocations": [
    "very expensive",
    "too expensive"
  ]
}
```

---

# 25. مدل داده Lesson

```json
{
  "id": "lesson_001",
  "level": "A2",
  "title": "At the Airport",
  "topic": "Travel",
  "vocabulary": [],
  "grammar": [],
  "dialogue": [],
  "listening": [],
  "reading": [],
  "speaking": [],
  "writing": [],
  "exercises": []
}
```

---

# 26. Learning Engine

هسته نرم‌افزار:

```text
Learning Engine
│
├── Placement Engine
├── Curriculum Engine
├── Review Engine
├── Spaced Repetition
├── Retrieval Engine
├── Difficulty Engine
├── Weakness Detection
├── Recommendation Engine
└── Progress Engine
```

---

# 27. الگوریتم تصمیم‌گیری روزانه

نمونه منطق:

```text
1. دریافت Reviewهای سررسید شده
2. بررسی نقاط ضعف
3. بررسی Lesson فعلی
4. انتخاب تمرین
5. اجرای تمرین
6. ثبت نتیجه
7. بروزرسانی mastery
8. محاسبه Review بعدی
9. بروزرسانی Learning Profile
```

---

# 28. Offline-first

برای اپ اندروید، محتوا بهتر است تا حد امکان Offline-first طراحی شود.

کاربر بتواند بدون اینترنت:

- درس بخواند
- Vocabulary مرور کند
- Listening انجام دهد
- تمرین Grammar انجام دهد
- Progress را مشاهده کند

قابلیت‌هایی که به اینترنت نیاز دارند:

- AI Speaking
- Cloud Sync
- دریافت محتوای جدید
- به‌روزرسانی مدل‌ها

---

# 29. ساختار پیشنهادی Android

پیشنهاد اولیه:

```text
app/
├── core/
│   ├── database/
│   ├── datastore/
│   ├── network/
│   ├── audio/
│   └── common/
│
├── feature/
│   ├── onboarding/
│   ├── placement/
│   ├── home/
│   ├── lesson/
│   ├── vocabulary/
│   ├── grammar/
│   ├── listening/
│   ├── speaking/
│   ├── reading/
│   ├── writing/
│   ├── review/
│   └── progress/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
└── data/
    ├── local/
    ├── remote/
    └── repository/
```

---

# 30. اصول UX

Home نباید شلوغ باشد.

کاربر باید در چند ثانیه بفهمد:

1. امروز چه کاری دارد؟
2. چه چیزی را باید مرور کند؟
3. در چه سطحی است؟
4. چقدر پیشرفت کرده؟

اصول:

- Minimal
- Clear
- Material Design
- Progressive Disclosure
- RTL برای رابط فارسی
- محتوای انگلیسی با LTR
- Typography مناسب فارسی و انگلیسی
- Feedback فوری
- Animation محدود و کاربردی

---

# 31. Gamification

Gamification باید از یادگیری پشتیبانی کند، نه جایگزین آن.

قابلیت‌های مناسب:

- XP
- Streak
- Daily Goal
- Achievement
- Level Progress
- Weekly Progress

اما نباید معیار اصلی موفقیت باشد.

معیار اصلی:

```text
Retention
+
Comprehension
+
Production
+
Fluency
```

---

# 32. شاخص‌های واقعی موفقیت

برای ارزیابی محصول:

### Learning Metrics

- Vocabulary Retention
- Delayed Recall
- Listening Accuracy
- Speaking Accuracy
- Grammar Accuracy
- Reading Comprehension
- Writing Accuracy
- Fluency

### Product Metrics

- Daily Active Learners
- Weekly Active Learners
- Lesson Completion
- Review Completion
- Retention
- Session Duration
- Learning Streak

---

# 33. اشتباهاتی که نباید در محصول رخ دهد

## 1. تمرکز بیش از حد بر ترجمه

زبان باید در Context آموزش داده شود.

## 2. گرامر به شکل کتاب درسی

Grammar باید به Communication متصل باشد.

## 3. فلش‌کارت بدون Retrieval

صرفاً دیدن دوباره کلمه یادگیری واقعی نیست.

## 4. نبود مرور فاصله‌دار

بدون مرور، بخش زیادی از یادگیری فراموش می‌شود.

## 5. حذف Speaking

کاربر باید از ابتدا تولید زبان داشته باشد.

## 6. Listening مصنوعی

باید از Controlled English به Natural English حرکت کرد.

## 7. یک مسیر یکسان برای همه

عملکرد کاربران متفاوت است.

## 8. نمایش درصدهای غیرواقعی

Completion نباید معادل Mastery باشد.

---

# 34. نسخه پیشنهادی محصول

## Phase 1 — Foundation

- Content Model
- CEFR A1
- Vocabulary Engine
- Lesson Engine
- Review Engine
- Progress

## Phase 2 — Core Skills

- Grammar
- Listening
- Reading
- Pronunciation

## Phase 3 — Production

- Speaking
- Writing
- Fluency

## Phase 4 — Personalization

- Placement Test
- Weakness Detection
- Adaptive Learning
- Recommendation Engine

## Phase 5 — Advanced

- B1/B2
- AI Conversation
- Advanced Speaking
- Advanced Writing
- C1/C2

---

# 35. اصل نهایی طراحی

محصول باید از این مدل:

```text
Content
 ↓
User consumes content
 ↓
Quiz
 ↓
Next lesson
```

به این مدل حرکت کند:

```text
Assess
 ↓
Learn
 ↓
Retrieve
 ↓
Use
 ↓
Review
 ↓
Measure
 ↓
Adapt
 ↓
Use again
```

این چرخه باید هسته اصلی اپلیکیشن باشد.

---

# منابع و مبانی علمی

- Council of Europe — Common European Framework of Reference for Languages (CEFR) and Companion Volume
- Paul Nation — Four Strands of a Language Course
- Research on spaced practice in second-language learning
- Research on retrieval practice and vocabulary learning
- Research on second-language vocabulary acquisition and contextual learning

