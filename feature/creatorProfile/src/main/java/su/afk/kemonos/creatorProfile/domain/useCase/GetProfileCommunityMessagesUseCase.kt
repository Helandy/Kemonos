package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import javax.inject.Inject

internal class GetProfileCommunityMessagesUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, channelId: String, offset: Int): List<CommunityMessage> {
        return repository.getProfileCommunityMessages(service, channelId, offset)
    }

    fun nextOffset(currentOffset: Int): Int = repository.nextCommunityOffset(currentOffset)
}
