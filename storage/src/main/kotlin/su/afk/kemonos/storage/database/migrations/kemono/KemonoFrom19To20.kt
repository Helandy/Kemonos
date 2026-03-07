package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom19To20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.recreateVideoInfoTable()
    }
}
