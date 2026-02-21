package su.afk.kemonos.storage.repository.postsSearchHistory

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.repository.postsSearchHistory.IStoragePostsSearchHistoryRepository
import su.afk.kemonos.storage.entity.postsSearch.history.PostsSearchHistoryEntity
import su.afk.kemonos.storage.entity.postsSearch.history.dao.CoomerPostsSearchHistoryDao
import su.afk.kemonos.storage.entity.postsSearch.history.dao.KemonoPostsSearchHistoryDao
import javax.inject.Inject

internal class StoragePostsSearchHistoryRepository @Inject constructor(
    private val kemonoDao: KemonoPostsSearchHistoryDao,
    private val coomerDao: CoomerPostsSearchHistoryDao,
) : IStoragePostsSearchHistoryRepository {
    private companion object {
        const val MAX_HISTORY_LIMIT = 10
    }

    override fun observeRecent(site: SelectedSite, limit: Int): Flow<List<String>> =
        when (site) {
            SelectedSite.K -> kemonoDao.observeRecent(limit.coerceAtMost(MAX_HISTORY_LIMIT))
            SelectedSite.C -> coomerDao.observeRecent(limit.coerceAtMost(MAX_HISTORY_LIMIT))
        }

    override suspend fun save(site: SelectedSite, query: String, limit: Int) {
        val appliedLimit = limit.coerceAtMost(MAX_HISTORY_LIMIT)
        val normalized = query.trim()
        if (normalized.isEmpty()) return

        val item = PostsSearchHistoryEntity(
            query = normalized,
            updatedAt = System.currentTimeMillis()
        )
        when (site) {
            SelectedSite.K -> kemonoDao.saveAndTrim(item, appliedLimit)
            SelectedSite.C -> coomerDao.saveAndTrim(item, appliedLimit)
        }
    }

    override suspend fun delete(site: SelectedSite, query: String) {
        val normalized = query.trim()
        if (normalized.isEmpty()) return
        when (site) {
            SelectedSite.K -> kemonoDao.delete(normalized)
            SelectedSite.C -> coomerDao.delete(normalized)
        }
    }
}
