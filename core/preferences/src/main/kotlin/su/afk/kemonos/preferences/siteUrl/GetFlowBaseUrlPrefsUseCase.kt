package su.afk.kemonos.preferences.siteUrl

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.UrlPrefs
import javax.inject.Inject

internal class GetFlowBaseUrlPrefsUseCase @Inject constructor(
    urlPrefs: UrlPrefs,
) : IGetBaseUrlsUseCase {

    override val siteUrls: Map<SelectedSite, StateFlow<String>> = urlPrefs.siteUrls
    override val imageHostOverrides: Map<SelectedSite, StateFlow<String>> = urlPrefs.imageHostOverrides
    override val fileHostOverrides: Map<SelectedSite, StateFlow<String>> = urlPrefs.fileHostOverrides
}
