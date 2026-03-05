package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import javax.inject.Inject

internal class GetProfileLinksUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<ProfileLink> {
        return repository.getProfileLinks(service, id)
    }
}