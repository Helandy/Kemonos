package su.afk.kemonos.common.domain.useCase

import su.afk.kemonos.common.repository.IProfileRepository
import su.afk.kemonos.domain.domain.models.Profile
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: IProfileRepository
) {
    suspend operator fun invoke(service: String, id: String): Profile? {
        return repository.getProfile(service, id)
    }
}