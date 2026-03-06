package su.afk.kemonos.profile.presenter.blacklist

import android.net.Uri
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class AuthorsBlacklistState {
    data class State(
        val isLoading: Boolean = false,
        val query: String = "",
        val items: List<BlacklistedAuthor> = emptyList(),
        val pendingRemoveAuthor: BlacklistedAuthor? = null,
        val isImportExportInProgress: Boolean = false,
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object Back : Event
        data class QueryChanged(val value: String) : Event
        data class OpenProfile(val service: String, val creatorId: String) : Event
        data class RequestRemoveAuthor(val author: BlacklistedAuthor) : Event
        data object ConfirmRemoveAuthor : Event
        data object DismissRemoveAuthor : Event
        data object ExportBlacklist : Event
        data class SaveExportToFolder(val folderUri: Uri?) : Event
        data object ImportBlacklist : Event
        data class ImportBlacklistFromFile(val fileUri: Uri?) : Event
    }

    sealed interface Effect : UiEffect {
        data object OpenExportFolderPicker : Effect
        data object OpenImportFilePicker : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
