package su.afk.kemonos.profile.domain.blacklist

import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import javax.inject.Inject

internal class RemoveBlacklistedAuthorUseCase @Inject constructor(
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
) {
    suspend operator fun invoke(service: String, creatorId: String) =
        blacklistedAuthorsRepository.remove(service = service, creatorId = creatorId)
}
