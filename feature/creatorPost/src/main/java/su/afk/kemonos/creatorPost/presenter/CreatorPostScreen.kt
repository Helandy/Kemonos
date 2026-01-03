package su.afk.kemonos.creatorPost.presenter

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import su.afk.kemonos.creatorPost.presenter.view.PostAttachmentsSection
import su.afk.kemonos.creatorPost.presenter.view.TagsRow
import su.afk.kemonos.creatorPost.presenter.view.postCommentsSection
import su.afk.kemonos.creatorPost.presenter.view.preview.postPreviewsSection
import su.afk.kemonos.creatorPost.presenter.view.video.postVideosSection

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
        val post = state.post?.post ?: return@BaseScreen

        val profile = state.profile
        val previews = state.post?.previews

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

        val downloadStarted = stringResource(R.string.download_started)
        val downloadStartedNamed = stringResource(R.string.download_started_named)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "HeaderBlock") {
                /** Шапка автора */
                if (state.showBarCreator && profile != null) {
                    CreatorHeader(
                        service = profile.service,
                        creatorId = profile.id,
                        creatorName = profile.name,
                        updated = profile.updated,
                        showSearchButton = false,
                        showInfoButton = false,
                        onSearchClick = {},
                        onClickHeader = viewModel::navigateToCreatorProfile
                    )
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

            postPreviewsSection(
                previews = uniquePreviews,
                imgBaseUrl = imgBaseUrl,
                onOpenImage = viewModel::navigateOpenImage,
                onOpenUrl = { url ->
                    Intent(Intent.ACTION_VIEW, url.toUri())
                        .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                        .also(context::startActivity)
                },
                downloadStarted = downloadStarted,
                downloadStartedNamed = downloadStartedNamed,
                download = { fullUrl, fileName ->
                    enqueueSystemDownload(
                        context = context,
                        url = fullUrl,
                        fileName = fileName,
                        mimeType = null
                    )
                },
                toast = { msg -> context.toast(msg) }
            )

            postVideosSection(
                videos = state.post?.videos.orEmpty(),
                observeVideoInfo = viewModel::observeVideoInfo,
            )

            item(key = "tags") {
                TagsRow(tags = post.tags)
            }

            item(key = "attachments") {
                PostAttachmentsSection(
                    attachments = state.post?.attachments.orEmpty(),
                    onAttachmentClick = { url ->
                        Intent(Intent.ACTION_VIEW, url.toUri())
                            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            .also(context::startActivity)
                    }
                )
            }

            postCommentsSection(
                commentDomains = state.commentDomains
            )

            item {
                Spacer(Modifier.height(56.dp))
            }
        }
    }
}