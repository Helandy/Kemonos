package su.afk.kemonos.storage.repository.favorites.artist

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheTimestampUseCase
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
    private val cacheTimestamps: CacheTimestampUseCase,
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
        return System.currentTimeMillis() - ts < CACHE_TTL_MS
    }

    override suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean =
        dao.exists(site, service, creatorId)

    private fun cacheKey(site: SelectedSite) = "${KEY}_${site.name}"

    private companion object {
        private const val CACHE_TTL_MS = 1L * 60 * 60 * 1000
        private const val KEY = "favorites_artists_cache_time"
    }
}
