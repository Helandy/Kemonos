package su.afk.kemonos.data.dto

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.IncompleteRewards

data class IncompleteRewardsDto(
    @SerializedName("price")
    val price: Double?,
    @SerializedName("media_count")
    val mediaCount: Int?,
    @SerializedName("photo_count")
    val photoCount: Int?,
    @SerializedName("video_count")
    val videoCount: Int?,
    @SerializedName("complete")
    val complete: Int?,
    @SerializedName("incomplete")
    val incomplete: Int?
) {
    companion object {
        fun IncompleteRewardsDto.toDomain(): IncompleteRewards =
            IncompleteRewards(
                price = this.price,
                mediaCount = this.mediaCount,
                photoCount = this.photoCount,
                videoCount = this.videoCount,
                completeCount = this.complete,
                incompleteCount = this.incomplete
            )
    }
}