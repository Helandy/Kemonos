package su.afk.kemonos.creatorPost.api.domain.model

import kotlinx.serialization.Serializable
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain

@Serializable
data class PostContentDomain(
    val post: PostDomain,
    val videos: List<VideoDomain> = emptyList(),
    val attachments: List<AttachmentDomain> = emptyList(),
    val previews: List<PreviewDomain> = emptyList(),
    val revisions: List<PostContentRevisionDomain> = emptyList(),
) {
    companion object {
        fun PostContentDomain.withPawchiveMediaServer(fileBaseUrl: String): PostContentDomain {
            fun AttachmentDomain.withCurrentServer() = copy(server = fileBaseUrl)
            fun PreviewDomain.withCurrentServer() =
                if (type == "thumbnail" && path != null) copy(server = fileBaseUrl) else this
            fun VideoDomain.withCurrentServer() = copy(server = fileBaseUrl)

            return copy(
                post = post.copy(attachments = post.attachments.map { it.withCurrentServer() }),
                attachments = attachments.map { it.withCurrentServer() },
                previews = previews.map { it.withCurrentServer() },
                videos = videos.map { it.withCurrentServer() },
            )
        }

    }
}

@Serializable
data class PostContentRevisionDomain(
    val revisionId: Int,
    val post: PostDomain,
    val backendRevisionId: Long? = null,
)
