package su.afk.kemonos.storage.useCases.clear

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.storage.api.clear.IClearCacheStorageUseCase
import su.afk.kemonos.storage.api.repository.comments.IStoreCommentsRepository
import su.afk.kemonos.storage.api.repository.community.IStoreCommunityRepository
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.api.repository.discord.IStoreDiscordRepository
import su.afk.kemonos.storage.api.repository.dms.IStorageDmsRepository
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
    private val storeCommunityRepository: IStoreCommunityRepository,
    private val storeDiscordRepository: IStoreDiscordRepository,
    private val storeCreatorProfileCacheRepository: IStoreCreatorProfileRepository,
    private val popularPostsCacheRepository: IStoragePopularPostsRepository,
    private val postStorageRepository: IStoragePostStorageRepository,
    private val postsSearchCacheRepository: IStoragePostsSearchRepository,
    private val dmsCacheRepository: IStorageDmsRepository,
    private val trackedDownloadsRepository: ITrackedDownloadsRepository,
    private val storeProfileRepository: IStoreProfileRepository,
    private val creatorPostsCacheRepository: IStorageCreatorPostsRepository,
    private val storeTagsRepository: IStoreTagsRepository,
    private val storeVideoInfoRepository: IStoreMediaInfoRepository,
    private val freshFavoriteArtistsUpdatesRepository: IFreshFavoriteArtistsUpdatesRepository,
) : IClearCacheStorageUseCase {

    override suspend fun clear() = coroutineScope {
        awaitAll(
            /** Чистка комментариев */
            async { storeCommentsRepository.clearCacheOver7Days() },
            /** Чистка информации о профиле */
            async { storeCreatorProfileCacheRepository.clearCacheOver7Days() },
            /** Чистка community (channels + messages) */
            async { storeCommunityRepository.clearCacheOver7Days() },
            /** Чистка discord community (channels + messages) */
            async { storeDiscordRepository.clearCacheOver7Days() },
            /** Чистка популярных постов */
            async {
                popularPostsCacheRepository.clearCache(SelectedSite.K)
                popularPostsCacheRepository.clearCache(SelectedSite.C)
            },
            /** Чистка сохраненного поста */
            async { postStorageRepository.clearCache() },
            /** Чистка поиска */
            async {
                postsSearchCacheRepository.clearCache(SelectedSite.K)
                postsSearchCacheRepository.clearCache(SelectedSite.C)
            },
            /** Чистка dms */
            async {
                dmsCacheRepository.clearCache(SelectedSite.K)
                dmsCacheRepository.clearCache(SelectedSite.C)
            },
            /** Чистка истории загрузок старше 60 дней */
            async { trackedDownloadsRepository.clearCache() },
            /** Чистка профилей креаторов */
            async { storeProfileRepository.clearCacheOver24Hours() },
            /** Чистка постов в профиле */
            async { creatorPostsCacheRepository.clearCache() },
            /** Чистка сохраненных тэгов */
            async {
                storeTagsRepository.clearIfExpired(SelectedSite.K)
                storeTagsRepository.clearIfExpired(SelectedSite.C)
            },
            /** Чистка кэша информации о видео */
            async { storeVideoInfoRepository.clearCache() },
            /** Чистка кэша свежих авторов */
            async { freshFavoriteArtistsUpdatesRepository.clearExpired() }
        )
        Unit
    }
}
