package su.afk.kemonos.storage.repository.favorites.post

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.useCase.CacheTimestampUseCase
import su.afk.kemonos.storage.entity.favorites.post.FavoritePostsDao
import su.afk.kemonos.storage.entity.favorites.post.mapper.FavoritePostMapper
import javax.inject.Inject

interface IStoreFavoritePostsRepository {
    suspend fun getAll(site: SelectedSite): List<PostDomain>
    suspend fun replaceAll(site: SelectedSite, items: List<PostDomain>)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
    suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean

    suspend fun add(site: SelectedSite, item: PostDomain)
    suspend fun remove(site: SelectedSite, service: String, creatorId: String, postId: String)
}

internal class StoreFavoritePostsRepository @Inject constructor(
    private val dao: FavoritePostsDao,
    private val cacheTimestamps: CacheTimestampUseCase,
    private val mapper: FavoritePostMapper,
) : IStoreFavoritePostsRepository {

    override suspend fun getAll(site: SelectedSite): List<PostDomain> =
        dao.getAll(site).map(mapper::toDomain)

    override suspend fun replaceAll(site: SelectedSite, items: List<PostDomain>) {
        dao.replaceAll(site, items.map { mapper.toEntity(site, it) })
        cacheTimestamps.updateCacheTimestamp(cacheKey(site))
    }

    override suspend fun add(site: SelectedSite, item: PostDomain) {
        dao.upsert(mapper.toEntity(site, item))
    }

    override suspend fun remove(site: SelectedSite, service: String, creatorId: String, postId: String) {
        dao.delete(site, service, creatorId, postId)
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

    override suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean =
        dao.exists(site, service, creatorId, postId)

    private fun cacheKey(site: SelectedSite) = "${KEY}_${site.name}"

    private companion object {
        private const val CACHE_TTL_MS = 1L * 60 * 60 * 1000 // 1 час
        private const val KEY = "favorites_posts_cache_time"
    }
}
