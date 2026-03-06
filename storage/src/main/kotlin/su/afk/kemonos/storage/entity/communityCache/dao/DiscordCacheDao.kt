package su.afk.kemonos.storage.entity.communityCache.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType

@Dao
interface DiscordCacheDao {

    suspend fun getFresh(
        id: String,
        type: CommunityCacheType,
        minCachedAt: Long
    ): String? = when (type) {
        CommunityCacheType.CHANNELS -> getFreshChannels(id, minCachedAt)
        CommunityCacheType.MESSAGES_PAGE0 -> getFreshMessagesPage0(id, minCachedAt)
    }

    suspend fun upsert(
        id: String,
        type: CommunityCacheType,
        json: String,
        cachedAt: Long
    ) = when (type) {
        CommunityCacheType.CHANNELS -> upsertChannels(id, json, cachedAt)
        CommunityCacheType.MESSAGES_PAGE0 -> upsertMessagesPage0(id, json, cachedAt)
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
        SELECT json FROM discord_cache_channels
        WHERE id = :id
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshChannels(id: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM discord_cache_messages_page0
        WHERE id = :id
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshMessagesPage0(id: String, minCachedAt: Long): String?

    @Query(
        """
        INSERT OR REPLACE INTO discord_cache_channels(id, json, cachedAt)
        VALUES (:id, :json, :cachedAt)
        """
    )
    suspend fun upsertChannels(id: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO discord_cache_messages_page0(id, json, cachedAt)
        VALUES (:id, :json, :cachedAt)
        """
    )
    suspend fun upsertMessagesPage0(id: String, json: String, cachedAt: Long)

    @Query("DELETE FROM discord_cache_channels WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanChannels(minCachedAt: Long)

    @Query("DELETE FROM discord_cache_messages_page0 WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanMessagesPage0(minCachedAt: Long)

    @Query("DELETE FROM discord_cache_channels")
    suspend fun clearAllChannels()

    @Query("DELETE FROM discord_cache_messages_page0")
    suspend fun clearAllMessagesPage0()
}
