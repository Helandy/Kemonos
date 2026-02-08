package su.afk.kemonos.domain.models

import kotlinx.serialization.Serializable

/** Мета сущность поста для поиска */
@Serializable
data class PostDomain(
    val id: String,
    val userId: String,
    val service: String,
    val title: String?,
    /** будет null пока не открывали пост */
    val content: String?,
    /** будет null если открыли пост */
    val substring: String?,
    val added: String?,
    val published: String?,
    val edited: String?,
    val file: FileDomain?,
    val incompleteRewards: IncompleteRewards?,
    val poll: PollDomain?,
    val attachments: List<AttachmentDomain>,
    val tags: List<String>,
    val nextId: String?,
    val prevId: String?,
    /** будет null если не было в “избранном” */
    val favedSeq: Int?,
    /** может быть null если не было “поиска” */
    val favCount: Int?,
) {
    companion object {
        fun default() = PostDomain(
            id = "1",
            userId = "1",
            service = "onlyfans",
            title = "Super Mega Title",
            content = "Content Content Content Content ContentContentContent",
            substring = "Content Content Content Content ContentContentContent",
            added = "22.01.2026",
            published = "22.01.2026",
            edited = "22.01.2026",
            file = null,
            incompleteRewards = null,
            poll = null,
            attachments = emptyList(),
            tags = emptyList(),
            nextId = null,
            prevId = null,
            favedSeq = null,
            favCount = null,
        )

        fun PostDomain.stableKey(): String = "$service:$userId:$id"
    }
}

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

@Serializable
data class IncompleteRewards(
    val price: Double?,
    val mediaCount: Int?,
    val photoCount: Int?,
    val videoCount: Int?,
    val completeCount: Int?,
    val incompleteCount: Int?,
)

@Serializable
data class PollDomain(
    val title: String?,
    val choices: List<ChoicesDomain>?,
    val closesAt: String?,
    val createdAt: String?,
    val description: String?,

    val allowsMultiple: Boolean,
)

@Serializable
data class ChoicesDomain(
    val text: String?,
    val votes: Int?,
)