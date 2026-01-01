package su.afk.kemonos.core.preferences

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetKemonoRootUrlUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) {
    operator fun invoke(): String {
        return urlPrefs.kemonoUrl.value.toRootUrl()
    }
}

@Singleton
class GetCoomerRootUrlUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) {
    operator fun invoke(): String {
        return urlPrefs.coomerUrl.value.toRootUrl()
    }
}

private fun String.toRootUrl(): String {
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
