package su.afk.kemonos.storage.entity.creatorProfileCache

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "creator_profile_cache_links",
    primaryKeys = ["service", "profileId"],
    indices = [
        Index(value = ["cachedAt"]),
        Index(value = ["service", "cachedAt"])
    ]
)
data class CreatorProfileLinksCacheEntity(
    val service: String,
    val profileId: String,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis(),
)
