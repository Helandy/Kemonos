package su.afk.kemonos.storage.api.repository.dms

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.dms.DmDomain

interface IStorageDmsRepository {
    suspend fun getFreshPageOrNull(site: SelectedSite, queryKey: String, offset: Int): List<DmDomain>?
    suspend fun getStalePageOrEmpty(site: SelectedSite, queryKey: String, offset: Int): List<DmDomain>
    suspend fun putPage(site: SelectedSite, queryKey: String, offset: Int, items: List<DmDomain>)
    suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int)
    suspend fun clearCache(site: SelectedSite)
    suspend fun clearAll(site: SelectedSite)
}
