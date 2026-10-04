package su.afk.kemonos.profile.data.parser

import com.google.gson.JsonElement
import kotlinx.coroutines.withContext
import su.afk.kemonos.profile.domain.favorites.ArtistImportItem
import su.afk.kemonos.profile.domain.favorites.IFavoritesJsonParser
import su.afk.kemonos.profile.domain.favorites.PostImportItem
import su.afk.kemonos.utils.DispatcherProvider
import javax.inject.Inject

internal class FavoritesJsonParser @Inject constructor(
    private val dispatchers: DispatcherProvider,
) : IFavoritesJsonParser {

    override suspend fun countRows(rawJson: String): Int = withContext(dispatchers.default) {
        parseJsonArrayOrNull(rawJson)?.size() ?: 0
    }

    override suspend fun parseArtists(rawJson: String): List<ArtistImportItem?> =
        withContext(dispatchers.default) { requireArray(rawJson).map(::parseArtist) }

    override suspend fun parsePosts(rawJson: String): List<PostImportItem?> =
        withContext(dispatchers.default) { requireArray(rawJson).map(::parsePost) }

    private fun requireArray(rawJson: String) =
        parseJsonArrayOrNull(rawJson) ?: error("Invalid import file format")

    private fun parseArtist(element: JsonElement): ArtistImportItem? {
        if (!element.isJsonObject) return null
        val obj = element.asJsonObject
        return ArtistImportItem(
            service = obj.stringField("service") ?: return null,
            id = obj.stringField("id") ?: return null,
        )
    }

    private fun parsePost(element: JsonElement): PostImportItem? {
        if (!element.isJsonObject) return null
        val obj = element.asJsonObject
        return PostImportItem(
            service = obj.stringField("service") ?: return null,
            creatorId = obj.stringField("user") ?: return null,
            postId = obj.stringField("id") ?: return null,
        )
    }
}
