package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.domain.repository.ICreatorsRepository
import su.afk.kemonos.domain.models.Tag
import javax.inject.Inject


internal class GetProfileTagsUseCase @Inject constructor(
    private val repository: ICreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<Tag> {
        return repository.getProfileTags(service, id)
    }
}