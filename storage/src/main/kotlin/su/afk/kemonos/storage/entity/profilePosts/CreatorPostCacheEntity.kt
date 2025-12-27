package su.afk.kemonos.storage.entity.profilePosts

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "creator_posts_cache",
    primaryKeys = ["queryKey", "offset", "id"],
    indices = [
        Index(value = ["queryKey", "offset"]),
        Index(value = ["updatedAt"]),
        Index(value = ["queryKey", "updatedAt"]),
    ]
)
data class CreatorPostCacheEntity(
    /** service|userId|search|tag */
    val queryKey: String,
    /** 0, 50, 100 */
    val offset: Int,
    val id: String,

    val userId: String,
    val service: String,
    val title: String?,
    val published: String?,
    val added: String?,
    val edited: String?,

    val fileName: String?,
    val filePath: String?,
    val attachmentsJson: String?,
    val tagsJson: String?,

    val indexInPage: Int,
    val updatedAt: Long,
)