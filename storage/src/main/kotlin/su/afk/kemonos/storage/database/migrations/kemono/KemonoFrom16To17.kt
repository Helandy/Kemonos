package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom16To17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS creator_profile_cache_dms (
                service TEXT NOT NULL,
                profileId TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, profileId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_dms_cachedAt
            ON creator_profile_cache_dms(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_dms_service_cachedAt
            ON creator_profile_cache_dms(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS creator_profile_cache_tags (
                service TEXT NOT NULL,
                profileId TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, profileId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_tags_cachedAt
            ON creator_profile_cache_tags(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_tags_service_cachedAt
            ON creator_profile_cache_tags(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS creator_profile_cache_announcements (
                service TEXT NOT NULL,
                profileId TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, profileId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_announcements_cachedAt
            ON creator_profile_cache_announcements(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_announcements_service_cachedAt
            ON creator_profile_cache_announcements(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS creator_profile_cache_fancards (
                service TEXT NOT NULL,
                profileId TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, profileId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_fancards_cachedAt
            ON creator_profile_cache_fancards(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_fancards_service_cachedAt
            ON creator_profile_cache_fancards(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS creator_profile_cache_links (
                service TEXT NOT NULL,
                profileId TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, profileId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_links_cachedAt
            ON creator_profile_cache_links(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_links_service_cachedAt
            ON creator_profile_cache_links(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS creator_profile_cache_similar (
                service TEXT NOT NULL,
                profileId TEXT NOT NULL,
                json TEXT NOT NULL,
                cachedAt INTEGER NOT NULL,
                PRIMARY KEY(service, profileId)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_similar_cachedAt
            ON creator_profile_cache_similar(cachedAt)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_creator_profile_cache_similar_service_cachedAt
            ON creator_profile_cache_similar(service, cachedAt)
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT OR REPLACE INTO creator_profile_cache_dms(service, profileId, json, cachedAt)
            SELECT service, profileId, json, cachedAt
            FROM creator_profile_cache
            WHERE type IN ('DMS', 0, '0')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO creator_profile_cache_tags(service, profileId, json, cachedAt)
            SELECT service, profileId, json, cachedAt
            FROM creator_profile_cache
            WHERE type IN ('TAGS', 1, '1')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO creator_profile_cache_announcements(service, profileId, json, cachedAt)
            SELECT service, profileId, json, cachedAt
            FROM creator_profile_cache
            WHERE type IN ('ANNOUNCEMENTS', 2, '2')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO creator_profile_cache_fancards(service, profileId, json, cachedAt)
            SELECT service, profileId, json, cachedAt
            FROM creator_profile_cache
            WHERE type IN ('FANCARDS', 3, '3')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO creator_profile_cache_links(service, profileId, json, cachedAt)
            SELECT service, profileId, json, cachedAt
            FROM creator_profile_cache
            WHERE type IN ('LINKS', 4, '4')
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT OR REPLACE INTO creator_profile_cache_similar(service, profileId, json, cachedAt)
            SELECT service, profileId, json, cachedAt
            FROM creator_profile_cache
            WHERE type IN ('SIMILAR', 5, '5')
            """.trimIndent()
        )

        db.execSQL("DROP TABLE IF EXISTS creator_profile_cache")
    }
}
