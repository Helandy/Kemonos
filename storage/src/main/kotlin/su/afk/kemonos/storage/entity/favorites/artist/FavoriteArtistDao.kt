package su.afk.kemonos.storage.entity.favorites.artist

import androidx.room.*
import su.afk.kemonos.domain.SelectedSite

@Dao
interface FavoriteArtistsDao {

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM favorite_artists
            WHERE site = :site AND service = :service AND id = :creatorId
        )
    """
    )
    suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean

    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
        ORDER BY favedSeq DESC
    """
    )
    suspend fun getAll(site: SelectedSite): List<FavoriteArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FavoriteArtistEntity>)

    @Query(
        """
        DELETE FROM favorite_artists
        WHERE site = :site AND service = :service AND id = :id
    """
    )
    suspend fun delete(site: SelectedSite, service: String, id: String)

    @Query("DELETE FROM favorite_artists WHERE site = :site")
    suspend fun clear(site: SelectedSite)

    @Transaction
    suspend fun replaceAll(site: SelectedSite, items: List<FavoriteArtistEntity>) {
        clear(site)
        if (items.isNotEmpty()) insertAll(items)
    }
}
