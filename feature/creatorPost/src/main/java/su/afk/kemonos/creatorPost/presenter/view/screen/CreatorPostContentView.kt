package su.afk.kemonos.creatorPost.presenter.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorPost.api.domain.model.PostContentRevisionDomain
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.Event
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.State
import su.afk.kemonos.creatorPost.presenter.view.attachment.PostAttachmentsSection
import su.afk.kemonos.creatorPost.presenter.view.audio.distinctAudioAttachments
import su.afk.kemonos.creatorPost.presenter.view.audio.postAudioSection
import su.afk.kemonos.creatorPost.presenter.view.comments.postCommentsSection
import su.afk.kemonos.creatorPost.presenter.view.content.postContentSection
import su.afk.kemonos.creatorPost.presenter.view.header.PostTitleBlock
import su.afk.kemonos.creatorPost.presenter.view.incompleteRewards.incompleteRewardsBlock
import su.afk.kemonos.creatorPost.presenter.view.poll.PollBlock
import su.afk.kemonos.creatorPost.presenter.view.preview.postPreviewsSection
import su.afk.kemonos.creatorPost.presenter.view.preview.previewKey
import su.afk.kemonos.creatorPost.presenter.view.section.CollapsibleSectionHeader
import su.afk.kemonos.creatorPost.presenter.view.section.PostRevisionSwitcher
import su.afk.kemonos.creatorPost.presenter.view.state.CreatorPostSectionState
import su.afk.kemonos.creatorPost.presenter.view.swipe.SwipeArrowHint
import su.afk.kemonos.creatorPost.presenter.view.swipe.SwipeHintDirection
import su.afk.kemonos.creatorPost.presenter.view.swipe.rememberTikTokSwipeState
import su.afk.kemonos.creatorPost.presenter.view.tags.TagsRow
import su.afk.kemonos.creatorPost.presenter.view.translate.PostTranslateItem
import su.afk.kemonos.creatorPost.presenter.view.video.postVideosSection
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.creator.header.CreatorHeader
import su.afk.kemonos.ui.shared.view.ShareLoadingOverlay
import su.afk.kemonos.ui.uiUtils.format.audioMimeType
import su.afk.kemonos.utils.url.buildContentUrlToDataSite
import kotlin.math.roundToInt

@Composable
internal fun CreatorPostContentView(
    state: State,
    onEvent: (Event) -> Unit,
    sectionState: CreatorPostSectionState,
    showPreviewFileNames: Boolean,
    onTogglePreviewFileNames: () -> Unit,
    onCopyOriginalText: (String) -> Unit,
    onShareRemote: (url: String, fileName: String?, mime: String) -> Unit,
    shareInProgress: Boolean,
    shareBytesRead: Long,
    shareTotalBytes: Long,
) {
    val resolvedPost = state.post ?: return
    val post = resolvedPost.post
    val profile = state.profile
    val blocks = state.contentBlocks.orEmpty()
    val resolver = LocalDomainResolver.current

    val imgBaseUrl = remember(post.service) { resolver.imageBaseUrlByService(post.service) }
    val fallbackBaseUrl = remember(post.service) { resolver.baseUrlByService(post.service) }
    val uniquePreviews = remember(resolvedPost.previews) {
        resolvedPost.previews.distinctBy { it.previewKey() }
    }
    val uniqueVideos = remember(resolvedPost.videos) {
        resolvedPost.videos.distinctBy { "video:${it.server}:${it.path}" }
    }
    val uniqueAudios = remember(resolvedPost.attachments) {
        resolvedPost.attachments.distinctAudioAttachments()
    }

    val hasComments = state.uiSettingModel.showCommentsInPost &&
            state.commentDomains.firstOrNull()?.id?.isNotBlank() == true
    val hasTags = !post.tags.isNullOrEmpty()
    val hasAttachments = resolvedPost.attachments.isNotEmpty()

    val canPrevPost = post.prevId != null
    val canNextPost = post.nextId != null
    val currentRevisionLabel = stringResource(R.string.post_version_current)
    val revisionsById = remember(state.sourcePost?.revisions) {
        state.sourcePost?.revisions?.associateBy { it.revisionId }.orEmpty()
    }

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
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, swipe.dragOffsetPx.roundToInt()) },
        ) {
            item(key = "HeaderBlock") {
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
                PostTitleBlock(
                    title = post.title,
                    showPreviewNames = showPreviewFileNames,
                    onTogglePreviewNames = onTogglePreviewFileNames,
                    onDownloadAllClick = { onEvent(Event.DownloadAllClicked) },
                    onShareClick = { onEvent(Event.CopyPostLinkClicked) },
                    onCopyOriginalClick = { onCopyOriginalText(post.content.orEmpty()) },
                    showCreatorBannerAction = !state.showBarCreator,
                    onShowCreatorBannerClick = { onEvent(Event.ShowCreatorBanner) },
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
                        revisionLabel = { revisionId ->
                            buildRevisionLabel(
                                revisionId = revisionId,
                                currentRevisionLabel = currentRevisionLabel,
                                revisionsById = revisionsById,
                            )
                        },
                        selectedRevisionId = state.selectedRevisionId,
                        onSelectRevision = { revisionId ->
                            onEvent(Event.SelectRevision(revisionId))
                        }
                    )
                }
            }

            item(key = "incompleteRewards") {
                val rewards = resolvedPost.post.incompleteRewards ?: return@item
                incompleteRewardsBlock(rewards)
            }

            item(key = "poll") {
                val poll = resolvedPost.post.poll ?: return@item
                PollBlock(
                    poll = poll,
                    dateMode = state.uiSettingModel.dateFormatMode,
                )
            }

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
                        expanded = sectionState.previewsExpanded,
                        onToggle = { sectionState.previewsExpanded = !sectionState.previewsExpanded }
                    )
                }
                if (sectionState.previewsExpanded) {
                    postPreviewsSection(
                        uiSettingModel = state.uiSettingModel,
                        previews = uniquePreviews,
                        imgBaseUrl = imgBaseUrl,
                        showNames = showPreviewFileNames,
                        onOpenImage = { url -> onEvent(Event.OpenImage(url)) },
                        download = { fullUrl, fileName ->
                            onEvent(Event.Download(fullUrl, fileName))
                        },
                        share = { fullUrl, fileName ->
                            onShareRemote(fullUrl, fileName, "image/*")
                        }
                    )
                }
            }

            if (uniqueVideos.isNotEmpty()) {
                item(key = "videos_toggle") {
                    CollapsibleSectionHeader(
                        title = stringResource(R.string.video_section),
                        expanded = sectionState.videosExpanded,
                        onToggle = { sectionState.videosExpanded = !sectionState.videosExpanded }
                    )
                }
                if (sectionState.videosExpanded) {
                    postVideosSection(
                        uiSettingModel = state.uiSettingModel,
                        requestKey = state.selectedRevisionId,
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
                        expanded = sectionState.audioExpanded,
                        onToggle = { sectionState.audioExpanded = !sectionState.audioExpanded }
                    )
                }
                if (sectionState.audioExpanded) {
                    postAudioSection(
                        audios = uniqueAudios,
                        fallbackBaseUrl = fallbackBaseUrl,
                        audioInfo = state.audioInfo,
                        onInfoRequested = { url -> onEvent(Event.AudioInfoRequested(url)) },
                        onPlay = { att ->
                            val url = att.buildContentUrlToDataSite(fallbackBaseUrl)
                            onEvent(
                                Event.PlayAudio(
                                    url = url,
                                    name = att.name,
                                    mime = audioMimeType(att.path)
                                )
                            )
                        },
                        onDownload = { att ->
                            val url = att.buildContentUrlToDataSite(fallbackBaseUrl)
                            onEvent(Event.Download(url, att.name))
                        },
                        onShare = { att ->
                            val url = att.buildContentUrlToDataSite(fallbackBaseUrl)
                            onShareRemote(
                                url,
                                att.name,
                                audioMimeType(att.path)
                            )
                        },
                        showHeader = false,
                    )
                }
            }

            if (hasTags) {
                item(key = "tags_toggle") {
                    CollapsibleSectionHeader(
                        title = stringResource(R.string.tags),
                        expanded = sectionState.tagsExpanded,
                        onToggle = { sectionState.tagsExpanded = !sectionState.tagsExpanded }
                    )
                }
                if (sectionState.tagsExpanded) {
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
                        title = stringResource(
                            R.string.attachment_section,
                            resolvedPost.attachments.size
                        ),
                        expanded = sectionState.attachmentsExpanded,
                        onToggle = { sectionState.attachmentsExpanded = !sectionState.attachmentsExpanded }
                    )
                }
                if (sectionState.attachmentsExpanded) {
                    item(key = "attachments") {
                        PostAttachmentsSection(
                            attachments = resolvedPost.attachments,
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
                        expanded = sectionState.commentsExpanded,
                        onToggle = { sectionState.commentsExpanded = !sectionState.commentsExpanded }
                    )
                }
                if (sectionState.commentsExpanded) {
                    postCommentsSection(
                        dateMode = state.uiSettingModel.dateFormatMode,
                        commentDomains = state.commentDomains,
                        showHeader = false,
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(Modifier.height(72.dp))
            }
        }

        ShareLoadingOverlay(
            visible = shareInProgress,
            bytesRead = shareBytesRead,
            totalBytes = shareTotalBytes
        )

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

private fun buildRevisionLabel(
    revisionId: Int?,
    currentRevisionLabel: String,
    revisionsById: Map<Int, PostContentRevisionDomain>,
): String {
    if (revisionId == null) return currentRevisionLabel

    val revision = revisionsById[revisionId]
    val yearMonth = revision
        ?.post
        ?.published
        ?.takeIf { it.length >= 7 }
        ?.take(7)
        ?: revision?.post?.added
            ?.takeIf { it.length >= 7 }
            ?.take(7)
        ?: revision?.post?.edited
            ?.takeIf { it.length >= 7 }
            ?.take(7)
        ?: "unknown"

    return buildString {
        append(yearMonth)
        append(" <")
        append(revisionId)
        append(">")
        revision?.backendRevisionId?.let {
            append(" ")
            append(it)
        }
    }
}
