# Store listing — Lingo

The listing text for the three marketplaces. This is copy, not code: the offer itself
(storefront wording, the companion grant) lives in
[`billing.md`](billing.md), and the rules for what may be *claimed* are below — a line
that stops being true is a line to delete, not to defend.

- Persian is the primary listing language (the audience is Persian-speaking); Bazaar and
  Myket read it first, and Google Play takes the Persian text as the `fa` translation.
- English is the second field: Bazaar has separate English short/full fields, and Play
  needs a default language.
- Every claim here is checked against the shipped build (see *Claims* at the bottom).

## Field limits (checked 2026-10)

| Store | Title | Short description | Full description |
|---|---:|---:|---:|
| Google Play | 30 | 80 | 4000 |
| Cafe Bazaar | 100 max — names over ~16 characters can truncate on small devices | not published | not published |
| Myket | keep ≤ 16 for full display (their guidance) | not published | not published |

Sources: [Play Console Help — best practices for your store listing](https://support.google.com/googleplay/android-developer/answer/13393723)
(confirms 30 / 80 / 4000), [Bazaar app-name guidance](https://developers.cafebazaar.ir/fa/guidelines/build-app/app-name)
and [Myket title guidance](https://myket.ir/kb/pages/choose-app-title-fa/) (both only give the
display advice above; neither publishes a description limit — Play's numbers are used as the
safe ceiling here).

Icon: [`app/src/main/ic_launcher-playstore.png`](../app/src/main/ic_launcher-playstore.png)
(the Play / Bazaar / Myket store icon, generated with the rest of the launcher assets).

---

## Persian — کافه‌بازار و مایکت

### عنوان

```text
Lingo انگلیسی
```
13 characters — inside the ≤16 display advice, and the Latin wordmark is what people
actually search for.

### توضیح کوتاه

```text
انگلیسی را قدم به قدم یاد بگیر؛ درس روزانه، مرور فاصله‌دار و همه‌چیز آفلاین.
```
76 characters (limit 80 on Play).

### توضیح بلند

```text
یادگیری انگلیسی باید منظم و بی‌دردسر باشد، نه انبوهی از ویدیو که فقط دانلودشان می‌کنی و کنار می‌گذاری.

Lingo برای فارسی‌زبانان ساخته شده است: رابط کاملاً فارسی، محتوای اصیل انگلیسی، و مسیری که از سطح A1 تا C2 مشخص است. هر چه جلوتر بروی، همان چیزی را یاد می‌گیری که الان به آن نیاز داری، نه فهرستی از درس‌های تصادفی.

از جای درست شروع کن
آزمون تعیین سطح با ۳۶ سؤال در شش بازهٔ A1 تا C2، نقطهٔ شروعت را مشخص می‌کند و برنامهٔ روزانه از همان‌جا چیده می‌شود. اگر مطمئن نیستی از کجا شروع کنی، این اولین قدم است.

هر روز، کم و حساب‌شده
درس کوتاه روزانه، تمرین‌های متنوع (انتخاب گزینه، جای خالی، نوشتن، شنیدن) و توضیح فارسی برای هر پاسخ اشتباه. غلط فقط امتیاز نیست: همان مورد دوباره می‌آید تا یاد بگیری.

واژه‌ها فراموش نمی‌شوند
مرور فاصله‌دار واژه‌ها را درست در لحظهٔ فراموشی جلوی چانت می‌آورد، پس وقتت را صرف چیزهایی که بلدی نمی‌کنی.

بدون اینترنت هم کار می‌کند
درس‌ها، واژگان و مرور روی خود دستگاه ذخیره می‌شوند. در مترو، هواپیما یا جایی که آنتن نیست، یادگیری قطع نمی‌شود.

حریم خصوصی، بی‌چون‌وچرا
نه حساب کاربری، نه تبلیغ، نه ردیابی رفتار. پیشرفتت فقط روی دستگاه خودت است.

اشتراک، فقط اگر ارزشش را داشته باشد
دسترسی کامل با اشتراک ماهانه یا سالانه از همان فروشگاهی که در آن نصب کرده‌ای فعال می‌شود و هر زمان بخواهی از همان‌جا لغو می‌کنی. اگر برنامهٔ «زریبار» را نصب داشته باشی، همهٔ درس‌ها بدون هیچ پرداختی باز است.

همین حالا شروع کن — اولین درس چند دقیقه بیشتر طول نمی‌کشد.
```

---

## English — Bazaar (English fields) and Google Play

### Title

```text
Lingo — Learn English
```
21 characters (Play limit 30).

### Short description

```text
Learn English step by step: daily lessons, spaced review, fully offline.
```
72 characters (Play limit 80).

### Full description

```text
Learning English should be a routine you can keep, not a folder of videos you never open.

Lingo is built for Persian speakers: a fully Persian interface around real English content,
with a path that runs from A1 to C2. You always work on what is next for you instead of
picking from a random list of lessons.

Start at the right level
A 36-question placement test in six graded bands (A1–C2) finds your starting point, and the
daily plan is built from that result. Skip it and you begin at A1 — but the test takes a few
minutes and saves you weeks.

A little every day
One short lesson, varied exercises (multiple choice, fill-in-the-blank, typing, listening)
and a Persian explanation for every wrong answer. A miss is never just a score: the same
item comes back once so it sticks.

Words you don't forget
Spaced repetition brings each vocabulary item back just before you would forget it, so your
time goes to what you haven't learned yet.

Works without internet
Lessons, vocabulary and review history live on the device. On the metro, on a plane, or
anywhere without signal, your progress continues.

Privacy by default
No account, no ads, no behavioural tracking. Your learning data stays on your phone.

Subscription, only if it earns it
Full access with a monthly or yearly subscription purchased through the store you
installed from — cancel any time from the same store. If the Zaribar app is installed on
the device, everything is unlocked at no cost.

Start now: the first lesson takes a few minutes.
```

---

## Claims — what may and may not be said

Every line above traces to the build; check these before each release, because a store
claim that stops being true is a policy problem, not a copy problem.

| Claim | Source of truth |
|---|---|
| Free with the companion app | `CompanionOffer.isOffered` + `CompanionApp` (see [`billing.md`](billing.md)) |
| 36 questions, six bands A1–C2 | `placement.json` + README placement section |
| 48 lessons, 290 vocabulary entries | `lessons.json`, `vocabulary.json` |
| Fully offline learning core | [`connectivity.md`](connectivity.md) — the offline rule |
| No account / no ads / no tracking | no auth, no ad SDK, no analytics dependency in `app/build.gradle.kts` |
| Cancel any time via the store | [`billing.md`](billing.md) — subscription × 3 marketplaces |

Do **not** claim anything on the *suspended* list (AI conversation practice, cloud sync or
backup, downloadable content packages, analytics) — those features are not shipped.
