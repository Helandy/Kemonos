package su.afk.kemonos.storage.database.migrations.coomer

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CoomerFrom5To6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN nextId TEXT")
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN prevId TEXT")
    }
}