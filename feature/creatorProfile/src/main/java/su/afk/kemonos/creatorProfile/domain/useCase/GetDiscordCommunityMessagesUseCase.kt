package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.domain.repository.IDiscordRepository
import javax.inject.Inject

internal class GetDiscordCommunityMessagesUseCase @Inject constructor(
    private val repository: IDiscordRepository
) {
    suspend operator fun invoke(channelId: String, offset: Int): List<CommunityMessage> {
        return repository.getCommunityMessages(channelId, offset)
    }
}
