package su.afk.kemonos.creatorPost.data.dto.profilePost

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.common.data.dto.AttachmentDto
import su.afk.kemonos.common.data.dto.AttachmentDto.Companion.toDomain
import su.afk.kemonos.common.data.dto.PostUnifiedDto
import su.afk.kemonos.common.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.data.dto.profilePost.PreviewDto.Companion.toDomain
import su.afk.kemonos.creatorPost.data.dto.profilePost.VideoDto.Companion.toDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain

internal data class PostResponseDto(
    @SerializedName("post")
    val post: PostUnifiedDto,

    @SerializedName("attachments")
    val attachments: List<AttachmentDto>? = null,

    @SerializedName("previews")
    val previews: List<PreviewDto>? = null,

    @SerializedName("videos")
    val videos: List<VideoDto>? = null,

    @SerializedName("props")
    val props: PropsDto? = null
) {
    companion object {
        fun PostResponseDto.toDomain(): PostContentDomain {
            val postDomain = post.toDomain()

            val root = attachments.orEmpty().map { it.toDomain() }
            val merged = root.ifEmpty { postDomain.attachments }

            return PostContentDomain(
                post = postDomain,
                videos = videos.orEmpty().map { it.toDomain() },
                attachments = merged,
                previews = previews.orEmpty().map { it.toDomain() },
            )
        }
    }
}

internal data class PreviewDto(
    @SerializedName("server")
    val server: String?,

    @SerializedName("path")
    val path: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("subject")
    val subject: String? = null,

    @SerializedName("description")
    val description: String? = null
) {
    companion object {
        fun PreviewDto.toDomain() = PreviewDomain(
            server = server,
            path = path,
            name = name,
            type = type,
            url = url,
            subject = subject,
            description = description
        )
    }
}

internal data class VideoDto(
    @SerializedName("path")
    val path: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("extension")
    val extension: String,

    @SerializedName("server")
    val server: String
) {
    companion object {
        fun VideoDto.toDomain() = VideoDomain(
            server = server,
            path = path,
            name = name
        )
    }
}

internal data class PropsDto(
    @SerializedName("flagged")
    val flagged: String? = null
)