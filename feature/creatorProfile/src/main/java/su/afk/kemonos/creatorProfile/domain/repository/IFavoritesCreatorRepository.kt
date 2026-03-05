package su.afk.kemonos.creatorProfile.domain.repository

import su.afk.kemonos.domain.SelectedSite

internal interface IFavoritesCreatorRepository {
    suspend fun addCreator(site: SelectedSite, service: String, id: String): Result<Unit>
    suspend fun removeCreator(site: SelectedSite, service: String, id: String): Result<Unit>
}