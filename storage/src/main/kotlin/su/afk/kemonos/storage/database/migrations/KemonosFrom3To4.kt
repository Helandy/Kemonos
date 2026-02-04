package su.afk.kemonos.storage.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom3To4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_posts ADD COLUMN incompleteRewardsJson TEXT")
        db.execSQL("ALTER TABLE post_content_cache ADD COLUMN incompleteRewardsJson TEXT")
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN incompleteRewardsJson TEXT")
        db.execSQL("ALTER TABLE creator_posts_cache ADD COLUMN incompleteRewardsJson TEXT")
    }
}