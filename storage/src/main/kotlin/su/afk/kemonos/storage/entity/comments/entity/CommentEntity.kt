package su.afk.kemonos.storage.entity.comments.entity

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain

@Entity(
    tableName = "comments",
    primaryKeys = ["service", "userId", "postId", "commentId"],
    indices = [
        Index(value = ["service", "userId", "postId"]),
        Index(value = ["cachedAt"])
    ]
)
data class CommentEntity(
    val service: String,
    val userId: String,
    val postId: String,

    val commentId: String,
    val commenter: String,
    val commenterName: String?,
    val content: String,
    val published: String,

    /** время кэширования (для TTL и чистки) */
    val cachedAt: Long
) {
    companion object {
        fun CommentDomain.toEntity(
            service: String,
            userId: String,
            postId: String,
            cachedAt: Long
        ): CommentEntity = CommentEntity(
            service = service,
            userId = userId,
            postId = postId,
            commentId = id,
            commenter = commenter,
            commenterName = commenter_name,
            content = content,
            published = published,
            cachedAt = cachedAt
        )
    }
}