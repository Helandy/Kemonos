package su.afk.kemonos.creatorPost.presenter

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
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
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.button.FavoriteActionButton
import su.afk.kemonos.ui.components.creator.header.CreatorHeader
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.preview.KemonosPreviewScreen
import su.afk.kemonos.ui.shared.ShareActions
import su.afk.kemonos.ui.toast.toast
import su.afk.kemonos.ui.uiUtils.format.isAudioFile
import su.afk.kemonos.ui.uiUtils.format.openAudioExternally
import su.afk.kemonos.utils.url.buildContentUrlToDataSite
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorPostScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
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
                is Effect.OpenAudio -> openAudioExternally(context, effect.url, effect.name, effect.mime)
                is Effect.DownloadToast -> {
                    val safeName = effect.fileName.trim().takeIf { it.isNotBlank() }

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
        val hasComments = state.uiSettingModel.showCommentsInPost &&
                state.commentDomains.isNotEmpty() &&
                !state.commentDomains.firstOrNull()?.id.isNullOrBlank()
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
            val showCreatorHeader = state.showBarCreator && profile != null

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
                    .offset { IntOffset(0, swipe.dragOffsetPx.roundToInt()) },
            ) {
                item(key = "HeaderBlock") {
                    /** Шапка автора */
                    if (showCreatorHeader) {
                        CreatorHeader(
                            service = profile.service,
                            creatorId = profile.id,
                            creatorName = profile.name,
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
                        onDownloadAllClick = { onEvent(Event.DownloadAllClicked) },
                        onShareClick = { onEvent(Event.CopyPostLinkClicked) },
                        onCopyOriginalClick = {
                            scope.launch {
                                val clip = ClipData.newPlainText("post", post.content.orEmpty())
                                clipboard.setClipEntry(ClipEntry(clip))
                            }
                        },
                        onBackClick = { onEvent(Event.Back) }
                    )
                }

                item(key = "translate") {
                    PostTranslateItem(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        published = post.published,
                        edited = post.edited,
                        added = post.added,
                        expanded = state.translateExpanded,
                        loading = state.translateLoading,
                        translated = state.translateText,
                        error = state.translateError,

                        showButtonTranslate = state.showButtonTranslate,
                        onToggleTranslate = { onEvent(Event.ToggleTranslate) }
                    )
                }

                if (state.revisionIds.size > 1) {
                    item(key = "revision_switcher") {
                        PostRevisionSwitcher(
                            revisionIds = state.revisionIds,
                            selectedRevisionId = state.selectedRevisionId,
                            onSelectRevision = { revisionId ->
                                onEvent(Event.SelectRevision(revisionId))
                            }
                        )
                    }
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
                            videos = uniqueVideos,
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
                            fallbackBaseUrl = fallbackBaseUrl,
                            audioInfo = state.audioInfo,
                            onInfoRequested = { url -> onEvent(Event.AudioInfoRequested(url)) },
                            onPlay = { att ->
                                val url = att.buildContentUrlToDataSite(fallbackBaseUrl)
                                onEvent(Event.PlayAudio(url, att.name))
                            },
                            onDownload = { att ->
                                val url = att.buildContentUrlToDataSite(fallbackBaseUrl)
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
                            TagsRow(
                                tags = post.tags,
                                showHeader = false,
                                onTagClick = { tag -> onEvent(Event.TagClicked(tag)) }
                            )
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
                                onAttachmentClick = { url, fileName ->
                                    onEvent(Event.Download(url, fileName))
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
private fun PostRevisionSwitcher(
    revisionIds: List<Int?>,
    selectedRevisionId: Int?,
    onSelectRevision: (Int?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.post_version_title),
            style = MaterialTheme.typography.titleSmall,
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            revisionIds.forEach { revisionId ->
                val isSelected = revisionId == selectedRevisionId
                val label = if (revisionId == null) {
                    stringResource(R.string.post_version_current)
                } else {
                    stringResource(R.string.post_version_revision, revisionId)
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectRevision(revisionId) },
                    label = { Text(text = label) },
                )
            }
        }
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
