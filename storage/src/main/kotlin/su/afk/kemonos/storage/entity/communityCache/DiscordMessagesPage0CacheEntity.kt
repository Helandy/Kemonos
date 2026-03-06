package su.afk.kemonos.storage.entity.communityCache

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "discord_cache_messages_page0",
    primaryKeys = ["id"],
    indices = [
        Index(value = ["cachedAt"]),
    ]
)
data class DiscordMessagesPage0CacheEntity(
    val id: String,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis(),
)
