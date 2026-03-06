package su.afk.kemonos.storage.database.migrations.community

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CommunityFrom2To3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS discord_cache_channels (
                id TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_discord_cache_channels_cachedAt
            ON discord_cache_channels(cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS discord_cache_messages_page0 (
                id TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_discord_cache_messages_page0_cachedAt
            ON discord_cache_messages_page0(cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR REPLACE INTO discord_cache_channels(id, json, cachedAt)
            SELECT id, json, cachedAt
            FROM community_cache_channels
            WHERE LOWER(service) = 'discord'
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO discord_cache_messages_page0(id, json, cachedAt)
            SELECT id, json, cachedAt
            FROM community_cache_messages_page0
            WHERE LOWER(service) = 'discord'
            """.trimIndent()
        )

        db.execSQL(
            """
            DELETE FROM community_cache_channels
            WHERE LOWER(service) = 'discord'
            """.trimIndent()
        )
        db.execSQL(
            """
            DELETE FROM community_cache_messages_page0
            WHERE LOWER(service) = 'discord'
            """.trimIndent()
        )
    }
}
