package su.afk.kemonos.storage.entity.comments.dao

import androidx.room.*
import su.afk.kemonos.storage.entity.comments.CommentWithRevisions
import su.afk.kemonos.storage.entity.comments.entity.CommentEntity
import su.afk.kemonos.storage.entity.comments.entity.CommentRevisionEntity

@Dao
interface CommentsDao {

    // ----- READ -----

    @Transaction
    @Query(
        """
        SELECT * FROM comments
        WHERE service = :service AND userId = :userId AND postId = :postId
        ORDER BY published DESC
    """
    )
    suspend fun getThread(
        service: String,
        userId: String,
        postId: String
    ): List<CommentWithRevisions>

    @Transaction
    @Query(
        """
        SELECT * FROM comments
        WHERE service = :service AND userId = :userId AND postId = :postId
          AND (:now - cachedAt) < :ttlMs
        ORDER BY published DESC
    """
    )
    suspend fun getThreadFresh(
        service: String,
        userId: String,
        postId: String,
        now: Long,
        ttlMs: Long
    ): List<CommentWithRevisions>

    // ----- WRITE -----

    @Query(
        """
        DELETE FROM comments
        WHERE service = :service AND userId = :userId AND postId = :postId
    """
    )
    suspend fun deleteThread(
        service: String,
        userId: String,
        postId: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(items: List<CommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisions(items: List<CommentRevisionEntity>)

    @Transaction
    suspend fun replaceThread(
        service: String,
        userId: String,
        postId: String,
        comments: List<CommentEntity>,
        revisions: List<CommentRevisionEntity>
    ) {
        /** удаляем тред целиком -> каскадно удалятся ревизии */
        deleteThread(service, userId, postId)

        if (comments.isNotEmpty()) insertComments(comments)
        if (revisions.isNotEmpty()) insertRevisions(revisions)
    }

    // ----- CLEANUP -----

    @Query(
        """
        DELETE FROM comments
        WHERE (:now - cachedAt) > :maxAgeMs
    """
    )
    suspend fun deleteOlderThan(now: Long, maxAgeMs: Long)
}