# PLAN.md — ساخت اپلیکیشن اندروید TTS با Phonon 31M (Kotlin)

> این فایل نقشهٔ همان MVP است که در `phonon-tts/` پیاده شده. برای اجرا و نتیجهٔ واقعی
> تست‌ها `README.md` را ببین.

## 0. دستورالعمل برای عامل هوش مصنوعی

**نقش:** مهندس اندروید ارشد؛ پیاده‌سازی مرحله‌به‌مرحله یک اپلیکیشن اندروید به زبان Kotlin که
متن انگلیسی را با مدل Phonon 31M به گفتار تبدیل می‌کند.

**هدف:** MVP کاملاً آفلاین با MediaPipe Tasks Audio و مدل `phonon_int8.litertlm`.

**قواعد الزامی:**
1. امضای دقیق API کتابخانه `com.google.mediapipe:tasks-audio` از مستندات رسمی بررسی شود،
   نام متدها حدس زده نشود، و هر تفاوت با این پلن در گزارش بیاید.
2. مدل از Hugging Face دریافت شود؛ لینک یا فایل مدل جعل نشود.
3. `Delegate.CPU` (Phonon برای CPU بهینه است).
4. پخش با `AudioTrack`، `ENCODING_PCM_FLOAT`، نرخ 24000، مونو.
5. `minSdk` حداقل 26.
6. تست نهایی روی دستگاه فیزیکی arm64.
7. TODO ناتمام در مسیر اصلی MVP نباشد.
8. مدل (۸۷ مگابایت) در Git معمولی کامیت نشود.

## 1. مشخصات محصول (MVP)

- ورود متن انگلیسی در `EditText`.
- انتخاب صدا از `Spinner`.
- دکمهٔ «پخش» برای تولید و پخش صدا.
- دکمهٔ «توقف» برای قطع پخش.
- نمایش وضعیت: آماده، در حال بارگذاری، در حال تولید، در حال پخش، متوقف، خطا.
- اجرای کاملاً آفلاین پس از نصب.

**خارج از محدوده:** چندزبانه، کلون صدا، TTS ابری، همگام‌سازی کلمه‌به‌کلمه، استریمینگ.

## 2. تصمیم‌های فنی

| مورد | تصمیم |
|---|---|
| زبان | Kotlin |
| حداقل SDK | 26 |
| کتابخانه | `com.google.mediapipe:tasks-audio:1.2.3` |
| مدل | `phonon_int8.litertlm` (۸۷ MB) |
| مسیر مدل | `app/src/main/assets/phonon_int8.litertlm` |
| Delegate | `Delegate.CPU` |
| صدای پیش‌فرض | `freya` |
| نرخ نمونه‌برداری | خروجی مدل (۲۴۰۰۰) |
| فرمت پخش | PCM Float، مونو |
| معماری | `arm64-v8a` |

## 3. ساختار فایل‌ها

```text
phonon-tts/
  build.gradle.kts
  settings.gradle.kts
  gradle.properties
  app/
    build.gradle.kts
    src/main/
      AndroidManifest.xml
      assets/phonon_int8.litertlm        # دانلودی، gitignored
      java/com/example/phonontts/
        MainActivity.kt
        PhononTtsManager.kt
        AudioPlayer.kt
      res/layout/activity_main.xml
      res/values/strings.xml
    src/androidTest/java/com/example/phonontts/
      PhononTtsInstrumentedTest.kt       # شاهد دستگاه (T7)
  scripts/download_model.sh
  README.md
  PLAN.md
```

## 4. وظایف

- **T1 پروژه:** AGP 9.5 (Kotlin داخلی)، `minSdk 26`، وابستگی MediaPipe، `noCompress += "litertlm"`.
- **T2 مدل:** `scripts/download_model.sh` با `hf download litert-community/phonon-31m-en phonon_int8.litertlm`.
- **T3 PhononTtsManager:** `TextToSpeech.create(TextToSpeechOptions(...))`، `listVoices()`،
  `synthesize(text, SynthesisOptions(voiceId = …))`، `cancel()`، `close()`.
- **T4 AudioPlayer:** `AudioTrack` با `ENCODING_PCM_FLOAT`، `MODE_STATIC`، نرخ از نتیجهٔ مدل،
  پایان پخش با marker.
- **T5 رابط کاربری:** `EditText`، `Spinner`، دو دکمه، `TextView` وضعیت.
- **T6 اتصال:** مقداردهی مدل بیرون از ترد اصلی، پر کردن Spinner از خود باندل، پخش/توقف، آزادسازی در `onDestroy`.
- **T7 تست:** تست instrumented روی دستگاه arm64 (مدل، صداها، سنتز ۲۴k، پخش و توقف).
- **T8 مستندسازی:** `README.md`.

## 5. چک‌لیست تأیید

- [x] پروژه Build می‌شود.
- [x] مدل در assets است (۸۷ MB، بدون commit).
- [x] `noCompress` برای `litertlm` تنظیم شده (در APK به‌صورت `Stored`).
- [x] مقداردهی TTS بدون crash.
- [x] Spinner از `listVoices()` پر می‌شود.
- [x] متن انگلیسی تولید و پخش می‌شود (تست instrumented).
- [x] توقف پخش را قطع می‌کند و پخش دوباره کار می‌کند.
- [x] منابع در `onDestroy` آزاد می‌شوند.
- [x] تست روی دستگاه فیزیکی arm64 (Poco X3 / M2007J20CG).
- [x] README شامل دانلود مدل و اجراست.

## 6. عیب‌یابی سریع

| مشکل | بررسی |
|---|---|
| خطای پیدا نشدن مدل | `scripts/download_model.sh`، وجود `app/src/main/assets/phonon_int8.litertlm` |
| crash هنگام initialize | نسخهٔ ۱.۲.۳، `minSdk 26`، ABI arm64 |
| صدای خالی | `AudioResult.samples` و `sampleRateHz` |
| صدای خش‌دار | `MODE_STATIC` و اندازهٔ بافر |
| خطای API | امضای واقعی `listVoices`/`synthesize` (نه `availableVoices`/builder) |
| حجم APK | مدل در assets است؛ برای انتشار App Bundle یا دانلود اولین‌اجرا |

## 7. معیار پذیرش نهایی

اپلیکیشن روی دستگاه arm64 نصب می‌شود، متن انگلیسی را می‌گیرد، با صدای انتخاب‌شده آفلاین گفتار
تولید می‌کند، پخش می‌کند و دکمهٔ توقف بدون crash عمل می‌کند.
