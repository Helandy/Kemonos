package su.afk.kemonos.download.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.download.navigation.DownloadDest
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class DownloadsNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<DownloadDest.Downloads> {
            val viewModel = hiltViewModel<DownloadsViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                DownloadsScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                )
            }
        }
    }
}
