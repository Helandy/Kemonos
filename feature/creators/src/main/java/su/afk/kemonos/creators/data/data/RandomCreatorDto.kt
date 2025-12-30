package su.afk.kemonos.creators.data.data

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creators.domain.model.RandomCreator

internal data class RandomCreatorDto(
    @SerializedName("service")
    val service: String,

    @SerializedName("artist_id")
    val artistId: String,
) {
    companion object {
        fun RandomCreatorDto.toDomain(): RandomCreator = RandomCreator(
            service = this.service,
            artistId = this.artistId,
        )
    }
}