package org.token.english.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.dao.KnowledgeDao
import org.token.english.data.local.dao.ProgressDao
import org.token.english.data.local.dao.ReviewDao
import org.token.english.data.local.entity.ExerciseEntity
import org.token.english.data.local.entity.KnowledgeItemEntity
import org.token.english.data.local.entity.KnowledgeStateEntity
import org.token.english.data.local.entity.LessonEntity
import org.token.english.data.local.entity.LessonStateEntity
import org.token.english.data.local.entity.ReviewAttemptEntity
import org.token.english.data.local.entity.ReviewItemEntity
import org.token.english.data.local.entity.SkillMasteryEntity
import org.token.english.data.local.entity.StudySessionEntity
import org.token.english.data.local.entity.VocabularyEntity

@Database(
    entities = [
        LessonEntity::class,
        VocabularyEntity::class,
        ExerciseEntity::class,
        KnowledgeItemEntity::class,
        KnowledgeStateEntity::class,
        LessonStateEntity::class,
        ReviewItemEntity::class,
        ReviewAttemptEntity::class,
        SkillMasteryEntity::class,
        StudySessionEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun reviewDao(): ReviewDao
    abstract fun progressDao(): ProgressDao
    abstract fun knowledgeDao(): KnowledgeDao

    companion object {
        /**
         * v1 → v2: review_item gains introducedAt (daily new-card cap) and
         * lesson gains grammarTipFa (intro stage). Schemas are exported to
         * app/schemas (room.schemaLocation, KSP) so the next schema change can
         * ship with a tested migration too.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE review_item ADD COLUMN introducedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE lesson ADD COLUMN grammarTipFa TEXT")
            }
        }

        /**
         * v2 → v3: the knowledge graph becomes first-class curriculum data
         * (audit §4/§6). Purely additive — a new content table, so no learner
         * progress is touched. Rows arrive from the seeder, which re-runs because
         * the bundle contentVersion moved to 6.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS knowledge_item (" +
                        "id TEXT NOT NULL, " +
                        "type TEXT NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "titleFa TEXT NOT NULL, " +
                        "level TEXT NOT NULL, " +
                        "prerequisitesJson TEXT NOT NULL, " +
                        "lessonIdsJson TEXT NOT NULL, " +
                        "skillsJson TEXT NOT NULL, " +
                        "PRIMARY KEY(id))",
                )
            }
        }

        /**
         * v3 → v4: learner state per knowledge item (audit §20). Additive and
         * empty on arrival — it fills up as the learner answers exercises, and
         * no existing progress row is touched.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS knowledge_state (" +
                        "itemId TEXT NOT NULL, " +
                        "mastery REAL NOT NULL, " +
                        "exposureCount INTEGER NOT NULL, " +
                        "consecutiveCorrect INTEGER NOT NULL, " +
                        "consecutiveIncorrect INTEGER NOT NULL, " +
                        "intervalDays INTEGER NOT NULL, " +
                        "easeFactor REAL NOT NULL, " +
                        "repetitions INTEGER NOT NULL, " +
                        "lapses INTEGER NOT NULL, " +
                        "lastReviewedAt INTEGER, " +
                        "nextReviewAt INTEGER NOT NULL, " +
                        "PRIMARY KEY(itemId))",
                )
            }
        }
    }
}
