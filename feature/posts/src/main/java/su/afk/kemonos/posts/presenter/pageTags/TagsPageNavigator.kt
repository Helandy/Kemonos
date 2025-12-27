package su.afk.kemonos.posts.presenter.pageTags

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
internal fun TagsPageNavigation() {
    TagsPageScreen(viewModel = hiltViewModel<TagsPageViewModel>())
}