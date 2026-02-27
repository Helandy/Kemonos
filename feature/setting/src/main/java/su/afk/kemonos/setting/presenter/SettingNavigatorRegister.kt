package su.afk.kemonos.setting.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.setting.navigation.SettingIntent
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class SettingNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<SettingIntent.Open> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                SettingScreen(
                    state = state,
                    onEvent = event,
                    effect = effect
                )
            }
        }

        entry<SettingIntent.CreatorTabsOrder> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                CreatorTabsOrderScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }
    }
}
