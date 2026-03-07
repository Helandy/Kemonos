package su.afk.kemonos.creatorPost.data.dto.file

import com.google.gson.annotations.SerializedName

internal data class FileInfoRequestDto(
    @SerializedName("site")
    val site: String,

    @SerializedName("server")
    val server: String?,

    @SerializedName("path")
    val path: String,
)
