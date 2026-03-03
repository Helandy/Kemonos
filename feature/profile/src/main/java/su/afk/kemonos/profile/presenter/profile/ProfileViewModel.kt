package su.afk.kemonos.profile.presenter.profile

import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import su.afk.kemonos.auth.ObserveAuthStateUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.download.api.IDownloadNavigator
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.domain.favorites.fresh.IFreshFavoriteArtistsUpdatesUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.profile.ProfileState.*
import su.afk.kemonos.profile.presenter.profile.delegate.LogoutDelegate
import su.afk.kemonos.profile.presenter.profile.model.AuthSnapshot
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import su.afk.kemonos.setting.api.useCase.IGetSettingDestinationUseCase
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val downloadNavigator: IDownloadNavigator,
    private val getSettingDestinationUseCase: IGetSettingDestinationUseCase,
    private val logoutDelegate: LogoutDelegate,
    private val uiSetting: IUiSettingUseCase,
    private val freshUpdatesUseCase: IFreshFavoriteArtistsUpdatesUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()
    private var authObserveJob: Job? = null

    override fun onRetry() {
        refreshFavoritesCounters()
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.LogoutClick -> onLogoutClick(event.site)
            Event.LogoutConfirm -> onLogoutConfirm()
            Event.LogoutDismiss -> onLogoutDismiss()
            is Event.LoginClick -> onLoginClick(event.site)
            is Event.FavoriteProfilesNavigate -> onFavoriteProfilesNavigate(event.site)
            is Event.FavoritePostNavigate -> onFavoritePostNavigate(event.site)
            Event.NavigateToDownloads -> navigateToDownloads()
            Event.NavigateToSettings -> navigateToSettings()
            Event.NavigateToAuthorsBlacklist -> navigateToAuthorsBlacklist()
            Event.NavigateToFaq -> navigateToFaq()
        }
    }

    init {
        observeUiSetting()
        startObserveAuth()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    /** Подписка на auth-state. Важно запускать только один collector на lifecycle VM. */
    private fun startObserveAuth() {
        if (authObserveJob != null) return

        authObserveJob = observeAuthStateUseCase()
            .map { auth ->
                AuthSnapshot(
                    isKemonoAuthorized = auth.isKemonoAuthorized,
                    isCoomerAuthorized = auth.isCoomerAuthorized,
                    kemonoLogin = auth.kemono.user,
                    coomerLogin = auth.coomer.user,
                    kemonoUpdatedFavoritesCount = freshUpdatesUseCase.get(SelectedSite.K).size,
                    coomerUpdatedFavoritesCount = freshUpdatesUseCase.get(SelectedSite.C).size,
                )
            }
            .distinctUntilChanged()
            .onEach { snapshot ->
                setState {
                    copy(
                        isLoading = false,
                        isLoginKemono = snapshot.isKemonoAuthorized,
                        isLoginCoomer = snapshot.isCoomerAuthorized,
                        isLogin = snapshot.isKemonoAuthorized || snapshot.isCoomerAuthorized,
                        kemonoLogin = snapshot.kemonoLogin,
                        coomerLogin = snapshot.coomerLogin,
                        kemonoUpdatedFavoritesCount = snapshot.kemonoUpdatedFavoritesCount,
                        coomerUpdatedFavoritesCount = snapshot.coomerUpdatedFavoritesCount,
                    )
                }
            }
            .launchIn(viewModelScope)
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
        navigateWithSelectedSite(site = site, destination = AuthDestination.Login)
    }

    /** Любимые профили */
    private fun onFavoriteProfilesNavigate(site: SelectedSite) {
        navigateWithSelectedSite(site = site, destination = AuthDestination.FavoriteProfiles)
    }

    /** Любимые посты */
    private fun onFavoritePostNavigate(site: SelectedSite) {
        navigateWithSelectedSite(site = site, destination = AuthDestination.FavoritePosts)
    }

    private fun navigateWithSelectedSite(site: SelectedSite, destination: NavKey) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(destination)
    }

    /** Настройки */
    private fun navigateToSettings() = navigationManager.navigate(getSettingDestinationUseCase())

    private fun navigateToDownloads() = navigationManager.navigate(downloadNavigator.getDownloadsDest())

    private fun navigateToAuthorsBlacklist() = navigationManager.navigate(AuthDestination.AuthorsBlacklist)

    private fun navigateToFaq() = navigationManager.navigate(AuthDestination.Faq)

    private fun refreshFavoritesCounters() {
        setState {
            copy(
                isLoading = false,
                kemonoUpdatedFavoritesCount = freshUpdatesUseCase.get(SelectedSite.K).size,
                coomerUpdatedFavoritesCount = freshUpdatesUseCase.get(SelectedSite.C).size,
            )
        }
    }
}
