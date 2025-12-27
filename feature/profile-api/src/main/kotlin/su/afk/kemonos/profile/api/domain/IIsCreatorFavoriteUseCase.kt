package su.afk.kemonos.profile.api.domain

import su.afk.kemonos.domain.SelectedSite

interface IIsCreatorFavoriteUseCase {
    suspend fun invoke(site: SelectedSite, service: String, id: String): Boolean
}