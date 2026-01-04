package su.afk.kemonos.profile.presenter.profile

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import su.afk.kemonos.auth.ObserveAuthStateUseCase
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.NavigationStorage
import su.afk.kemonos.profile.data.FreshFavoriteArtistsUpdates
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.profile.presenter.profile.delegate.LogoutDelegate
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val logoutDelegate: LogoutDelegate,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<ProfileState>(ProfileState()) {

    override fun onRetry() {
        observeAuth()
    }

    init {
        observeAuth()
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

    /** Настройки */
    fun navigateToSettings() = navigationManager.navigate(AuthDest.Setting)

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