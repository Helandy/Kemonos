package su.afk.kemonos.commonscreen.errorScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ErrorScreen(viewModel: ErrorViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BaseScreen(
        error = state.error,
        onRetry = viewModel::retry,
        onBack = viewModel::back,
    ) {}
}