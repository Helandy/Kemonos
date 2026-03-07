package su.afk.kemonos.creatorPost.data.dto.videoInfo

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorPost.domain.videoInfo.model.VideoInfo

internal data class VideoInfoResponseDto(
    @SerializedName(value = "path")
    val path: String? = null,

    @SerializedName("sizeBytes")
    val sizeBytes: Long,

    @SerializedName("durationSeconds")
    val durationSeconds: Long,

    @SerializedName("lastStatusCode")
    val lastStatusCode: Int,
) {
    companion object {
        fun VideoInfoResponseDto.toDomain() = VideoInfo(
            path = path.orEmpty(),
            sizeBytes = sizeBytes,
            durationSeconds = durationSeconds,
            lastStatusCode = lastStatusCode,
        )
    }
}
