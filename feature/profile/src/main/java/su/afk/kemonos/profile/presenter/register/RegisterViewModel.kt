package su.afk.kemonos.profile.presenter.register

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.profile.domain.register.RegisterResult
import su.afk.kemonos.profile.domain.register.RegisterUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.register.RegisterState.*
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val selectedSiteProvider: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    /** Начальное состояние экрана регистрации. */
    override fun createInitialState(): State = State()

    private var pendingNavigateToLogin = false

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

    /** Центральная обработка UI-событий экрана регистрации. */
    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navigationManager.back()

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

    /** Запускает регистрацию: локальная валидация, сетевой вызов и эффект сохранения credentials. */
    private fun onRegisterClick() {
        viewModelScope.launch {
            if (currentState.isLoading) return@launch

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

    /** Явный переход на экран логина с сохранением выбранного сайта. */
    private fun onNavigateToLoginClick() {
        navigationStorage.put(KEY_SELECT_SITE, currentState.selectSite)
        navigationManager.replace(AuthDestination.Login)
    }

    /** Завершает post-register flow после попытки сохранения пароля. */
    private fun onPasswordSaveFinished() {
        if (!pendingNavigateToLogin) return
        pendingNavigateToLogin = false
        navigationStorage.put(KEY_SELECT_SITE, currentState.selectSite)
        navigationManager.replace(AuthDestination.Login)
    }
}
