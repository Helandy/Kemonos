package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.favoriteProfiles.IFavoriteProfilesFiltersUseCase
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.api.domain.IGetFavoriteArtistsUseCase
import su.afk.kemonos.profile.api.domain.favoriteProfiles.FavoriteSortedType
import su.afk.kemonos.profile.domain.favorites.creator.GetFavoriteArtistsPagingUseCase
import su.afk.kemonos.profile.domain.favorites.fresh.IFreshFavoriteArtistsUpdatesUseCase
import su.afk.kemonos.profile.presenter.favoriteProfiles.FavoriteProfilesState.*
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
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
        loadSelectedSite()
        observeUiSetting()
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navManager.back()

            is Event.QueryChanged -> {
                setState { copy(searchQuery = event.value) }
                searchQueryFlow.value = event.value
            }

            is Event.ServiceSelected -> {
                setState { copy(selectedService = event.value) }
                requestPaging()
            }

            is Event.SortSelected -> {
                setState { copy(sortedType = event.value) }
                saveSortedType(event.value)
                requestPaging()
            }

            Event.ToggleSortOrder -> {
                val newSortAscending = !currentState.sortAscending
                setState { copy(sortAscending = newSortAscending) }
                saveSortAscending(newSortAscending)
                requestPaging()
            }

            Event.Refresh -> load(refresh = true)
            Event.Retry -> load(refresh = true)

            is Event.CreatorClicked -> onCreatorClick(event.creator)
        }
    }

    /** Подписка на UI-настройки (вид карточек, формат даты и т.д.). */
    private fun observeUiSetting() {
        uiSetting.prefs
            .distinctUntilChanged()
            .onEach { model -> setState { copy(uiSettingModel = model) } }
            .launchIn(viewModelScope)
    }

    /** Запускает observe-пайплайн поиска только один раз за lifecycle VM. */
    private fun startObserveSearch() {
        if (observeJob != null) return

        observeJob = observeSearch()
    }

    @OptIn(FlowPreview::class)
    /** Debounce + trim + distinct для поиска, после чего пересоздаем paging flow. */
    private fun observeSearch(): Job {
        return searchQueryFlow
            .debounce(500L)
            .map { it.trim() }
            .distinctUntilChanged()
            .onEach { q ->
                setState { copy(searchQuery = q) }
                requestPaging()
            }
            .launchIn(viewModelScope)
    }

    /**
     * Инициализирует сайт/фильтры и запускает первичный refresh+paging.
     * Если сайт не передали через навигацию, используем defaultSite из настроек.
     */
    private fun loadSelectedSite() = viewModelScope.launch {
        val site = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE)
            ?: uiSetting.prefs.first().siteDisplayMode.defaultSite

        selectedSiteUseCase.setSiteAndAwait(site)

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

    /** Пересоздает Flow пейджинга из локального Room с текущими фильтрами/sort. */
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

    /** Обновляет кэш из сети и синхронизирует UI-метаданные (services + freshSet). */
    private fun load(refresh: Boolean) = viewModelScope.launch {
        if (currentState.loading || currentState.refreshing) return@launch

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

    private fun onCreatorClick(creator: FavoriteArtist) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = creator.service,
                id = creator.id,
            )
        )
    }
}
