package su.afk.kemonos.profile.presenter.login

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.profile.domain.login.LoginPasswordErrorCode
import su.afk.kemonos.profile.domain.login.LoginUsernameErrorCode
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class LoginState {
    data class State(
        val selectSite: SelectedSite = SelectedSite.K,

        val isLoading: Boolean = false,
        val error: ErrorItem? = null,

        val username: String = "",
        val password: String = "",

        val usernameError: LoginUsernameErrorCode? = null,
        val passwordError: LoginPasswordErrorCode? = null,

        val filledFromCredentialManager: Boolean = false,
    ) : UiState

    sealed interface Event : UiEvent {
        data object Back : Event

        data class UsernameChanged(val value: String) : Event
        data class PasswordChanged(val value: String) : Event
        data object LoginClick : Event
        data object NavigateToRegisterClick : Event
        data object RequestSavedCredentials : Event
    }

    sealed interface Effect : UiEffect {
        data object PickPassword : Effect
        data class SavePasswordAndNavigate(val username: String, val password: String) : Effect
        data object NavigateToProfile : Effect
    }
}
