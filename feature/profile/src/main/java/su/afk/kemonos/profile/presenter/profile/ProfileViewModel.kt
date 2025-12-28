package su.afk.kemonos.profile.presenter.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.core.auth.ClearAuthUseCase
import su.afk.kemonos.core.auth.ObserveAuthStateUseCase
import su.afk.kemonos.core.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.core.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.core.storage.NavigationStorage
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.profile.presenter.profile.delegate.LogoutDelegate
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val getCoomerRootUrlUseCase: GetCoomerRootUrlUseCase,
    private val getKemonoRootUrlUseCase: GetKemonoRootUrlUseCase,
    private val clearAuthUseCase: ClearAuthUseCase,
    private val logoutDelegate: LogoutDelegate,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<ProfileState>(ProfileState()) {

    override fun onRetry() {
        observeAuth()
    }

    init {
        observeAuth()
        observeUrls()
        getAppVersion()
    }

    /** Проверка авторизации */
    private fun observeAuth() = viewModelScope.launch {
        observeAuthStateUseCase().distinctUntilChanged().collect { auth ->
            val isKemono = auth.isKemonoAuthorized
            val isCoomer = auth.isCoomerAuthorized

            setState {
                copy(
                    isLoading = false,
                    isLoginKemono = isKemono,
                    isLoginCoomer = isCoomer,
                    isLogin = isKemono || isCoomer,
                    kemonoLogin = auth.kemono.user,
                    coomerLogin = auth.coomer.user,
                )
            }
        }
    }

    /** Получение версии */
    fun getAppVersion() {
        setState {
            copy(
                appVersion = BuildConfig.VERSION_NAME
            )
        }
    }

    /** Выйти */
    fun onLogoutClick(site: SelectedSite) = logoutDelegate.onLogoutClick(
        site = site,
        updateState = { reducer -> setState(reducer) }
    )

    fun onLogoutConfirm() = logoutDelegate.onLogoutConfirm(
        scope = viewModelScope,
        getState = { state.value },
        updateState = { reducer -> setState(reducer) }
    )

    fun onLogoutDismiss() = logoutDelegate.onLogoutDismiss(
        updateState = { reducer -> setState(reducer) }
    )


    /** Актуальные урлы на сайт */
    private fun observeUrls() {
        setState {
            copy(
                kemonoUrl = getKemonoRootUrlUseCase(),
                coomerUrl = getCoomerRootUrlUseCase()
            )
        }
    }

    /** Логин */
    fun onLoginClick(site: SelectedSite) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(AuthDest.Login)
    }

    /** Любимые профили */
    fun onFavoriteProfilesNavigate(site: SelectedSite) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(AuthDest.FavoriteProfiles)
    }

    /** Любимые посты */
    fun onFavoritePostNavigate(site: SelectedSite) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(AuthDest.FavoritePosts)
    }

    fun onKeysClick() {
    }

    fun onReviewDMsClick() {
    }

    fun onExportFavoritesClick() {
    }

    fun onChangeUsernameClick() {
    }

    fun onChangePasswordClick() {
    }
}