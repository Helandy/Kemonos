package su.afk.kemonos.profile.presenter.favoriteProfiles

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.baseScreen.ScreenNavigator
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDest

class FavoriteProfilesRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDest.FavoriteProfiles> {
            val viewModel = hiltViewModel<FavoriteProfilesViewModel>()
            ScreenNavigator(viewModel) { state, effect, onEventSent ->
                FavoriteProfilesScreen(
                    state = state,
                    effect = effect,
                    onEvent = onEventSent,
                )
            }
        }
    }
}