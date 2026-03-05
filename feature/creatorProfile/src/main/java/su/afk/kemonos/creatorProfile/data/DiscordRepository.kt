package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.data.api.DiscordApi
import su.afk.kemonos.creatorProfile.data.cache.CreatorProfileCacheJson
import su.afk.kemonos.creatorProfile.data.dto.profileCommunity.DiscordCommunityMapper.toCommunityMessages
import su.afk.kemonos.creatorProfile.data.dto.profileCommunity.DiscordCommunityMapper.toDiscordCommunityChannels
import su.afk.kemonos.creatorProfile.domain.repository.DiscordCommunityChannels
import su.afk.kemonos.creatorProfile.domain.repository.IDiscordRepository
import su.afk.kemonos.network.util.call
import su.afk.kemonos.network.util.safeCallOrNull
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType
import su.afk.kemonos.storage.api.repository.discord.IStoreDiscordRepository
import javax.inject.Inject

internal class DiscordRepository @Inject constructor(
    private val api: DiscordApi,
    private val discordCacheStore: IStoreDiscordRepository,
    private val cacheJson: CreatorProfileCacheJson,
) : IDiscordRepository {

    private companion object {
        const val OFFSET_CACHE_DELIMITER = "#o="
    }

    override suspend fun getCommunityChannels(serverId: String): DiscordCommunityChannels {
        val cachedChannels =
            discordCacheStore.getFreshJsonOrNull(serverId, CommunityCacheType.CHANNELS)
                ?.let { cacheJson.discordChannelsFromJson(it) }

        if (cachedChannels != null && !cachedChannels.serverName.isNullOrBlank()) {
            return cachedChannels
        }

        val fromNet = runCatching {
            api.getDiscordServer(serverId).call { it.toDiscordCommunityChannels() }
        }.getOrNull()

        if (fromNet != null) {
            discordCacheStore.putJson(
                id = serverId,
                type = CommunityCacheType.CHANNELS,
                json = cacheJson.discordChannelsToJson(fromNet),
            )
            return fromNet
        }

        return cachedChannels ?: DiscordCommunityChannels(
            serverName = null,
            updated = null,
            channels = emptyList()
        )
    }

    override suspend fun getCommunityMessages(channelId: String, offset: Int): List<CommunityMessage> {
        val pageCacheId = channelOffsetCacheId(channelId = channelId, offset = offset)
        discordCacheStore.getFreshJsonOrNull(pageCacheId, CommunityCacheType.MESSAGES_PAGE0)
            ?.let { return cacheJson.communityMessagesFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getDiscordChannelMessages(channelId = channelId, offset = if (offset == 0) null else offset) },
            mapper = { dto -> dto.toCommunityMessages() }
        )

        if (fromNet != null) {
            discordCacheStore.putJson(
                id = pageCacheId,
                type = CommunityCacheType.MESSAGES_PAGE0,
                json = cacheJson.communityMessagesToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    private fun channelOffsetCacheId(channelId: String, offset: Int): String {
        if (offset <= 0) return channelId
        return "$channelId$OFFSET_CACHE_DELIMITER$offset"
    }
}
