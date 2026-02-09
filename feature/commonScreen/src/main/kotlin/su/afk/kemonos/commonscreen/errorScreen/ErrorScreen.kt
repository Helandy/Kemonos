package su.afk.kemonos.commonscreen.errorScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.commonscreen.errorScreen.ErrorScreenState.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ErrorScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>,
) {
    BaseScreen(
        error = state.error,
        onRetry = { onEvent(Event.Retry) },
        onBack = { onEvent(Event.Back) },
    ) {}
}
