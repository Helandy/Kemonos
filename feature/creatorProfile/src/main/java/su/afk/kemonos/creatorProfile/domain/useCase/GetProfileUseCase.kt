package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.creatorProfile.data.IProfileRepository
import su.afk.kemonos.domain.models.Profile
import javax.inject.Inject

internal class GetProfileUseCase @Inject constructor(
    private val repository: IProfileRepository
) : IGetProfileUseCase {
    override suspend operator fun invoke(service: String, id: String): Profile? {
        return repository.getProfile(service, id)
    }
}