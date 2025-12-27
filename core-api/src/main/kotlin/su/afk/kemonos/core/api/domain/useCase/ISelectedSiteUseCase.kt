package su.afk.kemonos.core.api.domain.useCase

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.domain.SelectedSite

interface ISelectedSiteUseCase {
    val selectedSite: StateFlow<SelectedSite>

    suspend fun setSite(site: SelectedSite)

    fun getSite(): SelectedSite
}