package su.afk.kemonos.creators.presenter

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModelNew
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.domain.RandomCreatorUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.creators.presenter.delegates.CreatorsListDelegate
import su.afk.kemonos.creators.presenter.delegates.RandomListDelegate
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import javax.inject.Inject

@HiltViewModel
internal class CreatorsViewModel @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val randomCreatorUseCase: RandomCreatorUseCase,
    private val listDelegate: CreatorsListDelegate,
    private val randomListDelegate: RandomListDelegate,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {
    override fun createInitialState(): State = State()

    override fun onRetry() {
        viewModelScope.launch { initAndReloadSite() }
    }

    override suspend fun reloadSite(site: SelectedSite) {
        setState {
            copy(
                selectedService = "Services",
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
            setState { copy(uiSettingModel = model) }
        }.launchIn(viewModelScope)
    }

    init {
        observeUiSetting()
        initSiteAware()
    }

    private suspend fun initAndReloadSite() {
        setState { copy(loading = true) }
        try {
            /** Проверка свежий ли кэш (если нет загружаем с сети) */
            checkFreshCache()

            /** Получение доступных сервисов из бд для фильтра */
            getAvailableServicesFilter()

            /** Список авторов */
            listDelegate.creatorsFilterPaging(
                viewModelScope,
                { state.value },
                ::setState,
                ::setEffect,
                scrollToTop = false
            )

            /** Подгрузка рандомных авторов */
            randomListDelegate.loadRandom(viewModelScope, currentState, ::setState, ::setEffect)
        } finally {
            setState { copy(loading = false) }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.QueryChanged -> {
                listDelegate.searchQuery(
                    scope = viewModelScope,
                    state = { state.value },
                    setState = ::setState,
                    setEffect = ::setEffect,
                    query = event.value,
                )

                /** Поиск в для рандомных авторов */
                randomListDelegate.applyFilterToRandomCreators(
                    query = event.value,
                    state = { state.value },
                    setState = ::setState,
                )
            }

            is Event.ServiceSelected -> {
                setState { copy(selectedService = event.value) }

                listDelegate.creatorsFilterPaging(
                    scope = viewModelScope,
                    state = { state.value },
                    setState = ::setState,
                    setEffect = ::setEffect,
                    scrollToTop = true,
                )
            }

            is Event.SortSelected -> {
                setState { copy(sortedType = event.value) }

                listDelegate.creatorsFilterPaging(
                    viewModelScope,
                    { state.value },
                    ::setState,
                    ::setEffect,
                    scrollToTop = true
                )
            }

            Event.ToggleSortOrder -> {
                setState { copy(sortAscending = !state.value.sortAscending) }

                listDelegate.creatorsFilterPaging(
                    viewModelScope,
                    { state.value },
                    ::setState,
                    ::setEffect,
                    scrollToTop = true
                )
            }

            is Event.CreatorClicked -> onCreatorClick(event.creator)
            Event.RandomClicked -> randomCreator()
            Event.SwitchSiteClicked -> switchSite()
            Event.ToggleRandomExpanded -> setState { copy(randomExpanded = !randomExpanded) }
        }
    }

    /** Проверка свежий ли кэш */
    private suspend fun checkFreshCache() {
        getCreatorsPagedUseCase.checkFreshCache()
    }

    /** Получение доступных сервисов из бд для фильтра */
    private suspend fun getAvailableServicesFilter() {
        val services = getCreatorsPagedUseCase.getServices()

        setState {
            copy(
                services = services,
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
        setState { copy(loading = true) }

        try {
            val creator = randomCreatorUseCase()

            navManager.navigate(
                creatorProfileNavigator.getCreatorProfileDest(
                    service = creator.service,
                    id = creator.artistId,
                )
            )
        } finally {
            setState { copy(loading = false) }
        }
    }
}
