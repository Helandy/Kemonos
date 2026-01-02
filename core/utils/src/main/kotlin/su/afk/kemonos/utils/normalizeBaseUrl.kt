package su.afk.kemonos.utils

fun String.normalizeBaseUrl(): String {
    var url = trim()
    if (url.isEmpty()) return url

    /** если юзер ввёл без схемы */
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "https://$url"
    }

    /** убираем хвостовые слеши для нормальной склейки */
    url = url.trimEnd('/')

    /** добавляем /api если его нет как сегмента */
    val hasApiSegment = url.endsWith("/api") || url.contains("/api/")
    if (!hasApiSegment) {
        url += "/api"
    }

    /** retrofit baseUrl должен заканчиваться на / */
    return url.trimEnd('/') + "/"
}