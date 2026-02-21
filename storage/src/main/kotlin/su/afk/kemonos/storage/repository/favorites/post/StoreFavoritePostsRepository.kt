package su.afk.kemonos.storage.repository.favorites.post

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.useCase.CacheKeys.FAVORITES_POSTS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import su.afk.kemonos.storage.entity.favorites.post.FavoritePostsDao
import su.afk.kemonos.storage.entity.favorites.post.mapper.FavoritePostMapper
import javax.inject.Inject

internal class StoreFavoritePostsRepository @Inject constructor(
    private val dao: FavoritePostsDao,
    private val cacheTimestamps: ICacheTimestampUseCase,
    private val mapper: FavoritePostMapper,
) : IStoreFavoritePostsRepository {

    override suspend fun page(site: SelectedSite, limit: Int, offset: Int): List<PostDomain> =
        dao.page(site = site, limit = limit, offset = offset).map(mapper::toDomain)

    override suspend fun pageSearch(site: SelectedSite, query: String, limit: Int, offset: Int): List<PostDomain> =
        dao.pageSearch(site = site, query = query, limit = limit, offset = offset).map(mapper::toDomain)

    override suspend fun pageGrouped(site: SelectedSite, limit: Int, offset: Int): List<PostDomain> =
        dao.pageGrouped(site = site, limit = limit, offset = offset).map(mapper::toDomain)

    override suspend fun pageSearchGrouped(
        site: SelectedSite,
        query: String,
        limit: Int,
        offset: Int
    ): List<PostDomain> =
        dao.pageSearchGrouped(site = site, query = query, limit = limit, offset = offset).map(mapper::toDomain)

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
        return System.currentTimeMillis() - ts < TTL_1_HOURS
    }

    override suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean =
        dao.exists(site, service, creatorId, postId)

    private fun cacheKey(site: SelectedSite) = "${FAVORITES_POSTS}_${site.name}"
}
