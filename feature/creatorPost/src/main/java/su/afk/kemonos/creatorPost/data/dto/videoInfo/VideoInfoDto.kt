package su.afk.kemonos.creatorPost.data.dto.videoInfo

import com.google.gson.annotations.SerializedName

internal data class VideoInfoDto(
    @SerializedName("site")
    val site: String,

    @SerializedName("server")
    val server: String?,

    @SerializedName("path")
    val path: String,
)
