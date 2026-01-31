package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.common.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.data.api.CreatorProfileApi
import su.afk.kemonos.creatorProfile.data.cache.CreatorProfileCacheJson
import su.afk.kemonos.creatorProfile.data.dto.profileAnnouncements.ProfileAnnouncementsDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileDms.DmDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileFanCards.ProfileFanCardsDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileLinks.ProfileLinksDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.data.dto.profileTags.TagDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.util.Utils.queryKey
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.network.util.call
import su.afk.kemonos.network.util.safeCallOrNull
import su.afk.kemonos.storage.api.repository.creatorProfile.CreatorProfileCacheType
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import javax.inject.Inject

internal class CreatorsRepository @Inject constructor(
    private val api: CreatorProfileApi,
    private val cacheStore: IStoreCreatorProfileRepository,
    private val cacheJson: CreatorProfileCacheJson,
    private val postsCache: IStorageCreatorPostsRepository,
) {
    /** Профиль посты и поиск */
    suspend fun getProfilePosts(
        service: String,
        id: String,
        search: String? = null,
        tag: String? = null,
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
    suspend fun getProfileDms(service: String, id: String): List<Dm> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.DMS)
            ?.let { return cacheJson.dmsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileDms(service, id) },
            mapper = { dto -> dto.toDomain() }
        ) ?: emptyList()

        if (fromNet.isNotEmpty()) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.DMS,
                cacheJson.dmsToJson(fromNet)
            )
        }

        return fromNet
    }

    /** Tags профиля (кэш 7 дней) */
    suspend fun getProfileTags(service: String, id: String): List<Tag> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.TAGS)
            ?.let { return cacheJson.tagsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileTags(service, id) },
            mapper = { dto -> dto.toDomain() }
        ) ?: emptyList()

        if (fromNet.isNotEmpty()) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.TAGS,
                cacheJson.tagsToJson(fromNet)
            )
        }

        return fromNet
    }

    /** Announcements профиля (кэш 7 дней) */
    suspend fun getProfileAnnouncements(service: String, id: String): List<ProfileAnnouncement> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.ANNOUNCEMENTS)
            ?.let { return cacheJson.announcementsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileAnnouncements(service, id) },
            mapper = { dto -> dto.toDomain() }
        ) ?: emptyList()

        if (fromNet.isNotEmpty()) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.ANNOUNCEMENTS,
                cacheJson.announcementsToJson(fromNet)
            )
        }

        return fromNet
    }

    /** FanCards профиля (кэш 7 дней) */
    suspend fun getProfileFanCards(service: String, id: String): List<ProfileFanCard> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.FANCARDS)
            ?.let { return cacheJson.fanCardsFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileFanCards(service, id) },
            mapper = { dto -> dto.toDomain() }
        ) ?: emptyList()

        if (fromNet.isNotEmpty()) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.FANCARDS,
                cacheJson.fanCardsToJson(fromNet)
            )
        }

        return fromNet
    }

    /** Links профиля (кэш 7 дней) */
    suspend fun getProfileLinks(service: String, id: String): List<ProfileLink> {
        cacheStore.getFreshJsonOrNull(service, id, CreatorProfileCacheType.LINKS)
            ?.let { return cacheJson.linksFromJson(it) }

        val fromNet = safeCallOrNull(
            api = { api.getProfileLinks(service, id) },
            mapper = { dto -> dto.toDomain() }
        ) ?: emptyList()

        if (fromNet.isNotEmpty()) {
            cacheStore.putJson(
                service,
                id,
                CreatorProfileCacheType.LINKS,
                cacheJson.linksToJson(fromNet)
            )
        }

        return fromNet
    }
}