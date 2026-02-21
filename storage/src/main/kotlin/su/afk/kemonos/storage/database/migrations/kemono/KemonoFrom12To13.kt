package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom12To13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS dms_cache (
                queryKey TEXT NOT NULL,
                offset INTEGER NOT NULL,
                hash TEXT NOT NULL,
                service TEXT NOT NULL,
                user TEXT NOT NULL,
                content TEXT NOT NULL,
                added TEXT NOT NULL,
                published TEXT NOT NULL,
                artistId TEXT NOT NULL,
                artistName TEXT NOT NULL,
                artistUpdated TEXT,
                indexInPage INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                PRIMARY KEY(queryKey, offset, hash)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_dms_cache_queryKey_offset
            ON dms_cache(queryKey, offset)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_dms_cache_updatedAt
            ON dms_cache(updatedAt)
            """.trimIndent()
        )
    }
}
