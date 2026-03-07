package su.afk.kemonos.storage.database.migrations.coomer

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val COOMER_DESTRUCTIVE_TO_10_MIGRATIONS: Array<Migration> =
    (1..8).map { fromVersion ->
        object : Migration(fromVersion, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                rebuildCoomerSchema(db)
            }
        }
    }.toTypedArray()

internal fun rebuildCoomerSchema(db: SupportSQLiteDatabase) {
    val dropSql = listOf(
        "DROP TABLE IF EXISTS `creators`",
        "DROP TABLE IF EXISTS `tags`",
        "DROP TABLE IF EXISTS `posts_search_cache`",
        "DROP TABLE IF EXISTS `posts_search_history`",
        "DROP TABLE IF EXISTS `dms_cache`",
        "DROP TABLE IF EXISTS `posts_popular_cache`",
    )
    dropSql.forEach(db::execSQL)

    val createSql = listOf(
        "CREATE TABLE IF NOT EXISTS `creators` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `service` TEXT NOT NULL, `favorited` INTEGER NOT NULL, `indexed` INTEGER NOT NULL, `updated` INTEGER NOT NULL, PRIMARY KEY(`id`))",
        "CREATE INDEX IF NOT EXISTS `index_creators_service` ON `creators` (`service`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_favorited` ON `creators` (`favorited`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_indexed` ON `creators` (`indexed`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_updated` ON `creators` (`updated`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_service_favorited` ON `creators` (`service`, `favorited`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_service_indexed` ON `creators` (`service`, `indexed`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_service_updated` ON `creators` (`service`, `updated`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_service_name` ON `creators` (`service`, `name`)",
        "CREATE INDEX IF NOT EXISTS `index_creators_name` ON `creators` (`name`)",
        "CREATE TABLE IF NOT EXISTS `tags` (`tags` TEXT NOT NULL, `count` INTEGER, PRIMARY KEY(`tags`))",
        "CREATE TABLE IF NOT EXISTS `posts_search_cache` (`queryKey` TEXT NOT NULL, `offset` INTEGER NOT NULL, `id` TEXT NOT NULL, `userId` TEXT NOT NULL, `service` TEXT NOT NULL, `title` TEXT, `substring` TEXT, `published` TEXT, `added` TEXT, `edited` TEXT, `incompleteRewardsJson` TEXT, `pollJson` TEXT, `fileName` TEXT, `filePath` TEXT, `attachmentsJson` TEXT, `tagsJson` TEXT, `nextId` TEXT, `prevId` TEXT, `indexInPage` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`queryKey`, `offset`, `service`, `userId`, `id`))",
        "CREATE INDEX IF NOT EXISTS `index_posts_search_cache_queryKey_offset_indexInPage` ON `posts_search_cache` (`queryKey`, `offset`, `indexInPage`)",
        "CREATE INDEX IF NOT EXISTS `index_posts_search_cache_updatedAt` ON `posts_search_cache` (`updatedAt`)",
        "CREATE TABLE IF NOT EXISTS `posts_search_history` (`query` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`query`))",
        "CREATE INDEX IF NOT EXISTS `index_posts_search_history_updatedAt` ON `posts_search_history` (`updatedAt`)",
        "CREATE TABLE IF NOT EXISTS `dms_cache` (`queryKey` TEXT NOT NULL, `offset` INTEGER NOT NULL, `hash` TEXT NOT NULL, `service` TEXT NOT NULL, `user` TEXT NOT NULL, `content` TEXT NOT NULL, `added` TEXT NOT NULL, `published` TEXT NOT NULL, `artistId` TEXT NOT NULL, `artistName` TEXT NOT NULL, `artistUpdated` TEXT, `indexInPage` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`queryKey`, `offset`, `hash`))",
        "CREATE INDEX IF NOT EXISTS `index_dms_cache_queryKey_offset` ON `dms_cache` (`queryKey`, `offset`)",
        "CREATE INDEX IF NOT EXISTS `index_dms_cache_updatedAt` ON `dms_cache` (`updatedAt`)",
        "CREATE TABLE IF NOT EXISTS `posts_popular_cache` (`queryKey` TEXT NOT NULL, `offset` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `payloadJson` TEXT NOT NULL, PRIMARY KEY(`queryKey`, `offset`))",
        "CREATE INDEX IF NOT EXISTS `index_posts_popular_cache_queryKey_updatedAt` ON `posts_popular_cache` (`queryKey`, `updatedAt`)",
    )
    createSql.forEach(db::execSQL)
}
