package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.addColumnIfMissing

val KemonoFrom3To4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.addColumnIfMissing("favorite_posts", "incompleteRewardsJson", "TEXT")
        db.addColumnIfMissing("post_content_cache", "incompleteRewardsJson", "TEXT")
        db.addColumnIfMissing("posts_search_cache", "incompleteRewardsJson", "TEXT")
        db.addColumnIfMissing("creator_posts_cache", "incompleteRewardsJson", "TEXT")
    }
}
