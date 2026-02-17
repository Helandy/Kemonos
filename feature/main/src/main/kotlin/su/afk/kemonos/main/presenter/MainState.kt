package su.afk.kemonos.main.presenter

import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class MainState {

    data class State(
        val isLoading: Boolean = true,

        val kemonoError: ErrorItem? = null,
        val coomerError: ErrorItem? = null,

        val apiSuccess: Boolean? = null,

        /** Значения из Prefs (текущие, «истинные») */
        val kemonoUrl: String = "",
        val coomerUrl: String = "",

        val inputKemonoDomain: String = "",
        val inputCoomerDomain: String = "",

        val updateInfo: AppUpdateInfo? = null,
        val pendingCrashPath: String? = null,
    ) : UiState

    sealed interface Event : UiEvent {
        data class UpdateClick(val info: AppUpdateInfo) : Event
        data object UpdateLaterClick : Event
        data object SaveAndCheck : Event
        data object SkipCheck : Event
        data class InputKemonoDomainChanged(val value: String) : Event
        data class InputCoomerDomainChanged(val value: String) : Event
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
