package su.afk.kemonos.creatorPost.data.dto.onlyhaven

import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.data.dto.onlyhaven.OnlyHavenAttachmentDto
import su.afk.kemonos.data.dto.onlyhaven.OnlyHavenAttachmentDto.Companion.toDomainOrNull
import su.afk.kemonos.data.dto.onlyhaven.OnlyHavenPostDto
import su.afk.kemonos.data.dto.onlyhaven.OnlyHavenPostDto.Companion.toDomain
import su.afk.kemonos.domain.models.PreviewDomain
import su.afk.kemonos.domain.models.VideoDomain

private const val KIND_VIDEO = "video"
private val IMAGE_KINDS = setOf("image", "gif")

/**
 * Деталь поста OnlyHaven.
 *
 * Отдельных списков превью и видео в ответе нет — всё лежит в attachments,
 * поэтому раскладываем по [PostContentDomain] исходя из kind вложения.
 * Ревизий у источника нет.
 */
internal fun OnlyHavenPostDto.toPostContentDomain(
    fileBaseUrl: String,
    creatorId: String,
): PostContentDomain {
    val unlocked: List<Pair<OnlyHavenAttachmentDto, su.afk.kemonos.domain.models.AttachmentDomain>> =
        attachments.orEmpty().mapNotNull { dto ->
            dto.toDomainOrNull(fileBaseUrl)?.let { dto to it }
        }

    return PostContentDomain(
        post = toDomain(fileBaseUrl = fileBaseUrl, creatorIdFallback = creatorId),
        attachments = unlocked.map { (_, domain) -> domain },
        videos = unlocked
            .filter { (dto, _) -> dto.kind == KIND_VIDEO }
            .map { (dto, domain) ->
                VideoDomain(
                    server = domain.server.orEmpty(),
                    path = domain.path,
                    name = domain.name.orEmpty(),
                    thumbnailPath = domain.thumbnailPath,
                    durationMs = dto.durationMs,
                    sizeBytes = dto.bytes,
                )
            },
        previews = unlocked
            .filter { (dto, _) -> dto.kind in IMAGE_KINDS }
            .map { (_, domain) ->
                PreviewDomain(
                    server = domain.server,
                    path = domain.path,
                    name = domain.name,
                    type = "thumbnail",
                    thumbnailPath = domain.thumbnailPath,
                )
            },
        revisions = emptyList(),
    )
}
