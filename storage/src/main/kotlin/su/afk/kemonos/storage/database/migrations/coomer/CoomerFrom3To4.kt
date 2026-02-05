package su.afk.kemonos.storage.database.migrations.coomer

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CoomerFrom3To4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE posts_search_cache ADD COLUMN incompleteRewardsJson TEXT")
    }
}