package su.afk.kemonos.common.presenter.androidView


/**
 * Удаляет из HTML inline-картинки (и их обёртки), если они соответствуют файлам из attachments/file.
 *
 * Работает с паттернами:
 * - <a href="https://n3.kemono.cr/data/..../file.jpg" class="inlineThumb"> ... </a>
 * - <img src="/data/..../file.jpg" ...>
 * - <img data-src="https://n3.kemono.cr/data/..../file.jpg" ...>
 *
 * НЕ трогает обычные ссылки на внешние сайты (mingazofff, boosty и т.п.)
 */
fun cleanDuplicatedMediaFromContent(
    html: String,
    attachmentPaths: List<String>,
): String {
    if (html.isBlank() || attachmentPaths.isEmpty()) return html

    // Нормализуем пути: "/32/d5/xxx.jpg" и "/data/32/d5/xxx.jpg" -> "32/d5/xxx.jpg"
    val normalizedPaths = attachmentPaths
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { ref ->
            // ref может быть:
            // - "/32/d5/xxx.jpg"
            // - "/data/32/d5/xxx.jpg"
            // - "https://n3.kemono.cr/data/32/d5/xxx.jpg"
            // Приводим к "32/d5/xxx.jpg" где возможно
            ref
                .substringAfter("/data/", missingDelimiterValue = ref)
                .removePrefix("data/")
                .removePrefix("/")
        }
        .toSet()

    if (normalizedPaths.isEmpty()) return html

    fun String.containsAnyAttachmentPath(): Boolean {
        val u = this.trim()
        // Проверяем по суффиксу: .../data/32/d5/file.jpg  или .../32/d5/file.jpg
        return normalizedPaths.any { tail ->
            u.endsWith("/$tail", ignoreCase = true) ||
                    u.endsWith("/data/$tail", ignoreCase = true) ||
                    u.contains("/data/$tail", ignoreCase = true) ||
                    u.contains("/$tail", ignoreCase = true)
        }
    }

    // 1) Удаляем обёртки inlineThumb целиком (внутри обычно thumbnail + data-src)
    val inlineThumbAnchor = Regex(
        """<a\b[^>]*class\s*=\s*(['"])[^'"]*\binlineThumb\b[^'"]*\1[^>]*>.*?</a>""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )

    var out = html.replace(inlineThumbAnchor) { mr ->
        val block = mr.value

        // вытащим href внутри блока
        val href = Regex(
            """\bhref\s*=\s*(['"])([^'"]+)\1""",
            RegexOption.IGNORE_CASE
        ).find(block)?.groupValues?.getOrNull(2)

        // если href ведёт на attachment — вырезаем весь блок
        if (href != null && href.containsAnyAttachmentPath()) "" else block
    }

    // 2) Удаляем одиночные <img ...> если src/data-src указывает на attachment
    val imgTag = Regex(
        """<img\b[^>]*>""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )

    val attrUrl = Regex(
        """\b(src|data-src)\s*=\s*(['"])([^'"]+)\2""",
        RegexOption.IGNORE_CASE
    )

    out = out.replace(imgTag) { mr ->
        val tag = mr.value
        val urls = attrUrl.findAll(tag).map { it.groupValues[3] }.toList()
        val isAttachmentImg = urls.any { it.containsAnyAttachmentPath() }
        if (isAttachmentImg) "" else tag
    }

    // 3) Чуть подчистим пустые контейнеры вида <div><span></span></div> после вырезаний
    out = out
        .replace(Regex("""<div>\s*</div>""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""<span>\s*</span>""", RegexOption.IGNORE_CASE), "")

    return out
}