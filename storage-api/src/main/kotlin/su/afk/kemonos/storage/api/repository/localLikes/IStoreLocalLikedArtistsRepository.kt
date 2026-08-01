package su.afk.kemonos.storage.api.repository.localLikes

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist

interface IStoreLocalLikedArtistsRepository {
    suspend fun page(site: SelectedSite, query: String, limit: Int, offset: Int): List<FavoriteArtist>

    suspend fun getAll(site: SelectedSite): List<FavoriteArtist>

    suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean

    suspend fun add(site: SelectedSite, item: FavoriteArtist)
    suspend fun remove(site: SelectedSite, service: String, id: String)
    suspend fun clear(site: SelectedSite)
}
