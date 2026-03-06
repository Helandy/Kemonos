package su.afk.kemonos.profile.domain.favorites

internal interface IImportExportRepository {
    suspend fun getFavoriteArtistsRaw(): String
    suspend fun getFavoritePostsRaw(): String
    suspend fun addFavoriteArtist(service: String, id: String): Boolean
    suspend fun addFavoritePost(service: String, creatorId: String, postId: String): Boolean
}
