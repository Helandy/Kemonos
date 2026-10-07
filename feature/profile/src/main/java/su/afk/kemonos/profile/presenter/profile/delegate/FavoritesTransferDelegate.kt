package su.afk.kemonos.profile.presenter.profile.delegate

import android.net.Uri
import androidx.annotation.StringRes
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.displayName
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.domain.favorites.FavoritesExportPayload
import su.afk.kemonos.profile.domain.favorites.FavoritesExportType
import su.afk.kemonos.profile.domain.favorites.FavoritesImportType
import su.afk.kemonos.profile.domain.favorites.ImportFavoritesFromJsonUseCase
import su.afk.kemonos.profile.domain.favorites.PrepareFavoritesExportUseCase
import su.afk.kemonos.profile.domain.favorites.model.FavoritesImportRequest
import su.afk.kemonos.profile.domain.file.ReadJsonFromUriUseCase
import su.afk.kemonos.profile.domain.file.SaveJsonToFolderUseCase
import su.afk.kemonos.profile.presenter.importResult.ImportResultPayload
import su.afk.kemonos.profile.presenter.importResult.importFailurePayload
import su.afk.kemonos.profile.presenter.importResult.toImportResultItem
import su.afk.kemonos.profile.presenter.profile.ProfileState.Effect
import su.afk.kemonos.profile.presenter.profile.ProfileState.ExportType
import su.afk.kemonos.profile.presenter.profile.ProfileState.State
import su.afk.kemonos.ui.presenter.baseViewModel.UiText
import javax.inject.Inject

/**
 * Экспорт/импорт избранного: хранит промежуточные данные между шагами SAF-пикера
 * (подготовка экспорта → выбор папки → запись; выбор типа → выбор файла → импорт).
 */
internal class FavoritesTransferDelegate @Inject constructor(
    private val prepareFavoritesExportUseCase: PrepareFavoritesExportUseCase,
    private val importFavoritesFromJsonUseCase: ImportFavoritesFromJsonUseCase,
    private val readJsonFromUriUseCase: ReadJsonFromUriUseCase,
    private val saveJsonToFolderUseCase: SaveJsonToFolderUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) {
    private var pendingExport: FavoritesExportPayload? = null
    private var pendingImport: FavoritesImportRequest? = null

    /** Готовит JSON экспорта и просит открыть выбор папки. */
    suspend fun prepareExport(
        site: SelectedSite,
        type: ExportType,
        getState: () -> State,
        updateState: (State.() -> State) -> Unit,
        sendEffect: (Effect) -> Unit,
    ) {
        if (getState().isBusy) return

        selectedSiteUseCase.setSiteAndAwait(site)

        updateState { copy(isExportInProgress = true) }
        val prepared = runCatching {
            prepareFavoritesExportUseCase(
                site = site,
                type = when (type) {
                    ExportType.ARTISTS -> FavoritesExportType.ARTISTS
                    ExportType.POSTS -> FavoritesExportType.POSTS
                }
            )
        }
        updateState { copy(isExportInProgress = false) }

        prepared.onSuccess { payload ->
            pendingExport = payload
            sendEffect(Effect.OpenExportFolderPicker)
        }.onFailure {
            sendEffect(message(R.string.profile_export_prepare_failed))
        }
    }

    /** Сохраняет подготовленный JSON экспорта в выбранную пользователем папку. */
    suspend fun saveExport(
        folderUri: Uri?,
        updateState: (State.() -> State) -> Unit,
        sendEffect: (Effect) -> Unit,
    ) {
        val export = pendingExport
        if (export == null) {
            if (folderUri != null) sendEffect(message(R.string.profile_export_no_pending_data))
            return
        }

        pendingExport = null

        if (folderUri == null) {
            sendEffect(message(R.string.profile_export_cancelled))
            return
        }

        updateState { copy(isExportInProgress = true) }
        val saveResult = runCatching {
            saveJsonToFolderUseCase(
                folderUri = folderUri.toString(),
                fileName = export.fileName,
                json = export.json,
            )
        }
        updateState { copy(isExportInProgress = false) }

        saveResult.onSuccess {
            sendEffect(message(R.string.profile_export_saved, export.fileName))
        }.onFailure {
            sendEffect(message(R.string.profile_export_save_failed))
        }
    }

    /** Запоминает выбранный сайт/тип, пока пользователь выбирает файл в SAF. */
    fun requestImport(
        site: SelectedSite,
        type: ExportType,
        getState: () -> State,
        sendEffect: (Effect) -> Unit,
    ) {
        if (getState().isBusy) return

        pendingImport = FavoritesImportRequest(
            site = site,
            type = when (type) {
                ExportType.ARTISTS -> FavoritesImportType.ARTISTS
                ExportType.POSTS -> FavoritesImportType.POSTS
            },
        )
        sendEffect(Effect.OpenImportFilePicker)
    }

    /** Читает JSON-файл, импортирует избранное и отдаёт payload для экрана результата. */
    suspend fun importFromFile(
        fileUri: Uri?,
        updateState: (State.() -> State) -> Unit,
        sendEffect: (Effect) -> Unit,
        showResult: (ImportResultPayload) -> Unit,
    ) {
        val import = pendingImport
        if (import == null) {
            if (fileUri != null) sendEffect(message(R.string.profile_import_no_pending_request))
            return
        }

        pendingImport = null

        if (fileUri == null) {
            sendEffect(message(R.string.profile_import_cancelled))
            return
        }

        selectedSiteUseCase.setSiteAndAwait(import.site)

        updateState { copy(isImportInProgress = true) }
        val importResult = runCatching {
            importFavoritesFromJsonUseCase(
                site = import.site,
                type = import.type,
                rawJson = readJsonFromUriUseCase(fileUri.toString()),
            )
        }
        updateState { copy(isImportInProgress = false) }

        val payload = importResult.fold(
            onSuccess = { result ->
                val typeName = when (import.type) {
                    FavoritesImportType.ARTISTS -> R.string.profile_export_authors
                    FavoritesImportType.POSTS -> R.string.profile_export_posts
                }
                ImportResultPayload(
                    title = UiText.Resource(
                        R.string.profile_import_result_title_with_context,
                        listOf(import.site.displayName, UiText.Resource(typeName)),
                    ),
                    summary = UiText.Resource(
                        R.string.profile_import_result_summary,
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
                    title = UiText.Resource(R.string.profile_import_result_default_title),
                    summary = UiText.Resource(R.string.profile_import_failed),
                    cause = throwable,
                )
            },
        )
        showResult(payload)
    }

    private val State.isBusy: Boolean get() = isExportInProgress || isImportInProgress

    private fun message(@StringRes resId: Int, vararg args: Any): Effect =
        Effect.ShowMessage(UiText.Resource(resId, args.toList()))
}
