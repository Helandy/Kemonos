package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDestination
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class FavoritePostsNavigatorRegister @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDestination.FavoritePosts> {
            val viewModel = hiltViewModel<FavoritePostsViewModel>()
            ScreenNavigator(viewModel) { state, _, event ->
                FavoritePostsScreen(
                    state = state,
                    onEvent = event,
                )
            }
        }
    }
}
