package su.afk.kemonos.core.preferences.siteUrl

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.core.api.domain.useCase.IGetBaseUrlsUseCase
import su.afk.kemonos.core.preferences.UrlPrefs
import javax.inject.Inject

internal class GetFlowBaseUrlPrefsUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) : IGetBaseUrlsUseCase {

    override val kemonoUrl: StateFlow<String> = urlPrefs.kemonoUrl
    override val coomerUrl: StateFlow<String> = urlPrefs.coomerUrl
}