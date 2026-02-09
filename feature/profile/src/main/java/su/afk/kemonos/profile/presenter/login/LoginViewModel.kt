package su.afk.kemonos.profile.presenter.login

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
import su.afk.kemonos.profile.domain.login.LoginResult
import su.afk.kemonos.profile.domain.login.LoginUseCase
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.profile.presenter.login.LoginState.*
import su.afk.kemonos.profile.utils.AppCredentialStore
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    private val credentialStore: AppCredentialStore,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    private var credentialsRequested = false

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
                        filledFromCredentialManager = false
                    )
                }
            }

            is Event.PasswordChanged -> {
                setState {
                    copy(
                        password = event.value,
                        passwordError = null,
                        filledFromCredentialManager = false
                    )
                }
            }

            Event.LoginClick -> onLoginClick()
            Event.NavigateToRegisterClick -> onNavigateToRegisterClick()
            Event.RequestSavedCredentials -> requestSavedCredentials()
        }
    }

    private fun onLoginClick() = viewModelScope.launch {
        if (currentState.isLoading) return@launch

        setState { copy(isLoading = true, error = null) }

        when (
            val result = loginUseCase(
                username = currentState.username,
                password = currentState.password,
            )
        ) {
            is LoginResult.Success -> {
                setState { copy(isLoading = false) }

                val shouldAskToSave = !currentState.filledFromCredentialManager
                if (shouldAskToSave) {
                    setEffect(
                        Effect.SavePasswordAndNavigate(
                            username = currentState.username,
                            password = currentState.password
                        )
                    )
                } else {
                    setEffect(Effect.NavigateToProfile)
                }
            }

            is LoginResult.ValidationError -> {
                setState {
                    copy(
                        isLoading = false,
                        usernameError = result.usernameError,
                        passwordError = result.passwordError,
                    )
                }
            }

            is LoginResult.Error -> {
                setState {
                    copy(
                        isLoading = false,
                        error = result.error,
                    )
                }
            }
        }
    }

    /** навигация на регистрацию */
    private fun onNavigateToRegisterClick() {
        navigationStorage.put(KEY_SELECT_SITE, currentState.selectSite)
        navigationManager.replace(AuthDest.Register)
    }

    suspend fun savePassword(activity: Activity, username: String, password: String) {
        credentialStore.savePassword(activity, username, password)
    }

    fun navigateAfterLogin() {
        navigationManager.popBackTo(AuthDest.Profile)
    }

    fun pickPassword(activity: Activity) = viewModelScope.launch {
        val cred = runCatching { credentialStore.pickPassword(activity) }.getOrNull() ?: return@launch

        setState {
            copy(
                username = cred.id,
                password = cred.password,
                filledFromCredentialManager = true,
                usernameError = null,
                passwordError = null,
                error = null
            )
        }

        onLoginClick()
    }

    /** Использовать существуюющие креды */
    private fun requestSavedCredentials() {
        if (credentialsRequested) return
        if (currentState.username.isNotBlank() || currentState.password.isNotBlank()) return
        credentialsRequested = true
        setEffect(Effect.PickPassword)
    }
}
