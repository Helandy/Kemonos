package su.afk.kemonos.storage.entity.post.mapper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentRevisionDomain
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
    private val revisionListSer = ListSerializer(PostContentRevisionDomain.serializer())

    fun toEntity(
        domain: PostContentDomain,
        cachedAt: Long = System.currentTimeMillis(),
    ): PostContentCacheEntity {
        val post = domain.post

        val attachments = domain.attachments.ifEmpty { post.attachments }

        return PostContentCacheEntity(
            service = post.service,
            userId = post.userId,
            postId = post.id,

            title = post.title,
            content = post.content,
            substring = post.substring,
            published = post.published,
            added = post.added,
            edited = post.edited,

            fileName = post.file?.name,
            filePath = post.file?.path,
            incompleteRewardsJson = post.incompleteRewards?.let {
                json.encodeToString(IncompleteRewards.serializer(), it)
            },
            pollJson = post.poll?.let {
                json.encodeToString(PollDomain.serializer(), it)
            },
            attachmentsJson = json.encodeToString(attachmentListSer, attachments),
            tagsJson = json.encodeToString(stringListSer, post.tags),
            videosJson = json.encodeToString(videoListSer, domain.videos),
            previewsJson = json.encodeToString(previewListSer, domain.previews),
            revisionsJson = json.encodeToString(revisionListSer, domain.revisions),

            nextId = post.nextId,
            prevId = post.prevId,
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
            substring = entity.substring,
            added = entity.added,
            published = entity.published,
            edited = entity.edited,

            file = if (!entity.fileName.isNullOrBlank() && !entity.filePath.isNullOrBlank()) {
                FileDomain(name = entity.fileName, path = entity.filePath)
            } else null,
            incompleteRewards = entity.incompleteRewardsJson?.let {
                runCatching { json.decodeFromString(IncompleteRewards.serializer(), it) }.getOrNull()
            },
            poll = entity.pollJson?.let {
                runCatching { json.decodeFromString(PollDomain.serializer(), it) }.getOrNull()
            },
            attachments = attachments,
            tags = tags,

            nextId = entity.nextId,
            prevId = entity.prevId,
            favedSeq = null,
            favCount = null
        )

        return PostContentDomain(
            post = post,
            videos = decodeList(entity.videosJson, videoListSer),
            attachments = attachments,
            previews = decodeList(entity.previewsJson, previewListSer),
            revisions = decodeList(entity.revisionsJson, revisionListSer),
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
