package su.afk.kemonos.profile.presenter.profile

import su.afk.kemonos.domain.capabilities
import su.afk.kemonos.domain.displayName
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.auth.ObserveAuthStateUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AuthUser
import su.afk.kemonos.download.api.IDownloadNavigator
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.ui.IUiSettingsReader
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.domain.favorites.fresh.IFreshFavoriteArtistsUpdatesUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.importResult.ImportResultPayload
import su.afk.kemonos.profile.presenter.profile.ProfileState.*
import su.afk.kemonos.profile.presenter.profile.delegate.FavoritesTransferDelegate
import su.afk.kemonos.profile.presenter.profile.delegate.LogoutDelegate
import su.afk.kemonos.profile.presenter.profile.model.AuthSnapshot
import su.afk.kemonos.profile.utils.Const.KEY_IMPORT_RESULT_PAYLOAD
import su.afk.kemonos.setting.api.useCase.IGetSettingDestinationUseCase
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiText
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val downloadNavigator: IDownloadNavigator,
    private val getSettingDestinationUseCase: IGetSettingDestinationUseCase,
    private val logoutDelegate: LogoutDelegate,
    private val favoritesTransferDelegate: FavoritesTransferDelegate,
    private val uiSetting: IUiSettingsReader,
    private val freshUpdatesUseCase: IFreshFavoriteArtistsUpdatesUseCase,
    savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<State, Event, Effect>(savedStateHandle) {

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
            is Event.ExportFavorites -> onExportFavorites(event.site, event.type)
            is Event.SaveExportToFolder -> onSaveExportToFolder(event.folderUri)
            is Event.ImportFavorites -> onImportFavorites(event.site, event.type)
            is Event.ImportFavoritesFromFile -> onImportFavoritesFromFile(event.fileUri)
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
                    authorizedSites = SelectedSite.entries
                        .filter { auth.isAuthorized(it) }
                        .toSet(),
                    logins = SelectedSite.entries.mapNotNull { site ->
                        auth.forSite(site).user?.toLogin()?.let { site to it }
                    }.toMap(),
                    updatedFavoritesCounts = SelectedSite.entries.associateWith {
                        freshUpdatesUseCase.get(it).size
                    },
                )
            }
            .distinctUntilChanged()
            .onEach { snapshot ->
                setState {
                    copy(
                        isLoading = false,
                        loggedInSites = snapshot.authorizedSites,
                        isLogin = snapshot.authorizedSites.isNotEmpty(),
                        logins = snapshot.logins,
                        updatedFavoritesCounts = snapshot.updatedFavoritesCounts,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun AuthUser.toLogin(): Login = Login(
        id = id,
        username = username,
        createdAt = createdAt,
        role = role,
    )

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
        if (!site.capabilities.auth) {
            setEffect(
                Effect.ShowMessage(
                    UiText.Resource(R.string.login_site_unsupported, listOf(site.displayName))
                )
            )
            return
        }

        navigationManager.navigate(AuthDestination.Login(site))
    }

    /** Любимые профили */
    private fun onFavoriteProfilesNavigate(site: SelectedSite) {
        navigationManager.navigate(AuthDestination.FavoriteProfiles(site))
    }

    /** Любимые посты */
    private fun onFavoritePostNavigate(site: SelectedSite) {
        navigationManager.navigate(AuthDestination.FavoritePosts(site))
    }

    /** Экспорт избранного */
    private fun onExportFavorites(site: SelectedSite, type: ExportType) = viewModelScope.launch {
        favoritesTransferDelegate.prepareExport(
            site = site,
            type = type,
            getState = { currentState },
            updateState = { reducer -> setState(reducer) },
            sendEffect = ::setEffect,
        )
    }

    /** Сохраняет подготовленный JSON экспорта в выбранную пользователем папку. */
    private fun onSaveExportToFolder(folderUri: Uri?) = viewModelScope.launch {
        favoritesTransferDelegate.saveExport(
            folderUri = folderUri,
            updateState = { reducer -> setState(reducer) },
            sendEffect = ::setEffect,
        )
    }

    /** Запоминает выбранный сайт/тип, пока пользователь выбирает файл в SAF. */
    private fun onImportFavorites(site: SelectedSite, type: ExportType) =
        favoritesTransferDelegate.requestImport(
            site = site,
            type = type,
            getState = { currentState },
            sendEffect = ::setEffect,
        )

    /** Читает файл, импортирует избранное и открывает экран результата импорта. */
    private fun onImportFavoritesFromFile(fileUri: Uri?) = viewModelScope.launch {
        favoritesTransferDelegate.importFromFile(
            fileUri = fileUri,
            updateState = { reducer -> setState(reducer) },
            sendEffect = ::setEffect,
            showResult = ::navigateToImportResult,
        )
    }

    /** Настройки */
    private fun navigateToSettings() = navigationManager.navigate(getSettingDestinationUseCase())

    /** Экран загрузок. */
    private fun navigateToDownloads() = navigationManager.navigate(downloadNavigator.getDownloadsDest())

    /** Экран blacklist авторов. */
    private fun navigateToAuthorsBlacklist() = navigationManager.navigate(AuthDestination.AuthorsBlacklist)

    /** Экран FAQ. */
    private fun navigateToFaq() = navigationManager.navigate(AuthDestination.Faq)

    /** Навигация на экран результата импорта с payload через navigation storage. */
    private fun navigateToImportResult(payload: ImportResultPayload) {
        navigationStorage.put(KEY_IMPORT_RESULT_PAYLOAD, payload)
        navigationManager.navigate(AuthDestination.ImportResult)
    }

    /** Обновляет счетчики свежих обновлений по избранным авторам для двух сайтов. */
    private fun refreshFavoritesCounters() {
        setState {
            copy(
                isLoading = false,
                updatedFavoritesCounts = SelectedSite.entries.associateWith {
                    freshUpdatesUseCase.get(it).size
                },
            )
        }
    }

}
