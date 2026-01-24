package su.afk.kemonos.main.presenter

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.main.presenter.view.*

@Composable
internal fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainState.MainEffect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    MainScreenContent(
        state = state,
        onUpdateClick = viewModel::onUpdateClick,
        onLaterClick = viewModel::onUpdateLaterClick,
        onSkipCheck = viewModel::onSkipCheck,
        onSaveAndCheck = viewModel::onSaveAndCheck,
        onInputKemonoChanged = viewModel::onInputKemonoDomainChanged,
        onInputCoomerChanged = viewModel::onInputCoomerDomainChanged,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreenContent(
    state: MainState.State,
    onUpdateClick: (AppUpdateInfo) -> Unit,
    onLaterClick: () -> Unit,
    onSkipCheck: () -> Unit,
    onSaveAndCheck: () -> Unit,
    onInputKemonoChanged: (String) -> Unit,
    onInputCoomerChanged: (String) -> Unit,
) {
    val updateInfo = state.updateInfo

    val showErrorScreen = updateInfo == null && !state.isLoading && state.apiSuccess != true

    BaseScreen(
        isScroll = showErrorScreen,
        contentPadding = PaddingValues(12.dp),
        contentAlignment = Alignment.TopCenter,
        floatingActionButtonEnd = if (showErrorScreen) {
            {
                Box(Modifier.padding(12.dp)) {
                    MainActions(
                        onSkipCheck = onSkipCheck,
                        onSaveAndCheck = onSaveAndCheck,
                    )
                }
            }
        } else null,
    ) {
        when {
            updateInfo != null -> MainUpdateBanner(
                info = updateInfo,
                onUpdateClick = { onUpdateClick(updateInfo) },
                onLaterClick = onLaterClick
            )

            state.isLoading -> MainLoading()

            state.apiSuccess == true -> MainSuccess()

            else -> {
                MainApiUnavailableContent(
                    state = state,
                    onInputKemonoChanged = onInputKemonoChanged,
                    onInputCoomerChanged = onInputCoomerChanged,
                )

                Spacer(Modifier.height(96.dp))
            }
        }
    }
}


@Composable
private fun PreviewHost(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface { content() }
    }
}

@Preview(name = "Main • Update banner", showBackground = true)
@Composable
private fun MainScreenPreview_Update() = PreviewHost {
    MainScreenContent(
        state = MainState.State(
            isLoading = false,
            kemonoUrl = "https://kemono.cr",
            coomerUrl = "https://coomer.st",
            inputKemonoDomain = "kemono.cr",
            inputCoomerDomain = "coomer.st",
            updateInfo = AppUpdateInfo(
                latestVersionName = "1.0.0",
                releaseUrl = "url",
                changelog = "changelog",
            ),
        ),
        onUpdateClick = {},
        onLaterClick = {},
        onSkipCheck = {},
        onSaveAndCheck = {},
        onInputKemonoChanged = {},
        onInputCoomerChanged = {},
    )
}

@Preview(name = "Main • Loading", showBackground = true)
@Composable
private fun MainScreenPreview_Loading() = PreviewHost {
    MainScreenContent(
        state = MainState.State(
            kemonoUrl = "https://kemono.cr",
            coomerUrl = "https://coomer.st",
            inputKemonoDomain = "kemono.cr",
            inputCoomerDomain = "coomer.st",
        ),
        onUpdateClick = {},
        onLaterClick = {},
        onSkipCheck = {},
        onSaveAndCheck = {},
        onInputKemonoChanged = {},
        onInputCoomerChanged = {},
    )
}

@Preview(name = "Main • Success", showBackground = true)
@Composable
private fun MainScreenPreview_Success() = PreviewHost {
    MainScreenContent(
        state = MainState.State(
            isLoading = false,
            apiSuccess = true,
            kemonoUrl = "https://kemono.cr",
            coomerUrl = "https://coomer.st",
            inputKemonoDomain = "kemono.cr",
            inputCoomerDomain = "coomer.st",
        ),
        onUpdateClick = {},
        onLaterClick = {},
        onSkipCheck = {},
        onSaveAndCheck = {},
        onInputKemonoChanged = {},
        onInputCoomerChanged = {},
    )
}

@Preview(name = "Main • API unavailable", showBackground = true)
@Composable
private fun MainScreenPreview_Error() = PreviewHost {
    MainScreenContent(
        state = MainState.State(
            isLoading = false,
            kemonoError = ErrorItem(
                title = "API недоступно",
                message = "Не удалось выполнить запрос. Проверь домен или попробуй позже.",
                code = 503,
                method = "GET",
                url = "https://kemono.cr/api/v1/ping",
                requestId = "req_01HXYZ...",
                body = """{"error":"Service Unavailable"}""",
                cause = "Timeout"
            ),
            apiSuccess = false,
            kemonoUrl = "https://kemono.cr",
            coomerUrl = "https://coomer.st",
            inputKemonoDomain = "kemono.cr",
            inputCoomerDomain = "coomer.st",
        ),
        onUpdateClick = {},
        onLaterClick = {},
        onSkipCheck = {},
        onSaveAndCheck = {},
        onInputKemonoChanged = {},
        onInputCoomerChanged = {},
    )
}