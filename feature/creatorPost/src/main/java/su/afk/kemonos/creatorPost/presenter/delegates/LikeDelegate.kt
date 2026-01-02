package su.afk.kemonos.creatorPost.presenter.delegates

import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.useCase.FavoritesPostUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

internal class LikeDelegate @Inject constructor(
    private val favoritesPostUseCase: FavoritesPostUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
) {

    /** Проверка можно ли лайкнуть */
    suspend fun postIsAvailableLike(): Boolean {
        return when (selectedSiteUseCase.getSite()) {
            SelectedSite.C -> isAuthCoomerUseCase().first()
            SelectedSite.K -> isAuthKemonoUseCase().first()
        }
    }

    /** добавить в избранное */
    /** удалить из избранное */
    suspend fun onFavoriteClick(
        isFavorite: Boolean,
        post: PostContentDomain?,
        service: String,
        creatorId: String,
        postId: String
    ): Boolean {
        // todo     /** сделать получение ошибок в тост бар */
        return if (isFavorite) {
            favoritesPostUseCase.removePost(
                site = selectedSiteUseCase.getSite(),
                service = service,
                creatorId = creatorId,
                postId = postId
            )
        } else {
            val domain = post?.post ?: return false
            favoritesPostUseCase.addPost(
                site = selectedSiteUseCase.getSite(),
                post = domain
            )
        }
    }

    /** Проверит в избранном ли пост */
    suspend fun isPostFavorite(service: String, creatorId: String, postId: String): Boolean {
        val favorite = favoritesPostUseCase.isPostFavorite(
            site = selectedSiteUseCase.getSite(),
            service = service,
            creatorId = creatorId,
            postId = postId,
        )

        return favorite
    }
}