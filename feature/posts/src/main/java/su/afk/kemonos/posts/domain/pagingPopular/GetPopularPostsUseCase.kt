package su.afk.kemonos.posts.domain.pagingPopular

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.posts.data.PostsRepository
import su.afk.kemonos.posts.domain.model.popular.Period
import javax.inject.Inject

internal class GetPopularPostsUseCase @Inject constructor(
    private val repository: PostsRepository,
) {
    operator fun invoke(
        site: SelectedSite,
        date: String?,
        period: Period,
        onMeta: (PopularPosts) -> Unit,
    ): Flow<PagingData<PostDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                PopularPostsPagingSource(
                    site = site,
                    repository = repository,
                    date = date,
                    period = period,
                    pageSize = PAGE_SIZE,
                    onMeta = onMeta,
                )
            }
        ).flow
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}

