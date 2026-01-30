package su.afk.kemonos.creatorPost.presenter

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.R
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.shared.ShareActions
import su.afk.kemonos.common.shared.view.SharedActionButton
import su.afk.kemonos.common.toast.limitForToast
import su.afk.kemonos.common.toast.toast
import su.afk.kemonos.common.util.buildDataUrl
import su.afk.kemonos.common.util.openAudioExternally
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.common.view.button.FavoriteActionButton
import su.afk.kemonos.common.view.creator.header.CreatorHeader
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.*
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.State
import su.afk.kemonos.creatorPost.presenter.view.PostTitleBlock
import su.afk.kemonos.creatorPost.presenter.view.TagsRow
import su.afk.kemonos.creatorPost.presenter.view.attachment.PostAttachmentsSection
import su.afk.kemonos.creatorPost.presenter.view.audio.postAudioSection
import su.afk.kemonos.creatorPost.presenter.view.content.PostContentBlock
import su.afk.kemonos.creatorPost.presenter.view.postCommentsSection
import su.afk.kemonos.creatorPost.presenter.view.preview.postPreviewsSection
import su.afk.kemonos.creatorPost.presenter.view.translate.PostTranslateItem
import su.afk.kemonos.creatorPost.presenter.view.translate.openGoogleTranslate
import su.afk.kemonos.creatorPost.presenter.view.video.postVideosSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorPostScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    var showPreviewFileNames by rememberSaveable(state.postId) { mutableStateOf(false) }

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is Effect.ShowToast -> context.toast(effect.message)
                is Effect.CopyPostLink -> ShareActions.copyToClipboard(context, "Post link", effect.message)
                is Effect.OpenGoogleTranslate -> {
                    openGoogleTranslate(context, effect.text, effect.targetLangTag)
                }

                is Effect.OpenUrl -> {
                    Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        .also(context::startActivity)
                }
                is Effect.OpenAudio -> openAudioExternally(context, effect.url, effect.name, effect.mime)
                is Effect.DownloadToast -> {
                    val safeName = effect.fileName.trim().takeIf { it.isNotBlank() }?.limitForToast()

                    val message = if (safeName != null) {
                        context.getString(
                            R.string.download_started_named,
                            safeName
                        )
                    } else {
                        context.getString(R.string.download_started)
                    }

                    context.toast(message)
                }
            }
        }
    }

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 4.dp),
        isScroll = false,
        floatingActionButtonBottomPadding = 12.dp,
        floatingActionButtonStart = {
            if (!state.loading) {
                SharedActionButton(
                    onClick = { onEvent(Event.CopyPostLinkClicked) }
                )
            }
        },
        floatingActionButtonEnd = {
            if (state.isFavoriteShowButton && state.loading.not()) {
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
        val post = state.post?.post ?: return@BaseScreen

        val profile = state.profile
        val previews = state.post.previews

        val imgBaseUrl = remember(post.service) { resolver.imageBaseUrlByService(post.service) }

        val uniquePreviews = remember(previews) {
            previews.distinctBy { p ->
                when (p.type) {
                    "thumbnail" -> "t:${p.path}"
                    "embed" -> "e:${p.url}"
                    else -> "${p.type}:${p.path}:${p.url}"
                }
            }
        }

        val listState = rememberSaveable(
            state.postId,
            saver = LazyListState.Saver
        ) { LazyListState() }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "HeaderBlock") {
                /** Шапка автора */
                if (state.showBarCreator && profile != null) {
                    CreatorHeader(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        service = profile.service,
                        creatorId = profile.id,
                        creatorName = profile.name,
                        updated = profile.updated,
                        showSearchButton = false,
                        showInfoButton = false,
                        onSearchClick = {},
                        onClickHeader = { onEvent(Event.CreatorHeaderClicked) }
                    )
                }
            }

            item(key = "TitleBlock") {
                /** Заголовок поста */
                PostTitleBlock(
                    title = post.title,
                )
            }

            item(key = "translate") {
                PostTranslateItem(
                    dateMode = state.uiSettingModel.dateFormatMode,
                    published = post.published,
                    edited = post.edited,
                    added = post.added,
                    body = post.content.orEmpty(),

                    expanded = state.translateExpanded,
                    loading = state.translateLoading,
                    translated = state.translateText,
                    error = state.translateError,

                    showButtonTranslate = state.showButtonTranslate,
                    onToggleTranslate = { onEvent(Event.ToggleTranslate) }
                )
            }

            item(key = "contentBlock") {
                /** Контент поста */
                PostContentBlock(
                    blocks = state.contentBlocks,
                    onOpenImage = { url -> onEvent(Event.OpenImage(url)) }
                )
            }

            postPreviewsSection(
                previews = uniquePreviews,
                imgBaseUrl = imgBaseUrl,
                showNames = showPreviewFileNames,
                onTogglePreviewNames = { showPreviewFileNames = !showPreviewFileNames },
                onOpenImage = { url -> onEvent(Event.OpenImage(url)) },
                onOpenUrl = { url -> onEvent(Event.OpenExternalUrl(url)) },
                download = { fullUrl, fileName ->
                    onEvent(Event.Download(fullUrl, fileName))
                },
            )

            postVideosSection(
                videos = state.post.videos,
                videoThumbs = state.videoThumbs,
                requestThumb = { server, path ->
                    onEvent(Event.VideoThumbRequested(server = server, path = path))
                },
                videoInfo = state.videoInfo,
                onVideoInfoRequested = { server, path ->
                    onEvent(Event.VideoInfoRequested(server = server, path = path))
                },
                onDownload = { url, fileName ->
                    onEvent(Event.Download(url, fileName))
                }
            )

            postAudioSection(
                attachments = state.post.attachments,
                audioInfo = state.audioInfo,
                onInfoRequested = { url -> onEvent(Event.AudioInfoRequested(url)) },
                onPlay = { att ->
                    val url = att.buildDataUrl()
                    onEvent(Event.PlayAudio(url, att.name))
                },
                onDownload = { att ->
                    val url = att.buildDataUrl()
                    onEvent(Event.Download(url, att.name))
                }
            )

            item(key = "tags") {
                TagsRow(tags = post.tags)
            }

            item(key = "attachments") {
                PostAttachmentsSection(
                    attachments = state.post.attachments,
                    onAttachmentClick = { url ->
                        Intent(Intent.ACTION_VIEW, url.toUri())
                            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            .also(context::startActivity)
                    }
                )
            }

            postCommentsSection(
                dateMode = state.uiSettingModel.dateFormatMode,
                commentDomains = state.commentDomains
            )

            item {
                Spacer(Modifier.height(56.dp))
            }
        }
    }
}

@Preview("PreviewCreatorPostScreen")
@Composable
private fun PreviewCreatorPostScreen() {
    KemonosPreviewScreen {
        CreatorPostScreen(
            state = State().copy(loading = false),
            onEvent = {},
            effect = emptyFlow()
        )
    }
}