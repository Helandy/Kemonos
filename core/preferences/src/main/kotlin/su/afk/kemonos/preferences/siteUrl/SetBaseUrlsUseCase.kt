package su.afk.kemonos.preferences.siteUrl

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.UrlPrefs
import javax.inject.Inject

internal class SetBaseUrlsUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) : ISetBaseUrlsUseCase {

    override suspend fun invoke(updates: Map<SelectedSite, SiteUrlUpdate>) {
        updates.forEach { (site, update) ->
            urlPrefs.setSiteUrl(site, update.apiUrl)
            update.imageHostOverride?.let { urlPrefs.setImageHostOverride(site, it) }
            update.fileHostOverride?.let { urlPrefs.setFileHostOverride(site, it) }
        }
    }
}
