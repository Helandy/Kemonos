package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileDms.Dm
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import javax.inject.Inject

internal class GetProfileDmsUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<Dm> {
        return repository.getProfileDms(service, id)
    }
}