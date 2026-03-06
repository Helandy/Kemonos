package su.afk.kemonos.creatorProfile.data.cache

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.creatorProfile.domain.repository.DiscordCommunityChannels
import su.afk.kemonos.domain.models.Tag
import javax.inject.Inject

class CreatorProfileCacheJson @Inject constructor(
    private val json: Json,
) {
    @Serializable
    private data class DiscordChannelsCachePayload(
        val serverName: String? = null,
        val updated: String? = null,
        val channels: List<CommunityChannel> = emptyList()
    )

    fun dmsToJson(items: List<Dm>): String =
        json.encodeToString(ListSerializer(Dm.serializer()), items)

    fun dmsFromJson(raw: String): List<Dm> =
        json.decodeFromString(ListSerializer(Dm.serializer()), raw)

    fun tagsToJson(items: List<Tag>): String =
        json.encodeToString(ListSerializer(Tag.serializer()), items)

    fun tagsFromJson(raw: String): List<Tag> =
        json.decodeFromString(ListSerializer(Tag.serializer()), raw)

    fun announcementsToJson(items: List<ProfileAnnouncement>): String =
        json.encodeToString(ListSerializer(ProfileAnnouncement.serializer()), items)

    fun announcementsFromJson(raw: String): List<ProfileAnnouncement> =
        json.decodeFromString(ListSerializer(ProfileAnnouncement.serializer()), raw)

    fun fanCardsToJson(items: List<ProfileFanCard>): String =
        json.encodeToString(ListSerializer(ProfileFanCard.serializer()), items)

    fun fanCardsFromJson(raw: String): List<ProfileFanCard> =
        json.decodeFromString(ListSerializer(ProfileFanCard.serializer()), raw)

    fun linksToJson(items: List<ProfileLink>): String =
        json.encodeToString(ListSerializer(ProfileLink.serializer()), items)

    fun linksFromJson(raw: String): List<ProfileLink> =
        json.decodeFromString(ListSerializer(ProfileLink.serializer()), raw)

    fun similarToJson(items: List<SimilarCreator>): String =
        json.encodeToString(ListSerializer(SimilarCreator.serializer()), items)

    fun similarFromJson(raw: String): List<SimilarCreator> =
        json.decodeFromString(ListSerializer(SimilarCreator.serializer()), raw)

    fun communityChannelsToJson(items: List<CommunityChannel>): String =
        json.encodeToString(ListSerializer(CommunityChannel.serializer()), items)

    fun communityChannelsFromJson(raw: String): List<CommunityChannel> =
        json.decodeFromString(ListSerializer(CommunityChannel.serializer()), raw)

    fun communityMessagesToJson(items: List<CommunityMessage>): String =
        json.encodeToString(ListSerializer(CommunityMessage.serializer()), items)

    fun communityMessagesFromJson(raw: String): List<CommunityMessage> =
        json.decodeFromString(ListSerializer(CommunityMessage.serializer()), raw)

    internal fun discordChannelsToJson(channels: DiscordCommunityChannels): String =
        json.encodeToString(
            DiscordChannelsCachePayload.serializer(),
            DiscordChannelsCachePayload(
                serverName = channels.serverName,
                updated = channels.updated,
                channels = channels.channels
            )
        )

    internal fun discordChannelsFromJson(raw: String): DiscordCommunityChannels {
        return runCatching {
            json.decodeFromString(DiscordChannelsCachePayload.serializer(), raw)
        }.map { payload ->
            DiscordCommunityChannels(
                serverName = payload.serverName,
                updated = payload.updated,
                channels = payload.channels
            )
        }.getOrElse {
            DiscordCommunityChannels(
                serverName = null,
                updated = null,
                channels = communityChannelsFromJson(raw)
            )
        }
    }
}
