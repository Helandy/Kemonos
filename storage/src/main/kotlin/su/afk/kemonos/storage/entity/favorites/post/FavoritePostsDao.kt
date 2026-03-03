package su.afk.kemonos.storage.entity.favorites.post

import androidx.room.*
import su.afk.kemonos.domain.SelectedSite

@Dao
interface FavoritePostsDao {

    @Query(
        """
    SELECT * FROM favorite_posts
    WHERE site = :site
    ORDER BY 
        CASE WHEN favedSeq IS NULL THEN 0 ELSE 1 END,
        favedSeq DESC,
        id DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun page(site: SelectedSite, limit: Int, offset: Int): List<FavoritePostEntity>

    @Query(
        """
    SELECT * FROM favorite_posts
    WHERE site = :site
    ORDER BY
        -- grouped-режим группирует уже на UI, поэтому порядок должен совпадать с обычным feed
        CASE WHEN favedSeq IS NULL THEN 0 ELSE 1 END,
        favedSeq DESC,
        id DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun pageGrouped(site: SelectedSite, limit: Int, offset: Int): List<FavoritePostEntity>

    @Query(
        """
    SELECT * FROM favorite_posts
    WHERE site = :site
      AND title LIKE '%' || :query || '%'
    ORDER BY 
        CASE WHEN favedSeq IS NULL THEN 0 ELSE 1 END,
        favedSeq DESC,
        id DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun pageSearch(site: SelectedSite, query: String, limit: Int, offset: Int): List<FavoritePostEntity>

    @Query(
        """
    SELECT * FROM favorite_posts
    WHERE site = :site
      AND title LIKE '%' || :query || '%'
    ORDER BY
        -- grouped-режим с поиском также должен сохранять обычную сортировку по дате
        CASE WHEN favedSeq IS NULL THEN 0 ELSE 1 END,
        favedSeq DESC,
        id DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun pageSearchGrouped(site: SelectedSite, query: String, limit: Int, offset: Int): List<FavoritePostEntity>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM favorite_posts
            WHERE site = :site
              AND service = :service
              AND userId = :creatorId
              AND id = :postId
        )
    """
    )
    suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean

    @Query("SELECT * FROM favorite_posts WHERE site = :site")
    suspend fun getAll(site: SelectedSite): List<FavoritePostEntity>

    @Query(
        """
        SELECT DISTINCT service || ':' || userId
        FROM favorite_posts
        WHERE site = :site
        """
    )
    suspend fun getDistinctAuthorCompositeKeys(site: SelectedSite): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FavoritePostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: FavoritePostEntity)

    @Query("DELETE FROM favorite_posts WHERE site = :site")
    suspend fun clear(site: SelectedSite)

    @Query(
        """
        DELETE FROM favorite_posts
        WHERE site = :site
          AND service = :service
          AND userId = :creatorId
          AND id = :postId
    """
    )
    suspend fun delete(site: SelectedSite, service: String, creatorId: String, postId: String)

    @Transaction
    suspend fun replaceAll(site: SelectedSite, items: List<FavoritePostEntity>) {
        clear(site)
        if (items.isNotEmpty()) insertAll(items)
    }
}
