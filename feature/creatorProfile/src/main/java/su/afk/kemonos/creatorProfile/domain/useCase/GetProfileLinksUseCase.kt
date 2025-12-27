package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import javax.inject.Inject

internal class GetProfileLinksUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<ProfileLink> {
        return repository.getProfileLinks(service, id)
    }
}