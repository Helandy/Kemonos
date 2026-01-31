package su.afk.kemonos.domain.models.creator

/** Используется в избранном */
data class FavoriteArtist(
    val favedSeq: Int,
    val id: String,
    val indexed: String,
    val lastImported: String,
    val name: String,
    val publicId: String,
    val relationId: Int,
    val service: String,
    val updated: String,
    val favorited: Int? = null,
)