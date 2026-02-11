package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom6To7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS fresh_favorite_artist_updates (
                site TEXT NOT NULL,
                name TEXT NOT NULL,
                service TEXT NOT NULL,
                id TEXT NOT NULL,
                savedAtMs INTEGER NOT NULL,
                PRIMARY KEY(site, service, id, name)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_fresh_favorite_artist_updates_savedAtMs
            ON fresh_favorite_artist_updates(savedAtMs)
            """.trimIndent()
        )
    }
}

