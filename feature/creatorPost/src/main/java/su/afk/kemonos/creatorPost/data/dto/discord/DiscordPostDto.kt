package su.afk.kemonos.creatorPost.data.dto.discord

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.PostDomain

internal data class DiscordServerResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String? = null,

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

    @SerializedName("server")
    val server: String? = null,

    @SerializedName("channel")
    val channel: String? = null,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("added")
    val added: String? = null,

    @SerializedName("published")
    val published: String? = null,

    @SerializedName("edited")
    val edited: String? = null,

    @SerializedName("attachments")
    val attachments: List<DiscordAttachmentDto>? = null,

    @SerializedName("seq")
    val seq: Int? = null,
)

internal data class DiscordAuthorDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("global_name")
    val globalName: String? = null,
)

internal data class DiscordAttachmentDto(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("path")
    val path: String? = null,

    @SerializedName("server")
    val server: String? = null,
)

internal object DiscordPostMapper {

    fun toPostContentDomain(
        messages: List<DiscordChannelMessageDto>,
        service: String,
        fallbackServerId: String,
        channelId: String,
        channelName: String?,
    ): PostContentDomain {
        val ordered = messages.sortedWith(compareBy<DiscordChannelMessageDto> { it.seq ?: Int.MAX_VALUE })
        val firstMessage = ordered.firstOrNull()
        val titleFromMessage = ordered.firstOrNull { !it.content.isNullOrBlank() }
            ?.content
            ?.lineSequence()
            ?.firstOrNull()
            ?.trim()
            ?.takeIf { it.isNotBlank() }
        val title = channelName?.takeIf { it.isNotBlank() } ?: titleFromMessage

        val attachments = ordered
            .asSequence()
            .flatMap { it.attachments.orEmpty().asSequence() }
            .mapNotNull { it.toDomainOrNull() }
            .distinctBy { "${it.server}:${it.path}:${it.name}" }
            .toList()

        val resolvedServerId = ordered
            .firstNotNullOfOrNull { it.server?.takeIf(String::isNotBlank) }
            ?: fallbackServerId

        val post = PostDomain(
            id = channelId,
            userId = resolvedServerId.ifBlank { fallbackServerId },
            service = service,
            title = title,
            content = buildHtmlContent(ordered).takeIf { it.isNotBlank() },
            substring = null,
            added = firstMessage?.added,
            published = firstMessage?.published,
            edited = firstMessage?.edited,
            file = null,
            incompleteRewards = null,
            poll = null,
            attachments = attachments,
            tags = emptyList(),
            nextId = null,
            prevId = null,
            favedSeq = null,
            favCount = null,
        )

        return PostContentDomain(
            post = post,
            attachments = attachments,
        )
    }

    private fun DiscordAttachmentDto.toDomainOrNull(): AttachmentDomain? {
        val normalizedPath = path?.takeIf { it.isNotBlank() } ?: return null
        return AttachmentDomain(
            server = server?.takeIf { it.isNotBlank() },
            path = normalizedPath,
            name = name,
        )
    }

    private fun buildHtmlContent(messages: List<DiscordChannelMessageDto>): String {
        if (messages.isEmpty()) return ""
        return buildString {
            messages.forEach { message ->
                val text = message.content?.trim().orEmpty()
                if (text.isBlank()) return@forEach

                val author = message.author
                val authorName = when {
                    !author?.globalName.isNullOrBlank() -> author.globalName
                    !author?.username.isNullOrBlank() -> author.username
                    else -> null
                }

                append("<p>")
                if (!authorName.isNullOrBlank()) {
                    append("<strong>")
                    append(escapeHtml(authorName))
                    append(":</strong> ")
                }
                append(escapeHtml(text).replace("\n", "<br/>"))
                append("</p>")
            }
        }
    }

    private fun escapeHtml(raw: String): String = buildString(raw.length) {
        raw.forEach { ch ->
            when (ch) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&#39;")
                else -> append(ch)
            }
        }
    }
}
