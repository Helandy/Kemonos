package su.afk.kemonos.main.presenter

import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class StartCheckState {

    data class State(
        val isLoading: Boolean = true,
        val enabledSites: Set<SelectedSite> = UiSettingModel.DEFAULT_ENABLED_SITES,

        val errors: Map<SelectedSite, ErrorItem> = emptyMap(),

        val apiSuccess: Boolean? = null,

        /** Значения из Prefs (текущие, «истинные») */
        val siteUrls: Map<SelectedSite, String> = emptyMap(),

        val inputDomains: Map<SelectedSite, String> = emptyMap(),

        val updateInfo: AppUpdateInfo? = null,
        val pendingCrashPath: String? = null,
    ) : UiState {

        fun error(site: SelectedSite): ErrorItem? = errors[site]
        fun siteUrl(site: SelectedSite): String = siteUrls[site].orEmpty()
        fun inputDomain(site: SelectedSite): String = inputDomains[site].orEmpty()
    }

    sealed interface Event : UiEvent {
        data class UpdateClick(val info: AppUpdateInfo) : Event
        data object UpdateLaterClick : Event
        data object SaveAndCheck : Event
        data object SkipCheck : Event
        data class InputDomainChanged(val site: SelectedSite, val value: String) : Event
        data class ToggleApiSite(val site: SelectedSite, val enabled: Boolean) : Event
        data object CrashReportDelete : Event
        data object CrashReportSaveToDevice : Event
        data class CrashReportShared(val path: String) : Event
        data class CrashReportShareFailed(val path: String) : Event
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
        data class SaveCrashReportToDevice(val path: String) : Effect
    }
}
