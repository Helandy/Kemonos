package su.afk.kemonos.posts.presenter.pager

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.posts.navigation.PostsDest
import su.afk.kemonos.ui.presenter.baseViewModel.ScreenNavigator

class PostsPagerNavigator @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<PostsDest.PostsPager> {
            val viewModel = hiltViewModel<PostsPagerViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                PostsScreen(
                    state = state,
                    effect = effect,
                    onEvent = event,
                )
            }
        }
    }
}
