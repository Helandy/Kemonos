package su.afk.kemonos.data.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import su.afk.kemonos.data.dto.AttachmentDto.Companion.toDomainOrNull
import su.afk.kemonos.data.dto.FileDto.Companion.toDomain
import su.afk.kemonos.data.dto.IncompleteRewardsDto.Companion.toDomain
import su.afk.kemonos.data.dto.PollDto.Companion.toDomain
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
    @JsonAdapter(PostTagsDtoAdapter::class)
    val tags: List<String?>?,

    @SerializedName("attachments")
    val attachments: List<AttachmentDto>?,
    @SerializedName("file")
    val file: FileDto?,

    /** Если контент в посте заблокирован */
    @SerializedName("incomplete_rewards")
    val incompleteRewards: IncompleteRewardsDto?,

    /** Голосования */
    @SerializedName("poll")
    val poll: PollDto?,

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
            incompleteRewards = incompleteRewards?.toDomain(),
            poll = poll?.toDomain(),
            attachments = attachments.orEmpty().mapNotNull { it.toDomainOrNull() },
            tags = tags.orEmpty().filterNotNull(),
            nextId = next,
            prevId = prev,
            favedSeq = favedSeq,
            favCount = favCount,
        )
    }
}

class PostTagsDtoAdapter : JsonDeserializer<List<String?>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): List<String?> {
        if (json == null || json.isJsonNull) return emptyList()

        if (json.isJsonArray) {
            return json.asJsonArray.map { element ->
                if (element.isJsonNull) null else element.asString
            }
        }

        if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            return json.asString
                .trim()
                .takeIf { it.isNotEmpty() }
                ?.let { listOf(it) }
                .orEmpty()
        }

        return emptyList()
    }
}
