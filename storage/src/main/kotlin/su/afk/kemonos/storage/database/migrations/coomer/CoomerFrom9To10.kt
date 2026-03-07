package su.afk.kemonos.storage.database.migrations.coomer

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val CoomerFrom9To10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        rebuildCoomerSchema(db)
    }
}
