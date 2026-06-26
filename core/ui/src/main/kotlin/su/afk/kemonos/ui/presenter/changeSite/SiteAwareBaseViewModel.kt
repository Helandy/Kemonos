package su.afk.kemonos.ui.presenter.changeSite

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

abstract class SiteAwareBaseViewModelNew<S : UiState, E : UiEvent, F : UiEffect>(
    savedStateHandle: SavedStateHandle
) : BaseViewModelNew<S, E, F>(savedStateHandle) {

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

    private suspend fun onSiteLoaded(site: SelectedSite) = loadInitialSite(site)
    private suspend fun onSiteChanged(site: SelectedSite) = reloadSite(site)

    protected open suspend fun loadInitialSite(site: SelectedSite) = reloadSite(site)

    protected abstract suspend fun reloadSite(site: SelectedSite)

    protected fun ensureSiteEnabled(enabledSites: List<SelectedSite>) {
        val sites = enabledSites.distinct().ifEmpty { UiSettingModel.SELECTED_SITE_ORDER }
        if (_site.value in sites) return

        viewModelScope.launch {
            selectedSiteUseCase.setSite(sites.first())
        }
    }

    fun switchSite(visibleSites: List<SelectedSite> = UiSettingModel.SELECTED_SITE_ORDER) {
        if (_siteSwitching.value) return

        viewModelScope.launch {
            val current = _site.value
            val sites = visibleSites.distinct().ifEmpty { UiSettingModel.SELECTED_SITE_ORDER }
            val currentIndex = sites.indexOf(current)
            val newSite = if (currentIndex == -1) {
                sites.first()
            } else {
                sites[(currentIndex + 1) % sites.size]
            }
            selectedSiteUseCase.setSite(newSite)
        }
    }
}
