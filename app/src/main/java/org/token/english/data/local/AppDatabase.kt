package org.token.english.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.token.english.data.local.dao.ContentDao
import org.token.english.data.local.dao.ProgressDao
import org.token.english.data.local.dao.ReviewDao
import org.token.english.data.local.entity.ExerciseEntity
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
        LessonStateEntity::class,
        ReviewItemEntity::class,
        ReviewAttemptEntity::class,
        SkillMasteryEntity::class,
        StudySessionEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun reviewDao(): ReviewDao
    abstract fun progressDao(): ProgressDao

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
    }
}
