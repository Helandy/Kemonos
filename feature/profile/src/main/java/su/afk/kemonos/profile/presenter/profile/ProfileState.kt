package su.afk.kemonos.profile.presenter.profile

import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.profile.api.model.Login

internal class ProfileState {
    data class State(
        val isLoading: Boolean = true,

        val isLoginKemono: Boolean = false,
        val isLoginCoomer: Boolean = false,
        val isLogin: Boolean = false,

        val kemonoLogin: Login? = null,
        val coomerLogin: Login? = null,

        val showLogoutConfirm: Boolean = false,
        val logoutSite: SelectedSite? = null,

        val kemonoUpdatedFavoritesCount: Int = 0,
        val coomerUpdatedFavoritesCount: Int = 0,

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class LogoutClick(val site: SelectedSite) : Event
        data object LogoutConfirm : Event
        data object LogoutDismiss : Event
        data class LoginClick(val site: SelectedSite) : Event
        data class FavoriteProfilesNavigate(val site: SelectedSite) : Event
        data class FavoritePostNavigate(val site: SelectedSite) : Event
        data object NavigateToDownloads : Event
        data object NavigateToSettings : Event
        data object KeysClick : Event
        data object ReviewDMsClick : Event
        data object ExportFavoritesClick : Event
        data object ChangeUsernameClick : Event
        data object ChangePasswordClick : Event
    }

    sealed interface Effect : UiEffect
}
