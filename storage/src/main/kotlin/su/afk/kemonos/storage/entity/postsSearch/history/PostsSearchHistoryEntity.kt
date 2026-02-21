package su.afk.kemonos.storage.entity.postsSearch.history

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "posts_search_history",
    primaryKeys = ["query"],
    indices = [Index(value = ["updatedAt"])]
)
data class PostsSearchHistoryEntity(
    val query: String,
    val updatedAt: Long,
)
