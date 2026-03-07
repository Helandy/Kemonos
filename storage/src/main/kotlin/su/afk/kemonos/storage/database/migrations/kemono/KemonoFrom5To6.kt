package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.addColumnIfMissing

val KemonoFrom5To6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.addColumnIfMissing("post_content_cache", "nextId", "TEXT")
        db.addColumnIfMissing("post_content_cache", "prevId", "TEXT")
        db.addColumnIfMissing("posts_search_cache", "nextId", "TEXT")
        db.addColumnIfMissing("posts_search_cache", "prevId", "TEXT")
        db.addColumnIfMissing("creator_posts_cache", "nextId", "TEXT")
        db.addColumnIfMissing("creator_posts_cache", "prevId", "TEXT")
    }
}
