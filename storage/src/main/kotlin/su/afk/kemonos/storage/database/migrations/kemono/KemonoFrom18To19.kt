package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom18To19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.recreateVideoInfoTable()
    }
}

internal fun SupportSQLiteDatabase.recreateVideoInfoTable() {
    execSQL("DROP TABLE IF EXISTS `video_info`")
    execSQL(
        "CREATE TABLE IF NOT EXISTS `video_info` (" +
                "`site` TEXT NOT NULL, " +
                "`path` TEXT NOT NULL, " +
                "`durationMs` INTEGER NOT NULL, " +
                "`sizeBytes` INTEGER NOT NULL, " +
                "`durationSeconds` INTEGER, " +
                "`lastStatusCode` INTEGER, " +
                "`createdAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`site`, `path`))"
    )
    execSQL("CREATE INDEX IF NOT EXISTS `index_video_info_createdAt` ON `video_info` (`createdAt`)")
}
