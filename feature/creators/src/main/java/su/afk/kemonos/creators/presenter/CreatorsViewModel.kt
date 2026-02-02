package su.afk.kemonos.creators.presenter

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModelNew
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.creators.domain.GetCreatorsPagedUseCase
import su.afk.kemonos.creators.domain.RandomCreatorUseCase
import su.afk.kemonos.creators.presenter.CreatorsState.*
import su.afk.kemonos.creators.presenter.delegates.CreatorsListDelegate
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.CreatorsSort
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

@HiltViewModel
internal class CreatorsViewModel @Inject constructor(
    private val getCreatorsPagedUseCase: GetCreatorsPagedUseCase,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val randomCreatorUseCase: RandomCreatorUseCase,
    private val listDelegate: CreatorsListDelegate,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {


    override fun createInitialState(): State = State()

    init {
        listDelegate.observeUiSetting(viewModelScope, ::setState)
        initSiteAware()

        viewModelScope.launch {
            loadServicesOnce()
            listDelegate.rebuildPaging(viewModelScope, { state.value }, ::setState, ::setEffect, scrollToTop = false)

            listDelegate.loadRandom(viewModelScope, { state.value }, ::setState, ::setEffect)
        }
    }


    override fun onRetry() {
        viewModelScope.launch { ensureFreshAndReloadServices() }
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

        loadServicesOnce()

        listDelegate.rebuildPaging(viewModelScope, { state.value }, ::setState, ::setEffect, scrollToTop = true)

        // ✅ random перезагружаем только тут (смена сайта)
        listDelegate.loadRandom(viewModelScope, { state.value }, ::setState, ::setEffect)

        ensureFreshAndReloadServices()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.QueryChanged -> listDelegate.updateSearch(
                scope = viewModelScope,
                state = { state.value },
                setState = ::setState,
                setEffect = ::setEffect,
                query = event.value,
            )

            is Event.ServiceSelected -> {
                setState { copy(selectedService = event.value) }
                listDelegate.rebuildPaging(
                    scope = viewModelScope,
                    state = { state.value },
                    setState = ::setState,
                    setEffect = ::setEffect,
                    scrollToTop = true,
                )
                // random НЕ трогаем (по твоему требованию)
                listDelegate.applyRandomFilter(
                    query = state.value.searchQuery,
                    state = { state.value },
                    setState = ::setState,
                )
            }

            is Event.SortSelected -> {
                setState { copy(sortedType = event.value) }
                listDelegate.rebuildPaging(viewModelScope, { state.value }, ::setState, ::setEffect, scrollToTop = true)
            }

            Event.ToggleSortOrder -> {
                setState { copy(sortAscending = !state.value.sortAscending) }
                listDelegate.rebuildPaging(viewModelScope, { state.value }, ::setState, ::setEffect, scrollToTop = true)
            }

            is Event.CreatorClicked -> onCreatorClick(event.creator)
            Event.RandomClicked -> randomCreator()
            Event.SwitchSiteClicked -> switchSite()
        }
    }

    private suspend fun ensureFreshAndReloadServices() {
        loadServicesOnce()
        val updated = ensureFresh()
        if (updated) {
            loadServicesOnce()
            listDelegate.rebuildPaging(
                scope = viewModelScope,
                state = { state.value },
                setState = ::setState,
                setEffect = ::setEffect,
                scrollToTop = true,
            )
        }
    }

    private suspend fun loadServicesOnce() {
        val services = getCreatorsPagedUseCase.getServices()

        setState {
            val firstReal = services.firstOrNull().orEmpty()
            val newSelected =
                if (selectedService == "Services" && firstReal.isNotBlank()) firstReal
                else selectedService

            copy(
                services = services,
                selectedService = newSelected
            )
        }
    }

    private suspend fun ensureFresh(): Boolean {
        return try {
            setState { copy(refreshing = true) }
            getCreatorsPagedUseCase.ensureFresh()
        } finally {
            setState { copy(refreshing = false) }
        }
    }

    private fun onCreatorClick(creator: FavoriteArtist) = viewModelScope.launch {
        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = creator.service,
                id = creator.id,
            )
        )
    }

    private fun randomCreator() = viewModelScope.launch {
        try {
            setState { copy(loading = true) }
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