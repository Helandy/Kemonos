package su.afk.kemonos.storage.entity.comments.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import su.afk.kemonos.creatorPost.api.domain.model.CommentRevision

@Entity(
    tableName = "comment_revisions",
    primaryKeys = ["service", "userId", "postId", "commentId", "revisionId"],
    foreignKeys = [
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["service", "userId", "postId", "commentId"],
            childColumns = ["service", "userId", "postId", "commentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["service", "userId", "postId", "commentId"])
    ]
)
data class CommentRevisionEntity(
    val service: String,
    val userId: String,
    val postId: String,

    val commentId: String,
    val revisionId: Int,

    val added: String,
    val content: String
) {
    companion object {
        fun CommentRevision.toEntity(
            service: String,
            userId: String,
            postId: String,
            commentId: String
        ): CommentRevisionEntity = CommentRevisionEntity(
            service = service,
            userId = userId,
            postId = postId,
            commentId = commentId,
            revisionId = id,
            added = added,
            content = content
        )


        fun CommentRevisionEntity.toDomain(): CommentRevision =
            CommentRevision(
                id = revisionId,
                added = added,
                content = content
            )
    }
}