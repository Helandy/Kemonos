package su.afk.kemonos.profile.presenter.profile

import android.content.Context
import android.net.Uri
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import su.afk.kemonos.auth.ObserveAuthStateUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.download.api.IDownloadNavigator
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.domain.favorites.*
import su.afk.kemonos.profile.domain.favorites.fresh.IFreshFavoriteArtistsUpdatesUseCase
import su.afk.kemonos.profile.domain.favorites.model.FavoritesImportRequest
import su.afk.kemonos.profile.domain.file.ReadJsonFromUriUseCase
import su.afk.kemonos.profile.domain.file.SaveJsonToFolderUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.importResult.ImportResultItem
import su.afk.kemonos.profile.presenter.importResult.ImportResultPayload
import su.afk.kemonos.profile.presenter.importResult.ImportResultStatus
import su.afk.kemonos.profile.presenter.profile.ProfileState.*
import su.afk.kemonos.profile.presenter.profile.delegate.LogoutDelegate
import su.afk.kemonos.profile.presenter.profile.model.AuthSnapshot
import su.afk.kemonos.profile.utils.Const.KEY_IMPORT_RESULT_PAYLOAD
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import su.afk.kemonos.setting.api.useCase.IGetSettingDestinationUseCase
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val navigationManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val downloadNavigator: IDownloadNavigator,
    private val getSettingDestinationUseCase: IGetSettingDestinationUseCase,
    private val logoutDelegate: LogoutDelegate,
    private val uiSetting: IUiSettingUseCase,
    private val prepareFavoritesExportUseCase: PrepareFavoritesExportUseCase,
    private val importFavoritesFromJsonUseCase: ImportFavoritesFromJsonUseCase,
    private val readJsonFromUriUseCase: ReadJsonFromUriUseCase,
    private val saveJsonToFolderUseCase: SaveJsonToFolderUseCase,
    private val freshUpdatesUseCase: IFreshFavoriteArtistsUpdatesUseCase,
    @param:ApplicationContext private val appContext: Context,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()
    private var authObserveJob: Job? = null
    private var pendingExport: FavoritesExportPayload? = null
    private var pendingImport: FavoritesImportRequest? = null

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

    /** Экспорт избранного */
    private fun onExportFavorites(site: SelectedSite, type: ExportType) = viewModelScope.launch {
        if (currentState.isExportInProgress || currentState.isImportInProgress) return@launch

        syncSelectedSite(site)

        setState { copy(isExportInProgress = true) }
        val prepared = runCatching {
            prepareFavoritesExportUseCase(
                site = site,
                type = when (type) {
                    ExportType.ARTISTS -> FavoritesExportType.ARTISTS
                    ExportType.POSTS -> FavoritesExportType.POSTS
                }
            )
        }
        setState { copy(isExportInProgress = false) }

        prepared.onSuccess { payload ->
            pendingExport = payload
            setEffect(Effect.OpenExportFolderPicker)
        }.onFailure {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_export_prepare_failed)))
        }
    }

    /** Сохраняет подготовленный JSON экспорта в выбранную пользователем папку. */
    private fun onSaveExportToFolder(folderUri: Uri?) = viewModelScope.launch {
        val export = pendingExport
        if (export == null) {
            if (folderUri != null) {
                setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_export_no_pending_data)))
            }
            return@launch
        }

        pendingExport = null

        if (folderUri == null) {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_export_cancelled)))
            return@launch
        }

        setState { copy(isExportInProgress = true) }
        val saveResult = runCatching {
            withContext(Dispatchers.IO) {
                saveJsonToFolderUseCase(
                    folderUri = folderUri,
                    fileName = export.fileName,
                    json = export.json,
                )
            }
        }
        setState { copy(isExportInProgress = false) }

        saveResult.onSuccess {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_export_saved, export.fileName)))
        }.onFailure {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_export_save_failed)))
        }
    }

    /** Stores selected site/type until user picks import file in SAF. */
    private fun onImportFavorites(site: SelectedSite, type: ExportType) {
        if (currentState.isExportInProgress || currentState.isImportInProgress) return

        pendingImport = FavoritesImportRequest(
            site = site,
            type = when (type) {
                ExportType.ARTISTS -> FavoritesImportType.ARTISTS
                ExportType.POSTS -> FavoritesImportType.POSTS
            },
        )
        setEffect(Effect.OpenImportFilePicker)
    }

    /** Reads file json, imports favorites and navigates to detailed import result screen. */
    private fun onImportFavoritesFromFile(fileUri: Uri?) = viewModelScope.launch {
        val import = pendingImport
        if (import == null) {
            if (fileUri != null) {
                setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_import_no_pending_request)))
            }
            return@launch
        }

        pendingImport = null

        if (fileUri == null) {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_import_cancelled)))
            return@launch
        }

        syncSelectedSite(import.site)

        setState { copy(isImportInProgress = true) }
        val importResult = runCatching {
            withContext(Dispatchers.IO) {
                val rawJson = readJsonFromUriUseCase(fileUri)
                importFavoritesFromJsonUseCase(
                    site = import.site,
                    type = import.type,
                    rawJson = rawJson,
                )
            }
        }
        setState { copy(isImportInProgress = false) }

        importResult.onSuccess { result ->
            val siteName = if (import.site == SelectedSite.K) {
                appContext.getString(R.string.kemono)
            } else {
                appContext.getString(R.string.coomer)
            }
            val importTypeName = when (import.type) {
                FavoritesImportType.ARTISTS -> appContext.getString(R.string.profile_export_authors)
                FavoritesImportType.POSTS -> appContext.getString(R.string.profile_export_posts)
            }
            val payload = ImportResultPayload(
                title = appContext.getString(
                    R.string.profile_import_result_title_with_context,
                    siteName,
                    importTypeName,
                ),
                summary = appContext.getString(
                    R.string.profile_import_result_summary,
                    result.importedCount,
                    result.processedCount,
                    result.failedCount,
                    result.skippedCount,
                ),
                items = result.entries.map { entry ->
                    ImportResultItem(
                        rowNumber = entry.rowNumber,
                        target = entry.target.ifBlank {
                            appContext.getString(R.string.profile_import_result_unknown_target)
                        },
                        status = when (entry.status) {
                            FavoritesImportEntryStatus.SUCCESS -> ImportResultStatus.SUCCESS
                            FavoritesImportEntryStatus.FAILED -> ImportResultStatus.FAILED
                            FavoritesImportEntryStatus.SKIPPED -> ImportResultStatus.SKIPPED
                        },
                        reason = when (entry.reason) {
                            FavoritesImportEntryReason.NONE ->
                                appContext.getString(R.string.profile_import_reason_none)

                            FavoritesImportEntryReason.INVALID_ITEM ->
                                appContext.getString(R.string.profile_import_reason_invalid_item)

                            FavoritesImportEntryReason.DUPLICATE_IN_FILE ->
                                appContext.getString(R.string.profile_import_reason_duplicate)

                            FavoritesImportEntryReason.REQUEST_FAILED ->
                                appContext.getString(R.string.profile_import_reason_request_failed)
                        },
                    )
                }
            )
            navigateToImportResult(payload)
        }.onFailure { throwable ->
            val payload = ImportResultPayload(
                title = appContext.getString(R.string.profile_import_result_default_title),
                summary = appContext.getString(R.string.profile_import_failed),
                items = listOf(
                    ImportResultItem(
                        rowNumber = 1,
                        target = appContext.getString(R.string.profile_import_result_unknown_target),
                        status = ImportResultStatus.FAILED,
                        reason = throwable.message
                            ?: appContext.getString(R.string.profile_import_reason_request_failed),
                    ),
                )
            )
            navigateToImportResult(payload)
        }
    }

    /** Навигация с предварительным сохранением выбранного сайта в storage. */
    private fun navigateWithSelectedSite(site: SelectedSite, destination: NavKey) {
        navigationStorage.put(KEY_SELECT_SITE, site)
        navigationManager.navigate(destination)
    }

    /** Keeps selected site aligned with operation target site before API/local work. */
    private suspend fun syncSelectedSite(site: SelectedSite) {
        val service = when (site) {
            SelectedSite.K -> KEMONO_REFERENCE_SERVICE
            SelectedSite.C -> COOMER_REFERENCE_SERVICE
        }
        val targetSite = domainResolver.selectedSiteByService(service)
        selectedSiteUseCase.setSiteAndAwait(targetSite)
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
                kemonoUpdatedFavoritesCount = freshUpdatesUseCase.get(SelectedSite.K).size,
                coomerUpdatedFavoritesCount = freshUpdatesUseCase.get(SelectedSite.C).size,
            )
        }
    }

    private companion object {
        const val KEMONO_REFERENCE_SERVICE = "patreon"
        const val COOMER_REFERENCE_SERVICE = "onlyfans"
    }
}
