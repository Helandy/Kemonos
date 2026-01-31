package su.afk.kemonos.storage.entity.favorites.artist

import androidx.room.*
import su.afk.kemonos.domain.SelectedSite

@Dao
interface FavoriteArtistsDao {

    @Query("SELECT DISTINCT service FROM favorite_artists WHERE site = :site ORDER BY service ASC")
    suspend fun getDistinctServices(site: SelectedSite): List<String>

    // --- UPDATED ---
    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
          AND (:service = 'Services' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY updated ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun pageUpdatedAsc(
        site: SelectedSite,
        service: String,
        q: String,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtistEntity>

    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
          AND (:service = 'Services' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY updated DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun pageUpdatedDesc(
        site: SelectedSite,
        service: String,
        q: String,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtistEntity>

    // --- FAVED ---
    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
          AND (:service = 'Services' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY favedSeq ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun pageFavedAsc(
        site: SelectedSite,
        service: String,
        q: String,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtistEntity>

    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
          AND (:service = 'Services' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY favedSeq DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun pageFavedDesc(
        site: SelectedSite,
        service: String,
        q: String,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtistEntity>

    // --- REIMPORT ---
    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
          AND (:service = 'Services' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY lastImported ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun pageReimportAsc(
        site: SelectedSite,
        service: String,
        q: String,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtistEntity>

    @Query(
        """
        SELECT * FROM favorite_artists
        WHERE site = :site
          AND (:service = 'Services' OR service = :service)
          AND (:q = '' OR name LIKE '%' || :q || '%')
        ORDER BY lastImported DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun pageReimportDesc(
        site: SelectedSite,
        service: String,
        q: String,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtistEntity>

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
