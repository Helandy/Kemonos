package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.favoriteProfiles.IFavoriteProfilesFiltersUseCase
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType
import su.afk.kemonos.profile.domain.favorites.creator.GetFavoriteArtistsPagingUseCase
import su.afk.kemonos.profile.domain.favorites.fresh.IFreshFavoriteArtistsUpdatesUseCase
import su.afk.kemonos.profile.presenter.favoriteProfiles.FavoriteProfilesState.*
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class FavoriteProfilesViewModel @Inject constructor(
    private val getFavoriteArtistsUseCase: IGetFavoriteArtistsUseCase,
    private val getFavoriteArtistsPagingUseCase: GetFavoriteArtistsPagingUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val favoriteProfilesFiltersUseCase: IFavoriteProfilesFiltersUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    private val freshUpdatesUseCase: IFreshFavoriteArtistsUpdatesUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    private val searchQueryFlow = MutableStateFlow("")
    private var observeJob: Job? = null

    override fun createInitialState(): State = State()

    init {
        observeUiSetting()
        loadSelectedSite()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.QueryChanged -> {
                setState { copy(searchQuery = event.value) }
                searchQueryFlow.value = event.value
            }

            is Event.ServiceSelected -> {
                setState { copy(selectedService = event.value) }
                requestPaging()
                setEffect(Effect.ScrollToTop)
            }

            is Event.SortSelected -> {
                setState { copy(sortedType = event.value) }
                saveSortedType(event.value)
                requestPaging()
                setEffect(Effect.ScrollToTop)
            }

            Event.ToggleSortOrder -> {
                val newSortAscending = !currentState.sortAscending
                setState { copy(sortAscending = newSortAscending) }
                saveSortAscending(newSortAscending)
                requestPaging()
                setEffect(Effect.ScrollToTop)
            }

            Event.Refresh -> load(refresh = true)
            Event.Retry -> load(refresh = true)

            is Event.CreatorClicked -> onCreatorClick(event.creator, event.isFresh)
        }
    }

    private fun observeUiSetting() {
        uiSetting.prefs
            .distinctUntilChanged()
            .onEach { model -> setState { copy(uiSettingModel = model) } }
            .launchIn(viewModelScope)
    }

    private fun startObserveSearch() {
        if (observeJob != null) return

        observeJob = observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch(): Job {
        return searchQueryFlow
            .debounce(500L)
            .map { it.trim() }
            .distinctUntilChanged()
            .onEach { q ->
                // важно: state.searchQuery должен совпадать с тем, что реально используем
                setState { copy(searchQuery = q) }
                requestPaging()
                setEffect(Effect.ScrollToTop)
            }
            .launchIn(viewModelScope)
    }

    private fun loadSelectedSite() = viewModelScope.launch {
        val site = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE) ?: SelectedSite.K

        selectedSiteUseCase.setSite(site)
        selectedSiteUseCase.selectedSite.first { it == site }

        val savedFilters = favoriteProfilesFiltersUseCase.read(site)
        val restoredSortType = runCatching {
            enumValueOf<FavoriteSortedType>(savedFilters.sortedTypeName)
        }.getOrDefault(FavoriteSortedType.NewPostsDate)
        setState {
            copy(
                selectedSite = site,
                selectedService = "Services",
                sortedType = restoredSortType,
                sortAscending = savedFilters.sortAscending,
                freshSet = freshUpdatesUseCase.get(site),
            )
        }

        // 1) сначала обновляем БД сетью (если нужно)
        load(refresh = false)

        // 2) затем включаем дебаунс и пейджинг из БД
        startObserveSearch()
        requestPaging()
    }

    private fun requestPaging() {
        val s = currentState.selectedService
        val q = currentState.searchQuery
        val sort = currentState.sortedType
        val asc = currentState.sortAscending
        val site = currentState.selectedSite

        setState {
            copy(
                artistsPaged = getFavoriteArtistsPagingUseCase(
                    site = site,
                    service = s,
                    query = q,
                    sort = sort,
                    ascending = asc
                ).cachedIn(viewModelScope)
            )
        }
    }

    private fun load(refresh: Boolean) = viewModelScope.launch {
        setState { copy(loading = !refresh, refreshing = refresh) }

        runCatching {
            // это то, что реально кладёт в БД
            getFavoriteArtistsUseCase(site = currentState.selectedSite, checkDifferent = false, refresh = refresh)
        }.onFailure { t ->
            errorHandler.parse(t)
        }

        // список сервисов (distinct) удобно держать в state
        val services = runCatching {
            getFavoriteArtistsPagingUseCase.getDistinctServices(currentState.selectedSite)
        }.getOrDefault(emptyList())

        setState {
            copy(
                services = listOf("Services") + services,
                freshSet = freshUpdatesUseCase.get(currentState.selectedSite),
                loading = false,
                refreshing = false
            )
        }
    }

    private fun saveSortedType(value: FavoriteSortedType) = viewModelScope.launch {
        favoriteProfilesFiltersUseCase.setSortedTypeName(currentState.selectedSite, value.name)
    }

    private fun saveSortAscending(value: Boolean) = viewModelScope.launch {
        favoriteProfilesFiltersUseCase.setSortAscending(currentState.selectedSite, value)
    }

    override fun onRetry() {
        onEvent(Event.Retry)
    }

    private fun onCreatorClick(creator: FavoriteArtist, isFresh: Boolean) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = creator.service,
                id = creator.id,
                isFresh = isFresh
            )
        )
    }
}
