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

/**
 * The schema version. Bump it with every schema change: it must equal the newest
 * exported schema and the highest migration target, which `RoomMigrationTest`
 * checks on the JVM (Room itself only notices a mismatch at open time on an
 * upgrading install).
 */
internal const val DATABASE_VERSION = 5

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
    version = DATABASE_VERSION,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun reviewDao(): ReviewDao
    abstract fun progressDao(): ProgressDao
    abstract fun knowledgeDao(): KnowledgeDao

    companion object {
        /**
         * Every migration's SQL, kept as **data** rather than inline calls so
         * `RoomMigrationTest` can check each step against the exported schemas in
         * `app/schemas` on the JVM — no device or emulator needed. Room itself only
         * validates a migration when it actually runs on an upgrading install, and
         * AGENTS.md §10 records that a missing path crashes that install, which is
         * exactly the case these statements are here to make testable.
         *
         * Keys are `"from->to"`.
         */
        val migrationStatements: Map<String, List<String>> = mapOf(
            // v1 → v2: review_item gains introducedAt (daily new-card cap) and
            // lesson gains grammarTipFa (intro stage).
            "1->2" to listOf(
                "ALTER TABLE review_item ADD COLUMN introducedAt INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE lesson ADD COLUMN grammarTipFa TEXT",
            ),
            // v2 → v3: the knowledge graph becomes first-class curriculum data
            // (audit §4/§6). Purely additive — a new content table, so no learner
            // progress is touched. Rows arrive from the seeder, which re-runs because
            // the bundle contentVersion moved to 6.
            "2->3" to listOf(
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
            ),
            // v3 → v4: learner state per knowledge item (audit §20). Additive and
            // empty on arrival — it fills up as the learner answers exercises, and
            // no existing progress row is touched.
            "3->4" to listOf(
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
            ),
            // v4 → v5: vocabulary gains an authored Persian usage note (checklist
            // A-1), shown in the review session after a miss. Additive and nullable,
            // and the seeder refills it because the bundle contentVersion was bumped
            // in the same release (see the vocabulary `explanationFa` batch).
            "4->5" to listOf(
                "ALTER TABLE vocabulary ADD COLUMN explanationFa TEXT",
            ),
        )

        val MIGRATION_1_2 = migration(1, 2)
        val MIGRATION_2_3 = migration(2, 3)
        val MIGRATION_3_4 = migration(3, 4)
        val MIGRATION_4_5 = migration(4, 5)

        /** Every migration this database ships, oldest first. */
        val ALL_MIGRATIONS: List<Migration>
            get() = listOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)

        fun migration(from: Int, to: Int): Migration = object : Migration(from, to) {
            override fun migrate(db: SupportSQLiteDatabase) {
                migrationStatements.getValue("$from->$to").forEach(db::execSQL)
            }
        }
    }
}
