package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.addColumnIfMissing
import su.afk.kemonos.storage.database.migrations.hasTable

val KEMONO_MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        ensurePostContentCache(db)
        ensureCreatorPostsCache(db)
        ensureFavoritePosts(db)
        ensurePostsSearchCache(db)

        db.addColumnIfMissing("post_content_cache", "substring", "TEXT")
        db.addColumnIfMissing("creator_posts_cache", "substring", "TEXT")
        db.addColumnIfMissing("favorite_posts", "substring", "TEXT")
        db.addColumnIfMissing("posts_search_cache", "substring", "TEXT")
    }
}

private fun ensurePostContentCache(db: SupportSQLiteDatabase) {
    if (db.hasTable("post_content_cache")) return

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS post_content_cache (
            service TEXT NOT NULL,
            userId TEXT NOT NULL,
            postId TEXT NOT NULL,
            title TEXT,
            content TEXT,
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
            videosJson TEXT,
            previewsJson TEXT,
            revisionsJson TEXT,
            nextId TEXT,
            prevId TEXT,
            cachedAt INTEGER NOT NULL,
            PRIMARY KEY(service, userId, postId)
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_post_content_cache_cachedAt
        ON post_content_cache(cachedAt)
        """.trimIndent()
    )
}

private fun ensureCreatorPostsCache(db: SupportSQLiteDatabase) {
    if (db.hasTable("creator_posts_cache")) return

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS creator_posts_cache (
            queryKey TEXT NOT NULL,
            offset INTEGER NOT NULL,
            id TEXT NOT NULL,
            userId TEXT NOT NULL,
            service TEXT NOT NULL,
            title TEXT,
            published TEXT,
            substring TEXT,
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
            PRIMARY KEY(queryKey, offset, id)
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_creator_posts_cache_queryKey_offset
        ON creator_posts_cache(queryKey, offset)
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_creator_posts_cache_updatedAt
        ON creator_posts_cache(updatedAt)
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_creator_posts_cache_queryKey_updatedAt
        ON creator_posts_cache(queryKey, updatedAt)
        """.trimIndent()
    )
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
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_favorite_posts_site_favedSeq_id
        ON favorite_posts(site, favedSeq, id)
        """.trimIndent()
    )
}

private fun ensurePostsSearchCache(db: SupportSQLiteDatabase) {
    if (db.hasTable("posts_search_cache")) return

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS posts_search_cache (
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
        CREATE INDEX IF NOT EXISTS index_posts_search_cache_queryKey_offset_indexInPage
        ON posts_search_cache(queryKey, offset, indexInPage)
        """.trimIndent()
    )
    db.execSQL(
        """
        CREATE INDEX IF NOT EXISTS index_posts_search_cache_updatedAt
        ON posts_search_cache(updatedAt)
        """.trimIndent()
    )
}
