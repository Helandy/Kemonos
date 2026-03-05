package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.domain.repository.DiscordCommunityChannels
import su.afk.kemonos.creatorProfile.domain.repository.IDiscordRepository
import javax.inject.Inject

internal class GetDiscordCommunityChannelsUseCase @Inject constructor(
    private val repository: IDiscordRepository
) {
    suspend operator fun invoke(serverId: String): DiscordCommunityChannels {
        return repository.getCommunityChannels(serverId)
    }
}
