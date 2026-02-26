package su.afk.kemonos.creatorProfile.data.dto.profileCommunity

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel

internal data class CommunityChannelDto(
    @SerializedName("channel_id")
    val channelId: String,
    @SerializedName("creator_id")
    val creatorId: String,
    @SerializedName("campaign_id")
    val campaignId: String?,
    @SerializedName("channel_type_name")
    val channelTypeName: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("community_guidelines")
    val communityGuidelines: String?,
    @SerializedName("emoji")
    val emoji: String?,
    @SerializedName("added_at")
    val addedAt: String?,
    @SerializedName("messages_refreshed_at")
    val messagesRefreshedAt: String?,
    @SerializedName("messages_full_refreshed_at")
    val messagesFullRefreshedAt: String?
) {
    companion object {
        fun CommunityChannelDto.toDomain(): CommunityChannel = CommunityChannel(
            channelId = channelId,
            creatorId = creatorId,
            campaignId = campaignId,
            channelTypeName = channelTypeName,
            name = name,
            communityGuidelines = communityGuidelines,
            emoji = emoji,
            addedAt = addedAt,
            messagesRefreshedAt = messagesRefreshedAt,
            messagesFullRefreshedAt = messagesFullRefreshedAt
        )

        fun List<CommunityChannelDto>.toDomain(): List<CommunityChannel> = map { it.toDomain() }
    }
}
