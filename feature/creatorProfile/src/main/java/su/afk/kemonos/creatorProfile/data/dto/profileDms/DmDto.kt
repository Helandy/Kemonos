package su.afk.kemonos.creatorProfile.data.dto.profileDms


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm

data class DmDto(
    @SerializedName("added")
    val added: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("published")
    val published: String,
    @SerializedName("service")
    val service: String,
    @SerializedName("user")
    val user: String
) {
    companion object {
        fun DmDto.toDomain(): Dm = Dm(
            added = this.added,
            content = this.content,
            hash = this.hash,
            published = this.published,
            service = this.service,
            user = this.user,
        )

        fun List<DmDto>.toDomain(): List<Dm> = map { it.toDomain() }
    }
}