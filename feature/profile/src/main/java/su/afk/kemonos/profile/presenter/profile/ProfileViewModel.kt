package su.afk.kemonos.profile.presenter.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.auth.ObserveAuthStateUseCase
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.profile.presenter.profile.ProfileState.*
import su.afk.kemonos.profile.presenter.profile.delegate.LogoutDelegate
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val logoutDelegate: LogoutDelegate,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onRetry() {
        observeAuth()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.LogoutClick -> onLogoutClick(event.site)
            Event.LogoutConfirm -> onLogoutConfirm()
            Event.LogoutDismiss -> onLogoutDismiss()
            is Event.LoginClick -> onLoginClick(event.site)
            is Event.FavoriteProfilesNavigate -> onFavoriteProfilesNavigate(event.site)
            is Event.FavoritePostNavigate -> onFavoritePostNavigate(event.site)
            Event.NavigateToSettings -> navigateToSettings()
            Event.KeysClick -> onKeysClick()
            Event.ReviewDMsClick -> onReviewDMsClick()
            Event.ExportFavoritesClick -> onExportFavoritesClick()
            Event.ChangeUsernameClick -> onChangeUsernameClick()
            Event.ChangePasswordClick -> onChangePasswordClick()
        }
    }

    init {
        observeUiSetting()
        observeAuth()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    /** Проверка авторизации */
    private fun observeAuth() = viewModelScope.launch {
        observeAuthStateUseCase().distinctUntilChanged().collect { auth ->
            val isKemono = auth.isKemonoAuthorized
            val isCoomer = auth.isCoomerAuthorized

            val kCount = FreshFavoriteArtistsUpdates.get(SelectedSite.K).size
            val cCount = FreshFavoriteArtistsUpdates.get(SelectedSite.C).size

            setState {
                copy(
                    isLoading = false,
                    isLoginKemono = isKemono,
                    isLoginCoomer = isCoomer,
                    isLogin = isKemono || isCoomer,
                    kemonoLogin = auth.kemono.user,
                    coomerLogin = auth.coomer.user,
                    kemonoUpdatedFavoritesCount = kCount,
                    coomerUpdatedFavoritesCount = cCount,
                )
            }
        }
    }

    /** Выйти */
    private fun onLogoutClick(site: SelectedSite) = logoutDelegate.onLogoutClick(
        site = site,
        updateState = { reducer -> setState(reducer) }
    )

    private fun onLogoutConfirm() = logoutDelegate.onLogoutConfirm(
        scope = viewModelScope,
        getState = { currentState },
        updateState = { reducer -> setState(reducer) }
    )

    private fun onLogoutDismiss() = logoutDelegate.onLogoutDismiss(
        updateState = { reducer -> setState(reducer) }
    )

    /** Логин */
    private fun onLoginClick(site: SelectedSite) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(AuthDest.Login)
    }

    /** Любимые профили */
    private fun onFavoriteProfilesNavigate(site: SelectedSite) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(AuthDest.FavoriteProfiles)
    }

    /** Любимые посты */
    private fun onFavoritePostNavigate(site: SelectedSite) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(AuthDest.FavoritePosts)
    }

    /** Настройки */
    private fun navigateToSettings() = navigationManager.navigate(AuthDest.Setting)

    private fun onKeysClick() {
    }

    private fun onReviewDMsClick() {
    }

    private fun onExportFavoritesClick() {
    }

    private fun onChangeUsernameClick() {
    }

    private fun onChangePasswordClick() {
    }
}
