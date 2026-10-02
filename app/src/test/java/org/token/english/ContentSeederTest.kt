package org.token.english

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentSeeder
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonStateEntity
import org.token.english.data.local.entity.VocabularyEntity
import org.token.english.domain.model.AppSettings
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.ThemeMode
import org.token.english.domain.repository.SettingsRepository
import org.token.english.core.billing.TrialAndSubscription

/**
 * ContentSeeder against the real bundled assets with an in-memory ContentDao:
 * seeding writes the whole bundle, runs through the transactional replace, and
 * the JSON `contentVersion` gates re-seeding (no Kotlin constant involved).
 */
class ContentSeederTest {

    private fun assetReader(path: String): String {
        val candidates = listOf(
            java.io.File("src/main/assets/$path"),
            java.io.File("app/src/main/assets/$path"),
        )
        return candidates.firstOrNull { it.exists() }?.readText() ?: error("asset not found: $path")
    }

    private class FakeContentDao : ContentDao {
        val lessons = linkedMapOf<String, LessonEntity>()
        val vocabulary = linkedMapOf<String, VocabularyEntity>()
        val exercises = linkedMapOf<String, ExerciseEntity>()
        var insertCalls = 0

        override fun observeLessons(): Flow<List<LessonEntity>> = flowOf(lessons.values.sortedBy { it.orderIndex })
        override suspend fun getLesson(id: String): LessonEntity? = lessons[id]
        override fun observeLessonStates(): Flow<List<LessonStateEntity>> = flowOf(emptyList())
        override fun observeCompletedLessonCount(): Flow<Int> = flowOf(0)
        override suspend fun ensureLessonState(lessonId: String) = Unit
        override suspend fun updateLessonIndex(lessonId: String, index: Int) = Unit
        override suspend fun markLessonCompleted(lessonId: String, at: Long) = Unit
        override suspend fun getLessonState(lessonId: String): LessonStateEntity? = null
        override fun observeVocabulary(): Flow<List<VocabularyEntity>> = flowOf(vocabulary.values.sortedBy { it.word })
        override suspend fun getVocabularyByLesson(lessonId: String): List<VocabularyEntity> =
            vocabulary.values.filter { it.lessonId == lessonId }

        override suspend fun getVocabulary(id: String): VocabularyEntity? = vocabulary[id]
        override suspend fun searchVocabulary(query: String): List<VocabularyEntity> =
            vocabulary.values.filter {
                it.word.contains(query, ignoreCase = true) || it.translation.contains(query, ignoreCase = true)
            }

        override suspend fun countVocabulary(): Int = vocabulary.size
        override suspend fun getExercises(lessonId: String): List<ExerciseEntity> =
            exercises.values.filter { it.lessonId == lessonId }.sortedBy { it.orderIndex }

        override suspend fun insertLessons(items: List<LessonEntity>) {
            insertCalls++
            items.forEach { lessons[it.id] = it }
        }

        override suspend fun insertVocabulary(items: List<VocabularyEntity>) {
            insertCalls++
            items.forEach { vocabulary[it.id] = it }
        }

        override suspend fun insertExercises(items: List<ExerciseEntity>) {
            insertCalls++
            items.forEach { exercises[it.id] = it }
        }

        override suspend fun clearLessons() = lessons.clear()
        override suspend fun clearVocabulary() = vocabulary.clear()
        override suspend fun clearExercises() = exercises.clear()
        override suspend fun clearLessonStates() = Unit
    }

    private class FakeSettings : SettingsRepository {
        private var contentVersionValue = 0
        var writes = 0

        private val state = kotlinx.coroutines.flow.MutableStateFlow(
            AppSettings(
                isFirstLaunch = true,
                level = LearningLevel.A1,
                themeMode = ThemeMode.SYSTEM,
                dailyGoalMinutes = 15,
                soundEnabled = true,
                contentVersion = 0,
                trialStartedAt = 0L,
                subscriptionUntil = 0L,
            ),
        )

        override val settings: Flow<AppSettings> get() = state
        override fun observeTrialAndSubscription(): Flow<TrialAndSubscription> = flowOf(
            TrialAndSubscription(0L, 0L, 0L, 0L, 0L),
        )

        override suspend fun ensureTrialStarted(now: Long, elapsedRealtime: Long) = Unit
        override suspend fun saveTrialCheckpoint(consumedMs: Long, lastWallMs: Long, lastElapsedMs: Long) = Unit
        override suspend fun setSubscriptionUntil(epochMillis: Long) = Unit
        override suspend fun completeFirstLaunch() = Unit
        override suspend fun setLevel(level: LearningLevel) = Unit
        override suspend fun setThemeMode(mode: ThemeMode) = Unit
        override suspend fun setDailyGoalMinutes(minutes: Int) = Unit
        override suspend fun setSoundEnabled(enabled: Boolean) = Unit
        override suspend fun setDailyReminderEnabled(enabled: Boolean) = Unit

        override suspend fun setContentVersion(version: Int) {
            contentVersionValue = version
            writes++
            state.value = state.value.copy(contentVersion = version)
        }

        val storedVersion: Int get() = contentVersionValue
    }

    @Test
    fun `seeding loads the full bundle and stores the json content version`() = runBlocking {
        val dao = FakeContentDao()
        val settings = FakeSettings()
        ContentSeeder(dao, ::assetReader, settings).ensureSeeded()

        assertTrue("lessons seeded", dao.lessons.size >= 24)
        assertTrue("vocabulary seeded", dao.vocabulary.size >= 144)
        assertTrue("exercises + placement seeded", dao.exercises.size >= 174)
        assertEquals("stored version comes from the JSON", 4, settings.storedVersion)
        // Grammar tips survive the seed (intro stage needs them).
        assertTrue(dao.lessons.values.all { it.grammarTipFa != null })
    }

    @Test
    fun `second run is a no-op while the version matches`() = runBlocking {
        val dao = FakeContentDao()
        val settings = FakeSettings()
        val seeder = ContentSeeder(dao, ::assetReader, settings)
        seeder.ensureSeeded()
        val insertsAfterFirst = dao.insertCalls
        seeder.ensureSeeded()
        assertEquals("no re-seed when version matches", insertsAfterFirst, dao.insertCalls)

        // Bump the stored version → next run re-seeds.
        settings.setContentVersion(0)
        seeder.ensureSeeded()
        assertTrue("re-seeds after a version change", dao.insertCalls > insertsAfterFirst)
    }

    @Test
    fun `replace clears stale rows instead of accumulating them`() = runBlocking {
        val dao = FakeContentDao()
        dao.lessons["stale.lesson"] = LessonEntity(
            id = "stale.lesson",
            level = "A1",
            title = "Old",
            titleFa = "قدیمی",
            topic = "x",
            estimatedMinutes = 5,
            orderIndex = 999,
            grammarTipFa = null,
        )
        val settings = FakeSettings()
        ContentSeeder(dao, ::assetReader, settings).ensureSeeded()
        assertTrue("stale row removed by replaceContent", !dao.lessons.containsKey("stale.lesson"))
        assertTrue(dao.lessons.none { it.key.startsWith("stale") })
    }
}
