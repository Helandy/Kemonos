package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.addColumnIfMissing

val KemonoFrom11To12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.addColumnIfMissing("post_content_cache", "revisionsJson", "TEXT")
    }
}
