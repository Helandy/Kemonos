package su.afk.kemonos.profile.domain.favorites

internal data class ArtistImportItem(
    val service: String,
    val id: String,
)

internal data class PostImportItem(
    val service: String,
    val creatorId: String,
    val postId: String,
)

internal interface IFavoritesJsonParser {
    /** Number of rows in an exported array; 0 when [rawJson] is not a valid array. */
    suspend fun countRows(rawJson: String): Int

    /** One element per row, `null` for a row that is not a valid artist. Throws if the file is not a JSON array. */
    suspend fun parseArtists(rawJson: String): List<ArtistImportItem?>

    /** One element per row, `null` for a row that is not a valid post. Throws if the file is not a JSON array. */
    suspend fun parsePosts(rawJson: String): List<PostImportItem?>
}
