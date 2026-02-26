package su.afk.kemonos.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import su.afk.kemonos.storage.entity.communityCache.CommunityCacheEntity
import su.afk.kemonos.storage.entity.communityCache.dao.CommunityCacheDao

@Database(
    entities = [CommunityCacheEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class CommunityDatabase : RoomDatabase() {
    abstract fun communityCacheDao(): CommunityCacheDao
}
