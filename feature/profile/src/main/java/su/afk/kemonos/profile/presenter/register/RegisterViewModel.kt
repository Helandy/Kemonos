package su.afk.kemonos.profile.presenter.register

import android.app.Activity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.domain.register.RegisterResult
import su.afk.kemonos.profile.domain.register.RegisterUseCase
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.profile.presenter.register.RegisterState.*
import su.afk.kemonos.profile.utils.AppCredentialStore
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    private val credentialStore: AppCredentialStore,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    private var pendingNavigateToLogin = false

    override fun onRetry() {
        super.onRetry()
        setState { copy(isLoading = false, error = null) }
    }

    init {
        val selectSite = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE)

        if (selectSite != null) {
            viewModelScope.launch {
                selectedSiteProvider.setSite(selectSite)
            }
            setState { copy(selectSite = selectSite) }
        } else {
            setState {
                copy(
                    error = ErrorItem(
                        title = "Error",
                        message = "Couldn't identify the site (navigation error)"
                    )
                )
            }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.UsernameChanged -> {
                setState {
                    copy(
                        username = event.value,
                        usernameError = null,
                    )
                }
            }

            is Event.PasswordChanged -> {
                setState {
                    copy(
                        password = event.value,
                        passwordError = null,
                    )
                }
            }

            is Event.ConfirmChanged -> {
                setState {
                    copy(
                        confirm = event.value,
                        confirmError = null,
                    )
                }
            }

            Event.RegisterClick -> onRegisterClick()
            Event.NavigateToLoginClick -> onNavigateToLoginClick()
            Event.PasswordSaveFinished -> onPasswordSaveFinished()
        }
    }

    private fun onRegisterClick() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            when (val result = registerUseCase(
                username = currentState.username,
                password = currentState.password,
                confirm = currentState.confirm
            )) {
                is RegisterResult.Success -> {
                    setState { copy(isLoading = false) }

                    pendingNavigateToLogin = true
                    setEffect(Effect.SavePassword(currentState.username, currentState.password))
                }

                is RegisterResult.ValidationError -> {
                    setState {
                        copy(
                            isLoading = false,
                            usernameError = result.usernameError,
                            passwordError = result.passwordError,
                            confirmError = result.confirmError,
                        )
                    }
                }

                is RegisterResult.Error -> {
                    setState { copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    private fun onNavigateToLoginClick() {
        navigationStorage.put(KEY_SELECT_SITE, currentState.selectSite)
        navigationManager.replace(AuthDest.Login)
    }

    suspend fun savePassword(activity: Activity, username: String, password: String) {
        credentialStore.savePassword(activity, username, password)
    }

    /** Вызывается из UI когда сохранение (попытка) завершено */
    private fun onPasswordSaveFinished() {
        if (!pendingNavigateToLogin) return
        pendingNavigateToLogin = false

        navigationStorage.put(KEY_SELECT_SITE, currentState.selectSite)
        navigationManager.navigate(AuthDest.Login)
    }
}
