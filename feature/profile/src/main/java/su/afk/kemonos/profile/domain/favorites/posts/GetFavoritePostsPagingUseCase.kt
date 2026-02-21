package su.afk.kemonos.profile.domain.favorites.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class GetFavoritePostsPagingUseCase @Inject constructor(
    private val repository: IFavoritesRepository
) {
    operator fun invoke(
        site: SelectedSite,
        query: String?,
        groupByAuthor: Boolean,
    ): Flow<PagingData<PostDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                initialLoadSize = 50,
                prefetchDistance = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FavoritePostsPagingSource(
                    repository = repository,
                    site = site,
                    query = query,
                    groupByAuthor = groupByAuthor,
                )
            }
        ).flow
    }
}
