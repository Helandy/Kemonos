package su.afk.kemonos.storage.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom4To5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN pollJson TEXT")
        db.execSQL("ALTER TABLE post_content_cache ADD COLUMN pollJson TEXT")
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN pollJson TEXT")
        db.execSQL("ALTER TABLE creator_posts_cache ADD COLUMN pollJson TEXT")
    }
}