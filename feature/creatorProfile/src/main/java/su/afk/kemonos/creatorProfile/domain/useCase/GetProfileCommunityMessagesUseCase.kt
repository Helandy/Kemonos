package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import javax.inject.Inject

internal class GetProfileCommunityMessagesUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, channelId: String, offset: Int): List<CommunityMessage> {
        return repository.getProfileCommunityMessages(service, channelId, offset)
    }

    fun nextOffset(currentOffset: Int): Int = repository.nextCommunityOffset(currentOffset)
}
