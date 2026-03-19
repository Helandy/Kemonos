package su.afk.kemonos.ui.components.posts.postCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.preferences.ui.PostsSize
import su.afk.kemonos.preferences.ui.PostsSize.Companion.toPaddingInCornerBadge
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.components.posts.postCard.preview.PostPreview
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
fun PostCard(
    post: PostDomain,
    onClick: () -> Unit,
    showFavCount: Boolean = false,
    uiSettingModel: UiSettingModel,
    postsSize: PostsSize = uiSettingModel.postsSize,
) {
    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(post.service) { resolver.imageBaseUrlByService(post.service) }
    val meta = rememberPostCardMeta(post)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp,
            hoveredElevation = 8.dp,
            focusedElevation = 8.dp,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            PostPreview(
                preview = meta.preview,
                imgBaseUrl = imgBaseUrl,
                uiSettingModel = uiSettingModel,
                title = post.title,
                textPreview = post.substring,
                blurImage = uiSettingModel.blurImages,
            )

            /** Число лайков */
            if (showFavCount && meta.favCount > 0) {
                CornerBadge(
                    text = "❤ ${meta.favCount}",
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(postsSize.toPaddingInCornerBadge())
                )
            }

            /** Число видео */
            if (meta.videoCount > 0) {
                CornerBadge(
                    text = "🎬 ${meta.videoCount}",
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(postsSize.toPaddingInCornerBadge())
                )
            }

            /** Число вложений */
            if (post.attachments.isNotEmpty()) {
                CornerBadge(
                    text = "\uD83D\uDCCE ${post.attachments.size}",
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(postsSize.toPaddingInCornerBadge())
                )
            }
        }

        Column(
            modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
        ) {
            /** Тайтл и дата */
            if (showFavCount || post.title?.isNotBlank() == true) {
                Text(
                    text = post.title.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row {
                Text(
                    text = "📅 ${post.published?.toUiDateTime(uiSettingModel.dateFormatMode)} ",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun CornerBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(99.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 2.dp,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
        )
    }
}

@Preview("PreviewPostCard")
@Composable
private fun PreviewPostCard() {
    KemonosPreviewScreen {
        PostCard(
            post = PostDomain.default(),
            onClick = {},
            showFavCount = false,
            uiSettingModel = UiSettingModel()
        )
    }
}
