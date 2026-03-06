package su.afk.kemonos.profile.data

import su.afk.kemonos.network.util.call
import su.afk.kemonos.profile.data.api.FavoritesApi
import su.afk.kemonos.profile.domain.favorites.IImportExportRepository
import javax.inject.Inject

internal class ImportExportRepository @Inject constructor(
    private val api: FavoritesApi,
) : IImportExportRepository {

    override suspend fun getFavoriteArtistsRaw(): String =
        api.getFavoriteArtistsRaw().call { it.string() }

    override suspend fun getFavoritePostsRaw(): String =
        api.getFavoritePostsRaw().call { it.string() }

    override suspend fun addFavoriteArtist(service: String, id: String): Boolean {
        return runCatching { api.addFavoriteCreator(service = service, id = id).isSuccessful }
            .getOrDefault(false)
    }

    override suspend fun addFavoritePost(service: String, creatorId: String, postId: String): Boolean {
        return runCatching {
            api.addFavoritePost(service = service, creatorId = creatorId, postId = postId).isSuccessful
        }.getOrDefault(false)
    }
}
