package su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity

import kotlinx.serialization.Serializable

@Serializable
data class CommunityMessage(
    val messageId: String,
    val userId: String,
    val createdAt: String,
    val deletedAt: String?,
    val text: String?,
    val userName: String?,
    val userRole: String?,
    val attachments: List<CommunityAttachment> = emptyList(),
    val embeds: List<CommunityEmbed> = emptyList(),
    val replies: List<CommunityMessage> = emptyList()
)

@Serializable
data class CommunityAttachment(
    val name: String? = null,
    val path: String? = null,
    val thumbUrl: String? = null
)

@Serializable
data class CommunityEmbed(
    val type: String? = null,
    val text: String? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val thumbUrl: String? = null,
    val titleLink: String? = null
)
