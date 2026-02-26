package su.afk.kemonos.creatorProfile.data.dto.profileCommunity

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityAttachment
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityEmbed
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage

internal data class CommunityMessageDto(
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("deleted_at")
    val deletedAt: String?,
    @SerializedName("attachments")
    val attachments: List<AttachmentDto?>? = null,
    @SerializedName("embeds")
    val embeds: List<EmbedDto?>? = null,
    @SerializedName("original_json")
    val originalJson: OriginalJsonDto? = null,
    @SerializedName("replies")
    val replies: List<CommunityMessageDto>? = null
) {
    internal data class AttachmentDto(
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("path")
        val path: String? = null,
        @SerializedName("chat_image_thumbnail")
        val chatImageThumbnail: String? = null
    )

    internal data class EmbedDto(
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
        val titleLink: String? = null
    )

    internal data class OriginalJsonDto(
        @SerializedName("text")
        val text: String? = null,
        @SerializedName("created_at")
        val createdAt: String? = null,
        @SerializedName("user")
        val user: UserDto? = null
    )

    internal data class UserDto(
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("role")
        val role: String? = null
    )

    companion object {
        fun CommunityMessageDto.toDomain(): CommunityMessage = CommunityMessage(
            messageId = messageId,
            userId = userId,
            createdAt = originalJson?.createdAt ?: createdAt,
            deletedAt = deletedAt,
            text = originalJson?.text,
            userName = originalJson?.user?.name,
            userRole = originalJson?.user?.role,
            attachments = attachments.orEmpty().filterNotNull().map {
                CommunityAttachment(
                    name = it.name,
                    path = it.path,
                    thumbUrl = it.chatImageThumbnail
                )
            },
            embeds = embeds.orEmpty().filterNotNull().map {
                CommunityEmbed(
                    type = it.type,
                    text = it.text,
                    title = it.title,
                    imageUrl = it.imageUrl,
                    thumbUrl = it.thumbUrl,
                    titleLink = it.titleLink
                )
            },
            replies = replies.orEmpty().map { it.toDomain() }
        )

        fun List<CommunityMessageDto>.toDomain(): List<CommunityMessage> = map { it.toDomain() }
    }
}
