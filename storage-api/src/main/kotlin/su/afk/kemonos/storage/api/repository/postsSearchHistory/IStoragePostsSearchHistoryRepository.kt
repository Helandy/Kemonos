package su.afk.kemonos.storage.api.repository.postsSearchHistory

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite

interface IStoragePostsSearchHistoryRepository {
    fun observeRecent(site: SelectedSite, limit: Int = 10): Flow<List<String>>
    suspend fun save(site: SelectedSite, query: String, limit: Int = 10)
    suspend fun delete(site: SelectedSite, query: String)
}
