package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import javax.inject.Inject

internal class GetProfileSimilarUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<SimilarCreator> {
        return repository.getProfileSimilar(service, id)
    }
}
