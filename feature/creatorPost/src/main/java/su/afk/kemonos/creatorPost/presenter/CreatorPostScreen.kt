package su.afk.kemonos.creatorPost.presenter

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.*
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.State
import su.afk.kemonos.creatorPost.presenter.helper.copyTextToClipboard
import su.afk.kemonos.creatorPost.presenter.view.screen.CreatorPostContentView
import su.afk.kemonos.creatorPost.presenter.view.state.rememberCreatorPostSectionState
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.button.FavoriteActionButton
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.preview.KemonosPreviewScreen
import su.afk.kemonos.ui.shared.ShareActions
import su.afk.kemonos.ui.shared.openRemoteAudioInExternalApp
import su.afk.kemonos.ui.shared.shareRemoteMedia
import su.afk.kemonos.ui.toast.toast
import su.afk.kemonos.ui.translate.openGoogleTranslate

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CreatorPostScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<Effect>
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    var showPreviewFileNames by rememberSaveable(state.postId) { mutableStateOf(false) }
    val sectionState = rememberCreatorPostSectionState(postId = state.postId)

    collectCreatorPostEffects(
        effect = effect,
        context = context,
    )

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 4.dp),
        isScroll = false,
        floatingActionButtonEnd = {
            if (state.isFavoriteShowButton && !state.loading) {
                FavoriteActionButton(
                    enabled = !state.favoriteActionLoading,
                    isFavorite = state.isFavorite,
                    onFavoriteClick = { onEvent(Event.FavoriteClicked) }
                )
            }
        },
        isLoading = state.loading,
        isEmpty = state.post == null && !state.loading,
        onRetry = { onEvent(Event.Retry) }
    ) {
        CreatorPostContentView(
            state = state,
            onEvent = onEvent,
            sectionState = sectionState,
            showPreviewFileNames = showPreviewFileNames,
            onTogglePreviewFileNames = { showPreviewFileNames = !showPreviewFileNames },
            onCopyOriginalText = { text ->
                scope.launch {
                    copyTextToClipboard(
                        clipboard = clipboard,
                        label = "post",
                        text = text,
                    )
                }
            },
            onShareRemote = { url, fileName, mime ->
                launchShare(
                    scope = scope,
                    context = context,
                    onEvent = onEvent,
                    shareInProgress = state.shareInProgress,
                    url = url,
                    fileName = fileName,
                    mime = mime,
                )
            },
            shareInProgress = state.shareInProgress,
            shareBytesRead = state.shareBytesRead,
            shareTotalBytes = state.shareTotalBytes,
        )
    }
}

@Composable
private fun collectCreatorPostEffects(
    effect: Flow<Effect>,
    context: android.content.Context,
) {
    LaunchedEffect(effect, context) {
        effect.collect { item ->
            when (item) {
                is Effect.ShowToast -> context.toast(item.message)
                is Effect.CopyPostLink -> {
                    ShareActions.copyToClipboard(context, "Post link", item.message)
                }

                is Effect.OpenGoogleTranslate -> {
                    openGoogleTranslate(context, item.text, item.targetLangTag)
                }

                is Effect.OpenAudio -> {
                    val opened = openRemoteAudioInExternalApp(
                        context = context,
                        url = item.url,
                        fileName = item.name,
                        mime = item.mime
                    )
                    if (!opened) {
                        context.toast(context.getString(R.string.audio_open_failed))
                    }
                }

                is Effect.DownloadToast -> {
                    val safeName = item.fileName.trim().takeIf { it.isNotBlank() }
                    val message = if (safeName != null) {
                        context.getString(R.string.download_started_named, safeName)
                    } else {
                        context.getString(R.string.download_started)
                    }
                    context.toast(message)
                }
            }
        }
    }
}

private fun launchShare(
    scope: CoroutineScope,
    context: android.content.Context,
    onEvent: (Event) -> Unit,
    shareInProgress: Boolean,
    url: String,
    fileName: String?,
    mime: String,
) {
    if (shareInProgress) return

    scope.launch {
        onEvent(Event.ShareStarted)
        val shared = try {
            shareRemoteMedia(
                context = context,
                url = url,
                fileName = fileName,
                mime = mime,
                onProgress = { bytesRead, totalBytes ->
                    onEvent(Event.ShareProgress(bytesRead = bytesRead, totalBytes = totalBytes))
                },
            )
        } finally {
            onEvent(Event.ShareFinished)
        }

        if (!shared) {
            context.toast(context.getString(R.string.share_failed))
        }
    }
}

@Preview("PreviewCreatorPostScreen")
@Composable
private fun PreviewCreatorPostScreen() {
    KemonosPreviewScreen {
        CreatorPostScreen(
            state = State.default().copy(loading = false),
            onEvent = {},
            effect = emptyFlow()
        )
    }
}
