package su.afk.kemonos.storage.entity.localLikes

import androidx.room.*
import su.afk.kemonos.domain.SelectedSite

@Dao
interface LocalLikedPostsDao {

    @Query(
        """
    SELECT * FROM local_liked_posts
    WHERE site = :site
    ORDER BY likedAt DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun page(site: SelectedSite, limit: Int, offset: Int): List<LocalLikedPostEntity>

    @Query(
        """
    SELECT * FROM local_liked_posts
    WHERE site = :site
      AND title LIKE '%' || :query || '%'
    ORDER BY likedAt DESC
    LIMIT :limit OFFSET :offset
    """
    )
    suspend fun pageSearch(site: SelectedSite, query: String, limit: Int, offset: Int): List<LocalLikedPostEntity>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM local_liked_posts
            WHERE site = :site
              AND service = :service
              AND userId = :creatorId
              AND id = :postId
        )
    """
    )
    suspend fun exists(site: SelectedSite, service: String, creatorId: String, postId: String): Boolean

    @Query(
        """
    SELECT * FROM local_liked_posts
    WHERE site = :site
    ORDER BY likedAt DESC
    """
    )
    suspend fun getAll(site: SelectedSite): List<LocalLikedPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: LocalLikedPostEntity)

    @Query(
        """
        DELETE FROM local_liked_posts
        WHERE site = :site
          AND service = :service
          AND userId = :creatorId
          AND id = :postId
    """
    )
    suspend fun delete(site: SelectedSite, service: String, creatorId: String, postId: String)

    @Query("DELETE FROM local_liked_posts WHERE site = :site")
    suspend fun clear(site: SelectedSite)
}
