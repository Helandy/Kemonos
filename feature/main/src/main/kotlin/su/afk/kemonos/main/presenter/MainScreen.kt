package su.afk.kemonos.main.presenter

import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.toast.toast
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.main.presenter.MainState.Effect
import su.afk.kemonos.main.presenter.MainState.Event
import su.afk.kemonos.main.presenter.view.*
import java.io.File

@Composable
internal fun MainScreen(
    state: MainState.State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        effect.collect { item ->
            when (item) {
                is Effect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, item.url.toUri())
                    context.startActivity(intent)
                }

                is Effect.SaveCrashReportToDevice -> {
                    runCatching { saveCrashReportToDownloads(context, item.path) }
                        .onSuccess {
                            context.toast(
                                text = context.getString(R.string.crash_report_saved_success),
                                long = true
                            )
                            onEvent(Event.CrashReportShared(item.path))
                        }
                        .onFailure {
                            onEvent(Event.CrashReportShareFailed(item.path))
                        }
                }
            }
        }
    }

    val updateInfo = state.updateInfo

    val showErrorScreen = updateInfo == null && !state.isLoading && state.apiSuccess != true

    if (state.pendingCrashPath != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.crash_report_dialog_title)) },
            text = { Text(stringResource(R.string.crash_report_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = { onEvent(Event.CrashReportSaveToDevice) }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onEvent(Event.CrashReportDelete) }
                ) {
                    Text(stringResource(R.string.crash_report_dialog_delete))
                }
            },
        )
    }

    BaseScreen(
        isScroll = showErrorScreen,
        contentPadding = PaddingValues(12.dp),
        contentAlignment = Alignment.TopCenter,
        floatingActionButtonEnd = if (showErrorScreen) {
            {
                Box(Modifier.padding(12.dp)) {
                    MainActions(
                        onSkipCheck = { onEvent(Event.SkipCheck) },
                        onSaveAndCheck = { onEvent(Event.SaveAndCheck) },
                    )
                }
            }
        } else null,
    ) {
        when {
            updateInfo != null -> MainUpdateBanner(
                info = updateInfo,
                onUpdateClick = { onEvent(Event.UpdateClick(updateInfo)) },
                onLaterClick = { onEvent(Event.UpdateLaterClick) }
            )

            state.isLoading -> MainLoading()

            state.apiSuccess == true -> MainSuccess()

            else -> {
                MainApiUnavailableContent(
                    state = state,
                    onInputKemonoChanged = { onEvent(Event.InputKemonoDomainChanged(it)) },
                    onInputCoomerChanged = { onEvent(Event.InputCoomerDomainChanged(it)) },
                )

                Spacer(Modifier.height(96.dp))
            }
        }
    }
}

private fun saveCrashReportToDownloads(
    context: android.content.Context,
    sourcePath: String,
) {
    val crashFile = File(sourcePath)
    check(crashFile.exists()) { "Crash file does not exist" }

    val targetName = if (crashFile.name.startsWith("crash_")) {
        crashFile.name.replaceFirst("crash_", "kemono_crash_")
    } else {
        "kemono_crash_${System.currentTimeMillis()}.txt"
    }

    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, targetName)
        put(MediaStore.Downloads.MIME_TYPE, "text/plain")
        put(MediaStore.Downloads.RELATIVE_PATH, "Download")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
        values,
    ) ?: error("Failed to create destination file")

    context.contentResolver.openOutputStream(uri)?.use { output ->
        crashFile.inputStream().use { input -> input.copyTo(output) }
    } ?: error("Failed to open destination stream")
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
    MainScreen(
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
        onEvent = {},
        effect = emptyFlow()
    )
}

@Preview(name = "Main • Loading", showBackground = true)
@Composable
private fun MainScreenPreview_Loading() = PreviewHost {
    MainScreen(
        state = MainState.State(
            kemonoUrl = "https://kemono.cr",
            coomerUrl = "https://coomer.st",
            inputKemonoDomain = "kemono.cr",
            inputCoomerDomain = "coomer.st",
        ),
        onEvent = {},
        effect = emptyFlow()
    )
}

@Preview(name = "Main • Success", showBackground = true)
@Composable
private fun MainScreenPreview_Success() = PreviewHost {
    MainScreen(
        state = MainState.State(
            isLoading = false,
            apiSuccess = true,
            kemonoUrl = "https://kemono.cr",
            coomerUrl = "https://coomer.st",
            inputKemonoDomain = "kemono.cr",
            inputCoomerDomain = "coomer.st",
        ),
        onEvent = {},
        effect = emptyFlow()
    )
}

@Preview(name = "Main • API unavailable", showBackground = true)
@Composable
private fun MainScreenPreview_Error() = PreviewHost {
    MainScreen(
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
        onEvent = {},
        effect = emptyFlow()
    )
}
