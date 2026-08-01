package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val KemonoFrom21To22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `local_liked_posts` (" +
                    "`site` TEXT NOT NULL, " +
                    "`id` TEXT NOT NULL, " +
                    "`userId` TEXT NOT NULL, " +
                    "`service` TEXT NOT NULL, " +
                    "`title` TEXT, " +
                    "`content` TEXT, " +
                    "`substring` TEXT, " +
                    "`added` TEXT, " +
                    "`published` TEXT, " +
                    "`edited` TEXT, " +
                    "`incompleteRewardsJson` TEXT, " +
                    "`pollJson` TEXT, " +
                    "`fileName` TEXT, " +
                    "`filePath` TEXT, " +
                    "`attachmentsJson` TEXT, " +
                    "`tagsJson` TEXT, " +
                    "`nextId` TEXT, " +
                    "`prevId` TEXT, " +
                    "`likedAt` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`site`, `service`, `userId`, `id`))"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_local_liked_posts_site_userId` " +
                    "ON `local_liked_posts` (`site`, `userId`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_local_liked_posts_site_likedAt` " +
                    "ON `local_liked_posts` (`site`, `likedAt`)"
        )
    }
}
