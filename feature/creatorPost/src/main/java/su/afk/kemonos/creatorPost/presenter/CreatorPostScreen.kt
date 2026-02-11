package su.afk.kemonos.creatorPost.presenter

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.R
import su.afk.kemonos.common.components.button.FavoriteActionButton
import su.afk.kemonos.common.components.creator.header.CreatorHeader
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.shared.ShareActions
import su.afk.kemonos.common.toast.limitForToast
import su.afk.kemonos.common.toast.toast
import su.afk.kemonos.common.util.buildDataUrl
import su.afk.kemonos.common.util.isAudioFile
import su.afk.kemonos.common.util.openAudioExternally
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.*
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.State
import su.afk.kemonos.creatorPost.presenter.view.PostTitleBlock
import su.afk.kemonos.creatorPost.presenter.view.TagsRow
import su.afk.kemonos.creatorPost.presenter.view.attachment.PostAttachmentsSection
import su.afk.kemonos.creatorPost.presenter.view.audio.postAudioSection
import su.afk.kemonos.creatorPost.presenter.view.comments.postCommentsSection
import su.afk.kemonos.creatorPost.presenter.view.content.postContentSection
import su.afk.kemonos.creatorPost.presenter.view.incompleteRewards.incompleteRewardsBlock
import su.afk.kemonos.creatorPost.presenter.view.poll.PollBlock
import su.afk.kemonos.creatorPost.presenter.view.preview.postPreviewsSection
import su.afk.kemonos.creatorPost.presenter.view.swipe.SwipeArrowHint
import su.afk.kemonos.creatorPost.presenter.view.swipe.SwipeHintDirection
import su.afk.kemonos.creatorPost.presenter.view.swipe.rememberTikTokSwipeState
import su.afk.kemonos.creatorPost.presenter.view.translate.PostTranslateItem
import su.afk.kemonos.creatorPost.presenter.view.translate.openGoogleTranslate
import su.afk.kemonos.creatorPost.presenter.view.video.postVideosSection
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorPostScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    var showPreviewFileNames by rememberSaveable(state.postId) { mutableStateOf(false) }
    var previewsExpanded by rememberSaveable(state.postId) { mutableStateOf(true) }
    var videosExpanded by rememberSaveable(state.postId) { mutableStateOf(true) }
    var audioExpanded by rememberSaveable(state.postId) { mutableStateOf(true) }
    var tagsExpanded by rememberSaveable(state.postId) { mutableStateOf(true) }
    var attachmentsExpanded by rememberSaveable(state.postId) { mutableStateOf(true) }
    var commentsExpanded by rememberSaveable(state.postId) { mutableStateOf(true) }

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is Effect.ShowToast -> context.toast(effect.message)
                is Effect.CopyPostLink -> ShareActions.copyToClipboard(context, "Post link", effect.message)
                is Effect.OpenGoogleTranslate -> {
                    openGoogleTranslate(context, effect.text, effect.targetLangTag)
                }

                is Effect.OpenUrl -> {
                    context.openExternalUrlSafely(effect.url)
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
        val blocks = state.contentBlocks.orEmpty()

        val imgBaseUrl = remember(post.service) { resolver.imageBaseUrlByService(post.service) }
        val fallbackBaseUrl = remember(post.service) { resolver.baseUrlByService(post.service) }

        val uniquePreviews = remember(previews) {
            previews.distinctBy { p ->
                when (p.type) {
                    "thumbnail" -> "t:${p.path}"
                    "embed" -> "e:${p.url}"
                    else -> "${p.type}:${p.path}:${p.url}"
                }
            }
        }
        val uniqueVideos = remember(state.post.videos) {
            state.post.videos.distinctBy { "video:${it.server}:${it.path}" }
        }
        val uniqueAudios = remember(state.post.attachments) {
            state.post.attachments
                .asSequence()
                .filter { isAudioFile(it.path) }
                .distinctBy { "${it.server.orEmpty()}|${it.path}" }
                .toList()
        }
        val hasComments = state.commentDomains.isNotEmpty() && !state.commentDomains.firstOrNull()?.id.isNullOrBlank()
        val hasTags = !post.tags.isNullOrEmpty()
        val hasAttachments = state.post.attachments.isNotEmpty()

        val canPrevPost = post.prevId != null
        val canNextPost = post.nextId != null

        val listState = rememberSaveable(
            state.postId,
            saver = LazyListState.Saver
        ) { LazyListState() }

        val swipe = rememberTikTokSwipeState(
            listState = listState,
            threshold = 300.dp,
            dragDamping = 0.55f,

            canSwipeDownAtTop = canPrevPost,
            canSwipeUpAtBottom = canNextPost,

            onSwipeDownAtTop = { onEvent(Event.OpenPrevPost) },
            onSwipeUpAtBottom = { onEvent(Event.OpenNextPost) },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(swipe.modifier)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
                    .offset { IntOffset(0, swipe.dragOffsetPx.roundToInt()) },
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
                        showPreviewNames = showPreviewFileNames,
                        onTogglePreviewNames = { showPreviewFileNames = !showPreviewFileNames },
                        onShareClick = { onEvent(Event.CopyPostLinkClicked) }
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

                item(key = "incompleteRewards") {
                    val rewards = state.post.post.incompleteRewards ?: return@item

                    incompleteRewardsBlock(rewards)
                }

                item(key = "poll") {
                    val poll = state.post.post.poll ?: return@item

                    PollBlock(
                        poll = poll,
                        dateMode = state.uiSettingModel.dateFormatMode,
                    )
                }

                /** Контент поста */
                if (blocks.isNotEmpty()) {
                    postContentSection(
                        blocks = blocks,
                        onOpenImage = { url -> onEvent(Event.OpenImage(url)) }
                    )
                }

                if (uniquePreviews.isNotEmpty()) {
                    item(key = "previews_toggle") {
                        CollapsibleSectionHeader(
                            title = stringResource(R.string.previews_title, uniquePreviews.size),
                            expanded = previewsExpanded,
                            onToggle = { previewsExpanded = !previewsExpanded }
                        )
                    }
                    if (previewsExpanded) {
                        postPreviewsSection(
                            uiSettingModel = state.uiSettingModel,
                            previews = uniquePreviews,
                            imgBaseUrl = imgBaseUrl,
                            showNames = showPreviewFileNames,
                            onOpenImage = { url -> onEvent(Event.OpenImage(url)) },
                            onOpenUrl = { url -> onEvent(Event.OpenExternalUrl(url)) },
                            download = { fullUrl, fileName ->
                                onEvent(Event.Download(fullUrl, fileName))
                            },
                        )
                    }
                }

                if (uniqueVideos.isNotEmpty()) {
                    item(key = "videos_toggle") {
                        CollapsibleSectionHeader(
                            title = stringResource(R.string.video_section),
                            expanded = videosExpanded,
                            onToggle = { videosExpanded = !videosExpanded }
                        )
                    }
                    if (videosExpanded) {
                        postVideosSection(
                            uiSettingModel = state.uiSettingModel,
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
                            },
                            showHeader = false,
                        )
                    }
                }

                if (uniqueAudios.isNotEmpty()) {
                    item(key = "audio_toggle") {
                        CollapsibleSectionHeader(
                            title = stringResource(R.string.audio_file),
                            expanded = audioExpanded,
                            onToggle = { audioExpanded = !audioExpanded }
                        )
                    }
                    if (audioExpanded) {
                        postAudioSection(
                            attachments = state.post.attachments,
                            audioInfo = state.audioInfo,
                            onInfoRequested = { url -> onEvent(Event.AudioInfoRequested(url)) },
                            onPlay = { att ->
                                val url = att.buildDataUrl(fallbackBaseUrl)
                                onEvent(Event.PlayAudio(url, att.name))
                            },
                            onDownload = { att ->
                                val url = att.buildDataUrl(fallbackBaseUrl)
                                onEvent(Event.Download(url, att.name))
                            },
                            showHeader = false,
                        )
                    }
                }

                if (hasTags) {
                    item(key = "tags_toggle") {
                        CollapsibleSectionHeader(
                            title = stringResource(R.string.tags),
                            expanded = tagsExpanded,
                            onToggle = { tagsExpanded = !tagsExpanded }
                        )
                    }
                    if (tagsExpanded) {
                        item(key = "tags") {
                            TagsRow(tags = post.tags, showHeader = false)
                        }
                    }
                }

                if (hasAttachments) {
                    item(key = "attachments_toggle") {
                        CollapsibleSectionHeader(
                            title = stringResource(R.string.attachment_section),
                            expanded = attachmentsExpanded,
                            onToggle = { attachmentsExpanded = !attachmentsExpanded }
                        )
                    }
                    if (attachmentsExpanded) {
                        item(key = "attachments") {
                            PostAttachmentsSection(
                                attachments = state.post.attachments,
                                fallbackBaseUrl = fallbackBaseUrl,
                                onAttachmentClick = { url ->
                                    Log.e("super", "url: $url")
                                    context.openExternalUrlSafely(url)
                                },
                                showHeader = false,
                            )
                        }
                    }
                }

                if (hasComments) {
                    item(key = "comments_toggle") {
                        CollapsibleSectionHeader(
                            title = stringResource(R.string.comments_section),
                            expanded = commentsExpanded,
                            onToggle = { commentsExpanded = !commentsExpanded }
                        )
                    }
                    if (commentsExpanded) {
                        postCommentsSection(
                            dateMode = state.uiSettingModel.dateFormatMode,
                            commentDomains = state.commentDomains,
                            showHeader = false,
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(72.dp))
                }
            }

            /** подсказка свайпа */
            if (swipe.direction == SwipeHintDirection.DOWN && canPrevPost) {
                SwipeArrowHint(
                    modifier = Modifier.align(Alignment.TopCenter),
                    progress = swipe.progress,
                    isDown = true
                )
            } else if (swipe.direction == SwipeHintDirection.UP && canNextPost) {
                SwipeArrowHint(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    progress = swipe.progress,
                    isDown = false
                )
            }
        }
    }
}

private fun Context.openExternalUrlSafely(rawUrl: String) {
    val trimmed = rawUrl.trim()
    if (trimmed.isBlank() || trimmed.startsWith("/")) {
        toast(getString(R.string.error_default))
        return
    }

    val normalized = if (trimmed.startsWith("http://", ignoreCase = true) ||
        trimmed.startsWith("https://", ignoreCase = true)
    ) trimmed else "https://$trimmed"

    val uri = runCatching { normalized.toUri() }.getOrNull()
    if (uri == null) {
        toast(getString(R.string.error_default))
        return
    }

    val chromeIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        setPackage("com.android.chrome")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching { startActivity(chromeIntent) }
        .onFailure {
            val baseIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(baseIntent, getString(R.string.open_with)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            runCatching { startActivity(chooser) }
                .onFailure { toast(getString(R.string.error_default)) }
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

@Composable
private fun CollapsibleSectionHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = stringResource(
                if (expanded) R.string.collapse else R.string.expand
            )
        )
    }
}
