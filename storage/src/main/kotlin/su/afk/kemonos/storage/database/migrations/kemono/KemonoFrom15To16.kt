package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom15To16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP INDEX IF EXISTS index_posts_search_cache_queryKey_offset")
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_posts_search_cache_queryKey_offset_indexInPage
            ON posts_search_cache(queryKey, offset, indexInPage)
            """.trimIndent()
        )
    }
}
