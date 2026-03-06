package su.afk.kemonos.creatorProfile.data.dto.profileCommunity

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityAttachment
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityEmbed
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.domain.repository.DiscordCommunityChannels

internal data class DiscordServerResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("indexed")
    val indexed: String? = null,
    @SerializedName("updated")
    val updated: String? = null,
    @SerializedName("channels")
    val channels: List<DiscordServerChannelDto>? = null,
)

internal data class DiscordServerChannelDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("server_id")
    val serverId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("parent_channel_id")
    val parentChannelId: String? = null,
    @SerializedName("topic")
    val topic: String? = null,
    @SerializedName("icon_emoji")
    val iconEmoji: String? = null,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("post_count")
    val postCount: Int? = null,
)

internal data class DiscordChannelMessageDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("author")
    val author: DiscordAuthorDto? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("added")
    val added: String? = null,
    @SerializedName("published")
    val published: String? = null,
    @SerializedName("attachments")
    val attachments: List<DiscordAttachmentDto>? = null,
    @SerializedName("embeds")
    val embeds: List<DiscordEmbedDto>? = null,
)

internal data class DiscordAuthorDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("global_name")
    val globalName: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("discriminator")
    val discriminator: String? = null,
)

internal data class DiscordAttachmentDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("path")
    val path: String? = null,
)

internal data class DiscordEmbedDto(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("thumb_url")
    val thumbUrl: String? = null,
    @SerializedName("title_link")
    val titleLink: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: DiscordEmbedAssetDto? = null,
    @SerializedName("video")
    val video: DiscordEmbedAssetDto? = null,
)

internal data class DiscordEmbedAssetDto(
    @SerializedName("url")
    val url: String? = null,
)

internal object DiscordCommunityMapper {
    private const val CHANNEL_TYPE_TEXT = 0
    private const val CHANNEL_TYPE_PUBLIC_THREAD = 11

    fun DiscordServerResponseDto.toDiscordCommunityChannels(): DiscordCommunityChannels {
        return DiscordCommunityChannels(
            serverName = name,
            updated = updated,
            channels = toCommunityChannels()
        )
    }

    fun DiscordServerResponseDto.toCommunityChannels(): List<CommunityChannel> {
        return channels
            .orEmpty()
            .asSequence()
            .filter { channel ->
                channel.type == CHANNEL_TYPE_TEXT ||
                        channel.type == CHANNEL_TYPE_PUBLIC_THREAD ||
                        (channel.postCount ?: 0) > 0
            }
            .mapNotNull { channel ->
                val name = channel.name?.trim().orEmpty()
                if (name.isBlank()) return@mapNotNull null
                CommunityChannel(
                    channelId = channel.id,
                    creatorId = channel.serverId ?: id,
                    campaignId = channel.parentChannelId,
                    channelTypeName = channel.type?.toString(),
                    name = name,
                    communityGuidelines = channel.topic,
                    emoji = channel.iconEmoji,
                    addedAt = null,
                    messagesRefreshedAt = null,
                    messagesFullRefreshedAt = null,
                    postCount = channel.postCount,
                )
            }
            .toList()
    }

    fun List<DiscordChannelMessageDto>.toCommunityMessages(): List<CommunityMessage> {
        return map { message ->
            CommunityMessage(
                messageId = message.id,
                userId = message.author?.id.orEmpty(),
                createdAt = message.published ?: message.added.orEmpty(),
                deletedAt = null,
                text = message.content,
                userName = message.author?.globalName ?: message.author?.username,
                userRole = null,
                userAvatarUrl = message.author.toAvatarUrl(),
                attachments = message.attachments.orEmpty().map { attachment ->
                    CommunityAttachment(
                        name = attachment.name,
                        path = attachment.path,
                        thumbUrl = null
                    )
                },
                embeds = message.embeds.orEmpty().map { embed ->
                    val embedVideoUrl = embed.video?.url
                    val embedThumbnailUrl = embed.thumbnail?.url
                    val embedDirectUrl = embed.url

                    CommunityEmbed(
                        type = embed.type,
                        text = embed.text,
                        title = embed.title,
                        imageUrl = embed.imageUrl
                            ?: embedVideoUrl
                            ?: embedDirectUrl
                            ?: embedThumbnailUrl,
                        thumbUrl = embed.thumbUrl
                            ?: embedThumbnailUrl,
                        titleLink = embed.titleLink
                            ?: embedDirectUrl
                    )
                },
                replies = emptyList(),
            )
        }
    }

    private fun DiscordAuthorDto?.toAvatarUrl(): String? {
        val author = this ?: return null
        val userId = author.id?.trim().orEmpty()
        if (userId.isBlank()) return null

        val avatarHash = author.avatar?.trim().orEmpty()
        if (avatarHash.isNotBlank()) {
            val extension = if (avatarHash.startsWith("a_")) "gif" else "png"
            return "https://cdn.discordapp.com/avatars/$userId/$avatarHash.$extension?size=128"
        }

        val defaultIndex = author.discriminator
            ?.toIntOrNull()
            ?.takeIf { it > 0 }
            ?.rem(5)
            ?: userId.toLongOrNull()
                ?.shr(22)
                ?.rem(6)
                ?.toInt()
            ?: return null

        return "https://cdn.discordapp.com/embed/avatars/$defaultIndex.png"
    }
}
