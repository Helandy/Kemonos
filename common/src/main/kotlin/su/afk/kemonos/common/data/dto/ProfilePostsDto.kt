package su.afk.kemonos.common.data.dto

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.common.data.dto.AttachmentDto.Companion.toDomain
import su.afk.kemonos.common.data.dto.FileDto.Companion.toDomain
import su.afk.kemonos.domain.models.PostDomain

/** Общая модель для поиск и избранного */
data class PostUnifiedDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("service")
    val service: String,

    @SerializedName("title")
    val title: String?,

    /** будет null в полном посте */
    @SerializedName("substring")
    val substring: String?,
    /** будет null в list/search */
    @SerializedName("content")
    val content: String?,

    @SerializedName("added")
    val added: String?,
    @SerializedName("published")
    val published: String?,
    @SerializedName("edited")
    val edited: String?,

    @SerializedName("tags")
    val tags: List<String>?,

    @SerializedName("attachments")
    val attachments: List<AttachmentDto>?,
    @SerializedName("file")
    val file: FileDto?,

    /** detail-only */
    @SerializedName("next") val next: String? = null,
    @SerializedName("prev") val prev: String? = null,

    /** list/search-only */
    @SerializedName("faved_seq") val favedSeq: Int? = null,
    @SerializedName("fav_count") val favCount: Int? = null,
) {
    companion object {
        fun PostUnifiedDto.toDomain(): PostDomain = PostDomain(
            id = id,
            userId = user,
            service = service,
            title = title,
            content = content,
            substring = substring,
            added = added,
            published = published,
            edited = edited,
            file = file?.toDomain(),
            attachments = attachments.orEmpty().map { it.toDomain() },
            tags = tags.orEmpty(),
            nextId = next,
            prevId = prev,
            favedSeq = favedSeq,
            favCount = favCount,
        )
    }
}