package su.afk.kemonos.storage.repository.postsSearchHistory

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.repository.postsSearchHistory.IStoragePostsSearchHistoryRepository
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity
import su.afk.kemonos.storage.entity.postsSearch.history.dao.PostsSearchHistoryDao
import javax.inject.Inject

internal class StoragePostsSearchHistoryRepository @Inject constructor(
    private val daos: Map<SelectedSite, @JvmSuppressWildcards PostsSearchHistoryDao>,
) : IStoragePostsSearchHistoryRepository {
    private companion object {
        const val MAX_HISTORY_LIMIT = 25
    }

    private fun dao(site: SelectedSite): PostsSearchHistoryDao = daos.getValue(site)

    override fun observeRecent(site: SelectedSite, limit: Int): Flow<List<String>> =
        dao(site).observeRecent(limit.coerceAtMost(MAX_HISTORY_LIMIT))

    override suspend fun save(site: SelectedSite, query: String, limit: Int) {
        val normalized = query.trim()
        if (normalized.isEmpty()) return

        val item = PostsSearchHistoryEntity(
            query = normalized,
            updatedAt = System.currentTimeMillis()
        )
        dao(site).saveAndTrim(item, limit.coerceAtMost(MAX_HISTORY_LIMIT))
    }

    override suspend fun delete(site: SelectedSite, query: String) {
        val normalized = query.trim()
        if (normalized.isEmpty()) return
        dao(site).delete(normalized)
    }
}
