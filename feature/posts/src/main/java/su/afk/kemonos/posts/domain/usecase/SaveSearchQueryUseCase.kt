package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.repository.postsSearchHistory.IStoragePostsSearchHistoryRepository
import javax.inject.Inject

internal class SaveSearchQueryUseCase @Inject constructor(
    private val searchHistoryRepository: IStoragePostsSearchHistoryRepository,
) {
    suspend operator fun invoke(site: SelectedSite, query: String, limit: Int) =
        searchHistoryRepository.save(site = site, query = query, limit = limit)
}
