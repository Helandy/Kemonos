package su.afk.kemonos.creatorPost.data.dto.comments


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.CommentRevision
import su.afk.kemonos.creatorPost.data.dto.comments.Revision.Companion.toDomain

internal data class ProfilePostCommentsDto(
    @SerializedName("commenter")
    val commenter: String,
    @SerializedName("commenter_name")
    val commenter_name: String?,
    @SerializedName("content")
    val content: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("published")
    val published: String,
    @SerializedName("revisions")
    val revisions: List<Revision>
) {
    companion object {
        fun ProfilePostCommentsDto.toDomain(): CommentDomain {
            return CommentDomain(
                id = id,
                commenter = commenter,
                commenter_name = commenter_name,
                content = content,
                published = published,
                revisions = revisions.map { it.toDomain() }
            )
        }
    }
}

internal data class Revision(
    @SerializedName("added")
    val added: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("id")
    val id: Int
) {
    companion object {
        fun Revision.toDomain(): CommentRevision {
            return CommentRevision(
                id = id,
                added = added,
                content = content
            )
        }
    }
}