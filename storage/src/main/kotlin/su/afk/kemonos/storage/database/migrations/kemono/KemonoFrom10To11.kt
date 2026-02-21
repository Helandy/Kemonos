package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom10To11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS posts_search_history (
                query TEXT NOT NULL,
                updatedAt INTEGER NOT NULL,
                PRIMARY KEY(query)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_posts_search_history_updatedAt
            ON posts_search_history(updatedAt)
            """.trimIndent()
        )
    }
}
