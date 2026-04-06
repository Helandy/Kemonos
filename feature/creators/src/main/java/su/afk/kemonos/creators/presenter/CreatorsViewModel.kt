package su.afk.kemonos.creators.presenter

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.constants.Constant.SEARCH_DELAY_MILLIS
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.domain.random.RandomCreatorUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.creators.presenter.delegates.CreatorsListDelegate
import su.afk.kemonos.creators.presenter.delegates.RandomListDelegate
import su.afk.kemonos.creators.presenter.delegates.VideoInfoDomainStatusDelegate
import su.afk.kemonos.creators.presenter.model.CreatorsFilters
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class CreatorsViewModel @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val randomCreatorUseCase: RandomCreatorUseCase,
    private val listDelegate: CreatorsListDelegate,
    private val randomListDelegate: RandomListDelegate,
    private val videoInfoDomainStatusDelegate: VideoInfoDomainStatusDelegate,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    private val creatorsFilters = MutableStateFlow(
        CreatorsFilters(
            service = null,
            query = "",
            sort = CreatorsSort.POPULARITY,
            ascending = false,
        )
    )
    private val creatorsPagingRefresh = MutableStateFlow(0)

    /** Обновить фильтры в creatorsFilters */
    private fun updateCreatorsFiltersFromState() {
        creatorsFilters.value = CreatorsFilters(
            service = currentState.selectedServiceFilter,
            query = currentState.searchQuery,
            sort = currentState.sortedType,
            ascending = currentState.sortAscending,
        )
    }

    private fun requestScrollToTop() {
        setEffect(Effect.ScrollToTop)
    }

    private fun requestCreatorsPagingRefresh() {
        creatorsPagingRefresh.update { it + 1 }
    }

    /** Получить paging flow */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun bindCreatorsPaging() {
        val creatorsPagedFlow = combine(
            creatorsFilters,
            site,
            creatorsPagingRefresh,
        ) { filters, _, _ -> filters }
            .flatMapLatest { filters ->
                listDelegate.creatorsPagedFlow(
                    service = filters.service,
                    searchQuery = filters.query,
                    sortedType = filters.sort,
                    sortAscending = filters.ascending,
                )
            }
            .cachedIn(viewModelScope)

        setState { copy(creatorsPaged = creatorsPagedFlow) }
    }

    private var searchDebounceJob: Job? = null
    private var videoInfoDomainObserveStarted = false
    private var siteInitializedFromSettings = false
    private var lastDefaultSite: SelectedSite? = null

    override fun createInitialState(): State = State()

    override fun onRetry() {
        viewModelScope.launch { initAndReloadSite() }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        setState {
            copy(
                selectedService = CreatorsState.ALL_SERVICES_LABEL,
                selectedServiceFilter = null,
                searchQuery = "",
                sortedType = CreatorsSort.POPULARITY,
                sortAscending = false,
            )
        }

        initAndReloadSite()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged().onEach { model ->
            if (!siteInitializedFromSettings) {
                siteInitializedFromSettings = true
                lastDefaultSite = model.defaultSite
                viewModelScope.launch {
                    selectedSiteUseCase.setSite(model.defaultSite)
                }
            } else if (model.defaultSite != lastDefaultSite) {
                lastDefaultSite = model.defaultSite
                viewModelScope.launch {
                    selectedSiteUseCase.setSite(model.defaultSite)
                }
            }

            if (model.creatorsGithubRateBannerInstallTsMs == 0L) {
                val now = System.currentTimeMillis()
                uiSetting.setCreatorsGithubRateBannerInstallTsMs(now)

                val modelWithInstallTs = model.copy(creatorsGithubRateBannerInstallTsMs = now)
                setState {
                    copy(
                        uiSettingModel = modelWithInstallTs,
                        showGithubRateBanner = shouldShowGithubRateBanner(modelWithInstallTs),
                    )
                }
                return@onEach
            }

            setState {
                copy(
                    uiSettingModel = model,
                    showGithubRateBanner = shouldShowGithubRateBanner(model),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observeVideoInfoDomainStatus() {
        if (videoInfoDomainObserveStarted) return
        videoInfoDomainObserveStarted = true

        uiSetting.prefs
            .map { it.videoPreviewServerUrl }
            .distinctUntilChanged()
            .onEach {
                val isAvailable = videoInfoDomainStatusDelegate.check()
                setState {
                    copy(
                        isVideoInfoDomainAvailable = isAvailable,
                        showVideoInfoDomainBanner = if (isVideoInfoDomainAvailable != false && !isAvailable) {
                            true
                        } else {
                            showVideoInfoDomainBanner
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    init {
        bindCreatorsPaging()
        observeUiSetting()
        observeVideoInfoDomainStatus()
        initSiteAware()
    }

    private suspend fun initAndReloadSite() {
        setState { copy(loading = true) }
        try {
            /** Проверка свежий ли кэш (если нет загружаем с сети) */
            getCreatorsPagedUseCase.checkFreshCache()

            /** Получение доступных сервисов из бд для фильтра */
            getAvailableServicesFilter()

            /** Список авторов */
            updateCreatorsFiltersFromState()
            requestCreatorsPagingRefresh()

            /** Подгрузка рандомных авторов */
            randomListDelegate.loadRandom(currentState, ::setState)
        } finally {
            setState { copy(loading = false) }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.QueryChanged -> {
                setState { copy(searchQuery = event.value) }
                searchDebounceJob?.cancel()

                val trimmed = event.value.trim()
                if (trimmed.isEmpty()) {
                    updateCreatorsFiltersFromState()
                } else if (trimmed.length >= 2) {
                    searchDebounceJob = viewModelScope.launch {
                        delay(SEARCH_DELAY_MILLIS)
                        updateCreatorsFiltersFromState()
                    }
                }

                /** Поиск в для рандомных авторов */
                randomListDelegate.applyFilterToRandomCreators(
                    query = event.value,
                    state = { state.value },
                    setState = ::setState,
                )
                requestScrollToTop()
            }

            is Event.ServiceSelected -> {
                setState {
                    copy(
                        selectedService = event.value,
                        selectedServiceFilter = event.value.takeUnless { it == CreatorsState.ALL_SERVICES_LABEL }
                    )
                }

                updateCreatorsFiltersFromState()
                requestScrollToTop()
            }

            is Event.SortSelected -> {
                setState { copy(sortedType = event.value) }
                updateCreatorsFiltersFromState()
                requestScrollToTop()
            }

            Event.ToggleSortOrder -> {
                setState { copy(sortAscending = !state.value.sortAscending) }
                updateCreatorsFiltersFromState()
                requestScrollToTop()
            }

            is Event.CreatorClicked -> onCreatorClick(event.creator)
            Event.RandomClicked -> randomCreator()
            Event.SwitchSiteClicked -> switchSite()
            Event.HeaderRandomExpanded -> setState { copy(randomExpanded = !randomExpanded) }
            Event.GithubRateClick -> onGithubRateClick()
            Event.HideGithubRateBanner -> onHideGithubRateBanner()
            Event.HideVideoInfoDomainBanner -> onHideVideoInfoDomainBanner()
        }
    }

    /** Получение доступных сервисов из бд для фильтра */
    private suspend fun getAvailableServicesFilter() {
        val services = getCreatorsPagedUseCase.getServices()
        val list = listOf(CreatorsState.ALL_SERVICES_LABEL) + services

        setState {
            copy(
                services = list,
            )
        }
    }

    /** Открыть автора */
    private fun onCreatorClick(creator: FavoriteArtist) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = creator.service,
                id = creator.id,
            )
        )
    }

    /** Получить случайного автора */
    private fun randomCreator() = viewModelScope.launch {
        setState { copy(randomExpanded = true) }

        try {
            val creator = randomCreatorUseCase()

            navManager.navigate(
                creatorProfileNavigator.getCreatorProfileDest(
                    service = creator.service,
                    id = creator.artistId,
                )
            )
        } finally {
            setState { copy(randomExpanded = false) }
        }
    }

    private fun onGithubRateClick() {
        setEffect(Effect.OpenUrl(GITHUB_PROJECT_URL))
    }

    private fun onHideGithubRateBanner() = viewModelScope.launch {
        uiSetting.setCreatorsGithubRateBannerDisabled(true)
        setState { copy(showGithubRateBanner = false) }
    }

    private fun onHideVideoInfoDomainBanner() {
        setState { copy(showVideoInfoDomainBanner = false) }
    }

    private fun shouldShowGithubRateBanner(model: UiSettingModel): Boolean {
        if (model.creatorsGithubRateBannerDisabled) return false

        val now = System.currentTimeMillis()
        return (now - model.creatorsGithubRateBannerInstallTsMs) >= GITHUB_RATE_BANNER_DELAY_MS
    }

    private companion object {
        const val GITHUB_RATE_BANNER_DELAY_MS = 7L * 24L * 60L * 60L * 1000L
        const val GITHUB_PROJECT_URL = "https://github.com/Helandy/Kemonos"
    }
}
