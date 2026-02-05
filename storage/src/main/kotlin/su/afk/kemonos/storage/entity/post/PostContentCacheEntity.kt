package su.afk.kemonos.storage.entity.post

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "post_content_cache",
    primaryKeys = ["service", "userId", "postId"],
    indices = [Index("cachedAt")]
)
data class PostContentCacheEntity(
    val service: String,
    val userId: String,
    val postId: String,

    val title: String?,
    val content: String?,
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
    val videosJson: String?,
    val previewsJson: String?,

    val nextId: String?,
    val prevId: String?,
    val cachedAt: Long,
)