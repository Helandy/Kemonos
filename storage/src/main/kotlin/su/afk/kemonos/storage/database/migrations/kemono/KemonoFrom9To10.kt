package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom9To10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS blacklisted_authors (
                service TEXT NOT NULL,
                creatorId TEXT NOT NULL,
                creatorName TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(service, creatorId)
            )
            """.trimIndent()
        )
    }
}
