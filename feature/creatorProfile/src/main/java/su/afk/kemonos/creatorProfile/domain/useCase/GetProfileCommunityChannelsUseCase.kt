package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import javax.inject.Inject

internal class GetProfileCommunityChannelsUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<CommunityChannel> {
        return repository.getProfileCommunityChannels(service, id)
    }
}
