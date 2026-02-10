package su.afk.kemonos.storage.entity.favorites.updates

import androidx.room.*
import su.afk.kemonos.domain.SelectedSite

@Dao
interface FreshFavoriteArtistUpdatesDao {

    @Query(
        """
        SELECT * FROM fresh_favorite_artist_updates
        WHERE savedAtMs >= :minSavedAtMs
        """
    )
    suspend fun getAll(minSavedAtMs: Long): List<FreshFavoriteArtistUpdateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FreshFavoriteArtistUpdateEntity>)

    @Query("DELETE FROM fresh_favorite_artist_updates WHERE site = :site")
    suspend fun clear(site: SelectedSite)

    @Query("DELETE FROM fresh_favorite_artist_updates WHERE savedAtMs < :minSavedAtMs")
    suspend fun clearExpired(minSavedAtMs: Long)

    @Transaction
    suspend fun replace(site: SelectedSite, items: List<FreshFavoriteArtistUpdateEntity>) {
        clear(site)
        if (items.isNotEmpty()) insertAll(items)
    }
}

