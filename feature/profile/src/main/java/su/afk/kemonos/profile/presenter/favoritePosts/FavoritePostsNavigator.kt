package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.baseViewModel.ScreenNavigator
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.profile.navigation.AuthDest

class FavoritePostsNavigator @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<AuthDest.FavoritePosts> {
            val viewModel = hiltViewModel<FavoritePostsViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                FavoritePostsScreen(
                    state = state,
                    onEvent = event,
                    effect = effect,
                )
            }
        }
    }
}
