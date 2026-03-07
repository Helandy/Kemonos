package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.hasTable

val KemonoFrom13To14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensureFavoritePosts(db)
        ensureFavoriteArtists(db)

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_favorite_posts_site_favedSeq_id
            ON favorite_posts(site, favedSeq, id)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_favorite_artists_site_updated
            ON favorite_artists(site, updated)
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_favorite_artists_site_lastImported
            ON favorite_artists(site, lastImported)
            """.trimIndent()
        )
    }
}

private fun ensureFavoritePosts(db: SupportSQLiteDatabase) {
    if (db.hasTable("favorite_posts")) return

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS favorite_posts (
            site TEXT NOT NULL,
            id TEXT NOT NULL,
            userId TEXT NOT NULL,
            service TEXT NOT NULL,
            title TEXT,
            content TEXT,
            substring TEXT,
            added TEXT,
            published TEXT,
            edited TEXT,
            incompleteRewardsJson TEXT,
            pollJson TEXT,
            fileName TEXT,
            filePath TEXT,
            attachmentsJson TEXT,
            tagsJson TEXT,
            nextId TEXT,
            prevId TEXT,
            favedSeq INTEGER,
            favCount INTEGER,
            cachedAt INTEGER NOT NULL,
            PRIMARY KEY(site, service, userId, id)
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_favorite_posts_site_userId
        ON favorite_posts(site, userId)
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_favorite_posts_site_cachedAt
        ON favorite_posts(site, cachedAt)
        """.trimIndent()
    )
}

private fun ensureFavoriteArtists(db: SupportSQLiteDatabase) {
    if (db.hasTable("favorite_artists")) return

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS favorite_artists (
            site TEXT NOT NULL,
            id TEXT NOT NULL,
            service TEXT NOT NULL,
            favedSeq INTEGER NOT NULL,
            indexed TEXT NOT NULL,
            lastImported TEXT NOT NULL,
            name TEXT NOT NULL,
            publicId TEXT NOT NULL,
            relationId INTEGER NOT NULL,
            updated TEXT NOT NULL,
            PRIMARY KEY(site, service, id)
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_favorite_artists_site_favedSeq
        ON favorite_artists(site, favedSeq)
        """.trimIndent()
    )
}
