# English Learning App — Technical Architecture & Implementation Specification

> نسخه پیشنهادی فنی برای یک اپلیکیشن Android آموزش زبان انگلیسی با رویکرد Offline-first، معماری قابل توسعه، یادگیری تطبیقی و Spaced Repetition.

---

## 1. اهداف فنی

اپلیکیشن باید:

- Offline-first باشد.
- محتوای آموزشی را بدون اینترنت در اختیار کاربر قرار دهد.
- معماری Modular و قابل توسعه داشته باشد.
- Domain Logic از UI مستقل باشد.
- قابلیت Sync در آینده داشته باشد.
- سیستم Spaced Repetition مستقل از UI باشد.
- امکان اضافه‌شدن AI Speaking در آینده وجود داشته باشد.
- تست‌پذیر باشد.
- قابلیت توسعه از A1/A2 تا C1/C2 را داشته باشد.
- قابلیت تغییر یا توسعه الگوریتم آموزشی بدون بازنویسی UI را داشته باشد.

---

# 2. Tech Stack پیشنهادی

## Android

```text
Language: Kotlin
UI: Jetpack Compose
Build: Gradle + Version Catalog
Architecture: Clean Architecture + Feature Modules
DI: Hilt
Database: Room
Preferences: DataStore
Async: Kotlin Coroutines
Reactive State: Flow / StateFlow
Serialization: kotlinx.serialization
Navigation: Navigation Compose
Background Work: WorkManager
Testing: JUnit + AndroidX Test + Compose UI Test
```

## اختیاری / آینده

```text
Network: Retrofit / OkHttp
Backend Sync: REST API
Authentication: OAuth2 / Google / Email
Analytics: Privacy-conscious analytics
Speech: Android Speech APIs / Cloud Speech / On-device model
AI Conversation: Backend AI service
```

---

# 3. معماری کلی

پیشنهاد اصلی:

```text
                    ┌─────────────────────┐
                    │     Presentation    │
                    │  Compose / ViewModel│
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │       Domain        │
                    │ UseCases / Entities │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │        Data         │
                    │ Repository / Local  │
                    │ Remote / Sync       │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              ▼                ▼                ▼
            Room           DataStore         Network
```

اصل مهم:

```text
UI
 ↓
ViewModel
 ↓
UseCase
 ↓
Repository
 ↓
Data Source
```

UI نباید مستقیماً با Room یا API کار کند.

---

# 4. معماری Feature-based

ساختار پیشنهادی پروژه:

```text
app/
├── core/
│   ├── common/
│   ├── designsystem/
│   ├── database/
│   ├── datastore/
│   ├── network/
│   ├── audio/
│   ├── speech/
│   └── testing/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
├── data/
│   ├── local/
│   ├── remote/
│   ├── mapper/
│   └── repository/
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
│   ├── pronunciation/
│   ├── reading/
│   ├── writing/
│   ├── review/
│   ├── progress/
│   └── settings/
│
└── app/
    ├── navigation/
    └── MainActivity.kt
```

---

# 5. پیشنهاد Modularization

برای MVP می‌توان پروژه را با یک Application Module شروع کرد، اما مرزهای ماژولار باید از ابتدا مشخص باشند.

ساختار قابل توسعه:

```text
:app

:core:common
:core:designsystem
:core:database
:core:datastore
:core:network
:core:audio
:core:speech
:core:testing

:domain

:data

:feature:onboarding
:feature:placement
:feature:home
:feature:lesson
:feature:vocabulary
:feature:review
:feature:progress
```

در نسخه‌های بعد:

```text
:feature:grammar
:feature:listening
:feature:speaking
:feature:reading
:feature:writing
```

---

# 6. Dependency Rule

وابستگی‌ها باید یک‌طرفه باشند.

```text
feature
   ↓
domain
   ↓
data abstractions
```

و:

```text
data
   ↓
domain
```

نباید:

```text
domain → Android UI
domain → Room
domain → Compose
```

Domain باید تا حد امکان Pure Kotlin باقی بماند.

---

# 7. Domain Layer

Domain قلب آموزشی برنامه است.

ساختار:

```text
domain/
├── model/
│   ├── User.kt
│   ├── LearningLevel.kt
│   ├── Skill.kt
│   ├── Lesson.kt
│   ├── VocabularyItem.kt
│   ├── Exercise.kt
│   ├── ReviewItem.kt
│   ├── LearningProgress.kt
│   └── LearningProfile.kt
│
├── repository/
│   ├── LessonRepository.kt
│   ├── VocabularyRepository.kt
│   ├── ReviewRepository.kt
│   ├── ProgressRepository.kt
│   └── LearningProfileRepository.kt
│
└── usecase/
    ├── GetTodayPlanUseCase.kt
    ├── StartLessonUseCase.kt
    ├── SubmitExerciseUseCase.kt
    ├── ScheduleReviewUseCase.kt
    ├── GetDueReviewsUseCase.kt
    ├── UpdateMasteryUseCase.kt
    └── GetLearningProfileUseCase.kt
```

---

# 8. Core Domain Models

## LearningLevel

```kotlin
enum class LearningLevel {
    A1,
    A2,
    B1,
    B2,
    C1,
    C2
}
```

## Skill

```kotlin
enum class Skill {
    VOCABULARY,
    GRAMMAR,
    LISTENING,
    SPEAKING,
    READING,
    WRITING,
    PRONUNCIATION,
    FLUENCY
}
```

---

# 9. Lesson Model

```kotlin
data class Lesson(
    val id: String,
    val level: LearningLevel,
    val title: String,
    val topic: String,
    val estimatedMinutes: Int,
    val order: Int
)
```

محتوای Lesson نباید مستقیماً داخل UI ذخیره شود.

---

# 10. Vocabulary Model

```kotlin
data class VocabularyItem(
    val id: String,
    val word: String,
    val translation: String,
    val definition: String?,
    val pronunciation: String?,
    val audioPath: String?,
    val level: LearningLevel,
    val partOfSpeech: String?,
    val examples: List<String>,
    val collocations: List<String>
)
```

---

# 11. Exercise Model

پیشنهاد:

```kotlin
sealed interface Exercise {

    data class MultipleChoice(
        val id: String,
        val question: String,
        val options: List<String>,
        val correctOption: String
    ) : Exercise

    data class Translation(
        val id: String,
        val prompt: String,
        val acceptedAnswers: List<String>
    ) : Exercise

    data class FillBlank(
        val id: String,
        val sentence: String,
        val acceptedAnswers: List<String>
    ) : Exercise

    data class Listening(
        val id: String,
        val audioPath: String,
        val acceptedAnswers: List<String>
    ) : Exercise

    data class Speaking(
        val id: String,
        val prompt: String,
        val referenceAnswers: List<String>
    ) : Exercise
}
```

برای پاسخ‌های آزاد، `referenceAnswers` نباید به‌عنوان تنها پاسخ صحیح استفاده شود.

---

# 12. Repository Pattern

Domain فقط Interface را می‌شناسد.

```kotlin
interface VocabularyRepository {

    fun getVocabularyItem(
        id: String
    ): Flow<VocabularyItem?>

    fun getLessonVocabulary(
        lessonId: String
    ): Flow<List<VocabularyItem>>
}
```

Implementation در Data قرار می‌گیرد.

```text
Domain
  ↓
VocabularyRepository
  ↑
RoomVocabularyRepository
```

---

# 13. Database

Room برای داده‌های ساختاریافته و قابل Query مناسب است.

## Tables

```text
User
Lesson
LessonSection
VocabularyItem
VocabularyExample
GrammarTopic
Exercise
ReviewItem
ReviewAttempt
LearningProgress
LearningProfile
StudySession
```

---

# 14. Database Relationship

```text
Course
  │
  └── Level
       │
       └── Unit
            │
            └── Lesson
                 ├── Vocabulary
                 ├── Grammar
                 ├── Listening
                 ├── Reading
                 ├── Speaking
                 └── Exercises
```

---

# 15. Review Database

برای Spaced Repetition:

```text
ReviewItem
├── id
├── userId
├── contentId
├── contentType
├── state
├── dueAt
├── interval
├── easeFactor
├── stability
├── difficulty
├── repetitions
├── lapses
└── lastReviewedAt
```

---

# 16. Review State

```kotlin
enum class ReviewState {
    NEW,
    LEARNING,
    REVIEW,
    RELEARNING,
    MASTERED
}
```

---

# 17. Spaced Repetition Engine

الگوریتم مرور باید مستقل باشد.

```text
ReviewEngine
│
├── calculateNextReview()
├── calculateInterval()
├── calculateDifficulty()
├── updateStability()
└── updateState()
```

Interface:

```kotlin
interface ReviewScheduler {

    fun schedule(
        item: ReviewItem,
        result: ReviewResult,
        now: Instant
    ): ReviewSchedule
}
```

---

# 18. Review Result

```kotlin
enum class ReviewResult {
    AGAIN,
    HARD,
    GOOD,
    EASY
}
```

خروجی:

```kotlin
data class ReviewSchedule(
    val nextReviewAt: Instant,
    val intervalDays: Int,
    val state: ReviewState
)
```

---

# 19. نکته مهم الگوریتم

در نسخه MVP می‌توان الگوریتم ساده ساخت.

اما معماری باید اجازه دهد بعداً الگوریتم دقیق‌تر مانند مدل‌های مبتنی بر Stability/Difficulty یا الگوریتم‌های مدرن SRS جایگزین شود.

UI نباید بداند الگوریتم چگونه محاسبه می‌شود.

---

# 20. Learning Profile

```kotlin
data class LearningProfile(
    val userId: String,
    val vocabularyMastery: Float,
    val grammarMastery: Float,
    val listeningMastery: Float,
    val speakingMastery: Float,
    val readingMastery: Float,
    val writingMastery: Float,
    val pronunciationMastery: Float,
    val fluencyMastery: Float
)
```

مقادیر:

```text
0.0 → 1.0
```

---

# 21. Mastery Engine

Mastery فقط بر اساس آخرین پاسخ تعیین نشود.

عوامل:

```text
Correctness
+
Repeated Recall
+
Delayed Recall
+
Difficulty
+
Contextual Use
+
Production
```

نمونه:

```kotlin
interface MasteryEngine {

    fun calculateMastery(
        history: List<ReviewAttempt>
    ): Float
}
```

---

# 22. Adaptive Learning Engine

وظیفه:

```text
User Performance
       ↓
Learning Profile
       ↓
Weakness Detection
       ↓
Content Selection
       ↓
Daily Plan
```

Interface:

```kotlin
interface LearningPlanner {

    fun createDailyPlan(
        profile: LearningProfile,
        dueReviews: List<ReviewItem>,
        availableMinutes: Int
    ): DailyLearningPlan
}
```

---

# 23. Daily Plan

```kotlin
data class DailyLearningPlan(
    val date: LocalDate,
    val targetMinutes: Int,
    val reviewItems: List<String>,
    val lessonId: String?,
    val recommendedSkills: List<Skill>
)
```

---

# 24. Daily Plan Algorithm

نمونه منطق:

```text
1. Reviewهای سررسید شده را دریافت کن
2. موارد مهم/ضعیف را اولویت‌بندی کن
3. وضعیت Lesson فعلی را بررسی کن
4. مهارت‌های ضعیف را پیدا کن
5. زمان در دسترس کاربر را بررسی کن
6. Review را تخصیص بده
7. Lesson/Exercise را اضافه کن
8. Fluency Practice را در صورت امکان اضافه کن
```

---

# 25. Presentation Layer

هر Feature:

```text
feature/
└── lesson/
    ├── LessonRoute.kt
    ├── LessonScreen.kt
    ├── LessonViewModel.kt
    ├── LessonUiState.kt
    └── LessonEvent.kt
```

---

# 26. UiState

```kotlin
data class LessonUiState(
    val isLoading: Boolean = false,
    val lesson: Lesson? = null,
    val currentExercise: Exercise? = null,
    val progress: Float = 0f,
    val error: String? = null
)
```

---

# 27. UI Events

```kotlin
sealed interface LessonEvent {

    data object StartLesson : LessonEvent

    data class SubmitAnswer(
        val answer: String
    ) : LessonEvent

    data object NextExercise : LessonEvent

    data object ExitLesson : LessonEvent
}
```

---

# 28. ViewModel

```kotlin
@HiltViewModel
class LessonViewModel @Inject constructor(
    private val getLesson: GetLessonUseCase,
    private val submitExercise: SubmitExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> =
        _uiState.asStateFlow()

    fun onEvent(event: LessonEvent) {
        // handle event
    }
}
```

ViewModel نباید مستقیماً Room را صدا بزند.

---

# 29. Compose UI

```kotlin
@Composable
fun LessonScreen(
    state: LessonUiState,
    onEvent: (LessonEvent) -> Unit
) {
    // UI only
}
```

اصل:

```text
Composable
    ↓
Event
    ↓
ViewModel
    ↓
UseCase
```

---

# 30. Navigation

Graph اصلی:

```text
Onboarding
    ↓
Placement
    ↓
Home
    ├── Lesson
    ├── Review
    ├── Vocabulary
    ├── Progress
    └── Settings
```

برای Featureها Routeهای مستقل تعریف شود.

---

# 31. Offline-first

اصل مهم:

```text
UI
 ↓
Repository
 ↓
Local Database
```

و در صورت وجود اینترنت:

```text
Remote
 ↓
Sync
 ↓
Local Database
 ↓
UI
```

Local Database باید Source of Truth باشد.

---

# 32. Content Delivery

دو نوع محتوا:

## Static Core Content

داخل اپ یا Asset:

```text
assets/content/
├── a1/
├── a2/
└── media/
```

## Downloadable Content

در آینده:

```text
Server
 ↓
Content Manifest
 ↓
Download
 ↓
Validation
 ↓
Room / File Storage
```

---

# 33. Content Versioning

هر بسته محتوا باید Version داشته باشد.

```json
{
  "contentVersion": 12,
  "minAppVersion": 5,
  "level": "A2"
}
```

قبل از نصب:

```text
Validate
 ↓
Checksum
 ↓
Version
 ↓
Import
```

---

# 34. Audio Architecture

Audioها نباید به شکل Blob داخل Room ذخیره شوند.

پیشنهاد:

```text
Room
 └── audioPath

File Storage
 └── audio files
```

مثلاً:

```text
audio/
├── vocabulary/
├── listening/
├── pronunciation/
└── dialogues/
```

---

# 35. Audio Player

یک سرویس مستقل:

```kotlin
interface AudioPlayer {

    fun play(path: String)

    fun pause()

    fun stop()

    fun release()
}
```

UI نباید مستقیماً Player implementation را کنترل کند.

---

# 36. Speaking Architecture

Speaking باید به Interface وابسته باشد:

```kotlin
interface SpeechEvaluator {

    suspend fun evaluate(
        audio: File,
        expectedContext: SpeakingContext
    ): SpeechEvaluation
}
```

مدل خروجی:

```kotlin
data class SpeechEvaluation(
    val pronunciationScore: Float?,
    val fluencyScore: Float?,
    val accuracyScore: Float?,
    val feedback: List<String>
)
```

---

# 37. Speaking Provider

در آینده می‌توان Providerهای مختلف داشت:

```text
SpeechEvaluator
├── AndroidSpeechEvaluator
├── CloudSpeechEvaluator
└── AiSpeechEvaluator
```

این طراحی باعث می‌شود تغییر سرویس Speech باعث تغییر UI نشود.

---

# 38. AI Architecture

AI نباید مستقیماً وارد Domain اصلی شود.

بهتر:

```text
AI Gateway
│
├── Conversation
├── Writing Feedback
├── Speaking Feedback
├── Explanation
└── Content Generation
```

و Domain فقط Interface ببیند:

```kotlin
interface ConversationEngine {

    suspend fun respond(
        conversation: List<ConversationMessage>
    ): ConversationResponse
}
```

---

# 39. Security

اطلاعات حساس:

- Token
- Credentials
- API Keys

نباید داخل APK به شکل Hardcoded قرار گیرند.

برای Authentication:

```text
Secure Storage
Token Rotation
Short-lived Access Token
Refresh Token
```

در صورت استفاده از Backend.

---

# 40. DataStore

DataStore برای Settings مناسب است.

نمونه:

```text
User Preferences
├── theme
├── language
├── dailyGoal
├── notifications
├── soundEnabled
├── speechEnabled
└── reminderTime
```

داده‌های آموزشی نباید صرفاً در DataStore ذخیره شوند.

---

# 41. Notifications

برای Review:

```text
Review Due
      ↓
WorkManager / Alarm Strategy
      ↓
Notification
```

متن Notification بهتر است کاربر را به یک فعالیت مشخص هدایت کند:

```text
18 review items are ready
```

---

# 42. WorkManager

کارهای مناسب:

- Content Sync
- Database Maintenance
- Backup
- Download
- Analytics Upload
- Reminder Preparation

کارهای Real-time UI نباید به WorkManager منتقل شوند.

---

# 43. Backup & Sync

در آینده:

```text
Local Database
      ↓
Sync Queue
      ↓
Backend
      ↓
Cloud Data
```

هر تغییر قابل Sync باید شناسه و زمان داشته باشد:

```text
id
updatedAt
deletedAt
syncState
```

---

# 44. Conflict Resolution

برای داده‌های آموزشی:

```text
Last Write Wins
```

می‌تواند برای برخی Settings مناسب باشد.

اما برای Progress و Review History بهتر است داده‌ها Merge شوند.

مثلاً:

```text
ReviewAttempt
```

به‌صورت Event ذخیره شود تا از بین نرود.

---

# 45. Event-based Learning History

به‌جای نگهداری فقط وضعیت فعلی:

```text
Current Mastery = 0.72
```

تاریخچه نیز نگهداری شود:

```text
ReviewAttempt
├── itemId
├── timestamp
├── result
├── responseTime
├── difficulty
└── source
```

این اطلاعات برای Adaptive Learning بسیار ارزشمند هستند.

---

# 46. Analytics

Analytics آموزشی باید با حداقل داده لازم انجام شود.

نمونه Events:

```text
lesson_started
lesson_completed
exercise_answered
review_completed
speaking_started
speaking_completed
daily_goal_completed
```

از جمع‌آوری داده غیرضروری خودداری شود.

---

# 47. Privacy

اصول:

- Privacy by Design
- Minimum Data Collection
- واضح‌بودن دلیل جمع‌آوری داده
- حذف داده در صورت درخواست کاربر
- رمزنگاری داده حساس
- عدم ارسال Audio بدون رضایت مناسب
- امکان استفاده Offline تا حد امکان

---

# 48. Testing Strategy

## Unit Tests

برای:

- ReviewScheduler
- MasteryEngine
- LearningPlanner
- UseCases
- Mappers

مثال:

```text
Given:
correct answer

When:
schedule()

Then:
next review must be later than now
```

---

# 49. Repository Tests

تست:

```text
Room
 ↓
Repository
 ↓
UseCase
```

هدف:

- صحت Query
- Mapping
- Offline behavior
- Error handling

---

# 50. Compose UI Tests

موارد مهم:

- Lesson navigation
- Answer submission
- Review interaction
- Progress rendering
- Accessibility
- RTL/LTR behavior

---

# 51. Integration Tests

سناریو:

```text
New User
 ↓
Placement Test
 ↓
A1 selected
 ↓
Daily Plan
 ↓
Lesson
 ↓
Exercise
 ↓
Review scheduled
 ↓
Progress updated
```

---

# 52. Error Handling

خطاها باید در Domain به شکل قابل مدیریت تبدیل شوند.

مثال:

```kotlin
sealed interface AppError {

    data object NetworkUnavailable : AppError

    data object ContentNotFound : AppError

    data object AudioUnavailable : AppError

    data object DatabaseError : AppError

    data object SpeechUnavailable : AppError
}
```

UI نباید Exceptionهای Data Layer را مستقیماً دریافت کند.

---

# 53. State Management

اصل:

```text
Single Source of Truth
```

هر Screen:

```text
UiState
```

و تعامل:

```text
UI Event
```

مثال:

```text
LessonScreen
     ↓
LessonEvent.SubmitAnswer
     ↓
LessonViewModel
     ↓
SubmitExerciseUseCase
     ↓
Repository
     ↓
ReviewEngine
     ↓
Updated State
```

---

# 54. Dependency Injection

Hilt برای:

- Repository
- Database
- UseCase
- AudioPlayer
- SpeechEvaluator
- Network Client
- ReviewScheduler

استفاده شود.

مثال:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object LearningModule {

    @Provides
    fun provideReviewScheduler(): ReviewScheduler {
        return DefaultReviewScheduler()
    }
}
```

---

# 55. Build Configuration

Versionها باید در Version Catalog مدیریت شوند.

```text
gradle/
└── libs.versions.toml
```

از پراکنده‌کردن Versionها در Moduleها جلوگیری شود.

---

# 56. Build Variants

حداقل:

```text
debug
release
```

در آینده:

```text
internal
beta
release
```

برای محیط‌های مختلف:

```text
Development
Staging
Production
```

---

# 57. Logging

در Debug:

```text
Verbose logging
```

در Release:

```text
Minimal logging
```

اطلاعات حساس نباید در Log ثبت شود.

---

# 58. Performance

اهداف:

- Startup سریع
- Lazy Loading
- Paging برای محتواهای بزرگ
- عدم Load هم‌زمان تمام Audioها
- عدم Query سنگین روی Main Thread
- استفاده از Coroutine/Dispatchers مناسب
- کاهش Recomposition غیرضروری

---

# 59. Accessibility

اپ باید از ابتدا:

- Content Description
- Touch Target مناسب
- Font Scaling
- Contrast
- Screen Reader
- Keyboard Navigation در صورت نیاز

را در نظر بگیرد.

---

# 60. RTL / LTR

چون کاربر فارسی‌زبان است:

```text
UI → RTL
Persian → RTL
English content → LTR
English text input → LTR
```

برای کلمات و جملات انگلیسی باید Layout Direction مناسب رعایت شود.

---

# 61. Content Authoring System

تولید محتوای آموزشی نباید نیازمند تغییر کد Android باشد.

پیشنهاد:

```text
Content Source
     ↓
JSON / CMS
     ↓
Validation
     ↓
Content Package
     ↓
Android Importer
     ↓
Room
```

---

# 62. Content Validation

قبل از ورود محتوا:

```text
Validate
├── ID uniqueness
├── CEFR validity
├── Missing translation
├── Missing audio
├── Invalid exercise
├── Duplicate vocabulary
└── Broken references
```

---

# 63. Content IDs

IDها باید Stable باشند.

بد:

```text
1
2
3
```

بهتر:

```text
a1.travel.airport.vocabulary.passport
```

مثال:

```text
a1.daily-routine.lesson-01
a1.daily-routine.word.wake-up
```

---

# 64. Content Package

نمونه:

```text
content-a1-v1/
├── manifest.json
├── lessons.json
├── vocabulary.json
├── grammar.json
├── exercises.json
├── dialogues.json
└── audio/
```

---

# 65. Dependency Direction

قانون پروژه:

```text
UI
 ↓
Feature
 ↓
Domain
 ↓
Repository Interface
 ↑
Data Implementation
```

هیچ Feature نباید مستقیماً Feature دیگر را Import کند.

در صورت نیاز:

```text
Feature A
 ↓
Domain Contract
 ↓
Feature B behavior
```

---

# 66. Feature Communication

برای مثال Lesson پس از تکمیل:

```text
Lesson Feature
      ↓
Domain Event / UseCase
      ↓
Progress updated
      ↓
Home observes updated state
```

نباید:

```text
LessonScreen → HomeScreen
```

باشد.

---

# 67. Recommended Package Structure

نمونه برای Lesson:

```text
feature/lesson/

├── data/
│   └── ...
│
├── domain/
│   └── ...
│
└── presentation/
    ├── LessonRoute.kt
    ├── LessonScreen.kt
    ├── LessonViewModel.kt
    ├── LessonUiState.kt
    ├── LessonEvent.kt
    └── components/
        ├── LessonHeader.kt
        ├── ExerciseCard.kt
        └── LessonProgress.kt
```

در پروژه بزرگ‌تر Data و Domain می‌توانند Module مستقل باشند.

---

# 68. MVP Technical Scope

نسخه اول:

```text
Android
├── Compose
├── Hilt
├── Room
├── DataStore
├── Coroutines
├── Flow
├── Navigation
├── WorkManager
└── Local Audio
```

Features:

```text
Onboarding
Placement
Home
Lesson
Vocabulary
Review
Progress
Settings
```

---

# 69. MVP Learning Engine

در MVP:

```text
Placement
 ↓
A1/A2
 ↓
Daily Plan
 ↓
Lesson
 ↓
Exercise
 ↓
Review
 ↓
Progress
```

بعداً:

```text
Speaking
Writing
Adaptive Learning
AI
Cloud Sync
```

اضافه شوند.

---

# 70. توسعه مرحله‌ای

## Phase 1 — Foundation

- Project setup
- Architecture
- Database
- DataStore
- Navigation
- Design System

## Phase 2 — Content

- Content schema
- A1 curriculum
- Lesson engine
- Vocabulary

## Phase 3 — Learning Engine

- Review
- SRS
- Mastery
- Daily Plan

## Phase 4 — Skills

- Listening
- Grammar
- Reading
- Pronunciation

## Phase 5 — Production

- Speaking
- Writing
- Fluency

## Phase 6 — Intelligence

- Adaptive Learning
- AI Conversation
- AI Feedback

## Phase 7 — Cloud

- Authentication
- Sync
- Backup
- Content Updates

---

# 71. اولویت پیاده‌سازی

ترتیب پیشنهادی:

```text
1. Architecture
2. Domain Models
3. Database
4. Content Schema
5. Repository
6. Lesson Engine
7. Exercise Engine
8. Review Engine
9. Daily Plan
10. Progress
11. Placement
12. Listening
13. Pronunciation
14. Speaking
15. Writing
16. Adaptive Learning
17. Cloud Sync
18. AI
```

---

# 72. اصل مهم برای توسعه

نباید از ابتدا AI را هسته اپ قرار داد.

هسته اصلی:

```text
Curriculum
+
Content
+
Learning Engine
+
Review Engine
+
Assessment
```

AI باید یک قابلیت روی این زیرساخت باشد.

---

# 73. Definition of Done

یک Feature زمانی کامل است که:

- Domain logic تست شده باشد.
- Repository تست شده باشد.
- UI state مشخص باشد.
- Loading state داشته باشد.
- Error state داشته باشد.
- Empty state داشته باشد.
- Offline behavior مشخص باشد.
- Accessibility بررسی شده باشد.
- RTL/LTR بررسی شده باشد.
- Performance قابل قبول باشد.
- Analytics غیرضروری اضافه نشده باشد.

---

# 74. Architecture Summary

```text
                 ┌─────────────────────┐
                 │       Compose       │
                 │    UI / Screens     │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │      ViewModel      │
                 │   UiState / Event   │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │       UseCase       │
                 │   Domain Business   │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │     Repository      │
                 └──────────┬──────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
       ┌──────────────┐            ┌──────────────┐
       │     Room     │            │    Remote    │
       │ Local Source │            │ API / Cloud  │
       └──────────────┘            └──────────────┘

                  Learning Engine
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
   Review Engine    Mastery Engine   Planning Engine
        │                │                │
        └────────────────┼────────────────┘
                         ▼
                  Personalized Plan
```

---

# 75. نتیجه نهایی

این معماری باید سه اصل را حفظ کند:

## اصل 1 — UI قابل تعویض

تغییر UI نباید Learning Engine را خراب کند.

## اصل 2 — Learning Engine مستقل

تغییر الگوریتم SRS یا Mastery نباید نیازمند تغییر Screenها باشد.

## اصل 3 — Content مستقل از Code

افزودن درس جدید نباید نیازمند تغییر کد Android باشد.

معماری نهایی:

```text
Content
   ↓
Curriculum
   ↓
Learning Engine
   ↓
Assessment
   ↓
Personalization
   ↓
Android UI
```

این ساختار اجازه می‌دهد محصول از یک MVP آفلاین A1/A2 به یک پلتفرم کامل آموزش زبان با A1 تا C2، Speaking، AI، Cloud Sync و محتوای قابل دانلود توسعه پیدا کند.
