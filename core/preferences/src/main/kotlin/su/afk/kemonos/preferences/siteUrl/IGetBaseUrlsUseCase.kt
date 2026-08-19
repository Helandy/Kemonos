package su.afk.kemonos.preferences.siteUrl

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.domain.SelectedSite

interface IGetBaseUrlsUseCase {
    val siteUrls: Map<SelectedSite, StateFlow<String>>
    val imageHostOverrides: Map<SelectedSite, StateFlow<String>>
    val fileHostOverrides: Map<SelectedSite, StateFlow<String>>

    fun siteUrl(site: SelectedSite): StateFlow<String> = siteUrls.getValue(site)
    fun imageHostOverride(site: SelectedSite): StateFlow<String> = imageHostOverrides.getValue(site)
    fun fileHostOverride(site: SelectedSite): StateFlow<String> = fileHostOverrides.getValue(site)
}
