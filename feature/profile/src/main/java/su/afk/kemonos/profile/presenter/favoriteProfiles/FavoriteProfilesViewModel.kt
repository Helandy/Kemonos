package su.afk.kemonos.profile.presenter.favoriteProfiles

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import su.afk.kemonos.profile.api.model.FavoriteArtist
import su.afk.kemonos.profile.presenter.favoriteProfiles.views.FavoriteSortedType
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class FavoriteProfilesViewModel @Inject constructor(
    private val getFavoriteArtistsUseCase: IGetFavoriteArtistsUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<FavoriteProfilesState>(FavoriteProfilesState()) {

    override fun onRetry() {
        load()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    init {
        observeUiSetting()
        loadSelectedSite()
    }

    private fun loadSelectedSite() = viewModelScope.launch {
        val selectSite = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE) ?: SelectedSite.K

        selectedSiteUseCase.setSite(selectSite)
        selectedSiteUseCase.selectedSite.first { it == selectSite }

        setState {
            copy(
                selectSite = selectSite
            )
        }
        load()
    }

    /** Первичная загрузка избранных профилей */
    fun load() = viewModelScope.launch {
        setState { copy(loading = true) }

        val favorites: List<FavoriteArtist> = getFavoriteArtistsUseCase(site = currentState.selectSite)

        /** По умолчанию сортируем по "дате новой публикации" */
        val sorted = favorites.sortedByDescending { it.updated }

        setState {
            copy(
                loading = false,
                favoriteProfiles = sorted,
                searchCreators = sorted,
                selectedService = "Services"
            )
        }
    }

    /** Обновление поисковой строки */
    fun updateSearch(query: String) {
        setState { copy(searchQuery = query) }
        filterFavorites()
    }

    /** Выбор сервиса (All / Patreon / Fanbox / и т.п.) */
    fun setService(service: String) {
        setState { copy(selectedService = service) }
        filterFavorites()
    }

    /** Выбор метода сортировки */
    fun setSortType(type: FavoriteSortedType) {
        setState { copy(sortedType = type) }
        filterFavorites()
    }

    /** Переключаем направление сортировки (по возрастанию / убыванию) */
    fun toggleSortOrder() {
        setState { copy(sortAscending = !state.value.sortAscending) }
        filterFavorites()
    }

    /**
     * Локальная фильтрация + сортировка:
     *  - по selectedService
     *  - по searchQuery
     *  - по выбранному sortedType
     *  - по sortAscending
     */
    private fun filterFavorites() = viewModelScope.launch(Dispatchers.Default) {
        val current = state.value

        val query = current.searchQuery
        val service = current.selectedService
        val sortedType = current.sortedType
        val ascending = current.sortAscending

        var filtered = current.favoriteProfiles

        /** Фильтр по сервису */
        if (service != "Services") {
            filtered = filtered.filter { it.service == service }
        }

        if (query.length >= 2) {
            filtered = filtered.filter { artist ->
                artist.name.contains(query, ignoreCase = true)
            }
        }

        /** Сортировка по выбранному типу:
         * - NewPostsDate  -> updated
         * - FavedDate     -> favedSeq
         * - ReimportDate  -> lastImported
         */
        filtered = when (sortedType) {
            FavoriteSortedType.NewPostsDate ->
                filtered.sortedBy { it.updated }

            FavoriteSortedType.FavedDate ->
                filtered.sortedBy { it.favedSeq }

            FavoriteSortedType.ReimportDate ->
                filtered.sortedBy { it.lastImported }
        }

        if (!ascending) {
            filtered = filtered.reversed()
        }

        setState { copy(searchCreators = filtered) }
    }

    /** Список сервисов для дропа: All + distinct по service */
    fun getServices(): List<String> =
        state.value.favoriteProfiles
            .map { it.service }
            .distinct()
            .sorted()
            .let { listOf("Services") + it }


    fun onCreatorClick(creator: FavoriteArtist, isFresh: Boolean) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = creator.service,
                id = creator.id,
                isFresh = isFresh
            )
        )
    }
}

