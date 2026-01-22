package su.afk.kemonos.common.presenter.baseScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState

@Composable
fun <S : UiState, E : UiEvent, F : UiEffect> ScreenNavigator(
    viewModel: BaseViewModelNew<S, E, F>,
    content: @Composable (state: S, onEventSent: (E) -> Unit) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    content(state, viewModel::setEvent)
}
