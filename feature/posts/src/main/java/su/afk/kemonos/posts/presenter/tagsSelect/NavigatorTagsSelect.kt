package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import jakarta.inject.Inject
import su.afk.kemonos.common.presenter.baseViewModel.ScreenNavigator
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.posts.navigation.PostsDest

class NavigatorTagsSelect @Inject constructor() : NavRegistrar {
    override fun register(builder: EntryProviderScope<NavKey>, nav: NavigationManager) = with(builder) {
        entry<PostsDest.TagsSelect> {
            val viewModel = hiltViewModel<TagsSelectViewModel>()
            ScreenNavigator(viewModel) { state, effect, event ->
                TagsPostsScreen(
                    state = state,
                    onEvent = event,
                    effect = effect,
                )
            }
        }
    }
}
