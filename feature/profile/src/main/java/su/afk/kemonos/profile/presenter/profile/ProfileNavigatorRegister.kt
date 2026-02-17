package su.afk.kemonos.profile.presenter.profile

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDest
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class ProfileNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDest.Profile> {
            val viewModel = hiltViewModel<ProfileViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                ProfileScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                )
            }
        }
    }
}
