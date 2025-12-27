package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.common.data.common.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.core.api.domain.net.helpers.call
import su.afk.kemonos.core.api.domain.net.helpers.callOrNull
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
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.domain.domain.models.Tag
import su.afk.kemonos.storage.api.creatorProfileCache.CreatorProfileCacheType
import su.afk.kemonos.storage.api.creatorProfileCache.IStoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.api.profilePosts.ICreatorPostsCacheUseCase
import javax.inject.Inject

internal class CreatorsRepository @Inject constructor(
    private val api: CreatorProfileApi,
    private val cacheStore: IStoreCreatorProfileCacheUseCase,
    private val cacheJson: CreatorProfileCacheJson,
    private val postsCache: ICreatorPostsCacheUseCase,
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

        val fromNet = api.getProfileDms(service, id).callOrNull { dto ->
            dto.toDomain()
        } ?: emptyList()

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

        val fromNet = api.getProfileTags(service, id).callOrNull { dto ->
            dto.toDomain()
        } ?: emptyList()

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

        val fromNet = api.getProfileAnnouncements(service, id).callOrNull { dto ->
            dto.toDomain()
        } ?: emptyList()

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

        val fromNet = api.getProfileFanCards(service, id).callOrNull { dto ->
            dto.toDomain()
        } ?: emptyList()

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

        val fromNet = api.getProfileLinks(service, id).callOrNull { dto ->
            dto.toDomain()
        } ?: emptyList()

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