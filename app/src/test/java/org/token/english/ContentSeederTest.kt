package org.token.english

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.token.english.data.content.ContentParser
import org.token.english.data.content.ContentSeeder
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonAttemptEntity
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
        val knowledge = linkedMapOf<String, KnowledgeItemEntity>()
        var insertCalls = 0

        override fun observeLessons(): Flow<List<LessonEntity>> = flowOf(lessons.values.sortedBy { it.orderIndex })
        override suspend fun getLesson(id: String): LessonEntity? = lessons[id]
        override fun observeLessonStates(): Flow<List<LessonStateEntity>> = flowOf(emptyList())
        override fun observeCompletedLessonCount(): Flow<Int> = flowOf(0)
        override suspend fun ensureLessonState(lessonId: String) = Unit
        override suspend fun updateLessonIndex(lessonId: String, index: Int) = Unit
        override suspend fun markLessonCompleted(lessonId: String, at: Long) = Unit
        override suspend fun updateLessonStage(
            lessonId: String,
            stage: String,
            completed: String,
            skipped: String,
            completedAt: String,
        ) = Unit
        override suspend fun getLessonState(lessonId: String): LessonStateEntity? = null
        override suspend fun insertLessonAttempt(item: LessonAttemptEntity) = Unit
        override suspend fun getLessonAttempts(lessonId: String): List<LessonAttemptEntity> = emptyList()
        override suspend fun getRecentLessonAttempts(limit: Int): List<LessonAttemptEntity> = emptyList()
        override suspend fun clearLessonAttempts() = Unit
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

        override suspend fun getKnowledgeItems(): List<KnowledgeItemEntity> =
            knowledge.values.sortedBy { it.id }

        override suspend fun countKnowledgeItems(): Int = knowledge.size

        override suspend fun insertKnowledgeItems(items: List<KnowledgeItemEntity>) {
            insertCalls++
            items.forEach { knowledge[it.id] = it }
        }

        override suspend fun clearLessons() = lessons.clear()
        override suspend fun clearVocabulary() = vocabulary.clear()
        override suspend fun clearExercises() = exercises.clear()
        override suspend fun clearKnowledgeItems() = knowledge.clear()
        override suspend fun clearLessonStates() = Unit

        override suspend fun lessonIdsWithDialogue(): List<String> = emptyList()
    }

    private class FakeSettings : SettingsRepository {
        private var contentVersionValue = 0
        var writes = 0

        /** Set to fail the next version write, i.e. die between the content and its stamp. */
        var failNextVersionWrite = false

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

        override suspend fun setSubscriptionUntil(epochMillis: Long) = Unit
        override suspend fun completeFirstLaunch() = Unit
        override suspend fun setLevel(level: LearningLevel) = Unit
        override suspend fun setThemeMode(mode: ThemeMode) = Unit
        override suspend fun setDailyGoalMinutes(minutes: Int) = Unit
        override suspend fun setSoundEnabled(enabled: Boolean) = Unit
        override suspend fun setDailyReminderEnabled(enabled: Boolean) = Unit
        override suspend fun setDailyReminderHour(hour: Int) = Unit

        override suspend fun setContentVersion(version: Int) {
            if (failNextVersionWrite) {
                failNextVersionWrite = false
                throw IllegalStateException("the preferences file could not be written")
            }
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

        assertTrue("lessons seeded", dao.lessons.size >= 38)
        assertTrue("vocabulary seeded", dao.vocabulary.size >= 228)
        assertTrue("exercises + placement seeded", dao.exercises.size >= 258)
        assertTrue("knowledge graph seeded", dao.knowledge.size >= 72)
        // Read the expected version from the bundle itself so a content bump
        // never needs a test edit (the seeder stores exactly what the JSON says).
        assertEquals(
            "stored version comes from the JSON",
            ContentParser.bundleVersion(assetReader("content/lessons.json")),
            settings.storedVersion,
        )
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

    // ---- recovery paths (checklist P1) ---------------------------------------

    @Test
    fun `a bundle that cannot be read writes nothing and stamps no version`() = runBlocking {
        val dao = FakeContentDao()
        val settings = FakeSettings()
        val seeder = ContentSeeder(
            dao = dao,
            readAsset = { path -> throw IllegalStateException("asset missing: $path") },
            settingsRepository = settings,
        )

        val thrown = runCatching { seeder.ensureSeeded() }

        assertTrue("the failure must reach the caller, which turns it into a state", thrown.isFailure)
        assertEquals("nothing was written", 0, dao.insertCalls)
        assertTrue(dao.lessons.isEmpty())
        assertEquals(
            "a failed seed must not claim the bundle was written",
            0,
            settings.storedVersion,
        )
    }

    @Test
    fun `a malformed asset is not half-written`() = runBlocking {
        val dao = FakeContentDao()
        val settings = FakeSettings()
        val seeder = ContentSeeder(
            dao = dao,
            readAsset = { path ->
                // An array where the parser expects the bundle object: a truncated or
                // damaged asset used to be indistinguishable from an empty one here.
                if (path == "content/vocabulary.json") "[]" else assetReader(path)
            },
            settingsRepository = settings,
        )

        val thrown = runCatching { seeder.ensureSeeded() }

        assertTrue("a parse failure must reach the caller", thrown.isFailure)
        assertEquals("no partial bundle is visible", 0, dao.insertCalls)
        assertEquals("and it is not stamped as seeded", 0, settings.storedVersion)
    }

    /**
     * The seed writes the content in one transaction and the stamp afterwards, so the
     * crash window is between the two — not in the middle of the rows. What must hold:
     * the bundle is either complete or absent, and the next run finishes the job
     * instead of duplicating it.
     */
    @Test
    fun `a seed interrupted before the stamp leaves a complete bundle and the retry finishes it`() = runBlocking {
        val dao = FakeContentDao()
        val settings = FakeSettings()
        val seeder = ContentSeeder(dao, ::assetReader, settings)
        settings.failNextVersionWrite = true

        val thrown = runCatching { seeder.ensureSeeded() }

        assertTrue("the failed stamp reaches the caller", thrown.isFailure)
        assertEquals("the content itself committed", 0, settings.storedVersion)
        val lessons = dao.lessons.size
        val vocabulary = dao.vocabulary.size
        val exercises = dao.exercises.size
        assertTrue("the bundle is complete, not half-written", lessons >= 38 && vocabulary >= 228)

        // The retry: the version still differs, so the seeder runs again and stamps it.
        seeder.ensureSeeded()

        assertEquals(
            "the stamp now matches the bundle",
            ContentParser.bundleVersion(assetReader("content/lessons.json")),
            settings.storedVersion,
        )
        assertEquals("re-seeding replaces rather than appends", lessons, dao.lessons.size)
        assertEquals("re-seeding replaces rather than appends", vocabulary, dao.vocabulary.size)
        assertEquals("re-seeding replaces rather than appends", exercises, dao.exercises.size)
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
