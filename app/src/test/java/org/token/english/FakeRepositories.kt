package org.token.english

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.token.english.core.audio.AudioPlayer
import org.token.english.core.audio.Speaker
import org.token.english.core.billing.TrialAndSubscription
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeState
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.LessonState
import org.token.english.domain.model.ReviewAttempt
import org.token.english.domain.model.ReviewItem
import org.token.english.domain.model.Skill
import org.token.english.domain.model.StudyStats
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.model.VocabularyItem
import org.token.english.domain.repository.KnowledgeRepository
import org.token.english.domain.repository.LessonRepository
import org.token.english.domain.repository.ProgressRepository
import org.token.english.domain.repository.ReviewRepository
import org.token.english.domain.repository.SettingsRepository
import org.token.english.domain.repository.VocabularyRepository

// ---------------------------------------------------------------------------
// In-memory fakes for the ViewModel tests (checklist B-6). They implement the
// domain repository interfaces, so a ViewModel can be built with no Android
// context, no Room and no DataStore.
// ---------------------------------------------------------------------------

fun testSettings(
    level: LearningLevel = LearningLevel.A1,
    dailyGoalMinutes: Int = 15,
) = AppSettings(
    isFirstLaunch = false,
    level = level,
    themeMode = ThemeMode.SYSTEM,
    dailyGoalMinutes = dailyGoalMinutes,
    soundEnabled = true,
    contentVersion = 20,
    trialStartedAt = 0L,
    subscriptionUntil = 0L,
)

class FakeSettingsRepository(
    initial: AppSettings = testSettings(),
    trial: TrialAndSubscription = TrialAndSubscription(0L, 0L, 0L, 0L, 0L),
) : SettingsRepository {

    val settingsFlow = MutableStateFlow(initial)
    val trialFlow = MutableStateFlow(trial)

    override val settings: Flow<AppSettings> = settingsFlow
    override fun observeTrialAndSubscription(): Flow<TrialAndSubscription> = trialFlow

    override suspend fun completeFirstLaunch() {
        settingsFlow.value = settingsFlow.value.copy(isFirstLaunch = false)
    }

    override suspend fun setLevel(level: LearningLevel) {
        settingsFlow.value = settingsFlow.value.copy(level = level)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        settingsFlow.value = settingsFlow.value.copy(themeMode = mode)
    }

    override suspend fun setDailyGoalMinutes(minutes: Int) {
        settingsFlow.value = settingsFlow.value.copy(dailyGoalMinutes = minutes)
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        settingsFlow.value = settingsFlow.value.copy(soundEnabled = enabled)
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        settingsFlow.value = settingsFlow.value.copy(dailyReminderEnabled = enabled)
    }

    override suspend fun setContentVersion(version: Int) {
        settingsFlow.value = settingsFlow.value.copy(contentVersion = version)
    }

    override suspend fun ensureTrialStarted(now: Long, elapsedRealtime: Long) {
        if (trialFlow.value.trialStartedAt == 0L) {
            trialFlow.value = trialFlow.value.copy(trialStartedAt = now, trialLastWallMs = now, trialLastElapsedMs = elapsedRealtime)
        }
    }

    override suspend fun saveTrialCheckpoint(consumedMs: Long, lastWallMs: Long, lastElapsedMs: Long) {
        trialFlow.value = trialFlow.value.copy(
            trialConsumedMs = consumedMs,
            trialLastWallMs = lastWallMs,
            trialLastElapsedMs = lastElapsedMs,
        )
    }

    override suspend fun setSubscriptionUntil(epochMillis: Long) {
        trialFlow.value = trialFlow.value.copy(subscriptionUntil = epochMillis)
    }
}

class FakeLessonRepository(
    lessons: List<Lesson> = emptyList(),
    states: List<LessonState> = emptyList(),
) : LessonRepository {

    val lessonsFlow = MutableStateFlow(lessons)
    val statesFlow = MutableStateFlow(states)
    val exercises: MutableMap<String, List<Exercise>> = mutableMapOf()
    val vocabulary: MutableMap<String, List<VocabularyItem>> = mutableMapOf()
    val savedIndex = mutableMapOf<String, Int>()
    val completed = mutableSetOf<String>()

    override fun observeLessons(): Flow<List<Lesson>> = lessonsFlow
    override fun observeLessonStates(): Flow<List<LessonState>> = statesFlow

    override suspend fun getLesson(lessonId: String): Lesson? =
        lessonsFlow.value.firstOrNull { it.id == lessonId }

    override suspend fun getExercises(lessonId: String): List<Exercise> = exercises[lessonId].orEmpty()

    override suspend fun getVocabulary(lessonId: String): List<VocabularyItem> = vocabulary[lessonId].orEmpty()

    override suspend fun saveLessonIndex(lessonId: String, index: Int) {
        savedIndex[lessonId] = index
        statesFlow.value = statesFlow.value.filterNot { it.lessonId == lessonId } +
            LessonState(lessonId, index, completed.contains(lessonId), null)
    }

    override suspend fun getLessonState(lessonId: String): LessonState? =
        statesFlow.value.firstOrNull { it.lessonId == lessonId }

    override suspend fun completeLesson(lessonId: String, at: Long) {
        completed += lessonId
    }

    override suspend fun enqueueLessonVocabulary(lessonId: String, now: Long) = Unit
}

class FakeVocabularyRepository(
    initial: List<VocabularyItem> = emptyList(),
) : VocabularyRepository {

    val allFlow = MutableStateFlow(initial)

    override fun observeAll(): Flow<List<VocabularyItem>> = allFlow

    override suspend fun search(query: String): List<VocabularyItem> =
        allFlow.value.filter { it.word.contains(query, ignoreCase = true) }

    override suspend fun get(id: String): VocabularyItem? = allFlow.value.firstOrNull { it.id == id }

    override suspend fun count(): Int = allFlow.value.size
}

class FakeReviewRepository : ReviewRepository {

    val dueFlow = MutableStateFlow<List<ReviewItem>>(emptyList())
    val scheduled = mutableListOf<ReviewItem>()
    val attempts = mutableListOf<ReviewAttempt>()

    override fun observeDueCount(now: Long): Flow<Int> = dueFlow.map { it.size }

    override suspend fun getDue(now: Long, limit: Int): List<ReviewItem> = dueFlow.value.take(limit)

    override suspend fun countDue(now: Long): Int = dueFlow.value.size

    override suspend fun getItem(contentId: String): ReviewItem? =
        dueFlow.value.firstOrNull { it.contentId == contentId }

    override suspend fun schedule(item: ReviewItem) {
        scheduled += item
        dueFlow.value = dueFlow.value.filterNot { it.contentId == item.contentId } + item
    }

    override suspend fun recordAttempt(attempt: ReviewAttempt) {
        attempts += attempt
    }

    override suspend fun getAttempts(contentId: String): List<ReviewAttempt> =
        attempts.filter { it.contentId == contentId }

    override suspend fun reset() {
        dueFlow.value = emptyList()
    }
}

class FakeProgressRepository : ProgressRepository {

    val masteryFlow = MutableStateFlow<Map<Skill, Float>>(emptyMap())
    val statsFlow = MutableStateFlow(StudyStats(0L, 0, 0, 0, 0, 0f))
    val attempts = mutableListOf<Pair<Skill, Boolean>>()
    var studySeconds: Long = 0L

    override fun observeMastery(): Flow<Map<Skill, Float>> = masteryFlow
    override fun observeStats(): Flow<StudyStats> = statsFlow

    override suspend fun applyAttempt(skill: Skill, correct: Boolean, source: String) {
        attempts += skill to correct
        val current = masteryFlow.value.toMutableMap()
        val previous = current[skill] ?: 0f
        val target = if (correct) 1f else 0f
        current[skill] = previous + (target - previous) * 0.3f
        masteryFlow.value = current
    }

    override suspend fun addStudySeconds(seconds: Long) {
        studySeconds += seconds
    }

    override suspend fun reset() {
        masteryFlow.value = emptyMap()
    }
}

class FakeKnowledgeRepository(
    items: List<KnowledgeItem> = emptyList(),
) : KnowledgeRepository {

    val itemsFlow = MutableStateFlow(items)
    val statesFlow = MutableStateFlow<List<KnowledgeState>>(emptyList())
    val recorded = mutableListOf<Pair<String, Boolean>>()

    override fun observeStates(): Flow<List<KnowledgeState>> = statesFlow
    override fun observePractisedCount(): Flow<Int> = statesFlow.map { it.size }
    override suspend fun getState(itemId: String): KnowledgeState? = statesFlow.value.firstOrNull { it.itemId == itemId }
    override suspend fun itemsForLesson(lessonId: String): List<KnowledgeItem> =
        itemsFlow.value.filter { lessonId in it.lessonIds }

    override suspend fun allItems(): List<KnowledgeItem> = itemsFlow.value

    override suspend fun recordAttempt(lessonId: String, skill: Skill, correct: Boolean, now: Long) {
        recorded += lessonId to correct
    }

    override suspend fun reset() {
        statesFlow.value = emptyList()
    }
}

/** Audio player that reports a working English voice and finishes instantly. */
class FakeAudioPlayer(available: Boolean? = true) : AudioPlayer {

    override val isEnglishAvailable = MutableStateFlow(available)
    override var isSpeaking: Boolean = false
    var stopped: Boolean = false

    override fun speak(text: String, onDone: (() -> Unit)?, onError: (() -> Unit)?) {
        onDone?.invoke()
    }

    override fun stop() {
        stopped = true
    }

    override fun release() = Unit
}

/** Records what a ViewModel asked to speak; never throws. */
class RecordingSpeaker : Speaker {
    val spoken = mutableListOf<String>()

    override fun speak(text: String, onDone: (() -> Unit)?, onError: (() -> Unit)?) {
        spoken += text
        onDone?.invoke()
    }
}
