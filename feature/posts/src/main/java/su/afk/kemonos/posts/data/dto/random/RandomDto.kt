package su.afk.kemonos.posts.data.dto.random

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.posts.domain.model.random.RandomDomain

internal data class RandomDto(
    @SerializedName("service")
    val service: String,

    @SerializedName("artist_id")
    val artistId: String,

    @SerializedName("post_id")
    val postId: String,
) {
    companion object {
        fun RandomDto.toDomain() = RandomDomain(
            service = this.service,
            artistId = this.artistId,
            postId = this.postId
        )
    }
}