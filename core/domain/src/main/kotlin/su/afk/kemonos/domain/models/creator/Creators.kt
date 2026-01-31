package su.afk.kemonos.domain.models.creator

data class Creators(
    val favorited: Int,

    val id: String,

    val indexed: Int,

    val name: String,

    val service: String,

    val updated: Int,
) {
    companion object {
        fun Creators.toFavoriteArtistUi(): FavoriteArtist = FavoriteArtist(
            favedSeq = favorited,
            id = id,
            indexed = indexed.toString(),
            lastImported = "",
            name = name,
            publicId = "",
            relationId = 0,
            service = service,
            updated = updated.toString(),
        )
    }
}