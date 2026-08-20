package su.afk.kemonos.data.dto.onlyhaven

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.AttachmentDomain
import su.afk.kemonos.domain.models.FileDomain

data class OnlyHavenVariantDto(
    @SerializedName("name") val name: String?,
    @SerializedName("bytes") val bytes: Long?,
)

/**
 * Вложение OnlyHaven.
 *
 * В спеке это oneOf, но обе ветки различаются только набором полей и флагом
 * [locked], поэтому хватает одного плоского DTO: у платных записей файла нет
 * вообще (только цена), у остальных заполнены sha256/variants.
 */
data class OnlyHavenAttachmentDto(
    @SerializedName("locked") val locked: Boolean = false,
    @SerializedName("position") val position: Int = 0,

    /** доступные */
    @SerializedName("sha256") val sha256: String? = null,
    @SerializedName("kind") val kind: String? = null,
    @SerializedName("mimeType") val mimeType: String? = null,
    @SerializedName("variants") val variants: List<OnlyHavenVariantDto>? = null,
    @SerializedName("durationMs") val durationMs: Long? = null,
    @SerializedName("bytes") val bytes: Long? = null,
    @SerializedName("originalFilename") val originalFilename: String? = null,

    /** платные */
    @SerializedName("externalId") val externalId: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("priceCents") val priceCents: Int? = null,
) {
    companion object {

        /** Полноразмерный вариант: предпочитаем original, иначе первый доступный. */
        private fun OnlyHavenAttachmentDto.fullVariantName(): String? =
            variants?.firstOrNull { it.name?.startsWith("original") == true }?.name
                ?: variants?.firstOrNull()?.name

        /**
         * Ссылок в ответе нет — путь собирается из sha256 и имени варианта.
         * Хост подставляется на уровне [AttachmentDomain.server].
         */
        fun OnlyHavenAttachmentDto.toDomainOrNull(fileBaseUrl: String): AttachmentDomain? {
            if (locked) return null
            val hash = sha256?.takeIf { it.isNotBlank() } ?: return null
            val variant = fullVariantName() ?: return null

            return AttachmentDomain(
                server = fileBaseUrl,
                path = "/media/$hash/$variant",
                name = originalFilename?.takeIf { it.isNotBlank() } ?: variant,
                thumbnailPath = "/thumbnail/$hash/preview.webp",
            )
        }

        fun OnlyHavenAttachmentDto.toFileDomainOrNull(fileBaseUrl: String): FileDomain? =
            toDomainOrNull(fileBaseUrl)?.let { FileDomain(name = it.name.orEmpty(), path = it.path) }
    }
}
