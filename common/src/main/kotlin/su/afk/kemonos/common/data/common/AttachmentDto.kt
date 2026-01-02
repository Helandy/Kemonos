package su.afk.kemonos.common.data.common


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.AttachmentDomain

data class AttachmentDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("path")
    val path: String,
    @SerializedName("server")
    val server: String?,
) {
    companion object {
        fun AttachmentDto.toDomain(): AttachmentDomain =
            AttachmentDomain(
                name = this.name,
                path = this.path,
                server = this.server
            )
    }
}