package su.afk.kemonos.storage.useCases.clear

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.storage.api.repository.comments.IStoreCommentsRepository
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.api.repository.download.ITrackedDownloadsRepository
import su.afk.kemonos.storage.api.repository.favorites.updates.IFreshFavoriteArtistsUpdatesRepository
import su.afk.kemonos.storage.api.repository.media.IStoreMediaInfoRepository
import su.afk.kemonos.storage.api.repository.popular.IStoragePopularPostsRepository
import su.afk.kemonos.storage.api.repository.post.IStoragePostStorageRepository
import su.afk.kemonos.storage.api.repository.postsSearch.IStoragePostsSearchRepository
import su.afk.kemonos.storage.api.repository.profile.IStoreProfileRepository
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import su.afk.kemonos.storage.api.repository.tags.IStoreTagsRepository
import javax.inject.Inject

internal class ClearCacheStorageUseCase @Inject constructor(
    private val storeCommentsRepository: IStoreCommentsRepository,
    private val storeCreatorProfileCacheRepository: IStoreCreatorProfileRepository,
    private val popularPostsCacheRepository: IStoragePopularPostsRepository,
    private val postStorageRepository: IStoragePostStorageRepository,
    private val postsSearchCacheRepository: IStoragePostsSearchRepository,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
    private val storeProfileRepository: IStoreProfileRepository,
    private val creatorPostsCacheRepository: IStorageCreatorPostsRepository,
    private val storeTagsRepository: IStoreTagsRepository,
    private val storeVideoInfoRepository: IStoreMediaInfoRepository,
    private val freshFavoriteArtistsUpdatesRepository: IFreshFavoriteArtistsUpdatesRepository,
) : IClearCacheStorageUseCase {

    override suspend fun clear() {
        /** Чистка коментариев */
        storeCommentsRepository.clearCacheOver7Days()
        /** Чистка информации о профиле */
        storeCreatorProfileCacheRepository.clearCacheOver7Days()
        /** Чистка популярных постов */
        popularPostsCacheRepository.clearCache(SelectedSite.K)
        popularPostsCacheRepository.clearCache(SelectedSite.C)
        /** Чистка сохраненного поста */
        postStorageRepository.clearCache()
        /** Чистка поиска */
        postsSearchCacheRepository.clearCache(SelectedSite.K)
        postsSearchCacheRepository.clearCache(SelectedSite.C)
        /** Чистка истории загрузок старше 60 дней */
        trackedDownloadsRepository.clearCache()
        /** Чистка профилей креаторов */
        storeProfileRepository.clearCacheOver24Hours()
        /** Чистка постов в профиле */
        creatorPostsCacheRepository.clearCache()
        /** Чистка сохраненных тэгов */
        storeTagsRepository.clearIfExpired(SelectedSite.K)
        storeTagsRepository.clearIfExpired(SelectedSite.C)
        /** Чистка кэша информации о видео */
        storeVideoInfoRepository.clearCache()
        /** Чистка кэша свежих авторов */
        freshFavoriteArtistsUpdatesRepository.clearExpired()
    }
}
