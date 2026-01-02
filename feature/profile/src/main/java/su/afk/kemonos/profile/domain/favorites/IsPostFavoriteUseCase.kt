package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.api.domain.IIsPostFavoriteUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import javax.inject.Inject

internal class IsPostFavoriteUseCase @Inject constructor(
    private val getFavoritePostsUseCase: GetFavoritePostsUseCase,
    private val store: IStoreFavoritePostsUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : IIsPostFavoriteUseCase {

    override suspend fun invoke(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean {
        return if (store.isCacheFresh(site)) {
            store.exists(site, service, creatorId, postId)
        } else {
            getFavoritePostsUseCase(site = selectedSiteUseCase.getSite())
            store.exists(site, service, creatorId, postId)
        }
    }
}