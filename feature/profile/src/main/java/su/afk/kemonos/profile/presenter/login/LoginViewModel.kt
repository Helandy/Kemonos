package su.afk.kemonos.profile.presenter.login

import android.app.Activity
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.domain.login.LoginResult
import su.afk.kemonos.profile.domain.login.LoginUseCase
import su.afk.kemonos.profile.navigation.AuthDest
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
) : BaseViewModel<LoginState>(LoginState()) {

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

    fun onUsernameChange(value: String) {
        setState {
            copy(
                username = value,
                usernameError = null,
            )
        }
    }

    fun onPasswordChange(value: String) {
        setState {
            copy(
                password = value,
                passwordError = null,
            )
        }
    }

    fun onLoginClick() = viewModelScope.launch {
        setState { copy(isLoading = true, error = null) }

        when (val result = loginUseCase(
            username = state.value.username,
            password = state.value.password,
        )) {
            is LoginResult.Success -> {
                setState {
                    copy(
                        isLoading = false,
                    )
                }

                _effect.trySend(LoginEffect.SavePassword(state.value.username, state.value.password))

                navigationManager.popBackTo(AuthDest.Profile)
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
    fun onNavigateToRegisterClick() {
        navigationStorage.put(KEY_SELECT_SITE, state.value.selectSite)
        navigationManager.replace(AuthDest.Register)
    }

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun pickPassword(activity: Activity) = viewModelScope.launch {
        runCatching { credentialStore.pickPassword(activity) }
            .onSuccess { cred ->
                if (cred == null) return@onSuccess
                setState {
                    copy(
                        username = cred.id,
                        password = cred.password,
                        usernameError = null,
                        passwordError = null,
                        error = null
                    )
                }
            }
            .onFailure { e ->
                when (e) {
                    is NoCredentialException -> Unit
                    is GetCredentialException -> Unit
                    else -> Unit
                }
            }
    }

    fun savePassword(activity: Activity, username: String, password: String) = viewModelScope.launch {
        runCatching { credentialStore.savePassword(activity, username, password) }
    }

    /** Использовать существуюющие креды */
    fun requestSavedCredentials() {
        if (credentialsRequested) return
        if (currentState.username.isNotBlank() || currentState.password.isNotBlank()) return

        credentialsRequested = true
        _effect.trySend(LoginEffect.PickPassword)
    }
}