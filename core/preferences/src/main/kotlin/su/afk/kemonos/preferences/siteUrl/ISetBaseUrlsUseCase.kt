package su.afk.kemonos.preferences.siteUrl

import su.afk.kemonos.domain.SelectedSite

/** null в переопределении хоста = не трогать сохранённое значение. */
data class SiteUrlUpdate(
    val apiUrl: String,
    val imageHostOverride: String? = null,
    val fileHostOverride: String? = null,
)

interface ISetBaseUrlsUseCase {
    suspend operator fun invoke(updates: Map<SelectedSite, SiteUrlUpdate>)
}
