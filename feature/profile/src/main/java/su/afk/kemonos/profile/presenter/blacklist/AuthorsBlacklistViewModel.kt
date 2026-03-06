package su.afk.kemonos.profile.presenter.blacklist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistState.*
import su.afk.kemonos.profile.presenter.importResult.ImportResultItem
import su.afk.kemonos.profile.presenter.importResult.ImportResultPayload
import su.afk.kemonos.profile.presenter.importResult.ImportResultStatus
import su.afk.kemonos.profile.utils.Const.KEY_IMPORT_RESULT_PAYLOAD
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
internal class AuthorsBlacklistViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
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

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    private fun removeAuthor(service: String, creatorId: String) = viewModelScope.launch {
        blacklistedAuthorsRepository.remove(service = service, creatorId = creatorId)
    }

    private fun confirmRemoveAuthor() = viewModelScope.launch {
        val pending = currentState.pendingRemoveAuthor ?: return@launch
        removeAuthor(service = pending.service, creatorId = pending.creatorId)
        setState { copy(pendingRemoveAuthor = null) }
    }

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

    private fun onExportBlacklist() {
        if (currentState.isImportExportInProgress) return
        setEffect(Effect.OpenExportFolderPicker)
    }

    private fun onSaveExportToFolder(folderUri: Uri?) = viewModelScope.launch {
        if (folderUri == null) {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_blacklist_export_cancelled)))
            return@launch
        }

        setState { copy(isImportExportInProgress = true) }
        val exportResult = runCatching {
            withContext(Dispatchers.IO) {
                val items = blacklistedAuthorsRepository.observeAll().first()
                val fileName = buildExportFileName(items.size)
                val json = buildExportJson(items)
                saveExportToFolder(folderUri = folderUri, fileName = fileName, json = json)
                fileName
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

    private fun onImportBlacklist() {
        if (currentState.isImportExportInProgress) return
        setEffect(Effect.OpenImportFilePicker)
    }

    private fun onImportBlacklistFromFile(fileUri: Uri?) = viewModelScope.launch {
        if (fileUri == null) {
            setEffect(Effect.ShowMessage(appContext.getString(R.string.profile_blacklist_import_cancelled)))
            return@launch
        }

        setState { copy(isImportExportInProgress = true) }
        val importResult = runCatching {
            withContext(Dispatchers.IO) {
                val rawJson = readJsonFromUri(fileUri)
                importFromJson(rawJson)
            }
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
                    items = result.entries,
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

    private fun buildExportJson(items: List<BlacklistedAuthor>): String {
        return buildString(items.size * 64 + 2) {
            append("[")
            items.forEachIndexed { index, item ->
                if (index > 0) append(",")
                append(
                    """
                    {"service":${item.service.toJsonString()},"creatorId":${item.creatorId.toJsonString()},"creatorName":${item.creatorName.toJsonString()},"createdAt":${item.createdAt}}
                    """.trimIndent()
                )
            }
            append("]")
        }
    }

    private suspend fun importFromJson(rawJson: String): ImportResult {
        val root = JsonParser.parseString(rawJson)
        if (!root.isJsonArray) error("Invalid blacklist import format")

        val unique = LinkedHashMap<String, IndexedBlacklistAuthor>()
        val entries = ArrayList<ImportResultItem>(root.asJsonArray.size())

        for ((index, element) in root.asJsonArray.withIndex()) {
            val rowNumber = index + 1
            val parsed = parseBlacklistItem(element)
            if (parsed == null) {
                entries += ImportResultItem(
                    rowNumber = rowNumber,
                    target = appContext.getString(R.string.profile_import_result_unknown_target),
                    status = ImportResultStatus.SKIPPED,
                    reason = appContext.getString(R.string.profile_import_reason_invalid_item),
                )
                continue
            }

            val key = "${parsed.service}:${parsed.creatorId}"
            if (unique.containsKey(key)) {
                entries += ImportResultItem(
                    rowNumber = rowNumber,
                    target = key,
                    status = ImportResultStatus.SKIPPED,
                    reason = appContext.getString(R.string.profile_import_reason_duplicate),
                )
                continue
            }

            unique[key] = IndexedBlacklistAuthor(
                rowNumber = rowNumber,
                author = parsed,
            )
        }

        for ((key, indexedAuthor) in unique.entries) {
            runCatching { blacklistedAuthorsRepository.upsert(indexedAuthor.author) }
                .onSuccess {
                    entries += ImportResultItem(
                        rowNumber = indexedAuthor.rowNumber,
                        target = key,
                        status = ImportResultStatus.SUCCESS,
                        reason = appContext.getString(R.string.profile_import_reason_none),
                    )
                }
                .onFailure {
                    entries += ImportResultItem(
                        rowNumber = indexedAuthor.rowNumber,
                        target = key,
                        status = ImportResultStatus.FAILED,
                        reason = appContext.getString(R.string.profile_import_reason_request_failed),
                    )
                }
        }

        return ImportResult(
            entries = entries.sortedBy { it.rowNumber },
        )
    }

    private fun parseBlacklistItem(element: JsonElement): BlacklistedAuthor? {
        if (!element.isJsonObject) return null
        val obj = element.asJsonObject

        val service = obj.stringField("service") ?: return null
        val creatorId = obj.stringField("creatorId") ?: obj.stringField("id") ?: return null
        val creatorName = obj.stringField("creatorName")
            ?: obj.stringField("name")
            ?: creatorId
        val createdAt = obj.longField("createdAt") ?: System.currentTimeMillis()

        return BlacklistedAuthor(
            service = service,
            creatorId = creatorId,
            creatorName = creatorName,
            createdAt = createdAt,
        )
    }

    private fun JsonObject.stringField(name: String): String? =
        runCatching { get(name) }
            .getOrNull()
            ?.takeIf { !it.isJsonNull }
            ?.let { runCatching { it.asString }.getOrNull() }
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

    private fun JsonObject.longField(name: String): Long? =
        runCatching { get(name) }
            .getOrNull()
            ?.takeIf { !it.isJsonNull }
            ?.let { runCatching { it.asLong }.getOrNull() }

    private fun String.toJsonString(): String = buildString(length + 2) {
        append('"')
        for (ch in this@toJsonString) {
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
        append('"')
    }

    private fun buildExportFileName(count: Int): String {
        val datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
        return "Blacklist_Authors_(${count})_${datePart}.json"
    }

    private fun saveExportToFolder(
        folderUri: Uri,
        fileName: String,
        json: String,
    ) {
        val resolver = appContext.contentResolver
        val rwFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        runCatching { resolver.takePersistableUriPermission(folderUri, rwFlags) }

        val treeId = DocumentsContract.getTreeDocumentId(folderUri)
        val treeDocumentUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, treeId)

        val createdFileUri = DocumentsContract.createDocument(
            resolver,
            treeDocumentUri,
            "application/json",
            fileName,
        ) ?: error("Failed to create export file")

        resolver.openOutputStream(createdFileUri)?.use { stream ->
            stream.write(json.toByteArray(Charsets.UTF_8))
        } ?: error("Failed to open export file stream")
    }

    private fun readJsonFromUri(fileUri: Uri): String {
        val resolver = appContext.contentResolver
        runCatching {
            resolver.takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return resolver.openInputStream(fileUri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
            reader.readText()
        } ?: error("Failed to open import file stream")
    }

    private fun navigateToImportResult(payload: ImportResultPayload) {
        navigationStorage.put(KEY_IMPORT_RESULT_PAYLOAD, payload)
        navManager.navigate(AuthDestination.ImportResult)
    }

    private data class ImportResult(
        val entries: List<ImportResultItem>,
    ) {
        val processedCount: Int get() = entries.size
        val importedCount: Int get() = entries.count { it.status == ImportResultStatus.SUCCESS }
        val failedCount: Int get() = entries.count { it.status == ImportResultStatus.FAILED }
        val skippedCount: Int get() = entries.count { it.status == ImportResultStatus.SKIPPED }
    }

    private data class IndexedBlacklistAuthor(
        val rowNumber: Int,
        val author: BlacklistedAuthor,
    )
}
