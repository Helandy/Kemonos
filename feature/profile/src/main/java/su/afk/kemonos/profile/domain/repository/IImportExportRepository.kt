package su.afk.kemonos.profile.domain.repository

import su.afk.kemonos.domain.SelectedSite

internal interface IImportExportRepository {
    suspend fun getFavoriteArtistsRaw(site: SelectedSite): String
    suspend fun getFavoritePostsRaw(site: SelectedSite): String
    suspend fun addFavoriteArtist(site: SelectedSite, service: String, id: String): Boolean
    suspend fun addFavoritePost(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean
}
