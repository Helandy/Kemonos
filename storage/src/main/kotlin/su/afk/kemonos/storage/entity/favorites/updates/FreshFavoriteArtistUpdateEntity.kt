package su.afk.kemonos.storage.entity.favorites.updates

import androidx.room.Entity
import androidx.room.Index
import su.afk.kemonos.domain.SelectedSite

@Entity(
    tableName = "fresh_favorite_artist_updates",
    primaryKeys = ["site", "service", "id", "name"],
    indices = [Index(value = ["savedAtMs"])]
)
data class FreshFavoriteArtistUpdateEntity(
    val site: SelectedSite,
    val name: String,
    val service: String,
    val id: String,
    val savedAtMs: Long,
)

