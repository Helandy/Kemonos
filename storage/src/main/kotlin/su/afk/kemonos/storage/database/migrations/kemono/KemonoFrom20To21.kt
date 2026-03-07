package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom20To21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.recreateVideoInfoTable()
    }
}
