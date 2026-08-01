package su.afk.kemonos.creatorPost.presenter.delegates

import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.auth.IsAuthPawchiveUseCase
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.useCase.FavoritesPostUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.localLikes.IStoreLocalLikedPostsRepository
import javax.inject.Inject

internal class LikeDelegate @Inject constructor(
    private val favoritesPostUseCase: FavoritesPostUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
    private val isAuthPawchiveUseCase: IsAuthPawchiveUseCase,
    private val localLikedPostsRepository: IStoreLocalLikedPostsRepository,
) {

    /** Проверка авторизован ли пользователь на выбранном сайте (лайк без авторизации хранится только локально) */
    suspend fun postIsAvailableLike(): Boolean {
        return when (selectedSiteUseCase.getSite()) {
            SelectedSite.C -> isAuthCoomerUseCase().first()
            SelectedSite.K -> isAuthKemonoUseCase().first()
            SelectedSite.P -> isAuthPawchiveUseCase().first()
        }
    }

    /** добавить/удалить лайк: авторизован — через API, иначе — только локально */
    suspend fun onFavoriteClick(
        isFavorite: Boolean,
        post: PostContentDomain?,
        service: String,
        creatorId: String,
        postId: String
    ): Result<Unit> {
        val site = selectedSiteUseCase.getSite()

        if (!postIsAvailableLike()) {
            return runCatching {
                if (isFavorite) {
                    localLikedPostsRepository.remove(
                        site = site,
                        service = service,
                        creatorId = creatorId,
                        postId = postId,
                    )
                } else {
                    val domain = post?.post ?: throw IllegalStateException("Post is null")
                    localLikedPostsRepository.add(site = site, item = domain)
                }
            }
        }

        return if (isFavorite) {
            favoritesPostUseCase.removePost(
                site = site,
                service = service,
                creatorId = creatorId,
                postId = postId
            )
        } else {
            val domain = post?.post ?: return Result.failure(IllegalStateException("Post is null"))
            favoritesPostUseCase.addPost(
                site = site,
                post = domain
            )
        }
    }

    /** Проверит в избранном/локальных лайках ли пост */
    suspend fun isPostFavorite(service: String, creatorId: String, postId: String): Boolean {
        val site = selectedSiteUseCase.getSite()

        if (!postIsAvailableLike()) {
            return runCatching {
                localLikedPostsRepository.exists(
                    site = site,
                    service = service,
                    creatorId = creatorId,
                    postId = postId,
                )
            }.getOrElse { false }
        }

        return runCatching {
            favoritesPostUseCase.isPostFavorite(
                site = site,
                service = service,
                creatorId = creatorId,
                postId = postId,
            )
        }.getOrElse { false }
    }
}
