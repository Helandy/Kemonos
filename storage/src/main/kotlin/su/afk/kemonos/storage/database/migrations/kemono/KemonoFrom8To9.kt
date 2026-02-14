package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom8To9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE tracked_downloads
            ADD COLUMN lastStatus INTEGER
            """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE tracked_downloads
            ADD COLUMN lastReason INTEGER
            """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE tracked_downloads
            ADD COLUMN lastErrorLabel TEXT
            """.trimIndent()
        )
        db.execSQL(
            """
            ALTER TABLE tracked_downloads
            ADD COLUMN lastSeenAtMs INTEGER
            """.trimIndent()
        )
    }
}
