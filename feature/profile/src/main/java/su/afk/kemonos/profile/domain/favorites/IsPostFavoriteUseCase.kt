package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.domain.IIsPostFavoriteUseCase
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import javax.inject.Inject

internal class IsPostFavoriteUseCase @Inject constructor(
    private val getFavoritePostsUseCase: GetFavoritePostsUseCase,
    private val store: IStoreFavoritePostsRepository,
) : IIsPostFavoriteUseCase {

    /**
     * Проверяет наличие поста в избранном.
     * Если кэш не свежий — обновляет его по переданному [site], затем повторяет проверку.
     */
    override suspend fun invoke(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean {
        return if (store.isCacheFresh(site)) {
            store.exists(site, service, creatorId, postId)
        } else {
            getFavoritePostsUseCase(site = site, refresh = false)
            store.exists(site, service, creatorId, postId)
        }
    }
}
