package su.afk.kemonos.creators.data.dto

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.creator.Creators

data class CreatorsDto(
    @SerializedName("favorited")
    val favorited: Int,

    @SerializedName("id")
    val id: String,

    @SerializedName("indexed")
    val indexed: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("service")
    val service: String,

    @SerializedName("updated")
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