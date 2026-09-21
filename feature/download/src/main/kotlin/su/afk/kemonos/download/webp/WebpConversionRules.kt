package su.afk.kemonos.download.webp

/**
 * Что имеет смысл пережимать в webp.
 *
 * png/jpeg дают заметный выигрыш. Уже сжатый webp трогать нельзя — повторное
 * кодирование только ухудшит картинку, почти ничего не сэкономив; gif потерял бы
 * анимацию, а всё остальное вообще не картинки.
 */
internal object WebpConversionRules {

    private val CONVERTIBLE_EXTENSIONS = setOf("png", "jpg", "jpeg", "bmp")

    fun shouldConvert(fileName: String?): Boolean {
        val extension = fileName.extensionOrNull() ?: return false
        return extension in CONVERTIBLE_EXTENSIONS
    }

    /** `1.png` -> `1.webp`; имя без расширения получает его впервые. */
    fun targetFileName(fileName: String): String {
        val base = fileName.substringBeforeLast('.', missingDelimiterValue = fileName)
        return "$base.webp"
    }

    private fun String?.extensionOrNull(): String? = this
        ?.substringAfterLast('/')
        ?.substringBefore('?')
        ?.substringAfterLast('.', missingDelimiterValue = "")
        ?.lowercase()
        ?.takeIf { it.isNotBlank() }
}
