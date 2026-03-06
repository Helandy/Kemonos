package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import su.afk.kemonos.storage.entity.communityCache.CommunityChannelsCacheEntity
import su.afk.kemonos.storage.entity.communityCache.CommunityMessagesPage0CacheEntity
import su.afk.kemonos.storage.entity.communityCache.DiscordChannelsCacheEntity
import su.afk.kemonos.storage.entity.communityCache.DiscordMessagesPage0CacheEntity
import su.afk.kemonos.storage.entity.communityCache.dao.CommunityCacheDao
import su.afk.kemonos.storage.entity.communityCache.dao.DiscordCacheDao

@Database(
    entities = [
        CommunityChannelsCacheEntity::class,
        CommunityMessagesPage0CacheEntity::class,
        DiscordChannelsCacheEntity::class,
        DiscordMessagesPage0CacheEntity::class,
    ],
    version = 3,
    exportSchema = false
)
internal abstract class CommunityDatabase : RoomDatabase() {
    abstract fun communityCacheDao(): CommunityCacheDao
    abstract fun discordCacheDao(): DiscordCacheDao
}
