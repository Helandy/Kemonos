package su.afk.kemonos.profile.presenter.login

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.baseViewModel.ScreenNavigator
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDest

class LoginNavigator @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDest.Login> {
            val viewModel = hiltViewModel<LoginViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                LoginScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                    pickPassword = viewModel::pickPassword,
                    savePassword = viewModel::savePassword,
                    navigateAfterLogin = viewModel::navigateAfterLogin,
                )
            }
        }
    }
}
