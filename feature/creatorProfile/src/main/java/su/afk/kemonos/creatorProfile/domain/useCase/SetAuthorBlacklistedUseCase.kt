package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import javax.inject.Inject

internal class SetAuthorBlacklistedUseCase @Inject constructor(
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
) {
    suspend operator fun invoke(
        service: String,
        creatorId: String,
        creatorName: String,
        blacklisted: Boolean,
    ) {
        if (blacklisted) {
            blacklistedAuthorsRepository.upsert(
                BlacklistedAuthor(
                    service = service,
                    creatorId = creatorId,
                    creatorName = creatorName,
                    createdAt = System.currentTimeMillis(),
                )
            )
        } else {
            blacklistedAuthorsRepository.remove(service = service, creatorId = creatorId)
        }
    }
}
