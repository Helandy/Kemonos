package su.afk.kemonos.storage.useCases.tags

import su.afk.kemonos.api.domain.tags.Tags
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.tags.IStoreTagsUseCase
import su.afk.kemonos.storage.repository.tags.IStoreTagsRepository
import javax.inject.Inject

internal class StoreTagsUseCase @Inject constructor(
    private val repo: IStoreTagsRepository,
) : IStoreTagsUseCase {
    override suspend fun getAll(site: SelectedSite): List<Tags> = repo.getAll(site = site)
    override suspend fun update(site: SelectedSite, items: List<Tags>) = repo.update(site = site, items)
    override suspend fun clear(site: SelectedSite) = repo.clear(site = site)
    override suspend fun isCacheFresh(site: SelectedSite): Boolean = repo.isCacheFresh(site = site)
}