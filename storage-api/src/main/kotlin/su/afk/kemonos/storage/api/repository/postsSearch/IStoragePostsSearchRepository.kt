package su.afk.kemonos.storage.api.repository.postsSearch

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain

interface IStoragePostsSearchRepository {
    suspend fun getFreshPageOrNull(site: SelectedSite, queryKey: String, offset: Int): List<PostDomain>?
    suspend fun getStalePageOrEmpty(site: SelectedSite, queryKey: String, offset: Int): List<PostDomain>
    suspend fun putPage(site: SelectedSite, queryKey: String, offset: Int, items: List<PostDomain>)
    suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int)
    suspend fun clearCache(site: SelectedSite)
    suspend fun clearAll(site: SelectedSite)
}