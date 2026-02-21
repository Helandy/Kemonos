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
)

@Serializable
data class PostContentRevisionDomain(
    val revisionId: Int,
    val post: PostDomain,
)
