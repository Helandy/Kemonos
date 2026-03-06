package su.afk.kemonos.profile.presenter.importResult

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class ImportResultNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDestination.ImportResult> {
            val viewModel = hiltViewModel<ImportResultViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                ImportResultScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }
    }
}
