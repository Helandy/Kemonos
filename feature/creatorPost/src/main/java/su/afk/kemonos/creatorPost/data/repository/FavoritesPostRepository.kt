package su.afk.kemonos.creatorPost.data.repository

import retrofit2.HttpException
import su.afk.kemonos.creatorPost.data.api.FavoritesPostApi
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import javax.inject.Inject

internal interface IFavoritesPostRepository {
    suspend fun addPost(site: SelectedSite, post: PostDomain): Result<Unit>
    suspend fun removePost(site: SelectedSite, service: String, creatorId: String, postId: String): Result<Unit>
}

internal class FavoritesPostRepository @Inject constructor(
    private val api: FavoritesPostApi,
    private val store: IStoreFavoritePostsRepository,
) : IFavoritesPostRepository {

    override suspend fun addPost(site: SelectedSite, post: PostDomain): Result<Unit> = runCatching {
        val resp = api.addFavoritePost(
            service = post.service,
            creatorId = post.userId,
            postId = post.id,
        )

        if (!resp.isSuccessful) throw HttpException(resp)

        store.add(site = site, post)
    }

    override suspend fun removePost(
        site: SelectedSite,
        service: String,
        creatorId: String,
        postId: String
    ): Result<Unit> = runCatching {
        val resp = api.removeFavoritePost(service, creatorId, postId)

        if (!resp.isSuccessful) throw HttpException(resp)

        store.remove(site = site, service, creatorId, postId)
    }
}
