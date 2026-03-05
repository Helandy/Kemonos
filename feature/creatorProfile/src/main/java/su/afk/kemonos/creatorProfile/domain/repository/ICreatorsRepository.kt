package su.afk.kemonos.creatorProfile.domain.repository

import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.Tag

internal interface ICreatorsRepository {
    suspend fun getProfilePosts(
        service: String,
        id: String,
        search: String?,
        tag: String?,
        offset: Int,
    ): List<PostDomain>

    suspend fun getProfileDms(service: String, id: String): List<Dm>
    suspend fun getProfileTags(service: String, id: String): List<Tag>
    suspend fun getProfileAnnouncements(service: String, id: String): List<ProfileAnnouncement>
    suspend fun getProfileFanCards(service: String, id: String): List<ProfileFanCard>
    suspend fun getProfileLinks(service: String, id: String): List<ProfileLink>
    suspend fun getProfileSimilar(service: String, id: String): List<SimilarCreator>

    suspend fun getProfileCommunityChannels(service: String, id: String): List<CommunityChannel>
    suspend fun getProfileCommunityMessages(service: String, channelId: String, offset: Int): List<CommunityMessage>
    fun nextCommunityOffset(currentOffset: Int): Int
}
