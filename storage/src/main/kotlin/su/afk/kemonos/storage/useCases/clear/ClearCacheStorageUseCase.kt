package su.afk.kemonos.storage.useCases.clear

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.storage.repository.comments.IStoreCommentsRepository
import su.afk.kemonos.storage.repository.creatorProfileCache.IStoreCreatorProfileCacheRepository
import su.afk.kemonos.storage.repository.popular.IPopularPostsCacheRepository
import su.afk.kemonos.storage.repository.post.IPostStorageRepository
import su.afk.kemonos.storage.repository.postsSearch.IPostsSearchCacheRepository
import su.afk.kemonos.storage.repository.profile.IStoreProfileRepository
import su.afk.kemonos.storage.repository.profilePosts.ICreatorPostsCacheRepository
import su.afk.kemonos.storage.repository.tags.IStoreTagsRepository
import su.afk.kemonos.storage.repository.video.IStoreVideoInfoRepository
import javax.inject.Inject

internal class ClearCacheStorageUseCase @Inject constructor(
    private val storeCommentsRepository: IStoreCommentsRepository,
    private val storeCreatorProfileCacheRepository: IStoreCreatorProfileCacheRepository,
    private val popularPostsCacheRepository: IPopularPostsCacheRepository,
    private val postStorageRepository: IPostStorageRepository,
    private val postsSearchCacheRepository: IPostsSearchCacheRepository,
    private val storeProfileRepository: IStoreProfileRepository,
    private val creatorPostsCacheRepository: ICreatorPostsCacheRepository,
    private val storeTagsRepository: IStoreTagsRepository,
    private val storeVideoInfoRepository: IStoreVideoInfoRepository,
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
        /** Чистка профилей креаторов */
        storeProfileRepository.clearCacheOver24Hours()
        /** Чистка постов в профиле */
        creatorPostsCacheRepository.clearCache()
        /** Чистка сохраненных тэгов */
        storeTagsRepository.clearIfExpired(SelectedSite.K)
        storeTagsRepository.clearIfExpired(SelectedSite.C)
        /** Чистка кэша информации о видео */
        storeVideoInfoRepository.clearCache()
    }
}