package su.afk.kemonos.common.data.dto

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.common.data.dto.ChoicesDto.Companion.toDomain
import su.afk.kemonos.domain.models.ChoicesDomain
import su.afk.kemonos.domain.models.PollDomain

data class PollDto(
    @SerializedName("title")
    val title: String?,
    @SerializedName("choices")
    val choicesDto: List<ChoicesDto>?,
    @SerializedName("closes_at")
    val closesAt: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("allows_multiple")
    val allowsMultiple: Boolean,
) {
    companion object {
        fun PollDto.toDomain(): PollDomain =
            PollDomain(
                title = this.title,
                choices = this.choicesDto?.map {
                    it.toDomain()
                },
                closesAt = this.closesAt,
                createdAt = this.createdAt,
                description = this.description,
                allowsMultiple = this.allowsMultiple,
            )
    }
}

data class ChoicesDto(
    @SerializedName("text")
    val text: String?,
    @SerializedName("votes")
    val votes: Int?,
) {
    companion object {
        fun ChoicesDto.toDomain() = ChoicesDomain(
            text = this.text,
            votes = this.votes,
        )
    }
}