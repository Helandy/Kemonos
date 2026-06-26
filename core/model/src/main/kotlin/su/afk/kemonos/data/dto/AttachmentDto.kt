package su.afk.kemonos.data.dto


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.AttachmentDomain

data class AttachmentDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("path")
    val path: String?,
    @SerializedName("server")
    val server: String?,
) {
    companion object {
        fun AttachmentDto.toDomain(): AttachmentDomain =
            AttachmentDomain(
                name = this.name,
                path = this.path.orEmpty(),
                server = this.server
            )

        fun AttachmentDto.toDomainOrNull(): AttachmentDomain? {
            val normalizedPath = path?.takeIf { it.isNotBlank() } ?: return null

            return AttachmentDomain(
                name = this.name,
                path = normalizedPath,
                server = this.server,
            )
        }
    }
}
