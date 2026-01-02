package su.afk.kemonos.common.data.dto


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.FileDomain

data class FileDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("path")
    val path: String?
) {
    companion object {
        fun FileDto.toDomain() = FileDomain(
            name = name.orEmpty(),
            path = path.orEmpty()
        )
    }
}