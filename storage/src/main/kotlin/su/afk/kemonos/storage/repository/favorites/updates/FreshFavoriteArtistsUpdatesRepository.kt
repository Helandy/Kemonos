package su.afk.kemonos.storage.repository.favorites.updates

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.useCase.CacheTimes.TTL_3_DAYS
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FreshFavoriteArtistKey
import su.afk.kemonos.storage.api.repository.favorites.updates.IFreshFavoriteArtistsUpdatesRepository
import su.afk.kemonos.storage.api.repository.favorites.updates.StoredFreshFavoriteArtistKey
import su.afk.kemonos.storage.entity.favorites.updates.FreshFavoriteArtistUpdateEntity
import su.afk.kemonos.storage.entity.favorites.updates.FreshFavoriteArtistUpdatesDao
import javax.inject.Inject

internal class FreshFavoriteArtistsUpdatesRepository @Inject constructor(
    private val dao: FreshFavoriteArtistUpdatesDao,
) : IFreshFavoriteArtistsUpdatesRepository {

    override suspend fun getAllActive(): List<StoredFreshFavoriteArtistKey> {
        val minSavedAtMs = System.currentTimeMillis() - TTL_3_DAYS
        return dao.getAll(minSavedAtMs).map { entity ->
            StoredFreshFavoriteArtistKey(
                site = entity.site,
                name = entity.name,
                service = entity.service,
                id = entity.id,
                savedAtMs = entity.savedAtMs,
            )
        }
    }

    override suspend fun replace(
        site: SelectedSite,
        savedAtMs: Long,
        items: Set<FreshFavoriteArtistKey>
    ) {
        dao.replace(
            site = site,
            items = items.map { item ->
                FreshFavoriteArtistUpdateEntity(
                    site = site,
                    name = item.name,
                    service = item.service,
                    id = item.id,
                    savedAtMs = savedAtMs,
                )
            }
        )
    }

    override suspend fun clear(site: SelectedSite) {
        dao.clear(site)
    }

    override suspend fun clearExpired() {
        val minSavedAtMs = System.currentTimeMillis() - TTL_3_DAYS
        dao.clearExpired(minSavedAtMs)
    }
}
