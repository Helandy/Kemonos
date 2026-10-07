package su.afk.kemonos.profile.presenter.blacklist

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.preferences.ui.IUiSettingsReader
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.domain.blacklist.ImportBlacklistFromJsonUseCase
import su.afk.kemonos.profile.domain.blacklist.PrepareBlacklistExportUseCase
import su.afk.kemonos.profile.domain.blacklist.ObserveBlacklistedAuthorsUseCase
import su.afk.kemonos.profile.domain.blacklist.RemoveBlacklistedAuthorUseCase
import su.afk.kemonos.profile.domain.file.ReadJsonFromUriUseCase
import su.afk.kemonos.profile.domain.file.SaveJsonToFolderUseCase
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistState.*
import su.afk.kemonos.profile.presenter.importResult.ImportResultPayload
import su.afk.kemonos.profile.presenter.importResult.importFailurePayload
import su.afk.kemonos.profile.presenter.importResult.toImportResultItem
import su.afk.kemonos.profile.utils.Const.KEY_IMPORT_RESULT_PAYLOAD
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiText
import su.afk.kemonos.ui.presenter.baseViewModel.getSerializableState
import su.afk.kemonos.ui.presenter.baseViewModel.setSerializableState
import javax.inject.Inject

@HiltViewModel
internal class AuthorsBlacklistViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val observeBlacklistedAuthors: ObserveBlacklistedAuthorsUseCase,
    private val removeBlacklistedAuthor: RemoveBlacklistedAuthorUseCase,
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val prepareBlacklistExportUseCase: PrepareBlacklistExportUseCase,
    private val importBlacklistFromJsonUseCase: ImportBlacklistFromJsonUseCase,
    private val readJsonFromUriUseCase: ReadJsonFromUriUseCase,
    private val saveJsonToFolderUseCase: SaveJsonToFolderUseCase,
    private val uiSetting: IUiSettingsReader,
    savedStateHandle: SavedStateHandle,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<State, Event, Effect>(savedStateHandle) {
    override fun createInitialState(): State =
        savedStateHandle.getSerializableState<AuthorsBlacklistPersistedState>(KEY_STATE)?.toState()
            ?: State()

    override fun saveToSavedState(state: State) {
        savedStateHandle.setSerializableState(KEY_STATE, state.toPersistedState())
    }

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
        observeBlacklistedAuthors()
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
        removeBlacklistedAuthor(service = service, creatorId = creatorId)
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
            setEffect(Effect.ShowMessage(UiText.Resource(R.string.profile_blacklist_export_cancelled)))
            return@launch
        }

        setState { copy(isImportExportInProgress = true) }
        val exportResult = runCatching {
            val items = observeBlacklistedAuthors().first()
            items.firstOrNull()?.let { firstAuthor ->
                syncSelectedSiteByService(firstAuthor.service)
            }
            val payload = prepareBlacklistExportUseCase(items)

            saveJsonToFolderUseCase(
                folderUri = folderUri.toString(),
                fileName = payload.fileName,
                json = payload.json,
            )
            payload.fileName
        }
        setState { copy(isImportExportInProgress = false) }

        exportResult.onSuccess { fileName ->
            setEffect(
                Effect.ShowMessage(
                    UiText.Resource(R.string.profile_blacklist_export_saved, listOf(fileName))
                )
            )
        }.onFailure {
            setEffect(Effect.ShowMessage(UiText.Resource(R.string.profile_blacklist_export_failed)))
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
            setEffect(Effect.ShowMessage(UiText.Resource(R.string.profile_blacklist_import_cancelled)))
            return@launch
        }

        setState { copy(isImportExportInProgress = true) }
        val importResult = runCatching {
            val rawJson = readJsonFromUriUseCase(fileUri.toString())
            importBlacklistFromJsonUseCase(rawJson)
        }
        setState { copy(isImportExportInProgress = false) }

        val payload = importResult.fold(
            onSuccess = { result ->
                ImportResultPayload(
                    title = UiText.Resource(R.string.profile_blacklist_import_title),
                    summary = UiText.Resource(
                        R.string.profile_blacklist_import_result_summary,
                        listOf(
                            result.importedCount,
                            result.processedCount,
                            result.failedCount,
                            result.skippedCount,
                        ),
                    ),
                    items = result.entries.map { it.toImportResultItem() },
                )
            },
            onFailure = { throwable ->
                importFailurePayload(
                    title = UiText.Resource(R.string.profile_blacklist_import_title),
                    summary = UiText.Resource(R.string.profile_blacklist_import_failed),
                    cause = throwable,
                )
            },
        )
        navigateToImportResult(payload)
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

    private companion object {
        const val KEY_STATE = "authors_blacklist_state"
    }
}
