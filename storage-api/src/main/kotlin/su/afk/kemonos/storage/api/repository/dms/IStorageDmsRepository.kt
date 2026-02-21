package su.afk.kemonos.storage.api.repository.dms

import su.afk.kemonos.domain.SelectedSite

interface IStorageDmsRepository {
    suspend fun getFreshPageOrNull(site: SelectedSite, queryKey: String, offset: Int): List<StorageDmItem>?
    suspend fun putPage(site: SelectedSite, queryKey: String, offset: Int, items: List<StorageDmItem>)
    suspend fun clearPage(site: SelectedSite, queryKey: String, offset: Int)
    suspend fun clearCache(site: SelectedSite)
    suspend fun clearAll(site: SelectedSite)
}

data class StorageDmItem(
    val hash: String,
    val service: String,
    val user: String,
    val content: String,
    val added: String,
    val published: String,
    val artistId: String,
    val artistName: String,
    val artistUpdated: String?,
)
