package su.afk.kemonos.common.data.creators

import kotlinx.serialization.SerialName
import su.afk.kemonos.domain.domain.models.Creators

data class CreatorsDto(
    @SerialName("favorited")
    val favorited: Int,

    @SerialName("id")
    val id: String,

    @SerialName("indexed")
    val indexed: Int,

    @SerialName("name")
    val name: String,

    @SerialName("service")
    val service: String,

    @SerialName("updated")
    val updated: Int,
) {
    companion object {
        fun CreatorsDto.toDomain(): Creators = Creators(
            favorited = this.favorited,
            id = this.id,
            indexed = this.indexed,
            name = this.name,
            service = this.service,
            updated = this.updated
        )
    }
}