package su.afk.kemonos.profile.domain.favorites.creator

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType
import su.afk.kemonos.profile.data.IFavoritesRepository
import javax.inject.Inject

internal class GetFavoriteArtistsPagingUseCase @Inject constructor(
    private val store: IFavoritesRepository
) {
    operator fun invoke(
        site: SelectedSite,
        service: String,
        query: String,
        sort: FavoriteSortedType,
        ascending: Boolean,
    ): Flow<PagingData<FavoriteArtist>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                initialLoadSize = 50,
                prefetchDistance = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FavoriteArtistsPagingSource(
                    store = store,
                    site = site,
                    service = service,
                    query = query.trim(),
                    sort = sort,
                    ascending = ascending,
                )
            }
        ).flow
    }

    suspend fun getDistinctServices(site: SelectedSite): List<String> =
        store.getDistinctServices(site)
}