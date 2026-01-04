package su.afk.kemonos.storage.repository.favorites.artist

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheKeys.FAVORITES_ARTISTS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.storage.entity.favorites.artist.FavoriteArtistEntity.Companion.toDomain
import su.afk.kemonos.storage.entity.favorites.artist.FavoriteArtistEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.favorites.artist.FavoriteArtistsDao
import javax.inject.Inject

interface IStoreFavoriteArtistsRepository {
    suspend fun getAll(site: SelectedSite): List<FavoriteArtist>
    suspend fun replaceAll(site: SelectedSite, items: List<FavoriteArtist>)
    suspend fun remove(site: SelectedSite, service: String, id: String)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
    suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean
}

internal class StoreFavoriteArtistsRepository @Inject constructor(
    private val dao: FavoriteArtistsDao,
    private val cacheTimestamps: ICacheTimestampUseCase,
) : IStoreFavoriteArtistsRepository {

    override suspend fun getAll(site: SelectedSite): List<FavoriteArtist> =
        dao.getAll(site).map { it.toDomain() }

    override suspend fun replaceAll(site: SelectedSite, items: List<FavoriteArtist>) {
        dao.replaceAll(site, items.map { it.toEntity(site) })
        cacheTimestamps.updateCacheTimestamp(cacheKey(site))
    }

    override suspend fun remove(site: SelectedSite, service: String, id: String) {
        dao.delete(site, service, id)
    }

    override suspend fun clear(site: SelectedSite) {
        dao.clear(site)
        cacheTimestamps.clearCacheTimestamp(cacheKey(site))
    }

    override suspend fun isCacheFresh(site: SelectedSite): Boolean {
        val ts = cacheTimestamps.getCacheTimestamp(cacheKey(site))
        if (ts == 0L) return false
        return System.currentTimeMillis() - ts < TTL_1_HOURS
    }

    override suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean =
        dao.exists(site, service, creatorId)

    private fun cacheKey(site: SelectedSite) = "${FAVORITES_ARTISTS}_${site.name}"
}
