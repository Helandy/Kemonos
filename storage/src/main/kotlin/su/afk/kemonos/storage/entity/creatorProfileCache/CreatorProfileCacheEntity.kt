package su.afk.kemonos.storage.entity.creatorProfileCache

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.storage.api.creatorProfileCache.CreatorProfileCacheType

@Entity(
    tableName = "creator_profile_cache",
    primaryKeys = ["service", "profileId", "type"],
    indices = [
        Index(value = ["service", "profileId"]),
        Index(value = ["cachedAt"]),
        Index(value = ["service", "cachedAt"])
    ]
)
data class CreatorProfileCacheEntity(
    val service: String,
    val profileId: String,
    val type: CreatorProfileCacheType,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis(),
)