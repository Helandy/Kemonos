package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.creatorProfile.data.api.CreatorProfileApi
import su.afk.kemonos.creatorProfile.data.cache.CreatorProfileCacheJson
import su.afk.kemonos.creatorProfile.data.dto.profileAnnouncements.ProfileAnnouncementsDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileCommunity.CommunityChannelDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileCommunity.CommunityMessageDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileDms.DmDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileFanCards.ProfileFanCardsDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileLinks.ProfileLinksDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileSimilar.SimilarCreatorDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileTags.TagDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import su.afk.kemonos.creatorProfile.util.Utils.queryKey
import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.network.util.call
import su.afk.kemonos.network.util.safeCallOrNull
import su.afk.kemonos.storage.api.repository.community.CommunityCacheType
import su.afk.kemonos.storage.api.repository.community.IStoreCommunityRepository
import su.afk.kemonos.storage.api.repository.creatorProfile.CreatorProfileCacheType
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import javax.inject.Inject

internal class CreatorsRepository @Inject constructor(
    private val api: CreatorProfileApi,
    private val cacheStore: IStoreCreatorProfileRepository,
    private val communityCacheStore: IStoreCommunityRepository,
    private val cacheJson: CreatorProfileCacheJson,
    private val postsCache: IStorageCreatorPostsRepository,
) : ICreatorsRepository {
    companion object {
        private const val COMMUNITY_PAGE_SIZE = 150
        private const val OFFSET_CACHE_DELIMITER = "#o="
    }

    /** Профиль посты и поиск */
    override suspend fun getProfilePosts(
        service: String,
        id: String,
        search: String?,
        tag: String?,
        offset: Int,
    ): List<PostDomain> {
        val qk = queryKey(service, id, search, tag)

        postsCache.getFreshPageOrNull(qk, offset)?.let { return it }

        val apiOffset = if (offset == 0) null else offset

        val net = api.getProfilePosts(
            service = service,
            id = id,
            search = search,
            tag = tag,
            offset = apiOffset,
        ).call { list ->
            list.map { it.toDomain() }
        }

        postsCache.putPage(qk, offset, net)
        return net
    }

    /** ЛС профиля (кэш 7 дней) */
    override suspend fun getProfileDms(service: String, id: String): List<Dm> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.DMS)
            ?.let { return cacheJson.dmsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileDms(service, id) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.DMS,
                cacheJson.dmsToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    /** Tags профиля (кэш 7 дней) */
    override suspend fun getProfileTags(service: String, id: String): List<Tag> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.TAGS)
            ?.let { return cacheJson.tagsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileTags(service, id) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.TAGS,
                cacheJson.tagsToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    /** Announcements профиля (кэш 7 дней) */
    override suspend fun getProfileAnnouncements(service: String, id: String): List<ProfileAnnouncement> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.ANNOUNCEMENTS)
            ?.let { return cacheJson.announcementsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileAnnouncements(service, id) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.ANNOUNCEMENTS,
                cacheJson.announcementsToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    /** FanCards профиля (кэш 7 дней) */
    override suspend fun getProfileFanCards(service: String, id: String): List<ProfileFanCard> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.FANCARDS)
            ?.let { return cacheJson.fanCardsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileFanCards(service, id) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.FANCARDS,
                cacheJson.fanCardsToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    /** Links профиля (кэш 7 дней) */
    override suspend fun getProfileLinks(service: String, id: String): List<ProfileLink> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.LINKS)
            ?.let { return cacheJson.linksFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileLinks(service, id) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.LINKS,
                cacheJson.linksToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    /** Similar creators */
    override suspend fun getProfileSimilar(service: String, id: String): List<SimilarCreator> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.SIMILAR)
            ?.let { return cacheJson.similarFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileRecommended(service, id) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            cacheStore.putJson(
                service = service,
                id = id,
                type = CreatorProfileCacheType.SIMILAR,
                json = cacheJson.similarToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    override suspend fun getProfileCommunityChannels(service: String, id: String): List<CommunityChannel> {
        communityCacheStore.getFreshJsonOrNull(service, id, CommunityCacheType.CHANNELS)
            ?.let { return cacheJson.communityChannelsFromJson(it) }

        val fromNet = runCatching {
            val response = api.getProfileCommunityChannels(service, id)
            when {
                response.code() == 404 -> emptyList()
                else -> response.call { list -> list.map { it.toDomain() } }
            }
        }.getOrNull()

        if (fromNet != null) {
            communityCacheStore.putJson(
                service = service,
                id = id,
                type = CommunityCacheType.CHANNELS,
                json = cacheJson.communityChannelsToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    override suspend fun getProfileCommunityMessages(
        service: String,
        channelId: String,
        offset: Int
    ): List<CommunityMessage> {
        val pageCacheId = channelOffsetCacheId(channelId = channelId, offset = offset)
        communityCacheStore.getFreshJsonOrNull(service, pageCacheId, CommunityCacheType.MESSAGES_PAGE0)
            ?.let { return cacheJson.communityMessagesFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileCommunityMessages(service, channelId, if (offset == 0) null else offset) },
            mapper = { dto -> dto.toDomain() }
        )

        if (fromNet != null) {
            communityCacheStore.putJson(
                service = service,
                id = pageCacheId,
                type = CommunityCacheType.MESSAGES_PAGE0,
                json = cacheJson.communityMessagesToJson(fromNet),
            )
        }

        return fromNet ?: emptyList()
    }

    override fun nextCommunityOffset(currentOffset: Int): Int = currentOffset + COMMUNITY_PAGE_SIZE

    private fun channelOffsetCacheId(channelId: String, offset: Int): String {
        if (offset <= 0) return channelId
        return "$channelId$OFFSET_CACHE_DELIMITER$offset"
    }
}
