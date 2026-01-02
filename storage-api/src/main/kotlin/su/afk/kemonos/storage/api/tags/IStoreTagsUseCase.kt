package su.afk.kemonos.storage.api.tags

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.tags.Tags

interface IStoreTagsUseCase {
    suspend fun getAll(site: SelectedSite): List<Tags>
    suspend fun update(site: SelectedSite, items: List<Tags>)
    suspend fun clear(site: SelectedSite)
    suspend fun isCacheFresh(site: SelectedSite): Boolean
}