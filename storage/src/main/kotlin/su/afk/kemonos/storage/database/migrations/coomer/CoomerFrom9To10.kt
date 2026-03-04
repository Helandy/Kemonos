package su.afk.kemonos.storage.database.migrations.coomer

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CoomerFrom9To10 = object : Migration(9, 10) {
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
