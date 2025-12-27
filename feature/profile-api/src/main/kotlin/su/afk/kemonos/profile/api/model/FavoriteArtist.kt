package su.afk.kemonos.profile.api.model

data class FavoriteArtist(
    val favedSeq: Int,
    val id: String,
    val indexed: String,
    val lastImported: String,
    val name: String,
    val publicId: String,
    val relationId: Int,
    val service: String,
    val updated: String
)