package su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity

import kotlinx.serialization.Serializable

@Serializable
data class CommunityChannel(
    val channelId: String,
    val creatorId: String,
    val campaignId: String?,
    val channelTypeName: String?,
    val name: String,
    val communityGuidelines: String?,
    val emoji: String?,
    val addedAt: String?,
    val messagesRefreshedAt: String?,
    val messagesFullRefreshedAt: String?
)
