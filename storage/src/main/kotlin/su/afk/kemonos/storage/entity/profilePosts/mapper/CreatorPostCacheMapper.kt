package su.afk.kemonos.storage.entity.profilePosts.mapper

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import su.afk.kemonos.domain.models.*
import su.afk.kemonos.storage.entity.profilePosts.CreatorPostCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CreatorPostCacheMapper @Inject constructor(
    private val json: Json,
) {
    private val attachmentListSer = ListSerializer(AttachmentDomain.serializer())
    private val stringListSer = ListSerializer(String.serializer())

    fun toEntity(
        domain: PostDomain,
        queryKey: String,
        offset: Int,
        indexInPage: Int,
        updatedAt: Long,
    ): CreatorPostCacheEntity =
        CreatorPostCacheEntity(
            queryKey = queryKey,
            offset = offset,
            id = domain.id,
            userId = domain.userId,
            service = domain.service,
            title = domain.title,
            published = domain.published,
            added = domain.added,
            edited = domain.edited,
            substring = domain.substring,

            fileName = domain.file?.name,
            filePath = domain.file?.path,
            incompleteRewardsJson = domain.incompleteRewards?.let {
                json.encodeToString(IncompleteRewards.serializer(), it)
            },
            pollJson = domain.poll?.let {
                json.encodeToString(PollDomain.serializer(), it)
            },
            attachmentsJson = json.encodeToString(attachmentListSer, domain.attachments),
            tagsJson = json.encodeToString(stringListSer, domain.tags),

            indexInPage = indexInPage,
            updatedAt = updatedAt
        )

    fun toDomain(entity: CreatorPostCacheEntity): PostDomain =
        PostDomain(
            id = entity.id,
            userId = entity.userId,
            service = entity.service,
            title = entity.title,
            content = null,
            substring = entity.substring,
            added = entity.added,
            published = entity.published,
            edited = entity.edited,

            file = if (!entity.fileName.isNullOrBlank() && !entity.filePath.isNullOrBlank())
                FileDomain(name = entity.fileName, path = entity.filePath)
            else null,
            incompleteRewards = entity.incompleteRewardsJson?.let {
                runCatching { json.decodeFromString(IncompleteRewards.serializer(), it) }.getOrNull()
            },
            poll = entity.pollJson?.let {
                runCatching { json.decodeFromString(PollDomain.serializer(), it) }.getOrNull()
            },
            attachments = json.decodeFromString(attachmentListSer, entity.attachmentsJson ?: "[]"),
            tags = json.decodeFromString(stringListSer, entity.tagsJson ?: "[]"),

            nextId = null,
            prevId = null,
            favedSeq = null,
            favCount = null
        )
}