package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import su.afk.kemonos.storage.entity.communityCache.CommunityChannelsCacheEntity
import su.afk.kemonos.storage.entity.communityCache.CommunityMessagesPage0CacheEntity
import su.afk.kemonos.storage.entity.communityCache.dao.CommunityCacheDao

@Database(
    entities = [
        CommunityChannelsCacheEntity::class,
        CommunityMessagesPage0CacheEntity::class,
    ],
    version = 2,
    exportSchema = false
)
internal abstract class CommunityDatabase : RoomDatabase() {
    abstract fun communityCacheDao(): CommunityCacheDao
}
