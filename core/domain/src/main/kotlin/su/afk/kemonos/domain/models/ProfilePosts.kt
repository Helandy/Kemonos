package su.afk.kemonos.domain.models

import kotlinx.serialization.Serializable

/** Мета сущность поста для поиска */
@Serializable
data class PostDomain(
    val id: String,
    val userId: String,
    val service: String,
    val title: String?,
    /** может быть null пока не открывали пост */
    val content: String?,
    val added: String?,
    val published: String?,
    val edited: String?,
    val file: FileDomain?,
    val attachments: List<AttachmentDomain>,
    val tags: List<String>,
    val nextId: String?,
    val prevId: String?,
    /** может быть null если не было “избранного” */
    val favedSeq: Int?,
    /** может быть null если не было “поиска” */
    val favCount: Int?,
)

@Serializable
data class FileDomain(
    val name: String,
    val path: String
)

@Serializable
data class AttachmentDomain(
    val server: String?,
    val path: String,
    val name: String?,
)

@Serializable
data class PreviewDomain(
    val server: String?,
    val path: String?,
    val name: String?,
    val type: String?,
    val url: String? = null,
    val subject: String? = null,
    val description: String? = null
)

@Serializable
data class VideoDomain(
    val server: String,
    val path: String,
    val name: String
)