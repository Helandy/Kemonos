package su.afk.kemonos.storage.repository.localLikes

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.storage.api.repository.localLikes.IStoreLocalLikedArtistsRepository
import su.afk.kemonos.storage.entity.localLikes.LocalLikedArtistEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.localLikes.LocalLikedArtistEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.localLikes.LocalLikedArtistsDao
import javax.inject.Inject

internal class StoreLocalLikedArtistsRepository @Inject constructor(
    private val dao: LocalLikedArtistsDao,
) : IStoreLocalLikedArtistsRepository {

    override suspend fun page(site: SelectedSite, query: String, limit: Int, offset: Int): List<FavoriteArtist> =
        dao.page(site = site, query = query, limit = limit, offset = offset).map { it.toDomain() }

    override suspend fun getAll(site: SelectedSite): List<FavoriteArtist> =
        dao.getAll(site).map { it.toDomain() }

    override suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean =
        dao.exists(site, service, creatorId)

    override suspend fun add(site: SelectedSite, item: FavoriteArtist) {
        dao.upsert(item.toEntity(site))
    }

    override suspend fun remove(site: SelectedSite, service: String, id: String) {
        dao.delete(site, service, id)
    }

    override suspend fun clear(site: SelectedSite) {
        dao.clear(site)
    }
}
