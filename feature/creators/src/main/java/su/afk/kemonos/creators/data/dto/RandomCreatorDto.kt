package su.afk.kemonos.creators.data.dto

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creators.domain.random.RandomCreatorModel

internal data class RandomCreatorDto(
    @SerializedName("service")
    val service: String,

    @SerializedName("artist_id")
    val artistId: String,
) {
    companion object {
        fun RandomCreatorDto.toDomain(): RandomCreatorModel = RandomCreatorModel(
            service = this.service,
            artistId = this.artistId,
        )
    }
}