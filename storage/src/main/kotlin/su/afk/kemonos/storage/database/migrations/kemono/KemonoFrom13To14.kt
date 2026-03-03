package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom13To14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
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
