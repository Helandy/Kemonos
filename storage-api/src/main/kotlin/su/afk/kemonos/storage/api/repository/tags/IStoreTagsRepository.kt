package su.afk.kemonos.storage.api.repository.tags

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.tags.Tags

interface IStoreTagsRepository {
    suspend fun getAll(site: SelectedSite): List<Tags>
    suspend fun update(site: SelectedSite, items: List<Tags>)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
    suspend fun clearIfExpired(site: SelectedSite)
}
