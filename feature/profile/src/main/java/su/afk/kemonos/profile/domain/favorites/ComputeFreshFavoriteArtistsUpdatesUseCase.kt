package su.afk.kemonos.profile.domain.favorites

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.profile.data.FreshFavoriteArtistKey
import java.time.Instant
import javax.inject.Inject

interface IComputeFreshFavoriteArtistsUpdatesUseCase {
    operator fun invoke(
        site: SelectedSite,
        oldCache: List<FavoriteArtist>,
        network: List<FavoriteArtist>,
    ): Set<FreshFavoriteArtistKey>
}

internal class ComputeFreshFavoriteArtistsUpdatesUseCase @Inject constructor(
) : IComputeFreshFavoriteArtistsUpdatesUseCase {

    override fun invoke(
        site: SelectedSite,
        oldCache: List<FavoriteArtist>,
        network: List<FavoriteArtist>
    ): Set<FreshFavoriteArtistKey> {
        val oldByKey: Map<String, FavoriteArtist> = oldCache.associateBy { keyOf(it) }

        return network.asSequence()
            .filter { fresh ->
                val prev = oldByKey[keyOf(fresh)]
                prev == null || isNewerIso(fresh.updated, prev.updated)
            }
            .map { fresh ->
                FreshFavoriteArtistKey(
                    name = fresh.name,
                    service = fresh.service,
                    id = fresh.id
                )
            }
            .toSet()
    }

    private fun keyOf(a: FavoriteArtist): String =
        "${a.service}::${a.id}::${a.name}"

    private fun isNewerIso(newValue: String?, oldValue: String?): Boolean {
        if (newValue.isNullOrBlank()) return false
        if (oldValue.isNullOrBlank()) return true

        return runCatching {
            Instant.parse(newValue).isAfter(Instant.parse(oldValue))
        }.getOrElse {
            newValue > oldValue
        }
    }
}