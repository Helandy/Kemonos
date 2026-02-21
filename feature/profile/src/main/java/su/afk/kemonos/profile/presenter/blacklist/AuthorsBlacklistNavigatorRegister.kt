package su.afk.kemonos.profile.presenter.blacklist

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class AuthorsBlacklistNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDest.AuthorsBlacklist> {
            val viewModel = hiltViewModel<AuthorsBlacklistViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                AuthorsBlacklistScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                )
            }
        }
    }
}
