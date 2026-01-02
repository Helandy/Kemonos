package su.afk.kemonos.preferences.site

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.domain.SelectedSite

interface ISelectedSiteUseCase {
    val selectedSite: StateFlow<SelectedSite>

    suspend fun setSite(site: SelectedSite)

    fun getSite(): SelectedSite
}