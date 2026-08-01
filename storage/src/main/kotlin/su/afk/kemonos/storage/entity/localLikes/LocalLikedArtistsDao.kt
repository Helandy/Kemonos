package su.afk.kemonos.storage.entity.localLikes

import androidx.room.*
import su.afk.kemonos.domain.SelectedSite

@Dao
interface LocalLikedArtistsDao {

    @Query(
        """
    SELECT * FROM local_liked_artists
    WHERE site = :site
      AND (:query = '' OR name LIKE '%' || :query || '%')
    ORDER BY likedAt DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun page(site: SelectedSite, query: String, limit: Int, offset: Int): List<LocalLikedArtistEntity>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM local_liked_artists
            WHERE site = :site
              AND service = :service
              AND id = :creatorId
        )
    """
    )
    suspend fun exists(site: SelectedSite, service: String, creatorId: String): Boolean

    @Query(
        """
    SELECT * FROM local_liked_artists
    WHERE site = :site
    ORDER BY likedAt DESC
    """
    )
    suspend fun getAll(site: SelectedSite): List<LocalLikedArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: LocalLikedArtistEntity)

    @Query("DELETE FROM local_liked_artists WHERE site = :site AND service = :service AND id = :id")
    suspend fun delete(site: SelectedSite, service: String, id: String)

    @Query("DELETE FROM local_liked_artists WHERE site = :site")
    suspend fun clear(site: SelectedSite)
}
