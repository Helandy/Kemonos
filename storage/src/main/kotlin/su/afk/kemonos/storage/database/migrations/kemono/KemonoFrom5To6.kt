package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom5To6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE post_content_cache ADD COLUMN nextId TEXT")
        db.execSQL("ALTER TABLE post_content_cache ADD COLUMN prevId TEXT")
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN nextId TEXT")
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN prevId TEXT")
        db.execSQL("ALTER TABLE creator_posts_cache ADD COLUMN nextId TEXT")
        db.execSQL("ALTER TABLE creator_posts_cache ADD COLUMN prevId TEXT")
    }
}