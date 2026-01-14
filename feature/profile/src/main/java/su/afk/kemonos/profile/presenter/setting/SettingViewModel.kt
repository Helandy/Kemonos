package su.afk.kemonos.profile.presenter.setting

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.siteUrl.ISetBaseUrlsUseCase
import su.afk.kemonos.preferences.useCase.CacheKeys
import su.afk.kemonos.preferences.useCase.CacheTimes
import su.afk.kemonos.preferences.useCase.ICacheTimestampUseCase
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.storage.api.IStoreCreatorsUseCase
import su.afk.kemonos.storage.api.creatorProfileCache.IStoreCreatorProfileCacheUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoriteArtistsUseCase
import su.afk.kemonos.storage.api.favorites.IStoreFavoritePostsUseCase
import su.afk.kemonos.storage.api.popular.IStoragePopularPostsCacheUseCase
import su.afk.kemonos.storage.api.post.IStoragePostUseCase
import su.afk.kemonos.storage.api.profilePosts.IStorageCreatorPostsCacheUseCase
import su.afk.kemonos.storage.api.tags.IStoreTagsUseCase
import su.afk.kemonos.utils.buildBaseUrl
import su.afk.kemonos.utils.normalizeDomain
import su.afk.kemonos.utils.toRootUrl
import javax.inject.Inject

@HiltViewModel
internal class SettingViewModel @Inject constructor(
    private val getCoomerRootUrlUseCase: GetCoomerRootUrlUseCase,
    private val getKemonoRootUrlUseCase: GetKemonoRootUrlUseCase,
    private val cacheTimestamps: ICacheTimestampUseCase,
    private val setBaseUrlsUseCase: ISetBaseUrlsUseCase,
    private val storeCreatorProfileCacheUseCase: IStoreCreatorProfileCacheUseCase,
    private val storeCreatorsUseCase: IStoreCreatorsUseCase,
    private val storeFavoriteArtistsUseCase: IStoreFavoriteArtistsUseCase,
    private val storeFavoritePostsUseCase: IStoreFavoritePostsUseCase,
    private val storeTagsUseCase: IStoreTagsUseCase,
    private val creatorPostsCacheUseCase: IStorageCreatorPostsCacheUseCase,
    private val storagePostUseCase: IStoragePostUseCase,
    private val storagePopularPostsCacheUseCase: IStoragePopularPostsCacheUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage
) : BaseViewModel<SettingState.State>(SettingState.State()) {

    init {
        observeUrls()
        getAppVersion()
        observeCacheTimes()
        setState { copy(loading = false) }
    }

    fun onInputKemonoDomainChanged(value: String) {
        setState { copy(inputKemonoDomain = normalizeDomain(value)) }
    }

    fun onInputCoomerDomainChanged(value: String) {
        setState { copy(inputCoomerDomain = normalizeDomain(value)) }
    }

    fun onSaveUrls() = viewModelScope.launch {
        setState { copy(isSaving = true, saveSuccess = false) }

        val kemono = buildBaseUrl(state.value.inputKemonoDomain)
        val coomer = buildBaseUrl(state.value.inputCoomerDomain)

        runCatching {
            setBaseUrlsUseCase(kemonoUrl = kemono, coomerUrl = coomer)
        }.onSuccess {
            setState {
                copy(
                    isSaving = false,
                    saveSuccess = true,
                    kemonoUrl = kemono.toRootUrl(),
                    coomerUrl = coomer.toRootUrl()
                )
            }
        }.onFailure { e ->
            setState { copy(isSaving = false, saveSuccess = false) }
            errorHandler.parse(e)
        }
    }

    /** Получение версии */
    private fun getAppVersion() {
        setState { copy(appVersion = BuildConfig.VERSION_NAME) }
    }

    /** Актуальные урлы на сайт (и заполнение инпутов по умолчанию) */
    private fun observeUrls() {
        val kemono = getKemonoRootUrlUseCase()
        val coomer = getCoomerRootUrlUseCase()

        setState {
            copy(
                kemonoUrl = kemono,
                coomerUrl = coomer,
                inputKemonoDomain = state.value.inputKemonoDomain.ifEmpty { normalizeDomain(kemono) },
                inputCoomerDomain = state.value.inputCoomerDomain.ifEmpty { normalizeDomain(coomer) },
            )
        }
    }

    /** Время кэширования */
    private fun observeCacheTimes() {
        setState {
            copy(
                creatorsKemonoCache = cacheTimestamps.cacheTimeUi(CacheKeys.CREATORS_KEMONO, CacheTimes.TTL_7_DAYS),
                creatorsCoomerCache = cacheTimestamps.cacheTimeUi(CacheKeys.CREATORS_COOMER, CacheTimes.TTL_7_DAYS),

                tagsKemonoCache = cacheTimestamps.cacheTimeUi(CacheKeys.TAGS_KEMONO, CacheTimes.TTL_30_DAYS),
                tagsCoomerCache = cacheTimestamps.cacheTimeUi(CacheKeys.TAGS_COOMER, CacheTimes.TTL_30_DAYS),

                favoritesPostsKemonoCache = cacheTimestamps.cacheTimeUi(
                    "${CacheKeys.FAVORITES_POSTS}_${SelectedSite.K.name}",
                    CacheTimes.TTL_1_HOURS
                ),
                favoritesPostsCoomerCache = cacheTimestamps.cacheTimeUi(
                    "${CacheKeys.FAVORITES_POSTS}_${SelectedSite.C.name}",
                    CacheTimes.TTL_1_HOURS
                ),

                favoritesArtistsKemonoCache = cacheTimestamps.cacheTimeUi(
                    "${CacheKeys.FAVORITES_ARTISTS}_${SelectedSite.K.name}",
                    CacheTimes.TTL_1_HOURS
                ),
                favoritesArtistsCoomerCache = cacheTimestamps.cacheTimeUi(
                    "${CacheKeys.FAVORITES_ARTISTS}_${SelectedSite.C.name}",
                    CacheTimes.TTL_1_HOURS
                ),
            )
        }
    }

    fun onClear(action: CacheClearAction) = viewModelScope.launch {
        setState { copy(clearInProgress = true, clearSuccess = null) }

        runCatching {
            when (action) {
                is CacheClearAction.Tags -> clearTagsCacheUseCase(action.site)
                is CacheClearAction.Creators -> clearCreatorsCacheUseCase(action.site)

                CacheClearAction.CreatorProfiles -> clearCreatorProfilesCacheUseCase()

                CacheClearAction.CreatorPostsPages -> clearCreatorPostsCacheUseCase()
                CacheClearAction.PostContents -> clearPostContentsCacheUseCase()

                is CacheClearAction.PopularPosts -> clearPopularPostsCacheUseCase()

                is CacheClearAction.FavoritesArtists -> clearFavoritesArtistsCacheUseCase()
                is CacheClearAction.FavoritesPosts -> clearFavoritesPostsCacheUseCase()
            }
        }.onSuccess {
            observeCacheTimes()
            setState { copy(clearInProgress = false, clearSuccess = true) }
        }.onFailure { e ->
            setState { copy(clearInProgress = false, clearSuccess = false) }
            errorHandler.parse(e)
        }
    }

    private suspend fun clearTagsCacheUseCase(site: SelectedSite) {
        storeTagsUseCase.clear(site)
    }

    private suspend fun clearCreatorsCacheUseCase(site: SelectedSite) {
        storeCreatorsUseCase.clear(site)
    }

    private suspend fun clearCreatorProfilesCacheUseCase() {
        storeCreatorProfileCacheUseCase.clearAll()
    }

    private suspend fun clearCreatorPostsCacheUseCase() {
        creatorPostsCacheUseCase.clearAll()
    }

    private suspend fun clearPostContentsCacheUseCase() {
        storagePostUseCase.clearAll()
    }

    private suspend fun clearPopularPostsCacheUseCase() {
        storagePopularPostsCacheUseCase.clearAll(SelectedSite.K)
        storagePopularPostsCacheUseCase.clearAll(SelectedSite.C)
    }

    private suspend fun clearFavoritesArtistsCacheUseCase() {
        storeFavoriteArtistsUseCase.clear(SelectedSite.K)
        storeFavoriteArtistsUseCase.clear(SelectedSite.C)
    }

    private suspend fun clearFavoritesPostsCacheUseCase() {
        storeFavoritePostsUseCase.clear(SelectedSite.K)
        storeFavoritePostsUseCase.clear(SelectedSite.C)
    }
}