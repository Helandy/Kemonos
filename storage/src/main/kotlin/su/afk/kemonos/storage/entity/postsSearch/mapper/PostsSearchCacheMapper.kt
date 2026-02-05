package su.afk.kemonos.storage.entity.postsSearch.mapper

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import su.afk.kemonos.domain.models.*
import su.afk.kemonos.storage.entity.postsSearch.entity.PostsSearchCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PostsSearchCacheMapper @Inject constructor(
    private val json: Json,
) {
    private val attachmentListSer = ListSerializer(AttachmentDomain.serializer())
    private val stringListSer = ListSerializer(String.serializer())

    fun toEntity(
        post: PostDomain,
        queryKey: String,
        offset: Int,
        indexInPage: Int,
        updatedAt: Long,
    ): PostsSearchCacheEntity =
        PostsSearchCacheEntity(
            queryKey = queryKey,
            offset = offset,
            id = post.id,
            userId = post.userId,
            service = post.service,
            title = post.title,
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
            attachmentsJson = json.encodeToString(attachmentListSer, post.attachments),
            tagsJson = json.encodeToString(stringListSer, post.tags),

            nextId = post.nextId,
            prevId = post.prevId,
            indexInPage = indexInPage,
            updatedAt = updatedAt
        )

    fun toDomain(entity: PostsSearchCacheEntity): PostDomain =
        PostDomain(
            id = entity.id,
            userId = entity.userId,
            service = entity.service,
            title = entity.title,
            substring = entity.substring,
            content = null,
            added = entity.added,
            published = entity.published,
            edited = entity.edited,

            file = if (!entity.fileName.isNullOrBlank() && !entity.filePath.isNullOrBlank())
                FileDomain(entity.fileName, entity.filePath)
            else null,
            incompleteRewards = entity.incompleteRewardsJson?.let {
                runCatching { json.decodeFromString(IncompleteRewards.serializer(), it) }.getOrNull()
            },
            poll = entity.pollJson?.let {
                runCatching { json.decodeFromString(PollDomain.serializer(), it) }.getOrNull()
            },
            attachments = json.decodeFromString(
                attachmentListSer,
                entity.attachmentsJson ?: "[]"
            ),
            tags = json.decodeFromString(
                stringListSer,
                entity.tagsJson ?: "[]"
            ),

            nextId = entity.nextId,
            prevId = entity.prevId,
            favedSeq = null,
            favCount = null
        )
}