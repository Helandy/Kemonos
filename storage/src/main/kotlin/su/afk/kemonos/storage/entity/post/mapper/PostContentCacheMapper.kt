package su.afk.kemonos.storage.entity.post.mapper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.*
import su.afk.kemonos.storage.entity.post.PostContentCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PostContentCacheMapper @Inject constructor(
    private val json: Json,
) {
    private val attachmentListSer = ListSerializer(AttachmentDomain.serializer())
    private val stringListSer = ListSerializer(String.serializer())
    private val videoListSer = ListSerializer(VideoDomain.serializer())
    private val previewListSer = ListSerializer(PreviewDomain.serializer())

    fun toEntity(
        domain: PostContentDomain,
        cachedAt: Long = System.currentTimeMillis(),
    ): PostContentCacheEntity {
        val p = domain.post

        val attachments = domain.attachments.ifEmpty { p.attachments }

        return PostContentCacheEntity(
            service = p.service,
            userId = p.userId,
            postId = p.id,

            title = p.title,
            content = p.content,
            published = p.published,
            added = p.added,
            edited = p.edited,

            fileName = p.file?.name,
            filePath = p.file?.path,

            attachmentsJson = json.encodeToString(attachmentListSer, attachments),
            tagsJson = json.encodeToString(stringListSer, p.tags),
            videosJson = json.encodeToString(videoListSer, domain.videos),
            previewsJson = json.encodeToString(previewListSer, domain.previews),

            cachedAt = cachedAt
        )
    }

    fun toDomain(entity: PostContentCacheEntity): PostContentDomain {
        val attachments = decodeList(entity.attachmentsJson, attachmentListSer)
        val tags = decodeList(entity.tagsJson, stringListSer)

        val post = PostDomain(
            id = entity.postId,
            userId = entity.userId,
            service = entity.service,

            title = entity.title,
            content = entity.content,
            added = entity.added,
            published = entity.published,
            edited = entity.edited,

            file = if (!entity.fileName.isNullOrBlank() && !entity.filePath.isNullOrBlank()) {
                FileDomain(name = entity.fileName, path = entity.filePath)
            } else null,

            attachments = attachments,
            tags = tags,

            nextId = null,
            prevId = null,
            favedSeq = null,
            favCount = null
        )

        return PostContentDomain(
            post = post,
            videos = decodeList(entity.videosJson, videoListSer),
            attachments = attachments,
            previews = decodeList(entity.previewsJson, previewListSer),
        )
    }

    private fun <T> decodeList(
        payload: String?,
        serializer: KSerializer<List<T>>,
    ): List<T> {
        val safe = payload ?: "[]"
        return runCatching<List<T>> {
            json.decodeFromString(serializer, safe)
        }.getOrElse { emptyList() }
    }
}