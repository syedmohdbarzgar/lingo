# English Learning App — Design System
## 1. Brand Foundation
### Brand Concept
The visual identity is based on four ideas:
- Structured learning
- Continuous progress
- Simplicity
- Trust
The logo concept combines:
- An open book → learning and knowledge
- A speech bubble → communication and speaking
- A vertical connection → progression from learning to using the language
### Brand Direction
```text
Modern
Minimal
Educational
Friendly
Trustworthy
Progress-oriented
```
---
# 2. Brand Color
## Primary Brand Color
```text
Primary: #2169EF
```
This should be the main recognizable brand color.
### Why Blue?
Blue works well for this product because it communicates:
- Trust
- Clarity
- Technology
- Stability
- Focus
- Education
The color should be used consistently rather than introducing many unrelated accent colors.
---
# 3. Color Palette
## Primary
| Token | HEX | Usage |
|---|---|---|
| Primary 900 | #0F2D6B | Very dark brand surfaces |
| Primary 800 | #123E91 | Dark emphasis |
| Primary 700 | #1856C7 | Dark interactive state |
| Primary 600 | #2169EF | Main brand color |
| Primary 500 | #3D7FF2 | Secondary interactive |
| Primary 400 | #60A5FA | Light brand accent |
| Primary 300 | #93C5FD | Soft accent |
| Primary 200 | #BFDBFE | Borders / selected backgrounds |
| Primary 100 | #E8F2FF | Brand-tinted surface |
| Primary 50 | #F4F8FF | Very subtle background |
---
# 4. Semantic Colors
Semantic colors should communicate state rather than branding.
## Success
```text
Success 700: #15803D
Success 500: #22C55E
Success 100: #DCFCE7
```
Use for:
- Correct answers
- Completed lessons
- Mastered vocabulary
- Positive progress
## Warning
```text
Warning 700: #B45309
Warning 500: #F59E0B
Warning 100: #FEF3C7
```
Use for:
- Review reminders
- Moderate difficulty
- Attention states
## Error
```text
Error 700: #B91C1C
Error 500: #EF4444
Error 100: #FEE2E2
```
Use for:
- Incorrect answers
- Validation errors
- Failed operations
## Info
```text
Info 700: #0369A1
Info 500: #0EA5E9
Info 100: #E0F2FE
```
Use for:
- Educational hints
- Explanations
- Informational messages
---
# 5. Neutral Palette
```text
Neutral 950: #0F172A
Neutral 900: #111827
Neutral 800: #1F2937
Neutral 700: #334155
Neutral 600: #475569
Neutral 500: #64748B
Neutral 400: #94A3B8
Neutral 300: #CBD5E1
Neutral 200: #E2E8F0
Neutral 100: #F1F5F9
Neutral 50:  #F8FAFC
White:       #FFFFFF
```
---
# 6. Light Theme
```text
Background:
#FFFFFF
Surface:
#FFFFFF
Surface Variant:
#F8FAFC
Primary:
#2169EF
Primary Container:
#E8F2FF
Text Primary:
#0F172A
Text Secondary:
#475569
Text Disabled:
#94A3B8
Border:
#E2E8F0
```
---
# 7. Dark Theme
Dark mode should not simply invert the light theme.
```text
Background:
#0B1220
Surface:
#111827
Surface Elevated:
#172033
Primary:
#60A5FA
Primary Container:
#17366F
Text Primary:
#F8FAFC
Text Secondary:
#CBD5E1
Text Disabled:
#64748B
Border:
#263449
```
The brand identity remains blue, but the primary interactive color becomes lighter for accessibility.
---
# 8. Typography
The application contains both Persian and English content.
Recommended font strategy:
```text
Persian UI:
Vazirmatn
English UI:
Inter / Roboto
```
If a single font family is preferred:
```text
Vazirmatn + system fallback
```
For English learning content, readability has priority over decorative typography.
---
# 9. Type Scale
## Display
```text
Display Large
48sp / Bold
Display Medium
40sp / Bold
Display Small
36sp / Bold
```
## Headline
```text
Headline Large
32sp / Bold
Headline Medium
28sp / Bold
Headline Small
24sp / Bold
```
## Title
```text
Title Large
22sp / SemiBold
Title Medium
18sp / SemiBold
Title Small
16sp / SemiBold
```
## Body
```text
Body Large
16sp / Regular
Body Medium
14sp / Regular
Body Small
12sp / Regular
```
## Label
```text
Label Large
14sp / Medium
Label Medium
12sp / Medium
Label Small
11sp / Medium
```
---
# 10. English Learning Typography
English learning content should be visually distinct from interface text.
Example:
```text
I usually drink coffee in the morning.
```
Use:
```text
24sp
Medium / SemiBold
High line height
LTR
```
Translation:
```text
من معمولاً صبح قهوه می‌نوشم.
```
Use:
```text
15–16sp
Regular
RTL
Secondary color
```
---
# 11. Spacing System
Use an 8dp base grid.
```text
4dp   → Micro
8dp   → Small
12dp  → Compact
16dp  → Standard
20dp  → Medium
24dp  → Large
32dp  → Section
40dp  → Major
48dp  → Hero
64dp  → Page separation
```
Avoid arbitrary spacing values unless required by a specific component.
---
# 12. Corner Radius
Recommended radius tokens:
```text
Radius XS: 4dp
Radius SM: 8dp
Radius MD: 12dp
Radius LG: 16dp
Radius XL: 24dp
Radius Full: 999dp
```
Use:
```text
Cards: 16dp
Buttons: 12dp
Text fields: 12dp
Chips: Full
Bottom sheets: 24dp
Dialog: 24dp
```
---
# 13. Elevation
The design should be mostly flat.
Avoid heavy shadows.
```text
Level 0:
No elevation
Level 1:
1–2dp
Level 2:
3–4dp
Level 3:
6–8dp
```
Primary separation should come from:
- Surface color
- Border
- Spacing
- Typography
rather than shadows.
---
# 14. Iconography
Use a consistent Material-style icon system.
Preferred characteristics:
- Simple
- Geometric
- Recognizable
- 24dp base size
- Consistent stroke weight
Sizes:
```text
16dp → Inline
20dp → Compact
24dp → Standard
32dp → Feature
48dp → Empty state
64dp → Hero
```
Do not mix multiple icon styles.
---
# 15. Buttons
## Primary Button
```text
Background: #2169EF
Text: White
Height: 48dp
Radius: 12dp
Horizontal Padding: 20dp
```
Example:
```text
[ شروع درس ]
```
## Secondary Button
```text
Background: #E8F2FF
Text: #2169EF
```
## Outlined Button
```text
Background: Transparent
Border: #CBD5E1
Text: #2169EF
```
## Text Button
For low-priority actions.
---
# 16. Cards
Cards should communicate learning context.
### Lesson Card
```text
┌─────────────────────────────┐
│ درس امروز                   │
│                             │
│ At the Airport              │
│ سفر و فرودگاه               │
│                             │
│ 15 دقیقه        A2          │
│                             │
│              [ شروع ]       │
└─────────────────────────────┘
```
Properties:
```text
Radius: 16dp
Padding: 16dp
Border: Optional
Elevation: 0–2dp
```
---
# 17. Learning Card
برای نمایش محتوای آموزشی:
```text
┌─────────────────────────────┐
│        expensive            │
│                             │
│       /ɪkˈspensɪv/          │
│                             │
│           🔊                │
│                             │
│            گران             │
│                             │
│ This phone is expensive.    │
└─────────────────────────────┘
```
اصل مهم:
> یک کارت آموزشی نباید بیش از حد اطلاعات را هم‌زمان نمایش دهد.
---
# 18. Exercise UI
تمرین باید تمرکز کاربر را روی یک وظیفه نگه دارد.
```text
Question
     ↓
Answer Area
     ↓
Feedback
     ↓
Next
```
از نمایش هم‌زمان چند سؤال خودداری شود.
---
# 19. Multiple Choice
```text
What does "expensive" mean?
○ Cheap
○ Fast
● Costing a lot
○ New
```
پس از پاسخ:
```text
✓ Correct
```
یا:
```text
Try again
```
Feedback باید کوتاه و واضح باشد.
---
# 20. Fill in the Blank
```text
This phone is very ______.
```
Input:
```text
┌──────────────────────────┐
│                          │
└──────────────────────────┘
             [ بررسی ]
```
---
# 21. Listening UI
```text
          🔊
     0:04 / 0:08
    ━━━━━━━━━━━
[ Replay ]
What did you hear?
```
گزینه نمایش Transcript:
```text
Show transcript
```
نباید همیشه به‌صورت پیش‌فرض متن صوتی نمایش داده شود.
---
# 22. Speaking UI
```text
          🎙
   What did you do today?
       [ Hold to Speak ]
       ━━━━━━━━━━━
   Release when finished
```
پس از پاسخ:
```text
Pronunciation     82%
Fluency            74%
Accuracy           88%
```
امتیازها باید با توضیح کوتاه همراه باشند.
---
# 23. Progress Component
Progress باید چندبعدی باشد.
```text
Vocabulary
████████░░ 80%
Listening
██████░░░░ 60%
Speaking
█████░░░░░ 50%
```
از یک امتیاز کلی به‌عنوان تنها معیار استفاده نشود.
---
# 24. Progress Ring
برای هدف روزانه:
```text
       ╭──────╮
      │  12m  │
      │ /15m  │
       ╰──────╯
```
رنگ:
```text
Primary #2169EF
```
---
# 25. Streak
Streak باید فرعی باشد.
```text
🔥 7 روز
```
Streak نباید بیشتر از Learning Progress در UI برجسته شود.
---
# 26. Review UI
```text
مرور امروز
18 مورد
Vocabulary     12
Grammar         3
Listening       2
Pronunciation   1
[ شروع مرور ]
```
---
# 27. Correct Answer
از Success استفاده شود:
```text
Background:
#DCFCE7
Icon:
Success 700
Text:
Success 700
```
پیام:
```text
✓ درست است
```
---
# 28. Incorrect Answer
```text
Background:
#FEE2E2
Icon:
Error 700
Text:
Error 700
```
پیام:
```text
پاسخ درست:
expensive
```
از رنگ قرمز بسیار تند یا افکت‌های تنبیهی استفاده نشود.
---
# 29. Difficulty Indicator
به جای رنگ‌های زیاد:
```text
Easy
●○○
Medium
●●○
Hard
●●●
```
یا:
```text
آسان
متوسط
سخت
```
---
# 30. CEFR Badge
```text
A1
A2
B1
B2
C1
C2
```
Style:
```text
Background: Primary 100
Text: Primary 700
Radius: Full
Padding: 8dp / 12dp
```
---
# 31. Chips
برای:
- CEFR
- Topic
- Skill
- Grammar category
مثال:
```text
[A2] [Travel] [Listening]
```
---
# 32. Bottom Navigation
حداکثر 4–5 مقصد اصلی.
پیشنهاد:
```text
خانه
یادگیری
مرور
پیشرفت
تنظیمات
```
---
# 33. Home Screen
ساختار:
```text
Home
│
├── Greeting
├── Daily Goal
├── Today's Lesson
├── Review Queue
├── Continue Learning
└── Progress Summary
```
اولویت:
```text
Today's Action
>
Review
>
Continue
>
Statistics
```
---
# 34. Lesson Screen
```text
Top Bar
 ├── Back
 ├── Lesson title
 └── Progress
Content
 └── Current Exercise
Bottom
 └── Primary Action
```
در طول Lesson از عناصر غیرضروری جلوگیری شود.
---
# 35. Onboarding
حداکثر 3–4 صفحه.
### Page 1
```text
Learn English
One step at a time.
```
### Page 2
```text
Practice every day.
```
### Page 3
```text
Build real communication skills.
```
سپس:
```text
[ تعیین سطح ]
```
---
# 36. Placement Test UI
```text
Determine your level
Question 8 / 20
━━━━━━━━━━━━━━
Choose the correct answer.
...
[ Continue ]
```
در طول آزمون نتیجه لحظه‌ای نمایش داده نشود.
---
# 37. Empty States
مثال Review:
```text
🎉
No reviews due
You're all caught up.
```
دکمه:
```text
[ Continue Learning ]
```
Empty State نباید کاربر را سرزنش کند.
---
# 38. Loading States
ترجیح:
```text
Skeleton
```
به‌جای Spinnerهای طولانی.
برای عملیات کوتاه:
```text
Circular Progress
```
---
# 39. Error States
```text
مشکلی پیش آمد
داده آموزشی بارگذاری نشد.
[ تلاش دوباره ]
```
در Offline:
```text
اتصال اینترنت در دسترس نیست.
محتوای ذخیره‌شده همچنان قابل استفاده است.
```
---
# 40. Snackbar
برای پیام‌های کوتاه:
```text
✓ Progress saved
```
یا:
```text
Review scheduled for tomorrow
```
برای خطاهای مهم از Snackbar به‌تنهایی استفاده نشود.
---
# 41. Dialog
برای:
- حذف داده
- خروج از آزمون
- عملیات برگشت‌ناپذیر
استفاده شود.
برای پیام‌های معمولی Dialog نسازید.
---
# 42. Bottom Sheet
برای:
- انتخاب فیلتر
- اطلاعات تکمیلی
- تنظیمات تمرین
- انتخاب گزینه‌های مرتبط
مناسب است.
---
# 43. Motion Design
Animation باید عملکردی باشد.
موارد مناسب:
- Progress transition
- Correct answer feedback
- Card transition
- Screen transition
- Streak celebration
Duration:
```text
Fast: 120–180ms
Normal: 200–300ms
Emphasis: 300–450ms
```
از Animationهای دائمی یا سنگین خودداری شود.
---
# 44. Illustration Style
Illustrationها باید:
- Flat
- Minimal
- Friendly
- Blue-oriented
- بدون جزئیات اضافی
- بدون سایه‌های سنگین
باشند.
رنگ‌های اصلی Illustration:
```text
#2169EF
#60A5FA
#E8F2FF
#0F2D6B
```
رنگ‌های Semantic فقط در صورت نیاز.
---
# 45. App Icon
ساختار پیشنهادی:
```text
Open Book
+
Speech Bubble
```
رنگ اصلی:
```text
#2169EF
```
نسخه Dark:
```text
#60A5FA
+
#111827
```
آیکون باید بدون متن نیز قابل شناسایی باشد.
---
# 46. Design Tokens
توکن‌ها باید در یک لایه مرکزی تعریف شوند.
```text
Color
Typography
Spacing
Radius
Elevation
IconSize
Motion
```
نمونه:
```kotlin
object AppSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}
```
---
# 47. Compose Theme
ساختار:
```text
core/designsystem/
├── color/
│   └── Color.kt
├── typography/
│   └── Type.kt
├── shape/
│   └── Shape.kt
├── spacing/
│   └── Spacing.kt
├── component/
│   ├── AppButton.kt
│   ├── AppCard.kt
│   ├── LearningCard.kt
│   ├── Progress.kt
│   └── ExerciseOption.kt
└── theme/
    └── AppTheme.kt
```
---
# 48. Design System API
Featureها نباید مستقیماً رنگ‌های خام را استفاده کنند.
بد:
```kotlin
Color(0xFF2169EF)
```
بهتر:
```kotlin
MaterialTheme.colorScheme.primary
```
یا:
```kotlin
AppColors.learningPrimary
```
---
# 49. Semantic Design Tokens
نمونه:
```text
learningPrimary
learningSurface
learningSuccess
learningError
learningWarning
learningInfo
reviewDue
masteryHigh
masteryMedium
masteryLow
```
این کار تغییر Theme را ساده می‌کند.
---
# 50. Accessibility
حداقل:
```text
Touch Target >= 48dp
```
بررسی:
- Contrast
- Font Scaling
- TalkBack
- Content Description
- Color-independent feedback
مثلاً Correct Answer فقط با رنگ سبز مشخص نشود.
بلکه:
```text
✓ درست است
```
هم نمایش داده شود.
---
# 51. RTL/LTR Rules
صفحه:
```text
RTL
```
متن انگلیسی:
```text
LTR
```
مثال:
```text
┌────────────────────────────┐
│          expensive         │
│                            │
│ This phone is expensive.   │
│                            │
│             گران           │
└────────────────────────────┘
```
از قرار دادن متن انگلیسی در Layout RTL بدون کنترل Direction جلوگیری شود.
---
# 52. Responsive Design
اپ باید برای اندازه‌های مختلف مناسب باشد.
### Phone
```text
Compact
```
### Large Phone
```text
Expanded content
```
### Tablet
```text
Two-column layout where useful
```
مثلاً:
```text
┌───────────────┬───────────────┐
│ Exercise      │ Explanation   │
│               │               │
└───────────────┴───────────────┘
```
---
# 53. Brand Usage Rules
## Do
- استفاده ثابت از #2169EF
- فضای سفید کافی
- تایپوگرافی خوانا
- UI ساده
- آیکون‌های یکپارچه
- استفاده محدود از رنگ‌ها
## Don't
- استفاده زیاد از Gradient
- استفاده از چند رنگ برند
- Shadow سنگین
- Neon colors
- UI شلوغ
- Gamification بیش از حد
- استفاده تزئینی از Emoji در تمام UI
---
# 54. Brand Gradient
Gradient باید اختیاری و بسیار محدود باشد.
پیشنهاد:
```text
#2169EF
→
#60A5FA
```
موارد مناسب:
- Hero
- Empty State
- Marketing
- Onboarding
برای کنترل‌های روزمره از رنگ Solid استفاده شود.
---
# 55. Marketing Visual Language
تصاویر تبلیغاتی باید:
```text
White / very light background
+
Blue brand accent
+
Phone UI
+
Learning illustration
+
Large typography
```
باشد.
از رنگ‌های متعدد و پس‌زمینه‌های شلوغ استفاده نشود.
---
# 56. Design System Summary
```text
Brand
 └── Blue #2169EF
Visual Style
 ├── Minimal
 ├── Modern
 ├── Educational
 └── Friendly
Typography
 ├── Persian
 └── English
Components
 ├── Buttons
 ├── Cards
 ├── Exercises
 ├── Progress
 ├── Review
 ├── CEFR
 └── Navigation
Principles
 ├── Clarity
 ├── Focus
 ├── Accessibility
 ├── Consistency
 └── Learning-first
```
---
# 57. Final Design Principle
این محصول نباید شبیه یک بازی با محتوای زبان باشد.
باید شبیه یک **محصول آموزشی مدرن** باشد که از تکنیک‌های تعاملی و Gamification فقط برای افزایش کیفیت یادگیری استفاده می‌کند.
فرمول بصری محصول:
```text
Minimal UI
+
Strong Typography
+
Blue Identity
+
Clear Learning Hierarchy
+
Meaningful Feedback
+
Low Cognitive Load
```
و فرمول تجربه کاربر:
```text
Open App
 ↓
Know What To Do
 ↓
Learn
 ↓
Practice
 ↓
Get Feedback
 ↓
Review
 ↓
See Progress
 ↓
Continue
```
