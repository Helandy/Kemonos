package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KEMONO_MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE post_content_cache ADD COLUMN substring TEXT")
        db.execSQL("ALTER TABLE creator_posts_cache ADD COLUMN substring TEXT")
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN substring TEXT")
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN substring TEXT")
    }
}