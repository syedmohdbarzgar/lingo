package org.token.english.data.content

import android.util.Log
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.VocabularyEntity
import org.token.english.domain.repository.SettingsRepository

/**
 * Loads the offline curriculum bundle (assets/content) into Room.
 * Local Room database is the single source of truth (technical spec §31).
 *
 * Re-seeding is keyed by the `contentVersion` field authored inside the JSON
 * (see [ContentParser.bundleVersion]) versus the version stored in settings —
 * content authors bump the field, no Kotlin change needed. The clear+insert
 * runs as one Room transaction ([ContentDao.replaceContent]), so a crash can
 * never leave a half-seeded database.
 */
class ContentSeeder(
    private val dao: ContentDao,
    private val readAsset: (String) -> String,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun ensureSeeded() {
        val lessonsJson = readAsset("content/lessons.json")
        val bundleVersion = ContentParser.bundleVersion(lessonsJson)
        val current = settingsRepository.settings.first().contentVersion
        // The knowledge table is checked too: an install seeded before the graph
        // existed must still receive it (MIGRATION_2_3 only creates the table).
        if (current == bundleVersion && dao.countVocabulary() > 0 && dao.countKnowledgeItems() > 0) return

        val vocabularyJson = readAsset("content/vocabulary.json")
        val exercisesJson = readAsset("content/exercises.json")
        val placementJson = readAsset("content/placement.json")
        val knowledgeJson = readAsset("content/knowledge.json")
        warnOnVersionMismatch("vocabulary.json", vocabularyJson, bundleVersion)
        warnOnVersionMismatch("exercises.json", exercisesJson, bundleVersion)
        warnOnVersionMismatch("placement.json", placementJson, bundleVersion)
        warnOnVersionMismatch("knowledge.json", knowledgeJson, bundleVersion)

        val lessons = ContentParser.parseLessons(lessonsJson)
        val vocabulary = ContentParser.parseVocabulary(vocabularyJson)
        val knowledge = ContentParser.parseKnowledge(knowledgeJson)
        val exercises = ContentParser.parseRawExercises(exercisesJson) +
            ContentParser.parseRawPlacement(placementJson)

        dao.replaceContent(
            lessons = lessons.map {
                LessonEntity(
                    id = it.id,
                    level = it.level.name,
                    title = it.title,
                    titleFa = it.titleFa,
                    topic = it.topic,
                    estimatedMinutes = it.estimatedMinutes,
                    orderIndex = it.order,
                    grammarTipFa = it.grammarTipFa,
                )
            },
            vocabulary = vocabulary.map {
                VocabularyEntity(
                    id = it.id,
                    word = it.word,
                    translation = it.translation,
                    definition = it.definition,
                    pronunciation = it.pronunciation,
                    level = it.level.name,
                    partOfSpeech = it.partOfSpeech,
                    examplesJson = JSONArray(it.examples).toString(),
                    collocationsJson = JSONArray(it.collocations).toString(),
                    lessonId = it.lessonId,
                )
            },
            exercises = exercises.map {
                ExerciseEntity(
                    id = it.id,
                    lessonId = it.lessonId,
                    type = it.type,
                    orderIndex = it.orderIndex,
                    payloadJson = it.payloadJson,
                )
            },
            knowledge = knowledge.map {
                KnowledgeItemEntity(
                    id = it.id,
                    type = it.type.name,
                    title = it.title,
                    titleFa = it.titleFa,
                    level = it.level.name,
                    prerequisitesJson = JSONArray(it.prerequisites).toString(),
                    lessonIdsJson = JSONArray(it.lessonIds).toString(),
                    skillsJson = JSONArray(it.skills.map { skill -> skill.name }).toString(),
                )
            },
        )

        settingsRepository.setContentVersion(bundleVersion)
    }

    private fun warnOnVersionMismatch(file: String, json: String, bundleVersion: Int) {
        val version = ContentParser.bundleVersion(json)
        if (version != bundleVersion) {
            Log.w(TAG, "$file contentVersion=$version differs from lessons.json=$bundleVersion")
        }
    }

    companion object {
        private const val TAG = "ContentSeeder"
    }
}
