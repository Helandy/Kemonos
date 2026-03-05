package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import javax.inject.Inject

internal class GetProfileFanCardsUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<ProfileFanCard> {
        return repository.getProfileFanCards(service, id)
    }
}