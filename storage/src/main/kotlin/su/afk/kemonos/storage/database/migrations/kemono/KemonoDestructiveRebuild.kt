package su.afk.kemonos.storage.database.migrations.kemono

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import su.afk.kemonos.storage.database.migrations.DestructiveMigrationPrefSync

internal val KEMONO_DESTRUCTIVE_TO_18_MIGRATIONS: Array<Migration> =
    (1..16).map { fromVersion ->
        object : Migration(fromVersion, 18) {
            override fun migrate(db: SupportSQLiteDatabase) {
                rebuildKemonoSchema(db)
            }
        }
    }.toTypedArray()

internal fun rebuildKemonoSchema(db: SupportSQLiteDatabase) {
    db.execSQL("PRAGMA foreign_keys=OFF")

    val dropSql = listOf(
        "DROP TABLE IF EXISTS `comment_revisions`",
        "DROP TABLE IF EXISTS `comments`",
        "DROP TABLE IF EXISTS `creator_profile_cache`",
        "DROP TABLE IF EXISTS `creator_profile_cache_dms`",
        "DROP TABLE IF EXISTS `creator_profile_cache_tags`",
        "DROP TABLE IF EXISTS `creator_profile_cache_announcements`",
        "DROP TABLE IF EXISTS `creator_profile_cache_fancards`",
        "DROP TABLE IF EXISTS `creator_profile_cache_links`",
        "DROP TABLE IF EXISTS `creator_profile_cache_similar`",
        "DROP TABLE IF EXISTS `creators`",
        "DROP TABLE IF EXISTS `favorite_artists`",
        "DROP TABLE IF EXISTS `favorite_posts`",
        "DROP TABLE IF EXISTS `fresh_favorite_artist_updates`",
        "DROP TABLE IF EXISTS `profiles`",
        "DROP TABLE IF EXISTS `tags`",
        "DROP TABLE IF EXISTS `video_info`",
        "DROP TABLE IF EXISTS `creator_posts_cache`",
        "DROP TABLE IF EXISTS `post_content_cache`",
        "DROP TABLE IF EXISTS `posts_search_cache`",
        "DROP TABLE IF EXISTS `posts_search_history`",
        "DROP TABLE IF EXISTS `dms_cache`",
        "DROP TABLE IF EXISTS `posts_popular_cache`",
        "DROP TABLE IF EXISTS `tracked_downloads`",
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
        "CREATE TABLE IF NOT EXISTS `favorite_artists` (`site` TEXT NOT NULL, `id` TEXT NOT NULL, `service` TEXT NOT NULL, `favedSeq` INTEGER NOT NULL, `indexed` TEXT NOT NULL, `lastImported` TEXT NOT NULL, `name` TEXT NOT NULL, `publicId` TEXT NOT NULL, `relationId` INTEGER NOT NULL, `updated` TEXT NOT NULL, PRIMARY KEY(`site`, `service`, `id`))",
        "CREATE INDEX IF NOT EXISTS `index_favorite_artists_site_favedSeq` ON `favorite_artists` (`site`, `favedSeq`)",
        "CREATE INDEX IF NOT EXISTS `index_favorite_artists_site_updated` ON `favorite_artists` (`site`, `updated`)",
        "CREATE INDEX IF NOT EXISTS `index_favorite_artists_site_lastImported` ON `favorite_artists` (`site`, `lastImported`)",
        "CREATE TABLE IF NOT EXISTS `favorite_posts` (`site` TEXT NOT NULL, `id` TEXT NOT NULL, `userId` TEXT NOT NULL, `service` TEXT NOT NULL, `title` TEXT, `content` TEXT, `substring` TEXT, `added` TEXT, `published` TEXT, `edited` TEXT, `incompleteRewardsJson` TEXT, `pollJson` TEXT, `fileName` TEXT, `filePath` TEXT, `attachmentsJson` TEXT, `tagsJson` TEXT, `nextId` TEXT, `prevId` TEXT, `favedSeq` INTEGER, `favCount` INTEGER, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`site`, `service`, `userId`, `id`))",
        "CREATE INDEX IF NOT EXISTS `index_favorite_posts_site_userId` ON `favorite_posts` (`site`, `userId`)",
        "CREATE INDEX IF NOT EXISTS `index_favorite_posts_site_cachedAt` ON `favorite_posts` (`site`, `cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_favorite_posts_site_favedSeq_id` ON `favorite_posts` (`site`, `favedSeq`, `id`)",
        "CREATE TABLE IF NOT EXISTS `fresh_favorite_artist_updates` (`site` TEXT NOT NULL, `name` TEXT NOT NULL, `service` TEXT NOT NULL, `id` TEXT NOT NULL, `savedAtMs` INTEGER NOT NULL, PRIMARY KEY(`site`, `service`, `id`, `name`))",
        "CREATE INDEX IF NOT EXISTS `index_fresh_favorite_artist_updates_savedAtMs` ON `fresh_favorite_artist_updates` (`savedAtMs`)",
        "CREATE TABLE IF NOT EXISTS `profiles` (`id` TEXT NOT NULL, `service` TEXT NOT NULL, `name` TEXT NOT NULL, `indexed` TEXT, `updated` TEXT, `publicId` TEXT, `hasChats` INTEGER, `relationId` INTEGER, `cachedAt` INTEGER NOT NULL, `postCount` INTEGER, `dmCount` INTEGER, `shareCount` INTEGER, `chatCount` INTEGER, PRIMARY KEY(`service`, `id`))",
        "CREATE INDEX IF NOT EXISTS `index_profiles_cachedAt` ON `profiles` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_profiles_service_id_cachedAt` ON `profiles` (`service`, `id`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_profile_cache_dms` (`service` TEXT NOT NULL, `profileId` TEXT NOT NULL, `json` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `profileId`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_dms_cachedAt` ON `creator_profile_cache_dms` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_dms_service_cachedAt` ON `creator_profile_cache_dms` (`service`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_profile_cache_tags` (`service` TEXT NOT NULL, `profileId` TEXT NOT NULL, `json` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `profileId`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_tags_cachedAt` ON `creator_profile_cache_tags` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_tags_service_cachedAt` ON `creator_profile_cache_tags` (`service`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_profile_cache_announcements` (`service` TEXT NOT NULL, `profileId` TEXT NOT NULL, `json` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `profileId`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_announcements_cachedAt` ON `creator_profile_cache_announcements` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_announcements_service_cachedAt` ON `creator_profile_cache_announcements` (`service`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_profile_cache_fancards` (`service` TEXT NOT NULL, `profileId` TEXT NOT NULL, `json` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `profileId`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_fancards_cachedAt` ON `creator_profile_cache_fancards` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_fancards_service_cachedAt` ON `creator_profile_cache_fancards` (`service`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_profile_cache_links` (`service` TEXT NOT NULL, `profileId` TEXT NOT NULL, `json` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `profileId`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_links_cachedAt` ON `creator_profile_cache_links` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_links_service_cachedAt` ON `creator_profile_cache_links` (`service`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_profile_cache_similar` (`service` TEXT NOT NULL, `profileId` TEXT NOT NULL, `json` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `profileId`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_similar_cachedAt` ON `creator_profile_cache_similar` (`cachedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_profile_cache_similar_service_cachedAt` ON `creator_profile_cache_similar` (`service`, `cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `comments` (`service` TEXT NOT NULL, `userId` TEXT NOT NULL, `postId` TEXT NOT NULL, `commentId` TEXT NOT NULL, `commenter` TEXT NOT NULL, `commenterName` TEXT, `content` TEXT NOT NULL, `published` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `userId`, `postId`, `commentId`))",
        "CREATE INDEX IF NOT EXISTS `index_comments_service_userId_postId` ON `comments` (`service`, `userId`, `postId`)",
        "CREATE INDEX IF NOT EXISTS `index_comments_cachedAt` ON `comments` (`cachedAt`)",
        "CREATE TABLE IF NOT EXISTS `comment_revisions` (`service` TEXT NOT NULL, `userId` TEXT NOT NULL, `postId` TEXT NOT NULL, `commentId` TEXT NOT NULL, `revisionId` INTEGER NOT NULL, `added` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`service`, `userId`, `postId`, `commentId`, `revisionId`), FOREIGN KEY(`service`, `userId`, `postId`, `commentId`) REFERENCES `comments`(`service`, `userId`, `postId`, `commentId`) ON UPDATE NO ACTION ON DELETE CASCADE)",
        "CREATE INDEX IF NOT EXISTS `index_comment_revisions_service_userId_postId_commentId` ON `comment_revisions` (`service`, `userId`, `postId`, `commentId`)",
        "CREATE TABLE IF NOT EXISTS `tags` (`tags` TEXT NOT NULL, `count` INTEGER, PRIMARY KEY(`tags`))",
        "CREATE TABLE IF NOT EXISTS `video_info` (`site` TEXT NOT NULL, `path` TEXT NOT NULL, `durationMs` INTEGER NOT NULL, `sizeBytes` INTEGER NOT NULL, `durationSeconds` INTEGER, `lastStatusCode` INTEGER, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`site`, `path`))",
        "CREATE INDEX IF NOT EXISTS `index_video_info_createdAt` ON `video_info` (`createdAt`)",
        "CREATE TABLE IF NOT EXISTS `creator_posts_cache` (`queryKey` TEXT NOT NULL, `offset` INTEGER NOT NULL, `id` TEXT NOT NULL, `userId` TEXT NOT NULL, `service` TEXT NOT NULL, `title` TEXT, `published` TEXT, `substring` TEXT, `added` TEXT, `edited` TEXT, `incompleteRewardsJson` TEXT, `pollJson` TEXT, `fileName` TEXT, `filePath` TEXT, `attachmentsJson` TEXT, `tagsJson` TEXT, `nextId` TEXT, `prevId` TEXT, `indexInPage` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`queryKey`, `offset`, `id`))",
        "CREATE INDEX IF NOT EXISTS `index_creator_posts_cache_queryKey_offset` ON `creator_posts_cache` (`queryKey`, `offset`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_posts_cache_updatedAt` ON `creator_posts_cache` (`updatedAt`)",
        "CREATE INDEX IF NOT EXISTS `index_creator_posts_cache_queryKey_updatedAt` ON `creator_posts_cache` (`queryKey`, `updatedAt`)",
        "CREATE TABLE IF NOT EXISTS `post_content_cache` (`service` TEXT NOT NULL, `userId` TEXT NOT NULL, `postId` TEXT NOT NULL, `title` TEXT, `content` TEXT, `substring` TEXT, `published` TEXT, `added` TEXT, `edited` TEXT, `incompleteRewardsJson` TEXT, `pollJson` TEXT, `fileName` TEXT, `filePath` TEXT, `attachmentsJson` TEXT, `tagsJson` TEXT, `videosJson` TEXT, `previewsJson` TEXT, `revisionsJson` TEXT, `nextId` TEXT, `prevId` TEXT, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `userId`, `postId`))",
        "CREATE INDEX IF NOT EXISTS `index_post_content_cache_cachedAt` ON `post_content_cache` (`cachedAt`)",
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
        "CREATE TABLE IF NOT EXISTS `tracked_downloads` (`downloadId` INTEGER NOT NULL, `url` TEXT NOT NULL, `fileName` TEXT, `service` TEXT, `creatorName` TEXT, `postId` TEXT, `postTitle` TEXT, `createdAtMs` INTEGER NOT NULL, `lastStatus` INTEGER, `lastReason` INTEGER, `lastErrorLabel` TEXT, `lastSeenAtMs` INTEGER, PRIMARY KEY(`downloadId`))",
        "CREATE INDEX IF NOT EXISTS `index_tracked_downloads_createdAtMs` ON `tracked_downloads` (`createdAtMs`)",
        "CREATE TABLE IF NOT EXISTS `blacklisted_authors` (`service` TEXT NOT NULL, `creatorId` TEXT NOT NULL, `creatorName` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`service`, `creatorId`))",
    )
    createSql.forEach(db::execSQL)

    DestructiveMigrationPrefSync.markDestructiveRebuild(db, scope = "kemono")
    db.execSQL("PRAGMA foreign_keys=ON")
}
