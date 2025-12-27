package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.IIsCreatorFavoriteUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import javax.inject.Inject

internal class IsCreatorFavoriteUseCase @Inject constructor(
    private val getFavoriteArtistsUseCase: GetFavoriteArtistsUseCase,
    private val store: IStoreFavoriteArtistsUseCase,
) : IIsCreatorFavoriteUseCase {

    override suspend fun invoke(site: SelectedSite, service: String, id: String): Boolean {
        return if (store.isCacheFresh(site = site)) {
            store.exists(site = site, service, id)
        } else {
            getFavoriteArtistsUseCase(site = site)
            store.exists(site = site, service, id)
        }
    }
}