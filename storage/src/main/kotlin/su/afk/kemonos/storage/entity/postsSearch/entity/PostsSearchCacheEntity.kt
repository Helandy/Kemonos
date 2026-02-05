package su.afk.kemonos.storage.entity.postsSearch.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "posts_search_cache",
    primaryKeys = ["queryKey", "offset", "id"],
    indices = [
        Index(value = ["queryKey", "offset"]),
        Index(value = ["updatedAt"])
    ]
)
data class PostsSearchCacheEntity(
    /** query|tag */
    val queryKey: String,
    /** 0, 50, 100 */
    val offset: Int,
    val id: String,

    val userId: String,
    val service: String,
    val title: String?,
    val substring: String?,
    val published: String?,
    val added: String?,
    val edited: String?,

    val incompleteRewardsJson: String?,
    val pollJson: String?,
    val fileName: String?,
    val filePath: String?,
    val attachmentsJson: String?,
    val tagsJson: String?,

    val nextId: String?,
    val prevId: String?,
    val indexInPage: Int,
    val updatedAt: Long,
)