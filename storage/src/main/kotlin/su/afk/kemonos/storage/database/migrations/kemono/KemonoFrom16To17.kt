package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom16To17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        rebuildKemonoSchema(db)
    }
}
