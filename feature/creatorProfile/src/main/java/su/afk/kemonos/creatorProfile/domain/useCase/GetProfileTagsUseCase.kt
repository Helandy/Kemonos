package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import su.afk.kemonos.domain.domain.models.Tag
import javax.inject.Inject


internal class GetProfileTagsUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<Tag> {
        return repository.getProfileTags(service, id)
    }
}