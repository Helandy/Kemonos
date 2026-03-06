package su.afk.kemonos.creatorProfile.domain.repository

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage

internal data class DiscordCommunityChannels(
    val serverName: String?,
    val updated: String?,
    val channels: List<CommunityChannel>
)

internal interface IDiscordRepository {
    suspend fun getCommunityChannels(serverId: String): DiscordCommunityChannels
    suspend fun getCommunityMessages(channelId: String, offset: Int): List<CommunityMessage>
}
