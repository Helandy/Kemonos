package su.afk.kemonos.preferences.siteUrl

import su.afk.kemonos.preferences.UrlPrefs
import javax.inject.Inject

internal class SetBaseUrlsUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) : ISetBaseUrlsUseCase {

    override suspend fun invoke(kemonoUrl: String, coomerUrl: String) {
        urlPrefs.setKemonoUrl(kemonoUrl)
        urlPrefs.setCoomerUrl(coomerUrl)
    }
}