package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Массовая загрузка складывает посты в одну папку — её и запоминаем у задачи. */
val KemonoFrom23To24 = object : Migration(23, 24) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `tracked_downloads` ADD COLUMN `subDir` TEXT")
    }
}
