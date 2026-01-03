package su.afk.kemonos.storage.repository.comments

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.preferences.useCase.CacheTimes.TLL_1_DAYS
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_7_DAYS
import su.afk.kemonos.storage.entity.comments.CommentWithRevisions.Companion.toDomain
import su.afk.kemonos.storage.entity.comments.dao.CommentsDao
import su.afk.kemonos.storage.entity.comments.entity.CommentEntity.Companion.toEntity
import su.afk.kemonos.storage.entity.comments.entity.CommentRevisionEntity.Companion.toEntity
import javax.inject.Inject

interface IStoreCommentsRepository {
    suspend fun getCommentsFreshOrNull(service: String, userId: String, postId: String): List<CommentDomain>?
    suspend fun updateComments(service: String, userId: String, postId: String, comments: List<CommentDomain>)
    suspend fun clearCacheOver7Days()
}

internal class StoreCommentsRepository @Inject constructor(
    private val dao: CommentsDao
) : IStoreCommentsRepository {

    override suspend fun getCommentsFreshOrNull(
        service: String,
        userId: String,
        postId: String
    ): List<CommentDomain>? {
        val now = System.currentTimeMillis()
        val cached = dao.getThreadFresh(service, userId, postId, now, TLL_1_DAYS)
            .map { it.toDomain() }

        return cached.takeIf { it.isNotEmpty() }
    }

    override suspend fun updateComments(
        service: String,
        userId: String,
        postId: String,
        comments: List<CommentDomain>
    ) {
        val now = System.currentTimeMillis()

        val commentEntities = comments.map { it.toEntity(service, userId, postId, cachedAt = now) }
        val revisionEntities = comments.flatMap { c ->
            c.revisions.map { rev -> rev.toEntity(service, userId, postId, commentId = c.id) }
        }

        dao.replaceThread(
            service = service,
            userId = userId,
            postId = postId,
            comments = commentEntities,
            revisions = revisionEntities
        )
    }

    override suspend fun clearCacheOver7Days() {
        dao.deleteOlderThan(
            now = System.currentTimeMillis(),
            maxAgeMs = TTL_7_DAYS
        )
    }
}