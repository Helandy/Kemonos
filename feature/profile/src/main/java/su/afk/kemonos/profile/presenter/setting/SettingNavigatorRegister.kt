package su.afk.kemonos.profile.presenter.setting

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.baseViewModel.ScreenNavigator
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDest

class SettingNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDest.Setting> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                SettingScreen(
                    state = state,
                    onEvent = event,
                    effect = effect
                )
            }
        }
    }
}