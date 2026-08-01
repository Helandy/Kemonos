package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom22To23 = object : Migration(22, 23) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `local_liked_artists` (" +
                    "`site` TEXT NOT NULL, " +
                    "`id` TEXT NOT NULL, " +
                    "`service` TEXT NOT NULL, " +
                    "`name` TEXT NOT NULL, " +
                    "`indexed` TEXT NOT NULL, " +
                    "`publicId` TEXT NOT NULL, " +
                    "`relationId` INTEGER NOT NULL, " +
                    "`updated` TEXT NOT NULL, " +
                    "`likedAt` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`site`, `service`, `id`))"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_local_liked_artists_site_likedAt` " +
                    "ON `local_liked_artists` (`site`, `likedAt`)"
        )
    }
}
