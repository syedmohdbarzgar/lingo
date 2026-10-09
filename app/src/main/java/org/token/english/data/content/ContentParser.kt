package org.token.english.data.content

import org.json.JSONArray
import org.json.JSONObject
import org.token.english.domain.model.Exercise
import org.token.english.domain.model.KnowledgeItem
import org.token.english.domain.model.KnowledgeType
import org.token.english.domain.model.LearningLevel
import org.token.english.domain.model.Lesson
import org.token.english.domain.model.Skill
import org.token.english.domain.model.VocabularyItem

/**
 * Parses bundled curriculum JSON (assets/content) into domain models.
 * Uses org.json — no serialization plugin needed, content stays code-independent
 * (technical spec §61/§63: stable string IDs, content evolves without app code changes).
 */
object ContentParser {

    const val PLACEMENT_LESSON_ID = "placement"

    /**
     * Bundle version authored inside the JSON itself (top-level `contentVersion`).
     * Content authors bump it whenever any bundled file changes — [ContentSeeder]
     * re-seeds when it differs from the stored version, so extending content
     * needs no Kotlin change (technical spec §63).
     */
    fun bundleVersion(json: String): Int = JSONObject(json).optInt("contentVersion", 1)

    /** Types the app actually runs; others are parsed-but-inactive (see AGENTS.md). */
    private val supportedTypes = setOf("multiple_choice", "fill_blank", "translation", "listening")

    data class RawExercise(
        val id: String,
        val lessonId: String,
        val type: String,
        val orderIndex: Int,
        val payloadJson: String,
    )

    /** Reads the full exercises file into storable rows (seeding only). */
    fun parseRawExercises(json: String): List<RawExercise> {
        val array = JSONObject(json).getJSONArray("exercises")
        val result = mutableListOf<RawExercise>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            val type = o.optString("type")
            if (type !in supportedTypes) continue
            result += RawExercise(
                id = o.getString("id"),
                lessonId = o.getString("lessonId"),
                type = type,
                orderIndex = o.getInt("order"),
                payloadJson = o.toString(),
            )
        }
        return result
    }

    /** Parses a single stored payload back into the domain model. */
    fun exerciseFromPayload(payloadJson: String, lessonId: String): Exercise? =
        runCatching { parseExercise(JSONObject(payloadJson), lessonId) }.getOrNull()

    /** Placement test questions live under a virtual lesson id. */
    fun parseRawPlacement(json: String): List<RawExercise> {
        val array = JSONObject(json).getJSONArray("questions")
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            RawExercise(
                id = o.getString("id"),
                lessonId = PLACEMENT_LESSON_ID,
                type = "multiple_choice",
                orderIndex = i,
                payloadJson = o.toString(),
            )
        }
    }

    fun parseLessons(json: String): List<Lesson> {
        val root = JSONObject(json)
        val array = root.getJSONArray("lessons")
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            Lesson(
                id = o.getString("id"),
                level = LearningLevel.valueOf(o.getString("level")),
                title = o.getString("title"),
                titleFa = o.getString("titleFa"),
                topic = o.getString("topic"),
                estimatedMinutes = o.getInt("estimatedMinutes"),
                order = o.getInt("order"),
                grammarTipFa = o.optString("grammarTipFa").ifBlank { null },
            )
        }
    }

    /**
     * Parses the curriculum knowledge graph (assets/content/knowledge.json).
     * The graph is authored content, so it goes through the same JSON pipeline as
     * lessons and vocabulary — no Kotlin change is needed to add a knowledge item.
     */
    fun parseKnowledge(json: String): List<KnowledgeItem> {
        val array = JSONObject(json).getJSONArray("knowledge")
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            KnowledgeItem(
                id = o.getString("id"),
                type = KnowledgeType.valueOf(o.getString("type").uppercase()),
                title = o.getString("title"),
                titleFa = o.getString("titleFa"),
                level = LearningLevel.valueOf(o.getString("level")),
                prerequisites = o.optJSONArraytoString("prerequisites"),
                lessonIds = o.optJSONArraytoString("lessons"),
                skills = o.optJSONArraytoString("skills")
                    .mapNotNull { runCatching { Skill.valueOf(it.uppercase()) }.getOrNull() },
            )
        }
    }

    fun parseVocabulary(json: String): List<VocabularyItem> {
        val root = JSONObject(json)
        val array = root.getJSONArray("vocabulary")
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            VocabularyItem(
                id = o.getString("id"),
                word = o.getString("word"),
                translation = o.getString("translation"),
                definition = o.optString("definition").ifBlank { null },
                pronunciation = o.optString("pronunciation").ifBlank { null },
                level = LearningLevel.valueOf(o.getString("level")),
                partOfSpeech = o.optString("partOfSpeech").ifBlank { null },
                examples = o.parseExamples(),
                collocations = o.optJSONArraytoString("collocations"),
                lessonId = o.optString("lessonId").ifBlank { null },
                // Authored Persian usage note (A-1) — shown after a missed review card.
                explanationFa = o.optString("explanationFa").ifBlank { null },
            )
        }
    }

    /** Returns ordered exercises; unknown/suspended types are skipped gracefully. */
    fun parseExercises(json: String, lessonId: String): List<Pair<String, Exercise>> {
        val root = JSONObject(json)
        val array = root.getJSONArray("exercises")
        val result = mutableListOf<Pair<String, Exercise>>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            if (o.optString("lessonId", lessonId) != lessonId) continue
            parseExercise(o, lessonId)?.let { result += o.getString("id") to it }
        }
        return result
    }

    fun parsePlacementQuestions(json: String): List<Exercise> {
        val root = JSONObject(json)
        val array = root.getJSONArray("questions")
        return (0 until array.length()).mapNotNull { i ->
            parseExercise(array.getJSONObject(i), PLACEMENT_LESSON_ID)
        }
    }

    fun parseExercise(o: JSONObject, lessonId: String): Exercise? {
        val id = o.getString("id")
        val type = o.getString("type")
        val skill = o.skillOrNull()
        val explanation = o.optString("explanationFa").ifBlank { null }
        return when (type) {
            "multiple_choice" -> Exercise.MultipleChoice(
                id = id,
                lessonId = lessonId,
                question = o.getString("question"),
                questionFa = o.optString("questionFa").ifBlank { null },
                options = o.getJSONArray("options").let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                },
                correctIndex = o.getInt("correctIndex"),
                skill = skill,
                explanation = explanation,
            )

            "fill_blank" -> Exercise.FillBlank(
                id = id,
                lessonId = lessonId,
                sentence = o.getString("sentence"),
                accepted = o.acceptedList(),
                skill = skill,
                explanation = explanation,
            )

            "translation" -> Exercise.Translation(
                id = id,
                lessonId = lessonId,
                prompt = o.getString("prompt"),
                accepted = o.acceptedList(),
                bank = o.optJSONArraytoString("bank"),
                skill = skill,
                explanation = explanation,
            )

            "listening" -> Exercise.Listening(
                id = id,
                lessonId = lessonId,
                audioText = o.getString("text"),
                accepted = o.acceptedList(),
                skill = skill,
                explanation = explanation,
            )

            "speaking" -> Exercise.Speaking(
                id = id,
                lessonId = lessonId,
                prompt = o.getString("prompt"),
                referenceAnswers = o.acceptedList(),
                skill = skill,
                explanation = explanation,
            )

            else -> null // unknown / future types never break lesson loading
        }
    }

    /**
     * Bilingual examples (A-10). The authored shape is
     * `"examples": [{ "en": "…", "fa": "…" }]`.
     *
     * A bare string element is still accepted — an installed database written by
     * an older bundle holds the legacy `["sentence"]` array until the version-gated
     * re-seed rewrites it, and a content read must never crash on that. Such an
     * element keeps the English sentence and carries an empty translation, which is
     * exactly what `validateContent` rejects for new content.
     */
    private fun JSONObject.parseExamples(): List<org.token.english.domain.model.VocabularyExample> {
        val arr = optJSONArray("examples") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            when (val entry = arr.opt(i)) {
                is JSONObject -> {
                    val en = entry.optString("en")
                    if (en.isBlank()) null else org.token.english.domain.model.VocabularyExample(en, entry.optString("fa"))
                }
                is String -> if (entry.isBlank()) null else org.token.english.domain.model.VocabularyExample(entry, "")
                else -> null
            }
        }
    }

    private fun JSONObject.skillOrNull(): Skill? =
        optString("skill").ifBlank { null }?.let { runCatching { Skill.valueOf(it.uppercase()) }.getOrNull() }

    private fun JSONObject.acceptedList(): List<String> {
        val arr = getJSONArray("accepted")
        return (0 until arr.length()).map { arr.getString(it) }
    }

    private fun JSONObject.optJSONArraytoString(name: String): List<String> {
        val arr = optJSONArray(name) ?: return emptyList()
        return (0 until arr.length()).map { arr.getString(it) }
    }
}

/** Convenience for reading raw exercise payloads when re-serializing to Room. */
fun JSONArray.toList(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
