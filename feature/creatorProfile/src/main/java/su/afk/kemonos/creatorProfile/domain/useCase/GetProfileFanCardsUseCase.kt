package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import javax.inject.Inject

internal class GetProfileFanCardsUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<ProfileFanCard> {
        return repository.getProfileFanCards(service, id)
    }
}