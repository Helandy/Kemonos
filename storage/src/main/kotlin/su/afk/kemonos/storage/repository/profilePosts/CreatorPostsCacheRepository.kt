package su.afk.kemonos.storage.repository.profilePosts

import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.storage.entity.profilePosts.dao.CreatorPostsCacheDao
import su.afk.kemonos.storage.entity.profilePosts.mapper.CreatorPostCacheMapper
import javax.inject.Inject

interface ICreatorPostsCacheRepository {
    suspend fun getFreshPageOrNull(queryKey: String, offset: Int): List<PostDomain>?
    suspend fun getStalePageOrEmpty(queryKey: String, offset: Int): List<PostDomain>
    suspend fun putPage(queryKey: String, offset: Int, items: List<PostDomain>)
    suspend fun clearPage(queryKey: String, offset: Int)
    suspend fun clearCache()
    suspend fun clearAll()
}

internal class CreatorPostsCacheRepository @Inject constructor(
    private val dao: CreatorPostsCacheDao,
    private val mapper: CreatorPostCacheMapper,
) : ICreatorPostsCacheRepository {

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

    override suspend fun clearCache() {
        val now = System.currentTimeMillis()

        /** search/tag (1 час) */
        dao.deleteExpiredSearchTag(minUpdatedAt = now - TTL_SEARCH_TAG)

        /** default (3 дня) */
        dao.deleteExpiredDefault(minUpdatedAt = now - TTL_DEFAULT)
    }

    override suspend fun clearAll() = dao.clearAll()

    private fun ttlFor(queryKey: String): Long =
        if (isSearchOrTag(queryKey)) TTL_SEARCH_TAG else TTL_DEFAULT

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

    private companion object {
        /** 1 час */
        private const val TTL_SEARCH_TAG = 1L * 60 * 60 * 1000
        /** 3 дня */
        private const val TTL_DEFAULT = 3L * 24 * 60 * 60 * 1000
    }
}