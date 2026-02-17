package su.afk.kemonos.ui.presenter.changeSite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

@Composable
fun <S : UiState, E : UiEvent, F : UiEffect> SiteAwareScreenNavigator(
    viewModel: SiteAwareBaseViewModelNew<S, E, F>,
    content: @Composable (
        state: S,
        effect: Flow<F>,
        site: SelectedSite,
        siteSwitching: Boolean,
        onEventSent: (E) -> Unit
    ) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val site by viewModel.site.collectAsStateWithLifecycle()
    val switching by viewModel.siteSwitching.collectAsStateWithLifecycle()

    content(state, viewModel.effect, site, switching, viewModel::setEvent)
}
