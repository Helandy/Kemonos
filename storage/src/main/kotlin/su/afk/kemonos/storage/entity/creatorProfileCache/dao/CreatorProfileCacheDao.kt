package su.afk.kemonos.storage.entity.creatorProfileCache.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import su.afk.kemonos.storage.api.repository.creatorProfile.CreatorProfileCacheType

@Dao
interface CreatorProfileCacheDao {

    suspend fun get(service: String, profileId: String, type: CreatorProfileCacheType): String? = when (type) {
        CreatorProfileCacheType.DMS -> getDms(service, profileId)
        CreatorProfileCacheType.TAGS -> getTags(service, profileId)
        CreatorProfileCacheType.ANNOUNCEMENTS -> getAnnouncements(service, profileId)
        CreatorProfileCacheType.FANCARDS -> getFancards(service, profileId)
        CreatorProfileCacheType.LINKS -> getLinks(service, profileId)
        CreatorProfileCacheType.SIMILAR -> getSimilar(service, profileId)
    }

    suspend fun getFresh(
        service: String,
        profileId: String,
        type: CreatorProfileCacheType,
        minCachedAt: Long
    ): String? = when (type) {
        CreatorProfileCacheType.DMS -> getFreshDms(service, profileId, minCachedAt)
        CreatorProfileCacheType.TAGS -> getFreshTags(service, profileId, minCachedAt)
        CreatorProfileCacheType.ANNOUNCEMENTS -> getFreshAnnouncements(service, profileId, minCachedAt)
        CreatorProfileCacheType.FANCARDS -> getFreshFancards(service, profileId, minCachedAt)
        CreatorProfileCacheType.LINKS -> getFreshLinks(service, profileId, minCachedAt)
        CreatorProfileCacheType.SIMILAR -> getFreshSimilar(service, profileId, minCachedAt)
    }

    suspend fun upsert(
        service: String,
        profileId: String,
        type: CreatorProfileCacheType,
        json: String,
        cachedAt: Long
    ) = when (type) {
        CreatorProfileCacheType.DMS -> upsertDms(service, profileId, json, cachedAt)
        CreatorProfileCacheType.TAGS -> upsertTags(service, profileId, json, cachedAt)
        CreatorProfileCacheType.ANNOUNCEMENTS -> upsertAnnouncements(service, profileId, json, cachedAt)
        CreatorProfileCacheType.FANCARDS -> upsertFancards(service, profileId, json, cachedAt)
        CreatorProfileCacheType.LINKS -> upsertLinks(service, profileId, json, cachedAt)
        CreatorProfileCacheType.SIMILAR -> upsertSimilar(service, profileId, json, cachedAt)
    }

    @Transaction
    suspend fun clearProfile(service: String, profileId: String) {
        clearProfileDms(service, profileId)
        clearProfileTags(service, profileId)
        clearProfileAnnouncements(service, profileId)
        clearProfileFancards(service, profileId)
        clearProfileLinks(service, profileId)
        clearProfileSimilar(service, profileId)
    }

    @Transaction
    suspend fun clearAll() {
        clearAllDms()
        clearAllTags()
        clearAllAnnouncements()
        clearAllFancards()
        clearAllLinks()
        clearAllSimilar()
    }

    @Transaction
    suspend fun deleteOlderThan(minCachedAt: Long) {
        deleteOlderThanDms(minCachedAt)
        deleteOlderThanTags(minCachedAt)
        deleteOlderThanAnnouncements(minCachedAt)
        deleteOlderThanFancards(minCachedAt)
        deleteOlderThanLinks(minCachedAt)
        deleteOlderThanSimilar(minCachedAt)
    }

    @Query(
        """
        SELECT json FROM creator_profile_cache_dms
        WHERE service = :service AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun getDms(service: String, profileId: String): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_tags
        WHERE service = :service AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun getTags(service: String, profileId: String): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_announcements
        WHERE service = :service AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun getAnnouncements(service: String, profileId: String): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_fancards
        WHERE service = :service AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun getFancards(service: String, profileId: String): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_links
        WHERE service = :service AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun getLinks(service: String, profileId: String): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_similar
        WHERE service = :service AND profileId = :profileId
        LIMIT 1
        """
    )
    suspend fun getSimilar(service: String, profileId: String): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_dms
        WHERE service = :service AND profileId = :profileId
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshDms(service: String, profileId: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_tags
        WHERE service = :service AND profileId = :profileId
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshTags(service: String, profileId: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_announcements
        WHERE service = :service AND profileId = :profileId
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshAnnouncements(service: String, profileId: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_fancards
        WHERE service = :service AND profileId = :profileId
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshFancards(service: String, profileId: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_links
        WHERE service = :service AND profileId = :profileId
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshLinks(service: String, profileId: String, minCachedAt: Long): String?

    @Query(
        """
        SELECT json FROM creator_profile_cache_similar
        WHERE service = :service AND profileId = :profileId
          AND cachedAt >= :minCachedAt
        LIMIT 1
        """
    )
    suspend fun getFreshSimilar(service: String, profileId: String, minCachedAt: Long): String?

    @Query(
        """
        INSERT OR REPLACE INTO creator_profile_cache_dms(service, profileId, json, cachedAt)
        VALUES (:service, :profileId, :json, :cachedAt)
        """
    )
    suspend fun upsertDms(service: String, profileId: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO creator_profile_cache_tags(service, profileId, json, cachedAt)
        VALUES (:service, :profileId, :json, :cachedAt)
        """
    )
    suspend fun upsertTags(service: String, profileId: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO creator_profile_cache_announcements(service, profileId, json, cachedAt)
        VALUES (:service, :profileId, :json, :cachedAt)
        """
    )
    suspend fun upsertAnnouncements(service: String, profileId: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO creator_profile_cache_fancards(service, profileId, json, cachedAt)
        VALUES (:service, :profileId, :json, :cachedAt)
        """
    )
    suspend fun upsertFancards(service: String, profileId: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO creator_profile_cache_links(service, profileId, json, cachedAt)
        VALUES (:service, :profileId, :json, :cachedAt)
        """
    )
    suspend fun upsertLinks(service: String, profileId: String, json: String, cachedAt: Long)

    @Query(
        """
        INSERT OR REPLACE INTO creator_profile_cache_similar(service, profileId, json, cachedAt)
        VALUES (:service, :profileId, :json, :cachedAt)
        """
    )
    suspend fun upsertSimilar(service: String, profileId: String, json: String, cachedAt: Long)

    @Query("DELETE FROM creator_profile_cache_dms WHERE service = :service AND profileId = :profileId")
    suspend fun clearProfileDms(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache_tags WHERE service = :service AND profileId = :profileId")
    suspend fun clearProfileTags(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache_announcements WHERE service = :service AND profileId = :profileId")
    suspend fun clearProfileAnnouncements(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache_fancards WHERE service = :service AND profileId = :profileId")
    suspend fun clearProfileFancards(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache_links WHERE service = :service AND profileId = :profileId")
    suspend fun clearProfileLinks(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache_similar WHERE service = :service AND profileId = :profileId")
    suspend fun clearProfileSimilar(service: String, profileId: String)

    @Query("DELETE FROM creator_profile_cache_dms")
    suspend fun clearAllDms()

    @Query("DELETE FROM creator_profile_cache_tags")
    suspend fun clearAllTags()

    @Query("DELETE FROM creator_profile_cache_announcements")
    suspend fun clearAllAnnouncements()

    @Query("DELETE FROM creator_profile_cache_fancards")
    suspend fun clearAllFancards()

    @Query("DELETE FROM creator_profile_cache_links")
    suspend fun clearAllLinks()

    @Query("DELETE FROM creator_profile_cache_similar")
    suspend fun clearAllSimilar()

    @Query("DELETE FROM creator_profile_cache_dms WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanDms(minCachedAt: Long)

    @Query("DELETE FROM creator_profile_cache_tags WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanTags(minCachedAt: Long)

    @Query("DELETE FROM creator_profile_cache_announcements WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanAnnouncements(minCachedAt: Long)

    @Query("DELETE FROM creator_profile_cache_fancards WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanFancards(minCachedAt: Long)

    @Query("DELETE FROM creator_profile_cache_links WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanLinks(minCachedAt: Long)

    @Query("DELETE FROM creator_profile_cache_similar WHERE cachedAt < :minCachedAt")
    suspend fun deleteOlderThanSimilar(minCachedAt: Long)
}
