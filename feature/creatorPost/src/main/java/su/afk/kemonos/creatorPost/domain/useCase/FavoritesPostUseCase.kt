package su.afk.kemonos.creatorPost.domain.useCase

import su.afk.kemonos.creatorPost.data.repository.IFavoritesPostRepository
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.profile.api.domain.IIsPostFavoriteUseCase
import javax.inject.Inject

internal class FavoritesPostUseCase @Inject constructor(
    private val repository: IFavoritesPostRepository,
    private val isPostFavoriteUseCase: IIsPostFavoriteUseCase
) {
    suspend fun addPost(site: SelectedSite, post: PostDomain) = repository.addPost(site, post)

    suspend fun removePost(site: SelectedSite, service: String, creatorId: String, postId: String) =
        repository.removePost(
            site = site,
            service = service,
            creatorId = creatorId,
            postId = postId
        )

    suspend fun isPostFavorite(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean {
        return isPostFavoriteUseCase(
            site = site,
            service = service,
            creatorId = creatorId,
            postId = postId,
        )
    }
}