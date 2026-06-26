package su.afk.kemonos.storage.repository.profilePosts

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import su.afk.kemonos.storage.entity.profilePosts.dao.CreatorPostsCacheDao
import su.afk.kemonos.storage.entity.profilePosts.mapper.CreatorPostCacheMapper
import javax.inject.Inject

internal class StorageCreatorPostsRepository @Inject constructor(
    private val dao: CreatorPostsCacheDao,
    private val mapper: CreatorPostCacheMapper,
) : IStorageCreatorPostsRepository {

    override suspend fun getFreshPageOrNull(site: SelectedSite, queryKey: String, offset: Int): List<PostDomain>? {
        val siteQueryKey = queryKey.cacheKey(site)
        val minTs = System.currentTimeMillis() - ttlFor(queryKey)

        val rows = dao.getFreshPage(
            queryKey = siteQueryKey,
            offset = offset,
            minUpdatedAt = minTs
        )

        return rows.takeIf { it.isNotEmpty() }?.map(mapper::toDomain)
    }

    override suspend fun getStalePageOrEmpty(site: SelectedSite, queryKey: String, offset: Int): List<PostDomain> =
        dao.getPage(queryKey.cacheKey(site), offset).map(mapper::toDomain)

    override suspend fun putPage(site: SelectedSite, queryKey: String, offset: Int, items: List<PostDomain>) {
        val siteQueryKey = queryKey.cacheKey(site)
        dao.deletePage(siteQueryKey, offset)

        if (items.isEmpty()) return

        val now = System.currentTimeMillis()
        val entities = items.mapIndexed { index, post ->
            mapper.toEntity(
                post = post,
                queryKey = siteQueryKey,
                offset = offset,
                indexInPage = index,
                updatedAt = now
            )
        }

        dao.upsertAll(entities)
    }

    override suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int) {
        dao.deletePage(queryKey.cacheKey(site), offset)
    }

    override suspend fun clearQuery(site: SelectedSite, queryKey: String) {
        dao.deleteByQueryKey(queryKey.cacheKey(site))
    }

    override suspend fun clearCache() {
        val now = System.currentTimeMillis()

        /** search/tag (1 час) */
        dao.deleteExpiredSearchTag(minUpdatedAt = now - TTL_1_HOURS)

        /** default (3 дня) */
        dao.deleteExpiredDefault(minUpdatedAt = now - TTL_3_DAYS)
    }

    override suspend fun clearAll() = dao.clearAll()

    private fun ttlFor(queryKey: String): Long =
        if (isSearchOrTag(queryKey)) TTL_1_HOURS else TTL_3_DAYS

    /**
     * queryKey = service|userId|search|tag
     * search/tag считаем активным, если хоть одно не пустое.
     */
    private fun isSearchOrTag(queryKey: String): Boolean {
        val parts = queryKey.removeSitePrefix().split('|')
        val search = parts.getOrNull(2).orEmpty()
        val tag = parts.getOrNull(3).orEmpty()
        return search.isNotBlank() || tag.isNotBlank()
    }

    private fun String.cacheKey(site: SelectedSite): String = "${site.name}|$this"

    private fun String.removeSitePrefix(): String =
        substringAfter('|', missingDelimiterValue = this)
}
