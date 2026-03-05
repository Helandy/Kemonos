package su.afk.kemonos.storage.database.migrations.community

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CommunityFrom1To2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS community_cache_channels (
                service TEXT NOT NULL,
                id TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, id)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_community_cache_channels_cachedAt
            ON community_cache_channels(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_community_cache_channels_service_cachedAt
            ON community_cache_channels(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS community_cache_messages_page0 (
                service TEXT NOT NULL,
                id TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, id)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_community_cache_messages_page0_cachedAt
            ON community_cache_messages_page0(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_community_cache_messages_page0_service_cachedAt
            ON community_cache_messages_page0(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR REPLACE INTO community_cache_channels(service, id, json, cachedAt)
            SELECT service, id, json, cachedAt
            FROM community_cache
            WHERE type IN ('CHANNELS', 0, '0')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO community_cache_messages_page0(service, id, json, cachedAt)
            SELECT service, id, json, cachedAt
            FROM community_cache
            WHERE type IN ('MESSAGES_PAGE0', 1, '1')
            """.trimIndent()
        )

        db.execSQL("DROP TABLE IF EXISTS community_cache")
    }
}
