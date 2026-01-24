package su.afk.kemonos.common.presenter.changeSite

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.kemonos.common.presenter.baseViewModel.*
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase

abstract class SiteAwareBaseViewModelNew<S : UiState, E : UiEvent, F : UiEffect> :
    BaseViewModelNew<S, E, F>() {

    protected abstract val selectedSiteUseCase: ISelectedSiteUseCase

    private val _site = MutableStateFlow(SelectedSite.K)
    val site: StateFlow<SelectedSite> = _site.asStateFlow()

    private val _siteSwitching = MutableStateFlow(false)
    val siteSwitching: StateFlow<Boolean> = _siteSwitching.asStateFlow()

    private var siteInitialized = false
    private var observeJob: Job? = null

    protected fun initSiteAware() {
        if (observeJob != null) return

        observeJob = viewModelScope.launch {
            selectedSiteUseCase.selectedSite.collectLatest { newSite ->
                val wasInitialized = siteInitialized
                siteInitialized = true

                _site.value = newSite
                _siteSwitching.value = true
                try {
                    if (!wasInitialized) onSiteLoaded(newSite)
                    else onSiteChanged(newSite)
                } finally {
                    _siteSwitching.value = false
                }
            }
        }
    }

    protected open suspend fun onSiteLoaded(site: SelectedSite) = reloadSite(site)
    protected open suspend fun onSiteChanged(site: SelectedSite) = reloadSite(site)

    protected abstract suspend fun reloadSite(site: SelectedSite)

    fun switchSite() {
        if (_siteSwitching.value) return

        viewModelScope.launch {
            val current = _site.value
            val newSite = if (current == SelectedSite.K) SelectedSite.C else SelectedSite.K
            selectedSiteUseCase.setSite(newSite)
        }
    }
}

abstract class SiteAwareBaseViewModel<S>(
    initialState: S,
) : BaseViewModel<S>(initialState) {

    protected abstract val selectedSiteUseCase: ISelectedSiteUseCase

    private val _site = MutableStateFlow(SelectedSite.K)
    val site: StateFlow<SelectedSite> = _site

    private val _siteSwitching = MutableStateFlow(false)
    val siteSwitching: StateFlow<Boolean> = _siteSwitching

    private var siteInitialized = false
    private var observeJob: Job? = null

    protected fun initSiteAware() {
        if (observeJob != null) return

        observeJob = viewModelScope.launch {
            selectedSiteUseCase.selectedSite
                .collectLatest { newSite ->
                    val wasInitialized = siteInitialized
                    siteInitialized = true

                    _site.value = newSite
                    /** true пока перезагрузка не закончится */
                    _siteSwitching.value = true
                    try {
                        if (!wasInitialized) onSiteLoaded(newSite)
                        else onSiteChanged(newSite)
                    } finally {
                        _siteSwitching.value = false
                    }
                }
        }
    }

    protected open suspend fun onSiteLoaded(site: SelectedSite) = reloadSite(site)
    protected open suspend fun onSiteChanged(site: SelectedSite) = reloadSite(site)

    protected abstract suspend fun reloadSite(site: SelectedSite)

    fun switchSite() {
        if (_siteSwitching.value) return

        viewModelScope.launch {
            val current = site.value
            val newSite = if (current == SelectedSite.K) SelectedSite.C else SelectedSite.K
            selectedSiteUseCase.setSite(newSite)
        }
    }
}