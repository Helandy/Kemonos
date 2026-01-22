package su.afk.kemonos.profile.presenter.setting.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.presenter.setting.SettingState.Event.CacheClearAction
import su.afk.kemonos.profile.presenter.setting.SettingState.State
import su.afk.kemonos.storage.api.IStoreCreatorsUseCase
import su.afk.kemonos.storage.api.creatorProfileCache.IStoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import su.afk.kemonos.storage.api.popular.IStoragePopularPostsCacheUseCase
import su.afk.kemonos.storage.api.post.IStoragePostUseCase
import su.afk.kemonos.storage.api.profilePosts.IStorageCreatorPostsCacheUseCase
import su.afk.kemonos.storage.api.tags.IStoreTagsUseCase
import javax.inject.Inject

internal class SettingCacheDelegate @Inject constructor(
    private val errorHandler: IErrorHandlerUseCase,
    private val storeTagsUseCase: IStoreTagsUseCase,
    private val storeCreatorsUseCase: IStoreCreatorsUseCase,
    private val storeCreatorProfileCacheUseCase: IStoreCreatorProfileCacheUseCase,
    private val creatorPostsCacheUseCase: IStorageCreatorPostsCacheUseCase,
    private val storagePostUseCase: IStoragePostUseCase,
    private val storagePopularPostsCacheUseCase: IStoragePopularPostsCacheUseCase,
    private val storeFavoriteArtistsUseCase: IStoreFavoriteArtistsUseCase,
    private val storeFavoritePostsUseCase: IStoreFavoritePostsUseCase,
) {
    fun handle(
        event: CacheClearAction,
        scope: CoroutineScope,
        setState: (State.() -> State) -> Unit,
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

    suspend fun clearHandler(action: CacheClearAction) {
        when (action) {
            is CacheClearAction.Tags -> clearTags(action.site)
            is CacheClearAction.Creators -> clearCreators(action.site)

            CacheClearAction.CreatorProfiles -> clearCreatorProfiles()

            CacheClearAction.CreatorPostsPages -> clearCreatorPostsPages()
            CacheClearAction.PostContents -> clearPostContents()

            is CacheClearAction.PopularPosts -> clearPopularPosts()

            is CacheClearAction.FavoritesArtists -> clearFavoritesArtists()
            is CacheClearAction.FavoritesPosts -> clearFavoritesPosts()
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
