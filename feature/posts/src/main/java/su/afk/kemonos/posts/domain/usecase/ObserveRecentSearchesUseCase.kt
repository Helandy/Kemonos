package su.afk.kemonos.posts.domain.usecase

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.repository.postsSearchHistory.IStoragePostsSearchHistoryRepository
import javax.inject.Inject

internal class ObserveRecentSearchesUseCase @Inject constructor(
    private val searchHistoryRepository: IStoragePostsSearchHistoryRepository,
) {
    operator fun invoke(site: SelectedSite, limit: Int): Flow<List<String>> =
        searchHistoryRepository.observeRecent(site = site, limit = limit)
}
