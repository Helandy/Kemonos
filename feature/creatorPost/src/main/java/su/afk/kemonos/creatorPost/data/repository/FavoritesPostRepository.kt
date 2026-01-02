package su.afk.kemonos.creatorPost.data.repository

import su.afk.kemonos.creatorPost.data.api.FavoritesPostApi
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.network.util.successOrFalse
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import javax.inject.Inject

internal interface IFavoritesPostRepository {
    suspend fun addPost(site: SelectedSite, post: PostDomain): Boolean
    suspend fun removePost(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean
}

internal class FavoritesPostRepository @Inject constructor(
    private val api: FavoritesPostApi,
    private val store: IStoreFavoritePostsUseCase,
) : IFavoritesPostRepository {

    override suspend fun addPost(site: SelectedSite, post: PostDomain): Boolean {
        val response = api.addFavoritePost(
            service = post.service,
            creatorId = post.userId,
            postId = post.id,
        ).successOrFalse()

        if (response) store.add(site = site, post)
        return response
    }

    override suspend fun removePost(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean {
        val response = api.removeFavoritePost(service, creatorId, postId).successOrFalse()
        if (response) store.remove(site = site, service, creatorId, postId)
        return response
    }
}
