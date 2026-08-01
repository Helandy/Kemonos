package su.afk.kemonos.profile.data.repository

import kotlinx.coroutines.flow.first
import su.afk.kemonos.auth.IsAuthCoomerUseCase
import su.afk.kemonos.auth.IsAuthKemonoUseCase
import su.afk.kemonos.auth.IsAuthPawchiveUseCase
import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.network.util.call
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType
import su.afk.kemonos.profile.data.api.FavoritesApi
import su.afk.kemonos.profile.data.dto.favorites.artist.FavoriteArtistDto.Companion.toDomain
import su.afk.kemonos.profile.data.repository.FavoritesRepository.Companion.MIN_POST_SEARCH_QUERY_LENGTH
import su.afk.kemonos.profile.domain.repository.IFavoritesRepository
import su.afk.kemonos.storage.api.repository.favorites.artist.IStoreFavoriteArtistsRepository
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import su.afk.kemonos.storage.api.repository.localLikes.IStoreLocalLikedArtistsRepository
import su.afk.kemonos.storage.api.repository.localLikes.IStoreLocalLikedPostsRepository
import javax.inject.Inject

internal class FavoritesRepository @Inject constructor(
    private val api: FavoritesApi,
    private val artistsStore: IStoreFavoriteArtistsRepository,
    private val postsStore: IStoreFavoritePostsRepository,
    private val localLikedPostsStore: IStoreLocalLikedPostsRepository,
    private val localLikedArtistsStore: IStoreLocalLikedArtistsRepository,
    private val isAuthKemonoUseCase: IsAuthKemonoUseCase,
    private val isAuthCoomerUseCase: IsAuthCoomerUseCase,
    private val isAuthPawchiveUseCase: IsAuthPawchiveUseCase,
) : IFavoritesRepository {

    private suspend fun isAuthorized(site: SelectedSite): Boolean {
        return when (site) {
            SelectedSite.C -> isAuthCoomerUseCase().first()
            SelectedSite.K -> isAuthKemonoUseCase().first()
            SelectedSite.P -> isAuthPawchiveUseCase().first()
        }
    }

    /**
     * Пагинация favorite artists. Без авторизации — из локальных лайков (сервер недоступен без сессии),
     * без учета сортировки/фильтра по сервису (не применимо для локального режима).
     */
    override suspend fun pageFavoriteArtists(
        site: SelectedSite,
        service: String,
        query: String,
        sort: FavoriteSortedType,
        ascending: Boolean,
        limit: Int,
        offset: Int,
    ): List<FavoriteArtist> {
        if (!isAuthorized(site)) {
            return localLikedArtistsStore.page(site = site, query = query, limit = limit, offset = offset)
        }

        return artistsStore.page(
            site = site,
            service = service,
            query = query,
            sort = sort,
            ascending = ascending,
            limit = limit,
            offset = offset
        )
    }

    override suspend fun getDistinctServices(site: SelectedSite): List<String> =
        artistsStore.getDistinctServices(site)

    /**
     * Пагинация favorite posts. Без авторизации — из локальных лайков (сервер недоступен без сессии).
     * Для короткого запроса (< [MIN_POST_SEARCH_QUERY_LENGTH]) отключаем LIKE-поиск ради скорости.
     */
    override suspend fun pageFavoritePosts(
        site: SelectedSite,
        query: String?,
        groupByAuthor: Boolean,
        limit: Int,
        offset: Int,
    ): List<PostDomain> {
        val q = query?.trim().orEmpty()

        if (!isAuthorized(site)) {
            return if (q.length >= MIN_POST_SEARCH_QUERY_LENGTH) {
                localLikedPostsStore.pageSearch(site = site, query = q, limit = limit, offset = offset)
            } else {
                localLikedPostsStore.page(site = site, limit = limit, offset = offset)
            }
        }

        return if (q.length >= MIN_POST_SEARCH_QUERY_LENGTH) {
            if (groupByAuthor) {
                postsStore.pageSearchGrouped(site = site, query = q, limit = limit, offset = offset)
            } else {
                postsStore.pageSearch(site = site, query = q, limit = limit, offset = offset)
            }
        } else {
            if (groupByAuthor) {
                postsStore.pageGrouped(site = site, limit = limit, offset = offset)
            } else {
                postsStore.page(site = site, limit = limit, offset = offset)
            }
        }
    }

    /**
     * Возвращает favorite artists с учетом freshness кэша и флага принудительного refresh.
     * Без авторизации на сайте сервер недоступен — список читается из локально лайкнутых авторов.
     */
    override suspend fun getFavoriteArtists(
        site: SelectedSite,
        getOldCache: Boolean,
        forceRefresh: Boolean,
    ): List<FavoriteArtist> {
        if (!isAuthorized(site)) return localLikedArtistsStore.getAll(site)

        if (getOldCache) return artistsStore.getAll(site)
        if (forceRefresh) return refreshFavoriteArtists(site)

        if (artistsStore.isCacheFresh(site)) return artistsStore.getAll(site)
        return refreshFavoriteArtists(site)
    }

    /** Принудительно обновляет favorite artists из сети и полностью синхронизирует Room-кэш. */
    override suspend fun refreshFavoriteArtists(site: SelectedSite): List<FavoriteArtist> {
        api.getFavoriteArtists().call { list ->
            val network = list.map { it.toDomain() }
            artistsStore.replaceAll(site, network)
            return network
        }
    }

    /**
     * Возвращает favorite posts. Без авторизации на сайте сервер недоступен —
     * список читается из локально лайкнутых постов.
     */
    override suspend fun getFavoritePosts(site: SelectedSite, refresh: Boolean): List<PostDomain> {
        if (!isAuthorized(site)) {
            return localLikedPostsStore.getAll(site)
        }

        if (!refresh && postsStore.isCacheFresh(site)) {
            return postsStore.getAll(site)
        }

        return api.getFavoritePosts().call { list ->
            val network = list.map { it.toDomain() }
            postsStore.replaceAll(site, network)
            network
        }
    }

    /** Отправляет ранее лайкнутые без авторизации посты и авторов на сервер после логина. */
    override suspend fun syncLocalLikes(site: SelectedSite) {
        if (!isAuthorized(site)) return

        syncLocalLikedPosts(site)
        syncLocalLikedArtists(site)
    }

    private suspend fun syncLocalLikedPosts(site: SelectedSite) {
        val pending = runCatching { localLikedPostsStore.getAll(site) }.getOrDefault(emptyList())
        if (pending.isEmpty()) return

        var synced = false
        pending.forEach { post ->
            val success = runCatching {
                api.addFavoritePost(
                    service = post.service,
                    creatorId = post.userId,
                    postId = post.id,
                ).isSuccessful
            }.getOrDefault(false)

            if (success) {
                localLikedPostsStore.remove(
                    site = site,
                    service = post.service,
                    creatorId = post.userId,
                    postId = post.id,
                )
                synced = true
            }
        }

        if (synced) {
            runCatching { getFavoritePosts(site = site, refresh = true) }
        }
    }

    private suspend fun syncLocalLikedArtists(site: SelectedSite) {
        val pending = runCatching { localLikedArtistsStore.getAll(site) }.getOrDefault(emptyList())
        if (pending.isEmpty()) return

        var synced = false
        pending.forEach { artist ->
            val success = runCatching {
                api.addFavoriteCreator(service = artist.service, id = artist.id).isSuccessful
            }.getOrDefault(false)

            if (success) {
                localLikedArtistsStore.remove(site = site, service = artist.service, id = artist.id)
                synced = true
            }
        }

        if (synced) {
            runCatching { refreshFavoriteArtists(site) }
        }
    }

    private companion object {
        private const val MIN_POST_SEARCH_QUERY_LENGTH = 2
    }
}
