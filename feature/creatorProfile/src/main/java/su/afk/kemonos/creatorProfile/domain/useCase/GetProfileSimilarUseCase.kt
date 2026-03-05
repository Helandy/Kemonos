package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import javax.inject.Inject

internal class GetProfileSimilarUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<SimilarCreator> {
        return repository.getProfileSimilar(service, id)
    }
}
