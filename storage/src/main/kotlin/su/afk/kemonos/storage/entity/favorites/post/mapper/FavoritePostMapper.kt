package su.afk.kemonos.storage.entity.favorites.post.mapper

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.FileDomain
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.storage.entity.favorites.post.FavoritePostEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FavoritePostMapper @Inject constructor(
    private val json: Json,
) {
    private val attachmentListSer = ListSerializer(AttachmentDomain.serializer())
    private val stringListSer = ListSerializer(String.serializer())

    fun toEntity(site: SelectedSite, domain: PostDomain, now: Long = System.currentTimeMillis()): FavoritePostEntity =
        FavoritePostEntity(
            site = site,

            id = domain.id,
            userId = domain.userId,
            service = domain.service,

            title = domain.title,
            content = domain.content,
            substring = domain.substring,
            added = domain.added,
            published = domain.published,
            edited = domain.edited,

            fileName = domain.file?.name,
            filePath = domain.file?.path,
            attachmentsJson = json.encodeToString(attachmentListSer, domain.attachments),
            tagsJson = json.encodeToString(stringListSer, domain.tags),

            nextId = domain.nextId,
            prevId = domain.prevId,
            favedSeq = domain.favedSeq,
            favCount = domain.favCount,

            cachedAt = now
        )

    fun toDomain(entity: FavoritePostEntity): PostDomain =
        PostDomain(
            id = entity.id,
            userId = entity.userId,
            service = entity.service,

            title = entity.title,
            content = entity.content,
            substring = entity.substring,
            added = entity.added,
            published = entity.published,
            edited = entity.edited,

            file = if (!entity.fileName.isNullOrBlank() && !entity.filePath.isNullOrBlank())
                FileDomain(name = entity.fileName, path = entity.filePath)
            else null,

            attachments = json.decodeFromString(attachmentListSer, entity.attachmentsJson ?: "[]"),
            tags = json.decodeFromString(stringListSer, entity.tagsJson ?: "[]"),

            nextId = entity.nextId,
            prevId = entity.prevId,
            favedSeq = entity.favedSeq,
            favCount = entity.favCount,
        )
}