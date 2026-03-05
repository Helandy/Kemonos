package su.afk.kemonos.download.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.download.navigation.DownloadDestination
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class DownloadsNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<DownloadDestination.Downloads> {
            val viewModel = hiltViewModel<DownloadsViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                DownloadsScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }
    }
}
