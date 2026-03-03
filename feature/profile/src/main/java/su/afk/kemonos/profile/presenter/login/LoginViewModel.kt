package su.afk.kemonos.profile.presenter.login

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.domain.login.LoginResult
import su.afk.kemonos.profile.domain.login.LoginUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.login.LoginState.*
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    /** Начальное состояние экрана логина. */
    override fun createInitialState(): State = State()

    private var credentialsRequested = false
    private var pendingNavigateAfterPasswordSave = false

    /** Очищает отображаемую ошибку после retry из общего error-слоя. */
    override fun onRetry() {
        super.onRetry()
        setState { copy(isLoading = false, error = null) }
    }

    /** Инициализирует текущий сайт из navigation storage и синхронизирует его в preferences. */
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

    /** Центральная обработка UI-событий экрана логина. */
    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navigationManager.back()

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
            Event.NavigateToProfile -> navigateAfterLogin()
            Event.RequestSavedCredentials -> requestSavedCredentials()
            is Event.CredentialsPicked -> onCredentialsPicked(event.username, event.password)
            Event.PasswordSaveFinished -> onPasswordSaveFinished()
        }
    }

    /** Запускает логин: валидация, сетевой запрос и дальнейшая навигация/сохранение credentials. */
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
                    pendingNavigateAfterPasswordSave = true
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

    /** Переход на экран регистрации с сохранением выбранного сайта. */
    private fun onNavigateToRegisterClick() {
        navigationStorage.put(KEY_SELECT_SITE, currentState.selectSite)
        navigationManager.replace(AuthDestination.Register)
    }

    /** Завершает auth-flow и возвращает пользователя на профиль. */
    private fun navigateAfterLogin() {
        navigationManager.popBackTo(AuthDestination.Profile)
    }

    /** Принимает выбранные системным credential manager данные и инициирует повторный вход. */
    private fun onCredentialsPicked(username: String, password: String) {
        setState {
            copy(
                username = username,
                password = password,
                filledFromCredentialManager = true,
                usernameError = null,
                passwordError = null,
                error = null
            )
        }
        onLoginClick()
    }

    /** Завершает навигацию после попытки сохранения пароля, если она действительно ожидалась. */
    private fun onPasswordSaveFinished() {
        if (!pendingNavigateAfterPasswordSave) return
        pendingNavigateAfterPasswordSave = false
        navigateAfterLogin()
    }

    /** Однократно запрашивает подсказку сохраненных credentials для пустой формы. */
    private fun requestSavedCredentials() {
        if (credentialsRequested) return
        if (currentState.username.isNotBlank() || currentState.password.isNotBlank()) return
        credentialsRequested = true
        setEffect(Effect.PickPassword)
    }
}
