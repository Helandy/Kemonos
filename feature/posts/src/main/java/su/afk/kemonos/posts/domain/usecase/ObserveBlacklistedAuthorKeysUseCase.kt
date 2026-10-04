package su.afk.kemonos.posts.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.blacklist.blacklistKey
import javax.inject.Inject

/** Ключи (`service:creatorId`) авторов из чёрного списка, для фильтрации лент. */
internal class ObserveBlacklistedAuthorKeysUseCase @Inject constructor(
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
) {
    operator fun invoke(): Flow<Set<String>> =
        blacklistedAuthorsRepository.observeAll()
            .map { items ->
                items.mapTo(mutableSetOf()) { blacklisted ->
                    blacklistKey(blacklisted.service, blacklisted.creatorId)
                }
            }
            .distinctUntilChanged()
}
