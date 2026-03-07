package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.addColumnIfMissing

val KemonoFrom4To5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.addColumnIfMissing("favorite_posts", "pollJson", "TEXT")
        db.addColumnIfMissing("post_content_cache", "pollJson", "TEXT")
        db.addColumnIfMissing("posts_search_cache", "pollJson", "TEXT")
        db.addColumnIfMissing("creator_posts_cache", "pollJson", "TEXT")
    }
}
