package su.afk.kemonos.main.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.main.navigation.StartCheckDestination
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class StartCheckNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<StartCheckDestination> {
            val viewModel = hiltViewModel<StartCheckViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                StartCheckScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                )
            }
        }
    }
}
