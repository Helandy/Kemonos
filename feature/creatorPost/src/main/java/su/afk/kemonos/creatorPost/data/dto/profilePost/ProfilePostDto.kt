package su.afk.kemonos.creatorPost.data.dto.profilePost

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentRevisionDomain
import su.afk.kemonos.creatorPost.data.dto.profilePost.PreviewDto.Companion.toDomain
import su.afk.kemonos.creatorPost.data.dto.profilePost.VideoDto.Companion.toDomain
import su.afk.kemonos.data.dto.AttachmentDto
import su.afk.kemonos.data.dto.AttachmentDto.Companion.toDomain
import su.afk.kemonos.data.dto.FileDto
import su.afk.kemonos.data.dto.FileDto.Companion.toDomain
import su.afk.kemonos.data.dto.IncompleteRewardsDto
import su.afk.kemonos.data.dto.IncompleteRewardsDto.Companion.toDomain
import su.afk.kemonos.data.dto.PollDto
import su.afk.kemonos.data.dto.PollDto.Companion.toDomain
import su.afk.kemonos.data.dto.PostUnifiedDto
import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.data.dto.PostTagsDtoAdapter
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain
import su.afk.kemonos.utils.file.isImageFile
import su.afk.kemonos.utils.file.isVideoFile
import su.afk.kemonos.utils.pawchive.PawchiveConstants

internal data class PostResponseDto(
    @SerializedName("post")
    val post: PostUnifiedDto,

    @SerializedName(value = "attachments", alternate = ["result_attachments"])
    val attachments: List<AttachmentDto>? = null,

    @SerializedName(value = "previews", alternate = ["result_previews"])
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
                revisions = props.toRevisionsDomain(),
            )
        }
    }
}

internal data class PawchivePostResponseDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("user")
    val user: String?,
    @SerializedName("service")
    val service: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("substring")
    val substring: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("added")
    val added: String?,
    @SerializedName("published")
    val published: String?,
    @SerializedName("edited")
    val edited: String?,
    @SerializedName("tags")
    @JsonAdapter(PostTagsDtoAdapter::class)
    val tags: List<String?>?,
    @SerializedName("file")
    val file: FileDto?,
    @SerializedName("incomplete_rewards")
    val incompleteRewards: IncompleteRewardsDto?,
    @SerializedName("poll")
    val poll: PollDto?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("prev")
    val prev: String?,
    val post: PostUnifiedDto?,
    @SerializedName("attachments")
    val attachments: List<AttachmentDto>?,
    @SerializedName("previews")
    val previews: List<PreviewDto>?,
    @SerializedName("videos")
    val videos: List<VideoDto>?,
    @SerializedName("props")
    val props: PropsDto?,
) {
    fun toDomain(
        service: String,
        creatorId: String,
        postId: String,
    ): PostContentDomain {
        val postDomain = post?.toDomain() ?: toRootPostDomain(
            service = service,
            creatorId = creatorId,
            postId = postId,
        )
        val rootAttachments = attachments.orEmpty().map { it.toDomain() }
        val mergedAttachments = rootAttachments.ifEmpty { postDomain.attachments }
        val attachmentPreviews = mergedAttachments.filter { isImageFile(it.path) }.map {
            PreviewDomain(
                server = it.server ?: PawchiveConstants.FILE_BASE_URL,
                path = it.path,
                name = it.fileNameOrPathName(),
                type = "thumbnail",
            )
        }
        val attachmentVideos = mergedAttachments.mapNotNull { it.toPawchiveVideoOrNull() }
        val filePreview = postDomain.file
            ?.takeIf { isImageFile(it.path) }
            ?.let {
                PreviewDomain(
                    server = PawchiveConstants.FILE_BASE_URL,
                    path = it.path,
                    name = it.name.takeIf { name -> name.isNotBlank() } ?: it.path.substringAfterLast('/'),
                    type = "thumbnail",
                )
            }

        return PostContentDomain(
            post = postDomain,
            videos = (videos.orEmpty().map { it.toDomain() } + attachmentVideos)
                .distinctBy { "${it.server}:${it.path}" },
            attachments = mergedAttachments,
            previews = (previews.orEmpty().map { it.toDomain() } + listOfNotNull(filePreview) + attachmentPreviews)
                .distinctBy { "${it.type}:${it.path}:${it.url}" },
            revisions = props.toRevisionsDomain(),
        )
    }
}

private fun su.afk.kemonos.domain.models.AttachmentDomain.toPawchiveVideoOrNull(): VideoDomain? {
    if (!isVideoAttachment()) return null
    return VideoDomain(
        server = server ?: PawchiveConstants.FILE_BASE_URL,
        path = path,
        name = fileNameOrPathName(),
    )
}

private fun su.afk.kemonos.domain.models.AttachmentDomain.isVideoAttachment(): Boolean {
    return isVideoFile(path) || isVideoFile(name)
}

private fun su.afk.kemonos.domain.models.AttachmentDomain.fileNameOrPathName(): String =
    name?.takeIf { it.isNotBlank() } ?: path.substringAfterLast('/')

private fun PawchivePostResponseDto.toRootPostDomain(
    service: String,
    creatorId: String,
    postId: String,
): PostDomain = PostDomain(
    id = id ?: postId,
    userId = user ?: creatorId,
    service = this.service ?: service,
    title = title,
    content = content,
    substring = substring,
    added = added,
    published = published,
    edited = edited,
    file = file?.toDomain(),
    incompleteRewards = incompleteRewards?.toDomain(),
    poll = poll?.toDomain(),
    attachments = attachments.orEmpty().map { it.toDomain() },
    tags = tags.orEmpty().filterNotNull(),
    nextId = next,
    prevId = prev,
    favedSeq = null,
    favCount = null,
)

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
    val flagged: String? = null,

    @SerializedName("revisions")
    val revisions: List<List<JsonElement>>? = null,
)

private val revisionGson = Gson()

private fun PropsDto?.toRevisionsDomain(): List<PostContentRevisionDomain> {
    val raw = this?.revisions.orEmpty()
    return raw.mapNotNull { pair ->
        val revisionId = pair.getOrNull(0)?.toIntSafe() ?: return@mapNotNull null
        val postPayload = pair.getOrNull(1) ?: return@mapNotNull null
        val backendRevisionId = postPayload.toBackendRevisionId()
        val revisionPostDto = runCatching {
            revisionGson.fromJson(postPayload, PostUnifiedDto::class.java)
        }.getOrNull() ?: return@mapNotNull null

        PostContentRevisionDomain(
            revisionId = revisionId,
            post = revisionPostDto.toDomain(),
            backendRevisionId = backendRevisionId,
        )
    }
}

private fun JsonElement.toIntSafe(): Int? = runCatching { asInt }.getOrNull()

private fun JsonElement.toBackendRevisionId(): Long? = runCatching {
    asJsonObject.get("revision_id")?.asLong
}.getOrNull()
