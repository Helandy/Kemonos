package su.afk.kemonos.preferences

import su.afk.kemonos.utils.url.toRootUrl
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