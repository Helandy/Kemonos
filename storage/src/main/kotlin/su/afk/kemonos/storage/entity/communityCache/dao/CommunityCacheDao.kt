package su.afk.kemonos.storage.entity.communityCache.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType

@Dao
interface CommunityCacheDao {

    suspend fun getFresh(
        service: String,
        id: String,
        type: CommunityCacheType,
        minCachedAt: Long
    ): String? = when (type) {
        CommunityCacheType.CHANNELS -> getFreshChannels(service, id, minCachedAt)
        CommunityCacheType.MESSAGES_PAGE0 -> getFreshMessagesPage0(service, id, minCachedAt)
    }

    suspend fun upsert(
        service: String,
        id: String,
        type: CommunityCacheType,
        json: String,
        cachedAt: Long
    ) = when (type) {
        CommunityCacheType.CHANNELS -> upsertChannels(service, id, json, cachedAt)
        CommunityCacheType.MESSAGES_PAGE0 -> upsertMessagesPage0(service, id, json, cachedAt)
    }

    @Transaction
    suspend fun deleteOlderThan(minCachedAt: Long) {
        deleteOlderThanChannels(minCachedAt)
        deleteOlderThanMessagesPage0(minCachedAt)
    }

    @Transaction
    suspend fun clearAll() {
        clearAllChannels()
        clearAllMessagesPage0()
    }

    @Query(
        """
        SELECT json FROM community_cache_channels
        WHERE service = :service AND id = :id
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshChannels(service: String, id: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM community_cache_messages_page0
        WHERE service = :service AND id = :id
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshMessagesPage0(service: String, id: String, minCachedAt: Long): String?

    @Query(
        """
        INSERT OR REPLACE INTO community_cache_channels(service, id, json, cachedAt)
        VALUES (:service, :id, :json, :cachedAt)
        """
    )
    suspend fun upsertChannels(service: String, id: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO community_cache_messages_page0(service, id, json, cachedAt)
        VALUES (:service, :id, :json, :cachedAt)
        """
    )
    suspend fun upsertMessagesPage0(service: String, id: String, json: String, cachedAt: Long)

    @Query("DELETE FROM community_cache_channels WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanChannels(minCachedAt: Long)

    @Query("DELETE FROM community_cache_messages_page0 WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanMessagesPage0(minCachedAt: Long)

    @Query("DELETE FROM community_cache_channels")
    suspend fun clearAllChannels()

    @Query("DELETE FROM community_cache_messages_page0")
    suspend fun clearAllMessagesPage0()
}
