# Phonon TTS — آفلاین‌ترین راه برای صدای طبیعی انگلیسی در اندروید

MVP مستقل: متن انگلیسی می‌گیری، صدا را انتخاب می‌کنی، و مدل **Phonon 31M** (INT8، ۸۷ مگابایت)
روی خود گوشی گفتار تولید می‌کند. نه شبکه‌ای در کار است، نه مجوزی جز هیچ — `INTERNET` هم اعلام نشده.

جای Google TTS را می‌گیرد چون صدای ماشینی آن برای گوشِ زبان‌آموز تاب ندارد؛ Phonon خروجی
۲۴ کیلوهرتز مونو با کدک عصبی Mimi می‌دهد، نه concatenative.

## اجرا

```bash
# ۱) مدل (۸۷ MB، در git نیست)
pip install -U huggingface_hub     # اگر hf نداری
./scripts/download_model.sh

# ۲) بیلد و نصب (arm64)
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# ۳) شاهد دستگاه: مدل، صداها، سنتز ۲۴k، پخش و توقف
./gradlew :app:connectedDebugAndroidTest
```

یا در Android Studio همین پوشه را به‌عنوان پروژهٔ مستقل باز کن (settings.gradle.kts خودش را دارد).

## API واقعی (تأییدشده، نه حدس)

`com.google.mediapipe:tasks-audio:1.2.3` از Google Maven (روی Maven Central نیست):

| پلن گفته بود | واقعیت |
|---|---|
| `TextToSpeechOptions.builder()…build()` | سازندهٔ Kotlin با آرگومان نام‌دار: `TextToSpeechOptions(baseOptions=…, defaultVoiceId=…, speakingRate=…, cacheDir=…)` |
| `availableVoices()` | `listVoices(): List<VoiceInfo>` با `voiceId/displayName/locale/sampleRateHz` |
| `SynthesisOptions.builder()` | `SynthesisOptions(voiceId = "freya")` |
| `synthesize(text, options)` (فقط) | هم `synthesize(...)` مسدودکننده و هم `synthesizeAsync(...)` suspend؛ خروجی `AudioResult(samples: FloatArray, sampleRateHz: Int)` |
| `setModelAssetPath("phonon_int8.litertlm")` کافی است | **نه.** روی دستگاه واقعی: `IllegalStateException: "phonon_int8.litertlm/tokenizer.model": No such file or directory`. تسک به **مسیر فایل واقعی** نیاز دارد؛ مدل یک‌بار از assets به `filesDir/models` کپی می‌شود و مسیر مطلق داده می‌شود |
| `cacheDirectory` | منسوخ؛ نام فعلی `cacheDir` |

صداهای داخل باندل روی همین گوشی: `freya` (en-US، پیش‌فرض)، `claire` (fr-FR)، `lukas` (de-DE) و…

## نتیجهٔ تست روی دستگاه فیزیکی

Poco X3 NFC (M2007J20CG، arm64-v8a، اندروید 12):

- `connectedDebugAndroidTest` → **۳/۳ سبز**: بارگذاری مدل + فهرست صداها (شامل `freya`)، سنتز
  `AudioResult` با ۲۴۰۰۰ هرتز و نمونه‌های غیرساکت در بازهٔ [-1, 1]، پخش و توقف بدون نشت `AudioTrack`.
- اجرای دستی: ورود متن → «پخش» → وضعیت «در حال پخش» و فعال شدن «توقف» → پایان خودکار و بازگشت به
  «آماده» و فعال شدن دوبارهٔ «پخش»؛ لاگ صدا `appname=-com.example.phonontts` روی مسیر speaker.
- زمان تقریبی: کپی اولیهٔ مدل هنگام اولین اجرا چند ثانیه، سنتز یک جملهٔ کوتاه حدود یک ثانیه.

## محدودیت‌ها

- فقط `arm64-v8a` و `minSdk 26` (خود MediaPipe همین را می‌خواهد) — روی شبیه‌ساز x86 اجرا نمی‌شود.
- APK حدود ۱۲۷ مگابایت (۸۷ مدل + native)؛ برای انتشار: App Bundle یا دانلود مدل در اولین اجرا.
- صداهای غیرانگلیسی باندل (`claire`, `lukas`) برای جملهٔ انگلیسی نامناسب‌اند؛ برای اپ انگلیسی
  فقط `freya` را نشان بده یا بقیه را در UI فیلتر کن.
- متن بلند در یک کلیک سنتز می‌شود (بدون استریم)؛ برای جمله‌های چندپاراگرافی `synthesizeStream` را
  در همین کلاس جایگزین کن (API موجود است).
- موتور CPU-only است و مدل را در حافظه نگه می‌دارد؛ روی گوشی حافظهٔ کم ممکن است کشته شود.

## ساختار

```text
app/src/main/java/com/example/phonontts/
  PhononTtsManager.kt   # TextToSpeech task: assets→filesDir، listVoices، synthesize، close
  AudioPlayer.kt        # AudioTrack: PCM_FLOAT، نرخ از نتیجه، MODE_STATIC، نشانگر پایان
  MainActivity.kt       # متن، Spinner صداها، پخش/توقف، وضعیت
app/src/androidTest/…   # شاهد دستگاه (۳ تست)
scripts/download_model.sh
```

## آوردنش به اپ Lingo

اپ اصلی (`org.token.english`) صدای انگلیسی را از `AudioPlayer`/`TtsAudioPlayer` می‌گیرد و همین
یک درز برای جابه‌جایی کافی است. اما پیش از آن سه تصمیم محصولی لازم است، چون هزینه دارند:
`minSdk` از ۲۴ به ۲۶، محدودشدن ABI به `arm64-v8a` (وصک ۳۲ بیتی و شبیه‌ساز x86 می‌افتند)، و
۸۷ مگابایت مدل در APK یا دانلود اولین‌اجرا. تا آن تصمیم، این پروژه مستقل است و به اپ دست نمی‌زند.
