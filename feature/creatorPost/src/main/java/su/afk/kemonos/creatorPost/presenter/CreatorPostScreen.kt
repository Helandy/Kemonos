package su.afk.kemonos.creatorPost.presenter

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.views.block.PostContentBlock
import su.afk.kemonos.common.presenter.views.block.PostTitleBlock
import su.afk.kemonos.common.presenter.views.creator.CreatorHeader
import su.afk.kemonos.common.presenter.views.elements.FavoriteActionButton
import su.afk.kemonos.common.shared.view.SharedActionButton
import su.afk.kemonos.common.util.download.enqueueSystemDownload
import su.afk.kemonos.common.util.selectDomain.getImageBaseUrlByService
import su.afk.kemonos.common.util.toast
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.presenter.view.PostAttachmentsSection
import su.afk.kemonos.creatorPost.presenter.view.PostCommentsSection
import su.afk.kemonos.creatorPost.presenter.view.TagsRow
import su.afk.kemonos.creatorPost.presenter.view.preview.EmbedPreviewItem
import su.afk.kemonos.creatorPost.presenter.view.preview.ThumbnailPreviewItem
import su.afk.kemonos.creatorPost.presenter.view.video.PostVideosSection
import su.afk.kemonos.domain.domain.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorPostScreen(
    viewModel: CreatorPostViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 6.dp),
        isScroll = false,
        floatingActionButtonBottomPadding = 12.dp,
        floatingActionButtonStart = {
            if (!state.loading) {
                SharedActionButton(
                    onClick = { viewModel.copyPostLink(context) }
                )
            }
        },
        floatingActionButton = {
            if (state.isFavoriteShowButton && state.loading.not()) {
                FavoriteActionButton(
                    isFavorite = state.isFavorite,
                    onFavoriteClick = { viewModel.onFavoriteClick() }
                )
            }
        },
        isLoading = state.loading,
        isEmpty = state.post == null,
        onRetry = { viewModel.loadingPost() }
    ) {
        Content(
            showCreatorBar = state.showBarCreator,
            profile = state.profile,
            post = state.post?.post,
            previews = state.post?.previews,
            attachments = state.post?.attachments,
            video = state.post?.videos,
            commentDomains = state.commentDomains,
            viewModel = viewModel
        )
    }
}


@Composable
private fun Content(
    showCreatorBar: Boolean,
    profile: Profile?,
    post: PostDomain?,
    previews: List<PreviewDomain>?,
    attachments: List<AttachmentDomain>?,
    video: List<VideoDomain>?,
    commentDomains: List<CommentDomain>,
    viewModel: CreatorPostViewModel,
) {
    val post = post ?: return
    val context = LocalContext.current

    val imgBaseUrl = remember(post.service) { getImageBaseUrlByService(post.service) }
    val uniquePreviews = remember(previews) {
        previews.orEmpty().distinctBy { p ->
            when (p.type) {
                "thumbnail" -> "t:${p.path}"
                "embed" -> "e:${p.url}"
                else -> "${p.type}:${p.path}:${p.url}"
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {

        item(key = "HeaderBlock") {
            /** Шапка автора */
            if (showCreatorBar && profile != null) {
                CreatorHeader(
                    service = profile.service,
                    creatorId = profile.id,
                    creatorName = profile.name,
                    updated = profile.updated,
                    showSearchButton = false,
                    onSearchClick = {},
                    onClickHeader = viewModel::navigateToCreatorProfile
                )
            } else if (showCreatorBar) {
                Spacer(Modifier.height(56.dp))
            }
        }

        item(key = "TitleBlock") {
            /** Заголовок поста */
            PostTitleBlock(
                title = post.title.orEmpty(),
                published = post.published,
                edited = post.edited,
                added = post.added,
            )
        }

        item(key = "contentBlock") {
            /** Контент поста */
            PostContentBlock(
                service = post.service,
                body = post.content.orEmpty()
            )
        }

        items(
            items = uniquePreviews,
            key = { p ->
                when (p.type) {
                    "thumbnail" -> "t:${p.path}"
                    "embed" -> "e:${p.url}"
                    else -> "${p.type}:${p.path}:${p.url}"
                }
            }
        ) { preview ->
            when (preview.type) {
                "thumbnail" -> ThumbnailPreviewItem(
                    preview = preview,
                    imgBaseUrl = imgBaseUrl,
                    onPreviewClick = viewModel::navigateOpenImage,
                    onDownloadClick = { fullUrl, fileName ->
                        enqueueSystemDownload(
                            context = context,
                            url = fullUrl,
                            fileName = fileName,
                            mimeType = null,
                        )

                        val msg = if (fileName.isBlank()) {
                            context.getString(R.string.download_started)
                        } else {
                            context.getString(R.string.download_started_named, fileName)
                        }
                        context.toast(msg)
                    }
                )

                "embed" -> EmbedPreviewItem(
                    preview = preview,
                    onEmbedClick = { url ->
                        Intent(Intent.ACTION_VIEW, url.toUri())
                            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            .also(context::startActivity)
                    }
                )
            }
        }


        item(key = "videos") {
            PostVideosSection(
                videos = video.orEmpty(),
                observeVideoInfo = viewModel::observeVideoInfo,
            )
        }

        item(key = "tags") {
            TagsRow(tags = post.tags)
        }

        item(key = "attachments") {
            PostAttachmentsSection(
                attachments = attachments.orEmpty(),
                onAttachmentClick = { url ->
                    Intent(Intent.ACTION_VIEW, url.toUri())
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        .also(context::startActivity)
                }
            )
        }

        item(key = "comments") {
            PostCommentsSection(commentDomains)
        }
    }
}