package su.afk.kemonos.storage.entity.comments

import androidx.room.Embedded
import androidx.room.Relation
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.storage.entity.comments.entity.CommentEntity
import su.afk.kemonos.storage.entity.comments.entity.CommentRevisionEntity
import su.afk.kemonos.storage.entity.comments.entity.CommentRevisionEntity.Companion.toDomain

data class CommentWithRevisions(
    @Embedded val comment: CommentEntity,
    @Relation(
        parentColumn = "commentId",
        entityColumn = "commentId",
        entity = CommentRevisionEntity::class
    )
    val revisions: List<CommentRevisionEntity>
) {
    companion object {
        fun CommentWithRevisions.toDomain(): CommentDomain = CommentDomain(
            id = comment.commentId,
            commenter = comment.commenter,
            commenter_name = comment.commenterName,
            content = comment.content,
            published = comment.published,
            revisions = revisions
                .sortedBy { it.revisionId }
                .map { it.toDomain() }
        )
    }
}