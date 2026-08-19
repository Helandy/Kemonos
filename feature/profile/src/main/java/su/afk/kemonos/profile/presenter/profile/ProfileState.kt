package su.afk.kemonos.profile.presenter.profile

import android.net.Uri
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class ProfileState {
    data class State(
        val isLoading: Boolean = true,

        val loggedInSites: Set<SelectedSite> = emptySet(),
        val isLogin: Boolean = false,

        val logins: Map<SelectedSite, Login> = emptyMap(),

        val showLogoutConfirm: Boolean = false,
        val logoutSite: SelectedSite? = null,

        val updatedFavoritesCounts: Map<SelectedSite, Int> = emptyMap(),

        val isExportInProgress: Boolean = false,
        val isImportInProgress: Boolean = false,

        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState {

        fun isLoggedIn(site: SelectedSite): Boolean = site in loggedInSites
        fun login(site: SelectedSite): Login? = logins[site]
        fun updatedFavoritesCount(site: SelectedSite): Int = updatedFavoritesCounts[site] ?: 0
    }

    enum class ExportType {
        ARTISTS,
        POSTS,
    }

    sealed interface Event : UiEvent {
        data class LogoutClick(val site: SelectedSite) : Event
        data object LogoutConfirm : Event
        data object LogoutDismiss : Event
        data class LoginClick(val site: SelectedSite) : Event
        data class FavoriteProfilesNavigate(val site: SelectedSite) : Event
        data class FavoritePostNavigate(val site: SelectedSite) : Event
        data class ExportFavorites(val site: SelectedSite, val type: ExportType) : Event
        data class SaveExportToFolder(val folderUri: Uri?) : Event
        data class ImportFavorites(val site: SelectedSite, val type: ExportType) : Event
        data class ImportFavoritesFromFile(val fileUri: Uri?) : Event
        data object NavigateToDownloads : Event
        data object NavigateToSettings : Event
        data object NavigateToAuthorsBlacklist : Event
        data object NavigateToFaq : Event
    }

    sealed interface Effect : UiEffect {
        data object OpenExportFolderPicker : Effect
        data object OpenImportFilePicker : Effect
        data class ShowMessage(val message: String) : Effect
    }
}
