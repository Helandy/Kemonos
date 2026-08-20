package su.afk.kemonos.profile.data.repository

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.util.call
import su.afk.kemonos.profile.data.api.FavoritesApi
import su.afk.kemonos.profile.domain.repository.IImportExportRepository
import javax.inject.Inject

internal class ImportExportRepository @Inject constructor(
    private val apis: Map<SelectedSite, @JvmSuppressWildcards FavoritesApi>,
) : IImportExportRepository {

    override suspend fun getFavoriteArtistsRaw(site: SelectedSite): String =
        apis.getValue(site).getFavoriteArtistsRaw().call { it.string() }

    override suspend fun getFavoritePostsRaw(site: SelectedSite): String =
        apis.getValue(site).getFavoritePostsRaw().call { it.string() }

    override suspend fun addFavoriteArtist(site: SelectedSite, service: String, id: String): Boolean {
        return runCatching { apis.getValue(site).addFavoriteCreator(service = service, id = id).isSuccessful }
            .getOrDefault(false)
    }

    override suspend fun addFavoritePost(
        site: SelectedSite,
        service: String,
        creatorId: String,
        postId: String,
    ): Boolean {
        return runCatching {
            apis.getValue(site).addFavoritePost(service = service, creatorId = creatorId, postId = postId).isSuccessful
        }.getOrDefault(false)
    }
}
