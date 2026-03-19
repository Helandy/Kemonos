package su.afk.kemonos.creatorPost.data.dto.file

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorPost.api.domain.model.media.MediaInfo

internal data class FileInfoResponseDto(
    @SerializedName("sizeBytes")
    val sizeBytes: Long,

    @SerializedName("durationSeconds")
    val durationSeconds: Long,

    @SerializedName("lastStatusCode")
    val lastStatusCode: Int,
) {
    companion object {
        fun FileInfoResponseDto.toMediaInfo(): MediaInfo =
            MediaInfo(
                durationMs = durationSeconds * 1000L,
                sizeBytes = sizeBytes,
                durationSeconds = durationSeconds,
                lastStatusCode = lastStatusCode,
            )
    }
}
