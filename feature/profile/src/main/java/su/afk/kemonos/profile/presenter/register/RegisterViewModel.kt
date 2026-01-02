package su.afk.kemonos.profile.presenter.register

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.domain.register.RegisterResult
import su.afk.kemonos.profile.domain.register.RegisterUseCase
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<RegisterState>(RegisterState()) {

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

    fun onConfirmChange(value: String) {
        setState {
            copy(
                confirm = value,
                confirmError = null,
            )
        }
    }

    fun onRegisterClick() = viewModelScope.launch {
        setState { copy(isLoading = true, error = null) }

        when (val result = registerUseCase(
            username = state.value.username,
            password = state.value.password,
            confirm = state.value.confirm
        )) {
            is RegisterResult.Success -> {
                setState {
                    copy(
                        isLoading = false,
                    )
                }

                navigationStorage.put(KEY_SELECT_SITE, state.value.selectSite)
                navigationManager.navigate(AuthDest.Login)
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
                setState {
                    copy(
                        isLoading = false,
                        error = result.error,
                    )
                }
            }
        }
    }

    /** навигация на логин  */
    fun onNavigateToLoginClick() {
        navigationStorage.put(KEY_SELECT_SITE, state.value.selectSite)
        navigationManager.replace(AuthDest.Login)
    }
}