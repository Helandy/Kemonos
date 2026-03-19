package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom17To18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        rebuildKemonoSchema(db)
    }
}
