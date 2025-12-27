package su.afk.kemonos.storage.entity.popular

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "posts_popular_cache",
    primaryKeys = ["queryKey", "offset"],
    indices = [
        Index("queryKey", "updatedAt")
    ]
)
data class PostsPopularCacheEntity(
    val queryKey: String,
    val offset: Int,
    val updatedAt: Long,
    val payloadJson: String,
)