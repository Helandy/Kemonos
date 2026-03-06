package su.afk.kemonos.profile.presenter.blacklist

import android.content.Context
import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
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
import su.afk.kemonos.profile.domain.blacklist.BlacklistImportEntryReason
import su.afk.kemonos.profile.domain.blacklist.BlacklistImportEntryStatus
import su.afk.kemonos.profile.domain.blacklist.ImportBlacklistFromJsonUseCase
import su.afk.kemonos.profile.domain.blacklist.PrepareBlacklistExportUseCase
import su.afk.kemonos.profile.domain.file.ReadJsonFromUriUseCase
import su.afk.kemonos.profile.domain.file.SaveJsonToFolderUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistState.*
import su.afk.kemonos.profile.presenter.importResult.ImportResultItem
import su.afk.kemonos.profile.presenter.importResult.ImportResultPayload
import su.afk.kemonos.profile.presenter.importResult.ImportResultStatus
import su.afk.kemonos.profile.utils.Const.KEY_IMPORT_RESULT_PAYLOAD
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class AuthorsBlacklistViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val prepareBlacklistExportUseCase: PrepareBlacklistExportUseCase,
    private val importBlacklistFromJsonUseCase: ImportBlacklistFromJsonUseCase,
    private val readJsonFromUriUseCase: ReadJsonFromUriUseCase,
    private val saveJsonToFolderUseCase: SaveJsonToFolderUseCase,
    private val uiSetting: IUiSettingUseCase,
    @param:ApplicationContext private val appContext: Context,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {
    override fun createInitialState(): State = State()

    init {
        observeUiSetting()
        observeBlacklist()
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navManager.back()
            is Event.QueryChanged -> setState { copy(query = event.value) }
            is Event.OpenProfile -> openProfile(event.service, event.creatorId)
            is Event.RequestRemoveAuthor -> setState { copy(pendingRemoveAuthor = event.author) }
            Event.DismissRemoveAuthor -> setState { copy(pendingRemoveAuthor = null) }
            Event.ConfirmRemoveAuthor -> confirmRemoveAuthor()
            Event.ExportBlacklist -> onExportBlacklist()
            is Event.SaveExportToFolder -> onSaveExportToFolder(event.folderUri)
            Event.ImportBlacklist -> onImportBlacklist()
            is Event.ImportBlacklistFromFile -> onImportBlacklistFromFile(event.fileUri)
        }
    }

    /** Наблюдает за локальным blacklist в Room и обновляет список на экране. */
    private fun observeBlacklist() {
        blacklistedAuthorsRepository.observeAll()
            .onEach { items ->
                setState {
                    copy(
                        items = items
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /** Синхронизирует UI-настройки между экраном и хранилищем prefs. */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    /** Удаляет автора из локального blacklist по service/id. */
    private fun removeAuthor(service: String, creatorId: String) = viewModelScope.launch {
        blacklistedAuthorsRepository.remove(service = service, creatorId = creatorId)
    }

    /** Подтверждает удаление автора из диалога и очищает pending-состояние. */
    private fun confirmRemoveAuthor() = viewModelScope.launch {
        val pending = currentState.pendingRemoveAuthor ?: return@launch
        removeAuthor(service = pending.service, creatorId = pending.creatorId)
        setState { copy(pendingRemoveAuthor = null) }
    }

    /** Перед открытием профиля переключает активный сайт в соответствии с service автора. */
    private fun openProfile(service: String, creatorId: String) = viewModelScope.launch {
        val targetSite = domainResolver.selectedSiteByService(service)
        selectedSiteUseCase.setSiteAndAwait(targetSite)

        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = creatorId,
            )
        )
    }

    /** Стартует workflow экспорта: открывает системный выбор папки. */
    private fun onExportBlacklist() {
        if (currentState.isImportExportInProgress) return
        setEffect(Effect.OpenExportFolderPicker)
    }

    /** Экспортирует текущий blacklist в JSON-файл в выбранную папку. */
    private fun onSaveExportToFolder(folderUri: Uri?) = viewModelScope.launch {
        if (folderUri == null) {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_blacklist_export_cancelled)))
            return@launch
        }

        setState { copy(isImportExportInProgress = true) }
        val exportResult = runCatching {
            val items = withContext(Dispatchers.IO) {
                blacklistedAuthorsRepository.observeAll().first()
            }
            items.firstOrNull()?.let { firstAuthor ->
                syncSelectedSiteByService(firstAuthor.service)
            }
            val payload = prepareBlacklistExportUseCase(items)

            withContext(Dispatchers.IO) {
                saveJsonToFolderUseCase(
                    folderUri = folderUri,
                    fileName = payload.fileName,
                    json = payload.json,
                )
                payload.fileName
            }
        }
        setState { copy(isImportExportInProgress = false) }

        exportResult.onSuccess { fileName ->
            setEffect(
                Effect.ShowMessage(
                    appContext.getString(R.string.profile_blacklist_export_saved, fileName)
                )
            )
        }.onFailure {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_blacklist_export_failed)))
        }
    }

    /** Стартует workflow импорта: открывает системный выбор JSON-файла. */
    private fun onImportBlacklist() {
        if (currentState.isImportExportInProgress) return
        setEffect(Effect.OpenImportFilePicker)
    }

    /** Импортирует blacklist из JSON и навигирует на экран детального результата. */
    private fun onImportBlacklistFromFile(fileUri: Uri?) = viewModelScope.launch {
        if (fileUri == null) {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_blacklist_import_cancelled)))
            return@launch
        }

        setState { copy(isImportExportInProgress = true) }
        val importResult = runCatching {
            val rawJson = withContext(Dispatchers.IO) { readJsonFromUriUseCase(fileUri) }
            importBlacklistFromJsonUseCase(rawJson)
        }
        setState { copy(isImportExportInProgress = false) }

        importResult.onSuccess { result ->
            navigateToImportResult(
                ImportResultPayload(
                    title = appContext.getString(R.string.profile_blacklist_import_title),
                    summary = appContext.getString(
                        R.string.profile_blacklist_import_result_summary,
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
                                BlacklistImportEntryStatus.SUCCESS -> ImportResultStatus.SUCCESS
                                BlacklistImportEntryStatus.FAILED -> ImportResultStatus.FAILED
                                BlacklistImportEntryStatus.SKIPPED -> ImportResultStatus.SKIPPED
                            },
                            reason = when (entry.reason) {
                                BlacklistImportEntryReason.NONE ->
                                    appContext.getString(R.string.profile_import_reason_none)

                                BlacklistImportEntryReason.INVALID_ITEM ->
                                    appContext.getString(R.string.profile_import_reason_invalid_item)

                                BlacklistImportEntryReason.DUPLICATE_IN_FILE ->
                                    appContext.getString(R.string.profile_import_reason_duplicate)

                                BlacklistImportEntryReason.REQUEST_FAILED ->
                                    appContext.getString(R.string.profile_import_reason_request_failed)
                            },
                        )
                    },
                )
            )
        }.onFailure { throwable ->
            navigateToImportResult(
                ImportResultPayload(
                    title = appContext.getString(R.string.profile_blacklist_import_title),
                    summary = appContext.getString(R.string.profile_blacklist_import_failed),
                    items = listOf(
                        ImportResultItem(
                            rowNumber = 1,
                            target = appContext.getString(R.string.profile_import_result_unknown_target),
                            status = ImportResultStatus.FAILED,
                            reason = throwable.message
                                ?: appContext.getString(R.string.profile_import_reason_request_failed),
                        )
                    ),
                )
            )
        }
    }

    /** Открывает экран результата импорта с сохраненным payload в navigation storage. */
    private fun navigateToImportResult(payload: ImportResultPayload) {
        navigationStorage.put(KEY_IMPORT_RESULT_PAYLOAD, payload)
        navManager.navigate(AuthDestination.ImportResult)
    }

    /** Синхронизирует выбранный сайт приложения с service из импортируемых/экспортируемых данных. */
    private suspend fun syncSelectedSiteByService(service: String) {
        val targetSite = domainResolver.selectedSiteByService(service)
        selectedSiteUseCase.setSiteAndAwait(targetSite)
    }

}
