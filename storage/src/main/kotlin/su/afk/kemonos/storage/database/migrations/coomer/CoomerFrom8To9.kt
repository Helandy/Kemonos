package su.afk.kemonos.storage.database.migrations.coomer

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CoomerFrom8To9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS posts_search_cache_new (
                queryKey TEXT NOT NULL,
                offset INTEGER NOT NULL,
                id TEXT NOT NULL,
                userId TEXT NOT NULL,
                service TEXT NOT NULL,
                title TEXT,
                substring TEXT,
                published TEXT,
                added TEXT,
                edited TEXT,
                incompleteRewardsJson TEXT,
                pollJson TEXT,
                fileName TEXT,
                filePath TEXT,
                attachmentsJson TEXT,
                tagsJson TEXT,
                nextId TEXT,
                prevId TEXT,
                indexInPage INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                PRIMARY KEY(queryKey, offset, service, userId, id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO posts_search_cache_new (
                queryKey, offset, id, userId, service, title, substring, published, added, edited,
                incompleteRewardsJson, pollJson, fileName, filePath, attachmentsJson, tagsJson,
                nextId, prevId, indexInPage, updatedAt
            )
            SELECT
                queryKey, offset, id, userId, service, title, substring, published, added, edited,
                incompleteRewardsJson, pollJson, fileName, filePath, attachmentsJson, tagsJson,
                nextId, prevId, indexInPage, updatedAt
            FROM posts_search_cache
            """.trimIndent()
        )

        db.execSQL("DROP TABLE posts_search_cache")
        db.execSQL("ALTER TABLE posts_search_cache_new RENAME TO posts_search_cache")

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_posts_search_cache_queryKey_offset
            ON posts_search_cache(queryKey, offset)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_posts_search_cache_updatedAt
            ON posts_search_cache(updatedAt)
            """.trimIndent()
        )
    }
}
