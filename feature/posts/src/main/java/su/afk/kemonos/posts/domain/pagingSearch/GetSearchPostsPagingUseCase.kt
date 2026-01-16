package su.afk.kemonos.posts.domain.pagingSearch

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.common.presenter.postsScreen.stableKey
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.data.PostsRepository
import javax.inject.Inject

internal class GetSearchPostsPagingUseCase @Inject constructor(
    private val repository: PostsRepository,
) {
    operator fun invoke(
        tag: String?,
        search: String?,
        site: SelectedSite,
    ): Flow<PagingData<PostDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPostsPagingSource(
                    repository = repository,
                    site = site,
                    tag = tag,
                    search = search,
                    pageSize = PAGE_SIZE,
                )
            }
        ).flow.map { pagingData ->
            val seen = HashSet<String>(PAGE_SIZE * 2)

            pagingData.filter { post ->
                seen.add(post.stableKey())
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}