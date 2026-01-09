package su.afk.kemonos.storage.repository.profilePosts

import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_1_HOURS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.storage.entity.profilePosts.dao.CreatorPostsCacheDao
import su.afk.kemonos.storage.entity.profilePosts.mapper.CreatorPostCacheMapper
import javax.inject.Inject

interface IStorageCreatorPostsCacheRepository {
    suspend fun getFreshPageOrNull(queryKey: String, offset: Int): List<PostDomain>?
    suspend fun getStalePageOrEmpty(queryKey: String, offset: Int): List<PostDomain>
    suspend fun putPage(queryKey: String, offset: Int, items: List<PostDomain>)
    suspend fun clearQuery(queryKey: String)
    suspend fun clearPage(queryKey: String, offset: Int)
    suspend fun clearCache()
    suspend fun clearAll()
}

internal class StorageCreatorPostsCacheRepository @Inject constructor(
    private val dao: CreatorPostsCacheDao,
    private val mapper: CreatorPostCacheMapper,
) : IStorageCreatorPostsCacheRepository {

    override suspend fun getFreshPageOrNull(queryKey: String, offset: Int): List<PostDomain>? {
        val minTs = System.currentTimeMillis() - ttlFor(queryKey)

        val rows = dao.getFreshPage(
            queryKey = queryKey,
            offset = offset,
            minUpdatedAt = minTs
        )

        return rows.takeIf { it.isNotEmpty() }?.map(mapper::toDomain)
    }

    override suspend fun getStalePageOrEmpty(queryKey: String, offset: Int): List<PostDomain> =
        dao.getPage(queryKey, offset).map(mapper::toDomain)

    override suspend fun putPage(queryKey: String, offset: Int, items: List<PostDomain>) {
        dao.deletePage(queryKey, offset)

        if (items.isEmpty()) return

        val now = System.currentTimeMillis()
        val entities = items.mapIndexed { index, post ->
            mapper.toEntity(
                domain = post,
                queryKey = queryKey,
                offset = offset,
                indexInPage = index,
                updatedAt = now
            )
        }

        dao.upsertAll(entities)
    }

    override suspend fun clearPage(queryKey: String, offset: Int) {
        dao.deletePage(queryKey, offset)
    }

    override suspend fun clearQuery(queryKey: String) {
        dao.deleteByQueryKey(queryKey)
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
        val parts = queryKey.split('|')
        val search = parts.getOrNull(2).orEmpty()
        val tag = parts.getOrNull(3).orEmpty()
        return search.isNotBlank() || tag.isNotBlank()
    }
}