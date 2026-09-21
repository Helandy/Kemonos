package su.afk.kemonos.utils.download

import su.afk.kemonos.utils.file.extensionOrNull

/** Короче — уже осмысленное имя, а не хеш. sha256 даёт 64 символа. */
private const val MIN_HASH_NAME_LENGTH = 24

private val HEX_ONLY = Regex("^[0-9a-fA-F]+$")

/**
 * Имя файла — хеш содержимого, а не название.
 *
 * Pawchive отдаёт вложения как `8f3c…c1.png`: такое имя ничего не говорит о
 * порядке страниц, поэтому его есть смысл заменить на порядковый номер.
 */
fun isHashLikeFileName(name: String?): Boolean {
    val base = name.orEmpty()
        .substringAfterLast('/')
        .substringBefore('?')
        .substringBeforeLast('.')
        .replace("-", "")

    return base.length >= MIN_HASH_NAME_LENGTH && HEX_ONLY.matches(base)
}

/**
 * Порядковый номер вместо имени: `1.png`, `2.webp`.
 *
 * Расширение берём из исходного имени, а если его там нет — из ссылки.
 */
fun numberedFileName(number: Int, sourceName: String?, sourceUrl: String? = null): String {
    val extension = sourceName?.extensionOrNull() ?: sourceUrl?.extensionOrNull()
    return if (extension.isNullOrBlank()) "$number" else "$number.$extension"
}
