package su.afk.kemonos.storage.entity.communityCache

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType

@Entity(
    tableName = "community_cache",
    primaryKeys = ["service", "id", "type"],
    indices = [
        Index(value = ["service", "id"]),
        Index(value = ["cachedAt"]),
    ]
)
data class CommunityCacheEntity(
    val service: String,
    val id: String,
    val type: CommunityCacheType,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis(),
)
