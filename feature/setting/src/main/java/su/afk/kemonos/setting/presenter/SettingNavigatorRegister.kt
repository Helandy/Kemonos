package su.afk.kemonos.setting.presenter

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.setting.navigation.SettingDestination
import su.afk.kemonos.setting.presenter.screens.*
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class SettingNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<SettingDestination.Open> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                SettingScreen(
                    state = state,
                    onEvent = event,
                    effect = effect
                )
            }
        }

        entry<SettingDestination.Ui> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingUiScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.Translate> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingTranslateScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.Network> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingNetworkScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.Database> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingDatabaseScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.Downloads> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingDownloadsScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.HelpImport> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingHelpImportScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.DebugStorage> {
            val viewModel = hiltViewModel<SettingViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                SettingDebugStorageScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }

        entry<SettingDestination.CreatorTabsOrder> {
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
