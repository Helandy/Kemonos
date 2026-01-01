package su.afk.kemonos.core.preferences.siteUrl

import su.afk.kemonos.core.api.domain.useCase.ISetBaseUrlsUseCase
import su.afk.kemonos.core.preferences.UrlPrefs
import javax.inject.Inject

internal class SetBaseUrlsUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) : ISetBaseUrlsUseCase {

    override suspend fun invoke(kemonoUrl: String, coomerUrl: String) {
        urlPrefs.setKemonoUrl(kemonoUrl)
        urlPrefs.setCoomerUrl(coomerUrl)
    }
}