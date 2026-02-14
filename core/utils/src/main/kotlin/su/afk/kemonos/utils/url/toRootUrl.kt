package su.afk.kemonos.utils.url

fun String.toRootUrl(): String {
    val trimmed = trim()

    val withoutTrailingSlash = trimmed.removeSuffix("/")

    /** если заканчивается на "/api" — срезаем его */
    val withoutApi = if (withoutTrailingSlash.endsWith("/api")) {
        withoutTrailingSlash.removeSuffix("/api")
    } else {
        withoutTrailingSlash
    }

    return withoutApi
}
