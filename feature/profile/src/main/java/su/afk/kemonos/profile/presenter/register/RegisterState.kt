package su.afk.kemonos.profile.presenter.register

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.profile.domain.register.ConfirmErrorCode
import su.afk.kemonos.profile.domain.register.PasswordErrorCode
import su.afk.kemonos.profile.domain.register.UsernameErrorCode
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class RegisterState {
    data class State(
        val selectSite: SelectedSite = SelectedSite.K,

        val isLoading: Boolean = false,
        val error: ErrorItem? = null,

        val username: String = "",
        val password: String = "",
        val confirm: String = "",

        val usernameError: UsernameErrorCode? = null,
        val passwordError: PasswordErrorCode? = null,
        val confirmError: ConfirmErrorCode? = null,
    ) : UiState

    sealed interface Event : UiEvent {
        data object Back : Event

        data class UsernameChanged(val value: String) : Event
        data class PasswordChanged(val value: String) : Event
        data class ConfirmChanged(val value: String) : Event
        data object RegisterClick : Event
        data object NavigateToLoginClick : Event
        data object PasswordSaveFinished : Event
    }

    sealed interface Effect : UiEffect {
        data class SavePassword(val username: String, val password: String) : Effect
    }
}
