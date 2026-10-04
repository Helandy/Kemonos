package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import javax.inject.Inject

/**
 * Отображаемые имена авторов избранных постов (composite key -> name) для grouped-режима.
 * Использует легковесные composite-keys из БД без чтения полных PostDomain.
 */
internal class GetFavoriteAuthorNamesUseCase @Inject constructor(
    private val storeFavoritePostsRepository: IStoreFavoritePostsRepository,
    private val storeCreatorsRepository: IStoreCreatorsRepository,
) {
    suspend operator fun invoke(site: SelectedSite): Map<String, String> {
        val compositeKeys = runCatching {
            storeFavoritePostsRepository.getAllAuthorCompositeKeys(site)
        }.getOrDefault(emptySet())

        if (compositeKeys.isEmpty()) return emptyMap()

        return runCatching {
            storeCreatorsRepository.getNamesByCompositeKeys(site = site, compositeKeys = compositeKeys)
        }.getOrDefault(emptyMap())
    }
}
