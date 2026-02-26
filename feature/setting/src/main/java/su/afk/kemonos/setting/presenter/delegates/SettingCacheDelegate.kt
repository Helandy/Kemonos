package su.afk.kemonos.setting.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.storage.api.repository.creatorProfile.IStoreCreatorProfileRepository
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.api.repository.favorites.artist.IStoreFavoriteArtistsRepository
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import su.afk.kemonos.storage.api.repository.popular.IStoragePopularPostsRepository
import su.afk.kemonos.storage.api.repository.post.IStoragePostStorageRepository
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import su.afk.kemonos.storage.api.repository.tags.IStoreTagsRepository
import javax.inject.Inject

class SettingCacheDelegate @Inject constructor(
    private val errorHandler: IErrorHandlerUseCase,
    private val storeTagsUseCase: IStoreTagsRepository,
    private val storeCreatorsUseCase: IStoreCreatorsRepository,
    private val storeCreatorProfileCacheUseCase: IStoreCreatorProfileRepository,
    private val creatorPostsCacheUseCase: IStorageCreatorPostsRepository,
    private val storagePostUseCase: IStoragePostStorageRepository,
    private val storagePopularPostsCacheUseCase: IStoragePopularPostsRepository,
    private val storeFavoriteArtistsUseCase: IStoreFavoriteArtistsRepository,
    private val storeFavoritePostsUseCase: IStoreFavoritePostsRepository,
) {
    fun handle(
        event: SettingState.Event.CacheClearAction,
        scope: CoroutineScope,
        setState: (SettingState.State.() -> SettingState.State) -> Unit,
        onAfterSuccess: () -> Unit,
    ) {
        scope.launch {
            setState { copy(clearInProgress = true, clearSuccess = null) }

            runCatching { clearHandler(event) }
                .onSuccess {
                    onAfterSuccess()
                    setState { copy(clearInProgress = false, clearSuccess = true) }
                }
                .onFailure { e ->
                    setState { copy(clearInProgress = false, clearSuccess = false) }
                    errorHandler.parse(e)
                }
        }
    }

    suspend fun clearHandler(action: SettingState.Event.CacheClearAction) {
        when (action) {
            is SettingState.Event.CacheClearAction.Tags -> clearTags(action.site)
            is SettingState.Event.CacheClearAction.Creators -> clearCreators(action.site)

            SettingState.Event.CacheClearAction.CreatorProfiles -> clearCreatorProfiles()

            SettingState.Event.CacheClearAction.CreatorPostsPages -> clearCreatorPostsPages()
            SettingState.Event.CacheClearAction.PostContents -> clearPostContents()

            is SettingState.Event.CacheClearAction.PopularPosts -> clearPopularPosts()

            is SettingState.Event.CacheClearAction.FavoritesArtists -> clearFavoritesArtists()
            is SettingState.Event.CacheClearAction.FavoritesPosts -> clearFavoritesPosts()
        }
    }

    private suspend fun clearTags(site: SelectedSite) {
        storeTagsUseCase.clear(site)
    }

    private suspend fun clearCreators(site: SelectedSite) {
        storeCreatorsUseCase.clear(site)
    }

    private suspend fun clearCreatorProfiles() {
        storeCreatorProfileCacheUseCase.clearAll()
    }

    private suspend fun clearCreatorPostsPages() {
        creatorPostsCacheUseCase.clearAll()
    }

    private suspend fun clearPostContents() {
        storagePostUseCase.clearAll()
    }

    private suspend fun clearPopularPosts() {
        storagePopularPostsCacheUseCase.clearAll(SelectedSite.K)
        storagePopularPostsCacheUseCase.clearAll(SelectedSite.C)
    }

    private suspend fun clearFavoritesArtists() {
        storeFavoriteArtistsUseCase.clear(SelectedSite.K)
        storeFavoriteArtistsUseCase.clear(SelectedSite.C)
    }

    private suspend fun clearFavoritesPosts() {
        storeFavoritePostsUseCase.clear(SelectedSite.K)
        storeFavoritePostsUseCase.clear(SelectedSite.C)
    }
}
