package su.afk.kemonos.storage.api.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.FavoriteArtist

interface IStoreFavoriteArtistsUseCase {
    suspend fun getAll(site: SelectedSite): List<FavoriteArtist>
    suspend fun replaceAll(site: SelectedSite, items: List<FavoriteArtist>)
    suspend fun remove(site: SelectedSite, service: String, id: String)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
    suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean
}