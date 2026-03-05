package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import javax.inject.Inject

internal class GetProfileCommunityChannelsUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<CommunityChannel> {
        return repository.getProfileCommunityChannels(service, id)
    }
}
