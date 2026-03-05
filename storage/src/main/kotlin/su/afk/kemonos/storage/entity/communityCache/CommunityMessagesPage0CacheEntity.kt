package su.afk.kemonos.storage.entity.communityCache

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "community_cache_messages_page0",
    primaryKeys = ["service", "id"],
    indices = [
        Index(value = ["cachedAt"]),
        Index(value = ["service", "cachedAt"])
    ]
)
data class CommunityMessagesPage0CacheEntity(
    val service: String,
    val id: String,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis(),
)
