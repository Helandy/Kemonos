package su.afk.kemonos.creatorProfile.domain.useCase

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import javax.inject.Inject

internal class ObserveAuthorBlacklistedUseCase @Inject constructor(
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
) {
    operator fun invoke(service: String, creatorId: String): Flow<Boolean> =
        blacklistedAuthorsRepository.observeContains(service = service, creatorId = creatorId)
}
