package su.afk.kemonos.profile.domain.blacklist

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import javax.inject.Inject

internal class ObserveBlacklistedAuthorsUseCase @Inject constructor(
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
) {
    operator fun invoke(): Flow<List<BlacklistedAuthor>> = blacklistedAuthorsRepository.observeAll()
}
