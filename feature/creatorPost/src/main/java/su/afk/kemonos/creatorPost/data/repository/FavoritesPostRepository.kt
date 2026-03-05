package su.afk.kemonos.creatorPost.data.repository

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import su.afk.kemonos.creatorPost.data.api.FavoritesPostApi
import su.afk.kemonos.creatorPost.domain.repository.IFavoritesPostRepository
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import javax.inject.Inject

internal class FavoritesPostRepository @Inject constructor(
    private val api: FavoritesPostApi,
    private val store: IStoreFavoritePostsRepository,
) : IFavoritesPostRepository {

    override suspend fun addPost(site: SelectedSite, post: PostDomain): Result<Unit> {
        return mutateFavorites {
            val resp = api.addFavoritePost(
                service = post.service,
                creatorId = post.userId,
                postId = post.id,
            )
            if (!resp.isSuccessful) throw HttpException(resp)
            store.add(site = site, post)
        }
    }

    override suspend fun removePost(
        site: SelectedSite,
        service: String,
        creatorId: String,
        postId: String
    ): Result<Unit> {
        return mutateFavorites {
            val resp = api.removeFavoritePost(service, creatorId, postId)
            if (!resp.isSuccessful) throw HttpException(resp)
            store.remove(site = site, service, creatorId, postId)
        }
    }

    private suspend fun mutateFavorites(block: suspend () -> Unit): Result<Unit> {
        return try {
            block()
            Result.success(Unit)
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            Result.failure(t)
        }
    }

}
