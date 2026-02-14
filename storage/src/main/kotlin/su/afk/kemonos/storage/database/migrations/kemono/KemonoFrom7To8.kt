package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom7To8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS tracked_downloads (
                downloadId INTEGER NOT NULL,
                url TEXT NOT NULL,
                fileName TEXT,
                service TEXT,
                creatorName TEXT,
                postId TEXT,
                postTitle TEXT,
                createdAtMs INTEGER NOT NULL,
                PRIMARY KEY(downloadId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_tracked_downloads_createdAtMs
            ON tracked_downloads(createdAtMs)
            """.trimIndent()
        )
    }
}
